package com.oda.interfaces.api.dashboard;

import com.oda.infrastructure.security.SecurityConfig;
import com.oda.infrastructure.security.jwt.JwtAuthenticationFilter;
import com.oda.infrastructure.security.jwt.JwtTokenProvider;
import com.oda.application.dashboard.DashboardService;
import com.oda.application.dashboard.dto.DDayItem;
import com.oda.application.dashboard.dto.DashboardResult;
import com.oda.application.dashboard.dto.TodaySummary;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DashboardController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DashboardService dashboardService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    private UsernamePasswordAuthenticationToken authFor(Long userId) {
        return new UsernamePasswordAuthenticationToken(userId, null, Collections.emptyList());
    }

    @Test
    void 대시보드_조회_성공() throws Exception {
        Long userId = 1L;
        TodaySummary summary = new TodaySummary(3L, 2L, 5L);
        List<DDayItem> dDayItems = List.of(new DDayItem(1L, -2, "시험", "EXAM", "http://example.com"));
        DashboardResult result = new DashboardResult(summary, dDayItems, 4L);

        given(dashboardService.getDashboard(anyLong())).willReturn(result);

        mockMvc.perform(get("/api/v1/dashboard")
                        .with(SecurityMockMvcRequestPostProcessors.authentication(authFor(userId))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.meta.result").value("SUCCESS"))
                .andExpect(jsonPath("$.data.todaySummary.pendingTodos").value(3))
                .andExpect(jsonPath("$.data.todaySummary.completedTodos").value(2))
                .andExpect(jsonPath("$.data.unreadNotifications").value(4))
                .andExpect(jsonPath("$.data.dDayItems[0].dDay").value(-2));
    }

    @Test
    void D_Day_목록_조회_성공() throws Exception {
        Long userId = 1L;
        List<DDayItem> dDayItems = List.of(
                new DDayItem(1L, 0, "오늘마감", "JOB", "http://example.com"),
                new DDayItem(2L, -3, "3일후마감", "POLICY", "http://example2.com")
        );

        given(dashboardService.getDDayItems(anyLong())).willReturn(dDayItems);

        mockMvc.perform(get("/api/v1/dashboard/dday")
                        .with(SecurityMockMvcRequestPostProcessors.authentication(authFor(userId))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.meta.result").value("SUCCESS"))
                .andExpect(jsonPath("$.data[0].title").value("오늘마감"))
                .andExpect(jsonPath("$.data[0].dDay").value(0))
                .andExpect(jsonPath("$.data[1].dDay").value(-3));
    }

    @Test
    void 인증_없이_접근_시_401() throws Exception {
        mockMvc.perform(get("/api/v1/dashboard"))
                .andExpect(status().isUnauthorized());
    }
}
