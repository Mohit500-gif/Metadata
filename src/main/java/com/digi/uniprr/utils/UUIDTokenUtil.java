package com.digi.uniprr.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class UUIDTokenUtil {

    @Value("${UUID.TOKEN}")
    private String globalToken; 

    @Value("${UUID.Uk.TOKEN}")
    private String globalUkToken;
    
    @Value("${UUID.REPORT.TOKEN}")
    private String reportToken;
    
    public String getReportToken() {
        return reportToken; 
    }

    public boolean isValidReportToken(String token) {
        return reportToken.equals(token); 
    }
    
    public String getGlobalToken() {
        return globalToken; 
    }

    public boolean isValidToken(String token) {
        return globalToken.equals(token); 
    }

	public String getGlobalUkTokenn() {
		return globalUkToken;
	}
    
	public boolean isValidUkToken(String token) {
        return globalUkToken.equals(token); 
    }
	
	
}
