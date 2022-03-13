package com.beeswork.balanceaccountservice.vm.chat;

import com.beeswork.balanceaccountservice.validator.ValidUUID;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Getter
@Setter
public class ListChatMessageVM {

    @ValidUUID
    private UUID chatId;

    @ValidUUID
    private UUID appToken;

    @NotNull
    private Integer startPosition;

    @NotNull
    private Integer loadSize;
}
