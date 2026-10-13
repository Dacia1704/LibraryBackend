package com.example.library.service.impl;

import com.example.library.dto.report.response.StatisticResponse;
import com.example.library.entity.enums.BorrowStatus;
import com.example.library.entity.enums.CardStatus;
import com.example.library.repository.*;
import com.example.library.service.ReportService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReportServiceImpl implements ReportService {

    BorrowRecordRepository borrowRecordRepository;
    BorrowDetailRepository borrowDetailRepository;
    FineRepository fineRepository;
    FinePaymentRepository finePaymentRepository;
    BookRepository bookRepository;
    AuthorRepository authorRepository;
    CategoryRepository categoryRepository;
    ShelfRepository shelfRepository;
    PublisherRepository publisherRepository;
    UserRepository userRepository;
    MemberRepository memberRepository;

    @Override
    @Transactional(readOnly = true)
    public StatisticResponse generateStatistics() {
        log.info("Generating library statistics report");

        StatisticResponse.StatisticResponseBuilder builder = StatisticResponse.builder();

        // Borrow Record Statistics
        builder.totalBorrowRecords(getTotalBorrowRecords())
                .borrowingRecords(getBorrowingRecords())
                .returnedRecords(getReturnedRecords())
                .overdueRecords(getOverdueRecords());

        // Borrow Detail Statistics
        builder.totalBorrowDetails(getTotalBorrowDetails())
                .returnedBorrowDetails(getReturnedBorrowDetails())
                .notReturnedBorrowDetails(getNotReturnedBorrowDetails());

        // Fine Statistics
        builder.totalFines(getTotalFines())
                .unpaidFines(getUnpaidFines())
                .totalFinePayments(getTotalFinePayments());

        // Fine Amount Details
        builder.totalFineAmountDetail(getTotalFineAmount())
                .totalPaidAmountDetail(getTotalPaidAmount())
                .totalRemainingAmountDetail(getTotalRemainingAmount());

        // Book Statistics
        builder.totalBooks(getTotalBooks())
                .totalBookCopies(getTotalBookCopies())
                .availableBookCopies(getAvailableBookCopies())
                .borrowedBookCopies(getBorrowedBookCopies());

        // Book Info Statistics
        builder.totalAuthors(getTotalAuthors())
                .totalCategories(getTotalCategories())
                .totalShelves(getTotalShelves())
                .totalPublishers(getTotalPublishers());

        // Reader Statistics
        builder.totalReaders(getTotalReaders())
                .activeReaders(getActiveReaders())
                .inactiveReaders(getInactiveReaders());

        // Member Statistics
        builder.totalMembers(getTotalMembers())
                .activeMembers(getActiveMembers())
                .expiredMembers(getExpiredMembers());

        // Librarian Statistics
        builder.totalLibrarians(getTotalLibrarians())
                .activeLibrarians(getActiveLibrarians())
                .inactiveLibrarians(getInactiveLibrarians());

        return builder.build();
    }

    @Override
    @Transactional(readOnly = true)
    public StatisticResponse getStatisticsSummary() {
        return generateStatistics();
    }

    @Override
    public byte[] exportStatisticsPdf(String title) {
        log.info("Exporting statistics report as PDF");

        StatisticResponse statistics = generateStatistics();

        // Prepare parameters for JasperReport
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("title", title != null ? title : "BÁO CÁO THỐNG KÊ THƯ VIỆN");
        parameters.put("generatedDate", new Date());

        // Convert StatisticResponse to a Map for JasperReport fields
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("totalBorrowRecords", statistics.getTotalBorrowRecords());
        dataMap.put("borrowingRecords", statistics.getBorrowingRecords());
        dataMap.put("returnedRecords", statistics.getReturnedRecords());
        dataMap.put("overdueRecords", statistics.getOverdueRecords());
        dataMap.put("totalBorrowDetails", statistics.getTotalBorrowDetails());
        dataMap.put("returnedBorrowDetails", statistics.getReturnedBorrowDetails());
        dataMap.put("notReturnedBorrowDetails", statistics.getNotReturnedBorrowDetails());
        dataMap.put("totalFines", statistics.getTotalFines());
        dataMap.put("unpaidFines", statistics.getUnpaidFines());
        dataMap.put("totalFinePayments", statistics.getTotalFinePayments());
        dataMap.put("totalBooks", statistics.getTotalBooks());
        dataMap.put("totalBookCopies", statistics.getTotalBookCopies());
        dataMap.put("availableBookCopies", statistics.getAvailableBookCopies());
        dataMap.put("borrowedBookCopies", statistics.getBorrowedBookCopies());
        dataMap.put("totalAuthors", statistics.getTotalAuthors());
        dataMap.put("totalCategories", statistics.getTotalCategories());
        dataMap.put("totalShelves", statistics.getTotalShelves());
        dataMap.put("totalPublishers", statistics.getTotalPublishers());
        dataMap.put("totalReaders", statistics.getTotalReaders());
        dataMap.put("activeReaders", statistics.getActiveReaders());
        dataMap.put("inactiveReaders", statistics.getInactiveReaders());
        dataMap.put("totalMembers", statistics.getTotalMembers());
        dataMap.put("activeMembers", statistics.getActiveMembers());
        dataMap.put("expiredMembers", statistics.getExpiredMembers());
        dataMap.put("totalLibrarians", statistics.getTotalLibrarians());
        dataMap.put("activeLibrarians", statistics.getActiveLibrarians());
        dataMap.put("inactiveLibrarians", statistics.getInactiveLibrarians());
        dataMap.put("totalFineAmount", statistics.getTotalFineAmountDetail());
        dataMap.put("totalPaidAmount", statistics.getTotalPaidAmountDetail());
        dataMap.put("totalRemainingAmount", statistics.getTotalRemainingAmountDetail());

        try {
            // Load and compile JasperReport
            InputStream reportStream = new ClassPathResource("reports/Statistic.jrxml").getInputStream();
            JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);

            // Create data source
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(Collections.singletonList(dataMap));

            // Fill report
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

            // Export to PDF
            return JasperExportManager.exportReportToPdf(jasperPrint);
        } catch (Exception e) {
            log.error("Error exporting PDF report", e);
            throw new RuntimeException("Failed to export PDF report", e);
        }
    }

    // ============== Borrow Record Statistics ==============

    private Long getTotalBorrowRecords() {
        return borrowRecordRepository.findAllByIsDeletedFalse().stream().count();
    }

    private Long getBorrowingRecords() {
        return borrowRecordRepository.findAllByIsDeletedFalse().stream()
                .filter(record -> record.getBorrowDetails().stream()
                        .anyMatch(detail -> !detail.getIsDeleted() &&
                                (detail.getStatus() == BorrowStatus.BORROWING ||
                                        detail.getStatus() == BorrowStatus.OVERDUE)))
                .count();
    }

    private Long getReturnedRecords() {
        return borrowRecordRepository.findAllByIsDeletedFalse().stream()
                .filter(record -> record.getBorrowDetails().stream()
                        .allMatch(detail -> !detail.getIsDeleted() &&
                                detail.getStatus() == BorrowStatus.RETURNED))
                .count();
    }

    private Long getOverdueRecords() {
        LocalDate today = LocalDate.now();
        return borrowRecordRepository.findAllByIsDeletedFalse().stream()
                .filter(record -> record.getBorrowDetails().stream()
                        .anyMatch(detail -> !detail.getIsDeleted() &&
                                detail.getStatus() == BorrowStatus.OVERDUE &&
                                record.getDueDate().isBefore(today)))
                .count();
    }

    // ============== Borrow Detail Statistics ==============

    private Long getTotalBorrowDetails() {
        return borrowDetailRepository.findAllByIsDeletedFalse().stream().count();
    }

    private Long getReturnedBorrowDetails() {
        return borrowDetailRepository.findAllByIsDeletedFalse().stream()
                .filter(detail -> detail.getStatus() == BorrowStatus.RETURNED)
                .count();
    }

    private Long getNotReturnedBorrowDetails() {
        return borrowDetailRepository.findAllByIsDeletedFalse().stream()
                .filter(detail -> detail.getStatus() == BorrowStatus.BORROWING ||
                        detail.getStatus() == BorrowStatus.OVERDUE)
                .count();
    }

    // ============== Fine Statistics ==============

    private Long getTotalFines() {
        return fineRepository.findAllByIsDeletedFalse(Pageable.unpaged()).getTotalElements();
    }

    private Long getUnpaidFines() {
        return fineRepository.findAllByIsDeletedFalse(Pageable.unpaged())
                .getContent().stream()
                .filter(fine -> fine.getBorrowDetail() != null &&
                        (fine.getBorrowDetail().getStatus() == BorrowStatus.BORROWING ||
                                fine.getBorrowDetail().getStatus() == BorrowStatus.OVERDUE))
                .count();
    }

    private Long getTotalFinePayments() {
        return finePaymentRepository.findAllByIsDeletedFalse(Pageable.unpaged()).getTotalElements();
    }

    private BigDecimal getTotalFineAmount() {
        return fineRepository.findAllByIsDeletedFalse(Pageable.unpaged())
                .getContent().stream()
                .map(fine -> fine.getAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal getTotalPaidAmount() {
        return finePaymentRepository.findAllByIsDeletedFalse(Pageable.unpaged())
                .getContent().stream()
                .map(payment -> payment.getAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal getTotalRemainingAmount() {
        BigDecimal totalFine = getTotalFineAmount();
        BigDecimal totalPaid = getTotalPaidAmount();
        return totalFine.subtract(totalPaid);
    }

    // ============== Book Statistics ==============

    private Long getTotalBooks() {
        return bookRepository.countTotalTitles();
    }

    private Long getTotalBookCopies() {
        return bookRepository.sumTotalCopies();
    }

    private Long getAvailableBookCopies() {
        return bookRepository.findAllByIsDeletedFalse().stream()
                .mapToLong(book -> book.getAvailable())
                .sum();
    }

    private Long getBorrowedBookCopies() {
        return bookRepository.sumBorrowedCopies();
    }

    // ============== Book Info Statistics ==============

    private Long getTotalAuthors() {
        return authorRepository.findAllByIsDeletedFalse().stream().count();
    }

    private Long getTotalCategories() {
        return categoryRepository.findAllByIsDeletedFalse().stream().count();
    }

    private Long getTotalShelves() {
        return shelfRepository.count();
    }

    private Long getTotalPublishers() {
        return publisherRepository.findAllByIsDeletedFalse().stream().count();
    }

    // ============== Reader Statistics ==============

    private Long getTotalReaders() {
        return userRepository.findAllByIsDeletedFalse().stream()
                .filter(user -> user.getRole() != null &&
                        user.getRole().getName() != null &&
                        user.getRole().getName().equalsIgnoreCase("READER"))
                .count();
    }

    private Long getActiveReaders() {
        return userRepository.findAllByIsDeletedFalse().stream()
                .filter(user -> user.getIsActive() &&
                        user.getRole() != null &&
                        user.getRole().getName() != null &&
                        user.getRole().getName().equalsIgnoreCase("READER"))
                .count();
    }

    private Long getInactiveReaders() {
        return userRepository.findAllByIsDeletedFalse().stream()
                .filter(user -> !user.getIsActive() &&
                        user.getRole() != null &&
                        user.getRole().getName() != null &&
                        user.getRole().getName().equalsIgnoreCase("READER"))
                .count();
    }

    // ============== Member Statistics ==============

    private Long getTotalMembers() {
        return memberRepository.findAllByIsDeletedFalse().stream().count();
    }

    private Long getActiveMembers() {
        LocalDate today = LocalDate.now();
        return memberRepository.findAllByIsDeletedFalse().stream()
                .filter(member -> member.getCardExpiry().isAfter(today) &&
                        member.getCardStatus() == CardStatus.ISSUED)
                .count();
    }

    private Long getExpiredMembers() {
        LocalDate today = LocalDate.now();
        return memberRepository.findAllByIsDeletedFalse().stream()
                .filter(member -> member.getCardExpiry().isBefore(today) ||
                        member.getCardExpiry().isEqual(today))
                .count();
    }

    // ============== Librarian Statistics ==============

    private Long getTotalLibrarians() {
        return userRepository.findAllByIsDeletedFalse().stream()
                .filter(user -> user.getRole() != null &&
                        user.getRole().getName() != null &&
                        user.getRole().getName().equalsIgnoreCase("LIBRARIAN"))
                .count();
    }

    private Long getActiveLibrarians() {
        return userRepository.findAllByIsDeletedFalse().stream()
                .filter(user -> user.getIsActive() &&
                        user.getRole() != null &&
                        user.getRole().getName() != null &&
                        user.getRole().getName().equalsIgnoreCase("LIBRARIAN"))
                .count();
    }

    private Long getInactiveLibrarians() {
        return userRepository.findAllByIsDeletedFalse().stream()
                .filter(user -> !user.getIsActive() &&
                        user.getRole() != null &&
                        user.getRole().getName() != null &&
                        user.getRole().getName().equalsIgnoreCase("LIBRARIAN"))
                .count();
    }
}
