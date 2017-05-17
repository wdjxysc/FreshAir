package com.rthc.freshair.util;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Administrator on 2016/7/11.
 */
public class UtilAndroid {

    public static class PositionAddress {
        private double latitude = 0.0;
        private double longitude = 0.0;

        public double getLongitude() {
            return longitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }

        public double getLatitude() {
            return latitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }
    }

    static LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Log.i("location", "onLocationChanged");
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.i("location", "onStatusChanged:" + provider +", status:" + status);
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.i("location", "onProviderEnabled:" + provider);
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.i("location", "onProviderDisabled:" + provider);
        }
    };

    public static PositionAddress getLocation(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            Toast.makeText(context, "获取定位权限失败，请前往应用权限设置允许定位！", Toast.LENGTH_SHORT).show();
            return null;
        }

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {


            PositionAddress positionAddress = new PositionAddress();

//            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 1, locationListener);

            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if (location != null) {
                positionAddress.latitude = location.getLatitude();
                positionAddress.longitude = location.getLongitude();
//                Toast.makeText(context, positionAddress.latitude + "   " + positionAddress.longitude, Toast.LENGTH_SHORT).show();
                Log.i(Const.TAG, "getLocation: " + positionAddress.latitude + "   " + positionAddress.longitude);
                return positionAddress;
            }


        }

        if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
            PositionAddress positionAddress = new PositionAddress();
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (location != null) {
                positionAddress.latitude = location.getLatitude();
                positionAddress.longitude = location.getLongitude();
                //Toast.makeText(context, positionAddress.latitude + "   " + positionAddress.longitude, Toast.LENGTH_SHORT).show();
                return positionAddress;
            }
        }

        if(locationManager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER)){
            PositionAddress positionAddress = new PositionAddress();
            Location location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
            if (location != null) {
                positionAddress.latitude = location.getLatitude();
                positionAddress.longitude = location.getLongitude();
                //Toast.makeText(context, positionAddress.latitude + "   " + positionAddress.longitude, Toast.LENGTH_SHORT).show();
                return positionAddress;
            }
        }

        return null;
    }
}
