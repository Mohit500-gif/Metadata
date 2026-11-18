package com.digi.uniprr.service;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.digi.uniprr.VO.AuthorForReportDTO;
import com.digi.uniprr.VO.ManuscriptReportDTO;
import com.digi.uniprr.VO.ManuscriptTimelineDTO;
import com.digi.uniprr.VO.ReviewerForReportDTO;
import com.digi.uniprr.VO.StageTimelineDTO;
import com.digi.uniprr.repository.ManuScriptRepo;

@Service
public class ManuscriptReportService {

    private static final Logger log =
            LoggerFactory.getLogger(ManuscriptReportService.class);

    private static final String DATE_FORMAT = "dd MMM yyyy";
    private static final String INPUT_DATE_FORMAT = "yyyy-MM-dd";

    @Autowired
    private ManuScriptRepo manuScriptRepo;


    @Transactional(readOnly = true)
    public Page<ManuscriptReportDTO> getManuscriptReport(
            String startDate,
            String endDate,
            Pageable pageable) {

        validateRequest(startDate, endDate, pageable);

        Page<Object> manuscriptIdPage =
                manuScriptRepo.findManuscriptIds(
                        startDate, endDate, pageable);

        if (manuscriptIdPage.isEmpty()) {
            return new PageImpl<>(
                    Collections.emptyList(),
                    pageable,
                    manuscriptIdPage.getTotalElements());
        }

        Set<Long> uniqueIds = new LinkedHashSet<>();

        for (Object rawId : manuscriptIdPage.getContent()) {
            Long id = toLong(rawId);

            if (id != null) {
                uniqueIds.add(id);
            } else {
                log.warn(
                        "Ignoring invalid manuscript ID returned by query: {}",
                        rawId);
            }
        }

        List<Long> ids = new ArrayList<>(uniqueIds);

        if (ids.isEmpty()) {
            return new PageImpl<>(
                    Collections.emptyList(),
                    pageable,
                    manuscriptIdPage.getTotalElements());
        }

        Map<Long, ManuscriptReportDTO> reportMap =
                buildBaseMap(manuScriptRepo.findBaseData(ids));

        buildAuthorMap(
                manuScriptRepo.findAuthors(ids),
                reportMap);

        buildReviewerMap(
                manuScriptRepo.findReviewers(ids),
                reportMap);

        buildTimelineMap(
                manuScriptRepo.findTimeline(ids),
                reportMap);

        /*
         * Preserve the order returned by the paginated ID query.
         */
        List<ManuscriptReportDTO> result = new ArrayList<>();

        for (Long id : ids) {
            ManuscriptReportDTO dto = reportMap.get(id);

            if (dto == null) {
                continue;
            }

            if (dto.getAuthors() == null) {
                dto.setAuthors(new ArrayList<>());
            }

            if (dto.getReviewers() == null) {
                dto.setReviewers(new ArrayList<>());
            }

            if (dto.getTimeline() == null) {
                dto.setTimeline(new ManuscriptTimelineDTO());
            }

            result.add(dto);
        }

        log.debug(
                "Manuscript report generated. Requested IDs={}, returned={}, total={}",
                ids.size(),
                result.size(),
                manuscriptIdPage.getTotalElements());

        return new PageImpl<>(
                result,
                pageable,
                manuscriptIdPage.getTotalElements());
    }

    private void validateRequest(
            String startDate,
            String endDate,
            Pageable pageable) {

        if (!StringUtils.hasText(startDate)
                || !StringUtils.hasText(endDate)) {
            throw new IllegalArgumentException(
                    "Start date and end date are required.");
        }

        if (pageable == null) {
            throw new IllegalArgumentException(
                    "Pageable must not be null.");
        }

        Date start = parseInputDate(startDate);
        Date end = parseInputDate(endDate);

        if (start.after(end)) {
            throw new IllegalArgumentException(
                    "Start date must not be after end date.");
        }
    }

    private Date parseInputDate(String value) {
        SimpleDateFormat sdf =
                new SimpleDateFormat(INPUT_DATE_FORMAT, Locale.ENGLISH);

        sdf.setLenient(false);
        sdf.setTimeZone(TimeZone.getDefault());

        try {
            Date parsedDate = sdf.parse(value);

            // Reject values that contain trailing characters.
            if (!sdf.format(parsedDate).equals(value)) {
                throw new IllegalArgumentException(
                        "Invalid date. Expected yyyy-MM-dd: " + value);
            }

            return parsedDate;
        } catch (ParseException ex) {
            throw new IllegalArgumentException(
                    "Invalid date. Expected yyyy-MM-dd: " + value,
                    ex);
        }
    }

    private Map<Long, ManuscriptReportDTO> buildBaseMap(
            List<Object[]> rows) {

        Map<Long, ManuscriptReportDTO> map = new LinkedHashMap<>();

        if (rows == null || rows.isEmpty()) {
            return map;
        }

        for (Object[] row : rows) {

            // 18 columns required: indexes 0 to 17.
            if (row == null || row.length < 18) {
                log.warn(
                        "Skipping invalid manuscript base row. Column count={}",
                        row == null ? 0 : row.length);
                continue;
            }

            Long manuscriptId = toLong(row[0]);

            if (manuscriptId == null) {
                log.warn("Skipping report row with null manuscript ID.");
                continue;
            }

            if (map.containsKey(manuscriptId)) {
                log.warn(
                        "Duplicate base data returned for manuscript ID={}",
                        manuscriptId);
                continue;
            }

            ManuscriptReportDTO dto = new ManuscriptReportDTO();

            dto.setManuscriptId(toInteger(row[0]));
            dto.setManuscriptCode(toStringValue(row[1]));
            dto.setManuscriptTitle(toStringValue(row[2]));
            dto.setDoi(toStringValue(row[3]));
            dto.setSubmissionDate(formatDate(row[4]));

            // Column 5: production_date.
            // Column 6: publishing_date.
            String articleStatus = toStringValue(row[15]);

            dto.setArticleStatus(articleStatus);

            // Do not show publication date for rejected articles.
            if (isRejectedStatus(articleStatus)) {
                dto.setPublished(null);
            } else {
                dto.setPublished(formatDate(row[6]));
            }

            dto.setJournalTitle(toStringValue(row[12]));
            dto.setManuscriptType(toStringValue(row[13]));
            dto.setInstitutions(toStringValue(row[14]));

            /*
             * Column 16: country.
             */
            dto.setCountry(toStringValue(row[16]));

            // Editor details: columns 8 to 11.
            dto.setEditorName(toStringValue(row[8]));
            dto.setEditorEmail(toStringValue(row[9]));
            dto.setEditorId(toInteger(row[10]));
            dto.setEditorNo(toStringValue(row[11]));

            // Column 17: rejection date from manuscript table.
            dto.setRejectedDate(formatDate(row[17]));

            dto.setEditorialReview(null);
            dto.setInviteReviewer(null);
            dto.setAcceptReviewInvitation(null);
            dto.setReviewedManuscript(null);
            dto.setRevision(null);
            dto.setFinalDecision(null);
            dto.setAcceptedRejected(null);

            dto.setAuthors(new ArrayList<>());
            dto.setReviewers(new ArrayList<>());
            dto.setTimeline(new ManuscriptTimelineDTO());

            map.put(manuscriptId, dto);
        }

        return map;
    }

    private boolean isRejectedStatus(String status) {
        return status != null
                && "rejected".equalsIgnoreCase(status.trim());
    }

    private void buildAuthorMap(
            List<Object[]> rows,
            Map<Long, ManuscriptReportDTO> reportMap) {

        if (rows == null || rows.isEmpty()) {
            return;
        }

        Map<Long, List<AuthorForReportDTO>> authorMap =
                new LinkedHashMap<>();

        Map<Long, Set<String>> institutionMap =
                new LinkedHashMap<>();

        for (Object[] row : rows) {
            if (row == null || row.length < 7) {
                log.warn("Skipping invalid author report row.");
                continue;
            }

            Long manuscriptId = toLong(row[0]);

            if (manuscriptId == null
                    || !reportMap.containsKey(manuscriptId)) {
                continue;
            }

            AuthorForReportDTO author = new AuthorForReportDTO();

            author.setAuthorOrder(toInteger(row[1]));
            author.setAuthorId(toInteger(row[2]));
            author.setAuthorName(normalizeName(toStringValue(row[3])));
            author.setAuthorEmail(toStringValue(row[4]));
            author.setAuthorMobile(toStringValue(row[5]));
            author.setCorresponding(toBoolean(row[6]));

            if (row.length > 7) {
                String institution =
                        normalizeName(toStringValue(row[7]));

                author.setInstitution(institution);

                if (StringUtils.hasText(institution)) {
                    institutionMap
                            .computeIfAbsent(
                                    manuscriptId,
                                    key -> new LinkedHashSet<>())
                            .add(institution);
                }
            }

            if (row.length > 8) {
                author.setCountry(toStringValue(row[8]));
            }

            authorMap
                    .computeIfAbsent(
                            manuscriptId,
                            key -> new ArrayList<>())
                    .add(author);
        }

        for (Map.Entry<Long, List<AuthorForReportDTO>> entry
                : authorMap.entrySet()) {

            ManuscriptReportDTO dto = reportMap.get(entry.getKey());

            if (dto != null) {
                dto.setAuthors(entry.getValue());
            }
        }

        for (Map.Entry<Long, Set<String>> entry
                : institutionMap.entrySet()) {

            ManuscriptReportDTO dto = reportMap.get(entry.getKey());

            if (dto != null && !entry.getValue().isEmpty()) {
                dto.setInstitutions(
                        String.join(", ", entry.getValue()));
            }
        }
    }

    private void buildReviewerMap(
            List<Object[]> rows,
            Map<Long, ManuscriptReportDTO> reportMap) {

        if (rows == null || rows.isEmpty()) {
            return;
        }

        Map<Long, List<ReviewerForReportDTO>> reviewerMap =
                new LinkedHashMap<>();

        for (Object[] row : rows) {
            if (row == null || row.length < 5) {
                log.warn("Skipping invalid reviewer report row.");
                continue;
            }

            Long manuscriptId = toLong(row[0]);

            if (manuscriptId == null
                    || !reportMap.containsKey(manuscriptId)) {
                continue;
            }

            ReviewerForReportDTO reviewer =
                    new ReviewerForReportDTO();

            reviewer.setReviewer_id(toStringValue(row[1]));
            reviewer.setReviewer_name(
                    normalizeName(toStringValue(row[2])));
            reviewer.setReviewer_email(toStringValue(row[3]));
            reviewer.setReviewer_mobile_no(toStringValue(row[4]));

            if (row.length > 5) {
                reviewer.setInstitution(
                        normalizeName(toStringValue(row[5])));
            }

            if (row.length > 6) {
                reviewer.setCountry(toStringValue(row[6]));
            }

            reviewerMap
                    .computeIfAbsent(
                            manuscriptId,
                            key -> new ArrayList<>())
                    .add(reviewer);
        }

        for (Map.Entry<Long, List<ReviewerForReportDTO>> entry
                : reviewerMap.entrySet()) {

            ManuscriptReportDTO dto = reportMap.get(entry.getKey());

            if (dto != null) {
                dto.setReviewers(entry.getValue());
            }
        }
    }

    private void buildTimelineMap(
            List<Object[]> rows,
            Map<Long, ManuscriptReportDTO> reportMap) {

        if (rows == null || rows.isEmpty()) {
            return;
        }

        for (Object[] row : rows) {
            if (row == null || row.length < 7) {
                log.warn("Skipping invalid timeline report row.");
                continue;
            }

            Long manuscriptId = toLong(row[0]);

            if (manuscriptId == null) {
                continue;
            }

            ManuscriptReportDTO dto = reportMap.get(manuscriptId);

            if (dto == null) {
                continue;
            }

            ManuscriptTimelineDTO timeline = dto.getTimeline();

            if (timeline == null) {
                timeline = new ManuscriptTimelineDTO();
            }

            timeline.setSubmission(
                    stage(dto.getSubmissionDate(), null));

            timeline.setEditorialReview(
                    stage(formatDate(row[1]), null));

            timeline.setInviteReviewer(
                    stage(formatDate(row[2]), null));

            timeline.setAcceptReviewInvitation(
                    stage(formatDate(row[3]), null));

            timeline.setReviewedManuscript(
                    stage(formatDate(row[4]), null));

            timeline.setRevisions(
                    stage(formatDate(row[5]), null));

            timeline.setFinalDecision(
                    stage(formatDate(row[6]), null));

            /*
             * Do not infer acceptance/rejection from the final-decision
             * timestamp. These need their actual status/date source.
             */
            timeline.setAcceptedRejected(
                    stage(dto.getAcceptedRejected(), null));

            timeline.setPublished(
                    stage(dto.getPublished(), null));

            dto.setTimeline(timeline);

            dto.setEditorialReview(formatDate(row[1]));
            dto.setInviteReviewer(formatDate(row[2]));
            dto.setAcceptReviewInvitation(formatDate(row[3]));
            dto.setReviewedManuscript(formatDate(row[4]));
            dto.setRevision(formatDate(row[5]));
            dto.setFinalDecision(formatDate(row[6]));
        }
    }

    private StageTimelineDTO stage(
            String startDate,
            String endDate) {

        StageTimelineDTO stage = new StageTimelineDTO();
        stage.setStage_startDate(startDate);
        stage.setStage_endDate(endDate);
        return stage;
    }

    private String normalizeName(String value) {
        if (!StringUtils.hasText(value)) {
            return value;
        }

        return value.trim().replaceAll("\\s+", " ");
    }

    private String toStringValue(Object value) {
        if (value == null) {
            return null;
        }

        String result = String.valueOf(value).trim();
        return result.isEmpty() ? null : result;
    }

    private Long toLong(Object value) {
        if (value == null) {
            return null;
        }

        try {
            if (value instanceof Number) {
                return ((Number) value).longValue();
            }

            String text = value.toString().trim();

            if (text.isEmpty()) {
                return null;
            }

            return Long.valueOf(text);
        } catch (NumberFormatException ex) {
            log.warn(
                    "Unable to convert value to Long. Value={}, type={}",
                    value,
                    value.getClass().getName());
            return null;
        }
    }

    private Integer toInteger(Object value) {
        if (value == null) {
            return null;
        }

        try {
            if (value instanceof Number) {
                return ((Number) value).intValue();
            }

            String text = value.toString().trim();

            if (text.isEmpty()) {
                return null;
            }

            return Integer.valueOf(text);
        } catch (NumberFormatException ex) {
            log.warn(
                    "Unable to convert value to Integer. Value={}, type={}",
                    value,
                    value.getClass().getName());
            return null;
        }
    }

    private Boolean toBoolean(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof Boolean) {
            return (Boolean) value;
        }

        if (value instanceof Number) {
            return ((Number) value).intValue() != 0;
        }

        String normalized =
                value.toString().trim().toLowerCase(Locale.ENGLISH);

        if ("true".equals(normalized)
                || "1".equals(normalized)
                || "y".equals(normalized)
                || "yes".equals(normalized)) {
            return Boolean.TRUE;
        }

        if ("false".equals(normalized)
                || "0".equals(normalized)
                || "n".equals(normalized)
                || "no".equals(normalized)) {
            return Boolean.FALSE;
        }

        return null;
    }

    private String formatDate(Object value) {
        if (value == null) {
            return null;
        }

        Date date = null;

        if (value instanceof Timestamp) {
            date = new Date(((Timestamp) value).getTime());
        } else if (value instanceof java.sql.Date) {
            date = new Date(((java.sql.Date) value).getTime());
        } else if (value instanceof Date) {
            date = (Date) value;
        } else if (value instanceof java.time.LocalDate) {
            date = java.sql.Date.valueOf(
                    (java.time.LocalDate) value);
        } else if (value instanceof java.time.LocalDateTime) {
            date = Timestamp.valueOf(
                    (java.time.LocalDateTime) value);
        } else if (value instanceof CharSequence) {
            String text = value.toString().trim();

            if (text.isEmpty()) {
                return null;
            }

            String[] patterns = {
                    "yyyy-MM-dd HH:mm:ss.SSS",
                    "yyyy-MM-dd HH:mm:ss",
                    "yyyy-MM-dd",
                    "dd MMM yyyy"
            };

            for (String pattern : patterns) {
                SimpleDateFormat sdf =
                        new SimpleDateFormat(pattern, Locale.ENGLISH);

                sdf.setLenient(false);

                try {
                    date = sdf.parse(text);

                    if (sdf.format(date).equals(text)) {
                        break;
                    }

                    date = null;
                } catch (ParseException ignored) {
                    // Try the next supported database date format.
                }
            }

            if (date == null) {
                log.warn("Unable to parse report date: {}", text);
                return text;
            }
        }

        if (date == null) {
            return null;
        }

        SimpleDateFormat output =
                new SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH);

        output.setTimeZone(TimeZone.getDefault());

        return output.format(date);
    }
}