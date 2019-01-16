package com.example.user.apfiber.network.model;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

/**
 * Created by KT on 23/12/15.
 */
public class User {

    @SerializedName("_id")
    private String id;

    @SerializedName("splicerName")
    private String splicerName;

    @SerializedName("zonal")
    private String zonal;

    @SerializedName("district")
    private String district;

    @SerializedName("contact")
    private String contact;


    private String token;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getSplicerName() {
        return splicerName;
    }

    public void setSplicerName(String splicerName) {
        this.splicerName = splicerName;
    }

    public String getZonal() {
        return zonal;
    }

    public void setZonal(String zonal) {
        this.zonal = zonal;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}