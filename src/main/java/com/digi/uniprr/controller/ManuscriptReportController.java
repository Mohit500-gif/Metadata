package com.digi.uniprr.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.digi.uniprr.VO.ManuscriptReportDTO;
import com.digi.uniprr.VO.ReportRequest;
import com.digi.uniprr.VO.ReportResponse;
import com.digi.uniprr.service.ManuscriptReportService;
import com.digi.uniprr.utils.ExcelGenerator;
import com.digi.uniprr.utils.UUIDTokenUtil;


@RestController
@RequestMapping("/api/report")
public class ManuscriptReportController {

    @Autowired
    private ManuscriptReportService reportService;

    @Autowired
    private ExcelGenerator excelGenerator;

    @Autowired
    private UUIDTokenUtil uuidTokenUtil;

    @PostMapping("/data")
    public ResponseEntity<?> getReport(
            @RequestHeader("Authorization") String authorization,
            @RequestBody ReportRequest request
    ) {

        String token = authorization.replace("Bearer ", "");

        if (!uuidTokenUtil.isValidReportToken(token)) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", 401);
            error.put("message", "Invalid Token");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }

        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());

        Page<ManuscriptReportDTO> pageData = reportService.getManuscriptReport(
                request.getStartDate(),
                request.getEndDate(),
                pageable
        );

        ReportResponse response = new ReportResponse();
        response.setData(pageData.getContent());
        response.setTotalCount(pageData.getTotalElements());
        response.setTotalPages(pageData.getTotalPages());
        response.setCurrentPage(pageData.getNumber());
        response.setPageSize(pageData.getSize());

        return ResponseEntity.ok(response);
    }
}


