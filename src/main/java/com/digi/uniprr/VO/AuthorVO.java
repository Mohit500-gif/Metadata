package com.digi.uniprr.VO;

import java.io.Serializable;

import com.digi.uniprr.enums.Salutation;

public class AuthorVO implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Salutation salutatio;

	private String email;
	
	private String department;

	private String orcidId;
	
	private String address;
	
	private String address_2;
	
	private String address_3;
	
	private String fname;
	
	private String lname;
	
	private String mname;
	
	private String phonenumber;
	
	private String city;
	
	private String country;
	
	private String state;
	
	private String zip;
 	
	private String mobilenumber;
	
	private String faxno;
	
	private String institution;
	
    private String corresponding_address_1;
	
	private String corresponding_address_2;
	
	private String corresponding_address_3;
	
    private String corresponding_city;
	
	private String corresponding_country;
	
	private String corresponding_state;
	
	private String is_corresponding;
	
	private String corresponding_zip;
	
	private String coAuthorOrder;
	
	private String copyRightForm;
	
	private String copyRightContent;
	
	private String researcherId;
	
	public String getResearcherId() {
		return researcherId;
	}

	public void setResearcherId(String researcherId) {
		this.researcherId = researcherId;
	}

	public String getCopyRightForm() {
		return copyRightForm;
	}

	public void setCopyRightForm(String copyRightForm) {
		this.copyRightForm = copyRightForm;
	}

	public String getCopyRightContent() {
		return copyRightContent;
	}

	public void setCopyRightContent(String copyRightContent) {
		this.copyRightContent = copyRightContent;
	}

	public String getCoAuthorOrder() {
		return coAuthorOrder;
	}

	public void setCoAuthorOrder(String coAuthorOrder) {
		this.coAuthorOrder = coAuthorOrder;
	}

	public Salutation getSalutatio() {
		return salutatio;
	}

	public void setSalutatio(Salutation salutatio) {
		this.salutatio = salutatio;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getAddress_2() {
		return address_2;
	}

	public void setAddress_2(String address_2) {
		this.address_2 = address_2;
	}

	public String getAddress_3() {
		return address_3;
	}

	public void setAddress_3(String address_3) {
		this.address_3 = address_3;
	}

	public String getFname() {
		return fname;
	}

	public void setFname(String fname) {
		this.fname = fname;
	}

	public String getLname() {
		return lname;
	}

	public void setLname(String lname) {
		this.lname = lname;
	}

	public String getPhonenumber() {
		return phonenumber;
	}

	public void setPhonenumber(String phonenumber) {
		this.phonenumber = phonenumber;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getMobilenumber() {
		return mobilenumber;
	}

	public void setMobilenumber(String mobilenumber) {
		this.mobilenumber = mobilenumber;
	}

	public String getFaxno() {
		return faxno;
	}

	public void setFaxno(String faxno) {
		this.faxno = faxno;
	}

	public String getInstitution() {
		return institution;
	}

	public void setInstitution(String institution) {
		this.institution = institution;
	}

	public String getOrcidId() {
		return orcidId;
	}

	public void setOrcidId(String orcidId) {
		this.orcidId = orcidId;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getMname() {
		return mname;
	}

	public void setMname(String mname) {
		this.mname = mname;
	}

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	public String getCorresponding_address_1() {
		return corresponding_address_1;
	}

	public void setCorresponding_address_1(String corresponding_address_1) {
		this.corresponding_address_1 = corresponding_address_1;
	}

	public String getCorresponding_address_2() {
		return corresponding_address_2;
	}

	public void setCorresponding_address_2(String corresponding_address_2) {
		this.corresponding_address_2 = corresponding_address_2;
	}

	public String getCorresponding_address_3() {
		return corresponding_address_3;
	}

	public void setCorresponding_address_3(String corresponding_address_3) {
		this.corresponding_address_3 = corresponding_address_3;
	}

	public String getCorresponding_city() {
		return corresponding_city;
	}

	public void setCorresponding_city(String corresponding_city) {
		this.corresponding_city = corresponding_city;
	}

	public String getCorresponding_country() {
		return corresponding_country;
	}

	public void setCorresponding_country(String corresponding_country) {
		this.corresponding_country = corresponding_country;
	}

	public String getCorresponding_state() {
		return corresponding_state;
	}

	public void setCorresponding_state(String corresponding_state) {
		this.corresponding_state = corresponding_state;
	}

	public String getCorresponding_zip() {
		return corresponding_zip;
	}

	public void setCorresponding_zip(String corresponding_zip) {
		this.corresponding_zip = corresponding_zip;
	}

	public String getIs_corresponding() {
		return is_corresponding;
	}

	public void setIs_corresponding(String is_corresponding) {
		this.is_corresponding = is_corresponding;
	}
	
	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	@Override
	public String toString() {
		return "AuthorVO [salutatio=" + salutatio + ", email=" + email + ", department=" + department + ", orcidId="
				+ orcidId + ", address=" + address + ", address_2=" + address_2 + ", address_3=" + address_3
				+ ", fname=" + fname + ", lname=" + lname + ", mname=" + mname + ", phonenumber=" + phonenumber
				+ ", city=" + city + ", country=" + country + ", state=" + state + ", zip=" + zip + ", mobilenumber="
				+ mobilenumber + ", faxno=" + faxno + ", institution=" + institution + ", corresponding_address_1="
				+ corresponding_address_1 + ", corresponding_address_2=" + corresponding_address_2
				+ ", corresponding_address_3=" + corresponding_address_3 + ", corresponding_city=" + corresponding_city
				+ ", corresponding_country=" + corresponding_country + ", corresponding_state=" + corresponding_state
				+ ", is_corresponding=" + is_corresponding + ", corresponding_zip=" + corresponding_zip
				+ ", coAuthorOrder=" + coAuthorOrder + ", copyRightForm=" + copyRightForm + ", copyRightContent="
				+ copyRightContent + ", researcherId=" + researcherId + "]";
	}

}
