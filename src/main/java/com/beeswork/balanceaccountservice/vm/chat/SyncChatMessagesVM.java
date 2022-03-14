package com.beeswork.balanceaccountservice.vm.chat;

import com.beeswork.balanceaccountservice.validator.ValidUUID;
import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class SyncChatMessagesVM {

    @ValidUUID
    private UUID chatId;

    @ValidUUID
    private UUID appToken;

    private List<Long> chatMessageIds = new ArrayList<>();
}
