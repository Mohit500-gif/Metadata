package com.digi.uniprr.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.digi.uniprr.VO.ArticleInfoAPI;
import com.digi.uniprr.VO.EICDashBoardListVO;
import com.digi.uniprr.VO.EmailTriggerVO;
import com.digi.uniprr.VO.IDVO;
import com.digi.uniprr.VO.JournalVO;
import com.digi.uniprr.VO.ManuScriptTrackCodeVO;
import com.digi.uniprr.VO.ManuScriptTrackDateVO;
import com.digi.uniprr.VO.ManuScriptTrackJNameVO;
import com.digi.uniprr.VO.ManuScriptTrackTitleVO;
import com.digi.uniprr.VO.ManuScriptTrackVO;
import com.digi.uniprr.VO.ManuScriptVO;
import com.digi.uniprr.VO.ManuscriptDetailForJleVO;
import com.digi.uniprr.VO.ManuscriptFilterVO;
import com.digi.uniprr.VO.ManuscriptListVO;
import com.digi.uniprr.VO.ManuscriptsDashboardCountVO;
import com.digi.uniprr.VO.ManuscriptsEditorDashboardCountVO;
import com.digi.uniprr.VO.TrackManuScriptVO;
import com.digi.uniprr.VO.UserVO;
import com.digi.uniprr.model.JournalChargeDetails;
import com.digi.uniprr.model.JournalManuscriptTypeCharge;
import com.digi.uniprr.model.ManuScript;
import com.digi.uniprr.model.StatusMaster;
import com.digi.uniprr.model.User;
import com.digi.uniprr.model.UserJournal;
import com.digi.uniprr.repository.JournalManuscriptTypeChargeRepo;
import com.digi.uniprr.repository.ManuScriptRepo;
import com.digi.uniprr.repository.UserJournalRepo;
import com.digi.uniprr.service.AwatingPaymentService;
import com.digi.uniprr.service.CommonService;
import com.digi.uniprr.service.EmailTriggerService;
import com.digi.uniprr.service.JournalService;
import com.digi.uniprr.service.ManuScriptService;
import com.digi.uniprr.service.StatusMasterService;
import com.digi.uniprr.service.TatEmailConfigurationService;
import com.digi.uniprr.service.UserService;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping(value = "/manuScriptAwating")
public class ManuScriptAwatingPaymentRestController {
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	AwatingPaymentService awatingPaymentService;
	
	
	JSONObject obj = new JSONObject();
	
	@RequestMapping(value = "/getAwatingPaymentManuscript", method = RequestMethod.POST)
	public ResponseEntity<?> getAwatingPaymentManuscript(@RequestBody ManuscriptListVO manuscriptVO) {
		try {
			Integer totalAwatingPaymentManuscript =0;
			List<ManuscriptListVO> manuscriptList = awatingPaymentService.getAwatingPaymentManuscript(manuscriptVO);
			if(manuscriptVO.getSearch() != null && manuscriptVO.getSearch() != "")
				totalAwatingPaymentManuscript = awatingPaymentService.getTotalAwatingPaymentManuscriptSearch(manuscriptVO);
			else
				totalAwatingPaymentManuscript = awatingPaymentService.getTotalAwatingPaymentManuscript(manuscriptVO);
			
			if(manuscriptVO.getSortBy().equalsIgnoreCase("manuscripCode"))
			{
			             
			Comparator<ManuscriptListVO> compareById = (ManuscriptListVO o1, ManuscriptListVO o2) -> o1.getManuscripCode().compareTo( o2.getManuscripCode() );
			 
			if(manuscriptVO.getSort().equalsIgnoreCase("asc"))
			Collections.sort(manuscriptList, compareById);
			else
			Collections.sort(manuscriptList, compareById.reversed());

			}
			
			if(manuscriptVO.getSortBy().equalsIgnoreCase("manuscripTitle"))
			{
			             
			Comparator<ManuscriptListVO> compareById = (ManuscriptListVO o1, ManuscriptListVO o2) -> o1.getManuscripTitle().compareTo( o2.getManuscripTitle() );
			 
			if(manuscriptVO.getSort().equalsIgnoreCase("asc"))
			Collections.sort(manuscriptList, compareById);
			else
			Collections.sort(manuscriptList, compareById.reversed());

			}
			
			if(manuscriptVO.getSortBy().equalsIgnoreCase("manuScriptType"))
			{
			             
			Comparator<ManuscriptListVO> compareById = (ManuscriptListVO o1, ManuscriptListVO o2) -> o1.getManuScriptType().compareTo( o2.getManuScriptType() );
			 
			if(manuscriptVO.getSort().equalsIgnoreCase("asc"))
			Collections.sort(manuscriptList, compareById);
			else
			Collections.sort(manuscriptList, compareById.reversed());

			}
			
			if(manuscriptVO.getSortBy().equalsIgnoreCase("journalTitle"))
			{
			             
			Comparator<ManuscriptListVO> compareById = (ManuscriptListVO o1, ManuscriptListVO o2) -> o1.getJournalTitle().compareTo( o2.getJournalTitle() );
			 
			if(manuscriptVO.getSort().equalsIgnoreCase("asc"))
			Collections.sort(manuscriptList, compareById);
			else
			Collections.sort(manuscriptList, compareById.reversed());

			}
			
			if(manuscriptVO.getSortBy().equalsIgnoreCase("authorName"))
			{
			             
			Comparator<ManuscriptListVO> compareById = (ManuscriptListVO o1, ManuscriptListVO o2) -> o1.getAuthorName().compareTo( o2.getAuthorName() );
			 
			if(manuscriptVO.getSort().equalsIgnoreCase("asc"))
			Collections.sort(manuscriptList, compareById);
			else
			Collections.sort(manuscriptList, compareById.reversed());

			}
			
			if(manuscriptVO.getSortBy().equalsIgnoreCase("updatedOn"))
			{
			             			 
			if(manuscriptVO.getSort().equalsIgnoreCase("asc")) {
				Collections.sort(manuscriptList, new Comparator<ManuscriptListVO>() {
					  public int compare(ManuscriptListVO o1, ManuscriptListVO o2) {
					      if (o1.getUpdatedOn() == null || o2.getUpdatedOn() == null)
					        return 0;
					      return o1.getUpdatedOn().compareTo(o2.getUpdatedOn());
					  }
					});
			}
			else
			{
				Collections.sort(manuscriptList, new Comparator<ManuscriptListVO>() {
					  public int compare(ManuscriptListVO o1, ManuscriptListVO o2) {
					      if (o1.getUpdatedOn() == null || o2.getUpdatedOn() == null)
					        return 0;
					      return o2.getUpdatedOn().compareTo(o1.getUpdatedOn());
					  }
					});			}
			}			
			obj.put("totalAwatingPaymentManuscript", totalAwatingPaymentManuscript);
			obj.put("data", manuscriptList);
		}catch(Exception e) {
			obj.put("data", "Something Went Wrong");
			log.info("Exception :" +e);
			log.error("Exception : " + e.getMessage());
		}
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}
	
	
}