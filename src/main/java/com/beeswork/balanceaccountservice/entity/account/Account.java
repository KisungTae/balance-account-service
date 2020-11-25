package com.beeswork.balanceaccountservice.entity.account;

import com.beeswork.balanceaccountservice.constant.AccountType;
import com.beeswork.balanceaccountservice.entity.photo.Photo;
import com.beeswork.balanceaccountservice.entity.swipe.Swipe;
import com.beeswork.balanceaccountservice.entity.match.Match;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.locationtech.jts.geom.Point;

import javax.persistence.*;
import java.util.*;


@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "account")
public class Account {

    @Version
    private int version;

    @Id
    @Column(name = "id")
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID",
                      strategy = "org.hibernate.id.UUIDGenerator")
    @Type(type="pg-uuid")
    private UUID id;

    @Column(name = "social_login_id")
    private String socialLoginId;

    @Column(name = "identity_token")
    @Type(type = "pg-uuid")
    private UUID identityToken;

    @Column(name = "enabled")
    private boolean enabled;

    @Column(name = "blocked")
    private boolean blocked;

    @Column(name = "name")
    private String name;

    @Column(name = "height")
    private Integer height;

    @Column(name = "email")
    private String email;

    @Column(name = "birth_year")
    private int birthYear;

    @Column(name = "birth")
    private Date birth;

    @Column(name = "gender")
    private boolean gender;

    @Column(name = "about")
    private String about;

    @Column(name = "score")
    private int score;

    @Column(name = "index")
    private int index;

    @Column(name = "point")
    private int point;

    @Column(name = "fcm_token")
    private String fcmToken;

    @Column(name = "swiped_count")
    private int swipedCount;

    @Column(name = "rep_photo_key")
    private String repPhotoKey;

    @Column(name = "rep_photo_key_updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date repPhotoKeyUpdatedAt;

    @Column(name = "location_updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date locationUpdatedAt;

    @Column(name = "location",
            columnDefinition = "GEOGRAPHY(POINT)")
    private Point location;

    @Enumerated
    private AccountType accountType;

//    @Column(name = "account_type_id", insertable = false, updatable = false)
//    private int accountTypeId;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "account_type_id")
//    private AccountType accountType;

    @OneToMany(mappedBy = "account",
               fetch = FetchType.LAZY,
               cascade = CascadeType.ALL,
               orphanRemoval = true)
    private List<AccountQuestion> accountQuestions = new ArrayList<>();

    @OneToMany(mappedBy = "matcher",
               fetch = FetchType.LAZY,
               cascade = CascadeType.ALL,
               orphanRemoval = true)
    private List<Match> matches = new ArrayList<>();

    @OneToMany(mappedBy = "account",
               fetch = FetchType.LAZY,
               cascade = CascadeType.ALL,
               orphanRemoval = true)
    private List<Photo> photos = new ArrayList<>();

    @OneToMany(mappedBy = "swiper",
               fetch = FetchType.LAZY,
               cascade = CascadeType.ALL,
               orphanRemoval = true)
    private List<Swipe> swipes = new ArrayList<>();

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;
}
