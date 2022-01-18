package com.dso34bt.jobportal.services;

import com.dso34bt.jobportal.model.StaffAccount;
import com.dso34bt.jobportal.repositories.StaffAccountRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class StaffAccountService {
    private final StaffAccountRepository repository;

    public StaffAccountService(StaffAccountRepository repository) {
        this.repository = repository;
    }

    public Optional<StaffAccount> getUserAccountByEmail(String email){
        return repository.findByEmail(email);
    }

    public Optional<StaffAccount> getUserAccount(long id){
        return repository.findById(id);
    }

    public boolean existsByEmail(String email){
        return repository.existsByEmail(email);
    }

    public boolean saveStaffAccount(StaffAccount staffAccount){
        repository.save(staffAccount);

        return repository.existsByEmail(staffAccount.getEmail());
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
