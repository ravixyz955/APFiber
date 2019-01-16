package com.example.user.apfiber.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

import com.example.user.apfiber.network.RemoteServerAPI;
import com.example.user.apfiber.network.TokenAuthenticator;
import com.example.user.apfiber.network.service.UserAPIService;
import com.example.user.apfiber.network.service.UserAPIServiceImpl;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mapbox.geojson.BoundingBox;
import com.mapbox.geojson.Geometry;
import com.mapbox.geojson.Point;
import com.mapbox.geojson.gson.BoundingBoxDeserializer;
import com.mapbox.geojson.gson.GeoJsonAdapterFactory;
import com.mapbox.geojson.gson.GeometryDeserializer;
import com.mapbox.geojson.gson.PointDeserializer;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.logging.HttpLoggingInterceptor;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

/**
 * Created by KT on 5/24/15.
 */
public class NetworkUtils {

    private static final String SERVER_IP = "3rdi.xyz";
    private static final String BASE_URL = "https://" + SERVER_IP + "";
    private static final String BASE_CONTEXT = "/api";

    private static GsonBuilder gsonBuilder;
    private static OkHttpClient okHttpClient;
    private static Retrofit retrofit;
    private static RemoteServerAPI remoteServerAPI;
    private static UserAPIService userAPIService;

    public static boolean isConnectingToInternet(Context _context) {
        ConnectivityManager connectivity = (ConnectivityManager) _context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (NetworkInfo anInfo : info)
                    if (anInfo.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }

        }
        return false;
    }

    private static Cache provideOkHttpCache(Context mContext) {
        int cacheSize = 10 * 1024 * 1024; // 10 MiB
        return new Cache(mContext.getCacheDir(), cacheSize);
    }

    private static Gson provideGson() {
        if (gsonBuilder == null)
            gsonBuilder = new GsonBuilder()
                    .registerTypeAdapterFactory(GeoJsonAdapterFactory.create())
                    .registerTypeAdapter(Point.class, new PointDeserializer())
                    .registerTypeAdapter(BoundingBox.class, new BoundingBoxDeserializer())
                    .registerTypeAdapter(Geometry.class, new GeometryDeserializer())
                    .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        return gsonBuilder.create();
    }

    private static HttpLoggingInterceptor provideOkHttpClientLogging() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        return logging;
    }

    private static OkHttpClient provideOkHttpClient(Context mContext, boolean enableLogging) {
        if (okHttpClient == null) {
            okHttpClient = new OkHttpClient();
            okHttpClient.setCache(provideOkHttpCache(mContext));
            okHttpClient.setConnectTimeout(45, TimeUnit.SECONDS);
            okHttpClient.setReadTimeout(45, TimeUnit.SECONDS);
            if (enableLogging) {
                okHttpClient.interceptors().add(provideOkHttpClientLogging());
            }
            okHttpClient.setAuthenticator(new TokenAuthenticator(mContext));
        }
        return okHttpClient;
    }

    private static Retrofit provideRetrofit(Context mContext, String baseUrl, boolean enableLogging) {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create(provideGson()))
                    .baseUrl(baseUrl)
                    .client(provideOkHttpClient(mContext, enableLogging))
                    .build();
        }
        return retrofit;
    }

    private static RemoteServerAPI provideServerAPI(Context mContext) {
        if (remoteServerAPI == null)
            remoteServerAPI = provideRetrofit(mContext, BASE_URL, true).create(RemoteServerAPI.class);
        return remoteServerAPI;
    }

    public static UserAPIService provideUserAPIService(Context mContext) {
        if (userAPIService == null)
            userAPIService = new UserAPIServiceImpl(provideServerAPI(mContext));
        return userAPIService;
    }

    public static String provideAvatarUrl(String mobile) {
        return BASE_URL + BASE_CONTEXT + "/image/" + mobile;
    }

    public static boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int exitValue = ipProcess.waitFor();
            return (exitValue == 0);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String getNetworkClass(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info == null || !info.isConnected())
            return null; //not connected
        if (info.getType() == ConnectivityManager.TYPE_WIFI)
            return "WIFI";
        if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
            int networkType = info.getSubtype();
            switch (networkType) {
                case TelephonyManager.NETWORK_TYPE_GPRS:
                case TelephonyManager.NETWORK_TYPE_EDGE:
                case TelephonyManager.NETWORK_TYPE_CDMA:
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                case TelephonyManager.NETWORK_TYPE_IDEN: //api<8 : replace by 11
                    return "2G";
                case TelephonyManager.NETWORK_TYPE_UMTS:
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                case TelephonyManager.NETWORK_TYPE_HSPA:
                case TelephonyManager.NETWORK_TYPE_EVDO_B: //api<9 : replace by 14
                case TelephonyManager.NETWORK_TYPE_EHRPD:  //api<11 : replace by 12
                case TelephonyManager.NETWORK_TYPE_HSPAP:  //api<13 : replace by 15
                    return "3G";
                case TelephonyManager.NETWORK_TYPE_LTE:    //api<11 : replace by 13
                    return "4G";
                default:
                    return "?";
            }
        }
        return "?";
    }
}