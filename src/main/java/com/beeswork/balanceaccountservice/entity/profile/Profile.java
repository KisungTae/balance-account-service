package com.beeswork.balanceaccountservice.entity.profile;

import com.beeswork.balanceaccountservice.entity.account.Account;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.locationtech.jts.geom.Point;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Date;
import java.util.UUID;


@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "profile")
@SqlResultSetMapping(
        name = "Card",
        classes = {
                @ConstructorResult(
                        targetClass = Card.class,
                        columns = {
                                @ColumnResult(name = "account_id", type = UUID.class),
                                @ColumnResult(name = "name", type = String.class),
                                @ColumnResult(name = "gender", type = Boolean.class),
                                @ColumnResult(name = "birth_year", type = Integer.class),
                                @ColumnResult(name = "height", type = Integer.class),
                                @ColumnResult(name = "about", type = String.class),
                                @ColumnResult(name = "distance", type = Double.class),
                                @ColumnResult(name = "photo_key", type = String.class)
                        }
                )
        }
)
public class Profile {

    @Id
    private UUID accountId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    @MapsId
    private Account account;

    @Column(name = "name")
    private String name;

    @Column(name = "birth_year")
    private int birthYear;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "gender")
    private boolean gender;

    @Column(name = "height")
    private Integer height;

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

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    public Profile(Account account,
                   String name,
                   int birthYear,
                   LocalDate birthDate,
                   boolean gender,
                   Integer height,
                   String about,
                   Point location,
                   boolean enabled,
                   Date createdAt) {
        this.account = account;
        this.name = name;
        this.birthYear = birthYear;
        this.birthDate = birthDate;
        this.gender = gender;
        this.height = height;
        this.about = about;
        this.location = location;
        this.locationUpdatedAt = createdAt;
        this.enabled = enabled;
        this.createdAt = createdAt;
        this.updatedAt = createdAt;
    }

    public void incrementScore() {
        this.score = this.score + 1;
    }
}
