package com.beeswork.balanceaccountservice.dto.chat;

import com.beeswork.balanceaccountservice.validator.ValidUUID;
import com.beeswork.balanceaccountservice.vm.account.AccountIdentityVM;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
public class ChatMessageDTO {
    private String message;
    private String accountId;
    private String recipientId;
    private String chatId;
    private Date createdAt = new Date();
}
