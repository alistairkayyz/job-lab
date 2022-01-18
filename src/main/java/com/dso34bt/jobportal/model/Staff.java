package com.dso34bt.jobportal.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
@Table
public class Staff implements Serializable {
    @Id
    private Long id;

    @NotNull(message = "Firstname cannot be null")
    @Column(length = 50)
    private String first_name;

    @NotNull(message = "Last Name cannot be null")
    @Column(length = 50)
    private String last_name;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "email", referencedColumnName = "email", unique = true)
    private StaffAccount staffAccount;

    @NotNull(message = "Designation cannot be null")
    @Column(length = 25)
    private String designation;

    public Staff() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public StaffAccount getStaffAccount() {
        return staffAccount;
    }

    public void setStaffAccount(StaffAccount staffAccount) {
        this.staffAccount = staffAccount;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    @Override
    public String toString() {
        return "Staff{" +
                "id=" + id +
                ", first_name='" + first_name + '\'' +
                ", last_name='" + last_name + '\'' +
                ", staffAccount=" + staffAccount +
                ", designation='" + designation + '\'' +
                '}';
    }
}
