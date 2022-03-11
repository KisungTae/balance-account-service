package com.beeswork.balanceaccountservice.vm.chat;

import com.beeswork.balanceaccountservice.validator.ValidUUID;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Getter
@Setter
public class FetchChatMessagesVM {

    @ValidUUID
    private UUID chatId;

    private Long lastChatMessageId;

    @NotNull
    private Integer loadSize;
}
