package com.example.user.apfiber;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.example.user.apfiber.network.service.UserAPIService;
import com.example.user.apfiber.utils.DataUtils;
import com.example.user.apfiber.utils.NetworkUtils;
import com.example.user.apfiber.utils.Utils;
import com.example.user.apfiber.widget.ImagePickerView;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.ResponseBody;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class PoleActivity extends AppCompatActivity {
    private LinearLayout clamp_photo_id;
    private ImageView pole_img;
    private RadioGroup pole_radio_rg, cable_sag_rg;
    private RadioGroup clamp_error_details_rg;
    private RadioGroup line_type_rg, cable_type_rg;
    private RadioGroup enlosure_type_rg, enclosure_frame_rg;
    private RadioButton rectangular_cement_rb;
    private RadioButton rectangular_iron_rb;
    private RadioButton round_cement_rb;
    private RadioButton clamp_error_yes_rb;
    private RadioButton clamp_error_no_rb;
    private RadioButton line_type_1_rb;
    private RadioButton line_type_2_rb;
    private RadioButton line_type_3_rb;
    private RadioButton cable_type_1_rb, cable_type_2_rb, cable_type_3_rb;
    private RadioButton cable_type_4_rb, cable_type_5_rb, cable_type_6_rb;
    private RadioButton cable_sag_yes_rb, cable_sag_no_rb;
    private RadioButton enclosure_type_2way_rb;
    private RadioButton enclosure_type_3way_rb;
    private RadioButton enclosure_type_4way_rb;
    private RadioButton enclosure_frame_yes_rb;
    private RadioButton enclosure_frame_no_rb;
    private Spinner splitter_box_sp;
    private CheckBox suspension_cb, tension_cb, wedge_cb;
    private String pole_type;
    private String clamp_error_details;
    private String line_type;
    private String enclosure_tye, enclosure_frame;
    private String cable_type, sag_txt;
    private Button submit;
    private Location location;
    private UserAPIService userAPIService;
    private ImagePickerView imagePickerView;
    private Uri imgUri;
    private FrameLayout progress_bar;
    private CoordinatorLayout coordinatorLayout;
    private static final int CHOOSING_FILE_REQUEST = 1234;
    private String imei, clamp_type;
    private Picasso picasso = Picasso.get();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pole);

        pole_radio_rg = findViewById(R.id.pole_type_rg);
        clamp_error_details_rg = findViewById(R.id.clamp_error_details_rg);
        line_type_rg = findViewById(R.id.line_type_rg);
        pole_img = findViewById(R.id.pole_img);
        enlosure_type_rg = findViewById(R.id.enlosure_type_rg);
        enclosure_frame_rg = findViewById(R.id.enlosure_frame_rg);
        cable_type_rg = findViewById(R.id.cable_type_rg);
        cable_sag_rg = findViewById(R.id.cable_sag_rg);
        splitter_box_sp = findViewById(R.id.splitter_box_sp);
        rectangular_cement_rb = findViewById(R.id.rectangular_cement);
        rectangular_iron_rb = findViewById(R.id.rectangular_iron);
        round_cement_rb = findViewById(R.id.round_cement);
        clamp_error_yes_rb = findViewById(R.id.clamp_error_yes_rb);
        clamp_error_no_rb = findViewById(R.id.clamp_error_no_rb);
        line_type_1_rb = findViewById(R.id.line_type_1);
        line_type_2_rb = findViewById(R.id.line_type_2);
        line_type_3_rb = findViewById(R.id.line_type_3);
        cable_type_1_rb = findViewById(R.id.cable_type_1);
        cable_type_2_rb = findViewById(R.id.cable_type_2);
        cable_type_3_rb = findViewById(R.id.cable_type_3);
        cable_type_4_rb = findViewById(R.id.cable_type_4);
        cable_type_5_rb = findViewById(R.id.cable_type_5);
        cable_type_6_rb = findViewById(R.id.cable_type_6);
        cable_sag_yes_rb = findViewById(R.id.cable_sag_yes_rb);
        cable_sag_no_rb = findViewById(R.id.cable_sag_no_rb);
        enclosure_type_2way_rb = findViewById(R.id.enclosure_type_2way);
        enclosure_type_3way_rb = findViewById(R.id.enclosure_type_3way);

        enclosure_type_4way_rb = findViewById(R.id.enclosure_type_4way);
        enclosure_frame_yes_rb = findViewById(R.id.enclosure_frame_yes);
        enclosure_frame_no_rb = findViewById(R.id.enclosure_frame_no);
        suspension_cb = findViewById(R.id.suspension_cb);
        tension_cb = findViewById(R.id.tension_cb);
        wedge_cb = findViewById(R.id.wedge_cb);
        submit = findViewById(R.id.submit);
        imagePickerView = findViewById(R.id.pole_images);
        clamp_photo_id = findViewById(R.id.clamp_photo_id);
        progress_bar = findViewById(R.id.progress_bar);
        coordinatorLayout = findViewById(R.id.coordinatorLayout);
        userAPIService = NetworkUtils.provideUserAPIService(this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            setTitle("POLE");
        }

        if (getIntent().hasExtra(Location.class.getName())) {
            location = getIntent().getParcelableExtra(Location.class.getName());
        }

        imei = Utils.getIMEI(this);
        pole_radio_rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rectangular_cement:
                        pole_type = rectangular_cement_rb.getText().toString();
                        break;
                    case R.id.rectangular_iron:
                        pole_type = rectangular_iron_rb.getText().toString();
                        break;
                    case R.id.round_cement:
                        pole_type = round_cement_rb.getText().toString();
                        break;
                }
            }
        });

        clamp_error_details_rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.clamp_error_yes_rb:
                        clamp_error_details = clamp_error_yes_rb.getText().toString();
                        break;
                    case R.id.clamp_error_no_rb:
                        clamp_error_details = clamp_error_no_rb.getText().toString();
                        break;
                }
            }
        });

        line_type_rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.line_type_1:
                        line_type = line_type_1_rb.getText().toString();
                        break;
                    case R.id.line_type_2:
                        line_type = line_type_2_rb.getText().toString();
                        break;
                    case R.id.line_type_3:
                        line_type = line_type_3_rb.getText().toString();
                        break;
                }
            }
        });

        cable_sag_rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.cable_sag_yes_rb:
                        sag_txt = cable_sag_yes_rb.getText().toString();
                        break;
                    case R.id.cable_sag_no_rb:
                        sag_txt = cable_sag_no_rb.getText().toString();
                        break;
                }
            }
        });

        enlosure_type_rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.enclosure_type_2way:
                        enclosure_tye = enclosure_type_2way_rb.getText().toString();
                        break;
                    case R.id.enclosure_type_3way:
                        enclosure_tye = enclosure_type_3way_rb.getText().toString();
                        break;
                    case R.id.enclosure_type_4way:
                        enclosure_tye = enclosure_type_4way_rb.getText().toString();
                        break;
                }
            }
        });

        enclosure_frame_rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.enclosure_frame_yes:
                        enclosure_frame = enclosure_frame_yes_rb.getText().toString();
                        break;
                    case R.id.enclosure_frame_no:
                        enclosure_frame = enclosure_frame_no_rb.getText().toString();
                        break;
                }
            }
        });

        cable_type_rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.cable_type_1:
                        cable_type = cable_type_1_rb.getText().toString();
                        break;
                    case R.id.cable_type_2:
                        cable_type = cable_type_2_rb.getText().toString();
                        break;
                    case R.id.cable_type_3:
                        cable_type = cable_type_3_rb.getText().toString();
                        break;
                    case R.id.cable_type_4:
                        cable_type = cable_type_4_rb.getText().toString();
                        break;
                    case R.id.cable_type_5:
                        cable_type = cable_type_5_rb.getText().toString();
                        break;
                    case R.id.cable_type_6:
                        cable_type = cable_type_6_rb.getText().toString();
                        break;
                }
            }
        });

        clamp_photo_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImages();
            }
        });

        imagePickerView.setImagePickerListener(new ImagePickerView.ImagePickerListener() {
            @Override
            public void onClick() {
                pickImages();
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
                Utils.showToast(PoleActivity.this, "Successful!");
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

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePoleFormData(v);
            }
        });
    }

    public void pickImages() {
        startActivityForResult(getPickImageIntent(PoleActivity.this), CHOOSING_FILE_REQUEST);
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
        ArrayList<Uri> imageUri = new ArrayList<Uri>();

        if (requestCode == CHOOSING_FILE_REQUEST && resultCode == RESULT_OK && data == null) {
            Uri fileUri = getImageUri();
            imageUri.add(fileUri);
            picasso.load(fileUri).centerCrop().resize(100, 100).into(pole_img);
            imagePickerView.addImages(imageUri);
        }

        if (data != null && data.getClipData() != null) {
            int count = data.getClipData().getItemCount();
            for (int i = 0; i < count; i++) {
                imageUri.add(data.getClipData().getItemAt(i).getUri());
            }
            imagePickerView.addImages(imageUri);
        }
    }

    private void savePoleFormData(View v) {
        JSONObject formJson = new JSONObject();
        JSONObject geometry = new JSONObject();
        JSONArray coordinates = new JSONArray();
        JSONObject properties = new JSONObject();

        if (suspension_cb.isChecked())
            clamp_type = suspension_cb.getText().toString();
        if (tension_cb.isChecked())
            clamp_type = tension_cb.getText().toString();
        if (wedge_cb.isChecked())
            clamp_type = wedge_cb.getText().toString();

        if (pole_type == null) {
            Utils.showToast(this, "poly type cannot be empty");
        } else if (line_type == null) {
            Utils.showToast(this, "line type cannot be empty");
        } else if (cable_type == null) {
            Utils.showToast(this, "cable type cannot be empty");
        } else if (clamp_type == null) {
            Utils.showToast(this, "clamp type cannot be empty");
        } else if (enclosure_frame == null) {
            Utils.showToast(this, "enclosure frame cannot be empty");
        } else if (enclosure_tye == null) {
            Utils.showToast(this, "enclosure type cannot be empty");
        } else if (splitter_box_sp.getSelectedItemPosition() < 1) {
            Utils.showToast(this, "splitter &  box type cannot be empty");
        } else if (clamp_error_details == null) {
            Utils.showToast(this, "clamp error details cannot be empty");
        } else if (sag_txt == null)
            Utils.showToast(this, "cable sag cannot be empty");
        else {
            progress_bar.setVisibility(View.VISIBLE);

            try {
                geometry.put("type", "Point");
                coordinates.put(location.getLongitude());
                coordinates.put(location.getLatitude());
                formJson.put("type", "Feature");
                formJson.put("geometry", geometry);
                formJson.put("imei", imei);
                formJson.put("mobile", DataUtils.getMobileNumber(this));
                formJson.put("password", DataUtils.getPassword(this));
                formJson.put("password", DataUtils.getPassword(this));
                geometry.put("coordinates", coordinates);

                if (!TextUtils.isEmpty(pole_type))
                    properties.put("pole_type", pole_type);
                if (!TextUtils.isEmpty(line_type))
                    properties.put("line_type", line_type);
                if (!TextUtils.isEmpty(cable_type))
                    properties.put("cable_type", cable_type);
                if (!TextUtils.isEmpty(clamp_type))
                    properties.put("clamp_type", clamp_type);
                if (!TextUtils.isEmpty(enclosure_frame))
                    properties.put("enclosure_frame", enclosure_frame);
                if (!TextUtils.isEmpty(enclosure_tye))
                    properties.put("enclosure_tye", enclosure_tye);
                if (!TextUtils.isEmpty(splitter_box_sp.getSelectedItem().toString()))
                    properties.put("splitter_box_type", splitter_box_sp.getSelectedItem().toString());
                if (!TextUtils.isEmpty(clamp_error_details))
                    properties.put("clamp_error_details", clamp_error_details);
                if (!TextUtils.isEmpty(sag_txt))
                    properties.put("sag_txt", sag_txt);
                formJson.put("zonal", DataUtils.getZonal(this));
                formJson.put("district", DataUtils.getDistrict(this));
                formJson.put("district", DataUtils.getDistrict(this));
                properties.put("ts", System.currentTimeMillis());
                formJson.put("properties", properties);

                if (NetworkUtils.isConnectingToInternet(PoleActivity.this)) {
                    final RequestBody formDataRequestBody = RequestBody.create(MediaType.parse("application/json"), formJson.toString());

                    userAPIService.postPoleData("survey", formDataRequestBody).enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Response<ResponseBody> response, Retrofit retrofit) {
                            progress_bar.setVisibility(View.GONE);
                            if (response.isSuccess()) {
                                JSONObject responseJson = null;
                                try {
                                    responseJson = new JSONObject(response.body().string());
                                    String id = responseJson.getJSONObject("_id").getString("$oid");
                                    if (imagePickerView.getImages() != null && !imagePickerView.getImages().isEmpty()) {
                                        imagePickerView.uploadImages(TextUtils.isEmpty(id) ? "poles" : id);
                                    } else {
                                        Utils.showToast(PoleActivity.this, "Successful!");
                                        finish();
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
//                    Utils.getSnackbar(coordinatorLayout, "Unable to fetch data.");
                            Log.d("Form Failure", "onFailure: " + t.getMessage());
                        }
                    });
                } else {
                    progress_bar.setVisibility(View.GONE);
                    DataUtils.savePole(this, formJson);
                    if (imagePickerView.getImages() != null && !imagePickerView.getImages().isEmpty()) {
                        ArrayList<Uri> images = imagePickerView.getImages();
                        ArrayList<String> imgList = new ArrayList<>();
                        for (Uri image : images) {
                            imgList.add(image.toString());
                        }
                        DataUtils.savePoleImages(this, imgList);
                    }
                    finish();
                    Utils.showToast(this, "saved offline");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
            TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            imei = telephonyManager.getDeviceId();
            Log.d("IMEI", "onRequestPermissionsResult: " + telephonyManager.getDeviceId());
        }
    }
}
