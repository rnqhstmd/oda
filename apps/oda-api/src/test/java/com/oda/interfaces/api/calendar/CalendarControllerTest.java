package com.oda.interfaces.api.calendar;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oda.interfaces.api.calendar.dto.AddEventRequest;
import com.oda.application.calendar.CalendarEventService;
import com.oda.application.calendar.dto.CalendarEventResult;
import com.oda.domain.calendar.EventType;
import com.oda.infrastructure.security.SecurityConfig;
import com.oda.infrastructure.security.jwt.JwtAuthenticationFilter;
import com.oda.infrastructure.security.jwt.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CalendarController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
class CalendarControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CalendarEventService calendarEventService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    private UsernamePasswordAuthenticationToken authFor(Long userId) {
        return new UsernamePasswordAuthenticationToken(userId, null, Collections.emptyList());
    }

    private CalendarEventResult sampleEventResult(Long id) {
        return new CalendarEventResult(
                id, 1L, "스터디 모임", "알고리즘",
                LocalDate.of(2025, 6, 15), LocalDate.of(2025, 6, 15),
                null, null, EventType.CUSTOM, null, null, true, null
        );
    }

    @Test
    void 인증_없이_이벤트_목록_조회_401() throws Exception {
        mockMvc.perform(get("/api/v1/calendar/events")
                        .param("start", "2025-06-01")
                        .param("end", "2025-06-30"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void 이벤트_목록_조회_성공() throws Exception {
        Long userId = 1L;
        given(calendarEventService.getEvents(eq(userId), any(), any()))
                .willReturn(List.of(sampleEventResult(1L)));

        mockMvc.perform(get("/api/v1/calendar/events")
                        .param("start", "2025-06-01")
                        .param("end", "2025-06-30")
                        .with(SecurityMockMvcRequestPostProcessors.authentication(authFor(userId))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.meta.result").value("SUCCESS"))
                .andExpect(jsonPath("$.data[0].title").value("스터디 모임"));
    }

    @Test
    void 이벤트_단건_조회_성공() throws Exception {
        Long userId = 1L;
        Long eventId = 1L;
        given(calendarEventService.getEvent(eventId)).willReturn(sampleEventResult(eventId));

        mockMvc.perform(get("/api/v1/calendar/events/{id}", eventId)
                        .with(SecurityMockMvcRequestPostProcessors.authentication(authFor(userId))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.meta.result").value("SUCCESS"))
                .andExpect(jsonPath("$.data.id").value(eventId));
    }

    @Test
    void 커스텀_이벤트_추가_성공() throws Exception {
        Long userId = 1L;
        AddEventRequest request = new AddEventRequest(
                "새 이벤트", "설명", LocalDate.of(2025, 6, 15), null,
                null, null, true, null, null
        );
        given(calendarEventService.addEvent(eq(userId), any())).willReturn(sampleEventResult(1L));

        mockMvc.perform(post("/api/v1/calendar/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(SecurityMockMvcRequestPostProcessors.authentication(authFor(userId))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.meta.result").value("SUCCESS"));
    }

    @Test
    void 이벤트_수정_성공() throws Exception {
        Long userId = 1L;
        Long eventId = 1L;
        AddEventRequest request = new AddEventRequest(
                "수정된 이벤트", "새 설명", LocalDate.of(2025, 7, 1), null,
                null, null, true, null, null
        );

        mockMvc.perform(put("/api/v1/calendar/events/{id}", eventId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(SecurityMockMvcRequestPostProcessors.authentication(authFor(userId))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.meta.result").value("SUCCESS"));
    }

    @Test
    void 이벤트_삭제_성공() throws Exception {
        Long userId = 1L;
        Long eventId = 1L;

        mockMvc.perform(delete("/api/v1/calendar/events/{id}", eventId)
                        .with(SecurityMockMvcRequestPostProcessors.authentication(authFor(userId))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.meta.result").value("SUCCESS"));
    }
}
