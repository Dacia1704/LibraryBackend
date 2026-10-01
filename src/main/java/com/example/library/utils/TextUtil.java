package com.example.library.utils;

import java.text.Normalizer;

public final class TextUtil {

    private TextUtil() {
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
}