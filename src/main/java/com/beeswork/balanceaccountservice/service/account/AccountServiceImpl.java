package com.beeswork.balanceaccountservice.service.account;


import com.beeswork.balanceaccountservice.dao.account.AccountDAO;
import com.beeswork.balanceaccountservice.dao.accountquestion.AccountQuestionDAO;
import com.beeswork.balanceaccountservice.dao.question.QuestionDAO;
import com.beeswork.balanceaccountservice.dto.AccountQuestionDTO;
import com.beeswork.balanceaccountservice.dto.account.AccountDTO;
import com.beeswork.balanceaccountservice.entity.Account;
import com.beeswork.balanceaccountservice.entity.AccountQuestion;
import com.beeswork.balanceaccountservice.entity.AccountQuestionId;
import com.beeswork.balanceaccountservice.entity.Question;
import com.beeswork.balanceaccountservice.exception.account.AccountNotFoundException;
import com.beeswork.balanceaccountservice.exception.question.QuestionNotFoundException;
import com.beeswork.balanceaccountservice.service.base.BaseServiceImpl;
import com.beeswork.balanceaccountservice.service.question.QuestionEntityService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AccountServiceImpl extends BaseServiceImpl implements AccountService {

    private final QuestionEntityService questionEntityService;


    private final AccountDAO accountDAO;

    @Autowired
    public AccountServiceImpl(AccountDAO accountDAO,
                              ModelMapper modelMapper,
                              QuestionEntityService questionEntityService) {
        super(modelMapper);
        this.accountDAO = accountDAO;
        this.questionEntityService = questionEntityService;
    }




    @Override
    @Transactional
    public void save(AccountDTO accountDTO) throws AccountNotFoundException, QuestionNotFoundException {

        Account account = accountDAO.findById(accountDTO.getId());

        modelMapper.map(accountDTO, account);

        List<AccountQuestion> accountQuestions = account.getAccountQuestions();
        List<AccountQuestionDTO> accountQuestionDTOs = accountDTO.getAccountQuestionDTOs();

        List<Long> questionIds = accountQuestionDTOs.stream()
                                                    .map(AccountQuestionDTO::getQuestionId)
                                                    .collect(Collectors.toList());

        List<Question> questions = questionEntityService.findAllByIds(questionIds);

        if (questions.size() != questionIds.size()) throw new QuestionNotFoundException();

        for (AccountQuestion accountQuestion : account.getAccountQuestions()) {
            System.out.println(accountQuestion.toString());
        }

//        for (Question question : questions) {
//            AccountQuestionDTO accountQuestionDTO = accountQuestionDTOs.stream()
//                                                                       .filter(a -> a.getQuestionId() == question.getId())
//                                                                       .findFirst()
//                                                                       .orElseThrow(QuestionNotFoundException::new);
//
//            accountQuestions.add(new AccountQuestion(new AccountQuestionId(account.getId(), question.getId()),
//                                                     account,
//                                                     question,
//                                                     true,
//                                                     accountQuestionDTO.isSelected(),
//                                                     accountQuestionDTO.getSequence(),
//                                                     new Date(),
//                                                     new Date()));
//        }

        accountDAO.save(account);
    }

    public void saveQuestions(List<AccountQuestionDTO> accountQuestionDTOs, UUID accountId) {

    }

    @Transactional
    public void saveQuestions(List<AccountQuestionDTO> accountQuestionDTOs, Account account) {

    }


}
