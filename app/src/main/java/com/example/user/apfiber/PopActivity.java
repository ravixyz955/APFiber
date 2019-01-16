package com.example.user.apfiber;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewParent;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.user.apfiber.network.model.Pop;
import com.example.user.apfiber.network.service.UserAPIService;
import com.example.user.apfiber.utils.DataUtils;
import com.example.user.apfiber.utils.NetworkUtils;
import com.example.user.apfiber.utils.Utils;
import com.example.user.apfiber.widget.ImagePickerView;
import com.mapbox.geojson.Feature;
import com.mapbox.mapboxsdk.maps.MapboxMap;
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
import java.util.HashSet;
import java.util.List;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class PopActivity extends AppCompatActivity {
    private LinearLayout tk_photo_rack_id, olt_add_layout;
    private LinearLayout olt_equipment_layout, add_layout;
    private static final int CHOOSING_FILE_REQUEST = 1234;
    private Uri imgUri;
    private Button submit;
    private ImageView pop_img;
    private Picasso picasso = Picasso.get();
    private ImagePickerView imagePickerView;
    private Spinner pop_type_sp, electrical_meter_sp, router_sp;
    private Spinner olt_sp, name_of_pop_sp, name_of_district_sp;
    private Spinner name_of_zonal_sp;
    private Spinner rack_spinner_sp, earthpit_sp, rack_spinner_type_sp;
    private EditText ups_edt, battery_no;
    private EditText battery_backup, battery_rating;
    private TextView pop_premise;
    private LinearLayout rock_type_layout;
    private LinearLayout electronics_equipment_layout;
    private UserAPIService userAPIService;
    private Location location;
    private ArrayAdapter<String> popNamesSpinnerAdapter, districtsSpinnerAdapter;
    private ArrayAdapter<String> earthpitSpinnerAdapter, meterSpinnerAdapter;
    private ArrayAdapter<String> zonalSpinnerAdapter, popTypeSpinnerAdapter;
    private ArrayAdapter<String> adapter, rack_type_adapter;
    public static MapboxMap map;
    private ArrayList<Pop> districtList;
    private ArrayList<Pop> zonalList;
    private ArrayList<Pop> popNamesList;
    private TextView switch_add_btn, olt_add_btn;
    private FrameLayout progress_bar;
    private ArrayList<Pop> popsList, popDistrictsList;
    private Feature selectedFeature;
    private CoordinatorLayout coordinatorLayout;
    private String imei;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop);

        imagePickerView = findViewById(R.id.pop_images);
        tk_photo_rack_id = findViewById(R.id.tk_photo_rack_id);
        pop_type_sp = findViewById(R.id.pop_type_sp);
        pop_img = findViewById(R.id.pop_img);
        ups_edt = findViewById(R.id.ups_edt);
        battery_no = findViewById(R.id.battery_no);
        electrical_meter_sp = findViewById(R.id.electrical_meter_sp);
        rack_spinner_sp = findViewById(R.id.rack_spinner_sp);
        earthpit_sp = findViewById(R.id.earthpit_sp);
        rack_spinner_type_sp = findViewById(R.id.rack_spinner_type_sp);
        router_sp = findViewById(R.id.router_sp);
        olt_sp = findViewById(R.id.olt_sp);
        name_of_pop_sp = findViewById(R.id.name_of_pop_sp);
        name_of_district_sp = findViewById(R.id.name_of_district_sp);
        name_of_zonal_sp = findViewById(R.id.name_of_zonal_sp);
        olt_add_layout = findViewById(R.id.olt_add_layout);
        rock_type_layout = findViewById(R.id.rock_type_layout);
        electronics_equipment_layout = findViewById(R.id.electronics_equipment_layout);
        battery_backup = findViewById(R.id.battery_backup);
        olt_equipment_layout = findViewById(R.id.olt_equipment_layout);
        battery_rating = findViewById(R.id.battery_rating);
        switch_add_btn = findViewById(R.id.switch_add_btn);
        add_layout = findViewById(R.id.add_layout);
        olt_add_btn = findViewById(R.id.olt_add_btn);
        progress_bar = findViewById(R.id.progress_bar);
        pop_premise = findViewById(R.id.pop_premise);
        coordinatorLayout = findViewById(R.id.coordinatorLayout);

        submit = findViewById(R.id.submit);
        userAPIService = NetworkUtils.provideUserAPIService(this);
        popNamesSpinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, android.R.id.text1);
        districtsSpinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, android.R.id.text1);
        zonalSpinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, android.R.id.text1);
        popTypeSpinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, android.R.id.text1);
        meterSpinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, android.R.id.text1);
        earthpitSpinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, android.R.id.text1);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, android.R.id.text1);
        rack_type_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, android.R.id.text1);
        popNamesSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        districtsSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        zonalSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        popTypeSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        meterSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        earthpitSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        rack_type_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        name_of_pop_sp.setAdapter(popNamesSpinnerAdapter);
        name_of_district_sp.setAdapter(districtsSpinnerAdapter);
        name_of_zonal_sp.setAdapter(zonalSpinnerAdapter);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            setTitle("POP");
        }

        if (getIntent().hasExtra(Location.class.getName())) {
            location = getIntent().getParcelableExtra(Location.class.getName());
        }

        if (getIntent().hasExtra("imei")) {
            imei = getIntent().getStringExtra("imei");
        }

        if (getIntent().hasExtra(Feature.class.getName())) {
            selectedFeature = Feature.fromJson(getIntent().getStringExtra(Feature.class.getName()));
            Log.d("SDSDSD", "onCreate: " + selectedFeature.toString());
            setFormFeatureData(selectedFeature);
        }

        if (getIntent().hasExtra("districts")) {
            popDistrictsList = getIntent().getParcelableArrayListExtra("districts");
        }

        if (!NetworkUtils.isConnectingToInternet(this)) {
            popDistrictsList = DataUtils.getDistrictList(this);
            getOfflineDistList();
        } else {
            if (getIntent().hasExtra(Pop.class.getName())) {
                popsList = getIntent().getParcelableArrayListExtra(Pop.class.getName());
                if (popsList.size() > 0)
                    getDistrictsList();
            }
        }


        tk_photo_rack_id.setOnClickListener(new View.OnClickListener() {
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
                Utils.showToast(PopActivity.this, "Successful!");
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

        rack_spinner_sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (rack_spinner_sp.getSelectedItem().toString().equals("Yes")) {
                    if (rack_spinner_sp.isEnabled())
                        rock_type_layout.setVisibility(View.VISIBLE);
                    else {
//                        ((TextView) parent.getChildAt(0)).setTextColor(Color.parseColor("#86888A"));
//                        rack_spinner_sp.setEnabled(false);
                    }
                } else {
                    rock_type_layout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        rack_spinner_type_sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (rack_spinner_type_sp.isEnabled()) {

                } else {
                    ((TextView) parent.getChildAt(0)).setTextColor(Color.parseColor("#86888A"));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        router_sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                View v = null;
                if (router_sp.getSelectedItem().toString().contains("Yes")) {
                    if (router_sp.isEnabled())
                        electronics_equipment_layout.setVisibility(View.VISIBLE);
                } else {
                    electronics_equipment_layout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        olt_sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                View v = null;
                if (olt_sp.getSelectedItem().toString().contains("Yes")) {
                    if (olt_sp.isEnabled())
                        olt_equipment_layout.setVisibility(View.VISIBLE);
                } else {
                    olt_equipment_layout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        name_of_district_sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!name_of_district_sp.getSelectedItem().toString().contains("--Select--")) {
                    progress_bar.setVisibility(View.VISIBLE);
                    if (name_of_district_sp.isEnabled())
                        getZonalList(name_of_district_sp.getSelectedItem().toString());
                    else {
                        progress_bar.setVisibility(View.GONE);
                        ((TextView) parent.getChildAt(0)).setTextColor(Color.parseColor("#86888A"));
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        name_of_zonal_sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                name_of_district_sp.getSelectedItem().toString();
                String district_name, zonal_name;
                district_name = name_of_district_sp.getSelectedItem().toString();
                zonal_name = name_of_zonal_sp.getSelectedItem().toString();
                if (name_of_zonal_sp.isEnabled())
                    getNameofPop(district_name, zonal_name);
                else
                    ((TextView) parent.getChildAt(0)).setTextColor(Color.parseColor("#86888A"));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        name_of_pop_sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (name_of_pop_sp.isEnabled()) {

                } else
                    ((TextView) parent.getChildAt(0)).setTextColor(Color.parseColor("#86888A"));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        pop_type_sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (pop_type_sp.isEnabled()) {

                } else {
                    ((TextView) parent.getChildAt(0)).setTextColor(Color.parseColor("#86888A"));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        electrical_meter_sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (electrical_meter_sp.isEnabled()) {

                } else {
                    ((TextView) parent.getChildAt(0)).setTextColor(Color.parseColor("#86888A"));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        earthpit_sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (earthpit_sp.isEnabled()) {

                } else {
                    ((TextView) parent.getChildAt(0)).setTextColor(Color.parseColor("#86888A"));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        switch_add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater vi = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.add_switch_olt_layout, null);
                olt_add_layout.addView(v);
            }
        });

        olt_add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater vi = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.olt_serial_no, null);
                add_layout.addView(v);
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePOPFormData(v);
            }
        });
    }

    private void getOfflineDistList() {
        districtList = popDistrictsList;
        ArrayList<String> districtsList = new ArrayList<>();
        if (districtList.size() > 0) {
            districtsSpinnerAdapter.add("--Select--");

            for (Pop pop : districtList) {
                districtsList.add(pop.getDistrict());
            }
            HashSet<String> hashSet = new HashSet<>();
            hashSet.addAll(districtsList);
            districtsList.clear();
            districtsList.addAll(hashSet);
            districtsSpinnerAdapter.addAll(districtsList);
            if (districtsList.contains(DataUtils.getDistrict(PopActivity.this))) {
                int position = districtsSpinnerAdapter.getPosition(DataUtils.getDistrict(PopActivity.this));
                name_of_district_sp.setSelection(position);
            }
            districtsSpinnerAdapter.notifyDataSetChanged();
        }
    }

    private void setFormFeatureData(Feature selectedFeature) {
        if (selectedFeature.hasProperty("district")) {
            districtsSpinnerAdapter.add(selectedFeature.getStringProperty("district"));
            name_of_district_sp.setAdapter(districtsSpinnerAdapter);
            name_of_district_sp.setEnabled(false);
        }
        if (selectedFeature.hasProperty("zonal")) {
            zonalSpinnerAdapter.add(selectedFeature.getStringProperty("zonal"));
            name_of_zonal_sp.setAdapter(zonalSpinnerAdapter);
            name_of_zonal_sp.setEnabled(false);
        }
        if (selectedFeature.hasProperty("pop name")) {
            popNamesSpinnerAdapter.add(selectedFeature.getStringProperty("pop name"));
            name_of_pop_sp.setAdapter(popNamesSpinnerAdapter);
            name_of_pop_sp.setEnabled(false);
        }
        if (selectedFeature.hasProperty("pop_type")) {
            popTypeSpinnerAdapter.add(selectedFeature.getStringProperty("pop_type"));
            pop_type_sp.setAdapter(popTypeSpinnerAdapter);
            pop_type_sp.setEnabled(false);
        }
        if (selectedFeature.hasProperty("meter")) {
            meterSpinnerAdapter.add(selectedFeature.getStringProperty("meter"));
            electrical_meter_sp.setAdapter(meterSpinnerAdapter);
            electrical_meter_sp.setEnabled(false);
        }
        if (selectedFeature.hasProperty("earthpit_sp")) {
            earthpitSpinnerAdapter.add(selectedFeature.getStringProperty("earthpit_sp"));
            earthpit_sp.setAdapter(earthpitSpinnerAdapter);
            earthpit_sp.setEnabled(false);
        }
        if (selectedFeature.hasProperty("ups")) {
            ups_edt.setText(selectedFeature.getStringProperty("ups"));
            ups_edt.setEnabled(false);
            ups_edt.setTextColor(Color.parseColor("#86888A"));
        }
        if (selectedFeature.hasProperty("battery_backup")) {
            battery_backup.setText(selectedFeature.getStringProperty("battery_backup"));
            battery_backup.setEnabled(false);
            battery_backup.setTextColor(Color.parseColor("#86888A"));

        }
        if (selectedFeature.hasProperty("battery_no")) {
            battery_no.setText(selectedFeature.getStringProperty("battery_no"));
            battery_no.setEnabled(false);
            battery_no.setTextColor(Color.parseColor("#86888A"));

        }
        if (selectedFeature.hasProperty("battery_rating")) {
            battery_rating.setText(selectedFeature.getStringProperty("battery_rating"));
            battery_rating.setEnabled(false);
            battery_rating.setTextColor(Color.parseColor("#86888A"));
        }
        if (selectedFeature.hasProperty("rack_spinner_type_sp")) {
            adapter.clear();
            adapter.add("Yes");
            rack_spinner_sp.setAdapter(adapter);
            rack_spinner_sp.setEnabled(false);
            rack_type_adapter.add(selectedFeature.getStringProperty("rack_spinner_type_sp"));
            rock_type_layout.setVisibility(View.VISIBLE);
            rack_spinner_type_sp.setAdapter(rack_type_adapter);
            rack_spinner_type_sp.setEnabled(false);
        }
        if (selectedFeature.hasProperty("router_serial_no")) {
            adapter.add("Yes");
            router_sp.setAdapter(adapter);
            router_sp.setEnabled(false);
            ViewParent viewParent = router_sp.getParent();
            electronics_equipment_layout.setVisibility(View.GONE);
        } else {
            adapter.add("No");
            router_sp.setAdapter(adapter);
            router_sp.setEnabled(false);
        }
        if (selectedFeature.hasProperty("olt_serial_no")) {
            adapter.add("Yes");
            olt_sp.setAdapter(adapter);
            olt_sp.setEnabled(false);
            olt_equipment_layout.setVisibility(View.GONE);
        } else {
            adapter.add("No");
            olt_sp.setAdapter(adapter);
            olt_sp.setEnabled(false);
        }
        if (selectedFeature.hasProperty("ts")) {
            submit.setVisibility(View.GONE);
            imagePickerView.setVisibility(View.GONE);
            tk_photo_rack_id.setVisibility(View.GONE);
            pop_premise.setVisibility(View.GONE);
        }
    }

    private void getNameofPop(String district_name, String zonal_name) {
        popNamesList = new ArrayList<>();
        popNamesSpinnerAdapter.clear();
        if (popsList != null && popsList.size() > 0)
            popNamesList = popsList;

        if (!NetworkUtils.isConnectingToInternet(this)) {
            popsList = DataUtils.getPopsList(this);
            popNamesList = popsList;
        }

        if (popNamesList.size() > 0) {
            popNamesSpinnerAdapter.add("--Select--");
            for (Pop pop : popNamesList) {
                if (pop.getZonal().contains(zonal_name) && pop.getDistrict().contains(district_name))
                    popNamesSpinnerAdapter.add(pop.getPopDetail());
            }
        }
    }

    private void getZonalList(String districtName) {

        progress_bar.setVisibility(View.GONE);
        ArrayList<String> zonalsList = new ArrayList<>();
        zonalList = new ArrayList<>();
        zonalList.clear();
        zonalSpinnerAdapter.clear();
        if (popsList != null && popsList.size() > 0)
            zonalList = popDistrictsList;
        else
            zonalList = popDistrictsList;

        if (zonalList.size() > 0) {
            zonalSpinnerAdapter.add("--Select--");
            for (Pop zonal : zonalList) {
                if (zonal.getDistrict().contains(districtName))
                    zonalsList.add(zonal.getZonal());
            }
            HashSet<String> hashSet = new HashSet<>();
            hashSet.addAll(zonalsList);
            zonalsList.clear();
            zonalsList.addAll(hashSet);
            zonalSpinnerAdapter.addAll(zonalsList);
            zonalSpinnerAdapter.notifyDataSetChanged();
            if (zonalsList.contains(DataUtils.getZonal(PopActivity.this))) {
                int position = zonalSpinnerAdapter.getPosition(DataUtils.getZonal(PopActivity.this));
                name_of_zonal_sp.setSelection(position);
            } else {
                name_of_zonal_sp.setSelection(0);
            }
        }
    }

    private void getDistrictsList() {
        progress_bar.setVisibility(View.VISIBLE);
        progress_bar.setVisibility(View.GONE);
        ArrayList<String> districtsList = new ArrayList<>();

        if (popsList.size() > 0)
            districtList = popDistrictsList;
        if (districtList.size() > 0) {
            districtsSpinnerAdapter.add("--Select--");

            for (Pop pop : districtList) {
                districtsList.add(pop.getDistrict());
            }
            HashSet<String> hashSet = new HashSet<>();
            hashSet.addAll(districtsList);
            districtsList.clear();
            districtsList.addAll(hashSet);
            districtsSpinnerAdapter.addAll(districtsList);
            if (districtsList.contains(DataUtils.getDistrict(PopActivity.this))) {
                int position = districtsSpinnerAdapter.getPosition(DataUtils.getDistrict(PopActivity.this));
                name_of_district_sp.setSelection(position);
            }
            districtsSpinnerAdapter.notifyDataSetChanged();
        }
    }

    public void pickImages() {
        startActivityForResult(getPickImageIntent(PopActivity.this), CHOOSING_FILE_REQUEST);
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

    private void savePOPFormData(View v) {
        JSONObject formJson = new JSONObject();
        JSONObject geometry = new JSONObject();
        JSONArray coordinates = new JSONArray();
        JSONObject properties = new JSONObject();
        JSONObject switch_olt = new JSONObject();
        JSONObject olt_serial = new JSONObject();


        if (name_of_district_sp.getSelectedItemPosition() < 1) {
            name_of_district_sp.requestFocus();
            Utils.showToast(this, "district cannot be empty");
        } else if (name_of_zonal_sp.getSelectedItemPosition() < 1) {
            name_of_zonal_sp.requestFocus();
            Utils.showToast(this, "zonal cannot be empty");
        } else if (name_of_pop_sp.getSelectedItemPosition() < 1) {
            name_of_pop_sp.requestFocus();
            Utils.showToast(this, "Name of pop cannot be empty");
        } else if (pop_type_sp.getSelectedItemPosition() < 1) {
            pop_type_sp.requestFocus();
            Utils.showToast(this, "pop type cannot be empty");
        } else if (electrical_meter_sp.getSelectedItemPosition() < 1) {
            electrical_meter_sp.requestFocus();
            Utils.showToast(this, "meter cannot be empty");
        } else if (earthpit_sp.getSelectedItemPosition() < 1) {
            earthpit_sp.requestFocus();
            Utils.showToast(this, "earth pit cannot be empty");
        } else if (TextUtils.isEmpty(ups_edt.getText().toString().trim())) {
            ups_edt.requestFocus();
            ups_edt.setError("enter ups hrs");
            Utils.showToast(this, "up cannot be empty");
        } else if (TextUtils.isEmpty(battery_backup.getText().toString().trim())) {
            battery_backup.requestFocus();
            battery_backup.setError("enter battery backup");
            Utils.showToast(this, "battery backup cannot be empty");
        } else if (TextUtils.isEmpty(battery_no.getText().toString().trim())) {
            battery_no.setError("enter battery no");
            Utils.showToast(this, "battery no cannot be empty");
        } else if (TextUtils.isEmpty(battery_rating.getText().toString())) {
            battery_rating.requestFocus();
            battery_rating.setError("enter battery rating");
            Utils.showToast(this, "battery rating cannot be empty");
        } else if (rack_spinner_sp.getSelectedItemPosition() < 1) {
            rack_spinner_sp.requestFocus();
            ((TextView) rack_spinner_sp.getSelectedView()).setError("cannot be empty");
            Utils.showToast(this, "rack cannot be empty");
        } else if (router_sp.getSelectedItemPosition() < 1) {
            router_sp.requestFocus();
            ((TextView) router_sp.getSelectedView()).setError("cannot be empty");
            Utils.showToast(this, "router cannot be empty");
        } else if (olt_sp.getSelectedItemPosition() < 1) {
            olt_sp.requestFocus();
            ((TextView) router_sp.getSelectedView()).setError("cannot be empty");
            Utils.showToast(this, "olt cannot be empty");
        } else {
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
                geometry.put("coordinates", coordinates);
                if (!TextUtils.isEmpty(name_of_district_sp.getSelectedItem().toString()))
                    properties.put("district", name_of_district_sp.getSelectedItem().toString());
                if (!TextUtils.isEmpty(name_of_zonal_sp.getSelectedItem().toString()))
                    properties.put("zonal", name_of_zonal_sp.getSelectedItem().toString());
                if (!TextUtils.isEmpty(name_of_pop_sp.getSelectedItem().toString()))
                    properties.put("pop name", name_of_pop_sp.getSelectedItem().toString());
                if (!TextUtils.isEmpty(pop_type_sp.getSelectedItem().toString()))
                    properties.put("pop_type", pop_type_sp.getSelectedItem().toString());
                if (!TextUtils.isEmpty(electrical_meter_sp.getSelectedItem().toString()))
                    properties.put("meter", electrical_meter_sp.getSelectedItem().toString());
                if (!TextUtils.isEmpty(earthpit_sp.getSelectedItem().toString()))
                    properties.put("earthpit_sp", earthpit_sp.getSelectedItem().toString());
                if (!TextUtils.isEmpty(ups_edt.getText()))
                    properties.put("ups", ups_edt.getText().toString());
                if (!TextUtils.isEmpty(battery_backup.getText()))
                    properties.put("battery_backup", battery_backup.getText().toString());
                if (!TextUtils.isEmpty(battery_no.getText()))
                    properties.put("battery_no", battery_no.getText().toString());
                if (!TextUtils.isEmpty(battery_rating.getText()))
                    properties.put("battery_rating", battery_rating.getText().toString());
                if (!TextUtils.isEmpty(rack_spinner_type_sp.getSelectedItem().toString()))
                    properties.put("rack_spinner_type_sp", rack_spinner_type_sp.getSelectedItem().toString());
                if (olt_add_layout != null) {
                    int switch_count = olt_add_layout.getChildCount();
                    for (int i = 0; i < switch_count; i++) {
                        final View row = olt_add_layout.getChildAt(i);
                        EditText switch_no_edt = (EditText) row.findViewById(R.id.switch_serial_no);
                        switch_olt.put("router_" + (i + 1), switch_no_edt.getText().toString());
                    }
                }
                if (add_layout != null) {
                    int olt_count = add_layout.getChildCount();
                    for (int i = 0; i < olt_count; i++) {
                        final View row = add_layout.getChildAt(i);
                        EditText olt_no_edt = (EditText) row.findViewById(R.id.olt_serial_no);
                        olt_serial.put("olt_" + (i + 1), olt_no_edt.getText().toString());
                    }
                }

                formJson.put("router_serial_no", switch_olt);
                formJson.put("olt_serial_no", olt_serial);
                formJson.put("zonal", DataUtils.getZonal(this));
                formJson.put("district", DataUtils.getDistrict(this));
                properties.put("ts", System.currentTimeMillis());
                formJson.put("properties", properties);

                if (NetworkUtils.isConnectingToInternet(this)) {
                    final RequestBody formDataRequestBody = RequestBody.create(MediaType.parse("application/json"), formJson.toString());

                    userAPIService.postPopData("survey", formDataRequestBody).enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Response<ResponseBody> response, Retrofit retrofit) {
                            progress_bar.setVisibility(View.GONE);
                            if (response.isSuccess()) {
                                JSONObject responseJson = null;
                                try {
                                    responseJson = new JSONObject(response.body().string());
                                    String id = responseJson.getJSONObject("_id").getString("$oid");
                                    if (imagePickerView.getImages() != null && !imagePickerView.getImages().isEmpty()) {
                                        imagePickerView.uploadImages(TextUtils.isEmpty(id) ? "pops" : id);
                                    } else {
                                        Utils.showToast(PopActivity.this, "Successful!");
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
                    DataUtils.savePop(this, formJson);
                    if (imagePickerView.getImages() != null && !imagePickerView.getImages().isEmpty()) {
                        ArrayList<Uri> images = imagePickerView.getImages();
                        ArrayList<String> imgList = new ArrayList<>();
                        for (Uri image : images) {
                            imgList.add(image.toString());
                        }
                        DataUtils.savePopImages(this, imgList);
                    }
                    finish();
                    Utils.showToast(this, "saved offline");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
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
            picasso.load(fileUri).centerCrop().resize(100, 100).into(pop_img);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
