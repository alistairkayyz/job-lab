package com.dso34bt.jobportal.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "candidate_emails")
public class CandidateEmails extends Emails implements Serializable {
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_email", referencedColumnName = "email")
    private CandidateAccount candidateAccount;

    public CandidateAccount getCandidateAccount() {
        return candidateAccount;
    }

    public void setCandidateAccount(CandidateAccount candidateAccount) {
        this.candidateAccount = candidateAccount;
    }
}
