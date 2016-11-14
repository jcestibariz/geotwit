package com.quest.geotwit;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends FragmentActivity
        implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener {

    private static final float HUE = 202f;

    private TextView tweetView;
    private boolean tweetVisible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tweetView = (TextView) findViewById(R.id.tweet);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap map) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            map.setMyLocationEnabled(true);
        }

        // Add a marker in T.O. and move the camera
        LatLng to = new LatLng(43.648, -79.369);
        map.addMarker(new MarkerOptions().position(to)
                .icon(BitmapDescriptorFactory.defaultMarker(HUE))) // "Twitter" hue
                .setTag("This is a tweet!");
        map.addMarker(new MarkerOptions().position(new LatLng(43.649, -79.368))
                .icon(BitmapDescriptorFactory.defaultMarker(HUE))) // "Twitter" hue
                .setTag("This is another tweet!");
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(to, 14f));

        map.setOnMarkerClickListener(this);
        map.setOnMapClickListener(this);

        UiSettings uiSettings = map.getUiSettings();
        uiSettings.setMapToolbarEnabled(false);
        uiSettings.setZoomControlsEnabled(true);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        tweetView.setText((String) marker.getTag());
        if (!tweetVisible) {
            tweetVisible = true;
            tweetView.setVisibility(View.VISIBLE);
        }
        return true;
    }

    @Override
    public void onMapClick(LatLng latLng) {
        if (tweetVisible) {
            tweetVisible = false;
            tweetView.setVisibility(View.GONE);
        }
    }
}
