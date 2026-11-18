package com.digi.uniprr.VO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@ToString
public class BulkEmailVO {
    private Integer journalId;
    private List<UserDetailsVO> emailIds;
    private String mailFrom;
    private String mailSubject;
    private String mailBody;
    private Integer isActive;
    private Integer manuscriptId;


}
