package com.dso34bt.jobportal.repositories;

import com.dso34bt.jobportal.model.JobPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

public interface JobPostRepository extends JpaRepository<JobPost,Long> {
    List<JobPost> findByRecruiterId(long id);

    @Query("select j from JobPost j where j.closingDate < ?1")
    List<JobPost> getJobPostsAfterClosingDate(Timestamp now);

    boolean existsByRecruiterEmail(String email);

    Optional<JobPost> findByTitle(String title);

    @Query("select max(c.id) from JobPost c")
    String lastId();
}
