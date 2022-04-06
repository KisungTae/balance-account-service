package com.beeswork.balanceaccountservice.service.question;

import com.beeswork.balanceaccountservice.dao.account.AccountDAO;
import com.beeswork.balanceaccountservice.dao.account.AccountQuestionDAO;
import com.beeswork.balanceaccountservice.dao.question.QuestionDAO;
import com.beeswork.balanceaccountservice.dao.swipe.SwipeMetaDAO;
import com.beeswork.balanceaccountservice.dao.wallet.WalletDAO;
import com.beeswork.balanceaccountservice.dto.question.ListQuestionsDTO;
import com.beeswork.balanceaccountservice.dto.question.QuestionDTO;
import com.beeswork.balanceaccountservice.entity.account.Account;
import com.beeswork.balanceaccountservice.entity.account.AccountQuestion;
import com.beeswork.balanceaccountservice.entity.account.Wallet;
import com.beeswork.balanceaccountservice.entity.question.Question;
import com.beeswork.balanceaccountservice.entity.swipe.SwipeMeta;
import com.beeswork.balanceaccountservice.exception.question.QuestionNotFoundException;
import com.beeswork.balanceaccountservice.service.base.BaseServiceImpl;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class QuestionServiceImpl extends BaseServiceImpl implements QuestionService {

    private final QuestionDAO questionDAO;
    private final ModelMapper modelMapper;
    private final AccountDAO accountDAO;
    private final WalletDAO          walletDAO;
    private final AccountQuestionDAO accountQuestionDAO;
    private static final int MIN_NUM_OF_QUESTIONS = 3;

    @Autowired
    public QuestionServiceImpl(QuestionDAO questionDAO,
                               ModelMapper modelMapper,
                               AccountDAO accountDAO,
                               WalletDAO walletDAO,
                               AccountQuestionDAO accountQuestionDAO) {
        this.modelMapper = modelMapper;
        this.questionDAO = questionDAO;
        this.accountDAO = accountDAO;
        this.walletDAO = walletDAO;
        this.accountQuestionDAO = accountQuestionDAO;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, readOnly = true)
    public QuestionDTO randomQuestion(List<Integer> questionIds) {
        long count = questionDAO.count() - questionIds.size();
        int random = new Random().nextInt((int) count);
        Question question = questionDAO.findNthNotIn(questionIds, random);
        if (question == null) throw new QuestionNotFoundException();
        return modelMapper.map(question, QuestionDTO.class);
    }




    //    TODO: right away check if account is required
    //  TEST 1. save accountQuestionDTOs without setAccount() and setQuestion()
    //          --> Hibernate does not insert those objects, no exception thrown
    //  TEST 2. create new accountQuestionDTO with the same AccountQuestionId
    //          --> Hibernate throws exception of creating two object with the same Id
    //  DESC 3. I don't need to put accountQuestionDTO or accountQuestion because Hibernate anyway need a whole list of
    //          accountQuestions to check if it needs to remove or insert or update entities
    @Override
    @Transactional
    public void saveQuestionAnswers(UUID accountId, Map<Integer, Boolean> answers) {
        Account account = accountDAO.findById(accountId, false);
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
    public ListQuestionsDTO listQuestions(UUID accountId) {
        ListQuestionsDTO listQuestionsDTO = new ListQuestionsDTO();
        listQuestionsDTO.setQuestionDTOs(accountQuestionDAO.findAllQuestionDTOsWithAnswer(accountId));
        // NOTE 1. this means that user has not saved any answers yet so it's equal to questionDTO.size() == 0
        if (listQuestionsDTO.getQuestionDTOs().size() < MIN_NUM_OF_QUESTIONS) {
            listQuestionsDTO.setQuestionDTOs(listRandomQuestions());
        }

        Wallet wallet = walletDAO.findByAccountId(accountId, true);
        if (wallet != null) {
            listQuestionsDTO.setPoint(wallet.getPoint());
        }
        return listQuestionsDTO;
    }

    private List<QuestionDTO> listRandomQuestions() {
        int questionCount = (int) questionDAO.count();
        if (questionCount <= 0) {
            throw new QuestionNotFoundException();
        }

        int offset = new Random().nextInt(questionCount - MIN_NUM_OF_QUESTIONS);
        List<Question> questions = questionDAO.findAll(MIN_NUM_OF_QUESTIONS, offset);
        return modelMapper.map(questions, new TypeToken<List<QuestionDTO>>() {}.getType());
    }

}




