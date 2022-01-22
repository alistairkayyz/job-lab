package com.dso34bt.jobportal.repositories;

import com.dso34bt.jobportal.model.RecruiterEmails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RecruiterEmailsRepository extends JpaRepository<RecruiterEmails, Long> {
    @Query("select max(c.id) from RecruiterEmails c")
    String lastId();
}
