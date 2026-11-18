package com.digi.uniprr.controller;

import com.digi.uniprr.dto.ResendCopyrightRequest;
import com.digi.uniprr.model.ManuScriptCoAuthor;
import com.digi.uniprr.repository.ManuScriptCoAuthorRepo;
import com.digi.uniprr.service.CopyrightService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/copyright")
@Slf4j
public class CopyrightController {

    @Autowired
    private CopyrightService copyrightService;
    
    @Autowired
    private ManuScriptCoAuthorRepo coAuthorRepo;

    @PostMapping("/trigger")
    public ResponseEntity<Map<String, Object>> triggerCopyrightEmails(
            @RequestParam Integer manuscriptId,
            @RequestParam Integer journalId,
            @RequestBody List<ManuScriptCoAuthor> authorsList) {

        Map<String, Object> response = new HashMap<>();
        try {
        	boolean flag =true;
            String result = copyrightService.queueCopyrightEmails(manuscriptId, journalId, authorsList,flag);
            response.put("message", result);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("❌ Error triggering copyright emails", e);
            response.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    @GetMapping("/status/{manuscriptId}")
    public ResponseEntity<Map<String, Object>> checkCopyrightStatus(@PathVariable Integer manuscriptId) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<ManuScriptCoAuthor> coAuthors = coAuthorRepo.findByCoAuthorManuscriptId(manuscriptId);

            if (coAuthors == null || coAuthors.isEmpty()) {
                response.put("message", "No co-authors found for this manuscript.");
                response.put("allApproved", true);
                return ResponseEntity.ok(response);
            }

            long totalCoAuthors = coAuthors.stream()
                    .filter(a -> a.getIsCorrespondingAuthor().equals("N") || a.getIsCorrespondingAuthor().isEmpty() || a.getIsCorrespondingAuthor().equals(null) || a.getIsCorrespondingAuthor().equals("")) // 1 = co-author
                    .count();

            long approvedCount = coAuthors.stream()
                    .filter(a -> a.getIsCorrespondingAuthor() != null && a.getIsCorrespondingAuthor().equals("N"))
                    .filter(a -> a.getCopyrightAgreement() != null && a.getCopyrightAgreement().equals("Yes"))
                    .count();

            boolean allApproved = totalCoAuthors == 0 || totalCoAuthors == approvedCount;

            response.put("totalCoAuthors", totalCoAuthors);
            response.put("approvedCount", approvedCount);
            response.put("allApproved", allApproved);

            if (allApproved) {
                response.put("message", "✅ All co-authors have approved the copyright form.");
            } else {
                response.put("message", "⏳ Waiting for some co-authors to approve the copyright form.");
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("❌ Error checking copyright approval status for manuscriptId={}", manuscriptId, e);
            response.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    @PostMapping("/resend")
    public ResponseEntity<?> resendCopyrightLink(@RequestBody ResendCopyrightRequest request) {
        String message = copyrightService.resendCopyrightLink(
            request.getManuscriptId(),
            request.getJournalId(),
            request.getCoAuthorId()
        );
        return ResponseEntity.ok(Collections.singletonMap("message", message));
    }

}
