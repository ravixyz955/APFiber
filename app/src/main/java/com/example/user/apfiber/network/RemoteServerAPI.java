package com.example.user.apfiber.network;

import com.example.user.apfiber.network.model.ActivateUserRequest;
import com.example.user.apfiber.network.model.AuthenticateUserRequest;
import com.example.user.apfiber.network.model.Pop;
import com.example.user.apfiber.network.model.RegisterUserRequest;
import com.example.user.apfiber.network.model.User;
import com.mapbox.geojson.Feature;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.ResponseBody;

import java.util.ArrayList;

import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Path;

/**
 * Created by KT on 23/12/15.
 */
public interface RemoteServerAPI {

    String BASE_CONTEXT = "/api";

    @Multipart
    @POST(BASE_CONTEXT + "/users/image")
    Call<Void> uploadImage(@Header("x-auth-token") String fcmId, @Part("file") RequestBody request);

    @POST(BASE_CONTEXT + "/register")
    Call<User> registerUser(@Body RegisterUserRequest request);

    @POST("http://13.229.193.42:3000/api/authenticate")
    Call<User> authenticate(@Body AuthenticateUserRequest request);

    @POST("/activate")
    Call<Void> activateUser(@Body ActivateUserRequest request);

    @POST("https://api.mlab.com/api/1/databases/ap-fiber/collections/survey?apiKey=W8BcdRqxTaA1XT3jgzLOz6g8qJKO8Gx7")
    Call<ResponseBody> postSurveyData(@Path("collection") String collection, @Body RequestBody requestBody);

    @POST("https://api.mlab.com/api/1/databases/ap-fiber/collections/installation?apiKey=W8BcdRqxTaA1XT3jgzLOz6g8qJKO8Gx7")
    Call<ResponseBody> postInstallationData(@Path("collection") String collection, @Body RequestBody requestBody);

    @POST("https://api.mlab.com/api/1/databases/ap-fiber/collections/route-survey?apiKey=W8BcdRqxTaA1XT3jgzLOz6g8qJKO8Gx7")
    Call<ResponseBody> postRouteSurveyData(@Path("collection") String collection, @Body RequestBody requestBody);

    @POST("https://api.mlab.com/api/1/databases/ap-fiber/collections/{collection}?apiKey=W8BcdRqxTaA1XT3jgzLOz6g8qJKO8Gx7")
    Call<ResponseBody> postPopData(@Path("collection") String collection, @Body RequestBody requestBody);

    @POST("https://api.mlab.com/api/1/databases/ap-fiber/collections/{collection}?apiKey=W8BcdRqxTaA1XT3jgzLOz6g8qJKO8Gx7")
    Call<ResponseBody> postPoleData(@Path("collection") String collection, @Body RequestBody requestBody);

    @GET("https://api.mlab.com/api/1/databases/ap-fiber/collections/{collection}?apiKey=W8BcdRqxTaA1XT3jgzLOz6g8qJKO8Gx7")
    Call<ArrayList<Feature>> getSurveyFeatures(@Path("collection") String collection);

    @GET("https://api.mlab.com/api/1/databases/ap-fiber/collections/pops?apiKey=W8BcdRqxTaA1XT3jgzLOz6g8qJKO8Gx7&l=10000")
    Call<ArrayList<Pop>> getSurveyZonals();

    @GET("https://api.mlab.com/api/1/databases/ap-fiber/collections/pops?apiKey=W8BcdRqxTaA1XT3jgzLOz6g8qJKO8Gx7&l=10000")
    Call<ArrayList<Pop>> getSurveyDistricts();

    @GET("https://api.mlab.com/api/1/databases/ap-fiber/collections/users?apiKey=W8BcdRqxTaA1XT3jgzLOz6g8qJKO8Gx7&l=10000")
    Call<ArrayList<Pop>> getDistricts();
}
