package com.dso34bt.jobportal.repositories;

import com.dso34bt.jobportal.model.CandidateAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CandidateAccountRepository extends JpaRepository<CandidateAccount, Long> {
    Optional<CandidateAccount> findByEmail(String email);
    boolean existsByEmail(String email);
    void deleteByEmail(String email);

    @Query("select max(c.id) from CandidateAccount c")
    String getLastId();
}
