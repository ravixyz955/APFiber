package com.example.user.apfiber.network.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Pop implements Parcelable {
    @SerializedName("popDetail")
    private String popDetail;

    @SerializedName("zonal")
    private String zonal;

    @SerializedName("district")
    private String district;

    private Pop(Parcel in) {
        popDetail = in.readString();
        zonal = in.readString();
        district = in.readString();
    }

    public static final Creator<Pop> CREATOR = new Creator<Pop>() {
        @Override
        public Pop createFromParcel(Parcel in) {
            return new Pop(in);
        }

        @Override
        public Pop[] newArray(int size) {
            return new Pop[size];
        }
    };

    public Pop() {
    }

    public String getPopDetail() {
        return popDetail;
    }

    public void setPopDetail(String popDetail) {
        this.popDetail = popDetail;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(popDetail);
        dest.writeString(zonal);
        dest.writeString(district);
    }
}
