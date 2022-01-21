package com.dso34bt.jobportal.repositories;

import com.dso34bt.jobportal.model.Recruiter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RecruiterRepository extends JpaRepository<Recruiter, Long> {
    Optional<Recruiter> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByWebsiteLink(String websiteLink);
    boolean existsByCompanyName(String companyName);
    void deleteByEmail(String email);

    @Query("select max(c.id) from Recruiter c")
    String lastId();
}
