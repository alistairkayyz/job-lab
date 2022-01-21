package com.dso34bt.jobportal.services;

import com.dso34bt.jobportal.model.JobPost;
import com.dso34bt.jobportal.repositories.JobPostRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class JobPostService {
    private final JobPostRepository repository;

    public JobPostService(JobPostRepository repository) {
        this.repository = repository;
    }

    public Optional<JobPost> getJobPostByID(long id){
        return repository.findById(id);
    }

    public List<JobPost> getJobPosts(){
        return repository.findAll();
    }

    public boolean existsById(long id){
        return repository.existsById(id);
    }

    public boolean existsByRecruiterEmail(String email){
        return repository.existsByRecruiterEmail(email);
    }

    public boolean saveJobPost(JobPost jobPost){
        repository.save(jobPost);

        return repository.existsById(jobPost.getId());
    }

    public List<JobPost> getByRecruiterId(long id){
        return repository.findByRecruiterId(id);
    }

    public Optional<JobPost> findByTitle(String title){
        return repository.findByTitle(title);
    }

    public boolean deleteJobPostById(long id){
        repository.deleteById(id);

        return !repository.existsById(id);
    }

    public long getLastId(){
        if (repository.lastId() == null)
            return 0;
        return Long.parseLong(repository.lastId());
    }
}
