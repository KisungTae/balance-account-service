package com.beeswork.balanceaccountservice.vm.chat;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ReceivedChatMessageVM {
    private UUID chatMessageId;
}
