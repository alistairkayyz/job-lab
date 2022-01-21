package com.dso34bt.jobportal.services;

import com.dso34bt.jobportal.model.Recruiter;
import com.dso34bt.jobportal.repositories.RecruiterRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RecruiterService {
    private final RecruiterRepository repository;

    public RecruiterService(RecruiterRepository repository) {
        this.repository = repository;
    }

    public Optional<Recruiter> getRecruiterByEmail(String email){
        return repository.findByEmail(email);
    }

    public Optional<Recruiter> getRecruiterById(long id){
        return repository.findById(id);
    }

    public boolean existsByEmail(String email){
        return repository.existsByEmail(email);
    }

    public boolean existsByWebsiteLink(String websiteLink){
        return repository.existsByWebsiteLink(websiteLink);
    }
    public boolean existsByCompanyName(String companyName){
        return repository.existsByCompanyName(companyName);
    }

    public boolean saveRecruiter(Recruiter recruiter){
        repository.save(recruiter);

        return repository.existsByEmail(recruiter.getEmail());
    }

    public boolean deleteByEmail(String email){
        repository.deleteByEmail(email);

        return repository.existsByEmail(email);
    }

    public long getLastId(){
        if (repository.lastId() == null)
            return 0;

        return Long.parseLong(repository.lastId());
    }
}
