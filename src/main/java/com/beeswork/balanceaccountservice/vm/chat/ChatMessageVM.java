package com.beeswork.balanceaccountservice.vm.chat;

import com.beeswork.balanceaccountservice.validator.ValidUUID;
import com.beeswork.balanceaccountservice.vm.account.AccountIdentityVM;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotEmpty;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
public class ChatMessageVM extends AccountIdentityVM {

    @NotEmpty
    private Long id;

    @NotEmpty(message = "{account.uuid.empty}")
    @ValidUUID(message = "{account.uuid.invalid}")
    private String recipientId;

    @NotEmpty(message = "{chat.message.empty}")
    private String message;

    public ChatMessageVM(String accountId, String message, String recipientId) {
        this.accountId = accountId;
        this.message = message;
        this.recipientId = recipientId;
    }
}
