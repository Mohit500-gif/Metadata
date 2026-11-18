package com.digi.uniprr.serviceImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import com.digi.uniprr.model.Journal;
import com.digi.uniprr.repository.*;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.digi.uniprr.VO.IDVO;
import com.digi.uniprr.VO.SpecializationVO;
import com.digi.uniprr.model.JournalMetadata;
import com.digi.uniprr.model.Specialization;
import com.digi.uniprr.model.Subject;
import com.digi.uniprr.service.SpecializationService;

@Service
@Transactional
public class SpecializationServiceImpl implements SpecializationService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    SpecilizationRepo specilizationRepo;

    @Autowired
    JournalMetadataRepo journalMetadataRepo;

    @Autowired
    ExpertiseRepo expertiseRepo;

    @Autowired
    KeywordsRepo keywordsRepo;
    JSONObject obj = new JSONObject();
    @Autowired
    private SubjectRepo subjectRepo;
    @Autowired
    private JournalsRepo journalsRepo;

    /*
     * @Override public List<SpecializationVO> getList() {
     * log.info("Inside specilizationList getList Impl"); List<Specialization>
     * specilizationList = specilizationRepo.findAll(); List<SpecializationVO> list
     * = new ArrayList<SpecializationVO>(); for(Specialization s :specilizationList
     * ) { SpecializationVO vo = new SpecializationVO(); vo.setId(s.getId());
     * vo.setExpertiseId(s.getSubjectId());
     * vo.setSpecialization(s.getSpecialization());
     * vo.setCreatedby(s.getCreatedBy()); vo.setCreatedOn(s.getCreatedOn());
     * vo.setUpdatedBy(s.getUpdatedBy()); vo.setUpdatedOn(s.getUpdatedOn());
     *
     * list.add(vo); } return list; }
     */

    @Override
    public List<Specialization> getspecializationListById(List<Integer> id) {
        return specilizationRepo.getSpecializationByIdList(id);
    }

//    @Override
//    public List<SpecializationVO> querysearchjournal(SpecializationVO specializationVO) {
//        List<Integer> jId = new ArrayList<>();
//        List<JournalMetadata> data = new ArrayList<>();
////        List<Specialization> specialization = new ArrayList<>();
////        Set<String> specialityTempData = new HashSet<>();
////        List<Subject> subject = new ArrayList<>();
////        Set<String> subjectTempData = new HashSet<>();
//        List<SpecializationVO> list = new ArrayList<SpecializationVO>();
//        if (specializationVO.getSubjectName().isEmpty())
//        {
//            for (String subjectName : specializationVO.getSubjectName()) {
//                List<Integer> jData = journalMetadataRepo.getSpecilaizationBySubject(subjectName);
//                if (jData != null && !jData.isEmpty())
//                {
//                    for (Integer jIdData : jData)
//                    {
//                        if(getJournal(jIdData))
//                        {
//                            jId.add(jIdData);
//                        }
//                    }
//                }
//
//            }
//        }
//        else
//        {
//            for (String specializationName : specializationVO.getSpecializationName())
//            {
//                List<Integer> jData = journalMetadataRepo.getSpecilaization(specializationName);
//                if (jData != null && !jData.isEmpty())
//                {
//                    for (Integer jIdData : jData)
//                    {
//                        if(getJournal(jIdData))
//                        {
//                            jId.add(jIdData);
//                        }
//                    }
//                }
//            }
//            for (String subjectName : specializationVO.getSubjectName())
//            {
//                List<Integer> jData = journalMetadataRepo.getSpecilaizationBySubject(subjectName);
//                if (jData != null && !jData.isEmpty())
//                {
//                    for (Integer jIdData : jData) {
//                        if(getJournal(jIdData))
//                        {
//                            jId.add(jIdData);
//                        }
//                    }
//                }
//            }
//        }
//        if (specializationVO.getPublicationType() != null)
//        {
//            List<Integer> jData = journalMetadataRepo
//                    .getJournalBySubscriptionType(specializationVO.getPublicationType());
//            if (jData != null && !jData.isEmpty())
//            {
//                for (Integer jIdData : jData)
//                {
//                    if(getJournal(jIdData))
//                    {
//                        jId.add(jIdData);
//                    }
//                }
//            }
//        }
//        if (specializationVO.getKeyword() != null && !specializationVO.getKeyword().isEmpty())
//        {
//            List<Integer> jData = keywordsRepo.getJournalIdByKeyword(specializationVO.getKeyword().get(0));
//            if (jData != null && !jData.isEmpty())
//            {
//                for (Integer jIdData : jData)
//                {
//                    if(getJournal(jIdData))
//                    {
//                        jId.add(jIdData);
//                    }
//                }
//            }
//        }
//        if (jId != null && !jId.isEmpty())
//        {
//            data = journalMetadataRepo.getMetaDataByListOfJId(jId);
//        }
//        if (data != null && !data.isEmpty())
//        {
//            for (JournalMetadata s : data)
//            {
//                Set<String> specialityTempData = new HashSet<>();
//                List<Specialization> specialization = new ArrayList<>();
//                List<Subject> subject = new ArrayList<>();
//                Set<String> subjectTempData = new HashSet<>();
//                if (specializationVO.getSubjectName() != null
//                        && !specializationVO.getSubjectName().isEmpty()
//                        && specializationVO.getPublicationType() == null
//                        || specializationVO.getPublicationType().isEmpty())
//                {
//                    if (s.getJournal().getIsActive() != null && s.getJournal().getIsActive().equals(1))
//                    {
//                        SpecializationVO vo = new SpecializationVO();
//                        vo.setJournalId(s.getJournalId());
//                        // vo.setJournalSpeciality(s.getJournalSpeciality());
//                        // vo.setJournalSubCategory(s.getJournalSubCategory());
//                        // vo.setJournalSubject(s.getJournalSubject());
//                        vo.setJournalDOI(s.getJournalDOI());
//                        vo.setJournalBlindType(s.getJournalBlindType());
//                        vo.setJournalEissn(s.getJournalEissn());
//                        vo.setJournalReadership(s.getJournalReadership());
//                        vo.setJournalFrequency(s.getJournalFrequency());
//                        vo.setJournalDOI(s.getJournalDOI());
//                        vo.setOpenAccess(s.getOpenAccess());
//                        vo.setJournalCode(s.getJournal().getJournalCode());
//                        vo.setJournalTitle(s.getJournal().getJournalTitle());
//                        vo.setJournalIssn(s.getJournalIssn());
//                        vo.setBibliography(s.getBibliography());
//                        vo.setPublicationType(s.getPublicationType());
//                        if (s.getJournal().getCharge() != null)
//                        {
//                            vo.setJournalChargeId(s.getJournal().getCharge().getJournalChargeId());
//                            vo.setChargeAmount(s.getJournal().getCharge().getChargeAmount());
//                            vo.setColorCharges(s.getJournal().getCharge().getColorCharges());
//                            vo.setFixedChargeAmount(s.getJournal().getCharge().getFixedChargeAmount());
//                            vo.setFixedCharges(s.getJournal().getCharge().getFixedCharges());
//                        }
//                        if (s.getJournalSpeciality() != null && !s.getJournalSpeciality().isEmpty())
//                        {
//                            String Speciality[] = s.getJournalSpeciality().split("@");
//                            if (Speciality.length > 0)
//                            {
//                                List<Integer> specialityId = new ArrayList<>();
//                                for (String specializationData : Speciality)
//                                {
//                                    specialityId.add(Integer.parseInt(specializationData));
//                                }
//                                specialization = specilizationRepo.getSpecializationByListOfId(specialityId);
//                            }
//                            if (specialization != null && !specialization.isEmpty())
//                            {
//                                for (Specialization sData : specialization)
//                                {
//                                    specialityTempData.add(sData.getSpecialization());
//                                }
//                            }
//                        }
//                        if (s.getJournalSubject() != null && !s.getJournalSubject().isEmpty())
//                        {
//                            String Subject[] = s.getJournalSubject().split("@");
//                            if (Subject.length > 0)
//                            {
//                                List<Integer> subjectId = new ArrayList<>();
//                                for (String subjectData : Subject)
//                                {
//                                    subjectId.add(Integer.parseInt(subjectData));
//                                }
//                                subject = subjectRepo.getSubjectListByIdList(subjectId);
//                            }
//                            if (subject != null && !subject.isEmpty())
//                            {
//                                for (Subject sData : subject)
//                                {
//                                    subjectTempData.add(sData.getSubjectName());
//                                }
//                            }
//                        }
//                        vo.setSpecializationName(specialityTempData);
//                        vo.setSubjectName(subjectTempData);
//                        vo.setExtendDueDate(s.getExtendDueDate());
//                        vo.setCreatedby(s.getCreatedBy());
//                        vo.setCreatedOn(s.getCreatedOn());
//                        vo.setUpdatedBy(s.getUpdatedBy());
//                        vo.setUpdatedOn(s.getUpdatedOn());
//                        list.add(vo);
//                    }
//
//                }
//
//                if (specializationVO.getSubjectName() != null
//                        && !specializationVO.getSubjectName().isEmpty()
//                        && specializationVO.getPublicationType() != null
//                        && !specializationVO.getPublicationType().isEmpty())
//                {
//                    if (specializationVO.getPublicationType().equals(s.getPublicationType()))
//                    {
//                        if (s.getJournal().getIsActive() != null && s.getJournal().getIsActive().equals(1))
//                        {
//                            SpecializationVO vo = new SpecializationVO();
//                            vo.setJournalId(s.getJournalId());
//                            // vo.setJournalSpeciality(s.getJournalSpeciality());
//                            // vo.setJournalSubCategory(s.getJournalSubCategory());
//                            // vo.setJournalSubject(s.getJournalSubject());
//                            vo.setJournalDOI(s.getJournalDOI());
//                            vo.setJournalBlindType(s.getJournalBlindType());
//                            vo.setJournalEissn(s.getJournalEissn());
//                            vo.setJournalReadership(s.getJournalReadership());
//                            vo.setJournalFrequency(s.getJournalFrequency());
//                            vo.setJournalDOI(s.getJournalDOI());
//                            vo.setOpenAccess(s.getOpenAccess());
//                            vo.setJournalCode(s.getJournal().getJournalCode());
//                            vo.setJournalTitle(s.getJournal().getJournalTitle());
//                            vo.setJournalIssn(s.getJournalIssn());
//                            vo.setBibliography(s.getBibliography());
//                            vo.setPublicationType(s.getPublicationType());
//                            if (s.getJournal().getCharge() != null)
//                            {
//                                vo.setJournalChargeId(s.getJournal().getCharge().getJournalChargeId());
//                                vo.setChargeAmount(s.getJournal().getCharge().getChargeAmount());
//                                vo.setColorCharges(s.getJournal().getCharge().getColorCharges());
//                                vo.setFixedChargeAmount(s.getJournal().getCharge().getFixedChargeAmount());
//                                vo.setFixedCharges(s.getJournal().getCharge().getFixedCharges());
//                            }
//
//                            if (s.getJournalSpeciality() != null && !s.getJournalSpeciality().isEmpty())
//                            {
//
//                                String[] Speciality = s.getJournalSpeciality().split("@");
//                                if (Speciality.length > 0)
//                                {
//                                    List<Integer> specialityId = new ArrayList<>();
//                                    for (String specializationData : Speciality)
//                                    {
//                                        specialityId.add(Integer.parseInt(specializationData));
//                                    }
//                                    System.out.println(specialityId);
//                                    specialization = specilizationRepo.getSpecializationByListOfId(specialityId);
//                                }
//                                if (specialization != null && !specialization.isEmpty())
//                                {
//                                    for (Specialization sData : specialization)
//                                    {
//                                        specialityTempData.add(sData.getSpecialization());
//                                    }
//                                    System.out.println(specialityTempData);
//                                }
//                            }
//                            if (s.getJournalSubject() != null && !s.getJournalSubject().isEmpty())
//                            {
//                                String Subject[] = s.getJournalSubject().split("@");
//                                if (Subject != null && Subject.length > 0)
//                                {
//                                    List<Integer> subjectId = new ArrayList<>();
//                                    for (String subjectData : Subject)
//                                    {
//                                        subjectId.add(Integer.parseInt(subjectData));
//                                    }
//                                    subject = subjectRepo.getSubjectListByIdList(subjectId);
//                                }
//                                if (subject != null && !subject.isEmpty())
//                                {
//                                    for (Subject sData : subject)
//                                    {
//                                        subjectTempData.add(sData.getSubjectName());
//                                    }
//                                }
//                            }
//                            vo.setSpecializationName(specialityTempData);
//                            vo.setSubjectName(subjectTempData);
//                            vo.setExtendDueDate(s.getExtendDueDate());
//                            vo.setCreatedby(s.getCreatedBy());
//                            vo.setCreatedOn(s.getCreatedOn());
//                            vo.setUpdatedBy(s.getUpdatedBy());
//                            vo.setUpdatedOn(s.getUpdatedOn());
//                            list.add(vo);
//                        }
//                    }
//
//                }
//
//                if (specializationVO.getPublicationType() != null
//                        && !specializationVO.getPublicationType().isEmpty()
//                        && specializationVO.getSubjectName().isEmpty())
//                {
//                    if (specializationVO.getPublicationType().equals(s.getPublicationType()))
//                    {
//                        if (s.getJournal().getIsActive() != null && s.getJournal().getIsActive().equals(1))
//                        {
//                            System.out.println(s.getJournal().getJournalId() + " inside last method");
//
//                            SpecializationVO vo = new SpecializationVO();
//                            vo.setJournalId(s.getJournalId());
//                            // vo.setJournalSpeciality(s.getJournalSpeciality());
//                            // vo.setJournalSubCategory(s.getJournalSubCategory());
//                            // vo.setJournalSubject(s.getJournalSubject());
//                            vo.setJournalDOI(s.getJournalDOI());
//                            vo.setJournalBlindType(s.getJournalBlindType());
//                            vo.setJournalEissn(s.getJournalEissn());
//                            vo.setJournalReadership(s.getJournalReadership());
//                            vo.setJournalFrequency(s.getJournalFrequency());
//                            vo.setJournalDOI(s.getJournalDOI());
//                            vo.setOpenAccess(s.getOpenAccess());
//                            vo.setJournalCode(s.getJournal().getJournalCode());
//                            vo.setJournalTitle(s.getJournal().getJournalTitle());
//                            vo.setJournalIssn(s.getJournalIssn());
//                            vo.setBibliography(s.getBibliography());
//                            vo.setPublicationType(s.getPublicationType());
//                            if (s.getJournal().getCharge() != null)
//                            {
//                                vo.setJournalChargeId(s.getJournal().getCharge().getJournalChargeId());
//                                vo.setChargeAmount(s.getJournal().getCharge().getChargeAmount());
//                                vo.setColorCharges(s.getJournal().getCharge().getColorCharges());
//                                vo.setFixedChargeAmount(s.getJournal().getCharge().getFixedChargeAmount());
//                                vo.setFixedCharges(s.getJournal().getCharge().getFixedCharges());
//                            }
//                            if (s.getJournalSpeciality() != null && !s.getJournalSpeciality().isEmpty())
//                            {
//
//                                String Speciality[] = s.getJournalSpeciality().split("@");
//                                if (Speciality != null && Speciality.length > 0)
//                                {
//                                    List<Integer> specialityId = new ArrayList<>();
//                                    for (String specializationData : Speciality)
//                                    {
//                                        specialityId.add(Integer.parseInt(specializationData));
//                                    }
//                                    specialization = specilizationRepo.getSpecializationByListOfId(specialityId);
//                                }
//                                if (specialization != null && !specialization.isEmpty())
//                                {
//                                    for (Specialization sData : specialization)
//                                    {
//                                        specialityTempData.add(sData.getSpecialization());
//                                    }
//                                }
//                            }
//                            if (s.getJournalSubject() != null && !s.getJournalSubject().isEmpty())
//                            {
//                                String Subject[] = s.getJournalSubject().split("@");
//                                if (Subject != null && Subject.length > 0)
//                                {
//                                    List<Integer> subjectId = new ArrayList<>();
//                                    for (String subjectData : Subject)
//                                    {
//                                        subjectId.add(Integer.parseInt(subjectData));
//                                    }
//                                    subject = subjectRepo.getSubjectListByIdList(subjectId);
//                                }
//                                if (subject != null && !subject.isEmpty())
//                                {
//                                    for (Subject sData : subject)
//                                    {
//                                        subjectTempData.add(sData.getSubjectName());
//                                    }
//                                }
//                            }
//                            vo.setSpecializationName(specialityTempData);
//                            vo.setSubjectName(subjectTempData);
//                            vo.setExtendDueDate(s.getExtendDueDate());
//                            vo.setCreatedby(s.getCreatedBy());
//                            vo.setCreatedOn(s.getCreatedOn());
//                            vo.setUpdatedBy(s.getUpdatedBy());
//                            vo.setUpdatedOn(s.getUpdatedOn());
//                            list.add(vo);
//                        }
//                    }
//                }
//            }
//        }
//        return list;
//    }

    @Override
    public List<SpecializationVO> querysearchjournal(SpecializationVO specializationVO) {
        List<JournalMetadata> data = new ArrayList<>();
        List<SpecializationVO> list = new ArrayList<SpecializationVO>();
        for (String subjectName : specializationVO.getSubjectName()) {
            String subjectName1 = "@" + subjectName + "@";
            String subjectName2 = '@' + subjectName;
            String subjectName3 = subjectName + '@';
            List<JournalMetadata> jData = journalMetadataRepo.getSpecializationBySubject(subjectName3, subjectName1, subjectName2);
            if (jData != null && !jData.isEmpty()) {
                data.addAll(jData);
            }
        }
        if (!data.isEmpty()) {

            for (JournalMetadata s : data) {
                List<Subject> subject = new ArrayList<>();
                Set<String> subjectTempData = new HashSet<>();

                SpecializationVO vo = new SpecializationVO();

                vo.setJournalId(s.getJournalId());
                vo.setJournalDOI(s.getJournalDOI());
                vo.setJournalBlindType(s.getJournalBlindType());
                vo.setJournalEissn(s.getJournalEissn());
                vo.setJournalReadership(s.getJournalReadership());
                vo.setJournalFrequency(s.getJournalFrequency());
                vo.setJournalDOI(s.getJournalDOI());
                vo.setOpenAccess(s.getOpenAccess());
                vo.setJournalCode(s.getJournal().getJournalCode());
                vo.setJournalTitle(s.getJournal().getJournalTitle());
                vo.setJournalIssn(s.getJournalIssn());
                vo.setBibliography(s.getBibliography());
                vo.setPublicationType(s.getPublicationType());
                if (s.getJournal().getCharge() != null) {
                    vo.setJournalChargeId(s.getJournal().getCharge().getJournalChargeId());
                    vo.setChargeAmount(s.getJournal().getCharge().getChargeAmount());
                    vo.setColorCharges(s.getJournal().getCharge().getColorCharges());
                    vo.setFixedChargeAmount(s.getJournal().getCharge().getFixedChargeAmount());
                    vo.setFixedCharges(s.getJournal().getCharge().getFixedCharges());
                }

                if (s.getJournalSubject() != null && !s.getJournalSubject().isEmpty()) {
                    String[] subjectArray = s.getJournalSubject().split("@");
                    if (subjectArray.length > 0) {
                        List<Integer> subjectId = new ArrayList<>();
                        for (String subjectData : subjectArray) {
                            subjectId.add(Integer.parseInt(subjectData));
                        }
                        subject = subjectRepo.getSubjectListByIdList(subjectId);
                    }
                    if (subject != null && !subject.isEmpty()) {
                        for (Subject sData : subject) {
                            subjectTempData.add(sData.getSubjectName());
                        }
                    }
                }
                vo.setSpecializationName(new HashSet<>());
                vo.setSubjectName(subjectTempData);
                vo.setExtendDueDate(s.getExtendDueDate());
                vo.setCreatedby(s.getCreatedBy());
                vo.setCreatedOn(s.getCreatedOn());
                vo.setUpdatedBy(s.getUpdatedBy());
                vo.setUpdatedOn(s.getUpdatedOn());
                list.add(vo);
            }
        }
        return list;
    }

    @Override
    public void createSpecilization(List<Specialization> specialization) {
        for (Specialization s : specialization) {
            s.setIsActive(1);
            specilizationRepo.save(s);
        }
    }

    /*
     * @Override public Specialization updateSpecilization(Specialization
     * specialization) { Specialization specializationData =
     * specilizationRepo.save(specialization); return specializationData; }
     */

    @Override
    public void deleteSpecilization(IDVO id) {
        specilizationRepo.deleteSpecilization(id.getId());
    }
    /*
     * @Override public List<Specialization> getSpecializationByID(SpecializationVO
     * specializationVO) { List<Specialization> data =
     * specilizationRepo.getSpecializationByIdList(specializationVO.getExpertiseid()
     * ); return data; }
     */

}
