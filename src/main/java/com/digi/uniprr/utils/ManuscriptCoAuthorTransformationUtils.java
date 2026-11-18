package com.digi.uniprr.utils;

import java.util.ArrayList;
import java.util.List;

import com.digi.uniprr.VO.ManuscriptCoAuthorVO;
import com.digi.uniprr.model.ManuScriptCoAuthor;

public class ManuscriptCoAuthorTransformationUtils {
	
	public static ManuscriptCoAuthorVO mapModelToVo(ManuScriptCoAuthor coAuthor) {
		
		ManuscriptCoAuthorVO vo = new ManuscriptCoAuthorVO();
		vo.setId(coAuthor.getId());
		vo.setCoAuthorId(coAuthor.getCoAuthorId());
		vo.setFirstName(coAuthor.getUser().getFirstName());
		vo.setMiddleName(coAuthor.getUser().getMiddleName());
		vo.setLastName(coAuthor.getUser().getLastName());
		vo.setAddress(coAuthor.getUser().getAddress());
		vo.setAddressTwo(coAuthor.getUser().getAddressTwo());
		vo.setAddressThree(coAuthor.getUser().getAddressThree());
		vo.setOrcidId(coAuthor.getUser().getOrcidId());
		// Added acedmic for coauthor
		vo.setAcademic(coAuthor.getUser().getAcademic());
		vo.setResearcherId(coAuthor.getUser().getResearcherId());
		vo.setTrialRegistrationNo(coAuthor.getUser().getTrialRegistrationNo());
		vo.setEmail(coAuthor.getUser().getEmail());
		vo.setInstitution(coAuthor.getUser().getInstitute());
		vo.setSalutation(coAuthor.getUser().getSalutation());
		vo.setOrder(coAuthor.getAuthorOrder());
		vo.setCountryId(coAuthor.getUser().getCountryId());
		vo.setState(coAuthor.getUser().getState());
		vo.setCity(coAuthor.getUser().getCity());
		vo.setScopusId(coAuthor.getUser().getScopusId());
		vo.setExpertise(coAuthor.getUser().getExpertise());
		vo.setSpecialization(coAuthor.getUser().getSpecialization());
		vo.setPubmedId(coAuthor.getUser().getPubmedId());
		vo.setMobileNumber(coAuthor.getUser().getMobileNumber());
		vo.setPhoneNumber(coAuthor.getUser().getPhoneNumber());
		vo.setPostalCode(coAuthor.getUser().getPostalCode());
		vo.setCountryCode(coAuthor.getUser().getCountryCode());
		if(coAuthor.getIsCorrespondingAuthor() != null 
				&& coAuthor.getIsCorrespondingAuthor().equalsIgnoreCase("Y")) {
			vo.setAuthorType("Primary");
		}
		vo.setIsCorrespondingAuthor(coAuthor.getIsCorrespondingAuthor());
		vo.setCoAuthorStatus(coAuthor.getCoAuthorStatus());
		return vo;
	}
	
	public static List<ManuscriptCoAuthorVO> mapModelListToVoList(List<ManuScriptCoAuthor> list) {
		List<ManuscriptCoAuthorVO> voList = new ArrayList<ManuscriptCoAuthorVO>();
		
		for (ManuScriptCoAuthor author : list)
			voList.add(mapModelToVo(author));
		
		return voList;
	}

}
