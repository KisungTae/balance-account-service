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

    @EmbeddedId
    private ChatMessageReceiptId chatMessageReceiptId;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("chatMessageId")
    private ChatMessage chatMessage;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("accountId")
    private Account account;

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

    public ChatMessageReceipt(ChatMessage chatMessage,
                              Account account,
                              UUID appToken,
                              Date createdAt,
                              Date updatedAt) {
        this.chatMessageReceiptId = new ChatMessageReceiptId(chatMessage.getId(), account.getId());
        this.chatMessage = chatMessage;
        this.account = account;
        this.chatId = chatMessage.getChatId();
        this.appToken = appToken;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
