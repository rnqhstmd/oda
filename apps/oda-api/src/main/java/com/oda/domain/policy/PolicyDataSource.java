package com.oda.domain.policy;

import com.oda.domain.policy.Policy;

import java.util.List;

public interface PolicyDataSource {
    List<Policy> fetchPolicies();
}
