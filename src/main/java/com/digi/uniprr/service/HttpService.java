package com.digi.uniprr.service;

import com.digi.uniprr.utils.HttpServiceRequestInfo;

public interface HttpService {
	String callGETService(HttpServiceRequestInfo requestInfo);

	String callPOSTService(HttpServiceRequestInfo requestInfo);

	String callJsonPOSTService(HttpServiceRequestInfo requestInfo);

}
