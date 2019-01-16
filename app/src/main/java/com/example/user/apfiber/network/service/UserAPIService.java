package com.example.user.apfiber.network.service;

import com.example.user.apfiber.network.model.ActivateUserRequest;
import com.example.user.apfiber.network.model.AuthenticateUserRequest;
import com.example.user.apfiber.network.model.Pop;
import com.example.user.apfiber.network.model.RegisterUserRequest;
import com.example.user.apfiber.network.model.User;
import com.mapbox.geojson.Feature;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.ResponseBody;

import java.io.File;
import java.util.ArrayList;

import retrofit.Call;

/**
 * Created by KT on 23/12/15.
 */
public interface UserAPIService {

    Call<Void> uploadImage(String fcmId, File file);

    Call<User> registerUser(RegisterUserRequest request);

    Call<User> authenticate(AuthenticateUserRequest request);

    Call<Void> activateUser(ActivateUserRequest request);

    Call<ResponseBody> postSurveyData(String collection, RequestBody requestBody);

    Call<ResponseBody> postPopData(String collection, RequestBody requestBody);

    Call<ResponseBody> postPoleData(String collection, RequestBody requestBody);

    Call<ArrayList<Feature>> getSurveyFeatures(String collection);

    Call<ArrayList<Pop>> getSurveyZonals();

    Call<ArrayList<Pop>> getSurveyDistricts();

    Call<ArrayList<Pop>> getDistricts();
}
