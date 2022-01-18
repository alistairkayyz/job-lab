package com.dso34bt.jobportal.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
@Table(name = "assessment_results")
public class AssessmentResults implements Serializable {
    @Id
    private Long id;

    @NotNull(message = "Results cannot be null")
    private double results;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_post_assessment_id", referencedColumnName = "id")
    private JobPostAssessment jobPostAssessment;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidate_id", referencedColumnName = "id")
    private Candidate candidate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getResults() {
        return results;
    }

    public void setResults(double results) {
        this.results = results;
    }

    public JobPostAssessment getJobPostAssessment() {
        return jobPostAssessment;
    }

    public void setJobPostAssessment(JobPostAssessment jobPostAssessment) {
        this.jobPostAssessment = jobPostAssessment;
    }

    public Candidate getCandidate() {
        return candidate;
    }

    public void setCandidate(Candidate candidate) {
        this.candidate = candidate;
    }

    @Override
    public String toString() {
        return "AssessmentResults{" +
                "id=" + id +
                ", results=" + results +
                ", jobPostAssessment=" + jobPostAssessment +
                ", candidate=" + candidate +
                '}';
    }
}
