package com.oda.domain.policy;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KoreanEligibilityParser {

    private static final Pattern AGE_RANGE_PATTERN =
            Pattern.compile("만\\s*(\\d+)\\s*세\\s*[~\\-~～]\\s*(\\d+)\\s*세");
    private static final Pattern AGE_MAX_PATTERN =
            Pattern.compile("만\\s*(\\d+)\\s*세\\s*이하");
    private static final Pattern AGE_MIN_PATTERN =
            Pattern.compile("만\\s*(\\d+)\\s*세\\s*이상");
    private static final Pattern INCOME_PATTERN =
            Pattern.compile("([\\d,]+)\\s*원\\s*이하");
    private static final Pattern REGION_PATTERN =
            Pattern.compile("([가-힣]+(?:,\\s*[가-힣]+)*)\\s*거주자");

    private KoreanEligibilityParser() {}

    public static EligibilityCriteria parse(String rawText) {
        if (rawText == null || rawText.isBlank()) {
            return new EligibilityCriteria(null, null, null, null, null,
                    null, null, null);
        }

        Integer minAge = null;
        Integer maxAge = null;
        Long maxPersonalIncome = null;
        List<String> requiredRegions = null;

        // Parse age range: "만 19세 ~ 34세"
        Matcher ageRangeMatcher = AGE_RANGE_PATTERN.matcher(rawText);
        if (ageRangeMatcher.find()) {
            minAge = Integer.parseInt(ageRangeMatcher.group(1));
            maxAge = Integer.parseInt(ageRangeMatcher.group(2));
        } else {
            Matcher ageMaxMatcher = AGE_MAX_PATTERN.matcher(rawText);
            if (ageMaxMatcher.find()) {
                maxAge = Integer.parseInt(ageMaxMatcher.group(1));
            }
            Matcher ageMinMatcher = AGE_MIN_PATTERN.matcher(rawText);
            if (ageMinMatcher.find()) {
                minAge = Integer.parseInt(ageMinMatcher.group(1));
            }
        }

        // Parse income: "2,564,238원 이하"
        Matcher incomeMatcher = INCOME_PATTERN.matcher(rawText);
        if (incomeMatcher.find()) {
            String amountStr = incomeMatcher.group(1).replace(",", "");
            maxPersonalIncome = Long.parseLong(amountStr);
        }

        // Parse regions: "서울, 경기 거주자"
        Matcher regionMatcher = REGION_PATTERN.matcher(rawText);
        if (regionMatcher.find()) {
            String regionsStr = regionMatcher.group(1);
            requiredRegions = new ArrayList<>();
            for (String region : regionsStr.split(",")) {
                String trimmed = region.trim();
                if (!trimmed.isEmpty()) {
                    requiredRegions.add(trimmed);
                }
            }
        }

        return new EligibilityCriteria(
                minAge, maxAge,
                maxPersonalIncome, null, null,
                requiredRegions,
                null, null
        );
    }
}
