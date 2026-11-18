package com.digi.uniprr.serviceImpl;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.digi.uniprr.VO.ReviewerSuggestedVO;
import com.digi.uniprr.VO.ReviewerSuggestionVO;
import com.digi.uniprr.enums.Salutation;
import com.digi.uniprr.model.AuthoritiesHistory;
import com.digi.uniprr.model.Countries;
import com.digi.uniprr.model.ManuScriptReviewer;
import com.digi.uniprr.model.ReviewerOpposed;
import com.digi.uniprr.model.ReviewerSuggested;
import com.digi.uniprr.model.User;
import com.digi.uniprr.model.UserJournal;
import com.digi.uniprr.repository.AuthoritiesHistoryRepo;
import com.digi.uniprr.repository.ManuScriptReviewerRepo;
import com.digi.uniprr.repository.ReviewerOpposedRepo;
import com.digi.uniprr.repository.ReviewerSuggestedRepo;
import com.digi.uniprr.repository.UserJournalRepo;
import com.digi.uniprr.repository.UserLoginRepo;
import com.digi.uniprr.repository.UserRepo;
import com.digi.uniprr.service.CountriesService;
import com.digi.uniprr.service.ReviewerSuggestedService;

@Service
@Transactional
public class ReviewerSuggestedServiceImpl implements ReviewerSuggestedService {

	@Autowired
	UserRepo userRepo;

	@Autowired
	UserLoginRepo userLoginRepo;

	@Autowired
	ReviewerSuggestedRepo reviewerSuggestedRepo;

	@Autowired
	ManuScriptReviewerRepo manuScriptReviewerRepo;

	@Autowired
	CountriesService countriesService;

	@Autowired
	AuthoritiesHistoryRepo authoritiesHistoryRepo;

	@Autowired
	ReviewerOpposedRepo reviewerOpposedRepo;

	@Autowired
	UserJournalRepo userJournalRepo;

	@Override
	public User getByEmail(String email) {
		return reviewerSuggestedRepo.findByEmail(email);
	}

	@Override
	public String createUser(ReviewerSuggestionVO reviewerSuggestionVO) {

		if (reviewerSuggestionVO.getBoxesofboxes() == "") {
			List<ManuScriptReviewer> data = manuScriptReviewerRepo.getmanuScriptReviewerById(
					reviewerSuggestionVO.getManuscriptId(), reviewerSuggestionVO.getReviewerId(),
					reviewerSuggestionVO.getManuscriptVersion());
			if (data != null && !data.isEmpty()) {
				for (ManuScriptReviewer manuScriptReviewerData1 : data) {
					if (manuScriptReviewerData1.getManuscriptStatus().equals(8)
							|| manuScriptReviewerData1.getManuscriptStatus().equals(66)) {
						ManuScriptReviewer manuScriptReviewerData = manuScriptReviewerRepo
								.getOne(manuScriptReviewerData1.getId());
						if (reviewerSuggestionVO.getManuscriptStatus() != null) {
							manuScriptReviewerData.setManuscriptStatus(reviewerSuggestionVO.getManuscriptStatus());
						}
						manuScriptReviewerData.setRejectDate(new Date());
						if (reviewerSuggestionVO.getRejectionReason() != null) {
							manuScriptReviewerData.setRejectionReason(reviewerSuggestionVO.getRejectionReason());
						}

						manuScriptReviewerRepo.save(manuScriptReviewerData);
					}
				}
			} else {
				ManuScriptReviewer manuScriptReviewerData = new ManuScriptReviewer();
				manuScriptReviewerData.setManuscriptId(reviewerSuggestionVO.getManuscriptId());
				manuScriptReviewerData.setManuscriptVersion(reviewerSuggestionVO.getManuscriptVersion());
				manuScriptReviewerData.setReviewerId(reviewerSuggestionVO.getReviewerId());
				manuScriptReviewerData.setManuscriptStatus(reviewerSuggestionVO.getManuscriptStatus());
				manuScriptReviewerData.setJournalId(reviewerSuggestionVO.getJournalId());
				manuScriptReviewerData.setEditorId(reviewerSuggestionVO.getEditorId());
				manuScriptReviewerData.setIsAssigned(reviewerSuggestionVO.getIsAssigned());
				manuScriptReviewerData.setPublonCredit(reviewerSuggestionVO.getPublonCredit());
				manuScriptReviewerData.setAssignedDate(reviewerSuggestionVO.getAssignedDate());
				manuScriptReviewerData.setAcceptDate(reviewerSuggestionVO.getAcceptDate());
				manuScriptReviewerData.setRejectDate(new Date());
				manuScriptReviewerData.setRejectionReason(reviewerSuggestionVO.getRejectionReason());
				manuScriptReviewerData.setIsActive(reviewerSuggestionVO.getIsActive());
				manuScriptReviewerRepo.save(manuScriptReviewerData);

			}

		} else {
			for (int i = 0; i <= reviewerSuggestionVO.getReviewerSuggested().size() - 1; i++) {

				ReviewerSuggested saved1 = new ReviewerSuggested();

				User user1 = new User();
				user1.setUsername(reviewerSuggestionVO.getReviewerSuggested().get(i).getEmail());
				user1.setEmail(reviewerSuggestionVO.getReviewerSuggested().get(i).getEmail());
				if (reviewerSuggestionVO.getReviewerSuggested().get(i).getSalutation() != null
						&& !reviewerSuggestionVO.getReviewerSuggested().get(i).getSalutation().isEmpty()) {
					user1.setSalutation(Enum.valueOf(Salutation.class,
							reviewerSuggestionVO.getReviewerSuggested().get(i).getSalutation()));
				} else {

				}
				user1.setFirstName(reviewerSuggestionVO.getReviewerSuggested().get(i).getFirstname());
				user1.setLastName(reviewerSuggestionVO.getReviewerSuggested().get(i).getLastname());
				user1.setAccountLocked(1);
				User savedUser = userLoginRepo.save(user1);

				saved1.setSuggestedBy(reviewerSuggestionVO.getReviewerSuggested().get(i).getSuggestedBy());
				saved1.setSuggestedReviewerId(savedUser.getId());
				saved1.setApprovedSuggestedId(
						reviewerSuggestionVO.getReviewerSuggested().get(i).getApprovedSuggestedId());
				saved1.setSuggestedByComments(
						reviewerSuggestionVO.getReviewerSuggested().get(i).getSuggestedByComments());
				saved1.setApprovedByComments(
						reviewerSuggestionVO.getReviewerSuggested().get(i).getApprovedByComments());
				saved1.setApproved(reviewerSuggestionVO.getReviewerSuggested().get(i).getApproved());
				reviewerSuggestedRepo.save(saved1);

			}

			List<ManuScriptReviewer> data = manuScriptReviewerRepo.getmanuScriptReviewerById(
					reviewerSuggestionVO.getManuscriptId(), reviewerSuggestionVO.getReviewerId(),
					reviewerSuggestionVO.getManuscriptVersion());
			if (data != null && !data.isEmpty()) {
				ManuScriptReviewer manuScriptReviewerData1 = data.get(0);
				ManuScriptReviewer manuScriptReviewerData = manuScriptReviewerRepo
						.getOne(manuScriptReviewerData1.getId());

				if (reviewerSuggestionVO.getManuscriptStatus() != null) {
					manuScriptReviewerData.setManuscriptStatus(reviewerSuggestionVO.getManuscriptStatus());
				}
				manuScriptReviewerData.setRejectDate(new Date());
				if (reviewerSuggestionVO.getRejectionReason() != null) {
					manuScriptReviewerData.setRejectionReason(reviewerSuggestionVO.getRejectionReason());

				}

				manuScriptReviewerRepo.save(manuScriptReviewerData);
			} else {
				ManuScriptReviewer manuScriptReviewerData = new ManuScriptReviewer();
				manuScriptReviewerData.setManuscriptId(reviewerSuggestionVO.getManuscriptId());
				manuScriptReviewerData.setManuscriptVersion(reviewerSuggestionVO.getManuscriptVersion());
				manuScriptReviewerData.setReviewerId(reviewerSuggestionVO.getReviewerId());
				manuScriptReviewerData.setManuscriptStatus(reviewerSuggestionVO.getManuscriptStatus());
				manuScriptReviewerData.setJournalId(reviewerSuggestionVO.getJournalId());
				manuScriptReviewerData.setEditorId(reviewerSuggestionVO.getEditorId());
				manuScriptReviewerData.setIsAssigned(reviewerSuggestionVO.getIsAssigned());
				manuScriptReviewerData.setPublonCredit(reviewerSuggestionVO.getPublonCredit());
				manuScriptReviewerData.setAssignedDate(reviewerSuggestionVO.getAssignedDate());
				manuScriptReviewerData.setAcceptDate(reviewerSuggestionVO.getAcceptDate());
				manuScriptReviewerData.setRejectDate(new Date());
				manuScriptReviewerData.setRejectionReason(reviewerSuggestionVO.getRejectionReason());
				manuScriptReviewerData.setIsActive(reviewerSuggestionVO.getIsActive());
				manuScriptReviewerRepo.save(manuScriptReviewerData);

			}
		}
		return "Save Decline data";
	}

	@Override
	public String createReviewerSuggestedArray(ReviewerSuggestedVO reviewerSuggestedVO) {
		reviewerSuggestedVO.getReviewerSuggested().stream().forEach(i -> {
			if (i.getEmail() != null && !i.getEmail().isEmpty()) {
				ReviewerSuggested j = reviewerSuggestedRepo.findByEmail(i.getEmail(), reviewerSuggestedVO.getManuscriptId(),
						reviewerSuggestedVO.getManuscriptVersion(), reviewerSuggestedVO.getJournalId());
				if (j != null) {
					j.setManuscriptId(reviewerSuggestedVO.getManuscriptId());
					j.setManuscriptVersion(reviewerSuggestedVO.getManuscriptVersion());
					j.setJournalId(reviewerSuggestedVO.getJournalId());
					j.setSalutation(i.getSalutation());
					j.setEmail(i.getEmail());
					j.setFirstName(i.getFirstname());
					j.setLastName(i.getLastname());
					j.setSuggestedBy(i.getSuggestedBy());
					j.setIsSuggested(i.getIsSuggested());
					j.setSpecialization(i.getSpecialization());
					if (i.getCountry() != null) {
						Countries countries = countriesService
								.getCountryByName(i.getCountry() != null ? i.getCountry().trim(): null);
						if (countries != null) {
							j.setCountry(countries.getId());
						}
					}
					j.setSubject(i.getSubject());
					j.setManuscriptStatus(i.getManuscriptStatus());
					j.setSuggestedOn(new Date());
					j.setCountry_desc(i.getCountry());
					j.setInstitution(i.getInstitution());
					reviewerSuggestedRepo.save(j);
				} else {
					ReviewerSuggested addReviewerSuggestedData = new ReviewerSuggested();
					addReviewerSuggestedData.setManuscriptId(reviewerSuggestedVO.getManuscriptId());
					addReviewerSuggestedData.setManuscriptVersion(reviewerSuggestedVO.getManuscriptVersion());
					addReviewerSuggestedData.setJournalId(reviewerSuggestedVO.getJournalId());
					addReviewerSuggestedData.setSalutation(i.getSalutation());
					addReviewerSuggestedData.setEmail(i.getEmail());
					addReviewerSuggestedData.setFirstName(i.getFirstname());
					addReviewerSuggestedData.setLastName(i.getLastname());
					addReviewerSuggestedData.setSuggestedBy(i.getSuggestedBy());
					addReviewerSuggestedData.setIsSuggested(i.getIsSuggested());
					if (i.getCountry() != null) {
						Countries countries = countriesService
								.getCountryByName(i.getCountry() != null ? i.getCountry().trim(): null);
						if (countries != null) {
							addReviewerSuggestedData.setCountry(countries.getId());
						}
					}
					addReviewerSuggestedData.setSpecialization(i.getSpecialization());
					addReviewerSuggestedData.setSubject(i.getSubject());
					addReviewerSuggestedData.setManuscriptStatus(i.getManuscriptStatus());
					addReviewerSuggestedData.setSuggestedOn(new Date());
					addReviewerSuggestedData.setCountry_desc(i.getCountry());
					addReviewerSuggestedData.setInstitution(i.getInstitution());
					reviewerSuggestedRepo.save(addReviewerSuggestedData);

					createSuggestedUser(addReviewerSuggestedData, i.getSuggestedBy());
				}
			}
		});
		return "Save ReviewerSuggested data";
	}

	@Autowired
	private PasswordEncoder passwordEncoder;
	@Value("${tempPassword}")
	String tempPassword;
	private void createSuggestedUser (ReviewerSuggested addReviewerSuggestedData, int suggestedBy){
		User isExist = userLoginRepo.findByEmailUser(addReviewerSuggestedData.getEmail());
		if (isExist != null){
			isExist.setIsActive(1); // Activate the user
			userRepo.save(isExist);// Save the updated user object to the database
		} else {
			User user1 = new User();
			user1.setUsername(addReviewerSuggestedData.getEmail());
			user1.setEmail(addReviewerSuggestedData.getEmail());
			user1.setSalutation(Enum.valueOf(Salutation.class, addReviewerSuggestedData.getSalutation()));
			user1.setFirstName(addReviewerSuggestedData.getFirstName());
			user1.setLastName(addReviewerSuggestedData.getLastName());
			user1.setAccountLocked(1);

			user1.setSpecialization(addReviewerSuggestedData.getSpecialization());
			user1.setExpertise(addReviewerSuggestedData.getSubject());
			user1.setCountryId(addReviewerSuggestedData.getCountry());
			String token = UUID.randomUUID().toString();
			user1.setPassword(passwordEncoder.encode(tempPassword.trim()));
			user1.setToken(token);
			user1.setIsActive(1);
			User savedUser = userLoginRepo.save(user1);

			UserJournal userJournalData = new UserJournal();
			userJournalData.setJournalId(addReviewerSuggestedData.getJournalId());
			userJournalData.setUserId(savedUser.getId());
			userJournalData.setRoleId(5);
			userJournalData.setUpdatedBy(suggestedBy);
			userJournalData.setIsActive(1);
			userJournalRepo.save(userJournalData);
		}
	}

	@Override
	public ReviewerSuggested getByEmailId(String email) {
		return reviewerSuggestedRepo.findByEmailId(email);
	}

	@Override
	public boolean isDeclineExist(ReviewerSuggestionVO reviewerSuggestionVO) {
		List<ManuScriptReviewer> getManuScriptReviewerData = manuScriptReviewerRepo.isAssignExist(
				reviewerSuggestionVO.getManuscriptId(), reviewerSuggestionVO.getManuscriptVersion(),
				reviewerSuggestionVO.getReviewerId(), reviewerSuggestionVO.getManuscriptStatus(),
				reviewerSuggestionVO.getJournalId());

		if (getManuScriptReviewerData != null && !getManuScriptReviewerData.isEmpty()) {
			return true;
		}
		return false;
	}
	@Override
	public boolean isInviteExist(ReviewerSuggestionVO reviewerSuggestionVO){
		List<ManuScriptReviewer> getManuScriptReviewerData = manuScriptReviewerRepo.isAssignExist(
				reviewerSuggestionVO.getManuscriptId(), reviewerSuggestionVO.getManuscriptVersion(),
				reviewerSuggestionVO.getReviewerId(), 8,
				reviewerSuggestionVO.getJournalId());

		if (getManuScriptReviewerData != null && !getManuScriptReviewerData.isEmpty()) {
			return true;
		}
		return false;
	}

	@Override
	public boolean isAcceptExist(ReviewerSuggestionVO reviewerSuggestionVO) {
		List<ManuScriptReviewer> getManuScriptReviewerData = manuScriptReviewerRepo.isAssignExist(
				reviewerSuggestionVO.getManuscriptId(), reviewerSuggestionVO.getManuscriptVersion(),
				reviewerSuggestionVO.getReviewerId(), 35, reviewerSuggestionVO.getJournalId());
		if (getManuScriptReviewerData != null && !getManuScriptReviewerData.isEmpty()) {
			return true;
		}
		return false;
	}

	@Override
	public String updateReviewerSuggested(ReviewerSuggestedVO reviewerSuggestedVO) {
		ReviewerSuggested i = reviewerSuggestedRepo.getOne(reviewerSuggestedVO.getId());
		if (i != null) {
			i.setFirstName(reviewerSuggestedVO.getFirstname());
			i.setLastName(reviewerSuggestedVO.getLastname());
			i.setSalutation(reviewerSuggestedVO.getSalutation());
			i.setSpecialization(reviewerSuggestedVO.getSpecialization());
			i.setSubject(reviewerSuggestedVO.getSubject());
			i.setEmail(reviewerSuggestedVO.getEmail());
			reviewerSuggestedRepo.save(i);
			return "Reviewer Suggested updated successfully";
		}
		return "There is no data to update";
	}

	@Override
	public String deleteReviewerSuggested(ReviewerSuggestedVO reviewerSuggestedVO) {
		reviewerSuggestedRepo.deleteById(reviewerSuggestedVO.getId());
		return "Reviewer Suggested deleted successfully";
	}

	@Override
	public Long getReviewerSuggestionCount(Integer manuscriptId, Integer manuscriptVersion, Integer journalId) {
		Long count = reviewerSuggestedRepo.getAuthorReviewerSuggested(manuscriptId, journalId).stream().count();
		return count;
	}

	@Override
	public String createReviewerOpposedArray(ReviewerSuggestedVO reviewerSuggestedVO) {
		reviewerSuggestedVO.getReviewerSuggested().stream().forEach(i -> {
			if (i.getEmail() != null && !i.getEmail().isEmpty()) {
				List<UserJournal> userJournal = userJournalRepo.getReviewerIdByEmail(reviewerSuggestedVO.getJournalId(),
						i.getEmail());
				if (userJournal.size() > 0) {
					reviewerSuggestedVO.setReviewerId(userJournal.get(0).getUserId());
				}
				ReviewerOpposed j = reviewerOpposedRepo.findByEmail(i.getEmail(), reviewerSuggestedVO.getManuscriptId(),
						reviewerSuggestedVO.getManuscriptVersion(), reviewerSuggestedVO.getJournalId());
				if (j != null) {
					j.setManuscriptId(reviewerSuggestedVO.getManuscriptId());
					j.setManuscriptVersion(reviewerSuggestedVO.getManuscriptVersion());
					j.setJournalId(reviewerSuggestedVO.getJournalId());
					j.setSalutation(i.getSalutation());
					j.setEmail(i.getEmail());
					j.setFirstName(i.getFirstname());
					j.setLastName(i.getLastname());
					j.setOpposedBy(i.getSuggestedBy());
					j.setIsOpposed(i.getIsSuggested());
					j.setSpecialization(i.getSpecialization());
					j.setCountry(i.getCountry());
					j.setSubject(i.getSubject());
					j.setInstitution(i.getInstitution());
					j.setManuscriptStatus(i.getManuscriptStatus());
					j.setOpposedDate(new Date());
					j.setComments(i.getComments());
					j.setOpposedReviewerId(reviewerSuggestedVO.getReviewerId());
					reviewerOpposedRepo.save(j);
				} else {
					ReviewerOpposed addReviewerOpposedData = new ReviewerOpposed();
					addReviewerOpposedData.setManuscriptId(reviewerSuggestedVO.getManuscriptId());
					addReviewerOpposedData.setManuscriptVersion(reviewerSuggestedVO.getManuscriptVersion());
					addReviewerOpposedData.setJournalId(reviewerSuggestedVO.getJournalId());
					addReviewerOpposedData.setSalutation(i.getSalutation());
					addReviewerOpposedData.setEmail(i.getEmail());
					addReviewerOpposedData.setFirstName(i.getFirstname());
					addReviewerOpposedData.setLastName(i.getLastname());
					addReviewerOpposedData.setOpposedBy(i.getSuggestedBy());
					addReviewerOpposedData.setIsOpposed(i.getIsSuggested());
					addReviewerOpposedData.setCountry(i.getCountry());
					addReviewerOpposedData.setSpecialization(i.getSpecialization());
					addReviewerOpposedData.setSubject(i.getSubject());
					addReviewerOpposedData.setInstitution(i.getInstitution());
					addReviewerOpposedData.setManuscriptStatus(i.getManuscriptStatus());
					addReviewerOpposedData.setOpposedDate(new Date());
					addReviewerOpposedData.setComments(i.getComments());
					addReviewerOpposedData.setOpposedReviewerId(reviewerSuggestedVO.getReviewerId());
					reviewerOpposedRepo.save(addReviewerOpposedData);
				}
			}
		});
		return "Save ReviewerOpposed data";
	}

	@Override
	public String updateReviewerOpposed(ReviewerSuggestedVO reviewerSuggestedVO) {
		ReviewerOpposed i = reviewerOpposedRepo.getById(reviewerSuggestedVO.getId());
		if (i != null) {
			i.setFirstName(reviewerSuggestedVO.getFirstname());
			i.setLastName(reviewerSuggestedVO.getLastname());
			i.setSalutation(reviewerSuggestedVO.getSalutation());
			i.setSpecialization(reviewerSuggestedVO.getSpecialization());
			i.setSubject(reviewerSuggestedVO.getSubject());
			i.setEmail(reviewerSuggestedVO.getEmail());
			reviewerOpposedRepo.save(i);
			return "Reviewer Opposed updated successfully";
		}
		return "There is no data to update";
	}

	@Override
	public String deleteReviewerOpposed(ReviewerSuggestedVO reviewerSuggestedVO) {
		reviewerOpposedRepo.deleteById(reviewerSuggestedVO.getId());
		return "Reviewer Opposed deleted successfully";
	}
}
