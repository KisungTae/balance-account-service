package com.beeswork.balanceaccountservice.service.pushtoken;

import com.beeswork.balanceaccountservice.constant.PushTokenType;
import com.beeswork.balanceaccountservice.dao.account.AccountDAO;
import com.beeswork.balanceaccountservice.dao.pushtoken.PushTokenDAO;
import com.beeswork.balanceaccountservice.entity.account.Account;
import com.beeswork.balanceaccountservice.entity.pushtoken.PushToken;
import com.beeswork.balanceaccountservice.entity.pushtoken.PushTokenId;
import com.beeswork.balanceaccountservice.service.base.BaseServiceImpl;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.UUID;

@Service
public class PushTokenServiceImpl extends BaseServiceImpl implements PushTokenService {

    private final PushTokenDAO pushTokenDAO;
    private final AccountDAO   accountDAO;

    @Autowired
    public PushTokenServiceImpl(PushTokenDAO pushTokenDAO,
                                AccountDAO accountDAO) {
        this.pushTokenDAO = pushTokenDAO;
        this.accountDAO = accountDAO;
    }

    @Override
    @Transactional
    public void savePushToken(UUID accountId, String token, PushTokenType type) {
        Account account = accountDAO.findById(accountId);
        PushToken pushToken = pushTokenDAO.findById(new PushTokenId(accountId, type));
        if (pushToken == null) {
            pushToken = new PushToken(account, type, token, new Date());
        } else {
            pushToken.setToken(token);
            pushToken.setUpdatedAt(new Date());
        }
        pushTokenDAO.persist(pushToken);
    }
}
