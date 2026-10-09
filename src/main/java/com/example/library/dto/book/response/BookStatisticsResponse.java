package com.example.library.dto.book.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookStatisticsResponse {

    /**
     * Tổng số đầu sách (sách chưa bị xóa)
     */
    private Long totalTitles;

    /**
     * Tổng số lượng bản ghi (tổng quantity của tất cả sách)
     */
    private Long totalCopies;

    /**
     * Số sách đang được mượn (quantity - available)
     */
    private Long borrowedCopies;

    /**
     * Số sách đã hết (available = 0)
     */
    private Long outOfStockTitles;
}
