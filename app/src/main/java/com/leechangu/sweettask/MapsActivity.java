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
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.leechangu.sweettask.settask.TaskPreferenceActivity;

public class MapsActivity extends BaseActionBarActivity implements SeekBar.OnSeekBarChangeListener {

    private static final LatLng HOME = new LatLng(43.474521,-80.537389);
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private SeekBar radiusSeekBar;
    private MapCircle mapCircle;
    private int DEFAULT_RADIUS = 2000;
    private int RADIUS_MAX = 20000;
    private int DEFAULT_COLOR = Color.argb(30, Color.red(Color.BLUE), Color.green(Color.BLUE),
            Color.blue(Color.BLUE));
    private float DEFAULT_ZOOM_FACTOR = 13.0f;
    private ImageButton homeButton;
    private TextView distanceTextView;
    private UiSettings mUiSettings;

    public final static String MAP_RESULT = "MAP_RESULT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();

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
        if (mapInfo!=null)
        {
            c = MapCircle.setLatLongRadius(c,mapInfo);
        }
        mapCircle = new MapCircle(c);
        distanceTextView.setText(radiusSeekBar.getProgress()+" m");

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

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        mMap.addMarker(new MarkerOptions().position(HOME).title("HOME"));
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
}
