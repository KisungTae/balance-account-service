package com.beeswork.balanceaccountservice.entity.account;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.locationtech.jts.geom.Point;

import javax.persistence.*;
import java.util.Date;

//@Getter
//@Setter
//@NoArgsConstructor
//@Entity
//@Table(name = "profile")
public class Profile {



    @OneToOne(mappedBy = "account")
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
    private Integer height;

    @Column(name = "about")
    private String about;

    @Column(name = "location",
            columnDefinition = "GEOGRAPHY(POINT)")
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
}
