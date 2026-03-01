package com.oda.application.policy.dto;

import com.oda.domain.policy.MatchResult;
import com.oda.domain.policy.Policy;

public record PolicyMatchResult(Policy policy, MatchResult result) {}
