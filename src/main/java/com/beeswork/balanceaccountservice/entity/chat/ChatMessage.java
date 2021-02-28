package com.beeswork.balanceaccountservice.entity.chat;


import com.beeswork.balanceaccountservice.entity.account.Account;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "chat_message")
public class ChatMessage {

    public ChatMessage(Chat chat, Account account, Account recipient, Long messageId, String body, Date createdAt) {
        this.chat = chat;
        this.account = account;
        this.recipient = recipient;
        this.messageId = messageId;
        this.body = body;
        this.createdAt = createdAt;
    }

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "message_id")
    private long messageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_id")
    private Chat chat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    @Column(name = "account_id", insertable = false, updatable = false)
    private UUID accountId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id")
    private Account recipient;

    @Column(name = "body")
    private String body;

    @Column(name = "read")
    private boolean read;

    @Column(name = "fetched")
    private boolean fetched;

    @Column(name = "received")
    private boolean received;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
}
