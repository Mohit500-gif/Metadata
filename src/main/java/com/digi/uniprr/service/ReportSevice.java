package com.digi.uniprr.service;

import java.util.List;

import com.digi.uniprr.VO.AcceptedArticleVo;
import com.digi.uniprr.VO.ArticleRatingVo;
import com.digi.uniprr.VO.ArticleReportVo;
import com.digi.uniprr.VO.AuthorPerformanceVo;
import com.digi.uniprr.VO.EditorPerformanceVo;
import com.digi.uniprr.VO.EditorPresentDataVO;
import com.digi.uniprr.VO.IDVO;
import com.digi.uniprr.VO.ManuScriptByJournalVo;
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

public interface ReportSevice {

	List<ReportVO> getReport(UserJournalManuscriptVo userJournalManuscriptVo);
	
	public List<ManuScriptByJournalVo> getManuScriptByJournal(ManuScriptByJournalVo manuScriptByJournalVo);
	
	public List<ReviewerOverviewVO> getReviewerOverview(Integer userId);
	
	public List<List<ReportVO>> getReportByUserId(IDVO userId);

	List<ReviewerDataWithCommentVO> getReviewerOverviewReport(IDVO userId);
	
	List<SubmisionReportVO> getSubmisionReportForEditor(IDVO userId);
	
	//Journal Based Reports
   /*******************Accepted Articles****************************/
	public List<AcceptedArticleVo> getAcceptedArticlesReports(IDVO iDVO);
	
	/*******************Rejected Articles****************************/
	public List<RejectedArticlesVo> getRejectedArticleReports(IDVO iDVO);
	
	/******************Performance For Year*************************/
	public PerformanceYearListVo getPerformanceForYearReport(IDVO iDVO);
	
	/*****************Author Performance**************************/
	public AuthorPerformanceVo getAuthorPerformanceReport(IDVO iDVO);
	
	/*****************Reviewers Performance**************************/
	public ReviewersPerformanceVo getReviewersPerformanceReport(IDVO iDVO);
	
	/******************Monthly Statistics************************/
	public MonthlyStatisticsVO getMonthlyStatisticsReport(IDVO iDVO);
	
	/************************ Editor Performance ********************/
	public EditorPerformanceVo getEditorPerformanceReport(IDVO iDVO);

	/************************ Present Status **********************/
	public PresentStatusVO getPresentStatus(IDVO iDVO);
	
	/************************ Article Rating ********************/
	public List<ArticleRatingVo> getArticleRating(IDVO iDVO);
	
	
	/******************** EIC PRESENT STATUS *********************/
	public PresentStatusForEiCVO getPresentStatusForEiC(IDVO iDVO);

	List<PresentStatusForEiCVO> getPresentStatusData(IDVO id);

	List<PresentStatusForEiCVO> getPresentStatusDataNew(IDVO id);

	List<PresentStatusForEiCVO> getPresentStatusDataForEditor(EditorPresentDataVO edvo);

	List<ArticleReportVo> getArticleReport(ArticleReportVo articleReportVo);
}
