package com.digi.uniprr.service;

import java.util.List;

import com.digi.uniprr.VO.AuthoritiesHistoryVo;

public interface AuthoritiesHistoryService {

	String authoritiesHistory(AuthoritiesHistoryVo authoritiesHistoryVo);

	List<AuthoritiesHistoryVo> getAuthoritiesHistory(AuthoritiesHistoryVo authoritiesHistoryVo);

}
