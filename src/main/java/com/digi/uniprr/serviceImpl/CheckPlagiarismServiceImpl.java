package com.digi.uniprr.serviceImpl;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Tuple;
import javax.transaction.Transactional;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.digi.uniprr.VO.FailedIthenicateVo;
import com.digi.uniprr.VO.IDVO;
import com.digi.uniprr.VO.ManuscriptListVO;
import com.digi.uniprr.VO.PlagiarismListVO;
import com.digi.uniprr.VO.PlagiarismVO;
import com.digi.uniprr.enums.MyTypeFactory;
import com.digi.uniprr.model.FailedIauthenticate;
import com.digi.uniprr.model.ManuScript;
import com.digi.uniprr.model.ManuScriptFile;
import com.digi.uniprr.model.PlagiarismCheck;
import com.digi.uniprr.repository.FailedIauthRepo;
import com.digi.uniprr.repository.ManuScriptFileRepo;
import com.digi.uniprr.repository.ManuScriptRepo;
import com.digi.uniprr.repository.PlagiarismCheckRepo;
import com.digi.uniprr.service.CheckPlagiarismService;
import com.digi.uniprr.service.MailQueueService;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.stream.Collectors;

@Service
@Transactional
public class CheckPlagiarismServiceImpl implements CheckPlagiarismService {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private ManuScriptRepo manuScriptRepo;

	@Autowired
	private PlagiarismCheckRepo plagiarismCheckRepo;

	@Autowired
	private ManuScriptFileRepo manuScriptFileRepo;

	@Autowired
	private FailedIauthRepo  failedIauthRepo;

	@Autowired
	private Environment env;

	@Autowired
	private MailQueueService mailQueueService;

	@Value("${uniprrFolderPath}")
	private String uniprrFolderPath;

	@Value("${baseUrl}")
	private String baseUrl;

	public static final String SECVPN_HOST_ADDRESS = "https://api.ithenticate.com/rpc";

//	@Override
//	public String uploadFile(IDVO id) {
//
//		String output = null;
//
//		try {
//			long startTotal = System.currentTimeMillis();
//			String sid = GetSid();
//			long totalTime = System.currentTimeMillis() - startTotal;
//
//			String folderid = GetFolderList(sid.trim());
//
//			String uploadfileId = uploadFile(sid.trim(), folderid, id);
//
//			String updateInDb = saveDataInDb(id, sid, uploadfileId);
//
//			if (updateInDb.equalsIgnoreCase("Data Save")) {
//				output = "File Uploaded";
//			}
//
//			log.info("sid  : " + sid);
//			log.info("uploadfileId  : " + uploadfileId);
//
//		} catch (MalformedURLException m) {
//			output = "Something Went Wrong";
//			log.info("Exception :" + m);
//		} catch (XmlRpcException x) {
//			output = "Something Went Wrong";
//			log.info("Exception :" + x);
//		}
//		return output;
//	}

	public String uploadFile(IDVO id) {
	    log.info("Entering uploadFile with IDVO: {}", id);

	    String output = null;
	    try {
	        long startTotal = System.currentTimeMillis();

	        String sid = GetSid();
	        log.info("Retrieved SID: {}", sid);

	        long totalTime = System.currentTimeMillis() - startTotal;
	        log.info("Time taken to get SID: {} ms", totalTime);

	        String folderid = GetFolderList(sid.trim());
	        log.info("Retrieved Folder ID: {}", folderid);

	        String uploadfileId = uploadFile(sid.trim(), folderid, id);
	        log.info("Uploaded File ID: {}", uploadfileId);

	        String updateInDb = saveDataInDb(id, sid, uploadfileId);
	        log.info("Result of saveDataInDb: {}", updateInDb);

	        if ("Data Save".equalsIgnoreCase(updateInDb)) {
	            output = "File Uploaded";
	    	    PlagiarismCheck plagiarismCheck = plagiarismCheckRepo.getReportByManuscriptAndNumber(
	    	            id.getManuscriptId(),
	    	            id.getManuscriptVersion(),
	    	            id.getIsActive()
	    	    );
		        if (plagiarismCheck != null&&plagiarismCheck.getDocId() == null) {
		        	
		    		try {
		    			String fileId = Getfilestatus(plagiarismCheck.getSessionId(), plagiarismCheck.getFileId());

		    			int docId =0;

		    			if(fileId !=null) {
		    				docId = Integer.parseInt(fileId);
		    			}

		    			if (docId != 0) {
		    				log.info("docId: "+docId);
		    				plagiarismCheck.setDocId(fileId);
		    				plagiarismCheck.setIsActive(1);
		    				plagiarismCheckRepo.saveAndFlush(plagiarismCheck);
		    			}

		    			String url = GetSimilarityperfile(plagiarismCheck.getSessionId(), docId);
		    			if (url != null) {
		    				plagiarismCheck.setUrl(url);
		    				plagiarismCheck.setIsActive(1);
		    				plagiarismCheck.setDocId(fileId);
		    				plagiarismCheckRepo.save(plagiarismCheck);
		    				log.info("url saved for plagiarismCheck id " +plagiarismCheck.getId()+" is: "+url);
		    			}

		    		} catch (MalformedURLException | XmlRpcException e) {
		    			log.info("Exception :" + e);
		    			e.printStackTrace();
		    		}
		        }
	        } else {
	            output = "Database Update Failed";
	        }
	    } catch (MalformedURLException m) {
	        output = "Something Went Wrong";
	        log.error("MalformedURLException occurred:", m);
	    } catch (XmlRpcException x) {
	        output = "Something Went Wrong";
	        log.error("XmlRpcException occurred:", x);
	    } catch (Exception e) {
	    	output = "Something Went Wrong";
	    	log.error("Unexpected Exception occurred:", e);
	    }

	    log.info("Exiting uploadFile with result: {}", output);
	    return output;
	}
	
	@Override
	public String uploadFileForInProgress(IDVO id) {

		String output = null;

		try {
			long startTotal = System.currentTimeMillis();
			String sid = GetSid();
			long totalTime = System.currentTimeMillis() - startTotal;

			String folderid = GetFolderList(sid.trim());
            id.setIsActive(id.getIsActive()-1);
			String uploadfileId = uploadFile(sid.trim(), folderid, id);
//			id.setIsActive(id.getIsActive()+1);

			String updateInDb = saveDataInDb(id, sid, uploadfileId);

			if (updateInDb.equalsIgnoreCase("Data Save")) {
				output = "File Uploaded";
			}

			log.debug("sid  : " + sid);
			log.debug("uploadfileId  : " + uploadfileId);

		} catch (MalformedURLException m) {
			output = "Something Went Wrong";
			log.info("Exception :" + m);
		} catch (XmlRpcException x) {
			output = "Something Went Wrong";
			log.info("Exception :" + x);
		}
		return output;
	}

	@Override
	public String activePlagiarism(Integer id) {
		String result = null;
		PlagiarismCheck plagiarismCheck = plagiarismCheckRepo.getOne(id);
		try {
			String fileId = Getfilestatus(plagiarismCheck.getSessionId(), plagiarismCheck.getFileId());

			int docId =0;

			if(fileId !=null) {
				docId = Integer.parseInt(fileId);
			}

			if (docId != 0) {
				plagiarismCheck.setDocId(fileId);
				plagiarismCheck.setIsActive(1);
				plagiarismCheckRepo.saveAndFlush(plagiarismCheck);
			}

			String url = GetSimilarityperfile(plagiarismCheck.getSessionId(), docId);
			if (url != null) {
				plagiarismCheck.setUrl(url);
				plagiarismCheck.setIsActive(1);
				plagiarismCheck.setDocId(fileId);
				plagiarismCheckRepo.save(plagiarismCheck);
				result = "Data Save In DB";
			}

		} catch (MalformedURLException | XmlRpcException e) {
			log.info("Exception :" + e);
			e.printStackTrace();
		}

		return result;
	}

	private  String GetSid() throws MalformedURLException, XmlRpcException {
	    log.info("Entering GetSid() to retrieve session ID");

	    String sessionId = null;

	    try {
	        XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
	        XmlRpcClient client = new XmlRpcClient();

	        config.setServerURL(new URL(SECVPN_HOST_ADDRESS));
	        client.setConfig(config);
	        client.setTypeFactory(new MyTypeFactory(client));

	        Map<String, String> credentials = new HashMap<>();
	        credentials.put("username", "ithenticate@medknow.com");
	        credentials.put("password", "medknowjow14");

	        Object[] params = new Object[] { credentials };

	        log.info("Sending login request to iThenticate API...");
	        Object response = client.execute("login", params);
	        log.info("Received login response: {}", response);

	        // Extract SID
	        String[] parts = response.toString().split(",");
	        for (String part : parts) {
	            if (part.contains("sid")) {
	                sessionId = part.replace("sid=", "").trim().replace("}", "");
	                log.info("Session ID extracted: {}", sessionId);
	            }
	        }

	        if (sessionId == null) {
	            log.warn("Session ID could not be found in login response: {}", response);
	        }
	    } catch (MalformedURLException e) {
	        log.error("MalformedURLException occurred while creating session: ", e);
	        throw e;
	    } catch (XmlRpcException e) {
	        log.error("XmlRpcException occurred during login: ", e);
	        throw e;
	    } catch (Exception e) {
	        log.error("Unexpected exception in GetSid(): ", e);
	        throw new RuntimeException("Unexpected error in GetSid()", e);
	    }

	    log.info("Exiting GetSid() with sessionId: {}", sessionId);
	    return sessionId;
	}


	private String GetFolderList(String Sid) throws MalformedURLException, XmlRpcException {
		log.info("Entering GetFolderList with SID: {}", Sid);
		String folderId = null;

		XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
		XmlRpcClient client = new XmlRpcClient();

		config.setServerURL(new URL(SECVPN_HOST_ADDRESS));
		client.setConfig(config);
		client.setTypeFactory(new MyTypeFactory(client));

		Map m = new HashMap();
		log.debug(Sid);
		m.put("sid", Sid);
		Object[] params = new Object[] { m };
		Object object = client.execute("folder.list", params);
		log.info("Received folder.list response: {}", object);
		Map<String, Object> fl1 = (Map) object;
		Object[] ob = (Object[]) fl1.get("folders");
		for (Object folObj : ob) {
			Map<String, Integer> fo = (Map) folObj;
			folderId = fo.get("id").toString();
			log.info("Found Folder ID: {}", folderId);
		}

		String[] Substringarray = object.toString().split(",");
		log.info("Exiting GetFolderList with Folder ID: {}", folderId);

		return folderId;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private String uploadFile(String Sid, String folderId, IDVO id) throws MalformedURLException, XmlRpcException {
		log.info("Entering uploadFile with SID: {}, folderId: {}, IDVO: {}", Sid, folderId, id);
		String fileLocation = null;
		String uploadId = "";

		List<ManuScriptFile> manuScriptFile = manuScriptFileRepo.getDataByManuscriptIdAndVersionAndNumber(
				id.getManuscriptId(), id.getManuscriptVersion(), id.getIsActive());
		log.info("Fetched {} ManuscriptFiles", manuScriptFile.size());
		ManuScript manuscript = manuScriptRepo.getById(id.getManuscriptId());
		String authorName = manuscript.getUser().getFirstName() + " " + manuscript.getUser().getLastName();

		if (manuScriptFile != null && !manuScriptFile.isEmpty()) {
			log.info("Found {} manuscript file(s).", manuScriptFile.size());
			for (ManuScriptFile manuScriptFile2 : manuScriptFile) {
				log.info("Checking fileType={} and isActive={}", 
		                manuScriptFile2.getFileType(), manuScriptFile2.getIsActive());
				if (manuScriptFile2.getFileType().equalsIgnoreCase("Main Documents/Article File")) {
					fileLocation = manuScriptFile2.getFilePath();
					log.info("Main document file found at path: {}", fileLocation);
				}
			}
		}

		fileLocation = fileLocation.replace(baseUrl, uniprrFolderPath);
		log.info("fileLocation : " + fileLocation);
		log.debug(fileLocation);
		if (fileLocation != null) {
			XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
			XmlRpcClient client = new XmlRpcClient();
			config.setEncoding("UTF-8");
			config.setServerURL(new URL(SECVPN_HOST_ADDRESS));
			client.setConfig(config);
			client.setTypeFactory(new MyTypeFactory(client));

			// Setting Parameter for login

			Map login = new HashMap();
			Map fileDetails = new HashMap();
			List<Map<String, String>> listOfMaps = new ArrayList<Map<String, String>>();
			fileDetails.put("author_last", authorName);
			fileDetails.put("upload", encoder(fileLocation));
			fileDetails.put("title", "Manuscript");
			fileDetails.put("filename", "Manuscript.docx");
			listOfMaps.add(fileDetails);
			login.put("sid", Sid);
			login.put("submit_to", 1);
			login.put("folder", folderId);
//			login.put("folder",1820198 );
			login.put("uploads", listOfMaps);
			Object[] params = new Object[] { login };

			Object object = client.execute("document.add", params);
			log.info("Received document.add response: {}", object);

			try {
				Thread.sleep(10 * 1000); // Wait for 10 seconds
			} catch (InterruptedException e) {
				log.warn("Sleep interrupted: ", e);
				e.printStackTrace();
			}
			Map<String, Object> fl1 = (Map) object;
			Object[] ob = (Object[]) fl1.get("uploaded");

			for (Object folObj : ob) {
				Map<String, Integer> fo = (Map) folObj;
				uploadId = fo.get("id").toString();
				log.info("Extracted uploadId: {}", uploadId);
			}

			String[] Substringarray = object.toString().split(",");

			for (int i = 0; i < Substringarray.length; i++) {

				// log.debug(Substringarray[i]);
			}
		} else {
			uploadId = "No Data Found";
		}
		log.info("Exiting uploadFile with uploadId: {}", uploadId);
		return uploadId;
	}

//	public static byte[] encoder(String fileUrl) {
//	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
//	    try (InputStream in = new URL(fileUrl.replace("\\", "/")).openStream()) {
//	        byte[] buffer = new byte[1024];
//	        int n;
//	        while ((n = in.read(buffer)) != -1) {
//	            baos.write(buffer, 0, n);
//	        }
//	    } catch (IOException e) {
//	        e.printStackTrace();
//	    }
//	    return baos.toByteArray();
//	}
	
	public static byte[] encoder(String filePath) {
		byte[] bFile = null;
		try {
			bFile = Files.readAllBytes(new File(filePath).toPath());
		} catch (IOException e) {
			e.printStackTrace();
		}

		return bFile;
	}


	private String saveDataInDb(IDVO id, String sid, String uploadfileId) {
		log.info("Entering saveDataInDb with IDVO: {}, SID: {}, UploadFileId: {}", id, sid, uploadfileId);
		String result = null;
		Date date = new Date();
		PlagiarismCheck plagiarismCheckData = plagiarismCheckRepo.getDataByFileNumber(id.getManuscriptId(),
				id.getManuscriptVersion(), id.getIsActive());
		if (plagiarismCheckData != null) {
			log.info("Existing PlagiarismCheck found");
			result = "Data Save";
		} else {
			PlagiarismCheck plagiarismCheck = new PlagiarismCheck();
			plagiarismCheck.setManuscriptId(id.getManuscriptId());
			plagiarismCheck.setManuscriptVersion(id.getManuscriptVersion());
			plagiarismCheck.setFileNubmer(id.getIsActive());
			plagiarismCheck.setSessionId(sid);
			plagiarismCheck.setFileId(uploadfileId);
			plagiarismCheck.setUrl("");
			plagiarismCheck.setJournalId(id.getJournalId());
			plagiarismCheck.setIsActive(0);
			plagiarismCheck.setCreatedDate(getCurrentUtcTime());

			plagiarismCheckRepo.saveAndFlush(plagiarismCheck);
			log.info("PlagiarismCheck data inserted for manucriptId: {}, version: {}",id.getManuscriptId(),id.getManuscriptVersion());

			result = "Data Save";

		}

		return result;
	}

//	private String saveDataInDb(IDVO id, String sid, String uploadfileId) {
//		String result = null;
//		Date date = new Date();
//		PlagiarismCheck plagiarismCheckData = plagiarismCheckRepo.getDataByFileNumber(id.getManuscriptId(), id.getManuscriptVersion());
//		if (plagiarismCheckData!=null && plagiarismCheckData.getId()!=null) {
//			plagiarismCheckData.setManuscriptId(id.getManuscriptId());
//			plagiarismCheckData.setManuscriptVersion(id.getManuscriptVersion());
//			plagiarismCheckData.setFileNubmer(id.getIsActive());
//			plagiarismCheckData.setSessionId(sid);
//			plagiarismCheckData.setFileId(uploadfileId);
//			plagiarismCheckData.setUrl("");
//			plagiarismCheckData.setJournalId(id.getJournalId());
//			plagiarismCheckData.setIsActive(0);
//			plagiarismCheckData.setCreatedDate(getCurrentUtcTime());
//			plagiarismCheckRepo.save(plagiarismCheckData);
//
//			result = "Data Save";
//		} else {
//			PlagiarismCheck plagiarismCheck = new PlagiarismCheck();
//			plagiarismCheck.setManuscriptId(id.getManuscriptId());
//			plagiarismCheck.setManuscriptVersion(id.getManuscriptVersion());
//			plagiarismCheck.setFileNubmer(id.getIsActive());
//			plagiarismCheck.setSessionId(sid);
//			plagiarismCheck.setFileId(uploadfileId);
//			plagiarismCheck.setUrl("");
//			plagiarismCheck.setJournalId(id.getJournalId());
//			plagiarismCheck.setIsActive(0);
//			plagiarismCheck.setCreatedDate(getCurrentUtcTime());
//			plagiarismCheckRepo.saveAndFlush(plagiarismCheck);
//
//			result = "Data Save";
//
//		}
//
//		return result;
//	}

	public static Date getCurrentUtcTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss");
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		SimpleDateFormat ldf = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss");
		Date d1 = null;
		try {
			d1 = ldf.parse( sdf.format(new Date()) );
		}
		catch (java.text.ParseException e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
		return d1;
	}
	
	private String Getfilestatus(String Sid, String docid) throws MalformedURLException, XmlRpcException {
		String sessionId = null;

		XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
		XmlRpcClient client = new XmlRpcClient();

		config.setServerURL(new URL(SECVPN_HOST_ADDRESS));
		client.setConfig(config);
		client.setTypeFactory(new MyTypeFactory(client));

		log.info("Initiating Getfilestatus with Sid: " + Sid + " and docid: " + docid);
		log.info("XML-RPC server URL configured: " + SECVPN_HOST_ADDRESS);

		// Setting Parameter for login
		Map m = new HashMap();
		// log.debug(Sid);
		m.put("sid", Sid);
		m.put("id", docid);

		log.info("Request parameters prepared: " + m);

		Object[] params = new Object[] { m };

		Object object = client.execute("document.get", params);
		log.info("Executed XML-RPC method: document.get");

		Map<String, Object> fl1 = (Map) object;

		Object ob[] = (Object[]) fl1.get("documents");
		String folderId = null;

		log.info("Documents received: " + (ob != null ? ob.length : 0));

		if (ob != null && ob.length > 0) {
			for (Object folObj : ob) {
				Map<String, Object> fo = (Map) folObj;
				// log.debug(fo.get("is_pending"));

				Object foId[] = (Object[]) fo.get("parts");
				log.debug("foId : " + foId);
				log.info("Parts found in document: " + (foId != null ? foId.length : 0));

				if (foId != null && foId.length > 0) {
					for (Object object2 : foId) {
						Map<String, Object> fogID = (Map) object2;
						// log.debug(fogID.get("id"));
						folderId = fogID.get("id").toString();
						log.info("Extracted folderId: " + folderId);
					}
				}
			}
		} else {
			log.info("No documents found in the response.");
		}

		String[] Substringarray = object.toString().split(",");

		for (int i = 0; i < Substringarray.length; i++) {
			// log.debug(Substringarray[i]);
			log.info("Response segment [" + i + "]: " + Substringarray[i]);
		}

		log.info("Returning folderId: " + folderId);
		return folderId;
	}



//	private String Getfilestatus(String Sid, String docid) throws MalformedURLException, XmlRpcException {
//		String sessionId = null;
//
//		XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
//		XmlRpcClient client = new XmlRpcClient();
//
//		config.setServerURL(new URL(SECVPN_HOST_ADDRESS));
//		client.setConfig(config);
//		client.setTypeFactory(new MyTypeFactory(client));
//
//		// Setting Parameter for login
//
//		Map m = new HashMap();
//		// log.debug(Sid);
//		m.put("sid", Sid);
//		m.put("id", docid);
//
//		Object[] params = new Object[] { m };
//
//		Object object = client.execute("document.get", params);
//
//		Map<String, Object> fl1 = (Map) object;
//
//		Object ob[] = (Object[]) fl1.get("documents");
//		String folderId = null;
//		if(ob !=null && ob.length>0) {
//			for (Object folObj : ob) {
//				Map<String, Object> fo = (Map) folObj;
//				// log.debug(fo.get("is_pending"));
//
//				Object foId[] = (Object[]) fo.get("parts");
//				log.debug("foId : " + foId);
//				if(foId!=null && foId.length>0) {
//					for (Object object2 : foId) {
//						Map<String, Object> fogID = (Map) object2;
//						// log.debug(fogID.get("id"));
//						folderId = fogID.get("id").toString();
//					}
//				}
//
//			}
//		}
//
//		String[] Substringarray = object.toString().split(",");
//
//		for (int i = 0; i < Substringarray.length; i++) {
//
//			// log.debug(Substringarray[i]);
//		}
//
//		return folderId;
//	}

	public String GetSimilarityperfile(String Sid, int docid) throws MalformedURLException, XmlRpcException {
		String url = null;

		XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
		XmlRpcClient client = new XmlRpcClient();

		config.setServerURL(new URL(SECVPN_HOST_ADDRESS));
		client.setConfig(config);
		client.setTypeFactory(new MyTypeFactory(client));

		// Setting Parameter for login

		Map m = new HashMap();
		// log.debug(Sid);
		m.put("sid", Sid);
		// log.debug("docid--->" + docid);
		m.put("id", docid);
		m.put("exclude_quotes", 1);
		m.put("exclude_biblio", 1);
		m.put("exclude_small_matches", 1);

		Object[] params = new Object[] { m };

		Object object = client.execute("report.get", params);
		log.debug("object : " + object);

		Map<String, Object> fl1 = (Map) object;

		url = (String) fl1.get("view_only_url");

		String[] Substringarray = object.toString().split(",");

		/*
		 * for (int i = 0; i < Substringarray.length; i++) {
		 *
		 * log.debug(Substringarray[i]); }
		 */
		return url;
	}

	@Override
	public List<PlagiarismCheck> getReport() {
		List<PlagiarismCheck> data = plagiarismCheckRepo.getReport();
		return data;
	}

//	@Override
//	public PlagiarismCheck checkPlagiarismReport(IDVO id) {
//		PlagiarismCheck data = plagiarismCheckRepo.getReportByManuscriptAndNumber(id.getManuscriptId(),
//				id.getManuscriptVersion(), id.getIsActive());
//		if (data != null && data.getDocId() != null) {
//			int docId = Integer.parseInt(data.getDocId());
//			String url;
//			try {
//				String sid = GetSid();
//				data.setSessionId(sid);
//				url = GetSimilarityperfile(data.getSessionId(), docId);
//				if (url != null) {
//					data.setUrl(url);//
//					plagiarismCheckRepo.saveAndFlush(data);
//				}
//			} catch (MalformedURLException e) {
//				ManuScript manuscript = manuScriptRepo.getOne(id.getManuscriptId());
//				String mailFrom = env.getProperty("spring.mail.username");
//				String mailTo = env.getProperty("support.email");
//				String mailCC = env.getProperty("supportCC.email");
//				String body = "Palagarism URL generation Failed for Artcle  : "+manuscript.getManuscriptCode()+"\nFailure Reason: "+e.getMessage();
//				String subject = "Palagarism failed for Manuscript "+manuscript.getManuscriptCode();
//				Integer journalId = id.getJournalId();
//				Integer manuscriptId = id.getManuscriptId();
//				mailQueueService.insertMailQueue(mailFrom, mailTo, mailCC, journalId, manuscriptId, subject, body);
//
//				e.printStackTrace();
//			} catch (XmlRpcException e) {
//				ManuScript manuscript = manuScriptRepo.getOne(id.getManuscriptId());
//				String mailFrom = env.getProperty("spring.mail.username");
//				String mailTo = env.getProperty("support.email");
//				String mailCC = env.getProperty("supportCC.email");
//				String body = "Palagarism URL generation Failed for Artcle  : "+manuscript.getManuscriptCode()+"\nFailure Reason: "+e.getMessage();
//				String subject = "Palagarism failed for Manuscript "+manuscript.getManuscriptCode();
//				Integer journalId = id.getJournalId();
//				Integer manuscriptId = id.getManuscriptId();
//				mailQueueService.insertMailQueue(mailFrom, mailTo, mailCC, journalId, manuscriptId, subject, body);
//
//				e.printStackTrace();
//			}
//
//		}
//		return data;
//	}
	
	
	@Override
	@Transactional
	public PlagiarismCheck checkPlagiarismReport(IDVO id) {
	    PlagiarismCheck plagiarismCheck = plagiarismCheckRepo.getReportByManuscriptAndNumber(
	            id.getManuscriptId(),
	            id.getManuscriptVersion(),
	            id.getIsActive()
	    );

	    try {
	    	String sid = GetSid();
	    	plagiarismCheck.setSessionId(sid);
	        // Case 1: Record exists with docId
	        if (plagiarismCheck != null && plagiarismCheck.getDocId() != null) {
	            int docId = Integer.parseInt(plagiarismCheck.getDocId());

	                
	                

	                String url = GetSimilarityperfile(plagiarismCheck.getSessionId(), docId);
	                if (url != null) {
	                    plagiarismCheck.setUrl(url);
	                    plagiarismCheckRepo.saveAndFlush(plagiarismCheck);

	                    log.info("Generated plagiarism URL for ManuscriptId: {}, DocId: {}, SID: {}, URL: {}",
	                            id.getManuscriptId(), docId, sid, url);
	                } else {
	                    log.warn("Failed to generate plagiarism URL for ManuscriptId: {}, DocId: {}",
	                            id.getManuscriptId(), docId);
	                }

	            return plagiarismCheck;
	        }

	        // Case 2: Record exists but docId is missing → Upload file & update record
	        if (plagiarismCheck != null&&plagiarismCheck.getDocId() == null) {
	    		try {
	    			String fileId = Getfilestatus(plagiarismCheck.getSessionId(), plagiarismCheck.getFileId());

	    			int docId =0;

	    			if(fileId !=null) {
	    				docId = Integer.parseInt(fileId);
	    			}

	    			if (docId != 0) {
	    				log.info("docId: "+docId);
	    				plagiarismCheck.setDocId(fileId);
	    				plagiarismCheck.setIsActive(1);
	    				plagiarismCheckRepo.saveAndFlush(plagiarismCheck);
	    			}

	    			String url = GetSimilarityperfile(plagiarismCheck.getSessionId(), docId);
	    			if (url != null) {
	    				log.info("url: "+url);
	    				plagiarismCheck.setUrl(url);
	    				plagiarismCheck.setIsActive(1);
	    				plagiarismCheck.setDocId(fileId);
	    				plagiarismCheckRepo.save(plagiarismCheck);
	    			}

	    		} catch (MalformedURLException | XmlRpcException e) {
	    			log.info("Exception :" + e);
	    			e.printStackTrace();
	    		}
	        }

	    } catch (MalformedURLException | XmlRpcException e) {
	        handlePlagiarismFailure(id, e);
	    } catch (Exception e) {
	        log.error("Unexpected error in plagiarism check for ManuscriptId: {}, Reason: {}", id.getManuscriptId(), e.getMessage(), e);
	    }

	    return plagiarismCheck;
	    
	}

	
	private void handlePlagiarismFailure(IDVO id, Exception e) {
	    ManuScript manuscript = manuScriptRepo.findById(id.getManuscriptId()).orElse(null);

	    String mailFrom = env.getProperty("spring.mail.username");
	    String mailTo = env.getProperty("support.email");
	    String mailCC = env.getProperty("supportCC.email");

	    String manuscriptCode = (manuscript != null) ? manuscript.getManuscriptCode() : "Unknown";

	    String subject = "Plagiarism failed for Manuscript " + manuscriptCode;
	    String body = String.format("Plagiarism URL generation failed for Article: %s%nFailure Reason: %s",
	            manuscriptCode, e.getMessage());

	    mailQueueService.insertMailQueue(
	            mailFrom,
	            mailTo,
	            mailCC,
	            id.getJournalId(),
	            id.getManuscriptId(),
	            subject,
	            body
	    );

	    log.error("Plagiarism report generation failed for ManuscriptId: {}, Reason: {}", id.getManuscriptId(), e.getMessage(), e);
	}

	@Override
	public String checkCopyscape(IDVO id) {

		String u = "medknow";
		String k = "q7yyth5w3yz7zg8y";
		String o = "cpsearch";
		String e = "WINDOWS-1251";
		String t = id.getName();

		StringBuffer response = new StringBuffer();
		try {
			// String urlParameters =
			// "client_id=APP-XY03FOWMVO25GH81&client_secret=879c9868-4bf6-447a-b9e6-c6345f30302c&code=9VnuZ7&redirect_uri=http://localhost:8080/uniprr/orcid/orcidresponse&grant_type=authorization_code";
			String urlParameters = "";
			urlParameters = urlParameters.concat("u=" + u).concat("&k=" + k).concat("&o=" + o).concat("&e=" + e)
					.concat("&t=" + t);

			byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
			int postDataLength = postData.length;
			String request = "http://www.copyscape.com/api/";
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
			int responseCode = conn.getResponseCode();
			if (responseCode == 200) {
				BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				String inputLine;
				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				log.debug("response : " + response);
				in.close();
			} else {
				response.append("Something went wrong");
			}
		} catch (Exception ex) {
			log.debug("Exception : " + ex);
		}
		log.debug(response.toString());
		return response.toString();

		// return null;
	}

	@Override
	public boolean checkIfExist(IDVO id) {
		boolean data = false;
		PlagiarismCheck plagiarismCheck = plagiarismCheckRepo.getReportByManuscript(id.getManuscriptId(),
				id.getManuscriptVersion(), id.getIsActive());
		if (plagiarismCheck != null) {
			data = true;
		}
		return data;
	}

	@Override
	public List<ManuscriptListVO> getPlagiarismReport(IDVO id) {
		List<ManuscriptListVO> result = new ArrayList<>();
		List<Tuple> data = plagiarismCheckRepo.getReportByJournal(id.getJournalId());

		if (data != null) {
			result = data.stream().map(t -> new ManuscriptListVO(t.get(0, Integer.class), t.get(1, Integer.class), t.get(2, String.class),
							t.get(3, String.class), t.get(4, String.class), t.get(5, String.class), t.get(6, String.class), t.get(7, Date.class), t.get(8, String.class), t.get(9, Integer.class)))
					.collect(Collectors.toList());
		}


		return result;

	}
	
	@Override
	public Page<ManuscriptListVO> getPlagiarismReportPaged(IDVO id, Pageable pageable) {
	    String searchTerm = id.getSearchTerm() != null ? id.getSearchTerm().trim() : "";

	    Page<Tuple> data = plagiarismCheckRepo.getReportByJournal(id.getJournalId(), searchTerm, pageable);

	    return data.map(t -> new ManuscriptListVO(
	            t.get(0, Integer.class),
	            t.get(1, Integer.class),
	            t.get(2, String.class),
	            t.get(3, String.class),
	            t.get(4, String.class),
	            t.get(5, String.class),
	            t.get(6, String.class),
	            t.get(7, Date.class),
	            t.get(8, String.class),
	            t.get(9, Integer.class)
	    ));
	}
		

	@Override
	public List<ManuscriptListVO> getPlagiarismReportAll(IDVO id) {
	    Pageable pageable = Pageable.unpaged();
	    return getPlagiarismReportPaged(id, pageable).getContent();
	}



	@Override
	public List<PlagiarismListVO> getPlagiarism(PlagiarismVO pvo) {

		List<Object[]> plagiarism = null;
		if (pvo.getMonth() == 0) {
			plagiarism = plagiarismCheckRepo.getPlagiarismByYear(pvo.getYear());
		} else {
			plagiarism = plagiarismCheckRepo.getPlagiarismByMonthYear(pvo.getMonth(), pvo.getYear());
		}
		List<PlagiarismListVO> plagiarismList = new ArrayList<>();
		if (plagiarism != null) {
			for (Object[] objects : plagiarism) {
				PlagiarismListVO plagiarismListVO = new PlagiarismListVO();
				if (objects.length == 4) {
					plagiarismListVO.setJournalTitle(objects[0].toString());
					plagiarismListVO.setPlagcheckcount(Integer.parseInt(objects[1].toString()));
					plagiarismListVO.setCheckmonth(Integer.parseInt(objects[2].toString()));
					plagiarismListVO.setCheckyear(Integer.parseInt(objects[3].toString()));
					plagiarismList.add(plagiarismListVO);
				}
			}
		}
		return plagiarismList;
	}

	@Override
	public List<PlagiarismCheck> getInactivedata() {
		List<PlagiarismCheck> plagiarismCheck1 = plagiarismCheckRepo.getInActiveData();
		return plagiarismCheck1;
	}

	@Override
	public List<PlagiarismCheck> getListForURLGeneration() {
		List<PlagiarismCheck> listForURL = plagiarismCheckRepo.getListForURL();
		return listForURL;
	}

	@Override
	public void deletePalagrismById(Integer id) {
		plagiarismCheckRepo.deleteById(id);

	}

	@Override
	public void insertFailedPalagarism(FailedIthenicateVo failedIthenicateVo) {

		FailedIauthenticate failedIauthenticate = new FailedIauthenticate();
		failedIauthenticate.setJournalId(failedIthenicateVo.getJournalId());
		failedIauthenticate.setManuscriptId(failedIthenicateVo.getManuscriptId());
		failedIauthRepo.saveAndFlush(failedIauthenticate);

	}

	@Override
	public List<PlagiarismCheck> getInActiveData() {
		List<PlagiarismCheck> listForURL = plagiarismCheckRepo.getInActiveData();
		return listForURL;
	}
}
