package com.digi.uniprr.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.digi.uniprr.service.AutoWithdrawalSchedulerService;

@RestController
@RequestMapping("/api/auto-withdrawal")
public class AutoWithdrawalSchedulerController {

    @Autowired
    private AutoWithdrawalSchedulerService autoWithdrawalService;

    /**
     * Manual trigger API for testing the Auto Withdrawal Scheduler.
     * You can call this from Postman or browser:
     * 👉 GET http://localhost:8080/api/auto-withdrawal/run
     */
    @GetMapping("/run")
    public ResponseEntity<String> runAutoWithdrawalScheduler() {
        String result = autoWithdrawalService.processAutoWithdrawalReminders();
        return ResponseEntity.ok(result);
    }
}
