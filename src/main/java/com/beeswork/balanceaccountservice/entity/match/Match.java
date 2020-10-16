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

    @EmbeddedId
    private MatchId matchId;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("matcherId")
    private Account matcher;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("matchedId")
    private Account matched;

    @Column(name = "matcher_id", insertable = false, updatable = false)
    private UUID matcherId;

    @Column(name = "matched_id", insertable = false, updatable = false)
    private UUID matchedId;

    @Column(name = "unmatched")
    private boolean unmatched;

    @Column(name = "unmatcher")
    private boolean unmatcher;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    public Match(Account matcher, Account matched, boolean unmatched, Date createdAt, Date updatedAt) {
        this.matchId = new MatchId(matcher.getId(), matched.getId());
        this.matcher = matcher;
        this.matched = matched;
        this.unmatched = unmatched;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getMatcherId() {
        return matchId.getMatcherId();
    }

    public UUID getMatchedId() {
        return matchId.getMatchedId();
    }

}
