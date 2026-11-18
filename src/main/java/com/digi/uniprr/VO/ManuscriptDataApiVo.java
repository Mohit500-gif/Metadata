package com.digi.uniprr.VO;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class ManuscriptDataApiVo {

	private Integer manuscriptId;
	private String manuscriptCode;
	private Integer manuscriptStatusId;
	private String inputFile;
	private String outputFile;
	private Integer manuscriptTaskId;
	private String manuscriptTaskName;
	private String authorEmail;
	private String fileType;
	private String isRecheck;
	private String noChangeApproved;
	private String comment;
	private String commentFromProduction;
	private String issueName;

	@ToString.Exclude
	private Map<String, String> filePathMap = new HashMap<>();

}
