package com.digi.uniprr.controller;

import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.digi.uniprr.model.ImpactMessageLogs;
import com.digi.uniprr.model.ImpactMessageRequest;
import com.digi.uniprr.model.ImpactMessageResponse;
import com.digi.uniprr.model.ImpactMessageResponseUk;
import com.digi.uniprr.model.ImpactRequest;
import com.digi.uniprr.model.UkTaskClosure;
import com.digi.uniprr.model.UkTaskClosureRequest;
import com.digi.uniprr.model.UkTaskClosureResponse;
import com.digi.uniprr.repository.ImpactMessageLogRepository;
import com.digi.uniprr.repository.ImpactRequestRepository;
import com.digi.uniprr.repository.UkTaskClosureRepo;
import com.digi.uniprr.utils.UUIDTokenUtil;

@RestController
@RequestMapping("/api")
public class ImpactMessageController {

	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private ImpactMessageLogRepository impactMessageLogRepository;
	
    @Autowired
    private UUIDTokenUtil uuidTokenUtil; 
    
    @Autowired
    private ImpactRequestRepository impactRequestRepository;
    
    @Autowired
    private UkTaskClosureRepo ukTaskClouserRepo;
	
    @PostMapping("message-from-partners")
    public ResponseEntity<?> receiveMessage(@RequestHeader("Authorization") String authorization,@RequestBody ImpactMessageRequest request) {
        
    	String token = authorization.replace("Bearer ", "");
        if (!uuidTokenUtil.isValidToken(token)) { // Use the injected instance
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ConflictResponse(401, "Invalid Token"));
        }
    	
        if (impactMessageLogRepository.existsByDoi(request.getDoi())) {
            // If DOI already exists, respond with a 409 Conflict status
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new  ConflictResponse(409, "DOI already exists"));
        }
    	String receiptId = UUID.randomUUID().toString();
        int status;
        String message;
        
        log.info("Received message from partner: {}", request.getPartner());
        log.info("Event: {}, DOI: {}", request.getEvent(), request.getDoi());

        // Validate DOI format (for example, check if it starts with "10.")
        if (request.getDoi() == null || !request.getDoi().startsWith("10.")) {
            status = HttpStatus.INTERNAL_SERVER_ERROR.value();
            message = "Invalid DOI";
            log.warn("Invalid DOI format for DOI: {}", request.getDoi());
        } else {
            // Assume success if DOI format is valid
            status = HttpStatus.OK.value();
            message = "Received successfully";
            log	.info("DOI is valid: {}", request.getDoi());
        }

        // Save to database
        ImpactMessageLogs logEntry = new ImpactMessageLogs(
                request.getPartner(),
                request.getEvent(),
                request.getDoi(),
                request.getAuthor(),
                request.getEditor(),
                status,
                message,
                receiptId
        );
        impactMessageLogRepository.save(logEntry);
        
        log.info("Message logged in ImpactMessageLogs with receipt ID: {}", receiptId);

        // Create response object
        ImpactMessageResponse response = new ImpactMessageResponse(
                status,
                message,
                receiptId,
                request.getDoi()
        );

        log.info("Responding with status: {}, message: {}, receiptId: {}", status, message, receiptId);
        return new ResponseEntity<>(response, status == 200 ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR);
    }
      
    @PostMapping("/message-from-partners-uk")
   public ResponseEntity<ImpactMessageResponseUk> receiveMessageFromUK(
           @RequestHeader("Authorization") String authorization,
           @RequestBody ImpactRequest request) {

       String receiptId = UUID.randomUUID().toString();
       String doi = request.getDoi();

       try {
           String token = Optional.ofNullable(authorization)
                   .map(auth -> auth.replace("Bearer ", ""))
                   .orElse("");

           if (!uuidTokenUtil.isValidUkToken(token)) {
               log.warn("Unauthorized access attempt with token: {}", token);
               return new ResponseEntity<>(
                       new ImpactMessageResponseUk(401, "FAILURE", receiptId, doi),
                       HttpStatus.UNAUTHORIZED);
           }

           if (doi == null || doi.trim().isEmpty()) {
               log.warn("Bad Request - DOI is null or empty");
               return new ResponseEntity<>(
                       new ImpactMessageResponseUk(400, "FAILURE", receiptId, doi),
                       HttpStatus.BAD_REQUEST);
           }

           if (impactRequestRepository.existsByDoi(doi)) {
               log.warn("Duplicate DOI received: {}", doi);
               return new ResponseEntity<>(
                       new ImpactMessageResponseUk(409, "Duplicate DOI received", receiptId, doi),
                       HttpStatus.CONFLICT);
           }

           log.info("Received message from partner '{}', event '{}', DOI '{}'", 
                     request.getPartner(), request.getEvent(), doi);

           if (request.getUrls() != null) {
               request.getUrls().forEach(url -> url.setImpactRequest(request));
           }

           impactRequestRepository.save(request);
           log.info("ImpactRequest saved successfully with Receipt ID: {}", receiptId);

           ImpactMessageResponseUk response = new ImpactMessageResponseUk(
                   200,
                   "SUCCESS",
                   receiptId,
                   doi,
                   request.getUrls(),
                   request.getFileName()
           );
           return ResponseEntity.ok(response);

       } catch (Exception ex) {
    	   log.error("Internal Server Error while processing message from UK: {}", ex.getMessage(), ex);
           return new ResponseEntity<>(
                   new ImpactMessageResponseUk(500, "FAILURE", receiptId, doi),
                   HttpStatus.INTERNAL_SERVER_ERROR
           );
       }
   }
    
    @PostMapping("impact-uk-task-closure")
    public ResponseEntity<UkTaskClosureResponse> impactTaskClosure(
            @RequestHeader("Authorization") String authorization,
            @RequestBody UkTaskClosureRequest request) {

        String receiptId = UUID.randomUUID().toString();
        String doi = request.getDoi();
        String role = request.getRole();

        try {
            String token = Optional.ofNullable(authorization)
                    .map(auth -> auth.replace("Bearer ", ""))
                    .orElse("");

            if (!uuidTokenUtil.isValidUkToken(token)) {
                log.warn("Unauthorized access attempt with token: {}", token);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new UkTaskClosureResponse(401, "FAILURE", role, receiptId, doi));
            }

            if (doi == null || doi.trim().isEmpty()) {
                log.warn("Invalid DOI format: {}", doi);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new UkTaskClosureResponse(400, "FAILURE", role, receiptId, doi));
            }

            if (ukTaskClouserRepo.existsByDoi(doi)) {
                log.warn("Duplicate DOI received: {}", doi);
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new UkTaskClosureResponse(409, "FAILURE", role, receiptId, doi));
            }

            UkTaskClosure ukTaskClouser = new UkTaskClosure();
            ukTaskClouser.setDoi(doi);
            ukTaskClouser.setEvent(request.getEvent());
            ukTaskClouser.setRole(role);

            ukTaskClouserRepo.save(ukTaskClouser);
            log.info("UkTaskClosure entry saved successfully with Receipt ID: {}", receiptId);

            UkTaskClosureResponse response = new UkTaskClosureResponse(200, "SUCCESS", role, receiptId, doi);
            return ResponseEntity.ok(response);

        } catch (Exception ex) {
            log.error("Error occurred in impactTaskClosure: {}", ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new UkTaskClosureResponse(500, "FAILURE", role, receiptId, doi));
        }
    }

 
    private static class ConflictResponse {
        private int status;
        private String message;

        public ConflictResponse(int status, String message) {
            this.status = status;
            this.message = message;
        }

        // Getters (no setters needed for this simple response)
        public int getStatus() { return status; }
        public String getMessage() { return message; }
    }
}
