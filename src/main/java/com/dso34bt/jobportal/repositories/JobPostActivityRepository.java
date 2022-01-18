package com.dso34bt.jobportal.repositories;

import com.dso34bt.jobportal.model.JobPostActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface JobPostActivityRepository extends JpaRepository<JobPostActivity,Long> {
    List<JobPostActivity> findByCandidateId(long id);
    List<JobPostActivity> findByJobPostId(long id);
    boolean existsByCandidate_Id(long id);
    void deleteByCandidateIdAndJobPostId(long candidateId, long jobPostId);

    @Query("select max(c.id) from JobPostActivity c")
    String lastId();

    boolean existsByJobPostIdAndCandidateId(long jobPostId, long candidateId);
}
