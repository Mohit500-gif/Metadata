package com.digi.uniprr.serviceImpl;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.digi.uniprr.VO.WorkFlowSetUpVO;
import com.digi.uniprr.VO.WorkFlowTaskVO;
import com.digi.uniprr.model.Workflow;
import com.digi.uniprr.model.WorkflowRoleSeq;
import com.digi.uniprr.repository.WorkflowRepo;
import com.digi.uniprr.repository.WorkflowRoleRepo;
import com.digi.uniprr.service.WorkflowService;

@Service
@Transactional
public class WorkflowServiceImpl implements WorkflowService {

	@Autowired
	WorkflowRepo workflowRepo;

	@Autowired
	WorkflowRoleRepo workflowRoleRepo;

	@Override
	public List<Workflow> getWorkflow() {
		return workflowRepo.findAll();
	}

	@Override
	public Integer saveWorkflow(Workflow workflow) {
		workflowRepo.save(workflow);
		workflowRepo.flush();

		return workflow.getWorkflowId();
	}

	@Override
	public void deleteWorkflowById(Integer workflowId) {
		workflowRepo.deleteById(workflowId);

	}

	@Override
	public WorkFlowSetUpVO getWorkFlowById(Integer workflowId) {
		WorkFlowSetUpVO addWorkFlowSetUpVoList = new WorkFlowSetUpVO();
		List<WorkFlowTaskVO> addWorkFlowTaskVOList = new ArrayList<>();
		Workflow workflow = workflowRepo.getOne(workflowId);
		if (workflow != null && workflow.getWorkflowRoleSeq() != null && !workflow.getWorkflowRoleSeq().isEmpty()) {
			for (WorkflowRoleSeq taskData : workflow.getWorkflowRoleSeq()) {
				WorkFlowTaskVO addWorkFlowTaskVO = new WorkFlowTaskVO();
				addWorkFlowTaskVO.setId(taskData.getId());
				addWorkFlowTaskVO.setWorkflowId(taskData.getWorkflowId());
				addWorkFlowTaskVO.setTaskId(taskData.getTaskId());
				addWorkFlowTaskVO.setTat(taskData.getTat());
				addWorkFlowTaskVO.setEmailFlag(taskData.getEmailFlag());
				addWorkFlowTaskVO.setTaskName(taskData.getTaskMaster().getTaskName());
				addWorkFlowTaskVOList.add(addWorkFlowTaskVO);
			}
		}
		addWorkFlowSetUpVoList.setWorkFlowId(workflow.getWorkflowId());
		addWorkFlowSetUpVoList.setWorkFlowName(workflow.getName());
		addWorkFlowSetUpVoList.setWorkFlowTaskVO(addWorkFlowTaskVOList);

		return addWorkFlowSetUpVoList;
	}

	@Override
	public WorkFlowSetUpVO getJournalWorkFlowById(Integer journalId) {
		WorkFlowSetUpVO addWorkFlowSetUpVoList = new WorkFlowSetUpVO();
		List<WorkFlowTaskVO> addWorkFlowTaskVOList = new ArrayList<>();
		List<WorkflowRoleSeq> workflowJournalRole = workflowRoleRepo.getWorkflowByJournalId(journalId);
		if (workflowJournalRole != null && !workflowJournalRole.isEmpty()) {
			for (WorkflowRoleSeq taskData : workflowJournalRole) {
				WorkFlowTaskVO addWorkFlowTaskVO = new WorkFlowTaskVO();
				addWorkFlowTaskVO.setId(taskData.getId());
				addWorkFlowTaskVO.setWorkflowId(taskData.getWorkflowId());
				addWorkFlowTaskVO.setTaskId(taskData.getTaskId());
				addWorkFlowTaskVO.setTat(taskData.getTat());
				addWorkFlowTaskVO.setEmailFlag(taskData.getEmailFlag());
				addWorkFlowTaskVO.setTaskName(taskData.getTaskMaster().getTaskName());
				addWorkFlowTaskVO.setIconText(taskData.getTaskMaster().getIconText());
				addWorkFlowTaskVOList.add(addWorkFlowTaskVO);
			}
			addWorkFlowSetUpVoList.setWorkFlowTaskVO(addWorkFlowTaskVOList);
			addWorkFlowSetUpVoList.setWorkFlowId(workflowJournalRole.get(0).getWorkflow().getWorkflowId());
			addWorkFlowSetUpVoList.setWorkFlowName(workflowJournalRole.get(0).getWorkflow().getName());
		}
		return addWorkFlowSetUpVoList;
	}

	@Override
	public void createJournalWorkflow(Integer journalId, Integer userId) {

		List<WorkflowRoleSeq> workflowJourna = workflowRoleRepo.getWorkflowByJournalIdForCreateWorkflow(journalId);
		if (workflowJourna.isEmpty()) {
			List<WorkflowRoleSeq> workflowJournalRole = workflowRoleRepo.getWorkflowByJournalIdForCreateWorkflow(0);
			for (WorkflowRoleSeq workflowRoleSeq : workflowJournalRole) {

				WorkflowRoleSeq wrkNew = new WorkflowRoleSeq();
				wrkNew.setWorkflowId(workflowRoleSeq.getWorkflowId());
				wrkNew.setTaskId(workflowRoleSeq.getTaskId());
				wrkNew.setTat(workflowRoleSeq.getTat());
				wrkNew.setEmailFlag(workflowRoleSeq.getEmailFlag());
				wrkNew.setCreatedBy(userId);
				wrkNew.setJournalId(journalId);
				wrkNew.setIsActive(workflowRoleSeq.getIsActive());
				workflowRoleRepo.save(wrkNew);
			}

		}
	}
}
