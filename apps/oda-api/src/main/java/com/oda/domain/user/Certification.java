package com.oda.domain.user;

import java.time.LocalDate;

public record Certification(String name, String issuer, LocalDate acquiredDate) {}
