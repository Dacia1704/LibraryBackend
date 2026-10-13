package com.example.library.dto.report.request;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExportPdfRequest {

    private String title;
}
