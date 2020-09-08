package com.beeswork.balanceaccountservice.entity;

import lombok.AllArgsConstructor;
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

    @Id
    @Column(name = "id")
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @Column(name = "blocked")
    private boolean blocked;

    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "birth")
    private int birth;

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

    @Column(name = "favor_count")
    private int favorCount;

    @Column(name = "favor_count_updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date favorCountUpdatedAt;

    @Column(name = "location",
            columnDefinition = "GEOGRAPHY(POINT)")
    private Point location;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_type_id")
    private AccountType accountType;

    @OneToMany(mappedBy = "account",
               fetch = FetchType.LAZY,
               cascade = CascadeType.ALL,
               orphanRemoval = true)
    private List<AccountQuestion> accountQuestions = new ArrayList<>();

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;
}
