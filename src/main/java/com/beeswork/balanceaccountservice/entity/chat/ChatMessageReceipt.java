package com.beeswork.balanceaccountservice.entity.chat;

import com.beeswork.balanceaccountservice.entity.account.Account;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "chat_message_receipt")
public class ChatMessageReceipt {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "chat_message_id")
    private long chatMessageId;

    @Column(name = "account_id")
    private UUID accountId;

    @Column(name = "chat_id")
    private UUID chatId;

    @Column(name = "app_token")
    private UUID appToken;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    public ChatMessageReceipt(long chatMessageId, UUID accountId, UUID chatId, UUID appToken, Date createdAt) {
        this.chatMessageId = chatMessageId;
        this.accountId = accountId;
        this.chatId = chatId;
        this.appToken = appToken;
        this.createdAt = createdAt;
        this.updatedAt = createdAt;
    }
}
