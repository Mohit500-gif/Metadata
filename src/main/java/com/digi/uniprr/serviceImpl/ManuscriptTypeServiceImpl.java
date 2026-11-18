package com.digi.uniprr.serviceImpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.digi.uniprr.VO.JournalManuscriptTypeVo;
import com.digi.uniprr.model.ManuscriptType;
import com.digi.uniprr.model.ManuscriptTypeJournal;
import com.digi.uniprr.repository.ManuscriptTypeJournalsRepo;
import com.digi.uniprr.repository.ManuscriptTypeRepo;
import com.digi.uniprr.service.ManuscriptTypeService;

@Service
@Transactional
public class ManuscriptTypeServiceImpl implements ManuscriptTypeService {

	@Autowired
	ManuscriptTypeRepo manuscriptTypeRepo;

	@Autowired
	ManuscriptTypeJournalsRepo manuscriptTypeJournalsRepo;

	@Override
	public List<ManuscriptType> getManuscriptTypeList() {
		return manuscriptTypeRepo.getActiveManuscriptTypes();
	}

	@Override
	public void saveManuscriptType(ManuscriptType type) {

		// ManuscriptType manuscriptType =new ManuscriptType();

		type.setIsActive(1);
		manuscriptTypeRepo.save(type);
	}

	@Override
	public void deleteManuscriptType(Integer id) {
		ManuscriptType type = manuscriptTypeRepo.getById(id);
		ManuscriptTypeJournal mtjid=null;
		try
		{
			mtjid = manuscriptTypeJournalsRepo.getJournalManuscriptTypeBy1(type.getName(),type.getJournalId());
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		if (mtjid != null)
		{
			manuscriptTypeJournalsRepo.deleteById(mtjid.getMtjId());
			manuscriptTypeRepo.deleteById(id);
		}
		else
		{
			manuscriptTypeRepo.deleteById(id);
		}
		// type.setIsActive(0);
//		manuscriptTypeRepo.deleteById(id);
	}

	@Override
	public ManuscriptType getManuscriptTypeById(Integer id) {
		return manuscriptTypeRepo.getOne(id);
	}

	@Override
	public boolean isManuscriptTypeExist(ManuscriptType manuscriptType) {
		//ManuscriptType getManuscriptTypeData = manuscriptTypeRepo.getManuscriptTypeBy(manuscriptType.getName());
		ManuscriptType getManuscriptTypeData = manuscriptTypeRepo.getManuscriptTypeByJournal(manuscriptType.getName(),manuscriptType.getJournalId());
		if (getManuscriptTypeData != null)
			return true;
		return false;
	}

	@Override
	public void saveDataInDb(String manusciptType) {
		ManuscriptType getManuscriptTypeData = null;
		getManuscriptTypeData = manuscriptTypeRepo.getManuscriptTypeBy(manusciptType.trim());
		if (getManuscriptTypeData == null) {
			ManuscriptType manuscriptType = new ManuscriptType();
			manuscriptType.setName(manusciptType.trim());
			manuscriptType.setCreatedBy(1);
			manuscriptType.setIsActive(1);
			manuscriptType.setJournalId(0);
			manuscriptTypeRepo.save(manuscriptType);
		}
	}

	@Override
	public ManuscriptType getManuscriptTypeByName(String name) {

		return manuscriptTypeRepo.getManuscriptTypeBy(name);
	}

	@Override
	public boolean isJournalManuscriptTypeExist(JournalManuscriptTypeVo manuscriptType) {
		ManuscriptTypeJournal i = manuscriptTypeJournalsRepo
				.getJournalManuscriptTypeBy(manuscriptType.getManutypeName(),manuscriptType.getJournalId());
		if (i != null)
			return true;
		return false;
	}

	@Override
	public void saveJournalManuscriptType(JournalManuscriptTypeVo manuscriptType) {
		if (manuscriptType.getMtjId() != null) {
			ManuscriptTypeJournal j = manuscriptTypeJournalsRepo.getOne(manuscriptType.getMtjId());
			if (j != null) {
				j.setManutypeName(manuscriptType.getManutypeName());
				j.setUpdatedOn(new Date());
				j.setIsActive(1);
				manuscriptTypeJournalsRepo.save(j);
			}
		} else {
			ManuscriptTypeJournal p = new ManuscriptTypeJournal();
			p.setJournalId(manuscriptType.getJournalId());
			p.setManutypeName(manuscriptType.getManutypeName());
			p.setCreatedOn(new Date());
			p.setIsActive(1);
			p.setIsAbstract(0);
			manuscriptTypeJournalsRepo.save(p);
		}

	}

	@Override
	public void deleteJournalManuscriptType(JournalManuscriptTypeVo manuscriptType) {
		manuscriptTypeJournalsRepo.deleteById(manuscriptType.getMtjId());
	}

	@Override
	public List<ManuscriptTypeJournal> getJournalManuscriptType(JournalManuscriptTypeVo manuscriptType) {
		List<ManuscriptTypeJournal> add = new ArrayList<ManuscriptTypeJournal>();
		List<ManuscriptTypeJournal> k = manuscriptTypeJournalsRepo
				.getManuscriptTypesFromJournalId(manuscriptType.getJournalId());
		if (k != null && !k.isEmpty()) {
			k.stream().forEach(l -> {
				ManuscriptTypeJournal a = new ManuscriptTypeJournal();
				a.setMtjId(l.getMtjId());
				a.setIsAbstract(l.getIsAbstract());
				a.setManutypeName(l.getManutypeName());
				a.setJournalId(l.getJournalId());
				add.add(a);
			});
			return add;
		}
		return null;
	}

	@Override
	public List<ManuscriptType> getListByJournal(Integer journalId) {
		List<Integer> journalList = new ArrayList<>();
		journalList.add(journalId);
		journalList.add(0);
		return manuscriptTypeRepo.getActiveManuscriptTypesByJourna(journalList);
	}

	@Override
	public List<ManuscriptType> getListByJournalId(Integer journalId) {
		return manuscriptTypeRepo.getListByJournalId(journalId);
	}

}