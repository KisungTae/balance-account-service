package com.beeswork.balanceaccountservice.entity.account;

import com.beeswork.balanceaccountservice.constant.LoginType;
import com.beeswork.balanceaccountservice.entity.photo.Photo;
import com.beeswork.balanceaccountservice.entity.profile.Profile;
import com.beeswork.balanceaccountservice.entity.swipe.Swipe;
import com.beeswork.balanceaccountservice.entity.match.Match;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CacheConcurrencyStrategy;
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
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Account {

    @Id
    @Column(name = "id")
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Type(type="pg-uuid")
    private UUID id;

    @Column(name = "identity_token")
    @Type(type = "pg-uuid")
    private UUID identityToken;

    @Column(name = "name")
    private String name;

    @Column(name = "point")
    private int point;

    @Column(name = "rep_photo_key")
    private String repPhotoKey;

    @Column(name = "free_swipe")
    private int freeSwipe;

    @Column(name = "free_swipe_updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date freeSwipeUpdatedAt;

    @Column(name = "blocked")
    private boolean blocked;

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

    @OneToOne(mappedBy = "account")
    private Profile profile;







    @Column(name = "index")
    private int index;


    @Column(name = "social_login_id")
    private String socialLoginId;



    @Column(name = "enabled")
    private boolean enabled;





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



    @Column(name = "fcm_token")
    private String fcmToken;



    @Column(name = "location_updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date locationUpdatedAt;

    @Column(name = "location",
            columnDefinition = "GEOGRAPHY(POINT)")
    private Point location;

    @Enumerated
    private LoginType loginType;

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


}
