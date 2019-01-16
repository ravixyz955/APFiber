package com.example.user.apfiber.network;

import android.content.Context;
import android.text.TextUtils;

import com.example.user.apfiber.network.model.AuthenticateUserRequest;
import com.example.user.apfiber.network.model.User;
import com.example.user.apfiber.network.service.UserAPIService;
import com.example.user.apfiber.utils.DataUtils;
import com.example.user.apfiber.utils.NetworkUtils;
import com.squareup.okhttp.Authenticator;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.net.Proxy;

/**
 * Created by apple on 23/01/18.
 */

public class TokenAuthenticator implements Authenticator {

    private static final String AUTHORIZATION = "x-auth-token";
    private Context mContext;
    private String newAccessToken;

    private UserAPIService userAPIService;

    public TokenAuthenticator(Context mContext) {
        this.mContext = mContext;
        userAPIService = NetworkUtils.provideUserAPIService(mContext);
    }

    @Override
    public Request authenticate(Proxy proxy, Response response) throws IOException {
        // Refresh your access_token using a synchronous api request

        if (!TextUtils.isEmpty(DataUtils.getEmail(mContext))) {
            if (!response.request().url().getPath().endsWith("/authenticate")) {
                AuthenticateUserRequest authenticateUserRequest = new AuthenticateUserRequest();
                authenticateUserRequest.setContact(DataUtils.getMobileNumber(mContext));
                authenticateUserRequest.setPassword(DataUtils.getPassword(mContext));
                retrofit.Response<User> authenticateResponse = userAPIService.authenticate(authenticateUserRequest).execute();
                if (authenticateResponse.isSuccess()) {
                    User user = authenticateResponse.body();
                    DataUtils.saveToken(mContext, user.getToken());
                    this.newAccessToken = user.getToken();
                } else
                    return null;
            } else
                return null;


            // Add new header to rejected request and retry it
            return response.request().newBuilder()
                    .header(AUTHORIZATION, newAccessToken)
                    .build();
        } else
            return null;
    }

    @Override
    public Request authenticateProxy(Proxy proxy, Response response) throws IOException {
        // Null indicates no attempt to authenticate.
        return null;
    }
}