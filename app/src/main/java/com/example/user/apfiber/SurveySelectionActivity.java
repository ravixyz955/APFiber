package com.example.user.apfiber;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.user.apfiber.network.service.UserAPIService;
import com.example.user.apfiber.utils.DataUtils;
import com.example.user.apfiber.utils.NetworkUtils;
import com.example.user.apfiber.utils.Utils;
import com.example.user.apfiber.widget.ImagePickerView;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.ResponseBody;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class SurveySelectionActivity extends AppCompatActivity {
    private TextView survey_btn;
    private TextView instal_commision_btn;
    private String[] permissions;
    private static final int PERMISSION_ALL = 1;
    private ProgressDialog dialog;
    private UserAPIService userapiService;
    private ImagePickerView imagePickerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey_selection);

        survey_btn = findViewById(R.id.survey_btn);
        instal_commision_btn = findViewById(R.id.instal_commision_btn);
        userapiService = NetworkUtils.provideUserAPIService(this);
        imagePickerView = findViewById(R.id.pole_images);
        dialog = new ProgressDialog(SurveySelectionActivity.this);
        dialog.setCancelable(false);
        dialog.setMessage("sync in progress...");
        permissions = new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.READ_PHONE_STATE
        };

        if (!hasPermissions(this, permissions)) {
            ActivityCompat.requestPermissions(this, permissions, PERMISSION_ALL);
        }

        if (Build.VERSION.SDK_INT >= 24) {
            try {
                Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                m.invoke(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (!NetworkUtils.isConnectingToInternet(this)) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(
                    SurveySelectionActivity.this);
            builder.setTitle("AP-Fiber");
            builder.setCancelable(false);
            builder.setMessage("The mobile internet service is off. Do you want to connect to wifi?");
            builder.setPositiveButton("Settings",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(
                                final DialogInterface dialogInterface,
                                final int i) {
                            startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
                        }
                    });
            builder.setNegativeButton("Cancel", null);
            builder.create().show();
        } else {
            JSONArray polesArray = DataUtils.getPoles(this);
            JSONArray popsArray = DataUtils.getPops(this);
            JSONArray feasibilityArray = DataUtils.getFeasibilities(this);
            JSONArray installationArray = DataUtils.getInstallations(this);
            ArrayList<ArrayList<String>> poleImgList = DataUtils.getPoleImageArrayList(this);
            ArrayList<ArrayList<String>> popImgList = DataUtils.getPopImageArrayList(this);
            ArrayList<ArrayList<String>> feasibilityImgList = DataUtils.getFeasibilityImageArrayList(this);
            ArrayList<ArrayList<String>> installationImgList = DataUtils.getInstallationImageArrayList(this);
            if (polesArray != null && polesArray.length() > 0) {
                for (int i = 0; i < polesArray.length(); i++) {
                    dialog.show();
                    try {
                        ArrayList<String> uriList = poleImgList.get(i);
                        ArrayList<Uri> uriArrayList = new ArrayList<>();
                        for (String uri : uriList) {
                            uriArrayList.add(Uri.parse(uri));
                        }
                        imagePickerView.clear();
                        imagePickerView.addImages(uriArrayList);
                        final RequestBody formDataRequestBody = RequestBody.create(MediaType.parse("application/json"), polesArray.get(i).toString());
                        userapiService.postPoleData("survey", formDataRequestBody).enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Response<ResponseBody> response, Retrofit retrofit) {
                                if (response.isSuccess()) {
                                    JSONObject responseJson = null;
                                    try {
                                        responseJson = new JSONObject(response.body().string());
                                        String id = responseJson.getJSONObject("_id").getString("$oid");
                                        if (imagePickerView.getImages() != null && !imagePickerView.getImages().isEmpty()) {
                                            imagePickerView.uploadImages(TextUtils.isEmpty(id) ? "poles" : id);
                                        } else {
                                            Utils.showToast(SurveySelectionActivity.this, "Successful!");
                                            dialog.dismiss();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                } else {
//                        Utils.getSnackbar(coordinatorLayout, "Unable to fetch data.");
                                    Log.d("Form Error", "onResponse: " + response.errorBody().toString());
                                }
                            }

                            @Override
                            public void onFailure(Throwable t) {
                                Log.d("Form Failure", "onFailure: " + t.getMessage());
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                DataUtils.clearPolePref();
            }

            if (popsArray != null && popsArray.length() > 0) {
                for (int i = 0; i < popsArray.length(); i++) {
                    dialog.show();
                    try {
                        ArrayList<String> uriList = popImgList.get(i);
                        ArrayList<Uri> uriArrayList = new ArrayList<>();
                        for (String uri : uriList) {
                            uriArrayList.add(Uri.parse(uri));
                        }
                        imagePickerView.clear();
                        imagePickerView.addImages(uriArrayList);
                        final RequestBody formDataRequestBody = RequestBody.create(MediaType.parse("application/json"), popsArray.get(i).toString());
                        userapiService.postPoleData("survey", formDataRequestBody).enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Response<ResponseBody> response, Retrofit retrofit) {
                                if (response.isSuccess()) {
                                    JSONObject responseJson = null;
                                    try {
                                        responseJson = new JSONObject(response.body().string());
                                        String id = responseJson.getJSONObject("_id").getString("$oid");
                                        if (imagePickerView.getImages() != null && !imagePickerView.getImages().isEmpty()) {
                                            imagePickerView.uploadImages(TextUtils.isEmpty(id) ? "pops" : id);
                                        } else {
                                            Utils.showToast(SurveySelectionActivity.this, "Successful!");
                                            dialog.dismiss();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                } else {
//                        Utils.getSnackbar(coordinatorLayout, "Unable to fetch data.");
                                    Log.d("Form Error", "onResponse: " + response.errorBody().toString());
                                }
                            }

                            @Override
                            public void onFailure(Throwable t) {
                                Log.d("Form Failure", "onFailure: " + t.getMessage());
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                DataUtils.clearPopPref();
            }

            if (feasibilityArray != null && feasibilityArray.length() > 0) {
                for (int i = 0; i < feasibilityArray.length(); i++) {
                    dialog.show();
                    try {
                        ArrayList<String> uriList = feasibilityImgList.get(i);
                        ArrayList<Uri> uriArrayList = new ArrayList<>();
                        for (String uri : uriList) {
                            uriArrayList.add(Uri.parse(uri));
                        }
                        imagePickerView.clear();
                        imagePickerView.addImages(uriArrayList);
                        final RequestBody formDataRequestBody = RequestBody.create(MediaType.parse("application/json"), feasibilityArray.get(i).toString());
                        userapiService.postPoleData("feasibility", formDataRequestBody).enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Response<ResponseBody> response, Retrofit retrofit) {
                                if (response.isSuccess()) {
                                    JSONObject responseJson = null;
                                    try {
                                        responseJson = new JSONObject(response.body().string());
                                        String id = responseJson.getJSONObject("_id").getString("$oid");
                                        if (imagePickerView.getImages() != null && !imagePickerView.getImages().isEmpty()) {
                                            imagePickerView.uploadImages(TextUtils.isEmpty(id) ? "feasibile" : id);
                                        } else {
                                            Utils.showToast(SurveySelectionActivity.this, "Successful!");
                                            dialog.dismiss();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                } else {
//                        Utils.getSnackbar(coordinatorLayout, "Unable to fetch data.");
                                    Log.d("Form Error", "onResponse: " + response.errorBody().toString());
                                }
                            }

                            @Override
                            public void onFailure(Throwable t) {
                                Log.d("Form Failure", "onFailure: " + t.getMessage());
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                DataUtils.clearFeasibilityPref();
            }

            if (installationArray != null && installationArray.length() > 0) {
                for (int i = 0; i < installationArray.length(); i++) {
                    dialog.show();
                    try {
                        ArrayList<String> uriList = installationImgList.get(i);
                        ArrayList<Uri> uriArrayList = new ArrayList<>();
                        for (String uri : uriList) {
                            uriArrayList.add(Uri.parse(uri));
                        }
                        imagePickerView.clear();
                        imagePickerView.addImages(uriArrayList);
                        final RequestBody formDataRequestBody = RequestBody.create(MediaType.parse("application/json"), installationArray.get(i).toString());
                        userapiService.postPoleData("installation", formDataRequestBody).enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Response<ResponseBody> response, Retrofit retrofit) {
                                if (response.isSuccess()) {
                                    JSONObject responseJson = null;
                                    try {
                                        responseJson = new JSONObject(response.body().string());
                                        String id = responseJson.getJSONObject("_id").getString("$oid");
                                        if (imagePickerView.getImages() != null && !imagePickerView.getImages().isEmpty()) {
                                            imagePickerView.uploadImages(TextUtils.isEmpty(id) ? "installation" : id);
                                        } else {
                                            Utils.showToast(SurveySelectionActivity.this, "Successful!");
                                            dialog.dismiss();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                } else {
//                        Utils.getSnackbar(coordinatorLayout, "Unable to fetch data.");
                                    Log.d("Form Error", "onResponse: " + response.errorBody().toString());
                                }
                            }

                            @Override
                            public void onFailure(Throwable t) {
                                Log.d("Form Failure", "onFailure: " + t.getMessage());
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                DataUtils.clearInstallationPref();
            }
        }
        survey_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SurveySelectionActivity.this, MainActivity.class));
            }
        });

        instal_commision_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SurveySelectionActivity.this, InstallationCommsioningActivity.class));
            }
        });

        imagePickerView.setImagePickerListener(new ImagePickerView.ImagePickerListener() {
            @Override
            public void onClick() {
            }
        });

        imagePickerView.setTransferListener(new ImagePickerView.ImageTransferListener() {
            @Override
            public void onProgressUpdate(int progress) {
                Log.d("MSPSP - Progress", String.valueOf(progress));
            }

            @Override
            public void onTransferComplete() {
                Log.d("MSPSP - Progress", "Completed");
                Utils.showToast(SurveySelectionActivity.this, "Successful!");
                dialog.dismiss();
            }

            @Override
            public void onTransferFailed() {
                Log.d("MSPSP - Progress", "Failed");
            }

            @Override
            public void onError(Exception ex) {

            }
        });
    }

    private boolean hasPermissions(Context context, String[] permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == 1) {
        }
    }
}
