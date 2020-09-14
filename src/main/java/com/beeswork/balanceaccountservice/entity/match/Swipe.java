package com.beeswork.balanceaccountservice.entity.match;


import com.beeswork.balanceaccountservice.entity.account.Account;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @Column(name = "swiper_id")
    private Account swiper;

    @ManyToOne(fetch = FetchType.LAZY)
    @Column(name = "swiped_id")
    private Account swiped;

    @Column(name = "balanced")
    private boolean balanced;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updated;

    public Swipe(Account swiper, Account swiped, boolean balanced, Date createdAt) {
        this.swiper = swiper;
        this.swiped = swiped;
        this.balanced = balanced;
        this.createdAt = createdAt;
    }
}
