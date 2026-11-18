package com.digi.uniprr.controller;

import java.util.Date;
import java.util.List;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.digi.uniprr.VO.AcceptedArticleVo;
import com.digi.uniprr.VO.ArticleRatingVo;
import com.digi.uniprr.VO.ArticleReportVo;
import com.digi.uniprr.VO.AuthorPerformanceVo;
import com.digi.uniprr.VO.EditorPerformanceVo;
import com.digi.uniprr.VO.EditorPresentDataVO;
import com.digi.uniprr.VO.IDVO;
import com.digi.uniprr.VO.ManuScriptByJournalVo;
import com.digi.uniprr.VO.ManuscriptListVO;
import com.digi.uniprr.VO.MonthlyStatisticsVO;
import com.digi.uniprr.VO.PerformanceYearListVo;
import com.digi.uniprr.VO.PresentStatusForEiCVO;
import com.digi.uniprr.VO.PresentStatusVO;
import com.digi.uniprr.VO.RejectedArticlesVo;
import com.digi.uniprr.VO.ReportVO;
import com.digi.uniprr.VO.ReviewerDataWithCommentVO;
import com.digi.uniprr.VO.ReviewerOverviewVO;
import com.digi.uniprr.VO.ReviewersPerformanceVo;
import com.digi.uniprr.VO.SubmisionReportVO;
import com.digi.uniprr.VO.UserJournalManuscriptVo;
import com.digi.uniprr.service.CheckPlagiarismService;
import com.digi.uniprr.service.ReportSevice;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/report")
public class ReportRestController {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	ReportSevice reportSevice;
	
	@Autowired
	CheckPlagiarismService checkPlagiarismService;

	JSONObject obj = new JSONObject();

	@PostMapping("getReport")
	public List<ReportVO> getReport(@RequestBody UserJournalManuscriptVo userJournalManuscriptVo) {
		log.info("Inside ReportRestController getReport");
		List<ReportVO> report = reportSevice.getReport(userJournalManuscriptVo);
		return report;
	}

	@PostMapping("getManuScriptByJournal")
	public ResponseEntity<?> getManuScriptByJournal(@RequestBody ManuScriptByJournalVo manuScriptByJournalVo) {
		log.info("Inside ReportRestController getManuScriptByJournal");
		try {
			List<ManuScriptByJournalVo> list = reportSevice.getManuScriptByJournal(manuScriptByJournalVo);
			if (list != null && !list.isEmpty()) {
				return new ResponseEntity<>(list, HttpStatus.OK);
			}
			obj.put("data", "Data Not Found");
			return new ResponseEntity<>(obj, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			obj.put("data", "Error");
			return new ResponseEntity<>(obj, HttpStatus.OK);
		}
	}

	@PostMapping("getReviewerOverview")
	public ResponseEntity<?> getReviewerOverview(@RequestBody IDVO userId) {
		log.info("Inside ReportRestController getReviewerOverview");
		try {
			List<ReviewerOverviewVO> list = reportSevice.getReviewerOverview(userId.getId());
			if (list != null && !list.isEmpty()) {
				return new ResponseEntity<>(list, HttpStatus.OK);
			}
			obj.put("data", "Data Not Found");
			return new ResponseEntity<>(obj, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			obj.put("data", "Error");
			return new ResponseEntity<>(obj, HttpStatus.OK);
		}
	}

	@PostMapping("getReportByUserId")
	public ResponseEntity<?> getReportByUserId(@RequestBody IDVO userId) {
		log.info("Inside ReportRestController getReportByUserId");
		try {
			List<List<ReportVO>> reportList = reportSevice.getReportByUserId(userId);
			if (reportList != null && !reportList.isEmpty()) {
				obj.put("data", reportList);
			} else {
				obj.put("data", "Data Not Found");
			}
		} catch (Exception e) {
			log.info("Error");
			obj.put("data", "Error");
		}
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}

	@PostMapping("getReviewerOverviewReport")
	public ResponseEntity<?> getReviewerOverviewReport(@RequestBody IDVO userId) {
		log.info("Inside ReportRestController getReviewerOverviewReport");
		try {
			List<ReviewerDataWithCommentVO> reportList = reportSevice.getReviewerOverviewReport(userId);
			if (reportList != null && !reportList.isEmpty()) {
				obj.put("data", reportList);
			} else {
				obj.put("data", "Data Not Found");
			}
		} catch (Exception e) {
			log.info("Error");
			obj.put("data", "Data Not Found");
		}
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}

	// For Editor
	@PostMapping("getSubmissionReportForEditor")
	public ResponseEntity<?> getSubmisionReportForEditor(@RequestBody IDVO userId) {
		log.info("Inside ReportRestController getSubmissionReportForEditor");
		try {
			List<SubmisionReportVO> submissionReportList = reportSevice.getSubmisionReportForEditor(userId);
			if (submissionReportList != null && !submissionReportList.isEmpty()) {
				return new ResponseEntity<>(submissionReportList, HttpStatus.OK);
			} else {
				obj.put("data", "Data Not Found");
			}
		} catch (Exception e) {
			log.info("Error");
			obj.put("data", "Error");
		}
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}

	// Journal Based Reports

	// Accepted Articles
	@PostMapping("getAcceptedArticlesReports")
	public ResponseEntity<?> getAcceptedArticlesReports(@RequestBody IDVO idvo) {
		log.info("Inside ReportRestController getAcceptedArticlesReports");
		try {
			List<AcceptedArticleVo> acceptedArticleReportList = reportSevice.getAcceptedArticlesReports(idvo);
			if (acceptedArticleReportList != null && !acceptedArticleReportList.isEmpty()) {
				return new ResponseEntity<>(acceptedArticleReportList, HttpStatus.OK);
			} else {
				obj.put("data", "Data Not Found");
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Error");
			obj.put("data", "Error");
		}
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}

	// Rejected Articles
	@PostMapping("getRejectedArticleReports")
	public ResponseEntity<?> getRejectedArticleReports(@RequestBody IDVO idvo) {
		log.info("Inside ReportRestController getRejectedArticleReports");
		try {
			List<RejectedArticlesVo> rejectedArticlesList = reportSevice.getRejectedArticleReports(idvo);
			if (rejectedArticlesList != null && !rejectedArticlesList.isEmpty()) {
				return new ResponseEntity<>(rejectedArticlesList, HttpStatus.OK);
			} else {
				obj.put("data", "Data Not Found");
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Error");
			obj.put("data", "Error");
		}
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}

	// Performance for year
	@PostMapping("getPerformanceForYearReport")
	public ResponseEntity<?> getPerformanceForYearReport(@RequestBody IDVO idvo) {
		log.info("Inside ReportRestController getPerformanceForYearReport");
		try {
			PerformanceYearListVo performanceYearList = reportSevice.getPerformanceForYearReport(idvo);
			if (performanceYearList != null) {
				return new ResponseEntity<>(performanceYearList, HttpStatus.OK);
			} else {
				obj.put("data", "Data Not Found");
			}
		} catch (Exception e) {
			log.info("Error");
			e.printStackTrace();
			obj.put("data", "Error");
		}
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}

	// Author Performance
	@PostMapping("getAuthorPerformanceReport")
	public ResponseEntity<?> getAuthorPerformanceReport(@RequestBody IDVO idvo) {
		log.info("Inside ReportRestController getAuthorPerformanceReport");
		try {
			AuthorPerformanceVo dataAuthorPerformanceVo = reportSevice.getAuthorPerformanceReport(idvo);
			if (dataAuthorPerformanceVo != null) {
				obj.put("data", dataAuthorPerformanceVo);
			} else {
				obj.put("data", "Data Not Found");
			}
		} catch (Exception e) {
			log.info("Error");
			obj.put("data", "Error");
		}
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}

	// Reviewers Performance
	@PostMapping("getReviewersPerformanceReport")
	public ResponseEntity<?> getReviewersPerformanceReport(@RequestBody IDVO idvo) {
		log.info("Inside ReportRestController getReviewersPerformanceReport");
		try {
			ReviewersPerformanceVo dataReviewersPerformanceVo = reportSevice.getReviewersPerformanceReport(idvo);
			if (dataReviewersPerformanceVo != null) {
				obj.put("data", dataReviewersPerformanceVo);
			} else {
				obj.put("data", "Data Not Found");
			}

		} catch (Exception e) {
			log.info("Error");
			obj.put("data", "Error");
		}
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}

	// Editor Performance
	@PostMapping("getEditorPerformanceReport")
	public ResponseEntity<?> getEditorPerformanceReport(@RequestBody IDVO idvo) {
		log.info("Inside ReportRestController getEditorPerformanceReport");
		try {
			EditorPerformanceVo dataEditorPerformanceVo = reportSevice.getEditorPerformanceReport(idvo);
			if (dataEditorPerformanceVo != null) {
				obj.put("data", dataEditorPerformanceVo);
				return new ResponseEntity<>(obj, HttpStatus.OK);
			} else {
				obj.put("data", "Data Not Found");
				return new ResponseEntity<>(obj, HttpStatus.OK);
			}
		} catch (Exception e) {
			log.info("Error");
			obj.put("data", "Error");
			return new ResponseEntity<>(obj, HttpStatus.OK);
		}
		
	}

	// Monthly Statistics
	@PostMapping("getMonthlyStatisticsReport")
	public ResponseEntity<?> getMonthlyStatisticsReport(@RequestBody IDVO idvo) {
		log.info("Inside ReportRestController getMonthlyStatisticsReport");
		try {
			MonthlyStatisticsVO addMonthlyStatisticsVO = reportSevice.getMonthlyStatisticsReport(idvo);
			if (addMonthlyStatisticsVO != null) {
				obj.put("data", addMonthlyStatisticsVO);
			} else {
				obj.put("data", "Data Not Found");
			}
		} catch (Exception e) {
			log.info("Error");
			obj.put("data", "Error");
		}
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}

	// Present Status
	@PostMapping("getPresentStatus")
	public ResponseEntity<?> getPresentStatus(@RequestBody IDVO iDVO) {
		JSONObject obj = new JSONObject();
		try {
			PresentStatusVO dataPresentStatusVO = reportSevice.getPresentStatus(iDVO);
			if (dataPresentStatusVO != null)
				obj.put("data", dataPresentStatusVO);
			else
				obj.put("data", "data not found");
		} catch (Exception e) {
			e.printStackTrace();
			obj.put("data", "Something Went Wrong");
		}
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}

	// Article Rating
	@PostMapping("getArticleRating")
	public ResponseEntity<?> getArticleRating(@RequestBody IDVO iDVO) {
		JSONObject obj = new JSONObject();
		try {
			List<ArticleRatingVo> dataArticleRatingVO = reportSevice.getArticleRating(iDVO);
			if (dataArticleRatingVO != null && !dataArticleRatingVO.isEmpty())
				obj.put("data", dataArticleRatingVO);
			else
				obj.put("data", "data not found");
		} catch (Exception e) {
			e.printStackTrace();
			obj.put("data", "Something Went Wrong");
		}
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}

	// Present Status
	@PostMapping("getPresentStatusForEiC")
	public ResponseEntity<?> getPresentStatusForEiC(@RequestBody IDVO iDVO) {
		JSONObject obj = new JSONObject();
		try {
			PresentStatusForEiCVO getgetPresentStatusForEiCData = reportSevice.getPresentStatusForEiC(iDVO);
			if (getgetPresentStatusForEiCData != null)
				obj.put("data", getgetPresentStatusForEiCData);
			else
				obj.put("data", "data not found");
		} catch (Exception e) {
			e.printStackTrace();
			obj.put("data", "Something Went Wrong");
		}
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}
	
	@PostMapping("getPresentStatusData")
	public ResponseEntity<?> getPresentStatusData(@RequestBody IDVO id) {
		JSONObject obj = new JSONObject();
		try {
			List<PresentStatusForEiCVO> getgetPresentStatusForEiCData = reportSevice.getPresentStatusDataNew(id);
			if (getgetPresentStatusForEiCData != null)
				obj.put("data", getgetPresentStatusForEiCData);
			else
				obj.put("data", "data not found");
		} catch (Exception e) {
			e.printStackTrace();
			obj.put("data", "Something Went Wrong");
		}
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}
	
	@PostMapping("getPresentStatusDataForEditor")
	public ResponseEntity<?> getPresentStatusDataForEditor(@RequestBody EditorPresentDataVO edvo) {
		JSONObject obj = new JSONObject();
		try {
			List<PresentStatusForEiCVO> getgetPresentStatusForEiCData = reportSevice.getPresentStatusDataForEditor(edvo);
			if (getgetPresentStatusForEiCData != null)
				obj.put("data", getgetPresentStatusForEiCData);
			else
				obj.put("data", "data not found");
		} catch (Exception e) {
			e.printStackTrace();
			obj.put("data", "Something Went Wrong");
		}
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}
	
//	@PostMapping("getPlagiarismReport")
//	public ResponseEntity<?> getPlagiarismReport(@RequestBody IDVO id) {
//		JSONObject obj = new JSONObject();
//		try {
//			List<ManuscriptListVO> getPlagiarismData = checkPlagiarismService.getPlagiarismReport(id);
//			if (getPlagiarismData != null)
//				obj.put("data", getPlagiarismData);
//			else
//				obj.put("data", "data not found");
//		} catch (Exception e) {
//			e.printStackTrace();
//			obj.put("data", "Something Went Wrong");
//		}
//		return new ResponseEntity<>(obj, HttpStatus.OK);
//	}
	
	@PostMapping("getPlagiarismReport")
	public ResponseEntity<?> getPlagiarismReport(@RequestBody IDVO id,
	                                             @RequestParam(defaultValue = "0") int page,
	                                             @RequestParam(defaultValue = "10") int size,
	                                             @RequestParam(defaultValue = "false") boolean exportAll) {
	    JSONObject obj = new JSONObject();
	    try {
	        if (exportAll) {
	            List<ManuscriptListVO> allData = checkPlagiarismService.getPlagiarismReportAll(id);
	            obj.put("data", allData);
	        } else {
	            Pageable pageable = PageRequest.of(page, size);
	            Page<ManuscriptListVO> pagedData = checkPlagiarismService.getPlagiarismReportPaged(id, pageable);
	            obj.put("data", pagedData.getContent());
	            obj.put("totalElements", pagedData.getTotalElements());
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	        obj.put("data", "Something Went Wrong");
	    }
	    return new ResponseEntity<>(obj, HttpStatus.OK);
	}

	
	@PostMapping("getArticleReport")
	public ResponseEntity<?> getArticleReport(@RequestBody ArticleReportVo articleReportVo) {
		JSONObject obj = new JSONObject();
		try {
			List<ArticleReportVo> getArticleReportData = reportSevice.getArticleReport(articleReportVo);
			if (getArticleReportData != null && !getArticleReportData.isEmpty())
				obj.put("data", getArticleReportData);
			else
				obj.put("data", "data not found");
		} catch (Exception e) {
			e.printStackTrace();
			obj.put("data", "Something Went Wrong");
		}
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}
}
