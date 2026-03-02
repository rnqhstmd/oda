package com.oda.infrastructure.persistence;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class CsvStringUtils {
    private CsvStringUtils() {}

    public static List<String> parse(String csv) {
        if (csv == null || csv.isBlank()) return Collections.emptyList();
        return Arrays.stream(csv.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }

    public static String join(List<String> list) {
        if (list == null || list.isEmpty()) return null;
        return String.join(",", list);
    }
}
