package com.foodies.hangrymatesrider.ActivitiesAndFragments.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.foodies.hangrymatesrider.Constants.ApiRequest;
import com.foodies.hangrymatesrider.Constants.Callback;
import com.foodies.hangrymatesrider.Constants.Config;
import com.foodies.hangrymatesrider.Constants.PreferenceClass;
import com.foodies.hangrymatesrider.R;
import com.gmail.samehadar.iosdialog.CamomileSpinner;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Calendar;

public class WeeklyEarningsActivity extends AppCompatActivity {

    ImageView back_icon;
    TextView txt_start_date,txt_end_date;
    int mYear,mMonth,mDay;
    int mYear_end,mMonth_end,mDay_end;
    SharedPreferences pending_job_pref;
    String user_id = "0";
    RelativeLayout progressDialog;
    CamomileSpinner logInProgress;
    Button search;
    TextView txt_total_earnings;
    CardView cardview_total_earning;

    ArrayList<Double>delivery_fee_price;
    ArrayList<Double>rider_price;
    Double delivery_sum =0.0;
    Double rider_sum =0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weekly_earnings);
        back_icon = findViewById(R.id.back_icon);
        txt_start_date = findViewById(R.id.txt_start_date);
        txt_end_date = findViewById(R.id.txt_end_date);
        progressDialog = findViewById(R.id.progressDialog);
        logInProgress = findViewById(R.id.logInProgress);
        search = findViewById(R.id.btn_search);
        logInProgress.start();
        txt_total_earnings = findViewById(R.id.txt_total_earnings);
        cardview_total_earning = findViewById(R.id.cardview_total_earning);

        pending_job_pref = getSharedPreferences(PreferenceClass.user, Context.MODE_PRIVATE);
        user_id  = pending_job_pref.getString(PreferenceClass.pre_user_id, "");

        delivery_fee_price = new ArrayList<Double>();
        rider_price = new ArrayList<Double>();
        back_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();

            }
        });

        Calendar mcurrentDate=Calendar.getInstance();
        mYear=mcurrentDate.get(Calendar.YEAR);
        mMonth=mcurrentDate.get(Calendar.MONTH);
        mDay=mcurrentDate.get(Calendar.DAY_OF_MONTH);


        mYear_end=mcurrentDate.get(Calendar.YEAR);
        mMonth_end=mcurrentDate.get(Calendar.MONTH);
        mDay_end=mcurrentDate.get(Calendar.DAY_OF_MONTH);

        txt_start_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DatePickerDialog mDatePicker=new DatePickerDialog(WeeklyEarningsActivity.this, new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {

                        mYear = selectedyear;
                        mMonth = selectedmonth;
                        mDay = selectedday;

                        String get_month = String.valueOf(mMonth+1);
                        txt_start_date.setText(selectedyear+"-"+get_month+"-"+selectedday);
                    }
                },mYear, mMonth, mDay);
                mDatePicker.setTitle("Select date");
                mDatePicker.show();
            }
        });


        txt_end_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DatePickerDialog mDatePicker=new DatePickerDialog(WeeklyEarningsActivity.this, new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {

                        mYear_end = selectedyear;
                        mMonth_end = selectedmonth;
                        mDay_end = selectedday;

                        String get_month = String.valueOf(mMonth_end+1);
                        txt_end_date.setText(selectedyear+"-"+get_month+"-"+selectedday);
                    }
                },mYear_end, mMonth_end, mDay_end);
                mDatePicker.setTitle("Select date");
                mDatePicker.show();
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (txt_start_date.getText().equals("Start Date")){

                    Toast.makeText(WeeklyEarningsActivity.this, "Please select start date", Toast.LENGTH_SHORT).show();
                }else  if (txt_end_date.getText().equals("End Date")){

                    Toast.makeText(WeeklyEarningsActivity.this, "Please select end date", Toast.LENGTH_SHORT).show();
                }else {

                    getEarnings();
                }

            }
        });

    }

    private void getEarnings() {

        progressDialog.setVisibility(View.VISIBLE);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user_id", user_id);
            jsonObject.put("starting_date", txt_start_date.getText().toString());
            jsonObject.put("ending_date", txt_end_date.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiRequest.Call_Api(this, Config.WEEKLY_EARNINGS, jsonObject, new Callback() {
            @Override
            public void Responce(String resp) {

                try {
                    progressDialog.setVisibility(View.GONE);
                    JSONObject jsonObject1 = new JSONObject(resp);

                    int code = Integer.parseInt(jsonObject1.optString("code"));

                    if (code == 200) {

                        JSONObject json = new JSONObject(jsonObject1.toString());
                        JSONArray jsonarray = json.getJSONArray("msg");

                        Log.d("order_arrey", "array"+String.valueOf(jsonarray));



                        for (int i = 0; i < jsonarray.length(); i++) {

                            JSONObject json1 = jsonarray.getJSONObject(i);
                            JSONObject order = json1.getJSONObject("Order");
                            Log.d("order_arrey", String.valueOf(order));

                            delivery_fee_price.add(Double.valueOf(order.getString("delivery_fee")));
                            rider_price.add(Double.valueOf(order.getString("rider_tip")));
                            Log.d("price_weekly", order.getString("delivery_fee")+" "+order.getString("rider_tip"));
                           // Toast.makeText(WeeklyEarningsActivity.this, ""+order.getString("delivery_fee"), Toast.LENGTH_SHORT).show();
                        }

                        for (int i = 0; i <delivery_fee_price.size() ; i++) {
                            Log.d("save_price", String.valueOf(delivery_fee_price.get(i)));
                            delivery_sum = delivery_sum+delivery_fee_price.get(i);

                        }

                        for (int i = 0; i <rider_price.size() ; i++) {
                            Log.d("save_price_rider", String.valueOf(rider_price.get(i)));
                            rider_sum= rider_sum+rider_price.get(i);
                        }

                        Double final_sum = delivery_sum+rider_sum;
                        Log.d("save_price_total",delivery_sum+" "+rider_sum+" "+ String.valueOf(final_sum));



                        try {
                            cardview_total_earning.setVisibility(View.VISIBLE);
                            txt_total_earnings.setText("$"+final_sum);
                        }catch (Exception e){
                            Log.d("exception",e.getLocalizedMessage());
                        }

                       // Log.d("json", String.valueOf(sum));

                    }

                } catch (JSONException e) {
                    progressDialog.setVisibility(View.GONE);
                    Log.d("exception",  String.valueOf(e.getMessage()));
                    e.printStackTrace();

                }

            }
        });


    }
}