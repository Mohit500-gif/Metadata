package com.digi.uniprr.serviceImpl;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import com.digi.uniprr.model.DevelopedCountriesByManuscriptType;
import com.digi.uniprr.repository.DevelopedCountriesByManuscriptTypeRepo;
import com.digi.uniprr.utils.AppConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.digi.uniprr.VO.JournalManuscriptTypeChargeVo;
import com.digi.uniprr.VO.JournalsConfigurationVO;
import com.digi.uniprr.VO.ManuscriptTypeJournalVO;
import com.digi.uniprr.model.Journal;
import com.digi.uniprr.model.JournalManuscriptTypeCharge;
import com.digi.uniprr.model.ManuscriptTypeJournal;
import com.digi.uniprr.repository.JournalManuscriptTypeChargeRepo;
import com.digi.uniprr.repository.JournalsRepo;
import com.digi.uniprr.repository.ManuscriptTypeJournalsRepo;
import com.digi.uniprr.service.ManuscriptTypeChargeService;
import com.digi.uniprr.utils.ManuscriptTypeJournalTransformationUtils;

import static com.digi.uniprr.utils.AppConstants.YES;

@Service
@Transactional
public class JournalManuscriptTypeChargeServiceImpl implements ManuscriptTypeChargeService {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    JournalManuscriptTypeChargeRepo journalManuscriptTypeChargeRepo;
    @Autowired
    JournalsRepo journalsRepo;
    @Autowired
    private ManuscriptTypeJournalsRepo manuscriptTypeJournalsRepo;

    @Autowired
    private DevelopedCountriesByManuscriptTypeRepo developedCountriesByManuscriptTypeRepo;

    public void saveJournalManuscriptTypeCharge(JournalsConfigurationVO journalsConfigurationVO) {

        if (journalsConfigurationVO == null) {
            log.error("journalsConfigurationVO is null. Unable to save.");
            return;
        }

        List<ManuscriptTypeJournalVO> listManuscriptTypes = journalsConfigurationVO.getManuscriptTypeJournals();
        log.info("Manuscript types list size: " + (listManuscriptTypes != null ? listManuscriptTypes.size() : 0));

        if(journalsConfigurationVO.getTrialArray()!=null
                && !journalsConfigurationVO.getTrialArray().isEmpty())
        {
            listManuscriptTypes.stream().forEach(obj -> {
                if (journalsConfigurationVO.getTrialArray().contains(obj.getManutypeName()))
                    obj.setTrialNumberMandatory("Y");
                else
                    obj.setTrialNumberMandatory("N");
            });
        }


        if (listManuscriptTypes != null) {
            List<ManuscriptTypeJournal> manuscriptTypeJournalList = ManuscriptTypeJournalTransformationUtils
                    .mapVOListToModelList(listManuscriptTypes);

            int i = 0;
            int jid = journalsConfigurationVO.getJournalId();

            if (jid <= 0) {
                log.error("Invalid journal ID: " + jid + ". Unable to save.");
                return;
            }

            try {
                manuscriptTypeJournalsRepo.deleteByJournalId(jid);
                journalManuscriptTypeChargeRepo.deleteJournalManuscriptTypeCharge(jid);

                for (ManuscriptTypeJournal manuscriptTypeJournal : manuscriptTypeJournalList) {
                    if (manuscriptTypeJournal != null) {
                        manuscriptTypeJournal.setJournalId(jid);
                        ManuscriptTypeJournal mtj = manuscriptTypeJournalsRepo.save(manuscriptTypeJournal);

//                        if (YES.equalsIgnoreCase(journalsConfigurationVO.getChargeFlag())) {
                            List<JournalManuscriptTypeChargeVo> data = journalsConfigurationVO.getManusriptTypeCharges();

                            if (data != null && !data.isEmpty() && i < data.size()) {
                                JournalManuscriptTypeChargeVo chargeData = data.get(i);

                                if (chargeData != null) {
                                    JournalManuscriptTypeCharge manuscriptTypeCharge = new JournalManuscriptTypeCharge();
                                    manuscriptTypeCharge.setJournalId(chargeData.getJournalId());
                                    manuscriptTypeCharge.setManuscriptTypeId(mtj.getMtjId());
                                    manuscriptTypeCharge.setFixedCharges("Yes");
                                    manuscriptTypeCharge.setChargeOnPublication(chargeData.getChargeOnPublication());
                                    manuscriptTypeCharge.setChargeOnSubmission(chargeData.getChargeOnSubmission());
                                    manuscriptTypeCharge.setIsChargesRequired(chargeData.getIsChargesRequired());
                                    manuscriptTypeCharge.setIsActive(1);
                                    manuscriptTypeCharge
                                            .setCountryChargeOnPublication(chargeData.getCountryChargeOnPublication());
                                    manuscriptTypeCharge
                                            .setCountryChargeOnSubmission(chargeData.getCountryChargeOnSubmission());
                                    manuscriptTypeCharge.setUpdatedBy(journalsConfigurationVO.getCreatedBy());
                                    journalManuscriptTypeChargeRepo.save(manuscriptTypeCharge);
                                    log.debug("Saved data for ManuscriptTypeCharge " + i);
                                } else {
                                    log.warn("ManuscriptTypeCharge data is null at index " + i);
                                }
                            } else {
                                log.warn("ManuscriptTypeCharge data list is null or empty.");
                            }
//                        }

                        i++;
                    } else {
                        log.warn("ManuscriptTypeJournal is null at index " + i);
                    }
                }
            } catch (Exception e) {
                log.error("An error occurred while saving Journal Manuscript Type Charge: " + e.getMessage());
            }
        }
    }

    @Override
    public void updateJournalManuscriptTypeCharge(JournalsConfigurationVO journalsConfigurationVO) {
        if (journalsConfigurationVO == null) {
            log.error("Invalid input: journalsConfigurationVO is null");
            return; // Nothing to process, invalid input
        }

        List<JournalManuscriptTypeChargeVo> manuscriptTypeCharges = journalsConfigurationVO.getManusriptTypeCharges();

        if (manuscriptTypeCharges == null || manuscriptTypeCharges.isEmpty()) {
            log.warn("No manuscript type charges provided for processing.");
            return; // Nothing to process
        }

        try {
            // Delete existing data in a batch
            journalManuscriptTypeChargeRepo.deleteJournalManuscriptTypeCharge(journalsConfigurationVO.getJournalId());
            developedCountriesByManuscriptTypeRepo.deleteJournalManuscriptTypeCharge(journalsConfigurationVO.getJournalId());

            List<JournalManuscriptTypeCharge> manuscriptTypeChargeList = new ArrayList<>();

            for (JournalManuscriptTypeChargeVo chargeData : manuscriptTypeCharges) {
                if (chargeData != null) {
                    JournalManuscriptTypeCharge manuscriptTypeCharge = new JournalManuscriptTypeCharge();
                    manuscriptTypeCharge.setJournalId(chargeData.getJournalId());
                    manuscriptTypeCharge.setManuscriptTypeId(chargeData.getManuscriptTypeId());
                    manuscriptTypeCharge.setFixedCharges("Yes");
                    manuscriptTypeCharge.setChargeOnPublication(chargeData.getChargeOnPublication());
                    manuscriptTypeCharge.setChargeOnSubmission(chargeData.getChargeOnSubmission());
                    manuscriptTypeCharge.setIsChargesRequired(chargeData.getIsChargesRequired());
                    manuscriptTypeCharge.setIsActive(1);
                    manuscriptTypeCharge.setCountryChargeOnPublication(chargeData.getCountryChargeOnPublication());
                    manuscriptTypeCharge.setCountryChargeOnSubmission(chargeData.getCountryChargeOnSubmission());
                    manuscriptTypeCharge.setUpdatedBy(journalsConfigurationVO.getCreatedBy());

                    DevelopedCountriesByManuscriptType developedCountriesByManuscriptType= new DevelopedCountriesByManuscriptType();
                    developedCountriesByManuscriptType.setJournalId(chargeData.getJournalId());
                    developedCountriesByManuscriptType.setManuscriptTypeId(chargeData.getManuscriptTypeId());
                    if(chargeData.getPaymentCountriesOnPublishing()!=null && !chargeData.getPaymentCountriesOnPublishing().isEmpty())
                    {
                        developedCountriesByManuscriptType.setPaymentCountriesOnPublishing(chargeData.getPaymentCountriesOnPublishing());
                    }
                    else{
                        developedCountriesByManuscriptType.setPaymentCountriesOnPublishing("");
                    }
                    if(chargeData.getPaymentCountriesOnSubmission()!=null && !chargeData.getPaymentCountriesOnSubmission().isEmpty())
                    {
                        developedCountriesByManuscriptType.setPaymentCountriesOnSubmission(chargeData.getPaymentCountriesOnSubmission());
                    }
                    else{
                        developedCountriesByManuscriptType.setPaymentCountriesOnSubmission("");
                    }
                    developedCountriesByManuscriptTypeRepo.save(developedCountriesByManuscriptType);

                    manuscriptTypeChargeList.add(manuscriptTypeCharge);
                }
            }

            // Batch insert the charges
            if (!manuscriptTypeChargeList.isEmpty()) {
                journalManuscriptTypeChargeRepo.saveAll(manuscriptTypeChargeList);
                log.info("Batch inserted " + manuscriptTypeChargeList.size() + " manuscript type charges.");
            } else {
                log.warn("No manuscript type charges to insert.");
            }
        } catch (Exception e) {
            log.error("Error occurred while updating manuscript type charges.", e);
        }
    }

    @Override
    public void deleteJournalManuscriptTypeCharge(JournalsConfigurationVO journalsConfigurationVO) {
        try {
            // Delete journal manuscript type charge
            journalManuscriptTypeChargeRepo.deleteJournalManuscriptTypeCharge(journalsConfigurationVO.getJournalId());
            developedCountriesByManuscriptTypeRepo.deleteJournalManuscriptTypeCharge(journalsConfigurationVO.getJournalId());

            // Optionally, you can log that the operation was successful.
            log.info("Deleted journal manuscript type charge for journal with ID: {}",
                    journalsConfigurationVO.getJournalId());
            log.info("Deleted DevelopedCountriesByManuscriptType for journal with ID: {}",
                    journalsConfigurationVO.getJournalId());

        } catch (Exception e) {
            log.error("Error deleting journal manuscript type charge: {}", e.getMessage());
            log.error("Error deleting DevelopedCountriesByManuscriptType: {}", e.getMessage());

            // Handle the exception appropriately, e.g., by logging, rethrowing, or
            // returning an error response.
        }
    }

    @Override
    public JournalManuscriptTypeCharge getJournalManuscriptTypeCharge(JournalsConfigurationVO journalsConfigurationVO) {
        // TODO Auto-generated method stub
        return null;
    }

}
