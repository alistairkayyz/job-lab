package com.dso34bt.jobportal.services;

import com.dso34bt.jobportal.model.RecruiterEmails;
import com.dso34bt.jobportal.repositories.RecruiterEmailsRepository;
import org.springframework.stereotype.Service;

@Service
public class RecruiterEmailsService {
    private final RecruiterEmailsRepository repository;

    public RecruiterEmailsService(RecruiterEmailsRepository repository) {
        this.repository = repository;
    }

    public long getLastId(){
        if (repository.lastId() == null)
            return 0;

        return Long.parseLong(repository.lastId());
    }

    public boolean save(RecruiterEmails email) {
        repository.save(email);
        return repository.existsById(email.getId());
    }
}
