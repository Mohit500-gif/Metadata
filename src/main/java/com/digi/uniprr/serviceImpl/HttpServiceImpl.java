package com.digi.uniprr.serviceImpl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.digi.uniprr.service.HttpService;
import com.digi.uniprr.utils.HttpServiceRequestInfo;

@Service("httpService")
public class HttpServiceImpl implements HttpService {
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Value("${socket.session.timeout}")
	private int socketTimeout;

	@Value("${connection.timeout}")
	private int connectionTimeout;

	@Override
	public String callGETService(HttpServiceRequestInfo requestInfo) {
		log.info("callGETService responseString API url :" + requestInfo.getRequestUri());
		
		String responseString = null;
		try(CloseableHttpClient httpClient = getHttpClient();) {
			RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(socketTimeout).setConnectTimeout(connectionTimeout).build();
			HttpGet httpMethod = prepareHttpGetMethod(requestInfo);
			httpMethod.setConfig(requestConfig);
			//check if header is present, if yes then add
			if (requestInfo.getHeaders() != null && requestInfo.getHeaders().size() > 0) {
				for (String key : requestInfo.getHeaders().keySet()) {
					httpMethod.setHeader(key, requestInfo.getHeaders().get(key));
				}
			}
			
			try (CloseableHttpResponse httpClientResponse = httpClient.execute(httpMethod);) {
				log.info("API response :" + httpClientResponse);
				if (isSuccessStatusCode(httpClientResponse.getStatusLine().getStatusCode())) {
					StringBuilder buffer = new StringBuilder();
					BufferedReader reader = new BufferedReader(new InputStreamReader(httpClientResponse.getEntity().getContent(), Charset.forName(requestInfo.getCharset())));
					String dataLine = null;
					while ((dataLine = reader.readLine()) != null) {
						buffer.append(dataLine);
					}
					responseString = buffer.toString();
					
					log.info("Success : " + httpClientResponse.getStatusLine());
				} else {
					responseString = "Failed";
					log.error("Failed to receive expected response :" + httpClientResponse.getStatusLine());
				}
			}
		} catch (IOException | URISyntaxException ioe) {
			responseString = "Failed : " + ioe.getMessage();
			log.error("Unable to connect to : " + requestInfo.getRequestUri() + ", exception :  " +  ioe.getMessage(), ioe);
		} 
		
		log.info("jsonResultString : " + responseString);
		return responseString;
	}

	@Override
	public String callPOSTService(HttpServiceRequestInfo requestInfo) {
		log.info("callPOSTService responseString API url :" + requestInfo.getRequestUri());
		
		String responseString = null;
		try(CloseableHttpClient httpClient = getHttpClient();) {
			RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(socketTimeout).setConnectTimeout(connectionTimeout).build();
			HttpPost httpMethod = prepareHttpPostMethod(requestInfo);
			httpMethod.setConfig(requestConfig);
			log.info(requestInfo.getQueryStringParameters().toString());

			if (requestInfo.getQueryStringParameters() != null && !requestInfo.getQueryStringParameters().isEmpty()) {
				UrlEncodedFormEntity entity = new UrlEncodedFormEntity(requestInfo.getQueryStringParameters(), Charset.forName(requestInfo.getCharset()));
				log.debug("Entity : " + entity);
				httpMethod.setEntity(entity);
			}
			//check if header is present, if yes then add
			if (requestInfo.getHeaders() != null && requestInfo.getHeaders().size() > 0) {
				for (String key : requestInfo.getHeaders().keySet()) {
					httpMethod.setHeader(key, requestInfo.getHeaders().get(key));
				}
			}
			
			try (CloseableHttpResponse httpClientResponse = httpClient.execute(httpMethod);) {
				if (isSuccessStatusCode(httpClientResponse.getStatusLine().getStatusCode())) {
					StringBuilder buffer = new StringBuilder();
					BufferedReader reader = new BufferedReader(new InputStreamReader(httpClientResponse.getEntity().getContent(), Charset.forName(requestInfo.getCharset())));
					String dataLine = null;
					while ((dataLine = reader.readLine()) != null) {
						buffer.append(dataLine);
					}
					responseString = buffer.toString();
				} else {
					responseString = "Failed";
					log.error("Failed to receive expected response :" + httpClientResponse.getStatusLine() + ", Request Info :" + requestInfo + ", API response :" + httpClientResponse);
				}
			}
		} catch (IOException ioe) {
			responseString = "Failed : " + ioe.getMessage();
			log.error("Unable to connect to : " + requestInfo.getRequestUri() + ", exception :  " +  ioe.getMessage(), ioe);
		} 

		return responseString;
	}

	@Override
	public String callJsonPOSTService(HttpServiceRequestInfo requestInfo) {
		log.info("callJsonPOSTService responseString API url :" + requestInfo.getRequestUri());
		
		String responseString = null;
		try(CloseableHttpClient httpClient = getHttpClient();) {
			RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(socketTimeout).setConnectTimeout(connectionTimeout).build();
			HttpPost httpMethod = prepareHttpPostMethod(requestInfo);
			httpMethod.setConfig(requestConfig);

			if (StringUtils.isNotBlank(requestInfo.getRequestBody())) {
				StringEntity entity = new StringEntity(requestInfo.getRequestBody(), ContentType.APPLICATION_JSON);
				httpMethod.setEntity(entity);
			}
			//check if header is present, if yes then add
			if (requestInfo.getHeaders() != null && requestInfo.getHeaders().size() > 0) {
				for (String key : requestInfo.getHeaders().keySet()) {
					httpMethod.setHeader(key, requestInfo.getHeaders().get(key));
				}
			}
			
			try (CloseableHttpResponse httpClientResponse = httpClient.execute(httpMethod);) {
				if (isSuccessStatusCode(httpClientResponse.getStatusLine().getStatusCode())) {
					StringBuilder buffer = new StringBuilder();
					BufferedReader reader = new BufferedReader(new InputStreamReader(httpClientResponse.getEntity().getContent(), Charset.forName(requestInfo.getCharset())));
					String dataLine = null;
					while ((dataLine = reader.readLine()) != null) {
						buffer.append(dataLine);
					}
					responseString = buffer.toString();
					
					log.info("Received 200 response from URL : " + httpClientResponse.getStatusLine());
				} else {
					responseString = "Failed";
					log.error("Failed to receive 200 response from URL :" + httpClientResponse.getStatusLine());
					log.info("Bundling request body : " + requestInfo.getRequestBody());
				}
			}
		} catch (IOException ioe) {
			responseString = "Failed : " + ioe.getMessage();
			log.error("Unable to connect to : " + requestInfo.getRequestUri() + ", exception :  " +  ioe.getMessage(), ioe);
			log.info("Bundling request body : " + requestInfo.getRequestBody());
		} 

		return responseString;
	}

	protected CloseableHttpClient getHttpClient() {
		return HttpClientBuilder.create().build();
	}

	private boolean isSuccessStatusCode(int statusCode) {
		return (statusCode >= HttpStatus.SC_OK && statusCode < HttpStatus.SC_MULTIPLE_CHOICES);
	}

	protected HttpGet prepareHttpGetMethod(HttpServiceRequestInfo requestInfo) throws URISyntaxException {
		URI uri = new URIBuilder(requestInfo.getRequestUri()).setCharset(Charset.forName(requestInfo.getCharset())).build();
		if (requestInfo.getQueryStringParameters() != null && !requestInfo.getQueryStringParameters().isEmpty()) {
			uri = new URIBuilder(requestInfo.getRequestUri()).addParameters(requestInfo.getQueryStringParameters()).setCharset(Charset.forName(requestInfo.getCharset())).build();
		}

		return new HttpGet(uri);
	}

	protected HttpPost prepareHttpPostMethod(HttpServiceRequestInfo requestInfo) {
		return new HttpPost(requestInfo.getRequestUri());
	}

	protected HttpDelete prepareHttpDeleteMethod(HttpServiceRequestInfo requestInfo) {
		return new HttpDelete(requestInfo.getRequestUri());
	}

}
