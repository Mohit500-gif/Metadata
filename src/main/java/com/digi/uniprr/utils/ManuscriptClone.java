package com.digi.uniprr.utils;

import lombok.*;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "manuscript_clone")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ManuscriptClone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "clone_id")
    private Integer cloneId;

    @Column(name = "manuscript_id", nullable = false)
    private Integer manuscriptId;

    @Column(name = "manuscript_code")
    private String manuscriptCode;

    @Column(name = "manuscript_version")
    private Integer manuscriptVersion;

    @Column(name = "status_code")
    private Integer statusCode;

    @Column(name = "title")
    private String title;

    @Column(name = "submission_status")
    private String submissionStatus;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "submission_date")
    private Date submissionDate;

    @Column(name = "declaration_of_interest")
    private String declarationOfInterest;

    @Column(name = "declaration_of_interest_content", columnDefinition = "TEXT")
    private String declarationOfInterestContent;

    @Column(name = "copyright_agreement")
    private String copyrightAgreement;

    @Column(name = "copyright_agreement_content", columnDefinition = "TEXT")
    private String copyrightAgreementContent;

    @Column(name = "retry_count")
    private Integer retryCount;

    @Column(name = "status")
    private String status;  // e.g. FAILED, SYNCED
    
	@Column(name="doi")
	private String doi;

    @Column(name = "sync_with_manuscript")
    private Boolean syncWithManuscript = false;

	@Override
	public String toString() {
		return "ManuscriptClone [cloneId=" + cloneId + ", manuscriptId=" + manuscriptId + ", manuscriptCode="
				+ manuscriptCode + ", manuscriptVersion=" + manuscriptVersion + ", statusCode=" + statusCode
				+ ", title=" + title + ", submissionStatus=" + submissionStatus + ", submissionDate=" + submissionDate
				+ ", declarationOfInterest=" + declarationOfInterest + ", declarationOfInterestContent="
				+ declarationOfInterestContent + ", copyrightAgreement=" + copyrightAgreement
				+ ", copyrightAgreementContent=" + copyrightAgreementContent + ", retryCount=" + retryCount
				+ ", status=" + status + ", doi=" + doi + ", syncWithManuscript=" + syncWithManuscript + "]";
	}
    
}


