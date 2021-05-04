package com.beeswork.balanceaccountservice.entity.swipe;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "swipe_meta")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class SwipeMeta {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "swipe_point")
    private int swipePoint;

    @Column(name = "super_swipe_point")
    private int superSwipePoint;

    @Column(name = "free_swipe_period")
    private int freeSwipePeriod;

    @Column(name = "max_free_swipe")
    private int maxFreeSwipe;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

}
