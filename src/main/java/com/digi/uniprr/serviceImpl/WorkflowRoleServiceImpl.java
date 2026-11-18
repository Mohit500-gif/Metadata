package com.digi.uniprr.serviceImpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.digi.uniprr.VO.WorkFlowSetUpVO;
import com.digi.uniprr.VO.WorkFlowTaskVO;
import com.digi.uniprr.model.Workflow;
import com.digi.uniprr.model.WorkflowJournalRoleSeq;
import com.digi.uniprr.model.WorkflowRoleSeq;
import com.digi.uniprr.repository.WorkflowJournalRoleRepo;
import com.digi.uniprr.repository.WorkflowRepo;
import com.digi.uniprr.repository.WorkflowRoleRepo;
import com.digi.uniprr.service.WorkflowRoleService;

@Service
@Transactional
public class WorkflowRoleServiceImpl implements WorkflowRoleService {

	@Autowired
	WorkflowRoleRepo workflowRoleRepo;

	@Autowired
	WorkflowRepo workflowRepo;

	@Override
	public String workFlowUpdateForJournal(WorkFlowSetUpVO WorkFlowSetUpVO) {

		if (WorkFlowSetUpVO != null && !WorkFlowSetUpVO.getWorkFlowTaskVO().isEmpty()) {
			List<WorkflowRoleSeq> addJournalWorkflowRole = workflowRoleRepo
					.getWorkflowByJournalId(WorkFlowSetUpVO.getJournalId());
			WorkFlowSetUpVO.getWorkFlowTaskVO().stream().forEach(i -> {
				if (addJournalWorkflowRole != null && !addJournalWorkflowRole.isEmpty()) {
					for (WorkflowRoleSeq workflowset : addJournalWorkflowRole) {
						if (i.getTaskId().equals(workflowset.getTaskId())) {
							workflowset.setTat(i.getTat());
							workflowset.setEmailFlag(i.getEmailFlag());
							workflowset.setUpdatedBy(i.getCreatedBy());
							workflowset.setUpdatedAt(new Date());
							workflowRoleRepo.save(workflowset);
						}
					}
				}
			});
		}
		return WorkFlowSetUpVO.getWorkFlowId() + ",WorkFlow Updated Successfully";

	}

	@Override
	public List<WorkFlowSetUpVO> getMasterWorkFlowList() {
		List<Workflow> getAllList = workflowRepo.getTaskByWorkflowDetails();
		List<WorkFlowSetUpVO> addWorkFlowSetUpVoList = new ArrayList<>();
		if (getAllList != null && !getAllList.isEmpty()) {
			getAllList.stream().forEach(i -> {
				List<WorkFlowTaskVO> addWorkFlowTaskVOList = new ArrayList<>();
				WorkFlowSetUpVO addWorkFlowSetUpVO = new WorkFlowSetUpVO();
				addWorkFlowSetUpVO.setWorkFlowId(i.getWorkflowId());
				addWorkFlowSetUpVO.setWorkFlowName(i.getName());
				i.getWorkflowRoleSeq().stream()
						.sorted((WorkflowRoleSeq s1, WorkflowRoleSeq s2) -> s1.getTaskId() - s2.getTaskId())
						.forEach(j -> {
							WorkFlowTaskVO addWorkFlowTaskVO = new WorkFlowTaskVO();
							addWorkFlowTaskVO.setId(j.getId());
							addWorkFlowTaskVO.setWorkflowId(j.getWorkflowId());
							addWorkFlowTaskVO.setTaskId(j.getTaskId());
							addWorkFlowTaskVO.setTat(j.getTat());
							addWorkFlowTaskVO.setEmailFlag(j.getEmailFlag());
							addWorkFlowTaskVO.setTaskName(j.getTaskMaster().getTatDisplayname());
							addWorkFlowTaskVOList.add(addWorkFlowTaskVO);
						});
				addWorkFlowSetUpVO.setWorkFlowTaskVO(addWorkFlowTaskVOList);
				addWorkFlowSetUpVoList.add(addWorkFlowSetUpVO);
			});
		}
		return addWorkFlowSetUpVoList;
	}

	@Override
	public String deleteMasterWorkFlow(WorkFlowSetUpVO WorkFlowSetUpVO) {
		/*
		 * workflowRoleRepo.deleteWorkflowRoleSeqById(WorkFlowSetUpVO.getWorkFlowId());
		 * workflowRepo.deleteWorkflowById(WorkFlowSetUpVO.getWorkFlowId());
		 */
		return "WorkFlow D Successfully";
	}

	@Override
	public boolean isWorkFlowExist(WorkFlowSetUpVO workFlowSetUpVO) {
		Workflow getWorkFlowByName = workflowRepo.getWorkFlowByWorkFlowName(workFlowSetUpVO.getWorkFlowName());
		if (getWorkFlowByName != null) {
			return true;
		}
		return false;
	}

}
