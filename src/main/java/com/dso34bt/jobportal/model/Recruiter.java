package com.dso34bt.jobportal.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
@Table
public class Recruiter extends UserAccount implements Serializable {
    @NotNull(message = "Firstname cannot be null")
    @Column(length = 100)
    private String firstname;

    @NotNull(message = "Lastname cannot be null")
    @Column(length = 100)
    private String lastname;

    @NotNull(message = "Company name cannot be null")
    @Column(length = 100)
    private String companyName;

    @NotNull(message = "Website link cannot be null")
    @Column(length = 255)
    private String websiteLink;

    @NotNull(message = "Role cannot be null")
    @Column(length = 100)
    private String role;

    @NotNull(message = "Cellphone cannot be null")
    @Column(length = 100)
    private String cellphone;

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getWebsiteLink() {
        return websiteLink;
    }

    public void setWebsiteLink(String websiteLink) {
        this.websiteLink = websiteLink;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getCellphone() {
        return cellphone;
    }

    public void setCellphone(String cellphone) {
        this.cellphone = cellphone;
    }

    @Override
    public String toString() {
        return "Recruiter{" +
                "firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", companyName='" + companyName + '\'' +
                ", websiteLink='" + websiteLink + '\'' +
                ", role='" + role + '\'' +
                ", cellphone='" + cellphone + '\'' +
                '}';
    }
}
