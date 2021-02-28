package com.beeswork.balanceaccountservice.entity.profile;

import com.beeswork.balanceaccountservice.entity.account.Account;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.LazyToOne;
import org.hibernate.annotations.LazyToOneOption;
import org.locationtech.jts.geom.Point;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "profile")
public class Profile {

    @Id
    private UUID accountId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    @LazyToOne(value = LazyToOneOption.NO_PROXY)
    @MapsId
    private Account account;

    @Column(name = "name")
    private String name;

    @Column(name = "birth_year")
    private int birthYear;

    @Column(name = "birth")
    private Date birth;

    @Column(name = "gender")
    private boolean gender;

    @Column(name = "height")
    private int height;

    @Column(name = "about")
    private String about;

    @Column(name = "location", columnDefinition = "GEOGRAPHY(POINT)")
    private Point location;

    @Column(name = "location_updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date locationUpdatedAt;

    @Column(name = "score")
    private int score;

    @Column(name = "page_index")
    private int pageIndex;

    @Column(name = "enabled")
    private boolean enabled;

    @Column(name = "deleted")
    private boolean deleted;

    @Version
    private int version;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    public Profile(Account account,
                   String name,
                   int birthYear,
                   Date birth,
                   boolean gender,
                   Integer height,
                   String about,
                   Point location,
                   Date createdAt) {
        this.account = account;
        this.name = name;
        this.birthYear = birthYear;
        this.birth = birth;
        this.gender = gender;
        this.height = height;
        this.about = about;
        this.location = location;
        this.locationUpdatedAt = createdAt;
        this.createdAt = createdAt;
        this.updatedAt = createdAt;
    }
}
