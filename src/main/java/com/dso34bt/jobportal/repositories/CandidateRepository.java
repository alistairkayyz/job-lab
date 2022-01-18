package com.dso34bt.jobportal.repositories;

import com.dso34bt.jobportal.model.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CandidateRepository extends JpaRepository<Candidate, Long> {

    Optional<Candidate> findByCandidateAccountEmail(String email);
    boolean existsByCandidateAccountEmail(String email);

    void deleteByCandidateAccountEmail(String email);

    @Query("select max(c.id) from Candidate c")
    String lastId();
}
