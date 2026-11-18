package com.digi.uniprr.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Test {

	public static void main(String[] args) throws IOException {

	  	String filePath = "D:\\db\\abc1.txt";
			String text="";
			Scanner scanner = new Scanner(System.in);
        FileInputStream excelFile = new FileInputStream(scanner.next());
        Workbook workbook = new XSSFWorkbook(excelFile);
        Sheet sheet = workbook.getSheetAt(0); // Assuming data is on the first sheet
        BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true));
        for (Row row : sheet) {
//            Cell cellA = row.getCell(0); // Assuming column A is the first column (index 0)
            Cell cellB = row.getCell(0); // Assuming column B is the second column (index 1)
            
	        
	            
            if (cellB != null) {
            
                   
                    createDirectory("D:\\db\\unitouch\\ijmr\\ijmr."+cellB.getStringCellValue()+"");
                    String valueB = cellB.getStringCellValue(); // Read column B as a string
                    
                    text = "open sftp://medknow@172.31.21.70 -password=medknow -hostkey=\"ssh-ed25519 255 HZR+PJ+hAZFDcSPzOI/YexuAgCM6/KASLNOF0RFTNvI\" \r\n"
                    		+ "lcd D:\\db\\unitouch\\ijmr\\ijmr."+valueB+"\r\n"
                    		+ "cd /medknow/ijmr/10.4103/ijmr."+valueB+"\r\n"
                    		+ "synchronize local D:\\db\\unitouch\\ijmr\\ijmr."+valueB+"\r\n"
                    		+ "\r\n";
    	           
    	           
    	            writer.write(text);
                    System.out.println(valueB);
                 
            }
        }
        writer.close();
        System.out.println("Text has been generated and appended for each ID in abc.txt.");
        excelFile.close();
	}
	
	
	public static void createDirectory(String directoryName) {
	    // Use File.separator to ensure the correct path separator is used
	    File directory = new File(directoryName);

	    if (directory.mkdir() || directory.exists()) {
	        System.out.println("Directory created or already exists: " + directory.getAbsolutePath());
	    } else {
	        System.out.println("Failed to create directory: " + directory.getAbsolutePath());
	    }
	}
}
