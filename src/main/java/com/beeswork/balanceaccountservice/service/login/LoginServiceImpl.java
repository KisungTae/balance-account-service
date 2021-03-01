package com.beeswork.balanceaccountservice.service.login;

import com.beeswork.balanceaccountservice.constant.LoginType;
import com.beeswork.balanceaccountservice.dao.login.LoginDAO;
import com.beeswork.balanceaccountservice.entity.login.Login;
import com.beeswork.balanceaccountservice.exception.account.AccountEmailDuplicateException;
import com.beeswork.balanceaccountservice.exception.account.AccountEmailNotMutableException;
import com.beeswork.balanceaccountservice.service.base.BaseServiceImpl;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class LoginServiceImpl extends BaseServiceImpl implements LoginService {

    private final LoginDAO loginDAO;

    @Autowired
    public LoginServiceImpl(ModelMapper modelMapper, LoginDAO loginDAO) {
        super(modelMapper);
        this.loginDAO = loginDAO;
    }

    @Override
    @Transactional
    public void saveEmail(UUID accountId, UUID identityToken, String email) {
        Login login = loginDAO.findByAccountId(accountId);
        validateAccount(login.getAccount(), identityToken);

        LoginType loginType = login.getLoginId().getType();
        if (loginType == LoginType.NAVER || loginType == LoginType.GOOGLE)
            throw new AccountEmailNotMutableException();

        if (loginDAO.existsByEmail(email))
            throw new AccountEmailDuplicateException();
        login.setEmail(email);
    }
}
