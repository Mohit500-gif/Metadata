package com.digi.uniprr.VO;

public class GetRefreeDetailsVO {

	private String refreename;
	private Integer ArticleReviewed;
	private Integer TimeTaken;
	private String AvgReviewerOnTime;
	private String AvgReturnLate;
	private String AvgNeverReturnLate;
	private String AvgRequiredReminder;
	private String AvgRating;
	private String AvgRatingTime;

	public Integer getArticleReviewed() {
		return ArticleReviewed;
	}

	public void setArticleReviewed(Integer articleReviewed) {
		ArticleReviewed = articleReviewed;
	}

	public Integer getTimeTaken() {
		return TimeTaken;
	}

	public void setTimeTaken(Integer timeTaken) {
		TimeTaken = timeTaken;
	}

	public String getAvgReviewerOnTime() {
		return AvgReviewerOnTime;
	}

	public void setAvgReviewerOnTime(String avgReviewerOnTime) {
		AvgReviewerOnTime = avgReviewerOnTime;
	}

	public String getAvgReturnLate() {
		return AvgReturnLate;
	}

	public void setAvgReturnLate(String avgReturnLate) {
		AvgReturnLate = avgReturnLate;
	}

	public String getAvgNeverReturnLate() {
		return AvgNeverReturnLate;
	}

	public void setAvgNeverReturnLate(String avgNeverReturnLate) {
		AvgNeverReturnLate = avgNeverReturnLate;
	}

	public String getAvgRequiredReminder() {
		return AvgRequiredReminder;
	}

	public void setAvgRequiredReminder(String avgRequiredReminder) {
		AvgRequiredReminder = avgRequiredReminder;
	}

	public String getRefreename() {
		return refreename;
	}

	public void setRefreename(String refreename) {
		this.refreename = refreename;
	}

	public String getAvgRating() {
		return AvgRating;
	}

	public void setAvgRating(String avgRating) {
		AvgRating = avgRating;
	}

	public String getAvgRatingTime() {
		return AvgRatingTime;
	}

	public void setAvgRatingTime(String avgRatingTime) {
		AvgRatingTime = avgRatingTime;
	}

	@Override
	public String toString() {
		return "GetRefreeDetailsVO [refreename=" + refreename + ", ArticleReviewed=" + ArticleReviewed + ", TimeTaken="
				+ TimeTaken + ", AvgReviewerOnTime=" + AvgReviewerOnTime + ", AvgReturnLate=" + AvgReturnLate
				+ ", AvgNeverReturnLate=" + AvgNeverReturnLate + ", AvgRequiredReminder=" + AvgRequiredReminder
				+ ", AvgRating=" + AvgRating + ", AvgRatingTime=" + AvgRatingTime + "]";
	}

}
