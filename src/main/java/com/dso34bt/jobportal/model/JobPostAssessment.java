package com.dso34bt.jobportal.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
@Table(name = "job_post_assessment")
public class JobPostAssessment implements Serializable {
    @Id
    private Long id;

    @NotNull(message = "Assessment Link cannot be null")
    @Column(name = "assessment_link")
    private String assessmentLink;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_post_id", referencedColumnName = "id")
    private JobPost jobPost;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAssessmentLink() {
        return assessmentLink;
    }

    public void setAssessmentLink(String assessmentLink) {
        this.assessmentLink = assessmentLink;
    }

    public JobPost getJobPost() {
        return jobPost;
    }

    public void setJobPost(JobPost jobPost) {
        this.jobPost = jobPost;
    }

    @Override
    public String toString() {
        return "JobPostAssessment{" +
                "id=" + id +
                ", assessmentLink='" + assessmentLink + '\'' +
                ", jobPost=" + jobPost +
                '}';
    }
}
