package com.leechangu.sweettask;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.leechangu.sweettask.settask.TaskPreferenceActivity;

import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;

/**
 * This demo shows how GMS Location can be used to check for changes to the users location.  The
 * "My Location" button uses GMS Location to set the blue dot representing the users location. To
 * track changes to the users location on the map, we request updates from the
 * {@link com.google.android.gms.location.FusedLocationProviderApi}.
 */
public class MyLocationActivity extends BaseActionBarActivity
        implements
        ConnectionCallbacks,
        OnConnectionFailedListener,
        LocationListener,
        OnMyLocationButtonClickListener,
        OnMapReadyCallback {

    private GoogleApiClient mGoogleApiClient;
    private MapCircle mapCircle;
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private static final LatLng HOME = new LatLng(43.474521,-80.537389);
    private int DEFAULT_COLOR = Color.argb(30, Color.red(Color.BLUE), Color.green(Color.BLUE),
            Color.blue(Color.BLUE));

    private TextView distanceTextView;
    private TextView goalTextView;
    private TextView finishTextView;
    private boolean finishMapTask = false;
    public final static String LOCATION_RESULT = "LOCATION_RESULT";

    // These settings are the same as the settings for the map. They will in fact give you updates
    // at the maximal rates currently possible.
    private static final LocationRequest REQUEST = LocationRequest.create()
            .setInterval(5000)         // 5 seconds
            .setFastestInterval(16)    // 16ms = 60fps
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_location);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        // Customized part
        setUpMapIfNeeded();
        Bundle bundle = getIntent().getExtras();
        String mapInfo = bundle.getString("map_info");

        Circle c = mMap.addCircle(new CircleOptions()
                .center(HOME)
                .radius(0)
                .strokeWidth(0)
                .strokeColor(DEFAULT_COLOR)
                .fillColor(DEFAULT_COLOR));
        c = MapCircle.setLatLongRadius(c,mapInfo);
        mapCircle = new MapCircle(c);
        //mMap.location();
        CameraPosition position = new CameraPosition.Builder()
                .target(HOME)
                .zoom(13).build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(position));
        goalTextView = (TextView)findViewById(R.id.goalTextView);
        goalTextView.setText("" + mapCircle.getRadius());

        distanceTextView = (TextView) findViewById(R.id.distanceTextView);
        finishTextView = (TextView)findViewById(R.id.finishTextView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    public void onPause() {
        super.onPause();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        map.setMyLocationEnabled(true);
        map.setOnMyLocationButtonClickListener(this);
    }

    /**
     * Button to get current Location. This demonstrates how to get the current Location as required
     * without needing to register a LocationListener.
     */
    public void showMyLocation() {
        if (mGoogleApiClient.isConnected()) {
            Location nowLocation =LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            Location homeLocation = new Location("Test");
            homeLocation.setLatitude(HOME.latitude);
            homeLocation.setLongitude(HOME.longitude);
            homeLocation.setTime(new Date().getTime()); //Set time as current Date
            float distance = nowLocation.distanceTo(homeLocation);
            distanceTextView.setText("" + distance);
            if (distance>mapCircle.getRadius()) {
                finishTextView.setText("( finished )");
                finishMapTask = true;
            }
            else {
                finishTextView.setText("( not finished )");
                finishMapTask = false;
            }
        }
    }

    /**
     * Implementation of {@link LocationListener}.
     */
    @Override
    public void onLocationChanged(Location location) {
    }

    /**
     * Callback called when connected to GCore. Implementation of {@link ConnectionCallbacks}.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient,
                REQUEST,
                this);  // LocationListener
        showMyLocation();
    }

    /**
     * Callback called when disconnected from GCore. Implementation of {@link ConnectionCallbacks}.
     */
    @Override
    public void onConnectionSuspended(int cause) {
        // Do nothing
    }

    /**
     * Implementation of {@link OnConnectionFailedListener}.
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Do nothing
    }

    @Override
    public boolean onMyLocationButtonClick() {
        showMyLocation();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

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
    private void setUpMap() {
        mMap.addMarker(new MarkerOptions().position(HOME).title("Home"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean result = super.onCreateOptionsMenu(menu);
        menu.findItem(R.id.menu_item_new).setVisible(false);
        menu.findItem(R.id.menu_item_delete).setVisible(false);
        menu.findItem(R.id.menu_item_save).setVisible(true);
        return result;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.menu_item_save:
                Intent intent = new Intent();
                intent.putExtra(LOCATION_RESULT,finishMapTask );
                setResult(MainActivity.REQUESTCODE_LOCATION, intent);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


}
