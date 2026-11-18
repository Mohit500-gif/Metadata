package com.digi.uniprr.utils;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.io.FilenameUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.IBodyElement;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFPictureData;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.json.simple.JSONObject;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.digi.uniprr.model.ManuScriptFile;
import com.lowagie.text.pdf.PdfReader;

public class TestMergeDocx {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private final OutputStream result;
	private final List<InputStream> inputs;
	private XWPFDocument first;

	@Autowired
	public TestMergeDocx(OutputStream result) {
		this.result = result;
		inputs = new ArrayList<>();
	}

	public Boolean mergeTextFiles(File location, List<ManuScriptFile> manuFileList, String cdnUrl, String uniprrFolderPath) throws Exception {

		log.info("Inside mergeTextFiles");
		log.info("Folder Location " + location);
		String str = location.toString().replace(cdnUrl, uniprrFolderPath);
		location = new File(str.replace("/", File.separator));
		log.info("new Location : " + location);

		// Delete Existing folder
         deleteExistingFolder(location);

	
			// extractImageFromDocx(location.getAbsolutePath());

			File outputFolder = new File(location + File.separator + "output");

			log.info("outputFolder Location :" + outputFolder);
			if (!outputFolder.exists()) {
				outputFolder.mkdir();
				log.info("Output folder created");
			}
			
			CreatePdf createPdf = new CreatePdf();
			log.info("Call docxToPdf");
			createPdf.docxToPdf(location, manuFileList);
			log.info("docxToPdf Completed");
			
	        ArrayList<String> orderList = new ArrayList<String>();
			
			manuFileList.stream().forEach(j ->{
			if(j.getFileType().equalsIgnoreCase("First Page file/Cover Letter") && j.getFileName().contains("FirstPage"))
				orderList.add(j.getFileName()); 
			});
			
			manuFileList.stream().forEach(j ->{
			if(j.getFileType().equalsIgnoreCase("Main Documents/Article File") && j.getFileName().contains("Manuscript"))
		        orderList.add(j.getFileName()); 
			});
			
			manuFileList.stream().forEach(j ->{
			if(j.getFileType().equalsIgnoreCase("Figures"))
		        orderList.add(j.getFileName()); 
			});
			
			manuFileList.stream().forEach(j ->{
			if(j.getFileType().equalsIgnoreCase("Tables"))
		        orderList.add(j.getFileName()); 
			});
			
			manuFileList.stream().forEach(j ->{
			if(j.getFileType().equalsIgnoreCase("Comments to Referees"))
		        orderList.add(j.getFileName()); 
			});
			
			log.info("List of filename :::"+orderList);
			log.info("python API got hit");
		   String result = pythonAPICall(location.getPath(),orderList ,"EIC");	
		   log.info("Bundle file generation status"+ result);
		   orderList.remove(0);
		   String result1 = pythonAPICall(location.getPath(),orderList ,"Reviewer");	
		   log.info("Reviewer Bundle file generation status"+ result1);
		   if(result.equalsIgnoreCase("fail") || result1.equalsIgnoreCase("fail") ) {
				log.info("python API failed");
				createPdf.docxToPdfNew(location, manuFileList);
				log.info("Java API started");
				createPdf.javaBundleAPI(location, manuFileList, outputFolder);
		   }
		   else {
	       File oldBundel = new File(location.getAbsolutePath()+"/output/Bundled.docx");
	       File newBundel = new File(location.getAbsolutePath()+"/output/BundledEIC.docx");

	        if (oldBundel.renameTo(newBundel)) 
	            log.info("Bundled.docx Renamed successfully");        
	        else 
	        	log.info("Getting error during renaming of file Bundled.docx"); 
	       
	       File oldRevBundel = new File(location.getAbsolutePath()+"/output/BundledPdfForReviewer.docx");
	       File newRevBundel = new File(location.getAbsolutePath()+"/output/Bundled.docx");
	       
	        if (oldRevBundel.renameTo(newRevBundel)) 
	        	log.info("BundledPdfForReviewer.docx Renamed successfully");        
	        else 
	        	log.info("Getting error during renaming of file BundledPdfForReviewer.docx"); 
		   }
			TestMergeDocx c = new TestMergeDocx(null);
			c.countNumberofImagefiles(location);
			extractImageFromDocx(location.getAbsolutePath());

			log.info("@@@@@@@@@@@@@@@@    DOC and PDF file both generated.   @@@@@@@@@@@@@ ");

		return true;

	}
	
	
	public String pythonAPICall(String location, ArrayList<String> orderList, String fileType)
	{
		JSONObject obj = new JSONObject(); 
		obj.put("path", location);
		obj.put("files", orderList);
		if(fileType.equalsIgnoreCase("EIC")) {
			obj.put("outFilename", "Bundled");	
		}else {
			obj.put("outFilename", "BundledPdfForReviewer");
		}
		log.info("Input json Data *********** "+obj);
		CloseableHttpClient client = HttpClientBuilder.create().build();
		HttpPost httpPost = new HttpPost("http://18.237.24.121:8080/getmergeDocument");
		
		  try {
	            StringEntity entity = new StringEntity(obj.toString());
	            httpPost.setEntity(entity);

	            // set your POST request headers to accept json contents
	            httpPost.setHeader("Accept", "application/json");
	            httpPost.setHeader("Content-type", "application/json");

	            try {
	                // your closeablehttp response
	                CloseableHttpResponse response = client.execute(httpPost);

	                // print your status code from the response
	                log.info("Response code from Bundling generation paython API ::::::::::::::"+response.getStatusLine().getStatusCode());
	                
	                // take the response body as a json formatted string 
	                String responseJSON = EntityUtils.toString(response.getEntity());

	                log.info("Response from Bundling generation paython API ||||||||||||"+ responseJSON);
	                
	                if(response.getStatusLine().getStatusCode() !=200 || response.getStatusLine().getStatusCode() !=201)
	                {
	                	return "Fail";
	                }
                
	            } catch (IOException e) {
	                e.printStackTrace();	                
	                return "Fail";
	            } 

	        } catch (UnsupportedEncodingException e) {
	            e.printStackTrace();
	            return "Fail";
	        }		
		
		  return "success";
	}
	
	void countNumberofImagefiles(File location) throws Exception {
		try {
			log.info("Inside countNumberofImagefiles");
			log.info("Image file location " + location);
			javaxt.io.Directory directory = new javaxt.io.Directory(location.getAbsolutePath());
			directory.getFiles();
			javaxt.io.File[] files;
			java.io.FileFilter filter = file -> !file.isHidden()
					&& (file.isDirectory() || (file.getName().endsWith(".docx")));
			files = directory.getFiles(filter, true);
			// files.length
			// log.debug("Count" + Arrays.toString(files));
			Arrays.asList(files);

			File dir = new File(location.getAbsolutePath());

			// log.debug("Count in files:::"+l);

			final String[] EXTENSIONS = new String[] { "gif", "png", "bmp", "jpg" // and other formats you need
			};
			// filter to identify images based on their extensions
			final FilenameFilter IMAGE_FILTER = new FilenameFilter() {

				@Override
				public boolean accept(final File dir, final String name) {
					for (final String ext : EXTENSIONS) {
						if (name.endsWith("." + ext)) {
							return (true);
						}
					}
					return (false);
				}
			};

			File[] fili = new File(location.getAbsolutePath()).listFiles(IMAGE_FILTER);
			int len = fili.length;

			saveToWord(fili, dir);

		} catch (Exception e) {
			log.error("Exception :" + e);
		}
	}

	public void saveToWord(File[] locations, File dir) throws Exception, IOException {

		try {
			XWPFDocument docx = new XWPFDocument();

			File imageOutDoc = new File(dir.getAbsoluteFile() + File.separator + "ZYX.docx");
			imageOutDoc.createNewFile();
			FileOutputStream out = new FileOutputStream(imageOutDoc, false);

			for (File location : locations) {

				XWPFParagraph par = docx.createParagraph();
			//	XWPFRun paragraphFiveRunOne = par.createRun();
				XWPFRun run = par.createRun();

				BufferedImage image = ImageIO.read(location);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ImageIO.write(image, "png", baos);
				baos.flush();
				ByteArrayInputStream bis = new ByteArrayInputStream(baos.toByteArray());
				baos.close();

				run.addPicture(bis, XWPFDocument.PICTURE_TYPE_PNG, "image file", Units.toEMU(300), Units.toEMU(200));

				run.addBreak();
				run.addBreak();
				run.addBreak();
				run.addBreak();
				bis.close();

			}
			docx.write(out);
			out.close();
		} catch (Exception e) {
			log.error("Exception : " + e.getMessage());
		}

	}

	public void add(InputStream stream) throws Exception {
		inputs.add(stream);
		OPCPackage srcPackage = OPCPackage.open(stream);
		XWPFDocument src1Document = new XWPFDocument(srcPackage);
		if (inputs.size() == 1) {
			first = src1Document;
		} else {
			CTBody srcBody = src1Document.getDocument().getBody();
			src1Document.createParagraph();
			XWPFParagraph title = first.createParagraph();
			XWPFRun run = title.createRun();
			run.addBreak();
			first.getDocument().addNewBody().set(srcBody);
		}
	}

	public void doMerge() throws Exception {
		first.write(result);
	}

	public void close() throws Exception {
		if (result != null) {
			result.flush();
			result.close();
		}

		for (InputStream input : inputs) {
			input.close();
		}

	}

	public void readtables(FileInputStream fileInputStream) throws Exception {
		XWPFDocument xdoc = new XWPFDocument(OPCPackage.open(fileInputStream));
		Iterator<IBodyElement> bodyElementIterator = xdoc.getBodyElementsIterator();
		while (bodyElementIterator.hasNext()) {
			IBodyElement element = bodyElementIterator.next();

			if ("TABLE".equalsIgnoreCase(element.getElementType().name())) {
				List<XWPFTable> tableList = element.getBody().getTables();
				// log.debug("Total Number of Tables :" + tableList.size());
				for (XWPFTable table : tableList) {
					// log.debug("Total Number of Rows of Table:" +
					// table.getNumberOfRows());
				}
			}
		}
	}

	void extractImageFromDocx(String location) throws Exception {

		File imageFolder = new File(location + File.separator + "images");
		// Create output Folder if not created
		if (!imageFolder.exists()) {
			imageFolder.mkdir();
			// log.debug(imageFolder);
		}
		File file = new File(location);
		int i = 1;
		for (File fileEntry : file.listFiles()) {
			// log.debug(fileEntry);

			if (fileEntry.getName().equalsIgnoreCase("ZYX.docx")
					|| fileEntry.getName().equalsIgnoreCase("manuscript.docx")) {
				FilenameUtils.getExtension(fileEntry.getName());
				String newFileName = fileEntry.getName().replace(FilenameUtils.getExtension(fileEntry.getName()), "")
						.replace(".", "");

				FileInputStream fs = new FileInputStream(fileEntry);
				// log.debug(fs);
				XWPFDocument docx = new XWPFDocument(fs);
				List<XWPFPictureData> piclist = docx.getAllPictures();
				Iterator<XWPFPictureData> iterator = piclist.iterator();
				// log.debug(piclist.size());
				// int i = 0;
				while (iterator.hasNext()) {
					XWPFPictureData pic = iterator.next();
					byte[] bytepic = pic.getData();

					BufferedImage imag = ImageIO.read(new ByteArrayInputStream(bytepic));
					if (imag != null) {
						ImageIO.write(imag, "jpg",
								new File(imageFolder.getAbsolutePath() + File.separator + "image" + i + ".jpg"));
					} else {
						// log.debug("imag is empty");
					}
					i++;
				}
			}
		}
	}

	public Integer wordCount(File fils, String cdnUrl, String uniprrFolderPath) throws Exception {
		log.info("Inside wordCount");

		log.info("Folder Location " + fils);
		String str = fils.toString().replace(cdnUrl, uniprrFolderPath);
		fils = new File(str);
		log.info("new Location : " + fils);

		Integer wordCountList = 0;
		try {
			FileInputStream fis = new FileInputStream(fils.getAbsolutePath() + File.separator + "Manuscript.docx");
			XWPFDocument xdoc = new XWPFDocument(OPCPackage.open(fis));
			XWPFWordExtractor extractor = new XWPFWordExtractor(xdoc);

			String count[] = extractor.getText().split(" ");
			wordCountList = count.length;
			log.info("Wrod Count ----------------------" + count.length);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return wordCountList;
	}

	public Integer pageCount(File fils, String cdnUrl, String uniprrFolderPath) throws Exception {
		log.info("Inside pageCount");

		log.info("Folder Location " + fils);
		String str = fils.toString().replace(cdnUrl, uniprrFolderPath);
		fils = new File(str);
		log.info("new Location : " + fils);

		Integer pageCountList = 0;

		PdfReader reader;
		try {
			reader = new PdfReader(fils.getAbsolutePath() + File.separator + "PDF" + File.separator + "Manuscript.pdf");
			pageCountList = reader.getNumberOfPages();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return pageCountList;
	}

	private void deleteExistingFolder(File location) {
		log.info("Inside deleteExistingFolder");
		File docxImagePDF = new File(location + File.separator + "DocxImagePDF");
		if (docxImagePDF.exists() && docxImagePDF.isDirectory()) {
			String[] entries = docxImagePDF.list();
			if (entries.length > 0) {
				for (String s : entries) {
					File currentFile = new File(docxImagePDF.getPath(), s);
					currentFile.delete();
				}
			}
			docxImagePDF.delete();
		}

		File output = new File(location + File.separator + "output");
		if (output.exists() && output.isDirectory()) {
			String[] entries = output.list();
			if (entries.length > 0) {
				for (String s : entries) {
					File currentFile = new File(output.getPath(), s);
					currentFile.delete();
				}
			}
			output.delete();
		}

		File images = new File(location + File.separator + "images");
		if (images.exists() && images.isDirectory()) {
			String[] entries = images.list();
			if (entries.length > 0) {
				for (String s : entries) {
					File currentFile = new File(images.getPath(), s);
					currentFile.delete();
				}
			}
			images.delete();
		}

		File pdf = new File(location + File.separator + "PDF");
		if (pdf.exists() && pdf.isDirectory()) {
			String[] entries = pdf.list();
			if (entries.length > 0) {
				for (String s : entries) {
					File currentFile = new File(pdf.getPath(), s);
					currentFile.delete();
				}
			}
			pdf.delete();
		}

		File docxForImage = new File(location + File.separator + "DocxForImage");
		if (docxForImage.exists() && docxForImage.isDirectory()) {
			String[] entries = docxForImage.list();
			if (entries.length > 0) {
				for (String s : entries) {
					File currentFile = new File(docxForImage.getPath(), s);
					currentFile.delete();
				}
			}
			docxForImage.delete();
		}

	}

}
