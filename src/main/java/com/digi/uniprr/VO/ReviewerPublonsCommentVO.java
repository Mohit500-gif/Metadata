package com.digi.uniprr.VO;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ReviewerPublonsCommentVO {

	String reviewerFirstName;
	String reviewerLastName;
	String reviewerEmail;
	String manuscriptCode;
	Integer manuscriptVersion;
	String manuscriptTitle;
	String manuscriptAbstract;
	Date assignedDate;
	Date acceptDate;
	Date completeDate;
	String reviewerRecomendation;
	String commentsToAuthor;
	String editorFirstName;
	String editorLastName;
	String reviewerResposne;
	Integer id;
	
}
