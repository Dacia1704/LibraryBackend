package com.example.library.service;

import com.example.library.dto.report.response.StatisticResponse;

public interface ReportService {

    /**
     * Generate library statistics report
     * @return StatisticResponse containing all library statistics
     */
    StatisticResponse generateStatistics();

    /**
     * Get library statistics summary (simplified version)
     * @return StatisticResponse with key metrics
     */
    StatisticResponse getStatisticsSummary();

    /**
     * Export statistics report as PDF byte array
     * @param title custom title for the report
     * @return PDF file as byte array
     */
    byte[] exportStatisticsPdf(String title);
}
