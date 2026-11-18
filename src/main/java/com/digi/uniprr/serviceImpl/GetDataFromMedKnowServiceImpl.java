package com.digi.uniprr.serviceImpl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.digi.uniprr.service.GetDataFromMedKnowService;

@Service
public class GetDataFromMedKnowServiceImpl implements GetDataFromMedKnowService {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Override
	public String getImage(String url) {
		log.info("Inside GetDataFromMedKnowServiceImpl -> getImage");
		log.info("url : " + url);
		String test = "";
		try {
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("GET");
			con.setRequestProperty("User-Agent", "Mozilla/5.0");
			int responseCode = con.getResponseCode();
			log.info("ResponseCode : " + responseCode);
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			test = response.toString();
		} catch (Exception e) {
			log.error(e.toString());
		}
		return test;
	}

	@Override
	public String getIndex(String url) {
		log.info("Inside GetDataFromMedKnowServiceImpl -> getIndex");
		log.info("url : " + url);
		String test = "";
		try {
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("GET");
			con.setRequestProperty("User-Agent", "Mozilla/5.0");
			int responseCode = con.getResponseCode();
			log.info("ResponseCode : " + responseCode);
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			test = response.toString();
		} catch (Exception e) {
			log.error(e.toString());
		}
		return test;
	}

}
