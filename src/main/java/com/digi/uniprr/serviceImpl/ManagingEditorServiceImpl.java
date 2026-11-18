package com.digi.uniprr.serviceImpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.digi.uniprr.VO.JournalIssuesPlannerVO;
import com.digi.uniprr.VO.ReviewerVO;
import com.digi.uniprr.model.JournalIssuesPlanner;
import com.digi.uniprr.model.MeAvailability;
import com.digi.uniprr.model.ReviewerAvailability;
import com.digi.uniprr.model.Role;
import com.digi.uniprr.model.User;
import com.digi.uniprr.repository.JournalIssuesPlannerRepo;
import com.digi.uniprr.repository.MeAvailabilityRepo;
import com.digi.uniprr.repository.UserRepo;
import com.digi.uniprr.service.ManagingEditorService;

@Service
@Transactional
public class ManagingEditorServiceImpl implements ManagingEditorService {

	@Autowired
	MeAvailabilityRepo meAvailabilityRepo;

	@Autowired
	UserRepo userRepo;

	@Autowired
	JournalIssuesPlannerRepo journalIssuesPlannerRepo;
	
	

	@Override
	public void saveManagingEditorAvailability(MeAvailability meAvailability) {
		if (meAvailability.getIsActive() == null) {
			meAvailability.setIsActive(1);
		}
		meAvailabilityRepo.save(meAvailability);

	}

	@Override
	public List<ReviewerVO> getManagingEditorWithAvailability() {

		List<ReviewerVO> managingEditorData = new ArrayList<>();

		List<User> user = userRepo.getAllActiveUser();
		if (user != null && !user.isEmpty()) {

			for (User uerDetail : user) {
				ReviewerVO data = new ReviewerVO();
				for (Role roleDetail : uerDetail.getRole()) {
					if (roleDetail.getRoleId().equals(2)) {
						List<MeAvailability> managingEditorUnavaailableList = meAvailabilityRepo
								.getManagingEditorAvailabilityById(uerDetail.getId());
						List<ReviewerAvailability> mEUnavaailable = new ArrayList<>();
						if (managingEditorUnavaailableList != null && !managingEditorUnavaailableList.isEmpty()) {
							for (MeAvailability mEData : managingEditorUnavaailableList) {
								ReviewerAvailability mEUnavailableData = new ReviewerAvailability();
								mEUnavailableData.setUnavilableFrom(mEData.getUnavilableFrom());
								mEUnavailableData.setUnavailableTo(mEData.getUnavailableTo());
								mEUnavaailable.add(mEUnavailableData);
							}
						}
						data.setFirstname(uerDetail.getFirstName());
						data.setLastname(uerDetail.getLastName());
						data.setEmail(uerDetail.getEmail());
						if (mEUnavaailable != null) {
							data.setReviewerAvailability(mEUnavaailable);
						}
						managingEditorData.add(data);
					}
				}
			}
		}
		return managingEditorData;

	}

	@Override
	public List<MeAvailability> getManagingEditorAvailabilityById(Integer id) {
		return meAvailabilityRepo.getManagingEditorAvailabilityById(id);
	}

	@Override
	public void deleteManagingEditorAvailabilityById(Integer id) {
		meAvailabilityRepo.deleteManagingEditorAvailabilityById(id);

	}

	@Override
	public String createIssue(JournalIssuesPlannerVO journalIssuesPlannerVO) {
		if (journalIssuesPlannerVO.getId() != null) {
			JournalIssuesPlanner journalIssueUpdate = journalIssuesPlannerRepo.getOne(journalIssuesPlannerVO.getId());
			journalIssueUpdate.setIssueName(journalIssuesPlannerVO.getIssueName());
			journalIssueUpdate.setIssueVolume(journalIssuesPlannerVO.getIssueVolume());
			journalIssueUpdate.setIssueYear(journalIssuesPlannerVO.getIssueYear());
			journalIssueUpdate.setUpdatedBy(journalIssuesPlannerVO.getUpdatedBy());
			journalIssueUpdate.setIssueMonth(journalIssuesPlannerVO.getIssueMonth());
			journalIssueUpdate.setUpdatedDate(new Date());
			journalIssuesPlannerRepo.save(journalIssueUpdate);
			return "Journal Issue has been Updated Successfully";
		} else {
			JournalIssuesPlanner journalIssueCreate = new JournalIssuesPlanner();
			journalIssueCreate.setIssueName(journalIssuesPlannerVO.getIssueName());
			journalIssueCreate.setIssueVolume(journalIssuesPlannerVO.getIssueVolume());
			journalIssueCreate.setIssueYear(journalIssuesPlannerVO.getIssueYear());
			journalIssueCreate.setJournalId(journalIssuesPlannerVO.getJournalId());
			journalIssueCreate.setCreatedBy(journalIssuesPlannerVO.getCreatedBy());
			journalIssueCreate.setIssueMonth(journalIssuesPlannerVO.getIssueMonth());
			journalIssueCreate.setIsActive(1);
			journalIssuesPlannerRepo.save(journalIssueCreate);
			return "Journal Issue has been Created Successfully";
		}
	}

	@Override
	public List<JournalIssuesPlannerVO> getIssueList(JournalIssuesPlannerVO journalIssuesPlannerVO) {
		List<JournalIssuesPlanner> journalIssuesPlannerList = journalIssuesPlannerRepo
				.getIssueList(journalIssuesPlannerVO.getJournalId());
		List<JournalIssuesPlannerVO> jpurnalIssuesList = new ArrayList<>();
		if (journalIssuesPlannerList != null && !journalIssuesPlannerList.isEmpty()) {
			journalIssuesPlannerList.stream().forEach(i -> {
				JournalIssuesPlannerVO journalIssuesPlannerSetupList = new JournalIssuesPlannerVO();
				journalIssuesPlannerSetupList.setId(i.getId());
				journalIssuesPlannerSetupList.setIssueMonth(i.getIssueMonth());
				journalIssuesPlannerSetupList.setIssueName(i.getIssueName());
				journalIssuesPlannerSetupList.setIssueVolume(i.getIssueVolume());
				journalIssuesPlannerSetupList.setIssueYear(i.getIssueYear());
				journalIssuesPlannerSetupList.setJournalId(i.getJournalId());
				journalIssuesPlannerSetupList.setCreatedBy(i.getCreatedBy());
				journalIssuesPlannerSetupList.setUpdatedBy(i.getUpdatedBy());
				jpurnalIssuesList.add(journalIssuesPlannerSetupList);
			});
		}
		return jpurnalIssuesList;
	}

	@Override
	public String deleteIssue(JournalIssuesPlannerVO journalIssuesPlannerVO) {
		journalIssuesPlannerRepo.deleteIssue(journalIssuesPlannerVO.getId());
		return "Journal Issue has been Deleted Successfully";
	}

}
