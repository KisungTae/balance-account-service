package com.beeswork.balanceaccountservice.vm.match;

import com.beeswork.balanceaccountservice.validator.ValidUUID;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Getter
@Setter
public class SyncMatchVM {

    @ValidUUID
    private UUID chatId;

    @NotNull
    private Long lastReadReceivedChatMessageId;
}
