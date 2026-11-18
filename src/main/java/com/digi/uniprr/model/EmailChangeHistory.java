package com.digi.uniprr.model;

import java.time.LocalDateTime;

import javax.persistence.*;

@Entity
@Table(name = "email_change_history")
public class EmailChangeHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "old_email")
    private String oldEmail;

    @Column(name = "new_email")
    private String newEmail;

    @Column(name = "old_user_id")
    private Integer oldUserId;

    @Column(name = "new_user_id")
    private Integer newUserId;

    @Column(name = "journal_id")
    private Integer journalId;

    @Column(name = "action_by")
    private Integer actionBy;

    @Column(name = "action_date")
    private LocalDateTime actionDate;

    @Column(name = "remarks", length = 1000)
    private String remarks;

    // Getters / setters
    public Integer getId() { return id; }
    public String getOldEmail() { return oldEmail; }
    public void setOldEmail(String oldEmail) { this.oldEmail = oldEmail; }
    public String getNewEmail() { return newEmail; }
    public void setNewEmail(String newEmail) { this.newEmail = newEmail; }
    public Integer getOldUserId() { return oldUserId; }
    public void setOldUserId(Integer oldUserId) { this.oldUserId = oldUserId; }
    public Integer getNewUserId() { return newUserId; }
    public void setNewUserId(Integer newUserId) { this.newUserId = newUserId; }
    public Integer getJournalId() { return journalId; }
    public void setJournalId(Integer journalId) { this.journalId = journalId; }
    public Integer getActionBy() { return actionBy; }
    public void setActionBy(Integer actionBy) { this.actionBy = actionBy; }
    public LocalDateTime getActionDate() { return actionDate; }
    public void setActionDate(LocalDateTime actionDate) { this.actionDate = actionDate; }
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
}
