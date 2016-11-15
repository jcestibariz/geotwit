package com.quest.geotwit;

import android.Manifest;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.models.Search;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.services.SearchService;
import com.twitter.sdk.android.core.services.params.Geocode;

import java.io.IOException;
import java.util.Locale;

import io.fabric.sdk.android.Fabric;
import retrofit2.Call;
import retrofit2.Response;

public class MainActivity extends FragmentActivity
        implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener,
        LoginDialogFragment.LoginListener {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "jjbUmqJiQRBIXBkxi77NUeUuo";
    private static final String TWITTER_SECRET = "5Hg33mVYouVXvMnnAiBikmw7UcFMt5ZculjtF4pcxT43uQRN0e";

    private static final String TAG = "MainActivity";
    private static final String LOGIN_FRAGMENT = "Login";

    private static final float HUE = 202f;

    private static final String[] PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION};

    private GoogleMap googleMap;
    private TextView tweetView;
    private boolean tweetVisible;

    private LocationTracker locationTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        setContentView(R.layout.activity_main);

        tweetView = (TextView) findViewById(R.id.tweet);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //FIXME: I'm clearing these so you're forced to reauth - until we
        // can clear 'em through a settings dialog, or get annoyed.
        Twitter.getSessionManager().clearActiveSession();
        TwitterSession session = Twitter.getSessionManager().getActiveSession();
        if (session == null) {
            new LoginDialogFragment()
                .show(getFragmentManager(), LOGIN_FRAGMENT);
        }

        locationTracker = new LocationTracker(this);
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
        googleMap = map;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            map.setMyLocationEnabled(true);
        }
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(PERMISSIONS, 42);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Fragment fragment = getFragmentManager().findFragmentByTag(LOGIN_FRAGMENT);
        if (fragment != null) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for (int i = 0; i < permissions.length; ++i) {
            if (Manifest.permission.ACCESS_FINE_LOCATION.equals(permissions[i]) &&
                    grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                try {
                    googleMap.setMyLocationEnabled(true);
                }
                catch (SecurityException e) {
                    throw new IllegalStateException("Unexpected exception", e);
                }
            }
        }
    }

    @Override
    public void onLogin(TwitterSession session) {
        Log.d("Main", "Building up a query...");
        Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT)
                .show();

        searchTweets();
    }

    private void searchTweets() {
        final String queryStr = "cat";

        Location loc = locationTracker.getLocation();
        final int radius = 5;
        final Geocode geocode = null;//for now I don't care about location - use next line if you do
        //new Geocode(loc.getLatitude(), loc.getLongitude(), radius, Geocode.Distance.KILOMETERS);

        final String lang = Locale.getDefault().getDisplayLanguage();//optional - restricts tweets to given lang
        final String locale = null;//optional - like lang, but only 'ja' is effective.
        final String result_type = "mixed";//one of mixed|recent|popular.
        final int count = 15;//optional. Max tweets - up to 100.
        final String until = null;//for tweets before given date (e.g.: "2016-07-19" - at least they used sane date ordering)
        final Long since_id = null;//tweets have unique ID; returns results with ID greater than this.  Useful if looking for updates (cache the last value).
        final Long max_id = null;//results before this ID.
        final Boolean include_entities = null;//whether an entitities node is included in response.  Not sure what this is yet.

        //notice that we're pulling session from static - you can do this from any activity.
        TwitterSession session = Twitter.getSessionManager().getActiveSession();
        TwitterApiClient client = new TwitterApiClient(session);

        SearchService svc = client.getSearchService();

        Call<Search> searchCall = svc.tweets(//this invocation takes forever on emulator... not sure why.
                queryStr, geocode, lang, locale, result_type, count, until, since_id, max_id, include_entities);

        Toast toast = Toast.makeText(this, "Querying: " + queryStr, Toast.LENGTH_SHORT);
        toast.show();
        AsyncTwitterCall task = new AsyncTwitterCall();
        task.execute(searchCall);
    }

    private class AsyncTwitterCall extends AsyncTask<Call<Search>, Integer, Response<Search>> {

        @Override
        protected Response<Search> doInBackground(Call<Search>... calls) {
            if (calls.length != 1) {
                throw new IllegalArgumentException("Expecting one twitter search.");
            }
            try {
                return calls[0].execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Response<Search> response) {
            Log.d("Main", "Query results: " + response.code());
            StringBuilder results = new StringBuilder();
            for (Tweet t: response.body().tweets) {
                results.append(t.id + ": " + t.user + ": " + t.text + "\n");
            }
            //FIXME: we want to do something with the results...
            Toast toast = Toast.makeText(MainActivity.this, results.toString(), Toast.LENGTH_LONG);
            toast.show();
        }
    }
}
