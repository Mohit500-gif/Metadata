package com.digi.uniprr.serviceImpl;

import static com.digi.uniprr.utils.AppConstants.NO;
import static com.digi.uniprr.utils.AppConstants.PLAGARISM_CHECKLIST;
import static com.digi.uniprr.utils.AppConstants.TEMPLATE_PLACEHOLDERS;
import static com.digi.uniprr.utils.AppConstants.YES;
import static org.hamcrest.CoreMatchers.is;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import com.digi.uniprr.model.*;
import com.digi.uniprr.repository.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.digi.uniprr.VO.AssignAdminVO;
import com.digi.uniprr.VO.AutoMailersVO;
import com.digi.uniprr.VO.CharacterLimitVo;
import com.digi.uniprr.VO.IDVO;
import com.digi.uniprr.VO.JournalHistoryDataVO;
import com.digi.uniprr.VO.JournalMailTemplatesVO;
import com.digi.uniprr.VO.JournalManuscriptTypeChargeVo;
import com.digi.uniprr.VO.JournalPlagiarismMasterVO;
import com.digi.uniprr.VO.JournalPlagiarismUpdateVO;
import com.digi.uniprr.VO.JournalSubjectVo;
import com.digi.uniprr.VO.JournalTatConfigurationVO;
import com.digi.uniprr.VO.JournalVO;
import com.digi.uniprr.VO.JournalsConfigurationVO;
import com.digi.uniprr.VO.KeywordsVO;
import com.digi.uniprr.VO.ManuscriptTypeJournalVO;
import com.digi.uniprr.VO.TurnAroundTimeVO;
import com.digi.uniprr.jdbcConfig.RawJDBC;
import com.digi.uniprr.service.JournalConfigurationService;
import com.digi.uniprr.utils.KeywordsTransformationUtils;
import com.digi.uniprr.utils.ManuscriptTypeJournalTransformationUtils;
import com.google.common.net.HttpHeaders;

@Service
@Transactional
public class JournalConfigurationServiceImpl implements JournalConfigurationService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    CountriesRepo countriesRepo;

    @Autowired
    Environment environment;
    @Autowired
    JournalManuscriptTypeChargeRepo journalManuscriptTypeChargeRepo;
    @Autowired
    JournalSubjectRepo journalSubjectRepo;

    @Autowired
    DevelopedCountriesByManuscriptTypeRepo developedCountriesByManuscriptTypeRepo;
    JSONObject obj = new JSONObject();
    @Autowired
    private JournalsRepo journalRepo;
    @Autowired
    private ManuscriptTypeJournalsRepo manuscriptTypeJournalsRepo;
    @Autowired
    private JournalconfigFileUploadRepo journalconfigFileUploadRepo;
    @Autowired
    private FileTypeRepo fileTypeRepo;
    @Autowired
    private JournalconfigMasterfileTypeRepo journalconfigMasterfileTypeRepo;
    @Autowired
    private MailTemplatesRepo mailTemplatesRepo;
    @Autowired
    private KeywordsRepo keywordsRepo;
    @Autowired
    private JournalsRepo journalsRepo;
    @Autowired
    private JournalMailTemplatesRepo journalMailTemplatesRepo;
    @Autowired
    private JournalPlagirismRepo journalPlagirismRepo;
    @Autowired
    private JournalPlagiarismMasterRepo journalPlagiarismMasterRepo;
    @Autowired
    private JournalMetadataRepo journalMetadataRepo;
    @Autowired
    private JournalChargeDetailsRepo journalChargeDetailsRepo;
    @Autowired
    private StatusMasterRepo statusMasterRepo;
    @Autowired
    private JournalTatConfigurationRepo journalTatConfigurationRepo;
    @Autowired
    private EmailTemplateDataRepo emailTemplateDataRepo;
    @Autowired
    private RoleRepo roleRepo;
    @Autowired
    private TaskMasterRepo taskMasterRepo;
    @Autowired
    private TaskEmailMapRepo taskEmailMapRepo;
    @Autowired
    private TaskEmailMapReminderRepo taskEmailMapReminderRepo;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private UserJournalRepo userJournalRepo;
    @Autowired
    private MasterAttributesRepo masterAttributesRepo;
    @Autowired
    private SubjectRepo subjectRepo;
    @Autowired
    private SpecilizationRepo specilizationRepo;
    @Autowired
    private JournalHistoryDataRepo journeyHistoryDataRepo;
    
    @Autowired
    private CharacterLimitRepo characterLimitRepo;
    
    @Autowired
    private JournalDataIssnRepo journalDataIssnRepo;
    
    @Value("${uniprrFolderPath}")
    private String UPLOAD_FOLDER;
    @Value("${thumbnailFilePath}")
    private String THUMBNAIL_UPLOAD_FOLDER;
    
    @Value("${baseUrl}")
    private String baseUrl;
    
    @Value("${uniprrFolderPath}")
    private String uniprrFolderPath;

    @Override
    public Journal getJournalById(Integer id) {
        return journalRepo.getOne(id);
    }

    @SuppressWarnings("unused")
    @Override
    public JournalsConfigurationVO mapJournalProperties(Journal journal) {
        List<Specialization> SpecialityData = new ArrayList<>();
        List<Subject> SubjectData = new ArrayList<>();
        List<Subject> subject = new ArrayList<>();
        List<Specialization> specialization = new ArrayList<>();
        JournalsConfigurationVO journalsConfigurationVO = new JournalsConfigurationVO();
        journalsConfigurationVO.setManuscriptTypeJournals(ManuscriptTypeJournalTransformationUtils.mapModelListToVOList(
                manuscriptTypeJournalsRepo.getManuscriptTypesFromJournalId(journal.getJournalId())));

        journalsConfigurationVO.setJournalId(journal.getJournalId());
        journalsConfigurationVO.setJournalCode(journal.getJournalCode());
        journalsConfigurationVO.setCountryName(journal.getCountryName());
        journalsConfigurationVO.setChargeFlag(journal.getChargeFlag());
        journalsConfigurationVO.setJournalLogo(journal.getJournalLogo());
        journalsConfigurationVO.setJournalTitle(journal.getJournalTitle());
        journalsConfigurationVO.setJournalShortTitle(journal.getJournalShortTitle());
        journalsConfigurationVO.setJournalManuscriptCount(journal.getJournalManuscriptCount());
        journalsConfigurationVO.setPubCode(journal.getPubCode());
        journalsConfigurationVO.setReviewerSuggestion(journal.getReviewerSuggestion());
        journalsConfigurationVO.setReviewerCount(journal.getReviewerCount());
        journalsConfigurationVO.setCoAuthorCount(journal.getCorrespondingCount());

        if (journal.getJournalMetadata() != null) {
            journalsConfigurationVO.setChargeOn(journal.getJournalMetadata().getChargeOn());
            journalsConfigurationVO.setWorkflowId(journal.getJournalMetadata().getWorkflowId());
            journalsConfigurationVO.setJournalMetadataId(journal.getJournalMetadata().getMetadataId());
            journalsConfigurationVO.setIsSubmissionRequired(journal.getJournalMetadata().getIsSubmissionRequired());
            journalsConfigurationVO.setJournalType(journal.getJournalMetadata().getJournalType());
            journalsConfigurationVO.setJournalAffilation(journal.getJournalMetadata().getAffilitaedTo());
            journalsConfigurationVO.setJournalBibliography(journal.getJournalMetadata().getBibliography());
            journalsConfigurationVO.setChargeOnCountry(journal.getJournalMetadata().getChargeOnCountry());
            journalsConfigurationVO.setJournalEmail(journal.getJournalMetadata().getJournalEmail());
            journalsConfigurationVO.setJournalEmailPassword(journal.getJournalMetadata().getJournalEmailPassword());
            journalsConfigurationVO.setJournalLanguage(journal.getJournalMetadata().getJournalLanguage());
            journalsConfigurationVO.setJournalAlias(journal.getJournalMetadata().getJournalAlias());

            journalsConfigurationVO.setJournalCopernicusIndex(journal.getJournalMetadata().getJournalIndexCopernicus());
            journalsConfigurationVO.setJournalDoi(journal.getJournalMetadata().getJournalDOI());
            journalsConfigurationVO.setJournalEissn(journal.getJournalMetadata().getJournalEissn());
            journalsConfigurationVO.setJournalFrequency(journal.getJournalMetadata().getJournalFrequency());
            journalsConfigurationVO.setCopyrightContent(journal.getJournalMetadata().getCopyrightContent());
            String issn = journal.getJournalMetadata().getJournalIssn();
            JournalDataIssn dataIssn= journalDataIssnRepo.getIssnUrl(issn);
            if (dataIssn != null) {
                journalsConfigurationVO.setAbstractingAndIndexing(dataIssn.getInfo());
            } else {
                journalsConfigurationVO.setAbstractingAndIndexing("");
            }
            journalsConfigurationVO.setSubmissionMessage(journal.getJournalMetadata().getSubmissionMessage());
            journalsConfigurationVO.setJournalIssn(journal.getJournalMetadata().getJournalIssn());
            journalsConfigurationVO.setJournalOpenAccess(journal.getJournalMetadata().getOpenAccess());
            journalsConfigurationVO.setJournalReadership(journal.getJournalMetadata().getJournalReadership());
            journalsConfigurationVO.setJournalSubCategory(journal.getJournalMetadata().getJournalSubCategory());
            journalsConfigurationVO.setJournalWorkflowId(journal.getJournalWorkflowId());
            journalsConfigurationVO.setImpactFactor(journal.getJournalMetadata().getImpactFactor());
            journalsConfigurationVO.setAcceptanceRate(journal.getJournalMetadata().getAcceptanceRate());
            journalsConfigurationVO.setCiteScore(journal.getJournalMetadata().getCiteScore());
            journalsConfigurationVO.setPublicationType(journal.getJournalMetadata().getPublicationType());
            journalsConfigurationVO.setJournalOpenAccess(journal.getJournalMetadata().getOpenAccess());
            journalsConfigurationVO.setJournalEissn(journal.getJournalMetadata().getJournalEissn());
            journalsConfigurationVO.setJournalFrequency(journal.getJournalMetadata().getJournalFrequency());
            journalsConfigurationVO.setJournalAffilation(journal.getJournalMetadata().getAffilitaedTo());
            journalsConfigurationVO.setJournalBibliography(journal.getJournalMetadata().getBibliography());
            journalsConfigurationVO.setJournalDoi(journal.getJournalMetadata().getJournalDOI());
            journalsConfigurationVO.setJournalOpenAccess(journal.getJournalMetadata().getOpenAccess());
            journalsConfigurationVO.setJournalReadership(journal.getJournalMetadata().getJournalReadership());
            journalsConfigurationVO.setJournalSubject(journal.getJournalMetadata().getJournalSubject());
            journalsConfigurationVO.setJournalSpeciality(journal.getJournalMetadata().getJournalSpeciality());
            journalsConfigurationVO.setUrl(journal.getJournalMetadata().getUrl());
            journalsConfigurationVO.setWebsiteUrl(journal.getJournalMetadata().getWebsiteUrl());
            journalsConfigurationVO.setJournalMode(journal.getJournalMetadata().getJournalMode());
            journalsConfigurationVO.setCurrentIssueLink(journal.getJournalMetadata().getCurrentIssueLink());
            journalsConfigurationVO.setAuthorInstructionsLink(journal.getJournalMetadata().getAuthorInstructionsLink());
            journalsConfigurationVO.setAboutLink(journal.getJournalMetadata().getAboutLink());
            journalsConfigurationVO.setThumbnailFilePath(journal.getJournalMetadata().getThumbnailPath());
            journalsConfigurationVO.setPublonsCode(journal.getJournalMetadata().getPublonsCode());
            journalsConfigurationVO.setEicNameSignature(journal.getJournalMetadata().getEicNameSignature());
            journalsConfigurationVO.setEicSignature(journal.getJournalMetadata().getEicSignature());
            if (journal.getJournalMetadata().getJournalSubject() != null
                    && !journal.getJournalMetadata().getJournalSubject().isEmpty()) {
                String Subject[] = journal.getJournalMetadata().getJournalSubject().split("@");
                if (Subject != null && Subject.length > 0) {
                    List<Integer> subjectId = new ArrayList<>();
                    for (String data : Subject) {
                        subjectId.add(Integer.parseInt(data));
                    }
                    subject = subjectRepo.getSubjectListByIdList(subjectId);
                }
            }
            journalsConfigurationVO.setSubject(subject);
            // journalsConfigurationVO.setJournalSubject(journal.getJournalMetadata().getJournalSubject());
            if (journal.getJournalMetadata().getJournalSpeciality() != null
                    && !journal.getJournalMetadata().getJournalSpeciality().isEmpty()) {
                String Speciality[] = journal.getJournalMetadata().getJournalSpeciality().split("@");
                if (Speciality != null && Speciality.length > 0) {
                    List<Integer> specialityId = new ArrayList<>();
                    for (String data : Speciality) {
                        specialityId.add(Integer.parseInt(data));
                    }
                    specialization = specilizationRepo.getSpecializationByListOfId(specialityId);
                }
            }
            journalsConfigurationVO.setSpeciality(specialization);
            // journalsConfigurationVO.setJournalSpeciality(journal.getJournalMetadata().getJournalSpeciality());

        }

        JournalChargeDetails charge = journalChargeDetailsRepo.getJournalChargeDetailsByJournal(journal.getJournalId());
        if (charge != null) {
            // charge.get
            journalsConfigurationVO.setAdditionalCharges(charge.getAdditionalCharges());
            journalsConfigurationVO.setBlackWhiteCharges(charge.getBlackWhiteCharges());
            journalsConfigurationVO.setColorCharges(charge.getColorCharges());
            journalsConfigurationVO.setProductionCharges(charge.getProductionCharges());
            journalsConfigurationVO.setTableCharges(charge.getTableCharges());
            journalsConfigurationVO.setTypeSettingCharges(charge.getTypeSettingCharges());
            journalsConfigurationVO.setFixedCharges(charge.getFixedCharges());
            journalsConfigurationVO.setFixedChargeAmount(charge.getFixedChargeAmount());

            journalsConfigurationVO.setCountryChargeOnPublication(charge.getCountryChargeOnPublication());
            journalsConfigurationVO.setCountryChargeOnSubmission(charge.getCountryChargeOnSubmission());
            journalsConfigurationVO.setChargeOnPublication(charge.getChargeOnPublication());
            journalsConfigurationVO.setChargeOnSubmission(charge.getChargeOnSubmission());
            journalsConfigurationVO.setPublicationCharge(charge.getPublicationCharge());
            journalsConfigurationVO.setSubmissionCharge(charge.getSubmissionCharge());
            journalsConfigurationVO.setCurrencyOnPublication(charge.getCurrencyOnPublication());
            journalsConfigurationVO.setCurrencyOnSubmission(charge.getChargeOnSubmission());
            journalsConfigurationVO.setIsChargesRequired(charge.getIsChargesRequired());

        }

        List<JournalManuscriptTypeCharge> getJournalManuscriptTypeChargeList = journalManuscriptTypeChargeRepo
                .getJournalManuscriptTypeChargeList(journal.getJournalId());
        if (getJournalManuscriptTypeChargeList != null && !getJournalManuscriptTypeChargeList.isEmpty()) {
            List<JournalManuscriptTypeChargeVo> getJournalManuscriptTypeChargeVo = new ArrayList<>();
            getJournalManuscriptTypeChargeList.stream().forEach(i -> {
                JournalManuscriptTypeChargeVo addJournalManuscriptTypeChargeVo = new JournalManuscriptTypeChargeVo();
                addJournalManuscriptTypeChargeVo.setId(i.getId());
                addJournalManuscriptTypeChargeVo.setAdditionalCharges(i.getAdditionalCharges());
                addJournalManuscriptTypeChargeVo.setManuscriptTypeId(i.getManuscriptTypeId());
                addJournalManuscriptTypeChargeVo.setChargeAmount(i.getChargeAmount());
                addJournalManuscriptTypeChargeVo.setColorFigureCharges(i.getColorFigureCharges());
                addJournalManuscriptTypeChargeVo.setCurrency(i.getCurrency());
                addJournalManuscriptTypeChargeVo.setFigureCharges(i.getFigureCharges());
                addJournalManuscriptTypeChargeVo.setFixedChargeAmount(i.getFixedChargeAmount());
                addJournalManuscriptTypeChargeVo.setIsActive(i.getIsActive());
                addJournalManuscriptTypeChargeVo.setJournalId(i.getJournalId());
                addJournalManuscriptTypeChargeVo.setCountryChargeOnPublication(i.getCountryChargeOnPublication());
                addJournalManuscriptTypeChargeVo.setCountryChargeOnSubmission(i.getCountryChargeOnSubmission());
                addJournalManuscriptTypeChargeVo.setManuscriptTypeId(i.getManuscriptTypeId());
                addJournalManuscriptTypeChargeVo.setTablesCharges(i.getTablesCharges());
                addJournalManuscriptTypeChargeVo.setTypeSettingCharges(i.getTypeSettingCharges());
                //saddJournalManuscriptTypeChargeVo.setManuscriptTypeName(i.getManuscriptType().get);
                addJournalManuscriptTypeChargeVo.setChargeOnPublication(i.getChargeOnPublication());
                addJournalManuscriptTypeChargeVo.setChargeOnSubmission(i.getChargeOnSubmission());
                addJournalManuscriptTypeChargeVo.setPublicationCharge(i.getPublicationCharge());
                addJournalManuscriptTypeChargeVo.setSubmissionCharge(i.getSubmissionCharge());
                addJournalManuscriptTypeChargeVo.setCurrencyOnPublication(i.getCurrencyOnPublication());
                addJournalManuscriptTypeChargeVo.setCurrencyOnSubmission(i.getChargeOnSubmission());
                addJournalManuscriptTypeChargeVo.setIsChargesRequired(i.getIsChargesRequired());
                String paymentCountriesOnSubmission = "";
                String paymentCountriesOnPublishing = "";

                DevelopedCountriesByManuscriptType developedCountriesByManuscriptType =
                        developedCountriesByManuscriptTypeRepo.getByJournalAndManuscriptType(i.getJournalId(), i.getManuscriptTypeId());

                if (i.getCountryChargeOnPublication() != null
                        && !i.getCountryChargeOnPublication().isEmpty()
                        && i.getCountryChargeOnPublication().equals("FromDevelopingCountry")) {

                    if (developedCountriesByManuscriptType != null && !developedCountriesByManuscriptType.getPaymentCountriesOnPublishing().isEmpty()) {
                        addJournalManuscriptTypeChargeVo.setPaymentCountriesOnPublishing(developedCountriesByManuscriptType.getPaymentCountriesOnPublishing());
                    } else {
                        addJournalManuscriptTypeChargeVo.setPaymentCountriesOnPublishing("India,Others");
                    }
                } else {
                    addJournalManuscriptTypeChargeVo.setPaymentCountriesOnPublishing("India,Others");
                }

                if (i.getCountryChargeOnSubmission() != null
                        && !i.getCountryChargeOnSubmission().isEmpty()
                        && i.getCountryChargeOnSubmission().equals("FromDevelopingCountry")) {
                    if (developedCountriesByManuscriptType != null
                            && !developedCountriesByManuscriptType.getPaymentCountriesOnSubmission().isEmpty()) {
                        addJournalManuscriptTypeChargeVo.setPaymentCountriesOnSubmission(developedCountriesByManuscriptType.getPaymentCountriesOnSubmission());
                    } else {
                        addJournalManuscriptTypeChargeVo.setPaymentCountriesOnSubmission("India,Others");
                    }
                } else {
                    addJournalManuscriptTypeChargeVo.setPaymentCountriesOnSubmission("India,Others");
                }


                getJournalManuscriptTypeChargeVo.add(addJournalManuscriptTypeChargeVo);
            });
            journalsConfigurationVO.setManusriptTypeCharges(getJournalManuscriptTypeChargeVo);
        }

        return journalsConfigurationVO;
    }

    @Override
    public Integer configureAndSaveJournal(JournalsConfigurationVO journalConfigurationVO) {
        // Save journal details
        Journal journal = mapJournalConfigurationVOPropertiesToJournal(journalConfigurationVO);

        // Save manuscript type for journal
        if (!"manuscript_type".equalsIgnoreCase(journalConfigurationVO.getChargeOn())) {
            List<ManuscriptTypeJournalVO> list = journalConfigurationVO.getManuscriptTypeJournals();

            list.stream().forEach(obj -> {
                if (journalConfigurationVO.getTrialArray() != null && journalConfigurationVO.getTrialArray().contains(obj.getManutypeName()))
                    obj.setTrialNumberMandatory("Y");
                else
                    obj.setTrialNumberMandatory("N");
            });

            int jid = journal.getJournalId();
            // Delete existing data in a batch
            manuscriptTypeJournalsRepo.deleteByJournalId(jid);

            if (list != null) {
                List<ManuscriptTypeJournal> manuscriptTypeJournalList = ManuscriptTypeJournalTransformationUtils
                        .mapVOListToModelList(list);
                for (int i = 0; i < manuscriptTypeJournalList.size(); i++) {
                    ManuscriptTypeJournal manuscriptTypeJournal = manuscriptTypeJournalList.get(i);

                    if (manuscriptTypeJournal != null) {
                        try {
                            manuscriptTypeJournal.setJournalId(jid);
                            ManuscriptTypeJournal mtj = manuscriptTypeJournalsRepo.save(manuscriptTypeJournal);
                            log.info("Saved ManuscriptTypeJournal with ID: " + mtj.getMtjId());
                        } catch (Exception e) {
                            log.error("Error occurred while saving ManuscriptTypeJournal at index " + i, e);
                        }
                    } else {
                        log.warn("Skipping null ManuscriptTypeJournal at index " + i);
                    }
                }

            }
        }
        return journal.getJournalId();
    }

    public Journal mapJournalConfigurationVOPropertiesToJournal(JournalsConfigurationVO journalsConfigurationVO) {
        Journal journal;
        if (journalsConfigurationVO.getJournalId() != null && journalsConfigurationVO.getJournalId() > 0) {
            journal = journalRepo.getOne(journalsConfigurationVO.getJournalId());
            if (journal.getJournalManuscriptCount() != null) {
                if (journalsConfigurationVO.getJournalManuscriptCount() != null) {
                    if (journalsConfigurationVO.getJournalManuscriptCount() > journal.getJournalManuscriptCount()) {
                        journal.setJournalManuscriptCount(journalsConfigurationVO.getJournalManuscriptCount());
                    }
                }
            }
        } else {
            journal = new Journal();
            journal.setIsActive(1);
            if (journalsConfigurationVO.getJournalManuscriptCount() != null)
                journal.setJournalManuscriptCount(journalsConfigurationVO.getJournalManuscriptCount());
            else
                journal.setJournalManuscriptCount(0);
        }

        if (journalsConfigurationVO.getJournalId() != null)
            journal.setJournalId(journalsConfigurationVO.getJournalId());

        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        journal.setPublisherId(journalsConfigurationVO.getPublisherId());
        journal.setJournalCode(journalsConfigurationVO.getJournalCode());
        journal.setCountryName(journalsConfigurationVO.getCountryName());
        journal.setJournalTitle(journalsConfigurationVO.getJournalTitle());
//        journal.setChargeFlag(journalsConfigurationVO.getChargeFlag().isEmpty() ? "No" : journalsConfigurationVO.getChargeFlag());
        journal.setJournalLogo(journalsConfigurationVO.getJournalLogo());
        journal.setJournalShortTitle(journalsConfigurationVO.getJournalShortTitle());
        journal.setPubCode(journalsConfigurationVO.getPubCode());
        journal.setJournalManuscriptYear(calendar.get(Calendar.YEAR) % 100);
        if (journalsConfigurationVO.getCreatedBy() != null && !journalsConfigurationVO.getCreatedBy().isEmpty()) {
            journal.setCreatedBy(Integer.parseInt(journalsConfigurationVO.getCreatedBy()));
        }

        Journal savedJournal = journalRepo.save(journal);

        return savedJournal;

    }

    @Override
    public void saveJournalMetadata(JournalsConfigurationVO journalsConfigurationVO, Integer journalId) {

        JournalMetadata journalMetadata = journalMetadataRepo.metaDatalistByJournalId(journalId);
        String setSbject = "";
        String setSpeciality = "";
        if (journalMetadata == null)
            journalMetadata = new JournalMetadata();
        journalMetadata.setJournalId(journalId);
        journalMetadata.setColourFigures(journalsConfigurationVO.getColourFigure());
        journalMetadata.setJournalEmail(journalsConfigurationVO.getJournalEmail());
        journalMetadata.setJournalEmailPassword(journalsConfigurationVO.getJournalEmailPassword());
        journalMetadata.setIsSubmissionRequired(journalsConfigurationVO.getIsSubmissionRequired());
        journalMetadata.setJournalLanguage(journalsConfigurationVO.getJournalLanguage());
        journalMetadata.setJournalType(journalsConfigurationVO.getJournalType());
        journalMetadata.setJournalAlias(journalsConfigurationVO.getJournalAlias());
        journalMetadata.setIsChargingRequired(journalsConfigurationVO.getJournalChargeFlag());
        journalMetadata.setJournalDOI(journalsConfigurationVO.getJournalDoi());
        journalMetadata.setJournalEissn(journalsConfigurationVO.getJournalEissn());
        journalMetadata.setJournalIssn(journalsConfigurationVO.getJournalIssn());
        journalMetadata.setAffilitaedTo(journalsConfigurationVO.getJournalAffilation());
        journalMetadata.setBibliography(journalsConfigurationVO.getJournalBibliography());
        journalMetadata.setWebsiteUrl(journalsConfigurationVO.getWebsiteUrl());
        journalMetadata.setJournalIndexCopernicus(journalsConfigurationVO.getJournalCopernicusIndex());
        journalMetadata.setCopyrightContent(journalsConfigurationVO.getCopyrightContent());
        journalMetadata.setSubmissionMessage(journalsConfigurationVO.getSubmissionMessage());
        journalMetadata.setJournalReadership(journalsConfigurationVO.getJournalReadership());
        journalMetadata.setJournalSubCategory(journalsConfigurationVO.getJournalReadership());
        journalMetadata.setCiteScore(journalsConfigurationVO.getCiteScore());
        journalMetadata.setAcceptanceRate(journalsConfigurationVO.getAcceptanceRate());
        journalMetadata.setImpactFactor(journalsConfigurationVO.getImpactFactor());
        journalMetadata.setJournalEissn(journalsConfigurationVO.getJournalEissn());
        journalMetadata.setJournalFrequency(journalsConfigurationVO.getJournalFrequency());
        journalMetadata.setAffilitaedTo(journalsConfigurationVO.getJournalAffilation());
        journalMetadata.setBibliography(journalsConfigurationVO.getJournalBibliography());
        journalMetadata.setOpenAccess(journalsConfigurationVO.getJournalOpenAccess());
        journalMetadata.setJournalMode(journalsConfigurationVO.getJournalMode());
        journalMetadata.setChargeOnCountry(journalsConfigurationVO.getChargeOnCountry());
        journalMetadata.setCurrentIssueLink(journalsConfigurationVO.getCurrentIssueLink());
        journalMetadata.setAuthorInstructionsLink(journalsConfigurationVO.getAuthorInstructionsLink());
        journalMetadata.setAboutLink(journalsConfigurationVO.getAboutLink());
        journalMetadata.setThumbnailPath(journalsConfigurationVO.getThumbnailFilePath());
        journalMetadata.setPublonsCode(journalsConfigurationVO.getPublonsCode());
        journalMetadata.setEicNameSignature(journalsConfigurationVO.getEicNameSignature());
        journalMetadata.setEicSignature(journalsConfigurationVO.getEicSignature());

        //This is Temp code delete this whene comple this code
        if (journalsConfigurationVO.getPublicationType() != null
                && !journalsConfigurationVO.getPublicationType().equals("")) {
            journalMetadata.setPublicationType(journalsConfigurationVO.getPublicationType());
        } else {
            journalMetadata.setPublicationType("Subscription");
        }

        journalMetadata.setUrl(journalsConfigurationVO.getUrl());
        if (journalsConfigurationVO.getWorkflowId() != null) {
            journalMetadata.setWorkflowId(journalsConfigurationVO.getWorkflowId());
        }
        if (journalsConfigurationVO.getSubject() != null) {
            for (int i = 0; i <= journalsConfigurationVO.getSubject().size() - 1; i++) {
                setSbject = setSbject.concat(journalsConfigurationVO.getSubject().get(i).getId().toString())
                        .concat("@");
            }
            journalMetadata.setJournalSubject(setSbject);
        }
        if (journalsConfigurationVO.getSpeciality() != null) {
            for (int i = 0; i <= journalsConfigurationVO.getSpeciality().size() - 1; i++) {
                setSpeciality = setSpeciality.concat(journalsConfigurationVO.getSpeciality().get(i).getId().toString())
                        .concat("@");
            }
            journalMetadata.setJournalSpeciality(setSpeciality);
        }
        if (journalsConfigurationVO.getChargeFlag() != null) {
            journalMetadata.setIsChargingRequired(journalsConfigurationVO.getChargeFlag());
            if (journalsConfigurationVO.getChargeOn() != null && !journalsConfigurationVO.getChargeOn().isEmpty())
                journalMetadata.setChargeOn(journalsConfigurationVO.getChargeOn());
        } else {
            journalMetadata.setIsChargingRequired("no");
        }
        
        try {
            String issn = journalMetadata.getJournalIssn();
            JournalDataIssn dataIssn = journalDataIssnRepo.getIssnUrl(issn);

            if (dataIssn != null) {
                log.info("update Info for existing url: {}", dataIssn.getJournalIssnUrl());
                dataIssn.setInfo(journalsConfigurationVO.getAbstractingAndIndexing());
                journalDataIssnRepo.save(dataIssn);
            } else {
                String newUrl = "https://www.medknow.com/__bibtxt.asp?issn=" + issn;
                log.info("insert Info for new url: {}", newUrl);
                JournalDataIssn newEntry = new JournalDataIssn();
                newEntry.setJournalIssnUrl(newUrl);
                newEntry.setInfo(journalsConfigurationVO.getAbstractingAndIndexing());
                journalDataIssnRepo.save(newEntry);
            }
        } catch (Exception e) {
            log.error("Failed to save/update journal_data_issn for ISSN: {}. Error: {}", 
                      journalMetadata.getJournalIssn(), e.getMessage(), e);
        }


        journalMetadataRepo.save(journalMetadata);
    }

    @Override
    public void saveJournalChargeDetails(JournalsConfigurationVO journalsConfigurationVO, Integer journalId) {
        if (journalsConfigurationVO == null) {
            log.error("Invalid input: journalsConfigurationVO is null");
            return;
        }

        JournalChargeDetails charge = journalChargeDetailsRepo.getJournalChargeDetailsByJournal(journalId);

        if (charge == null) {
            charge = new JournalChargeDetails();
            charge.setJournalId(journalId);
        }

        charge.setFixedCharges(journalsConfigurationVO.getFixedCharges());

        if (YES.equalsIgnoreCase(journalsConfigurationVO.getFixedCharges())) {
            charge.setFixedChargeAmount(journalsConfigurationVO.getFixedChargeAmount());
        } else if (NO.equalsIgnoreCase(journalsConfigurationVO.getFixedCharges())) {
            charge.setTableCharges(journalsConfigurationVO.getTableCharges());
            charge.setBlackWhiteCharges(journalsConfigurationVO.getBlackWhiteCharges());
            charge.setColorCharges(journalsConfigurationVO.getColorCharges());
            charge.setAdditionalCharges(journalsConfigurationVO.getAdditionalCharges());
        } else {
            log.warn("Invalid value for FixedCharges: " + journalsConfigurationVO.getFixedCharges());
        }

        charge.setChargeOnPublication(journalsConfigurationVO.getChargeOnPublication());
        charge.setChargeOnSubmission(journalsConfigurationVO.getChargeOnSubmission());
        charge.setPublicationCharge(journalsConfigurationVO.getPublicationCharge());
        charge.setSubmissionCharge(journalsConfigurationVO.getSubmissionCharge());
        charge.setCurrencyOnPublication(journalsConfigurationVO.getCurrencyOnPublication());
        charge.setCurrencyOnSubmission(journalsConfigurationVO.getCurrencyOnSubmission());
        charge.setIsChargesRequired(journalsConfigurationVO.getIsChargesRequired());
        charge.setCurrency(journalsConfigurationVO.getCurrency());
        charge.setCountryChargeOnPublication(journalsConfigurationVO.getCountryChargeOnPublication());
        charge.setCountryChargeOnSubmission(journalsConfigurationVO.getCountryChargeOnSubmission());
        charge.setUpdatedBy(journalsConfigurationVO.getCreatedBy());

        try {
            journalChargeDetailsRepo.save(charge);
            log.info("Saved JournalChargeDetails for journalId: " + journalId);
        } catch (Exception e) {
            log.error("Error occurred while saving JournalChargeDetails for journalId: " + journalId, e);
        }
    }

    @Override
    public void saveJournalBlindType(Integer id, String blindType) {
        Journal journal = journalRepo.getOne(id);
        journalRepo.save(journal);
    }

    // Upload File
    @Override
    public List<JournalconfigFileUpload> getFileList(Integer journalId) {
        return journalconfigFileUploadRepo.getFileList(journalId);
    }

    @Override
    public String deleteJournalconfigFile(JournalVO journalVO) {
        JournalconfigFileUpload fileDetails = journalconfigFileUploadRepo.getOne(journalVO.getId());
        String finalPath = UPLOAD_FOLDER + File.separator + "UniprrFileJournalConfigFile" + File.separator
                + "MNT_LWW_JOURNAL_" + fileDetails.getJournalId() + File.separator + fileDetails.getFileName();
        try {
            FileUtils.forceDelete(new File(finalPath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        journalconfigFileUploadRepo.deleteById(journalVO.getId());
        return "File Deleted ";
    }

    @Override
    public boolean isIdExist(Integer id) {
        return journalconfigFileUploadRepo.existsById(id);
    }

    @Override
    public JournalconfigFileUpload saveFile(JournalconfigFileUpload journalconfigFileUpload) {
        return journalconfigFileUploadRepo.save(journalconfigFileUpload);
    }

    @Override
    public List<JournalconfigMasterfileType> getFileTypeList() {
        return journalconfigMasterfileTypeRepo.findAll();
    }

    @Override
    public JournalconfigFileUpload JournalconfigFile(Integer id) {
        return journalconfigFileUploadRepo.JournalconfigFileByID(id);
    }

    // Email Template

    @Override
    public List<MailTemplates> getEmailList() {
        return mailTemplatesRepo.findAll();
    }

    @Override
    public String deleteEmail(Integer id) {
        mailTemplatesRepo.deleteById(id);
        return "Email Deleted";
    }

    @Override
    public boolean isEmailIdExist(Integer id) {
        return mailTemplatesRepo.existsById(id);
    }

    // KeyWords
    @Override
    public List<KeywordsVO> getJournalKeyWordList(Integer journalId) {
        List<Keywords> list = keywordsRepo.getKeywordsByJournalId(journalId);
        List<KeywordsVO> voList = KeywordsTransformationUtils.mapModelListToVOList(list);
        return voList;
    }

    @Override
    public void saveKeyWords(List<Keywords> keywords) {
        for (Keywords data : keywords) {
            keywordsRepo.save(data);
        }
    }

    @Override
    public List<Journal> getJournalsList() {
        return journalsRepo.findAll();
    }

    @Override
    public void deleteKeyword(Integer id) {
        keywordsRepo.deleteKeyword(id);
    }

    @Override
    public Keywords updateKeyword(Keywords keywords) {
        return keywordsRepo.save(keywords);
    }

    @Override
    public void updateKeywordList(List<KeywordsVO> voList) {
        List<Keywords> list = KeywordsTransformationUtils.mapVoListToModelList(voList);
        keywordsRepo.saveAll(list);
    }

    // AutoMailers
    @Override
    public List<JournalMailTemplates> getTemplate(Integer journalId) {
        return journalMailTemplatesRepo.getTemplate(journalId);
    }

    @Override
    public String saveList(List<JournalconfigFileUpload> journalconfigFileUpload) {
        journalconfigFileUpload.stream().forEach(i -> {
            journalconfigFileUploadRepo.updateJournalconfigFileUpload(i.getId(), i.getFileTypeId());
        });
        return "file successfully saved";
    }

    // Dev Here New
    @Override
    public List<JournalPlagiarismMasterVO> getjournalPlagiarismlist(Integer journalId) {
        List<JournalPlagiarismMaster> journalPlagiarismList = journalPlagiarismMasterRepo
                .getjournalPlagiarismlist(journalId);
        if (journalPlagiarismList != null && !journalPlagiarismList.isEmpty()) {
            List<JournalPlagiarismMasterVO> jVo = new ArrayList<JournalPlagiarismMasterVO>();
            journalPlagiarismList.stream().forEach(i -> {
                JournalPlagiarismMasterVO jour = new JournalPlagiarismMasterVO();
                jour.setPlaMid(i.getId());
                jour.setPlaId(i.getId());
                jour.setProcessName(i.getProcessName());
                jour.setJournalId(journalId);
                jour.setProcessDescription(i.getProcessDescription());
                JournalPlagirism journalPlagiarism = journalPlagirismRepo.getjournalPlagiarismlist(i.getJournalId(),
                        i.getId());
                if (journalPlagiarism != null) {

                    jour.setAcceptedSubmission(journalPlagiarism.getAcceptedSubmission());
                    jour.setOrginalSubmission(journalPlagiarism.getOrginalSubmission());
                    jour.setResubmissionSubmission(journalPlagiarism.getResubmissionSubmission());
                    jour.setRevisedSubmission(journalPlagiarism.getRevisedSubmission());
                } else {
                    try {
                        JournalPlagirism createJournalPlagirism = new JournalPlagirism();
                        createJournalPlagirism.setPlaId(i.getId());
                        createJournalPlagirism.setJournalId(i.getJournalId());
                        createJournalPlagirism.setAcceptedSubmission(0);
                        createJournalPlagirism.setOrginalSubmission(0);
                        createJournalPlagirism.setResubmissionSubmission(0);
                        createJournalPlagirism.setRevisedSubmission(0);
                        JournalPlagirism jp = journalPlagirismRepo.save(createJournalPlagirism);
                        jour.setAcceptedSubmission(jp.getAcceptedSubmission());
                        jour.setOrginalSubmission(jp.getOrginalSubmission());
                        jour.setResubmissionSubmission(jp.getResubmissionSubmission());
                        jour.setRevisedSubmission(jp.getRevisedSubmission());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                jVo.add(jour);
            });
            return jVo;
        } else {
            List<MasterAttributes> masetPlagrismlis = masterAttributesRepo.getCheckList(PLAGARISM_CHECKLIST);
            List<JournalPlagiarismMasterVO> jVo = new ArrayList<JournalPlagiarismMasterVO>();
            for (MasterAttributes masterAttributes : masetPlagrismlis) {
                JournalPlagiarismMasterVO jour = new JournalPlagiarismMasterVO();
                jour.setProcessName(masterAttributes.getChkKey());
                jour.setJournalId(journalId);
                jour.setProcessDescription(masterAttributes.getChkDesc());
                jVo.add(jour);
            }
            return jVo;
        }
    }

    @Override
    public void updatePlagiarism(List<JournalPlagiarismUpdateVO> journalPlagiarismUpdateVO) {
        journalPlagiarismUpdateVO.stream().forEach(i -> {
            if (i.getPlaId() != null) {
                journalPlagirismRepo.updatePlagiarism(i.getJournalId(), i.getPlaId(), i.getOrginalSubmission(),
                        i.getRevisedSubmission(), i.getResubmissionSubmission(), i.getAcceptedSubmission());
            } else {
                JournalPlagiarismMaster master = new JournalPlagiarismMaster();
                master.setJournalId(i.getJournalId());
                master.setProcessName(i.getProcessName());
                if (i.getProcessDescription() != null)
                    master.setProcessDescription(i.getProcessDescription());
                else
                    master.setProcessDescription("");
                master.setPublisherId(1);
                JournalPlagiarismMaster plaId = journalPlagiarismMasterRepo.save(master);
                JournalPlagirism journalPlagrism = new JournalPlagirism();
                journalPlagrism.setJournalId(i.getJournalId());
                journalPlagrism.setPlaId(plaId.getId());
                journalPlagrism.setRoleId(4);
                journalPlagrism.setOrginalSubmission(i.getOrginalSubmission());
                journalPlagrism.setRevisedSubmission(i.getRevisedSubmission());
                journalPlagrism.setResubmissionSubmission(i.getResubmissionSubmission());
                journalPlagrism.setAcceptedSubmission(i.getAcceptedSubmission());
                journalPlagrism.setCreatedBy("1");
                journalPlagirismRepo.save(journalPlagrism);
            }

        });
    }

    @Override
    public void assignAdmin(AssignAdminVO assignAdminVO) {
        if (assignAdminVO.getRole() == 2) {
            log.debug("2 " + assignAdminVO.getRole());
            List<User> userList = userRepo.getListByjournaId(assignAdminVO.getJournalId(), assignAdminVO.getRole());
            List<Integer> userIdData = new ArrayList();
            if (userList != null && !userList.isEmpty()) {
                for (User userId : userList) {
                    userIdData.add(userId.getId());
                }
            }
            if (userIdData != null && !userIdData.isEmpty()) {
                log.debug(assignAdminVO.getJournalId() + "--->>>>" + userIdData);
                updateData(assignAdminVO.getJournalId(), userIdData);
            }


            //userRepo.deleteExistingManagingEditorsForJournal(assignAdminVO.getJournalId());
        }
        if (assignAdminVO.getRole() == 3) {
            log.debug("3 " + assignAdminVO.getRole());
            List<User> userList = userRepo.getListByjournaId(assignAdminVO.getJournalId(), assignAdminVO.getRole());
            List<Integer> userIdData = new ArrayList();
            if (userList != null && !userList.isEmpty()) {
                for (User userId : userList) {
                    userIdData.add(userId.getId());
                }
            }
            if (userIdData != null && !userIdData.isEmpty()) {
                log.debug(assignAdminVO.getJournalId() + "--->>>>" + userIdData);
                updateData(assignAdminVO.getJournalId(), userIdData);
            }
        }
        for (Integer userId : assignAdminVO.getUserId()) {
            UserJournal userJournalData = userJournalRepo.getUserJournalByJournalEntry(userId,
                    assignAdminVO.getJournalId(), assignAdminVO.getRole());
            if (userJournalData != null) {
                UserJournal userJournal = userJournalData;
                userJournal.setIsActive(1);
                userJournalRepo.save(userJournal);
            } else {
                UserJournal userJournal = new UserJournal();
                userJournal.setUserId(userId);
                userJournal.setIsActive(1);
                userJournal.setJournalId(assignAdminVO.getJournalId());
                userJournalRepo.save(userJournal);
            }

        }
    }

    @Override
    public List<TurnAroundTimeVO> getjournalTatConfigurationList(TurnAroundTimeVO turnAroundTimeVO) {
        List<JournalTatConfiguration> journalTatList = journalTatConfigurationRepo
                .journalTatConfiguration(turnAroundTimeVO.getJournalId());
        List<TurnAroundTimeVO> tAVo = new ArrayList<>();

        if (journalTatList != null && !journalTatList.isEmpty()) {
            journalTatList.stream().forEach(i -> {
                TurnAroundTimeVO turnAround = new TurnAroundTimeVO();
                StatusMaster status = statusMasterRepo.getStatusMaster(i.getStatusId());
                turnAround.setStatusId(status.getId());
                turnAround.setStatusValue(status.getStatusValue());
                turnAround.setJournalTatConfigId(i.getId());
                turnAround.setJournalId(i.getJournalId());
                turnAround.setStatusId(i.getStatusId());
                turnAround.setTurnArroundTime(i.getTurnAroundTime());
                turnAround.setBuffered(i.getBufferedTime());
                turnAround.setReqAlert(Integer.toString(i.getRequiredAlert()));
                tAVo.add(turnAround);
            });
            return tAVo;
        }
        List<StatusMaster> statusMasterList = statusMasterRepo.getStatusMasterTatValue();
        statusMasterList.stream().forEach(i -> {
            TurnAroundTimeVO turnAround = new TurnAroundTimeVO();
            turnAround.setJournalTatConfigId(0);
            turnAround.setStatusId(i.getId());
            turnAround.setStatusValue(i.getStatusValue());
            turnAround.setBuffered(0);
            turnAround.setTurnArroundTime(0);
            turnAround.setReqAlert("0");
            turnAround.setJournalId(turnAroundTimeVO.getJournalId());
            tAVo.add(turnAround);
        });
        return tAVo;
    }

    @Override
    public String saveJournalTatConfiguration(JournalTatConfigurationVO journalTatConfigurationVO) {
        journalTatConfigurationVO.getTurnAroundTimeVO().stream().forEach(i -> {
            if (i.getJournalTatConfigId() != 0) {
                JournalTatConfiguration journal = new JournalTatConfiguration();
                journal.setId(i.getJournalTatConfigId());
                journal.setJournalId(i.getJournalId());
                journal.setStatusId(i.getStatusId());
                journal.setTurnAroundTime(i.getTurnArroundTime());
                journal.setBufferedTime(i.getBuffered());
                journal.setRequiredAlert(Integer.parseInt(i.getReqAlert()));
                journalTatConfigurationRepo.save(journal);

            } else if (i.getJournalTatConfigId() == 0) {
                JournalTatConfiguration journal = new JournalTatConfiguration();
                journal.setJournalId(i.getJournalId());
                journal.setStatusId(i.getStatusId());
                journal.setTurnAroundTime(i.getTurnArroundTime());
                journal.setBufferedTime(i.getBuffered());
                journal.setRequiredAlert(Integer.parseInt(i.getReqAlert()));
                journalTatConfigurationRepo.save(journal);
            }

        });
        return "Journal Tat Configuration Updated ";
    }

    @Override
    public List<EmailTemplateData> getEmailTemplateData() {
        return emailTemplateDataRepo.getEmailTemplateData(TEMPLATE_PLACEHOLDERS);
    }

    @Override
    public String saveJournalMailTemplate(JournalMailTemplatesVO journalMailTemplatesVO) {
        if (journalMailTemplatesVO.getJournalEmailTemplateId() == null) {
            JournalMailTemplates journalTemplate = new JournalMailTemplates();
            journalTemplate.setEmailTemplateName(journalMailTemplatesVO.getEmailTemplateName());
            journalTemplate.setEmailTemplateForm(journalMailTemplatesVO.getEmailTemplateForm());
            journalTemplate.setJournalId(journalMailTemplatesVO.getJournalId());
            journalTemplate.setTemplatePlaceholderId(journalMailTemplatesVO.getTemplatePlaceholderId());
            journalTemplate.setIsActive("yes");
            journalTemplate.setMailBcc(journalMailTemplatesVO.getMailBcc());
            journalTemplate.setMailBody(journalMailTemplatesVO.getMailBody());
            journalTemplate.setMailcc(journalMailTemplatesVO.getMailcc());
            journalTemplate.setMailSubject(journalMailTemplatesVO.getMailSubject());
            journalTemplate.setMailTo(journalMailTemplatesVO.getMailTo());
            journalTemplate.setMailFrom(journalMailTemplatesVO.getMailFrom());
            journalTemplate.setUserGroup(journalMailTemplatesVO.getUserGroup());
            journalMailTemplatesRepo.save(journalTemplate);
            return "Mail template save successfully";
        } else {
            JournalMailTemplates journalTemplate = new JournalMailTemplates();
            journalTemplate.setJournalEmailTemplateId(journalMailTemplatesVO.getJournalEmailTemplateId());
            journalTemplate.setEmailTemplateName(journalMailTemplatesVO.getEmailTemplateName());
            journalTemplate.setEmailTemplateForm(journalMailTemplatesVO.getEmailTemplateForm());
            journalTemplate.setJournalId(journalMailTemplatesVO.getJournalId());
            journalTemplate.setTemplatePlaceholderId(journalMailTemplatesVO.getTemplatePlaceholderId());
            journalTemplate.setIsActive("yes");
            journalTemplate.setMailBcc(journalMailTemplatesVO.getMailBcc());
            journalTemplate.setMailBody(journalMailTemplatesVO.getMailBody());
            journalTemplate.setMailcc(journalMailTemplatesVO.getMailcc());
            journalTemplate.setMailSubject(journalMailTemplatesVO.getMailSubject());
            journalTemplate.setMailTo(journalMailTemplatesVO.getMailTo());
            journalTemplate.setMailFrom(journalMailTemplatesVO.getMailFrom());
            journalTemplate.setUserGroup(journalMailTemplatesVO.getUserGroup());
            journalMailTemplatesRepo.save(journalTemplate);
            return "Mail template updated successfully";
        }

    }

    @Override
    public List<JournalMailTemplatesVO> getEmailTemplates(Integer journalId) {
        List<JournalMailTemplates> journalMail = journalMailTemplatesRepo.getTemplate(journalId);
        if (journalMail != null) {
            List<JournalMailTemplatesVO> journalArray = new ArrayList<>();
            Journal journal = journalsRepo.getJournalById(journalId);
            boolean isImpact = RawJDBC.isImpactJournalById(journal.getJournalCode());
            log.info("jornal is impact: "+isImpact);
            journalMail.stream().forEach(i -> {
                JournalMailTemplatesVO jVo = new JournalMailTemplatesVO();
                jVo.setJournalEmailTemplateId(i.getJournalEmailTemplateId());
                jVo.setEmailTemplateName(i.getEmailTemplateName());
                jVo.setTemplatePlaceholderId(i.getTemplatePlaceholderId());
                jVo.setJournalId(i.getJournalId());
                jVo.setEmailTemplateForm(i.getEmailTemplateForm());
                jVo.setIsActive(i.getIsActive());
                jVo.setMailBcc(i.getMailBcc());

                jVo.setMailBody(i.getMailBody());
                jVo.setMailcc(i.getMailcc());
                jVo.setMailSubject(i.getMailSubject());
                jVo.setMailTo(i.getMailTo());
                jVo.setMailFrom(i.getMailFrom());
                /*
                 * List<Role> roleName = roleRepo.getRoleByName(i.getUserGroup());
                 * jVo.setUserGroup(roleName.get(0).getRoleName());
                 */
                jVo.setCreatedOn(i.getCreatedOn());
                jVo.setUpdatedOn(i.getUpdatedOn());
                journalArray.add(jVo);
            });
            return journalArray;
        }
        return null;
    }

    @Override
    public List<TaskMaster> getTaskName() {
        return taskMasterRepo.getTaskNameForNotification();
    }

    @Override
    public String saveAutoMailers(AutoMailersVO autoMailersVO) {
        TaskEmailMap taskEmailMap = taskEmailMapRepo.getUniqueTaskEmailMapData(autoMailersVO.getJournalMailTemplateId(),
                autoMailersVO.getTaskMasterId(), autoMailersVO.getJournalId());
        if (taskEmailMap != null) {
            return "Choose the unique template name and task name";
        } else {
            if (autoMailersVO.getTaskEmailMapId() != null) {
                TaskEmailMap task = taskEmailMapRepo.getOne(autoMailersVO.getTaskEmailMapId());
                task.setTemId(autoMailersVO.getTaskEmailMapId());
                task.setJournalEmailTemplateId(autoMailersVO.getJournalMailTemplateId());
                task.setTaskId(autoMailersVO.getTaskMasterId());
                task.setJournalId(autoMailersVO.getJournalId());
                task.setUpdatedBy(autoMailersVO.getUpdatedBy());
                task.setUpdatedOn(new Date());
                taskEmailMapRepo.save(task);
                return "Auto mailer updated successfully";
            } else {
                TaskEmailMap task1 = new TaskEmailMap();
                task1.setJournalEmailTemplateId(autoMailersVO.getJournalMailTemplateId());
                task1.setTaskId(autoMailersVO.getTaskMasterId());
                task1.setJournalId(autoMailersVO.getJournalId());
                task1.setCreatedBy(autoMailersVO.getCreatedBy());
                taskEmailMapRepo.save(task1);
                return "Auto mailer save successfully";
            }
        }
    }

    @Override
    public List<AutoMailersVO> getAutoMailesList(AutoMailersVO autoMailersVO) {
        List<TaskEmailMap> taskEmailList = taskEmailMapRepo.getAutoMailesList(autoMailersVO.getJournalId());
        if (taskEmailList != null && !taskEmailList.isEmpty()) {
            List<AutoMailersVO> autoMailer = new ArrayList<>();
            taskEmailList.stream().forEach(i -> {
                AutoMailersVO auto = new AutoMailersVO();
                auto.setJournalId(i.getJournalId());
                auto.setTaskMasterId(i.getTaskId());
                auto.setJournalMailTemplateId(i.getJournalMailTemplates().getJournalEmailTemplateId());
                auto.setEmailTemplateName(i.getJournalMailTemplates().getEmailTemplateName());
                auto.setEmailTemplateBody(i.getJournalMailTemplates().getMailBody());
                auto.setTaskName(i.getTaskMaster().getTaskName());
                auto.setEmailTemplateForm(i.getJournalMailTemplates().getEmailTemplateForm());
                auto.setTaskEmailMapId(i.getTemId());
                autoMailer.add(auto);
            });
            return autoMailer;
        }
        return null;
    }

    @Override
    public String deleteAutoMailer(AutoMailersVO autoMailersVO) {
        taskEmailMapRepo.deleteById(autoMailersVO.getTaskEmailMapId());
        return "Deleted Successfully";
    }

    @Override
    public List<Integer> getAssignAdminIdList(Integer journalId) {
        List<User> list = userRepo.getAssignAdmin(journalId);
        List<Integer> userId = new ArrayList<Integer>();
        list.stream().forEach(x -> userId.add(x.getId()));
        return userId;
    }

    @Override
    public void deleteManuscriptTypeJournal(Integer id) {
        manuscriptTypeJournalsRepo.deleteById(id);
    }

    @Override
    public String downloadJournalFile(JournalVO journalVO) {
        JournalconfigFileUpload fileDetails = journalconfigFileUploadRepo.getOne(journalVO.getId());
        String filePath = fileDetails.getFilePath();
        return filePath;
    }

    @Override
    public String updateJournalWorkflow(Integer workflowId, Integer jornalId) {
        try {
            journalMetadataRepo.updateJournalWorkflow(workflowId, jornalId);
            return "Workflow Updated";
        } catch (Exception e) {
            log.error("Exception : " + e.getMessage());
            return "Something Went Wrong";
        }
    }

    @Override
    public Boolean isAutomailerExist(AutoMailersVO autoMailersVO) {
        TaskEmailMap getDetails = taskEmailMapRepo.getUniqueTaskEmailMapData(autoMailersVO.getJournalMailTemplateId(),
                autoMailersVO.getTaskMasterId(), autoMailersVO.getJournalId());
        if (getDetails != null) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isJournalMailTemplate(JournalMailTemplatesVO journalMailTemplatesVO) {
        JournalMailTemplates getJournalMailTemplates = journalMailTemplatesRepo.getUniqueJournalMailTemplates(
                journalMailTemplatesVO.getJournalId(), journalMailTemplatesVO.getEmailTemplateName());
        if (getJournalMailTemplates != null) {
            return true;
        }
        return false;
    }

    private void updateData(Integer journalId, List<Integer> userIdData) {
        log.debug(journalId + "--->>>" + userIdData);
        userJournalRepo.deactivateAssignUser(journalId, userIdData);
    }

    @Override
    public ResponseEntity<Resource> downloadJournalFileWithFileType(
            JournalVO journalVO) {

        log.info("=======================================================");
        log.info("Inside downloadJournalFileWithFileType()");
        log.info("Journal Id    : {}", journalVO.getJournalId());
        log.info("File Type Id  : {}", journalVO.getId());

        try {

            List<JournalconfigFileUpload> fileUploadList =
                    journalconfigFileUploadRepo
                            .downloadJournalFileWithFileType(
                                    journalVO.getJournalId(),
                                    journalVO.getId());

            if (fileUploadList == null || fileUploadList.isEmpty()) {

                log.warn(
                        "No file found. Journal Id : {}, File Type Id : {}",
                        journalVO.getJournalId(),
                        journalVO.getId());

                return ResponseEntity.notFound().build();
            }

            JournalconfigFileUpload fileUpload =
                    fileUploadList.get(0);

            String filePath = fileUpload.getFilePath();

            log.info("File path received from DB : {}", filePath);

            if (filePath == null || filePath.trim().isEmpty()) {

                log.warn("File path is empty.");

                return ResponseEntity.notFound().build();
            }

            String actualFilePath =
                    filePath.replace(baseUrl, uniprrFolderPath);

            File file = new File(actualFilePath);

            log.info("Actual file path : {}",
                    file.getAbsolutePath());

            if (!file.exists()) {

                log.error(
                        "File not found : {}",
                        file.getAbsolutePath());

                return ResponseEntity.notFound().build();
            }

            if (file.length() == 0) {

                log.error(
                        "File exists but is empty : {}",
                        file.getAbsolutePath());

                return ResponseEntity.notFound().build();
            }

            InputStreamResource resource =
                    new InputStreamResource(
                            new FileInputStream(file));

            String fileName = file.getName();

            log.info("File Name : {}", fileName);
            log.info("File Size : {}", file.length());

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .contentLength(file.length())
                    .header(
                            HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + fileName + "\"")
                    .header(
                            HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS,
                            HttpHeaders.CONTENT_DISPOSITION)
                    .header(
                            HttpHeaders.CACHE_CONTROL,
                            "no-cache, no-store, must-revalidate")
                    .header(
                            HttpHeaders.PRAGMA,
                            "no-cache")
                    .header(
                            HttpHeaders.EXPIRES,
                            "0")
                    .body(resource);

        } catch (Exception e) {

            log.error(
                    "Exception while downloading journal file. "
                            + "Journal Id : {}, File Type Id : {}",
                    journalVO.getJournalId(),
                    journalVO.getId(),
                    e);

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }

    @Override
    public List<JournalMailTemplatesVO> getEmailTemplatesForAutoMailers(IDVO id) {
        List<JournalMailTemplates> journalMail = journalMailTemplatesRepo
                .getUniqueJournalMailTemplatesList(id.getJournalId(), id.getEmailTemplateForm());
        if (journalMail != null) {
            List<JournalMailTemplatesVO> journalArray = new ArrayList<>();
            journalMail.stream().forEach(i -> {
                JournalMailTemplatesVO jVo = new JournalMailTemplatesVO();
                jVo.setJournalEmailTemplateId(i.getJournalEmailTemplateId());
                jVo.setEmailTemplateName(i.getEmailTemplateName());
                jVo.setTemplatePlaceholderId(i.getTemplatePlaceholderId());
                jVo.setJournalId(i.getJournalId());
                jVo.setEmailTemplateForm(i.getEmailTemplateForm());
                jVo.setIsActive(i.getIsActive());
                jVo.setMailBcc(i.getMailBcc());
                jVo.setMailBody(i.getMailBody());
                jVo.setMailcc(i.getMailcc());
                jVo.setMailSubject(i.getMailSubject());
                jVo.setMailTo(i.getMailTo());
                jVo.setMailFrom(i.getMailFrom());
                jVo.setCreatedOn(i.getCreatedOn());
                jVo.setUpdatedOn(i.getUpdatedOn());
                journalArray.add(jVo);
            });
            return journalArray;
        }
        return null;
    }

    @Override
    public List<TaskMaster> getTaskNameForReminder() {
        return taskMasterRepo.getTaskNameForReminder();
    }

    @Override
    public boolean isAutomailerForReminderExist(AutoMailersVO autoMailersVO) {
        TaskEmailMapReminder getDetails = taskEmailMapReminderRepo
                .getTaskEmailMapDetails(autoMailersVO.getTaskMasterId(), autoMailersVO.getJournalId());
        if (getDetails != null) {
            return true;
        }
        return false;
    }

    @Override
    public String saveAutoMailersForReminder(AutoMailersVO autoMailersVO) {
        TaskEmailMapReminder taskEmailMap = taskEmailMapReminderRepo.getUniqueTaskEmailMapData(
                autoMailersVO.getJournalMailTemplateId(), autoMailersVO.getTaskMasterId(),
                autoMailersVO.getJournalId());
        if (taskEmailMap != null) {
            return "Choose the unique template name and task name";
        } else {
            if (autoMailersVO.getTaskEmailMapId() != null) {
                TaskEmailMapReminder task = taskEmailMapReminderRepo.getOne(autoMailersVO.getTaskEmailMapId());
                task.setTemId(autoMailersVO.getTaskEmailMapId());
                task.setJournalEmailTemplateId(autoMailersVO.getJournalMailTemplateId());
                task.setTaskId(autoMailersVO.getTaskMasterId());
                task.setJournalId(autoMailersVO.getJournalId());
                task.setUpdatedBy(autoMailersVO.getUpdatedBy());
                task.setUpdatedOn(new Date());
                taskEmailMapReminderRepo.save(task);
                return "Auto mailer updated successfully";
            } else {
                TaskEmailMapReminder task1 = new TaskEmailMapReminder();
                task1.setJournalEmailTemplateId(autoMailersVO.getJournalMailTemplateId());
                task1.setTaskId(autoMailersVO.getTaskMasterId());
                task1.setJournalId(autoMailersVO.getJournalId());
                task1.setCreatedBy(autoMailersVO.getCreatedBy());
                taskEmailMapReminderRepo.save(task1);
                return "Auto mailer save successfully";
            }
        }
    }

    @Override
    public List<AutoMailersVO> getAutoMailersListForReminder(AutoMailersVO autoMailersVO) {
        List<TaskEmailMapReminder> taskEmailList = taskEmailMapReminderRepo
                .getAutoMailesList(autoMailersVO.getJournalId());
        if (taskEmailList != null && !taskEmailList.isEmpty()) {
            List<AutoMailersVO> autoMailer = new ArrayList<>();
            taskEmailList.stream().forEach(i -> {
                AutoMailersVO auto = new AutoMailersVO();
                auto.setJournalId(i.getJournalId());
                auto.setTaskMasterId(i.getTaskId());
                auto.setJournalMailTemplateId(i.getJournalMailTemplates().getJournalEmailTemplateId());
                auto.setEmailTemplateName(i.getJournalMailTemplates().getEmailTemplateName());
                auto.setEmailTemplateBody(i.getJournalMailTemplates().getMailBody());
                auto.setTaskName(i.getTaskMaster().getTaskName());
                auto.setEmailTemplateForm(i.getJournalMailTemplates().getEmailTemplateForm());
                auto.setTaskEmailMapId(i.getTemId());
                autoMailer.add(auto);
            });
            return autoMailer;
        }
        return null;
    }

    @Override
    public String deleteAutoMailerForReminder(AutoMailersVO autoMailersVO) {
        taskEmailMapReminderRepo.deleteById(autoMailersVO.getTaskEmailMapId());
        return "Deleted Successfully";
    }

    @Override
    public List<JournalSubjectVo> getJournalSubjectList(JournalSubjectVo journalSubjectVo) {
        List<JournalSubjectVo> addJournalSubjectVo = new ArrayList<>();
        List<JournalSubject> getJournalSubjectList = journalSubjectRepo.findByJournalId(journalSubjectVo.getJournalId(),
                1);
        if (getJournalSubjectList != null && !getJournalSubjectList.isEmpty()) {
            getJournalSubjectList.stream().forEach(i -> {
                JournalSubjectVo addJournalSubjectVoInList = new JournalSubjectVo();
                addJournalSubjectVoInList.setId(i.getId());
                addJournalSubjectVoInList.setIsActive(i.getIsActive());
                addJournalSubjectVoInList.setJournalId(i.getJournalId());
                addJournalSubjectVoInList.setJournalSubject(i.getJournalSubject());
                addJournalSubjectVoInList.setCreatedAt(i.getCreatedAt());
                addJournalSubjectVo.add(addJournalSubjectVoInList);
            });
        }
        return addJournalSubjectVo;
    }

    @Override
    public String saveJournalSubject(JournalSubjectVo journalSubjectVo) {
        if (journalSubjectVo.getId() != null) {
            JournalSubject getJournalSubject = journalSubjectRepo.getById(journalSubjectVo.getId());
            if (getJournalSubject.getId() != null) {
                getJournalSubject.setJournalSubject(journalSubjectVo.getJournalSubject());
                journalSubjectRepo.save(getJournalSubject);
                return "Update successfully";
            } else {
                return "There is no data for this id";
            }
        } else {
            JournalSubject addJournalSubject = new JournalSubject();
            addJournalSubject.setJournalId(journalSubjectVo.getJournalId());
            addJournalSubject.setJournalSubject(journalSubjectVo.getJournalSubject());
            addJournalSubject.setIsActive(1);
            addJournalSubject.setCreatedAt(new Date());
            journalSubjectRepo.save(addJournalSubject);
            return "Created successfully";
        }
    }

//    @Override
//    public boolean isSubjectExist(String journalSubject) {
//        JournalSubject getJournalSubject = journalSubjectRepo.findByjournalSubject(journalSubject);
//        if (getJournalSubject != null)
//            return true;
//        return false;
//    }

    @Override
    public boolean isSubjectExist(String journalSubject, Integer journalId) {
        JournalSubject getJournalSubject = journalSubjectRepo.findByjournalSubjectAndJournalId(journalSubject, journalId);
        if (getJournalSubject != null)
            return true;
        return false;
    }

    @Override
    public String deleteJournalSubject(JournalSubjectVo journalSubjectVo) {
        if (journalSubjectVo.getId() != null) {
            //journalSubjectRepo.deleteById(journalSubjectVo.getId());
            JournalSubject jSub = journalSubjectRepo.findById(journalSubjectVo.getId()).orElse(new JournalSubject());
            jSub.setIsActive(0);
            journalSubjectRepo.save(jSub);
            return "Subject Deleted successfully";
        }
        return "Plese provide id";
    }

    @Override
    public String bulkSaveJournalMailTemplate(List<JournalMailTemplatesVO> journalMailTemplatesVO) {
        if (journalMailTemplatesVO != null && !journalMailTemplatesVO.isEmpty()) {
            journalMailTemplatesVO.stream().forEach(i -> {
                if (i.getJournalId() != null && i.getEmailTemplateName() != null
                        && !i.getEmailTemplateName().isEmpty()) {
                    JournalMailTemplates getJournalMailTemplates = journalMailTemplatesRepo
                            .getUniqueJournalMailTemplates(i.getJournalId(), i.getEmailTemplateName());
                    if (getJournalMailTemplates != null) {
                    } else {
                        JournalMailTemplates journalTemplate = new JournalMailTemplates();
                        journalTemplate.setEmailTemplateName(i.getEmailTemplateName());
                        journalTemplate.setEmailTemplateForm(i.getEmailTemplateForm());
                        journalTemplate.setJournalId(i.getJournalId());
                        journalTemplate.setTemplatePlaceholderId(i.getTemplatePlaceholderId());
                        journalTemplate.setIsActive("yes");
                        journalTemplate.setMailBcc(i.getMailBcc());
                        journalTemplate.setMailBody(i.getMailBody());
                        journalTemplate.setMailcc(i.getMailcc());
                        journalTemplate.setMailSubject(i.getMailSubject());
                        journalTemplate.setMailTo(i.getMailTo());
                        journalTemplate.setMailFrom(i.getMailFrom());
                        journalTemplate.setUserGroup(i.getUserGroup());
                        journalMailTemplatesRepo.save(journalTemplate);
                    }
                }
            });
            return "Bulk Email Template Upload successfully";
        }
        return "Please select email template";
    }

    @Override
    public void deleteJournalTemplate(JournalMailTemplatesVO journalMailTemplatesVO) {
        List<TaskEmailMap> i = taskEmailMapRepo
                .getTaskEmailMapTemplateData(journalMailTemplatesVO.getJournalEmailTemplateId());
        if (i != null && !i.isEmpty()) {
            i.stream().forEach(k -> {
                taskEmailMapRepo.deleteById(k.getTemId());
            });
        }
        List<TaskEmailMapReminder> j = taskEmailMapReminderRepo
                .getTaskEmailMapTemplateData(journalMailTemplatesVO.getJournalEmailTemplateId());
        if (j != null && !j.isEmpty()) {
            j.stream().forEach(l -> {
                taskEmailMapReminderRepo.deleteById(l.getTemId());
            });
        }
        journalMailTemplatesRepo.deleteById(journalMailTemplatesVO.getJournalEmailTemplateId());
    }

    @Override
    public void saveJournalHistoryData(JournalHistoryDataVO journalHistoryDataVO) {
        JournalHistoryData journalHistoryData = new JournalHistoryData();
        if (journalHistoryDataVO != null) {
            journalHistoryData.setJournalId(journalHistoryDataVO.getJournalId());
            journalHistoryData.setSection(journalHistoryDataVO.getSection());
            journalHistoryData.setUserName(journalHistoryDataVO.getUserName());
            journalHistoryData.setComment(journalHistoryDataVO.getComment());
            journalHistoryData.setUpdatedOn(new Date());
           if(journalHistoryDataVO.getCharLimitTitle()!=null) {
        	  CharacterLimit characterLimit=new CharacterLimit();
        	  characterLimit.setJournalId(journalHistoryDataVO.getJournalId());
        	  characterLimit.setTitle(journalHistoryDataVO.getCharLimitTitle());
        	  characterLimit.setRunningTitle(journalHistoryDataVO.getCharLimitRunTitle());
        	  characterLimit.setAbstractTitle(journalHistoryDataVO.getCharLimitAbstract());
        	  characterLimit.setUpdatedOn(new Date());
        	  characterLimitRepo.save(characterLimit);
           }
        }
        journeyHistoryDataRepo.save(journalHistoryData);

    }
    
    public CharacterLimit getCharacterLimit(JournalHistoryDataVO journalHistoryDataVO) {
    	CharacterLimit characterLimit = characterLimitRepo.getById(journalHistoryDataVO.getJournalId());
		return characterLimit;
    }

//    @Override
//    public String saveCharacterLimit(CharacterLimitVo characterLimitVo) {
//        CharacterLimit characterLimit = characterLimitRepo.findById(characterLimitVo.getJournalId()).orElse(null);
//
//        if (characterLimit != null) { 
//            characterLimit.setTitle(characterLimitVo.getTitle());
//            characterLimit.setRunningTitle(characterLimitVo.getRunningTitle());
//            characterLimit.setAbstractTitle(characterLimitVo.getAbstractTitle());
//            characterLimit.setUpdatedOn(new Date());
//            characterLimitRepo.save(characterLimit);
//            return "Updated successfully";
//        } else {
//            CharacterLimit newCharacterLimit = new CharacterLimit();
//            newCharacterLimit.setJournalId(characterLimitVo.getJournalId());
//            newCharacterLimit.setTitle(characterLimitVo.getTitle());
//            newCharacterLimit.setRunningTitle(characterLimitVo.getRunningTitle());
//            newCharacterLimit.setAbstractTitle(characterLimitVo.getAbstractTitle());
//            newCharacterLimit.setUpdatedOn(new Date());   
//            characterLimitRepo.save(newCharacterLimit);
//            return "Created successfully";
//        }
//    }

 

}
