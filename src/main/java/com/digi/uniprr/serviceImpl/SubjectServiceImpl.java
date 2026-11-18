package com.digi.uniprr.serviceImpl;

import java.util.List;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.digi.uniprr.VO.IDVO;
import com.digi.uniprr.VO.SubjectVO;
import com.digi.uniprr.model.Subject;
import com.digi.uniprr.repository.SpecilizationRepo;
import com.digi.uniprr.repository.SubjectRepo;
import com.digi.uniprr.service.SubjectService;

@Service
@Transactional
public class SubjectServiceImpl implements SubjectService {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	SubjectRepo subjectRepo;

	@Autowired
	SpecilizationRepo specilizationRepo;

	@Override
	public void createSubject(SubjectVO subjectVO) {

		Subject subject = new Subject();

		subject.setSubjectName(subjectVO.getSubjectName());
		subject.setSubjType(subjectVO.getSubjType());
		subject.setSubjIdParent(subjectVO.getSubjIdParent());
		subject.setCreatedBy(subjectVO.getCreatedBy());
		subject.setCreatedOn(subjectVO.getCreatedOn());
		subject.setUpdatedBy(subjectVO.getUpdatedBy());
		subject.setUpdatedOn(subjectVO.getUpdatedOn());
		subject.setIsActive(1);

		Subject subjectData = subjectRepo.save(subject);

		/*
		 * if (subjectVO != null && subjectVO.getSpecialization() != null &&
		 * !subjectVO.getSpecialization().isEmpty()) { for (Specialization
		 * specialization : subjectVO.getSpecialization()) {
		 * 
		 * Specialization specializationData = new Specialization();
		 * 
		 * specializationData.setSubjectId(subjectData.getId());
		 * specializationData.setSpecialization(specialization.getSpecialization());
		 * specializationData.setCreatedBy(specialization.getCreatedBy());
		 * specializationData.setCreatedOn(specialization.getCreatedOn());
		 * specializationData.setUpdatedBy(specialization.getUpdatedBy());
		 * specializationData.setUpdatedOn(specialization.getUpdatedOn());
		 * specializationData.setIsActive(1);
		 * specilizationRepo.save(specializationData);
		 * 
		 * } }
		 */

	}

	@Override
	public List<Subject> getSubjectList() {
		List<Subject> data = subjectRepo.getSubjectList();
		return data;
	}

	@Override
	public void deleteSubject(IDVO id) {
		subjectRepo.deleteSubject(id.getId());

	}

	@Override
	public void updateSubject(Subject subject) {
		Subject subjectData = subjectRepo.getOne(subject.getId());
		subjectData.setSubjectName(subject.getSubjectName());
		subjectRepo.save(subjectData);

	}

	@Override
	public String saveData(String dataForSave) {

		try {
			List<Subject> subject = subjectRepo.getSubjectByName(dataForSave);
			if (subject == null || subject.isEmpty()) {
				Subject subjectData = new Subject();
				subjectData.setSubjectName(dataForSave);
				subjectData.setCreatedBy(1);
				subjectData.setIsActive(1);
				subjectRepo.save(subjectData);
			}
		} catch (Exception e) {
			log.error("Exception : " + e.getMessage());
		}

		return "done";

	
	}

	@Override
	public List<Subject> getSubjectByName(String name) {
		List<Subject> subject = subjectRepo.getSubjectByName(name);
		return subject;
	}

}
