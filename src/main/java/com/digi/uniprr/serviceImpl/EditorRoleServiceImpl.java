package com.digi.uniprr.serviceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Tuple;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.digi.uniprr.VO.AdminJournalVo;
import com.digi.uniprr.VO.EditorRoleVo;
import com.digi.uniprr.model.EditorRole;
import com.digi.uniprr.repository.EditorRoleServiceRepo;
import com.digi.uniprr.service.EditorRoleService;

@Transactional
@Service
public class EditorRoleServiceImpl implements EditorRoleService {

	@Autowired
	EditorRoleServiceRepo editorRoleServiceRepo;
	
	@Override
	public List<EditorRoleVo> getEditorRoleList() {
		
		List<EditorRoleVo> listEditorRole = new ArrayList<>();
		
		List<Tuple> editorRole = editorRoleServiceRepo.getActiveData();
		
		if (editorRole != null) {
			listEditorRole = editorRole.stream().map(t -> new EditorRoleVo(t.get(0, Integer.class), t.get(1, String.class), t.get(2, Integer.class), t.get(3, Integer.class)))
				.collect(Collectors.toList());
			}
		
		return listEditorRole;
	}

}
