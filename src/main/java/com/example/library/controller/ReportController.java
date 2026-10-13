package com.example.library.controller;

import com.example.library.common.ApiResponse;
import com.example.library.dto.report.request.ExportPdfRequest;
import com.example.library.dto.report.response.StatisticResponse;
import com.example.library.service.ReportService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReportController {

    ReportService reportService;

    /**
     * Export statistics as PDF using JasperReport
     */
    @PostMapping("/statistics/export/pdf")
    @PreAuthorize("hasAuthority('REPORT_READ')")
    public ResponseEntity<byte[]> exportStatisticsPdf(
            @RequestBody(required = false) ExportPdfRequest request
    ) {
        log.info("Exporting statistics report as PDF");

        String title = request != null ? request.getTitle() : null;
        byte[] pdfBytes = reportService.exportStatisticsPdf(title);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "statistics_report.pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }
}
