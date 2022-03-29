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
    private Long   id;
    private UUID   recipientId;
    private boolean firstMessage;
    private Date   createdAt;
    private String error;

    public boolean isError() {
        return error != null;
    }
}
