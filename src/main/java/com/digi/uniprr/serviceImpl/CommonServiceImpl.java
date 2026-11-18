
package com.digi.uniprr.serviceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.digi.uniprr.model.*;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.digi.uniprr.VO.ManuscriptFilterVO;
import com.digi.uniprr.VO.SearchReviewerVO;
import com.digi.uniprr.VO.TrackManuScriptVO;
import com.digi.uniprr.repository.ManuScriptRepo;
import com.digi.uniprr.repository.UserJournalRepo;
import com.digi.uniprr.repository.UserLoginRepo;
import com.digi.uniprr.service.CommonService;

@Transactional

@Service
public class CommonServiceImpl implements CommonService {

	@PersistenceContext
	EntityManager entityManager;

	@Autowired
	UserLoginRepo userLoginRepo;

	@Autowired
	ManuScriptRepo manuscriptRepo;

	@Autowired
	UserJournalRepo userJournalRepo;

	@Override

	@SuppressWarnings({ "unchecked", "deprecation" })
	public List<TrackManuScriptVO> queryManuscript(ManuscriptFilterVO manuscriptFilterVO) {
		User user = userLoginRepo.getOne(manuscriptFilterVO.getUserId());
		Session session = entityManager.unwrap(Session.class);
		Criteria criteria = session.createCriteria(ManuScript.class);

		if (manuscriptFilterVO.getManuscriptCode() != null
				&& manuscriptFilterVO.getManuscriptCode().trim().length() > 0)
			criteria.add(Restrictions.eq("manuscriptCode", manuscriptFilterVO.getManuscriptCode()));

		if (manuscriptFilterVO.getManuscriptTitle() != null
				&& manuscriptFilterVO.getManuscriptTitle().trim().length() > 0)
			criteria.add(
					Restrictions.like("manuscriptTitle", manuscriptFilterVO.getManuscriptTitle(), MatchMode.ANYWHERE));

		if (manuscriptFilterVO.getSubmissionDateFrom() != null)
			criteria.add(Restrictions.ge("submissionDate", manuscriptFilterVO.getSubmissionDateFrom()));

		if (manuscriptFilterVO.getSubmissionDateTo() != null)
			criteria.add(Restrictions.le("submissionDate", manuscriptFilterVO.getSubmissionDateTo()));

		List<ManuScript> list = (List<ManuScript>) criteria.list();

		List<TrackManuScriptVO> voList = new ArrayList<TrackManuScriptVO>();

		for (ManuScript m : list) {
			for (Role role : user.getRole()) {
				if (manuscriptFilterVO.getUserId().equals(m.getAuthorId())) {
					if (role.getRoleId().equals(4)) {
						TrackManuScriptVO vo = new TrackManuScriptVO();
						vo.setManuscriptId(m.getManuScriptId());
						vo.setManuscriptCode(m.getManuscriptCode());
						vo.setAuthorId(m.getAuthorId());
						vo.setUserName(m.getUser().getFirstName() + " " + m.getUser().getLastName());
						vo.setManuscriptTitle(m.getManuscriptTitle());
						vo.setSubmissionDate(m.getSubmissionDate());
						vo.setManuscriptStatusId(m.getManuscriptStatusId());
						vo.setManuscriptValue(m.getStatusMaster().getStatusValue());
						vo.setJournalId(m.getJournalId());
						vo.setManuscriptVersion(m.getManuscriptVersion());
						vo.setSubmittedBy(m.getSubmittedBy());
						vo.setUpdatedBy(m.getUpdatedBy());
						vo.setCreatedBy(m.getCreatedBy());
						vo.setCreatedOn(m.getCreatedOn());
						vo.setUpdatedOn(m.getUpdatedOn());
						voList.add(vo);
					} else {
						TrackManuScriptVO vo = new TrackManuScriptVO();
						vo.setManuscriptId(m.getManuScriptId());
						vo.setManuscriptCode(m.getManuscriptCode());
						vo.setAuthorId(m.getAuthorId());
						vo.setUserName(m.getUser().getFirstName() + " " + m.getUser().getLastName());
						vo.setManuscriptTitle(m.getManuscriptTitle());
						vo.setSubmissionDate(m.getSubmissionDate());
						vo.setManuscriptStatusId(m.getManuscriptStatusId());
						vo.setManuscriptValue(m.getStatusMaster().getStatusValue());
						vo.setJournalId(m.getJournalId());
						vo.setManuscriptVersion(m.getManuscriptVersion());
						vo.setSubmittedBy(m.getSubmittedBy());
						vo.setUpdatedBy(m.getUpdatedBy());
						vo.setCreatedBy(m.getCreatedBy());
						vo.setCreatedOn(m.getCreatedOn());
						vo.setUpdatedOn(m.getUpdatedOn());
						voList.add(vo);
					}
				}
			}
		}
		return voList;

	}

	@SuppressWarnings({ "deprecation", "unchecked" })
	@Override
	public List<SearchReviewerVO> queryReviewer(SearchReviewerVO searchReviewerVO) {
		Session session = entityManager.unwrap(Session.class);
		Criteria criteria = session.createCriteria(Author.class);

		if (searchReviewerVO.getReviewerName() != null && searchReviewerVO.getReviewerName().trim().length() > 0)
			criteria.add(Restrictions.like("firstName", searchReviewerVO.getReviewerName(), MatchMode.ANYWHERE));

		if (searchReviewerVO.getReviewerSpeciality() != null
				&& searchReviewerVO.getReviewerSpeciality().trim().length() > 0)
			criteria.add(Restrictions.eq("speciality", searchReviewerVO.getReviewerSpeciality()));

		if (searchReviewerVO.getOrcid() != null && searchReviewerVO.getOrcid().trim().length() > 0)
			criteria.add(Restrictions.eq("orcidid", searchReviewerVO.getOrcid()));

		if (searchReviewerVO.getEmailId() != null && searchReviewerVO.getEmailId().trim().length() > 0)
			criteria.add(Restrictions.like("authorEmail", searchReviewerVO.getEmailId(), MatchMode.ANYWHERE));

		List<Author> list = (List<Author>) criteria.list();

		if (searchReviewerVO.getInstitutionName() != null) {
			List<Author> manuscriptList = list.stream()
					.filter(s -> s.getInstitution().getInstName().toUpperCase()
							.contains(searchReviewerVO.getInstitutionName().toUpperCase()))
					.collect(Collectors.toList());
			list = manuscriptList;
		}

		List<SearchReviewerVO> voList = new ArrayList<SearchReviewerVO>();
		for (Author a : list) {
			SearchReviewerVO vo = new SearchReviewerVO();
			vo.setReviewerName(a.getFirstName() + " " + a.getLastName());
			vo.setReviewerSpeciality(a.getSpeciality());
			vo.setInstitutionName(a.getInstitution().getInstName());
			vo.setOrcid(a.getOrcidid());
			vo.setEmailId(a.getAuthorEmail());

			voList.add(vo);
		}
		return voList;
	}

	@Override
	@SuppressWarnings({ "unchecked", "deprecation" })
	public List<TrackManuScriptVO> getManuscript(ManuscriptFilterVO manuscriptFilterVO) {
		Session session = entityManager.unwrap(Session.class);
		Criteria criteria = session.createCriteria(ManuScript.class);

		if (manuscriptFilterVO.getManuscriptCode() != null
				&& manuscriptFilterVO.getManuscriptCode().trim().length() > 0)
			criteria.add(Restrictions.eq("manuscriptCode", manuscriptFilterVO.getManuscriptCode()));

		if (manuscriptFilterVO.getManuscriptTitle() != null
				&& manuscriptFilterVO.getManuscriptTitle().trim().length() > 0)
			criteria.add(
					Restrictions.like("manuscriptTitle", manuscriptFilterVO.getManuscriptTitle(), MatchMode.ANYWHERE));

		if (manuscriptFilterVO.getSubmissionDateFrom() != null)
			criteria.add(Restrictions.ge("submissionDate", manuscriptFilterVO.getSubmissionDateFrom()));

		if (manuscriptFilterVO.getSubmissionDateTo() != null)
			criteria.add(Restrictions.le("submissionDate", manuscriptFilterVO.getSubmissionDateTo()));

		List<ManuScript> list = (List<ManuScript>) criteria.list();

		List<TrackManuScriptVO> voList = new ArrayList<TrackManuScriptVO>();

		for (ManuScript m : list) {
			TrackManuScriptVO vo = new TrackManuScriptVO();
			vo.setManuscriptId(m.getManuScriptId());
			vo.setManuscriptCode(m.getManuscriptCode());
			vo.setAuthorId(m.getAuthorId());
			vo.setUserName(m.getUser().getFirstName() + " " + m.getUser().getLastName());
			vo.setManuscriptTitle(m.getManuscriptTitle());
			vo.setSubmissionDate(m.getSubmissionDate());
			vo.setManuscriptStatusId(m.getManuscriptStatusId());
			vo.setManuscriptValue(m.getStatusMaster().getStatusValue());
			vo.setJournalId(m.getJournalId());
			vo.setManuscriptVersion(m.getManuscriptVersion());
			vo.setSubmittedBy(m.getSubmittedBy());
			vo.setUpdatedBy(m.getUpdatedBy());
			vo.setCreatedBy(m.getCreatedBy());
			vo.setCreatedOn(m.getCreatedOn());
			vo.setUpdatedOn(m.getUpdatedOn());
			voList.add(vo);
		}
		return voList;
	}

	@Override
	@SuppressWarnings({ "unchecked", "deprecation"})
	public List<TrackManuScriptVO> getJournalIdManuscript(ManuscriptFilterVO manuscriptFilterVO) {

        Criteria dataCriteria = getCriteriaForSearchManuscript(manuscriptFilterVO);
        dataCriteria.setFirstResult((manuscriptFilterVO.getPageNumber()-1) * manuscriptFilterVO.getPageSize());
        dataCriteria.setMaxResults(manuscriptFilterVO.getPageSize());
		
		List<ManuScript> list = (List<ManuScript>) dataCriteria.list();
		List<TrackManuScriptVO> voList = new ArrayList<TrackManuScriptVO>();

			for (ManuScript m : list) {
				// for (Role role : user.getRole()) {
					TrackManuScriptVO vo = new TrackManuScriptVO();
					vo.setManuscriptId(m.getManuScriptId());
					vo.setManuscriptCode(m.getManuscriptCode());
					vo.setAuthorId(m.getAuthorId());
					vo.setUserName(m.getUser().getFirstName() + " " + m.getUser().getLastName());
					vo.setManuscriptTitle(m.getManuscriptTitle());
					vo.setSubmissionDate(m.getSubmissionDate());
					vo.setManuscriptStatusId(m.getManuscriptStatusId());
					if (m.getStatusMaster() != null) {
						vo.setManuscriptStatus(m.getStatusMaster().getStatusValue());
						vo.setStatusValueForAuthor(m.getStatusMaster().getStatusValueForAuthor());
						vo.setStatusValueForEditor(m.getStatusMaster().getStatusValueForEditor());
					}
					vo.setManuscriptValue(m.getStatusMaster().getStatusValue());
					vo.setManuscriptAbstract(m.getManuscriptAbstract());
					vo.setManuscriptKeyword(m.getManuscriptKeywords());
					if (m.getUser() != null)
						vo.setCorrespondingAuthorEmailAddress(m.getUser().getEmail());
					vo.setJournalId(m.getJournalId());
					vo.setActivePhase(m.getIsActive());
					if (m.getManuscriptTypeDetails() != null)
						vo.setManuscriptType(m.getManuscriptTypeDetails().getManuscriptType());
					vo.setManuscriptVersion(m.getManuscriptVersion());
					vo.setSubmittedBy(m.getSubmittedBy());
					vo.setUpdatedBy(m.getUpdatedBy());
					vo.setCreatedBy(m.getCreatedBy());
					vo.setCreatedOn(m.getCreatedOn());
					vo.setUpdatedOn(m.getUpdatedOn());
					voList.add(vo);

				}

		return voList;

	}
	
	private Criteria getCriteriaForSearchManuscript(ManuscriptFilterVO manuscriptFilterVO) {
		
		Session session = entityManager.unwrap(Session.class);
		Criteria criteria = session.createCriteria(ManuScript.class, "manuscript");
		criteria.createAlias("manuscript.user", "user");
		criteria.createAlias("manuscript.manuscriptTypeDetails", "manuscriptTypeDetails");
		criteria.createAlias("manuscript.statusMaster", "statusMaster");

		if (manuscriptFilterVO.getManuscriptCode() != null && !manuscriptFilterVO.getManuscriptCode().isEmpty()
				&& manuscriptFilterVO.getManuscriptCode().trim().length() > 0)
			criteria.add(Restrictions.like("manuscript.manuscriptCode", manuscriptFilterVO.getManuscriptCode(),MatchMode.ANYWHERE));

		if (manuscriptFilterVO.getManuscriptKeyword() != null && !manuscriptFilterVO.getManuscriptKeyword().isEmpty()
				&& manuscriptFilterVO.getManuscriptKeyword().trim().length() > 0)
			criteria.add(Restrictions.like("manuscript.manuscriptKeywords", manuscriptFilterVO.getManuscriptKeyword(),
					MatchMode.ANYWHERE));

		if (manuscriptFilterVO.getCorrespondingAuthorEmailAddress() != null
				&& !manuscriptFilterVO.getCorrespondingAuthorEmailAddress().isEmpty()
				&& manuscriptFilterVO.getCorrespondingAuthorEmailAddress().trim().length() > 0)
			criteria.add(Restrictions.like("user.email", manuscriptFilterVO.getCorrespondingAuthorEmailAddress(),
					MatchMode.ANYWHERE));

		if (manuscriptFilterVO.getManuscriptStatus() != null && !manuscriptFilterVO.getManuscriptStatus().isEmpty()
				&& manuscriptFilterVO.getManuscriptStatus().trim().length() > 0)
			criteria.add(Restrictions.like("statusMaster.statusValue", manuscriptFilterVO.getManuscriptStatus(),MatchMode.ANYWHERE));

		if (manuscriptFilterVO.getManuscriptTitle() != null && !manuscriptFilterVO.getManuscriptTitle().isEmpty()
				&& manuscriptFilterVO.getManuscriptTitle().trim().length() > 0)
			criteria.add(Restrictions.like("manuscript.manuscriptTitle", manuscriptFilterVO.getManuscriptTitle(),
					MatchMode.ANYWHERE));

		if (manuscriptFilterVO.getSubmissionDateFrom() != null)
			criteria.add(Restrictions.ge("manuscript.submissionDate", manuscriptFilterVO.getSubmissionDateFrom()));

		if (manuscriptFilterVO.getSubmissionDateTo() != null)
			criteria.add(Restrictions.le("manuscript.submissionDate", manuscriptFilterVO.getSubmissionDateTo()));

			criteria.add(Restrictions.eq("isActive", 1));
		
			//criteria.add(Restrictions.ne("manuscriptStatusId", 1));
		    criteria.add(Restrictions.not(Restrictions.in("manuscriptStatusId", 1,56)));

		
		if (manuscriptFilterVO.getUserId() != null && manuscriptFilterVO.getRoleId().equals(3) && !manuscriptFilterVO.getCompleteSearchFlag().equals(1)) {
			criteria.add(Restrictions.eq("assignedUser", manuscriptFilterVO.getUserId()));
		}

		
		if (manuscriptFilterVO.getAuthorName() != null && !manuscriptFilterVO.getAuthorName().isEmpty()
				&& manuscriptFilterVO.getAuthorName().trim().length() > 0) {
			String[] name = manuscriptFilterVO.getAuthorName().split(" "); 
			if (name.length >0 && StringUtils.isNotBlank(name[0]))
			criteria.add(Restrictions.like("user.firstName", name[0], MatchMode.ANYWHERE));
			if (name.length >1 && StringUtils.isNotBlank(name[1]))
			criteria.add(Restrictions.like("user.lastName", name[1], MatchMode.ANYWHERE));
		}
		
		
		if (manuscriptFilterVO.getManuscriptAbstract() != null && !manuscriptFilterVO.getManuscriptAbstract().isEmpty()
				&& manuscriptFilterVO.getManuscriptAbstract().trim().length() > 0)
			criteria.add(Restrictions.like("manuscript.manuscriptAbstract", manuscriptFilterVO.getManuscriptAbstract(),
					MatchMode.ANYWHERE));

		if (manuscriptFilterVO.getManuscriptType() != null && !manuscriptFilterVO.getManuscriptType().isEmpty()
				&& manuscriptFilterVO.getManuscriptType().trim().length() > 0)
			criteria.add(Restrictions.like("manuscriptTypeDetails.manuscriptType",
					manuscriptFilterVO.getManuscriptType(), MatchMode.ANYWHERE));

		//criteria.add(Restrictions.in("journalId", journalId));
		if(manuscriptFilterVO.getJournalId()!=null) {
		criteria.add(Restrictions.eq("journalId", manuscriptFilterVO.getJournalId()));
		}
		
		return criteria;
	}

	@Override
	public Long getTotalManuscriptCount(ManuscriptFilterVO manuscriptFilterVO) {

        Criteria dataCountCriteria = getCriteriaForSearchManuscript(manuscriptFilterVO);
        dataCountCriteria.setProjection(Projections.rowCount());
        Long  rowCount = (Long) dataCountCriteria.uniqueResult();


		return rowCount;
	}
}
