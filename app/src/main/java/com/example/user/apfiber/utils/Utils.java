package com.example.user.apfiber.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.internal.Constants;
import com.mapbox.mapboxsdk.geometry.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by apple on 24/03/18.
 */

public class Utils {
    private static final int REQUEST_READ_PHONE_STATE = 1234;

    public static String formatDate(Date date, String format) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date.getTime());
    }

    public static Date getDate(int year, int month, int day, int hour, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, hour, minute, 0);
        return calendar.getTime();
    }

    public static void showToast(Context context, String res) {
        Toast toast = Toast.makeText(context, res, Toast.LENGTH_SHORT);
        toast.show();
    }

    public static void getSnackbar(CoordinatorLayout coordinatorLayout, String msg) {
        Snackbar snackbar = Snackbar.make(coordinatorLayout, msg, Snackbar.LENGTH_LONG);
        View snackbar_view = snackbar.getView();
        TextView snackbar_text = (TextView) snackbar_view.findViewById(android.support.design.R.id.snackbar_text);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            snackbar_text.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        } else {
            snackbar_text.setGravity(Gravity.CENTER_HORIZONTAL);
        }
//        snackbar_text.setCompoundDrawablesWithIntrinsicBounds(R.drawable.exclamationmark, 0, 0, 0);
        snackbar.show();

    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    @SuppressLint("MissingPermission")
    public static String getIMEI(Context mContext) {
        TelephonyManager telephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        String imei = null;

        int permissionCheck = ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_PHONE_STATE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) mContext, new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_READ_PHONE_STATE);
        } else {
            imei = telephonyManager.getDeviceId();
        }
        return imei;
    }
}
