package com.digi.uniprr.service;

import java.util.List;

import com.digi.uniprr.VO.IDVO;
import com.digi.uniprr.VO.ManuscriptListVO;
import com.digi.uniprr.VO.ManuscriptReportDataVO;
import com.digi.uniprr.VO.ReviewerReportDataVO;
import com.digi.uniprr.model.ManuScript;
import com.digi.uniprr.model.ManuScriptForDashboard;
import com.digi.uniprr.model.ManuScriptReviewer;
import com.digi.uniprr.model.ManuscriptDashboardList;
import com.digi.uniprr.model.ManuscriptEditor;

public interface ManuscriptDashboardArticleService {

	
	public List<ManuscriptDashboardList> getManuscriptByStatus(ManuscriptDashboardList manuscriptVO);

	public List<ManuscriptDashboardList> getManuscriptByStatusReport(ManuscriptDashboardList manuscriptVO);
	
	
	public List<ManuscriptDashboardList> getTotalManuscriptByStatus(ManuscriptDashboardList manuscriptVO);

	public List<String> getStatusList(ManuscriptDashboardList manuscriptVO);
	
//	public List<ManuscriptListVO> getAwatingPaymentManuscript(ManuscriptListVO manuscriptVO);
	
	public int getTotalSbmittedManuscriptForDashboard(ManuscriptDashboardList manuscriptVO);
	
	public int getTotalManuscriptStausForDashboard(ManuscriptDashboardList manuscriptVO);

	public int getTotalManuscriptStausEditorForDashboard(ManuscriptDashboardList manuscriptVO);

	public int getTotalManuscriptStausEICForDashboard(ManuscriptDashboardList manuscriptVO);

	
	public int getTotalSbmittedManuscriptForDashboardSearch(ManuscriptDashboardList manuscriptVO);

	public int getTotalManuscriptForDashboardSearchReport(ManuscriptDashboardList manuscriptVO);
	
	public int getTotalManuscriptForDashboardReport(ManuscriptDashboardList manuscriptVO);

	
	public int getTotalSbmittedManuscriptForDashboarEditordSearch(ManuscriptDashboardList manuscriptVO);

	
	public List<ManuscriptDashboardList> getfinaldecisionByStatus(ManuscriptDashboardList manuscriptVO);

	public List<ManuscriptListVO> getAllInReviewManuscript(ManuscriptDashboardList manuscriptVO);

	public List<ManuscriptListVO> getTotalInReviewManusriptForDashboard(ManuscriptDashboardList manuscriptVO);

	public int getTotalInReviewManusriptForDashboardSearch(ManuscriptDashboardList manuscriptVO);

	public int getTotalInReviewManusriptForDashboardCount(List<ManuScriptReviewer> getUniqueDataByjournal);

	public int getTotalInReviewManusriptForDashboardCountReport(List<ManuscriptReportDataVO> editorDataList, List<ReviewerReportDataVO> getUniqueDataByjournal);

	public List<ManuscriptListVO> getAllReviewedManuscript(ManuscriptDashboardList manuscriptVO);

	public int getTotalReviewedManusriptForDashboardCount(List<ManuScript> editorDataList, List<ManuScriptReviewer> getAllDataByjournal );

	public int getTotalReviewedManusriptForDashboardCountReport(List<ManuscriptReportDataVO> editorDataList, List<ReviewerReportDataVO> getAllDataByjournal );

	public int getTotalReviewedManusriptForDashboardCountSearch(ManuscriptDashboardList manuscriptVO);

	
	public List<ManuscriptListVO> getAllNoResponseManuscript(ManuscriptDashboardList manuscriptVO);

	public int getAllNoResponseManusriptForDashboardCount(List<ManuScript> editorDataList, List<ManuScriptReviewer> getAllDataByjournal );

	public int getAllNoResponseManusriptForDashboardCountReport(List<ManuscriptReportDataVO> editorDataList, List<ReviewerReportDataVO> getAllDataByjournal );

	public int getAllNoResponseManusriptForDashboardSearchCount(ManuscriptDashboardList manuscriptVO);

	
	public Integer getAllNoResponseManusriptForDashboardSearch(ManuscriptDashboardList manuscriptVO);

	public List<ManuscriptListVO> getAssignManuscriptData(ManuscriptDashboardList manuscriptVO);

	public Integer getAssignManuscriptForDashboardSearch(ManuscriptDashboardList manuscriptVO);

    public Integer getAssignManuscriptDatacount(ManuscriptDashboardList manuscriptVO);
    
    public Integer getAssignManuscriptDatacountSearch(ManuscriptDashboardList manuscriptVO);

	public List<ManuscriptDashboardList> getManuscriptByStatusByUser(ManuscriptDashboardList manuscriptVO);
	
	public List<ManuscriptDashboardList> getManuscriptByStatusByRole(ManuscriptDashboardList manuscriptVO);
	
	public List<ManuscriptDashboardList> getManuscriptByStatusByRoleReSubmiited(ManuscriptDashboardList manuscriptVO);

	public Integer getTotalSbmittedManuscriptForDashboardByUser(ManuscriptDashboardList manuscriptVO);
	
	public Integer getTotalManuscriptForDashboardByRole(ManuscriptDashboardList manuscriptVO);

	public Integer getTotalManuscriptForEditorByUser(ManuscriptDashboardList manuscriptVO);
	
	public Integer getTotalSbmittedManuscriptForDashboardSearchByUser(ManuscriptDashboardList manuscriptVO);
	
	public Integer getTotalManuscriptForDashboardSearchByRole(ManuscriptDashboardList manuscriptVO);


	public Integer getTotalManuscriptFoEditorSearchByUser(ManuscriptDashboardList manuscriptVO);
	
	public List<ManuscriptDashboardList> getTotalManuscriptByStatusByUser(ManuscriptDashboardList manuscriptVO);
	
	public List<ManuscriptDashboardList> getTotalManuscriptByStatusForEditor(ManuscriptDashboardList manuscriptVO);

	public Integer getTotalManuscriptStausForDashboardByUser(ManuscriptDashboardList manuscriptVO);

	int getTotalSbmittedManuscriptForDashboardEditor(ManuscriptDashboardList manuscriptVO);
	
	public int getTotalFinalSbmittedManuscriptForDashboard(ManuscriptDashboardList manuscriptVO);
	public int getTotalFinalDecisionManuscriptForDashboardStatusCode(ManuscriptDashboardList manuscriptVO);
	public int getTotalFinalDecisionManuscriptForDashboardSearchStatusCode(ManuscriptDashboardList manuscriptVO);

	int getTotalRevisedManuscriptForDashboard(ManuscriptDashboardList manuscriptVO);
	public String getNoResponseManuscriptNotification();
	
	public List<ManuScriptForDashboard> getNoResponseReminder();
	
	public List<ManuscriptDashboardList> sortManuscriptList(List<ManuscriptDashboardList> manuList,  String sortBy, String sort);

	public Integer getTotalEditorDashboard(ManuscriptDashboardList manuscriptVO);

}
