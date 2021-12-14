package com.beeswork.balanceaccountservice.vm.chat;

import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class SyncChatMessagesVM {
    private List<UUID> receivedChatMessageIds = new ArrayList<>();
    private List<UUID> sentChatMessageIds     = new ArrayList<>();
}
