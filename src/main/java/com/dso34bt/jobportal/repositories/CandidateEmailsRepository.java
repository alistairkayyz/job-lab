package com.dso34bt.jobportal.repositories;

import com.dso34bt.jobportal.model.CandidateEmails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CandidateEmailsRepository extends JpaRepository<CandidateEmails, Long> {
    @Query("select max(c.id) from CandidateEmails c")
    String lastId();
}
