package com.digi.uniprr.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.digi.uniprr.dto.*;
import com.digi.uniprr.service.EmailChangeService;

@RestController
@RequestMapping("/api/email-change")
public class EmailChangeController {

    private final EmailChangeService service;

    public EmailChangeController(EmailChangeService service) {
        this.service = service;
    }

    @GetMapping("/search")
    public ResponseEntity<SearchResultDTO> search(@RequestParam String oldEmail) {
        return ResponseEntity.ok(service.searchByOldEmail(oldEmail));
    }

    @PostMapping("/submit")
    public ResponseEntity<ApiResponse> submit(@Validated @RequestBody EmailChangeRequest req,
                                              @RequestHeader(value = "X-Admin-UserId", required = false) Integer adminUserId) {
        try {
            String msg = service.processEmailChange(req, adminUserId);
            return ResponseEntity.ok(new ApiResponse(true, msg));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, ex.getMessage()));
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.internalServerError().body(new ApiResponse(false, "Unexpected error: Failed to change the email."));
        }
    }
}
