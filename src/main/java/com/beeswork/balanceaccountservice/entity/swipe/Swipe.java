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

    @Version
    private int version;

    @EmbeddedId
    private SwipeId swipeId;

    @Column(name = "swiper_id", updatable = false, insertable = false)
    private UUID swiperId;

    @Column(name = "swiped_id", updatable = false, insertable = false)
    private UUID swipedId;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("swiperId")
    private Account swiper;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("swipedId")
    private Account swiped;

    @Column(name = "clicked")
    private boolean clicked;

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

    public Swipe(Account swiper, Account swiped, boolean clicked, int count, Date createdAt, Date updatedAt) {
        this.swipeId = new SwipeId(swiper.getId(), swiped.getId());
        this.swiper = swiper;
        this.swiped = swiped;
        this.clicked = clicked;
        this.count = count;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
