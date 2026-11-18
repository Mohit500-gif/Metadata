package com.digi.uniprr.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class HttpServiceRequestInfo {
	private String requestUri;
	private String charset;
	private String mediaType;
	private String requestBody;
	private String methodType;
	private List<NameValuePair> queryStringParameters;
	private String queryString;
	private Map<String, String> headers = new HashMap<>();

}
