package com.digi.uniprr.controller;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.digi.uniprr.VO.IDVO;
import com.digi.uniprr.VO.ManuscriptListVO;
import com.digi.uniprr.model.ManuscriptDashboardList;

import com.digi.uniprr.service.ManuscriptDashboardArticleService;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping(value = "/dashboardAticle")
public class ManuscriptDashboardArticle {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	ManuscriptDashboardArticleService manuscriptDashboardArticleService;

	// JSONObject obj = new JSONObject();

	@RequestMapping(value = "/getSubmittedManusript", method = RequestMethod.POST)
	public ResponseEntity<?> getSubmittedManusript(@RequestBody ManuscriptDashboardList manuscriptVO) {
		JSONObject obj = new JSONObject();
		try {
			Integer totalSubmitedManuscript = 0;
			Integer totalSubmitedManuscriptSearch = 0;
			List<ManuscriptDashboardList> manuscriptList = manuscriptDashboardArticleService
					.getManuscriptByStatus(manuscriptVO);
			if (StringUtils.isNotBlank(manuscriptVO.getSearch())) {
				totalSubmitedManuscript = manuscriptDashboardArticleService
						.getTotalSbmittedManuscriptForDashboard(manuscriptVO);
				totalSubmitedManuscriptSearch = manuscriptDashboardArticleService
						.getTotalSbmittedManuscriptForDashboardSearch(manuscriptVO);
			} else {
				totalSubmitedManuscript = manuscriptDashboardArticleService
						.getTotalSbmittedManuscriptForDashboard(manuscriptVO);
				totalSubmitedManuscriptSearch = totalSubmitedManuscript;
			}
			/*
			 * if (manuscriptVO.getSortBy().equalsIgnoreCase("manuscripCode")) {
			 * 
			 * Comparator<ManuscriptDashboardList> compareById = (ManuscriptDashboardList
			 * o1, ManuscriptDashboardList o2) ->
			 * o1.getManuscripCode().compareTo(o2.getManuscripCode());
			 * 
			 * if (manuscriptVO.getSort().equalsIgnoreCase("asc"))
			 * Collections.sort(manuscriptList, compareById); else
			 * Collections.sort(manuscriptList, compareById.reversed());
			 * 
			 * }
			 */

			if (manuscriptVO.getSortBy().equalsIgnoreCase("manuscriptCode")
					|| manuscriptVO.getSortBy().equalsIgnoreCase("assignDate")) {
				Comparator<ManuscriptDashboardList> compareByManuscriptCode = (o1, o2) -> {
					String[] parts1 = o1.getManuscripCode().split("_");
					String[] parts2 = o2.getManuscripCode().split("_");

					int year1 = Integer.parseInt(parts1[2]);
					int year2 = Integer.parseInt(parts2[2]);

					int yearComparison = Integer.compare(year2, year1);
					if (yearComparison != 0) {
						return yearComparison;
					}
					int seriesNum1 = Integer.parseInt(parts1[1]);
					int seriesNum2 = Integer.parseInt(parts2[1]);
					return Integer.compare(seriesNum2, seriesNum1);
				};

				if (manuscriptVO.getSort().equalsIgnoreCase("asc")) {
					Collections.sort(manuscriptList, compareByManuscriptCode.reversed()); // Ascending
				} else {
					Collections.sort(manuscriptList, compareByManuscriptCode); // Descending
				}

//			    System.out.println("Manuscripts after ordering:");
//			    for (ManuscriptDashboardList manuscript : manuscriptList) {
//			        System.out.println(manuscript.getManuscripCode());
//			    }
			}

			if (manuscriptVO.getSortBy().equalsIgnoreCase("manuscripTitle")) {

				Comparator<ManuscriptDashboardList> compareById = (ManuscriptDashboardList o1,
						ManuscriptDashboardList o2) -> o1.getManuscripTitle().compareTo(o2.getManuscripTitle());

				if (manuscriptVO.getSort().equalsIgnoreCase("asc"))
					Collections.sort(manuscriptList, compareById);
				else
					Collections.sort(manuscriptList, compareById.reversed());

			}

			if (manuscriptVO.getSortBy().equalsIgnoreCase("manuScriptType")) {

				Comparator<ManuscriptDashboardList> compareById = (ManuscriptDashboardList o1,
						ManuscriptDashboardList o2) -> o1.getManuScriptType().compareTo(o2.getManuScriptType());

				if (manuscriptVO.getSort().equalsIgnoreCase("asc"))
					Collections.sort(manuscriptList, compareById);
				else
					Collections.sort(manuscriptList, compareById.reversed());

			}

			if (manuscriptVO.getSortBy().equalsIgnoreCase("editorName")) {

				Comparator<ManuscriptDashboardList> compareById = (ManuscriptDashboardList o1,
						ManuscriptDashboardList o2) -> o1.getEditorName().compareTo(o2.getEditorName());

				if (manuscriptVO.getSort().equalsIgnoreCase("asc"))
					Collections.sort(manuscriptList, compareById);
				else
					Collections.sort(manuscriptList, compareById.reversed());

			}

			if (manuscriptVO.getSortBy().equalsIgnoreCase("authorName")) {

				Comparator<ManuscriptDashboardList> compareById = (ManuscriptDashboardList o1,
						ManuscriptDashboardList o2) -> o1.getAuthorName().compareTo(o2.getAuthorName());

				if (manuscriptVO.getSort().equalsIgnoreCase("asc"))
					Collections.sort(manuscriptList, compareById);
				else
					Collections.sort(manuscriptList, compareById.reversed());

			}

			if (manuscriptVO.getSortBy().equalsIgnoreCase("submissionDate")) {

				if (manuscriptVO.getSort().equalsIgnoreCase("asc")) {
					manuscriptList = manuscriptList.stream()
							.sorted(Comparator.comparing(ManuscriptDashboardList::getSubmissionDate,
									Comparator.nullsFirst(Comparator.naturalOrder())))
							.collect(Collectors.toList());
				} else {
					manuscriptList = manuscriptList.stream()
							.sorted(Comparator.comparing(ManuscriptDashboardList::getSubmissionDate,
									Comparator.nullsLast(Comparator.reverseOrder())))
							.collect(Collectors.toList());
				}
			}
			obj.put("totalSubmitedManuscript", totalSubmitedManuscript);
			obj.put("totalSubmitedManuscriptSearch", totalSubmitedManuscriptSearch);
			obj.put("data", manuscriptList);
		} catch (Exception e) {
			obj.put("data", "Something Went Wrong");
			log.info("Exception :" + e);
			log.error("Exception : " + e.getMessage());
		}
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}

	@RequestMapping(value = "/getTottalManusript", method = RequestMethod.POST)
	public ResponseEntity<?> getTottalManusript(@RequestBody ManuscriptDashboardList manuscriptVO) {
		JSONObject obj = new JSONObject();
		try {
			Integer totalSubmitedManuscript = 0;

			List<ManuscriptDashboardList> manuscriptList = manuscriptDashboardArticleService
					.getTotalManuscriptByStatus(manuscriptVO);

			List<String> statusList = manuscriptDashboardArticleService.getStatusList(manuscriptVO);

//			List<ManuscriptListVO> manuscriptList = ManuscriptDashboardArticleService.getAwatingPaymentManuscript(manuscriptVO);
			if (manuscriptVO != null && manuscriptVO.getStatusCode().equalsIgnoreCase("All")) {
				if (manuscriptVO != null && manuscriptVO.getSearch() != null && manuscriptVO.getSearch() != "") {
					;
					totalSubmitedManuscript = manuscriptDashboardArticleService
							.getTotalSbmittedManuscriptForDashboarEditordSearch(manuscriptVO);
				} else {
					totalSubmitedManuscript = manuscriptDashboardArticleService
							.getTotalSbmittedManuscriptForDashboardEditor(manuscriptVO);

				}
			} else {
				totalSubmitedManuscript = manuscriptDashboardArticleService
						.getTotalManuscriptStausEICForDashboard(manuscriptVO);

			}

			if (manuscriptVO.getSortBy().equalsIgnoreCase("manuscripCode")) {

				Comparator<ManuscriptDashboardList> compareById = (ManuscriptDashboardList o1,
						ManuscriptDashboardList o2) -> o1.getManuscripCode().compareTo(o2.getManuscripCode());

				if (manuscriptVO.getSort().equalsIgnoreCase("asc"))
					Collections.sort(manuscriptList, compareById);
				else
					Collections.sort(manuscriptList, compareById.reversed());

			}

			if (manuscriptVO.getSortBy().equalsIgnoreCase("manuscripTitle")) {

				Comparator<ManuscriptDashboardList> compareById = (ManuscriptDashboardList o1,
						ManuscriptDashboardList o2) -> o1.getManuscripTitle().compareTo(o2.getManuscripTitle());

				if (manuscriptVO.getSort().equalsIgnoreCase("asc"))
					Collections.sort(manuscriptList, compareById);
				else
					Collections.sort(manuscriptList, compareById.reversed());

			}

			if (manuscriptVO.getSortBy().equalsIgnoreCase("manuScriptType")) {

				Comparator<ManuscriptDashboardList> compareById = (ManuscriptDashboardList o1,
						ManuscriptDashboardList o2) -> o1.getManuScriptType().compareTo(o2.getManuScriptType());

				if (manuscriptVO.getSort().equalsIgnoreCase("asc"))
					Collections.sort(manuscriptList, compareById);
				else
					Collections.sort(manuscriptList, compareById.reversed());

			}

			if (manuscriptVO.getSortBy().equalsIgnoreCase("editorName")) {

				Comparator<ManuscriptDashboardList> compareById = (ManuscriptDashboardList o1,
						ManuscriptDashboardList o2) -> o1.getEditorName().compareTo(o2.getEditorName());

				if (manuscriptVO.getSort().equalsIgnoreCase("asc"))
					Collections.sort(manuscriptList, compareById);
				else
					Collections.sort(manuscriptList, compareById.reversed());

			}

			if (manuscriptVO.getSortBy().equalsIgnoreCase("authorName")) {

				Comparator<ManuscriptDashboardList> compareById = (ManuscriptDashboardList o1,
						ManuscriptDashboardList o2) -> o1.getAuthorName().compareTo(o2.getAuthorName());

				if (manuscriptVO.getSort().equalsIgnoreCase("asc"))
					Collections.sort(manuscriptList, compareById);
				else
					Collections.sort(manuscriptList, compareById.reversed());

			}

			if (manuscriptVO.getSortBy().equalsIgnoreCase("statusCode")) {

				Comparator<ManuscriptDashboardList> compareById = (ManuscriptDashboardList o1,
						ManuscriptDashboardList o2) -> o1.getStatusCode().compareTo(o2.getStatusCode());

				if (manuscriptVO.getSort().equalsIgnoreCase("asc"))
					Collections.sort(manuscriptList, compareById);
				else
					Collections.sort(manuscriptList, compareById.reversed());

			}

			if (manuscriptVO.getSortBy().equalsIgnoreCase("submissionDate")) {

				if (manuscriptVO.getSort().equalsIgnoreCase("asc")) {
					manuscriptList = manuscriptList.stream()
							.sorted(Comparator.comparing(ManuscriptDashboardList::getSubmissionDate,
									Comparator.nullsFirst(Comparator.naturalOrder())))
							.collect(Collectors.toList());
				} else {
					manuscriptList = manuscriptList.stream()
							.sorted(Comparator.comparing(ManuscriptDashboardList::getSubmissionDate,
									Comparator.nullsLast(Comparator.reverseOrder())))
							.collect(Collectors.toList());
				}
			}

			if (manuscriptVO.getSortBy().equalsIgnoreCase("dueDate")) {

				if (manuscriptVO.getSort().equalsIgnoreCase("asc")) {
					manuscriptList = manuscriptList.stream()
							.sorted(Comparator.comparing(ManuscriptDashboardList::getDueDate,
									Comparator.nullsFirst(Comparator.naturalOrder())))
							.collect(Collectors.toList());
				} else {
					manuscriptList = manuscriptList.stream()
							.sorted(Comparator.comparing(ManuscriptDashboardList::getDueDate,
									Comparator.nullsLast(Comparator.reverseOrder())))
							.collect(Collectors.toList());
				}
			}

			obj.put("totalEicManuscript", totalSubmitedManuscript);
			obj.put("data", manuscriptList);
			obj.put("statusList", statusList);
		} catch (Exception e) {
			obj.put("data", "Something Went Wrong");
			log.info("Exception :" + e);
			log.error("Exception : " + e.getMessage());
		}
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}

	@RequestMapping(value = "/getfinaldecision", method = RequestMethod.POST)
	public ResponseEntity<?> getfinaldecisionManusript(@RequestBody ManuscriptDashboardList manuscriptVO) {
		JSONObject obj = new JSONObject();
		try {
			Integer totalfinaldcision = 0;
			Integer totalfinaldcisionsearch = 0;

			List<ManuscriptDashboardList> manuscriptList = manuscriptDashboardArticleService
					.getfinaldecisionByStatus(manuscriptVO);

			if (manuscriptVO != null && manuscriptVO.getStatusCode().equalsIgnoreCase("All")) {
				if (manuscriptVO.getSearch() != null && manuscriptVO.getSearch() != "") {
					totalfinaldcision = manuscriptDashboardArticleService
							.getTotalFinalSbmittedManuscriptForDashboard(manuscriptVO);
					totalfinaldcisionsearch = manuscriptDashboardArticleService
							.getTotalSbmittedManuscriptForDashboardSearch(manuscriptVO);
				} else {
					totalfinaldcision = manuscriptDashboardArticleService
							.getTotalFinalSbmittedManuscriptForDashboard(manuscriptVO);
					totalfinaldcisionsearch = totalfinaldcision;
				}
			} else {
				if (manuscriptVO.getSearch() != null && manuscriptVO.getSearch() != "") {
					totalfinaldcision = manuscriptDashboardArticleService
							.getTotalFinalDecisionManuscriptForDashboardStatusCode(manuscriptVO);
					totalfinaldcisionsearch = manuscriptDashboardArticleService
							.getTotalFinalDecisionManuscriptForDashboardSearchStatusCode(manuscriptVO);
				} else {
					totalfinaldcision = manuscriptDashboardArticleService
							.getTotalFinalDecisionManuscriptForDashboardStatusCode(manuscriptVO);
					totalfinaldcisionsearch = totalfinaldcision;
				}

			}

			List<String> statusList = manuscriptDashboardArticleService.getStatusList(manuscriptVO);

			if (manuscriptVO.getSortBy().equalsIgnoreCase("manuscripCode")) {

				Comparator<ManuscriptDashboardList> compareById = (ManuscriptDashboardList o1,
						ManuscriptDashboardList o2) -> o1.getManuscripCode().compareTo(o2.getManuscripCode());

				if (manuscriptVO.getSort().equalsIgnoreCase("asc"))
					Collections.sort(manuscriptList, compareById);
				else
					Collections.sort(manuscriptList, compareById.reversed());

			}

			if (manuscriptVO.getSortBy().equalsIgnoreCase("manuscripTitle")) {

				Comparator<ManuscriptDashboardList> compareById = (ManuscriptDashboardList o1,
						ManuscriptDashboardList o2) -> o1.getManuscripTitle().compareTo(o2.getManuscripTitle());

				if (manuscriptVO.getSort().equalsIgnoreCase("asc"))
					Collections.sort(manuscriptList, compareById);
				else
					Collections.sort(manuscriptList, compareById.reversed());

			}

			if (manuscriptVO.getSortBy().equalsIgnoreCase("manuScriptType")) {

				Comparator<ManuscriptDashboardList> compareById = (ManuscriptDashboardList o1,
						ManuscriptDashboardList o2) -> o1.getManuScriptType().compareTo(o2.getManuScriptType());

				if (manuscriptVO.getSort().equalsIgnoreCase("asc"))
					Collections.sort(manuscriptList, compareById);
				else
					Collections.sort(manuscriptList, compareById.reversed());

			}

			if (manuscriptVO.getSortBy().equalsIgnoreCase("editorName")) {

				Comparator<ManuscriptDashboardList> compareById = (ManuscriptDashboardList o1,
						ManuscriptDashboardList o2) -> o1.getEditorName().compareTo(o2.getEditorName());

				if (manuscriptVO.getSort().equalsIgnoreCase("asc"))
					Collections.sort(manuscriptList, compareById);
				else
					Collections.sort(manuscriptList, compareById.reversed());

			}

			if (manuscriptVO.getSortBy().equalsIgnoreCase("authorName")) {

				Comparator<ManuscriptDashboardList> compareById = (ManuscriptDashboardList o1,
						ManuscriptDashboardList o2) -> o1.getAuthorName().compareTo(o2.getAuthorName());

				if (manuscriptVO.getSort().equalsIgnoreCase("asc"))
					Collections.sort(manuscriptList, compareById);
				else
					Collections.sort(manuscriptList, compareById.reversed());

			}

			if (manuscriptVO.getSortBy().equalsIgnoreCase("submissionDate")) {

				if (manuscriptVO.getSort().equalsIgnoreCase("asc")) {
					manuscriptList = manuscriptList.stream()
							.sorted(Comparator.comparing(ManuscriptDashboardList::getSubmissionDate,
									Comparator.nullsFirst(Comparator.naturalOrder())))
							.collect(Collectors.toList());
				} else {
					manuscriptList = manuscriptList.stream()
							.sorted(Comparator.comparing(ManuscriptDashboardList::getSubmissionDate,
									Comparator.nullsLast(Comparator.reverseOrder())))
							.collect(Collectors.toList());
				}
			}

			obj.put("totalfinaldcision", totalfinaldcision);
			obj.put("totalfinaldcisionsearch", totalfinaldcisionsearch);
			obj.put("data", manuscriptList);
			obj.put("statusList", statusList);

		} catch (Exception e) {
			obj.put("data", "Something Went Wrong");
			log.info("Exception :" + e);
			log.error("Exception : " + e.getMessage());
		}
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}

	@RequestMapping(value = "/getRevisedManusript", method = RequestMethod.POST)
	public ResponseEntity<?> getRevisedManusript(@RequestBody ManuscriptDashboardList manuscriptVO) {
		JSONObject obj = new JSONObject();
		try {
			Integer totalRevisedManusript = 0;
			Integer totalRevisedManusriptSearch = 0;
			List<ManuscriptDashboardList> manuscriptList = manuscriptDashboardArticleService
					.getManuscriptByStatusByRole(manuscriptVO);
			if (manuscriptVO != null && manuscriptVO.getRoleId() != null && manuscriptVO.getSearch() != null
					&& manuscriptVO.getSearch() != "") {
				totalRevisedManusriptSearch = manuscriptDashboardArticleService
						.getTotalRevisedManuscriptForDashboard(manuscriptVO);
			    totalRevisedManusript=totalRevisedManusriptSearch;
			} else {
				totalRevisedManusript = manuscriptDashboardArticleService
						.getTotalRevisedManuscriptForDashboard(manuscriptVO);
				totalRevisedManusriptSearch = totalRevisedManusript;
			}
			if (manuscriptVO.getSortBy().equalsIgnoreCase("manuscriptTypeDetails.manuscriptTitle")) {

				Comparator<ManuscriptDashboardList> compareById = (ManuscriptDashboardList o1,
						ManuscriptDashboardList o2) -> o1.getManuscripTitle().compareTo(o2.getManuscripTitle());

				if (manuscriptVO.getSort().equalsIgnoreCase("asc"))
					Collections.sort(manuscriptList, compareById);
				else
					Collections.sort(manuscriptList, compareById.reversed());

			}

			if (manuscriptVO.getSortBy().equalsIgnoreCase("manuscriptTypeDetails.manuscriptType")) {

				Comparator<ManuscriptDashboardList> compareById = (ManuscriptDashboardList o1,
						ManuscriptDashboardList o2) -> o1.getManuScriptType().compareTo(o2.getManuScriptType());

				if (manuscriptVO.getSort().equalsIgnoreCase("asc"))
					Collections.sort(manuscriptList, compareById);
				else
					Collections.sort(manuscriptList, compareById.reversed());

			}

			if (manuscriptVO.getSortBy().equalsIgnoreCase("assignedUser")) {

				Comparator<ManuscriptDashboardList> compareById = (ManuscriptDashboardList o1,
						ManuscriptDashboardList o2) -> o1.getEditorName().compareTo(o2.getEditorName());

				if (manuscriptVO.getSort().equalsIgnoreCase("asc"))
					Collections.sort(manuscriptList, compareById);
				else
					Collections.sort(manuscriptList, compareById.reversed());

			}

			if (manuscriptVO.getSortBy().equalsIgnoreCase("user.firstName")) {

				Comparator<ManuscriptDashboardList> compareById = (ManuscriptDashboardList o1,
						ManuscriptDashboardList o2) -> o1.getAuthorName().compareTo(o2.getAuthorName());

				if (manuscriptVO.getSort().equalsIgnoreCase("asc"))
					Collections.sort(manuscriptList, compareById);
				else
					Collections.sort(manuscriptList, compareById.reversed());

			}

			if (manuscriptVO.getSortBy().equalsIgnoreCase("submissionDate")) {

				if (manuscriptVO.getSort().equalsIgnoreCase("asc")) {
					manuscriptList = manuscriptList.stream()
							.sorted(Comparator.comparing(ManuscriptDashboardList::getSubmissionDate,
									Comparator.nullsFirst(Comparator.naturalOrder())))
							.collect(Collectors.toList());
				} else {
					manuscriptList = manuscriptList.stream()
							.sorted(Comparator.comparing(ManuscriptDashboardList::getSubmissionDate,
									Comparator.nullsLast(Comparator.reverseOrder())))
							.collect(Collectors.toList());
				}
			}

			if (manuscriptVO.getSortBy().equalsIgnoreCase("assignDate")) {

				if (manuscriptVO.getSort().equalsIgnoreCase("asc")) {
					manuscriptList = manuscriptList.stream()
							.sorted(Comparator.comparing(ManuscriptDashboardList::getAssignDate,
									Comparator.nullsFirst(Comparator.naturalOrder())))
							.collect(Collectors.toList());
				} else {
					manuscriptList = manuscriptList.stream()
							.sorted(Comparator.comparing(ManuscriptDashboardList::getAssignDate,
									Comparator.nullsLast(Comparator.reverseOrder())))
							.collect(Collectors.toList());
				}
			}

			if (manuscriptVO.getSortBy().equalsIgnoreCase("dueDate")) {

				if (manuscriptVO.getSort().equalsIgnoreCase("asc")) {
					manuscriptList = manuscriptList.stream()
							.sorted(Comparator.comparing(ManuscriptDashboardList::getDueDate,
									Comparator.nullsFirst(Comparator.naturalOrder())))
							.collect(Collectors.toList());
				} else {
					manuscriptList = manuscriptList.stream()
							.sorted(Comparator.comparing(ManuscriptDashboardList::getDueDate,
									Comparator.nullsLast(Comparator.reverseOrder())))
							.collect(Collectors.toList());
				}
			}

			int fromIndex = manuscriptVO.getLimit() * ((manuscriptVO.getPageNo() + 1) - 1);
			int toIndex = manuscriptVO.getLimit() * (manuscriptVO.getPageNo() + 1);
			if (toIndex > manuscriptList.size()) {
				toIndex = manuscriptList.size();
			}
			if (fromIndex > toIndex) {
				fromIndex = toIndex;
			}
			manuscriptList = manuscriptList.subList(fromIndex, toIndex);

			obj.put("totalRevisedManusript", totalRevisedManusript);
			obj.put("totalRevisedManusriptSearch", totalRevisedManusriptSearch);
			obj.put("data", manuscriptList);
		} catch (Exception e) {
			obj.put("data", "Something Went Wrong");
			log.info("Exception :" + e);
			log.error("Exception : " + e.getMessage());
		}
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}

	@RequestMapping(value = "/getPublishedManusript", method = RequestMethod.POST)
	public ResponseEntity<?> getPublishedManusript(@RequestBody ManuscriptDashboardList manuscriptVO) {
		JSONObject obj = new JSONObject();
		try {
			Integer totalManusript = 0;
			List<ManuscriptDashboardList> manuscriptList = manuscriptDashboardArticleService
					.getManuscriptByStatusReport(manuscriptVO);

			if (manuscriptVO.getSearch() != null && manuscriptVO.getSearch() != "") {
				totalManusript = manuscriptDashboardArticleService
						.getTotalManuscriptForDashboardSearchReport(manuscriptVO);
			} else {
				totalManusript = manuscriptDashboardArticleService.getTotalManuscriptForDashboardReport(manuscriptVO);
			}
			log.debug("total Manuscript count for published/rejected ####################### " + totalManusript);
			obj.put("totalPublishedManusript", totalManusript);
			obj.put("data", manuscriptList);
		} catch (Exception e) {
			obj.put("data", "Something Went Wrong");
			log.info("Exception :" + e);
			log.error("Exception : " + e.getMessage());
		}
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}

	@RequestMapping(value = "/getResubmitedManusript", method = RequestMethod.POST)
	public ResponseEntity<?> getResubmitedManusript(@RequestBody ManuscriptDashboardList manuscriptVO) {
		JSONObject obj = new JSONObject();
		try {
			Integer totalResubmitedManusript = 0;
			Integer totalResubmitedManusriptSearch = 0;
			List<ManuscriptDashboardList> manuscriptList = manuscriptDashboardArticleService
					.getManuscriptByStatusByRoleReSubmiited(manuscriptVO);
			if (manuscriptVO.getSearch() != null && manuscriptVO.getSearch() != "") {
				totalResubmitedManusript = manuscriptDashboardArticleService
						.getTotalManuscriptForDashboardByRole(manuscriptVO);
				totalResubmitedManusriptSearch = manuscriptDashboardArticleService
						.getTotalManuscriptForDashboardSearchByRole(manuscriptVO);
			} else {
				totalResubmitedManusript = manuscriptDashboardArticleService
						.getTotalManuscriptForDashboardByRole(manuscriptVO);
				totalResubmitedManusriptSearch = totalResubmitedManusript;
			}

			if (manuscriptVO.getSortBy().equalsIgnoreCase("manuscripCode")) {

				Comparator<ManuscriptDashboardList> compareById = (ManuscriptDashboardList o1,
						ManuscriptDashboardList o2) -> o1.getManuscripCode().compareTo(o2.getManuscripCode());

				if (manuscriptVO.getSort().equalsIgnoreCase("asc"))
					Collections.sort(manuscriptList, compareById);
				else
					Collections.sort(manuscriptList, compareById.reversed());

			}

			if (manuscriptVO.getSortBy().equalsIgnoreCase("manuscripTitle")) {

				Comparator<ManuscriptDashboardList> compareById = (ManuscriptDashboardList o1,
						ManuscriptDashboardList o2) -> o1.getManuscripTitle().compareTo(o2.getManuscripTitle());

				if (manuscriptVO.getSort().equalsIgnoreCase("asc"))
					Collections.sort(manuscriptList, compareById);
				else
					Collections.sort(manuscriptList, compareById.reversed());

			}

			if (manuscriptVO.getSortBy().equalsIgnoreCase("manuScriptType")) {

				Comparator<ManuscriptDashboardList> compareById = (ManuscriptDashboardList o1,
						ManuscriptDashboardList o2) -> o1.getManuScriptType().compareTo(o2.getManuScriptType());

				if (manuscriptVO.getSort().equalsIgnoreCase("asc"))
					Collections.sort(manuscriptList, compareById);
				else
					Collections.sort(manuscriptList, compareById.reversed());

			}

			if (manuscriptVO.getSortBy().equalsIgnoreCase("editorName")) {

				Comparator<ManuscriptDashboardList> compareById = (ManuscriptDashboardList o1,
						ManuscriptDashboardList o2) -> o1.getEditorName().compareTo(o2.getEditorName());

				if (manuscriptVO.getSort().equalsIgnoreCase("asc"))
					Collections.sort(manuscriptList, compareById);
				else
					Collections.sort(manuscriptList, compareById.reversed());

			}

			if (manuscriptVO.getSortBy().equalsIgnoreCase("authorName")) {

				Comparator<ManuscriptDashboardList> compareById = (ManuscriptDashboardList o1,
						ManuscriptDashboardList o2) -> o1.getAuthorName().compareTo(o2.getAuthorName());

				if (manuscriptVO.getSort().equalsIgnoreCase("asc"))
					Collections.sort(manuscriptList, compareById);
				else
					Collections.sort(manuscriptList, compareById.reversed());

			}

			if (manuscriptVO.getSortBy().equalsIgnoreCase("submissionDate")) {

				if (manuscriptVO.getSort().equalsIgnoreCase("asc")) {
					manuscriptList = manuscriptList.stream()
							.sorted(Comparator.comparing(ManuscriptDashboardList::getSubmissionDate,
									Comparator.nullsFirst(Comparator.naturalOrder())))
							.collect(Collectors.toList());
				} else {
					manuscriptList = manuscriptList.stream()
							.sorted(Comparator.comparing(ManuscriptDashboardList::getSubmissionDate,
									Comparator.nullsLast(Comparator.reverseOrder())))
							.collect(Collectors.toList());
				}
			}
			obj.put("totalResubmitedManusript", totalResubmitedManusript);
			obj.put("totalResubmitedManusriptSearch", totalResubmitedManusriptSearch);
			obj.put("data", manuscriptList);
		} catch (Exception e) {
			obj.put("data", "Something Went Wrong");
			log.info("Exception :" + e);
			log.error("Exception : " + e.getMessage());
		}
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}

	@PostMapping("getInAllReviewManuscript")
	public ResponseEntity<?> getInReviewManuscript(@RequestBody ManuscriptDashboardList manuscriptVO) {
		JSONObject obj = new JSONObject();

		Integer totalInReviewManusript = 0;

		try {
			List<ManuscriptListVO> manuscriptList = manuscriptDashboardArticleService
					.getAllInReviewManuscript(manuscriptVO);

			totalInReviewManusript = manuscriptList.size();

			if (manuscriptVO.getSortBy().equalsIgnoreCase("manuscripCode")) {

				Comparator<ManuscriptListVO> compareById = (ManuscriptListVO o1, ManuscriptListVO o2) -> o1
						.getManuscripCode().compareTo(o2.getManuscripCode());

				if (manuscriptVO.getSort().equalsIgnoreCase("asc"))
					Collections.sort(manuscriptList, compareById);
				else
					Collections.sort(manuscriptList, compareById.reversed());

			}

			if (manuscriptVO.getSortBy().equalsIgnoreCase("manuscripTitle")) {

				Comparator<ManuscriptListVO> compareById = (ManuscriptListVO o1, ManuscriptListVO o2) -> o1
						.getManuscripTitle().compareTo(o2.getManuscripTitle());

				if (manuscriptVO.getSort().equalsIgnoreCase("asc"))
					Collections.sort(manuscriptList, compareById);
				else
					Collections.sort(manuscriptList, compareById.reversed());

			}

			if (manuscriptVO.getSortBy().equalsIgnoreCase("manuScriptType")) {

				Comparator<ManuscriptListVO> compareById = (ManuscriptListVO o1, ManuscriptListVO o2) -> o1
						.getManuScriptType().compareTo(o2.getManuScriptType());

				if (manuscriptVO.getSort().equalsIgnoreCase("asc"))
					Collections.sort(manuscriptList, compareById);
				else
					Collections.sort(manuscriptList, compareById.reversed());

			}

			if (manuscriptVO.getSortBy().equalsIgnoreCase("editorName")) {

				Comparator<ManuscriptListVO> compareById = (ManuscriptListVO o1, ManuscriptListVO o2) -> o1
						.getEditorName().compareTo(o2.getEditorName());

				if (manuscriptVO.getSort().equalsIgnoreCase("asc"))
					Collections.sort(manuscriptList, compareById);
				else
					Collections.sort(manuscriptList, compareById.reversed());

			}

			if (manuscriptVO.getSortBy().equalsIgnoreCase("authorName")) {

				Comparator<ManuscriptListVO> compareById = (ManuscriptListVO o1, ManuscriptListVO o2) -> o1
						.getAuthorName().compareTo(o2.getAuthorName());

				if (manuscriptVO.getSort().equalsIgnoreCase("asc"))
					Collections.sort(manuscriptList, compareById);
				else
					Collections.sort(manuscriptList, compareById.reversed());

			}

			if (manuscriptVO.getSortBy().equalsIgnoreCase("submissionDate")) {

				if (manuscriptVO.getSort().equalsIgnoreCase("asc")) {
					manuscriptList = manuscriptList.stream()
							.sorted(Comparator.comparing(ManuscriptListVO::getSubmissionDate,
									Comparator.nullsFirst(Comparator.naturalOrder())))
							.collect(Collectors.toList());
				} else {
					manuscriptList = manuscriptList.stream()
							.sorted(Comparator.comparing(ManuscriptListVO::getSubmissionDate,
									Comparator.nullsLast(Comparator.reverseOrder())))
							.collect(Collectors.toList());
				}
			}

			if (manuscriptVO.getSortBy().equalsIgnoreCase("assignDate")) {

				if (manuscriptVO.getSort().equalsIgnoreCase("asc")) {
					manuscriptList = manuscriptList.stream()
							.sorted(Comparator.comparing(ManuscriptListVO::getAssignDate,
									Comparator.nullsFirst(Comparator.naturalOrder())))
							.collect(Collectors.toList());
				} else {
					manuscriptList = manuscriptList.stream()
							.sorted(Comparator.comparing(ManuscriptListVO::getAssignDate,
									Comparator.nullsLast(Comparator.reverseOrder())))
							.collect(Collectors.toList());
				}
			}

			if (manuscriptVO.getSortBy().equalsIgnoreCase("dueDate")) {

				if (manuscriptVO.getSort().equalsIgnoreCase("asc")) {
					manuscriptList = manuscriptList.stream().sorted(Comparator.comparing(ManuscriptListVO::getDueDate,
							Comparator.nullsFirst(Comparator.naturalOrder()))).collect(Collectors.toList());
				} else {
					manuscriptList = manuscriptList.stream().sorted(Comparator.comparing(ManuscriptListVO::getDueDate,
							Comparator.nullsLast(Comparator.reverseOrder()))).collect(Collectors.toList());
				}
			}

			int fromIndex = manuscriptVO.getLimit() * ((manuscriptVO.getPageNo() + 1) - 1);
			int toIndex = manuscriptVO.getLimit() * (manuscriptVO.getPageNo() + 1);
			if (toIndex > manuscriptList.size()) {
				toIndex = manuscriptList.size();
			}
			if (fromIndex > toIndex) {
				fromIndex = toIndex;
			}
			manuscriptList = manuscriptList.subList(fromIndex, toIndex);

			obj.put("totalInReviewManusript", totalInReviewManusript);
			obj.put("data", manuscriptList);

		} catch (Exception e) {
			obj.put("data", "Something Went Wrong");
			log.info("Exception :" + e);
			log.error("Exception : " + e.getMessage());
		}
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}

	@PostMapping("getAllReviewedManuscript")
	public ResponseEntity<?> getAllReviewedManuscript(@RequestBody ManuscriptDashboardList manuscriptVO) {
		JSONObject obj = new JSONObject();

		Integer totalReviewedManusript = 0;
		try {
			List<ManuscriptListVO> manuscriptList = manuscriptDashboardArticleService
					.getAllReviewedManuscript(manuscriptVO);
			totalReviewedManusript = manuscriptList.size();

			if (manuscriptVO.getSortBy().equalsIgnoreCase("manuscripCode")) {

				Comparator<ManuscriptListVO> compareById = (ManuscriptListVO o1, ManuscriptListVO o2) -> o1
						.getManuscripCode().compareTo(o2.getManuscripCode());

				if (manuscriptVO.getSort().equalsIgnoreCase("asc"))
					Collections.sort(manuscriptList, compareById);
				else
					Collections.sort(manuscriptList, compareById.reversed());

			}

			if (manuscriptVO.getSortBy().equalsIgnoreCase("manuscripTitle")) {

				Comparator<ManuscriptListVO> compareById = (ManuscriptListVO o1, ManuscriptListVO o2) -> o1
						.getManuscripTitle().compareTo(o2.getManuscripTitle());

				if (manuscriptVO.getSort().equalsIgnoreCase("asc"))
					Collections.sort(manuscriptList, compareById);
				else
					Collections.sort(manuscriptList, compareById.reversed());

			}

			if (manuscriptVO.getSortBy().equalsIgnoreCase("manuScriptType")) {

				Comparator<ManuscriptListVO> compareById = (ManuscriptListVO o1, ManuscriptListVO o2) -> o1
						.getManuScriptType().compareTo(o2.getManuScriptType());

				if (manuscriptVO.getSort().equalsIgnoreCase("asc"))
					Collections.sort(manuscriptList, compareById);
				else
					Collections.sort(manuscriptList, compareById.reversed());

			}

			if (manuscriptVO.getSortBy().equalsIgnoreCase("editorName")) {

				Comparator<ManuscriptListVO> compareById = (ManuscriptListVO o1, ManuscriptListVO o2) -> o1
						.getEditorName().compareTo(o2.getEditorName());

				if (manuscriptVO.getSort().equalsIgnoreCase("asc"))
					Collections.sort(manuscriptList, compareById);
				else
					Collections.sort(manuscriptList, compareById.reversed());

			}

			if (manuscriptVO.getSortBy().equalsIgnoreCase("authorName")) {

				Comparator<ManuscriptListVO> compareById = (ManuscriptListVO o1, ManuscriptListVO o2) -> o1
						.getAuthorName().compareTo(o2.getAuthorName());

				if (manuscriptVO.getSort().equalsIgnoreCase("asc"))
					Collections.sort(manuscriptList, compareById);
				else
					Collections.sort(manuscriptList, compareById.reversed());

			}

			if (manuscriptVO.getSortBy().equalsIgnoreCase("submissionDate")) {

				if (manuscriptVO.getSort().equalsIgnoreCase("asc")) {
					manuscriptList = manuscriptList.stream()
							.sorted(Comparator.comparing(ManuscriptListVO::getSubmissionDate,
									Comparator.nullsFirst(Comparator.naturalOrder())))
							.collect(Collectors.toList());
				} else {
					manuscriptList = manuscriptList.stream()
							.sorted(Comparator.comparing(ManuscriptListVO::getSubmissionDate,
									Comparator.nullsLast(Comparator.reverseOrder())))
							.collect(Collectors.toList());
				}
			}

			if (manuscriptVO.getSortBy().equalsIgnoreCase("assignDate")) {

				if (manuscriptVO.getSort().equalsIgnoreCase("asc")) {
					manuscriptList = manuscriptList.stream()
							.sorted(Comparator.comparing(ManuscriptListVO::getAssignDate,
									Comparator.nullsFirst(Comparator.naturalOrder())))
							.collect(Collectors.toList());
				} else {
					manuscriptList = manuscriptList.stream()
							.sorted(Comparator.comparing(ManuscriptListVO::getAssignDate,
									Comparator.nullsLast(Comparator.reverseOrder())))
							.collect(Collectors.toList());
				}
			}

			if (manuscriptVO.getSortBy().equalsIgnoreCase("dueDate")) {

				if (manuscriptVO.getSort().equalsIgnoreCase("asc")) {
					manuscriptList = manuscriptList.stream().sorted(Comparator.comparing(ManuscriptListVO::getDueDate,
							Comparator.nullsFirst(Comparator.naturalOrder()))).collect(Collectors.toList());
				} else {
					manuscriptList = manuscriptList.stream().sorted(Comparator.comparing(ManuscriptListVO::getDueDate,
							Comparator.nullsLast(Comparator.reverseOrder()))).collect(Collectors.toList());
				}
			}

			try {

				int fromIndex = manuscriptVO.getLimit() * ((manuscriptVO.getPageNo() + 1) - 1);
				int toIndex = manuscriptVO.getLimit() * (manuscriptVO.getPageNo() + 1);
				if (toIndex > manuscriptList.size()) {
					toIndex = manuscriptList.size();
				}
				if (fromIndex > toIndex) {
					fromIndex = toIndex;
				}

				manuscriptList = manuscriptList.subList(fromIndex, toIndex);
			} catch (Exception ex) {

				ex.printStackTrace();
			}

			obj.put("totalReviewedManusript", totalReviewedManusript);
			obj.put("data", manuscriptList);

		} catch (Exception e) {
			obj.put("data", "Something Went Wrong");
			log.info("Exception :" + e);
			log.error("Exception : " + e.getMessage());
		}
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}

	@PostMapping("getAllNoResponseManuscript")
	public ResponseEntity<?> getAllNoResponseManuscript(@RequestBody ManuscriptDashboardList manuscriptVO) {
		JSONObject obj = new JSONObject();

		Integer totalNoResponseManusript = 0;

		try {
			List<ManuscriptListVO> manuscriptList = manuscriptDashboardArticleService
					.getAllNoResponseManuscript(manuscriptVO);
			totalNoResponseManusript = manuscriptList.size();
			if (manuscriptVO.getSortBy().equalsIgnoreCase("manuscripCode")) {

				Comparator<ManuscriptListVO> compareById = (ManuscriptListVO o1, ManuscriptListVO o2) -> o1
						.getManuscripCode().compareTo(o2.getManuscripCode());

				if (manuscriptVO.getSort().equalsIgnoreCase("asc"))
					Collections.sort(manuscriptList, compareById);
				else
					Collections.sort(manuscriptList, compareById.reversed());

			}

			if (manuscriptVO.getSortBy().equalsIgnoreCase("manuscripTitle")) {

				Comparator<ManuscriptListVO> compareById = (ManuscriptListVO o1, ManuscriptListVO o2) -> o1
						.getManuscripTitle().compareTo(o2.getManuscripTitle());

				if (manuscriptVO.getSort().equalsIgnoreCase("asc"))
					Collections.sort(manuscriptList, compareById);
				else
					Collections.sort(manuscriptList, compareById.reversed());

			}

			if (manuscriptVO.getSortBy().equalsIgnoreCase("manuScriptType")) {

				Comparator<ManuscriptListVO> compareById = (ManuscriptListVO o1, ManuscriptListVO o2) -> o1
						.getManuScriptType().compareTo(o2.getManuScriptType());

				if (manuscriptVO.getSort().equalsIgnoreCase("asc"))
					Collections.sort(manuscriptList, compareById);
				else
					Collections.sort(manuscriptList, compareById.reversed());

			}

			if (manuscriptVO.getSortBy().equalsIgnoreCase("editorName")) {

				Comparator<ManuscriptListVO> compareById = (ManuscriptListVO o1, ManuscriptListVO o2) -> o1
						.getEditorName().compareTo(o2.getEditorName());

				if (manuscriptVO.getSort().equalsIgnoreCase("asc"))
					Collections.sort(manuscriptList, compareById);
				else
					Collections.sort(manuscriptList, compareById.reversed());

			}

			if (manuscriptVO.getSortBy().equalsIgnoreCase("authorName")) {

				Comparator<ManuscriptListVO> compareById = (ManuscriptListVO o1, ManuscriptListVO o2) -> o1
						.getAuthorName().compareTo(o2.getAuthorName());

				if (manuscriptVO.getSort().equalsIgnoreCase("asc"))
					Collections.sort(manuscriptList, compareById);
				else
					Collections.sort(manuscriptList, compareById.reversed());

			}

			if (manuscriptVO.getSortBy().equalsIgnoreCase("submissionDate")) {

				if (manuscriptVO.getSort().equalsIgnoreCase("asc")) {
					Collections.sort(manuscriptList, new Comparator<ManuscriptListVO>() {
						public int compare(ManuscriptListVO o1, ManuscriptListVO o2) {
							if (o1.getSubmissionDate() == null || o2.getSubmissionDate() == null)
								return 0;
							return o1.getSubmissionDate().compareTo(o2.getSubmissionDate());
						}
					});
				} else {
					Collections.sort(manuscriptList, new Comparator<ManuscriptListVO>() {
						public int compare(ManuscriptListVO o1, ManuscriptListVO o2) {
							if (o1.getSubmissionDate() == null || o2.getSubmissionDate() == null)
								return 0;
							return o2.getSubmissionDate().compareTo(o1.getSubmissionDate());
						}
					});
				}
			}

			if (manuscriptVO.getSortBy().equalsIgnoreCase("dueDate")) {

				if (manuscriptVO.getSort().equalsIgnoreCase("asc")) {
					manuscriptList = manuscriptList.stream().sorted(Comparator.comparing(ManuscriptListVO::getDueDate,
							Comparator.nullsFirst(Comparator.naturalOrder()))).collect(Collectors.toList());
				} else {
					manuscriptList = manuscriptList.stream().sorted(Comparator.comparing(ManuscriptListVO::getDueDate,
							Comparator.nullsLast(Comparator.reverseOrder()))).collect(Collectors.toList());
				}

			}

			if (manuscriptVO.getSortBy().equalsIgnoreCase("assignDate")) {

				if (manuscriptVO.getSort().equalsIgnoreCase("asc")) {
					manuscriptList = manuscriptList.stream()
							.sorted(Comparator.comparing(ManuscriptListVO::getAssignDate,
									Comparator.nullsFirst(Comparator.naturalOrder())))
							.collect(Collectors.toList());
				} else {
					manuscriptList = manuscriptList.stream()
							.sorted(Comparator.comparing(ManuscriptListVO::getAssignDate,
									Comparator.nullsLast(Comparator.reverseOrder())))
							.collect(Collectors.toList());
				}

			}

			int fromIndex = manuscriptVO.getLimit() * ((manuscriptVO.getPageNo() + 1) - 1);
			int toIndex = manuscriptVO.getLimit() * (manuscriptVO.getPageNo() + 1);
			if (toIndex > manuscriptList.size()) {
				toIndex = manuscriptList.size();
			}
			if (fromIndex > toIndex) {
				fromIndex = toIndex;
			}

			manuscriptList = manuscriptList.subList(fromIndex, toIndex);

			obj.put("totalNoResponseManusript", totalNoResponseManusript);
			obj.put("data", manuscriptList);

		} catch (Exception e) {
			obj.put("data", "Something Went Wrong");
			log.info("Exception :" + e);
			log.error("Exception : " + e.getMessage());
		}
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}

	//////////////////////////////////////////////////////////////////// EDITOR
	//////////////////////////////////////////////////////////////////// /////////////////////////////////////////
	@RequestMapping(value = "/getAllAssignedEditor", method = RequestMethod.POST)
	public ResponseEntity<?> getAllAssignedEditor(@RequestBody ManuscriptDashboardList manuscriptVO) {
		JSONObject obj = new JSONObject();
		try {
			Integer totalAssignManusript = 0;
			// Integer totalAssignManusriptSearch = 0;
			List<ManuscriptListVO> manuscriptList = manuscriptDashboardArticleService
					.getAssignManuscriptData(manuscriptVO);

//			For total count
			if (manuscriptVO.getSearch() != null) {
				totalAssignManusript = manuscriptDashboardArticleService.getAssignManuscriptDatacount(manuscriptVO);

			}
//			Old code for dynamic count

//			if (manuscriptVO.getSearch() != null && manuscriptVO.getSearch() != "") {
//				 totalAssignManusript= manuscriptDashboardArticleService.getAssignManuscriptDatacountSearch(manuscriptVO);
//			} else {
//				 totalAssignManusript= manuscriptDashboardArticleService.getAssignManuscriptDatacount(manuscriptVO);
//
//			}

			if (manuscriptVO.getSortBy().equalsIgnoreCase("manuscripCode")) {

				Comparator<ManuscriptListVO> compareById = (ManuscriptListVO o1, ManuscriptListVO o2) -> o1
						.getManuscripCode().compareTo(o2.getManuscripCode());

				if (manuscriptVO.getSort().equalsIgnoreCase("asc"))
					Collections.sort(manuscriptList, compareById);
				else
					Collections.sort(manuscriptList, compareById.reversed());

			}

			if (manuscriptVO.getSortBy().equalsIgnoreCase("manuscripTitle")) {

				Comparator<ManuscriptListVO> compareById = (ManuscriptListVO o1, ManuscriptListVO o2) -> o1
						.getManuscripTitle().compareTo(o2.getManuscripTitle());

				if (manuscriptVO.getSort().equalsIgnoreCase("asc"))
					Collections.sort(manuscriptList, compareById);
				else
					Collections.sort(manuscriptList, compareById.reversed());

			}

			if (manuscriptVO.getSortBy().equalsIgnoreCase("manuScriptType")) {

				Comparator<ManuscriptListVO> compareById = (ManuscriptListVO o1, ManuscriptListVO o2) -> o1
						.getManuScriptType().compareTo(o2.getManuScriptType());

				if (manuscriptVO.getSort().equalsIgnoreCase("asc"))
					Collections.sort(manuscriptList, compareById);
				else
					Collections.sort(manuscriptList, compareById.reversed());

			}

			if (manuscriptVO.getSortBy().equalsIgnoreCase("editorName")) {

				Comparator<ManuscriptListVO> compareById = (ManuscriptListVO o1, ManuscriptListVO o2) -> o1
						.getEditorName().compareTo(o2.getEditorName());

				if (manuscriptVO.getSort().equalsIgnoreCase("asc"))
					Collections.sort(manuscriptList, compareById);
				else
					Collections.sort(manuscriptList, compareById.reversed());

			}

			if (manuscriptVO.getSortBy().equalsIgnoreCase("authorName")) {

				Comparator<ManuscriptListVO> compareById = (ManuscriptListVO o1, ManuscriptListVO o2) -> o1
						.getAuthorName().compareTo(o2.getAuthorName());

				if (manuscriptVO.getSort().equalsIgnoreCase("asc"))
					Collections.sort(manuscriptList, compareById);
				else
					Collections.sort(manuscriptList, compareById.reversed());

			}

			if (manuscriptVO.getSortBy().equalsIgnoreCase("submissionDate")) {

				if (manuscriptVO.getSort().equalsIgnoreCase("asc")) {
					manuscriptList = manuscriptList.stream()
							.sorted(Comparator.comparing(ManuscriptListVO::getSubmissionDate,
									Comparator.nullsFirst(Comparator.naturalOrder())))
							.collect(Collectors.toList());
				} else {
					manuscriptList = manuscriptList.stream()
							.sorted(Comparator.comparing(ManuscriptListVO::getSubmissionDate,
									Comparator.nullsLast(Comparator.reverseOrder())))
							.collect(Collectors.toList());
				}
			}
			obj.put("totalAssignManusript", totalAssignManusript);
			// obj.put("totalAssignManusriptSearch", totalAssignManusriptSearch);
			obj.put("data", manuscriptList);
		} catch (Exception e) {
			obj.put("data", "Something Went Wrong");
			log.info("Exception :" + e);
			log.error("Exception : " + e.getMessage());
		}
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}

	@RequestMapping(value = "/getTotalManusriptEditor", method = RequestMethod.POST)
	public ResponseEntity<?> getTotalManusriptEditor(@RequestBody ManuscriptDashboardList manuscriptVO) {
		JSONObject obj = new JSONObject();
		try {
			manuscriptVO.getManuscriptStatusId().remove(manuscriptVO.getManuscriptStatusId().indexOf(2));
			List<ManuscriptDashboardList> manuscriptList = manuscriptDashboardArticleService
					.getTotalManuscriptByStatusForEditor(manuscriptVO);

			List<ManuscriptDashboardList> sortedList = manuscriptDashboardArticleService
					.sortManuscriptList(manuscriptList, manuscriptVO.getSortBy(), manuscriptVO.getSort());
			Integer totalCount = manuscriptDashboardArticleService.getTotalEditorDashboard(manuscriptVO);
			int pageSize = manuscriptVO.getLimit();
			int currentPage = manuscriptVO.getPageNo();

			int fromIndex = pageSize * currentPage;
			int toIndex = Math.min(pageSize * (currentPage + 1), sortedList.size());

			if (fromIndex > toIndex) {
				fromIndex = toIndex;
			}
			obj.put("totalEditorManuscript", totalCount);
			obj.put("totalEditorManuscriptSearch", sortedList.size());

			// Corrected the assignment of sublist to the sortedList
			sortedList = sortedList.subList(fromIndex, toIndex);

			obj.put("data", sortedList);
			obj.put("statusList", manuscriptDashboardArticleService.getStatusList(manuscriptVO));
		} catch (Exception e) {
			obj.put("data", "Something Went Wrong");
			log.info("Exception: " + e);
			log.error("Exception: " + e.getMessage());
		}
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}

	@RequestMapping(value = "/getResubmitedManusriptEditor", method = RequestMethod.POST)
	public ResponseEntity<?> getResubmitedManusriptEditor(@RequestBody ManuscriptDashboardList manuscriptVO) {
		JSONObject obj = new JSONObject();
		try {
			Integer totalResubmitedManusriptEditor = 0;
			Integer totalResubmitedManusriptEditorSearch = 0;
			List<ManuscriptDashboardList> manuscriptList = manuscriptDashboardArticleService
					.getManuscriptByStatusByUser(manuscriptVO);
//			List<ManuscriptListVO> manuscriptList = ManuscriptDashboardArticleService.getAwatingPaymentManuscript(manuscriptVO);
			if (manuscriptVO.getSearch() != null && manuscriptVO.getSearch() != "") {
				totalResubmitedManusriptEditor = manuscriptDashboardArticleService
						.getTotalSbmittedManuscriptForDashboardByUser(manuscriptVO);
				totalResubmitedManusriptEditorSearch = manuscriptDashboardArticleService
						.getTotalSbmittedManuscriptForDashboardSearchByUser(manuscriptVO);
			} else {
				totalResubmitedManusriptEditor = manuscriptDashboardArticleService
						.getTotalSbmittedManuscriptForDashboardByUser(manuscriptVO);
				totalResubmitedManusriptEditorSearch = totalResubmitedManusriptEditor;
			}

			if (manuscriptVO.getSortBy().equalsIgnoreCase("manuscripCode")) {

				Comparator<ManuscriptDashboardList> compareById = (ManuscriptDashboardList o1,
						ManuscriptDashboardList o2) -> o1.getManuscripCode().compareTo(o2.getManuscripCode());

				if (manuscriptVO.getSort().equalsIgnoreCase("asc"))
					Collections.sort(manuscriptList, compareById);
				else
					Collections.sort(manuscriptList, compareById.reversed());

			}

			if (manuscriptVO.getSortBy().equalsIgnoreCase("manuscripTitle")) {

				Comparator<ManuscriptDashboardList> compareById = (ManuscriptDashboardList o1,
						ManuscriptDashboardList o2) -> o1.getManuscripTitle().compareTo(o2.getManuscripTitle());

				if (manuscriptVO.getSort().equalsIgnoreCase("asc"))
					Collections.sort(manuscriptList, compareById);
				else
					Collections.sort(manuscriptList, compareById.reversed());

			}

			if (manuscriptVO.getSortBy().equalsIgnoreCase("manuScriptType")) {

				Comparator<ManuscriptDashboardList> compareById = (ManuscriptDashboardList o1,
						ManuscriptDashboardList o2) -> o1.getManuScriptType().compareTo(o2.getManuScriptType());

				if (manuscriptVO.getSort().equalsIgnoreCase("asc"))
					Collections.sort(manuscriptList, compareById);
				else
					Collections.sort(manuscriptList, compareById.reversed());

			}

			if (manuscriptVO.getSortBy().equalsIgnoreCase("editorName")) {

				Comparator<ManuscriptDashboardList> compareById = (ManuscriptDashboardList o1,
						ManuscriptDashboardList o2) -> o1.getEditorName().compareTo(o2.getEditorName());

				if (manuscriptVO.getSort().equalsIgnoreCase("asc"))
					Collections.sort(manuscriptList, compareById);
				else
					Collections.sort(manuscriptList, compareById.reversed());

			}

			if (manuscriptVO.getSortBy().equalsIgnoreCase("authorName")) {

				Comparator<ManuscriptDashboardList> compareById = (ManuscriptDashboardList o1,
						ManuscriptDashboardList o2) -> o1.getAuthorName().compareTo(o2.getAuthorName());

				if (manuscriptVO.getSort().equalsIgnoreCase("asc"))
					Collections.sort(manuscriptList, compareById);
				else
					Collections.sort(manuscriptList, compareById.reversed());

			}

			if (manuscriptVO.getSortBy().equalsIgnoreCase("submissionDate")) {

				if (manuscriptVO.getSort().equalsIgnoreCase("asc")) {
					manuscriptList = manuscriptList.stream()
							.sorted(Comparator.comparing(ManuscriptDashboardList::getSubmissionDate,
									Comparator.nullsFirst(Comparator.naturalOrder())))
							.collect(Collectors.toList());
				} else {
					manuscriptList = manuscriptList.stream()
							.sorted(Comparator.comparing(ManuscriptDashboardList::getSubmissionDate,
									Comparator.nullsLast(Comparator.reverseOrder())))
							.collect(Collectors.toList());
				}
			}
			obj.put("totalResubmitedManusriptEditor", totalResubmitedManusriptEditor);
			obj.put("totalResubmitedManusriptEditorSearch", totalResubmitedManusriptEditorSearch);
			obj.put("data", manuscriptList);
		} catch (Exception e) {
			obj.put("data", "Something Went Wrong");
			log.info("Exception :" + e);
			log.error("Exception : " + e.getMessage());
		}
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}

	@RequestMapping(value = "/getRevisedManusriptEditor", method = RequestMethod.POST)
	public ResponseEntity<?> getRevisedManusriptEditor(@RequestBody ManuscriptDashboardList manuscriptVO) {
		JSONObject obj = new JSONObject();
		try {
			Integer totalRevisedManusriptEditor = 0;
			Integer totalRevisedManusriptEditorSearch = 0;
			List<ManuscriptDashboardList> manuscriptList = manuscriptDashboardArticleService
					.getManuscriptByStatusByUser(manuscriptVO);

			if (manuscriptVO.getSearch() != null && manuscriptVO.getSearch() != "") {
				totalRevisedManusriptEditor = manuscriptDashboardArticleService
						.getTotalSbmittedManuscriptForDashboardByUser(manuscriptVO);
				totalRevisedManusriptEditorSearch = manuscriptDashboardArticleService
						.getTotalSbmittedManuscriptForDashboardSearchByUser(manuscriptVO);
			} else {
				totalRevisedManusriptEditor = manuscriptDashboardArticleService
						.getTotalSbmittedManuscriptForDashboardByUser(manuscriptVO);
				totalRevisedManusriptEditorSearch = totalRevisedManusriptEditor;
			}

			if (manuscriptVO.getSortBy().equalsIgnoreCase("manuscripCode")) {

				Comparator<ManuscriptDashboardList> compareById = (ManuscriptDashboardList o1,
						ManuscriptDashboardList o2) -> o1.getManuscripCode().compareTo(o2.getManuscripCode());

				if (manuscriptVO.getSort().equalsIgnoreCase("asc"))
					Collections.sort(manuscriptList, compareById);
				else
					Collections.sort(manuscriptList, compareById.reversed());

			}

			if (manuscriptVO.getSortBy().equalsIgnoreCase("manuscripTitle")) {

				Comparator<ManuscriptDashboardList> compareById = (ManuscriptDashboardList o1,
						ManuscriptDashboardList o2) -> o1.getManuscripTitle().compareTo(o2.getManuscripTitle());

				if (manuscriptVO.getSort().equalsIgnoreCase("asc"))
					Collections.sort(manuscriptList, compareById);
				else
					Collections.sort(manuscriptList, compareById.reversed());

			}

			if (manuscriptVO.getSortBy().equalsIgnoreCase("manuScriptType")) {

				Comparator<ManuscriptDashboardList> compareById = (ManuscriptDashboardList o1,
						ManuscriptDashboardList o2) -> o1.getManuScriptType().compareTo(o2.getManuScriptType());

				if (manuscriptVO.getSort().equalsIgnoreCase("asc"))
					Collections.sort(manuscriptList, compareById);
				else
					Collections.sort(manuscriptList, compareById.reversed());

			}

			if (manuscriptVO.getSortBy().equalsIgnoreCase("editorName")) {

				Comparator<ManuscriptDashboardList> compareById = (ManuscriptDashboardList o1,
						ManuscriptDashboardList o2) -> o1.getEditorName().compareTo(o2.getEditorName());

				if (manuscriptVO.getSort().equalsIgnoreCase("asc"))
					Collections.sort(manuscriptList, compareById);
				else
					Collections.sort(manuscriptList, compareById.reversed());

			}

			if (manuscriptVO.getSortBy().equalsIgnoreCase("authorName")) {

				Comparator<ManuscriptDashboardList> compareById = (ManuscriptDashboardList o1,
						ManuscriptDashboardList o2) -> o1.getAuthorName().compareTo(o2.getAuthorName());

				if (manuscriptVO.getSort().equalsIgnoreCase("asc"))
					Collections.sort(manuscriptList, compareById);
				else
					Collections.sort(manuscriptList, compareById.reversed());

			}

			if (manuscriptVO.getSortBy().equalsIgnoreCase("submissionDate")) {

				if (manuscriptVO.getSort().equalsIgnoreCase("asc")) {
					manuscriptList = manuscriptList.stream()
							.sorted(Comparator.comparing(ManuscriptDashboardList::getSubmissionDate,
									Comparator.nullsFirst(Comparator.naturalOrder())))
							.collect(Collectors.toList());
				} else {
					manuscriptList = manuscriptList.stream()
							.sorted(Comparator.comparing(ManuscriptDashboardList::getSubmissionDate,
									Comparator.nullsLast(Comparator.reverseOrder())))
							.collect(Collectors.toList());
				}
			}
			obj.put("totalRevisedManusriptEditor", totalRevisedManusriptEditor);
			obj.put("totalRevisedManusriptEditorSearch", totalRevisedManusriptEditorSearch);
			obj.put("data", manuscriptList);
		} catch (Exception e) {
			obj.put("data", "Something Went Wrong");
			log.info("Exception :" + e);
			log.error("Exception : " + e.getMessage());
		}
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}

	@GetMapping("sentNoresponseNotification")
	public ResponseEntity<?> noResponseReminderEditor() {
		log.info("Reminder for Editor/EIC");
		try {

			manuscriptDashboardArticleService.getNoResponseManuscriptNotification();
			return new ResponseEntity<>("Successfully Run", HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

}