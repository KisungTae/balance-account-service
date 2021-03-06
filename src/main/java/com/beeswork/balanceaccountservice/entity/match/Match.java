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
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Match {

    @Version
    private int version;

    @EmbeddedId
    private MatchId matchId;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("swiperId")
    private Account swiper;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("swipedId")
    private Account swiped;

    @Column(name = "swiped_id", insertable = false, updatable = false)
    private UUID swipedId;

    @Column(name = "chat_id", insertable = false, updatable = false)
    private long chatId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_id")
    private Chat chat;

    @Column(name = "unmatched")
    private boolean unmatched;

    @Column(name = "unmatcher")
    private boolean unmatcher;

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
        return matchId.getSwiperId();
    }

    public UUID getSwipedId() {
        return matchId.getSwipedId();
    }

    public void setupAsUnmatcher(Date updatedAt) {
        this.unmatcher = true;
        this.deleted = true;
        setupAsUnmatched(updatedAt);
    }

    public void setupAsUnmatched(Date updatedAt) {
        this.active = true;
        this.unmatched = true;
        this.updatedAt = updatedAt;
    }

}
