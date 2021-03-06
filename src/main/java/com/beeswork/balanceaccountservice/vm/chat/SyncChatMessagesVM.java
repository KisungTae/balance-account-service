package com.beeswork.balanceaccountservice.vm.chat;

import com.beeswork.balanceaccountservice.vm.account.AccountIdentityVM;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class SyncChatMessagesVM extends AccountIdentityVM {
    private List<Long> receivedChatMessageIds = new ArrayList<>();
    private List<Long> sentChatMessageIds = new ArrayList<>();
}
