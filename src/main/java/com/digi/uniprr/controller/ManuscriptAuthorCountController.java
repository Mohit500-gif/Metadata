package com.digi.uniprr.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.digi.uniprr.dto.ManuscriptAuthorCountDto;
import com.digi.uniprr.service.ManuscriptAuthorCountService;

@RestController
@RequestMapping("/coauthor/count")
public class ManuscriptAuthorCountController {

    @Autowired
    private ManuscriptAuthorCountService service;


    @PostMapping("/save")
    public ResponseEntity<?> saveAuthorCount(
            @RequestBody ManuscriptAuthorCountDto dto) {

        if (dto.getManuscriptId() == null || dto.getAuthorCount() == null) {
            return ResponseEntity.badRequest().body("Invalid request");
        }

        service.saveOrUpdateAuthorCount(
            dto.getManuscriptId(),
            dto.getAuthorCount()
        );

        return ResponseEntity.ok().build();
    }

    @GetMapping("/get/{manuscriptId}")
    public ResponseEntity<Integer> getAuthorCount(
            @PathVariable Long manuscriptId) {

        Integer count = service.getAuthorCount(manuscriptId);
        return ResponseEntity.ok(count);
    }
}

