package com.dso34bt.jobportal.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.sql.Date;

@Entity
@Table
public class Qualifications implements Serializable {
    @Id
    private Long id;

    @NotNull(message = "Institution name cannot be null")
    @Column(length = 100)
    private String institutionName;

    @NotNull(message = "Qualifications name Status cannot be null")
    @Column(length = 100)
    private String qualificationName;

    @NotNull(message = "Major Status cannot be null")
    @Column(length = 50)
    private String major;

    @NotNull(message = "Start Date cannot be null")
    @Column(name = "start_date")
    private Date startDate;

    @NotNull(message = "Completion Date cannot be null")
    @Column(name = "completion_date")
    private Date completionDate;

    @NotNull(message = "Aggregate cannot be null")
    private double aggregate;

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

    public String getInstitutionName() {
        return institutionName;
    }

    public void setInstitutionName(String institutionName) {
        this.institutionName = institutionName;
    }

    public String getQualificationName() {
        return qualificationName;
    }

    public void setQualificationName(String qualificationName) {
        this.qualificationName = qualificationName;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(Date completionDate) {
        this.completionDate = completionDate;
    }

    public double getAggregate() {
        return aggregate;
    }

    public void setAggregate(double aggregate) {
        this.aggregate = aggregate;
    }

    public CandidateAccount getCandidateAccount() {
        return candidateAccount;
    }

    public void setCandidateAccount(CandidateAccount candidateAccount) {
        this.candidateAccount = candidateAccount;
    }

    @Override
    public String toString() {
        return "Qualifications{" +
                "id=" + id +
                ", institutionName='" + institutionName + '\'' +
                ", qualificationName='" + qualificationName + '\'' +
                ", major='" + major + '\'' +
                ", startDate=" + startDate +
                ", completionDate=" + completionDate +
                ", aggregate=" + aggregate +
                ", candidateAccount=" + candidateAccount +
                ", action='" + action + '\'' +
                '}';
    }
}
