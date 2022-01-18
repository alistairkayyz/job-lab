package com.dso34bt.jobportal.services;

import com.dso34bt.jobportal.model.JobPostAssessment;
import com.dso34bt.jobportal.repositories.JobPostAssessmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class JobPostAssessmentService {
    private final JobPostAssessmentRepository repository;

    public JobPostAssessmentService(JobPostAssessmentRepository repository) {
        this.repository = repository;
    }

    public Optional<JobPostAssessment> getByJobPostId(long id){
        return repository.findByJobPostId(id);
    }

    public boolean existsByJobPostId(long id){
        return repository.existsByJobPostId(id);
    }

    public List<JobPostAssessment> getJobPostAssessments(){
        return repository.findAll();
    }

    public boolean saveJobPostAssessment(JobPostAssessment jobPostAssessment){
        repository.save(jobPostAssessment);

        return repository.existsByJobPostId(jobPostAssessment.getJobPost().getId());
    }

    public boolean deleteByJobPostId(long id){
        repository.deleteByJobPostId(id);

        return repository.existsByJobPostId(id);
    }

    public long getLastId(){
        if (repository.lastId() == null)
            return 0;
        return Long.parseLong(repository.lastId());
    }
}
