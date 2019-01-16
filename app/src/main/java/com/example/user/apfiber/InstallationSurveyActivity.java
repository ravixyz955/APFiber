package com.example.user.apfiber;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.user.apfiber.network.model.FeasibleSurvey;
import com.example.user.apfiber.network.model.InstallationSurvey;
import com.example.user.apfiber.network.service.UserAPIService;
import com.example.user.apfiber.utils.DataUtils;
import com.example.user.apfiber.utils.NetworkUtils;
import com.example.user.apfiber.utils.Utils;
import com.example.user.apfiber.widget.ImagePickerView;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.ResponseBody;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class InstallationSurveyActivity extends AppCompatActivity {
    private TextView pole_details;
    private Button submit;
    private Spinner optional_box_spinner_sp;
    private ArrayList<Uri> imageUri;
    private ArrayList<ImageView> viewsList;
    private Uri imgUri;
    private boolean optional_box_no;
    private FrameLayout progress_bar;
    private ImagePickerView imagePickerView;
    private EditText gpon_serial_no, caf_id, net_speed;
    private EditText telephone_number, wifi_password, uid_edt;
    private Picasso picasso = Picasso.get();
    private Location location;
    private UserAPIService userAPIService;
    private ArrayList<Double> latitude, longitude;
    private ArrayList<String> labels;
    private RealmList<String> uriStr;
    private RealmList<String> ts;
    private RealmList<String> lat;
    private RealmList<String> longi;
    private ImageView customer_premise, gpon_photo;
    private InstallationSurvey installationSurvey;
    private ImageView speed_test_photo, customer_certificate_photo, optional_box_photo;
    private LinearLayout tk_gpon_photo_id, tk_customer_premises_photo_id;
    private LinearLayout tk_speed_test_photo_id, tk_customer_certificate_id;
    private LinearLayout tk_optional_box_photo_id_layout;
    private int CHOOSING_FILE_REQUEST;
    private ArrayList<Long> timeStamp;
    private Realm mRealm;
    private InstallationSurvey mInstallationSurvey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_installation_survey);

        pole_details = findViewById(R.id.pole_details);
        submit = findViewById(R.id.submit);
        customer_premise = findViewById(R.id.customer_premise);
        gpon_photo = findViewById(R.id.gpon_photo);
        uid_edt = findViewById(R.id.uid_edt);
        progress_bar = findViewById(R.id.progress_bar);
        tk_gpon_photo_id = findViewById(R.id.tk_gpon_photo_id);
        tk_speed_test_photo_id = findViewById(R.id.tk_speed_test_photo_id);
        speed_test_photo = findViewById(R.id.speed_test_photo);
        optional_box_photo = findViewById(R.id.optional_box_photo);
        gpon_serial_no = findViewById(R.id.gpon_serial_no);
        caf_id = findViewById(R.id.caf_id);
        telephone_number = findViewById(R.id.telephone_number);
        wifi_password = findViewById(R.id.wifi_password);
        imagePickerView = findViewById(R.id.pole_images);
        net_speed = findViewById(R.id.net_speed);
        viewsList = new ArrayList<>();
        customer_certificate_photo = findViewById(R.id.customer_certificate_photo);
        optional_box_spinner_sp = findViewById(R.id.optional_box_spinner_sp);
        tk_optional_box_photo_id_layout = findViewById(R.id.tk_optional_box_photo_id);
        tk_customer_premises_photo_id = findViewById(R.id.tk_customer_premises_photo_id);
        tk_customer_certificate_id = findViewById(R.id.tk_customer_certificate_id);
        imageUri = new ArrayList<Uri>();
        timeStamp = new ArrayList<>();
        latitude = new ArrayList<>();
        longitude = new ArrayList<>();
        labels = new ArrayList<>();
        userAPIService = NetworkUtils.provideUserAPIService(this);
        mInstallationSurvey = new InstallationSurvey();
        Realm.init(getApplicationContext());
        mRealm = Realm.getDefaultInstance();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            setTitle("Installation Survey");
        }
        labels.add("Customer premises");
        labels.add("Gpon box");
        labels.add("Optional box");
        labels.add("Net speed");
        labels.add("Customer Certificate");

        viewsList.add(customer_premise);
        viewsList.add(gpon_photo);
        viewsList.add(optional_box_photo);
        viewsList.add(speed_test_photo);
        viewsList.add(customer_certificate_photo);

        getSavedFormData();
        optional_box_spinner_sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (optional_box_spinner_sp.getSelectedItem().toString().contains("Yes")) {
                    tk_optional_box_photo_id_layout.setVisibility(View.VISIBLE);
                    if (speed_test_photo.getDrawable() != null) {
                        speed_test_photo.setImageResource(0);
                    }
                    if (customer_certificate_photo.getDrawable() != null) {
                        customer_certificate_photo.setImageResource(0);
                    }
                    optional_box_no = false;
                } else if (optional_box_spinner_sp.getSelectedItemPosition() > 1) {
                    tk_optional_box_photo_id_layout.setVisibility(View.GONE);
                    optional_box_photo.setImageResource(0);
                    optional_box_no = true;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        pole_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InstallationSurveyActivity.this, FeasibilitySurveyActivity.class);
                intent.putExtra("editMode", "editMode");
                startActivity(intent);
            }
        });

        tk_customer_premises_photo_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImages(101);
            }
        });

        tk_gpon_photo_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (customer_premise.getDrawable() == null) {
                    Utils.showToast(InstallationSurveyActivity.this, "Customer premises photo cannot be empty");
                } else {
                    pickImages(102);
                }
            }
        });

        tk_optional_box_photo_id_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gpon_photo.getDrawable() == null) {
                    Utils.showToast(InstallationSurveyActivity.this, "Gpon photo cannot be empty");
                } else {
                    pickImages(103);
                }
            }
        });

        tk_speed_test_photo_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (optional_box_photo.getDrawable() == null) {
                    if (!optional_box_no) {
                        Utils.showToast(InstallationSurveyActivity.this, "Optional box photo cannot be empty");
                    } else if (gpon_photo.getDrawable() == null && optional_box_no) {
                        Utils.showToast(InstallationSurveyActivity.this, "Gpon box photo cannot be empty");
                    } else if (optional_box_photo.getDrawable() == null && !optional_box_no) {
                        Utils.showToast(InstallationSurveyActivity.this, "Optional box photo cannot be empty");
                    } else
                        pickImages(104);
//                    Utils.showToast(InstallationSurveyActivity.this, "Optional box photo cannot be empty");
                } else {
                    pickImages(104);
                }
            }
        });

        tk_customer_certificate_id.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                if (speed_test_photo.getDrawable() == null) {
                    Utils.showToast(InstallationSurveyActivity.this, "speed test photo cannot be empty");
                } else {
                    pickImages(105);
                }
            }
        });

        submit.setOnClickListener(new View.OnClickListener()

        {
            public void onClick(View v) {
                if (TextUtils.isEmpty(uid_edt.getText()))
                    Utils.showToast(InstallationSurveyActivity.this, "uid cannot be empty");
                else if (customer_premise.getDrawable() == null)
                    Utils.showToast(InstallationSurveyActivity.this, "Customer premises photo not taken");
                else if (gpon_photo.getDrawable() == null)
                    Utils.showToast(InstallationSurveyActivity.this, "Gpon box photo not taken");
                else if (TextUtils.isEmpty(gpon_serial_no.getText()))
                    Utils.showToast(InstallationSurveyActivity.this, "gpon serial no cannot be empty");
                else if (optional_box_spinner_sp.getSelectedItemPosition() < 1)
                    Utils.showToast(InstallationSurveyActivity.this, "optional box cannot be empty");
                else if (TextUtils.isEmpty(caf_id.getText()))
                    Utils.showToast(InstallationSurveyActivity.this, "Caf id cannot be empty");
                else if (TextUtils.isEmpty(telephone_number.getText()))
                    Utils.showToast(InstallationSurveyActivity.this, "Telephone number cannot be empty");
                else if (TextUtils.isEmpty(wifi_password.getText()))
                    Utils.showToast(InstallationSurveyActivity.this, "Wifi password cannot be empty");
                else if (TextUtils.isEmpty(net_speed.getText()))
                    Utils.showToast(InstallationSurveyActivity.this, "Net speed cannot be empty");
                else if (speed_test_photo.getDrawable() == null)
                    Utils.showToast(InstallationSurveyActivity.this, "Speed test photo not taken");
                else if (customer_certificate_photo.getDrawable() == null)
                    Utils.showToast(InstallationSurveyActivity.this, "Customer certificate photo not taken");
                else if (optional_box_photo.getDrawable() == null && !optional_box_no)
                    Utils.showToast(InstallationSurveyActivity.this, "Optional box photo not taken");
                else {
                    progress_bar.setVisibility(View.VISIBLE);
                    JSONObject formJson = new JSONObject();
                    JSONObject geometry = new JSONObject();
                    JSONObject properties = new JSONObject();
                    JSONArray photo_details = new JSONArray();
                    imagePickerView.addImages(imageUri);

                    try {
                        geometry.put("type", "Point");
                        formJson.put("type", "Feature");
                        formJson.put("geometry", geometry);
                        formJson.put("mobile", DataUtils.getMobileNumber(InstallationSurveyActivity.this));
                        formJson.put("password", DataUtils.getPassword(InstallationSurveyActivity.this));

                        if (!TextUtils.isEmpty(uid_edt.getText()))
                            properties.put("uid", uid_edt.getText().toString());
                        if (!TextUtils.isEmpty(gpon_serial_no.getText()))
                            properties.put("gpon_serial_no", gpon_serial_no.getText().toString());
                        if (!TextUtils.isEmpty(optional_box_spinner_sp.getSelectedItem().toString()))
                            properties.put("optional_box", optional_box_spinner_sp.getSelectedItem().toString());
                        if (!TextUtils.isEmpty(caf_id.getText()))
                            properties.put("caf_id", caf_id.getText().toString());
                        if (!TextUtils.isEmpty(telephone_number.getText()))
                            properties.put("telephone_number", telephone_number.getText().toString());
                        if (!TextUtils.isEmpty(wifi_password.getText()))
                            properties.put("wifi_password", wifi_password.getText().toString());
                        if (!TextUtils.isEmpty(net_speed.getText()))
                            properties.put("net_speed", net_speed.getText().toString());

                        formJson.put("properties", properties);

                        for (int i = 0; i < imageUri.size(); i++) {
                            JSONObject photo_info = new JSONObject();
                            JSONArray coordinates = new JSONArray();
                            photo_info.put("type", labels.get(i));
                            photo_info.put("timeStamp", timeStamp.get(i));
                            coordinates.put(latitude.get(i));
                            coordinates.put(longitude.get(i));
                            photo_info.put("coordinates", coordinates);
                            photo_details.put(photo_info);

                        }
                        formJson.put("photo_detetails", photo_details);
                        if (NetworkUtils.isConnectingToInternet(InstallationSurveyActivity.this)) {
                            final RequestBody formDataRequestBody = RequestBody.create(MediaType.parse("application/json"), formJson.toString());
                            userAPIService.postPoleData("installation", formDataRequestBody).enqueue(new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Response<ResponseBody> response, Retrofit retrofit) {
                                    if (response.isSuccess()) {
                                        JSONObject responseJson = null;
                                        try {
                                            responseJson = new JSONObject(response.body().string());
                                            String id = responseJson.getJSONObject("_id").getString("$oid");
                                            if (imagePickerView.getImages() != null && !imagePickerView.getImages().isEmpty()) {
                                                imagePickerView.uploadImages(TextUtils.isEmpty(id) ? "installation_survey" : id);
                                            } else {
                                                Utils.showToast(InstallationSurveyActivity.this, "Successful!");
                                                finish();
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                        Log.d("Form Success", "onResponse: " + "Success");
                                    } else {
                                        Log.d("Form Error", "onResponse: " + response.errorBody().toString());
                                    }
                                }

                                @Override
                                public void onFailure(Throwable t) {
                                    Log.d("Form Failure", "onFailure: " + t.getMessage());
                                }
                            });
                        } else {
                            progress_bar.setVisibility(View.GONE);
                            DataUtils.saveInstallation(InstallationSurveyActivity.this, formJson);
                            if (imagePickerView.getImages() != null && !imagePickerView.getImages().isEmpty()) {
                                ArrayList<Uri> images = imagePickerView.getImages();
                                ArrayList<String> imgList = new ArrayList<>();
                                for (Uri image : images) {
                                    if (image != null)
                                        imgList.add(image.toString());
                                }
                                DataUtils.saveInstallationImages(InstallationSurveyActivity.this, imgList);
                            }
                            finish();
                            Utils.showToast(InstallationSurveyActivity.this, "saved offline");
                            if (installationSurvey != null) {
                                mRealm.beginTransaction();
                                installationSurvey.deleteFromRealm();
                                mRealm.commitTransaction();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        imagePickerView.setImagePickerListener(new ImagePickerView.ImagePickerListener()

        {
            @Override
            public void onClick() {
//                pickImages(0);
            }
        });

        imagePickerView.setTransferListener(new ImagePickerView.ImageTransferListener()

        {
            @Override
            public void onProgressUpdate(int progress) {
                Log.d("MSPSP - Progress", String.valueOf(progress));
            }

            @Override
            public void onTransferComplete() {
                Log.d("MSPSP - Progress", "Completed");
                Utils.showToast(InstallationSurveyActivity.this, "Successful!");
                finish();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.feasibility_menu, menu);
        return true;
    }

    private void getSavedFormData() {
        installationSurvey = mRealm.where(InstallationSurvey.class).equalTo("offSet", 1).findFirst();
        if (installationSurvey != null) {
            uriStr = installationSurvey.getUriStr();
            ts = installationSurvey.getTs();
            lat = installationSurvey.getLatitude();
            longi = installationSurvey.getLongtitude();
            uid_edt.setText(installationSurvey.getUid());
            gpon_serial_no.setText(installationSurvey.getGpon_serial());
            optional_box_spinner_sp.setSelection(installationSurvey.getOptional_box());
            for (int i = 0; i < uriStr.size(); i++) {
                if (uriStr.get(i) == null) {
                    timeStamp.add(0L);
                    latitude.add(0.0d);
                    longitude.add(0.0d);
                    imageUri.add(null);
                    viewsList.get(i).setImageResource(0);
                } else {
                    imageUri.add(Uri.parse(uriStr.get(i)));
                    timeStamp.add(Long.parseLong(ts.get(i)));
                    latitude.add(Double.parseDouble(lat.get(i)));
                    longitude.add(Double.parseDouble(longi.get(i)));
                    picasso.load(Uri.parse(uriStr.get(i))).centerCrop().resize(100, 100).into(viewsList.get(i));
                }
            }
            caf_id.setText(installationSurvey.getCaf_id());
            telephone_number.setText(installationSurvey.getTelephone_num());
            wifi_password.setText(installationSurvey.getWifi_pwd());
            net_speed.setText(installationSurvey.getNet_speed());
        }
    }

    private void pickImages(int request_code) {
        CHOOSING_FILE_REQUEST = request_code;
        startActivityForResult(getPickImageIntent(InstallationSurveyActivity.this), CHOOSING_FILE_REQUEST);
    }

    public Intent getPickImageIntent(Context context) {
        Intent chooserIntent = null;

        List<Intent> intentList = new ArrayList<>();

        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePhotoIntent.putExtra("return-data", true);
        takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, setImageUri());
        intentList = addIntentsToList(context, intentList, pickIntent);
        intentList = addIntentsToList(context, intentList, takePhotoIntent);

        if (intentList.size() > 0) {
            chooserIntent = Intent.createChooser(intentList.remove(intentList.size() - 1),
                    context.getString(R.string.pick_image_intent_text));
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentList.toArray(new Parcelable[]{}));
        }
        return chooserIntent;
    }

    private static List<Intent> addIntentsToList(Context context, List<Intent> list, Intent intent) {
        List<ResolveInfo> resInfo = context.getPackageManager().queryIntentActivities(intent, 0);
        for (ResolveInfo resolveInfo : resInfo) {
            String packageName = resolveInfo.activityInfo.packageName;
            Intent targetedIntent = new Intent(intent);
            targetedIntent.setPackage(packageName);
            list.add(targetedIntent);
        }
        return list;
    }

    private Uri setImageUri() {
        File file = new File(Environment.getExternalStorageDirectory() + "/DCIM/AP-Fiber", "XYZone" + new Date().getTime() + ".jpg");
        imgUri = Uri.fromFile(file);
        return imgUri;
    }

    public Uri getImageUri() {
        return imgUri;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Uri fileUri = getImageUri();
        if (requestCode == 101 && resultCode == RESULT_OK && data == null) {
            location = InstallationCommsioningActivity.getLocation();
            imageUri.add(fileUri);
            timeStamp.add(System.currentTimeMillis());
            if (location == null) {
                Utils.showToast(InstallationSurveyActivity.this, "Unable to fetch location");
                finish();
            } else {
                latitude.add(location.getLatitude());
                longitude.add(location.getLongitude());
            }
            picasso.load(fileUri).centerCrop().memoryPolicy(MemoryPolicy.NO_CACHE).resize(100, 100).into(customer_premise);
        } else if (requestCode == 102 && resultCode == RESULT_OK && data == null) {
            location = InstallationCommsioningActivity.getLocation();
            timeStamp.add(System.currentTimeMillis());
            latitude.add(location.getLatitude());
            longitude.add(location.getLongitude());
            imageUri.add(fileUri);
            picasso.load(fileUri).centerCrop().resize(100, 100).into(gpon_photo);
        } else if (requestCode == 103 && resultCode == RESULT_OK && data == null) {
            location = InstallationCommsioningActivity.getLocation();
            timeStamp.add(System.currentTimeMillis());
            latitude.add(location.getLatitude());
            longitude.add(location.getLongitude());
            imageUri.add(fileUri);
            picasso.load(fileUri).centerCrop().resize(100, 100).into(optional_box_photo);
        } else if (requestCode == 104 && resultCode == RESULT_OK && data == null) {
            if (optional_box_no) {
                timeStamp.add(0L);
                latitude.add(0.0d);
                longitude.add(0.0d);
                imageUri.add(null);
            }
            location = InstallationCommsioningActivity.getLocation();
            timeStamp.add(System.currentTimeMillis());
            latitude.add(location.getLatitude());
            longitude.add(location.getLongitude());
            imageUri.add(fileUri);
            picasso.load(fileUri).centerCrop().resize(100, 100).into(speed_test_photo);
        } else if (requestCode == 105 && resultCode == RESULT_OK && data == null) {
            location = InstallationCommsioningActivity.getLocation();
            timeStamp.add(System.currentTimeMillis());
            latitude.add(location.getLatitude());
            longitude.add(location.getLongitude());
            imageUri.add(fileUri);
            picasso.load(fileUri).centerCrop().resize(100, 100).into(customer_certificate_photo);
        }

        if (data != null && data.getClipData() != null) {
            int count = data.getClipData().getItemCount();
            for (int i = 0; i < count; i++) {
                imageUri.add(data.getClipData().getItemAt(i).getUri());
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            saveFormData();
            finish();
            return true;
        } else if (id == R.id.clear) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(
                    InstallationSurveyActivity.this);
            builder.setCancelable(false);
            builder.setMessage("Are you sure want to clear?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    installationSurvey = mRealm.where(InstallationSurvey.class).equalTo("offSet", 1).findFirst();
                    if (imageUri != null) {
                        for (int i = 0; i < imageUri.size(); i++) {
                            Log.d("SSS", "onClick: ");
                            picasso.invalidate(imageUri.get(i));
                            viewsList.get(i).setImageResource(0);
                        }
                        imageUri.clear();
                    }

                    if (timeStamp != null)
                        timeStamp.clear();
                    if (latitude != null)
                        latitude.clear();
                    if (longitude != null)
                        longitude.clear();
                    if (!TextUtils.isEmpty(uid_edt.getText()))
                        uid_edt.getText().clear();
                    if (!TextUtils.isEmpty(gpon_serial_no.getText()))
                        gpon_serial_no.getText().clear();
                    if (optional_box_spinner_sp.getSelectedItemPosition() > 0)
                        optional_box_spinner_sp.setSelection(0);
                    if (!TextUtils.isEmpty(caf_id.getText()))
                        caf_id.getText().clear();
                    if (!TextUtils.isEmpty(telephone_number.getText()))
                        telephone_number.getText().clear();
                    if (!TextUtils.isEmpty(wifi_password.getText()))
                        wifi_password.getText().clear();
                    if (!TextUtils.isEmpty(net_speed.getText()))
                        net_speed.getText().clear();
                    if (installationSurvey != null) {
                        mRealm.beginTransaction();
                        installationSurvey.deleteFromRealm();
                        mRealm.commitTransaction();
                    }
                    Utils.showToast(InstallationSurveyActivity.this, "cleared!");
                }
            });
            builder.setNegativeButton("No", null);
            builder.create().show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveFormData() {
        uriStr = new RealmList<>();
        ts = new RealmList<>();
        lat = new RealmList<>();
        longi = new RealmList<>();
        mInstallationSurvey.setUid(uid_edt.getText().toString());
        if (imageUri.size() > 0) {
            for (int i = 0; i < imageUri.size(); i++) {
                if (imageUri.get(i) == null) {
                    uriStr.add(null);
                    ts.add(String.valueOf(0L));
                    lat.add(String.valueOf(0.0d));
                    longi.add(String.valueOf(0.0d));
                } else {
                    uriStr.add(imageUri.get(i).toString());
                    ts.add(timeStamp.get(i).toString());
                    lat.add(latitude.get(i).toString());
                    longi.add(longitude.get(i).toString());
                }
            }
        }
        mInstallationSurvey.setOffSet(1);
        mInstallationSurvey.setUriStr(uriStr);
        mInstallationSurvey.setUriStr(uriStr);
        mInstallationSurvey.setTs(ts);
        mInstallationSurvey.setLatitude(lat);
        mInstallationSurvey.setLongtitude(longi);
        mInstallationSurvey.setGpon_serial(gpon_serial_no.getText().toString());
        mInstallationSurvey.setOptional_box(optional_box_spinner_sp.getSelectedItemPosition());
        mInstallationSurvey.setCaf_id(caf_id.getText().toString());
        mInstallationSurvey.setTelephone_num(telephone_number.getText().toString());
        mInstallationSurvey.setWifi_pwd(wifi_password.getText().toString());
        mInstallationSurvey.setNet_speed(net_speed.getText().toString());
        mRealm.beginTransaction();
        mRealm.insertOrUpdate(mInstallationSurvey);
        mRealm.commitTransaction();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        saveFormData();
    }
}
