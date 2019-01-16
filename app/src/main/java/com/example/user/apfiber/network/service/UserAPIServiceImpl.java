package com.example.user.apfiber.network.service;

import com.example.user.apfiber.network.RemoteServerAPI;
import com.example.user.apfiber.network.model.ActivateUserRequest;
import com.example.user.apfiber.network.model.AuthenticateUserRequest;
import com.example.user.apfiber.network.model.Pop;
import com.example.user.apfiber.network.model.RegisterUserRequest;
import com.example.user.apfiber.network.model.User;
import com.mapbox.geojson.Feature;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.ResponseBody;

import java.io.File;
import java.util.ArrayList;

import retrofit.Call;

/**
 * Created by KT on 23/12/15.
 */
public class UserAPIServiceImpl implements UserAPIService {

    private final RemoteServerAPI remoteServerAPI;

    public UserAPIServiceImpl(RemoteServerAPI remoteServerAPI) {
        this.remoteServerAPI = remoteServerAPI;
    }

    @Override
    public Call<Void> uploadImage(String fcmId, File file) {
        RequestBody photo = RequestBody.create(MediaType.parse("image/*"), file);
        RequestBody body = new MultipartBuilder()
                .type(MultipartBuilder.FORM)
                .addFormDataPart("file", file.getName(), photo)
                .build();
        return remoteServerAPI.uploadImage(fcmId, photo);
    }

    @Override
    public Call<User> registerUser(RegisterUserRequest request) {
        return remoteServerAPI.registerUser(request);
    }

    @Override
    public Call<User> authenticate(AuthenticateUserRequest request) {
        return remoteServerAPI.authenticate(request);
    }

    @Override
    public Call<Void> activateUser(final ActivateUserRequest request) {
        return remoteServerAPI.activateUser(request);
    }

    @Override
    public Call<ResponseBody> postSurveyData(String collection, RequestBody requestBody) {
        return remoteServerAPI.postSurveyData(collection, requestBody);
    }

    @Override
    public Call<ResponseBody> postPopData(String collection, RequestBody requestBody) {
        return remoteServerAPI.postPopData(collection, requestBody);
    }

    @Override
    public Call<ResponseBody> postPoleData(String collection, RequestBody requestBody) {
        return remoteServerAPI.postPoleData(collection, requestBody);
    }

    @Override
    public Call<ArrayList<Feature>> getSurveyFeatures(String collection) {
        return remoteServerAPI.getSurveyFeatures(collection);
    }

    @Override
    public Call<ArrayList<Pop>> getSurveyZonals() {
        return remoteServerAPI.getSurveyZonals();
    }

    @Override
    public Call<ArrayList<Pop>> getSurveyDistricts() {
        return remoteServerAPI.getSurveyDistricts();
    }

    @Override
    public Call<ArrayList<Pop>> getDistricts() {
        return remoteServerAPI.getDistricts();
    }
}
