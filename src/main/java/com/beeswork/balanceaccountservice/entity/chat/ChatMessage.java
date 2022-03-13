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
@Table(name = "chat_message")
public class ChatMessage {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_id")
    private Chat chat;

    @Column(name = "chat_id", insertable = false, updatable = false)
    private UUID chatId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private Account sender;

    @Column(name = "sender_id", insertable = false, updatable = false)
    private UUID senderId;

    @Column(name = "body")
    private String body;

    @Column(name = "tag")
    private UUID tag;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    public ChatMessage(Chat chat, Account sender, String body, UUID tag, Date createdAt, Date updatedAt) {
        this.chat = chat;
        this.sender = sender;
        this.body = body;
        this.tag = tag;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
