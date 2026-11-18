package com.digi.uniprr.serviceImpl;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import com.digi.uniprr.utils.AppConstants;
import com.digi.uniprr.utils.FileUtil;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.digi.uniprr.VO.UserBulkUploadDataVo;
import com.digi.uniprr.VO.UserVO;
import com.digi.uniprr.enums.Salutation;
import com.digi.uniprr.model.Countries;
import com.digi.uniprr.model.States;
import com.digi.uniprr.model.User;
import com.digi.uniprr.model.UserJournal;
import com.digi.uniprr.repository.EditorRoleAccessRepo;
import com.digi.uniprr.repository.JournalsRepo;
import com.digi.uniprr.repository.StatesRepo;
import com.digi.uniprr.repository.UserJournalRepo;
import com.digi.uniprr.repository.UserLoginRepo;
import com.digi.uniprr.repository.UserRoleRepo;
import com.digi.uniprr.service.BulkUploadDataService;
import com.digi.uniprr.service.CountriesService;
import com.digi.uniprr.service.UserJournalService;
import com.digi.uniprr.service.UserRoleService;
import com.digi.uniprr.service.UserService;

@Service
@Transactional
public class BulkUploadDataServiceImpl implements BulkUploadDataService {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    @Autowired
    Environment environment;
    @Autowired
    UserService userService;
    @Autowired
    CountriesService countriesService;
    @Autowired
    EditorRoleAccessRepo editorRoleAccessRepo;
    @Autowired
    UserRoleService userRoleService;
    @Autowired
    UserJournalService userJournalService;
    @Autowired
    UserRoleRepo userRoleRepo;
    @Autowired
    UserJournalRepo userJournalRepo;
    @Autowired
    JournalsRepo journalRepo;
    @Autowired
    UserLoginRepo userRepo;
    @Autowired
    StatesRepo statesRepo;
    @Value("${uniprrFolderPath}")
    private String UPLOAD_FOLDER;

    private static boolean isRowEmpty(Row row) {
        boolean isEmpty = true;
        DataFormatter dataFormatter = new DataFormatter();

        if (row != null) {
            for (Cell cell : row) {
                if (dataFormatter.formatCellValue(cell).trim().length() > 0) {
                    isEmpty = false;
                    break;
                }
            }
        }

        return isEmpty;
    }

    @Override
    public JSONObject userBulkUploadData(UserBulkUploadDataVo userBulkUploadDataVo, HttpServletRequest request) {
        log.info("-------------------Inside in userBulkUploadData service impl  ------------------>");
        BufferedOutputStream stream = null;
        JSONObject obj = new JSONObject();

        if (userBulkUploadDataVo.getJournalId() != null && userBulkUploadDataVo.getRoleId() != null
                && userBulkUploadDataVo.getXlsx() != null && !userBulkUploadDataVo.getXlsx().isEmpty()) {
            String checkExtension = FilenameUtils.getExtension(userBulkUploadDataVo.getXlsx().getOriginalFilename());
            if (checkExtension.equalsIgnoreCase("xlsx")) {
                try {
                    log.info("-------------------File extension match to xlsx------------------>");
                    String attachmentFolderPath = UPLOAD_FOLDER + File.separator + "BulkUploadDataFolder"
                            + File.separator + "UserBulkUploadFolder";
                    log.info("Folder Path Create : " + attachmentFolderPath);
                    File dir = new File(attachmentFolderPath);
                    if (!dir.exists()) {
                        dir.mkdirs();
                        log.info("Created directory: {}", dir.getAbsolutePath());
                    }
                    byte[] bytes = userBulkUploadDataVo.getXlsx().getBytes();
                    File serverFile = new File(dir.getAbsolutePath() + File.separator
                            + userBulkUploadDataVo.getXlsx().getOriginalFilename());
                    FileUtil.updateFilePermission(serverFile);
                    if (serverFile.exists() && serverFile.canRead()) {
                        log.info("File exists and is readable: {}", serverFile.getAbsolutePath());
                    } else {
                        log.error("File does not exist or is not readable.");
                    }
                    log.info("Saving file to: {}", serverFile.getAbsolutePath());
                    stream = new BufferedOutputStream(new FileOutputStream(serverFile));
                    stream.write(bytes);
                    log.info("File saved successfully. Processing file content.");
                    FileInputStream fis = new FileInputStream(serverFile); // obtaining bytes from the file
                    // creating Workbook instance that refers to .xlsx file
                    XSSFWorkbook wb = new XSSFWorkbook(fis);
                    XSSFSheet sheet = wb.getSheetAt(0); // creating a Sheet object to retrieve object
                    log.info("Successfully opened Excel workbook. Sheet name: {}", sheet.getSheetName());
                    Iterator<Row> itr = sheet.iterator(); // iterating over excel file
                    List<UserVO> userVoList = new ArrayList<>();
                    int cellCount = sheet.getRow(0).getLastCellNum();
                    int j = 0;
                    while (itr.hasNext()) {
                        UserVO userVO = new UserVO();
                        Row row = itr.next();
                        if (isRowEmpty(row)) {
                            continue;
                        }
                        if (j > 0) {
                            for (int i = 0; i < cellCount; i++) {
                                Cell cell = row.getCell(i);
                                if (!(cell != null)) {
                                    cell = row.createCell(i);
                                    cell.setCellValue("");
                                }
                                if (i == 0) {
                                    if (cell.toString().equalsIgnoreCase("NOT AVAILABLE")) {
                                        userVO.setUsername("");
                                    } else {
                                        userVO.setUsername(cell.toString());
                                    }
                                } else if (i == 1) {
                                    if (cell.toString().equalsIgnoreCase("NOT AVAILABLE")) {

                                    } else {
                                        userVO.setEmail(cell.toString());
                                        userVO.setEmailCC(cell.toString());
                                    }
                                } else if (i == 2) {
                                    if (cell.toString().equalsIgnoreCase("NOT AVAILABLE")) {
                                        userVO.setMobileNumber("");
                                    } else {
                                        userVO.setMobileNumber(cell.toString());
                                    }
                                } else if (i == 3) {
                                    if (cell.toString().equalsIgnoreCase("NOT AVAILABLE")) {
                                        userVO.setPhoneNumber("");
                                    } else {
                                        userVO.setPhoneNumber(cell.toString());
                                    }
                                } else if (i == 4) {
                                    if (cell.toString().equalsIgnoreCase("NOT AVAILABLE")) {
                                        userVO.setAcademic("");
                                    } else {
                                        userVO.setAcademic(cell.toString());
                                    }
                                } else if (i == 5) {
                                    if (cell.toString().equalsIgnoreCase("NOT AVAILABLE")) {
                                        userVO.setSalutation(Enum.valueOf(Salutation.class, "Mx"));
                                    } else {
                                        userVO.setValidSalutation(cell.toString().replace(" ", ""));
                                    }
                                } else if (i == 6) {
                                    if (cell.toString().equalsIgnoreCase("NOT AVAILABLE")) {
                                        userVO.setFirstName("");
                                    } else {
                                        userVO.setFirstName(cell.toString());
                                    }
                                } else if (i == 7) {
                                    if (cell.toString().equalsIgnoreCase("NOT AVAILABLE")) {
                                        userVO.setLastName("");
                                    } else {
                                        userVO.setLastName(cell.toString());
                                    }
                                } else if (i == 8) {
                                    if (cell.toString().equalsIgnoreCase("NOT AVAILABLE")) {
                                        userVO.setMiddleName("");
                                    } else {
                                        userVO.setMiddleName(cell.toString());
                                    }
                                } else if (i == 9) {
                                    if (cell.toString().equalsIgnoreCase("NOT AVAILABLE")) {
                                        userVO.setAddress("");
                                    } else {
                                        userVO.setAddress(cell.toString());
                                    }
                                } else if (i == 10) {
                                    if (cell.toString().equalsIgnoreCase("NOT AVAILABLE")) {
                                        userVO.setCountryId(249);
                                    } else {
                                        String country = cell.toString();
                                        if (country != null) {
                                            country = country.trim();
                                        }
                                        Countries countries = countriesService.getCountryByName(country);
                                        if (countries != null) {
                                            userVO.setCountryId(countries.getId());
                                            if(userVO.getCountryId()>251)
                                            {
                                                userVO.setCountryId(249);
                                            }
                                        } else {
                                            userVO.setCountryId(249);
                                        }
                                    }
                                } else if (i == 11) {
                                    if (cell.toString().equalsIgnoreCase("NOT AVAILABLE")) {
                                        userVO.setCity("");
                                    } else {
                                        userVO.setCity(cell.toString());
                                    }
                                } else if (i == 12) {
                                    if (cell.toString().equalsIgnoreCase("NOT AVAILABLE")) {
                                        userVO.setState("");
                                    } else {

                                        if (userVO.getCountryId() == 101) {
                                            States state = statesRepo.findByName(cell.toString());
                                            if (state != null) {
                                                userVO.setState(state.getCode());
                                            } else {
                                                userVO.setState(cell.toString());
                                            }
                                        } else {
                                            userVO.setState(cell.toString());
                                        }
                                    }
                                } else if (i == 13) {
                                    if (cell.toString().equalsIgnoreCase("NOT AVAILABLE")) {
                                        userVO.setPostalCode("");
                                    } else {
                                        userVO.setPostalCode(cell.toString().replace(".0", "").trim());
                                    }
                                } else if (i == 14) {
                                    if (cell.toString().equalsIgnoreCase("NOT AVAILABLE")) {
                                        userVO.setSpecialization("");
                                    } else {
                                        userVO.setSpecialization(cell.toString());
                                    }
                                } else if (i == 15) {
                                    if (cell.toString().equalsIgnoreCase("NOT AVAILABLE")) {
                                        userVO.setExpertise("");
                                    } else {
                                        userVO.setExpertise(cell.toString());
                                    }
                                } else if (i == 16) {
                                    if (cell.toString().equalsIgnoreCase("NOT AVAILABLE")) {
                                        userVO.setInstitute("");
                                    } else {
                                        userVO.setInstitute(cell.toString());
                                    }
                                }
                                if (userBulkUploadDataVo.getJournalId() != null) {
                                    List JournalId = new ArrayList();
                                    JournalId.add(userBulkUploadDataVo.getJournalId());
                                    userVO.setJournalId(JournalId);
                                }

                                if (userBulkUploadDataVo.getRoleId() != null) {
                                    if (userBulkUploadDataVo.getRoleId().equals(3)) {
                                        StringBuilder editorAccessDetails = new StringBuilder("{\"data\":[");
                                        for (int m = 0; m < AppConstants.EDITOR_RESPONSIBILITIES.length; m++) {
                                            if (m != AppConstants.EDITOR_RESPONSIBILITIES.length - 1) {
                                                editorAccessDetails.append("\"").append(AppConstants.EDITOR_RESPONSIBILITIES[m].trim()).append("\"").append(",");
                                            } else {
                                                editorAccessDetails.append("\"").append(AppConstants.EDITOR_RESPONSIBILITIES[m].trim()).append("\"").append("]}");
                                            }
                                        }
                                        userVO.setEditorAccessDetails(String.valueOf(editorAccessDetails));
                                    }
                                    List roleId = new ArrayList();
                                    roleId.add(userBulkUploadDataVo.getRoleId());
                                    userVO.setRoleId(roleId);
                                    if (userBulkUploadDataVo.getEditorRoleId() != null) {
                                        userVO.setEditorRoleId(userBulkUploadDataVo.getEditorRoleId());
                                    }
                                }

                            }

                            if (j > 301) {
                                fis.close();
                                wb.close();
                                List<String> validationCheck = new ArrayList<String>();
                                validationCheck.add("Maximum rows allowed is: 300. Please remove data row which is more than 300 in excel.");
                                obj.put("data", "please check validations");
                                obj.put("erros", validationCheck);
                                return obj;
                            }

                            userVO.setRowId(row.getRowNum());
                            userVoList.add(userVO);

                        }
                        j++;

                    }
                    log.info("File processing completed. Total users extracted: {}", userVoList.size());
                    List<String> validationCheck = validationCheck(userVoList);
                    if (!validationCheck.isEmpty()) {
                        obj.put("data", "please check validations");
                        obj.put("erros", validationCheck);
                        return obj;
                    }

                    for (UserVO userDetails : userVoList) {
                        userDetails.setSalutation(Enum.valueOf(Salutation.class, userDetails.getValidSalutation()));
                        userDetails.setIsBulkUpload(true);

                        String phoneNumber = "";
                        String mobileNumber = "";
                        if (!userDetails.getPhoneNumber().isEmpty()) {
                            phoneNumber = BigDecimal.valueOf(Double.parseDouble(userDetails.getPhoneNumber()))
                                    .toPlainString();
                        }

                        if (!userDetails.getMobileNumber().isEmpty()) {
                            mobileNumber = BigDecimal.valueOf(Double.parseDouble(userDetails.getMobileNumber()))
                                    .toPlainString();
                        }

                        userDetails.setPhoneNumber(phoneNumber);
                        userDetails.setMobileNumber(mobileNumber);
                        User usr = null;
                        if (!StringUtils.isEmpty(userDetails.getEmail())) {
                            usr = userRepo.findByEmail(userDetails.getEmail());
                        }
                        if (usr == null) {
                            userService.createUser(userDetails);
                        } else {
                            userDetails.setId(usr.getId());
                            this.updateUserDetails(userDetails, usr);
                            userService.addNewUserRole(userDetails);
                        }

                    }
                } catch (Exception e) {
//                    log.info("-------------------Exception due to code ------------------>" + e);
                	log.error("Error in userBulkUploadData method: {}", e.getMessage(), e);
                    obj.put("data", "Exception due to code");
                    e.printStackTrace();
                    return obj;
                } finally {
                    try {
                        if (userBulkUploadDataVo.getXlsx() != null && !userBulkUploadDataVo.getXlsx().isEmpty()) {
                            stream.close();
                            log.info("-------------------XLSX File Close------------------");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        log.info("-------------------Exception due to file ------------------>" + e);
                    }
                }
            }
            log.info("-------------------XLSX FILE HAS BEEN DELETED------------------>");
            log.info("-------------------All user has been added------------------>");
            obj.put("data", "User has been added");

            return obj;
        } else {
            log.info("-------------------Please upload file with extension xlsx------------------>");
            obj.put("data", "Please upload file with extension xlsx");
            return obj;
        }

    }

    public List<String> userDetailsCheckDup(List<UserVO> userVoList) {
        List<String> userExist = new ArrayList<>();
        List<String> userEmails = new ArrayList<String>();

        for (UserVO userDetails : userVoList) {
            // User user = userRepo.findByEmail(userDetails.getEmail());
            Integer user = userRepo.findByEmailId(userDetails.getEmail());
            if (user != null) {
                // UserJournal isExist = userJournalRepo.existsByEmail(userDetails.getEmail(),
                // userDetails.getJournalId(),userDetails.getRoleId());
                Integer isExist = userJournalRepo.existsByEmailActiveUser(userDetails.getEmail(),
                        userDetails.getJournalId(), userDetails.getRoleId());

                if (isExist != null) {
                    if (isExist == 0) {
                        userDetails.setIsUpdate(true);
                    } else {
                        userDetails.setIsExist(true);
                        // userExist.add("Email already exist in userDetails row : " +
                        // userDetails.getRowId());
                    }
                } else {
                    userDetails.setIsJournalMap(true);
                    userDetails.setId(user);
                }
            }

        }
        userVoList.stream().forEach(uservo -> {
            userEmails.add(uservo.getEmail());
        });

        userVoList.stream().forEach(uservo -> {
            if (Collections.frequency(userEmails, uservo.getEmail()) > 1) {
                userExist.add("Duplication email details present  :" + uservo.getRowId());
            }
        });
        return userExist;
    }

    public void updateUserDetails(UserVO userVo, User userDetails) {

        userDetails.setPubmedId(userVo.getPubmedId());
        if (userVo.getScopusId() != null && !userVo.getScopusId().isEmpty())
            userDetails.setScopusId(userVo.getScopusId());

        userDetails.setAfflication(userVo.getAfflication());
        if (userVo.getAcademic() != null && !userVo.getAcademic().isEmpty())
            userDetails.setAcademic(userVo.getAcademic());

        userDetails.setResearcherId(userVo.getResearcherId());
        if (userVo.getTrialRegistrationNo() != null && !userVo.getTrialRegistrationNo().isEmpty())
            userDetails.setTrialRegistrationNo(userVo.getTrialRegistrationNo());

        userDetails.setExpertise(userVo.getExpertise());
        if (userVo.getSpecialization() != null && !userVo.getSpecialization().isEmpty())
            userDetails.setSpecialization(userVo.getSpecialization());

        userDetails.setFaxNumber(userVo.getFaxNumber());

        userDetails.setPhoneNumber(userVo.getPhoneNumber());
        if (userVo.getSalutation() != null)
            userDetails.setSalutation(userVo.getSalutation());
        if (userVo.getFirstName() != null && !userVo.getFirstName().isEmpty())
            userDetails.setFirstName(userVo.getFirstName());

        userDetails.setMiddleName(userVo.getMiddleName());
        if (userVo.getLastName() != null && !userVo.getLastName().isEmpty())
            userDetails.setLastName(userVo.getLastName());
        if (userVo.getUsername() != null && !userVo.getUsername().isEmpty())
            userDetails.setUsername(userVo.getUsername());

        userDetails.setInstitute(userVo.getInstitute());

        userDetails.setMobileNumber(userVo.getMobileNumber());
        if (userVo.getOrcidId() != null && !userVo.getOrcidId().isEmpty())
            userDetails.setOrcidId(userVo.getOrcidId());

        userDetails.setAddress(userVo.getAddress());

        userDetails.setCity(userVo.getCity());
        if (userVo.getCountryId() != null)
            userDetails.setCountryId(userVo.getCountryId());
        if (userVo.getState() != null && !userVo.getState().isEmpty())
            userDetails.setState(userVo.getState());

        userDetails.setPostalCode(userVo.getPostalCode());
        if (StringUtils.isNotBlank(userVo.getEditorAccessDetails()))
            userDetails.setEditorAccessDetails(userVo.getEditorAccessDetails());
        userRepo.save(userDetails);
    }

    public void mappingUserWithRoleAndJournalID(UserVO userVo) {

        UserJournal userJournal = new UserJournal();
        userJournal.setIsActive(1);
        userJournal.setJournalId(userVo.getJournalId().get(0));
        userJournal.setUserId(userVo.getId());
        userJournal.setRoleId(userVo.getRoleId().get(0));
        userJournal.setUpdatedDate(new Date());
        userJournalRepo.save(userJournal);

    }

    private List<String> validationCheck(List<UserVO> userVoList) {
        List<String> validationCheck = new ArrayList<>();
        for (UserVO userDetails : userVoList) {
            List<?> validation = this.validation(userDetails);
            if (validation != null) {
                validationCheck
                        .add("Details unable to process in Row : " + userDetails.getRowId() + validation.toString());
            }
        }

        return validationCheck;

    }

    public List<String> userDetailsCheck(List<UserVO> userVoList) {
        List<String> userExist = new ArrayList<>();
        List<String> userEmails = new ArrayList<String>();

        // need to check with journal ID and Role if not avialble need to map

        for (UserVO userDetails : userVoList) {
            User isExist = userRepo.existsByEmail(userDetails.getEmail(), userDetails.getJournalId(),
                    userDetails.getRoleId());
            if (isExist != null) {
                UserJournal UserJournal = userJournalRepo.getUsersJournalDetailsByJournal(isExist.getId(),
                        userDetails.getJournalId().get(0), userDetails.getRoleId().get(0));

                if (UserJournal.getIsActive() == 0) {
                    userDetails.setIsUpdate(true);
                } else {
                    userExist.add("Email already exist in userDetails row : " + userDetails.getRowId());
                }
            }

            User user = userRepo.findByEmail(userDetails.getEmail());

            if (user != null) {
                List<UserJournal> userJournals = userJournalRepo.getUserJournalByUserId(user.getId());

                List<UserJournal> filterUserJournal = userJournals.stream()
                        .filter(userJournal -> userJournal.getJournalId() == userDetails.getJournalId().get(0)
                                && userJournal.getRoleId() == userDetails.getRoleId().get(0))
                        .collect(Collectors.toList());

                if (filterUserJournal.isEmpty()) {
                    userDetails.setIsJournalMap(true);
                    userDetails.setId(user.getId());
                }

            }

        }
        userVoList.stream().forEach(uservo -> {
            userEmails.add(uservo.getEmail());
        });

        userVoList.stream().forEach(uservo -> {
            if (Collections.frequency(userEmails, uservo.getEmail()) > 1) {
                userExist.add("Duplication email details present  :" + uservo.getRowId());
            }
        });
        return userExist;

    }

    public List<?> validation(UserVO userVO) {
        List<String> errors = new ArrayList<>();

        Pattern pattern = null;
        if (userVO.getFirstName() == null || userVO.getFirstName().equals("")) {
            errors.add(" First Name is Mandatory");
        }
        if (userVO.getEmail() == null || userVO.getEmail().equals("")) {
            errors.add(" Email is Mandatory");
        }
//		if (userVO.getMobileNumber() == "" || userVO.getMobileNumber() == null) {
//			errors.add(" mobileNo is Mandatory");
//		}
        if (userVO.getCountryId() == null) {
            errors.add(" Country is Mandatory");
        }
        List<Integer> roles = userVO.getRoleId();
        for (Integer roleId : roles) {
            if (roleId == 4) {
                if (userVO.getInstitute() == null && !userVO.getInstitute().equals("") || !StringUtils.isNotBlank(userVO.getInstitute())) {
                    errors.add("Institute is Mandatory");
                }
                if (userVO.getAcademic() == null && !userVO.getAcademic().equals("") || !StringUtils.isNotBlank(userVO.getAcademic())) {
                    errors.add("Department is Mandatory");
                }
            }
            if (roleId == 5) {
                if (userVO.getSpecialization() == null && !userVO.getSpecialization().equals("") || !StringUtils.isNotBlank(userVO.getSpecialization())) {
                    errors.add("Specialization is Mandatory");
                }
            }
        }


        if (userVO.getCountryId() != null) {
            if (userVO.getCountryId() == 0) {
                errors.add(" please mention valid Country");
            }
        }

        if (userVO.getCountryId() != null) {
            if (userVO.getCountryId() == 101) {
                States state = statesRepo.getNameAndCountryId(userVO.getState());
                if (userVO.getState() == null || userVO.getState().equals("") || !StringUtils.isNotBlank(userVO.getState())) {
                    errors.add("State is Mandatory");
                }
                if (state == null) {
                    errors.add("please mention valid state");
                }
            }
        }

        if (userVO.getValidSalutation() != null && !(userVO.getValidSalutation().equals(""))) {
            if (!EnumUtils.isValidEnum(Salutation.class, userVO.getValidSalutation())) {
                errors.add(" Salutation Is Not Properly Defined");
            }
        } else {
            errors.add("Salutation is Mandatory");
        }

        if (userVO.getUsername() != null && !(userVO.getUsername().equals(""))) {
            String userName = userVO.getUsername();
            if (userName.length() > 250) {
                errors.add("Maximum characters(250) exceeded for User Name");
            }
        }

        if (userVO.getEmail() != null && !userVO.getEmail().equals("")) {
            String email = userVO.getEmail();
            if (email.length() > 250) {
                errors.add("Maximum characters(250) exceeded for Email");
            }
            pattern = Pattern.compile("^[A-Za-z0-9+_.-]+@([A-Za-z0-9-]+\\.)+[A-Za-z]{2,4}$");
            Matcher matcher = pattern.matcher(userVO.getEmail());
            if (matcher.matches() == false) {
                errors.add(" Email Is Not Properly Defined");
            }
        }
        if (userVO.getMobileNumber() != null && !userVO.getMobileNumber().equals("")) {

            Matcher matcher = null;
            // Pattern.compile("^(\d{10}|\d{12})$"); this allows only 10 or 12 digits
            pattern = Pattern.compile("\\d{10,12}$");
            String mobileNumber = null;
            try {
                mobileNumber = BigDecimal.valueOf(Double.parseDouble(userVO.getMobileNumber())).toPlainString();
                if (mobileNumber.length() > 100) {
                    errors.add("Maximum characters(100) exceeded for Mobile Number");
                }
            } catch (Exception e) {
                errors.add(" MobileNumber contains characters or space");
                return errors;

            }
            matcher = pattern.matcher(mobileNumber);
            if (!matcher.matches()) {
                errors.add(" MobileNo Is Not Properly Defined");
            }
            String[] validMobileNo = mobileNumber.split(" ");
            if (validMobileNo.length > 1) {
                errors.add(" MobileNumber Is Not allowed with spaces");
            }

        }
        if (userVO.getPhoneNumber() != null && !userVO.getPhoneNumber().equals("")) {
            String phoneNumber = null;
//			System.out.println("BulkUploadDataServiceImpl.validation()" + userVO.getPhoneNumber());
            try {
                phoneNumber = BigDecimal.valueOf(Double.parseDouble(userVO.getPhoneNumber())).toPlainString();
                if (phoneNumber.length() > 100) {
                    errors.add("Maximum characters(100) exceeded for Phone Number");
                }
            } catch (Exception e) {
                errors.add(" phoneNumber contains characters");
                return errors;

            }

            pattern = Pattern.compile("\\d{10,15}$");
            Matcher matcher = pattern.matcher(phoneNumber);
            boolean isMatches = matcher.matches();

            if (!matcher.matches()) {
                errors.add(" phoneNumber Is Not Properly Defined");
            }
            String[] validMobileNo = phoneNumber.split(" ");
            if (validMobileNo.length > 1) {
                errors.add(" phoneNumber Is Not allowed with spaces");
            }

        }

        if (userVO.getFaxNumber() != null && !userVO.getFaxNumber().equals("")) {
            String faxNo = userVO.getFaxNumber();
            if (faxNo.length() > 100) {
                errors.add("Maximum characters(100) exceeded for Fax Number");
            }
        }

        if (userVO.getFirstName() != null && !userVO.getFirstName().equals("")) {
            String firstName = userVO.getFirstName();
            if (firstName.length() > 250) {
                errors.add("Maximum characters(250) exceeded for First Name");
            }
        }
        if (userVO.getMiddleName() != null && !userVO.getMiddleName().equals("")) {
            String middleName = userVO.getMiddleName();
            if (middleName.length() > 250) {
                errors.add("Maximum characters(250) exceeded for Medium Name");
            }
        }
        if (userVO.getLastName() != null && !userVO.getLastName().equals("")) {
            String lastName = userVO.getLastName();
            if (lastName.length() > 250) {
                errors.add("Maximum characters(250) exceeded for Last Name");
            }
        }
        if (userVO.getAddress() != null && !userVO.getAddress().equals("")) {
            String address = userVO.getAddress();
            if (address.length() > 250) {
                errors.add("Maximum characters(250) exceeded for Address");
            }
        }
        if (userVO.getCity() != null && !userVO.getCity().equals("")) {
            String city = userVO.getCity();
            if (city.length() > 250) {
                errors.add("Maximum characters(250) exceeded for City");
            }
        }
        if (userVO.getState() != null && !userVO.getState().equals("")) {
            String state = userVO.getState();
            if (state.length() > 250) {
                errors.add("Maximum characters(250) exceeded for State");
            }
        }
        if (userVO.getPostalCode() != null && !userVO.getPostalCode().equals("")) {
            String postalCode = userVO.getPostalCode();
            if (postalCode.length() > 250) {
                errors.add("Maximum characters(250) exceeded for Postal Code");
            }
        }
        if (userVO.getSpecialization() != null && !userVO.getSpecialization().equals("")) {
            String specialization = userVO.getSpecialization();
            if (specialization.length() > 250) {
                errors.add("Maximum characters(250) exceeded for Specialization");
            }
        }
        if (userVO.getExpertise() != null && !userVO.getExpertise().equals("")) {
            String expertise = userVO.getExpertise();
            if (expertise.length() > 250) {
                errors.add("Maximum characters(250) exceeded for Sub - specialization");
            }
        }
        if (userVO.getInstitute() != null && !userVO.getInstitute().equals("")) {
            String institute = userVO.getInstitute();
            if (institute.length() > 250) {
                log.info(institute + " :: " + institute.length());
                errors.add("Maximum characters(250) exceeded for Institute");
            }
        }

        if (userVO.getAcademic() != null && !userVO.getAcademic().equals("")) {
            String acdemic = userVO.getAcademic();
            if (acdemic.length() > 250) {
                log.info(acdemic + " :: " + acdemic.length());
                errors.add("Maximum characters(250) exceeded for Acdemic");
            }
        }

        if (errors.isEmpty()) {
            return null;
        }

        return errors;
    }
}
