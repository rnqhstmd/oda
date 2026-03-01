package com.oda.domain.user;

public record IncomeInfo(Long personalIncome, Long householdIncome, Integer householdSize) {
    public IncomeInfo {
        if (personalIncome != null && personalIncome < 0) throw new IllegalArgumentException("personalIncome must be >= 0");
        if (householdIncome != null && householdIncome < 0) throw new IllegalArgumentException("householdIncome must be >= 0");
        if (householdSize != null && householdSize < 1) throw new IllegalArgumentException("householdSize must be >= 1");
    }
}
