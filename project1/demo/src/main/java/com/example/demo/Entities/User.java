package com.example.demo.Entities;

import jakarta.persistence.Entity;

@Entity
public class User {
    @jakarta.persistence.Id
    private Long accountId;
    private String firstName;
    private String lastName;


    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountID) {
        this.accountId = accountID;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String toString(){
        return accountId +". "+ firstName +" "+ lastName;
    }
}
