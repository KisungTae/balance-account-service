package com.beeswork.balanceaccountservice.service.account;


import com.beeswork.balanceaccountservice.dao.account.AccountDAO;
import com.beeswork.balanceaccountservice.dao.account.AccountQuestionDAO;
import com.beeswork.balanceaccountservice.dao.login.LoginDAO;
import com.beeswork.balanceaccountservice.dao.profile.ProfileDAO;
import com.beeswork.balanceaccountservice.dao.question.QuestionDAO;
import com.beeswork.balanceaccountservice.dao.report.ReportDAO;
import com.beeswork.balanceaccountservice.dao.report.ReportReasonDAO;
import com.beeswork.balanceaccountservice.dto.account.DeleteAccountDTO;
import com.beeswork.balanceaccountservice.dto.question.QuestionDTO;
import com.beeswork.balanceaccountservice.entity.account.Account;
import com.beeswork.balanceaccountservice.entity.account.AccountQuestion;
import com.beeswork.balanceaccountservice.entity.login.Login;
import com.beeswork.balanceaccountservice.entity.photo.Photo;
import com.beeswork.balanceaccountservice.entity.profile.Profile;
import com.beeswork.balanceaccountservice.entity.question.Question;
import com.beeswork.balanceaccountservice.entity.report.Report;
import com.beeswork.balanceaccountservice.entity.report.ReportReason;
import com.beeswork.balanceaccountservice.exception.question.QuestionNotFoundException;
import com.beeswork.balanceaccountservice.exception.report.ReportReasonNotFoundException;
import com.beeswork.balanceaccountservice.exception.report.ReportedNotFoundException;
import com.beeswork.balanceaccountservice.service.base.BaseServiceImpl;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AccountServiceImpl extends BaseServiceImpl implements AccountService {

    private final AccountDAO accountDAO;
    private final QuestionDAO questionDAO;
    private final AccountQuestionDAO accountQuestionDAO;
    private final ProfileDAO profileDAO;
    private final LoginDAO loginDAO;

    @Autowired
    public AccountServiceImpl(AccountDAO accountDAO,
                              QuestionDAO questionDAO,
                              AccountQuestionDAO accountQuestionDAO,
                              ProfileDAO profileDAO,
                              LoginDAO loginDAO) {
        this.accountDAO = accountDAO;
        this.questionDAO = questionDAO;
        this.accountQuestionDAO = accountQuestionDAO;
        this.profileDAO = profileDAO;
        this.loginDAO = loginDAO;
    }


    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, readOnly = true)
    public void validateAccount(UUID accountId, UUID identityToken) {
        validateAccount(accountDAO.findById(accountId), identityToken);
    }

    //  TEST 1. save accountQuestionDTOs without setAccount() and setQuestion()
    //          --> Hibernate does not insert those objects, no exception thrown
    //  TEST 2. create new accountQuestionDTO with the same AccountQuestionId
    //          --> Hibernate throws exception of creating two object with the same Id
    //  DESC 3. I don't need to put accountQuestionDTO or accountQuestion because Hibernate anyway need a whole list of
    //          accountQuestions to check if it needs to remove or insert or update entities
    @Override
    @Transactional
    public void saveQuestionAnswers(UUID accountId, UUID identityToken, Map<Integer, Boolean> answers) {
        Account account = validateAccount(accountDAO.findById(accountId), identityToken);
        List<AccountQuestion> accountQuestions = accountQuestionDAO.findAllIn(accountId, answers.keySet());

        Map<Integer, Integer> sequences = new LinkedHashMap<>();
        int initialSequence = 1;

        for (Integer key : answers.keySet()) {
            sequences.put(key, initialSequence);
            initialSequence++;
        }

        if (accountQuestions != null) {
            for (int i = accountQuestions.size() - 1; i >= 0; i--) {
                AccountQuestion accountQuestion = accountQuestions.get(i);
                int questionId = accountQuestion.getQuestionId();

                if (answers.containsKey(questionId)) {
                    accountQuestion.setSelected(true);
                    accountQuestion.setAnswer(answers.get(questionId));
                    accountQuestion.setSequence(sequences.get(questionId));
                    accountQuestion.setUpdatedAt(new Date());
                    answers.remove(questionId);
                    sequences.remove(questionId);
                } else accountQuestion.setSelected(false);
            }
        }

        if (answers.size() > 0) {
            List<Question> questions = questionDAO.findAllIn(answers.keySet());
            if (answers.size() != questions.size())
                throw new QuestionNotFoundException();

            Date date = new Date();
            for (Question question : questions) {
                int questionId = question.getId();
                boolean answer = answers.get(questionId);
                int sequence = sequences.get(questionId);
                AccountQuestion accountQuestion = new AccountQuestion(answer, sequence, date, account, question);
                account.getAccountQuestions().add(accountQuestion);
            }
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, readOnly = true)
    public List<QuestionDTO> listQuestions(UUID accountId, UUID identityToken) {
        validateAccount(accountDAO.findById(accountId), identityToken);
        return accountQuestionDAO.findAllQuestionDTOsWithAnswer(accountId);
    }


    @Override
    @Transactional
    public DeleteAccountDTO deleteAccount(UUID accountId, UUID identityToken) {
        Account account = validateAccount(accountDAO.findById(accountId), identityToken);

        Login login = loginDAO.findByAccountId(accountId);
        if (login != null) loginDAO.remove(login);

        Profile profile = profileDAO.findById(accountId);
        if (profile != null) profileDAO.remove(profile);

        DeleteAccountDTO deleteAccountDTO = new DeleteAccountDTO();

        if (account != null) {
            account.getPushTokens().clear();
            account.getAccountQuestions().clear();
            account.setDeleted(true);
            account.setUpdatedAt(new Date());

            List<Photo> photos = account.getPhotos();
            deleteAccountDTO.setAccountId(accountId);
            deleteAccountDTO.setPhotoKeys(photos.stream().map(Photo::getKey).collect(Collectors.toList()));
            photos.clear();
        }
        return deleteAccountDTO;
    }

}
