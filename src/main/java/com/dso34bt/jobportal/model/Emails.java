package com.dso34bt.jobportal.model;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.sql.Timestamp;

@MappedSuperclass
public class Emails implements Serializable {
    @Id
    private Long id;

    @NotNull(message = "Subject cannot be null")
    @Column(length = 100)
    private String subject;

    @NotNull(message = "Email cannot be null")
    @Email(message = "Invalid Email")
    @Column(length = 100)
    private String senderEmail;

    @NotNull(message = "Message cannot be null")
    @Column(length = 5000)
    private String message;

    @NotNull(message = "Sent time cannot be null")
    private Timestamp timeSent;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Timestamp getTimeSent() {
        return timeSent;
    }

    public void setTimeSent(Timestamp timeSent) {
        this.timeSent = timeSent;
    }
}
