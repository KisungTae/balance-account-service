package com.beeswork.balanceaccountservice.service.account;


import com.beeswork.balanceaccountservice.dao.account.AccountDAO;
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
import java.util.*;
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
        saveQuestions(accountDTO.getAccountQuestionDTOs(), account);
        accountDAO.persist(account);

    }

    public void saveQuestions(List<AccountQuestionDTO> accountQuestionDTOs, Account account)
    throws QuestionNotFoundException {

        List<AccountQuestion> accountQuestions = account.getAccountQuestions();

        for (int i = accountQuestions.size() - 1; i >= 0; i--) {
            AccountQuestion accountQuestion = accountQuestions.get(i);
            AccountQuestionDTO accountQuestionDTO = accountQuestionDTOs.stream()
                                                                       .filter(a -> a.getQuestionId() ==
                                                                                    accountQuestion.getQuestionId())
                                                                       .findFirst()
                                                                       .orElse(null);

            if (accountQuestionDTO == null) accountQuestions.remove(accountQuestion);
            else {
                modelMapper.map(accountQuestionDTO, accountQuestion);
                accountQuestion.setUpdatedAt(new Date());
                accountQuestionDTOs.remove(accountQuestionDTO);
            }
        }

        List<Long> questionIds = accountQuestionDTOs.stream()
                                                    .map(AccountQuestionDTO::getQuestionId)
                                                    .collect(Collectors.toList());

        List<Question> questions = questionEntityService.findAllByIds(questionIds);

        for (int i = accountQuestionDTOs.size() - 1; i >= 0; i--) {
            AccountQuestionDTO accountQuestionDTO = accountQuestionDTOs.get(i);
            Question question = questions.stream()
                                         .filter(q -> q.getId() == accountQuestionDTO.getQuestionId())
                                         .findFirst()
                                         .orElseThrow(QuestionNotFoundException::new);

            AccountQuestion accountQuestion = modelMapper.map(accountQuestionDTO, AccountQuestion.class);
            accountQuestion.setAccountQuestionId(new AccountQuestionId(account.getId(), question.getId()));
            accountQuestion.setAccount(account);
            accountQuestion.setQuestion(question);
            accountQuestion.setCreatedAt(new Date());
            accountQuestion.setUpdatedAt(new Date());
            accountQuestions.add(accountQuestion);
            accountQuestionDTOs.remove(accountQuestionDTO);
        }
    }

}
