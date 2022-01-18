package com.dso34bt.jobportal.services;

import com.dso34bt.jobportal.model.CandidateAccount;
import com.dso34bt.jobportal.repositories.CandidateAccountRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CandidateAccountService {
    private final CandidateAccountRepository repository;

    public CandidateAccountService(CandidateAccountRepository repository) {
        this.repository = repository;
    }

    public Optional<CandidateAccount> getUserAccountByEmail(String email){
        return repository.findByEmail(email);
    }

    public boolean existsByEmail(String email){
        return repository.existsByEmail(email);
    }

    public boolean saveCandidateAccount(CandidateAccount candidateAccount){
        repository.save(candidateAccount);

        return repository.existsByEmail(candidateAccount.getEmail());
    }

    public boolean deleteByEmail(String email){
        repository.deleteByEmail(email);

        return repository.existsByEmail(email);
    }

    public long getLastId(){
        if (repository.getLastId() == null)
            return 0;
        return Long.parseLong(repository.getLastId());
    }
}
