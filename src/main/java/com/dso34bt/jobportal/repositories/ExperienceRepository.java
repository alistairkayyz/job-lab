package com.dso34bt.jobportal.repositories;

import com.dso34bt.jobportal.model.Experience;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ExperienceRepository extends JpaRepository<Experience,Long> {
    List<Experience> findByCandidateAccountEmail(String email);

    boolean existsByCandidateAccountEmail(String email);

    @Query("select max(c.id) from Experience c")
    String lastId();
}
