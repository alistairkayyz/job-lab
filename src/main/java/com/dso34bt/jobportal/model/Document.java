package com.dso34bt.jobportal.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Arrays;

@Entity
@Table(name = "documents")
public class Document implements Serializable {
    @Id
    private Long id;

    @NotNull(message = "File name cannot be null")
    @Column(unique = true)
    private String name;

    @NotNull(message = "Title name cannot be null")
    @Column(length = 20)
    private String title;

    @NotNull(message = "File size cannot be null")
    private long size;

    @Lob
    @Column(columnDefinition="BYTEA")
    @Type(type="org.hibernate.type.BinaryType")
    private byte[] content;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidate_email", referencedColumnName = "email")
    private CandidateAccount candidateAccount;

    public Document() {
    }

    public Document(Long id, String name, String title, long size) {
        this.id = id;
        this.name = name;
        this.title = title;
        this.size = size;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public CandidateAccount getCandidateAccount() {
        return candidateAccount;
    }

    public void setCandidateAccount(CandidateAccount candidateAccount) {
        this.candidateAccount = candidateAccount;
    }

    @Override
    public String toString() {
        return "Document{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", title='" + title + '\'' +
                ", size=" + size +
                ", candidateAccount=" + candidateAccount +
                '}';
    }
}
