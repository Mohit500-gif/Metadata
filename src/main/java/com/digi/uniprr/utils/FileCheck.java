package com.digi.uniprr.utils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class FileCheck {

	public static boolean isfileAvail(String pdfPath) {
		try {
            URL url = new URL(pdfPath);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("HEAD");
            int responseCode = connection.getResponseCode();
            
            if (responseCode == HttpURLConnection.HTTP_OK) {
            	
                System.out.println("File exists at the given URL.");
                return true;
            } else {
            	
                System.out.println("File does not exist at the given URL.");
                return false;
            }
        } catch (IOException e) {
            System.out.println("An error occurred while checking the file existence: " + e.getMessage());
            return false;
        }
    }
	
}
