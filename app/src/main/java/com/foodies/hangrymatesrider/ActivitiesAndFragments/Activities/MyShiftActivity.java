package com.foodies.hangrymatesrider.ActivitiesAndFragments.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.foodies.hangrymatesrider.Adapters.MyShiftAdapter;
import com.foodies.hangrymatesrider.Constants.ApiRequest;
import com.foodies.hangrymatesrider.Constants.Callback;
import com.foodies.hangrymatesrider.Constants.Config;
import com.foodies.hangrymatesrider.Constants.PreferenceClass;
import com.foodies.hangrymatesrider.Models.MyShiftModel;
import com.foodies.hangrymatesrider.R;
import com.gmail.samehadar.iosdialog.CamomileSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class MyShiftActivity extends AppCompatActivity {

    ImageView back_icon,add;
    TextView txt_open_shift;
    String user_id;
    SharedPreferences sharedPreferences;
    CamomileSpinner changePassProgress;
    RelativeLayout progressDialog;
    ArrayList<MyShiftModel> myShiftModels_list;
    RecyclerView recyclerview_my_shift;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shift);
        back_icon = findViewById(R.id.back_icon);
        add = findViewById(R.id.add);
        recyclerview_my_shift = findViewById(R.id.recyclerview_my_shift);
        txt_open_shift = findViewById(R.id.txt_open_shift);
        myShiftModels_list = new ArrayList<>();
        sharedPreferences = getApplicationContext().getSharedPreferences(PreferenceClass.user, Context.MODE_PRIVATE);
        user_id = sharedPreferences.getString(PreferenceClass.pre_user_id,"");
        changePassProgress = findViewById(R.id.changePassProgress);
        changePassProgress.start();
        progressDialog = findViewById(R.id.progressDialog);
        back_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyShiftActivity.this,AvailabilityActivity.class);
                startActivity(intent);
            }
        });

        txt_open_shift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MyShiftActivity.this,OpenShiftActivity.class);
                startActivity(intent);
                finish();

            }
        });

        getMyShift();
    }

    private void getMyShift() {
        progressDialog.setVisibility(View.VISIBLE);
        JSONObject jsonObject = new JSONObject();
        String date = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());

        try {
            jsonObject.put("user_id",user_id);
            jsonObject.put("date",date);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        ApiRequest.Call_Api(this, Config.SHIFT_DETAILS, jsonObject, new Callback() {
            @Override
            public void Responce(String resp) {
                try {
                    progressDialog.setVisibility(View.GONE);
                    JSONObject jsonResponse = new JSONObject(resp);
                    int code_id = Integer.parseInt(jsonResponse.optString("code"));

                    if (code_id == 200) {

                        JSONObject json = new JSONObject(jsonResponse.toString());
                        JSONObject jsonJSONObject = json.getJSONObject("msg");
                        JSONArray jsonArray = jsonJSONObject.getJSONArray("RiderTiming");

                        for (int j = 0; j <jsonArray.length() ; j++) {

                            Log.d("my_shift", String.valueOf(jsonJSONObject));
                            JSONObject mJsonObject = jsonArray.getJSONObject(j);
                            JSONArray mJsonArrayProperty = mJsonObject.getJSONArray("timing");
                            Log.d("my_shift_array", String.valueOf(mJsonArrayProperty));


                            for (int i = 0; i < mJsonArrayProperty.length(); i++) {
                                JSONObject mJsonObjectProperty = mJsonArrayProperty.getJSONObject(i);
                                MyShiftModel myShiftModel = new MyShiftModel();
                                myShiftModel.setId(mJsonObjectProperty.getString("id"));
                                myShiftModel.setDate(mJsonObjectProperty.getString("date"));
                                myShiftModel.setStarting_time(mJsonObjectProperty.getString("starting_time"));
                                myShiftModel.setEnding_time(mJsonObjectProperty.getString("ending_time"));
                                myShiftModel.setConfirm(mJsonObjectProperty.getString("confirm"));
                                myShiftModel.setAdmin_confirm(mJsonObjectProperty.getString("admin_confirm"));
                                myShiftModels_list.add(myShiftModel);

                            }

                        }

                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MyShiftActivity.this);
                        recyclerview_my_shift.setLayoutManager(linearLayoutManager);
                        MyShiftAdapter myShiftAdapter = new MyShiftAdapter(myShiftModels_list,MyShiftActivity.this, "my_shift",progressDialog);
                        recyclerview_my_shift.setAdapter(myShiftAdapter);

                    }else {
                        progressDialog.setVisibility(View.GONE);
                }



                }catch (JSONException e){
                    progressDialog.setVisibility(View.GONE);
                    e.getCause();
                }

            }
        });

    }
}