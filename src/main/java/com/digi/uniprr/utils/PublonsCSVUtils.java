package com.digi.uniprr.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVFormat.Builder;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.QuoteMode;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.HtmlUtils;

import com.digi.uniprr.VO.ReviewerPublonsCommentVO;

public final class PublonsCSVUtils {

	private final static Logger logger = LoggerFactory.getLogger(PublonsCSVUtils.class);
	
	private PublonsCSVUtils() {
	}

	public static void createCSV(String filePath, List<ReviewerPublonsCommentVO> reviewerPublonsCommentVOs) throws IOException {
		logger.info("checking file if exists...");
		Path path = Paths.get(filePath);
		logger.info("Absolute path: {}", path.toAbsolutePath());
		logger.info("Files.exists(path)? {}", Files.exists(path));

		File f = new File(filePath);
		logger.info("File.canRead(): {}  File.canWrite(): {}  isFile(): {}",
		            f.canRead(), f.canWrite(), f.isFile());
		
	    if (Files.exists(path)) {
	        
	        String timeStamp = new java.text.SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date());
	        String renamedFile = filePath + "." + timeStamp + ".bak";
	        Files.move(path, Paths.get(renamedFile), StandardCopyOption.REPLACE_EXISTING);
	        logger.info("Existing file renamed to: {}", renamedFile);
	    }else {
	        logger.info("File does not exist at path: {}", filePath);
	    }
		
		Builder csvBuilder = CSVFormat.Builder.create().setDelimiter("##");
		csvBuilder.setQuoteMode(QuoteMode.MINIMAL);
		csvBuilder.setHeader(getHeaders());
		
		logger.info("Wrting data to publon report....");
		final CSVFormat format = csvBuilder.build();
		try (FileOutputStream outputStream = new FileOutputStream(filePath);
			CSVPrinter csvPrinter = new CSVPrinter(new PrintWriter(outputStream), format);) {
			csvPrinter.printRecords(convertObject(reviewerPublonsCommentVOs));
			csvPrinter.flush();
		} 
		
		convertToExcel(filePath);
		Files.deleteIfExists(Paths.get(filePath));
	}
	
	private static void convertToExcel(String csvFileAddress) {
		String xlsxFileAddress =  csvFileAddress.replace(".csv", ".xlsx");
		
		logger.info("Converting the report to excel..");
		try {
			Files.deleteIfExists(Paths.get(xlsxFileAddress));
			XSSFWorkbook workBook = new XSSFWorkbook();
			XSSFSheet sheet = workBook.createSheet("sheet1");
			
			String currentLine=null;
	        int RowNum=0;
	        BufferedReader br = new BufferedReader(new FileReader(csvFileAddress));
	        while ((currentLine = br.readLine()) != null) {
	            String str[] = currentLine.split("##");
	            XSSFRow currentRow=sheet.createRow(RowNum);
	            for(int i=0;i<str.length;i++){
	                currentRow.createCell(i).setCellValue(str[i].replace("\"", ""));
	            }
	            RowNum++;
	        }
	        br.close();
	        
	        FileOutputStream fileOutputStream =  new FileOutputStream(xlsxFileAddress);
	        workBook.write(fileOutputStream);
	        fileOutputStream.close();
	        System.out.println("Done");
	        
		} catch (Exception e) {
			logger.error("Error ehile conversion....{}", e);
		}
		
	}

	private static String[] getHeaders() {
		List<String> headers = new ArrayList<>();
		headers.add("Reviewer First Name");
		headers.add("Reviewer Last Name");
		headers.add("E-mail Address");
		headers.add("Manuscript Number");
		headers.add("Revision Number");
		headers.add("Article Title");
		headers.add("Abstract");
		headers.add("Date Reviewer Invited");
		headers.add("Date Invitation Accepted");
		headers.add("Reviewer Stop Date");
		headers.add("Reviewer Recommendation");
		headers.add("Reviewer Comments to Author");
		headers.add("Handling Editor First Name");
		headers.add("Handling Editor Last Name");
		headers.add("Reviewer`s Response");

		return headers.toArray(new String[0]);
	}
	
	private static List<Object[]> convertObject(List<ReviewerPublonsCommentVO> reviewerPublonsCommentVOs) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
		
		return reviewerPublonsCommentVOs.stream().map(comment -> new Object[]{
		        comment.getReviewerFirstName(),
		        comment.getReviewerLastName(),
		        comment.getReviewerEmail(),
		        comment.getManuscriptCode(),
		        comment.getManuscriptVersion(),
		        parseText(comment.getManuscriptTitle()),           
		        parseText(comment.getManuscriptAbstract()),        
		        DateUtil.getFormattedDate(comment.getAssignedDate(), dateFormat),
		        DateUtil.getFormattedDate(comment.getAcceptDate(), dateFormat),
		        DateUtil.getFormattedDate(comment.getCompleteDate(), dateFormat),
		        parseText(comment.getReviewerRecomendation()),     
		        parseText(comment.getCommentsToAuthor()),          
		        comment.getEditorFirstName(),
		        comment.getEditorLastName(),
		        "Yes" 
		    }).collect(Collectors.toList());
		 
	}
	
	private static String parseText(String htmlContent) {
	    return htmlContent == null ? "" : Jsoup.parse(htmlContent).text();
	}

}