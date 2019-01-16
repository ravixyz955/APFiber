package com.example.user.apfiber.network.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by KT on 27/12/15.
 */
public class AuthenticateUserRequest {

    @SerializedName("contact")
    private String contact;

    @SerializedName("password")
    private String password;

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
