package com.dso34bt.jobportal.model;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "candidate_account")
public class CandidateAccount extends UserAccount implements Serializable {

    @Override
    public String toString() {
        return super.toString() + "}";
    }
}
