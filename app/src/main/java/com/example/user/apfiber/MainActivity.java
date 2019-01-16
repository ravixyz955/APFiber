package com.example.user.apfiber;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.RectF;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.StrictMode;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.example.user.apfiber.network.model.Pop;
import com.example.user.apfiber.network.service.UserAPIService;
import com.example.user.apfiber.utils.DataUtils;
import com.example.user.apfiber.utils.NetworkUtils;
import com.example.user.apfiber.utils.Utils;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineListener;
import com.mapbox.android.core.location.LocationEnginePriority;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin;
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.CameraMode;
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.RenderMode;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.Property;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, PermissionsListener, LocationEngineListener {
    private PermissionsManager permissionsManager;
    private LocationLayerPlugin locationPlugin;
    private LocationEngine locationEngine;
    public static MapView mapView;
    public static MapboxMap map;
    private FloatingSearchView mSearchView;
    private Location location;
    private UserAPIService userapiService;
    private FrameLayout progress_bar;
    private ArrayList<Feature> featureList;
    private ArrayList<Pop> popsList, districtsList;
    private CoordinatorLayout coordinatorLayout;
    public static final String SURVEY_GEOJSON_SOURCE = "risks_geojson-source";
    public static final String POLE_GEOJSON_SOURCE = "pole_geojson-source";
    public static final String POP_GEOJSON_SOURCE = "pop_geojson-source";
    public static final String LINE_SOURCE = "line-source";
    private static final String imei = "352313077197359/01";
    private int mDpi = 0;
    private Feature selectedFeature;
    private List<Point> routeCoordinates;
    private String ts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mapView = (MapView) findViewById(R.id.survey_mapView);
        progress_bar = findViewById(R.id.progress_bar);
        mSearchView = findViewById(R.id.floating_search_view);
        coordinatorLayout = findViewById(R.id.coordinatorLayout);
        userapiService = NetworkUtils.provideUserAPIService(this);

        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        mDpi = getResources().getDisplayMetrics().densityDpi;

        if (!NetworkUtils.isConnectingToInternet(this)) {
            progress_bar.setVisibility(View.GONE);
            com.example.user.apfiber.utils.Utils.showToast(this, "No Internet!");
        }

//        addSurveyFeatureList();
        getPopsList();
        getDistrictList();

        mSearchView.setOnLeftMenuClickListener(new FloatingSearchView.OnLeftMenuClickListener() {
            @Override
            public void onMenuOpened() {
            }

            @Override
            public void onMenuClosed() {
                finish();
            }
        });
        mSearchView.setOnMenuItemClickListener(new FloatingSearchView.OnMenuItemClickListener() {
            @Override
            public void onActionMenuItemSelected(MenuItem item) {
                int id = item.getItemId();

                switch (id) {
                    case R.id.pole_id:
                        Intent pole_intent = new Intent(MainActivity.this, PoleActivity.class);
                        pole_intent.putExtra(Location.class.getName(), location);
                        pole_intent.putExtra("imei", imei);
                        startActivity(pole_intent);
                        break;
                    case R.id.pop_id:
                        Intent pop_intent = new Intent(MainActivity.this, PopActivity.class);
                        if (popsList != null && popsList.size() > 0)
                            pop_intent.putParcelableArrayListExtra(Pop.class.getName(), popsList);
                        if (districtsList != null && districtsList.size() > 0)
                            pop_intent.putParcelableArrayListExtra("districts", districtsList);
                        pop_intent.putExtra(Location.class.getName(), location);
                        pop_intent.putExtra("imei", imei);
                        startActivity(pop_intent);
                        break;
                }
            }
        });
    }

    private void getDistrictList() {
        progress_bar.setVisibility(View.VISIBLE);
        userapiService.getDistricts().enqueue(new Callback<ArrayList<Pop>>() {
            @Override
            public void onResponse(Response<ArrayList<Pop>> response, Retrofit retrofit) {
                progress_bar.setVisibility(View.GONE);
                if (response.isSuccess()) {
                    districtsList = response.body();
                    DataUtils.saveDistricts(MainActivity.this, districtsList);
                } else {
                    Log.d("Form Error", "onResponse: " + response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(Throwable t) {
                progress_bar.setVisibility(View.GONE);
                Log.d("Form Failure", "onFailure: " + t.getMessage());
            }
        });
    }

    private void getPopsList() {
        progress_bar.setVisibility(View.VISIBLE);
        userapiService.getSurveyDistricts().enqueue(new Callback<ArrayList<Pop>>() {
            @Override
            public void onResponse(Response<ArrayList<Pop>> response, Retrofit retrofit) {
                progress_bar.setVisibility(View.GONE);
                if (response.isSuccess()) {
                    popsList = response.body();
                    DataUtils.savePops(MainActivity.this, popsList);
                } else {
                    Log.d("Form Error", "onResponse: " + response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(Throwable t) {
                progress_bar.setVisibility(View.GONE);
                Log.d("Form Failure", "onFailure: " + t.getMessage());
            }
        });
    }

    private void addSurveyFeatureList() {
        ArrayList<Feature> pole_featureList = new ArrayList<>();
        ArrayList<Feature> pop_featureList = new ArrayList<>();
        userapiService.getSurveyFeatures("survey").enqueue(new Callback<ArrayList<Feature>>() {
            @Override
            public void onResponse(Response<ArrayList<Feature>> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    featureList = response.body();

                    for (Feature feature : featureList) {
                        if (feature != null) {
                            if (feature.hasProperty("pop_type")) {
                                pop_featureList.add(feature);
                            } else {
                                pole_featureList.add(feature);
                            }
                        }
                    }
                    if (pole_featureList.size() > 0) {
                        if (map.getSource(POLE_GEOJSON_SOURCE) != null) {
                            map.removeLayer("poles");
                            map.removeSource(POLE_GEOJSON_SOURCE);
                        }
                        FeatureCollection poleFeatureCollection = FeatureCollection.fromFeatures(pole_featureList);
                        GeoJsonSource polesGeoJsonSource = new GeoJsonSource(POLE_GEOJSON_SOURCE, poleFeatureCollection);
                        map.addSource(polesGeoJsonSource);
                    }

                    if (pop_featureList.size() > 0) {
                        if (map.getSource(POP_GEOJSON_SOURCE) != null) {
                            map.removeLayer("pops");
                            map.removeSource(POP_GEOJSON_SOURCE);
                        }
                        FeatureCollection popFeatureCollection = FeatureCollection.fromFeatures(pop_featureList);
                        GeoJsonSource popGeoJsonSource = new GeoJsonSource(POP_GEOJSON_SOURCE, popFeatureCollection);
                        map.addSource(popGeoJsonSource);
                    }

                    Bitmap poleBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.mapbox_marker_icon_default);
                    Bitmap popBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.map_marker_dark);
                    SymbolLayer popmyLayer = new SymbolLayer("pops", POP_GEOJSON_SOURCE);
                    SymbolLayer polemyLayer = new SymbolLayer("poles", POLE_GEOJSON_SOURCE);

                    map.addImage("pole", poleBitmap);
                    map.addImage("pop", adjustImage(popBitmap));

                    popmyLayer.withProperties(PropertyFactory.iconImage("pop"), PropertyFactory.iconAllowOverlap(true));
                    polemyLayer.withProperties(PropertyFactory.iconImage("pole"), PropertyFactory.iconAllowOverlap(true));
                    map.addLayer(polemyLayer);
                    map.addLayer(popmyLayer);
                    drawRouteLine(featureList);
                } else {
                    Utils.getSnackbar(coordinatorLayout, "Unable to fetch data.");
                    Log.d("Error", "onResponse: " + "error");
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Utils.getSnackbar(coordinatorLayout, "Unable to fetch data.");
                Log.d("Failure", "onFailure: " + t.getMessage());
            }
        });
    }

    private void drawRouteLine(ArrayList<Feature> featureList) {
        ArrayList<Feature> routeFeatureList = featureList;
        LineString lineString;
        FeatureCollection featureCollection;
        routeCoordinates = new ArrayList<Point>();
        if (map.getSource(LINE_SOURCE) != null) {
            map.removeLayer("linelayer");
            map.removeSource(LINE_SOURCE);
        }
        for (Feature feature : routeFeatureList) {
            routeCoordinates.add(((Point) feature.geometry()));
        }
        if (routeCoordinates.size() > 0) {
            lineString = LineString.fromLngLats(routeCoordinates);
            featureCollection = FeatureCollection.fromFeatures(new Feature[]{Feature.fromGeometry(lineString)});
            GeoJsonSource lineGeoJson = new GeoJsonSource(LINE_SOURCE, featureCollection);
            map.addSource(lineGeoJson);
            LineLayer lineLayer = new LineLayer("linelayer", LINE_SOURCE);
            lineLayer.setProperties(
//                    PropertyFactory.lineDasharray(new Float[]{0.01f, 2f}),
                    PropertyFactory.lineCap(Property.LINE_CAP_ROUND),
                    PropertyFactory.lineJoin(Property.LINE_JOIN_ROUND),
                    PropertyFactory.lineWidth(2f),
                    PropertyFactory.lineColor(Color.parseColor("#e55e5e"))
            );
            map.addLayer(lineLayer);
        }
    }

    private Bitmap adjustImage(Bitmap image) {
        int dpi = image.getDensity();
        if (dpi == mDpi)
            return image;
        else {
            int width = (image.getWidth() * mDpi + dpi / 2) / dpi;
            int height = (image.getHeight() * mDpi + dpi / 2) / dpi;
            Bitmap adjustedImage = Bitmap.createScaledBitmap(image, width, height, true);
            adjustedImage.setDensity(mDpi);
            return adjustedImage;
        }
    }

    @Override
    @SuppressWarnings({"MissingPermission"})
    public void onConnected() {
        locationEngine.requestLocationUpdates();
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            this.location = location;
            setCameraPosition(location);
            locationEngine.removeLocationEngineListener(this);
        }
    }

    private void setCameraPosition(Location location) {
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(),
                location.getLongitude()), 12));
//        addMarker(new LatLng(location.getLatitude(), location.getLongitude()));
    }//

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {

    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            enableLocationPlugin();
        }
    }

    @SuppressWarnings({"MissingPermission"})
    private void enableLocationPlugin() {
        if (PermissionsManager.areLocationPermissionsGranted(this)) {

            initializeLocationEngine();

            locationPlugin = new LocationLayerPlugin(mapView, map, locationEngine);
            locationPlugin.setRenderMode(RenderMode.COMPASS);
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }

    @SuppressWarnings({"MissingPermission"})
    private void initializeLocationEngine() {
        LocationEngineProvider locationEngineProvider = new LocationEngineProvider(this);
        locationEngine = locationEngineProvider.obtainBestLocationEngineAvailable();
        locationEngine.setPriority(LocationEnginePriority.BALANCED_POWER_ACCURACY);
        locationEngine.activate();
        Location lastLocation = locationEngine.getLastLocation();
        if (lastLocation != null) {
            location = lastLocation;
            setCameraPosition(lastLocation);
        } else {
            /*Location targetLocation = new Location("");//provider name is unnecessary
            targetLocation.setLatitude(17.4275148);//your coords of course
            targetLocation.setLongitude(78.4105523);
            setCameraPosition(targetLocation);*/
            locationEngine.addLocationEngineListener(this);
        }
    }

    @Override
    public void onMapReady(MapboxMap mapboxMap) {
        this.map = mapboxMap;
        enableLocationPlugin();
        addSurveyFeatureList();

        mapboxMap.addOnMapClickListener(new MapboxMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng point) {
                PointF pointf = map.getProjection().toScreenLocation(point);
                RectF rectF = new RectF(pointf.x - 10, pointf.y - 10, pointf.x + 10, pointf.y + 10);
                Intent intent = new Intent(MainActivity.this, PopActivity.class);
                List<Feature> features = map.queryRenderedFeatures(rectF, "pops");
                if (!features.isEmpty()) {
                    selectedFeature = features.get(0);
                    if (features.get(0).hasProperty("ts")) {
                        ts = features.get(0).getStringProperty("ts");
                        intent.putExtra("ts", ts);
                        intent.putExtra(Feature.class.getName(), selectedFeature.toJson());
                        startActivity(intent);
                    }
                }
            }
        });
    }

    private void addMarker(LatLng latLng) {
        List<Marker> markers = map.getMarkers();
        for (Marker m : markers) {
            m.remove();
        }
        IconFactory iconFactory = IconFactory.getInstance(this);
        Icon icon = iconFactory.fromResource(R.drawable.map_marker_dark);

        map.addMarker(new MarkerOptions()
                .position(latLng)
                .icon(icon));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == 1) {

        } else {
            permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onStart() {
        super.onStart();
        if (locationEngine != null) {
            locationEngine.requestLocationUpdates();
        }
        if (locationPlugin != null) {
            locationPlugin.onStart();
        }
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (locationEngine != null) {
            locationEngine.removeLocationUpdates();
        }
        if (locationPlugin != null) {
            locationPlugin.onStop();
        }
        mapView.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        if (locationEngine != null) {
            locationEngine.deactivate();
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
        addSurveyFeatureList();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
}
