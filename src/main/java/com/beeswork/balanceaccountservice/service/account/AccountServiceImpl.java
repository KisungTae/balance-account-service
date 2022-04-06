package com.beeswork.balanceaccountservice.service.account;


import com.beeswork.balanceaccountservice.dao.account.AccountDAO;
import com.beeswork.balanceaccountservice.dao.account.AccountQuestionDAO;
import com.beeswork.balanceaccountservice.dao.login.LoginDAO;
import com.beeswork.balanceaccountservice.dao.profile.ProfileDAO;
import com.beeswork.balanceaccountservice.dao.question.QuestionDAO;
import com.beeswork.balanceaccountservice.dto.account.DeleteAccountDTO;
import com.beeswork.balanceaccountservice.dto.question.QuestionDTO;
import com.beeswork.balanceaccountservice.entity.account.Account;
import com.beeswork.balanceaccountservice.entity.account.AccountQuestion;
import com.beeswork.balanceaccountservice.entity.login.Login;
import com.beeswork.balanceaccountservice.entity.photo.Photo;
import com.beeswork.balanceaccountservice.entity.profile.Profile;
import com.beeswork.balanceaccountservice.entity.question.Question;
import com.beeswork.balanceaccountservice.exception.question.QuestionNotFoundException;
import com.beeswork.balanceaccountservice.service.base.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AccountServiceImpl extends BaseServiceImpl implements AccountService {

    private final AccountDAO         accountDAO;
    private final QuestionDAO        questionDAO;
    private final AccountQuestionDAO accountQuestionDAO;
    private final ProfileDAO         profileDAO;
    private final LoginDAO           loginDAO;

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
    @Transactional
    public DeleteAccountDTO deleteAccount(UUID accountId) {
        Account account = accountDAO.findById(accountId, true);

        Login login = loginDAO.findByAccountId(accountId);
        if (login != null) {
            loginDAO.remove(login);
        }

        Profile profile = profileDAO.findById(accountId, true);
        if (profile != null) {
            profile.setEnabled(false);
        }

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
