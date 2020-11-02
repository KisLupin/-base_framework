package com.backend.utils;

import java.text.Normalizer;
import java.util.regex.Pattern;

public class Utils {
    public static String removeAccent(String src) {
        String nfdNormalizedString = Normalizer.normalize(src, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(nfdNormalizedString).replaceAll("").replaceAll("đ", "d")
                .replaceAll("Đ", "d").toLowerCase().trim();
    }
}
