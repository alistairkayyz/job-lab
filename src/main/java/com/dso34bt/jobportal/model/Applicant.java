package com.dso34bt.jobportal.model;

import java.io.Serializable;

public class Applicant implements Serializable {
    private Candidate candidate;
    private JobPost jobPost;
    private JobPostActivity jobPostActivity;

    public Applicant(Candidate candidate, JobPost jobPost, JobPostActivity jobPostActivity) {
        this.candidate = candidate;
        this.jobPost = jobPost;
        this.jobPostActivity = jobPostActivity;
    }

    public Candidate getCandidate() {
        return candidate;
    }

    public void setCandidate(Candidate candidate) {
        this.candidate = candidate;
    }

    public JobPost getJobPost() {
        return jobPost;
    }

    public void setJobPost(JobPost jobPost) {
        this.jobPost = jobPost;
    }

    public JobPostActivity getJobPostActivity() {
        return jobPostActivity;
    }

    public void setJobPostActivity(JobPostActivity jobPostActivity) {
        this.jobPostActivity = jobPostActivity;
    }

    @Override
    public String toString() {
        return "Applicant{" +
                "candidate=" + candidate +
                ", jobPost=" + jobPost +
                ", jobPostActivity=" + jobPostActivity +
                '}';
    }
}
