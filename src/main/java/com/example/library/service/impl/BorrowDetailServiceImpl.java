package com.example.library.service.impl;

import com.example.library.common.PageResponse;
import com.example.library.dto.book.request.BorrowDetailFilter;
import com.example.library.dto.book.request.FineRequest;
import com.example.library.dto.book.request.ReturnBookRequest;
import com.example.library.dto.book.response.BorrowDetailResponse;
import com.example.library.dto.book.response.BorrowDetailSummaryResponse;
import com.example.library.entity.Book;
import com.example.library.entity.BorrowDetail;
import com.example.library.entity.Fine;
import com.example.library.entity.Setting;
import com.example.library.entity.enums.BorrowStatus;
import com.example.library.entity.enums.FineReason;
import com.example.library.exception.AppException;
import com.example.library.exception.ErrorCode;
import com.example.library.mapper.BorrowDetailMapper;
import com.example.library.repository.BorrowDetailRepository;
import com.example.library.repository.FineRepository;
import com.example.library.repository.SettingRepository;
import com.example.library.repository.specification.BorrowDetailSpecification;
import com.example.library.service.BorrowDetailService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BorrowDetailServiceImpl implements BorrowDetailService {

    final BorrowDetailRepository borrowDetailRepository;

    final BorrowDetailMapper borrowDetailMapper;

    final FineRepository fineRepository;

    final SettingRepository settingRepository;

    @Value("${app.setting-key.fine-overdue-per-day}")
    String fine_overdue_per_day;

    @Value("${app.setting-key.fine-lost-rate}")
    String fine_lost_rate;

    @Value("${app.setting-key.fine-damaged-light-rate}")
    String fine_damaged_light_rate;

    @Value("${app.setting-key.fine-damaged-heavy-repairable-rate}")
    String fine_damaged_heavy_repairable_rate;

    @Value("${app.setting-key.fine-damaged-heavy-irreparable-rate}")
    String fine_damaged_heavy_irrepairable_rate;

    @Override
    @Transactional(readOnly = true)
    public BorrowDetailResponse getBorrowDetail(String id) {

        BorrowDetail borrowDetail =
                borrowDetailRepository
                        .findByIdAndIsDeletedFalse(Long.valueOf(id))
                        .orElseThrow(() ->
                                new AppException(
                                        ErrorCode.BORROW_DETAIL_NOT_FOUND
                                )
                        );

        return borrowDetailMapper.toBorrowDetailResponse(
                borrowDetail
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<BorrowDetailResponse> getBorrowDetails() {

        return borrowDetailRepository
                .findAllByIsDeletedFalse()
                .stream()
                .map(borrowDetailMapper::toBorrowDetailResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<BorrowDetailResponse> getBorrowDetailsPagination(
            BorrowDetailFilter filter,
            int page,
            int size
    ) {

        return paginate(
                BorrowDetailSpecification.filter(filter),
                normalizePage(page),
                normalizeSize(size)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<BorrowDetailResponse> getMyBorrowDetailsPagination(
            BorrowDetailFilter filter,
            int page,
            int size
    ) {

        Long userId = Long.valueOf(
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getName()
        );

        Specification<BorrowDetail> specification =
                BorrowDetailSpecification
                        .filter(filter)
                        .and(BorrowDetailSpecification.belongsToUser(userId));

        return paginate(
                specification,
                normalizePage(page),
                normalizeSize(size)
        );
    }

    private int normalizePage(int page) {
        return page < 0 ? 0 : page;
    }

    private int normalizeSize(int size) {
        return size <= 0 ? 10 : size;
    }

    private PageResponse<BorrowDetailResponse> paginate(
            Specification<BorrowDetail> specification,
            int page,
            int size
    ) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "id")
        );

        Page<BorrowDetail> borrowDetailPage =
                borrowDetailRepository.findAll(specification, pageable);

        List<BorrowDetailResponse> content =
                borrowDetailPage.getContent()
                        .stream()
                        .map(borrowDetailMapper::toBorrowDetailResponse)
                        .toList();

        return PageResponse.<BorrowDetailResponse>builder()
                .data(content)
                .currentPage(borrowDetailPage.getNumber())
                .pageSize(borrowDetailPage.getSize())
                .totalElements(borrowDetailPage.getTotalElements())
                .totalPages(borrowDetailPage.getTotalPages())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public BorrowDetailSummaryResponse getMySummary() {

        Long userId = Long.valueOf(
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getName()
        );

        List<Object[]> rows = borrowDetailRepository.getMyBorrowSummary(userId);

        long total = 0L;
        long borrowing = 0L;
        long overdue = 0L;
        long returned = 0L;

        if (rows != null && !rows.isEmpty() && rows.get(0) != null) {
            Object[] row = rows.get(0);
            // MySQL driver may return the aggregate columns either as scalars
            // (Object[]) or wrapped in another Object[]; handle both shapes.
            Object[] cells = (row.length == 1 && row[0] instanceof Object[] inner)
                    ? inner
                    : row;

            if (cells.length >= 1) total     = toLong(cells[0]);
            if (cells.length >= 2) borrowing = toLong(cells[1]);
            if (cells.length >= 3) overdue   = toLong(cells[2]);
            if (cells.length >= 4) returned  = toLong(cells[3]);
        }

        return BorrowDetailSummaryResponse.builder()
                .total(total)
                .borrowing(borrowing)
                .overdue(overdue)
                .returned(returned)
                .build();
    }

    private long toLong(Object value) {
        if (value == null) return 0L;
        if (value instanceof Number number) return number.longValue();
        return Long.parseLong(value.toString());
    }

    @Override
    @Transactional
    public BorrowDetailResponse returnBook(String id, ReturnBookRequest request) {

        BorrowDetail borrowDetail =
                borrowDetailRepository
                        .findByIdAndIsDeletedFalse(Long.valueOf(id))
                        .orElseThrow(() -> new AppException(ErrorCode.BORROW_DETAIL_NOT_FOUND));

        Book book = borrowDetail.getBook();

        if (borrowDetail.getReturnDate() != null) {
            throw new AppException(ErrorCode.BOOK_ALREADY_RETURNED);
        }

        LocalDate returnDate = LocalDate.now();

        borrowDetail.setReturnDate(returnDate);
        borrowDetail.setStatus(BorrowStatus.RETURNED);

        // Tính tiền phạt
        List<String> settingKeys = List.of(
                fine_overdue_per_day,
                fine_lost_rate,
                fine_damaged_light_rate,
                fine_damaged_heavy_repairable_rate,
                fine_damaged_heavy_irrepairable_rate
        );

        Map<String, BigDecimal> fineRates = settingRepository
                .findAllBySettingKeyInAndIsDeletedFalse(settingKeys)
                .stream()
                .collect(Collectors.toMap(
                        Setting::getSettingKey,
                        setting -> new BigDecimal(setting.getSettingValue())
                ));
        List<Fine> newFines = new ArrayList<>();
        // đi muộn
        LocalDate dueDate = borrowDetail.getBorrowRecord().getDueDate();
        if (returnDate.isAfter(dueDate)) {

            long lateDays = ChronoUnit.DAYS.between(dueDate, returnDate);
            BigDecimal fineAmount = BigDecimal.valueOf(lateDays).multiply(fineRates.get(fine_overdue_per_day));
            borrowDetail.setFineAmount(fineAmount);
            newFines.add(Fine.builder()
                    .borrowRecord(borrowDetail.getBorrowRecord())
                    .borrowDetail(borrowDetail)
                    .amount(fineAmount)
                    .reason(FineReason.OVERDUE)
                    .overdueDays((int) lateDays)
                    .note(String.format("Tiền phạt trả sách %s muộn. Mã mượn %s",borrowDetail.getBook().getTitle(), borrowDetail.getId()))
                    .build());

        } else {
            borrowDetail.setFineAmount(BigDecimal.ZERO);
        }

        for(FineRequest fineRequest: request.getFineRequests()) {
            String attachment = null;
            if (fineRequest.getAttachment() != null && !fineRequest.getAttachment().isEmpty()) {
                try {
                    attachment = Base64.getEncoder().encodeToString(fineRequest.getAttachment().getBytes());
                } catch (IOException e) {
                    throw new AppException(ErrorCode.FILE_UPLOAD_FAILED);
                }
            }
            BigDecimal fineAmount = null;

            Fine fine = Fine.builder()
                    .borrowRecord(borrowDetail.getBorrowRecord())
                    .borrowDetail(borrowDetail)
                    .amount(fineAmount)
                    .attachment(attachment)
                    .reason(FineReason.LOST)
                    .note(String.format("Tiền phạt mất sách %s. Mã mượn %s",borrowDetail.getBook().getTitle(), borrowDetail.getId()))
                    .build();

            switch (fineRequest.getReason()) {
                case FineReason.LOST -> {
                    fineAmount = book.getPrice().multiply(fineRates.get(fine_lost_rate));
                    fine.setNote(String.format("Tiền phạt mất sách %s. Mã mượn %s",borrowDetail.getBook().getTitle(), borrowDetail.getId()));
                    fine.setReason(FineReason.LOST);
                }
                case FineReason.DAMAGED_LIGHT -> {
                    fineAmount = book.getPrice().multiply(fineRates.get(fine_damaged_light_rate));
                    fine.setNote(String.format("Tiền phạt làm hỏng sách %s (mức độ nhẹ). Mã mượn %s",borrowDetail.getBook().getTitle(), borrowDetail.getId()));
                    fine.setReason(FineReason.DAMAGED_LIGHT);
                }
                case FineReason.DAMAGED_HEAVY_REPAIRABLE -> {
                    fineAmount = book.getPrice().multiply(fineRates.get(fine_damaged_heavy_repairable_rate));
                    fine.setNote(String.format("Tiền phạt làm hỏng sách %s (mức độ nặng - có thể sửa chữa và tiếp tục sử dụng). Mã mượn %s",borrowDetail.getBook().getTitle(), borrowDetail.getId()));
                    fine.setReason(FineReason.DAMAGED_HEAVY_REPAIRABLE);
                }
                case FineReason.DAMAGED_HEAVY_IRREPARABLE -> {
                    fineAmount = book.getPrice().multiply(fineRates.get(fine_damaged_heavy_irrepairable_rate));
                    fine.setNote(String.format("Tiền phạt làm hỏng sách %s (mức độ nặng - không thể tiếp tục sử dụng). Mã mượn %s",borrowDetail.getBook().getTitle(), borrowDetail.getId()));
                    fine.setReason(FineReason.DAMAGED_HEAVY_IRREPARABLE);
                }
            }

            borrowDetail.setFineAmount(fineAmount);
            fine.setAmount(fineAmount);
            newFines.add(fine);

        }

        book.setAvailable(book.getAvailable() + 1);

        fineRepository.saveAll(newFines);
        borrowDetailRepository.save(borrowDetail);

        return borrowDetailMapper.toBorrowDetailResponse(borrowDetail);
    }
}