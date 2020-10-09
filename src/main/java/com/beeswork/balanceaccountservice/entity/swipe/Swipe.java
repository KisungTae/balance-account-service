package com.beeswork.balanceaccountservice.entity.swipe;


import com.beeswork.balanceaccountservice.entity.account.Account;
import com.querydsl.core.annotations.QueryProjection;
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

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "swiper_id", updatable = false, insertable = false)
    private UUID swiperId;

    @Column(name = "swiped_id", updatable = false, insertable = false)
    private UUID swipedId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "swiper_id")
    private Account swiper;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "swiped_id")
    private Account swiped;

    @Column(name = "clicked")
    private boolean clicked;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    public Swipe(Account swiper, Account swiped, boolean clicked, Date createdAt, Date updatedAt) {
        this.swiper = swiper;
        this.swiped = swiped;
        this.clicked = clicked;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
