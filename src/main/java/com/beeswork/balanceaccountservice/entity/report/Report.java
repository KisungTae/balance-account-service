package com.beeswork.balanceaccountservice.entity.report;


import com.beeswork.balanceaccountservice.entity.account.Account;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "report")
public class Report {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id")
    private Account reporter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reported_id")
    private Account reported;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_reason_id")
    private ReportReason reportReason;

    @Column(name = "description")
    private String description;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    public Report(Account reporter, Account reported, ReportReason reportReason, String description, Date createdAt) {
        this.reporter = reporter;
        this.reported = reported;
        this.reportReason = reportReason;
        this.description = description;
        this.createdAt = createdAt;
    }
}
