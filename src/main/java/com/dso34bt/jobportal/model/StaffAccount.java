package com.dso34bt.jobportal.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
@Table(name = "staff_account")
public class StaffAccount extends UserAccount implements Serializable {
    @NotNull(message = "Staff type cannot be null")
    @Column(length = 20)
    private String staffType;

    public String getStaffType() {
        return staffType;
    }

    public void setStaffType(String staffType) {
        this.staffType = staffType;
    }

    @Override
    public String toString() {
        return super.toString() +
                ", staffType='" + staffType + '\'' +
                '}';
    }
}
