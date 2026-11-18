
package com.digi.uniprr.VO;

public class ReviewerForReportDTO {

    private String reviewer_id;
    private String reviewer_name;
    private String reviewer_email;
    private String reviewer_mobile_no;
    private String institution;
    private String country;

    public String getReviewer_id() {
        return reviewer_id;
    }

    public void setReviewer_id(String reviewer_id) {
        this.reviewer_id = reviewer_id;
    }

    public String getReviewer_name() {
        return reviewer_name;
    }

    public void setReviewer_name(String reviewer_name) {
        this.reviewer_name = reviewer_name;
    }

    public String getReviewer_email() {
        return reviewer_email;
    }

    public void setReviewer_email(String reviewer_email) {
        this.reviewer_email = reviewer_email;
    }

    public String getReviewer_mobile_no() {
        return reviewer_mobile_no;
    }

    public void setReviewer_mobile_no(String reviewer_mobile_no) {
        this.reviewer_mobile_no = reviewer_mobile_no;
    }

    public String getInstitution() {
        return institution;
    }

    public void setInstitution(String institution) {
        this.institution = institution;
    }

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}


}
