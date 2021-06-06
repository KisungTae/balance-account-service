package com.beeswork.balanceaccountservice.service.question;

import com.beeswork.balanceaccountservice.dao.account.AccountDAO;
import com.beeswork.balanceaccountservice.dao.question.QuestionDAO;
import com.beeswork.balanceaccountservice.dto.question.QuestionDTO;
import com.beeswork.balanceaccountservice.entity.question.Question;
import com.beeswork.balanceaccountservice.exception.question.QuestionNotFoundException;
import com.beeswork.balanceaccountservice.service.base.BaseServiceImpl;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;

@Service
public class QuestionServiceImpl extends BaseServiceImpl implements QuestionService {

    private final QuestionDAO questionDAO;
    private final ModelMapper modelMapper;
    private static final int MIN_NUM_OF_QUESTIONS = 3;

    @Autowired
    public QuestionServiceImpl(QuestionDAO questionDAO,
                               ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
        this.questionDAO = questionDAO;
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
        int offset = new Random().nextInt((int) (questionDAO.count() - MIN_NUM_OF_QUESTIONS));
        List<Question> questions = questionDAO.findAll(MIN_NUM_OF_QUESTIONS, offset);
        return modelMapper.map(questions, new TypeToken<List<QuestionDTO>>() {}.getType());
    }


}




