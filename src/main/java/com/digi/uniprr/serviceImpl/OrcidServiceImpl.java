package com.digi.uniprr.serviceImpl;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.digi.uniprr.model.Role;
import com.digi.uniprr.repository.RoleRepo;
import com.digi.uniprr.repository.UserJournalRepo;
import com.digi.uniprr.repository.UserRoleRepo;
import com.digi.uniprr.service.OrcidService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

@Service
public class OrcidServiceImpl implements OrcidService {
	
	@Value("${oauthBaseUrl}")
	private String oauthBaseUrl;
	
	@Value("${oauthTokenUrl}")
	private String oauthTokenUrl;
	
	@Value("${oauthClientId}")
	private String oauthClientId;
	
	@Value("${oauthClientSecret}")
	private String oauthClientSecret;

	@Autowired
	UserRoleRepo userRoleRepo;
	
	@Autowired
	RoleRepo roleRepo;
	
	@Autowired
	UserJournalRepo userJournalRepo;
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Override
	public String getOrcidDetail(String id) {

		String test = "";
		try {
			XmlMapper xmlMapper = new XmlMapper();

			String url = "https://pub.orcid.org/v3.0/" + id;
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();

			con.setRequestMethod("GET");

			con.setRequestProperty("User-Agent", "Mozilla/5.0");

			int responseCode = con.getResponseCode();

			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			test = response.toString();

			JsonNode jsonNode = xmlMapper.readTree(test.getBytes());
			ObjectMapper objectMapper = new ObjectMapper();

			// Get JSON as a string
			String value = objectMapper.writeValueAsString(jsonNode);
			test = value;

		} catch (Exception e) {
			log.error("Exception : " + e.getMessage());
			return "Something went wrong";
		}
		return test;
	}

	@Override
	public String getOrcidID(String name, String readOrcid) {

//		String client_id = oauthClientId;
//		String client_secret = oauthClientSecret;
//		String code = name;
//		String redirect_uri = oauthBaseUrl + "/orcid/orcidresponse";
//		String grant_type = "authorization_code";

		log.info("orcid::::  Name :: "+name+", Read OrcidId in getOrcidId:: "+readOrcid);
		StringBuffer response = new StringBuffer();
		try {
			// String urlParameters =
			// "client_id=APP-XY03FOWMVO25GH81&client_secret=879c9868-4bf6-447a-b9e6-c6345f30302c&code=9VnuZ7&redirect_uri=http://localhost:8080/uniprr/orcid/orcidresponse&grant_type=authorization_code";
			String urlParameters = "";
		//	log.info("oauthClientId:: "+oauthClientId+", oauthClientSecret:: "+oauthClientSecret+", oauthBaseUrl:: "+oauthBaseUrl+", oauthTokenUrl:: "+oauthTokenUrl);
			urlParameters = urlParameters.concat("?client_id=" + oauthClientId)
					.concat("&client_secret=" + oauthClientSecret)
					.concat("&code=" + name);
			readOrcid = readOrcid == null ? "NO":readOrcid;
			
			if(readOrcid.equalsIgnoreCase("YES")) {
				urlParameters = urlParameters.concat("&grant_type=authorization_code");
			}else {
				urlParameters = urlParameters.concat("&redirect_uri=" + oauthBaseUrl + "/orcid/orcidresponse")
						.concat("&grant_type=authorization_code");
			}
        //    log.info("urlParameters:: "+urlParameters);
			byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
		//	log.info("postData:: "+Arrays.toString(postData));

			int postDataLength = postData.length;
		//	log.info("postDataLength:: "+postDataLength);
			String request = oauthTokenUrl+urlParameters;
		//	log.info("request:: "+request);
			URL url = new URL(request);
		//	log.info("url:: "+url);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			//log.info("Http Connection:: "+conn);
			conn.setDoOutput(true);
			conn.setInstanceFollowRedirects(false);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

			conn.setRequestProperty("charset", "utf-8");
			conn.setRequestProperty("Content-Length", Integer.toString(postDataLength));
			conn.setUseCaches(false);
			//log.info("Http Connection:: "+conn);
			try (DataOutputStream wr = new DataOutputStream(conn.getOutputStream())) {
				wr.write(postData);
			}
			int responseCode = conn.getResponseCode();
			log.debug("--------------------------" +responseCode);
			log.info("orcid:::: Request :: "+request+", responseCode from orcid :: "+responseCode);
			if (responseCode == 200) {
				BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				String inputLine;
				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				log.info("orcid:::: Request :: "+request+", responseCode from orcid :: "+responseCode+", Response :: "+response);
				in.close();
			}else {
				response.append("Something went wrong");
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Exception during retreving orcid id : " + e.getMessage());
		}
		return response.toString();
	}

	@Override
	public List<Role> getRolesByOrcid(Integer userId, Integer jid) {
		List<Integer> userData = new ArrayList<Integer>();
		
		List<Role> userRoles = new ArrayList<Role>();
		try {
			userData = userJournalRepo.getUserRolesByUserId(userId, jid);
			userRoles = roleRepo.getRoleListById(userData);
		} catch (Exception e) {
			log.error("Exception during retreving getRolesByOrcid : " + e.getMessage());
			e.printStackTrace();
		}
		return userRoles;
	}

}
