package com.beeswork.balanceaccountservice.entity.chat;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageReceiptId implements Serializable {

    private long chatMessageId;
    private UUID accountId;

}
