package com.digi.uniprr.controller;

import java.io.File;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.digi.uniprr.VO.AuthorProofFileVo;
import com.digi.uniprr.VO.IDVO;
import com.digi.uniprr.model.ManuScriptFile;
import com.digi.uniprr.service.ManuScriptFileService;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping(value = "/manuScriptFile")
public class ManuScriptFileRestController {
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	ManuScriptFileService manuScriptFileService;
	
	@Autowired
	Environment environment;

	@Autowired
	ServletContext context;

	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public ResponseEntity<Object> create(@RequestBody ManuScriptFile manuScriptFile) {
		if (manuScriptFileService.isIdExist(manuScriptFile.getManuscriptFileId())) {
			return new ResponseEntity<Object>("Already exist", HttpStatus.CONFLICT);
		}
		manuScriptFileService.createManuScriptFile(manuScriptFile);
		return new ResponseEntity<Object>("Created successfully", HttpStatus.OK);
	}

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public List<ManuScriptFile> getManuScriptFileDetails() {
		List<ManuScriptFile> manuScriptList = manuScriptFileService.getManuScriptFileDetails();
		return manuScriptList;
	}

	// For Editor Login- ManuscriptList from Drop-Down Action History
	// { "manuscriptFileId":1 }
	@RequestMapping(value = "/submissionFiles", method = RequestMethod.GET)
	public List<ManuScriptFile> submissionFiles(@RequestBody ManuScriptFile manuScriptFile) {
		List<ManuScriptFile> manuScriptList = manuScriptFileService.getFileDetailsById(manuScriptFile);
		return manuScriptList;
	}

	/***Send Back to Author
	 For Editor Login- ManuscriptList from Drop-Down Action History
	 { "manuscriptFileId":1 }****/
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public ResponseEntity<Object> update(@RequestBody ManuScriptFile manuScriptFile) {
		if (manuScriptFileService.isIdExist(manuScriptFile.getManuscriptFileId())) {
			manuScriptFileService.updateManuScriptFileDetails(manuScriptFile);
			return new ResponseEntity<Object>("Updated successfully", HttpStatus.OK);

		}
		return new ResponseEntity<Object>("Does not exist", HttpStatus.BAD_REQUEST);
	}

	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public ResponseEntity<Object> delete(@RequestBody ManuScriptFile manuScriptFile) {
		if (manuScriptFileService.isIdExist(manuScriptFile.getManuscriptFileId())) {
			manuScriptFileService.deleteManuScriptFileById(manuScriptFile.getManuscriptFileId());
			return new ResponseEntity<Object>("Deleted successfully", HttpStatus.OK);
		}
		return new ResponseEntity<Object>("Id does not exist", HttpStatus.BAD_REQUEST);
	}

	
	@RequestMapping(value = "/listById", method = RequestMethod.POST)
	public ResponseEntity<?> listById(@RequestBody ManuScriptFile manuscriptFile) {
		ManuScriptFile manu = manuScriptFileService.getListByManuscriptId(manuscriptFile.getManuscriptFileId());
		return new ResponseEntity<>(manu, HttpStatus.OK);
	}

	@RequestMapping(value = "/download", method = RequestMethod.POST)
	public ResponseEntity<?> download(@RequestBody ManuScriptFile manuscriptFile) {
		JSONObject obj = new JSONObject();
		ManuScriptFile manuscriptFileDetails = manuScriptFileService.getManuScriptFile(manuscriptFile);
		String downloadFile = manuscriptFileDetails.getFilePath() + File.separator
				+ manuscriptFileDetails.getFileName();

		obj.put("file", downloadFile);

		return new ResponseEntity<>(obj, HttpStatus.OK);
	}

	@RequestMapping(value = "/deleteFile", method = RequestMethod.POST)
	public ResponseEntity<?> deleteFile(@RequestBody ManuScriptFile manuscriptFile) {
		JSONObject obj = new JSONObject();
		ManuScriptFile manuscriptFileDetails = manuScriptFileService.getManuScriptFile(manuscriptFile);
		// String downloadFile = manuscriptFileDetails.getFilePath() + File.separator
		// +manuscriptFileDetails.getFileName();
		File dir = new File(manuscriptFileDetails.getFilePath() + File.separator + manuscriptFileDetails.getFileName());
		if (!dir.exists()) {
			obj.put("message", "File not found");
			return new ResponseEntity<>(obj, HttpStatus.CONFLICT);
		} else {
			dir.delete();
			manuScriptFileService.deleteManuScriptFileById(manuscriptFile.getManuscriptFileId());
			obj.put("message", "File Deleted...");
		}
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}

	@PostMapping(value = "copyAllFile")
	public ResponseEntity<?> copyAllFile(@RequestBody IDVO id) {
		JSONObject obj = new JSONObject();
		try {
			manuScriptFileService.copyAllFile(id);
			obj.put("data", "cpoied");
		}catch(Exception e) {
			log.info("Exception :::  "+e);
			obj.put("data", "Something went wrong");
			return new ResponseEntity<>(obj, HttpStatus.CONFLICT);
		}
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/readFileFromFolder", method = RequestMethod.POST)
	public ResponseEntity<?> readFileFromFolder(@RequestBody IDVO id) {
		Map<String,AuthorProofFileVo> fileLocation = manuScriptFileService.readFileFromFolder(id.getName(), id.getStepName());
		JSONObject obj = new JSONObject();
		if(fileLocation!=null) {
			obj.put("data", fileLocation.values());
			return new ResponseEntity<>(obj, HttpStatus.OK);
		}
		return new ResponseEntity<>("No file found", HttpStatus.OK);
	}
	
	@GetMapping("/downloadProofFile")
	public ResponseEntity<Resource> downloadProofFile(@RequestParam("filePath") String filePath,HttpServletRequest request) {

	    log.info("===========================================");
	    log.info("Download Proof File");
	    log.info("Requested Path : {}", filePath);
	    log.info("Client IP : {}", request.getRemoteAddr());

	    return manuScriptFileService.downloadProofFile(filePath);
	}
	
	@GetMapping("/downloadTechnicalFile")
	public ResponseEntity<Resource> downloadTechnicalFile(@RequestParam("filePath") String filePath,HttpServletRequest request) {

	    log.info("===========================================");
	    log.info("Download Technical File");
	    log.info("Requested Path : {}", filePath);
	    log.info("Client IP : {}", request.getRemoteAddr());

	    return manuScriptFileService.downloadTechnicalFile(filePath);
	}

}
