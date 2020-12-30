package com.foodies.hangrymatesrider.Services;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.foodies.hangrymatesrider.Constants.AllConstants;
import com.foodies.hangrymatesrider.Constants.Config;
import com.foodies.hangrymatesrider.Constants.PreferenceClass;
import com.foodies.hangrymatesrider.Models.TrackingModelClass;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by deepshikha on 24/11/16.
 */

public class UpdateLocationNew extends Service implements LocationListener {

    boolean isGPSEnable = false;
    boolean isNetworkEnable = false;
    double latitude, longitude;
    LocationManager locationManager;
    Location location;
    private Handler mHandler = new Handler();
    private Timer mTimer = null;
    long notify_interval = 40000;
    public static String str_receiver = "servicetutorial.service.receiver";
    Intent intent;
    DatabaseReference mDatabase;
    FirebaseDatabase firebaseDatabase;
    SharedPreferences pending_job_pref;
    String user_id;
    private static Double previousLat = 0.0;
    private static Double previousLong = 0.0;

    public UpdateLocationNew() {

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mTimer = new Timer();
        mTimer.schedule(new TimerTaskToGetLocation(), 5, notify_interval);
        intent = new Intent(str_receiver);
        pending_job_pref = getSharedPreferences(PreferenceClass.user, Context.MODE_PRIVATE);
        user_id = pending_job_pref.getString(PreferenceClass.pre_user_id, "");

        firebaseDatabase = FirebaseDatabase.getInstance();
        mDatabase = firebaseDatabase.getReference().child(AllConstants.TRACKING).child(user_id);
//        fn_getlocation();
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private void fn_getlocation() {
        locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        isGPSEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetworkEnable = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!isGPSEnable && !isNetworkEnable) {

        } else {

            if (isNetworkEnable) {
                location = null;
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, this);
                if (locationManager!=null){
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (location!=null){

                        Log.e("get_latitude",location.getLatitude()+"");
                        Log.e("get_longitude",location.getLongitude()+"");

                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                        fn_update(location);
                        displayLocation(location);
                        saveRiderLocation(location);
                    }
                }

            }


            if (isGPSEnable){
                location = null;
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000,0,this);
                if (locationManager!=null){
                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (location!=null){
                        Log.e("latitude",location.getLatitude()+"");
                        Log.e("longitude",location.getLongitude()+"");
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                        fn_update(location);
                        displayLocation(location);
                        saveRiderLocation(location);
                    }
                }
            }


        }

    }

    private class TimerTaskToGetLocation extends TimerTask {
        @Override
        public void run() {

            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    fn_getlocation();
                }
            });

        }
    }

    private void fn_update(Location location){

        intent.putExtra("latutide",location.getLatitude()+"");
        intent.putExtra("longitude",location.getLongitude()+"");
        sendBroadcast(intent);
    }

    public void saveRiderLocation(Location location){

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("user_id",user_id);
            jsonObject.put("lat",location.getLatitude());
            jsonObject.put("long",location.getLongitude());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d(AllConstants.tag,jsonObject.toString());
        Log.d(AllConstants.tag, Config.ADD_LOCATIONS);

        JsonObjectRequest updateLocationRequest = new JsonObjectRequest(Request.Method.POST, Config.ADD_LOCATIONS, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Log.d(AllConstants.tag,response.toString());
                deleteCache(UpdateLocationNew.this);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("api-key", "2a5588cf-4cf3-4f1c-9548-cc1db4b54ae3");
                return headers;
            }
        };

        queue.add(updateLocationRequest);

    }

    private void displayLocation(Location location) {

        if (location != null) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();

            mDatabase.keepSynced(true);
            mDatabase.setValue(new TrackingModelClass(""+latitude,""+longitude,""+previousLat,""+previousLong));

            previousLat = latitude;
            previousLong = longitude;

            Log.d("resp",previousLat+"---"+previousLong);

        } else {
            Toast.makeText(this, "Couldn't get the location. Make sure location is enabled on the device", Toast.LENGTH_SHORT).show();
        }
    }


    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) { e.printStackTrace();}
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if(dir!= null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }
}
