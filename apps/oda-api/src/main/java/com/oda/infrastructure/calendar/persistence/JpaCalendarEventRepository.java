package com.oda.infrastructure.calendar.persistence;

import com.oda.domain.calendar.CalendarEvent;
import com.oda.domain.calendar.CalendarEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaCalendarEventRepository implements CalendarEventRepository {

    private final SpringDataCalendarEventRepository springDataRepository;
    private final CalendarEventMapper mapper;

    @Override
    public CalendarEvent save(CalendarEvent event) {
        CalendarEventJpaEntity entity = mapper.toEntity(event);
        CalendarEventJpaEntity saved = springDataRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<CalendarEvent> findById(Long id) {
        return springDataRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<CalendarEvent> findByUserIdAndStartDateBetween(Long userId, LocalDate start, LocalDate end) {
        return springDataRepository.findByUserIdAndStartDateBetween(userId, start, end)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public void deleteById(Long id) {
        springDataRepository.deleteById(id);
    }
}
