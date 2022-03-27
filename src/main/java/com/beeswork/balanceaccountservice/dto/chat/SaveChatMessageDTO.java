package com.beeswork.balanceaccountservice.dto.chat;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
public class SaveChatMessageDTO {
    private Date   createdAt;
    private UUID   recipientId;
    private String error;

    public boolean isError() {
        return error != null;
    }
}
