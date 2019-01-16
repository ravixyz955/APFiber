package com.example.user.apfiber.network.model;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;

public class InstallationSurvey extends RealmObject implements Parcelable {

    public static final Creator<InstallationSurvey> CREATOR = new Creator<InstallationSurvey>() {
        @Override
        public InstallationSurvey createFromParcel(Parcel in) {
            return new InstallationSurvey(in);
        }

        @Override
        public InstallationSurvey[] newArray(int size) {
            return new InstallationSurvey[size];
        }
    };

    public InstallationSurvey() {
    }

    private InstallationSurvey(Parcel in) {
        uid = in.readString();
        uriStr = new RealmList<>();
        ts.addAll(in.createStringArrayList());
        latitude.addAll(in.createStringArrayList());
        longtitude.addAll(in.createStringArrayList());
        uriStr.addAll(in.createStringArrayList());
        gpon_serial = in.readString();
        optional_box = in.readInt();
        caf_id = in.readString();
        telephone_num = in.readString();
        wifi_pwd = in.readString();
        net_speed = in.readString();
    }

    @PrimaryKey
    private String uid;

    private RealmList<String> uriStr;

    private RealmList<String> ts;

    private RealmList<String> latitude;

    private RealmList<String> longtitude;

    private int offSet;

    private String gpon_serial;

    private int optional_box;

    private String caf_id;

    private String telephone_num;

    private String wifi_pwd;

    private String net_speed;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public RealmList<String> getUriStr() {
        return uriStr;
    }

    public void setUriStr(RealmList<String> uriStr) {
        this.uriStr = uriStr;
    }

    public String getGpon_serial() {
        return gpon_serial;
    }

    public void setGpon_serial(String gpon_serial) {
        this.gpon_serial = gpon_serial;
    }

    public int getOptional_box() {
        return optional_box;
    }

    public void setOptional_box(int optional_box) {
        this.optional_box = optional_box;
    }

    public String getCaf_id() {
        return caf_id;
    }

    public void setCaf_id(String caf_id) {
        this.caf_id = caf_id;
    }

    public String getTelephone_num() {
        return telephone_num;
    }

    public void setTelephone_num(String telephone_num) {
        this.telephone_num = telephone_num;
    }

    public String getWifi_pwd() {
        return wifi_pwd;
    }

    public void setWifi_pwd(String wifi_pwd) {
        this.wifi_pwd = wifi_pwd;
    }

    public String getNet_speed() {
        return net_speed;
    }

    public void setNet_speed(String net_speed) {
        this.net_speed = net_speed;
    }

    public int getOffSet() {
        return offSet;
    }

    public void setOffSet(int offSet) {
        this.offSet = offSet;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uid);
        dest.writeStringList(uriStr);
        dest.writeStringList(ts);
        dest.writeStringList(latitude);
        dest.writeStringList(longtitude);
        dest.writeString(gpon_serial);
        dest.writeInt(optional_box);
        dest.writeString(caf_id);
        dest.writeString(telephone_num);
        dest.writeString(wifi_pwd);
        dest.writeString(net_speed);
    }
}
