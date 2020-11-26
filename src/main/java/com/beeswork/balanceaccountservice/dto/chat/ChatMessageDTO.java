package com.beeswork.balanceaccountservice.dto.chat;


import com.beeswork.balanceaccountservice.entity.account.Account;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
public class ChatMessageDTO {

    private Long id;
    private Long chatId;
    private UUID accountId;
    private UUID recipientId;
    private String message;
    private Date createdAt;

}
