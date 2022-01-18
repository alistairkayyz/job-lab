package com.dso34bt.jobportal.services;

import com.dso34bt.jobportal.model.Staff;
import com.dso34bt.jobportal.repositories.StaffRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StaffService {
    private final StaffRepository repository;

    public StaffService(StaffRepository repository) {
        this.repository = repository;
    }

    public Optional<Staff> getStaffByEmail(String email){
        return repository.findByStaffAccountEmail(email);
    }

    public boolean existsByEmail(String email){
        return repository.existsByStaffAccountEmail(email);
    }

    public boolean saveStaff(Staff staff){
        repository.save(staff);

        return repository.existsById(staff.getId());
    }

    public void saveAll(List<Staff> staff){
        repository.saveAll(staff);
    }

    public List<Staff> getStaff(){
        return repository.findAll();
    }

    public boolean deleteStaff(long id){
        repository.deleteById(id);

        return repository.existsById(id);
    }

    public long getLastId(){
        if (repository.lastId() == null)
            return 0;
        return Long.parseLong(repository.lastId());
    }
}
