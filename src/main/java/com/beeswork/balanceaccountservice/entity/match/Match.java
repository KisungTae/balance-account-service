package com.beeswork.balanceaccountservice.entity.match;


import com.beeswork.balanceaccountservice.entity.account.Account;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "chat_id")
//    private Chat chat;

    @Column(name = "chat_id")
    private UUID chatId;

    @Column(name = "unmatched")
    private boolean unmatched;

    @Column(name = "deleted")
    private boolean deleted;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "last_read_chat_message_id")
//    private ChatMessage lastReadChatMessage;

    @Column(name = "last_read_chat_message_id")
    private long lastReadChatMessageId;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "last_chat_message_id")
//    private ChatMessage lastChatMessage;

    @Column(name = "last_chat_message_id")
    private long lastChatMessageId;

    @Column(name = "last_chat_message_body")
    private String lastChatMessageBody;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    public Match(Account swiper, Account swiped, UUID chatId, Date createdAt) {
        this.swiper = swiper;
        this.swiped = swiped;
        this.chatId = chatId;
        this.createdAt = createdAt;
        this.updatedAt = createdAt;
    }

    public UUID getSwiperId() {
        return this.swiper.getId();
    }

    public UUID getSwipedId() {
        return this.swiped.getId();
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
