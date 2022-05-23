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

    @Column(name = "swiper_Id", insertable = false, updatable = false)
    private UUID swiperId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "swiped_id")
    private Account swiped;

    @Column(name = "swiped_Id", insertable = false, updatable = false)
    private UUID swipedId;

    @Column(name = "chat_id")
    private UUID chatId;

    @Column(name = "unmatched")
    private boolean unmatched;

    @Column(name = "deleted")
    private boolean deleted;

    @Column(name = "last_received_chat_message_id")
    private long lastReceivedChatMessageId;

    @Column(name = "last_read_received_chat_message_id")
    private long lastReadReceivedChatMessageId;

    @Column(name = "last_read_by_chat_message_id")
    private long lastReadByChatMessageId;

    @Column(name = "last_chat_message_id")
    private long lastChatMessageId;

    @Column(name = "last_chat_message_body")
    private String lastChatMessageBody;

    @Column(name = "last_chat_message_created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastChatMessageCreatedAt;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    public Match(Account swiper, Account swiped, UUID chatId, Date createdAt) {
        this.swiper = swiper;
        this.swiperId = swiper.getId();
        this.swiped = swiped;
        this.swipedId = swiped.getId();
        this.chatId = chatId;
        this.createdAt = createdAt;
        this.updatedAt = createdAt;
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
