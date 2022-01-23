package com.dso34bt.jobportal.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.sql.Date;

@Entity
@Table
public class Candidate implements Serializable {
    @Id
    private Long id;

    @NotNull(message = "Title cannot be null")
    @Column(length = 10)
    private String title;

    @NotNull(message = "Firstname cannot be null")
    @Column(length = 50)
    private String first_name;

    @Column(length = 50)
    private String middle_name;

    @NotNull(message = "Last Name cannot be null")
    @Column(length = 50)
    private String last_name;

    @NotNull(message = "Date of Birth cannot be null")
    @Column(length = 20)
    private Date date_of_birth;

    @NotNull(message = "Language cannot be null")
    @Column(length = 25)
    private String language;

    @NotNull(message = "ID Number cannot be null")
    @Column(length = 50)
    private String id_number;

    @NotNull(message = "Gender cannot be null")
    @Column(length = 10)
    private String gender;

    @NotNull(message = "Licence cannot be null")
    @Column(length = 10, name = "drivers_licence")
    private boolean drivers_licence_valid;

    @NotNull(message = "Race cannot be null")
    @Column(length = 20)
    private String race;

    @JsonIgnore
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "email", referencedColumnName = "email", unique = true)
    private CandidateAccount candidateAccount;

    @NotNull(message = "Telephone cannot be null")
    @Column(length = 50)
    private String telephone;

    @NotNull(message = "Cellphone cannot be null")
    @Column(length = 50)
    private String cellphone;

    @NotNull(message = "Street Address cannot be null")
    @Column(length = 150)
    private String street_address;

    @NotNull(message = "Suburb cannot be null")
    @Column(length = 45)
    private String suburb;

    @NotNull(message = "City cannot be null")
    @Column(length = 45)
    private String city;

    @NotNull(message = "Province cannot be null")
    @Column(length = 45)
    private String province;

    @NotNull(message = "Country cannot be null")
    @Column(length = 45)
    private String country;

    @NotNull(message = "Post Code cannot be null")
    @Column(length = 5)
    private String post_code;

    @Transient
    private String action;

    public Candidate() {
    }

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getMiddle_name() {
        return middle_name;
    }

    public void setMiddle_name(String middle_name) {
        this.middle_name = middle_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public Date getDate_of_birth() {
        return date_of_birth;
    }

    public void setDate_of_birth(Date date_of_birth) {
        this.date_of_birth = date_of_birth;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getId_number() {
        return id_number;
    }

    public void setId_number(String id_number) {
        this.id_number = id_number;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public boolean isDrivers_licence_valid() {
        return drivers_licence_valid;
    }

    public void setDrivers_licence_valid(boolean drivers_licence_valid) {
        this.drivers_licence_valid = drivers_licence_valid;
    }

    public String getRace() {
        return race;
    }

    public void setRace(String race) {
        this.race = race;
    }

    public CandidateAccount getCandidateAccount() {
        return candidateAccount;
    }

    public void setCandidateAccount(CandidateAccount candidateAccount) {
        this.candidateAccount = candidateAccount;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getCellphone() {
        return cellphone;
    }

    public void setCellphone(String cellphone) {
        this.cellphone = cellphone;
    }

    public String getStreet_address() {
        return street_address;
    }

    public void setStreet_address(String street_address) {
        this.street_address = street_address;
    }

    public String getSuburb() {
        return suburb;
    }

    public void setSuburb(String suburb) {
        this.suburb = suburb;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPost_code() {
        return post_code;
    }

    public void setPost_code(String post_code) {
        this.post_code = post_code;
    }

    @Override
    public String toString() {
        return "Candidate{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", first_name='" + first_name + '\'' +
                ", middle_name='" + middle_name + '\'' +
                ", last_name='" + last_name + '\'' +
                ", date_of_birth=" + date_of_birth +
                ", language='" + language + '\'' +
                ", id_number='" + id_number + '\'' +
                ", gender='" + gender + '\'' +
                ", drivers_licence_valid=" + drivers_licence_valid +
                ", race='" + race + '\'' +
                ", candidateAccount=" + candidateAccount +
                ", telephone='" + telephone + '\'' +
                ", cellphone='" + cellphone + '\'' +
                ", street_address='" + street_address + '\'' +
                ", suburb='" + suburb + '\'' +
                ", city='" + city + '\'' +
                ", province='" + province + '\'' +
                ", country='" + country + '\'' +
                ", post_code='" + post_code + '\'' +
                '}';
    }
}
