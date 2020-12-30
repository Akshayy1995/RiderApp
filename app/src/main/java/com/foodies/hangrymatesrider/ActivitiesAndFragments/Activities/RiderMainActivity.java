package com.foodies.hangrymatesrider.ActivitiesAndFragments.Activities;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.app.AppCompatActivity;

import android.util.DisplayMetrics;
import android.view.Display;
import android.widget.Toast;

import com.foodies.hangrymatesrider.Constants.Config;
import com.foodies.hangrymatesrider.Constants.PreferenceClass;
import com.foodies.hangrymatesrider.Services.UpdateLocation;
import com.foodies.hangrymatesrider.Utils.ContextWrapper;
import com.foodies.hangrymatesrider.Utils.NotificationUtils;
import com.foodies.hangrymatesrider.ActivitiesAndFragments.Fragments.RPagerMainFragment;
import com.foodies.hangrymatesrider.R;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Created by Dinosoftlabs on 10/18/2019.
 */

public class RiderMainActivity extends AppCompatActivity {

    private RPagerMainFragment mCurrentFrag;
    public static Context context;
    private long mBackPressed;
    SharedPreferences sPre;
    public static boolean RMAINACTIVITY;
    public static boolean CHAT_FLAG;
    private BroadcastReceiver mRegistrationBroadcastReceiver;


    public static int width;
    public static int height;

    SharedPreferences sharedPreferences;
    @Override
    protected void attachBaseContext(Context newBase) {

        String[] language_array = newBase.getResources().getStringArray(R.array.language_code);
        List<String> language_code = Arrays.asList(language_array);
        sharedPreferences = newBase.getSharedPreferences(PreferenceClass.user, MODE_PRIVATE);
        String language = sharedPreferences.getString(PreferenceClass.selected_language, "");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && language_code.contains(language)) {
            Locale newLocale = new Locale(language);
            Context context = ContextWrapper.wrap(newBase, newLocale);
            super.attachBaseContext(context);
        }
        else {
            super.attachBaseContext(newBase);
        }


    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            set_language_local();
        }


        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;
        height = size.y;


        sPre = getSharedPreferences(PreferenceClass.user,MODE_PRIVATE);

        RMAINACTIVITY = true;

        mCurrentFrag = new RPagerMainFragment();
        if(mCurrentFrag!=null) {

            getSupportFragmentManager().beginTransaction().add(R.id.main_container, mCurrentFrag).commit();
        }

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {


                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                     FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);


                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {

                    String message = intent.getStringExtra("message");

                    sendMyNotification(message);

                    sendBroadcast(new Intent("newOrder"));

                }
            }
        };

        Intent intent = getIntent();

        if (intent.hasExtra("first_time")){

            Intent intent1 = new Intent(this,ROnlineStatusActivity.class);
            startActivity(intent1);
        }


    }

    @Override
    protected void onResume() {
        super.onResume();

        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

         LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));

         NotificationUtils.clearNotifications(getApplicationContext());

    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }



    @Override
    public void onDestroy() {
        super.onDestroy();

        Intent intent = new Intent(RiderMainActivity.this, UpdateLocation.class);
        stopService(intent);

    }



    public void set_language_local(){
        String [] language_array=getResources().getStringArray(R.array.language_code);
        List <String> language_code= Arrays.asList(language_array);

        String language=sharedPreferences.getString(PreferenceClass.selected_language,"");


        if(language_code.contains(language)) {
            Locale myLocale = new Locale(language);
            Resources res = getResources();
            DisplayMetrics dm = res.getDisplayMetrics();
            Configuration conf = new Configuration();
            conf.locale = myLocale;
            res.updateConfiguration(conf, dm);
            onConfigurationChanged(conf);

        }



    }


    @Override
    public void onBackPressed() {

        if (!mCurrentFrag.onBackPressed()) {
            int count = this.getSupportFragmentManager().getBackStackEntryCount();
            if (count == 0) {
                if (mBackPressed + 2000 > System.currentTimeMillis()) {
                    super.onBackPressed();
                    return;
                } else {
                    Toast.makeText(getBaseContext(), "Tap Again To Exit", Toast.LENGTH_SHORT).show();

                    mBackPressed = System.currentTimeMillis();

                }
            } else {
                super.onBackPressed();
            }
        }

    }


    private void sendMyNotification(String message) {
         Intent intent = new Intent(this, RiderMainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel = new NotificationChannel("channel-01",
                    "Foodomia",
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,"channel-01");
        builder.setSmallIcon(R.drawable.app_icon)
                .setColor(ContextCompat.getColor(context, R.color.colorRed))
                .setContentTitle("Foodomia")
                .setContentIntent(pendingIntent)
                .setContentText(String.format(message))
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setChannelId("channel-01");


        notificationManager.notify(0, builder.build());
    }

}



