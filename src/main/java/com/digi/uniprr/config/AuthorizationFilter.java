package com.digi.uniprr.config;
import com.digi.uniprr.service.TokenExpireService;
import com.digi.uniprr.utils.AppConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
@Component
public class AuthorizationFilter extends OncePerRequestFilter {
    @Autowired
    TokenExpireService tokenExpireService;
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        List<String> excludedURLs = new ArrayList<>();
        excludedURLs.add(AppConstants.CONTEXT_URL+"/tokenExpired/getExpirationMessage");
        excludedURLs.add(AppConstants.CONTEXT_URL+"/goLive");
        excludedURLs.add(AppConstants.CONTEXT_URL+"/journal/journalList");
        excludedURLs.add(AppConstants.CONTEXT_URL+"/journal/getByJournalCode");
        excludedURLs.add(AppConstants.CONTEXT_URL+"/announcement/getList");
        excludedURLs.add(AppConstants.CONTEXT_URL+"/getDataFromMedKnow/getIndex");
        excludedURLs.add(AppConstants.CONTEXT_URL+"/getDataFromMedKnow/getImage");
        excludedURLs.add(AppConstants.CONTEXT_URL+"/contactUs");
        excludedURLs.add(AppConstants.CONTEXT_URL+"/subject/getSubjectList");
        excludedURLs.add(AppConstants.CONTEXT_URL+"/countries/list");
        excludedURLs.add(AppConstants.CONTEXT_URL+"/user/create");
        excludedURLs.add(AppConstants.CONTEXT_URL+"/forgotPassword");
        excludedURLs.add(AppConstants.CONTEXT_URL+"/userLoginStatus");
        excludedURLs.add(AppConstants.CONTEXT_URL+"/userRole/isAcceessRole");
        excludedURLs.add(AppConstants.CONTEXT_URL+"/user/getUserCountOnAdmin");
        excludedURLs.add(AppConstants.CONTEXT_URL+"/journal/getJournalAdmin");
        excludedURLs.add(AppConstants.CONTEXT_URL+"/user/userVerificationUpdationPassword");
        excludedURLs.add(AppConstants.CONTEXT_URL+"/user/checkUserByJournal");
        excludedURLs.add(AppConstants.CONTEXT_URL+"/reviewerSuggested/createDecline");
        excludedURLs.add(AppConstants.CONTEXT_URL+"/authoritiesHistory");
        excludedURLs.add(AppConstants.CONTEXT_URL+"/manuscript/getManuscripDataForReviwer");
        excludedURLs.add(AppConstants.CONTEXT_URL+"/journal/list");
        excludedURLs.add(AppConstants.CONTEXT_URL+"/manuscript/getManuscriptDataByManuscriptId");
        excludedURLs.add(AppConstants.CONTEXT_URL+"/manuScriptCoAuthor/updateCopyright");
        excludedURLs.add(AppConstants.CONTEXT_URL+"/journal/getById");
        excludedURLs.add(AppConstants.CONTEXT_URL+"/user/userActivation");
        excludedURLs.add(AppConstants.CONTEXT_URL+"/countries/getStatesByCoutryId");
        excludedURLs.add(AppConstants.CONTEXT_URL+"/manuScriptReviewer/getReviewManuscriptDashboardCount");
        excludedURLs.add(AppConstants.CONTEXT_URL+"/dashboard/getManuscriptDashboardCountForAuthor");
        excludedURLs.add(AppConstants.CONTEXT_URL+"/manuscriptTypeJournal/getManuscriptCharge");
        excludedURLs.add(AppConstants.CONTEXT_URL+"/getManuscriptByactive");
        excludedURLs.add(AppConstants.CONTEXT_URL+"/tokenExpired/checkIfExpired");
        excludedURLs.add(AppConstants.CONTEXT_URL+"/manuScript/updateManuscriptStatus");
        excludedURLs.add(AppConstants.CONTEXT_URL+"/manuScript/updateManuscriptDetails");
        excludedURLs.add(AppConstants.CONTEXT_URL+"/manuscript/withdrwalUnitouchToUniprr");
        excludedURLs.add(AppConstants.CONTEXT_URL+"/journal/updateJournalActivationForUnitouch");
        excludedURLs.add(AppConstants.CONTEXT_URL+"/manuscript/updateManuScriptEditor");
        excludedURLs.add(AppConstants.CONTEXT_URL+"/journal/updateManuscriptIdByUnitouch");
        excludedURLs.add(AppConstants.CONTEXT_URL+"/manuScript/insertDataThroughUnitouch");
        excludedURLs.add(AppConstants.CONTEXT_URL+"/manuScript/updateManuscriptDetails");
        excludedURLs.add(AppConstants.CONTEXT_URL+"/manuScript/updateManuscriptDetailsWithoutEmail");
        excludedURLs.add(AppConstants.CONTEXT_URL+"/manuScript/bulkUpdateManuscriptDetails");
        excludedURLs.add(AppConstants.CONTEXT_URL+"/manuscript/getManuscriptStatus");
        excludedURLs.add(AppConstants.CONTEXT_URL+"/orcid/rolesByOrcid");
        excludedURLs.add(AppConstants.CONTEXT_URL+"/mailQueue/getMailQueue");
        excludedURLs.add(AppConstants.CONTEXT_URL+"/journalconfig/getOperationalPolices");
        excludedURLs.add(AppConstants.CONTEXT_URL+"/letterToEditor/getAuthorResponseList");
        excludedURLs.add(AppConstants.CONTEXT_URL+"/commentary/getCommentary");
        excludedURLs.add(AppConstants.CONTEXT_URL+"/manuScriptReviewer/updateManuScriptReviewer");
        excludedURLs.add(AppConstants.CONTEXT_URL+"/user/updateOrcid");
        excludedURLs.add(AppConstants.CONTEXT_URL+"/user/mapUserByJournal");
        excludedURLs.add(AppConstants.CONTEXT_URL+"/orcid/orcidDetail");
        excludedURLs.add(AppConstants.CONTEXT_URL+"/user/authorRegistrationMailSend");
        excludedURLs.add(AppConstants.CONTEXT_URL+"/specialization/searchjournal");
        excludedURLs.add(AppConstants.CONTEXT_URL+"/getManuscriptSyncData");
        excludedURLs.add(AppConstants.CONTEXT_URL+"/api/message-from-partners");
        excludedURLs.add(AppConstants.CONTEXT_URL+"/api/message-from-partners-uk");
        excludedURLs.add(AppConstants.CONTEXT_URL+"/api/impact-uk-task-closure");
        excludedURLs.add(AppConstants.CONTEXT_URL+"/api/version");
        excludedURLs.add(AppConstants.CONTEXT_URL+"/api/report/data");
        excludedURLs.add(AppConstants.CONTEXT_URL+"/Publons/generateReviewCommentReport");
//        excludedURLs.add("/tokenExpired/getExpirationMessage");
//        excludedURLs.add("/goLive");
//        excludedURLs.add("/journal/journalList");
//        excludedURLs.add("/journal/getByJournalCode");
//        excludedURLs.add("/announcement/getList");
//        excludedURLs.add("/getDataFromMedKnow/getIndex");
//        excludedURLs.add("/getDataFromMedKnow/getImage");
//        excludedURLs.add("/contactUs");
//        excludedURLs.add("/subject/getSubjectList");
//        excludedURLs.add("/countries/list");
//        excludedURLs.add("/user/create");
//        excludedURLs.add("/forgotPassword");
//        excludedURLs.add("/userLoginStatus");
//        excludedURLs.add("/userRole/isAcceessRole");
//        excludedURLs.add("/user/getUserCountOnAdmin");
//        excludedURLs.add("/journal/getJournalAdmin");
//        excludedURLs.add("/user/userVerificationUpdationPassword");
//        excludedURLs.add("/user/checkUserByJournal");
//        excludedURLs.add("/reviewerSuggested/createDecline");
//        excludedURLs.add("/authoritiesHistory");
//        excludedURLs.add("/authoritiesHistory");
//        excludedURLs.add("/manuscript/getManuscripDataForReviwer");
//        excludedURLs.add("/journal/list");
//        excludedURLs.add("/manuscript/getManuscriptDataByManuscriptId");
//        excludedURLs.add("/manuScriptCoAuthor/updateCopyright");
//        excludedURLs.add("/journal/getById");
//        excludedURLs.add("/user/userActivation");
//        excludedURLs.add("/countries/getStatesByCoutryId");
//        excludedURLs.add("/manuScriptReviewer/getReviewManuscriptDashboardCount");
//        excludedURLs.add("/dashboard/getManuscriptDashboardCountForAuthor");
//        excludedURLs.add("/manuscriptTypeJournal/getManuscriptCharge");
//        excludedURLs.add("/getManuscriptByactive");
//        excludedURLs.add("/tokenExpired/checkIfExpired");
//        excludedURLs.add("/manuScript/updateManuscriptStatus");
//        excludedURLs.add("/manuScript/updateManuscriptDetails");
//        excludedURLs.add("/manuscript/withdrwalUnitouchToUniprr");
//        excludedURLs.add("/journal/updateJournalActivationForUnitouch");
//        excludedURLs.add("/manuscript/updateManuScriptEditor");
//        excludedURLs.add("/journal/updateManuscriptIdByUnitouch");
//        excludedURLs.add("/manuScript/insertDataThroughUnitouch");
//        excludedURLs.add("/manuScript/updateManuscriptDetails");
//        excludedURLs.add("/manuScript/updateManuscriptDetailsWithoutEmail");
//        excludedURLs.add("/manuScript/bulkUpdateManuscriptDetails");
        for(String s : excludedURLs)
        {
            if(request.getRequestURI().contains(s))
            {
                return true;
            }
        }
//        return excludedURLs.stream()
//                .anyMatch(p -> p.equals(request.getRequestURI()));
        return false;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // Handle preflight requests
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setHeader("Access-Control-Allow-Origin","*");
            response.setHeader("Access-Control-Allow-Methods", "*");
            response.setHeader("Access-Control-Allow-Headers", "*");
            response.getWriter().println("OK");
            return;
        }
        // Logging Request
        List<String> requestParams = new ArrayList<>();
        request.getParameterMap().forEach((paramName, paramValues) -> {
            requestParams.add(paramName + ": " + String.join(", ", paramValues));
        });
        List<String> requestContent = new ArrayList<>();
        StringBuilder content = new StringBuilder();
        String requestBody = null;
        try  {
            BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                requestContent.add(line);
                content.append(line);
            }
            requestBody=content.toString();
            reader.close();
        } catch (IOException e) {
            log.info(e.getMessage());
        }
        if (requestBody != null) {
            try {
                request = new UniprrHttpServletRequestWrapper(request, requestBody);
            } catch (IOException e) {
                log.info(e.getMessage());
            }
        }
        log.info(AppConstants.REQUEST_METHOD+" :: "+request.getMethod() +" "+
                AppConstants.REQUEST_URI+" :: "+request.getRequestURI() +" "+
                AppConstants.REQUEST_PARAMS+" :: "+requestParams +" "+
                AppConstants.REQUEST_BODY+" :: "+requestContent);
        if (!request.getRequestURI().contains("/loginAuthenticate")
                && !request.getRequestURI().contains("/getOrcidID")
                && request.getHeader(AppConstants.AUTHORIZATION) != null
                && !request.getHeader(AppConstants.AUTHORIZATION).isEmpty()) {
//            HttpPost httpRequest = new HttpPost(AppConstants.VALIDATION_URL);
//            httpRequest.setHeader(AppConstants.CONTENT_TYPE, AppConstants.APPLICATION_JSON);
//            httpRequest.setHeader(AppConstants.AUTHORIZATION, request.getHeader(AppConstants.AUTHORIZATION));
//            String payload = "{\"username\":\"\",\"password\":\"\",\"role\":\"\"}";
//            httpRequest.setEntity(new StringEntity(payload));
//            HttpResponse httpResponse = HttpClientGenerator.getInstance().execute(httpRequest);
//            HttpEntity httpEntity = httpResponse.getEntity();
//            String jsonResponse = EntityUtils.toString(httpEntity);
//            int statusCode = Integer.parseInt(String.valueOf(new JSONObject(jsonResponse).get("statusCode")));
            int statusCode= tokenExpireService.checkIfExpiredInDB(request.getHeader(AppConstants.AUTHORIZATION));
            if (statusCode == 100) {
                filterChain.doFilter(request, response);
            } else {
                response.sendRedirect(AppConstants.CONTEXT_URL+"/tokenExpired/getExpirationMessage");
            }
        } else if (request.getRequestURI().contains("/loginAuthenticate")
                || request.getRequestURI().contains("/getOrcidID")) {
            filterChain.doFilter(request, response);
        } else {
            response.sendRedirect(AppConstants.CONTEXT_URL+"/tokenExpired/getExpirationMessage");
        }
    }
}