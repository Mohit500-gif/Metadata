package com.digi.uniprr.utils;

import java.util.List;

public class AppConstants {

    public static final String ENVIRONMENT_PROD = "prod";
    public static final String ENVIRONMENT_UAT = "uat";
    public static final String ENVIRONMENT_QA = "qa";
    public static final String ENVIRONMENT_DEV = "dev";

    public static final String CONFIGURE_JOURNAL = "Configure new journal";
    public static final String EDIT_JOURNAL = "Edit journal configuration";
    public static final String TEMPLATE_PLACEHOLDERS = "Template Placeholders";

    public static final String MANUSCRIPT_TYPE = "manuscript_type";
    public static final String MANUSCRIPT_TITLE = "manuscript_title";
    public static final String MANUSCRIPT_RUNNING_HEAD = "manuscript_running_head";
    public static final String MANUSCRIPT_ABSTRACT = "manuscript_abstract";
    public static final String MANUSCRIPT_FIGURES = "manuscript_figures";
    public static final String MANUSCRIPT_BLACK_WHITE_FIGURES = "manuscript_black_white_figures";
    public static final String MANUSCRIPT_COLOR_FIGURES = "manuscript_color_figures";
    public static final String MANUSCRIPT_TABLES = "manuscript_tables";
    public static final String MANUSCRIPT_WORDS = "manuscript_words";
    public static final String MANUSCRIPT_PAGES = "manuscript_pages";
    public static final String MANUSCRIPT_PARAGRAPH = "manuscript_paragraph";
    public static final String MANUSCRIPT_INTRODUCTION = "manuscript_introduction";
    public static final String MANUSCRIPT_ACKNOWLEDGMENT = "manuscript_acknowledgment";
    public static final String MANUSCRIPT_REFERENCES = "manuscript_references";
    public static final String MANUSCRIPT_SHORT_TITLE = "manuscript_short_title";
    public static final String FILE_PATH = "file_path";
    public static final String FINAL_DOC = "Bundled.docx";

    public static final String Avg_Syllables_Count = "Avg_Syllables_Count";
    public static final String Avg_Word_Length = "Avg_Word_Length";
    public static final String Difficult_Words_Ratio = "Difficult_Words_Ratio";
    public static final String Flesch_Kincaid_Grade = "Flesch_Kincaid_Grade";
    public static final String Flesch_Reading_Ease = "Flesch_Reading_Ease";
    public static final String Gunning_Fog_Index = "Gunning_Fog_Index";
    public static final String POS_Ratio = "POS_Ratio";
    public static final String Sentence_Length = "Sentence_Length";
    public static final String Sentence_No = "Sentence_No";
    public static final String Type_To_Token_Ratio = "Type_To_Token_Ratio";

    public static final String parse_tree_height = "parse_tree_height";
    public static final String sentence_coherence = "sentence_coherence";
    public static final String visual_words_ratio = "visual_words_ratio";


    public static final String FINAL_DOC_FILE_SIZE = "size";
    public static final String MANUSCRIPT_KEYWORDS = "manuscript_keywords";

    public static final String MANUSCRIPT_XML_PATH = "manuscript_xml_path";
    public static final String MANUSCRIPT_XHTML_PATH = "manuscript_html_path";

    public static final String MANUSCRIPT_VISUALIZATION = "Visualization";

    public static final String MERGED_FILE = "MergedFile";

    public static final String YES = "Yes";
    public static final String NO = "No";

    public static final String PLAGARISM_CHECKLIST = "PLAGARISM_CHECKLIST";

    public static final String TITLE = "Title";

    public static final String PUBLNSTOKEN = "Token bd665bb83baa19d53bb0cc9f99ca66de63579163";

    /**************************FOR EMAIL TEMPLATE **************************/
    public static final String MANUSCRIPT_ID = "[MANUSCRIPT_ID]";
    public static final String AUTHOR_NAME = "[AUTHOR_NAME]";
    public static final String JOURNAL_TITLE = "[JOURNAL_TITLE]";
    public static final String MANUSCRIPT_TITLES = "[MANUSCRIPT_TITLE]";
    public static final String SUBMISSION_DATE = "[SUBMISSION_DATE]";
    public static final String ARTICLE_DOWNLOAD_URL = "[ARTICLE_DOWNLOAD_URL]";
    public static final String SIGNATURE = "[SIGNATURE]";
    public static final String ADMIN_NAME = "[ADMIN_NAME]";
    public static final String URL = "[URL]";
    public static final String EDITOR_NAME = "[EDITOR_NAME]";
    public static final String REVIEWER_NAME = "[REVIEWER_NAME]";
    public static final String REVIEWER_RECOMMENDATION = "[REVIEWER_RECOMMENDATION]";
    public static final String REVIEWER_RATING = "[REVIEWER_RATING]";
    public static final String MANAGING_EDITOR_NAME = "[MANAGING_EDITOR_NAME]";
    public static final String EDITOR_DESSION_COMMENTS_TO_AUTHOR = "[EDITOR_DESSION_COMMENTS_TO_AUTHOR]";
    public static final String EDITOR_DECISION = "[EDITOR_DECISION]";
    public static final String REVIEWER_COMMENTS_TO_EDITOR = "[REVIEWER_COMMENTS_TO_EDITOR]";
    public static final String REVIEWER_COMMENTS_TO_AUTHOR = "[REVIEWER_COMMENTS_TO_AUTHOR]";
    public static final String REVIEWER_DECISION = "[REVIEWER_DECISION]";
    public static final String AUTHOR_SALUTATION = "[AUTHOR_SALUTATION]";
    public static final String AUTHOR_FIRST_NAME = "[AUTHOR_FIRST_NAME]";
    public static final String AUTHOR_LAST_NAME = "[AUTHOR_LAST_NAME]";
    public static final String REVIEWER_SALUTATION = "[REVIEWER_SALUTATION]";
    public static final String REVIEWER_FIRST_NAME = "[REVIEWER_FIRST_NAME]";
    public static final String REVIEWER_LAST_NAME = "[REVIEWER_LAST_NAME]";
    public static final String EDITOR_SALUTATION = "[EDITOR_SALUTATION]";
    public static final String EDITOR_FIRST_NAME = "[EDITOR_FIRST_NAME]";
    public static final String EDITOR_LAST_NAME = "[EDITOR_LAST_NAME]";
    public static final String ADMIN_SALUTATION = "[ADMIN_SALUTATION]";
    public static final String ADMIN_FIRST_NAME = "[ADMIN_FIRST_NAME]";
    public static final String ADMIN_LAST_NAME = "[ADMIN_LAST_NAME]";
    public static final String MANAGING_EDITOR_SALUTATION = "[MANAGING_EDITOR_SALUTATION]";
    public static final String MANAGING_EDITOR_FIRST_NAME = "[MANAGING_EDITOR_FIRST_NAME]";
    public static final String MANAGING_EDITOR_LAST_NAME = "[MANAGING_EDITOR_LAST_NAME]";
    public static final String DEFAULT_EDITOR = "[DEFAULT_EDITOR]";
    public static final String DEFAULT_EDITOR_FIRST_NAME = "[DEFAULT_EDITOR_FIRST_NAME]";
    public static final String DEFAULT_EDITOR_LAST_NAME = "[DEFAULT_EDITOR_LAST_NAME]";
    public static final String DEFAULT_EDITOR_SALUTATION = "[DEFAULT_EDITOR_SALUTATION]";
    public static final String JOURNAL_EMAIL = "[JOURNAL_EMAIL]";
    public static final String ACCEPT_BY_REVIEWER_URL = "[ACCEPT_BY_REVIEWER_URL]";
    public static final String REJECT_BY_REVIEWER_URL = "[REJECT_BY_REVIEWER_URL]";
    public static final String VIEW_BY_REVIEWER_URL = "[VIEW_BY_REVIEWER_URL]";
    public static final String JOURNAL_CODE = "[JOURNAL_CODE]";
    public static final String COMMENT_TO_AUTHOR = "[COMMENT_TO_AUTHOR]";
    public static final String ARTICLE_TYPE = "[ARTICLE_TYPE]";
    public static final String ROLE_TYPE = "[ROLE_TYPE]";
    public static final String RESUBMISSION_DATE = "[RESUBMISSION_DATE]";
    public static final String DATE_SENT = "[DATE_SENT]";
    public static final String EDITOR_IN_CHIEF_EMAIL = "[EDITOR_IN_CHIEF_EMAIL]";
    public static final String PHASE_NAME = "[PHASE_NAME]";
    public static final String JOURNAL_WEBSITE_URL = "[JOURNAL_WEBSITE_URL]";
    public static final String PRODUCTION_SITE_URL = "[PRODUCTION_SITE_URL]";
    public static final String EDITOR_COMMENT = "[EDITOR_COMMENT]";
    public static final String COMPLETION_DUE_DATE = "[COMPLETION_DUE_DATE]";
    public static final String REVIEWER_EMAIL_ID = "[REVIEWER_EMAIL_ID]";
    public static final String JOURNAL_NAME = "[JOURNAL_NAME]";
    public static final String SALUTATION = "[TITLE]";
    public static final String LAST_NAME = "[LAST_NAME]";
    public static final String CORRESPONDING_AUTHOR_NAME = "[CORRESPONDING_AUTHOR_NAME]";
    public static final String AUTHOR_COUNTRY = "[AUTHOR_COUNTRY]";
    public static final String EIC_FOLDER_NAME = "[EIC_FOLDER_NAME]";
    public static final String DUE_DATE = "[DUE_DATE]";
    public static final String AUTHOR_IMPACT_URL = "[AUTHOR_IMPACT_URL]";
    public static final String GET = "Get";
    public static final String POST = "Post";
    public static final String PUT = "Put";
    public static final String DELETE = "Delete";

    public static final String ACCEPT_FOR_PRODUCTION = "ACCEPT_FOR_PRODUCTION";
    public static final String FOR_JLE = "FOR_JLE";
    public static final String NORESPONSE = "NoResponse";
    public static final String REVIEWED = "Reviewed";
    public static final String INREVIEW = "InReview";
    public static final String FAILED = "Failed";
    public static final String INPROGRESS = "Inprogress";
    public static final String SUCCESS = "Success";
    public static final String BASIC_AUTH = "Basic YXBwQ2xpZW50OmFwcENsaWVudFNlY3JldA==";
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String AUTHORIZATION = "Authorization";
    public static final String APPLICATION_JSON = "application/json";
    public static final String ZERO_TIME=" 00:00:00";


 // For localhost
//  public static final String VALIDATION_URL="http://localhost:8085/td-authorization/validate";
//  public static final String APP_LOGIN_URL= "http://localhost:8085/td-authorization/app/login";
//  public static final String CONTEXT_URL="";


  //  For dev
//  public static final String CONTEXT_URL = "/uniprrapi";
//  public static final String VALIDATION_URL = "http://localhost:8080/td-authorization/validate";
//  public static final String APP_LOGIN_URL = "http://localhost:8080/td-authorization/app/login";

  // For prod
	public static final String CONTEXT_URL="/uniprroneapi";
	public static final String VALIDATION_URL="http://localhost:8080/td-authorization/validate";
	public static final String APP_LOGIN_URL= "http://localhost:8080/td-authorization/app/login";

    // Constants for Logger
    public static final String REQUEST_METHOD= "Request Method";
    public static final String REQUEST_URI = "Request URI";
    public static final String REQUEST_BODY = "Request Body";

    public static final String REQUEST_PARAMS ="Request Parameters";
    public static final String[] EDITOR_RESPONSIBILITIES={"Only Assigned Articles Search","Copy Scape Check","iThenticate Check","Manage Author","Manage Reviewer","Proxy Author","Proxy Reviewer","Statistics Report","Performance Report","Manuscript Status Report","Plagiarism Report","Commentary","Invite Reviewer and Invite for Re-review","Change Article Status"};
    public static final String INDIA_OTHERS="India,Others";
    public static final String INDIA="India";
    public static final String OTHERS="Others";

}
