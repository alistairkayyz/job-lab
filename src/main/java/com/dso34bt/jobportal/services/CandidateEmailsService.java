package com.dso34bt.jobportal.services;

import com.dso34bt.jobportal.model.CandidateEmails;
import com.dso34bt.jobportal.repositories.CandidateEmailsRepository;
import org.springframework.stereotype.Service;

@Service
public class CandidateEmailsService {
    private final CandidateEmailsRepository repository;

    public CandidateEmailsService(CandidateEmailsRepository repository) {
        this.repository = repository;
    }
    public boolean save(CandidateEmails candidateEmail){
        repository.save(candidateEmail);
        return repository.existsById(candidateEmail.getId());
    }
    public long getLastId(){
        if (repository.lastId() == null)
            return 0;

        return Long.parseLong(repository.lastId());
    }
}
