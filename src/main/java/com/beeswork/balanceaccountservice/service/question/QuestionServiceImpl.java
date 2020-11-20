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
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Repository
public class QuestionServiceImpl extends BaseServiceImpl implements QuestionService {

    private final QuestionDAO questionDAO;
    private final AccountDAO accountDAO;

    public QuestionServiceImpl(QuestionDAO questionDAO, ModelMapper modelMapper, AccountDAO accountDAO) {
        super(modelMapper);
        this.questionDAO = questionDAO;
        this.accountDAO = accountDAO;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED,
                   isolation = Isolation.READ_COMMITTED,
                   readOnly = true)
    public List<QuestionDTO> getQuestions(String accountId, String identityToken) {

        Account account = accountDAO.findWithQuestions(UUID.fromString(accountId), UUID.fromString(identityToken));
        checkIfAccountValid(account);

        List<QuestionDTO> questionDTOs = new ArrayList<>();
        for (AccountQuestion accountQuestion : account.getAccountQuestions()) {
            Question question = accountQuestion.getQuestion();
            questionDTOs.add(new QuestionDTO(accountQuestion.getQuestionId(),
                                             question.getDescription(),
                                             question.getTopOption(),
                                             question.getBottomOption()));
        }
        return questionDTOs;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, readOnly = true)
    public QuestionDTO randomQuestion(List<Integer> currentQuestionIds) {

        long count = questionDAO.count() - currentQuestionIds.size();
        int random = new Random().nextInt((int) count);
        Question question = questionDAO.findNthNotIn(currentQuestionIds, random);
        if (question == null) throw new QuestionNotFoundException();
        return modelMapper.map(question, QuestionDTO.class);
    }


}
