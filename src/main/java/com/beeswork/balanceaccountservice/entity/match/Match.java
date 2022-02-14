package com.beeswork.balanceaccountservice.entity.match;


import com.beeswork.balanceaccountservice.entity.account.Account;
import com.beeswork.balanceaccountservice.entity.chat.Chat;
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
//@Cacheable
//@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Match {

    @EmbeddedId
    private MatchId matchId;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("swiperId")
    private Account swiper;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("swipedId")
    private Account swiped;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_id")
    private Chat chat;

    @Column(name = "unmatched")
    private boolean unmatched;

    @Column(name = "active")
    private boolean active;

    @Column(name = "deleted")
    private boolean deleted;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    public Match(Account swiper, Account swiped, Chat chat, Date createdAt) {
        this.matchId = new MatchId(swiper.getId(), swiped.getId());
        this.chat = chat;
        this.swiper = swiper;
        this.swiped = swiped;
        this.createdAt = createdAt;
        this.updatedAt = createdAt;
    }

    public UUID getSwiperId() {
        return this.matchId.getSwiperId();
    }

    public UUID getSwipedId() {
        return this.matchId.getSwipedId();
    }

    public long getChatId() {
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

    public void swap() {
        Account tempSwiper = this.swiper;
        this.swiper = this.swiped;
        this.swiped = tempSwiper;
        this.matchId.setSwiperId(this.swiper.getId());
        this.matchId.setSwipedId(this.swiped.getId());
    }
}
