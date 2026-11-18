package com.digi.uniprr.utils;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.zeroturnaround.zip.ZipUtil;

import com.digi.uniprr.VO.AuthorVO;
import com.digi.uniprr.VO.EditorDashboardVO;
import com.digi.uniprr.VO.ImageVO;
import com.digi.uniprr.VO.ManuscriptListVO;
import com.digi.uniprr.model.logging.ErrorLog;
import com.digi.uniprr.repository.ErrorLogRepo;

public final class ManuscriptUtils {
	private static Logger log = LoggerFactory.getLogger(ManuscriptUtils.class);

	private ManuscriptUtils() {
	}

	public static String generateManuscriptFolderPath(String uploadFolder, int journalId, int mansuscriptId, int maniscriptVersion) {

		StringBuilder baseFolderPathString = new StringBuilder(uploadFolder).append(File.separator);
		baseFolderPathString.append("UniprrFileFolder").append(File.separator);
		baseFolderPathString.append("MNT_LWW_JOURNAL_R");
		baseFolderPathString.append(maniscriptVersion);
		baseFolderPathString.append("_");
		baseFolderPathString.append(journalId);
		baseFolderPathString.append("_");
		baseFolderPathString.append(mansuscriptId).append(File.separator);

		log.info("Manuscript folder path : " + baseFolderPathString);

		return baseFolderPathString.toString();
	}

	public static String generateFolderName(String serverFilePath, int serverPort) {
		String folderName = "";

		String fileMakingPath = System.getProperty("catalina.base") + File.separator + "webapps" + File.separator;
		try {
			String filePathServer = "http://" + InetAddress.getLocalHost().getHostAddress() + ":" + serverPort;
			String driveFilePath = serverFilePath.replace(filePathServer, fileMakingPath);

			driveFilePath = driveFilePath.replace("\\", File.separator);
			String pattern = Pattern.quote(System.getProperty("file.separator"));

			String[] array = driveFilePath.split(pattern);
			folderName = array[array.length - 2];
		} catch (UnknownHostException e) {
			log.error("Error while genrating folder name for path : " + serverFilePath, e);
		}

		log.info("Folder name : " + folderName);
		return folderName;
	}

	public static void generateXMLFile(ManuscriptListVO manuscriptListVO, String baseFolderPath) throws ParserConfigurationException, TransformerException {
		log.info("Started XML file generation");
		File xmlFilePath = new File(baseFolderPath + manuscriptListVO.getManuscripCode() + ".xml");
		log.info("xmlFilePath : " + xmlFilePath);

		DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
		Document document = documentBuilder.newDocument();

		// root element
		Element root = document.createElement("manuscript");
		document.appendChild(root);

		// id element
		Element id = document.createElement("id");
		if (manuscriptListVO.getManuscripCode() != null)
			id.appendChild(document.createTextNode(manuscriptListVO.getManuscripCode()));
		else
			id.appendChild(document.createTextNode(""));
		root.appendChild(id);

		// title element
		Element title = document.createElement("title");
		if (manuscriptListVO.getManuscripTitle() != null)
			title.appendChild(document.createTextNode(manuscriptListVO.getManuscripTitle()));
		else
			title.appendChild(document.createTextNode(""));
		root.appendChild(title);

		// Trial Registration element
		Element trialRegistrationNo = document.createElement("trial_registration_no");
		if (manuscriptListVO.getTrialRegistrationNo() != null)
			trialRegistrationNo.appendChild(document.createTextNode(manuscriptListVO.getManuscripTitle()));
		else
			trialRegistrationNo.appendChild(document.createTextNode("\u00A0"));
		root.appendChild(trialRegistrationNo);

		// articletype element
		Element articletype = document.createElement("articletype");
		if (manuscriptListVO.getManuScriptType() != null)
			articletype.appendChild(document.createTextNode(manuscriptListVO.getManuScriptType()));
		else
			articletype.appendChild(document.createTextNode(""));
		root.appendChild(articletype);

		// wordCount element
		Element wordCount = document.createElement("wordCount");
		if (manuscriptListVO.getWord() != null)
			wordCount.appendChild(document.createTextNode(manuscriptListVO.getWord()));
		else
			wordCount.appendChild(document.createTextNode("0"));
		root.appendChild(wordCount);

		// CurrentPhase element
		Element currentPhase = document.createElement("CurrentPhase");

		currentPhase.appendChild(document.createTextNode("Accepted for production"));
		root.appendChild(currentPhase);

		// Submissin Date element
		/*
		 * Element submission_date = document.createElement("SubmissionDate"); if
		 * (data.getSubmissionDate() != null) {
		 * submission_date.appendChild(document.createTextNode(data.
		 * getSubmissionDate().toString())); } else {
		 * submission_date.appendChild(document.createTextNode("")); }
		 * root.appendChild(submission_date);
		 */

		Element acceptForProduction = document.createElement("AcceptForProduction");

		acceptForProduction.appendChild(document.createTextNode(new Date().toString()));
		root.appendChild(acceptForProduction);

		// keywords elements
		Element keywords = document.createElement("keywords");
		if (manuscriptListVO.getKeywords() != null && !manuscriptListVO.getKeywords().equalsIgnoreCase(""))
			keywords.appendChild(document.createTextNode(manuscriptListVO.getKeywords()));
		else
			keywords.appendChild(document.createTextNode("\u00A0"));
		root.appendChild(keywords);

		// Comment for Production
		Element commentForProduction = document.createElement("Comment_For_Production");
		if (manuscriptListVO.getCommentForProduction() != null
				&& !manuscriptListVO.getCommentForProduction().equalsIgnoreCase(""))
			commentForProduction.appendChild(document.createTextNode(manuscriptListVO.getCommentForProduction()));
		else
			commentForProduction.appendChild(document.createTextNode("\u00A0"));
		root.appendChild(commentForProduction);

		// articlefile elements
		Element articlefile = document.createElement("articlefile");
		String manuscriptName = null;
		if (manuscriptListVO.getArticleFile() != null && !manuscriptListVO.getArticleFile().equalsIgnoreCase(""))
			manuscriptName =manuscriptListVO.getArticleFile();
		else
			manuscriptName ="Manuscript.docx";
		articlefile.appendChild(document.createTextNode(manuscriptName));
		root.appendChild(articlefile);

		// firstpagefile elements
		Element firstpagefile = document.createElement("firstpagefile");
		String firstPageName = null;
		if (manuscriptListVO.getFirstpagefile() != null && !manuscriptListVO.getFirstpagefile().equalsIgnoreCase(""))
			firstPageName = manuscriptListVO.getFirstpagefile();
		else
			firstPageName ="FirstPage.docx";
		
		firstpagefile.appendChild(document.createTextNode(firstPageName));
		root.appendChild(firstpagefile);

		// Doi elements
		Element doi = document.createElement("doi");
		if (manuscriptListVO.getDoi() != null)
			doi.appendChild(document.createTextNode(manuscriptListVO.getDoi()));
		else
			doi.appendChild(document.createTextNode(""));
		root.appendChild(doi);

		// qr_code elements
		Element qrCode = document.createElement("qr_code");
		qrCode.appendChild(document.createTextNode(manuscriptListVO.getManuscripCode() + ".png"));
		root.appendChild(qrCode);

		/****** sub-root element *********/
		// root element
		Element authors = document.createElement("authors");
		root.appendChild(authors);

		List<AuthorVO> authorData = manuscriptListVO.getAuhtor();
		Element author = null;
		Element authorSubElement = null;

		for (AuthorVO authorVO : authorData) {
			author = document.createElement("author");
			authors.appendChild(author);

			// salutation element
			authorSubElement = document.createElement("title");
			if (authorVO.getSalutatio() != null)
				authorSubElement.appendChild(document.createTextNode(authorVO.getSalutatio().toString()));
			else
				authorSubElement.appendChild(document.createTextNode("\u00A0"));
			author.appendChild(authorSubElement);

			// fname element
			authorSubElement = document.createElement("fname");
			if (StringUtils.isNotBlank(authorVO.getFname()))
				authorSubElement.appendChild(document.createTextNode(authorVO.getFname()));
			else
				authorSubElement.appendChild(document.createTextNode("\u00A0"));
			author.appendChild(authorSubElement);

			// fname element
			authorSubElement = document.createElement("mname");
			if (StringUtils.isNotBlank(authorVO.getMname()))
				authorSubElement.appendChild(document.createTextNode(authorVO.getMname()));
			else
				authorSubElement.appendChild(document.createTextNode("\u00A0"));
			author.appendChild(authorSubElement);

			// lname element
			authorSubElement = document.createElement("lname");
			if (StringUtils.isNotBlank(authorVO.getLname()))
				authorSubElement.appendChild(document.createTextNode(authorVO.getLname()));
			else
				authorSubElement.appendChild(document.createTextNode("\u00A0"));
			author.appendChild(authorSubElement);

			// email element
			authorSubElement = document.createElement("email");
			if (StringUtils.isNotBlank(authorVO.getEmail()))
				authorSubElement.appendChild(document.createTextNode(authorVO.getEmail()));
			else
				authorSubElement.appendChild(document.createTextNode("\u00A0"));
			author.appendChild(authorSubElement);

			//department element
			authorSubElement = document.createElement("department");
			if (StringUtils.isNotBlank(authorVO.getDepartment()))
				authorSubElement.appendChild(document.createTextNode(authorVO.getDepartment()));
			else
				authorSubElement.appendChild(document.createTextNode("\u00A0"));
			author.appendChild(authorSubElement);
			
			// institute element
			authorSubElement = document.createElement("institute");
			if (StringUtils.isNotBlank(authorVO.getInstitution()))
				authorSubElement.appendChild(document.createTextNode(authorVO.getInstitution()));
			else
				authorSubElement.appendChild(document.createTextNode("\u00A0"));
			author.appendChild(authorSubElement);

			// city element
			authorSubElement = document.createElement("city");
			if (StringUtils.isNotBlank(authorVO.getCity()))
				authorSubElement.appendChild(document.createTextNode(authorVO.getCity()));
			else
				authorSubElement.appendChild(document.createTextNode("\u00A0"));
			author.appendChild(authorSubElement);

			// state element
			authorSubElement = document.createElement("state");
			if (StringUtils.isNotBlank(authorVO.getState()))
				authorSubElement.appendChild(document.createTextNode(authorVO.getState()));
			else
				authorSubElement.appendChild(document.createTextNode("\u00A0"));
			author.appendChild(authorSubElement);

			// Researcher Id element
			authorSubElement = document.createElement("researcher_id");
			if (StringUtils.isNotBlank(authorVO.getResearcherId()))
				authorSubElement.appendChild(document.createTextNode(authorVO.getResearcherId()));
			else
				authorSubElement.appendChild(document.createTextNode("\u00A0"));
			author.appendChild(authorSubElement);

			// country element
			authorSubElement = document.createElement("country");
			if (StringUtils.isNotBlank(authorVO.getCountry()))
				authorSubElement.appendChild(document.createTextNode(authorVO.getCountry()));
			else
				authorSubElement.appendChild(document.createTextNode("\u00A0"));
			author.appendChild(authorSubElement);

			// zip element
			authorSubElement = document.createElement("zip");
			if (StringUtils.isNotBlank(authorVO.getZip()))
				authorSubElement.appendChild(document.createTextNode(authorVO.getZip()));
			else
				authorSubElement.appendChild(document.createTextNode("\u00A0"));
			author.appendChild(authorSubElement);

			// address element
			authorSubElement = document.createElement("address");
			if (StringUtils.isNotBlank(authorVO.getAddress()))
				authorSubElement.appendChild(document.createTextNode(authorVO.getAddress()));
			else
				authorSubElement.appendChild(document.createTextNode("\u00A0"));
			author.appendChild(authorSubElement);

			// address_2 element
			authorSubElement = document.createElement("address_2");
			if (StringUtils.isNotBlank(authorVO.getAddress_2()))
				authorSubElement.appendChild(document.createTextNode(authorVO.getAddress_2()));
			else
				authorSubElement.appendChild(document.createTextNode("\u00A0"));
			author.appendChild(authorSubElement);

			// address_3 element
			authorSubElement = document.createElement("address_3");
			if (StringUtils.isNotBlank(authorVO.getAddress_3()))
				authorSubElement.appendChild(document.createTextNode(authorVO.getAddress_3()));
			else
				authorSubElement.appendChild(document.createTextNode("\u00A0"));
			author.appendChild(authorSubElement);

			// corresponding_address_1 element
			authorSubElement = document.createElement("corresponding_address_1");
			if (StringUtils.isNotBlank(authorVO.getCorresponding_address_1()))
				authorSubElement.appendChild(document.createTextNode(authorVO.getCorresponding_address_1()));
			else
				authorSubElement.appendChild(document.createTextNode("\u00A0"));
			author.appendChild(authorSubElement);

			// corresponding_address_2 element
			authorSubElement = document.createElement("corresponding_address_2");
			if (StringUtils.isNotBlank(authorVO.getCorresponding_address_2()))
				authorSubElement.appendChild(document.createTextNode(authorVO.getCorresponding_address_2()));
			else
				authorSubElement.appendChild(document.createTextNode("\u00A0"));
			author.appendChild(authorSubElement);

			// corresponding_address_3 element
			authorSubElement = document.createElement("corresponding_address_3");
			if (StringUtils.isNotBlank(authorVO.getCorresponding_address_3()))
				authorSubElement.appendChild(document.createTextNode(authorVO.getCorresponding_address_3()));
			else
				authorSubElement.appendChild(document.createTextNode("\u00A0"));
			author.appendChild(authorSubElement);

			// corresponding_city element
			authorSubElement = document.createElement("corresponding_city");
			if (StringUtils.isNotBlank(authorVO.getCorresponding_city()))
				authorSubElement.appendChild(document.createTextNode(authorVO.getCorresponding_city()));
			else
				authorSubElement.appendChild(document.createTextNode("\u00A0"));
			author.appendChild(authorSubElement);

			// corresponding_state element
			authorSubElement = document.createElement("corresponding_state");
			if (StringUtils.isNotBlank(authorVO.getCorresponding_state()))
				authorSubElement.appendChild(document.createTextNode(authorVO.getCorresponding_state()));
			else
				authorSubElement.appendChild(document.createTextNode("\u00A0"));
			author.appendChild(authorSubElement);

			// corresponding_country element
			authorSubElement = document.createElement("corresponding_country");
			if (StringUtils.isNotBlank(authorVO.getCorresponding_country()))
				authorSubElement.appendChild(document.createTextNode(authorVO.getCorresponding_country()));
			else
				authorSubElement.appendChild(document.createTextNode("\u00A0"));
			author.appendChild(authorSubElement);

			// is_corresponding element
			authorSubElement = document.createElement("is_corresponding");
			if (StringUtils.isNotBlank(authorVO.getIs_corresponding()))
				authorSubElement.appendChild(document.createTextNode(authorVO.getIs_corresponding()));
			else
				authorSubElement.appendChild(document.createTextNode("\u00A0"));
			author.appendChild(authorSubElement);

			// corresponding_zip element
			authorSubElement = document.createElement("corresponding_zip");
			if (StringUtils.isNotBlank(authorVO.getCorresponding_zip()))
				authorSubElement.appendChild(document.createTextNode(authorVO.getCorresponding_zip()));
			else
				authorSubElement.appendChild(document.createTextNode("\u00A0"));
			author.appendChild(authorSubElement);

			// phonenumber element
			authorSubElement = document.createElement("phonenumber");
			if (StringUtils.isNotBlank(authorVO.getPhonenumber()))
				authorSubElement.appendChild(document.createTextNode(authorVO.getPhonenumber()));
			else
				authorSubElement.appendChild(document.createTextNode("\u00A0"));
			author.appendChild(authorSubElement);

			// mobilenumber element
			authorSubElement = document.createElement("mobilenumber");
			if (StringUtils.isNotBlank(authorVO.getMobilenumber()))
				authorSubElement.appendChild(document.createTextNode(authorVO.getMobilenumber()));
			else
				authorSubElement.appendChild(document.createTextNode("\u00A0"));
			author.appendChild(authorSubElement);

			// faxno element
			authorSubElement = document.createElement("fax");
			if (StringUtils.isNotBlank(authorVO.getFaxno()))
				authorSubElement.appendChild(document.createTextNode(authorVO.getFaxno()));
			else
				authorSubElement.appendChild(document.createTextNode("\u00A0"));
			author.appendChild(authorSubElement);

			// orcid element
			authorSubElement = document.createElement("orcid");
			if (StringUtils.isNotBlank(authorVO.getOrcidId()))
				authorSubElement.appendChild(document.createTextNode(authorVO.getOrcidId()));
			else
				authorSubElement.appendChild(document.createTextNode("\u00A0"));
			author.appendChild(authorSubElement);

			authorSubElement = document.createElement("author_order");
			if (StringUtils.isNotBlank(authorVO.getCoAuthorOrder()))
				authorSubElement.appendChild(document.createTextNode(authorVO.getCoAuthorOrder()));
			else
				authorSubElement.appendChild(document.createTextNode("\u00A0"));
			author.appendChild(authorSubElement);

			// copyright_agreement
			authorSubElement = document.createElement("copyright_agreement");
			if (StringUtils.isNotBlank(authorVO.getCopyRightForm()))
				authorSubElement.appendChild(document.createTextNode(authorVO.getCopyRightForm()));
			else
				authorSubElement.appendChild(document.createTextNode("NO"));
			author.appendChild(authorSubElement);

			// copyright_agreement_content
			authorSubElement = document.createElement("copyright_agreement_content");
			if (StringUtils.isNotBlank(authorVO.getCopyRightContent()))
				authorSubElement.appendChild(document.createTextNode(authorVO.getCopyRightContent()));
			else
				authorSubElement.appendChild(document.createTextNode("\u00A0"));
			author.appendChild(authorSubElement);
		}

		/****** sub-root element *********/
		// root element
		Element images = document.createElement("images");
		root.appendChild(images);

		SimpleDateFormat dateFor = new SimpleDateFormat("dd-MMM-yy");
		List<ImageVO> imageList = manuscriptListVO.getImage();
		Element imageElement = null;
		Element imageSubElement = null;
		for (ImageVO imageVO : imageList) {
			if (!imageVO.getImage_file_name().equalsIgnoreCase("Bundled.pdf")
					&& !imageVO.getImage_file_name().equalsIgnoreCase("BundledPdfForReviewer.pdf")
					&& !imageVO.getImage_file_name().equalsIgnoreCase("Bundled.docx")
					&& !imageVO.getImage_file_name().equalsIgnoreCase(manuscriptName)
					&& !imageVO.getImage_file_name().equalsIgnoreCase(firstPageName)) {

				imageElement = document.createElement("image");
				images.appendChild(imageElement);

				// image_file_name element
				imageSubElement = document.createElement("image_file_name");
				if (imageVO.getImage_file_name() != null
						&& !imageVO.getImage_file_name().equalsIgnoreCase(""))
					imageSubElement.appendChild(document.createTextNode(imageVO.getImage_file_name()));
				else
					imageSubElement.appendChild(document.createTextNode("\u00A0"));
				imageElement.appendChild(imageSubElement);

				// image_type element
				imageSubElement = document.createElement("image_type");
				if (imageVO.getImage_type() != null
						&& !imageVO.getImage_type().equalsIgnoreCase(""))
					imageSubElement.appendChild(document.createTextNode(imageVO.getImage_type()));
				else
					imageSubElement.appendChild(document.createTextNode("\u00A0"));
				imageElement.appendChild(imageSubElement);

				// legend element
				imageSubElement = document.createElement("legend");
				if (imageVO.getLegend() != null && !imageVO.getLegend().equalsIgnoreCase(""))
					imageSubElement.appendChild(document.createTextNode(imageVO.getLegend()));
				else
					imageSubElement.appendChild(document.createTextNode("\u00A0"));
				imageElement.appendChild(imageSubElement);

				// citation element
				imageSubElement = document.createElement("citation");
				if (imageVO.getCitation() != null
						&& !imageVO.getCitation().equalsIgnoreCase(""))
					imageSubElement.appendChild(document.createTextNode(imageVO.getCitation()));
				else
					imageSubElement.appendChild(document.createTextNode("\u00A0"));
				imageElement.appendChild(imageSubElement);
			}
		}

		// submission_date element
		Element submissionDate = document.createElement("submission_date");
		if (manuscriptListVO.getSubmissionDate() != null)
			submissionDate.appendChild(document.createTextNode(dateFor.format(manuscriptListVO.getSubmissionDate())));
		else
			submissionDate.appendChild(document.createTextNode("\u00A0"));
		root.appendChild(submissionDate);

		// finalDecisionDate element
		Element finalDecisionDate = document.createElement("review");
		if (manuscriptListVO.getFinalDecisionDate() != null)
			finalDecisionDate.appendChild(document.createTextNode(manuscriptListVO.getFinalDecisionDate().toString()));
		else
			finalDecisionDate.appendChild(document.createTextNode("\u00A0"));
		root.appendChild(finalDecisionDate);

		// accepted_date element
		Element acceptedDate = document.createElement("Accepted");
		acceptedDate.appendChild(document.createTextNode(dateFor.format(new Date())));
		root.appendChild(acceptedDate);

		// review_date element
		Element publishingDate = document.createElement("publishing_date");
		if (manuscriptListVO.getPublishingDate() != null)
			publishingDate.appendChild(document.createTextNode(manuscriptListVO.getPublishingDate().toString()));
		else
			publishingDate.appendChild(document.createTextNode("\u00A0"));
		root.appendChild(publishingDate);
		
		// resubmission_date element
		Element revisedDate = document.createElement("revision_date");
		if (manuscriptListVO.getResubmissionDate() != null)
			revisedDate.appendChild(document.createTextNode(dateFor.format(manuscriptListVO.getResubmissionDate())));
		else
			revisedDate.appendChild(document.createTextNode("\u00A0"));
		root.appendChild(revisedDate);

		// create the xml file
		// transform the DOM Object to an XML File
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource domSource = new DOMSource(document);
		StreamResult streamResult = new StreamResult(xmlFilePath.getAbsolutePath());

		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.transform(domSource, streamResult);
		// streamResult.getOutputStream().close();

		log.info("XML created successfully");
	}

	public static boolean createZipFile(ManuscriptListVO manuscriptListVO, ErrorLogRepo errorLogRepo, String baseFolderPath, String operation) {
		log.info("createZipFile start");

		boolean success = true;
		try {
			/************ Xml File Moved Another folder ***********/
			log.info("Moving Xml File to Another folder");
			File xmlSource = new File(baseFolderPath + manuscriptListVO.getManuscripCode() + ".xml");
			File xmlDest = new File(baseFolderPath + manuscriptListVO.getManuscripCode() + File.separator);
			log.info("xmlSource : " + xmlSource + " ," + "xmlDest : " + xmlDest);
			FileUtils.copyFileToDirectory(xmlSource, xmlDest);

			/************ Qr FIle File Moved Another folder ***********/
			log.info("Moving Qr FIle to Another folder");
			File qrSource = new File(baseFolderPath + manuscriptListVO.getManuscripCode() + ".png");
			File qrDest = new File(baseFolderPath + manuscriptListVO.getManuscripCode() + File.separator);
			log.info("qrSource : " + qrSource + " ," + "qrDest : " + qrDest);
			FileUtils.copyFileToDirectory(qrSource, qrDest);

			File source = null;
			File dest = null;
			for (ImageVO imageVO : manuscriptListVO.getImage()) {
				log.info("File Name :   " + imageVO.getImage_file_name());

				if (!imageVO.getImage_file_name().equalsIgnoreCase("Bundled.docx")
						&& !imageVO.getImage_file_name().equalsIgnoreCase("Bundled.pdf")
						&& !imageVO.getImage_file_name().equalsIgnoreCase("BundledPdfForReviewer.pdf")
						&& !imageVO.getImage_file_name().equalsIgnoreCase("Bundled.html")) {

					source = new File(baseFolderPath + imageVO.getImage_file_name());
					dest = new File(baseFolderPath + manuscriptListVO.getManuscripCode() + File.separator);
					log.info("Source : " + source + " ," + "dest : " + dest);
					FileUtils.copyFileToDirectory(source, dest);
				}
			}

			ZipUtil.pack(new File(baseFolderPath + manuscriptListVO.getManuscripCode()), new File(baseFolderPath + manuscriptListVO.getManuscripCode()
					+ "_UNIPRR.zip"));

			log.info("ZIP File created successfully");
		} catch (Exception e) {
			success = false;
			log.error("Exception while creating zip file : ", e);

			ErrorLog errorLog = new ErrorLog();
			errorLog.setJournalId(manuscriptListVO.getJournalId());
			errorLog.setManuscriptId(manuscriptListVO.getManuScriptId());
			errorLog.setManuscriptVersion(manuscriptListVO.getManuscriptVersion());
			errorLog.setErrorMessage("Zip creation failed : " + e.getMessage());
			errorLog.setOperation(operation);
			errorLogRepo.save(errorLog);
		}

		return success;
	}

	public static void copyForUnitouchIngestion(ManuscriptListVO manuscriptListVO, String baseFolderPath, String medknowLocalPath) throws IOException {
		log.info("---Inside copyForUnitouchIngestion---");
		File sourceFile = new File(baseFolderPath + manuscriptListVO.getManuscripCode() + "_UNIPRR.zip");
		File destFile = new File(medknowLocalPath + manuscriptListVO.getManuscripCode() + "_UNIPRR.zip");

		log.info("Local copy file to medknow folder start");
		FileUtils.copyFile(sourceFile, destFile);

		log.info("Local copy to medKnow done");

	}

	public static List<EditorDashboardVO> sortAndLimitEditorDashboardUtil(int page, int limit, String sortBy, String sort, List<EditorDashboardVO> list) {
		
		if (sortBy.equalsIgnoreCase("dueDate")) {
			
			if (sort.equalsIgnoreCase("asc")) {
				list = list.stream()
						.sorted(Comparator.comparing(EditorDashboardVO::getDueDate, Comparator.nullsFirst(Comparator.naturalOrder())))
				.collect(Collectors.toList());
			}else {
				list = list.stream()
						.sorted(Comparator.comparing(EditorDashboardVO::getDueDate, Comparator.nullsLast(Comparator.reverseOrder())))
				.collect(Collectors.toList());					
			}
		}
		
		if (sortBy.equalsIgnoreCase("submissionDate") || sortBy.equalsIgnoreCase("assignDate")) {

			if (sort.equalsIgnoreCase("asc")) {
				list = list.stream()
						.sorted(Comparator.comparing(EditorDashboardVO::getAssignedDate, Comparator.nullsFirst(Comparator.naturalOrder())))
				.collect(Collectors.toList());
			}else {
				list = list.stream()
						.sorted(Comparator.comparing(EditorDashboardVO::getAssignedDate, Comparator.nullsLast(Comparator.reverseOrder())))
				.collect(Collectors.toList());					
			}
		}
		
		int fromIndex = limit * ((page + 1) - 1);
		int toIndex = limit * (page + 1);
		if (toIndex > list.size()) {
			toIndex = list.size();
		}
		if (fromIndex > toIndex) {
			fromIndex = toIndex;
		}
		return list.subList(fromIndex, toIndex);

	}
	
	public static List<ManuscriptListVO> sortAndLimitManuscriptListUtil(int page, int limit, String sortBy, String sort, List<ManuscriptListVO> list) {
		
		if (sortBy.equalsIgnoreCase("dueDate")) {
			
			if (sort.equalsIgnoreCase("asc")) {
				list = list.stream()
						.sorted(Comparator.comparing(ManuscriptListVO::getDueDate, Comparator.nullsFirst(Comparator.naturalOrder())))
				.collect(Collectors.toList());
			}else {
				list = list.stream()
						.sorted(Comparator.comparing(ManuscriptListVO::getDueDate, Comparator.nullsLast(Comparator.reverseOrder())))
				.collect(Collectors.toList());					
			}
		}
		
		if (sortBy.equalsIgnoreCase("assignDate") || sortBy.equalsIgnoreCase("updatedOn")) {
			
			if (sort.equalsIgnoreCase("asc")) {
				list = list.stream()
						.sorted(Comparator.comparing(ManuscriptListVO::getAssignDate, Comparator.nullsFirst(Comparator.naturalOrder())))
				.collect(Collectors.toList());
			}else {
				list = list.stream()
						.sorted(Comparator.comparing(ManuscriptListVO::getAssignDate, Comparator.nullsLast(Comparator.reverseOrder())))
				.collect(Collectors.toList());					
			}
		}
		
		if (sortBy.equalsIgnoreCase("manuscripCode")) {

			Comparator<ManuscriptListVO> compareById = (ManuscriptListVO o1,
					ManuscriptListVO o2) -> o1.getManuscripCode().compareTo(o2.getManuscripCode());

			if (sort.equalsIgnoreCase("asc"))
				Collections.sort(list, compareById);
			else
				Collections.sort(list, compareById.reversed());

		}

		if (sortBy.equalsIgnoreCase("manuscripTitle")) {

			Comparator<ManuscriptListVO> compareById = (ManuscriptListVO o1,
					ManuscriptListVO o2) -> o1.getManuscripTitle().compareTo(o2.getManuscripTitle());

			if (sort.equalsIgnoreCase("asc"))
				Collections.sort(list, compareById);
			else
				Collections.sort(list, compareById.reversed());

		}

		if (sortBy.equalsIgnoreCase("manuScriptType")) {

			Comparator<ManuscriptListVO> compareById = (ManuscriptListVO o1,
					ManuscriptListVO o2) -> o1.getManuScriptType().compareTo(o2.getManuScriptType());

			if (sort.equalsIgnoreCase("asc"))
				Collections.sort(list, compareById);
			else
				Collections.sort(list, compareById.reversed());

		}

		if (sortBy.equalsIgnoreCase("editorName")) {

			Comparator<ManuscriptListVO> compareById = (ManuscriptListVO o1,
					ManuscriptListVO o2) -> o1.getEditorName().compareTo(o2.getEditorName());

			if (sort.equalsIgnoreCase("asc"))
				Collections.sort(list, compareById);
			else
				Collections.sort(list, compareById.reversed());

		}

		if (sortBy.equalsIgnoreCase("authorName")) {

			Comparator<ManuscriptListVO> compareById = (ManuscriptListVO o1,
					ManuscriptListVO o2) -> o1.getAuthorName().compareTo(o2.getAuthorName());

			if (sort.equalsIgnoreCase("asc"))
				Collections.sort(list, compareById);
			else
				Collections.sort(list, compareById.reversed());

		}

		if (sortBy.equalsIgnoreCase("submissionDate")) {

			if (sort.equalsIgnoreCase("asc")) {
				list = list.stream()
						.sorted(Comparator.comparing(ManuscriptListVO::getSubmissionDate, Comparator.nullsFirst(Comparator.naturalOrder())))
				.collect(Collectors.toList());
			}else {
				list = list.stream()
						.sorted(Comparator.comparing(ManuscriptListVO::getSubmissionDate, Comparator.nullsLast(Comparator.reverseOrder())))
				.collect(Collectors.toList());					
			}
		}
		
		int fromIndex = limit * ((page + 1) - 1);
		int toIndex = limit * (page + 1);
		if (toIndex > list.size()) {
			toIndex = list.size();
		}
		if (fromIndex > toIndex) {
			fromIndex = toIndex;
		}
		return list.subList(fromIndex, toIndex);

	}
}
