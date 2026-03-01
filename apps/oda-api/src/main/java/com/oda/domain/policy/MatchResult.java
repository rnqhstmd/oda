package com.oda.domain.policy;

public record MatchResult(boolean eligible, String reason) {

    public static MatchResult ofEligible() {
        return new MatchResult(true, null);
    }

    public static MatchResult ofIneligible(String reason) {
        return new MatchResult(false, reason);
    }
}
