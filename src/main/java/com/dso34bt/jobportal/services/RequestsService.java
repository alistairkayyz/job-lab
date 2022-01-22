package com.dso34bt.jobportal.services;

import com.dso34bt.jobportal.model.Requests;
import com.dso34bt.jobportal.repositories.RequestsRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RequestsService {
    private final RequestsRepository repository;

    public RequestsService(RequestsRepository repository) {
        this.repository = repository;
    }
    public List<Requests> findByRecruiterEmail(String email){
        return repository.findByRecruiterEmail(email);
    }
    public List<Requests> findByCandidateEmail(String email){
        return repository.findByCandidateCandidateAccountEmail(email);
    }
    public boolean save(Requests request){
        repository.save(request);
        return repository.existsById(request.getId());
    }
    public List<Requests> findByRecruiterEmailAndCandidateEmail(String recruiterEmail, String candidateEmail){
        return repository.findByRecruiterEmailAndCandidateCandidateAccountEmail(recruiterEmail, candidateEmail);
    }

    public List<Requests> findRequests(long id, String title, String status){
        return repository.findRequests(id, title, status);
    }

    public boolean existsByCandidateEmail(String email){
        return repository.existsByCandidateCandidateAccountEmail(email);
    }

    public List<String> findTitlesByCandidateIdAndStatus(long id, String status){
        return repository.findTitlesByCandidateIdAndStatus(id, status);
    }

    public List<Requests> findByCandidateIdAndStatu(long id, String status){
        return repository.findByCandidateIdAndStatus(id, status);
    }

    public long getLastId(){
        if (repository.lastId() == null)
            return 0;

        return Long.parseLong(repository.lastId());
    }
}
