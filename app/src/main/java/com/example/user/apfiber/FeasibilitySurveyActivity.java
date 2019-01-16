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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.user.apfiber.network.model.FeasibleSurvey;
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
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class FeasibilitySurveyActivity extends AppCompatActivity {
    private static final int CHOOSING_FILE_REQUEST = 1234;
    private EditText id_edt;
    private LinearLayout add_ins_commi_layout;
    private ImageView add_pop_pole_img;
    private LayoutInflater inflater;
    private TextView title_view, card_add_img_title;
    private TextView id_txt;
    private ImageView card_view_image;
    private ImagePickerView imagePickerView;
    private Button feasibile_ok, feasible_not_ok;
    private Animation animation;
    private ArrayList<Uri> imageUri;
    private ArrayList<Long> timeStamp;
    private ArrayList<Double> latitude, longitude;
    private RealmList<String> typeStr;
    private RealmList<String> uriStr;
    private RealmList<String> ts;
    private RealmList<String> lat;
    private RealmList<String> longi;
    private FeasibleSurvey feasibleSurvey;

    private View custom_view;
    private String label_txt;
    private CharSequence[] items;
    private FrameLayout progress_bar;
    private UserAPIService userAPIService;
    private Location location;
    private Realm mRealm;
    private FeasibleSurvey mFeasibleSurvey;
    private Picasso picasso = Picasso.get();
    private Uri imgUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feasibility_survey);

        id_edt = findViewById(R.id.id_edt);
        id_txt = findViewById(R.id.id_txt);
        add_pop_pole_img = findViewById(R.id.add_pop_pole_img);
        feasibile_ok = findViewById(R.id.feasibile_ok);
        feasible_not_ok = findViewById(R.id.feasible_not_ok);
        add_ins_commi_layout = findViewById(R.id.add_ins_commi_layout);
        progress_bar = findViewById(R.id.progress_bar);
        inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        userAPIService = NetworkUtils.provideUserAPIService(this);
        imagePickerView = findViewById(R.id.pole_images);
        animation = AnimationUtils.loadAnimation(
                getApplication(), R.anim.slide_top_to_bottom);
        items = new CharSequence[]{"POP/Splicer", "Pole"};
        imageUri = new ArrayList<Uri>();
        timeStamp = new ArrayList<>();
        latitude = new ArrayList<>();
        longitude = new ArrayList<>();
        mFeasibleSurvey = new FeasibleSurvey();
        Realm.init(getApplicationContext());
        mRealm = Realm.getDefaultInstance();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            setTitle("Feasibility Survey");
        }
        if (getIntent().hasExtra("editMode")) {
            id_edt.setEnabled(false);
            add_pop_pole_img.setVisibility(View.GONE);
            feasibile_ok.setVisibility(View.GONE);
            feasible_not_ok.setVisibility(View.GONE);
        }

        getSavedFormData();
        add_pop_pole_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int vCount = add_ins_commi_layout.getChildCount();
                boolean photoNotTaken = false;
                if (vCount > 0) {
                    for (int i = 0; i < vCount; i++) {
                        View view = add_ins_commi_layout.getChildAt(i);
                        card_view_image = view.findViewById(R.id.card_view_image);
                        if (card_view_image.getDrawable() == null) {
                            photoNotTaken = true;
                        }
                    }
                }

                if (photoNotTaken)
                    Utils.showToast(FeasibilitySurveyActivity.this, "Photo not taken!");
                else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(
                            FeasibilitySurveyActivity.this);
                    builder.setTitle("Select Option");
                    builder.setCancelable(false);
                    builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            custom_view = inflater.inflate(R.layout.pole_pop_inflate_layout, null);
                            title_view = custom_view.findViewById(R.id.card_view_image_title);
                            card_add_img_title = custom_view.findViewById(R.id.card_add_img_title);
                            card_view_image = custom_view.findViewById(R.id.card_view_image);
                            title_view.setText(items[which]);

                            card_add_img_title.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    pickImages();
                                }
                            });
                        }
                    });
                    builder.setPositiveButton("Ok",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(
                                        final DialogInterface dialogInterface,
                                        final int i) {
                                    add_ins_commi_layout.addView(custom_view);
                                }
                            });
                    builder.setNegativeButton("Cancel", null);
                    builder.create().show();
                }
            }
        });

        feasibile_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(
                        FeasibilitySurveyActivity.this);
                JSONObject formJson = new JSONObject();
                JSONObject geometry = new JSONObject();
                JSONArray pole_pop_details = new JSONArray();
                JSONObject properties = new JSONObject();
                builder.setTitle("Feasible");
                builder.setCancelable(false);
                builder.setMessage("Are you sure want to proceed?");
                builder.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(
                                    final DialogInterface dialogInterface,
                                    final int i) {
                                if (TextUtils.isEmpty(id_edt.getText())) {
                                    Utils.showToast(FeasibilitySurveyActivity.this, "Id cannot be empty");
                                } else {
                                    progress_bar.setVisibility(View.VISIBLE);
                                    imagePickerView.addImages(imageUri);
                                    try {
                                        geometry.put("type", "Point");
                                        formJson.put("type", "Feature");
                                        formJson.put("ID", id_edt.getText().toString());
                                        formJson.put("geometry", geometry);
                                        formJson.put("mobile", DataUtils.getMobileNumber(FeasibilitySurveyActivity.this));
                                        formJson.put("password", DataUtils.getPassword(FeasibilitySurveyActivity.this));
                                        properties.put("ts", System.currentTimeMillis());

                                        if (add_ins_commi_layout != null) {
                                            int pop_pole_count = add_ins_commi_layout.getChildCount();
                                            for (int i1 = 0; i1 < pop_pole_count; i1++) {
                                                JSONObject pop_pole = new JSONObject();
                                                JSONArray coordinates = new JSONArray();
                                                final View row = add_ins_commi_layout.getChildAt(i1);
                                                TextView title = row.findViewById(R.id.card_view_image_title);
                                                pop_pole.put("type", title.getText().toString());
                                                pop_pole.put("uri", imageUri.get(i1));
                                                pop_pole.put("timeStamp", timeStamp.get(i1));
                                                coordinates.put(latitude.get(i1));
                                                coordinates.put(longitude.get(i1));
                                                pop_pole.put("coordinates", coordinates);
                                                pole_pop_details.put(pop_pole);
                                            }
                                            formJson.put("pole_pop_details", pole_pop_details);

                                            progress_bar.setVisibility(View.GONE);
                                            DataUtils.saveFeasibility(FeasibilitySurveyActivity.this, formJson);
                                            if (imagePickerView.getImages() != null && !imagePickerView.getImages().isEmpty()) {
                                                ArrayList<Uri> images = imagePickerView.getImages();
                                                ArrayList<String> imgList = new ArrayList<>();
                                                for (Uri image : images) {
                                                    imgList.add(image.toString());
                                                }
                                                DataUtils.saveFeasibilityImages(FeasibilitySurveyActivity.this, imgList);
                                            }
                                            finish();
                                            Utils.showToast(FeasibilitySurveyActivity.this, "saved offline");
                                            if (feasibleSurvey != null) {
                                                mRealm.beginTransaction();
                                                feasibleSurvey.deleteFromRealm();
                                                mRealm.commitTransaction();
                                            }
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });
                builder.setNegativeButton("No", null);
                builder.create().show();
            }
        });

        feasible_not_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(
                        FeasibilitySurveyActivity.this);
                JSONObject formJson = new JSONObject();
                JSONObject geometry = new JSONObject();
                JSONArray pole_pop_details = new JSONArray();
                JSONObject properties = new JSONObject();
                custom_view = inflater.inflate(R.layout.custom_view, null);
                EditText reason_edt_txt = custom_view.findViewById(R.id.reason_edt_txt);
                builder.setTitle("Not Feasible");
                builder.setCancelable(false);
                builder.setView(custom_view);
                builder.setPositiveButton("Submit",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(
                                    final DialogInterface dialogInterface,
                                    final int i) {
                                progress_bar.setVisibility(View.VISIBLE);
                                if (TextUtils.isEmpty(id_edt.getText())) {
                                    Utils.showToast(FeasibilitySurveyActivity.this, "Id cannot be empty");
                                } else {
                                    progress_bar.setVisibility(View.VISIBLE);
                                    imagePickerView.addImages(imageUri);
                                    try {
                                        geometry.put("type", "Point");
                                        formJson.put("type", "Feature");
                                        formJson.put("ID", id_edt.getText().toString());
                                        formJson.put("geometry", geometry);
                                        if (!TextUtils.isEmpty(reason_edt_txt.getText()))
                                            formJson.put("not_feasible_reason", reason_edt_txt.getText().toString());
                                        formJson.put("mobile", DataUtils.getMobileNumber(FeasibilitySurveyActivity.this));
                                        formJson.put("password", DataUtils.getPassword(FeasibilitySurveyActivity.this));
                                        properties.put("ts", System.currentTimeMillis());

                                        if (add_ins_commi_layout != null) {
                                            int pop_pole_count = add_ins_commi_layout.getChildCount();
                                            for (int i1 = 0; i1 < pop_pole_count; i1++) {
                                                JSONObject pop_pole = new JSONObject();
                                                JSONArray coordinates = new JSONArray();
                                                final View row = add_ins_commi_layout.getChildAt(i1);
                                                TextView title = row.findViewById(R.id.card_view_image_title);
                                                pop_pole.put("type", title.getText().toString());
//                                                pop_pole.put("uri", imageUri.get(i1));
                                                pop_pole.put("timeStamp", timeStamp.get(i1));
                                                coordinates.put(latitude.get(i1));
                                                coordinates.put(longitude.get(i1));
                                                pop_pole.put("coordinates", coordinates);
                                                pole_pop_details.put(pop_pole);
                                            }
                                            formJson.put("pole_pop_details", pole_pop_details);

                                            progress_bar.setVisibility(View.GONE);
                                            DataUtils.saveFeasibility(FeasibilitySurveyActivity.this, formJson);
                                            if (imagePickerView.getImages() != null && !imagePickerView.getImages().isEmpty()) {
                                                ArrayList<Uri> images = imagePickerView.getImages();
                                                ArrayList<String> imgList = new ArrayList<>();
                                                for (Uri image : images) {
                                                    imgList.add(image.toString());
                                                }
                                                DataUtils.saveFeasibilityImages(FeasibilitySurveyActivity.this, imgList);
                                            }
                                            finish();
                                            Utils.showToast(FeasibilitySurveyActivity.this, "saved offline");
                                            if (feasibleSurvey != null) {
                                                mRealm.beginTransaction();
                                                feasibleSurvey.deleteFromRealm();
                                                mRealm.commitTransaction();
                                            }
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });
                builder.setNegativeButton("Cancel", null);
                builder.create().show();
            }
        });

        imagePickerView.setImagePickerListener(new ImagePickerView.ImagePickerListener() {
            @Override
            public void onClick() {
//                pickImages();
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
                Utils.showToast(FeasibilitySurveyActivity.this, "Successful!");
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

    private void getSavedFormData() {
        feasibleSurvey = mRealm.where(FeasibleSurvey.class).equalTo("offSet", 1).findFirst();
        if (feasibleSurvey != null) {
            typeStr = feasibleSurvey.getTypeStr();
            uriStr = feasibleSurvey.getUriStr();
            ts = feasibleSurvey.getTs();
            lat = feasibleSurvey.getLatitude();
            longi = feasibleSurvey.getLongtitude();
            id_edt.setText(feasibleSurvey.getUid());
            for (int i = 0; i < typeStr.size(); i++) {

                custom_view = inflater.inflate(R.layout.pole_pop_inflate_layout, null);
                title_view = custom_view.findViewById(R.id.card_view_image_title);
                card_add_img_title = custom_view.findViewById(R.id.card_add_img_title);
                card_view_image = custom_view.findViewById(R.id.card_view_image);
                title_view.setText(typeStr.get(i));
                imageUri.add(Uri.parse(uriStr.get(i)));
                timeStamp.add(Long.parseLong(ts.get(i)));
                latitude.add(Double.parseDouble(lat.get(i)));
                longitude.add(Double.parseDouble(longi.get(i)));
                card_add_img_title.setVisibility(View.GONE);
                card_view_image.setVisibility(View.VISIBLE);
                picasso.load(Uri.parse(uriStr.get(i))).centerCrop().resize(100, 100).into(card_view_image);

                add_ins_commi_layout.addView(custom_view);

                card_add_img_title.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pickImages();
                    }
                });
            }
        }
    }

    private void pickImages() {
        startActivityForResult(getPickImageIntent(FeasibilitySurveyActivity.this), CHOOSING_FILE_REQUEST);
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

        if (requestCode == CHOOSING_FILE_REQUEST && resultCode == RESULT_OK && data == null) {
            Uri fileUri = getImageUri();
            location = InstallationCommsioningActivity.getLocation();
//            location = FusedLocationActivity.getFusedLocation();
            imageUri.add(fileUri);
            timeStamp.add(System.currentTimeMillis());
            if (location == null) {
                Utils.showToast(FeasibilitySurveyActivity.this, "Unable to fetch location");
                finish();
            } else {
                latitude.add(location.getLatitude());
                longitude.add(location.getLongitude());
            }

            if (imageUri.size() > 0) {
                card_add_img_title.setVisibility(View.GONE);
                card_view_image.setVisibility(View.VISIBLE);
                picasso.load(fileUri).centerCrop().resize(100, 100).into(card_view_image);
            }
        }

        if (data != null && data.getClipData() != null) {
            int count = data.getClipData().getItemCount();
            for (int i = 0; i < count; i++) {
                imageUri.add(data.getClipData().getItemAt(i).getUri());
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.feasibility_menu, menu);
        if (getIntent().hasExtra("editMode")) {
            MenuItem clear_btn = menu.getItem(0);
            clear_btn.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            if (add_ins_commi_layout.getChildCount() != imageUri.size())
                Utils.showToast(this, "Photo not taken!");
            else {
                finish();
                saveFormData();
            }
            return true;
        } else if (id == R.id.clear) {
            if (add_ins_commi_layout.getChildCount() > 0) {

                final AlertDialog.Builder builder = new AlertDialog.Builder(
                        FeasibilitySurveyActivity.this);
                builder.setCancelable(false);
                builder.setMessage("Are you sure want to clear?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Utils.showToast(FeasibilitySurveyActivity.this, "cleared!");
                        if (imageUri != null)
                            imageUri.clear();
                        if (timeStamp != null)
                            timeStamp.clear();
                        if (latitude != null)
                            latitude.clear();
                        if (longitude != null)
                            longitude.clear();
                        if (!TextUtils.isEmpty(id_edt.getText()))
                            id_edt.getText().clear();
                        if (feasibleSurvey != null) {
                            mRealm.beginTransaction();
                            feasibleSurvey.deleteFromRealm();
                            mRealm.commitTransaction();
                        }
                        add_ins_commi_layout.startAnimation(animation);
                        add_ins_commi_layout.removeAllViews();
                    }
                });
                builder.setNegativeButton("No", null);
                builder.create().show();
            } else {
                Utils.showToast(FeasibilitySurveyActivity.this, "Nothing to clear!");
            }
        }
        return super.

                onOptionsItemSelected(item);

    }

    private void saveFormData() {
        int vCount = add_ins_commi_layout.getChildCount();
        typeStr = new RealmList<String>();
        uriStr = new RealmList<String>();
        ts = new RealmList<String>();
        lat = new RealmList<String>();
        longi = new RealmList<String>();

        if (vCount > 0) {
            mFeasibleSurvey.setUid(id_edt.getText().toString());
            mFeasibleSurvey.setOffSet(1);
            for (int i = 0; i < vCount; i++) {
                View v = add_ins_commi_layout.getChildAt(i);
                TextView title = v.findViewById(R.id.card_view_image_title);
                typeStr.add(title.getText().toString());
                uriStr.add(imageUri.get(i).toString());
                ts.add(timeStamp.get(i).toString());
                lat.add(latitude.get(i).toString());
                longi.add(longitude.get(i).toString());
            }
            mFeasibleSurvey.setTypeStr(typeStr);
            mFeasibleSurvey.setUriStr(uriStr);
            mFeasibleSurvey.setTs(ts);
            mFeasibleSurvey.setLatitude(lat);
            mFeasibleSurvey.setLongtitude(longi);
        }
        mRealm.beginTransaction();
        mRealm.insertOrUpdate(mFeasibleSurvey);
        mRealm.commitTransaction();
    }

    @Override
    public void onBackPressed() {
        if (add_ins_commi_layout.getChildCount() != imageUri.size())
            Utils.showToast(this, "Photo not taken!");
        else {
            super.onBackPressed();
            saveFormData();
        }
    }
}
