package com.dso34bt.jobportal.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.sql.Date;

@Entity
@Table
public class Experience implements Serializable {
    @Id
    private Long id;

    @NotNull(message = "Current Job Status cannot be null")
    @Column( name = "is_current_job")
    private boolean current_job;

    @NotNull(message = "Job Title cannot be null")
    @Column(length = 100, name = "job_title")
    private String jobTitle;

    @NotNull(message = "Description cannot be null")
    @Column(length = 5000)
    private String description;

    @NotNull(message = "Company Name cannot be null")
    @Column(length = 100, name = "company_name")
    private String companyName;

    @NotNull(message = "Start Date cannot be null")
    @Column(name = "start_date")
    private Date startDate;

    @NotNull(message = "End Date cannot be null")
    @Column(name = "end_date")
    private Date endDate;

    @NotNull(message = "Notice Period cannot be null")
    @Column(name = "notice_period")
    private String noticePeriod;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidate_email", referencedColumnName = "email")
    private CandidateAccount candidateAccount;

    @Transient
    private String action;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isCurrent_job() {
        return current_job;
    }

    public void setCurrent_job(boolean current_job) {
        this.current_job = current_job;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getNoticePeriod() {
        return noticePeriod;
    }

    public void setNoticePeriod(String noticePeriod) {
        this.noticePeriod = noticePeriod;
    }

    public CandidateAccount getCandidateAccount() {
        return candidateAccount;
    }

    public void setCandidateAccount(CandidateAccount candidateAccount) {
        this.candidateAccount = candidateAccount;
    }

    @Override
    public String toString() {
        return "Experience{" +
                "id=" + id +
                ", current_job=" + current_job +
                ", jobTitle='" + jobTitle + '\'' +
                ", description='" + description + '\'' +
                ", companyName='" + companyName + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", noticePeriod='" + noticePeriod + '\'' +
                ", candidateAccount=" + candidateAccount +
                ", action='" + action + '\'' +
                '}';
    }
}
