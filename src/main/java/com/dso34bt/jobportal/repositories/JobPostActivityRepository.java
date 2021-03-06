package com.dso34bt.jobportal.repositories;

import com.dso34bt.jobportal.model.JobPostActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface JobPostActivityRepository extends JpaRepository<JobPostActivity,Long> {
    List<JobPostActivity> findByCandidateId(long id);
    List<JobPostActivity> findByJobPostId(long id);
    List<JobPostActivity> findByJobPostRecruiterEmail(String email);
    List<JobPostActivity> findByCandidateIdAndStatus(long id, String status);
    List<JobPostActivity> findByJobPostIdAndStatus(long id, String status);
    Optional<JobPostActivity> findByCandidateIdAndJobPostId(long candidateId, long jobPostId);
    boolean existsByCandidate_Id(long id);
    boolean existsByJobPostRecruiterEmail(String email);
    void deleteByCandidateIdAndJobPostId(long candidateId, long jobPostId);

    @Query("select max(c.id) from JobPostActivity c")
    String lastId();

    boolean existsByJobPostIdAndCandidateId(long jobPostId, long candidateId);
}
