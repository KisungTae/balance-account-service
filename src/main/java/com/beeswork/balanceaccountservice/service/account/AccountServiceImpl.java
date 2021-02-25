package com.beeswork.balanceaccountservice.service.account;


import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.beeswork.balanceaccountservice.config.properties.AWSProperties;
import com.beeswork.balanceaccountservice.constant.PushTokenType;
import com.beeswork.balanceaccountservice.constant.LoginType;
import com.beeswork.balanceaccountservice.dao.account.AccountDAO;
import com.beeswork.balanceaccountservice.dao.account.PushTokenDAO;
import com.beeswork.balanceaccountservice.dao.question.QuestionDAO;
import com.beeswork.balanceaccountservice.entity.account.Account;
import com.beeswork.balanceaccountservice.entity.account.AccountQuestion;
import com.beeswork.balanceaccountservice.entity.account.PushToken;
import com.beeswork.balanceaccountservice.entity.account.PushTokenId;
import com.beeswork.balanceaccountservice.entity.photo.Photo;
import com.beeswork.balanceaccountservice.entity.question.Question;
import com.beeswork.balanceaccountservice.exception.account.AccountEmailNotMutableException;
import com.beeswork.balanceaccountservice.exception.question.QuestionNotFoundException;
import com.beeswork.balanceaccountservice.service.base.BaseServiceImpl;
import com.beeswork.balanceaccountservice.exception.account.AccountEmailDuplicateException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class AccountServiceImpl extends BaseServiceImpl implements AccountService {

    private final AccountDAO accountDAO;
    private final QuestionDAO questionDAO;
    private final PushTokenDAO pushTokenDAO;

    private final AmazonS3 amazonS3;
    private final AWSProperties awsProperties;

    @Autowired
    public AccountServiceImpl(AccountDAO accountDAO,
                              ModelMapper modelMapper,
                              QuestionDAO questionDAO,
                              PushTokenDAO pushTokenDAO,
                              AmazonS3 amazonS3,
                              AWSProperties awsProperties) {
        super(modelMapper);
        this.accountDAO = accountDAO;
        this.questionDAO = questionDAO;
        this.pushTokenDAO = pushTokenDAO;
        this.amazonS3 = amazonS3;
        this.awsProperties = awsProperties;
    }

    @Override
    @Transactional
    public void savePushToken(UUID accountId, UUID identityToken, String key, PushTokenType type) {
        Account account = findValidAccount(accountId, identityToken);
        PushToken pushToken = pushTokenDAO.findById(new PushTokenId(accountId, type));
        if (pushToken == null)
            pushToken = new PushToken(account, type, key, new Date());
        else {
            pushToken.setKey(key);
            pushToken.setUpdatedAt(new Date());
        }
        pushTokenDAO.persist(pushToken);
    }

    //  TEST 1. save accountQuestionDTOs without setAccount() and setQuestion()
    //          --> Hibernate does not insert those objects, no exception thrown
    //  TEST 2. create new accountQuestionDTO with the same AccountQuestionId
    //          --> Hibernate throws exception of creating two object with the same Id
    //  DESC 3. I don't need to put accountQuestionDTO or accountQuestion because Hibernate anyway need a whole list of
    //          accountQuestions to check if it needs to remove or insert or update entities
    @Override
    @Transactional
    public void saveAnswers(UUID accountId, UUID identityToken, Map<Integer, Boolean> answers) {
        Account account = accountDAO.findWithAccountQuestionsIn(accountId, identityToken, answers.keySet());
        validateAccount(account);

        Map<Integer, Integer> sequences = new LinkedHashMap<>();
        int sequence = 1;

        for (Integer key : answers.keySet()) {
            sequences.put(key, sequence);
            sequence++;
        }

        for (int i = account.getAccountQuestions().size() - 1; i >= 0; i--) {

            AccountQuestion accountQuestion = account.getAccountQuestions().get(i);
            int questionId = accountQuestion.getQuestionId();

            if (answers.containsKey(questionId)) {

                accountQuestion.setSelected(true);
                accountQuestion.setAnswer(answers.get(questionId));
                accountQuestion.setSequence(sequences.get(questionId));
                accountQuestion.setUpdatedAt(new Date());

                answers.remove(questionId);
                sequences.remove(questionId);
            } else {
                accountQuestion.setSelected(false);
            }
        }

        if (answers.size() > 0) {

            List<Question> questions = questionDAO.findAllByIds(answers.keySet());

            if (answers.size() != questions.size())
                throw new QuestionNotFoundException();

            for (Question question : questions) {
                Date date = new Date();
                account.getAccountQuestions()
                       .add(new AccountQuestion(true,
                                                answers.get(question.getId()),
                                                sequences.get(question.getId()),
                                                date,
                                                date,
                                                account,
                                                question));
            }
        }
    }

    @Override
    @Transactional
    public void saveEmail(UUID accountId, UUID identityToken, String email) {
        Account account = findValidAccount(accountId, identityToken);

        if (account.getLoginType() == LoginType.NAVER ||
            account.getLoginType() == LoginType.GOOGLE)
            throw new AccountEmailNotMutableException();

        if (accountDAO.existsByEmail(email))
            throw new AccountEmailDuplicateException();

        account.setEmail(email);
    }

    @Override
    @Transactional
    public void deleteAccount(UUID accountId, UUID identityToken) {
        Account account = accountDAO.findWithPhotosAndAccountQuestions(accountId, identityToken);

        // delete photos in s3
        ArrayList<DeleteObjectsRequest.KeyVersion> keys = new ArrayList<>();

        for (Photo photo : account.getPhotos()) {
            keys.add(new DeleteObjectsRequest.KeyVersion(account.getId().toString() + "/" + photo.getKey()));
        }

        if (!keys.isEmpty()) {
            DeleteObjectsRequest deleteObjectsRequest =
                    new DeleteObjectsRequest(awsProperties.getBalancePhotoBucket()).withKeys(keys).withQuiet(true);
            amazonS3.deleteObjects(deleteObjectsRequest);
        }

        // delete photos in database
        account.getPhotos().clear();

        // delete account_questions
        account.getAccountQuestions().clear();

        // delete profile
        Date today = new Date();

        account.setDeleted(true);
        account.setSocialLoginId(null);
        account.setIdentityToken(null);
        account.setName("");
        account.setEmail("");
        account.setHeight(0);
        account.setBirthYear(0);
        account.setBirth(today);
        account.setAbout("");
        account.setScore(0);
        account.setIndex(0);
        account.setPoint(0);
        account.setLocation(null);
        account.setRepPhotoKey(null);
        account.setLocationUpdatedAt(today);
        account.setFcmToken(null);
        account.setUpdatedAt(today);
    }

    private Account findValidAccount(UUID accountId, UUID identityToken) {
        Account account = accountDAO.findById(accountId);
        validateAccount(account, identityToken);
        return account;
    }
}
