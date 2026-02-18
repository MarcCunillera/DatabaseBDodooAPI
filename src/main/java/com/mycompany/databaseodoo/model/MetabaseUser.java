/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.databaseodoo.model;

/**
 *
 * @author marccunillera
 */
public class MetabaseUser {

    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private boolean isActive;
    private boolean isSuperuser;
    private String dateJoined;
    private String lastLogin;

    public MetabaseUser() {
    }

    public MetabaseUser(int id, String firstName, String lastName,
            String email, boolean isActive, boolean isSuperuser) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.isActive = isActive;
        this.isSuperuser = isSuperuser;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public boolean isActive() {
        return isActive;
    }

    public boolean isSuperuser() {
        return isSuperuser;
    }

    public String getDateJoined() {
        return dateJoined;
    }

    public String getLastLogin() {
        return lastLogin;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setActive(boolean active) {
        this.isActive = active;
    }

    public void setSuperuser(boolean superuser) {
        this.isSuperuser = superuser;
    }

    public void setDateJoined(String dateJoined) {
        this.dateJoined = dateJoined;
    }

    public void setLastLogin(String lastLogin) {
        this.lastLogin = lastLogin;
    }

    @Override
    public String toString() {
        return String.format(
                "MetabaseUser{id=%d, name='%s', email='%s', active=%s, superuser=%s}",
                id, getFullName(), email, isActive, isSuperuser);
    }
}
