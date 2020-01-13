package com.example.daniel.jobster.DataModels;

public class Profile
{
    private long id;
    private Integer gender;
    private String name, surname, birthday, state, city, phoneNumber, hobby, description;

    public final static int EMPLOYEE_ACCOUNT = 1;
    public final static int EMPLOYER_ACCOUNT = 2;

    public long getId() {
        return id;
    }

    public Integer getGender() {
        return gender;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getBirthday() {
        return birthday;
    }

    public String getState() {
        return state;
    }

    public String getCity() {
        return city;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getHobby() {
        return hobby;
    }

    public String getDescription() {
        return description;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setHobby(String hobby) {
        this.hobby = hobby;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
