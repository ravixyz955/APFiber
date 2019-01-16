package com.example.user.apfiber.network.model;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;

public class FeasibleSurvey extends RealmObject implements Parcelable {

    public static final Creator<FeasibleSurvey> CREATOR = new Creator<FeasibleSurvey>() {
        @Override
        public FeasibleSurvey createFromParcel(Parcel in) {
            return new FeasibleSurvey(in);
        }

        @Override
        public FeasibleSurvey[] newArray(int size) {
            return new FeasibleSurvey[size];
        }
    };

    @PrimaryKey
    private String uid;

    private RealmList<String> typeStr;

    private RealmList<String> uriStr;

    private RealmList<String> ts;

    private RealmList<String> latitude;

    private RealmList<String> longtitude;

    private int offSet;

    public FeasibleSurvey() {
    }

    private FeasibleSurvey(Parcel in) {
        uid = in.readString();
        typeStr = new RealmList<>();
        uriStr = new RealmList<>();
        typeStr.addAll(in.createStringArrayList());
        uriStr.addAll(in.createStringArrayList());
        ts.addAll(in.createStringArrayList());
        latitude.addAll(in.createStringArrayList());
        longtitude.addAll(in.createStringArrayList());
    }

    @Override

    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uid);
        dest.writeStringList(typeStr);
        dest.writeStringList(uriStr);
        dest.writeStringList(ts);
        dest.writeStringList(latitude);
        dest.writeStringList(longtitude);
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getOffSet() {
        return offSet;
    }

    public void setOffSet(int offSet) {
        this.offSet = offSet;
    }

    public RealmList<String> getTypeStr() {
        return typeStr;
    }

    public void setTypeStr(RealmList<String> typeStr) {
        this.typeStr = typeStr;
    }

    public RealmList<String> getUriStr() {
        return uriStr;
    }

    public void setUriStr(RealmList<String> uriStr) {
        this.uriStr = uriStr;
    }

    public RealmList<String> getTs() {
        return ts;
    }

    public void setTs(RealmList<String> ts) {
        this.ts = ts;
    }

    public RealmList<String> getLatitude() {
        return latitude;
    }

    public void setLatitude(RealmList<String> latitude) {
        this.latitude = latitude;
    }

    public RealmList<String> getLongtitude() {
        return longtitude;
    }

    public void setLongtitude(RealmList<String> longtitude) {
        this.longtitude = longtitude;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
