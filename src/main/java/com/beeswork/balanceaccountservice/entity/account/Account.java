package com.beeswork.balanceaccountservice.entity.account;

import com.beeswork.balanceaccountservice.entity.photo.Photo;
import com.beeswork.balanceaccountservice.entity.pushtoken.PushToken;
import com.beeswork.balanceaccountservice.entity.match.Match;
import com.beeswork.balanceaccountservice.entity.report.Report;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.*;


@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "account")
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Account {

    @Version
    private int version;

    @Id
    @Column(name = "id")
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Type(type = "pg-uuid")
    private UUID id;

    @Column(name = "identity_token")
    @Type(type = "pg-uuid")
    private UUID identityToken;

    @Column(name = "name")
    private String name;

    @Column(name = "profile_photo_key")
    private String profilePhotoKey;

    @Column(name = "blocked")
    private boolean blocked;

    @Column(name = "deleted")
    private boolean deleted;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    @OneToOne(mappedBy = "account")
    private Wallet wallet;

    @OneToMany(mappedBy = "account",
               fetch = FetchType.LAZY,
               cascade = CascadeType.ALL,
               orphanRemoval = true)
    private List<AccountQuestion> accountQuestions = new ArrayList<>();

    @OneToMany(mappedBy = "swiper",
               fetch = FetchType.LAZY,
               cascade = CascadeType.ALL,
               orphanRemoval = true)
    private List<Match> matches = new ArrayList<>();

    @OneToMany(mappedBy = "account",
               fetch = FetchType.LAZY,
               cascade = CascadeType.ALL,
               orphanRemoval = true)
    private List<Photo> photos = new ArrayList<>();

    @OneToMany(mappedBy = "account",
               fetch = FetchType.LAZY,
               cascade = CascadeType.ALL,
               orphanRemoval = true)
    private List<PushToken> pushTokens = new ArrayList<>();
}
