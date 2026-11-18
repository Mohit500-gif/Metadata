package com.digi.uniprr.controller;

import com.digi.uniprr.VO.TokenRequestVO;
import com.digi.uniprr.config.HttpClientGenerator;
import com.digi.uniprr.service.TokenExpireService;
import com.digi.uniprr.utils.AppConstants;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/tokenExpired")
public class TokenExpireController {

    @Autowired
    TokenExpireService tokenExpireService;

    private final Logger log = LoggerFactory.getLogger(this.getClass());


    @RequestMapping(value = "/getExpirationMessage",method = RequestMethod.GET)
    public ResponseEntity<?> getExpirationMessage()
    {
        log.info(AppConstants.REQUEST_METHOD+" :: "+AppConstants.GET+
                AppConstants.REQUEST_URI+" :: /tokenExpired/getExpirationMessage");

        JSONObject obj = new JSONObject();
        obj.put("data",tokenExpireService.tokenExpired());
        obj.put("statusCode",400);
        return new ResponseEntity<>(obj, HttpStatus.OK);
    }

    @RequestMapping(value="/checkIfExpired", method = RequestMethod.POST)
    public ResponseEntity<?> checkIfExpired(@RequestBody TokenRequestVO tokenRequestVO)
    {
        log.info(AppConstants.REQUEST_METHOD+" :: "+AppConstants.POST+
                AppConstants.REQUEST_URI+" :: /tokenExpired/checkIfExpired"+
                AppConstants.REQUEST_BODY+" :: "+tokenRequestVO.toString());
        JSONObject jsonObject=new JSONObject();
//        int statusCode=tokenExpireService.checkIfExpired(tokenRequestVO.getAccessToken());
//        if(statusCode==0 || statusCode==100)
//        {
//            jsonObject.put("statusCode",HttpStatus.OK);
//            jsonObject.put("message","Token is valid");
//            return new ResponseEntity<>(jsonObject, HttpStatus.OK);
//
//        }
//        else
//        {
//            jsonObject.put("statusCode",HttpStatus.UNAUTHORIZED);
//            jsonObject.put("message","Token is invalid");
//            return new ResponseEntity<>(jsonObject, HttpStatus.OK);
//
//        }
        int statusCode=tokenExpireService.checkIfExpiredInDB(tokenRequestVO.getAccessToken());
        if(statusCode==100)
        {
            jsonObject.put("statusCode",HttpStatus.OK);
            jsonObject.put("message","Token is valid");
            return new ResponseEntity<>(jsonObject, HttpStatus.OK);

        }
        else
        {
            jsonObject.put("statusCode",HttpStatus.UNAUTHORIZED);
            jsonObject.put("message","Token is invalid");
            return new ResponseEntity<>(jsonObject, HttpStatus.OK);

        }
    }
}
