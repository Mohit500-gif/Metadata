package com.digi.uniprr.utils;

import com.digi.uniprr.VO.AuthorVO;
import com.digi.uniprr.model.User;

public class AuthorTransformatonUtils {

	public static AuthorVO mapUserToAuthorVo(User user) {
		AuthorVO authorVO = new AuthorVO();
		
		authorVO.setSalutatio(user.getSalutation());
		authorVO.setFname(user.getFirstName());
		authorVO.setMname(user.getMiddleName());
		authorVO.setLname(user.getLastName());
		authorVO.setEmail(user.getEmail());
		authorVO.setDepartment(user.getAcademic());
		authorVO.setInstitution(user.getInstitute());
		authorVO.setPhonenumber(user.getPhoneNumber());
		authorVO.setAddress(user.getAddress());
		authorVO.setAddress_2(user.getAddressTwo());
		authorVO.setAddress_3(user.getAddressThree());
		authorVO.setCountry("");
		authorVO.setZip("");
		authorVO.setCity(user.getCity());
		authorVO.setMobilenumber(user.getMobileNumber());
		authorVO.setFaxno(user.getFaxNumber());
		authorVO.setAddress(user.getAddress());
		authorVO.setOrcidId(user.getOrcidId());
		authorVO.setState(user.getState());
		authorVO.setCorresponding_address_1("");
		authorVO.setCorresponding_address_2("");
		authorVO.setCorresponding_address_3("");
		authorVO.setCorresponding_city("");
		authorVO.setCorresponding_state("");
		authorVO.setCorresponding_zip("");
		authorVO.setCorresponding_country("");
		authorVO.setResearcherId(user.getResearcherId());
		
		return authorVO;
	}

	

}
