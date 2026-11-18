package com.digi.uniprr.model;

import java.util.Date;

import javax.persistence.*;

import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "journal_charge_flag_history")
public class JournalChargeFlagHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "journal_id", nullable = false)
    private Integer journalId;

    @Column(name = "charge_flag", nullable = false)
    private String chargeFlag;

    @CreationTimestamp
    @Column(name = "created_on")
    private Date createdOn;

    public JournalChargeFlagHistory() {}

    public JournalChargeFlagHistory(Integer journalId, String chargeFlag) {
        this.journalId = journalId;
        this.chargeFlag = chargeFlag;
    }

    // Getters/Setters
    public Long getId() { return id; }
    public Integer getJournalId() { return journalId; }
    public void setJournalId(Integer journalId) { this.journalId = journalId; }
    public String getChargeFlag() { return chargeFlag; }
    public void setChargeFlag(String chargeFlag) { this.chargeFlag = chargeFlag; }
    public Date getCreatedOn() { return createdOn; }
}
