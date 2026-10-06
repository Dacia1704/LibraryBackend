package com.example.library.utils;

import java.text.Normalizer;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public final class TextUtils {

    private TextUtils() {
    }

    public static String removeAccent(String text) {
        if (text == null || text.isBlank()) {
            return text;
        }

        String normalized = Normalizer.normalize(text, Normalizer.Form.NFD);

        return normalized
                .replaceAll("\\p{M}", "")
                .replace("đ", "d")
                .replace("Đ", "D");
    }

    public static String generateCode(String prefix) {

        String date = LocalDate.now()
                .format(DateTimeFormatter.ofPattern("yyMMdd"));

        String random = UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 3)
                .toUpperCase();

        return prefix + "-" + date + "-" + random;
    }
}