package com.digi.uniprr.VO;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ArticleMappingDetail implements Serializable {

	private static final long serialVersionUID = 57027911890882510L;

	private String manuscriptCode;
	private String linkedManuscriptCode;
	private String manuscriptType;

}