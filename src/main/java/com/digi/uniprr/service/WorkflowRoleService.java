package com.digi.uniprr.service;

import java.util.List;

import com.digi.uniprr.VO.WorkFlowSetUpVO;
import com.digi.uniprr.model.WorkflowRoleSeq;


public interface WorkflowRoleService {

	public String workFlowUpdateForJournal (WorkFlowSetUpVO WorkFlowSetUpVO);

	public List<WorkFlowSetUpVO> getMasterWorkFlowList();
	
	public String deleteMasterWorkFlow(WorkFlowSetUpVO WorkFlowSetUpVO);

	public boolean isWorkFlowExist(WorkFlowSetUpVO workFlowSetUpVO);
	
}
