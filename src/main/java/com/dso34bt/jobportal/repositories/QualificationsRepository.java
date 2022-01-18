package com.dso34bt.jobportal.repositories;

import com.dso34bt.jobportal.model.Qualifications;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface QualificationsRepository extends JpaRepository<Qualifications,Long> {
    List<Qualifications> findByCandidateAccountEmail(String email);

    boolean existsByCandidateAccountEmail(String email);

    @Query("select max(c.id) from Qualifications c")
    String lastId();
}
