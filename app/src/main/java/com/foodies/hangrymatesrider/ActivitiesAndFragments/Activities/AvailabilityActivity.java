package com.foodies.hangrymatesrider.ActivitiesAndFragments.Activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import com.aminography.primecalendar.PrimeCalendar;
import com.aminography.primecalendar.civil.CivilCalendar;
import com.aminography.primedatepicker.picker.PrimeDatePicker;
import com.aminography.primedatepicker.picker.callback.MultipleDaysPickCallback;
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
import java.util.List;

public class AvailabilityActivity extends AppCompatActivity {


    TextView txt_cancel;
    List<PrimeCalendar> selected_multipleDays;
    EditText et_select_start_time,et_select_end_time;
    private int mYear, mMonth, mDay, mHour, mMinute;
    Button btn_select_date;
    TextView txt_save;
    CamomileSpinner changePassProgress;
    RelativeLayout progressDialog;
    SharedPreferences sharedPreferences;
    String user_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_availability);
        txt_cancel = findViewById(R.id.txt_cancel);
        et_select_start_time = findViewById(R.id.et_select_start_time);
        et_select_end_time = findViewById(R.id.et_select_end_time);
        btn_select_date = findViewById(R.id.btn_select_date);
        txt_save = findViewById(R.id.txt_save);
        changePassProgress = findViewById(R.id.changePassProgress);
        changePassProgress.start();
        progressDialog = findViewById(R.id.progressDialog);
        selected_multipleDays = new ArrayList<>();
        sharedPreferences = getApplicationContext().getSharedPreferences(PreferenceClass.user, Context.MODE_PRIVATE);
        user_id = sharedPreferences.getString(PreferenceClass.pre_user_id,"");
        txt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        txt_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (TextUtils.isEmpty(et_select_start_time.getText())){
                    Toast.makeText(AvailabilityActivity.this, "Please choose Start Time", Toast.LENGTH_SHORT).show();
                }else  if (TextUtils.isEmpty(et_select_end_time.getText())){
                    Toast.makeText(AvailabilityActivity.this, "Please choose End Time", Toast.LENGTH_SHORT).show();
                }else if (selected_multipleDays.isEmpty()){
                    Toast.makeText(AvailabilityActivity.this, "Please select Dates", Toast.LENGTH_SHORT).show();
                }else {

                    SendDates();
                }

            }
        });

        et_select_start_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Calendar c = Calendar.getInstance();
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);

                // Launch Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(AvailabilityActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {

                                et_select_start_time.setText(hourOfDay + ":" + minute);
                            }
                        }, mHour, mMinute, false);
                timePickerDialog.show();

            }
        });


        et_select_end_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Calendar c = Calendar.getInstance();
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);

                // Launch Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(AvailabilityActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {

                                et_select_end_time.setText(hourOfDay + ":" + minute);
                            }
                        }, mHour, mMinute, false);
                timePickerDialog.show();

            }
        });


        btn_select_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PrimeCalendar primeCalendar = new CivilCalendar();
                PrimeDatePicker datePicker = PrimeDatePicker.Companion.dialogWith(primeCalendar)
                        // or bottomSheetWith(today)
                        .pickMultipleDays(new MultipleDaysPickCallback() {
                            @RequiresApi(api = Build.VERSION_CODES.M)
                            @Override
                            public void onMultipleDaysPicked(List<PrimeCalendar> multipleDays) {
                                selected_multipleDays.clear();
                                selected_multipleDays = multipleDays;
                                LinearLayout layout = (LinearLayout) findViewById(R.id.linearLayout1);
                                LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                layout.removeAllViews();
                                for (int i = 0; i < selected_multipleDays.size(); i++)
                                {
                                    String month = String.valueOf(selected_multipleDays.get(i).getMonth()+1);
                                    TextView tv = new TextView(getApplicationContext());
                                    tv.setTextColor(getApplicationContext().getColor(R.color.black));
                                    tv.setLayoutParams(lparams);
                                    tv.setText(selected_multipleDays.get(i).getDate()+"/"+month+"/"+selected_multipleDays.get(i).getYear());
                                    layout.addView(tv);
                                }

                            }
                        })  // Passing callback is optional, can be set later using setDayPickCallback()
                        // Optional
                        .minPossibleDate(primeCalendar)
                        .firstDayOfWeek(Calendar.MONDAY)       // Optional// Optional
                        .build();

                datePicker.show(getSupportFragmentManager(), "SOME_TAG");
            }
        });

    }

    private void SendDates() {

        //String date = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
        Log.d("json_Object", String.valueOf(selected_multipleDays.size()));
        for (int i = 0; i <selected_multipleDays.size() ; i++) {

            progressDialog.setVisibility(View.VISIBLE);
            JSONObject jsonObject = new JSONObject();

            String month = String.valueOf(selected_multipleDays.get(i).getMonth()+1);


            int count = i+1;
            Log.d("list_size", String.valueOf(count));

            Log.d("sending_dates",et_select_start_time.getText().toString()+" "+et_select_end_time.getText().toString()+" Dates  "+selected_multipleDays.get(i).getYear()+"-"+month+"-"+selected_multipleDays.get(i).getDate());

            try {
                jsonObject.put("user_id", user_id);
                jsonObject.put("starting_time",et_select_start_time.getText().toString());
                jsonObject.put("ending_time", et_select_end_time.getText().toString());
                jsonObject.put("date",  selected_multipleDays.get(i).getYear()+"-"+month+"-"+selected_multipleDays.get(i).getDate());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.d("json_Object", String.valueOf(jsonObject));
            ApiRequest.Call_Api(this, Config.ADD_SHIFT_DATE_TIME, jsonObject, new Callback() {
                @Override
                public void Responce(String resp) {

                    try {
                        progressDialog.setVisibility(View.GONE);
                        JSONObject jsonObject1 = new JSONObject(resp);

                        int code = Integer.parseInt(jsonObject1.optString("code"));

                        if (code == 200) {

                            JSONObject json = new JSONObject(jsonObject1.toString());
                            JSONArray jsonarray = json.getJSONArray("msg");
                            Log.d("Add_Date_Time", String.valueOf(jsonarray));

                            if (count == selected_multipleDays.size()){
                                Intent intent = new Intent(AvailabilityActivity.this,MyShiftActivity.class);
                                startActivity(intent);
                                finish();
                            }else {
                                Log.d("list_size", ""+count);
                            }

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
}