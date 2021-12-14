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
@Table(name = "sent_chat_message")
public class SentChatMessage {

    @Id
    private UUID chatMessageId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_message_id")
    @MapsId
    private ChatMessage chatMessage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    @Column(name = "key")
    private long key;

    @Column(name = "fetched")
    private boolean fetched;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    public SentChatMessage(ChatMessage chatMessage, Account account, long key, Date createdAt) {
//        this.chatMessage = chatMessage;
        this.account = account;
        this.key = key;
        this.createdAt = createdAt;
        this.updatedAt = createdAt;
    }
}
