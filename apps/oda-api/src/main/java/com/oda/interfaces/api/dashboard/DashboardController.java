package com.oda.interfaces.api.dashboard;

import com.oda.interfaces.api.ApiResponse;
import com.oda.interfaces.api.dashboard.dto.DDayItemResponse;
import com.oda.interfaces.api.dashboard.dto.DashboardResponse;
import com.oda.application.dashboard.DashboardService;
import com.oda.application.dashboard.dto.DDayItem;
import com.oda.application.dashboard.dto.DashboardResult;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping
    public ApiResponse<DashboardResponse> getDashboard() {
        Long userId = extractUserId();
        DashboardResult result = dashboardService.getDashboard(userId);
        return ApiResponse.success(DashboardResponse.from(result));
    }

    @GetMapping("/dday")
    public ApiResponse<List<DDayItemResponse>> getDDayItems() {
        Long userId = extractUserId();
        List<DDayItem> items = dashboardService.getDDayItems(userId);
        List<DDayItemResponse> responses = items.stream()
                .map(DDayItemResponse::from)
                .collect(Collectors.toList());
        return ApiResponse.success(responses);
    }

    private Long extractUserId() {
        return Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getName());
    }
}
