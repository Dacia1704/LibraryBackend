package com.example.library.service.impl;

import com.example.library.common.PageResponse;
import com.example.library.dto.book.request.BorrowRecordFilter;
import com.example.library.dto.book.request.BorrowRecordRequest;
import com.example.library.dto.book.response.BorrowRecordResponse;
import com.example.library.entity.*;
import com.example.library.entity.enums.BorrowStatus;
import com.example.library.exception.AppException;
import com.example.library.exception.ErrorCode;
import com.example.library.mapper.BorrowRecordMapper;
import com.example.library.repository.*;
import com.example.library.repository.specification.BorrowRecordSpecification;
import com.example.library.service.BorrowRecordService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BorrowRecordServiceImpl
        implements BorrowRecordService {

    BorrowRecordRepository borrowRecordRepository;

    BorrowDetailRepository borrowDetailRepository;

    MemberRepository memberRepository;

    UserRepository userRepository;

    BookRepository bookRepository;

    BorrowRecordMapper borrowRecordMapper;

    @Override
    @Transactional
    public BorrowRecordResponse createBorrowRecord(
            BorrowRecordRequest request
    ) {

        // 1. Tìm Member
        Member member = memberRepository.findById(Long.valueOf(request.getMemberId()))
                        .orElseThrow(() -> new AppException(ErrorCode.MEMBER_NOT_FOUND));

        // 2. Tìm Librarian
        User librarian =
                userRepository.findById(Long.valueOf(request.getLibrarianId()))
                        .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        // 3. Tạo BorrowRecord
        BorrowRecord borrowRecord = BorrowRecord.builder()
                        .member(member)
                        .librarian(librarian)
                        .borrowDate(request.getBorrowDate())
                        .dueDate(request.getDueDate())
                        .status(request.getStatus() != null ? request.getStatus() : BorrowStatus.BORROWING)
                        .note(request.getNote())
                        .build();

        borrowRecordRepository.save(borrowRecord);

        // 4. Tìm Book
        List<Long> bookIds =
                request.getBookIds()
                        .stream()
                        .map(Long::valueOf)
                        .toList();

        List<Book> books = bookRepository.findAllByIdInAndIsDeletedFalse(bookIds);

        if (books.size() != bookIds.size()) throw new AppException(ErrorCode.BOOK_NOT_FOUND);

        // 5. Tạo BorrowDetail
        List<BorrowDetail> borrowDetails = new ArrayList<>();

        for (Book book : books) {

            if (book.getAvailable() <= 0) throw new AppException(ErrorCode.BOOK_NOT_AVAILABLE, String.format("Sách %s mã ISBN %s đã hết", book.getTitle(), book.getIsbn()));

            BorrowDetail detail =
                    BorrowDetail.builder()
                            .borrowRecord(borrowRecord)
                            .book(book)
                            .fineAmount(java.math.BigDecimal.ZERO)
                            .isDeleted(false)
                            .build();

            borrowDetails.add(detail);

            // Giảm số lượng sách còn available
            book.setAvailable(book.getAvailable() - 1);
        }

        borrowDetailRepository.saveAll(borrowDetails);

        bookRepository.saveAll(books);

        borrowRecord.setBorrowDetails(borrowDetails);

        return borrowRecordMapper.toBorrowRecordResponse(borrowRecord);
    }

    @Override
    @Transactional
    public BorrowRecordResponse updateBorrowRecord(
            Long id,
            BorrowRecordRequest request
    ) {

        BorrowRecord borrowRecord =
                borrowRecordRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.BORROW_RECORD_NOT_FOUND));

        Member member =
                memberRepository.findById(Long.valueOf(request.getMemberId()))
                        .orElseThrow(() -> new AppException(ErrorCode.MEMBER_NOT_FOUND));

        User librarian =
                userRepository.findById(Long.valueOf(request.getLibrarianId())).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        borrowRecord.setMember(member);
        borrowRecord.setLibrarian(librarian);
        borrowRecord.setBorrowDate(request.getBorrowDate());
        borrowRecord.setDueDate(request.getDueDate());
        borrowRecord.setStatus(request.getStatus());
        borrowRecord.setNote(request.getNote());

        borrowRecordRepository.save(borrowRecord);

        return borrowRecordMapper.toBorrowRecordResponse(borrowRecord);
    }

    @Override
    @Transactional
    public BorrowRecordResponse deleteBorrowRecord(
            String id
    ) {

        BorrowRecord borrowRecord =
                borrowRecordRepository.findById(Long.valueOf(id)).orElseThrow(() -> new AppException(ErrorCode.BORROW_RECORD_NOT_FOUND));

        borrowRecord.setIsDeleted(true);

        borrowRecordRepository.save(borrowRecord);

        return borrowRecordMapper.toBorrowRecordResponse(borrowRecord);
    }

    @Override
    @Transactional(readOnly = true)
    public BorrowRecordResponse getBorrowRecord(String id) {

        BorrowRecord borrowRecord = borrowRecordRepository.findByIdAndIsDeletedFalse(Long.valueOf(id))
                        .orElseThrow(() -> new AppException(ErrorCode.BORROW_RECORD_NOT_FOUND));

        return borrowRecordMapper.toBorrowRecordResponse(borrowRecord);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BorrowRecordResponse> getBorrowRecords() {
        return borrowRecordRepository
                .findAllByIsDeletedFalse()
                .stream()
                .map(borrowRecordMapper::toBorrowRecordResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<BorrowRecordResponse> getBorrowRecordsPagination(
            BorrowRecordFilter filter,
            int page,
            int size
    ) {

        if (page < 0) {
            page = 0;
        }

        if (size <= 0) {
            size = 10;
        }

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "id")
        );

        Specification<BorrowRecord> specification =
                BorrowRecordSpecification.filter(filter);

        Page<BorrowRecord> borrowRecordPage =
                borrowRecordRepository.findAll(specification, pageable);

        List<BorrowRecordResponse> content =
                borrowRecordPage.getContent()
                        .stream()
                        .map(borrowRecordMapper::toBorrowRecordResponse)
                        .toList();

        return PageResponse.<BorrowRecordResponse>builder()
                .data(content)
                .currentPage(borrowRecordPage.getNumber())
                .pageSize(borrowRecordPage.getSize())
                .totalElements(borrowRecordPage.getTotalElements())
                .totalPages(borrowRecordPage.getTotalPages())
                .build();
    }
}