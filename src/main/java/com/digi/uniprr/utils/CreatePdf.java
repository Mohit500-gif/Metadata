package com.digi.uniprr.utils;

import com.digi.uniprr.VO.EditorDashboardVO;
import com.digi.uniprr.model.Journal;
import com.digi.uniprr.model.ManuScriptFile;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import fr.opensagres.poi.xwpf.converter.pdf.PdfConverter;
import fr.opensagres.poi.xwpf.converter.pdf.PdfOptions;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.fit.pdfdom.PDFDomTree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreatePdf {
	

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	public void docxToPdf(File fileLocation, List<ManuScriptFile> manuscriptFile) 
			throws MalformedURLException, IOException, Exception {
		log.info("Inside docxToPdf");
		File outputFolderForPDF = new File(fileLocation + File.separator + "PDF");
		log.info("utputFolder Location :" + outputFolderForPDF);
		if (!outputFolderForPDF.exists()) {
			outputFolderForPDF.mkdir();
			log.info("Output folder created for PDF");
		}
		for (File fileEntry : fileLocation.listFiles()) {
			if (!fileEntry.isDirectory()) {
				if (fileEntry.getName().endsWith(".doc")  || fileEntry.getName().endsWith(".docx") ) {
					if (!fileEntry.getName().equalsIgnoreCase("ZYX.docx")) {
						log.info("Found doc file");
						String docxLocation = fileLocation.getAbsolutePath() + File.separator + fileEntry.getName();
						String fileName = fileEntry.getName().replace("docx", "").replace("doc", "").concat("pdf");
						String pdfLocation = fileLocation.getAbsolutePath() + File.separator + "PDF" + File.separator
								+ fileName;
						// call doc to pdf api
						try {
							String urlParameters = "inputFile=" + docxLocation + "&outputfilePath=" + pdfLocation
									+ "&outputFormat=pdf";
							log.info("urlParameters   ::::  " + urlParameters);
							byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
							int postDataLength = postData.length;
							String request = "http://127.0.0.1:8080/conversion/app/converterForUniprr";
							URL url = new URL(request);
							HttpURLConnection conn = (HttpURLConnection) url.openConnection();
							conn.setDoOutput(true);
							conn.setInstanceFollowRedirects(false);
							conn.setRequestMethod("POST");
							conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
							conn.setRequestProperty("charset", "utf-8");
							conn.setRequestProperty("Content-Length", Integer.toString(postDataLength));
							conn.setUseCaches(false);
							try (DataOutputStream wr = new DataOutputStream(conn.getOutputStream())) {
								wr.write(postData);
							}
							conn.getResponseCode();
							log.info("Status Code ::: " + conn.getResponseCode());
						} catch (Exception e) {
							log.info("Exception :::  " + e);
						}
					}
					// done call pdftodoc
				}
			}
		}

		log.info("All file Save in PDF folder");
		File pdfFileLocation = new File(fileLocation.getAbsolutePath() + File.separator + "PDF");
		log.info("Call mergeAllPdf ::  pdfFileLocation ::::" + pdfFileLocation + "   fileLocation  ::" + fileLocation);
		
		//new code start here
		ImageToPdf.imageToDocx(fileLocation, manuscriptFile);	//Convert image into Docx
		convertImageDocxToPdf(fileLocation);					//convert image docx in to pdf
		//mergeAllPdfTest(fileLocation, fileLocation,manuscriptFile);
		//mergeAllPdfForReviewerTest(pdfFileLocation, fileLocation, manuscriptFile);
		// new code end here if anything is not working file please comment new line and  uncomment below 3 lines
		
		//pdfToHtml(fileLocation,manuscriptFile);
		


	}

	private void mergeAllPdfForReviewerTest(File fileLocationPfd, File location,
			List<ManuScriptFile> manuscriptFile) throws Exception {

		log.info("Inside mergeAllPdfForReviewer");
		log.info("File size : " + manuscriptFile.size());
		// File fileLocationForPfd = new File(location + File.separator + "PDF");
		File fileForPdf = new File(location + File.separator + "DocxImagePDF");
		PDFMergerUtility PDFmerger = new PDFMergerUtility();


			File output = new File(location.getAbsolutePath() + File.separator + "output" + File.separator
					+ "BundledPdfForReviewer.pdf");
			if (output.createNewFile()) {
				log.info("File created: " + output.getName());
			} else {
				log.info("File already exists.");
			}

			PDFmerger.setDestinationFileName(output.toString());
			
			if(fileLocationPfd.listFiles() != null)
			{
			for (File fileEntry : fileLocationPfd.listFiles()) {
				if (fileEntry.getName().equalsIgnoreCase("Manuscript.pdf")) {
					File file = new File(fileLocationPfd.getAbsolutePath() + File.separator + fileEntry.getName());
					PDFmerger.addSource(file);
				}
			}
			}
						
			for (ManuScriptFile manuScriptFile2 : manuscriptFile) {
				int pos = manuScriptFile2.getFileName().lastIndexOf(".");
				String uploadFileName = manuScriptFile2.getFileName().substring(0, pos);
				
				for (File fileEntry : fileLocationPfd.listFiles()) {
					if (!fileEntry.getName().equalsIgnoreCase("FirstPage.pdf")
							&& !fileEntry.getName().equalsIgnoreCase("Manuscript.pdf")
							&& FilenameUtils.removeExtension(fileEntry.getName()).equalsIgnoreCase(uploadFileName)
							&& (manuScriptFile2.getFileType().equalsIgnoreCase("Figures"))) {
						
						log.info("FileName :" + manuScriptFile2.getFileType() + " : " + manuScriptFile2.getFileName());
						File file = new File(fileLocationPfd.getAbsolutePath() + File.separator + fileEntry.getName());
						PDFmerger.addSource(file);
					}

				}
			if(fileForPdf.listFiles() != null ) {
			for (File fileEntry : fileForPdf.listFiles()) {
					
					if (FilenameUtils.removeExtension(fileEntry.getName()).equalsIgnoreCase(uploadFileName)) {
						log.info("FileName :" + manuScriptFile2.getFileType() + " : " + manuScriptFile2.getFileName());
						File file = new File(fileForPdf.getAbsolutePath() + File.separator + fileEntry.getName());
						PDFmerger.addSource(file);
					}

				}
			}
			if(fileForPdf.listFiles() != null ) {
			for (File fileEntry : location.listFiles()) {
			if (!fileEntry.isDirectory() && fileEntry.getName().endsWith(".pdf") && !fileEntry.getName().equalsIgnoreCase("FirstPage.pdf")
					&& !fileEntry.getName().equalsIgnoreCase("Manuscript.pdf")
					&& FilenameUtils.removeExtension(fileEntry.getName()).equalsIgnoreCase(uploadFileName)
					&& (manuScriptFile2.getFileType().equalsIgnoreCase("Figures"))) {
				File file = new File(location.getAbsolutePath() + File.separator + fileEntry.getName());
				PDFmerger.addSource(file);
				log.info("File Name ::::" + file.getName());
			}
		}
	}
}
			
			
			for (ManuScriptFile manuScriptFile2 : manuscriptFile) {
				int pos = manuScriptFile2.getFileName().lastIndexOf(".");
				String uploadFileName = manuScriptFile2.getFileName().substring(0, pos);
				
				if(fileLocationPfd.listFiles() != null) {
				for (File fileEntry : fileLocationPfd.listFiles()) {
					if (!fileEntry.getName().equalsIgnoreCase("FirstPage.pdf")
							&& !fileEntry.getName().equalsIgnoreCase("Manuscript.pdf")
							&& FilenameUtils.removeExtension(fileEntry.getName()).equalsIgnoreCase(uploadFileName)
							&& (manuScriptFile2.getFileType().equalsIgnoreCase("Tables"))) {
						
						log.info("FileName :" + manuScriptFile2.getFileType() + " : " + manuScriptFile2.getFileName());
						File file = new File(fileLocationPfd.getAbsolutePath() + File.separator + fileEntry.getName());
						PDFmerger.addSource(file);
					}

				}
				
				}
				 if(location.listFiles()!=null) {
				for (File fileEntry : location.listFiles()) {
					if (!fileEntry.isDirectory() && fileEntry.getName().endsWith(".pdf") && !fileEntry.getName().equalsIgnoreCase("FirstPage.pdf")
							&& !fileEntry.getName().equalsIgnoreCase("Manuscript.pdf")
							&& FilenameUtils.removeExtension(fileEntry.getName()).equalsIgnoreCase(uploadFileName)
							&& (manuScriptFile2.getFileType().equalsIgnoreCase("Tables"))) {
						File file = new File(location.getAbsolutePath() + File.separator + fileEntry.getName());
						PDFmerger.addSource(file);
						log.info("File Name ::::" + file.getName());
					}
				}
				 }
			}
			
			for (ManuScriptFile manuScriptFile2 : manuscriptFile) {
				int pos = manuScriptFile2.getFileName().lastIndexOf(".");
				String uploadFileName = manuScriptFile2.getFileName().substring(0, pos);
				
				if(fileLocationPfd.listFiles() != null) {
				for (File fileEntry : fileLocationPfd.listFiles()) {
					if (!fileEntry.getName().equalsIgnoreCase("FirstPage.pdf")
							&& !fileEntry.getName().equalsIgnoreCase("Manuscript.pdf")
							&& FilenameUtils.removeExtension(fileEntry.getName()).equalsIgnoreCase(uploadFileName)
							&& (manuScriptFile2.getFileType().equalsIgnoreCase("Comments to Referees"))) {
						
						log.info("FileName :" + manuScriptFile2.getFileType() + " : " + manuScriptFile2.getFileName());
						File file = new File(fileLocationPfd.getAbsolutePath() + File.separator + fileEntry.getName());
						PDFmerger.addSource(file);
					}

				}
				
				}
				 if(location.listFiles()!=null) {
				for (File fileEntry : location.listFiles()) {
					if (!fileEntry.isDirectory() && fileEntry.getName().endsWith(".pdf") && !fileEntry.getName().equalsIgnoreCase("FirstPage.pdf")
							&& !fileEntry.getName().equalsIgnoreCase("Manuscript.pdf")
							&& FilenameUtils.removeExtension(fileEntry.getName()).equalsIgnoreCase(uploadFileName)
							&& (manuScriptFile2.getFileType().equalsIgnoreCase("Comments to Referees"))) {
						File file = new File(location.getAbsolutePath() + File.separator + fileEntry.getName());
						PDFmerger.addSource(file);
						log.info("File Name ::::" + file.getName());
					}
				}
				 }
			}
											
			PDFmerger.mergeDocuments();
		//	Thread.sleep(3000);
			
			log.info("Documents merged");
	
		
	}

	private void mergeAllPdfTest(File fileLocation, File location, List<ManuScriptFile> manuscriptFile) throws Exception{

		log.info("Inside mergeAllPdf");
		PDFMergerUtility PDFmerger = new PDFMergerUtility();
		File fileLocationPfd = new File(fileLocation + File.separator + "PDF");
		File fileForPdf = new File(fileLocation + File.separator + "DocxImagePDF");
		log.info("fileLocationPfd : " + fileLocationPfd + " : " + "location : " + location);

			File output = new File(
					location.getAbsolutePath() + File.separator + "output" + File.separator + "Bundled.pdf");

			log.info("PDF File Location :::" + location.getAbsolutePath() + File.separator + "output" + File.separator
					+ "Bundled.pdf");

			if (output.createNewFile()) {
				log.info("File created: " + output.getName());
			} else {
				log.info("File already exists.");
			}
			log.info("output : " + output);
			PDFmerger.setDestinationFileName(output.toString());
           
			if(fileLocationPfd.listFiles() != null)
			{
			for (File fileEntry : fileLocationPfd.listFiles()) {
				if (fileEntry.getName().equalsIgnoreCase("FirstPage.pdf")) {
					File file = new File(fileLocationPfd.getAbsolutePath() + File.separator + fileEntry.getName());
					PDFmerger.addSource(file);
					log.info("File Name ::::" + file.getName());
				}
			}
			for (File fileEntry : fileLocationPfd.listFiles()) {
				if (fileEntry.getName().equalsIgnoreCase("Manuscript.pdf")) {
					File file = new File(fileLocationPfd.getAbsolutePath() + File.separator + fileEntry.getName());
					PDFmerger.addSource(file);
					log.info("File Name ::::" + file.getName());
				}
			}
			
		
			for (ManuScriptFile manuScriptFile2 : manuscriptFile) {
				int pos = manuScriptFile2.getFileName().lastIndexOf(".");
				String uploadFileName = manuScriptFile2.getFileName().substring(0, pos);
				
				for (File fileEntry : fileLocationPfd.listFiles()) {
					if (!fileEntry.getName().equalsIgnoreCase("FirstPage.pdf")
							&& !fileEntry.getName().equalsIgnoreCase("Manuscript.pdf")
							&& FilenameUtils.removeExtension(fileEntry.getName()).equalsIgnoreCase(uploadFileName)
							&& (manuScriptFile2.getFileType().equalsIgnoreCase("Figures"))) {
						
						log.info("FileName :" + manuScriptFile2.getFileType() + " : " + manuScriptFile2.getFileName());
						File file = new File(fileLocationPfd.getAbsolutePath() + File.separator + fileEntry.getName());
						PDFmerger.addSource(file);
					}

				}
				
			if(fileForPdf.listFiles() != null)
			{
				
			for (File fileEntry : fileForPdf.listFiles()) {
					
					if (FilenameUtils.removeExtension(fileEntry.getName()).equalsIgnoreCase(uploadFileName)) {
						log.info("FileName :" + manuScriptFile2.getFileType() + " : " + manuScriptFile2.getFileName());
						File file = new File(fileForPdf.getAbsolutePath() + File.separator + fileEntry.getName());
						PDFmerger.addSource(file);
					}

				}
			}

			for (File fileEntry : fileLocation.listFiles()) {
			if (!fileEntry.isDirectory() && fileEntry.getName().endsWith(".pdf") && !fileEntry.getName().equalsIgnoreCase("FirstPage.pdf")
					&& !fileEntry.getName().equalsIgnoreCase("Manuscript.pdf")
					&& FilenameUtils.removeExtension(fileEntry.getName()).equalsIgnoreCase(uploadFileName)
					&& (manuScriptFile2.getFileType().equalsIgnoreCase("Figures"))) {
				File file = new File(fileLocation.getAbsolutePath() + File.separator + fileEntry.getName());
				PDFmerger.addSource(file);
				log.info("File Name ::::" + file.getName());
			}
		}

	}
			
			
			for (ManuScriptFile manuScriptFile2 : manuscriptFile) {
				int pos = manuScriptFile2.getFileName().lastIndexOf(".");
				String uploadFileName = manuScriptFile2.getFileName().substring(0, pos);
				
				for (File fileEntry : fileLocationPfd.listFiles()) {
					if (!fileEntry.getName().equalsIgnoreCase("FirstPage.pdf")
							&& !fileEntry.getName().equalsIgnoreCase("Manuscript.pdf")
							&& FilenameUtils.removeExtension(fileEntry.getName()).equalsIgnoreCase(uploadFileName)
							&& (manuScriptFile2.getFileType().equalsIgnoreCase("Tables"))) {
						
						log.info("FileName :" + manuScriptFile2.getFileType() + " : " + manuScriptFile2.getFileName());
						File file = new File(fileLocationPfd.getAbsolutePath() + File.separator + fileEntry.getName());
						PDFmerger.addSource(file);
					}

				}
				
				for (File fileEntry : fileLocation.listFiles()) {
					if (!fileEntry.isDirectory() && fileEntry.getName().endsWith(".pdf") && !fileEntry.getName().equalsIgnoreCase("FirstPage.pdf")
							&& !fileEntry.getName().equalsIgnoreCase("Manuscript.pdf")
							&& FilenameUtils.removeExtension(fileEntry.getName()).equalsIgnoreCase(uploadFileName)
							&& (manuScriptFile2.getFileType().equalsIgnoreCase("Tables"))) {
						File file = new File(fileLocation.getAbsolutePath() + File.separator + fileEntry.getName());
						PDFmerger.addSource(file);
						log.info("File Name ::::" + file.getName());
					}
				}
			
			}	
			
			for (ManuScriptFile manuScriptFile2 : manuscriptFile) {
				int pos = manuScriptFile2.getFileName().lastIndexOf(".");
				String uploadFileName = manuScriptFile2.getFileName().substring(0, pos);
				
				for (File fileEntry : fileLocationPfd.listFiles()) {
					if (!fileEntry.getName().equalsIgnoreCase("FirstPage.pdf")
							&& !fileEntry.getName().equalsIgnoreCase("Manuscript.pdf")
							&& FilenameUtils.removeExtension(fileEntry.getName()).equalsIgnoreCase(uploadFileName)
							&& (manuScriptFile2.getFileType().equalsIgnoreCase("Comments to Referees"))) {
						
						log.info("FileName :" + manuScriptFile2.getFileType() + " : " + manuScriptFile2.getFileName());
						File file = new File(fileLocationPfd.getAbsolutePath() + File.separator + fileEntry.getName());
						PDFmerger.addSource(file);
					}

				}
				
				for (File fileEntry : fileLocation.listFiles()) {
					if (!fileEntry.isDirectory() && fileEntry.getName().endsWith(".pdf") && !fileEntry.getName().equalsIgnoreCase("FirstPage.pdf")
							&& !fileEntry.getName().equalsIgnoreCase("Manuscript.pdf")
							&& FilenameUtils.removeExtension(fileEntry.getName()).equalsIgnoreCase(uploadFileName)
							&& (manuScriptFile2.getFileType().equalsIgnoreCase("Comments to Referees"))) {
						File file = new File(fileLocation.getAbsolutePath() + File.separator + fileEntry.getName());
						PDFmerger.addSource(file);
						log.info("File Name ::::" + file.getName());
					}
				}
			
			}	
			
			}

			PDFmerger. mergeDocuments();
			

		log.info("Documents merged");
	
		
	}

	private void convertImageDocxToPdf(File fileLocationPath) throws Exception{
		File fileLocation = new File (fileLocationPath + File.separator +"DocxForImage");
		if(fileLocation.exists() && fileLocation.isDirectory()) {
		for (File fileEntry : fileLocation.listFiles()) {
			if (!fileEntry.isDirectory()) {
				if (fileEntry.getName().endsWith(".doc")  || fileEntry.getName().endsWith(".docx") ) {
					if (!fileEntry.getName().equalsIgnoreCase("ZYX.docx")) {
						log.info("Found doc file");
						String docxLocation = fileLocation.getAbsolutePath() + File.separator + fileEntry.getName();
						String fileName = fileEntry.getName().replace("docx", "").replace("doc", "").concat("pdf");
						File DocxForImage = new File(fileLocationPath + File.separator + "DocxImagePDF");
						 log.info("outputFolder Location :" + DocxForImage);
						if (!DocxForImage.exists()) {
							DocxForImage.mkdir();
							 log.info("Output folder created for PDF Image");
						}
						String pdfLocation = fileLocationPath.getAbsolutePath() + File.separator + "DocxImagePDF" + File.separator
								+ fileName;
						// call doc to pdf api
						try {
							String urlParameters = "inputFile=" + docxLocation + "&outputfilePath=" + pdfLocation
									+ "&outputFormat=pdf";
							log.info("urlParameters   ::::  " + urlParameters);
							byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
							int postDataLength = postData.length;
							String request = "http://127.0.0.1:8080/conversion/app/converterForUniprr";
							URL url = new URL(request);
							HttpURLConnection conn = (HttpURLConnection) url.openConnection();
							conn.setDoOutput(true);
							conn.setInstanceFollowRedirects(false);
							conn.setRequestMethod("POST");
							conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
							conn.setRequestProperty("charset", "utf-8");
							conn.setRequestProperty("Content-Length", Integer.toString(postDataLength));
							conn.setUseCaches(false);
							try (DataOutputStream wr = new DataOutputStream(conn.getOutputStream())) {
								wr.write(postData);
							}
							conn.getResponseCode();
							log.info("Status Code ::: " + conn.getResponseCode());
						} catch (Exception e) {
							log.info("Exception :::  " + e);
						}
					}
					// done call pdftodoc
				}
			}
		}
		}
		
	}

	public void pdfToHtml(File fileLocation, List<ManuScriptFile> manuscriptFile) throws Exception{
		log.info("Inside pdfToHtml");
		String str = fileLocation.getAbsolutePath() + File.separator + "output" + File.separator;
			File htmlFile = new File(fileLocation + File.separator + "output" + File.separator + "Bundled.html");
			htmlFile.createNewFile();
			log.info("HTML location : " + htmlFile);
			File pdfFile = new File(str + "Bundled.pdf");
			pdfFile.createNewFile();
			PDDocument pdf = PDDocument.load(new File(pdfFile.toString()));

			log.info("PDF location : " + pdf);
			Writer output = new PrintWriter(htmlFile, "utf-8");
			new PDFDomTree().writeText(pdf, output);
           // Thread.sleep(2000);
			output.close();

	}

	public void mergeAllPdf(File fileLocation, File location) {
		log.info("Inside mergeAllPdf");
		PDFMergerUtility PDFmerger = new PDFMergerUtility();
		File fileLocationPfd = new File(fileLocation + File.separator + "PDF");
		File fileForPdf = new File(fileLocation + File.separator + "PDFForImage");
		log.info("fileLocationPfd : " + fileLocationPfd + " : " + "location : " + location);

		try {
			File output = new File(
					location.getAbsolutePath() + File.separator + "output" + File.separator + "Bundled.pdf");

			log.info("PDF File Location :::" + location.getAbsolutePath() + File.separator + "output" + File.separator
					+ "Bundled.pdf");

			if (output.createNewFile()) {
				log.info("File created: " + output.getName());
			} else {
				log.info("File already exists.");
			}
			log.info("output : " + output);
			PDFmerger.setDestinationFileName(output.toString());

			for (File fileEntry : fileLocationPfd.listFiles()) {
				if (fileEntry.getName().equalsIgnoreCase("FirstPage.pdf")) {
					File file = new File(fileLocationPfd.getAbsolutePath() + File.separator + fileEntry.getName());
					PDFmerger.addSource(file);
					log.info("File Name ::::" + file.getName());
				}
			}
			for (File fileEntry : fileLocationPfd.listFiles()) {
				if (fileEntry.getName().equalsIgnoreCase("Manuscript.pdf")) {
					File file = new File(fileLocationPfd.getAbsolutePath() + File.separator + fileEntry.getName());
					PDFmerger.addSource(file);
					log.info("File Name ::::" + file.getName());
				}
			}

			for (File fileEntry : fileLocationPfd.listFiles()) {
				if (!fileEntry.getName().equalsIgnoreCase("FirstPage.pdf")
						&& !fileEntry.getName().equalsIgnoreCase("Manuscript.pdf")) {
					File file = new File(fileLocationPfd.getAbsolutePath() + File.separator + fileEntry.getName());
					PDFmerger.addSource(file);
					log.info("File Name ::::" + file.getName());
				}
			}
			//merge pdf file comes from front end
			for (File fileEntry : fileLocation.listFiles()) {
				if (!fileEntry.isDirectory() && fileEntry.getName().endsWith(".pdf")) {
					File file = new File(fileLocation.getAbsolutePath() + File.separator + fileEntry.getName());
					PDFmerger.addSource(file);
					log.info("File Name ::::" + file.getName());
				}
			}
			
			
			if (fileForPdf.exists() && fileForPdf.isDirectory()) {
				for (File fileEntry : fileForPdf.listFiles()) {
					if (fileEntry != null && fileEntry.exists()) {
						File file = new File(fileForPdf.getAbsolutePath() + File.separator + fileEntry.getName());
						PDFmerger.addSource(file);
					}
				}
			}

			PDFmerger.mergeDocuments();
		} catch (Exception e) {
			log.info("Exception : " + e);
		}
		log.info("Documents merged");
	}

	public void mergeAllPdfForReviewer(File fileLocationPfd, File location, List<ManuScriptFile> manuscriptFile) {
		log.info("Inside mergeAllPdfForReviewer");
		log.info("File size : " + manuscriptFile.size());
		// File fileLocationForPfd = new File(location + File.separator + "PDF");
		File fileForPdf = new File(location + File.separator + "PDFForImage");
		PDFMergerUtility PDFmerger = new PDFMergerUtility();

		try {
			File output = new File(location.getAbsolutePath() + File.separator + "output" + File.separator
					+ "BundledPdfForReviewer.pdf");
			if (output.createNewFile()) {
				log.info("File created: " + output.getName());
			} else {
				log.info("File already exists.");
			}

			PDFmerger.setDestinationFileName(output.toString());
			for (File fileEntry : fileLocationPfd.listFiles()) {
				if (fileEntry.getName().equalsIgnoreCase("Manuscript.pdf")) {
					File file = new File(fileLocationPfd.getAbsolutePath() + File.separator + fileEntry.getName());
					PDFmerger.addSource(file);
				}
			}
			
			for (File fileEntry : fileLocationPfd.listFiles()) {
				if (!fileEntry.getName().equalsIgnoreCase("FirstPage.pdf") && !fileEntry.getName().equalsIgnoreCase("Manuscript.pdf")) {
					File file = new File(fileLocationPfd.getAbsolutePath() + File.separator + fileEntry.getName());
					PDFmerger.addSource(file);
				}
			}

//			for (ManuScriptFile manuScriptFile2 : manuscriptFile) {
//
//				for (File fileEntry : fileLocationPfd.listFiles()) {
//					if (!fileEntry.getName().equalsIgnoreCase("FirstPage.pdf")
//							&& !fileEntry.getName().equalsIgnoreCase("Manuscript.pdf")
//							&& fileEntry.getName().equalsIgnoreCase(manuScriptFile2.getFileName())
//							&& (manuScriptFile2.getFileType().equalsIgnoreCase("Figures")
//									|| manuScriptFile2.getFileType().equalsIgnoreCase("Tables")
//									|| manuScriptFile2.getFileType().equalsIgnoreCase("Supplementary files"))) {
//						log.info("FileName :" + manuScriptFile2.getFileType() + " : " + manuScriptFile2.getFileName());
//						File file = new File(fileLocationPfd.getAbsolutePath() + File.separator + fileEntry.getName());
//						PDFmerger.addSource(file);
//					}
//
//				}
//			}
			//merge pdf file comes from front end
			for (File fileEntry : location.listFiles()) {
				if (!fileEntry.isDirectory() && fileEntry.getName().endsWith(".pdf")) {
					File file = new File(location.getAbsolutePath() + File.separator + fileEntry.getName());
					PDFmerger.addSource(file);
					log.info("File Name ::::" + file.getName());
				}
			}
			
			if (fileForPdf.exists() && fileForPdf.isDirectory()) {
				for (File fileEntry : fileForPdf.listFiles()) {
					if (fileEntry != null && fileEntry.exists()) {
						File file = new File(fileForPdf.getAbsolutePath() + File.separator + fileEntry.getName());
						PDFmerger.addSource(file);
					}
				}
			}

			PDFmerger.mergeDocuments();
		} catch (Exception e) {
			log.info("Exception : " + e);
		}
		log.info("Documents merged");
	}

	public void ConvertDocxToPDF(String docPath, String pdfPath) {
		log.info("Inside ConvertToPDF" + docPath);
		try {
			InputStream doc = new FileInputStream(new File(docPath));
			XWPFDocument document = new XWPFDocument(doc);
			PdfOptions options = PdfOptions.create();
			OutputStream out = new FileOutputStream(new File(pdfPath));
			PdfConverter.getInstance().convert(document, out, options);
			log.info("PDF creation Done");
		} catch (FileNotFoundException ex) {
			log.info(ex.getMessage());
		} catch (IOException ex) {

			log.info(ex.getMessage());
		}
	}

	public void insertImage(File imageLocation) {
		log.info("Inside insertImage");
		PDFMergerUtility PDFmerger = new PDFMergerUtility();
		File fileForPdf = new File(imageLocation + File.separator + "PDFForImage");
		try {
			File output = new File(
					imageLocation.getAbsolutePath() + File.separator + "output" + File.separator + "Bundled.pdf");
			if (output.createNewFile()) {
				log.info("File created: " + output.getName());
			} else {
				log.info("File already exists.");
			}

			PDFmerger.setDestinationFileName(output.toString());
			for (File fileEntry : fileForPdf.listFiles()) {
				File file = new File(fileForPdf.getAbsolutePath() + File.separator + fileEntry.getName());

				PDFmerger.addSource(file);

				log.info("File Name ::::" + file.getName());
			}
			PDFmerger.mergeDocuments();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void insertImageForReviewer(File imageLocation) {

		log.info("Inside insertImage For Reviewer");
		PDFMergerUtility PDFmerger = new PDFMergerUtility();
		File fileForPdf = new File(imageLocation + File.separator + "PDFForImage");
		try {
			File output = new File(imageLocation.getAbsolutePath() + File.separator + "output" + File.separator
					+ "BundledPdfForReviewer.pdf");
			if (output.createNewFile()) {
				log.info("File created: " + output.getName());
			} else {
				log.info("File already exists.");
			}

			PDFmerger.setDestinationFileName(output.toString());
			for (File fileEntry : fileForPdf.listFiles()) {
				File file = new File(fileForPdf.getAbsolutePath() + File.separator + fileEntry.getName());

				PDFmerger.addSource(file);

				log.info("File Name ::::" + file.getName());
			}
			PDFmerger.mergeDocuments();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void convertImageToPdf(File imageLocation, List<ManuScriptFile> manuscriptFile)
			throws Exception, MalformedURLException, IOException {
		for (File fileEntry : imageLocation.listFiles()) {
			if (fileEntry.getName().endsWith(".gif") || fileEntry.getName().endsWith(".png")
					|| fileEntry.getName().endsWith(".bmg") || fileEntry.getName().endsWith(".jpg")
					|| fileEntry.getName().endsWith(".jpeg") || fileEntry.getName().endsWith(".GIF") || fileEntry.getName().endsWith(".PNG")
					|| fileEntry.getName().endsWith(".BMG") || fileEntry.getName().endsWith(".JPG")
					|| fileEntry.getName().endsWith(".JPEG") || fileEntry.getName().endsWith(".tiff") ||  fileEntry.getName().endsWith(".tif")) {
				for (ManuScriptFile manuScriptFile2 : manuscriptFile) {
					if (manuScriptFile2.getFileName().equals(fileEntry.getName())) {
						File PDFForImage = new File(imageLocation + File.separator + "PDFForImage");
						log.info("outputFolder Location :" + PDFForImage);
						if (!PDFForImage.exists()) {
							PDFForImage.mkdir();
							log.info("Output folder created for PDF Image");
						}
						Image img = Image
								.getInstance(imageLocation.getAbsolutePath() + File.separator + fileEntry.getName());
						Document document = new Document(img);
						PdfWriter writer = PdfWriter.getInstance(document,
								new FileOutputStream(PDFForImage + File.separator
										+ fileEntry.getName().replace(".gif", ".pdf").replace(".png", ".pdf")
												.replace(".bmg", ".pdf").replace(".jpg", ".pdf")
												.replace(".jpeg", ".pdf").replace(".GIF", ".pdf").replace(".PNG", ".pdf")
												.replace(".BMG", ".pdf").replace(".JPG", ".pdf")
												.replace(".JPEG", ".pdf").replace(".tiff", ".pdf").replace(".tif", ".pdf")));
						document.open();
						document.setPageSize(img);
						document.newPage();
						img.setAbsolutePosition(0, 0);
						img.setAlignment(Image.MIDDLE);

						String legend = "";
						if (manuScriptFile2.getLegend() != null) {
							legend = manuScriptFile2.getLegend();
						}

						writer.getDirectContent().addImage(img, true);
						ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, new Phrase(legend),
								400, 5, 0);

						document.add(img);

						document.close();
					}
				}
			}
		}
	}
	
	public void docxToPdfNew(File fileLocation, List<ManuScriptFile> manuscriptFile) 
			throws MalformedURLException, IOException, Exception {
		log.info("Inside docxToPdf");
		File outputFolderForPDF = new File(fileLocation + File.separator + "PDF");
		log.info("utputFolder Location :" + outputFolderForPDF);
		if (!outputFolderForPDF.exists()) {
			outputFolderForPDF.mkdir();
			log.info("Output folder created for PDF");
		}
		for (File fileEntry : fileLocation.listFiles()) {
			if (!fileEntry.isDirectory()) {
				if (fileEntry.getName().endsWith(".doc")  || fileEntry.getName().endsWith(".docx") ) {
					if (!fileEntry.getName().equalsIgnoreCase("ZYX.docx")) {
						log.info("Found doc file");
						String docxLocation = fileLocation.getAbsolutePath() + File.separator + fileEntry.getName();
						String fileName = fileEntry.getName().replace("docx", "").replace("doc", "").concat("pdf");
						String pdfLocation = fileLocation.getAbsolutePath() + File.separator + "PDF" + File.separator
								+ fileName;
						// call doc to pdf api
						try {
							String urlParameters = "inputFile=" + docxLocation + "&outputfilePath=" + pdfLocation
									+ "&outputFormat=pdf";
							log.info("urlParameters   ::::  " + urlParameters);
							byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
							int postDataLength = postData.length;
							String request = "http://127.0.0.1:8080/conversion/app/converterForUniprr";
							URL url = new URL(request);
							HttpURLConnection conn = (HttpURLConnection) url.openConnection();
							conn.setDoOutput(true);
							conn.setInstanceFollowRedirects(false);
							conn.setRequestMethod("POST");
							conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
							conn.setRequestProperty("charset", "utf-8");
							conn.setRequestProperty("Content-Length", Integer.toString(postDataLength));
							conn.setUseCaches(false);
							try (DataOutputStream wr = new DataOutputStream(conn.getOutputStream())) {
								wr.write(postData);
							}
							conn.getResponseCode();
							log.info("Status Code ::: " + conn.getResponseCode());
						} catch (Exception e) {
							log.info("Exception :::  " + e);
						}
					}
					// done call pdftodoc
				}
			}
		}

		log.info("All file Save in PDF folder");
		File pdfFileLocation = new File(fileLocation.getAbsolutePath() + File.separator + "PDF");
		log.info("Call mergeAllPdf ::  pdfFileLocation ::::" + pdfFileLocation + "   fileLocation  ::" + fileLocation);
		
		//new code start here
		ImageToPdf.imageToDocx(fileLocation, manuscriptFile);	//Convert image into Docx
		convertImageDocxToPdf(fileLocation);					//convert image docx in to pdf
		mergeAllPdfTest(fileLocation, fileLocation,manuscriptFile);
		mergeAllPdfForReviewerTest(pdfFileLocation, fileLocation, manuscriptFile);
		 //new code end here if anything is not working file please comment new line and  uncomment below 3 lines
		
		pdfToHtml(fileLocation,manuscriptFile);
		


	}
	
	void javaBundleAPI(File location, List<ManuScriptFile> manuscriptFile, File outputFolder) throws Exception {
		File fileFordocx = new File(location + File.separator + "DocxForImage");

		// Create Final File if not exist
		File output = new File(outputFolder.getAbsoluteFile() + File.separator + "Bundled.docx");
		output.createNewFile(); // if file already exists will do nothing
		FileOutputStream finalOutput = new FileOutputStream(output, false);

		TestMergeDocx finalCode = new TestMergeDocx(finalOutput);

		TestMergeDocx c = new TestMergeDocx(null);
		
		if(location.listFiles() != null)
		{
		for (File fileEntry : location.listFiles()) {
			if (fileEntry.getName().endsWith(".docx")) {
				if (fileEntry.getName().equalsIgnoreCase("manuscript.docx")) {
					finalCode.add(new FileInputStream(fileEntry.getAbsoluteFile()));
				}
			}
		}


		
		for (ManuScriptFile manuScriptFile2 : manuscriptFile) {
			int pos = manuScriptFile2.getFileName().lastIndexOf(".");
			String uploadFileName = manuScriptFile2.getFileName().substring(0, pos);
			
			for (File fileEntry : location.listFiles()) {
				if (FilenameUtils.removeExtension(fileEntry.getName()).equalsIgnoreCase(uploadFileName) && (fileEntry.getName().endsWith(".docx") || fileEntry.getName().endsWith(".doc"))
						&& (manuScriptFile2.getFileType().equalsIgnoreCase("Figures"))) {
					
					log.info("FileName :" + manuScriptFile2.getFileType() + " : " + manuScriptFile2.getFileName());
					finalCode.add(new FileInputStream(fileEntry.getAbsoluteFile()));

				}

			}
			
		if(fileFordocx.listFiles() != null)
		{
		for (File fileEntry : fileFordocx.listFiles()) {
				
				if (FilenameUtils.removeExtension(fileEntry.getName()).equalsIgnoreCase(uploadFileName)) {
					log.info("FileName :" + manuScriptFile2.getFileType() + " : " + manuScriptFile2.getFileName());
					finalCode.add(new FileInputStream(fileEntry.getAbsoluteFile()));

				}

			}
		}
		
		}
		
		
		for (ManuScriptFile manuScriptFile2 : manuscriptFile) {
			int pos = manuScriptFile2.getFileName().lastIndexOf(".");
			String uploadFileName = manuScriptFile2.getFileName().substring(0, pos);
			
			for (File fileEntry : location.listFiles()) {
				if (FilenameUtils.removeExtension(fileEntry.getName()).equalsIgnoreCase(uploadFileName) && (fileEntry.getName().endsWith(".docx") || fileEntry.getName().endsWith(".doc"))
						&& (manuScriptFile2.getFileType().equalsIgnoreCase("Tables"))) {
					
					log.info("FileName :" + manuScriptFile2.getFileType() + " : " + manuScriptFile2.getFileName());
					finalCode.add(new FileInputStream(fileEntry.getAbsoluteFile()));
				}

			}
		
		}
		
		for (ManuScriptFile manuScriptFile2 : manuscriptFile) {
			int pos = manuScriptFile2.getFileName().lastIndexOf(".");
			String uploadFileName = manuScriptFile2.getFileName().substring(0, pos);
			
			for (File fileEntry : location.listFiles()) {
				if (FilenameUtils.removeExtension(fileEntry.getName()).equalsIgnoreCase(uploadFileName) && (fileEntry.getName().endsWith(".docx") || fileEntry.getName().endsWith(".doc"))
						&& (manuScriptFile2.getFileType().equalsIgnoreCase("Comments to Referees"))) {
					
					log.info("FileName :" + manuScriptFile2.getFileType() + " : " + manuScriptFile2.getFileName());
					finalCode.add(new FileInputStream(fileEntry.getAbsoluteFile()));
				}

			}
		
		}
		}
		
		log.info("---------------->Test Seven");
		long ststtime = System.currentTimeMillis();
		finalCode.doMerge();

	}
	
	
//	public void genrateCertificate(EditorDashboardVO requestVo, Journal journal, String fileName, String certificateDefaultImagePath, String feImageUrl, String journalImagePath, String thumbnailUrl) {
//		  log.info("Started building the Reviewer Certificate");
//		  try {
//		   String authorName = "";
//		   String manuscriptName = "";
//		   String manuscriptCode = "";
//		   String journalName = "";
//		   String eicNameAndSig = "";
//		   String reviewComletionDate = "";
//		   String downloadDate = "";
//		   String journalThumbnailImagePath = "";
//		   String eicSig = "";
//		   String htmlFilePath = certificateDefaultImagePath+"sample.html";
//		   boolean hasSignature = false;
//		   
//		   if(journal != null) {
//		    authorName = requestVo.getAuthorName();
//		    manuscriptName = requestVo.getManuscriptTitle();
//		    manuscriptCode = requestVo.getManuscriptCode();
//		    journalName = requestVo.getJournalTitle();
//		    eicNameAndSig = journal.getJournalMetadata().getEicNameSignature() != null ? journal.getJournalMetadata().getEicNameSignature() : "";
//		    eicSig = journal.getJournalMetadata().getEicSignature() != null ? journal.getJournalMetadata().getEicSignature() : "";
//		    hasSignature = journal.getJournalMetadata().isHasSignature();
//		    reviewComletionDate = new SimpleDateFormat("dd-MMM-yyyy").format(requestVo.getCompleteDate());
//		    downloadDate = new SimpleDateFormat("dd-MMM-yyyy").format(new Date());
//		    if(journal.getJournalMetadata().getThumbnailPath() != null && !journal.getJournalMetadata().getThumbnailPath().equalsIgnoreCase("")) {
//		     journalThumbnailImagePath = journal.getJournalMetadata().getThumbnailPath();
//		    } else if((journal.getJournalMetadata().getThumbnailPath() == null || journal.getJournalMetadata().getThumbnailPath().equalsIgnoreCase(""))
//		      && journal.getJournalMetadata().getWebsiteUrl() != null
//		      && !journal.getJournalMetadata().getWebsiteUrl().trim().equalsIgnoreCase("")) {
//		     journalThumbnailImagePath = readImageFromJournalWebsite(journal.getJournalMetadata().getWebsiteUrl()+"/images/cover.png", journal.getJournalCode(), journalImagePath, thumbnailUrl);
//		    } else {
//		     journalThumbnailImagePath = feImageUrl+"ones.jpg";
//		    }
//		    
//		    if(journalThumbnailImagePath.trim().equalsIgnoreCase("")) {
//		     journalThumbnailImagePath = feImageUrl+"ones.jpg";
//		    }
//		    
//		    System.out.println("journalThumbnailImagePath==="+journalThumbnailImagePath);
//		    String padding = "160px";
//		    if(manuscriptName.length() > 265) {
//		     padding = "100px";
//		     manuscriptName = manuscriptName.substring(0,250)+"...";
//		    }
//		    if(eicSig!= null && eicSig.length() > 25 && manuscriptName.length() > 78) {
//		     padding = "100px";
//		    }
//		    if(eicNameAndSig != null && eicNameAndSig.indexOf(",")>-1 && eicNameAndSig.split(",").length > 2) {
//		     padding = "100px";
//		    }
//		    
//		             String additionalImagePath = "";
//		             log.info("has signature : "+hasSignature);
//		             if (hasSignature) {
//		              if(journal.getJournalId()==259 || journal.getJournalId()==2152) {
//		               htmlFilePath = certificateDefaultImagePath + "sample_with_signature_reduce_img.html";
//		              }
//		              else if (journal.getJournalId() == 37145510 || journal.getJournalId() == 7 ) {
//		 
//		               log.info("Author Name: {}", authorName);
//		               log.info("Manuscript Name: {}", manuscriptName);
//		               log.info("Manuscript Code: {}", manuscriptCode);
//		               log.info("Journal Name: {}", journalName);
//		 
//		               // Extract EIC names
//		               String eicNameListRaw = journal.getJournalMetadata().getEicNameSignature();
//		               log.info("Raw EIC List: {}", eicNameListRaw);
//		 
//		               List<String> eicNames = Arrays.stream(eicNameListRaw.split(","))
//		                       .map(String::trim)
//		                       .collect(Collectors.toList());
//		 
//		               String eicName1 = eicNames.size() > 0 ? eicNames.get(0) : "";
//		               String eicName2 = eicNames.size() > 1 ? eicNames.get(1) : "";
//		 
//		               log.info("EIC Name 1: {}", eicName1);
//		               log.info("EIC Name 2: {}", eicName2);
//		 
//		               // Read signature images from folder
//		               String signatureFolder = certificateDefaultImagePath + journal.getJournalId();
//		               log.info("Signature folder: {}", signatureFolder);
//		 
//		               List<String> signatureList = new ArrayList<>();
//		 
//		               File dir = new File(signatureFolder);
//		               if (dir.exists()) {
//		                   File[] sigImgs = dir.listFiles((d, name) ->
//		                           name.matches(".*\\.(jpg|jpeg|png|gif|bmp)$"));
//		 
//		                   if (sigImgs != null) {
//		                       for (File img : sigImgs) {
//		                           log.info("Signature File Found: {}", img.getAbsolutePath());
//		                           signatureList.add(img.toURI().toString());
//		                       }
//		                   }
//		               }
//		 
//		               String eicSignature1 = signatureList.size() > 0 ? signatureList.get(0) : "";
//		               String eicSignature2 = signatureList.size() > 1 ? signatureList.get(1) : "";
//		 
//		               log.info("Picked EIC Signature #1: {}", eicSignature1);
//		               log.info("Picked EIC Signature #2: {}", eicSignature2);
//		 
//		               // Load HTML template
//		               String htmlPath = certificateDefaultImagePath + "MultipleSignAndName_JPDS_journal.html";
//		               log.info("HTML Template Path: {}", htmlPath);
//		 
//		               String html = new String(Files.readAllBytes(Paths.get(htmlPath)));
//		 
//		               // Prepare download date
//		               downloadDate = new SimpleDateFormat("dd-MMM-yyyy").format(new Date());
//		 
//		               // Replace placeholders
//		               html = html.replace("${reviewer_name}", authorName)
//		                       .replace("${manuscriptCode}", manuscriptCode)
//		                       .replace("${manuscriptName}", manuscriptName)
//		                       .replace("${journalName}", journalName)
//		 
//		                       // signature image paths
//		                       .replace("${signature1}", eicSignature1)
//		                       .replace("${signature2}", eicSignature2)
//		 
//		                       // eic names
//		                       .replace("${eicName1}", eicName1)
//		                       .replace("${eicName2}", eicName2)
//		 
//		                       // image URLs
//		                       .replace("${logoImagePath}", feImageUrl + "wolter.png")
//		                       .replace("${journalThumbnailImagePath}", journalThumbnailImagePath)
//		                       .replace("${reviewComletionDate}", reviewComletionDate)
//		                       // footer date
//		                       .replace("${downloadDate}", downloadDate);
//		 
//		               log.info("HTML replaced successfully. Starting PDF generation...");
//		 
////		               HtmlConverter.convertToPdf(html, new FileOutputStream(fileName));
//		               
//		               Document document = new Document(com.itextpdf.text.PageSize.A4.rotate(), 0, 0, 0, 0);
//		               PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(fileName));
//		               document.open();
//		 
//		               PdfContentByte cb = writer.getDirectContent();
//		               cb.setLineWidth(6);
//		               cb.rectangle(
//		                       3,  
//		                       3,
//		                       PageSize.A4.rotate().getWidth() - 6,
//		                       PageSize.A4.rotate().getHeight() - 6
//		               );
//		               cb.stroke();
//		 
//		               XMLWorkerHelper.getInstance().parseXHtml(
//		                       writer,
//		                       document,
//		                       new StringReader(html)
//		               );
//		               
//		               document.close();
//		 
//		 
//		 
//		               log.info("PDF generated successfully! Saved at: {}", fileName);
//		               log.info("=== Completed generateMultiEICCertificate() ===");
//		               
//		               return;
//		 
//		           }
//		              //start for atm
//		              else if (journal.getJournalId() == 7 || journal.getJournalId() == 1360) {
//		                
//		               log.info("Author Name: {}", authorName);
//		               log.info("Manuscript Name: {}", manuscriptName);
//		               log.info("Manuscript Code: {}", manuscriptCode);
//		               log.info("Journal Name: {}", journalName);
//		 
//		               // Extract EIC names
//		               String eicNameListRaw = journal.getJournalMetadata().getEicNameSignature();
//		               log.info("Raw EIC List: {}", eicNameListRaw);
//		 
//		               List<String> eicNames = Arrays.stream(eicNameListRaw.split(","))
//		                       .map(String::trim)
//		                       .collect(Collectors.toList());
//		 
//		               String eicName1 = eicNames.size() > 0 ? eicNames.get(0) : "";
//		               String eicName2 = eicNames.size() > 1 ? eicNames.get(1) : "";
//		 
//		               log.info("EIC Name 1: {}", eicName1);
//		               log.info("EIC Name 2: {}", eicName2);
//		 
//		               // Read signature images from folder
//		               String signatureFolder = certificateDefaultImagePath + journal.getJournalId();
//		               log.info("Signature folder: {}", signatureFolder);
//		 
//		               List<String> signatureList = new ArrayList<>();
//		 
//		               File dir = new File(signatureFolder);
//		               if (dir.exists()) {
//		                   File[] sigImgs = dir.listFiles((d, name) ->
//		                           name.matches(".*\\.(jpg|jpeg|png|gif|bmp)$"));
//		 
//		                   if (sigImgs != null) {
//		                       for (File img : sigImgs) {
//		                           log.info("Signature File Found: {}", img.getAbsolutePath());
//		                           signatureList.add(img.toURI().toString());
//		                       }
//		                   }
//		               }
//		 
//		               String eicSignature1 = signatureList.size() > 0 ? signatureList.get(0) : "";
//		               String eicSignature2 = signatureList.size() > 1 ? signatureList.get(1) : "";
//		 
//		               log.info("Picked EIC Signature #1: {}", eicSignature1);
//		               log.info("Picked EIC Signature #2: {}", eicSignature2);
//		 
//		               // Load HTML template
//		               String htmlPath = certificateDefaultImagePath + "MultipleSignAndName_journa_atm.html";
//		               log.info("HTML Template Path: {}", htmlPath);
//		 
//		               String html = new String(Files.readAllBytes(Paths.get(htmlPath)));
//		 
//		               // Prepare download date
//		               downloadDate = new java.text.SimpleDateFormat("dd-MMM-yyyy").format(new java.util.Date());
//		 
//		               // Replace placeholders
//		               html = html.replace("${reviewer_name}", authorName)
//		                       .replace("${manuscriptCode}", manuscriptCode)
//		                       .replace("${manuscriptName}", manuscriptName)
//		                       .replace("${journalName}", journalName)
//		 
//		                       // signature image paths
//		                       .replace("${signature1}", eicSignature1)
//		                       .replace("${signature2}", eicSignature2)
//		 
//		                       // eic names
//		                       .replace("${eicName1}", eicName1)
//		                       .replace("${eicName2}", eicName2)
//		 
//		                       // image URLs
//		                       .replace("${logoImagePath}", feImageUrl + "wolter.png")
//		                       .replace("${journalThumbnailImagePath}", journalThumbnailImagePath)
//		                       .replace("${reviewComletionDate}", reviewComletionDate)
//		                       // footer date
//		                       .replace("${downloadDate}", downloadDate);
//		 
//		               log.info("HTML replaced successfully. Starting PDF generation...");
//		 
////		               HtmlConverter.convertToPdf(html, new FileOutputStream(fileName));
//		               
//		               Document document = new Document(com.itextpdf.text.PageSize.A4.rotate(), 0, 0, 0, 0);
//		               PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(fileName));
//		               document.open();
//
//		               PdfContentByte cb = writer.getDirectContent();
//		               cb.setLineWidth(6);
//		               cb.rectangle(
//		                       3,
//		                       3,
//		                       PageSize.A4.rotate().getWidth() - 6,
//		                       PageSize.A4.rotate().getHeight() - 6
//		               );
//		               cb.stroke();
//
//		               XMLWorkerHelper.getInstance().parseXHtml(
//		                       writer,
//		                       document,
//		                       new StringReader(html)
//		               );
//
//		               document.close();
//		 
//		 
//		 
//		               log.info("PDF generated successfully! Saved at: {}", fileName);
//		               log.info("=== Completed generateMultiEICCertificate() ===");
//		               
//		               return;
//		 
//		           }
//		              //end atm
//		              
//		              else {
//		               htmlFilePath = certificateDefaultImagePath + "sample_with_signature.html";
//		              }
//		                 String journalDirectoryPath = certificateDefaultImagePath + journal.getJournalId();
//		 
//		                 File journalDirectory = new File(journalDirectoryPath);
//		                 if (journalDirectory.exists()) {
//		                     File[] files = journalDirectory.listFiles((dir, name) -> name.matches(".*\\.(jpg|jpeg|png|gif|bmp|tiff|svg)$"));
//		                     if (files != null && files.length > 0) {
//		                      File imageFile = files[0];
//		                         additionalImagePath = imageFile.toURI().toString();
//		                     }
//		                 }                
//		             }
//		   
//		    try {
//		     File file = new File(htmlFilePath);
//		     String content = new String(Files.readAllBytes(file.toPath()));
//		     content = content.replace("${reviewer_name}", authorName)
//		     .replace("${manuscriptName}", manuscriptName)
//		     .replace("${manuscriptCode}", manuscriptCode)
//		     .replace("${journalName}", journalName)
//		     .replace("${eicNameAndSig}", eicNameAndSig.replace(",", "<br>"))
//		     .replace("${eicSig}", eicSig)
//		     .replace("${reviewComletionDate}", reviewComletionDate)
//		     .replace("${downloadDate}", downloadDate)
//		     .replace("${journalThumbnailImagePath}", journalThumbnailImagePath)
//		     .replace("${padding}", padding)
//		     .replace("${logoImagePath}", feImageUrl+"wolter.png");
//		                 if (hasSignature) {
//		                     content = content.replace("${additionalImagePath}", additionalImagePath);
//		                 }
//		     
//		     System.out.println("Replace the parameters....");
//		           
//		     HtmlConverter.convertToPdf(content, new FileOutputStream(fileName));
//		     System.out.println("Done..");
//		    } catch (IOException e) {
//		     e.printStackTrace();
//		    }
//		   }
//		  } catch (Exception e) {
//		   e.printStackTrace();
//		  }
//		  log.info("Completed the Reviewer Certificate");
//		 }
	
	   public void genrateCertificate(EditorDashboardVO requestVo, Journal journal, String fileName, String certificateDefaultImagePath, String feImageUrl, String journalImagePath, String thumbnailUrl) {
		      log.info("Started building the Reviewer Certificate");

		      try {
		         String authorName = "";
		         String manuscriptName = "";
		         String manuscriptCode = "";
		         String journalName = "";
		         String eicNameAndSig = "";
		         String reviewComletionDate = "";
		         String downloadDate = "";
		         String journalThumbnailImagePath = "";
		         String eicSig = "";
		         String institution = "";
		         String htmlFilePath = certificateDefaultImagePath + "reviewer_certificate_global.html";
		         boolean hasSignature = false;
		         if (journal != null) {
		            authorName = requestVo.getAuthorName();
		            manuscriptName = requestVo.getManuscriptTitle();
		            manuscriptCode = requestVo.getManuscriptCode();
		            journalName = requestVo.getJournalTitle();
		            if (journal.getJournalMetadata() != null) {
		               eicNameAndSig = journal.getJournalMetadata().getEicNameSignature() != null ? journal.getJournalMetadata().getEicNameSignature() : "";
		               eicSig = journal.getJournalMetadata().getEicSignature() != null ? journal.getJournalMetadata().getEicSignature() : "";
		               hasSignature = journal.getJournalMetadata().isHasSignature();
		            }

		            if (requestVo.getCompleteDate() != null) {
		               reviewComletionDate = (new SimpleDateFormat("dd-MMM-yyyy")).format(requestVo.getCompleteDate());
		            }

		            downloadDate = (new SimpleDateFormat("dd-MMM-yyyy")).format(new Date());
		            if (journal.getJournalMetadata() != null && journal.getJournalMetadata().getThumbnailPath() != null && !journal.getJournalMetadata().getThumbnailPath().equalsIgnoreCase("")) {
		               journalThumbnailImagePath = journal.getJournalMetadata().getThumbnailPath();
		            } else if (journal.getJournalMetadata() != null && journal.getJournalMetadata().getWebsiteUrl() != null && !journal.getJournalMetadata().getWebsiteUrl().trim().equalsIgnoreCase("")) {
		               journalThumbnailImagePath = readImageFromJournalWebsite(journal.getJournalMetadata().getWebsiteUrl() + "/images/cover.png", journal.getJournalCode(), journalImagePath, thumbnailUrl);
		            } else {
		               journalThumbnailImagePath = feImageUrl + "ones.jpg";
		            }

		            if (journalThumbnailImagePath == null || journalThumbnailImagePath.trim().equalsIgnoreCase("")) {
		               journalThumbnailImagePath = feImageUrl + "ones.jpg";
		            }

		            log.info("journalThumbnailImagePath={}", journalThumbnailImagePath);
		            String padding = "160px";
		            if (manuscriptName != null && manuscriptName.length() > 265) {
		               padding = "100px";
		               manuscriptName = manuscriptName.substring(0, 250) + "...";
		            }

		            if (eicSig != null && eicSig.length() > 25 && manuscriptName != null && manuscriptName.length() > 78) {
		               padding = "100px";
		            }

		            if (eicNameAndSig != null && eicNameAndSig.indexOf(",") > -1 && eicNameAndSig.split(",").length > 2) {
		               padding = "100px";
		            }

		            String additionalImagePath = "";
		            log.info("has signature : {}", hasSignature);
		            if (hasSignature) {
		               if (journal.getJournalId() != 259 && journal.getJournalId() != 2152) {
		                  if (journal.getJournalId() == 37145510) {
		                     log.info("Author Name: {}", authorName);
		                     log.info("Manuscript Name: {}", manuscriptName);
		                     log.info("Manuscript Code: {}", manuscriptCode);
		                     log.info("Journal Name: {}", journalName);
		                     String eicNameListRaw = journal.getJournalMetadata().getEicNameSignature();
		                     log.info("Raw EIC List: {}", eicNameListRaw);
		                     List<String> eicNames = (List)Arrays.stream(eicNameListRaw.split(",")).map(String::trim).collect(Collectors.toList());
		                     String eicName1 = eicNames.size() > 0 ? (String)eicNames.get(0) : "";
		                     String eicName2 = eicNames.size() > 1 ? (String)eicNames.get(1) : "";
		                     log.info("EIC Name 1: {}", eicName1);
		                     log.info("EIC Name 2: {}", eicName2);
		                     String signatureFolder = certificateDefaultImagePath + journal.getJournalId();
		                     log.info("Signature folder: {}", signatureFolder);
		                     List<String> signatureList = new ArrayList();
		                     File dir = new File(signatureFolder);
		                     if (dir.exists()) {
		                        File[] sigImgs = dir.listFiles((d, name) -> name.matches(".*\\.(jpg|jpeg|png|gif|bmp)$"));
		                        if (sigImgs != null) {
		                           for(File img : sigImgs) {
		                              log.info("Signature File Found: {}", img.getAbsolutePath());
		                              signatureList.add(img.toURI().toString());
		                           }
		                        }
		                     }

		                     String eicSignature1 = signatureList.size() > 0 ? (String)signatureList.get(0) : "";
		                     String eicSignature2 = signatureList.size() > 1 ? (String)signatureList.get(1) : "";
		                     log.info("Picked EIC Signature #1: {}", eicSignature1);
		                     log.info("Picked EIC Signature #2: {}", eicSignature2);
		                     String htmlPath = certificateDefaultImagePath + "MultipleSignAndName_JPDS_journal.html";
		                     log.info("HTML Template Path: {}", htmlPath);
		                     String html = new String(Files.readAllBytes(Paths.get(htmlPath)), StandardCharsets.UTF_8);
		                     html = html.replace("${reviewer_name}", authorName).replace("${manuscriptCode}", manuscriptCode).replace("${manuscriptName}", manuscriptName).replace("${journalName}", journalName).replace("${signature1}", eicSignature1).replace("${signature2}", eicSignature2).replace("${eicName1}", eicName1).replace("${eicName2}", eicName2).replace("${logoImagePath}", feImageUrl + "wolter.png").replace("${journalThumbnailImagePath}", journalThumbnailImagePath).replace("${reviewComletionDate}", reviewComletionDate).replace("${downloadDate}", downloadDate);
		                     log.info("JPDS HTML replaced successfully.");
			                    
		                     Document document =new Document(
			                                    com.itextpdf.text.PageSize.A4.rotate(),
			                                    0,
			                                    0,
			                                    0,
			                                    0);
		                     PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(fileName));
		                     document.open();
		                     PdfContentByte cb = writer.getDirectContent();
		                     cb.setLineWidth(6.0F);
		                     cb.rectangle(3.0F, 3.0F, com.itextpdf.kernel.geom.PageSize.A4.rotate().getWidth() - 6.0F, com.itextpdf.kernel.geom.PageSize.A4.rotate().getHeight() - 6.0F);
		                     cb.stroke();
		                     XMLWorkerHelper.getInstance().parseXHtml(writer, document, new StringReader(html));
		                     document.close();
		                     log.info("JPDS reviewer certificate generated: {}", fileName);
		                     return;
		                  }

		                  if (journal.getJournalId() == 1360) {
		                     log.info("Author Name: {}", authorName);
		                     log.info("Manuscript Name: {}", manuscriptName);
		                     log.info("Manuscript Code: {}", manuscriptCode);
		                     log.info("Journal Name: {}", journalName);
		                     String eicNameListRaw = journal.getJournalMetadata().getEicNameSignature();
		                     log.info("Raw EIC List: {}", eicNameListRaw);
		                     List<String> eicNames = (List)Arrays.stream(eicNameListRaw.split(",")).map(String::trim).collect(Collectors.toList());
		                     String eicName1 = eicNames.size() > 0 ? (String)eicNames.get(0) : "";
		                     String eicName2 = eicNames.size() > 1 ? (String)eicNames.get(1) : "";
		                     String signatureFolder = certificateDefaultImagePath + journal.getJournalId();
		                     log.info("Signature folder: {}", signatureFolder);
		                     List<String> signatureList = new ArrayList();
		                     File dir = new File(signatureFolder);
		                     if (dir.exists()) {
		                        File[] sigImgs = dir.listFiles((d, name) -> name.matches(".*\\.(jpg|jpeg|png|gif|bmp)$"));
		                        if (sigImgs != null) {
		                           for(File img : sigImgs) {
		                              log.info("Signature File Found: {}", img.getAbsolutePath());
		                              signatureList.add(img.toURI().toString());
		                           }
		                        }
		                     }

		                     String eicSignature1 = signatureList.size() > 0 ? (String)signatureList.get(0) : "";
		                     String eicSignature2 = signatureList.size() > 1 ? (String)signatureList.get(1) : "";
		                     String htmlPath = certificateDefaultImagePath + "MultipleSignAndName_journa_atm.html";
		                     log.info("HTML Template Path: {}", htmlPath);
		                     String html = new String(Files.readAllBytes(Paths.get(htmlPath)), StandardCharsets.UTF_8);
		                     html = html.replace("${reviewer_name}", authorName).replace("${manuscriptCode}", manuscriptCode).replace("${manuscriptName}", manuscriptName).replace("${journalName}", journalName).replace("${signature1}", eicSignature1).replace("${signature2}", eicSignature2).replace("${eicName1}", eicName1).replace("${eicName2}", eicName2).replace("${logoImagePath}", feImageUrl + "wolter.png").replace("${journalThumbnailImagePath}", journalThumbnailImagePath).replace("${reviewComletionDate}", reviewComletionDate).replace("${downloadDate}", downloadDate).replace("${institution}", eicSig);
			                   Document document = new Document(
			                                    com.itextpdf.text.PageSize.A4.rotate(),
			                                    0,
			                                    0,
			                                    0,
			                                    0);
		                     PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(fileName));
		                     document.open();
		                     PdfContentByte cb = writer.getDirectContent();
		                     cb.setLineWidth(6.0F);
		                     cb.rectangle(3.0F, 3.0F, com.itextpdf.kernel.geom.PageSize.A4.rotate().getWidth() - 6.0F, com.itextpdf.kernel.geom.PageSize.A4.rotate().getHeight() - 6.0F);
		                     cb.stroke();
		                     XMLWorkerHelper.getInstance().parseXHtml(writer, document, new StringReader(html));
		                     document.close();
		                     log.info("ATM reviewer certificate generated: {}", fileName);
		                     return;
		                  }

		                  htmlFilePath = certificateDefaultImagePath + "reviewer_certificate_global.html";
		               } else {
		                  htmlFilePath = certificateDefaultImagePath + "sample_with_signature_reduce_img.html";
		               }

		               String journalDirectoryPath = certificateDefaultImagePath + journal.getJournalId();
		               File journalDirectory = new File(journalDirectoryPath);
		               if (journalDirectory.exists()) {
		                  File[] files = journalDirectory.listFiles((dirx, name) -> name.matches(".*\\.(jpg|jpeg|png|gif|bmp|tiff|svg)$"));
		                  if (files != null && files.length > 0) {
		                     additionalImagePath = files[0].toURI().toString();
		                     log.info("Additional signature image: {}", additionalImagePath);
		                  }
		               }
		            }

		            String signature1 = additionalImagePath != null ? additionalImagePath : "";
		            String signature2 = "";
		            String signatureSection = "";
		            List<String> globalEicNames = new ArrayList();
		            if (eicNameAndSig != null && !eicNameAndSig.trim().isEmpty()) {
		               globalEicNames = (List)Arrays.stream(eicNameAndSig.split(",")).map(String::trim).filter((name) -> !name.isEmpty()).collect(Collectors.toList());
		            }

		            String eicName1 = globalEicNames.size() > 0 ? (String)globalEicNames.get(0) : "";
		            String eicName2 = globalEicNames.size() > 1 ? (String)globalEicNames.get(1) : "";
		            log.info("Global EIC Name 1: {}", eicName1);
		            log.info("Global EIC Name 2: {}", eicName2);
		            if (hasSignature) {
		               String signatureFolder = certificateDefaultImagePath + journal.getJournalId();
		               File signatureDirectory = new File(signatureFolder);
		               if (signatureDirectory.exists() && signatureDirectory.isDirectory()) {
		                  File[] signatureFiles = signatureDirectory.listFiles((dirx, name) -> name.matches(".*\\.(jpg|jpeg|png|gif|bmp|tiff|svg)$"));
		                  if (signatureFiles != null && signatureFiles.length > 1) {
		                     signature2 = signatureFiles[1].toURI().toString();
		                     log.info("Second signature image: {}", signature2);
		                  }
		               }
		            }


		            boolean hasEicName1 =
		                    eicName1 != null && !eicName1.trim().isEmpty();

		            boolean hasEicName2 =
		                    eicName2 != null && !eicName2.trim().isEmpty();

		            boolean hasSignature1 =
		                    signature1 != null && !signature1.trim().isEmpty();

		            boolean hasSignature2 =
		                    signature2 != null && !signature2.trim().isEmpty();

		            String safeEicName1 = escapeHtml(eicName1);
		            String safeEicName2 = escapeHtml(eicName2);
		            String safeInstitution = escapeHtml(eicSig);

		            if (hasEicName1 || hasEicName2 || hasSignature1 || hasSignature2) {

		                if (hasEicName2 || hasSignature2) {

		                    // Multiple EIC layout: names remain parallel even if
		                    // one or both signature images are unavailable.
		                    StringBuilder section = new StringBuilder();

		                    section.append("<div class=\"multiple-signature\">");

		                    // EIC 1
		                    section.append("<div class=\"eic-signature-item\">");

		                    if (hasSignature1) {
		                        section.append("<img class=\"eic-signature\" src=\"")
		                                .append(signature1)
		                                .append("\" alt=\"EIC Signature 1\">");
		                    }

		                    if (hasEicName1) {
		                        section.append("<span class=\"eic-name\">")
		                                .append(safeEicName1)
		                                .append("</span>");
		                    }

		                    if (hasEicName1 || hasSignature1) {
		                        section.append("<span class=\"eic-title\">")
		                                .append("Editor in Chief")
		                                .append("</span>");
		                    }

		                    if (!safeInstitution.isEmpty()
		                            && (hasEicName1 || hasSignature1)) {
		                        section.append("<span class=\"eic-institution\">")
		                                .append(safeInstitution)
		                                .append("</span>");
		                    }

		                    section.append("</div>");

		                    // EIC 2
		                    section.append("<div class=\"eic-signature-item\">");

		                    if (hasSignature2) {
		                        section.append("<img class=\"eic-signature\" src=\"")
		                                .append(signature2)
		                                .append("\" alt=\"EIC Signature 2\">");
		                    }

		                    if (hasEicName2) {
		                        section.append("<span class=\"eic-name\">")
		                                .append(safeEicName2)
		                                .append("</span>");
		                    }

		                    if (hasEicName2 || hasSignature2) {
		                        section.append("<span class=\"eic-title\">")
		                                .append("Editor in Chief")
		                                .append("</span>");
		                    }

		                    if (!safeInstitution.isEmpty()
		                            && (hasEicName2 || hasSignature2)) {
		                        section.append("<span class=\"eic-institution\">")
		                                .append(safeInstitution)
		                                .append("</span>");
		                    }

		                    section.append("</div>");
		                    section.append("</div>");

		                    signatureSection = section.toString();

		                    log.info(
		                            "Multiple EIC section generated. journalId={}, "
		                                    + "eicName1Present={}, eicName2Present={}, "
		                                    + "signature1Present={}, signature2Present={}",
		                            journal.getJournalId(),
		                            hasEicName1,
		                            hasEicName2,
		                            hasSignature1,
		                            hasSignature2
		                    );

		                } else {

		                    // Single EIC layout.
		                    StringBuilder section = new StringBuilder();

		                    section.append(
		                            "<div class=\"eic-signature-row single-signature-row\">"
		                          + "<div class=\"eic-signature-item\">"
		                    );

		                    if (hasSignature1) {
		                        section.append("<img class=\"eic-signature\" src=\"")
		                                .append(signature1)
		                                .append("\" alt=\"EIC Signature\">");
		                    }

		                    if (hasEicName1) {
		                        section.append("<span class=\"eic-name\">")
		                                .append(safeEicName1)
		                                .append("</span>");
		                    }

		                    if (hasEicName1 || hasSignature1) {
		                        section.append("<span class=\"eic-title\">")
		                                .append("Editor in Chief")
		                                .append("</span>");
		                    }

		                    if (!safeInstitution.isEmpty()
		                            && (hasEicName1 || hasSignature1)) {
		                        section.append("<span class=\"eic-institution\">")
		                                .append(safeInstitution)
		                                .append("</span>");
		                    }

		                    section.append("</div></div>");

		                    signatureSection = section.toString();

		                    log.info(
		                            "Single EIC section generated. journalId={}, "
		                                    + "eicNamePresent={}, signaturePresent={}",
		                            journal.getJournalId(),
		                            hasEicName1,
		                            hasSignature1
		                    );
		                }

		            } else {
		                log.warn(
		                        "No EIC name or signature available for journalId={}",
		                        journal.getJournalId()
		                );
		            }	

		            File htmlFile = new File(htmlFilePath);
		            if (!htmlFile.exists()) {
		               log.error("Reviewer certificate HTML not found: {}", htmlFilePath);
		               return;
		            }

		            String content = new String(Files.readAllBytes(htmlFile.toPath()), StandardCharsets.UTF_8);
		            String iconImage = certificateDefaultImagePath + "certificate-icon.png";
		            content = content.replace("${reviewer_name}", authorName).replace("${manuscriptName}", manuscriptName).replace("${manuscriptCode}", manuscriptCode).replace("${journalName}", journalName).replace("${eicNameAndSig}", eicNameAndSig.replace(",", "<br>")).replace("${eicSig}", eicSig).replace("${reviewComletionDate}", reviewComletionDate).replace("${downloadDate}", downloadDate).replace("${journalThumbnailImagePath}", journalThumbnailImagePath).replace("${padding}", padding).replace("${logoImagePath}", feImageUrl + "wolter.png").replace("${institution}", institution).replace("${iconImage}", iconImage);
		            content = content.replace("${signatureSection}", signatureSection);
		            if (hasSignature) {
		               content = content.replace("${additionalImagePath}", additionalImagePath);
		            } else {
		               content = content.replace("${additionalImagePath}", "");
		            }

		            log.info("Generating reviewer certificate PDF using: {}", htmlFilePath);
		            HtmlConverter.convertToPdf(content, new FileOutputStream(fileName));
		            log.info("Reviewer certificate PDF generated successfully: {}", fileName);
		         } else {
		            log.error("Journal is null. Cannot generate reviewer certificate.");
		         }
		      } catch (IOException e) {
		         log.error("IO error while generating reviewer certificate.", e);
		      } catch (Exception e) {
		         log.error("Error generating reviewer certificate.", e);
		      }

		      log.info("Completed the Reviewer Certificate");
		   
	   }
	   
	   private String escapeHtml(String value) {
		    if (value == null) {
		        return "";
		    }

		    return value
		            .replace("&", "&amp;")
		            .replace("<", "&lt;")
		            .replace(">", "&gt;")
		            .replace("\"", "&quot;")
		            .replace("'", "&#39;");
		}
	   
	private String readImageFromJournalWebsite(String journalImageUrl, String journalCode, String imagePath, String thumbnailUrl) {
		String imageLocalUrl = "";
		log.info("Journal Image URL ----- "+journalImageUrl);
		try {
			File f = new File(imagePath);
			log.info("Checking Directory ----- ");
			if(!f.isDirectory()) {
				log.info("Directory created----- ");
				f.mkdirs();				
			}
			log.info("Directory already exist----- ");
			
			URL url = new URL(journalImageUrl.replace("http://", "https://"));
			log.info("URL Genreated----- ");
			HttpURLConnection httpcon = (HttpURLConnection) url.openConnection();
		    httpcon.addRequestProperty("User-Agent", "Mozilla/4.0");
		    InputStream in = httpcon.getInputStream();
//			InputStream in = new BufferedInputStream(url.openStream());
		    
			log.info("InputStream----- "+imagePath+journalCode+"-thumbnail.png");
			OutputStream out = new BufferedOutputStream(new FileOutputStream(imagePath+journalCode+"-thumbnail.png"));
			log.info("OutputStream----- ");
			
			for ( int i; (i = in.read()) != -1; ) {
			    out.write(i);
			}
			log.info("Image writed----- ");
			in.close();
			out.close();
			log.info("Image stream closing----- ");
			imageLocalUrl = thumbnailUrl+File.separator+journalCode+"-thumbnail.png";
		} catch (Exception e) {
			e.printStackTrace();
		}
		log.info("New Thumbnail Journal Image URL ----- "+imageLocalUrl);
		return imageLocalUrl;
	}	
	
	public void genrateIndianCancerReviewerCertificate(
	        EditorDashboardVO requestVo,
	        Journal journal,
	        String fileName,
	        String certificateDefaultImagePath,
	        String feImageUrl,
	        String journalLogoPath,
	        String jubileeLogoPath) {

	    log.info("Started building Indian Journal of Cancer Reviewer Certificate");

	    try {

	        String authorName = "";
	        String manuscriptName = "";
	        String manuscriptCode = "";
	        String journalName = "";
	        String eicNameAndSig = "";
	        String eicName = "";
	        String eicSig = "";
	        String reviewComletionDate = "";
	        String downloadDate = "";

	        if (journal != null) {

	            authorName = requestVo != null ? StringUtils.defaultString(requestVo.getAuthorName(),""): "";

	            manuscriptName = requestVo != null ? StringUtils.defaultString(requestVo.getManuscriptTitle(),""): "";

	            manuscriptCode = requestVo != null ? StringUtils.defaultString(requestVo.getManuscriptCode(), "")
	                            : "";

	            journalName =
	                    requestVo != null
	                            ? StringUtils.defaultString(
	                                    requestVo.getJournalTitle(),
	                                    "")
	                            : "";

	            /*
	             * Existing EIC value.
	             */
	            if (journal.getJournalMetadata() != null) {

	                eicNameAndSig =
	                        journal.getJournalMetadata()
	                                .getEicNameSignature() != null
	                                ? journal.getJournalMetadata()
	                                        .getEicNameSignature()
	                                : "";

	                eicSig =
	                        journal.getJournalMetadata()
	                                .getEicSignature() != null
	                                ? journal.getJournalMetadata()
	                                        .getEicSignature()
	                                : "";
	            }

	            /*
	             * New certificate needs only EIC name.
	             *
	             * Existing database field can contain:
	             *
	             * Dr. Vinay H Deshmane,...
	             *
	             * So take first name.
	             */
	            if (StringUtils.isNotBlank(eicNameAndSig)) {

	                String[] eicNames =
	                        eicNameAndSig.split(",");

	                if (eicNames.length > 0) {

	                    eicName =
	                            eicNames[0].trim();
	                }
	            }

	            /*
	             * Same date format as existing certificate.
	             */
	            if (requestVo != null
	                    && requestVo.getCompleteDate() != null) {

	                reviewComletionDate =
	                        new SimpleDateFormat(
	                                "dd-MMM-yyyy")
	                                .format(
	                                        requestVo.getCompleteDate());
	            }

	            /*
	             * Download date.
	             */
	            downloadDate =
	                    new SimpleDateFormat(
	                            "dd-MMM-yyyy")
	                            .format(new Date());
	        }


	        /*
	         * ============================================================
	         * HTML TEMPLATE
	         * ============================================================
	         */

	        String htmlFilePath =
	                certificateDefaultImagePath
	                + "indian_cancer_reviewer_certificate.html";

	        log.info(
	                "Indian Cancer Reviewer Certificate HTML: {}",
	                htmlFilePath);


	        /*
	         * ============================================================
	         * READ HTML
	         * ============================================================
	         */

	        File htmlFile =
	                new File(htmlFilePath);

	        if (!htmlFile.exists()) {

	            log.error(
	                    "Indian Cancer reviewer certificate HTML not found: {}",
	                    htmlFilePath);

	            return;
	        }
	        
	        String topDecorationPath =
	                new File(
	                        certificateDefaultImagePath
	                        + "certificate_top_divider.png")
	                        .toURI()
	                        .toString();

	        String middleDecorationPath =
	                new File(
	                        certificateDefaultImagePath
	                        + "certificate_middle_divider.png")
	                        .toURI()
	                        .toString();

	        String bottomDecorationPath =
	                new File(
	                        certificateDefaultImagePath
	                        + "certificate_bottom_decoration.png")
	                        .toURI()
	                        .toString();

	        String content =
	                new String(
	                        Files.readAllBytes(
	                                htmlFile.toPath()),
	                        StandardCharsets.UTF_8);


	        /*
	         * ============================================================
	         * REPLACE PLACEHOLDERS
	         * ============================================================
	         *
	         * Existing placeholders are retained wherever possible.
	         */

	        content =
	                content

	                        /*
	                         * Reviewer
	                         */
	                        .replace(
	                                "${reviewer_name}",
	                                authorName)

	                        /*
	                         * Existing manuscript values
	                         */
	                        .replace(
	                                "${manuscriptName}",
	                                manuscriptName)

	                        .replace(
	                                "${manuscriptCode}",
	                                manuscriptCode)

	                        /*
	                         * Journal
	                         */
	                        .replace(
	                                "${journalName}",
	                                journalName)

	                        /*
	                         * Existing EIC placeholder
	                         */
	                        .replace(
	                                "${eicNameAndSig}",
	                                eicNameAndSig
	                                        .replace(
	                                                ",",
	                                                "<br>"))

	                        /*
	                         * Existing EIC signature placeholder
	                         */
	                        .replace(
	                                "${eicSig}",
	                                eicSig)

	                        /*
	                         * Existing review date
	                         */
	                        .replace(
	                                "${reviewComletionDate}",
	                                reviewComletionDate)

	                        /*
	                         * Existing download date
	                         */
	                        .replace(
	                                "${downloadDate}",
	                                downloadDate)

	                        /*
	                         * New EIC name placeholder
	                         */
	                        .replace(
	                                "${eicName}",
	                                eicName)

	                        /*
	                         * New Indian Cancer Society logo
	                         */
	                        .replace(
	                                "${journalLogoPath}",
	                                journalLogoPath)

	                        /*
	                         * New Platinum Jubilee logo
	                         */
	                        .replace(
	                                "${jubileeLogoPath}",
	                                jubileeLogoPath)

	                        /*
	                         * Existing logo placeholder.
	                         *
	                         * Keep same path mechanism used by
	                         * your existing certificate.
	                         */
	                        .replace(
	                                "${logoImagePath}",
	                                feImageUrl
	                                + "wolter.png")
	        .replace("${topDecorationPath}",
	                topDecorationPath)
	        .replace("${middleDecorationPath}",
	                middleDecorationPath)
	        .replace("${bottomDecorationPath}",
	                bottomDecorationPath);

	        /*
	         * ============================================================
	         * LOG
	         * ============================================================
	         */

	        log.info(
	                "Indian Cancer reviewer certificate HTML placeholders replaced successfully");

	        log.info(
	                "Reviewer Name: {}",
	                authorName);

	        log.info(
	                "Manuscript Code: {}",
	                manuscriptCode);

	        log.info(
	                "Journal Name: {}",
	                journalName);

	        log.info(
	                "EIC Name: {}",
	                eicName);

	        log.info(
	                "Review Completion Date: {}",
	                reviewComletionDate);

	        log.info(
	                "Journal Logo: {}",
	                journalLogoPath);

	        log.info(
	                "Jubilee Logo: {}",
	                jubileeLogoPath);


	        /*
	         * ============================================================
	         * GENERATE PDF
	         * ============================================================
	         *
	         * Same HtmlConverter mechanism used in existing code.
	         */

	        HtmlConverter.convertToPdf(
	                content,
	                new FileOutputStream(fileName));


	        log.info(
	                "Indian Cancer reviewer certificate generated successfully: {}",
	                fileName);

	    } catch (Exception e) {

	        log.error(
	                "Error generating Indian Cancer reviewer certificate.",
	                e);
	    }

	    log.info(
	            "Completed Indian Journal of Cancer Reviewer Certificate");
	}
}
