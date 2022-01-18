package com.dso34bt.jobportal.services;

import com.dso34bt.jobportal.model.Experience;
import com.dso34bt.jobportal.repositories.ExperienceRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ExperienceService {
    private final ExperienceRepository repository;

    public ExperienceService(ExperienceRepository repository) {
        this.repository = repository;
    }

    public Optional<Experience> findById(long id){
        return repository.findById(id);
    }

    public List<Experience> findByCandidateEmail(String email){
        return repository.findByCandidateAccountEmail(email);
    }

    public boolean existsByCandidateEmail(String email){
        return repository.existsByCandidateAccountEmail(email);
    }

    public boolean saveExperience(Experience experience){
        repository.save(experience);

        return repository.existsByCandidateAccountEmail(experience.getCandidateAccount().getEmail());
    }

    public boolean deleteExperience(long id){
        repository.deleteById(id);

        return !repository.existsById(id);
    }

    public long getLastId(){
        if (repository.lastId() == null)
            return 0;
        return Long.parseLong(repository.lastId());
    }
}
