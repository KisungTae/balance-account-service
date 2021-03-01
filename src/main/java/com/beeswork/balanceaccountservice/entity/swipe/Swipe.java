package com.beeswork.balanceaccountservice.entity.swipe;


import com.beeswork.balanceaccountservice.entity.account.Account;
import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
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
@Table(name = "swipe")
public class Swipe {

    @EmbeddedId
    private SwipeId swipeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("swiperId")
    private Account swiper;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("swipedId")
    private Account swiped;

    @Column(name = "clicked")
    private boolean clicked;

    @Column(name = "matched")
    private boolean matched;

    @Column(name = "count")
    private int count;

    @Column(name = "super_click")
    private boolean superClick;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    public Swipe(Account swiper, Account swiped, Date createdAt) {
        this.swipeId = new SwipeId(swiper.getId(), swiped.getId());
        this.swiper = swiper;
        this.swiped = swiped;
        this.createdAt = createdAt;
        this.updatedAt = createdAt;
    }
}
