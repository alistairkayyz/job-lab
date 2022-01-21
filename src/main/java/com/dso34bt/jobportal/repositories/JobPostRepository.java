package com.dso34bt.jobportal.repositories;

import com.dso34bt.jobportal.model.JobPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface JobPostRepository extends JpaRepository<JobPost,Long> {
    List<JobPost> findByRecruiterId(long id);

    Optional<JobPost> findByTitle(String title);

    @Query("select max(c.id) from JobPost c")
    String lastId();
}
