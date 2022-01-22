package com.dso34bt.jobportal.repositories;

import com.dso34bt.jobportal.model.Requests;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RequestsRepository extends JpaRepository<Requests, Long> {
    List<Requests> findByRecruiterEmail(String email);
    List<Requests> findByCandidateCandidateAccountEmail(String email);
    List<Requests> findByRecruiterEmailAndCandidateCandidateAccountEmail(String recruiterEmail, String candidateEmail);

    @Query("select r from Requests r where r.candidate.id = ?1 and " +
            "r.documentTitle = ?2 and r.status = ?3")
    List<Requests> findRequests(long id, String title, String status);

    @Query("select distinct (r.documentTitle) from Requests r where r.candidate.id = ?1 and r.status = ?2")
    List<String> findTitlesByCandidateIdAndStatus(long id, String status);

    List<Requests> findByCandidateIdAndStatus(long id, String status);

    boolean existsByCandidateCandidateAccountEmail(String email);

    @Query("select max(c.id) from Requests c")
    String lastId();
}
