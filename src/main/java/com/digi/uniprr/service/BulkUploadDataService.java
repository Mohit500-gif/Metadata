package com.digi.uniprr.service;

import javax.servlet.http.HttpServletRequest;

import org.json.simple.JSONObject;

import com.digi.uniprr.VO.UserBulkUploadDataVo;

public interface BulkUploadDataService {

	JSONObject userBulkUploadData(UserBulkUploadDataVo userBulkUploadDataVo, HttpServletRequest request);

}
