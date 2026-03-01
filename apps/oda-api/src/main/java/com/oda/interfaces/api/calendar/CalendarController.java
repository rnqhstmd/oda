package com.oda.interfaces.api.calendar;

import com.oda.interfaces.api.calendar.dto.*;
import com.oda.application.calendar.CalendarEventService;
import com.oda.application.calendar.dto.CalendarEventResult;
import com.oda.interfaces.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/calendar/events")
@RequiredArgsConstructor
public class CalendarController {

    private final CalendarEventService calendarEventService;

    @GetMapping
    public ApiResponse<List<EventResponse>> getEvents(
            @AuthenticationPrincipal Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        List<CalendarEventResult> results = calendarEventService.getEvents(userId, start, end);
        return ApiResponse.success(results.stream().map(EventResponse::from).toList());
    }

    @GetMapping("/{id}")
    public ApiResponse<EventResponse> getEvent(@PathVariable Long id) {
        CalendarEventResult result = calendarEventService.getEvent(id);
        return ApiResponse.success(EventResponse.from(result));
    }

    @PostMapping
    public ApiResponse<EventResponse> addEvent(
            @AuthenticationPrincipal Long userId,
            @RequestBody AddEventRequest request) {
        CalendarEventResult result = calendarEventService.addEvent(userId, request.toCommand());
        return ApiResponse.success(EventResponse.from(result));
    }

    @PostMapping("/from-policy")
    public ApiResponse<EventResponse> addFromPolicy(
            @AuthenticationPrincipal Long userId,
            @RequestBody AddFromPolicyRequest request) {
        CalendarEventResult result = calendarEventService.addFromPolicy(userId, request.toCommand());
        return ApiResponse.success(EventResponse.from(result));
    }

    @PostMapping("/from-job")
    public ApiResponse<EventResponse> addFromJob(
            @AuthenticationPrincipal Long userId,
            @RequestBody AddFromJobRequest request) {
        CalendarEventResult result = calendarEventService.addFromJob(userId, request.toCommand());
        return ApiResponse.success(EventResponse.from(result));
    }

    @PutMapping("/{id}")
    public ApiResponse<Void> updateEvent(
            @PathVariable Long id,
            @RequestBody UpdateEventRequest request) {
        calendarEventService.updateEvent(id, request.toCommand());
        return ApiResponse.success();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteEvent(@PathVariable Long id) {
        calendarEventService.deleteEvent(id);
        return ApiResponse.success();
    }
}
