package com.beeswork.balanceaccountservice.entity.match;


import com.beeswork.balanceaccountservice.entity.account.Account;
import com.beeswork.balanceaccountservice.entity.chat.Chat;
import com.beeswork.balanceaccountservice.entity.chat.ChatMessage;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "match")
public class Match {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "swiper_id")
    private Account swiper;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "swiped_id")
    private Account swiped;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_id")
    private Chat chat;

    @Column(name = "unmatched")
    private boolean unmatched;

    @Column(name = "deleted")
    private boolean deleted;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "last_read_chat_message_id")
    private ChatMessage lastReadChatMessage;

    @Column(name = "last_read_chat_message_id", insertable = false, updatable = false)
    private long lastReadChatMessageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "last_chat_message_id")
    private ChatMessage lastChatMessage;

    @Column(name = "last_chat_message_id", insertable = false, updatable = false)
    private long lastChatMessageId;

    private String lastChatMessageBody;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    public Match(Account swiper, Account swiped, Chat chat, Date createdAt) {
        this.chat = chat;
        this.swiper = swiper;
        this.swiped = swiped;
        this.createdAt = createdAt;
        this.updatedAt = createdAt;
    }

    public UUID getSwiperId() {
        return this.swiper.getId();
    }

    public UUID getSwipedId() {
        return this.swiped.getId();
    }

    public UUID getChatId() {
        return this.chat.getId();
    }

    public String getSwipedName() {
        return this.swiped.getName();
    }

    public String getSwipedProfilePhotoKey() {
        return this.swiped.getProfilePhotoKey();
    }

    public boolean isSwipedDeleted() {
        return this.swiped.isDeleted();
    }
}
