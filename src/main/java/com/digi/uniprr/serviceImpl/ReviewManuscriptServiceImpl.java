package com.digi.uniprr.serviceImpl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.digi.uniprr.model.ManuScript;
import com.digi.uniprr.model.ManuScriptReviewer;
import com.digi.uniprr.repository.ManuScriptRepo;
import com.digi.uniprr.repository.ManuScriptReviewerRepo;
import com.digi.uniprr.service.ReviewManuscriptService;

@Service
public class ReviewManuscriptServiceImpl implements ReviewManuscriptService {
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	ManuScriptRepo manuScriptRepo;

	@Autowired
	ManuScriptReviewerRepo manuScriptReviewerRepo;

	@Override
	public String reviewManuscript() {

		List<ManuScript> manuscriptList = manuScriptRepo.getListByStatusId(8);

		for (ManuScript manuScript : manuscriptList) {
			try {

				List<ManuScriptReviewer> reviewerList = getManuscriptReviewerList(manuScript);

				for (ManuScriptReviewer manuscriptReviewer : reviewerList) {
					try {
						DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
						if (manuscriptReviewer.getDueDate() != null) {
							Date reviewerDueDate = df.parse(df.format(manuscriptReviewer.getDueDate()));
							Date todayDate = df.parse(df.format(new Date()));
							if ((manuscriptReviewer.getManuscriptStatus().equals(8)
									|| manuscriptReviewer.getManuscriptStatus().equals(35))
									&& reviewerDueDate.before(todayDate)) {

								updateManuscriptReviewerStatus(manuscriptReviewer.getId(), 36);

							}
						} else {
							updateManuscriptReviewerStatus(manuscriptReviewer.getId(), 36);
						}

					} catch (Exception ex) {
						ex.printStackTrace();
						log.info("Error occured during updating the ststus of reviewer method reviewManuscript ::::::"+ex.getMessage());
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				log.info("Error occured during getting the details of article in method reviewManuscript ::::::"+e.getMessage());

			}
		}
		return "SUCCESS";
	}

	
	private void updateManuscriptReviewerStatus(int manuscriptReviewerId, int statusId) {
		manuScriptReviewerRepo.updateReviewerStatus(statusId,manuscriptReviewerId);

	}

	
	private List<ManuScriptReviewer> getManuscriptReviewerList(ManuScript manuScript) {
		List<ManuScriptReviewer> reviewerList = manuScriptReviewerRepo.getmanuScriptReviewer(
				manuScript.getManuScriptId(), manuScript.getManuscriptVersion(), manuScript.getJournalId());
		return reviewerList;
	}

}
