package com.digi.uniprr.serviceImpl;

import java.io.BufferedOutputStream;

import com.digi.uniprr.model.*;
import com.digi.uniprr.repository.*;
import com.digi.uniprr.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.persistence.*;
import javax.transaction.Transactional;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;

import com.digi.uniprr.VO.ArticleInfoAPI;
import com.digi.uniprr.VO.AuthorArticleDetails;
import com.digi.uniprr.VO.AuthorDashboardCountVO;
import com.digi.uniprr.VO.AuthorDashboardVO;
import com.digi.uniprr.VO.AuthorVO;
import com.digi.uniprr.VO.AuthoritiesHistoryVo;
import com.digi.uniprr.VO.EICDashBoardListVO;
import com.digi.uniprr.VO.EditorDashboardVO;
import com.digi.uniprr.VO.EmailTriggerVO;
import com.digi.uniprr.VO.IDVO;
import com.digi.uniprr.VO.ImageVO;
import com.digi.uniprr.VO.ImpactArticleDTO;
import com.digi.uniprr.VO.ManagingEditorVO;
import com.digi.uniprr.VO.ManuScriptVO;
import com.digi.uniprr.VO.ManuscriptCoAuthorVO;
import com.digi.uniprr.VO.ManuscriptDataApiVo;
import com.digi.uniprr.VO.ManuscriptDetailForJleVO;
import com.digi.uniprr.VO.ManuscriptEditorVO;
import com.digi.uniprr.VO.ManuscriptJournalSubjectVo;
import com.digi.uniprr.VO.ManuscriptListVO;
import com.digi.uniprr.VO.ManuscriptManagingEditorVO;
import com.digi.uniprr.VO.ManuscriptReportDataVO;
import com.digi.uniprr.VO.ManuscriptSimilarityVo;
import com.digi.uniprr.VO.ManuscriptTypeJournalVO;
import com.digi.uniprr.VO.ManuscriptTypeTitleAbstractVO;
import com.digi.uniprr.VO.ManuscriptsDashboardCountVO;
import com.digi.uniprr.VO.ManuscriptsEditorDashboardCountVO;
import com.digi.uniprr.VO.ReviewerDataWithCommentVO;
import com.digi.uniprr.VO.ReviewerDecisionVO;
import com.digi.uniprr.VO.ReviewerReportDataVO;
import com.digi.uniprr.VO.UserJournalManuscriptVo;
import com.digi.uniprr.enums.Salutation;
import com.digi.uniprr.jdbcConfig.RawJDBC;
import com.digi.uniprr.model.logging.ErrorLog;
import com.digi.uniprr.service.AuthoritiesHistoryService;
import com.digi.uniprr.service.EmailTriggerService;
import com.digi.uniprr.service.HttpService;
import com.digi.uniprr.service.ManuScriptReviewerService;
import com.digi.uniprr.service.ManuScriptService;
import com.digi.uniprr.service.ManuscriptDashboardArticleService;
import com.digi.uniprr.service.ManuscriptEditorService;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class ManuScriptServiceImpl implements ManuScriptService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	@PersistenceContext
	EntityManager entityManager1;
	@PersistenceContext
	EntityManager entityManager2;
	@PersistenceContext
	EntityManager entityManager3;
	@Value("${uniprrFolderPath}")
	private String uniprrFolderPath;
	@Value("${unitouch.url}")
	private String unitouchUrl;
	@Autowired
	private HttpService httpService;
	@Value("${awsFileIp}")
	private String awsFilePath;
	@Value("${awsIp}")
	private String sftpIp;
	@Value("${awsPort}")
	private int sftpPort;
	@Value("${awsHost}")
	private String sftpUser;
	@Value("${awsPassword}")
	private String sftpPassword;
	@Value("${medknowPathFile}")
	private String medknowSftpPath;
	@Value("${medknowLocalPath}")
	private String medknowLocalPath;
	@Value("${spring.mail.username}")
	private String springMailUsername;
	@Value("${support.email}")
	private String supportEmail;
	@Value("${supportCC.email}")
	private String supportCCEmail;
	@Value("${environment.type}")
	private String environmentType;
	@Value("${jle.integration.url}")
	private String jleIntegratioUrl;
	@Value("${enable.unitouch.integration}")
	private boolean enableUnitouchIntegration;
	@Value("${vendor.sftp.host}")
	private String vendorSftpIp;
	@Value("${vendor.sftp.port}")
	private int vendorSftpPort;
	@Value("${vendor.sftp.user}")
	private String vendorSftpUser;
	@Value("${vendor.sftp.password}")
	private String vendorSftpPassword;
	@Value("${vendor.sftp.path}")
	private String vendorSftpPath;
	@Autowired
	private JournalsRepo journalsRepo;
	@Autowired
	private ManuScriptRepo manuScriptRepo;
	@Autowired
	private ManuScriptForDashboardRepo manuScriptForDashboardRepo;
	@Autowired
	private WorkflowRoleRepo workflowRoleRepo;
	@Autowired
	private ManuScriptForStatusRepo manuScriptForStatusRepo;
	@Autowired
	private AuthoritiesHistoryRepo authoritiesHistoryRepo;
	@Autowired
	private ManuScriptReviewerRepo manuScriptReviewerRepo;
	@Autowired
	private ManuscriptDashboardArticleService manuscriptDashboardArticleService;
	@Autowired
	private EmailTriggerService emailTriggerService;
	@Autowired
	private AuthoritiesHistoryService authoritiesHistoryService;
	@Autowired
	private ManuscriptEditorService manuscriptEditorService;
	@Autowired
	private ManuScriptReviewerService manuScriptReviewerService;
	@Autowired
	private StatusMasterRepo statusMasterRepo;
	@Autowired
	private UserRepo userRepo;
	@Autowired
	private ManuscriptTypeDetailsRepo manuscriptTypeDetailsRepo;
	@Autowired
	private ManuScriptCoAuthorRepo manuScriptCoAuthorRepo;
	@Autowired
	private ManuscriptTypeJournalsRepo manuscriptTypeJournalsRepo;
	@Autowired
	private ManuscriptManagingEditorRepo manuscriptManagingEditorRepo;
	@Autowired
	private ManuscriptEditorRepo manuscriptEditorRepo;
	@Autowired
	private CountriesRepo countriesRepo;
	@Autowired
	private ManscriptScoreRepo manscriptScoreRepo;
	@Autowired
	private ManuScriptFileRepo manuScriptFileRepo;
	@Autowired
	private SendMailUtils mailUtils;
	@Autowired
	private MailQueueRepo mailQueueRepo;
	@Autowired
	private ManuscriptVisualizationRepo manuscriptVisualizationRepo;
	@Autowired
	private JournalManuscriptTypeChargeRepo journalManuscriptTypeChargeRepo;
	@Autowired
	private ManuscriptJournalSubjectRepo manuscriptJournalSubjectRepo;
	@Autowired
	private UserJournalRepo userJournalRepo;
	@Autowired
	private JournalPoliciesRepo journalPoliciesRepo;
	@Autowired
	private OtherMailRepo otherMailRepo;
	@Autowired
	private Environment environment;
	@Autowired
	private ManuscriptEditorCommentRepo manuscriptEditorCommentRepo;
	@Autowired
	private ErrorLogRepo errorLogRepo;

	@Autowired
	private DevelopedCountriesByManuscriptTypeRepo developedCountriesByManuscriptTypeRepo;
	
	@Autowired
	private ManuscriptCloneRepository manuscriptCloneRepo;
	

	@Override
	public List<ManuScript> getManuScriptList() {
		return manuScriptRepo.findAll();
	}

	@Override
	public ManuScript findManuscriptById(int manuscriptId) {
		return manuScriptRepo.getById(manuscriptId);
	}

	@Override
	public ManuScript findManuscriptByCode(String manuscriptCode) {
		return manuScriptRepo.getManuscriptByManuscriptCode(manuscriptCode);
	}

	@Override
	public ManuScript createManuScript(ManuScript manuScript) {
		manuScript.setManuscriptStatusId(1);
		manuScript.setManuscriptVersion(0);
		manuScript.setIsActive(0);
		manuScript.setCreatedBy(manuScript.getAuthorId());
		Integer countryId = userRepo.getOne(manuScript.getAuthorId()).getCountryId();
		if (countryId != null) {
			String countryName = countriesRepo.getOne(countryId).getName();
			manuScript.setManuscriptOrigin(countryName);
		}
		manuScriptRepo.save(manuScript);
		return manuScript;
	}

	@Override
	public String updateManuScript(ManuScript manuScript) {
		manuScriptRepo.save(manuScript);
		return "Updated successfully";
	}

	@Override
	public String deleteManuScriptById(Integer id) {
		manuScriptRepo.deleteById(id);
		return "Deleted successfully";
	}

	@Override
	public boolean isExist(Integer id) {
		return manuScriptRepo.existsById(id);
	}

	@Override
	public List<ManuScript> getManuScriptListById(Integer id) {
		return manuScriptRepo.getManuScripById(id);
	}

	@Override
	public void updateStage(ManuScript manuScript) {
		manuScriptRepo.updateStage(manuScript.getStatusMaster().getId(), manuScript.getManuScriptId());

	}

	@Override
	public List<ManuScript> getListByStatusId(Integer manuscriptStatus) {
		return manuScriptRepo.getListByStatusId(manuscriptStatus);
	}

	@Override
	public List<ManuScript> trackManuScript(Integer id) {
		return manuScriptRepo.trackManuScript(id);
	}

	@Override
	public List<ManuScript> gettrackManuScriptList(Integer id, Integer journalId, Date submissionDate) {
		return manuScriptRepo.gettrackManuScript(id, journalId, submissionDate);
	}

	@Override
	public List<ManuScript> gettrackManuScriptListDate(String submissionDateTo, String submissionDateFrom) {
		return manuScriptRepo.gettrackManuScriptDate(submissionDateTo, submissionDateFrom);
	}

	@Override
	public List<ManuScript> gettrackManuScriptListTitle(String manuscriptTitle) {
		return manuScriptRepo.gettrackManuScriptTitle(manuscriptTitle);
	}

	@Override
	public List<ManuScript> gettrackManuScriptListCode(String manuscriptCode) {
		return manuScriptRepo.gettrackManuScriptCode(manuscriptCode);
	}

	@Override
	public List<ManuScript> gettrackManuScriptJtitle(String journalTitle) {
		return manuScriptRepo.gettrackJTitle(journalTitle);
	}

	@Override
	public List<ManuScript> getAllManuScriptRecord() {
		return manuScriptRepo.findAll();
	}

	@Override
	public List<ManuScript> reviewerInvitationList(Integer reviewerId) {
		return manuScriptRepo.reviewerInvitationList(reviewerId);
	}

	@Override
	public List<ManuScript> reviewerReviewList(Integer reviewerId) {
		return manuScriptRepo.reviewerReviewList(reviewerId);
	}

	@Override
	public List<ManuScript> reviewerRevisionList(Integer reviewerId) {
		return manuScriptRepo.reviewerRevisionList(reviewerId);
	}

	@Override
	public List<ManuScript> reviewerCompletedList(Integer reviewerId) {
		return manuScriptRepo.reviewerCompletedList(reviewerId);
	}

	@Override
	public void reviewerAccept(IDVO vo) {
		manuScriptRepo.reviewerAccept(vo.getId());
	}

	@Override
	public void reviewerReject(IDVO vo) {
		manuScriptRepo.rejectReviewer(vo.getId());

	}

	@Override
	public List<AuthorDashboardVO> authorTask(Integer authorId) {
		List<ManuScript> list = manuScriptRepo.authorTask(authorId);
		List<AuthorDashboardVO> authorDashboardVOList = mapAuthorDashboardProperties(list);
		return authorDashboardVOList;
	}

	@Override
	public List<AuthorDashboardVO> authorUnderReview(Integer authorId) {
		List<ManuScript> list = manuScriptRepo.authorUnderReview(authorId);
		List<AuthorDashboardVO> authorDashboardVOList = mapAuthorDashboardProperties(list);
		return authorDashboardVOList;
	}

	@Override
	public List<AuthorDashboardVO> authorManuscriptAccepted(Integer authorId) {
		List<ManuScript> list = manuScriptRepo.authorManuscriptAccepted(authorId);
		List<AuthorDashboardVO> authorDashboardVOList = mapAuthorDashboardProperties(list);
		return authorDashboardVOList;
	}

	@Override
	public List<AuthorDashboardVO> authorManuscriptRejected(Integer authorId) {
		List<ManuScript> list = manuScriptRepo.authorManuscriptRejected(authorId);
		List<AuthorDashboardVO> authorDashboardVOList = mapAuthorDashboardProperties(list);
		return authorDashboardVOList;
	}

	public List<AuthorDashboardVO> mapAuthorDashboardProperties(List<ManuScript> list) {
		List<AuthorDashboardVO> voList = new ArrayList<AuthorDashboardVO>();
		for (ManuScript manuScript : list) {
			AuthorDashboardVO vo = new AuthorDashboardVO();
			vo.setManuscriptId(manuScript.getManuScriptId());
// vo.setJournalCode(manuScript.getJournal().getJournalCode());
			vo.setManuscriptCode(manuScript.getManuscriptCode());
			vo.setManuscriptTitle(manuScript.getManuscriptTitle());
			vo.setCreatedOn(manuScript.getCreatedOn());
			vo.setManuscriptStatus(manuScript.getStatusMaster().getStatusDesc());
			voList.add(vo);
		}

		return voList;
	}

	// Editor Dashboard
	@Override
	public List<ManuScript> getEditorSubmitted(Integer id) {
		return manuScriptRepo.getEditorSubmitted(id);
	}

	@Override
	public List<ManuScript> getEditorReSubmitted(Integer id) {

		return manuScriptRepo.getEditorReSubmitted(id);
	}

	@Override
	public List<ManuScriptReviewer> getEditorSentForReview(Integer id) {
		return manuScriptRepo.getEditorSentForReview(id);
	}

	@Override
	public List<ManuScriptReviewer> getEditorAllList(Integer id) {
		return manuScriptRepo.getEditorAllList(id);
	}

	public String updateManuScriptEditor(ManuscriptEditorVO manuscriptEditorVO,
			JournalManuscriptTypeCharge journalManuscriptTypeCharge) {
		try {
			ManuScript manuscript = manuScriptRepo.getById(manuscriptEditorVO.getManuscriptId());

			if (manuscript == null) {
				return "Manuscript not found";
			}

			int userCountryStatus = Integer
					.parseInt(manuScriptRepo.getDevelopingCountryStatus(manuscript.getAuthorId()));
			String waveOff = manuscriptEditorVO.getWaveOff() != null ? manuscriptEditorVO.getWaveOff() : "";
			if (journalManuscriptTypeCharge != null
					&& "Yes".equalsIgnoreCase(journalManuscriptTypeCharge.getIsChargesRequired())
					&& "Yes".equalsIgnoreCase(journalManuscriptTypeCharge.getChargeOnPublication())
					&& ("SameForAll".equalsIgnoreCase(journalManuscriptTypeCharge.getCountryChargeOnPublication())
							|| ("FromDevelopingCountry"
									.equalsIgnoreCase(journalManuscriptTypeCharge.getCountryChargeOnPublication())
									&& userCountryStatus == 0))) {
				if (waveOff.equalsIgnoreCase("Yes")) {
					manuscript.setPaymentStatus("APC Payment wave-off");
				} else {
					manuscript.setPaymentStatus("APC Payment done");
				}

			}

			// Upload file
			String oldFileServerPath = null;
			String attachmentFolderPath = null;

			if (manuscriptEditorVO.getFile() != null && !manuscriptEditorVO.getFile().isEmpty()) {
				String location = uniprrFolderPath;

				attachmentFolderPath = location + File.separator + "UniprrRejectForProductionFolder" + File.separator
						+ "MNT_LWW_JOURNAL_" + "R" + manuscriptEditorVO.getManuscriptVersion() + "_"
						+ manuscript.getJournalId() + "_" + manuscriptEditorVO.getManuscriptId();

				if (environment.getProperty("server.status").equalsIgnoreCase("AWS")) {
					String Ip = "https://" + environment.getProperty("awsFileIp");
					oldFileServerPath = Ip + File.separator + "UniprrRejectForProductionFolder" + File.separator
							+ "MNT_LWW_JOURNAL_" + "R" + manuscriptEditorVO.getManuscriptVersion() + "_"
							+ manuscript.getJournalId() + "_" + manuscriptEditorVO.getManuscriptId() + File.separator
							+ manuscriptEditorVO.getFile().getOriginalFilename();
					logger.info("Server Path for AWS: " + oldFileServerPath);
				}

				File dir = new File(attachmentFolderPath);

				if (!dir.exists()) {
					dir.mkdirs();
					logger.info("File Folder Created: " + attachmentFolderPath);
				}

				try (BufferedOutputStream stream = new BufferedOutputStream(
						new FileOutputStream(new File(dir.getAbsolutePath() + File.separator
								+ manuscriptEditorVO.getFile().getOriginalFilename())))) {
					byte[] bytes = manuscriptEditorVO.getFile().getBytes();
					stream.write(bytes);
					logger.info("File uploaded successfully.");
				} catch (IOException e) {
					logger.error("Exception due to code: " + e);
					return "Failed to upload file";
				}
			}

			Date date = new Date();

			if (manuscriptEditorVO.getStatusCode().equals(30)) {
				manuscriptEditorRepo.updateManuScriptEditorCommentsR(manuscriptEditorVO.getManuscriptId(),
						manuscriptEditorVO.getManuscriptVersion(), manuscriptEditorVO.getStatusCode(),
						manuscriptEditorVO.getComments(), date);

				manuscript.setCommentForProduction(manuscriptEditorVO.getComments());
				manuscript.setManuscriptStatusBeforeReject(manuscript.getManuscriptStatusId());
				manuscript.setManuscriptStatusId(manuscriptEditorVO.getStatusCode());
				manuscript.setManuscriptVersion(manuscriptEditorVO.getManuscriptVersion());
				manuscript.setWaveOff(waveOff);
				manuscript.setRejectedDate(date);

				updateManuScriptReviewerStatus(manuscriptEditorVO, oldFileServerPath);

				sendReviewerEmails(manuscript, manuscriptEditorVO, 30);

				updateManuscriptEditorComments(manuscriptEditorVO, manuscript, oldFileServerPath, attachmentFolderPath);

				manuScriptRepo.save(manuscript);
				return "Manuscript Updated Successfully";
			} else if (manuscriptEditorVO.getStatusCode().equals(33)) {
				manuscriptEditorRepo.updateManuScriptEditorCommentsP(manuscriptEditorVO.getManuscriptId(),
						manuscriptEditorVO.getManuscriptVersion(), manuscriptEditorVO.getStatusCode(),
						manuscriptEditorVO.getComments(), date);

				manuscript.setCommentForProduction(manuscriptEditorVO.getComments());
				manuscript.setManuscriptStatusBeforeReject(manuscript.getManuscriptStatusId());
				manuscript.setManuscriptStatusId(manuscriptEditorVO.getStatusCode());
				manuscript.setManuscriptVersion(manuscriptEditorVO.getManuscriptVersion());
				manuscript.setProductionDate(date);
				manuscript.setWaveOff(waveOff);

				updateManuScriptReviewerStatus(manuscriptEditorVO, oldFileServerPath);

				sendReviewerEmails(manuscript, manuscriptEditorVO, 33);

				updateManuscriptEditorComments(manuscriptEditorVO, manuscript, oldFileServerPath, attachmentFolderPath);

				manuScriptRepo.save(manuscript);
				return "Manuscript Updated Successfully";
			} else if (manuscriptEditorVO.getStatusCode().equals(16)) {
				manuscript.setManuscriptStatusId(manuscriptEditorVO.getStatusCode());
				manuscript.setWithdrawnDate(new Date());
//		        manuScriptRepo.save(manuscript);
//	            return "Manuscript Updated Successfully";
				boolean flag = callUnitouchWithdrawlRequest(manuscript.getManuscriptCode());

				if (flag) {
					manuScriptRepo.save(manuscript);
					return "Manuscript Updated Successfully";
				} else {
					return "Failed to update status, Please contact support";
				}
			} else if (manuscriptEditorVO.getStatusCode().equals(56)) {
				List<ManuscriptTypeDetails> manuscriptTypeDetails = manuscriptTypeDetailsRepo
						.getManuscriptTypeDetailsById(manuscriptEditorVO.getManuscriptId());
				while (!manuscriptTypeDetails.isEmpty()) {
					manuscriptTypeDetailsRepo.delete(manuscriptTypeDetails.get(0));
					manuscriptTypeDetails.remove(0);
//					index1++;
				}

				List<ManuScriptCoAuthor> manuScriptCoAuthors = manuScriptCoAuthorRepo
						.sendEmailToCoAuthorOnManuscriptList(manuscriptEditorVO.getManuscriptId());
				while (!manuScriptCoAuthors.isEmpty()) {
					manuScriptCoAuthorRepo.delete(manuScriptCoAuthors.get(0));
					manuScriptCoAuthors.remove(0);
//					index2++;
				}

				List<ManuScriptFile> manuScriptFiles = manuScriptFileRepo
						.getManuScriptFilegetById(manuscriptEditorVO.getManuscriptId());
				while (!manuScriptFiles.isEmpty()) {
					manuScriptFileRepo.delete(manuScriptFiles.get(0));
					manuScriptFiles.remove(0);
//					index3++;
				}
				deleteManuScriptById(manuscriptEditorVO.getManuscriptId());

				return "Manuscript Updated Successfully";

			} else if (manuscriptEditorVO.getStatusCode().equals(58)) {
				boolean flag = false;
				flag = callUnitouchArticleWithdrawalRequest(manuscript.getManuscriptCode());
				if (flag) {
					manuScriptRepo.updateManuScriptStatusForWthdrawn(manuscriptEditorVO.getManuscriptId(),
							manuscriptEditorVO.getStatusCode(), manuscriptEditorVO.getComments(),
							manuscript.getManuscriptStatusId(), new Date());
					return "Manuscript Updated Successfully";
				} else {
					return "Failed to update status, Please contact support";
				}
			} else if (manuscriptEditorVO.getManuscriptCode() != null
					&& !manuscriptEditorVO.getManuscriptCode().isEmpty()
					&& manuscriptEditorVO.getStatusCode() != null) {
				manuscript.setManuscriptStatusId(manuscriptEditorVO.getStatusCode());
				manuScriptRepo.save(manuscript);
				return "Manuscript Updated Successfully";

			} else if (manuscriptEditorVO.getComments() != null && !manuscriptEditorVO.getComments().trim().isEmpty()) {
				manuScriptRepo.updateManuScriptStatusAndCommentById(manuscriptEditorVO.getManuscriptId(),
						manuscriptEditorVO.getStatusCode(), manuscriptEditorVO.getComments());
				return "Manuscript Updated Successfully";
			} else {
				return "There is no data for update";
			}
		} catch (Exception e) {
			logger.error("An error occurred: " + e.getMessage(), e);
			return "An error occurred during manuscript update";
		}
	}

	private void updateManuScriptReviewerStatus(ManuscriptEditorVO manuscriptEditorVO, String oldFileServerPath) {
		List<ManuScriptReviewer> manuscriptReviewerListByManuScriptIdAndVersionIsActive = manuScriptReviewerRepo
				.getManuscriptReviewerListByManuScriptIdAndVersionIsActive(manuscriptEditorVO.getManuscriptId(),
						manuscriptEditorVO.getManuscriptVersion());

		if (manuscriptReviewerListByManuScriptIdAndVersionIsActive != null
				&& !manuscriptReviewerListByManuScriptIdAndVersionIsActive.isEmpty()) {
			for (ManuScriptReviewer manuScriptReviewer : manuscriptReviewerListByManuScriptIdAndVersionIsActive) {
				if (oldFileServerPath != null) {
					manuScriptReviewer.setManuscriptStatus(manuscriptEditorVO.getStatusCode());
					manuScriptReviewer.setReviewerFileName(manuscriptEditorVO.getFile().getOriginalFilename());
					manuScriptReviewer.setReviewerFilePath(oldFileServerPath);
					manuScriptReviewer
							.setReviewerFileSize((int) Math.round((manuscriptEditorVO.getFile().getSize() / 1024)));
				}
				manuScriptReviewerRepo.save(manuScriptReviewer);
			}
		}
	}

	private void sendReviewerEmails(ManuScript manuscript, ManuscriptEditorVO manuscriptEditorVO,
			int manuscriptStatus) {
		List<ManuScriptReviewer> workingReviewerList = manuScriptReviewerRepo
				.getReviewedManu(manuscriptEditorVO.getManuscriptId());

		if (workingReviewerList != null && !workingReviewerList.isEmpty()) {
			for (ManuScriptReviewer manuScriptReviewer : workingReviewerList) {
				if ((manuScriptReviewer.getManuscriptStatus().equals(8)
						|| manuScriptReviewer.getManuscriptStatus().equals(66)
						|| manuScriptReviewer.getManuscriptStatus().equals(35))) {
					manuScriptReviewer.setIsActive(0);
					manuScriptReviewerRepo.save(manuScriptReviewer);

					EmailTriggerVO emailTriggerVO = new EmailTriggerVO();
					emailTriggerVO.setJournalId(manuscript.getJournalId());
					emailTriggerVO.setUserId(manuScriptReviewer.getReviewerId());
					emailTriggerVO.setReviewerUserId(manuScriptReviewer.getReviewerId());
					emailTriggerVO.setManuscriptId(manuscript.getManuScriptId());
					emailTriggerVO.setTaskId(87);
					emailTriggerVO.setReceiverRoleId(5);
					emailTriggerVO.setEditorUserId(manuscript.getAssignedUser());
					emailTriggerVO.setRoleId(manuscriptEditorVO.getRoleId());
					emailTriggerVO.setManuscriptStatus(manuscriptStatus);
					String message = emailTriggerService.emailTrigger(emailTriggerVO);
					logger.info("Email sent to Reviewer who has not reviewed the manuscript: " + message);
				}

				if ((manuScriptReviewer.getManuscriptStatus().equals(12)
						|| manuScriptReviewer.getManuscriptStatus().equals(13)
						|| manuScriptReviewer.getManuscriptStatus().equals(14)
						|| manuScriptReviewer.getManuscriptStatus().equals(15))) {
					boolean isEmailSent =mailQueueRepo.existsByManuscriptIdAndMailToUserIdAndTaskId(manuscriptEditorVO.getManuscriptId(), manuScriptReviewer.getReviewerId(), 88);
					if(!isEmailSent) {
						EmailTriggerVO emailTriggerVO = new EmailTriggerVO();
						emailTriggerVO.setJournalId(manuscript.getJournalId());
					    emailTriggerVO.setUserId(manuScriptReviewer.getReviewerId());
					    emailTriggerVO.setReviewerUserId(manuScriptReviewer.getReviewerId());
					    emailTriggerVO.setManuscriptId(manuscript.getManuScriptId());
					    emailTriggerVO.setTaskId(88);
					    emailTriggerVO.setReceiverRoleId(5);
					    emailTriggerVO.setEditorUserId(manuscript.getAssignedUser());
					    emailTriggerVO.setRoleId(manuscriptEditorVO.getRoleId());
					    emailTriggerVO.setManuscriptStatus(manuscriptStatus);
					    String message = emailTriggerService.emailTrigger(emailTriggerVO);
					    logger.info("Email sent to Reviewer who has reviewed the manuscript: " + message);
					}else {
						logger.info("Already mail sent for manuscript: "+manuscript.getManuScriptId()+" userId: "+manuScriptReviewer.getReviewerId()+" taskid: 88");					}
				}
			}
		}
	}

	private void updateManuscriptEditorComments(ManuscriptEditorVO manuscriptEditorVO, ManuScript manuscript,
			String oldFileServerPath, String attachmentFolderPath) {
		if (attachmentFolderPath != null) {
			ManuscriptEditorComment addManuscriptEditorComment = new ManuscriptEditorComment();
			addManuscriptEditorComment.setManuscriptId(manuscriptEditorVO.getManuscriptId());
			addManuscriptEditorComment.setManuscriptVersion(manuscriptEditorVO.getManuscriptVersion());
			addManuscriptEditorComment.setUserId(manuscriptEditorVO.getUserId());
			addManuscriptEditorComment.setRoleId(manuscriptEditorVO.getRoleId());
			addManuscriptEditorComment.setStatusId(manuscriptEditorVO.getStatusCode());
			addManuscriptEditorComment.setComment(manuscriptEditorVO.getComments());
			addManuscriptEditorComment.setCreatedAt(new Date());
			addManuscriptEditorComment.setFileName(manuscriptEditorVO.getFile().getOriginalFilename());
			addManuscriptEditorComment.setFilePath(oldFileServerPath);
			addManuscriptEditorComment.setFileSize((int) Math.round((manuscriptEditorVO.getFile().getSize() / 1024)));
			addManuscriptEditorComment.setFileType("Rejection Editor To Author");
			manuscriptEditorCommentRepo.save(addManuscriptEditorComment);
			logger.info("Editor Comment Has Been Created");
			manuscript.setRejectedFileName(manuscriptEditorVO.getFile().getOriginalFilename());
			manuscript.setRejectedFilePath(oldFileServerPath);
		}
		manuScriptRepo.save(manuscript);
	}

	private boolean callUnitouchWithdrawlRequest(String manuscriptCode) {
		boolean responseFlag = false;
		HttpServiceRequestInfo requestInfo = prepareHttpServiceRequestInfoW(unitouchUrl + "withdwalUniprrToUnitouch",
				manuscriptCode);
		String resultString = httpService.callJsonPOSTService(requestInfo);
		logger.info("callUnitouch response : " + resultString + ", for manuscript : ");

		if (resultString.equalsIgnoreCase("Success")) {
			responseFlag = true;
		} else {
			logger.error("Failed response unitouch URL-------------------" + resultString);
		}

		return responseFlag;
	}

	private boolean callUnitouchArticleWithdrawalRequest(String manuscriptCode) {
		boolean responseFlag = false;
		HttpServiceRequestInfo requestInfo = prepareHttpServiceRequestInfoW(
				unitouchUrl + "requestArticleWithdrawalUniprrToUnitouch", manuscriptCode);
		String resultString = httpService.callJsonPOSTService(requestInfo);
		logger.info("callUnitouch response : " + resultString + ", for manuscript : ");

		if (resultString.equalsIgnoreCase("Success")) {
			responseFlag = true;
		} else {
			logger.error("Failed response unitouch URL-------------------" + resultString);
		}

		return responseFlag;
	}

	private boolean callUnitouchArticleWithdrawalRequestRejection(String manuscriptCode) {
		boolean responseFlag = false;
		HttpServiceRequestInfo requestInfo = prepareHttpServiceRequestInfoW(
				unitouchUrl + "rejectArticleWithdrawalUniprrToUnitouch", manuscriptCode);
		String resultString = httpService.callJsonPOSTService(requestInfo);
		logger.info("callUnitouch response : " + resultString + ", for manuscript : ");

		if (resultString.equalsIgnoreCase("Success")) {
			responseFlag = true;
		} else {
			logger.error("Failed response unitouch URL-------------------" + resultString);
		}

		return responseFlag;
	}

	private HttpServiceRequestInfo prepareHttpServiceRequestInfoW(String apiURL, String manuscriptCode) {
		HttpServiceRequestInfo requestInfo1 = new HttpServiceRequestInfo();
		requestInfo1.setRequestUri(apiURL);
		requestInfo1.setCharset(StandardCharsets.UTF_8.name());
		requestInfo1.setMethodType(AppConstants.POST);

		StringBuilder jsonStringBuilder = new StringBuilder();
		jsonStringBuilder.append("{");
		jsonStringBuilder.append("\"manuscriptCode\":\"");
		jsonStringBuilder.append(manuscriptCode);
		jsonStringBuilder.append("\"");
		jsonStringBuilder.append("}");
		logger.info("Withdrwal request body : " + jsonStringBuilder);
		requestInfo1.setRequestBody(jsonStringBuilder.toString());

		return requestInfo1;
	}

	@Override
	public String rejectForWithdrwan(ManuscriptEditorVO manuscriptEditorVO, ManuScript manuscript) {
		boolean flag = false;
		logger.info("Before calling callUnitouch");
		// ManuScript getManuScriptid =
		// manuScriptRepo.getById(manuscriptEditorVO.getManuscriptId());
		flag = callUnitouchArticleWithdrawalRequestRejection(manuscript.getManuscriptCode());
		logger.info("After calling callUnitouch, flag : ");
		if (flag) {
			manuscript.setManuscriptStatusId(manuscript.getManuscriptStatusBeforeWithdrawn());
			manuscript.setWithdrawnDate(new Date());
			manuScriptRepo.saveAndFlush(manuscript);
			return "Withdrawn Rejected Successfully";
		} else {
			return "Failed to update status, Please contact support";
		}
	}

	@Override
	public String undoForRejectedManuscript(ManuscriptEditorVO manuscriptEditorVO, ManuScript manuscript) {

		manuscript.setManuscriptStatusId(manuscript.getManuscriptStatusBeforeReject());
		manuscript.setUpdatedOn(new Date());
		logger.debug("------------------------------------------------------" + new Date());
		manuScriptRepo.saveAndFlush(manuscript);
		return "Undo rejected manuscript Successfully";

	}

	@Override
	public List<ManuScript> getUniqueManuScript(Integer journalId) {
		return manuScriptRepo.getUniqueManuScript(journalId);
	}

	@Override
	public List<ManuscriptListVO> getManuscriptByStatusForEIC(ManuscriptListVO manuscriptVO) {

		long startTime = System.currentTimeMillis();
		List<ManuscriptListVO> newList = new ArrayList<>();

		List<Integer> mStatus = manuscriptVO.getManuscriptStatusId();

		List<ManuScriptForStatus> list = new ArrayList<>();

		if (manuscriptVO != null && manuscriptVO.getRoleId() != null) {
			list = manuScriptForStatusRepo.getManuscriptByJournalAndRoleForEIC(mStatus, manuscriptVO.getJournalId(),
					manuscriptVO.getUserId(), manuscriptVO.getRoleId());
		} else if (manuscriptVO != null && manuscriptVO.getUserId() != null) {
			list = manuScriptForStatusRepo.getManuscriptByStatusAndUserForEIC(mStatus, manuscriptVO.getJournalId(),
					manuscriptVO.getUserId());
		} else {
			list = manuScriptForStatusRepo.getManuscriptByStatusForEIC(mStatus, manuscriptVO.getJournalId());
		}

		if (list != null) {
			for (ManuScriptForStatus data : list) {
				// get data
				try {

					List<ManuscriptTypeDetails> manuScriptType = manuscriptTypeDetailsRepo
							.getManuscriptTypeDetailsById(data.getManuScriptId());
					User user = userRepo.getOne(data.getAuthorId());
					User editorNameData = userRepo.getOne(data.getAssignedUser());
					ManuscriptListVO manuscriptListVO = new ManuscriptListVO();

					if (data.getManuscriptStatusId().equals(2)) {
						int userCount = userJournalRepo.getCountOfUser(data.getJournalId(), 2);
						if (userCount > 1) {
							int editorCount = manuscriptEditorRepo.getEditorCount(data.getManuScriptId());
							if (editorCount > 0) {
								manuscriptListVO.setEditorName(
										editorNameData.getFirstName() + " " + editorNameData.getLastName());
							} else {
								manuscriptListVO.setEditorName("All(" + userCount + ")");
							}

						} else if (editorNameData != null) {
							manuscriptListVO
									.setEditorName(editorNameData.getFirstName() + " " + editorNameData.getLastName());

						}
					} else {
						if (editorNameData != null) {
							manuscriptListVO
									.setEditorName(editorNameData.getFirstName() + " " + editorNameData.getLastName());
						}
					}
					manuscriptListVO.setJournalId(data.getJournalId());
					manuscriptListVO.setManagingEditorId(data.getAssignedUser());
					manuscriptListVO.setCommentForProduction(data.getCommentForProduction());
					manuscriptListVO.setManuscripCode(data.getManuscriptCode());
					manuscriptListVO.setProductionDate(data.getProductionDate());
					manuscriptListVO.setManuscripCode(data.getManuscriptCode());
					manuscriptListVO.setManuscriptAuthorId(data.getAuthorId());
					manuscriptListVO.setManuscripTitle(data.getManuscriptTitle());
					manuscriptListVO.setManuScriptId(data.getManuScriptId());
					manuscriptListVO.setManuscriptVersion(data.getManuscriptVersion());
					manuscriptListVO.setSubmissionDate(data.getSubmissionDate());
					manuscriptListVO.setSubmittedBy(data.getSubmittedBy());
					manuscriptListVO.setCreatedBy(data.getCreatedBy());
					manuscriptListVO.setCreatedOn(data.getCreatedOn());
					manuscriptListVO.setUpdatedBy(data.getUpdatedBy());
					manuscriptListVO.setUpdatedOn(data.getUpdatedOn());
					manuscriptListVO.setWithdrawnDate(data.getWithdrawnDate());
					// Adding due date
					manuscriptListVO.setDueDate(data.getDueDate());
					manuscriptListVO.setManuStatusCode(data.getStatusMaster().getStatusValue());
					manuscriptListVO.setStatusValueForAuthor(data.getStatusMaster().getStatusValueForAuthor());
					manuscriptListVO.setStatusValueForEditor(data.getStatusMaster().getStatusValueForEditor());
					manuscriptListVO.setAuthorName(user.getFirstName() + " " + user.getLastName());
					if (user.getCountryId() != null && user.getCountries() != null)
						manuscriptListVO.setAuthorCountry(user.getCountries().getName());
					manuscriptListVO.setJournalTitle(data.getJournal().getJournalTitle());
					manuscriptListVO.setJournalCode(data.getJournal().getJournalCode());
					manuscriptListVO.setManuStatusId(data.getManuscriptStatusId());
					manuscriptListVO.setWithdrawalComment(data.getWithdrawalComment());
					if (manuScriptType != null && !manuScriptType.isEmpty()) {
						manuscriptListVO.setManuScriptType(manuScriptType.get(0).getManuscriptType());
					}

					if (data.getManuscriptStatusId() == 30) {
						AuthoritiesHistory as = authoritiesHistoryRepo
								.getAuthoritiesHistoryByStatusId(data.getManuScriptId(), data.getManuscriptStatusId());
						if (as != null && as.getPreviousUserId() != null) {
							User user1 = userRepo.getOne(as.getPreviousUserId());
							manuscriptListVO.setRejectedBy(user1.getFirstName() + ' ' + user1.getLastName());
						}
					}
					manuscriptListVO.setRejectedDate(data.getRejectedDate());
					manuscriptListVO.setReviewerFileName(data.getRejectedFileName());
					manuscriptListVO.setReviewerFilePath(data.getRejectedFilePath());
					manuscriptListVO.setManuscriptStatusBeforeWithdrawn(data.getManuscriptStatusBeforeWithdrawn());
					newList.add(manuscriptListVO);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		long endTime = System.currentTimeMillis();

		logger.debug("That took " + (endTime - startTime) + " milliseconds");

		return newList;
	}

	@Override
	public List<ManuscriptListVO> getManuscriptByStatus(ManuscriptListVO manuscriptVO) {
		List<ManuscriptListVO> newList = new ArrayList<>();
		try {
			List<ManuScriptForStatus> list = new ArrayList<>();
			if (manuscriptVO != null && manuscriptVO.getRoleId() != null) {
				list = manuScriptForStatusRepo.getManuscriptByJournalAndRoleForEIC(manuscriptVO.getManuscriptStatusId(),
						manuscriptVO.getJournalId(), manuscriptVO.getUserId(), manuscriptVO.getRoleId());
			} else if (manuscriptVO != null && manuscriptVO.getUserId() != null) {
				list = manuScriptForStatusRepo.getManuscriptByStatusAndUserForEIC(manuscriptVO.getManuscriptStatusId(),
						manuscriptVO.getJournalId(), manuscriptVO.getUserId());
			} else {
				list = manuScriptForStatusRepo.getManuscriptByStatusForEIC(manuscriptVO.getManuscriptStatusId(),
						manuscriptVO.getJournalId());
			}

			if (list != null) {
				for (ManuScriptForStatus data : list) {
					try {
						User editorNameData = userRepo.getByID(data.getAssignedUser());
						ManuscriptListVO manuscriptListVO = new ManuscriptListVO();
						if (data.getManuscriptStatusId().equals(2)) {
							int userCount = userJournalRepo.getCountOfUser(data.getJournalId(), 2);
							if (userCount <= 1) {
								manuscriptListVO.setEditorName(
										editorNameData.getFirstName() + " " + editorNameData.getLastName());
							} else {
								manuscriptListVO.setEditorName("All(" + userCount + ")");
							}

						} else if (editorNameData != null) {
							manuscriptListVO
									.setEditorName(editorNameData.getFirstName() + " " + editorNameData.getLastName());

						}

						manuscriptListVO.setJournalId(data.getJournalId());
						manuscriptListVO.setManagingEditorId(data.getAssignedUser());
						manuscriptListVO.setCommentForProduction(data.getCommentForProduction());
						manuscriptListVO.setManuscripCode(data.getManuscriptCode());
						manuscriptListVO.setProductionDate(data.getProductionDate());
						manuscriptListVO.setManuscripCode(data.getManuscriptCode());
						manuscriptListVO.setManuscriptAuthorId(data.getAuthorId());
						manuscriptListVO.setManuscripTitle(data.getManuscriptTitle());
						manuscriptListVO.setManuScriptId(data.getManuScriptId());
						manuscriptListVO.setManuscriptVersion(data.getManuscriptVersion());
						manuscriptListVO.setSubmissionDate(data.getSubmissionDate());
						manuscriptListVO.setSubmittedBy(data.getSubmittedBy());
						manuscriptListVO.setCreatedBy(data.getCreatedBy());
						manuscriptListVO.setCreatedOn(data.getCreatedOn());
						manuscriptListVO.setUpdatedBy(data.getUpdatedBy());
						manuscriptListVO.setUpdatedOn(data.getUpdatedOn());
						manuscriptListVO.setWithdrawnDate(data.getWithdrawnDate());
						// Adding due date
						manuscriptListVO.setDueDate(data.getDueDate());
						String manuStatusCode = Optional
								.ofNullable(authoritiesHistoryRepo.getPreStatus(data.getManuScriptId(),
										data.getJournalId()))
								.map(AuthoritiesHistory::getStatusMaster).map(StatusMaster::getStatusValue)
								.orElse(null);
						if (manuStatusCode != null && (manuStatusCode.equalsIgnoreCase("Accepted By Editor")
								|| manuStatusCode.equalsIgnoreCase("Author Proofs")
								|| manuStatusCode.equalsIgnoreCase("Author Check"))) {
							if (data.getManuscriptStatusBeforeWithdrawn() != null) {
								// Option 1
								StatusMaster sm = statusMasterRepo
										.getStatusMaster(data.getManuscriptStatusBeforeWithdrawn());
								if (sm != null)
									manuscriptListVO.setManuStatusCode(sm.getStatusValueForArticleLifeCycle());

							} else {
								// Option 2 via AuthoritiesHistory
								List<AuthoritiesHistory> j = authoritiesHistoryRepo
										.getAuthoritiesHistory(data.getManuScriptId(), data.getJournalId());
								if (j.size() > 1) {
									for (int i = 0; i < j.size(); i++) {
										if (j.get(i).getStatusMaster().getStatusValueForArticleLifeCycle()
												.equalsIgnoreCase("Manuscript Withdrawn")) {
											manuscriptListVO.setManuStatusCode(
													j.get(i - 1).getStatusMaster().getStatusValueForArticleLifeCycle());
											break;
										}
									}
								}
							}
						} else {
							manuscriptListVO.setManuStatusCode(manuStatusCode);
						}
						manuscriptListVO.setStatusValueForAuthor(data.getStatusMaster().getStatusValueForAuthor());
						manuscriptListVO.setStatusValueForEditor(data.getStatusMaster().getStatusValueForEditor());
						manuscriptListVO
								.setAuthorName(data.getUser().getFirstName() + " " + data.getUser().getLastName());
						if (data.getUser().getCountryId() != null && data.getUser().getCountries() != null)
							manuscriptListVO.setAuthorCountry(data.getUser().getCountries().getName());
						manuscriptListVO.setJournalTitle(data.getJournal().getJournalTitle());
						manuscriptListVO.setJournalCode(data.getJournal().getJournalCode());
						manuscriptListVO.setManuStatusId(data.getManuscriptStatusId());
						manuscriptListVO.setWithdrawnRequestDate(data.getWithdrawnRequestDate());
						manuscriptListVO.setWithdrawalComment(data.getWithdrawalComment());
						if (data.getManuscriptTypeDetails() != null) {
							manuscriptListVO.setManuScriptType(data.getManuscriptTypeDetails().getManuscriptType());
						}
						manuscriptListVO.setManuscriptStatusBeforeWithdrawn(data.getManuscriptStatusBeforeWithdrawn());
						manuscriptListVO.setManuscriptStatusBeforeReject(data.getManuscriptStatusBeforeReject());

						manuscriptListVO.setRejectedDate(data.getRejectedDate());
						manuscriptListVO.setReviewerFileName(data.getRejectedFileName());
						manuscriptListVO.setReviewerFilePath(data.getRejectedFilePath());
						newList.add(manuscriptListVO);
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return newList;

	}

	@Override
	public List<ManuscriptListVO> getManuscriptByJournalAndStatus(ManuscriptListVO manuscriptVO) {
		List<ManuscriptListVO> newList = new ArrayList<>();
		try {
			List<ManuScriptForStatus> list = new ArrayList<>();
			List<Tuple> manuscriptList = new ArrayList<>();
			if (manuscriptVO != null && manuscriptVO.getJournalId() != null
					&& manuscriptVO.getManuscriptStatusId() != null) {
				manuscriptList = manuScriptForStatusRepo.getManuscriptByJournalAndStatus(
						manuscriptVO.getManuscriptStatusId(), manuscriptVO.getJournalId());
			}
			if (list != null) {
				newList = manuscriptList.stream()
						.map(t -> new ManuscriptListVO(t.get(0, Integer.class), t.get(1, Integer.class),
								t.get(2, String.class), t.get(3, String.class), t.get(4, String.class),
								t.get(5, String.class)))
						.collect(Collectors.toList());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return newList;

	}

	@Override
	public List<ManuscriptListVO> getManuscriptByStatusForEditorTotalManuscript(ManuscriptListVO manuscriptVO) {
		List<ManuscriptListVO> newList = new ArrayList<>();
		for (Integer mStatus : manuscriptVO.getManuscriptStatusId()) {
			List<ManuScript> list = new ArrayList<>();
			list = manuScriptRepo.getManuscriptByJournalEditor(mStatus, manuscriptVO.getJournalId(),
					manuscriptVO.getUserId());

			StatusMaster statusCode = statusMasterRepo.getOne(mStatus);
			if (list != null) {
				for (ManuScript data : list) {
					// get data

					List<ManuScriptReviewer> uniqueUnassignedReviewerData = manuScriptReviewerRepo
							.getUniqueUnassignedReviewerData(data.getManuScriptId(), data.getManuscriptVersion(),
									data.getJournalId());
					List<ManuscriptTypeDetails> manuScriptType = manuscriptTypeDetailsRepo
							.getManuscriptTypeDetailsById(data.getManuScriptId());
					User user = userRepo.getOne(data.getAuthorId());
					User editorNameData = userRepo.getByID(data.getAssignedUser());
					ManuscriptListVO manuscriptListVO = new ManuscriptListVO();
					if (editorNameData != null) {
						manuscriptListVO
								.setEditorName(editorNameData.getFirstName() + " " + editorNameData.getLastName());
					}
					manuscriptListVO.setJournalId(data.getJournalId());
					manuscriptListVO.setManagingEditorId(data.getAssignedUser());
					manuscriptListVO.setCommentForProduction(data.getCommentForProduction());
					manuscriptListVO.setManuscripCode(data.getManuscriptCode());
					manuscriptListVO.setProductionDate(data.getProductionDate());
					manuscriptListVO.setManuscripCode(data.getManuscriptCode());
					manuscriptListVO.setManuscriptAuthorId(data.getAuthorId());
					manuscriptListVO.setManuscripTitle(data.getManuscriptTitle());
					manuscriptListVO.setManuScriptId(data.getManuScriptId());
					manuscriptListVO.setManuscriptVersion(data.getManuscriptVersion());
					manuscriptListVO.setSubmissionDate(data.getSubmissionDate());
					manuscriptListVO.setSubmittedBy(data.getSubmittedBy());
					manuscriptListVO.setCreatedBy(data.getCreatedBy());
					manuscriptListVO.setCreatedOn(data.getCreatedOn());
					manuscriptListVO.setUpdatedBy(data.getUpdatedBy());
					manuscriptListVO.setUpdatedOn(data.getUpdatedOn());
					// Adding due date
					manuscriptListVO.setDueDate(data.getDueDate());
					manuscriptListVO.setManuStatusCode(statusCode.getStatusValue());
					manuscriptListVO.setStatusValueForAuthor(statusCode.getStatusValueForAuthor());
					manuscriptListVO.setStatusValueForEditor(statusCode.getStatusValueForEditor());
					manuscriptListVO.setAuthorName(user.getFirstName() + " " + user.getLastName());
					if (user.getCountryId() != null && user.getCountries() != null)
						manuscriptListVO.setAuthorCountry(user.getCountries().getName());
					manuscriptListVO.setJournalTitle(data.getJournal().getJournalTitle());
					manuscriptListVO.setJournalCode(data.getJournal().getJournalCode());
					manuscriptListVO.setManuStatusId(mStatus);
					manuscriptListVO.setWithdrawalComment(data.getWithdrawalComment());
					if (manuScriptType != null && !manuScriptType.isEmpty()) {
						manuscriptListVO.setManuScriptType(manuScriptType.get(0).getManuscriptType());
					}

					if (uniqueUnassignedReviewerData != null && !uniqueUnassignedReviewerData.isEmpty()) {
						manuscriptListVO.setReviewerFileName(uniqueUnassignedReviewerData.get(0).getReviewerFileName());
						manuscriptListVO.setReviewerFilePath(uniqueUnassignedReviewerData.get(0).getReviewerFilePath());
					}

					List<ManuscriptManagingEditor> manuscriptManagingEditor = manuscriptManagingEditorRepo
							.getByJournalAndManuscriptId(data.getManuScriptId(), data.getJournalId());
					if (manuscriptManagingEditor != null && !manuscriptManagingEditor.isEmpty()) {
						List<ManuscriptManagingEditorVO> mmeVoList = new ArrayList<>();
						for (ManuscriptManagingEditor mme : manuscriptManagingEditor) {
							User managingEditor = userRepo.getOne(data.getAuthorId());
							ManuscriptManagingEditorVO mmeVO = new ManuscriptManagingEditorVO();
							mmeVO.setUserId(mme.getUserId());
							mmeVO.setName(managingEditor.getFirstName() + " " + managingEditor.getLastName());
							mmeVoList.add(mmeVO);

						}
						manuscriptListVO.setManagingEditor(mmeVoList);
					}

					List<ManuscriptEditor> me = manuscriptEditorRepo.getByJournalAndManuscript(data.getManuScriptId(),
							data.getJournalId());
					if (me != null && !me.isEmpty()) {
						List<ManagingEditorVO> meVOList = new ArrayList<>();
						for (ManuscriptEditor meData : me) {
							User editor = userRepo.getOne(data.getAuthorId());
							ManagingEditorVO meVO = new ManagingEditorVO();
							meVO.setEditorId(meData.getEditorId());
							meVO.setName(editor.getFirstName() + " " + editor.getLastName());
							meVOList.add(meVO);
						}
						manuscriptListVO.setEditorList(meVOList);
					}
					newList.add(manuscriptListVO);
				}
			}
		}
		return newList;
	}

	@Override
	public List<EditorDashboardVO> getManuscriptByData(UserJournalManuscriptVo userJournalManuscriptVo) {
		List<ManuScript> manuScriptList = new ArrayList<>();
		List<EditorDashboardVO> newList = new ArrayList<>();

		List<Integer> authorStatus = userJournalManuscriptVo.getManuscriptStatusId();

		
//		List<Integer> getManuscriptStatusIdAlt = Arrays.asList(44, 45, 63, 64, 16, 58, 67, 71);
//		userJournalManuscriptVo.getManuscriptStatusId().addAll(getManuscriptStatusIdAlt);
		if (userJournalManuscriptVo.getRoleId() != null && userJournalManuscriptVo.getRoleId() == 3) {
			Journal journalCode = journalsRepo.getById(userJournalManuscriptVo.getJournalId());
			if (userJournalManuscriptVo.getManuscriptStatusId().get(0) != 16) {
				List<ManuscriptEditor> dataList = manuscriptEditorService.getManuscriptEditor(userJournalManuscriptVo);
				if (dataList != null && dataList.size() > 0) {
					for (ManuscriptEditor editordata : dataList) {
						StatusMaster statusdDetail = statusMasterRepo.getById(editordata.getManuscriptStatus());
						User user = userRepo.getById(editordata.getEditorId());
						Integer manuscriptId = editordata.getManuscriptId();
						List<ManuScript> manuscriptData = getManuScriptListById(manuscriptId);
						if (manuscriptData != null) {
							for (ManuScript data : manuscriptData) {
								List<ManuscriptTypeDetails> manuScriptType = manuscriptTypeDetailsRepo
										.getManuscriptTypeDetailsById(data.getManuScriptId());
								EditorDashboardVO editorDashboardVO = new EditorDashboardVO();
								editorDashboardVO.setManuscriptId(data.getManuScriptId());
								editorDashboardVO.setWithdrawalComment(data.getWithdrawalComment());
								editorDashboardVO.setManuscriptCode(data.getManuscriptCode());
								editorDashboardVO.setCommentFromProduction(data.getCommentFromProduction());
								editorDashboardVO.setManuscriptTitle(data.getManuscriptTitle());
								editorDashboardVO.setManuscriptAbstract(data.getManuscriptAbstract());
								editorDashboardVO.setSubmittedDate(data.getSubmissionDate());
								/*
								 * if (data.getManuscriptVersion() > 0) { List<ManuScriptFile>
								 * getmanuScriptFileData = manuScriptFileRepo
								 * .getAllTheManuscriptFileWithMnuscriptVerison(data.getManuScriptId(),
								 * data.getManuscriptVersion()); if (getmanuScriptFileData != null &&
								 * !getmanuScriptFileData.isEmpty()) {
								 * editorDashboardVO.setSubmittedDate(getmanuScriptFileData.get(0).getCreatedOn(
								 * )); } else { editorDashboardVO.setSubmittedDate(data.getSubmissionDate()); }
								 * } else { editorDashboardVO.setSubmittedDate(data.getSubmissionDate()); }
								 */
								editorDashboardVO.setAuthorProofDate(data.getAuthorProofDate());
								editorDashboardVO.setAssignedDate(data.getUpdatedOn());
								editorDashboardVO.setDueDate(data.getDueDate());
								editorDashboardVO.setJournalCode(journalCode.getJournalCode());
								editorDashboardVO.setJournalTitle(journalCode.getJournalTitle());
								editorDashboardVO.setJournalId(journalCode.getJournalId());
								editorDashboardVO.setStatusId(statusdDetail.getId());
								editorDashboardVO.setStatusValue(statusdDetail.getStatusValue());
								editorDashboardVO.setStatusValueForAuthor(statusdDetail.getStatusValueForAuthor());
								editorDashboardVO.setStatusValueForEditor(statusdDetail.getStatusValueForEditor());
								editorDashboardVO.setWithdrawnDate(data.getWithdrawnDate());
								editorDashboardVO
										.setManuscriptStatusBeforeWithdrawn(data.getManuscriptStatusBeforeWithdrawn());
								editorDashboardVO.setUpdateDate(data.getUpdatedOn());
								editorDashboardVO.setManuscriptVersion(data.getManuscriptVersion());
								editorDashboardVO.setAuthorName(user.getFirstName() + " " + user.getLastName());
								editorDashboardVO.setAuthorId(user.getId());
								if (manuScriptType != null && !manuScriptType.isEmpty()) {
									editorDashboardVO.setManuscriptType(manuScriptType.get(0).getManuscriptType());

								}
								newList.add(editorDashboardVO);
							}
						}
					}
				} else {
					Journal journalCode1 = journalsRepo.getById(userJournalManuscriptVo.getJournalId());
					manuScriptList = getDataFromManuScript(userJournalManuscriptVo);
					if (manuScriptList != null) {
						for (ManuScript data : manuScriptList) {
							List<ManuscriptTypeDetails> manuScriptType = manuscriptTypeDetailsRepo
									.getManuscriptTypeDetailsById(data.getManuScriptId());
							StatusMaster statusdDetail = statusMasterRepo.getById(data.getManuscriptStatusId());
							User user = userRepo.getById(data.getAuthorId());
							EditorDashboardVO editorDashboardVO = new EditorDashboardVO();
							editorDashboardVO.setManuscriptId(data.getManuScriptId());
							editorDashboardVO.setManuscriptCode(data.getManuscriptCode());
							editorDashboardVO.setManuscriptTitle(data.getManuscriptTitle());
							editorDashboardVO.setManuscriptAbstract(data.getManuscriptAbstract());
							editorDashboardVO.setSubmittedDate(data.getSubmissionDate());
							/*
							 * if (data.getManuscriptVersion() > 0) { List<ManuScriptFile>
							 * getmanuScriptFileData = manuScriptFileRepo
							 * .getAllTheManuscriptFileWithMnuscriptVerison(data.getManuScriptId(),
							 * data.getManuscriptVersion()); if (getmanuScriptFileData != null &&
							 * !getmanuScriptFileData.isEmpty()) {
							 * editorDashboardVO.setSubmittedDate(getmanuScriptFileData.get(0).getCreatedOn(
							 * )); } else { editorDashboardVO.setSubmittedDate(data.getSubmissionDate()); }
							 * } else { editorDashboardVO.setSubmittedDate(data.getSubmissionDate()); }
							 */
							editorDashboardVO.setAssignedDate(data.getUpdatedOn());
							editorDashboardVO.setDueDate(data.getDueDate());
							editorDashboardVO.setWithdrawnDate(data.getWithdrawnDate());
							editorDashboardVO
									.setManuscriptStatusBeforeWithdrawn(data.getManuscriptStatusBeforeWithdrawn());
							editorDashboardVO.setAuthorProofDate(data.getAuthorProofDate());
							editorDashboardVO.setwithdrawnRequestDate(data.getWithdrawnRequestDate());
							editorDashboardVO.setWithdrawalComment(data.getWithdrawalComment());
							editorDashboardVO.setUpdateDate(data.getUpdatedOn());
							editorDashboardVO.setJournalCode(journalCode1.getJournalCode());
							editorDashboardVO.setJournalTitle(journalCode1.getJournalTitle());
							editorDashboardVO.setJournalId(journalCode1.getJournalId());
							editorDashboardVO.setStatusId(statusdDetail.getId());
							editorDashboardVO.setStatusValue(statusdDetail.getStatusValue());
							editorDashboardVO.setStatusValueForAuthor(statusdDetail.getStatusValueForAuthor());
							editorDashboardVO.setStatusValueForEditor(statusdDetail.getStatusValueForEditor());
							editorDashboardVO.setManuscriptVersion(data.getManuscriptVersion());
							editorDashboardVO.setAuthorName(user.getFirstName() + " " + user.getLastName());
							if (manuScriptType != null && !manuScriptType.isEmpty()) {
								editorDashboardVO.setManuscriptType(manuScriptType.get(0).getManuscriptType());
							}
							newList.add(editorDashboardVO);
						}
					}

				}
			} else {
				manuScriptList = getDataFromManuScript(userJournalManuscriptVo);
				if (manuScriptList != null) {
					for (ManuScript data : manuScriptList) {
						List<ManuscriptTypeDetails> manuScriptType = manuscriptTypeDetailsRepo
								.getManuscriptTypeDetailsById(data.getManuScriptId());
						StatusMaster statusdDetail = statusMasterRepo.getById(data.getManuscriptStatusId());
						User user = userRepo.getById(data.getAuthorId());
						EditorDashboardVO editorDashboardVO = new EditorDashboardVO();
						editorDashboardVO.setManuscriptId(data.getManuScriptId());
						editorDashboardVO.setManuscriptCode(data.getManuscriptCode());
						editorDashboardVO.setManuscriptTitle(data.getManuscriptTitle());
						editorDashboardVO.setManuscriptAbstract(data.getManuscriptAbstract());
						editorDashboardVO.setSubmittedDate(data.getSubmissionDate());
						/*
						 * if (data.getManuscriptVersion() > 0) { List<ManuScriptFile>
						 * getmanuScriptFileData = manuScriptFileRepo
						 * .getAllTheManuscriptFileWithMnuscriptVerison(data.getManuScriptId(),
						 * data.getManuscriptVersion()); if (getmanuScriptFileData != null &&
						 * !getmanuScriptFileData.isEmpty()) {
						 * editorDashboardVO.setSubmittedDate(getmanuScriptFileData.get(0).getCreatedOn(
						 * )); } else { editorDashboardVO.setSubmittedDate(data.getSubmissionDate()); }
						 * } else { editorDashboardVO.setSubmittedDate(data.getSubmissionDate()); }
						 */
						editorDashboardVO.setAssignedDate(data.getUpdatedOn());
						editorDashboardVO.setDueDate(data.getDueDate());
						editorDashboardVO.setWithdrawnDate(data.getWithdrawnDate());
						editorDashboardVO.setWithdrawalComment(data.getWithdrawalComment());
						editorDashboardVO.setwithdrawnRequestDate(data.getWithdrawnRequestDate());
						editorDashboardVO.setManuscriptStatusBeforeWithdrawn(data.getManuscriptStatusBeforeWithdrawn());
						editorDashboardVO.setAuthorProofDate(data.getAuthorProofDate());

						editorDashboardVO.setUpdateDate(data.getUpdatedOn());
						editorDashboardVO.setJournalCode(journalCode.getJournalCode());
						editorDashboardVO.setJournalTitle(journalCode.getJournalTitle());
						editorDashboardVO.setJournalId(journalCode.getJournalId());
						editorDashboardVO.setStatusId(statusdDetail.getId());
						editorDashboardVO.setStatusValue(statusdDetail.getStatusValue());
						editorDashboardVO.setStatusValueForAuthor(statusdDetail.getStatusValueForAuthor());
						editorDashboardVO.setStatusValueForEditor(statusdDetail.getStatusValueForEditor());
						editorDashboardVO.setManuscriptVersion(data.getManuscriptVersion());
						editorDashboardVO.setAuthorName(user.getFirstName() + " " + user.getLastName());
						if (manuScriptType != null && !manuScriptType.isEmpty()) {
							editorDashboardVO.setManuscriptType(manuScriptType.get(0).getManuscriptType());
						}
						newList.add(editorDashboardVO);
					}
				}

			}
		} else if (userJournalManuscriptVo.getRoleId() != null && userJournalManuscriptVo.getRoleId() == 5) {
			if (userJournalManuscriptVo.getManuscriptStatusId() != null
					&& !userJournalManuscriptVo.getManuscriptStatusId().isEmpty()) {
				List<ManuScriptReviewer> dataList = manuScriptReviewerService
						.getManuscriptReviewer(userJournalManuscriptVo);
				if (dataList != null) {
					for (ManuScriptReviewer reviewerData : dataList) {
						ManuScript data = reviewerData.getManuscript();
						if (data != null) {
							EditorDashboardVO editorDashboardVO = new EditorDashboardVO();
							editorDashboardVO.setManuscriptId(data.getManuScriptId());
							editorDashboardVO.setManuscriptCode(data.getManuscriptCode());
							editorDashboardVO.setCommentFromProduction(data.getCommentFromProduction());
							editorDashboardVO.setManuscriptTitle(data.getManuscriptTitle());
							editorDashboardVO.setManuscriptAbstract(data.getManuscriptAbstract());
							editorDashboardVO.setWithdrawalComment(data.getWithdrawalComment());
							editorDashboardVO.setSubmittedDate(data.getSubmissionDate());
							editorDashboardVO.setAssignedDate(data.getUpdatedOn());
							editorDashboardVO.setDueDate(data.getDueDate());
							editorDashboardVO.setWithdrawnDate(data.getWithdrawnDate());
							editorDashboardVO
									.setManuscriptStatusBeforeWithdrawn(data.getManuscriptStatusBeforeWithdrawn());

							editorDashboardVO.setAuthorProofDate(data.getAuthorProofDate());

							editorDashboardVO.setUpdateDate(data.getUpdatedOn());
							editorDashboardVO.setJournalCode(data.getJournal().getJournalCode());
							editorDashboardVO.setJournalTitle(data.getJournal().getJournalTitle());
							editorDashboardVO.setJournalId(data.getJournal().getJournalId());
							editorDashboardVO.setStatusId(data.getStatusMaster().getId());
							editorDashboardVO.setStatusValue(data.getStatusMaster().getStatusValue());
							editorDashboardVO.setStatusValueForAuthor(data.getStatusMaster().getStatusValueForAuthor());
							editorDashboardVO.setStatusValueForEditor(data.getStatusMaster().getStatusValueForEditor());
							editorDashboardVO.setManuscriptVersion(data.getManuscriptVersion());
							editorDashboardVO.setAuthorId(data.getUser().getId());
							editorDashboardVO
									.setAuthorName(data.getUser().getFirstName() + " " + data.getUser().getLastName());
							if (data.getManuscriptTypeDetails() != null) {
								editorDashboardVO
										.setManuscriptType(data.getManuscriptTypeDetails().getManuscriptType());
							}
							editorDashboardVO.setAssignedDate(reviewerData.getAssignedDate());
							editorDashboardVO.setEditorId(reviewerData.getEditorId());
							editorDashboardVO.setEditorRoleId(reviewerData.getEditorRoleId());
							editorDashboardVO.setUnassignedDate(reviewerData.getUnassignedDate());
							editorDashboardVO.setStartDate(reviewerData.getStartDate());
							editorDashboardVO.setCompleteDate(reviewerData.getCompleteDate());
							editorDashboardVO.setRejectDate(reviewerData.getRejectDate());
							editorDashboardVO.setAcceptDate(reviewerData.getAcceptDate());
							editorDashboardVO.setDueDate(reviewerData.getDueDate());
							editorDashboardVO.setStatusValueForUser(reviewerData.getStatusMaster().getStatusValue());
							newList.add(editorDashboardVO);

						}
					}
				}
			} else {
				Journal journalCode = journalsRepo.getById(userJournalManuscriptVo.getJournalId());
				List<ManuScriptReviewer> dataList = manuScriptReviewerRepo
						.getRereviewData(userJournalManuscriptVo.getJournalId(), userJournalManuscriptVo.getUserId());
				if (dataList != null) {
					for (ManuScriptReviewer reviewerData : dataList) {
						if (reviewerData.getManuscriptVersion() >= 1) {
							List<ManuScript> manuscriptData = getManuScriptListById(reviewerData.getManuscriptId());
							StatusMaster statusMasterValue = statusMasterRepo
									.getStatusMaster(reviewerData.getManuscriptStatus());
							if (manuscriptData != null) {
								for (ManuScript data : manuscriptData) {
									List<ManuscriptTypeDetails> manuScriptType = manuscriptTypeDetailsRepo
											.getManuscriptTypeDetailsById(data.getManuScriptId());
									EditorDashboardVO editorDashboardVO = new EditorDashboardVO();
									editorDashboardVO.setManuscriptId(data.getManuScriptId());
									editorDashboardVO.setManuscriptVersion(data.getManuscriptVersion());
									editorDashboardVO.setManuscriptCode(data.getManuscriptCode());
									editorDashboardVO.setCommentFromProduction(data.getCommentFromProduction());
									editorDashboardVO.setManuscriptTitle(data.getManuscriptTitle());
									editorDashboardVO.setWithdrawalComment(data.getWithdrawalComment());
									editorDashboardVO.setManuscriptAbstract(data.getManuscriptAbstract());
									editorDashboardVO.setSubmittedDate(data.getSubmissionDate());
									/*
									 * if (data.getManuscriptVersion() > 0) { List<ManuScriptFile>
									 * getmanuScriptFileData = manuScriptFileRepo
									 * .getAllTheManuscriptFileWithMnuscriptVerison(data.getManuScriptId(),
									 * data.getManuscriptVersion()); if (getmanuScriptFileData != null &&
									 * !getmanuScriptFileData.isEmpty()) { editorDashboardVO
									 * .setSubmittedDate(getmanuScriptFileData.get(0).getCreatedOn()); } else {
									 * editorDashboardVO.setSubmittedDate(data.getSubmissionDate()); } } else {
									 * editorDashboardVO.setSubmittedDate(data.getSubmissionDate()); }
									 */
									editorDashboardVO.setWithdrawnDate(data.getWithdrawnDate());
									editorDashboardVO.setAuthorProofDate(data.getAuthorProofDate());
									editorDashboardVO.setManuscriptStatusBeforeWithdrawn(
											data.getManuscriptStatusBeforeWithdrawn());

									editorDashboardVO.setUpdateDate(data.getUpdatedOn());
									editorDashboardVO.setJournalCode(journalCode.getJournalCode());
									editorDashboardVO.setJournalTitle(journalCode.getJournalTitle());
									editorDashboardVO.setJournalId(journalCode.getJournalId());
									editorDashboardVO.setStatusId(data.getStatusMaster().getId());
									editorDashboardVO.setStatusValue(data.getStatusMaster().getStatusValue());
									editorDashboardVO
											.setStatusValueForAuthor(data.getStatusMaster().getStatusValueForAuthor());
									editorDashboardVO
											.setStatusValueForEditor(data.getStatusMaster().getStatusValueForEditor());
									editorDashboardVO.setAuthorId(data.getUser().getId());
									editorDashboardVO.setAuthorName(
											data.getUser().getFirstName() + " " + data.getUser().getLastName());
									if (manuScriptType != null && !manuScriptType.isEmpty()) {
										editorDashboardVO.setManuscriptType(manuScriptType.get(0).getManuscriptType());
									}
									editorDashboardVO.setAssignedDate(reviewerData.getAssignedDate());
									editorDashboardVO.setEditorId(reviewerData.getEditorId());
									editorDashboardVO.setEditorRoleId(reviewerData.getEditorRoleId());
									editorDashboardVO.setUnassignedDate(reviewerData.getUnassignedDate());
									editorDashboardVO.setStartDate(reviewerData.getStartDate());
									editorDashboardVO.setCompleteDate(reviewerData.getCompleteDate());
									editorDashboardVO.setRejectDate(reviewerData.getRejectDate());
									editorDashboardVO.setAcceptDate(reviewerData.getAcceptDate());
									editorDashboardVO.setDueDate(reviewerData.getDueDate());
									editorDashboardVO.setStatusValueForUser(statusMasterValue.getStatusValue());
									newList.add(editorDashboardVO);
								}
							}
						}
					}
				}
			}
		} else if (userJournalManuscriptVo.getRoleId() != null && userJournalManuscriptVo.getRoleId() == 4) {

			if (authorStatus.stream().anyMatch(v -> Arrays.asList(33, 57, 61, 62, 63).contains(v)))
				authorStatus.add(69);
			
			for(int i=0;i<authorStatus.size();i++)
			{
				System.out.println(authorStatus.get(i));
			}

			List<ManuScript> list = manuScriptRepo.authorDashboardData(userJournalManuscriptVo.getUserId(),
					authorStatus, userJournalManuscriptVo.getJournalId());

			if (list != null && !list.isEmpty()) {
				for (ManuScript data : list) {
					EditorDashboardVO editorDashboardVO = new EditorDashboardVO();
					editorDashboardVO.setManuscriptId(data.getManuScriptId());
					editorDashboardVO.setManuscriptCode(data.getManuscriptCode());
					editorDashboardVO.setManuscriptTitle(data.getManuscriptTitle());
					editorDashboardVO.setProductionDate(data.getProductionDate());
					editorDashboardVO.setCommentForProduction(data.getCommentForProduction());
					editorDashboardVO.setCommentFromProduction(data.getCommentFromProduction());
					editorDashboardVO.setManuscriptAbstract(data.getManuscriptAbstract());
					editorDashboardVO.setRejectedDate(data.getRejectedDate());
					editorDashboardVO.setRejectedFileName(data.getRejectedFileName());
					editorDashboardVO.setRejectedFilePath(data.getRejectedFilePath());
					editorDashboardVO.setEditorRoleId(data.getAssignedUserRole());
					editorDashboardVO.setSubmittedDate(data.getSubmissionDate());
					/*
					 * if (data.getManuscriptVersion() > 0) { List<ManuScriptFile>
					 * getmanuScriptFileData = manuScriptFileRepo
					 * .getAllTheManuscriptFileWithMnuscriptVerison(data.getManuScriptId(),
					 * data.getManuscriptVersion()); if (getmanuScriptFileData != null &&
					 * !getmanuScriptFileData.isEmpty()) {
					 * editorDashboardVO.setSubmittedDate(getmanuScriptFileData.get(0).getCreatedOn(
					 * )); } else { editorDashboardVO.setSubmittedDate(data.getSubmissionDate()); }
					 * } else { editorDashboardVO.setSubmittedDate(data.getSubmissionDate()); }
					 */

					editorDashboardVO.setAssignedDate(data.getUpdatedOn());
					editorDashboardVO.setDueDate(data.getDueDate());
					editorDashboardVO.setWithdrawnDate(data.getWithdrawnDate());
					editorDashboardVO.setwithdrawnRequestDate(data.getWithdrawnRequestDate());
					editorDashboardVO.setManuscriptStatusBeforeWithdrawn(data.getManuscriptStatusBeforeWithdrawn());
					editorDashboardVO.setAuthorProofDate(data.getAuthorProofDate());
					editorDashboardVO.setWithdrawalComment(data.getWithdrawalComment());
					editorDashboardVO.setUpdateDate(data.getUpdatedOn());
					editorDashboardVO.setJournalCode(data.getJournal().getJournalCode());
					editorDashboardVO.setJournalTitle(data.getJournal().getJournalTitle());
					editorDashboardVO.setJournalId(data.getJournal().getJournalId());
					editorDashboardVO.setStatusId(data.getStatusMaster().getId());
					editorDashboardVO.setStatusValue(data.getStatusMaster().getStatusValue());
					editorDashboardVO.setStatusValueForAuthor(data.getStatusMaster().getStatusValueForAuthor());
					editorDashboardVO.setStatusValueForEditor(data.getStatusMaster().getStatusValueForEditor());
					editorDashboardVO.setManuscriptVersion(data.getManuscriptVersion());
					editorDashboardVO.setAuthorName(data.getUser().getFirstName() + " " + data.getUser().getLastName());
					if (data.getManuscriptStatusId() != null && data.getManuscriptStatusId().equals(2)) {
						editorDashboardVO.setStatusForAuthor("New Submission");
					} else if (data.getManuscriptStatusId() != null && data.getManuscriptStatusId().equals(26)) {
						editorDashboardVO.setStatusForAuthor("Re Submission");
					} else if (data.getManuscriptStatusId() != null && data.getManuscriptStatusId().equals(50)) {
						editorDashboardVO.setStatusForAuthor("Technical Modification Submission");
					}
					ManuscriptTypeDetails detail = manuscriptTypeDetailsRepo
							.getDetailsFromManuscriptId(data.getManuScriptId());
					if (detail != null)
						editorDashboardVO.setManuscriptType(detail.getManuscriptType());

					newList.add(editorDashboardVO);
				}
			}
		} else {
			Journal journalCode = journalsRepo.getById(userJournalManuscriptVo.getJournalId());
			manuScriptList = getDataFromManuScript(userJournalManuscriptVo);
			if (manuScriptList != null) {
				for (ManuScript data : manuScriptList) {
					List<ManuscriptTypeDetails> manuScriptType = manuscriptTypeDetailsRepo
							.getManuscriptTypeDetailsById(data.getManuScriptId());
					StatusMaster statusdDetail = statusMasterRepo.getById(data.getManuscriptStatusId());
					User user = userRepo.getById(data.getAuthorId());
					EditorDashboardVO editorDashboardVO = new EditorDashboardVO();
					editorDashboardVO.setManuscriptId(data.getManuScriptId());
					editorDashboardVO.setManuscriptCode(data.getManuscriptCode());
					editorDashboardVO.setManuscriptTitle(data.getManuscriptTitle());
					editorDashboardVO.setManuscriptAbstract(data.getManuscriptAbstract());
					editorDashboardVO.setSubmittedDate(data.getSubmissionDate());
					/*
					 * if (data.getManuscriptVersion() > 0) { List<ManuScriptFile>
					 * getmanuScriptFileData = manuScriptFileRepo
					 * .getAllTheManuscriptFileWithMnuscriptVerison(data.getManuScriptId(),
					 * data.getManuscriptVersion()); if (getmanuScriptFileData != null &&
					 * !getmanuScriptFileData.isEmpty()) {
					 * editorDashboardVO.setSubmittedDate(getmanuScriptFileData.get(0).getCreatedOn(
					 * )); } else { editorDashboardVO.setSubmittedDate(data.getSubmissionDate()); }
					 * } else { editorDashboardVO.setSubmittedDate(data.getSubmissionDate()); }
					 */
					editorDashboardVO.setAssignedDate(data.getUpdatedOn());
					editorDashboardVO.setDueDate(data.getDueDate());
					editorDashboardVO.setWithdrawnDate(data.getWithdrawnDate());
					editorDashboardVO.setManuscriptStatusBeforeWithdrawn(data.getManuscriptStatusBeforeWithdrawn());
					editorDashboardVO.setAuthorProofDate(data.getAuthorProofDate());
					editorDashboardVO.setwithdrawnRequestDate(data.getWithdrawnRequestDate());
					editorDashboardVO.setWithdrawalComment(data.getWithdrawalComment());
					editorDashboardVO.setUpdateDate(data.getUpdatedOn());
					editorDashboardVO.setJournalCode(journalCode.getJournalCode());
					editorDashboardVO.setJournalTitle(journalCode.getJournalTitle());
					editorDashboardVO.setJournalId(journalCode.getJournalId());
					editorDashboardVO.setStatusId(statusdDetail.getId());
					editorDashboardVO.setStatusValue(statusdDetail.getStatusValue());
					editorDashboardVO.setStatusValueForAuthor(statusdDetail.getStatusValueForAuthor());
					editorDashboardVO.setStatusValueForEditor(statusdDetail.getStatusValueForEditor());
					editorDashboardVO.setManuscriptVersion(data.getManuscriptVersion());
					editorDashboardVO.setAuthorName(user.getFirstName() + " " + user.getLastName());
					if (manuScriptType != null && !manuScriptType.isEmpty()) {
						editorDashboardVO.setManuscriptType(manuScriptType.get(0).getManuscriptType());
					}
					newList.add(editorDashboardVO);
				}
			}
		}

		return newList;
	}

	public List<ManuScript> getDataFromManuScript(UserJournalManuscriptVo userJournalManuscriptVo) {
		List<ManuScript> dataList = new ArrayList<>();
		for (Integer statusList : userJournalManuscriptVo.getManuscriptStatusId()) {

			List<ManuScript> list = new ArrayList<>();
			Integer status = statusList;
			if (userJournalManuscriptVo.getRoleId() != null
					&& (userJournalManuscriptVo.getRoleId().equals(2) || userJournalManuscriptVo.getRoleId().equals(3))
					&& userJournalManuscriptVo.getManuscriptStatusId() != null
					&& !userJournalManuscriptVo.getManuscriptStatusId().isEmpty()
					&& userJournalManuscriptVo.getManuscriptStatusId().get(0).equals(16)) {
				list = manuScriptRepo.getManuScriptWithdrawal(userJournalManuscriptVo.getJournalId(), status);
				if (list != null && !list.isEmpty()) {
					list.stream().forEach(i -> {
						dataList.add(i);
					});
				}
			} else {
				list = manuScriptRepo.getManuScriptDataByID(userJournalManuscriptVo.getUserId(),
						userJournalManuscriptVo.getJournalId(), status);
//				if (list != null && !list.isEmpty()) {
//					dataList.add(list.get(0));
//				}
				if (list != null && !list.isEmpty()) {
					list.stream().forEach(i -> {
						dataList.add(i);
					});
				}
			}

		}

		return dataList;
	}

	@Override
	public List<EditorDashboardVO> getAssignedManuscriptData(UserJournalManuscriptVo userJournalManuscriptVo) {
		List<EditorDashboardVO> newList = new ArrayList<>();
		if (userJournalManuscriptVo.getRoleId() != null && userJournalManuscriptVo.getRoleId() == 3) {
			List<ManuscriptEditor> manuScriptIdByEditor = manuscriptEditorService
					.getManuscriptEditorListById(userJournalManuscriptVo);
			for (ManuscriptEditor data : manuScriptIdByEditor) {
				Integer mID = data.getManuscriptId();
				StatusMaster statusdDetail = statusMasterRepo.getOne(data.getManuscriptStatus());
				List<ManuScript> manuscriptData = getManuScriptListById(mID);
				if (manuscriptData != null) {
					for (ManuScript mdata : manuscriptData) {
						EditorDashboardVO editorDashboardVO = new EditorDashboardVO();
						editorDashboardVO.setManuscriptId(mdata.getManuScriptId());
						editorDashboardVO.setManuscriptCode(mdata.getManuscriptCode());
						editorDashboardVO.setManuscriptTitle(mdata.getManuscriptTitle());
						editorDashboardVO.setManuscriptAbstract(mdata.getManuscriptAbstract());
						if (data.getManuscriptVersion() > 0) {
							List<ManuScriptFile> getmanuScriptFileData = manuScriptFileRepo
									.getAllTheManuscriptFileWithMnuscriptVerison(mdata.getManuScriptId(),
											mdata.getManuscriptVersion());
							if (getmanuScriptFileData != null && !getmanuScriptFileData.isEmpty()) {
								editorDashboardVO.setSubmittedDate(getmanuScriptFileData.get(0).getCreatedOn());
							} else {
								editorDashboardVO.setSubmittedDate(mdata.getSubmissionDate());
							}
						} else {
							editorDashboardVO.setSubmittedDate(mdata.getSubmissionDate());
						}
						editorDashboardVO.setAuthorId(mdata.getUser().getId());
						editorDashboardVO.setAuthorName(mdata.getUser().getFirstName() + mdata.getUser().getLastName());
						editorDashboardVO.setStatusValue(mdata.getStatusMaster().getStatusValue());
						editorDashboardVO.setStatusValueForAuthor(mdata.getStatusMaster().getStatusValueForAuthor());
						editorDashboardVO.setStatusValueForEditor(mdata.getStatusMaster().getStatusValueForEditor());
						editorDashboardVO.setStatusValueForUser(statusdDetail.getStatusValue());
						editorDashboardVO.setManuscriptVersion(mdata.getManuscriptVersion());
						editorDashboardVO.setManuscriptType(mdata.getManuscriptTypeDetails().getManuscriptType());
						newList.add(editorDashboardVO);
					}
				}
			}
		} else if (userJournalManuscriptVo.getRoleId() != null && userJournalManuscriptVo.getRoleId() == 5) {
			List<ManuScriptReviewer> dataList = manuScriptReviewerService
					.getManuscriptReviewerListById(userJournalManuscriptVo);
			if (dataList != null) {
				for (ManuScriptReviewer reviewerData : dataList) {
					Integer manuscriptId = reviewerData.getManuscriptId();
					StatusMaster statusdDetail = statusMasterRepo.getOne(reviewerData.getManuscriptStatus());
					List<ManuScript> manuscriptData = getManuScriptListById(manuscriptId);
					if (manuscriptData != null) {
						for (ManuScript data : manuscriptData) {

							EditorDashboardVO editorDashboardVO = new EditorDashboardVO();
							editorDashboardVO.setManuscriptId(data.getManuScriptId());
							editorDashboardVO.setManuscriptCode(data.getManuscriptCode());
							editorDashboardVO.setManuscriptTitle(data.getManuscriptTitle());
							editorDashboardVO.setManuscriptAbstract(data.getManuscriptAbstract());
							if (data.getManuscriptVersion() > 0) {
								List<ManuScriptFile> getmanuScriptFileData = manuScriptFileRepo
										.getAllTheManuscriptFileWithMnuscriptVerison(data.getManuScriptId(),
												data.getManuscriptVersion());
								if (getmanuScriptFileData != null && !getmanuScriptFileData.isEmpty()) {
									editorDashboardVO.setSubmittedDate(getmanuScriptFileData.get(0).getCreatedOn());
								} else {
									editorDashboardVO.setSubmittedDate(data.getSubmissionDate());
								}
							} else {
								editorDashboardVO.setSubmittedDate(data.getSubmissionDate());
							}
							editorDashboardVO.setAuthorId(data.getUser().getId());
							editorDashboardVO
									.setAuthorName(data.getUser().getFirstName() + data.getUser().getLastName());
							editorDashboardVO.setStatusValue(data.getStatusMaster().getStatusValue());
							editorDashboardVO.setStatusValueForAuthor(data.getStatusMaster().getStatusValueForAuthor());
							editorDashboardVO.setStatusValueForEditor(data.getStatusMaster().getStatusValueForEditor());
							editorDashboardVO.setStatusValueForUser(statusdDetail.getStatusValue());
							editorDashboardVO.setManuscriptVersion(data.getManuscriptVersion());
							if (data.getManuscriptTypeDetails() != null) {
								editorDashboardVO
										.setManuscriptType(data.getManuscriptTypeDetails().getManuscriptType());
							}

							newList.add(editorDashboardVO);
						}
					}
				}
			}
		}
		return newList;
	}

	@Override
	public void saveManuscriptDetails(ManuScript manuscriptData) {
		manuScriptRepo.save(manuscriptData);
	}

	@Override
	public List<ManuscriptTypeJournalVO> getManuscriptTypeJournalListByJournalId(Integer manuscriptId) {
		logger.info("Inside getManuscriptTypeJournalListByJournalId Impl");
		List<ManuscriptTypeJournalVO> voList = new ArrayList<>();
		ManuScript manuscript = manuScriptRepo.getOne(manuscriptId);
		List<ManuscriptTypeJournal> list = manuscriptTypeJournalsRepo
				.getManuscriptTypesFromJournalId(manuscript.getJournalId());
		if (list != null && !list.isEmpty()) {
			list.stream().forEach(i -> {
				ManuscriptTypeJournalVO addManuscriptTypeJournal = new ManuscriptTypeJournalVO();
				addManuscriptTypeJournal.setMtjId(i.getMtjId());
				addManuscriptTypeJournal.setManutypeName(i.getManutypeName());
				addManuscriptTypeJournal.setTrialNumberMandatory(i.getTrialNumberMandatory());
				addManuscriptTypeJournal.setIsActive(i.getIsActive());
				addManuscriptTypeJournal.setIsAbstract(i.getIsAbstract());
				JournalManuscriptTypeCharge j = journalManuscriptTypeChargeRepo
						.getJournalManuscriptTypeChargeIsActive(i.getJournalId(), i.getMtjId());
				if (j != null) {
					addManuscriptTypeJournal.setAdditionalCharges(j.getAdditionalCharges());
					addManuscriptTypeJournal.setChargeAmount(j.getChargeAmount());
					addManuscriptTypeJournal.setColorFigureCharges(j.getColorFigureCharges());
					addManuscriptTypeJournal.setCurrency(j.getCurrency());
					addManuscriptTypeJournal.setFigureCharges(j.getFigureCharges());
					addManuscriptTypeJournal.setFixedChargeAmount(j.getFixedChargeAmount());
					addManuscriptTypeJournal.setFixedCharges(j.getFixedCharges());
					addManuscriptTypeJournal.setTablesCharges(j.getTablesCharges());
					addManuscriptTypeJournal.setTypeSettingCharges(j.getTypeSettingCharges());
					addManuscriptTypeJournal.setChargeOnPublication(j.getChargeOnPublication());
					addManuscriptTypeJournal.setChargeOnSubmission(j.getChargeOnSubmission());
					if (j.getCountryChargeOnPublication() != null)
						addManuscriptTypeJournal.setIsCountryChargeOnPublication(j.getCountryChargeOnPublication());
					if (j.getCountryChargeOnSubmission() != null)
						addManuscriptTypeJournal.setIsCountryChargeOnSubmission(j.getCountryChargeOnSubmission());
				}
				voList.add(addManuscriptTypeJournal);
			});
		}
		return voList;
	}

	@Override
	public ManuscriptTypeTitleAbstractVO getManuscriptTypeAbstractDetails(IDVO vo) {
		logger.info("Inside getManuscriptTypeAbstractDetails Impl");
		ManuScript manuScript = manuScriptRepo.getById(vo.getId());
		ManuscriptTypeDetails details = manuscriptTypeDetailsRepo.getDetailsFromManuscriptIdForBundaling(vo.getId(),
				vo.getVersion());
		ManuscriptTypeTitleAbstractVO vo1 = new ManuscriptTypeTitleAbstractVO();
		if (manuScript != null) {
			if ((manuScript.getManuscriptTypeDetails() != null)
					&& (StringUtils.isNotBlank(manuScript.getManuscriptTypeDetails().getManuscriptType()))) {
				String manuscript_type = manuScript.getManuscriptTypeDetails().getManuscriptType();
				logger.info("getManuscriptTables: "+manuScript.getManuscriptTypeDetails().getManuscriptTables());
				logger.info("getManuscriptPages: "+manuScript.getManuscriptTypeDetails().getManuscriptPages());
				List<ManuscriptTypeJournal> manuscriptTypeJournals = manuscriptTypeJournalsRepo
						.getManuscriptTypesFromJournalId(manuScript.getJournalId());
				Optional<ManuscriptTypeJournal> typeJournal = manuscriptTypeJournals.stream()
						.filter(obj -> StringUtils.equalsIgnoreCase(obj.getManutypeName(), manuscript_type))
						.findFirst();
				if (typeJournal.isPresent()) {
				    vo1.setTrailNumberMandatory(typeJournal.get().getTrialNumberMandatory());
				} else {
				    logger.warn("No ManuscriptTypeJournal found for Journal Id: ",
				            manuScript.getJournalId());
				    vo1.setTrailNumberMandatory("");
				}
				vo1.setManuscriptTypeJournal(manuscript_type);
				vo1.setBlackAndWhiteFigures(manuScript.getManuscriptTypeDetails().getManuscriptBlackWhiteFigures());
				vo1.setManuscriptColorFigures(manuScript.getManuscriptTypeDetails().getManuscriptColorFigures());
				vo1.setManuscriptTables(manuScript.getManuscriptTypeDetails().getManuscriptTables());
				vo1.setManuscriptPages(manuScript.getManuscriptTypeDetails().getManuscriptPages());
				
			}
			vo1.setManuscriptCode(manuScript.getManuscriptCode());
			vo1.setTitle(manuScript.getManuscriptTitle());
			vo1.setShortTitle(manuScript.getManuscriptShortTitle());
			vo1.setManuscriptAbstract(manuScript.getManuscriptAbstract());
			vo1.setManuscriptRegionOfOrigin(manuScript.getManuscriptOrigin());
			vo1.setManuscriptVersion(vo.getVersion());
			vo1.setTrialRegistrationNo(manuScript.getTrialRegistrationNo());
			vo1.setManuscriptKeywords(manuScript.getManuscriptKeywords());
			vo1.setManuscriptId(vo.getId());
		}
		if (details != null) {
			vo1.setManuscriptTypeJournal(details.getManuscriptType());
			vo1.setManuscriptWords(details.getManuscriptWords());
			vo1.setBlackAndWhiteFigures(details.getManuscriptBlackWhiteFigures());
			vo1.setManuscriptColorFigures(details.getManuscriptColorFigures());
			vo1.setManuscriptTables(details.getManuscriptTables());
			vo1.setManuscriptPages(details.getManuscriptPages());
		}

		List<ManuscriptJournalSubject> i = manuscriptJournalSubjectRepo.findByManuscriptId(vo.getId(), 1);
		if (i != null && !i.isEmpty()) {
			List<ManuscriptJournalSubjectVo> p = new ArrayList<>();
			i.stream().forEach(j -> {
				ManuscriptJournalSubjectVo k = new ManuscriptJournalSubjectVo();
				k.setId(j.getId());
				k.setManuscriptId(j.getManuscriptId());
				k.setManuscriptVersion(j.getManuscriptVersion());
				k.setJournalId(j.getJournalId());
				k.setJournalSubjectName(j.getJournalSubject().getJournalSubject());
				k.setIsActive(j.getIsActive());
				k.setCreatedAt(j.getCreatedAt());
				p.add(k);
			});
			vo1.setManuscriptJournalSubject(p);
		}
		return vo1;
	}

	@Override
	public void saveTypeTitleAbstractDetails(ManuscriptTypeTitleAbstractVO vo, ManuScript manuscript) {
		logger.info("Inside saveTypeTitleAbstractDetails Impl");
		manuscript.setManuscriptVersion(vo.getManuscriptVersion());
		if (vo.getTitle() != null && !vo.getTitle().isEmpty())
			manuscript.setManuscriptTitle(vo.getTitle());
		if (vo.getShortTitle() != null && !vo.getShortTitle().isEmpty())
			manuscript.setManuscriptShortTitle(vo.getShortTitle());
		if (vo.getManuscriptAbstract() != null && !vo.getManuscriptAbstract().isEmpty())
			manuscript.setManuscriptAbstract(vo.getManuscriptAbstract());
		if (vo.getTrialRegistrationNo() != null && !vo.getTrialRegistrationNo().isEmpty())
			manuscript.setTrialRegistrationNo(vo.getTrialRegistrationNo());
		manuscript.setManuscriptVersion(vo.getManuscriptVersion());
		if (vo.getManuscriptKeywords() != null && !vo.getManuscriptKeywords().isEmpty())
			manuscript.setManuscriptKeywords(vo.getManuscriptKeywords());
		manuscript.setUpdatedOn(new Date());
		manuscript.setIsActive(1);
		manuScriptRepo.save(manuscript);
		ManuscriptTypeDetails details = manuscriptTypeDetailsRepo.getDetailsFromManuscriptId(vo.getManuscriptId());
		if (details == null) {
			details = new ManuscriptTypeDetails();
			details.setManuscriptId(vo.getManuscriptId());
		}
		if (vo.getManuscriptTypeJournal() != null && !vo.getManuscriptTypeJournal().isEmpty())
			details.setManuscriptType(vo.getManuscriptTypeJournal());
		if (vo.getManuscriptWords() != null && !vo.getManuscriptWords().isEmpty())
			details.setManuscriptWords(vo.getManuscriptWords());
		if (vo.getBlackAndWhiteFigures() != null && !vo.getBlackAndWhiteFigures().isEmpty())
			details.setManuscriptBlackWhiteFigures(vo.getBlackAndWhiteFigures());
		if (vo.getBlackAndWhiteFigures() != null && !vo.getBlackAndWhiteFigures().isEmpty())
			details.setManuscriptColorFigures(vo.getManuscriptColorFigures());
		if (vo.getManuscriptTables() != null && !vo.getManuscriptTables().isEmpty())
			details.setManuscriptTables(vo.getManuscriptTables());
		if (vo.getManuscriptPages() != null && !vo.getManuscriptPages().isEmpty())
			details.setManuscriptPages(vo.getManuscriptPages());
		if (vo.getManuscriptVersion() != null)
			details.setManuscriptVersion(vo.getManuscriptVersion());
		manuscriptTypeDetailsRepo.save(details);

		if (vo.getManuscriptId() != null && vo.getManuscriptVersion() != null) {
			manuscriptJournalSubjectRepo.disableManuscriptJournalSubject(vo.getManuscriptId());
			if (vo.getManuscriptJournalSubject() != null && !vo.getManuscriptJournalSubject().isEmpty()) {
				vo.getManuscriptJournalSubject().stream().forEach(i -> {
					ManuscriptJournalSubject getManuscriptJournalSubject = manuscriptJournalSubjectRepo
							.getManuscriptJournalSubject(i.getManuscriptId(), i.getJournalSubjectId());
					if (getManuscriptJournalSubject != null) {
						getManuscriptJournalSubject.setIsActive(1);
						getManuscriptJournalSubject.setManuscriptVersion(i.getManuscriptVersion());
						manuscriptJournalSubjectRepo.save(getManuscriptJournalSubject);
					} else {
						ManuscriptJournalSubject addManuscriptJournalSubject = new ManuscriptJournalSubject();
						addManuscriptJournalSubject.setJournalId(i.getJournalId());
						addManuscriptJournalSubject.setIsActive(1);
						addManuscriptJournalSubject.setJournalSubjectId(i.getJournalSubjectId());
						addManuscriptJournalSubject.setManuscriptId(i.getManuscriptId());
						addManuscriptJournalSubject.setManuscriptVersion(i.getManuscriptVersion());
						addManuscriptJournalSubject.setCreatedAt(new Date());
						manuscriptJournalSubjectRepo.save(addManuscriptJournalSubject);
					}
				});
			}
		}
	}

	@Override
	public List<ManuscriptCoAuthorVO> getCoAuthorList(Integer manuscriptId) {
		List<ManuScriptCoAuthor> list = manuScriptCoAuthorRepo.getCoAuthorListFromManuscriptId(manuscriptId);
		List<ManuscriptCoAuthorVO> voList = ManuscriptCoAuthorTransformationUtils.mapModelListToVoList(list);
		try {
			ManuScript manuscript = manuScriptRepo.getById(manuscriptId);
			ManuScriptCoAuthor i = manuScriptCoAuthorRepo.getManuScriptCoAuthorData(manuscript.getAuthorId(),
					manuscriptId);
			if (i != null) {
				User user = userRepo.getById(manuscript.getAuthorId());
				ManuscriptCoAuthorVO vo = new ManuscriptCoAuthorVO();
				JournalPolicies p = journalPoliciesRepo.getJournalPoliciesDetails(manuscript.getJournalId());
				if (p != null) {
					if (p.getOrcIdMandatory() != null && !p.getOrcIdMandatory().isEmpty()) {
						vo.setOrcIdMandatory(p.getOrcIdMandatory());
					}
				}
				vo.setId(i.getId());
				vo.setFirstName(user.getFirstName());
				vo.setMiddleName(user.getMiddleName());
				vo.setCoAuthorId(user.getId());
				vo.setLastName(user.getLastName());
				vo.setAddress(user.getAddress());
				vo.setOrcidId(user.getOrcidId());
				vo.setResearcherId(user.getResearcherId());
				vo.setTrialRegistrationNo(manuscript.getTrialRegistrationNo());
				vo.setAddressTwo(user.getAddressTwo());
				vo.setAddressThree(user.getAddressThree());
				vo.setAcademic(user.getAcademic());
				vo.setCountryId(user.getCountryId());
				vo.setState(user.getState());
				vo.setCity(user.getCity());
				vo.setOrder(i.getAuthorOrder());
				vo.setEmail(user.getEmail());
				vo.setInstitution(user.getInstitute());
				vo.setSalutation(user.getSalutation());
				vo.setScopusId(user.getScopusId());
				vo.setExpertise(user.getExpertise());
				vo.setPubmedId(user.getPubmedId());
				vo.setSpecialization(user.getSpecialization());
				vo.setResearcherId(user.getResearcherId());
				vo.setMobileNumber(user.getMobileNumber());
				vo.setPhoneNumber(user.getPhoneNumber());
				vo.setPostalCode(user.getPostalCode());
				vo.setCountryCode(user.getCountryCode());
				// vo.setAuthorType("Primary");
				// Added default flag for corresponding author 23/12/2021
				vo.setIsCorrespondingAuthor(i.getIsCorrespondingAuthor());
				if (i.getIsCorrespondingAuthor() != null && i.getIsCorrespondingAuthor().equals("Y")) {
					vo.setAuthorType("Primary");
				}
				vo.setCoAuthorStatus(i.getCoAuthorStatus());
				// END
				voList.add(vo);
			} else {
				ManuScriptCoAuthor addManuScriptCoAuthor = new ManuScriptCoAuthor();
				addManuScriptCoAuthor.setCoAuthorId(manuscript.getAuthorId());
				addManuScriptCoAuthor.setCoAuthorManuscriptId(manuscriptId);
				addManuScriptCoAuthor.setAuthorOrder(0);
				// Added default flag for corresponding author 23/12/2021
				addManuScriptCoAuthor.setIsCorrespondingAuthor("Y");
				// END
				ManuScriptCoAuthor j = manuScriptCoAuthorRepo.save(addManuScriptCoAuthor);
				User user = userRepo.getOne(manuscript.getAuthorId());
				ManuscriptCoAuthorVO vo = new ManuscriptCoAuthorVO();
				JournalPolicies p = journalPoliciesRepo.getJournalPoliciesDetails(manuscript.getJournalId());
				if (p != null) {
					if (p.getOrcIdMandatory() != null && !p.getOrcIdMandatory().isEmpty()) {
						vo.setOrcIdMandatory(p.getOrcIdMandatory());
					}
				}
				vo.setId(j.getId());
				vo.setFirstName(user.getFirstName());
				vo.setMiddleName(user.getMiddleName());
				vo.setCoAuthorId(user.getId());
				vo.setAcademic(user.getAcademic());
				vo.setLastName(user.getLastName());
				vo.setAddress(user.getAddress());
				vo.setOrcidId(user.getOrcidId());
				vo.setOrder(j.getAuthorOrder());
				vo.setResearcherId(user.getResearcherId());
				vo.setTrialRegistrationNo(manuscript.getTrialRegistrationNo());
				vo.setAddressTwo(user.getAddressTwo());
				vo.setAddressThree(user.getAddressThree());
				vo.setCountryId(user.getCountryId());
				vo.setState(user.getState());
				vo.setCity(user.getCity());
				vo.setEmail(user.getEmail());
				vo.setInstitution(user.getInstitute());
				vo.setSalutation(user.getSalutation());
				vo.setScopusId(user.getScopusId());
				vo.setExpertise(user.getExpertise());
				vo.setPubmedId(user.getPubmedId());
				vo.setSpecialization(user.getSpecialization());
				vo.setResearcherId(user.getResearcherId());
				vo.setMobileNumber(user.getMobileNumber());
				vo.setPhoneNumber(user.getPhoneNumber());
				vo.setPostalCode(user.getPostalCode());
				vo.setCountryCode(user.getCountryCode());
				vo.setAuthorType("Primary");
				// Added default flag for corresponding author 23/12/2021
				vo.setIsCorrespondingAuthor(addManuScriptCoAuthor.getIsCorrespondingAuthor());
				vo.setCoAuthorStatus(addManuScriptCoAuthor.getCoAuthorStatus());
				// END
				voList.add(vo);
			}
		} catch (Exception e) {
			logger.info("Exception :" + e);
			e.printStackTrace();
		}

		try {
			Collections.sort(voList, (v1, v2) -> v1.getOrder().compareTo(v2.getOrder()));
		} catch (Exception e) {
			logger.info("Exception :" + e);
			e.printStackTrace();
		}

		return voList;
	}

	@Override
	public String saveCoAuthorOrderList(List<ManuscriptCoAuthorVO> voList) {
		int order = 0;
		if (voList != null && !voList.isEmpty()) {
			for (ManuscriptCoAuthorVO vo : voList) {
				ManuScriptCoAuthor coAuthor = manuScriptCoAuthorRepo.getOne(vo.getId());
				if (coAuthor != null) {
					coAuthor.setAuthorOrder(order++);
					manuScriptCoAuthorRepo.save(coAuthor);
				}
			}
			return "Order Saved Successfully";
		} else {
			return "There is no data to saved order";
		}

	}

	@Override
	public ManuscriptTypeDetails getManuscriptTypeDetailsByManuscriptId(Integer manuscriptId) {
		ManuscriptTypeDetails details = manuscriptTypeDetailsRepo.getDetailsFromManuscriptId(manuscriptId);
		return details;
	}

	@Override
	public void saveWordCountDetails(ManuScriptVO vo) {
		ManuscriptTypeDetails details = manuscriptTypeDetailsRepo.getOne(vo.getManuscriptTypeDetailsId());
		details.setManuscriptFigures(vo.getManuscriptFigures());
		details.setManuscriptColorFigures(vo.getManuscriptColorFigures());

		try {
			if (vo.getManuscriptColorFigures() != null && vo.getManuscriptColorFigures().length() > 0
					&& vo.getBlackAndWhiteFigures() != null && vo.getBlackAndWhiteFigures().length() > 0) {
				Integer figures = 0;
				figures = Integer.parseInt(vo.getManuscriptColorFigures())
						+ Integer.parseInt(vo.getBlackAndWhiteFigures());
				details.setManuscriptFigures(figures.toString());
			}
		} catch (Exception e) {
			logger.info("Exception :" + e);
			e.printStackTrace();
		}

		details.setManuscriptTables(vo.getManuscriptTables());
		details.setManuscriptWords(vo.getManuscriptWords());
		details.setManuscriptPages(vo.getManuscriptPages());
		manuscriptTypeDetailsRepo.save(details);

	}

	@Override
	public void deleteCoAuthor(Integer authorId) {
		manuScriptCoAuthorRepo.deactivateCoAuthor(authorId);
	}

//	@Override
//	@Transactional
//	public String submitManuscript(ManuScriptVO vo, JournalManuscriptTypeCharge journalManuscriptTypeCharge) {
//	    
//		try {
//	        logger.info("Starting manuscript submission for ID: {}", vo.getId());
//
//	        // Step 1: Pre-fetch supporting data outside locked section
//	        int userCountryStatus = Integer.parseInt(manuScriptRepo.getDevelopingCountryStatus(vo.getAuthorId()));
//	        User authorOfManuscript = userRepo.getByID(vo.getAuthorId());
//	        logger.info("User country status: {}, Author ID: {}", userCountryStatus, vo.getAuthorId());
//
//	        // Step 2: Lock the manuscript row explicitly
//	        ManuScript manuscript = manuScriptRepo.fetchManuScript(vo.getId())
//	                .orElseThrow(() -> new IllegalArgumentException("No manuscript found for ID: " + vo.getId()));
//
//		        logger.info("Manuscript locked for update: {}", manuscript.getManuScriptId());
//	
//		        manuscript.setManuscriptVersion(vo.getManuscriptVersion());
//	
//		        // Step 3: Payment Status Logic
//		        if (vo.getStatusCode().equals(2) && journalManuscriptTypeCharge != null) {
//		            boolean isChargeRequired = "Yes".equalsIgnoreCase(journalManuscriptTypeCharge.getIsChargesRequired());
//		            boolean isSubmissionCharge = "Yes".equalsIgnoreCase(journalManuscriptTypeCharge.getChargeOnSubmission());
//		            boolean isPublicationCharge = "Yes".equalsIgnoreCase(journalManuscriptTypeCharge.getChargeOnPublication());
//		            String countryCharge = journalManuscriptTypeCharge.getCountryChargeOnSubmission();
//	
//		            if (isChargeRequired && isSubmissionCharge && !isPublicationCharge) {
//		                if ("SameForAll".equalsIgnoreCase(countryCharge)) {
//		                    vo.setStatusCode(64);
//		                } else if ("FromDevelopingCountry".equalsIgnoreCase(countryCharge)) {
//		                    DevelopedCountriesByManuscriptType developedCountries = developedCountriesByManuscriptTypeRepo
//		                            .getByJournalAndManuscriptType(journalManuscriptTypeCharge.getJournalId(),
//		                                    journalManuscriptTypeCharge.getManuscriptTypeId());
//		                    String paymentCountries = developedCountries.getPaymentCountriesOnSubmission();
//	
//		                    logger.info("Payment country rules: {}", paymentCountries);
//	
//		                    Integer countryId = authorOfManuscript.getCountries().getId();
//		                    if ("India,Others".equals(paymentCountries) ||
//		                            ("India".equals(paymentCountries) && countryId.equals(101)) ||
//		                            ("Others".equals(paymentCountries) && !countryId.equals(101))) {
//		                        vo.setStatusCode(2);
//		                    } else {
//		                        vo.setStatusCode(64);
//		                    }
//		                }
//		            } else if (isChargeRequired && isSubmissionCharge && isPublicationCharge) {
//		                if ("SameForAll".equalsIgnoreCase(countryCharge)) {
//		                    vo.setStatusCode(64);
//		                } else if ("FromDevelopingCountry".equalsIgnoreCase(countryCharge)) {
//		                    DevelopedCountriesByManuscriptType developedCountries = developedCountriesByManuscriptTypeRepo
//		                            .getByJournalAndManuscriptType(journalManuscriptTypeCharge.getJournalId(),
//		                                    journalManuscriptTypeCharge.getManuscriptTypeId());
//		                    String paymentCountries = developedCountries.getPaymentCountriesOnSubmission();
//	
//		                    Integer countryId = authorOfManuscript.getCountries().getId();
//		                    if ("India,Others".equals(paymentCountries) ||
//		                            ("India".equals(paymentCountries) && countryId.equals(101)) ||
//		                            ("Others".equals(paymentCountries) && !countryId.equals(101))) {
//		                        vo.setStatusCode(2);
//		                    } else {
//		                        vo.setStatusCode(64);
//		                    }
//		                }
//		            }
//	
//		            logger.info("Final status code after payment evaluation: {}", vo.getStatusCode());
//		        }
//	
//		        // Step 4: Status-based Date updates
//		        if (vo.getStatusCode() != null && (vo.getStatusCode().equals(2) || vo.getStatusCode().equals(64))) {
//		            manuscript.setSubmissionDate(new Date());
//		        }
//	
//		        if ("TECHNICAL-MODIFICATION".equalsIgnoreCase(vo.getSubmissionStatus())) {
//		            manuscript.setTechnicalModificationDate(new Date());
//		        } else if ("RE-SUBMISSION".equalsIgnoreCase(vo.getSubmissionStatus())) {
//		            manuscript.setResubmissionDate(new Date());
//	
//		            ManuscriptEditor oldEditor = manuscriptEditorRepo.getManuscriptEditorDataForRevision(manuscript.getManuScriptId());
//		            if (oldEditor != null) {
//		                oldEditor.setIsActive(0);
//		                manuscriptEditorRepo.save(oldEditor);
//	
//		                ManuscriptEditor newEditor = new ManuscriptEditor();
//		                newEditor.setManuscriptId(manuscript.getManuScriptId());
//		                newEditor.setManuscriptVersion(manuscript.getManuscriptVersion());
//		                newEditor.setManagingEditorId(oldEditor.getManagingEditorId());
//		                newEditor.setEditorId(manuscript.getAssignedUser());
//		                newEditor.setIsActive(1);
//		                newEditor.setJournalId(manuscript.getJournalId());
//		                newEditor.setAssignedDate(new Date());
//		                newEditor.setManuscriptStatus(manuscript.getManuscriptStatusId());
//		                manuscriptEditorRepo.save(newEditor);
//		            }
//		        }
//	
//		        if ("NEW-SUBMISSION".equalsIgnoreCase(vo.getSubmissionStatus())) {
//		            List<UserJournal> editors = userJournalRepo.getUserRoleJournalId(manuscript.getJournalId(), 2);
//		            if (editors != null && !editors.isEmpty()) {
//		                manuscript.setAssignedUser(editors.get(0).getUserId());
//		                manuscript.setAssignedUserRole(2);
//		            }
//		        }
//	
//		        manuscript.setManuscriptStatusId(vo.getStatusCode());
//		        manuscript.setDeclarationOfInterestContent(vo.getDeclarationOfInterestText());
//		        manuscript.setDeclarationOfInterest(vo.getDeclarationOfInterest());
//		        manuscript.setCopyrightAgreement(vo.getCopyrightAgreement());
//		        manuscript.setCopyrightAgreementContent(vo.getCopyrightAgreementContent());
//
//	        // Step 5: Generate Manuscript Code if missing
//	        if (manuscript.getManuscriptCode() == null || manuscript.getManuscriptCode().isEmpty()) {
//	            Journal journal = journalsRepo.getOne(vo.getJournalId());
//	            Calendar calendar = Calendar.getInstance();
//	            int currentYear = calendar.get(Calendar.YEAR) % 100;
//	            int count = journal.getJournalManuscriptCount() != null ? journal.getJournalManuscriptCount() : 0;
//	            int journalYear = journal.getJournalManuscriptYear() != null ? journal.getJournalManuscriptYear() : currentYear;
//
//	            String code;
//	            if (journalYear != currentYear) {
//	                code = journal.getJournalCode() + "_1_" + currentYear;
//	                journal.setJournalManuscriptCount(1);
//	                journal.setJournalManuscriptYear(currentYear);
//	            } else {
//	                int updatedCount = count + 1;
//	                code = journal.getJournalCode() + "_" + updatedCount + "_" + currentYear;
//	                while (!manuScriptRepo.gettrackManuScriptCode(code).isEmpty()) {
//	                    updatedCount++;
//	                    code = journal.getJournalCode() + "_" + updatedCount + "_" + currentYear;
//	                }
//	                journal.setJournalManuscriptCount(updatedCount);
//	            }
//
//	            manuscript.setManuscriptCode(code);
//	            if (journal.getJournalMetadata() != null && journal.getJournalMetadata().getJournalDOI() != null) {
//	                manuscript.setDoi(journal.getJournalMetadata().getJournalDOI() + journal.getJournalCode() + "." + code);
//	            }
//
//	            journalsRepo.save(journal);
//	            logger.info("Generated manuscript code: {}", code);
//	        }
//
//	        ManuScript savedManuscript = manuScriptRepo.saveAndFlush(manuscript);
//	        
//	        logger.info("Manuscript saved successfully. Code: {}, ID: {}", 
//	                    savedManuscript.getManuscriptCode(), savedManuscript.getManuScriptId());
//
//	        ManuScript fethManucript = manuScriptRepo.findByManuscriptCode(manuscript.getManuscriptCode());
//	        if(fethManucript==null) {
//	        	logger.info("Manuscript fetched again but manucript Code is null for manucriptId: "+savedManuscript.getManuScriptId());
//	        }
//	        return savedManuscript.getManuscriptCode();
//	    }catch (PessimisticLockingFailureException | PessimisticLockException ex) {
//	        logger.error("Pessimistic lock exception for manuscript ID: {}. Error: {}", vo.getId(), ex.getMessage(), ex);
//	        sendAlertEmailSES("Pessimistic Lock Error", vo.getTitle(), vo.getId()); 
//	        return "Lock wait timeout occurred. Please try again later.";
//	    } catch (Exception e) {
//	        logger.error("Exception during manuscript submission for ID: {}. Error: {}", vo.getId(), e.getMessage(), e);
//	        sendAlertEmailSES("Submission Error", vo.getTitle(), vo.getId()); 
//	        return "Error submitting manuscript: " + e.getMessage();
//	    }
//	}
	
	@Override
	@Transactional
	public String submitManuscript(ManuScriptVO vo, JournalManuscriptTypeCharge journalManuscriptTypeCharge) {
	    int retryCount = 0;
	    long sleepTime = 500;
	    String code = null;
	    String doi = null;
	    ManuScript manuscript = null;

	    while (retryCount < 10) {
	        try {
	            logger.info("Starting manuscript submission for ID: {}", vo.getId());
	            
		        int userCountryStatus = Integer.parseInt(manuScriptRepo.getDevelopingCountryStatus(vo.getAuthorId()));
		        User authorOfManuscript = userRepo.getByID(vo.getAuthorId());
		        logger.info("User country status: {}, Author ID: {}", userCountryStatus, vo.getAuthorId());
	            // 1. Lock row explicitly
	            manuscript = manuScriptRepo.fetchManuScript(vo.getId())
	                    .orElseThrow(() -> new IllegalArgumentException("No manuscript found for ID: " + vo.getId()));

		        logger.info("Manuscript locked for update: {}", manuscript.getManuScriptId());

		        manuscript.setManuscriptVersion(vo.getManuscriptVersion());

		        // Step 3: Payment Status Logic
		        if (vo.getStatusCode().equals(2) && journalManuscriptTypeCharge != null) {
		            boolean isChargeRequired = "Yes".equalsIgnoreCase(journalManuscriptTypeCharge.getIsChargesRequired());
		            boolean isSubmissionCharge = "Yes".equalsIgnoreCase(journalManuscriptTypeCharge.getChargeOnSubmission());
		            boolean isPublicationCharge = "Yes".equalsIgnoreCase(journalManuscriptTypeCharge.getChargeOnPublication());
		            String countryCharge = journalManuscriptTypeCharge.getCountryChargeOnSubmission();

		            if (isChargeRequired && isSubmissionCharge && !isPublicationCharge) {
		                if ("SameForAll".equalsIgnoreCase(countryCharge)) {
		                    vo.setStatusCode(64);
		                } else if ("FromDevelopingCountry".equalsIgnoreCase(countryCharge)) {
		                    DevelopedCountriesByManuscriptType developedCountries = developedCountriesByManuscriptTypeRepo
		                            .getByJournalAndManuscriptType(journalManuscriptTypeCharge.getJournalId(),
		                                    journalManuscriptTypeCharge.getManuscriptTypeId());
		                    String paymentCountries = developedCountries.getPaymentCountriesOnSubmission();

		                    logger.info("Payment country rules: {}", paymentCountries);

		                    Integer countryId = authorOfManuscript.getCountries().getId();
		                    if ("India,Others".equals(paymentCountries) ||
		                            ("India".equals(paymentCountries) && countryId.equals(101)) ||
		                            ("Others".equals(paymentCountries) && !countryId.equals(101))) {
		                        vo.setStatusCode(2);
		                    } else {
		                        vo.setStatusCode(64);
		                    }
		                }
		            } else if (isChargeRequired && isSubmissionCharge && isPublicationCharge) {
		                if ("SameForAll".equalsIgnoreCase(countryCharge)) {
		                    vo.setStatusCode(64);
		                } else if ("FromDevelopingCountry".equalsIgnoreCase(countryCharge)) {
		                    DevelopedCountriesByManuscriptType developedCountries = developedCountriesByManuscriptTypeRepo
		                            .getByJournalAndManuscriptType(journalManuscriptTypeCharge.getJournalId(),
		                                    journalManuscriptTypeCharge.getManuscriptTypeId());
		                    String paymentCountries = developedCountries.getPaymentCountriesOnSubmission();

		                    Integer countryId = authorOfManuscript.getCountries().getId();
		                    if ("India,Others".equals(paymentCountries) ||
		                            ("India".equals(paymentCountries) && countryId.equals(101)) ||
		                            ("Others".equals(paymentCountries) && !countryId.equals(101))) {
		                        vo.setStatusCode(2);
		                    } else {
		                        vo.setStatusCode(64);
		                    }
		                }
		            }

		            logger.info("Final status code after payment evaluation: {}", vo.getStatusCode());
		        }

		        // Step 4: Status-based Date updates
		        if (vo.getStatusCode() != null && (vo.getStatusCode().equals(2) || vo.getStatusCode().equals(64))) {
		            manuscript.setSubmissionDate(new Date());
		        }

		        if ("TECHNICAL-MODIFICATION".equalsIgnoreCase(vo.getSubmissionStatus())) {
		            manuscript.setTechnicalModificationDate(new Date());
		        } else if ("RE-SUBMISSION".equalsIgnoreCase(vo.getSubmissionStatus())) {
		            manuscript.setResubmissionDate(new Date());

		            ManuscriptEditor oldEditor = manuscriptEditorRepo.getManuscriptEditorDataForRevision(manuscript.getManuScriptId());
		            if (oldEditor != null) {
		                oldEditor.setIsActive(0);
		                manuscriptEditorRepo.save(oldEditor);

		                ManuscriptEditor newEditor = new ManuscriptEditor();
		                newEditor.setManuscriptId(manuscript.getManuScriptId());
		                newEditor.setManuscriptVersion(manuscript.getManuscriptVersion());
		                newEditor.setManagingEditorId(oldEditor.getManagingEditorId());
		                newEditor.setEditorId(manuscript.getAssignedUser());
		                newEditor.setIsActive(1);
		                newEditor.setJournalId(manuscript.getJournalId());
		                newEditor.setAssignedDate(new Date());
		                newEditor.setManuscriptStatus(manuscript.getManuscriptStatusId());
		                manuscriptEditorRepo.save(newEditor);
		            }
		        }

		        if ("NEW-SUBMISSION".equalsIgnoreCase(vo.getSubmissionStatus())) {
		            List<UserJournal> editors = userJournalRepo.getUserRoleJournalId(manuscript.getJournalId(), 2);
		            if (editors != null && !editors.isEmpty()) {
		                manuscript.setAssignedUser(editors.get(0).getUserId());
		                manuscript.setAssignedUserRole(2);
		            }
		        }		        
				manuscript.setManuscriptStatusId(vo.getStatusCode());
				if (vo.getDeclarationOfInterestText() != null && !vo.getDeclarationOfInterestText().isEmpty())
					manuscript.setDeclarationOfInterestContent(vo.getDeclarationOfInterestText());
				if (vo.getDeclarationOfInterest() != null && !vo.getDeclarationOfInterest().isEmpty())
					manuscript.setDeclarationOfInterest(vo.getDeclarationOfInterest());
				// Always store "Yes" or "No" on final submission (never leave it NULL)
				String copyrightAnswer = normalizeCopyrightAgreement(vo.getCopyrightAgreement());
				if (copyrightAnswer != null)
					manuscript.setCopyrightAgreement(copyrightAnswer);
				else if (manuscript.getCopyrightAgreement() == null)
					manuscript.setCopyrightAgreement("No");
				if (vo.getCopyrightAgreementContent() != null && !vo.getCopyrightAgreementContent().isEmpty())
					manuscript.setCopyrightAgreementContent(vo.getCopyrightAgreementContent());
	            // 2. Generate code if missing
		        if (manuscript.getManuscriptCode() == null || manuscript.getManuscriptCode().isEmpty()) {
		            Journal journal = journalsRepo.getOne(vo.getJournalId());
		            Calendar calendar = Calendar.getInstance();
		            int currentYear = calendar.get(Calendar.YEAR) % 100;
		            int count = journal.getJournalManuscriptCount() != null ? journal.getJournalManuscriptCount() : 0;
		            int journalYear = journal.getJournalManuscriptYear() != null ? journal.getJournalManuscriptYear() : currentYear;

		            if (journalYear != currentYear) {
		                code = journal.getJournalCode() + "_1_" + currentYear;
		                journal.setJournalManuscriptCount(1);
		                journal.setJournalManuscriptYear(currentYear);
		            } else {
		                int updatedCount = count + 1;
		                code = journal.getJournalCode() + "_" + updatedCount + "_" + currentYear;
		                while (!manuScriptRepo.gettrackManuScriptCode(code).isEmpty()) {
		                    updatedCount++;
		                    code = journal.getJournalCode() + "_" + updatedCount + "_" + currentYear;
		                }
		                journal.setJournalManuscriptCount(updatedCount);
		            }

		            manuscript.setManuscriptCode(code);
		            if (journal.getJournalMetadata() != null && journal.getJournalMetadata().getJournalDOI() != null) {
		            	doi= journal.getJournalMetadata().getJournalDOI() + journal.getJournalCode() + "." + code;
		                manuscript.setDoi(doi);
		            }

		            journalsRepo.save(journal);
		            logger.info("Generated manuscript code: {}", code);
		        }else {
		        	code = manuscript.getManuscriptCode();
		        }

	            ManuScript saved = manuScriptRepo.saveAndFlush(manuscript);
	            logger.info("✅ Manuscript saved successfully. Code: {}, ID: {}", saved.getManuscriptCode(), saved.getManuScriptId());
	            return saved.getManuscriptCode();

	        } catch (PessimisticLockingFailureException | PessimisticLockException ex) {
	            retryCount++;
	            logger.error("⚠️ Pessimistic lock exception for manuscript ID: {}. Retry {}/. Error: {}",
	                    vo.getId(), retryCount, ex.getMessage(), ex);
	            

	            try {
	                Thread.sleep(sleepTime); 
	            } catch (InterruptedException ie) {
	                Thread.currentThread().interrupt(); // Restore interrupted status
	                logger.info("Retry sleep interrupted for manuscript ID: {}", vo.getId());
	            }
	            sleepTime *= 2;

	            if (retryCount >= 10) {
	                saveManuscriptClone(vo, manuscript, code,doi, retryCount);
	                sendAlertEmailSES("Pessimistic Lock Error", manuscript.getManuscriptTitle(), vo.getId());
	                return "Lock wait timeout occurred. Data saved in ManuscriptClone.";
	            }
	        } catch (Exception e) {
	            logger.error("❌ Exception during manuscript submission for ID: {}. Error: {}", vo.getId(), e.getMessage(), e);
	            sendAlertEmailSES("Submission Error", vo.getTitle(), vo.getId());
	            return "Error submitting manuscript: " + e.getMessage();
	        }
	    }
	    return "Submission failed after retries.";
	}
	
	@org.springframework.transaction.annotation.Transactional(propagation = Propagation.REQUIRES_NEW)
	private void saveManuscriptClone(ManuScriptVO vo, ManuScript manuscript, String code,String doi, int retryCount) {
	    ManuscriptClone clone = new ManuscriptClone();
	    clone.setManuscriptId(vo.getId());

	    // Pick code
	    if (manuscript != null && manuscript.getManuscriptCode() != null) {
	        clone.setManuscriptCode(manuscript.getManuscriptCode());
	    } else if (code != null) {
	        clone.setManuscriptCode(code);
	    }
	    
	    if(doi!=null) {
	    	clone.setDoi(doi);
	    }

	    clone.setManuscriptVersion(vo.getManuscriptVersion());
	    clone.setStatusCode(vo.getStatusCode());
	    clone.setSubmissionStatus(vo.getSubmissionStatus());
	    clone.setSubmissionDate(new Date());
	    clone.setDeclarationOfInterest(vo.getDeclarationOfInterest());
	    clone.setDeclarationOfInterestContent(vo.getDeclarationOfInterestText());
	    String cloneCopyright = normalizeCopyrightAgreement(vo.getCopyrightAgreement());
	    clone.setCopyrightAgreement(cloneCopyright != null ? cloneCopyright : "No");
	    clone.setCopyrightAgreementContent(vo.getCopyrightAgreementContent());
	    clone.setRetryCount(retryCount);
	    clone.setStatus("FAILED");
	    clone.setSyncWithManuscript(false);

	    ManuscriptClone savedClone = manuscriptCloneRepo.saveAndFlush(clone);

	    logger.info("📌 Manuscript clone saved in DB: {}", savedClone);
	}



	private void sendAlertEmailSES(String alertType, String manuscriptTitle, int manuscriptId) {
	    String smtpHost = "email-smtp.us-east-1.amazonaws.com";
	    String smtpPort = "587";
	    String smtpUser = "AKIAZIRGP356SAZG222F"; 
	    String smtpPass = "BD3xeZ4MFbHzzqCtlr3GtK3tNMC9YblVGhoD4BEd0lVf"; 

	    String mailFrom = "uniprr.td@journalonweb.com";
	    String[] mailTo = {"uniprr.techsupport@digiscapetech.com"};
	    String[] mailCc = {"naresh.kumar@digiscapetech.com","saurabhvikram@digiscapetech.com"};
	    String mailSubject = "[ALERT] " + alertType + " - Manuscript: " + manuscriptTitle;

	    String mailBody = "<html><body>"
	            + "<h3>Manuscript Alert</h3>"
	            + "<p><b>Title:</b> " + manuscriptTitle + "</p>"
	            + "<p><b>Manuscript ID:</b> " + manuscriptId + "</p>"
	            + "<p><b>Alert Type:</b> " + alertType + "</p>"
	            + "<p><b>Time:</b> " + new Date() + "</p>"
	            + "</body></html>";

	    try {
	        Properties props = new Properties();
	        props.put("mail.smtp.host", smtpHost);
	        props.put("mail.smtp.port", smtpPort);
	        props.put("mail.smtp.auth", "true");
	        props.put("mail.smtp.starttls.enable", "true");

	        Session session = Session.getInstance(props);
	        Message message = new MimeMessage(session);
	        message.setFrom(new InternetAddress(mailFrom));

	        InternetAddress[] toAddresses = new InternetAddress[mailTo.length];
	        for (int i = 0; i < mailTo.length; i++) {
	            toAddresses[i] = new InternetAddress(mailTo[i]);
	        }
	        message.setRecipients(Message.RecipientType.TO, toAddresses);
	        
	        if (mailCc != null && mailCc.length > 0) {
	            InternetAddress[] ccAddresses = new InternetAddress[mailCc.length];
	            for (int i = 0; i < mailCc.length; i++) {
	                ccAddresses[i] = new InternetAddress(mailCc[i]);
	            }
	            message.setRecipients(Message.RecipientType.CC, ccAddresses);
	        }

	        message.setSubject(mailSubject);
	        message.setContent(mailBody, "text/html");

	        Transport transport = session.getTransport("smtp");
	        transport.connect(smtpHost, smtpUser, smtpPass);
	        transport.sendMessage(message, message.getAllRecipients());
	        transport.close();

	        logger.info("Alert email sent for manuscript ID: {}", manuscriptId);

	    } catch (MessagingException e) {
	        logger.error("Failed to send alert email for manuscript ID: {}. Error: {}", manuscriptId, e.getMessage(), e);
	    }
	}


	@Override
	public List<ReviewerDecisionVO> getReviewerDecision(UserJournalManuscriptVo userJournalManuscriptVo) {
		List<ReviewerDecisionVO> reviewerDecisionVO = new ArrayList<>();
		List<ManuScript> manuscript = manuScriptRepo.getManuScriptByJournalId(userJournalManuscriptVo.getJournalId());

		for (ManuScript m : manuscript) {
			ReviewerDecisionVO RDVO = new ReviewerDecisionVO();
			List<ManuScriptReviewer> manuScriptReviewer = manuScriptReviewerRepo
					.getManuscriptReviewerListByManuScriptIdAndStatus(m.getManuScriptId(), m.getManuscriptVersion(),
							userJournalManuscriptVo.getManuscriptStatusId());
			List<ReviewerDataWithCommentVO> rsdList = new ArrayList<>();
			if (manuScriptReviewer != null && !manuScriptReviewer.isEmpty()) {
				for (ManuScriptReviewer mr : manuScriptReviewer) {
					ReviewerDataWithCommentVO rsd = new ReviewerDataWithCommentVO();

					rsd.setReviewerName(mr.getManuscript().getUser().getFirstName() + " "
							+ mr.getManuscript().getUser().getLastName());
					rsd.setManuscriptVersion(mr.getManuscriptVersion());
					rsd.setReviewerId(mr.getReviewerId());
					rsd.setManuscriptId(mr.getManuscriptId());
					rsd.setManuscriptStatus(mr.getManuscriptStatus());
					rsd.setEditorId(mr.getEditorId());
					rsd.setManuscriptStatusCode(mr.getManuscript().getStatusMaster().getStatusValue());
					rsd.setCommentsToAuthor(mr.getCommentsToAuthor());
					rsd.setCommentsToEditor(mr.getCommentsToEditor());
					rsd.setDecisionLetter(mr.getDecisionLetter());
					rsdList.add(rsd);
				}
			}
			if (m.getManuscriptTypeDetails() != null) {
				RDVO.setManuscriptType(m.getManuscriptTypeDetails().getManuscriptType());
			}

			RDVO.setManuscriptId(m.getManuScriptId());
			RDVO.setManuscriptCode(m.getManuscriptCode());
			RDVO.setAuthornAME(m.getUser().getFirstName() + " " + m.getUser().getLastName());
			RDVO.setManuscriptTitle(m.getManuscriptTitle());
			RDVO.setSubmissionDate(m.getSubmissionDate());
			RDVO.setReviewerDataWithCommentVO(rsdList);

			reviewerDecisionVO.add(RDVO);

		}

		return reviewerDecisionVO;
	}

	@Override
	public void resubmissionSaveTypeTitleAbstract(ManuscriptTypeTitleAbstractVO vo) {
		ManuScript manuscript = manuScriptRepo.getOne(vo.getManuscriptId());
		manuscript.setManuscriptVersion(vo.getManuscriptVersion() + 1);
		manuscript.setManuscriptTitle(vo.getTitle());
		manuscript.setManuscriptShortTitle(vo.getShortTitle());
		manuscript.setManuscriptAbstract(vo.getManuscriptAbstract());
		manuScriptRepo.save(manuscript);
		ManuscriptTypeDetails details = manuscriptTypeDetailsRepo.getDetailsFromManuscriptId(vo.getManuscriptId());

		if (details == null) {
			details = new ManuscriptTypeDetails();
			details.setManuscriptId(vo.getManuscriptId());
		}
		details.setManuscriptType(vo.getManuscriptTypeJournal());
		details.setManuscriptWords(vo.getManuscriptWords());
		details.setManuscriptBlackWhiteFigures(vo.getBlackAndWhiteFigures());
		details.setManuscriptColorFigures(vo.getManuscriptColorFigures());
		details.setManuscriptTables(vo.getManuscriptTables());
		details.setManuscriptPages(vo.getManuscriptPages());
		manuscriptTypeDetailsRepo.save(details);
	}

	@Override
	public void resubmissionSaveWordCountDetails(ManuScriptVO vo) {
		ManuScript manuscript = manuScriptRepo.getOne(vo.getManuscriptId());
		manuscript.setManuscriptVersion(vo.getManuscriptVersion() + 1);
		manuScriptRepo.save(manuscript);

		ManuscriptTypeDetails details = manuscriptTypeDetailsRepo.getOne(vo.getManuscriptTypeDetailsId());
		details.setManuscriptFigures(vo.getManuscriptFigures());
		details.setManuscriptColorFigures(vo.getManuscriptColorFigures());

		try {
			if (vo.getManuscriptColorFigures() != null && vo.getManuscriptColorFigures().length() > 0
					&& vo.getBlackAndWhiteFigures() != null && vo.getBlackAndWhiteFigures().length() > 0) {
				Integer figures = 0;
				figures = Integer.parseInt(vo.getManuscriptColorFigures())
						+ Integer.parseInt(vo.getBlackAndWhiteFigures());
				details.setManuscriptFigures(figures.toString());
			}
		} catch (Exception e) {
			logger.info("Exception :" + e);
			e.printStackTrace();
		}

		details.setManuscriptTables(vo.getManuscriptTables());
		details.setManuscriptWords(vo.getManuscriptWords());
		details.setManuscriptPages(vo.getManuscriptPages());
		manuscriptTypeDetailsRepo.save(details);

	}

	@Override
	public ManscriptScore getManuScriptScoreCardDeatils(IDVO idvo) {
		return manscriptScoreRepo.getManscriptScoreData(idvo.getId(), idvo.getVersion());
	}

	@Override
	public boolean titleIsExist(ManuscriptTypeTitleAbstractVO vo) {
		List<ManuScript> manuscript = manuScriptRepo.titleIsExist(vo.getTitle());
		if (manuscript != null && !manuscript.isEmpty())
			return true;
		return false;
	}

	public String sendMailForAcknowledgement(String userEmail, String manuscriptCode) {

		String mailSubject = "Manuscript Submit successfully";
		String mailBody = activateUserMailTemplate(userEmail, manuscriptCode);

		mailUtils.sendEmail(springMailUsername, userEmail, mailSubject, mailBody, "", "", "");

		String message = "Manuscript Submit mail send successfully.";

		return message;
	}

	private String activateUserMailTemplate(String email, String manuscriptCode) {

		StringBuilder builder = new StringBuilder();
		/*
		 * builder.append("Dear Author,");
		 * builder.append("\n You have submit manuscript successfully");
		 * builder.append("\n For further enquiry please keep Manuscript id :  "
		 * +manuscriptCode); builder.append("\n \n \n THANK YOU !!");
		 */
		builder.append(
				"<p>Dear Author,</p>	<p>&nbsp;</p><p>You have submit manuscript successfully</p><p>For further enquiry please keep Manuscript id : +manuscriptCode</p><p>THANK YOU !!</p>");
		// <p>Dear Author,</p> <p>&nbsp;</p><p>You have submit manuscript
		// successfully</p><p>For further enquiry please keep Manuscript id :
		// +manuscriptCode</p><p>THANK YOU !!</p>
		return builder.toString();
	}

	@Override
	public ManuscriptVisualization getManuScriptVisualizationDeatils(IDVO idvo) {
		return manuscriptVisualizationRepo.getManuscriptVisualizationDetails(idvo.getId(), idvo.getVersion(),
				idvo.getJournalId());
	}

	@Override
	public List<ManuscriptListVO> getManuscriptStatusListForAuthor(IDVO idvo) {
		List<ManuscriptListVO> addManuscriptListVO = new ArrayList<>();

		if (idvo.getAuthorIdList() != null && !idvo.getAuthorIdList().isEmpty() && idvo.getYearList() != null
				&& !idvo.getYearList().isEmpty()) {
			List<ManuScript> listOfmanuscript = manuScriptRepo.getMnauscriptListByAuthorId(idvo.getAuthorIdList(),
					idvo.getYearList());
			if (listOfmanuscript != null && !listOfmanuscript.isEmpty()) {
				listOfmanuscript.stream().forEach(i -> {
					ManuscriptListVO addManuscriptList = new ManuscriptListVO();
					addManuscriptList.setManuScriptId(i.getManuScriptId());
					addManuscriptList.setManuscriptVersion(i.getManuscriptVersion());
					addManuscriptList.setManuscripCode(i.getManuscriptCode());
					addManuscriptList.setJournalCode(i.getJournal().getJournalCode());
					addManuscriptList.setJournalId(i.getJournalId());
					addManuscriptList.setManuScriptType(i.getManuscriptTypeDetails().getManuscriptType());
					addManuscriptList.setManuscripShortTitle(i.getManuscriptShortTitle());
					addManuscriptList.setManuscripTitle(i.getManuscriptTitle());
					addManuscriptList.setManuscriptAbstract(i.getManuscriptAbstract());
					addManuscriptList.setManuscriptRunningHead(i.getManuscriptRunningHead());
					addManuscriptList.setAuthorName(i.getUser().getFirstName() + " " + i.getUser().getLastName());
					addManuscriptList.setManuStatusCode(i.getStatusMaster().getStatusValue());
					addManuscriptList.setSubmissionDate(i.getSubmissionDate());
					addManuscriptList.setFinalDecisionDate(i.getFinalDecisionDate());
					addManuscriptList.setPublishingDate(i.getPublishingDate());
					addManuscriptList.setWithdrawnDate(i.getWithdrawnDate());
					addManuscriptListVO.add(addManuscriptList);
				});
			}
		}

		return addManuscriptListVO;
	}

	@Override
	public void deleteCoAuthorList(Integer id) {
		ManuScriptCoAuthor coauthor = manuScriptCoAuthorRepo.getCoAuthorById(id);
		int order = 1;
		manuScriptCoAuthorRepo.deleteById(id);
		List<ManuScriptCoAuthor> coauthorList = manuScriptCoAuthorRepo
				.getCoAuthorListFromManuscriptId(coauthor.getCoAuthorManuscriptId());
		for (ManuScriptCoAuthor manuCoauthor : coauthorList) {
			manuCoauthor.setAuthorOrder(order);
			manuScriptCoAuthorRepo.save(manuCoauthor);
			order++;
		}

	}

	@Override
	public ManuscriptListVO getManuscriptDataIdAndVersion(Integer mansuscriptId, Integer manuscriptVersion,
			String comment) {
		ManuscriptListVO manuscriptListVO = new ManuscriptListVO();
		try {
			if (mansuscriptId != null && manuscriptVersion != null) {
				ManuScript manuscript = manuScriptRepo.getManuscriptByIdAndVersion(mansuscriptId, manuscriptVersion);
				if (manuscript != null) {
					manuscriptListVO.setManuScriptId(manuscript.getManuScriptId());
					manuscriptListVO.setManuscriptVersion(manuscript.getManuscriptVersion());
					manuscriptListVO.setManuscripCode(manuscript.getManuscriptCode());
					manuscriptListVO.setJournalCode(manuscript.getJournal().getJournalCode());
					manuscriptListVO.setJournalId(manuscript.getJournalId());
					manuscriptListVO.setManuscripShortTitle(manuscript.getManuscriptShortTitle());
					manuscriptListVO.setManuscripTitle(manuscript.getManuscriptTitle());
					manuscriptListVO.setManuscriptAbstract(manuscript.getManuscriptAbstract());
					manuscriptListVO.setDoi(manuscript.getDoi());
					manuscriptListVO
							.setCommentForProduction(comment == null ? manuscript.getCommentForProduction() : comment);
					manuscriptListVO.setTrialRegistrationNo(manuscript.getTrialRegistrationNo());
					if (manuscript.getManuscriptTypeDetails() != null
							&& manuscript.getManuscriptTypeDetails().getManuscriptWords() != null) {
						manuscriptListVO.setWord(manuscript.getManuscriptTypeDetails().getManuscriptWords());
					}

					if (manuscript.getManuscriptTypeDetails().getManuscriptType() != null) {
						manuscriptListVO.setManuScriptType(manuscript.getManuscriptTypeDetails().getManuscriptType());
					} else {
						manuscriptListVO.setManuScriptType("");
					}

					manuscriptListVO.setKeywords(manuscript.getManuscriptKeywords());

					ManuScriptFile firstPageFile = manuScriptFileRepo.getManuScriptFirstPageFile(
							manuscript.getManuScriptId(), manuscript.getManuscriptVersion(), 1);
					if (firstPageFile != null) {
						manuscriptListVO.setFirstpagefile(firstPageFile.getFileName());
					} else {
						manuscriptListVO.setFirstpagefile("");
					}
					ManuScriptFile manuscriptFile = manuScriptFileRepo.getManuScriptFile(manuscript.getManuScriptId(),
							manuscript.getManuscriptVersion(), 1);
					if (manuscriptFile != null) {
						manuscriptListVO.setArticleFile(manuscriptFile.getFileName());
					} else {
						manuscriptListVO.setArticleFile("");
					}

					if (manuscript.getQrFileName() != null) {
						manuscriptListVO.setQr_code(manuscript.getQrFileName());
					} else {
						manuscriptListVO.setQr_code("");
					}

					manuscriptListVO.setEmail(manuscript.getUser().getEmail());
					manuscriptListVO.setSalutatio(manuscript.getUser().getSalutation());
					manuscriptListVO.setManuscriptRunningHead(manuscript.getManuscriptRunningHead());
					manuscriptListVO.setAuthorName(
							manuscript.getUser().getFirstName() + " " + manuscript.getUser().getLastName());
					manuscriptListVO.setManuStatusCode(manuscript.getStatusMaster().getStatusValue());
					manuscriptListVO.setStatusValueForAuthor(manuscript.getStatusMaster().getStatusValueForAuthor());
					manuscriptListVO.setStatusValueForEditor(manuscript.getStatusMaster().getStatusValueForEditor());
					manuscriptListVO.setSubmissionDate(manuscript.getSubmissionDate());
					manuscriptListVO.setResubmissionDate(manuscript.getResubmissionDate());
					manuscriptListVO.setFinalDecisionDate(manuscript.getFinalDecisionDate());
					manuscriptListVO.setPublishingDate(manuscript.getPublishingDate());
					manuscriptListVO.setWithdrawnDate(manuscript.getWithdrawnDate());
					manuscriptListVO.setAcceptedDate(manuscript.getUpdatedOn());
					manuscriptListVO.setEditorUserId(manuscript.getAssignedUser());
					manuscriptListVO.setEditorRoleId(manuscript.getAssignedUserRole());

					List<AuthorVO> authorList = new ArrayList<>();
					List<ManuScriptCoAuthor> authors = manuScriptCoAuthorRepo
							.getPrimaryAuthorsByManuscriptId(mansuscriptId);
					if (authors != null && !authors.isEmpty()) {
						User authorData = null;
						AuthorVO authorVO = null;
						for (ManuScriptCoAuthor author : authors) {
							authorData = author.getUser();
							if (authorData != null) {
								authorVO = AuthorTransformatonUtils.mapUserToAuthorVo(authorData);
								authorVO.setCoAuthorOrder("Primary");
								authorVO.setIs_corresponding(author.getIsCorrespondingAuthor());
								if (manuscript.getCopyrightAgreement() != null) {
									authorVO.setCopyRightForm(manuscript.getCopyrightAgreement());
								}
								if (manuscript.getCopyrightAgreementContent() != null) {
									authorVO.setCopyRightContent(manuscript.getCopyrightAgreementContent());
								}
								authorList.add(authorVO);
							}
						}
					}

					List<ManuScriptCoAuthor> manuScriptCoAuthors = manuScriptCoAuthorRepo
							.getCoAuthorsByManuscriptId(mansuscriptId);
					if (manuScriptCoAuthors != null && !manuScriptCoAuthors.isEmpty()) {
						User coAuthorData = null;
						AuthorVO authorVO = null;
						for (ManuScriptCoAuthor manuScriptCoAuthor : manuScriptCoAuthors) {
							coAuthorData = manuScriptCoAuthor.getUser();
							if (coAuthorData != null) {
								authorVO = AuthorTransformatonUtils.mapUserToAuthorVo(coAuthorData);
								authorVO.setCoAuthorOrder(manuScriptCoAuthor.getAuthorOrder().toString());
								authorVO.setIs_corresponding(manuScriptCoAuthor.getIsCorrespondingAuthor());
								if (manuScriptCoAuthor.getCopyrightAgreement() != null) {
									authorVO.setCopyRightForm(manuScriptCoAuthor.getCopyrightAgreement());
								}
								if (manuScriptCoAuthor.getCopyrightAgreementContent() != null) {
									authorVO.setCopyRightContent(manuScriptCoAuthor.getCopyrightAgreementContent());
								}
								authorList.add(authorVO);
							}
						}
					}
					manuscriptListVO.setAuhtor(authorList);

					List<ImageVO> imageList = new ArrayList<>();
					List<ManuScriptFile> manuscriptFiles = manuScriptFileRepo
							.getDataByManuscriptIdAndVersion(mansuscriptId, manuscriptVersion);
					if (manuscriptFiles != null && !manuscriptFiles.isEmpty()) {
						ImageVO imageData = null;
						for (int j = 0; j < manuscriptFiles.size(); j++) {
							imageData = new ImageVO();
							imageData.setImage_file_name(manuscriptFiles.get(j).getFileName());
							imageData.setImage_type(manuscriptFiles.get(j).getFileType());
							if (manuscriptFiles.get(j).getLegend() != null) {
								imageData.setLegend(manuscriptFiles.get(j).getLegend());
							} else {
								imageData.setLegend("");
							}
							imageData.setImage_file_path(manuscriptFiles.get(j).getFilePath());
							imageData.setCitation("");
							imageList.add(imageData);
						}
					}
					manuscriptListVO.setImage(imageList);
				}
			}
		} catch (Exception e) {
			String errorMessage = "An error occurred while processing the manuscript data: " + e.getMessage();
			logger.error(errorMessage);
		}

		return manuscriptListVO;
	}

	@Override
	public boolean craeatedXmlFileCreate(ManuscriptListVO manuscriptListVO, String operation) {
		logger.info("Inside craeatedXmlFileCreate");

		String baseFolderPath = ManuscriptUtils.generateManuscriptFolderPath(uniprrFolderPath,
				manuscriptListVO.getJournalId(), manuscriptListVO.getManuScriptId(),
				manuscriptListVO.getManuscriptVersion());

		ExecutorService executorService = Executors.newFixedThreadPool(1);
		CompletableFuture.runAsync(() -> {
			boolean success = true;
			try {
				// Create XML file
				ManuscriptUtils.generateXMLFile(manuscriptListVO, baseFolderPath);
			} catch (ParserConfigurationException | TransformerException e) {
				success = false;
				logger.error(
						"Exception while creating XML file for manuscriptId : " + manuscriptListVO.getManuScriptId(),
						e);

				ErrorLog errorLog = new ErrorLog();
				errorLog.setJournalId(manuscriptListVO.getJournalId());
				errorLog.setManuscriptId(manuscriptListVO.getManuScriptId());
				errorLog.setManuscriptVersion(manuscriptListVO.getManuscriptVersion());
				errorLog.setErrorMessage("XML creation failed : " + e.getMessage());
				errorLog.setOperation(operation);
				errorLogRepo.save(errorLog);
			}

			if (success) {
				// Create QR Code file
				success = generateBarCode(manuscriptListVO, baseFolderPath, operation);
				if (success) {
					// Create zip file
					success = ManuscriptUtils.createZipFile(manuscriptListVO, errorLogRepo, baseFolderPath, operation);
					if (success) {
						if (enableUnitouchIntegration) {
							// Upload/copy for Unitouch ingestion
							success = copyUploadForUnitouch(manuscriptListVO, baseFolderPath);
						} else {
							// Upload to vendor sftp
							success = uploadToVendorSftp(manuscriptListVO, baseFolderPath);
						}
					}
				}
			}
			logger.info("craeatedXmlFileCreate success : {}", success);
		}, executorService);

		return true;
	}

	/**
	 * Used for manual submission of manuscript for "Accept for Production"
	 */
	@Override
	public boolean submitForAFP(ManuscriptListVO manuscriptListVO, String operation) {
		logger.info("Inside craeatedXmlFileCreate");

		String baseFolderPath = ManuscriptUtils.generateManuscriptFolderPath(uniprrFolderPath,
				manuscriptListVO.getJournalId(), manuscriptListVO.getManuScriptId(),
				manuscriptListVO.getManuscriptVersion());

		boolean success = true;
		try {
			// Create XML file
			ManuscriptUtils.generateXMLFile(manuscriptListVO, baseFolderPath);
		} catch (ParserConfigurationException | TransformerException e) {
			success = false;
			logger.error("Exception while creating XML file for manuscriptId : " + manuscriptListVO.getManuScriptId(),
					e);

			ErrorLog errorLog = new ErrorLog();
			errorLog.setJournalId(manuscriptListVO.getJournalId());
			errorLog.setManuscriptId(manuscriptListVO.getManuScriptId());
			errorLog.setManuscriptVersion(manuscriptListVO.getManuscriptVersion());
			errorLog.setErrorMessage("XML creation failed : " + e.getMessage());
			errorLog.setOperation(operation);
			errorLogRepo.save(errorLog);
		}

		if (success) {
			// Create QR Code file
			success = generateBarCode(manuscriptListVO, baseFolderPath, operation);
			if (success) {
				// Create zip file
				success = ManuscriptUtils.createZipFile(manuscriptListVO, errorLogRepo, baseFolderPath, operation);
				if (success) {
					if (enableUnitouchIntegration) {
						// Upload/copy for Unitouch ingestion
						success = copyUploadForUnitouch(manuscriptListVO, baseFolderPath);
					} else {
						// Upload to vendor sftp
						success = uploadToVendorSftp(manuscriptListVO, baseFolderPath);
					}
				}
			}
		}
		logger.info("craeatedXmlFileCreate success : {}", success);

		return success;
	}

	@Override
	public boolean craeatedXmlFileCreateForJle(ManuscriptListVO manuscriptListVO, String operation) {
		logger.info("Inside craeatedXmlFileCreateForJle");

		String baseFolderPath = ManuscriptUtils.generateManuscriptFolderPath(uniprrFolderPath,
				manuscriptListVO.getJournalId(), manuscriptListVO.getManuScriptId(),
				manuscriptListVO.getManuscriptVersion());
		boolean success = true;
		try {
			ManuscriptUtils.generateXMLFile(manuscriptListVO, baseFolderPath);
		} catch (ParserConfigurationException | TransformerException e) {
			success = false;
			logger.error("Exception while creating XML file for manuscriptId : " + manuscriptListVO.getManuScriptId(),
					e);

			ErrorLog errorLog = new ErrorLog();
			errorLog.setJournalId(manuscriptListVO.getJournalId());
			errorLog.setManuscriptId(manuscriptListVO.getManuScriptId());
			errorLog.setManuscriptVersion(manuscriptListVO.getManuscriptVersion());
			errorLog.setErrorMessage("XML creation failed : " + e.getMessage());
			errorLog.setOperation(operation);
			errorLogRepo.save(errorLog);
		}

		if (success) {
			success = generateBarCode(manuscriptListVO, baseFolderPath, operation);
			if (success) {
				// Create zip file
				success = ManuscriptUtils.createZipFile(manuscriptListVO, errorLogRepo, baseFolderPath, operation);
			}
		}

		logger.info("craeatedXmlFileCreateForJle success : {} ", success);

		return success;
	}

	private boolean generateBarCode(ManuscriptListVO manuscriptListVO, String baseFolderPath, String operation) {
		boolean success = true;

		if (manuscriptListVO.getManuScriptId() != null) {
			String text = "https://journals.lww.com/" + manuscriptListVO.getDoi();
			int width = 500;
			int height = 500;

			try {
				File qrPath = new File(baseFolderPath + manuscriptListVO.getManuscripCode() + ".png");
				qrPath.createNewFile(); // if file already exists will do nothing
				Integer manuid = manuscriptListVO.getManuScriptId();
				String filepath = qrPath.toString().replace(uniprrFolderPath, awsFilePath);
				String filename = manuscriptListVO.getManuscripCode() + ".png";

				// Save Data in Db
				manuScriptRepo.saveQrcode(manuid, filepath, filename);

				QRCodeWriter qrCodeWriter = new QRCodeWriter();
				BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);
				Path path = FileSystems.getDefault().getPath(qrPath.getAbsolutePath());
				MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);

				logger.info("BarCode created successfully");
			} catch (WriterException | IOException e) {
				success = false;
				logger.error(
						"Exception while generating barcode for mansucriptId : " + manuscriptListVO.getManuScriptId(),
						e);

				ErrorLog errorLog = new ErrorLog();
				errorLog.setJournalId(manuscriptListVO.getJournalId());
				errorLog.setManuscriptId(manuscriptListVO.getManuScriptId());
				errorLog.setManuscriptVersion(manuscriptListVO.getManuscriptVersion());
				errorLog.setErrorMessage("Barcode generation failed : " + e.getMessage());
				errorLog.setOperation(operation);
				errorLogRepo.save(errorLog);
			}
		}

		return success;
	}

	private boolean copyUploadForUnitouch(ManuscriptListVO manuscriptListVO, String baseFolderPath) {
		boolean success = true;
		String message = "";
		if (AppConstants.ENVIRONMENT_PROD.equalsIgnoreCase(environmentType)) {
			success = SftpUtil.uploadToSftp(manuscriptListVO, baseFolderPath, sftpIp, sftpPort, sftpUser, sftpPassword,
					medknowSftpPath);
			if (!success) {
				message = "sFTP upload for Unitouch failed ";
			}
		} else {
			try {
				ManuscriptUtils.copyForUnitouchIngestion(manuscriptListVO, baseFolderPath, medknowLocalPath);
			} catch (IOException e) {
				success = false;
				message = "Local copy for Unitouch failed : " + e.getMessage();
				logger.error("Exception while copying for Unitouch ingestion :", e);
			}
		}

		if (!success) {
			ErrorLog errorLog = new ErrorLog();
			errorLog.setJournalId(manuscriptListVO.getJournalId());
			errorLog.setManuscriptId(manuscriptListVO.getManuScriptId());
			errorLog.setManuscriptVersion(manuscriptListVO.getManuscriptVersion());
			errorLog.setErrorMessage(message);
			errorLog.setOperation(AppConstants.ACCEPT_FOR_PRODUCTION);
			errorLogRepo.save(errorLog);
		}

		return success;
	}

	private boolean uploadToVendorSftp(ManuscriptListVO manuscriptListVO, String baseFolderPath) {
		String message = "";
		boolean success = SftpUtil.uploadToSftp(manuscriptListVO, baseFolderPath, vendorSftpIp, vendorSftpPort,
				vendorSftpUser, vendorSftpPassword, vendorSftpPath);
		if (!success) {
			message = "sFTP upload for Unitouch failed ";
		}

		if (!success) {
			ErrorLog errorLog = new ErrorLog();
			errorLog.setJournalId(manuscriptListVO.getJournalId());
			errorLog.setManuscriptId(manuscriptListVO.getManuScriptId());
			errorLog.setManuscriptVersion(manuscriptListVO.getManuscriptVersion());
			errorLog.setErrorMessage(message);
			errorLog.setOperation(AppConstants.ACCEPT_FOR_PRODUCTION);
			errorLogRepo.save(errorLog);
		}

		return success;
	}

	@Override
	public ManuscriptListVO getManuscriptDataIdAndVersionZipfile(Integer mansuscriptId, Integer manuscriptVersion) {
		ManuscriptListVO manuscriptListVO = new ManuscriptListVO();
		if (mansuscriptId != null && manuscriptVersion != null) {
			ManuScript manuscript = manuScriptRepo.getManuscriptByIdAndVersion(mansuscriptId, manuscriptVersion);
			if (manuscript != null) {
				manuscriptListVO.setManuScriptId(manuscript.getManuScriptId());
				manuscriptListVO.setManuscriptVersion(manuscript.getManuscriptVersion());
				manuscriptListVO.setManuscripCode(manuscript.getManuscriptCode());
				manuscriptListVO.setJournalCode(manuscript.getJournal().getJournalCode());
				manuscriptListVO.setJournalId(manuscript.getJournalId());
				manuscriptListVO.setManuscripShortTitle(manuscript.getManuscriptShortTitle());
				manuscriptListVO.setManuscripTitle(manuscript.getManuscriptTitle());
				manuscriptListVO.setManuscriptAbstract(manuscript.getManuscriptAbstract());
				if (manuscript.getManuscriptTypeDetails().getManuscriptType() != null) {
					manuscriptListVO.setManuScriptType(manuscript.getManuscriptTypeDetails().getManuscriptType());
				} else {
					manuscriptListVO.setManuScriptType("");
				}
				manuscriptListVO.setKeywords("");
				manuscriptListVO.setArticleFile("");
				manuscriptListVO.setDoi("");
				manuscriptListVO.setEmail(manuscript.getUser().getEmail());
				manuscriptListVO.setSalutatio(manuscript.getUser().getSalutation());
				manuscriptListVO.setManuscriptRunningHead(manuscript.getManuscriptRunningHead());
				manuscriptListVO
						.setAuthorName(manuscript.getUser().getFirstName() + " " + manuscript.getUser().getLastName());
				manuscriptListVO.setManuStatusCode(manuscript.getStatusMaster().getStatusValue());
				manuscriptListVO.setSubmissionDate(manuscript.getSubmissionDate());
				manuscriptListVO.setFinalDecisionDate(manuscript.getFinalDecisionDate());
				manuscriptListVO.setPublishingDate(manuscript.getPublishingDate());
				manuscriptListVO.setWithdrawnDate(manuscript.getWithdrawnDate());
				manuscriptListVO.setSubmissionDate(manuscript.getSubmissionDate());
				manuscriptListVO.setAcceptedDate(manuscript.getUpdatedOn());
				manuscriptListVO.setPublishingDate(manuscript.getPublishingDate());

				List<ImageVO> imageList = new ArrayList<>();
				List<ManuScriptFile> manuscriptFiles = manuScriptFileRepo.getManuScriptFilegetById(mansuscriptId);
				if (manuscriptFiles != null && !manuscriptFiles.isEmpty()) {
					ImageVO listdata = null;
					for (ManuScriptFile manuscriptFile : manuscriptFiles) {
						listdata = new ImageVO();
						listdata.setImage_file_name(manuscriptFile.getFileName());
						listdata.setImage_type(manuscriptFile.getFileType());
						listdata.setLegend("");
						listdata.setImage_file_path(manuscriptFile.getFilePath());
						listdata.setCitation("");
						imageList.add(listdata);
					}
				}
				manuscriptListVO.setImage(imageList);
			}
		}

		return manuscriptListVO;
	}

	@Override
	public boolean craeatedZipFileCreate(ManuscriptListVO manuscriptListVO) {
		logger.info("Inside craeatedZipFileCreate");
		boolean success = true;

		try {
			Path manuscriptInputPath = Paths.get(System.getProperty("catalina.base") + File.separator + "webapps"
					+ File.separator + "Uniprr_Zip" + File.separator);
			CommonFileUtils.deleteFolderRecursively(manuscriptInputPath);

			String baseFolderPath = ManuscriptUtils.generateManuscriptFolderPath(uniprrFolderPath,
					manuscriptListVO.getJournalId(), manuscriptListVO.getManuScriptId(),
					manuscriptListVO.getManuscriptVersion());
			boolean zipResult = ManuscriptUtils.createZipFile(manuscriptListVO, errorLogRepo, baseFolderPath, "-");
			if (zipResult) {
				if (enableUnitouchIntegration) {
					// Upload/copy for Unitouch ingestion
					success = copyUploadForUnitouch(manuscriptListVO, baseFolderPath);
				} else {
					// Upload to vendor sftp
					success = uploadToVendorSftp(manuscriptListVO, baseFolderPath);
				}
			}
		} catch (Exception e) {
			success = false;
			logger.error("Exception : ", e);
		}

		return success;

	}

	@Override
	public void updateUrl(ManuScript manuscript) {
		ManuScript manuScriptData = manuScriptRepo.findByManuscriptCode(manuscript.getManuscriptCode());
		if (manuScriptData != null) {
			manuScriptData.setManuscriptStatusId(manuscript.getManuscriptStatusId());
			manuScriptData.setInputFile(manuscript.getInputFile());
			manuScriptData.setOutputFile(manuscript.getOutputFile());
			manuScriptData.setManuscriptTaskId(manuscript.getManuscriptTaskId().trim());
			manuScriptData.setCommentFromProduction(manuscript.getCommentFromProduction());
			manuScriptData.setFileType(manuscript.getFileType());
			if (manuscript.getManuscriptStatusId().equals(57)) {
				WorkflowRoleSeq workflowtat = workflowRoleRepo.getJournalWorkflowIdTaskId(28,
						manuScriptData.getJournalId());
				if (null != workflowtat) {
					Date dueDate = workflowtat.getTat().equals(0) ? new Date()
							: new Date().from(LocalDate.now().plusDays(workflowtat.getTat())
									.atStartOfDay(ZoneId.systemDefault()).toInstant());
					manuScriptData.setDueDate(dueDate);
					manuScriptData.setAuthorProofDate(new Date());

					authorityHis(manuScriptData, dueDate, 57);
				}
			}
			if (manuscript.getManuscriptStatusId().equals(62)) {
				WorkflowRoleSeq workflowtat = workflowRoleRepo.getJournalWorkflowIdTaskId(39,
						manuScriptData.getJournalId());
				if (null != workflowtat) {
					Date dueDate = workflowtat.getTat().equals(0) ? new Date()
							: new Date().from(LocalDate.now().plusDays(workflowtat.getTat())
									.atStartOfDay(ZoneId.systemDefault()).toInstant());
					manuScriptData.setDueDate(dueDate);
					manuScriptData.setAuthorProofDate(new Date());
					authorityHis(manuScriptData, dueDate, 62);

				}
			}
			manuScriptRepo.save(manuScriptData);
		}

	}

	@Override
	public void updateUrlwithoutfile(ManuScript manuscript) {
		ManuScript manuScriptData = manuScriptRepo.findByManuscriptCode(manuscript.getManuscriptCode());
		if (manuScriptData != null) {
			manuScriptData.setManuscriptStatusId(manuscript.getManuscriptStatusId());
			manuScriptData.setPublishingDate(new Date());
			manuScriptData.setIssueName(manuscript.getInputFile());
			manuScriptRepo.save(manuScriptData);
		}

	}

	@Override
	public String sendEmailToCoAuthorOnManuscript(ManuScriptVO vo) {
		List<ManuScriptCoAuthor> getManuScriptCoAuthorList = manuScriptCoAuthorRepo
				.sendEmailToCoAuthorOnManuscript(vo.getId(), 0);
		if (getManuScriptCoAuthorList != null && !getManuScriptCoAuthorList.isEmpty()) {
			getManuScriptCoAuthorList.stream().forEach(i -> {
				if (i.getCoAuthorType() != null && i.getCoAuthorType().equals(0)) {
					ManuScript getManuScriptDetails = manuScriptRepo.getOne(i.getCoAuthorManuscriptId());
					User getUser = userRepo.getOne(i.getCoAuthorId());
					if (getManuScriptDetails != null && getUser != null) {
						List<String> journalWebSiteUrl = new ArrayList<>();
						if (getManuScriptDetails.getJournal().getJournalMetadata() != null) {
							journalWebSiteUrl.add(getManuScriptDetails.getJournal().getJournalMetadata().getUrl());
						}

						String message = sendMailForActivation(getUser.getEmail(), getUser.getSalutation().toString(),
								getUser.getFirstName(), getUser.getLastName(),
								getManuScriptDetails.getManuscriptTitle(),
								getManuScriptDetails.getJournal().getJournalTitle(), vo.getCopyRightUrlForCoAuthor(),
								i.getId(), getManuScriptDetails.getJournalId(),
								getManuScriptDetails.getJournal().getJournalCode(), journalWebSiteUrl);
						if (message.equalsIgnoreCase("Mail send successfully.")) {
							ManuScriptCoAuthor getManuScriptCoAuthor = manuScriptCoAuthorRepo.getOne(i.getId());
							getManuScriptCoAuthor.setIsEmailSent(1);
							manuScriptCoAuthorRepo.save(getManuScriptCoAuthor);
						}
					}
				} else if (i.getCoAuthorType() != null && i.getCoAuthorType().equals(1)) {
					ManuScript getManuScriptDetails = manuScriptRepo.getOne(i.getCoAuthorManuscriptId());
					User getUser = userRepo.getOne(i.getCoAuthorId());
					if (getManuScriptDetails != null && getUser != null) {
						String token = UUID.randomUUID().toString();
						List<String> journalWebSiteUrl = new ArrayList<>();
						if (getManuScriptDetails.getJournal().getJournalMetadata() != null) {
							journalWebSiteUrl.add(getManuScriptDetails.getJournal().getJournalMetadata().getUrl());
						}
						String message = sendMailForActivationForCoAuthor(getUser.getEmail(), token,
								vo.getActivationUrl(), getUser.getSalutation().toString(), getUser.getFirstName(),
								getUser.getLastName(), getManuScriptDetails.getManuscriptTitle(),
								getManuScriptDetails.getJournal().getJournalTitle(), vo.getCopyRightUrlForCoAuthor(),
								i.getId(), getManuScriptDetails.getJournalId(),
								getManuScriptDetails.getJournal().getJournalCode(), journalWebSiteUrl);
						if (message.equalsIgnoreCase("Password reset URL send on mail successfully.")) {
							ManuScriptCoAuthor getManuScriptCoAuthor = manuScriptCoAuthorRepo.getOne(i.getId());
							getManuScriptCoAuthor.setIsEmailSent(1);
							manuScriptCoAuthorRepo.save(getManuScriptCoAuthor);
						}
					}
				}
			});
			return "co author email sent";
		}
		return "there is no co author added to sent an email";
	}

	@Override
	public String coAuthorSendMail(ManuScriptVO vo) {
		ManuScriptCoAuthor i = manuScriptCoAuthorRepo.getOne(vo.getId());
		if (i != null) {
			if (i.getCoAuthorType() != null && i.getCoAuthorType().equals(0)) {
				ManuScript getManuScriptDetails = manuScriptRepo.getOne(i.getCoAuthorManuscriptId());
				User getUser = userRepo.getOne(i.getCoAuthorId());
				if (getManuScriptDetails != null && getUser != null) {
					List<String> journalWebSiteUrl = new ArrayList<>();
					if (getManuScriptDetails.getJournal().getJournalMetadata() != null) {
						journalWebSiteUrl.add(getManuScriptDetails.getJournal().getJournalMetadata().getUrl());
					}
					sendMailForActivation(getUser.getEmail(), getUser.getSalutation().toString(),
							getUser.getFirstName(), getUser.getLastName(), getManuScriptDetails.getManuscriptTitle(),
							getManuScriptDetails.getJournal().getJournalTitle(), vo.getCopyRightUrlForCoAuthor(),
							i.getId(), getManuScriptDetails.getJournalId(),
							getManuScriptDetails.getJournal().getJournalCode(), journalWebSiteUrl);
				}
			} else if (i.getCoAuthorType() != null && i.getCoAuthorType().equals(1)) {
				ManuScript getManuScriptDetails = manuScriptRepo.getOne(i.getCoAuthorManuscriptId());
				User getUser = userRepo.getOne(i.getCoAuthorId());
				if (getManuScriptDetails != null && getUser != null) {
					List<String> journalWebSiteUrl = new ArrayList<>();
					if (getManuScriptDetails.getJournal().getJournalMetadata() != null) {
						journalWebSiteUrl.add(getManuScriptDetails.getJournal().getJournalMetadata().getUrl());
					}

					String token = UUID.randomUUID().toString();
					sendMailForActivationForCoAuthor(getUser.getEmail(), token, vo.getActivationUrl(),
							getUser.getSalutation().toString(), getUser.getFirstName(), getUser.getLastName(),
							getManuScriptDetails.getManuscriptTitle(),
							getManuScriptDetails.getJournal().getJournalTitle(), vo.getCopyRightUrlForCoAuthor(),
							i.getId(), getManuScriptDetails.getJournalId(),
							getManuScriptDetails.getJournal().getJournalCode(), journalWebSiteUrl);
				}
			}
			return "co author email sent";
		}
		return "there is no co author added to sent an email";
	}
	
	@Transactional
	public String sendMailForActivation(String userEmail, String salutation, String firstName, String lastName,
	                                    String manuscriptTitle, String journalTitle, String copyRightUrl, Integer coAuthorPrimaryId,
	                                    Integer journalId, String journalCode, List<String> journalWebSiteUrl) {
	    logger.info("=== sendMailForActivation START ===");
	    logger.info("Email: {}, JournalId: {}, CoAuthorId: {}", userEmail, journalId, coAuthorPrimaryId);

	    String toMailID = userEmail;
	    String mailSubject = "Copyright agreement";
	    String mailBody = activateUserMailTemplate(userEmail, salutation, firstName, lastName, manuscriptTitle,
	            journalTitle, copyRightUrl, coAuthorPrimaryId, journalId, journalCode, journalWebSiteUrl);

	    OtherMail add = new OtherMail();
	    add.setJournalId(journalId);
	    add.setToMail(toMailID);
	    add.setSubject(mailSubject);
	    add.setMailBody(mailBody);

	    try {
	        Journal i = journalsRepo.getOne(journalId);
	        logger.info("Journal fetched: {}", i != null ? i.getJournalTitle() : "NULL");

	        MailQueue addMailQueue = new MailQueue();
	        addMailQueue.setMailFrom(springMailUsername);
	        addMailQueue.setMailTo(toMailID);
	        addMailQueue.setJournalId(journalId);
	        addMailQueue.setManuscriptId(0);
	        addMailQueue.setMailSubject(mailSubject);
	        addMailQueue.setMailBody(mailBody);
	        addMailQueue.setMail_send_date(new Date());
	        addMailQueue.setIsActive(0);

	        try {
	            logger.info("Saving to MailQueue...");
	            MailQueue savedQueue = mailQueueRepo.saveAndFlush(addMailQueue);
	            logger.info("MailQueue saved successfully with ID: {}", savedQueue.getId());
	        } catch (Exception e) {
	            logger.error("Error saving MailQueue: {}", e.getMessage(), e);
	            throw e; // rethrow to rollback transaction
	        }

	        add.setStatus("Insert into mailQueue");
	        add.setCreatedAt(new Date());
	        try {
	            logger.info("Saving to OtherMail...");
	            otherMailRepo.save(add);
	            logger.info("OtherMail saved successfully");
	        } catch (Exception e) {
	            logger.error("Error saving OtherMail: {}", e.getMessage(), e);
	            throw e;
	        }

	    } catch (Exception e) {
	        logger.error("Unexpected error in sendMailForActivation: {}", e.getMessage(), e);
	        return "Error: " + e.getMessage();
	    }

	    logger.info("=== sendMailForActivation END ===");
	    return "Mail send successfully.";
	}


//	@Transactional
//	public String sendMailForActivation(String userEmail, String salutation, String firstName, String lastName,
//			String manuscriptTitle, String journalTitle, String copyRightUrl, Integer coAuthorPrimaryId,
//			Integer journalId, String journalCode, List<String> journalWebSiteUrl) {
//		String toMailID = userEmail;
//		String mailSubject = "Copyright agreement";
//		String mailBody = activateUserMailTemplate(userEmail, salutation, firstName, lastName, manuscriptTitle,
//				journalTitle, copyRightUrl, coAuthorPrimaryId, journalId, journalCode, journalWebSiteUrl);
//		OtherMail add = new OtherMail();
//		add.setJournalId(journalId);
//		add.setToMail(toMailID);
//		add.setSubject(mailSubject);
//		add.setMailBody(mailBody);
//		Journal i = journalsRepo.getOne(journalId);
//		if (i != null && i.getJournalMetadata() != null) {
//			if (i.getJournalMetadata().getJournalEmail() != null
//					&& !i.getJournalMetadata().getJournalEmail().isEmpty()) {
//
//				MailQueue addMailQueue = new MailQueue();
//				addMailQueue.setMailFrom(springMailUsername);
//				addMailQueue.setMailTo(toMailID);
//				addMailQueue.setJournalId(journalId);
//				addMailQueue.setManuscriptId(0);
//				addMailQueue.setMailSubject(mailSubject);
//				addMailQueue.setMailBody(mailBody);
//				addMailQueue.setMail_send_date(new Date());
//				addMailQueue.setIsActive(0);
//				mailQueueRepo.saveAndFlush(addMailQueue);
//
//				add.setStatus("Insert into mailQueue");
//				add.setCreatedAt(new Date());
//				otherMailRepo.save(add);
//
////				String a = mailUtils.sendEmail(fromMailID, toMailID, mailSubject, mailBody,
////						i.getJournalMetadata().getJournalEmail(), i.getJournalMetadata().getJournalEmailPassword(),
////						i.getJournalMetadata().getJournalAlias());
////				if (a != null && !a.isEmpty() && a.equalsIgnoreCase("success")) {
////					add.setStatus("success");
////					add.setCreatedAt(new Date());
////					otherMailRepo.save(add);
////				} else {
////					add.setStatus("fail");
////					add.setCreatedAt(new Date());
////					otherMailRepo.save(add);
////				}
//			} else {
//
//				MailQueue addMailQueue = new MailQueue();
//				addMailQueue.setMailFrom(springMailUsername);
//				addMailQueue.setMailTo(toMailID);
//				addMailQueue.setJournalId(journalId);
//				addMailQueue.setManuscriptId(0);
//				addMailQueue.setMailSubject(mailSubject);
//				addMailQueue.setMailBody(mailBody);
//				addMailQueue.setMail_send_date(new Date());
//				addMailQueue.setIsActive(0);
//				mailQueueRepo.saveAndFlush(addMailQueue);
//
//				add.setStatus("Insert into mailQueue");
//				add.setCreatedAt(new Date());
//				otherMailRepo.save(add);
//
////				String a = mailUtils.sendEmail(fromMailID, toMailID, mailSubject, mailBody, "", "", "");
////				if (a != null && !a.isEmpty() && a.equalsIgnoreCase("success")) {
////					add.setStatus("success");
////					add.setCreatedAt(new Date());
////					otherMailRepo.save(add);
////				} else {
////					add.setStatus("fail");
////					add.setCreatedAt(new Date());
////					otherMailRepo.save(add);
////				}
//			}
//		} else {
//
//			MailQueue addMailQueue = new MailQueue();
//			addMailQueue.setMailFrom(springMailUsername);
//			addMailQueue.setMailTo(toMailID);
//			addMailQueue.setJournalId(journalId);
//			addMailQueue.setManuscriptId(0);
//			addMailQueue.setMailSubject(mailSubject);
//			addMailQueue.setMailBody(mailBody);
//			addMailQueue.setMail_send_date(new Date());
//			addMailQueue.setIsActive(0);
//			mailQueueRepo.saveAndFlush(addMailQueue);
//
//			add.setStatus("Insert into mailQueue");
//			add.setCreatedAt(new Date());
//			otherMailRepo.save(add);
//
////			String a = mailUtils.sendEmail(fromMailID, toMailID, mailSubject, mailBody, "", "", "");
////			if (a != null && !a.isEmpty() && a.equalsIgnoreCase("success")) {
////				add.setStatus("success");
////				add.setCreatedAt(new Date());
////				otherMailRepo.save(add);
////			} else {
////				add.setStatus("fail");
////				add.setCreatedAt(new Date());
////				otherMailRepo.save(add);
////			}
//		}
//		String message = "Mail send successfully.";
//		return message;
//	}

	private String activateUserMailTemplate(String email, String salutation, String firstName, String lastName,
			String manuscriptTitle, String journalTitle, String copyRightUrl, Integer coAuthorPrimaryId,
			Integer journalId, String journalCode, List<String> journalWebSiteUrl) {
		StringBuilder builder = new StringBuilder();
		builder.append("<p> Dear " + salutation + "." + firstName + " " + lastName + ",</p>");
		builder.append("<br>");
		builder.append("<p> You have been added as a co-author for article entitled " + manuscriptTitle
				+ " submitted to" + " " + journalTitle + " .</p>");
		builder.append("<br>");
		String copyRightFormWholeUrl = copyRightUrl + "?journal=" + journalId + "&author=" + coAuthorPrimaryId;
		builder.append(
				"<p> Please review the Copyright Form and agree on Terms & Conditions for the article by clicking on the below link:</p>");
		builder.append("<br>");
		builder.append(
				"<p> (<a href=" + copyRightFormWholeUrl + "  target=\"blank\">" + copyRightFormWholeUrl + "</a>)</p>");
		builder.append("<br>");
		// aian cc requirements
		if (journalId != null && (journalId.equals(1819) || journalId.equals(259))) {
			builder.append(
					"<p>Please note that the manuscript is visible only under the account of submitting author. Please co-ordinate with the submitting author for the article status.</p>");
			builder.append("<br>");
			builder.append(
					"<p>If you have been marked as a corresponding author on the system, you will receive a copy of the email notifications and reminders which will be sent out to the submitting author on new submission, requiring technical modification, requiring revision, on acceptance/rejection and author proofs if article is accepted.</p>");
		} else {
			builder.append(
					"<p>Please note that the manuscript is visible only under the account of submitting author. Please co-ordinate with the submitting author for the article status. You may use your author account to submit new manuscript after the activation.</p>");
		}

		builder.append("<br>");
		if (journalWebSiteUrl != null && !journalWebSiteUrl.isEmpty()) {
			int count = 1;
			builder.append("<p><b>You have been provided with the following Journal Url for login:</b></p>");
			builder.append("<br>");
			for (String i : journalWebSiteUrl) {
				builder.append(
						"<p><b>" + count + "</b>. " + "<a href=" + i + " target=\"blank\">" + i + "</a>" + "</p>");
				count++;
			}
		}
		builder.append("<br>");
		builder.append("<p> THANK YOU.</p>");
		builder.append("<br>");
		builder.append("<p></p>");
		builder.append("<p>Best Regards</p>");
		builder.append("<p>Editor in chief</p>");
		builder.append("<p>" + journalTitle + "</p>");
		return builder.toString();
	}

	public String sendMailForActivationForCoAuthor(String userEmail, String token, String activationUrl,
			String salutation, String firstName, String lastName, String manuscriptTitle, String journalTitle,
			String copyRightUrl, Integer coAuthorPrimaryId, Integer journalId, String journalCode,
			List<String> journalWebSiteUrl) {
		userRepo.updateTokenByUser(userEmail, token);
		String toMailID = userEmail;
		String mailSubject = "Copyright agreement & co-author account activation";
		String mailBody = activateUserMailTemplateForCoAuthor(userEmail, token, activationUrl, salutation, firstName,
				lastName, manuscriptTitle, journalTitle, copyRightUrl, coAuthorPrimaryId, journalId, journalCode,
				journalWebSiteUrl);
		OtherMail add = new OtherMail();
		add.setJournalId(journalId);
		add.setToMail(toMailID);
		add.setFromMail(springMailUsername);
		add.setSubject(mailSubject);
		add.setMailBody(mailBody);
		Journal i = journalsRepo.getOne(journalId);
		if (i != null && i.getJournalMetadata() != null) {
			if (i.getJournalMetadata().getJournalEmail() != null
					&& !i.getJournalMetadata().getJournalEmail().isEmpty()) {

				MailQueue addMailQueue = new MailQueue();
				addMailQueue.setMailFrom(springMailUsername);
				addMailQueue.setMailTo(toMailID);
				addMailQueue.setJournalId(journalId);
				addMailQueue.setManuscriptId(0);
				addMailQueue.setMailSubject(mailSubject);
				addMailQueue.setMailBody(mailBody);
				addMailQueue.setMail_send_date(new Date());
				addMailQueue.setIsActive(0);
				mailQueueRepo.saveAndFlush(addMailQueue);

				add.setStatus("Insert into mailQueue");
				add.setCreatedAt(new Date());
				otherMailRepo.save(add);

//				String a = mailUtils.sendEmail(fromMailID, toMailID, mailSubject, mailBody,
//						i.getJournalMetadata().getJournalEmail(), i.getJournalMetadata().getJournalEmailPassword(),
//						i.getJournalMetadata().getJournalAlias());
//				if (a != null && !a.isEmpty() && a.equalsIgnoreCase("success")) {
//					add.setStatus("success");
//					add.setCreatedAt(new Date());
//					otherMailRepo.save(add);
//				} else {
//					add.setStatus("fail");
//					add.setCreatedAt(new Date());
//					otherMailRepo.save(add);
//				}
			} else {

				MailQueue addMailQueue = new MailQueue();
				addMailQueue.setMailFrom(springMailUsername);
				addMailQueue.setMailTo(toMailID);
				addMailQueue.setJournalId(journalId);
				addMailQueue.setManuscriptId(0);
				addMailQueue.setMailSubject(mailSubject);
				addMailQueue.setMailBody(mailBody);
				addMailQueue.setMail_send_date(new Date());
				addMailQueue.setIsActive(0);
				mailQueueRepo.saveAndFlush(addMailQueue);

				add.setStatus("Insert into mailQueue");
				add.setCreatedAt(new Date());
				otherMailRepo.save(add);

//				String a = mailUtils.sendEmail(fromMailID, toMailID, mailSubject, mailBody, "", "", "");
//				if (a != null && !a.isEmpty() && a.equalsIgnoreCase("success")) {
//					add.setStatus("success");
//					add.setCreatedAt(new Date());
//					otherMailRepo.save(add);
//				} else {
//					add.setStatus("fail");
//					add.setCreatedAt(new Date());
//					otherMailRepo.save(add);
//				}
			}
		} else {

			MailQueue addMailQueue = new MailQueue();
			addMailQueue.setMailFrom(springMailUsername);
			addMailQueue.setMailTo(toMailID);
			addMailQueue.setJournalId(journalId);
			addMailQueue.setManuscriptId(0);
			addMailQueue.setMailSubject(mailSubject);
			addMailQueue.setMailBody(mailBody);
			addMailQueue.setMail_send_date(new Date());
			addMailQueue.setIsActive(0);
			mailQueueRepo.saveAndFlush(addMailQueue);

			add.setStatus("Insert into mailQueue");
			add.setCreatedAt(new Date());
			otherMailRepo.save(add);

//			String a = mailUtils.sendEmail(fromMailID, toMailID, mailSubject, mailBody, "", "", "");
//			if (a != null && !a.isEmpty() && a.equalsIgnoreCase("success")) {
//				add.setStatus("success");
//				add.setCreatedAt(new Date());
//				otherMailRepo.save(add);
//			} else {
//				add.setStatus("fail");
//				add.setCreatedAt(new Date());
//				otherMailRepo.save(add);
//			}
		}

		String message = "Password reset URL send on mail successfully.";
		return message;
	}

	private String activateUserMailTemplateForCoAuthor(String email, String token, String activationUrl,
			String salutation, String firstName, String lastName, String manuscriptTitle, String journalTitle,
			String copyRightUrl, Integer coAuthorPrimaryId, Integer journalId, String journalCode,
			List<String> journalWebSiteUrl) {
		StringBuilder builder = new StringBuilder();
		String resetUrl;
		if (activationUrl == null || activationUrl.isEmpty() || activationUrl.equals("null/outside/user-verification")
				|| activationUrl.contains("null")) {
			resetUrl = "https://review.jow.medknow.com/outside/user-verification";
		} else {
			resetUrl = activationUrl;
		}
		String resetPasswordURL = resetUrl + "?token=" + token + "&email=" + email + "&journalId=" + journalId
				+ "&journalCode=" + journalCode;
		builder.append("<p> Dear " + salutation + "." + firstName + " " + lastName + ",</p>");
		builder.append("<br>");
		builder.append("<p> You have been added as a co-author for article entitled " + manuscriptTitle
				+ " submitted to" + " " + journalTitle + " .</p>");
		String copyRightFormWholeUrl = copyRightUrl + "?journal=" + journalId + "&author=" + coAuthorPrimaryId;
		builder.append("<br>");
		builder.append(
				"<p>Please review the Copyright Form and agree on Terms & Conditions for the article by clicking on the below link:</p>");
		builder.append("<br>");
		builder.append(
				"<p>  (<a href=" + copyRightFormWholeUrl + "  target=\"blank\">" + copyRightFormWholeUrl + "</a>)</p>");
		builder.append("<br>");
		builder.append(
				"<p> To activate your account as co-author, click on the link given below or copy and paste the entire link to browser address bar:</p>");
		builder.append("<br>");
		builder.append("<p>  (<a href=" + resetPasswordURL + "  target=\"blank\">" + resetPasswordURL + "</a>)</p>");
		builder.append("<br>");
		builder.append("<p> Your Username is :" + email + "." + "</p>");
		builder.append("<br>");

		// aian cc requirements
		if (journalId != null && (journalId == 259 || journalId == 1819)) {
			builder.append(
					"<p> This will confirm your registration into our system.You may use your author account to submit new manuscript after the activation.</p>");
		} else {
			builder.append("<p> This will confirm your registration into our system.</p>");
		}

		builder.append("<br>");

		// aian cc requirements
		if (journalId != null && (journalId.equals(1819) || journalId.equals(259))) {
			builder.append(
					"<p>Please note that the manuscript is visible only under the account of submitting author. Please co-ordinate with the submitting author for the article status.</p>");
			builder.append("<br>");
			builder.append(
					"<p>If you have been marked as a corresponding author on the system, you will receive a copy of the email notifications and reminders which will be sent out to the submitting author on new submission, requiring technical modification, requiring revision, on acceptance/rejection and author proofs if article is accepted.</p>");
		} else {
			builder.append(
					"<p>Please note that the manuscript is visible only under the account of submitting author. Please co-ordinate with the submitting author for the article status. You may use your author account to submit new manuscript after the activation.</p>");
		}
		builder.append("<br>");
		if (journalWebSiteUrl != null && !journalWebSiteUrl.isEmpty()) {
			int count = 1;
			builder.append("<p><b>You have been provided with the following Journal Url for login:</b></p>");
			builder.append("<br>");
			for (String i : journalWebSiteUrl) {
				builder.append(
						"<p><b>" + count + "</b>. " + "<a href=" + i + " target=\"blank\">" + i + "</a>" + "</p>");
				count++;
			}
		}
		builder.append("<br>");
		builder.append("<p> Thank you.</p>");
		builder.append("<br>");
		builder.append("<p></p>");
		builder.append("<p>Best Regards</p>");
		builder.append("<p>Editor in chief</p>");
		builder.append("<p>" + journalTitle + "</p>");
		return builder.toString();
	}

	@Override
	public ManuScriptVO getManuscriptData(IDVO id) {
		ManuScript getManuScript = manuScriptRepo.getOne(id.getManuscriptId());
		if (getManuScript != null) {
			ManuScriptVO set = new ManuScriptVO();
			set.setManuscriptAbstract(getManuScript.getManuscriptAbstract());
			return set;
		}
		return null;
	}

	@Override
	public ManuScriptVO getManuscriptDataForReviewer(IDVO id) {
		ManuScript getManuScript = manuScriptRepo.getById(id.getManuscriptId());
		if (getManuScript != null) {
			ManuScriptVO set = new ManuScriptVO();
			set.setAssignedUser(getManuScript.getAssignedUser());
			set.setUserRole(getManuScript.getAssignedUserRole());
			return set;
		}
		return null;
	}

	public String withdrawnAllReviewerInvitation(ManuscriptEditorVO manuscriptEditorVO) {
		try {
			List<ManuScriptReviewer> reviewers = manuScriptReviewerRepo.getmanuScriptReviewerToMailAndRemove(
					manuscriptEditorVO.getManuscriptId(), manuscriptEditorVO.getManuscriptVersion());

			if (reviewers != null && !reviewers.isEmpty()) {
				reviewers.forEach(reviewer -> {
					try {
						EmailTriggerVO emailTriggerVO = createEmailTriggerForReviewerWithdrawal(manuscriptEditorVO,
								reviewer);

						String message = emailTriggerService.emailTrigger(emailTriggerVO);
						logger.info("Reviewer Withdrawal Email Sent: " + message);
					} catch (Exception e) {
						logger.error("Error sending reviewer withdrawal email: " + e.getMessage(), e);
					}
				});

				return "All withdrawal of reviewers done";
			} else {
				logger.warn("No data to withdraw");
				return "No data to withdraw";
			}
		} catch (Exception ex) {
			logger.error("An error occurred while processing reviewer withdrawal: " + ex.getMessage(), ex);
			return "Error occurred during reviewer withdrawal";
		}
	}

	private EmailTriggerVO createEmailTriggerForReviewerWithdrawal(ManuscriptEditorVO manuscriptEditorVO,
			ManuScriptReviewer reviewer) {
		EmailTriggerVO emailTriggerVO = new EmailTriggerVO();
		emailTriggerVO.setJournalId(reviewer.getJournalId());
		emailTriggerVO.setUserId(reviewer.getReviewerId());
		if (manuscriptEditorVO.getRoleId() != null) {
			if (manuscriptEditorVO.getRoleId().equals(2)) {
				emailTriggerVO.setManagingEditorId(manuscriptEditorVO.getUserId());
			} else {
				emailTriggerVO.setEditorUserId(manuscriptEditorVO.getUserId());
			}
		}
		emailTriggerVO.setManuscriptId(manuscriptEditorVO.getManuscriptId());
		emailTriggerVO.setManuscriptStatus(reviewer.getManuscriptStatus());
		emailTriggerVO.setReceiverRoleId(5);
		emailTriggerVO.setTaskId(22);
		emailTriggerVO.setRoleId(manuscriptEditorVO.getRoleId());
		return emailTriggerVO;
	}

	@Override
	public List<ManuscriptListVO> getManuscriptByType(IDVO id) {

		List<ManuscriptListVO> newList = new ArrayList<>();

		List<ManuScript> list = manuScriptRepo.getManuscriptBytype(id.getName(), id.getJournalId(), id.getManuId());
		if (list != null) {
			for (ManuScript data : list) {
				User editorName = new User();
				if (data.getAssignedUser() != null) {
					editorName = userRepo.getOne(data.getAssignedUser());
				}

				ManuscriptListVO manuscriptListVO = new ManuscriptListVO();
				manuscriptListVO.setJournalId(data.getJournalId());
				manuscriptListVO.setManuscripCode(data.getManuscriptCode());
				manuscriptListVO.setManuscriptAuthorId(data.getAuthorId());
				manuscriptListVO.setManuscripTitle(data.getManuscriptTitle());
				manuscriptListVO.setManuScriptId(data.getManuScriptId());
				manuscriptListVO.setManuscriptVersion(data.getManuscriptVersion());
				manuscriptListVO.setSubmissionDate(data.getSubmissionDate());
				manuscriptListVO.setSubmittedBy(data.getSubmittedBy());
				manuscriptListVO.setCreatedBy(data.getCreatedBy());
				manuscriptListVO.setCreatedOn(data.getCreatedOn());
				manuscriptListVO.setUpdatedBy(data.getUpdatedBy());
				manuscriptListVO.setUpdatedOn(data.getUpdatedOn());
				manuscriptListVO.setManuStatusCode(data.getStatusMaster().getStatusValue());
				manuscriptListVO.setAuthorName(data.getUser().getFirstName() + " " + data.getUser().getLastName());
				manuscriptListVO.setJournalTitle(data.getJournal().getJournalTitle());
				manuscriptListVO.setJournalCode(data.getJournal().getJournalCode());
				manuscriptListVO.setManuStatusId(id.getManuId());
				if (editorName != null) {
					manuscriptListVO.setEditorName(editorName.getFirstName() + " " + editorName.getLastName());
				}
				if (data.getManuscriptTypeDetails() != null) {
					manuscriptListVO.setManuScriptType(data.getManuscriptTypeDetails().getManuscriptType());
				}
				newList.add(manuscriptListVO);
			}
		}

		return newList;

	}

	@Override
	public List<ManuscriptListVO> getAssignedEditorbyType(IDVO id) {
		List<ManuScript> list = manuScriptRepo.getManuscriptAssignedEditorbyType(id.getName(), id.getJournalId());
		List<Integer> manuscriptId = new ArrayList();
		List<ManuscriptEditor> editorData = new ArrayList();
		List<ManuscriptListVO> newList = new ArrayList<>();
		if (list != null && !list.isEmpty()) {
			for (ManuScript manuScript : list) {
				manuscriptId.add(manuScript.getManuScriptId());
			}
		}
		if (manuscriptId != null && !manuscriptId.isEmpty()) {
			editorData = manuscriptEditorRepo.getAssignedEditorListByManuscriptListByStatus(manuscriptId);
		}

		if (list != null && !list.isEmpty() && editorData != null && !editorData.isEmpty()) {
			for (ManuscriptEditor manuscriptEditor : editorData) {
				for (ManuScript data : list) {
					if (manuscriptEditor.getManuscriptId().equals(data.getManuScriptId())) {

						List<ManuscriptTypeDetails> manuScriptType = manuscriptTypeDetailsRepo
								.getManuscriptTypeDetailsById(data.getManuScriptId());
						User user = userRepo.getOne(data.getAuthorId());
						User editorName = new User();
						if (data.getAssignedUser() != null) {
							editorName = userRepo.getOne(data.getAssignedUser());
						}
						ManuscriptListVO manuscriptListVO = new ManuscriptListVO();
						manuscriptListVO.setJournalId(data.getJournalId());
						manuscriptListVO.setManuscripCode(data.getManuscriptCode());
						manuscriptListVO.setManuscriptAuthorId(data.getAuthorId());
						manuscriptListVO.setManuscripTitle(data.getManuscriptTitle());
						manuscriptListVO.setManuScriptId(data.getManuScriptId());
						manuscriptListVO.setManuscriptVersion(data.getManuscriptVersion());
						manuscriptListVO.setSubmissionDate(data.getSubmissionDate());
						manuscriptListVO.setSubmittedBy(data.getSubmittedBy());
						manuscriptListVO.setCreatedBy(data.getCreatedBy());
						manuscriptListVO.setCreatedOn(data.getCreatedOn());
						manuscriptListVO.setUpdatedBy(data.getUpdatedBy());
						manuscriptListVO.setUpdatedOn(data.getUpdatedOn());
						// manuscriptListVO.setManuStatusCode(statusCode.getStatusValue());
						manuscriptListVO.setAuthorName(user.getFirstName() + " " + user.getLastName());
						manuscriptListVO.setJournalTitle(data.getJournal().getJournalTitle());
						manuscriptListVO.setJournalCode(data.getJournal().getJournalCode());
						manuscriptListVO.setManuStatusId(id.getManuId());
						if (editorName != null) {
							manuscriptListVO.setEditorName(editorName.getFirstName() + " " + editorName.getLastName());
						}
						if (manuScriptType != null && !manuScriptType.isEmpty()) {
							manuscriptListVO.setManuScriptType(manuScriptType.get(0).getManuscriptType());
						}

						List<ManuscriptManagingEditor> manuscriptManagingEditor = manuscriptManagingEditorRepo
								.getByJournalAndManuscriptId(data.getManuScriptId(), data.getJournalId());
						if (manuscriptManagingEditor != null && !manuscriptManagingEditor.isEmpty()) {
							List<ManuscriptManagingEditorVO> mmeVoList = new ArrayList<>();
							for (ManuscriptManagingEditor mme : manuscriptManagingEditor) {
								User managingEditor = userRepo.getOne(data.getAuthorId());
								ManuscriptManagingEditorVO mmeVO = new ManuscriptManagingEditorVO();
								mmeVO.setUserId(mme.getUserId());
								mmeVO.setName(managingEditor.getFirstName() + " " + managingEditor.getLastName());
								mmeVoList.add(mmeVO);

							}
							manuscriptListVO.setManagingEditor(mmeVoList);
						}

						List<ManuscriptEditor> me = manuscriptEditorRepo
								.getByJournalAndManuscript(data.getManuScriptId(), data.getJournalId());
						if (me != null && !me.isEmpty()) {
							List<ManagingEditorVO> meVOList = new ArrayList<>();
							for (ManuscriptEditor meData : me) {
								User editor = userRepo.getOne(data.getAuthorId());
								ManagingEditorVO meVO = new ManagingEditorVO();
								meVO.setEditorId(meData.getEditorId());
								meVO.setName(editor.getFirstName() + " " + editor.getLastName());
								meVOList.add(meVO);
							}
							manuscriptListVO.setEditorList(meVOList);
						}
						newList.add(manuscriptListVO);
					}
				}
			}
		}
		return newList;
	}

	@Override
	public List<ManuscriptListVO> getReviewedByType(IDVO id) {

		List<ManuScript> list = manuScriptRepo.getManuscriptByManuscriptType(id.getName(), id.getJournalId());
		List<ManuScript> manuscriptId = new ArrayList();
		List<ManuscriptListVO> reviewedManuscript = new ArrayList();
		if (list != null && !list.isEmpty()) {
			for (ManuScript manuScript : list) {
				if (manuScript.getManuscriptStatusId() != null
						&& ((manuScript.getManuscriptStatusId() == 8) || (manuScript.getManuscriptStatusId() == 66)))
					manuscriptId.add(manuScript);
			}
		}
		if (manuscriptId != null && !manuscriptId.isEmpty()) {
			reviewedManuscript = manuScriptReviewerService.getReviewedManuscriptByManuscriptId(manuscriptId);
		}

		return reviewedManuscript;

	}

	@Override
	public List<ManuscriptListVO> getNoResponseByType(IDVO id) {

		List<ManuScript> list = manuScriptRepo.getManuscriptByManuscriptType(id.getName(), id.getJournalId());
		List<ManuScript> manuscriptId = new ArrayList();
		List<ManuscriptListVO> noResponse = new ArrayList();
		if (list != null && !list.isEmpty()) {
			for (ManuScript manuScript : list) {
				if (manuScript.getManuscriptStatusId() != null
						&& ((manuScript.getManuscriptStatusId() == 8) || (manuScript.getManuscriptStatusId() == 66)))
					manuscriptId.add(manuScript);
			}
		}
		if (manuscriptId != null && !manuscriptId.isEmpty()) {
			noResponse = manuScriptReviewerService.getNoResponseManuscriptByManuscriptId(manuscriptId);
		}

		return noResponse;

	}

	@Override
	public List<ManuscriptListVO> getInReviewByType(IDVO id) {

		List<ManuScript> list = manuScriptRepo.getManuscriptByManuscriptType(id.getName(), id.getJournalId());
		List<ManuScript> manuscriptId = new ArrayList();
		List<ManuscriptListVO> inReview = new ArrayList();
		if (list != null && !list.isEmpty()) {
			for (ManuScript manuScript : list) {
				if (manuScript.getManuscriptStatusId() != null
						&& ((manuScript.getManuscriptStatusId() == 8) || (manuScript.getManuscriptStatusId() == 66)))
					manuscriptId.add(manuScript);
			}
		}
		if (manuscriptId != null && !manuscriptId.isEmpty()) {
			inReview = manuScriptReviewerService.getInReviewManuscriptByManuscriptId(manuscriptId);
		}

		return inReview;

	}

//	@Override
//	public List<ManuscriptListVO> getFinalDecisionByType(IDVO id) {
////		List<ManuScript> list = manuScriptRepo.getManuscriptByManuscriptType(id.getName(), id.getJournalId());
//		List<ManuScript> list = manuScriptRepo.getManuscriptByFinalDecisionType(id.getName(), id.getJournalId());
//		List<Integer> manuscriptId = new ArrayList();
//		List<ManuscriptEditor> editorData = new ArrayList();
//		List<ManuscriptListVO> newList = new ArrayList<>();
//		if (list != null && !list.isEmpty()) {
//			for (ManuScript manuScript : list) {
//				manuscriptId.add(manuScript.getManuScriptId());
//			}
//		}
//		if (manuscriptId != null && !manuscriptId.isEmpty()) {
//			editorData = manuscriptEditorRepo.getFinalDecisionByType(manuscriptId);
//		}
//
//		if (list != null && !list.isEmpty() && editorData != null && !editorData.isEmpty()) {
//			for (ManuscriptEditor manuscriptEditor : editorData) {
//				for (ManuScript data : list) {
//					if (manuscriptEditor.getManuscriptId().equals(data.getManuScriptId())) {
//
//						List<ManuscriptTypeDetails> manuScriptType = manuscriptTypeDetailsRepo
//								.getManuscriptTypeDetailsById(data.getManuScriptId());
//						User user = userRepo.getOne(data.getAuthorId());
//						User editorName = new User();
//						if (data.getAssignedUser() != null) {
//							editorName = userRepo.getOne(data.getAssignedUser());
//						}
//						ManuscriptListVO manuscriptListVO = new ManuscriptListVO();
//						manuscriptListVO.setJournalId(data.getJournalId());
//						manuscriptListVO.setManuscripCode(data.getManuscriptCode());
//						manuscriptListVO.setManuscriptAuthorId(data.getAuthorId());
//						manuscriptListVO.setManuscripTitle(data.getManuscriptTitle());
//						manuscriptListVO.setManuScriptId(data.getManuScriptId());
//						manuscriptListVO.setManuscriptVersion(data.getManuscriptVersion());
//						manuscriptListVO.setSubmissionDate(data.getSubmissionDate());
//						manuscriptListVO.setSubmittedBy(data.getSubmittedBy());
//						manuscriptListVO.setCreatedBy(data.getCreatedBy());
//						manuscriptListVO.setCreatedOn(data.getCreatedOn());
//						manuscriptListVO.setUpdatedBy(data.getUpdatedBy());
//						manuscriptListVO.setUpdatedOn(data.getUpdatedOn());
//						// manuscriptListVO.setManuStatusCode(statusCode.getStatusValue());
//						manuscriptListVO.setAuthorName(user.getFirstName() + " " + user.getLastName());
//						manuscriptListVO.setJournalTitle(data.getJournal().getJournalTitle());
//						manuscriptListVO.setJournalCode(data.getJournal().getJournalCode());
//						manuscriptListVO.setManuStatusId(id.getManuId());
//						if (editorName != null) {
//							manuscriptListVO.setEditorName(editorName.getFirstName() + " " + editorName.getLastName());
//						}
//						if (manuScriptType != null && !manuScriptType.isEmpty()) {
//							manuscriptListVO.setManuScriptType(manuScriptType.get(0).getManuscriptType());
//						}
//
//						List<ManuscriptManagingEditor> manuscriptManagingEditor = manuscriptManagingEditorRepo
//								.getByJournalAndManuscriptId(data.getManuScriptId(), data.getJournalId());
//						if (manuscriptManagingEditor != null && !manuscriptManagingEditor.isEmpty()) {
//							List<ManuscriptManagingEditorVO> mmeVoList = new ArrayList<>();
//							for (ManuscriptManagingEditor mme : manuscriptManagingEditor) {
//								User managingEditor = userRepo.getOne(data.getAuthorId());
//								ManuscriptManagingEditorVO mmeVO = new ManuscriptManagingEditorVO();
//								mmeVO.setUserId(mme.getUserId());
//								mmeVO.setName(managingEditor.getFirstName() + " " + managingEditor.getLastName());
//								mmeVoList.add(mmeVO);
//
//							}
//							manuscriptListVO.setManagingEditor(mmeVoList);
//						}
//
//						List<ManuscriptEditor> me = manuscriptEditorRepo
//								.getByJournalAndManuscript(data.getManuScriptId(), data.getJournalId());
//						if (me != null && !me.isEmpty()) {
//							List<ManagingEditorVO> meVOList = new ArrayList<>();
//							for (ManuscriptEditor meData : me) {
//								User editor = userRepo.getOne(data.getAuthorId());
//								ManagingEditorVO meVO = new ManagingEditorVO();
//								meVO.setEditorId(meData.getEditorId());
//								meVO.setName(editor.getFirstName() + " " + editor.getLastName());
//								meVOList.add(meVO);
//							}
//							manuscriptListVO.setEditorList(meVOList);
//						}
//						newList.add(manuscriptListVO);
//					}
//				}
//			}
//		}
//		return newList;
//	}

	@Override
	public List<ManuscriptListVO> getFinalDecisionByType(IDVO id) {
		List<ManuScript> list = manuScriptRepo.getManuscriptByFinalDecisionType(id.getName(), id.getJournalId());
		List<Integer> manuscriptId = new ArrayList();
		List<ManuscriptListVO> newList = new ArrayList<>();
		if (list != null && !list.isEmpty()) {
			for (ManuScript data : list) {
				List<ManuscriptTypeDetails> manuScriptType = manuscriptTypeDetailsRepo
						.getManuscriptTypeDetailsById(data.getManuScriptId());
				User editorName = new User();
				if (data.getAssignedUser() != null) {
					editorName = userRepo.getOne(data.getAssignedUser());
				}
				ManuscriptListVO manuscriptListVO = new ManuscriptListVO();
				manuscriptListVO.setJournalId(data.getJournalId());
				manuscriptListVO.setManuscripCode(data.getManuscriptCode());
				manuscriptListVO.setManuscriptAuthorId(data.getAuthorId());
				manuscriptListVO.setManuscripTitle(data.getManuscriptTitle());
				manuscriptListVO.setManuScriptId(data.getManuScriptId());
				manuscriptListVO.setManuscriptVersion(data.getManuscriptVersion());
				manuscriptListVO.setSubmissionDate(data.getSubmissionDate());
				manuscriptListVO.setSubmittedBy(data.getSubmittedBy());
				manuscriptListVO.setCreatedBy(data.getCreatedBy());
				manuscriptListVO.setCreatedOn(data.getCreatedOn());
				manuscriptListVO.setUpdatedBy(data.getUpdatedBy());
				manuscriptListVO.setUpdatedOn(data.getUpdatedOn());
				// manuscriptListVO.setManuStatusCode(statusCode.getStatusValue());
				manuscriptListVO.setAuthorName(data.getUser().getFirstName() + " " + data.getUser().getLastName());
				manuscriptListVO.setJournalTitle(data.getJournal().getJournalTitle());
				manuscriptListVO.setJournalCode(data.getJournal().getJournalCode());
				manuscriptListVO.setManuStatusId(id.getManuId());
				if (editorName != null) {
					manuscriptListVO.setEditorName(editorName.getFirstName() + " " + editorName.getLastName());
				}
				if (manuScriptType != null && !manuScriptType.isEmpty()) {
					manuscriptListVO.setManuScriptType(manuScriptType.get(0).getManuscriptType());
				}

				List<ManuscriptManagingEditor> manuscriptManagingEditor = manuscriptManagingEditorRepo
						.getByJournalAndManuscriptId(data.getManuScriptId(), data.getJournalId());
				if (manuscriptManagingEditor != null && !manuscriptManagingEditor.isEmpty()) {
					List<ManuscriptManagingEditorVO> mmeVoList = new ArrayList<>();
					for (ManuscriptManagingEditor mme : manuscriptManagingEditor) {
						User managingEditor = userRepo.getOne(data.getAuthorId());
						ManuscriptManagingEditorVO mmeVO = new ManuscriptManagingEditorVO();
						mmeVO.setUserId(mme.getUserId());
						mmeVO.setName(managingEditor.getFirstName() + " " + managingEditor.getLastName());
						mmeVoList.add(mmeVO);

					}
					manuscriptListVO.setManagingEditor(mmeVoList);
				}

				List<ManuscriptEditor> me = manuscriptEditorRepo.getByJournalAndManuscript(data.getManuScriptId(),
						data.getJournalId());
				if (me != null && !me.isEmpty()) {
					List<ManagingEditorVO> meVOList = new ArrayList<>();
					for (ManuscriptEditor meData : me) {
						User editor = userRepo.getOne(data.getAuthorId());
						ManagingEditorVO meVO = new ManagingEditorVO();
						meVO.setEditorId(meData.getEditorId());
						meVO.setName(editor.getFirstName() + " " + editor.getLastName());
						meVOList.add(meVO);
					}
					manuscriptListVO.setEditorList(meVOList);
				}
				newList.add(manuscriptListVO);
			}
		}
		return newList;
	}

	@Override
	public String insertDataThroughUnitouch(ArticleInfoAPI articleInfoAPI) {
		logger.info("Inside insertDataThroughUnitouch serviceImp");
		String msg = "";
		if (articleInfoAPI != null && articleInfoAPI.getJournalAbbr() != "") {
			logger.info("journal Code not null ::" + articleInfoAPI.getJournalAbbr());
			Journal journal = journalsRepo.getJournalByCodeForUnitouch(articleInfoAPI.getJournalAbbr());
			if (journal != null) {
				logger.info("Journal Found journalId :::::" + journal.getJournalId());
				ManuScript manuscript = new ManuScript();
				ManuscriptTypeDetails manuscriptTypeDetails = new ManuscriptTypeDetails();
				SimpleDateFormat formatter = new SimpleDateFormat("dd-mm-yyyy");
				manuscript.setManuscriptVersion(0);
				manuscript.setJournalId(journal.getJournalId());
				manuscript.setManuscriptCode(articleInfoAPI.getArticle().getAid());
				manuscript.setManuscriptTitle(articleInfoAPI.getArticle().getArticle_title());
				manuscript.setDoi(articleInfoAPI.getArticle().getArticle_doi());
				try {
					if (articleInfoAPI.getArticle().getAccepted_date() != null
							&& articleInfoAPI.getArticle().getAccepted_date() != "") {
						manuscript.setProductionDate(formatter.parse(articleInfoAPI.getArticle().getAccepted_date()));
						manuscript.setSubmissionDate(formatter.parse(articleInfoAPI.getArticle().getSubmissionDate()));
					}
				} catch (ParseException e) {
					e.printStackTrace();
					logger.info("Exception   :::" + e);
				}
				manuscript.setCommentForProduction(articleInfoAPI.getArticle().getCommentoForProduction());
				manuscript.setManuscriptKeywords(articleInfoAPI.getArticle().getKeywords());
				manuscript.setManuscriptStatusId(33);
				manuscript.setIsActive(1);
				ManuScript manuscriptSavedData = manuScriptRepo.save(manuscript);

				logger.info("Manuscript Data Saved ::::" + manuscriptSavedData.getManuScriptId());

				manuscriptTypeDetails.setManuscriptId(manuscriptSavedData.getManuScriptId());
				manuscriptTypeDetails.setManuscriptVersion(0);
				manuscriptTypeDetails.setManuscriptType(articleInfoAPI.getArticle().getArticle_type_cd());
				if (articleInfoAPI.getArticle().getArticle_pages() != null) {
					manuscriptTypeDetails.setManuscriptPages(articleInfoAPI.getArticle().getArticle_pages().toString());
				}

				try {
					logger.info("Inside try block   :::   " + manuscriptTypeDetails.toString());
					manuscriptTypeDetailsRepo.save(manuscriptTypeDetails);
				} catch (Exception e) {
					logger.info("Inside Exception save manuscript type detail :::   " + e);
				}

				logger.info("manuscriptTypeDetails data saved :::");

				if (articleInfoAPI.getAuthors() != null && !articleInfoAPI.getAuthors().isEmpty()) {
					logger.info("Author data found :::");
					for (AuthorArticleDetails authorDetails : articleInfoAPI.getAuthors()) {
						logger.info("Author emailId ::::" + authorDetails.geteMail());
						if (authorDetails.getIs_corresponding() != null
								&& authorDetails.getIs_corresponding().equalsIgnoreCase("Y")
								|| authorDetails.getIs_corresponding().equalsIgnoreCase("yes")) {
							ManuScript manuscriptTempData = manuScriptRepo
									.getById(manuscriptSavedData.getManuScriptId());
							List<User> user = userRepo.getByEmail(authorDetails.geteMail());
							if (user != null && !user.isEmpty()) {
								manuscriptTempData.setAuthorId(user.get(0).getId());
							} else {
								logger.info("No user found");
								User saveUserData = new User();
								saveUserData.setSalutation(Enum.valueOf(Salutation.class,
										authorDetails.getTitle().replace(".", "").trim()));
								saveUserData.setFirstName(authorDetails.getfName());
								saveUserData.setLastName(authorDetails.getlName());
								saveUserData.setEmail(authorDetails.geteMail());
								saveUserData.setIsActive(1);
								saveUserData.setAccountLocked(1);
								User savedUser = userRepo.save(saveUserData);
								manuscriptTempData.setAuthorId(savedUser.getId());
							}

							msg = "data saved";

							logger.info("CoAuthor detail saved :::");
						}
					}
				}
			} else {
				msg = "No journal found";
				logger.info("No journal found");
			}
		} else {
			msg = "journal code not found";
			logger.info("journal code not found");
		}
		logger.info("msg return ::: " + msg);
		return msg;
	}

	@Override
	public ManuscriptDetailForJleVO getManuscriptDetailForJle(IDVO id) {
		ManuscriptDetailForJleVO manuscriptDetailForJle = new ManuscriptDetailForJleVO();
		ManuScript manuscript = manuScriptRepo.getById(id.getId());
		if (manuscript != null && manuscript.getManuScriptId() != null) {
			manuscriptDetailForJle.setManuscriptID(manuscript.getManuscriptCode());
			manuscriptDetailForJle.setJID(manuscript.getJournal().getJournalCode());
			manuscriptDetailForJle
					.setAuthorName(manuscript.getUser().getFirstName() + " " + manuscript.getUser().getLastName());
			if (manuscript.getManuscriptTypeDetails() != null
					&& manuscript.getManuscriptTypeDetails().getManuscriptPages() != null)
				manuscriptDetailForJle.setNoOfPages(manuscript.getManuscriptTypeDetails().getManuscriptPages());
		}
		return manuscriptDetailForJle;
	}

	/**
	 * Not in use
	 */
	@Override
	@Deprecated
	public Boolean pushDataForJle(ManuscriptDetailForJleVO manuscriptDetailForJle) {
		boolean flag = false;
		String filePath = "";
		List<ManuScriptFile> manuscriptFile = manuScriptFileRepo.getMainDocumentOfManuscriptFileWithMnuscriptVerison(
				manuscriptDetailForJle.getManuscript(), manuscriptDetailForJle.getMansucriptVersion());
		if (manuscriptFile != null && !manuscriptFile.isEmpty()) {
			for (ManuScriptFile manuScriptFile2 : manuscriptFile) {
				filePath = manuScriptFile2.getFilePath();
				String fileName = manuScriptFile2.getFileName();
				filePath = filePath.replace(fileName, "");
				filePath = filePath.concat(manuscriptDetailForJle.getManuscriptID() + "_UNIPRR.zip");

			}
			try {
				URL url = new URL(jleIntegratioUrl);
				HttpURLConnection con = (HttpURLConnection) url.openConnection();
				con.setRequestMethod("POST");
				con.setRequestProperty("Content-Type", "application/json; utf-8");
				con.setRequestProperty("Accept", "application/json");
				con.setDoOutput(true);
				String jsonInputString = "{\"ManuscriptID\":" + "\"" + manuscriptDetailForJle.getManuscriptID() + "\""
						+ "," + "\"JID\":" + "\"" + manuscriptDetailForJle.getJID() + "\"" + "," + "\"AID\":" + "\""
						+ manuscriptDetailForJle.getAID() + "\"" + "," + "\"AuthorName\":" + "\""
						+ manuscriptDetailForJle.getAuthorName() + "\"" + "," + "\"DocHead\":" + "\""
						+ manuscriptDetailForJle.getDocHead() + "\"" + "," + "\"Volume\":" + "\""
						+ manuscriptDetailForJle.getVolume() + "\"" + "," + "\"Issue\":" + "\""
						+ manuscriptDetailForJle.getIssue() + "\"" + "," + "\"NoOfPages\":" + "\""
						+ manuscriptDetailForJle.getNoOfPages() + "\"" + "," + "\"NoOfFigures\":" + "\""
						+ manuscriptDetailForJle.getNoOfFigures() + "\"" + "," + "\"TAT\":" + "\""
						+ manuscriptDetailForJle.getTAT() + "\"" + "," + "\"Copyediting\":" + "\""
						+ manuscriptDetailForJle.getCopyediting() + "\"" + "," + "\"PublishingYear\":" + "\""
						+ manuscriptDetailForJle.getPublishingYear() + "\"" + "," + "\"Remark\":" + "\""
						+ manuscriptDetailForJle.getRemark() + "\"" + "," + "\"FilePath\":" + "\"" + filePath + "\""
						+ "," + "\"CopyeditingType\":" + "\"" + manuscriptDetailForJle.getCopyeditingType() + "\""
						+ "}";
				// +"," +"\"filePathMap\":" +filePathMap
				logger.info("jsonInputString   :" + jsonInputString);
				try (OutputStream os = con.getOutputStream()) {
					byte[] input = jsonInputString.getBytes("utf-8");
					os.write(input, 0, input.length);
				}
				con.getResponseCode();
				if (con.getResponseCode() == 200) {
					flag = true;
					ManuScript manuscript = manuScriptRepo.getById(manuscriptDetailForJle.getManuscript());
					manuscript.setManuscriptStatusId(33);
					manuScriptRepo.save(manuscript);

					ManuscriptEditor manuscriptEditor = manuscriptEditorRepo.getAllAssignedEdiorsOnManuscriptActive(
							manuscriptDetailForJle.getManuscript(), manuscriptDetailForJle.getMansucriptVersion());
					if (manuscriptEditor != null && manuscriptEditor.getManuscriptId() != null) {
						manuscriptEditor.setManuscriptStatus(33);
						manuscriptEditorRepo.save(manuscriptEditor);
					}
				}
				try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"))) {
					StringBuilder response = new StringBuilder();
					String responseLine = null;
					while ((responseLine = br.readLine()) != null) {
						response.append(responseLine.trim());
					}
					logger.debug(response.toString());
					logger.info("Dot.net response  :::   " + response.toString());
				}
			} catch (Exception e) {
				logger.error("Eception : ", e);
			}
		}
		return flag;

	}

	@Override
	public ManuScript updateChargeComment(IDVO id) {

		try {
			if (id == null) {
				logger.error("IDVO parameter is null. Cannot update charge comment.");
				return null;
			}

			ManuScript manuScript = manuScriptRepo.getOne(id.getManuscriptId());

			if (manuScript == null) {
				logger.error(
						"ManuScript with ID " + id.getManuscriptId() + " not found. Cannot update charge comment.");
				return null;
			}

			manuScript.setManuscriptStatusId(id.getTaskId());
			manuScript.setCommentForProduction(id.getCopyrightAgreement());
			manuScript.setAssignedUser(id.getUserId());
			manuScript.setAssignedUserRole(id.getRoleId());

			int userCountryStatus = Integer
					.parseInt(manuScriptRepo.getDevelopingCountryStatus(manuScript.getAuthorId()));

			JournalManuscriptTypeCharge journalManuscriptTypeCharge = journalManuscriptTypeChargeRepo
					.getJournalManuscriptTypeChargeByManuiscriptId(id.getManuscriptId(), manuScript.getJournalId());

			String submissionCharge = "";
			if (journalManuscriptTypeCharge != null) {
				submissionCharge = (journalManuscriptTypeCharge.getCountryChargeOnPublication() != null)
						? journalManuscriptTypeCharge.getCountryChargeOnPublication()
						: "";
			}

			if (id.getTaskId() == 63)
//                    && (submissionCharge.equalsIgnoreCase("SameForAll")
//                    || (submissionCharge.equalsIgnoreCase("FromDevelopingCountry") && userCountryStatus == 0)))
			{
				manuScript.setPaymentStatus("APC Payment required");
				manuScript.setWaveOff(id.getWaveOff());
				manuScriptRepo.saveAndFlush(manuScript);
			} else if (id.getTaskId() == 2) {
				List<UserJournal> getEicList = userJournalRepo.getUserRoleJournalId(manuScript.getJournalId(), 2);

				if (getEicList != null && !getEicList.isEmpty()) {
					manuScript.setAssignedUser(getEicList.get(0).getUserId());
					manuScript.setPaymentStatus("Submission Payment done");
					manuScriptRepo.saveAndFlush(manuScript);

					AuthoritiesHistoryVo authr = new AuthoritiesHistoryVo();
					List<Integer> add = new ArrayList<>();
					authr.setJournalId(manuScript.getJournalId());
					authr.setManuscriptId(manuScript.getManuScriptId());
					authr.setManuscriptVersion(manuScript.getManuscriptVersion());
					authr.setManuscriptStatusId(2);
					authr.setPreviousUserId(id.getUserId());
					authr.setPreviousUserRole(1);
					add.add(getEicList.get(0).getUserId());
					authr.setCurrentUserId(add);
					authr.setCurrentUserRole(2);
					Journal getjournalDetails = journalsRepo.getJournalById(manuScript.getJournalId());
					if (getjournalDetails != null) {
						if (getjournalDetails.getJournalMetadata() != null) {
							if (getjournalDetails.getJournalMetadata().getWorkflowId() != null) {
								List<WorkflowRoleSeq> listOfWorkflowRoleSeq = workflowRoleRepo
										.getByWorkflowId(getjournalDetails.getJournalMetadata().getWorkflowId());
								if (listOfWorkflowRoleSeq != null && !listOfWorkflowRoleSeq.isEmpty()) {
									for (WorkflowRoleSeq l : listOfWorkflowRoleSeq) {

										if (l.getTaskId().equals(13)) {
											authr.setDueDate(new Date().from(LocalDate.now().plusDays(l.getTat())
													.atStartOfDay(ZoneId.systemDefault()).toInstant()));
										}
									}
								}
							}
						}
					}

					authoritiesHistoryService.authoritiesHistory(authr);

				} else {
					logger.error("No EIC found for journal ID " + manuScript.getJournalId()
							+ ". Cannot update charge comment.");
					return null;
				}
			}

			List<ManuScriptReviewer> workingReviewerLst = manuScriptReviewerRepo.getReviewedManu(id.getManuscriptId());
			if (workingReviewerLst != null && !workingReviewerLst.isEmpty()) {
				for (ManuScriptReviewer manuScriptReviewer : workingReviewerLst) {
					if (manuScriptReviewer.getManuscriptStatus().equals(8)
							|| manuScriptReviewer.getManuscriptStatus().equals(66)
							|| manuScriptReviewer.getManuscriptStatus().equals(35)) {
						manuScriptReviewer.setIsActive(0);
						manuScriptReviewerRepo.save(manuScriptReviewer);

						EmailTriggerVO emailTriggerVO = new EmailTriggerVO();
						emailTriggerVO.setJournalId(manuScript.getJournalId());
						emailTriggerVO.setUserId(manuScriptReviewer.getReviewerId());
						emailTriggerVO.setReviewerUserId(manuScriptReviewer.getReviewerId());
						emailTriggerVO.setManuscriptId(manuScript.getManuScriptId());
						emailTriggerVO.setTaskId(87);
						emailTriggerVO.setReceiverRoleId(5);
						emailTriggerVO.setEditorUserId(manuScript.getAssignedUser());
						emailTriggerVO.setRoleId(id.getRoleId());
						emailTriggerVO.setManuscriptStatus(63);
						String message = emailTriggerService.emailTrigger(emailTriggerVO);
						logger.info(" Email sent to Reviewer who has not reviewed the manuscript " + message);

					}

					if (manuScriptReviewer.getManuscriptStatus().equals(12)
							|| manuScriptReviewer.getManuscriptStatus().equals(13)
							|| manuScriptReviewer.getManuscriptStatus().equals(14)
							|| manuScriptReviewer.getManuscriptStatus().equals(15)) {
						EmailTriggerVO emailTriggerVO = new EmailTriggerVO();
						emailTriggerVO.setJournalId(manuScript.getJournalId());
						emailTriggerVO.setUserId(manuScriptReviewer.getReviewerId());
						emailTriggerVO.setReviewerUserId(manuScriptReviewer.getReviewerId());
						emailTriggerVO.setManuscriptId(manuScript.getManuScriptId());
						emailTriggerVO.setTaskId(88);
						emailTriggerVO.setReceiverRoleId(5);
						emailTriggerVO.setEditorUserId(manuScript.getAssignedUser());
						emailTriggerVO.setRoleId(id.getRoleId());
						emailTriggerVO.setManuscriptStatus(63);
						String message = emailTriggerService.emailTrigger(emailTriggerVO);
						logger.info(" Email sent to Reviewer who has reviewed the manuscript " + message);
					}
				}
			}
			return manuScript;
		} catch (Exception e) {
			logger.error("An error occurred while updating charge comment: " + e.getMessage(), e);
			// Handle the exception as appropriate for your application
			return null;
		}
	}

	@Override
	public List<ManuscriptListVO> getAllPayedManuscript(ManuscriptListVO manuscriptVO) {
		List<ManuscriptListVO> newList = new ArrayList<>();

		List<ManuScript> list = new ArrayList<>();

		list = manuScriptRepo.getAllPayedManuscript(manuscriptVO.getManuScriptId());

		StatusMaster statusCode = statusMasterRepo.getOne(manuscriptVO.getManuScriptId());
		if (list != null) {
			for (ManuScript data : list) {
				List<ManuscriptTypeDetails> manuScriptType = manuscriptTypeDetailsRepo
						.getManuscriptTypeDetailsById(data.getManuScriptId());
				User user = userRepo.getOne(data.getAuthorId());
				User editorNameData = userRepo.getByID(data.getAssignedUser());
				ManuscriptListVO manuscriptListVO = new ManuscriptListVO();
				if (editorNameData != null) {
					manuscriptListVO.setEditorName(editorNameData.getFirstName() + " " + editorNameData.getLastName());
				}
				manuscriptListVO.setJournalId(data.getJournalId());
				manuscriptListVO.setManagingEditorId(data.getAssignedUser());
				manuscriptListVO.setCommentForProduction(data.getCommentForProduction());
				manuscriptListVO.setManuscripCode(data.getManuscriptCode());
				manuscriptListVO.setProductionDate(data.getProductionDate());
				manuscriptListVO.setManuscripCode(data.getManuscriptCode());
				manuscriptListVO.setManuscriptAuthorId(data.getAuthorId());
				manuscriptListVO.setManuscripTitle(data.getManuscriptTitle());
				manuscriptListVO.setManuScriptId(data.getManuScriptId());
				manuscriptListVO.setManuscriptVersion(data.getManuscriptVersion());
				manuscriptListVO.setSubmissionDate(data.getSubmissionDate());
				manuscriptListVO.setSubmittedBy(data.getSubmittedBy());
				manuscriptListVO.setCreatedBy(data.getCreatedBy());
				manuscriptListVO.setCreatedOn(data.getCreatedOn());
				manuscriptListVO.setUpdatedBy(data.getUpdatedBy());
				manuscriptListVO.setUpdatedOn(data.getUpdatedOn());
				manuscriptListVO.setManuStatusCode(statusCode.getStatusValue());
				manuscriptListVO.setStatusValueForAuthor(statusCode.getStatusValueForAuthor());
				manuscriptListVO.setStatusValueForEditor(statusCode.getStatusValueForEditor());
				manuscriptListVO.setAuthorName(user.getFirstName() + " " + user.getLastName());
				manuscriptListVO.setJournalTitle(data.getJournal().getJournalTitle());
				manuscriptListVO.setJournalCode(data.getJournal().getJournalCode());
				manuscriptListVO.setManuStatusId(data.getManuscriptStatusId());
				manuscriptListVO.setWithdrawalComment(data.getWithdrawalComment());
				manuscriptListVO.setManagingEditorId(data.getAssignedUser());
				manuscriptListVO.setRoleId(data.getAssignedUserRole());
				if (manuScriptType != null && !manuScriptType.isEmpty()) {
					manuscriptListVO.setManuScriptType(manuScriptType.get(0).getManuscriptType());
				}

				List<ManuscriptManagingEditor> manuscriptManagingEditor = manuscriptManagingEditorRepo
						.getByJournalAndManuscriptId(data.getManuScriptId(), data.getJournalId());
				if (manuscriptManagingEditor != null && !manuscriptManagingEditor.isEmpty()) {
					List<ManuscriptManagingEditorVO> mmeVoList = new ArrayList<>();
					for (ManuscriptManagingEditor mme : manuscriptManagingEditor) {
						User managingEditor = userRepo.getOne(data.getAuthorId());
						ManuscriptManagingEditorVO mmeVO = new ManuscriptManagingEditorVO();
						mmeVO.setUserId(mme.getUserId());
						mmeVO.setName(managingEditor.getFirstName() + " " + managingEditor.getLastName());
						mmeVoList.add(mmeVO);

					}
					manuscriptListVO.setManagingEditor(mmeVoList);
				}

				List<ManuscriptEditor> me = manuscriptEditorRepo.getByJournalAndManuscript(data.getManuScriptId(),
						data.getJournalId());
				if (me != null && !me.isEmpty()) {
					List<ManagingEditorVO> meVOList = new ArrayList<>();
					for (ManuscriptEditor meData : me) {
						User editor = userRepo.getOne(data.getAuthorId());
						ManagingEditorVO meVO = new ManagingEditorVO();
						meVO.setEditorId(meData.getEditorId());
						meVO.setName(editor.getFirstName() + " " + editor.getLastName());
						meVOList.add(meVO);
					}
					manuscriptListVO.setEditorList(meVOList);
				}
				newList.add(manuscriptListVO);
			}
		}

		return newList;
	}

//    @Override
//    public ManuscriptTypeJournalVO getManuscriptCharge(Integer id, String manuscriptType) {
//        ManuscriptTypeJournalVO addManuscriptTypeJournal = new ManuscriptTypeJournalVO();
//
//        try {
//            List<ManuscriptTypeJournal> list = manuscriptTypeJournalsRepo.getManuscriptTypesFromJournalIdAndName(id,
//                    manuscriptType.trim());
//
//            if (list != null && !list.isEmpty()) {
//                JournalManuscriptTypeCharge j = journalManuscriptTypeChargeRepo.getJournalManuscriptTypeChargeIsActive(id,
//                        list.get(0).getMtjId());
//
//                if (j != null) {
//                    addManuscriptTypeJournal.setAdditionalCharges(j.getAdditionalCharges());
//                    addManuscriptTypeJournal.setChargeAmount(j.getChargeAmount());
//                    addManuscriptTypeJournal.setColorFigureCharges(j.getColorFigureCharges());
//                    addManuscriptTypeJournal.setCurrency(j.getCurrency());
//                    addManuscriptTypeJournal.setFigureCharges(j.getFigureCharges());
//                    addManuscriptTypeJournal.setFixedChargeAmount(j.getFixedChargeAmount());
//                    addManuscriptTypeJournal.setFixedCharges(j.getFixedCharges());
//                    addManuscriptTypeJournal.setTablesCharges(j.getTablesCharges());
//                    addManuscriptTypeJournal.setTypeSettingCharges(j.getTypeSettingCharges());
//                    addManuscriptTypeJournal.setChargeOnPublication(j.getChargeOnPublication());
//                    addManuscriptTypeJournal.setChargeOnSubmission(j.getChargeOnSubmission());
//                    addManuscriptTypeJournal.setIsCountryChargeOnPublication(j.getCountryChargeOnPublication());
//                    addManuscriptTypeJournal.setIsCountryChargeOnSubmission(j.getCountryChargeOnSubmission());
//                    addManuscriptTypeJournal.setIsChargesRequired(j.getIsChargesRequired());
//                } else {
//                    logger.error("JournalManuscriptTypeCharge is null for journalId: {} and manuscriptType: {}", id, manuscriptType);
//                }
//            } else {
//                logger.error("No ManuscriptTypeJournal found for journalId: {} and manuscriptType: {}", id, manuscriptType);
//            }
//        } catch (Exception e) {
//            logger.error("An error occurred while fetching manuscript charge: {}", e.getMessage(), e);
//        }
//
//        return addManuscriptTypeJournal;
//    }

	@Override
	public ManuscriptTypeJournalVO getManuscriptCharge(Integer id, String manuscriptType, Integer manuscriptId) {
		ManuscriptTypeJournalVO addManuscriptTypeJournal = new ManuscriptTypeJournalVO();
		boolean paymentFlag = true;

		try {
			List<ManuscriptTypeJournal> list = manuscriptTypeJournalsRepo.getManuscriptTypesFromJournalIdAndName(id,
					manuscriptType.trim());

			if (list != null && !list.isEmpty()) {
				JournalManuscriptTypeCharge j = journalManuscriptTypeChargeRepo
						.getJournalManuscriptTypeChargeIsActive(id, list.get(0).getMtjId());

				DevelopedCountriesByManuscriptType developedCountriesByManuscriptType = developedCountriesByManuscriptTypeRepo
						.getByJournalAndManuscriptType(id, list.get(0).getMtjId());

				ManuScript manuScript = manuScriptRepo.getById(manuscriptId);
				int userCountryStatus = Integer
						.parseInt(manuScriptRepo.getDevelopingCountryStatus(manuScript.getUser().getId()));

				if (developedCountriesByManuscriptType != null
						&& !j.getCountryChargeOnPublication().trim().equals("SameForAll")) {

					if (userCountryStatus > 0) {
						String paymentCountriesForPublishing = developedCountriesByManuscriptType
								.getPaymentCountriesOnPublishing();
						if (paymentCountriesForPublishing != null && !paymentCountriesForPublishing.isEmpty()) {
							Countries countries = manuScript.getUser().getCountries();

							if (paymentCountriesForPublishing.equals(AppConstants.INDIA_OTHERS)) {
								if (countries.getIsDeveloped() != null) {
									paymentFlag = !countries.getIsDeveloped().equals("N");
								}
							} else if (paymentCountriesForPublishing.equals(AppConstants.INDIA)) {
								if (countries.getIsDeveloped() != null && countries.getId().equals(101)) {
									paymentFlag = !countries.getIsDeveloped().equals("N");
								}
							} else if (paymentCountriesForPublishing.equals(AppConstants.OTHERS)) {
								if (countries.getIsDeveloped() != null && !countries.getId().equals(101)) {
									paymentFlag = !countries.getIsDeveloped().equals("N");
								}
							}
						}
					}
				}

				if (j != null) {
					addManuscriptTypeJournal.setAdditionalCharges(j.getAdditionalCharges());
					addManuscriptTypeJournal.setChargeAmount(j.getChargeAmount());
					addManuscriptTypeJournal.setColorFigureCharges(j.getColorFigureCharges());
					addManuscriptTypeJournal.setCurrency(j.getCurrency());
					addManuscriptTypeJournal.setFigureCharges(j.getFigureCharges());
					addManuscriptTypeJournal.setFixedChargeAmount(j.getFixedChargeAmount());
					addManuscriptTypeJournal.setFixedCharges(j.getFixedCharges());
					addManuscriptTypeJournal.setTablesCharges(j.getTablesCharges());
					addManuscriptTypeJournal.setTypeSettingCharges(j.getTypeSettingCharges());
					addManuscriptTypeJournal.setChargeOnPublication(j.getChargeOnPublication());
					addManuscriptTypeJournal.setChargeOnSubmission(j.getChargeOnSubmission());
					addManuscriptTypeJournal.setIsCountryChargeOnPublication(j.getCountryChargeOnPublication());
					addManuscriptTypeJournal.setIsCountryChargeOnSubmission(j.getCountryChargeOnSubmission());
					addManuscriptTypeJournal.setIsChargesRequired(j.getIsChargesRequired());
					addManuscriptTypeJournal.setPaymentFlag(paymentFlag);
				} else {
					logger.error("JournalManuscriptTypeCharge is null for journalId: {} and manuscriptType: {}", id,
							manuscriptType);
				}
			} else {
				logger.error("No ManuscriptTypeJournal found for journalId: {} and manuscriptType: {}", id,
						manuscriptType);
			}
		} catch (Exception e) {
			logger.error("An error occurred while fetching manuscript charge: {}", e.getMessage(), e);
		}

		return addManuscriptTypeJournal;
	}

	@Override
	public JournalManuscriptTypeCharge getManuscriptChargeByManuscriptId(Integer manuscriptId, Integer journalId) {

		JournalManuscriptTypeCharge journalManuscriptTypeCharge = new JournalManuscriptTypeCharge();

		journalManuscriptTypeCharge = journalManuscriptTypeChargeRepo
				.getJournalManuscriptTypeChargeByManuiscriptId(manuscriptId, journalId);

		return journalManuscriptTypeCharge;
	}

	@Override
	public String saveExitManuscript(ManuScriptVO vo) {
		ManuScript manuscript = manuScriptRepo.getOne(vo.getManuscriptId());
		manuscript.setManuscriptVersion(vo.getManuscriptVersion());

		if (vo.getSubmissionStatus() != null && !vo.getSubmissionStatus().isEmpty()
				&& vo.getSubmissionStatus().equalsIgnoreCase("TECHNICAL-MODIFICATION"))
			manuscript.setTechnicalModificationDate(new Date());

		if (vo.getSubmissionStatus() != null && !vo.getSubmissionStatus().isEmpty()
				&& vo.getSubmissionStatus().equalsIgnoreCase("RE-SUBMISSION"))
			manuscript.setResubmissionDate(new Date());

		if (vo.getDeclarationOfInterestText() != null && !vo.getDeclarationOfInterestText().isEmpty())
			manuscript.setDeclarationOfInterestContent(vo.getDeclarationOfInterestText());
		if (vo.getDeclarationOfInterest() != null && !vo.getDeclarationOfInterest().isEmpty())
			manuscript.setDeclarationOfInterest(vo.getDeclarationOfInterest());
		// Save as draft: store "Yes"/"No" when the author selected one
		String copyrightAnswer = normalizeCopyrightAgreement(vo.getCopyrightAgreement());
		if (copyrightAnswer != null)
			manuscript.setCopyrightAgreement(copyrightAnswer);
		if (vo.getCopyrightAgreementContent() != null && !vo.getCopyrightAgreementContent().isEmpty())
			manuscript.setCopyrightAgreementContent(vo.getCopyrightAgreementContent());
//		if (vo.getManuscriptChecklistResponse() != null && !vo.getManuscriptChecklistResponse().isEmpty())
//			manuscript.setManuscriptChecklistResponse(vo.getManuscriptChecklistResponse());

		ManuScript manuscriptData = manuScriptRepo.save(manuscript);
		return manuscriptData.getManuscriptCode();
	}

	/**
	 * Normalises the copyright agreement answer coming from the UI to "Yes" or "No".
	 * Accepts yes/no, y/n, true/false, 1/0 (any case). Returns null when nothing was sent.
	 */
	private String normalizeCopyrightAgreement(String value) {
		if (StringUtils.isBlank(value))
			return null;
		String v = value.trim().toLowerCase();
		if (v.equals("yes") || v.equals("y") || v.equals("true") || v.equals("1"))
			return "Yes";
		if (v.equals("no") || v.equals("n") || v.equals("false") || v.equals("0"))
			return "No";
		logger.warn("Unexpected copyrightAgreement value received: {}", value);
		return null;
	}

	@Override
	public String checkDevelopingCountryUser(Integer userId) {

		String userDevelopingCountryStatus = "";
		userDevelopingCountryStatus = manuScriptRepo.getDevelopingCountryStatus(userId);

		return userDevelopingCountryStatus;

	}

	@Override
	public ManuscriptsDashboardCountVO getAllManuscriptsDashboardCount(EICDashBoardListVO eicDashboardVO) {
//		List<ManuScript> list = new ArrayList<>();
		ManuscriptsDashboardCountVO manuscriptsDashboardCountVO = new ManuscriptsDashboardCountVO();

		List<Tuple> manuScriptListDataFetch = manuScriptRepo.getManuscriptByJournal(eicDashboardVO.getJournalId());
		List<ManuscriptReportDataVO> manuscriptReportData = manuScriptListDataFetch.stream()
				.map(t -> new ManuscriptReportDataVO(t.get(0, Integer.class), t.get(1, String.class),
						t.get(2, Integer.class), t.get(3, Integer.class), t.get(4, String.class),
						t.get(5, Integer.class), t.get(6, Integer.class), t.get(7, Date.class)))
				.collect(Collectors.toList());
		if (manuscriptReportData != null) {
			// Submitted manuscript count
			Integer totalSubmittedManuscriptCount = manuscriptReportData.stream()
					.filter(f -> f.getManuscriptStatus() != null && f.getManuscriptStatus().equals(2))
					.collect(Collectors.toList()).size();
			manuscriptsDashboardCountVO.setSubmittedManuscriptsCount(totalSubmittedManuscriptCount);
			// Resubmitted manuscript count
			Integer totalReSubmittedManuscriptCount = manuscriptReportData.stream()
					.filter(f -> ((f.getRoleId() != null && f.getRoleId().equals(eicDashboardVO.getRoleId()))
							&& (f.getManuscriptStatus() != null && f.getManuscriptStatus().equals(50))))
					.collect(Collectors.toList()).size();
			manuscriptsDashboardCountVO.setReSubmittedManuscriptsCount(totalReSubmittedManuscriptCount);
			// Reviewers manuscript count
			List<ManuscriptReportDataVO> manuscriptForReview = manuscriptReportData.stream()
					.filter(f -> ((f.getRoleId() != null && f.getRoleId().equals(eicDashboardVO.getRoleId()))
							&& (f.getManuscriptStatus() != null
									&& (f.getManuscriptStatus().equals(8) || f.getManuscriptStatus().equals(66)))))
					.collect(Collectors.toList());
			List<Integer> manuscriptIdList = new ArrayList();
			for (ManuscriptReportDataVO editorDataListData : manuscriptForReview) {
				manuscriptIdList.add(editorDataListData.getManuscriptId());
			}

			List<Tuple> reviewerList = manuScriptReviewerRepo
					.getManuscriptReviewerByJournalId(eicDashboardVO.getJournalId());
			List<ReviewerReportDataVO> reviewerReportData = reviewerList.stream()
					.map(t -> new ReviewerReportDataVO(t.get(0, Integer.class), t.get(1, Integer.class),
							t.get(2, Integer.class), t.get(3, Integer.class), Integer.valueOf(t.get(4, Short.class)),
							t.get(5, Date.class)))
					.collect(Collectors.toList());

			List<ReviewerReportDataVO> getReviewedData = reviewerReportData.stream()
					.filter(s -> s.getIsActive().equals(1)).collect(Collectors.toList());

			Integer totalReviewersInvitedNoResponse = manuscriptDashboardArticleService
					.getAllNoResponseManusriptForDashboardCountReport(manuscriptForReview, reviewerReportData);

			Integer totalManuscriptsInReview = manuScriptReviewerRepo
					.getInReviewManuscriptByTuple(eicDashboardVO.getJournalId(), manuscriptIdList).size();

			Integer totalReviewedManuscripts = manuscriptDashboardArticleService
					.getTotalReviewedManusriptForDashboardCountReport(manuscriptForReview, getReviewedData);

			manuscriptsDashboardCountVO.setReviewerInvitedNoResponseCount(totalReviewersInvitedNoResponse);
			manuscriptsDashboardCountVO.setManuscriptsInReviewCount(totalManuscriptsInReview);
			manuscriptsDashboardCountVO.setReviewedManuscriptsCount(totalReviewedManuscripts);

			// Final decision count
			Integer finanDecisionCount = manuscriptReportData.stream()
					.filter(f -> ((f.getRoleId() != null && f.getRoleId().equals(eicDashboardVO.getRoleId()))
							&& (f.getManuscriptStatus() != null && (f.getManuscriptStatus().equals(51)
									|| f.getManuscriptStatus().equals(52) || f.getManuscriptStatus().equals(68)
									|| f.getManuscriptStatus().equals(72) || f.getManuscriptStatus().equals(73)))))
					.collect(Collectors.toList()).size();
			manuscriptsDashboardCountVO.setFinalDecisionCount(finanDecisionCount);

			// Revision manuscript Count
			Integer revisedCount = manuscriptReportData.stream()
					.filter(f -> ((f.getRoleId() != null && f.getRoleId().equals(eicDashboardVO.getRoleId()))
							&& (f.getManuscriptStatus() != null && f.getManuscriptStatus().equals(26))))
					.collect(Collectors.toList()).size();
			manuscriptsDashboardCountVO.setRevisedManuscriptsCount(revisedCount);

			// total manuscript count
			Integer totalManuscriptCount = manuscriptReportData.stream()
					.filter(f -> (f.getManuscriptStatus() != null
							&& (f.getManuscriptStatus().equals(2) || f.getManuscriptStatus().equals(3)
									|| f.getManuscriptStatus().equals(4) || f.getManuscriptStatus().equals(8)
									|| f.getManuscriptStatus().equals(66) || f.getManuscriptStatus().equals(26)
									|| f.getManuscriptStatus().equals(46) || f.getManuscriptStatus().equals(50)
									|| f.getManuscriptStatus().equals(51) || f.getManuscriptStatus().equals(52)
									|| f.getManuscriptStatus().equals(68) || f.getManuscriptStatus().equals(58)
									|| f.getManuscriptStatus().equals(72) || f.getManuscriptStatus().equals(73))))
					.collect(Collectors.toList()).size();
			manuscriptsDashboardCountVO.setTotalManuscriptsCount(totalManuscriptCount);

		}

		return manuscriptsDashboardCountVO;
	}

	@Override
	public ManuscriptsEditorDashboardCountVO getAllManuscriptsEditorDashboardCount(EICDashBoardListVO eicDashboardVO) {
		IDVO id = new IDVO();
		id.setJournalId(eicDashboardVO.getJournalId());
		id.setRoleId(eicDashboardVO.getRoleId());
		id.setUserId(eicDashboardVO.getUserId());

		List<Tuple> manuScriptListDataFetch = manuScriptRepo.getManuscriptByJournalRoleUSer(id.getJournalId(),
				id.getRoleId(), id.getUserId());
		List<ManuscriptReportDataVO> manuscriptEditorDashboardData = manuScriptListDataFetch.stream()
				.map(t -> new ManuscriptReportDataVO(t.get(0, Integer.class), t.get(1, String.class),
						t.get(2, Integer.class), t.get(3, Integer.class), t.get(4, Integer.class),
						t.get(5, Date.class)))
				.collect(Collectors.toList());

		ManuscriptsEditorDashboardCountVO manuscriptsEditorDashboardCountVO = new ManuscriptsEditorDashboardCountVO();
		List<Integer> statusId = Arrays.asList(3, 4, 8, 26, 46, 50, 51, 52, 58, 66, 68);
		List<Tuple> list = manuScriptForDashboardRepo.getManuscriptForEditorTuple(statusId, id.getJournalId(),
				id.getUserId());
		List<Tuple> filterList = new ArrayList<>();
		for (Tuple tuple : list) {
			if (tuple.get(7, Integer.class).equals(3) || tuple.get(7, Integer.class).equals(50)) {
				if (tuple.get(9, Integer.class).equals(3)
						&& eicDashboardVO.getUserId().equals(tuple.get(4, Integer.class))) {
					filterList.add(tuple);
				}

			} else {
				filterList.add(tuple);
			}
		}
		if (manuscriptEditorDashboardData != null && !manuscriptEditorDashboardData.isEmpty()) {
			List<Tuple> reviewerList = manuScriptReviewerRepo
					.getManuscriptReviewerByJournalIdEditorId(id.getJournalId(), id.getUserId());
			List<ReviewerReportDataVO> reviewerReportData = reviewerList.stream()
					.map(t -> new ReviewerReportDataVO(t.get(0, Integer.class), t.get(1, Integer.class),
							t.get(2, Integer.class), t.get(3, Integer.class), Integer.valueOf(t.get(4, Short.class)),
							t.get(5, Date.class)))
					.collect(Collectors.toList());
			Integer totalWithAssignedEditors = 0;
			Integer totalReSubmittedManuscripts = 0;
			Integer totalReviewersInvitedNoResponse = 0;
			Integer totalManuscriptsInReview = 0;
			Integer totalReviewedManuscripts = 0;
			Integer revisedManuscript = 0;

			List<Integer> manuscriptId = new ArrayList();
			List<ManuscriptReportDataVO> manuscriptForReview = new ArrayList();
			for (ManuscriptReportDataVO manuscriptListData : manuscriptEditorDashboardData) {

				if (manuscriptListData != null) {
					if (manuscriptListData.getManuscriptStatus() != null) {
						if (manuscriptListData.getManuscriptStatus().equals(8)
								|| manuscriptListData.getManuscriptStatus().equals(66)) {
							manuscriptForReview.add(manuscriptListData);
							manuscriptId.add(manuscriptListData.getManuscriptId());

						}
						if (manuscriptListData.getManuscriptStatus().equals(3)) {
							totalWithAssignedEditors++;
						} else if (manuscriptListData.getManuscriptStatus().equals(26)) {
							revisedManuscript++;
						} else if (manuscriptListData.getManuscriptStatus().equals(50)) {
							totalReSubmittedManuscripts++;
						}
					}
				}
			}

			List<ReviewerReportDataVO> getReviewedData = reviewerReportData.stream()
					.filter(s -> s.getIsActive().equals(1)).collect(Collectors.toList());

			totalReviewersInvitedNoResponse = manuscriptDashboardArticleService
					.getAllNoResponseManusriptForDashboardCountReport(manuscriptForReview, reviewerReportData);

			totalManuscriptsInReview = manuScriptReviewerRepo
					.getInReviewManuscriptByTuple(eicDashboardVO.getJournalId(), manuscriptId).size();

			totalReviewedManuscripts = manuscriptDashboardArticleService
					.getTotalReviewedManusriptForDashboardCountReport(manuscriptForReview, getReviewedData);

			manuscriptsEditorDashboardCountVO.setAssignedManuscriptsCount(totalWithAssignedEditors);
			manuscriptsEditorDashboardCountVO.setReSubmittedManuscriptsCount(totalReSubmittedManuscripts);
			manuscriptsEditorDashboardCountVO.setReviewedManuscriptsCount(totalReviewedManuscripts);
			manuscriptsEditorDashboardCountVO.setReviewerInvitedNoResponseCount(totalReviewersInvitedNoResponse);
			manuscriptsEditorDashboardCountVO.setManuscriptsInReviewCount(totalManuscriptsInReview);
			manuscriptsEditorDashboardCountVO.setRevisedManuscriptsCount(revisedManuscript);

			manuscriptsEditorDashboardCountVO.setTotalManuscriptsCount(filterList.size());

		} else {

			manuscriptsEditorDashboardCountVO.setAssignedManuscriptsCount(0);
			manuscriptsEditorDashboardCountVO.setReSubmittedManuscriptsCount(0);
			manuscriptsEditorDashboardCountVO.setReviewedManuscriptsCount(0);
			manuscriptsEditorDashboardCountVO.setReviewerInvitedNoResponseCount(0);
			manuscriptsEditorDashboardCountVO.setManuscriptsInReviewCount(0);
			manuscriptsEditorDashboardCountVO.setRevisedManuscriptsCount(0);
			manuscriptsEditorDashboardCountVO.setTotalManuscriptsCount(filterList.size());
		}

		return manuscriptsEditorDashboardCountVO;
	}

	@Override
	public Integer getWithdrawnCount(IDVO id) {

		Integer withdrawnCount = manuScriptRepo.getCountWithdrawnManuscript(id.getJournalId(),
				id.getManuscriptStatusId());

		return withdrawnCount;
	}

	/**
	 * This method is created for one time use for specific purpose. Please don't
	 * use this anywhere else
	 */
	@Override
	public List<ManuscriptListVO> getManuscriptDataForAFP() {
		List<ManuscriptListVO> manuscriptListVOs = new ArrayList<>();

		List<ManuScript> manuscripts = manuScriptRepo.getManuscriptForAFPCheck();
		ManuscriptListVO manuscriptListVO = null;
		List<ImageVO> imageList = null;
		List<ManuScriptFile> manuscriptFiles = null;
		ImageVO imageData = null;
		for (ManuScript manuscript : manuscripts) {
			manuscriptListVO = new ManuscriptListVO();
			manuscriptListVO.setManuScriptId(manuscript.getManuScriptId());
			manuscriptListVO.setManuscriptVersion(manuscript.getManuscriptVersion());
			manuscriptListVO.setManuscripCode(manuscript.getManuscriptCode());
			manuscriptListVO.setJournalId(manuscript.getJournalId());

			imageList = new ArrayList<>();
			manuscriptFiles = manuScriptFileRepo.getDataByManuscriptIdAndVersion(manuscript.getManuScriptId(),
					manuscript.getManuscriptVersion());
			if (manuscriptFiles != null && !manuscriptFiles.isEmpty()) {
				for (ManuScriptFile manuScriptFile : manuscriptFiles) {
					imageData = new ImageVO();
					imageData.setImage_file_name(manuScriptFile.getFileName());
					imageData.setImage_type(manuScriptFile.getFileType());
					if (manuScriptFile.getLegend() != null) {
						imageData.setLegend(manuScriptFile.getLegend());
					} else {
						imageData.setLegend("");
					}
					imageData.setImage_file_path(manuScriptFile.getFilePath());
					imageData.setCitation("");
					imageList.add(imageData);
				}
			}
			manuscriptListVO.setImage(imageList);

			manuscriptListVOs.add(manuscriptListVO);
		}

		return manuscriptListVOs;
	}

	@Override
	public void updateManuscriptDetails(ManuscriptDataApiVo manuscriptDataApiVo) {
		ManuScript manuScript = manuScriptRepo.findByManuscriptCode(manuscriptDataApiVo.getManuscriptCode());
		if (manuScript != null) {
			manuScript.setManuscriptStatusId(manuscriptDataApiVo.getManuscriptStatusId());
			manuScript.setPublishingDate(new Date());
			manuScript.setIssueName(manuscriptDataApiVo.getIssueName());
			manuScriptRepo.save(manuScript);
		}
	}

	/**
	 * This method is created for one time use for specific purpose. Please don't
	 * use this anywhere else
	 */
	@Override
	public void checkManuscriptForAFP() {
		List<ManuscriptListVO> manuscriptListVOs = getManuscriptDataForAFP();

		String baseFolderPath = null;
		File source = null;
		List<ErrorLog> errorLogList = null;
		ErrorLog errorLog = null;
		for (ManuscriptListVO manuscriptListVO : manuscriptListVOs) {
			errorLogList = new ArrayList<>();
			baseFolderPath = ManuscriptUtils.generateManuscriptFolderPath(uniprrFolderPath,
					manuscriptListVO.getJournalId(), manuscriptListVO.getManuScriptId(),
					manuscriptListVO.getManuscriptVersion());
			for (ImageVO imageVO : manuscriptListVO.getImage()) {
				if (!imageVO.getImage_file_name().equalsIgnoreCase("Bundled.docx")
						&& !imageVO.getImage_file_name().equalsIgnoreCase("Bundled.pdf")
						&& !imageVO.getImage_file_name().equalsIgnoreCase("BundledPdfForReviewer.pdf")
						&& !imageVO.getImage_file_name().equalsIgnoreCase("Bundled.html")) {

					source = new File(baseFolderPath + imageVO.getImage_file_name());
					if (!source.exists()) {
						errorLog = new ErrorLog();
						errorLog.setJournalId(manuscriptListVO.getJournalId());
						errorLog.setManuscriptId(manuscriptListVO.getManuScriptId());
						errorLog.setManuscriptVersion(manuscriptListVO.getManuscriptVersion());
						errorLog.setErrorMessage("File doesn't exist : " + source.getPath());
						errorLog.setOperation("AFP_TEST");

						errorLogList.add(errorLog);
					}
				}
			}

			if (!errorLogList.isEmpty()) {
				errorLogRepo.saveAllAndFlush(errorLogList);
			}
		}
	}

	@Override
	public List<ManuscriptTypeJournalVO> getArticleTypeListByJournalId(Integer journalId) {
		logger.info("Inside getManuscriptTypeJournalListByJournalId Impl");
		List<ManuscriptTypeJournalVO> voList = new ArrayList<>();
		List<ManuscriptTypeJournal> list = manuscriptTypeJournalsRepo.getManuscriptTypesFromJournalId(journalId);
		if (list != null && !list.isEmpty()) {
			list.stream().forEach(i -> {
				ManuscriptTypeJournalVO addManuscriptTypeJournal = new ManuscriptTypeJournalVO();
				addManuscriptTypeJournal.setMtjId(i.getMtjId());
				addManuscriptTypeJournal.setManutypeName(i.getManutypeName());
				addManuscriptTypeJournal.setIsActive(i.getIsActive());
				addManuscriptTypeJournal.setIsAbstract(i.getIsAbstract());
				addManuscriptTypeJournal.setTrialNumberMandatory(i.getTrialNumberMandatory());
				voList.add(addManuscriptTypeJournal);
			});
		}
		return voList;
	}

	@Override
	public void updateArticleNumberFlag(ManuscriptTypeJournalVO vo) {

		ManuscriptTypeJournal typeJournal = manuscriptTypeJournalsRepo.getJournalManuscriptTypeBy(vo.getManutypeName(),
				vo.getJournalId());
		typeJournal.setTrialNumberMandatory(vo.getTrialNumberMandatory());
		manuscriptTypeJournalsRepo.save(typeJournal);
	}

	private String authorityHis(ManuScript mst, Date dueDate, Integer statusId) {
		AuthoritiesHistoryVo add = new AuthoritiesHistoryVo();
		add.setManuscriptId(mst.getManuScriptId());
		add.setManuscriptVersion(mst.getManuscriptVersion());
		add.setManuscriptStatusId(statusId);
		add.setJournalId(mst.getJournalId());
		List<Integer> id = new ArrayList<>();
		id.add(mst.getAuthorId());
		add.setCurrentUserId(id);
		add.setCurrentUserRole(4);
		add.setDueDate(dueDate);
		String result = authoritiesHistoryService.authoritiesHistory(add);
		logger.info("Authorities:->" + result);
		return result;
	}

	@Override
	public ManuScript findManuscriptByCommentary(String commentaryMSCode) {
		return manuScriptRepo.findManuscriptByCommentary(commentaryMSCode);
	}

	@Override
	public ManuScript findManuscriptByAuthorResponse(String authorResponseMSCode) {
		return manuScriptRepo.findManuscriptByAuthorResponse(authorResponseMSCode);
	}

	@Override
	public List<ManuScript> findManuscriptByLTE(String lteMSCode) {
		return manuScriptRepo.findManuscriptByLTE(lteMSCode);
	}

	@Override
	public List<ManuScript> findAuthorResponseByLTE(String lteMSCode) {
		return manuScriptRepo.findAuthorResponseByLTE(lteMSCode);
	}

	public List<EditorDashboardVO> getManuscriptForAuthor(UserJournalManuscriptVo userJournalManuscriptVo) {
//        Adding withdrawn status in submitted folder
//        userJournalManuscriptVo.getManuscriptStatusId().add(16);
		List<EditorDashboardVO> newList = new ArrayList();
		if (userJournalManuscriptVo.getSortBy().equalsIgnoreCase("assignedDate")) {
			userJournalManuscriptVo.setSortBy("updatedOn");
		}

		if (userJournalManuscriptVo.getSortBy().equalsIgnoreCase("withdrawnDate")) {
			userJournalManuscriptVo.setSortBy("withdrawnRequestDate");
		}

		if (userJournalManuscriptVo.getSortBy().equalsIgnoreCase("manuscriptType")) {
			userJournalManuscriptVo.setSortBy("manuscriptTypeDetails.manuscriptType");
		}
		if (userJournalManuscriptVo.getSortBy().equalsIgnoreCase("submittedDate")) {
			userJournalManuscriptVo.setSortBy("submissionDate");
		}

		Pageable paging = PageRequest.of(userJournalManuscriptVo.getPageNo(), userJournalManuscriptVo.getLimit(),
				Sort.by(userJournalManuscriptVo.getSort().equalsIgnoreCase("ASC") ? Sort.Direction.ASC
						: Sort.Direction.DESC, new String[] { userJournalManuscriptVo.getSortBy() }));
		if (userJournalManuscriptVo.getRoleId() != null && userJournalManuscriptVo.getRoleId() == 4) {
			new ArrayList();
			List list;
			if (StringUtils.isEmpty(userJournalManuscriptVo.getSearch())) {
				list = this.manuScriptRepo.authorDashboardArticle(userJournalManuscriptVo.getUserId(),
						userJournalManuscriptVo.getManuscriptStatusId(), userJournalManuscriptVo.getJournalId(),
						paging);
			} else {
				list = this.manuScriptRepo.authorDashboardSearchArticle(userJournalManuscriptVo.getUserId(),
						userJournalManuscriptVo.getManuscriptStatusId(), userJournalManuscriptVo.getJournalId(),
						userJournalManuscriptVo.getSearch(), paging);
			}

			if (list != null && !list.isEmpty()) {
				Iterator var5 = list.iterator();

				while (var5.hasNext()) {
					ManuScript data = (ManuScript) var5.next();
					EditorDashboardVO editorDashboardVO = new EditorDashboardVO();
					editorDashboardVO.setManuscriptId(data.getManuScriptId());
					editorDashboardVO.setManuscriptCode(data.getManuscriptCode());
					editorDashboardVO.setManuscriptTitle(data.getManuscriptTitle());
					editorDashboardVO.setProductionDate(data.getProductionDate());
					editorDashboardVO.setCommentForProduction(data.getCommentForProduction());
					editorDashboardVO.setCommentFromProduction(data.getCommentFromProduction());
					editorDashboardVO.setManuscriptAbstract(data.getManuscriptAbstract());
					editorDashboardVO.setRejectedDate(data.getRejectedDate());
					editorDashboardVO.setRejectedFileName(data.getRejectedFileName());
					editorDashboardVO.setRejectedFilePath(data.getRejectedFilePath());
					editorDashboardVO.setEditorRoleId(data.getAssignedUserRole());
					editorDashboardVO.setSubmittedDate(data.getSubmissionDate());
					editorDashboardVO.setAssignedDate(data.getUpdatedOn());
					editorDashboardVO.setAssignedDate(data.getUpdatedOn());

					if (data.getManuscriptStatusId() == 63 || data.getManuscriptStatusId() == 64) {
					    LocalDate assignedDate = data.getUpdatedOn().toInstant()
					        .atZone(ZoneId.systemDefault())
					        .toLocalDate();

					    LocalDate dueDate = assignedDate.plusDays(6);
					    logger.info("statusId: "+data.getManuscriptStatusId()+" assigned Date: "+assignedDate+" due date : "+java.sql.Date.valueOf(dueDate));
					    editorDashboardVO.setDueDate(java.sql.Date.valueOf(dueDate));
					} else {
					    editorDashboardVO.setDueDate(data.getDueDate());
					}
//					editorDashboardVO.setDueDate(data.getDueDate());
					editorDashboardVO.setWithdrawnDate(data.getWithdrawnDate());
					editorDashboardVO.setwithdrawnRequestDate(data.getWithdrawnRequestDate());
					editorDashboardVO.setManuscriptStatusBeforeWithdrawn(data.getManuscriptStatusBeforeWithdrawn());
					editorDashboardVO.setAuthorProofDate(data.getAuthorProofDate());
					editorDashboardVO.setWithdrawalComment(data.getWithdrawalComment());
					editorDashboardVO.setUpdateDate(data.getUpdatedOn());
					editorDashboardVO.setJournalCode(data.getJournal().getJournalCode());
					editorDashboardVO.setJournalTitle(data.getJournal().getJournalTitle());
					editorDashboardVO.setJournalId(data.getJournal().getJournalId());
					editorDashboardVO.setStatusId(data.getStatusMaster().getId());
					editorDashboardVO.setStatusValue(data.getStatusMaster().getStatusValue());
					editorDashboardVO.setStatusValueForAuthor(data.getStatusMaster().getStatusValueForAuthor());
					editorDashboardVO.setStatusValueForEditor(data.getStatusMaster().getStatusValueForEditor());
					editorDashboardVO.setManuscriptVersion(data.getManuscriptVersion());
					editorDashboardVO.setAuthorName(
							data.getUser() != null ? data.getUser().getFirstName() + " " + data.getUser().getLastName()
									: "");
					if (data.getManuscriptStatusId() != null && data.getManuscriptStatusId().equals(2)) {
						editorDashboardVO.setStatusForAuthor("New Submission");
					} else if (data.getManuscriptStatusId() != null && data.getManuscriptStatusId().equals(26)) {
						editorDashboardVO.setStatusForAuthor("Re Submission");
					} else if (data.getManuscriptStatusId() != null && data.getManuscriptStatusId().equals(50)) {
						editorDashboardVO.setStatusForAuthor("Technical Modification Submission");
					}

					editorDashboardVO.setManuscriptType(data.getManuscriptTypeDetails() != null
							? data.getManuscriptTypeDetails().getManuscriptType()
							: "");
					try {
					    List<ImpactArticleDTO> impactList = RawJDBC.fetchImpactData(data.getManuscriptCode());					    
					    if (!impactList.isEmpty()
					    		&&impactList.get(0).getIsImpact()
					    		&& impactList.get(0).getAuthorReviewUrl()!=null
					    		&&!impactList.get(0).getAuthorReviewUrl().isEmpty()) {
					    	logger.info("IMPACT : "+impactList);
					        editorDashboardVO.setAuthorLink(impactList.get(0).getAuthorReviewUrl());
					        editorDashboardVO.setImpactArticle(impactList.get(0).getIsImpact());
					        
					    }else {
					    editorDashboardVO.setImpactArticle(false);
					    }
					} catch (Exception e) {
					    logger.warn("No impact data found for AID: " + data.getManuscriptCode(), e);
					}
					newList.add(editorDashboardVO);
				}
			}
		}

		return newList;
	}

	public Integer getManuscriptAuthorCount(UserJournalManuscriptVo userJournalManuscriptVo) {
		Integer count = this.manuScriptRepo.authorDashboardArticleCount(userJournalManuscriptVo.getUserId(),
				userJournalManuscriptVo.getManuscriptStatusId(), userJournalManuscriptVo.getJournalId());
		return count;
	}

	public Integer getManuscriptAuthorSearchCount(UserJournalManuscriptVo userJournalManuscriptVo) {
		Integer searchCount = this.manuScriptRepo.authorDashboardSearchArticleCount(userJournalManuscriptVo.getUserId(),
				userJournalManuscriptVo.getManuscriptStatusId(), userJournalManuscriptVo.getJournalId(),
				userJournalManuscriptVo.getSearch());
		return searchCount;
	}

//	@Override
//	public List<EditorDashboardVO> getManuscriptForAuthor(UserJournalManuscriptVo userJournalManuscriptVo) {
//		List<EditorDashboardVO> newList = new ArrayList<>();
//		if (userJournalManuscriptVo.getSortBy().equalsIgnoreCase("assignedDate")) {
//			userJournalManuscriptVo.setSortBy("updatedOn");
//		}
//		if (userJournalManuscriptVo.getSortBy().equalsIgnoreCase("withdrawnDate")) {
//			userJournalManuscriptVo.setSortBy("withdrawnRequestDate");
//		}
//		if (userJournalManuscriptVo.getSortBy().equalsIgnoreCase("manuscriptType")) {
//			userJournalManuscriptVo.setSortBy("manuscriptTypeDetails.manuscriptType");
//		}
//		Pageable paging = PageRequest.of(userJournalManuscriptVo.getPageNo(), userJournalManuscriptVo.getLimit(),
//				Sort.by(userJournalManuscriptVo.getSort().equalsIgnoreCase("ASC") ? Sort.Direction.ASC
//						: Sort.Direction.DESC, userJournalManuscriptVo.getSortBy()));
//		if (userJournalManuscriptVo.getRoleId() != null && userJournalManuscriptVo.getRoleId() == 4) {
//			List<ManuScript> list = new ArrayList<>();
//			if (StringUtils.isEmpty(userJournalManuscriptVo.getSearch())) {
//				list = manuScriptRepo.authorDashboardArticle(userJournalManuscriptVo.getUserId(),
//						userJournalManuscriptVo.getManuscriptStatusId(), userJournalManuscriptVo.getJournalId(),
//						paging);
//			} else {
//				list = manuScriptRepo.authorDashboardSearchArticle(userJournalManuscriptVo.getUserId(),
//						userJournalManuscriptVo.getManuscriptStatusId(), userJournalManuscriptVo.getJournalId(),
//						userJournalManuscriptVo.getSearch(), paging);
//			}
//
//			if (list != null && !list.isEmpty()) {
//				for (ManuScript data : list) {
//					EditorDashboardVO editorDashboardVO = new EditorDashboardVO();
//					editorDashboardVO.setManuscriptId(data.getManuScriptId());
//					editorDashboardVO.setManuscriptCode(data.getManuscriptCode());
//					editorDashboardVO.setManuscriptTitle(data.getManuscriptTitle());
//					editorDashboardVO.setProductionDate(data.getProductionDate());
//					editorDashboardVO.setCommentForProduction(data.getCommentForProduction());
//					editorDashboardVO.setCommentFromProduction(data.getCommentFromProduction());
//					editorDashboardVO.setManuscriptAbstract(data.getManuscriptAbstract());
//					editorDashboardVO.setRejectedDate(data.getRejectedDate());
//					editorDashboardVO.setRejectedFileName(data.getRejectedFileName());
//					editorDashboardVO.setRejectedFilePath(data.getRejectedFilePath());
//					editorDashboardVO.setEditorRoleId(data.getAssignedUserRole());
//					editorDashboardVO.setSubmittedDate(data.getSubmissionDate());
//					editorDashboardVO.setAssignedDate(data.getUpdatedOn());
//					editorDashboardVO.setDueDate(data.getDueDate());
//					editorDashboardVO.setWithdrawnDate(data.getWithdrawnDate());
//					editorDashboardVO.setwithdrawnRequestDate(data.getWithdrawnRequestDate());
//					editorDashboardVO.setManuscriptStatusBeforeWithdrawn(data.getManuscriptStatusBeforeWithdrawn());
//					editorDashboardVO.setAuthorProofDate(data.getAuthorProofDate());
//					editorDashboardVO.setWithdrawalComment(data.getWithdrawalComment());
//					editorDashboardVO.setUpdateDate(data.getUpdatedOn());
//					editorDashboardVO.setJournalCode(data.getJournal().getJournalCode());
//					editorDashboardVO.setJournalTitle(data.getJournal().getJournalTitle());
//					editorDashboardVO.setJournalId(data.getJournal().getJournalId());
//					editorDashboardVO.setStatusId(data.getStatusMaster().getId());
//					editorDashboardVO.setStatusValue(data.getStatusMaster().getStatusValue());
//					editorDashboardVO.setStatusValueForAuthor(data.getStatusMaster().getStatusValueForAuthor());
//					editorDashboardVO.setStatusValueForEditor(data.getStatusMaster().getStatusValueForEditor());
//					editorDashboardVO.setManuscriptVersion(data.getManuscriptVersion());
//					editorDashboardVO.setAuthorName(
//							data.getUser() != null ? data.getUser().getFirstName() + " " + data.getUser().getLastName()
//									: "");
//					if (data.getManuscriptStatusId() != null && data.getManuscriptStatusId().equals(2)) {
//						editorDashboardVO.setStatusForAuthor("New Submission");
//					} else if (data.getManuscriptStatusId() != null && data.getManuscriptStatusId().equals(26)) {
//						editorDashboardVO.setStatusForAuthor("Re Submission");
//					} else if (data.getManuscriptStatusId() != null && data.getManuscriptStatusId().equals(50)) {
//						editorDashboardVO.setStatusForAuthor("Technical Modification Submission");
//					}
//					editorDashboardVO.setManuscriptType(data.getManuscriptTypeDetails() != null
//							? data.getManuscriptTypeDetails().getManuscriptType()
//							: "");
//
//					newList.add(editorDashboardVO);
//				}
//			}
//		}
//
//		return newList;
//	}
//
//	@Override
//	public Integer getManuscriptAuthorCount(UserJournalManuscriptVo userJournalManuscriptVo) {
//		Integer count = manuScriptRepo.authorDashboardArticleCount(userJournalManuscriptVo.getUserId(),
//				userJournalManuscriptVo.getManuscriptStatusId(), userJournalManuscriptVo.getJournalId());
//		return count;
//	}
//
//	@Override
//	public Integer getManuscriptAuthorSearchCount(UserJournalManuscriptVo userJournalManuscriptVo) {
//		Integer searchCount = manuScriptRepo.authorDashboardSearchArticleCount(userJournalManuscriptVo.getUserId(),
//				userJournalManuscriptVo.getManuscriptStatusId(), userJournalManuscriptVo.getJournalId(),
//				userJournalManuscriptVo.getSearch());
//		return searchCount;
//	}

	@Override
	public AuthorDashboardCountVO getManuscriptDashboardCountForAuthor(
			UserJournalManuscriptVo userJournalManuscriptVo) {

		List<Integer> technicalModification = new ArrayList<>(Arrays.asList(46));
		List<Integer> submittedManuscript = new ArrayList<>(
				Arrays.asList(2, 26, 50, 46, 4, 33, 30, 37, 38, 58, 3, 8, 51, 52, 57, 61, 62, 65, 66));
		List<Integer> revision = new ArrayList<>(Arrays.asList(4));
		List<Integer> paymentRequired = new ArrayList<>(Arrays.asList(63, 64));
		List<Integer> decision = new ArrayList<>(Arrays.asList(37, 38));
		List<Integer> incompleteSubmission = new ArrayList<>(Arrays.asList(1));
		List<Integer> withdrawlRequest = new ArrayList<>(Arrays.asList(58));
		List<Integer> checkingProofing = new ArrayList<>(Arrays.asList(57, 62));

		AuthorDashboardCountVO authorDashboardCountVO = new AuthorDashboardCountVO();

		authorDashboardCountVO.setTechnicalModificationCount(manuScriptRepo.authorDashboardArticleCount(
				userJournalManuscriptVo.getUserId(), technicalModification, userJournalManuscriptVo.getJournalId()));
		authorDashboardCountVO.setSubmittedManuscriptCount(manuScriptRepo.authorDashboardArticleCount(
				userJournalManuscriptVo.getUserId(), submittedManuscript, userJournalManuscriptVo.getJournalId()));
		authorDashboardCountVO.setRevisionCount(manuScriptRepo.authorDashboardArticleCount(
				userJournalManuscriptVo.getUserId(), revision, userJournalManuscriptVo.getJournalId()));
		authorDashboardCountVO.setPaymentRequiredCount(manuScriptRepo.authorDashboardArticleCount(
				userJournalManuscriptVo.getUserId(), paymentRequired, userJournalManuscriptVo.getJournalId()));
		authorDashboardCountVO.setDecisionCount(manuScriptRepo.authorDashboardArticleCount(
				userJournalManuscriptVo.getUserId(), decision, userJournalManuscriptVo.getJournalId()));
		authorDashboardCountVO.setIncompleteSubmissionCount(manuScriptRepo.authorDashboardArticleCount(
				userJournalManuscriptVo.getUserId(), incompleteSubmission, userJournalManuscriptVo.getJournalId()));
		authorDashboardCountVO.setWithdrawlRequestCount(manuScriptRepo.authorDashboardArticleCount(
				userJournalManuscriptVo.getUserId(), withdrawlRequest, userJournalManuscriptVo.getJournalId()));
		authorDashboardCountVO.setCheckingProofingCount(manuScriptRepo.authorDashboardArticleCount(
				userJournalManuscriptVo.getUserId(), checkingProofing, userJournalManuscriptVo.getJournalId()));

		return authorDashboardCountVO;
	}

	@Override
	public void updateStatus(ManuScript manuScript) {
		manuScriptRepo.updateStage(manuScript.getManuscriptStatusId(), manuScript.getManuScriptId());
	}

//	@Override
//	public int getManuscriptCountByTitleAndJournals(ManuscriptTypeTitleAbstractVO manuScriptVO) {
//		try {
//			if (manuScriptVO.getJournalId() != null && manuScriptVO.getTitle() != null) {
//				Integer manuScricptTitleCount = manuScriptRepo.countByManuscriptTitleAndJournalIdIn(
//						manuScriptVO.getTitle(), manuScriptVO.getJournalId(), manuScriptVO.getManuscriptId());
//				return manuScricptTitleCount;
//			}
//		} catch (Exception e) {
//			logger.error("JournalId not matched to Title");
//		}
//		return 0;
//	}

	public int getManuscriptCountByTitleAndJournals(
	        ManuscriptTypeTitleAbstractVO manuScriptVO) {

	    try {
	        if (manuScriptVO.getJournalId() != null &&
	            manuScriptVO.getTitle() != null) {

	            Integer journalId = manuScriptVO.getJournalId();

	            if (journalId == 259 || journalId == 1819) {
	                return manuScriptRepo.countTitleExcludeIncomplete(
	                        manuScriptVO.getTitle(),
	                        journalId,
	                        manuScriptVO.getManuscriptId());
	            } else {
	                return manuScriptRepo.countTitleAllStatus(
	                        manuScriptVO.getTitle(),
	                        journalId,
	                        manuScriptVO.getManuscriptId());
	            }
	        }

	    } catch (Exception e) {
	        logger.error("Error checking manuscript title count", e);
	    }

	    return 0;
	}

	
    public ManuscriptSimilarityVo checkTitleSimilarity(ManuscriptTypeTitleAbstractVO vo) {
        ManuscriptSimilarityVo dto = new ManuscriptSimilarityVo();
        dto.setMatchPercent(0);
        dto.setExactMatch(false);
        dto.setMatchedTitle(null);

        try {
            if (vo == null || vo.getTitle() == null || vo.getJournalId() == null) {
                return dto;
            }

            String inputTitle = vo.getTitle().trim().replaceAll("\\s+", " ");
            vo.setTitle(inputTitle);

            List<String> existingTitles = manuScriptRepo.findTitlesByJournalIdExcludingManuscriptId(
                vo.getJournalId(), vo.getManuscriptId());

            int highestMatchPercent = 0;
            String matchedTitle = null;

            for (String existingTitle : existingTitles) {
                if (existingTitle != null) {
                    int similarity = computeSimilarityPercent(inputTitle, existingTitle);
                    if (similarity >= 90 && similarity > highestMatchPercent) {
                        highestMatchPercent = similarity;
                        matchedTitle = existingTitle;
                    }
                }
            }

            if (highestMatchPercent >= 90) {
                dto.setMatchPercent(highestMatchPercent);
                dto.setExactMatch(highestMatchPercent == 100);
                dto.setMatchedTitle(matchedTitle);
            }
        } catch (Exception e) {
            logger.error("Error in checkTitleSimilarity: {}", e.getMessage(), e);
        }

        return dto;
    }
	
	public int computeSimilarityPercent(String str1, String str2) {
	    int lev = computeLevenshtein(str1, str2);
	    int jac = computeJaccard(str1, str2);
	    int cos = computeCosine(str1, str2);
	    return Math.max(lev, Math.max(jac, cos));
	}

	private int computeLevenshtein(String s1, String s2) {
	    s1 = s1.toLowerCase().replaceAll("\\s+", " ").trim();
	    s2 = s2.toLowerCase().replaceAll("\\s+", " ").trim();
	    int maxLength = Math.max(s1.length(), s2.length());
	    if (maxLength == 0) return 100;
	    int editDistance = org.apache.commons.lang3.StringUtils.getLevenshteinDistance(s1, s2);
	    return (int) ((1.0 - (double) editDistance / maxLength) * 100);
	}

	private int computeJaccard(String s1, String s2) {
	    Set<String> set1 = new HashSet<>(Arrays.asList(s1.toLowerCase().split("\\s+")));
	    Set<String> set2 = new HashSet<>(Arrays.asList(s2.toLowerCase().split("\\s+")));
	    Set<String> intersection = new HashSet<>(set1);
	    intersection.retainAll(set2);
	    Set<String> union = new HashSet<>(set1);
	    union.addAll(set2);
	    if (union.isEmpty()) return 0;
	    return (int) (((double) intersection.size() / union.size()) * 100);
	}

	private int computeCosine(String s1, String s2) {
	    Map<String, Integer> freq1 = getTermFrequency(s1);
	    Map<String, Integer> freq2 = getTermFrequency(s2);
	    Set<String> allWords = new HashSet<>();
	    allWords.addAll(freq1.keySet());
	    allWords.addAll(freq2.keySet());

	    double dot = 0, mag1 = 0, mag2 = 0;
	    for (String word : allWords) {
	        int v1 = freq1.getOrDefault(word, 0);
	        int v2 = freq2.getOrDefault(word, 0);
	        dot += v1 * v2;
	        mag1 += v1 * v1;
	        mag2 += v2 * v2;
	    }
	    if (mag1 == 0 || mag2 == 0) return 0;
	    return (int) ((dot / (Math.sqrt(mag1) * Math.sqrt(mag2))) * 100);
	}

	private Map<String, Integer> getTermFrequency(String s) {
	    Map<String, Integer> freq = new HashMap<>();
	    for (String word : s.toLowerCase().split("\\s+")) {
	        freq.put(word, freq.getOrDefault(word, 0) + 1);
	    }
	    return freq;
	}



}