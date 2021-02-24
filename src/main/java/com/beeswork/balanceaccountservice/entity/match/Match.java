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
    @MapsId("matcherId")
    private Account matcher;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("matchedId")
    private Account matched;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_id")
    private Chat chat;

    @Column(name = "chat_id", insertable = false, updatable = false)
    private Long chatId;

    @Column(name = "matcher_id", insertable = false, updatable = false)
    private UUID matcherId;

    @Column(name = "matched_id", insertable = false, updatable = false)
    private UUID matchedId;

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

    public Match(Account matcher, Account matched, Chat chat, Date createdAt) {
        this.matchId = new MatchId(matcher.getId(), matched.getId());
        this.chat = chat;
        this.matcher = matcher;
        this.matched = matched;
        this.createdAt = createdAt;
        this.updatedAt = createdAt;
    }

    public UUID getMatcherId() {
        return matchId.getMatcherId();
    }

    public UUID getMatchedId() {
        return matchId.getMatchedId();
    }

}
