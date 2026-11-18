package com.digi.uniprr.serviceImpl;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.digi.uniprr.model.EditorAccess;
import com.digi.uniprr.repository.EditorAccessRepo;
import com.digi.uniprr.service.EditorAccessService;

@Service
@Transactional
public class EditorAccessServiceImpl implements EditorAccessService {

	@Autowired
	EditorAccessRepo editorAccessRepo;
	
	@Override
	public List<EditorAccess> getActiveData() {
		List<EditorAccess> data = editorAccessRepo.getActiveData();
		return data;
	}

}
