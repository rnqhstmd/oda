package com.oda.infrastructure.job.persistence;

import com.oda.domain.job.JobPosting;
import com.oda.domain.job.JobSource;
import com.oda.domain.job.JobPostingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaJobPostingRepository implements JobPostingRepository {

    private final SpringDataJobPostingRepository springDataRepository;
    private final JobPostingMapper mapper;

    @Override
    public JobPosting save(JobPosting jobPosting) {
        JobPostingJpaEntity entity = mapper.toEntity(jobPosting);
        JobPostingJpaEntity saved = springDataRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<JobPosting> findById(Long id) {
        return springDataRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Page<JobPosting> findAll(Pageable pageable) {
        return springDataRepository.findAll(pageable).map(mapper::toDomain);
    }

    @Override
    public List<JobPosting> findBySource(JobSource source) {
        return springDataRepository.findBySource(source).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<JobPosting> findByActiveTrue() {
        return springDataRepository.findByActiveTrue().stream()
                .map(mapper::toDomain)
                .toList();
    }
}
