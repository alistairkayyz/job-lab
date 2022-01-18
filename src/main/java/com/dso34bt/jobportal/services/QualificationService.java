package com.dso34bt.jobportal.services;

import com.dso34bt.jobportal.model.Qualifications;
import com.dso34bt.jobportal.repositories.QualificationsRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class QualificationService {
    private final QualificationsRepository repository;

    public QualificationService(QualificationsRepository repository) {
        this.repository = repository;
    }

    public List<Qualifications> findByCandidateEmail(String email){
        return repository.findByCandidateAccountEmail(email);
    }

    public Optional<Qualifications> findByID(long id){
        return repository.findById(id);
    }

    public boolean existsByID(long id){
        return repository.existsById(id);
    }

    public boolean existsByEmail(String email){
        return repository.existsByCandidateAccountEmail(email);
    }

    public boolean saveQualification(Qualifications qualifications){
        repository.save(qualifications);

        return repository.existsById(qualifications.getId());
    }

    public boolean deleteQualification(long id){
        repository.deleteById(id);

        return !repository.existsById(id);
    }

    public long getLastId(){
        if (repository.lastId() == null)
            return 0;
        return Long.parseLong(repository.lastId());
    }
}
