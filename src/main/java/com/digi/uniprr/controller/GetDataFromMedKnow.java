package com.digi.uniprr.controller;

import com.digi.uniprr.utils.AppConstants;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.digi.uniprr.service.GetDataFromMedKnowService;
import com.digi.uniprr.service.JournalDataIssnService;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/getDataFromMedKnow")
public class GetDataFromMedKnow {
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	GetDataFromMedKnowService getDataFromMedKnowService;
	
	@Autowired
	JournalDataIssnService issnService;
	
	JSONObject obj = new JSONObject();
	
	@PostMapping("getImage")
	public ResponseEntity<?> getImage(@RequestParam String url) {
		log.info("Inside getDataFromMedKnow getImage");
		log.info(AppConstants.REQUEST_METHOD+" :: "+AppConstants.POST +" "+
				AppConstants.REQUEST_URI+" :: /getDataFromMedKnow/getImage"+
				AppConstants.REQUEST_PARAMS+" ::"+url);
		if(url!=null && !url.isEmpty()) {
			if(url.startsWith("https:")) {
				log.info("using correct https url :: "+url);	
			}else if(url.startsWith("http:")) {
				url = url.replace("http:", "https:");
			}
		}
		log.info("url : " + url);
		String image = getDataFromMedKnowService.getImage(url);
		if(image=="") {
			log.info("No image found");
			obj.put("data", "No image found");
		}else {
			log.info("image found");
			obj.put("data", image);
		}
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}
	
	@PostMapping("getIndex")
	public ResponseEntity<?> getIndex(@RequestParam String url) {
		log.info("Inside getDataFromMedKnow getIndex");
		log.info("url : " + url);
		log.info(AppConstants.REQUEST_METHOD+" :: "+AppConstants.POST +" "+
				AppConstants.REQUEST_URI+" :: /getDataFromMedKnow/getIndex"+
				AppConstants.REQUEST_PARAMS+" ::"+url);
		String index = getDataFromMedKnowService.getIndex(url);
		try {
		    if (index.equals("")) {
		        log.info("No data found, attempting to fetch HTML from the URL");

		        String htmlFromUrl = issnService.fetchHtmlFromUrl(url);

		        if (htmlFromUrl == null) {
		            obj.put("data", "");
		        } else {
		            obj.put("data", htmlFromUrl);
		        }
		    } else {
		        log.info("data found");
		        obj.put("data", index);
		    }
		} catch (Exception e) {
		    log.error("An error occurred while fetching HTML from the URL: " + e.getMessage());
		    obj.put("data", "");
		}
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}

}
