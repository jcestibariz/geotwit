package com.quest.geotwit;

import android.*;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

/**
 * Created by stefan on 11/15/16.
 */

public class LocationTracker implements LocationListener {
    private static final Location TORONTO;
    static {
        TORONTO = new Location("Hard Coded Toronto");
        TORONTO.setLatitude(43.6532);
        TORONTO.setLongitude(-79.3832);
    }
    private volatile Location loc;

    LocationTracker(Context ctx) {
        LocationManager mgr = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(ctx,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                &&
            ActivityCompat.checkSelfPermission(ctx,
                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            loc = TORONTO;
            return;
        }
        mgr.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        loc = mgr.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (loc == null) {
            loc = TORONTO;
        }
    }
    @Override
    public void onLocationChanged(Location location) {
        loc = location;
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    public Location getLocation() {return loc;}
}
