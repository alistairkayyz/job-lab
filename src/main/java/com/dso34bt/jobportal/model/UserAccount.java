package com.dso34bt.jobportal.model;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.sql.Timestamp;

@MappedSuperclass
public class UserAccount implements Serializable {
    @Id
    private Long id;

    @NotNull(message = "Email cannot be null")
    @Email(message = "Invalid Email")
    @Column(length = 100, unique = true)
    private String email;

    @NotNull(message = "Password cannot be null")
    @Column(length = 20)
    private String password;

    @Transient
    private String confirmPassword;

    @Column(length = 10, name = "email_notification_active")
    private boolean emailNotificationActive;

    @Column(length = 10, name = "registration_date")
    private Timestamp registrationDate;

    @Column(length = 10, name = "last_login_date")
    private Timestamp lastLoginDate;

    public UserAccount() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isEmailNotificationActive() {
        return emailNotificationActive;
    }

    public void setEmailNotificationActive(boolean emailNotificationActive) {
        this.emailNotificationActive = emailNotificationActive;
    }

    public Timestamp getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Timestamp registrationDate) {
        this.registrationDate = registrationDate;
    }

    public Timestamp getLastLoginDate() {
        return lastLoginDate;
    }

    public void setLastLoginDate(Timestamp lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }

    @Override
    public String toString() {
        return "UserAccount{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", emailNotificationActive=" + emailNotificationActive +
                ", registrationDate=" + registrationDate +
                ", lastLoginDate=" + lastLoginDate ;
    }

}
