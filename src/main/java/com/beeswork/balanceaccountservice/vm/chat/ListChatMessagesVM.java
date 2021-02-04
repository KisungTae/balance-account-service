package com.beeswork.balanceaccountservice.vm.chat;

import com.beeswork.balanceaccountservice.validator.ValidUUID;
import com.beeswork.balanceaccountservice.vm.account.AccountIdentityVM;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
public class ListChatMessagesVM extends AccountIdentityVM {


    @NotNull
    private Long chatId;

    @ValidUUID
    private UUID recipientId;

    @NotNull
    private Date lastChatMessageCreatedAt;
}
