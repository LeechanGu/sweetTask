package com.leechangu.sweettask;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.leechangu.sweettask.settask.TaskPreferenceActivity;

public class MapsActivity extends BaseActionBarActivity implements SeekBar.OnSeekBarChangeListener, GoogleMap.OnMapClickListener, OnMapReadyCallback {

    public final static String MAP_RESULT = "MAP_RESULT";
    private static final LatLng HOME = new LatLng(43.474521,-80.537389);
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private SeekBar radiusSeekBar;
    private MapCircle mapCircle;
    private int DEFAULT_RADIUS = 200;
    private int RADIUS_MAX = 2000;
    private int DEFAULT_COLOR = Color.argb(30, Color.red(Color.BLUE), Color.green(Color.BLUE),
            Color.blue(Color.BLUE));
    private float DEFAULT_ZOOM_FACTOR = 13.0f;
    private ImageButton homeButton;
    private TextView distanceTextView;
    private UiSettings mUiSettings;
    private Marker homeMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mMap.setMyLocationEnabled(true);
        mUiSettings = mMap.getUiSettings();
        mUiSettings.setZoomControlsEnabled(true);
        mUiSettings.setCompassEnabled(false);
        mUiSettings.setMyLocationButtonEnabled(false);
        mMap.setMyLocationEnabled(false);
        mUiSettings.setScrollGesturesEnabled(true);
        mUiSettings.setZoomGesturesEnabled(true);
        mUiSettings.setTiltGesturesEnabled(false);
        mUiSettings.setRotateGesturesEnabled(false);

        distanceTextView = (TextView)findViewById(R.id.distanceTextView);
        homeButton = (ImageButton)findViewById(R.id.homeButton);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                zoomBackHome();
            }
        });

        radiusSeekBar = (SeekBar)findViewById(R.id.radiusSeekBar);
        radiusSeekBar.setMax(RADIUS_MAX);
        radiusSeekBar.setProgress(DEFAULT_RADIUS);
        radiusSeekBar.setOnSeekBarChangeListener(this);
        // refresh HOME address
        Circle c = mMap.addCircle(new CircleOptions()
                .center(HOME)
                .radius(radiusSeekBar.getProgress())
                .strokeWidth(0)
                .strokeColor(DEFAULT_COLOR)
                .fillColor(DEFAULT_COLOR));
        Bundle bundle = getIntent().getExtras();
        String mapInfo = bundle.getString("map_info");
        if (!mapInfo.equals(""))
        {
            c = MapCircle.setLatLongRadius(c,mapInfo);
            homeMarker = addMarker(mMap, MapCircle.getLatLngFromString(mapInfo));
        } else {
            homeMarker = addMarker(mMap, HOME);
        }
        mapCircle = new MapCircle(c);
        distanceTextView.setText(radiusSeekBar.getProgress() + " m");

        zoomBackHome();
    }

    private void zoomBackHome()
    {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(HOME, DEFAULT_ZOOM_FACTOR));
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (seekBar == radiusSeekBar) {
            mapCircle.setRadius(progress);
            distanceTextView.setText(progress + " m");
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // Don't do anything here.
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // Don't do anything here.
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private Marker addMarker(GoogleMap map, LatLng center) {
        return map.addMarker(new MarkerOptions().position(center).title("Home"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean result = super.onCreateOptionsMenu(menu);
        menu.findItem(R.id.menu_item_new).setVisible(false);
        menu.findItem(R.id.menu_item_delete).setVisible(true);
        menu.findItem(R.id.menu_item_save).setVisible(true);
        return result;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = null;
        switch (item.getItemId()) {
            case R.id.menu_item_save:
                intent = new Intent();
                intent.putExtra(MAP_RESULT, mapCircle.toString());
                setResult(TaskPreferenceActivity.REQUESTCODE_MAP, intent);
                finish();
                break;
            case R.id.menu_item_delete:
                intent = new Intent();
                intent.putExtra(MAP_RESULT, MapCircle.NO_MAP);
                setResult(TaskPreferenceActivity.REQUESTCODE_MAP, intent);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        map.setOnMapClickListener(this);
    }

    @Override
    public void onMapClick(LatLng point) {
        mapCircle.setCenter(point);
        homeMarker.setPosition(point);
    }
}
