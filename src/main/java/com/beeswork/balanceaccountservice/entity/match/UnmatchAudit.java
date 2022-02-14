package com.beeswork.balanceaccountservice.entity.match;

import com.beeswork.balanceaccountservice.entity.account.Account;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;


@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "unmatch_audit")
public class UnmatchAudit {

    @EmbeddedId
    private UnmatchAuditId unmatchAuditId;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("swiperId")
    private Account swiper;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("swipedId")
    private Account swiped;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    public UnmatchAudit(Account swiper, Account swiped, Date createdAt) {
        this.unmatchAuditId = new UnmatchAuditId(swiper.getId(), swiped.getId());
        this.swiper = swiper;
        this.swiped = swiped;
        this.createdAt = createdAt;
    }
}
