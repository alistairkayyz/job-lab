package com.dso34bt.jobportal.services;

import com.dso34bt.jobportal.model.JobPostActivity;
import com.dso34bt.jobportal.repositories.JobPostActivityRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class JobPostActivityService {

    private final JobPostActivityRepository repository;

    public JobPostActivityService(JobPostActivityRepository repository) {
        this.repository = repository;
    }

    public List<JobPostActivity> getByJobPostId(long id) {
        return repository.findByJobPostId(id);
    }

    public List<JobPostActivity> findByJobPostRecruiterEmail(String email){
        return repository.findByJobPostRecruiterEmail(email);
    }

    public List<JobPostActivity> getByCandidateId(long id) {
        return repository.findByCandidateId(id);
    }

    public Optional<JobPostActivity> getById(long id) {
        return repository.findById(id);
    }

    public List<JobPostActivity> getJobPostActivities() {
        return repository.findAll();
    }

    public boolean saveJobPostActivity(JobPostActivity jobPostActivity) {
        repository.save(jobPostActivity);

        return repository.existsById(jobPostActivity.getId());
    }

    public boolean existsByRecruiterEmail(String email){
        return repository.existsByJobPostRecruiterEmail(email);
    }
    public boolean deleteByCandidateIdAndJobPostId(long candidateId, long jobPostId) {
        repository.deleteByCandidateIdAndJobPostId(candidateId, jobPostId);

        return repository.existsByCandidate_Id(candidateId);
    }

    public boolean existsByCandidateId(long id){
        return repository.existsByCandidate_Id(id);
    }

    public boolean existsById(long id) { return repository.existsById(id); }

    public boolean applied(long jobPostId, long candidateId){
        return repository.existsByJobPostIdAndCandidateId(jobPostId, candidateId);
    }

    public long getLastId() {
        if (repository.lastId() == null)
            return 0;
        return Long.parseLong(repository.lastId());
    }
}
