package com.beeswork.balanceaccountservice.service.question;

import com.beeswork.balanceaccountservice.dao.account.AccountDAO;
import com.beeswork.balanceaccountservice.dao.question.QuestionDAO;
import com.beeswork.balanceaccountservice.dto.question.QuestionDTO;
import com.beeswork.balanceaccountservice.entity.account.Account;
import com.beeswork.balanceaccountservice.entity.account.AccountQuestion;
import com.beeswork.balanceaccountservice.entity.question.Question;
import com.beeswork.balanceaccountservice.exception.question.QuestionNotFoundException;
import com.beeswork.balanceaccountservice.service.base.BaseServiceImpl;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class QuestionServiceImpl extends BaseServiceImpl implements QuestionService {

    private final QuestionDAO questionDAO;
    private final AccountDAO  accountDAO;

    private static final int MAX_NUM_OF_QUESTIONS = 3;

    public QuestionServiceImpl(QuestionDAO questionDAO, ModelMapper modelMapper, AccountDAO accountDAO) {
        super(modelMapper);
        this.questionDAO = questionDAO;
        this.accountDAO = accountDAO;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, readOnly = true)
    public List<QuestionDTO> listQuestions(String accountId, String identityToken) {

        Account account = accountDAO.findWithAccountQuestions(UUID.fromString(accountId),
                                                              UUID.fromString(identityToken));
        checkIfAccountValid(account);

        List<QuestionDTO> questionDTOs = new ArrayList<>();

        for (AccountQuestion accountQuestion : account.getAccountQuestions()) {
            Question question = accountQuestion.getQuestion();
            questionDTOs.add(new QuestionDTO(question.getId(),
                                             question.getDescription(),
                                             question.getTopOption(),
                                             question.getBottomOption(),
                                             accountQuestion.isAnswer()));
        }

        for (int i = account.getAccountQuestions().size(); i < MAX_NUM_OF_QUESTIONS; i++) {
            List<Integer> questionIds = questionDTOs.stream().map(QuestionDTO::getId).collect(Collectors.toList());
            questionDTOs.add(randomQuestion(questionIds));
        }

        return questionDTOs;
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

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, readOnly = true)
    public List<QuestionDTO> listRandomQuestions() {

        long count = questionDAO.count();
        int startIndex = new Random().nextInt((int) (count - MAX_NUM_OF_QUESTIONS));
        return modelMapper.map(questionDAO.findAllWithLimitAndOffset(MAX_NUM_OF_QUESTIONS, startIndex),
                               new TypeToken<List<QuestionDTO>>() {}.getType());
    }


}




