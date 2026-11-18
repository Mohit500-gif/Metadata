package com.digi.uniprr.serviceImpl;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import com.digi.uniprr.model.Countries;
import com.digi.uniprr.model.ManuScript;
import com.digi.uniprr.repository.CountriesRepo;
import com.digi.uniprr.repository.ManuScriptRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.digi.uniprr.VO.IDVO;
import com.digi.uniprr.model.IpTable;
import com.digi.uniprr.model.JournalChargeDetails;
import com.digi.uniprr.repository.IpTableRepo;
import com.digi.uniprr.repository.JournalChargeDetailsRepo;
import com.digi.uniprr.service.JournalChargeDetailsService;

@Service
@Transactional
public class JournalChargeDetailsServiceImpl implements JournalChargeDetailsService {

	@Autowired
	JournalChargeDetailsRepo journalChargeDetailsRepo;

	@Autowired
	ManuScriptRepo manuScriptRepo;

	@Autowired
	CountriesRepo countriesRepo;
	
	@Autowired
	IpTableRepo ipTableRepo;

	@Override
	public void createJournalChargeDetails(JournalChargeDetails journalChargeDetails) {
		journalChargeDetailsRepo.save(journalChargeDetails);
	}

	@Override
	public List<JournalChargeDetails> getJournalChargeDetailsList() {
		return journalChargeDetailsRepo.findAll();
	}

	@Override
	public JournalChargeDetails getJournalChargeDetailsById(JournalChargeDetails journalChargeDetails) {
		JournalChargeDetails journalChargeDetail = journalChargeDetailsRepo.getJournalChargeDetailsByJournal(journalChargeDetails.getJournalId());
		return journalChargeDetail;
	}

	@Override
	public void updateJournalChargeDetails(JournalChargeDetails journalChargeDetails) {
		journalChargeDetailsRepo.save(journalChargeDetails);
	}

	@Override
	public boolean isExist(Integer journalChargeId) {
		return journalChargeDetailsRepo.existsById(journalChargeId);
	}

	@Override
	public List<IDVO> getCountryDevlopedOrNotFlag(IDVO idvo) {
		List<IDVO> add=new ArrayList<IDVO>();
		if(idvo.getIpAddress()!=null && !idvo.getIpAddress().isEmpty()) {
			String[] ipAddressInArray = idvo.getIpAddress().split(".");
			long result = 0;
			for (int i = 0; i < ipAddressInArray.length; i++) {

				int power = 3 - i;
				int ip = Integer.parseInt(ipAddressInArray[i]);
				result += ip * Math.pow(256, power);
			}
			List<IpTable> i=ipTableRepo.getChargeFlagOnCountry(result);
			if(i!=null && !i.isEmpty()) {
				i.stream().forEach(k->{
					IDVO h=new IDVO();
					h.setId(k.getId());
					h.setIpEnd(k.getIpEnd());
					h.setIpAddress(idvo.getIpAddress());
					h.setDotEnd(k.getDotEnd());
					h.setDotStart(k.getDotStart());
					h.setCountryShort(k.getCountryShort());
					h.setCountry(k.getCountry());
					h.setCountryType(k.getCountryType());
					add.add(h);
				});
			}
		}

		if(idvo.getCountry()!=null && !idvo.getCountry().isEmpty()) {
			List<IpTable> i=ipTableRepo.getChargeFlagCountry(idvo.getCountry());
			if(i!=null && !i.isEmpty()) {
				i.stream().forEach(k->{
					IDVO h=new IDVO();
					h.setId(k.getId());
					h.setIpEnd(k.getIpEnd());
					h.setIpAddress(idvo.getIpAddress());
					h.setDotEnd(k.getDotEnd());
					h.setDotStart(k.getDotStart());
					h.setCountryShort(k.getCountryShort());
					h.setCountry(k.getCountry());
					h.setCountryType(k.getCountryType());
					add.add(h);
				});
			}
		}
		return add;
	}

//	@Override
//	public boolean getCountryDevlopedOrNotFlag(IDVO idvo) {
//
//		Integer manuscriptId=idvo.getManuscriptId();
//		ManuScript manuscript=manuScriptRepo.getById(manuscriptId);
//		if(manuscript.getUser()!=null
//				&& manuscript.getUser().getCountryId()!=null
//		&& (manuscript.getUser().getCountryId()<=251 && manuscript.getUser().getCountryId()>=1))
//		{
//			Countries countries=countriesRepo.getById(manuscript.getUser().getCountryId());
//			if(countries.getIsDeveloped().equals(""))
//		}
//
//	}


}
