package com.digi.uniprr.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.digi.uniprr.model.JournalDataIssn;
import com.digi.uniprr.repository.JournalDataIssnRepo;
import com.digi.uniprr.service.JournalDataIssnService;



@Service
public class JournalDataIssnServiceImpl implements JournalDataIssnService {
	

	@Autowired
	JournalDataIssnRepo dataIssnRepo;

	@Override
	public String fetchHtmlFromUrl(String url) {
		JournalDataIssn dataissn = dataIssnRepo.getById(url);
		return dataissn.getInfo();
	}
	
	

}
