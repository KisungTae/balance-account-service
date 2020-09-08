package com.beeswork.balanceaccountservice.service.question;

import com.beeswork.balanceaccountservice.dao.question.QuestionDAO;
import com.beeswork.balanceaccountservice.dto.question.QuestionDTO;
import com.beeswork.balanceaccountservice.entity.Question;
import com.beeswork.balanceaccountservice.service.base.BaseServiceImpl;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public class QuestionServiceImpl extends BaseServiceImpl implements QuestionService, QuestionEntityService {

    private final QuestionDAO questionDAO;

    public QuestionServiceImpl(QuestionDAO questionDAO, ModelMapper modelMapper) {
        super(modelMapper);
        this.questionDAO = questionDAO;
    }

    @Override
    @Transactional
    public List<Question> findAllByIds(List<Long> ids) {
        return questionDAO.findAllByIds(ids);
    }


}
