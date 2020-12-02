package com.foodies.hangrymatesrider.ActivitiesAndFragments.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.foodies.hangrymatesrider.ActivitiesAndFragments.Activities.MyShiftActivity;
import com.foodies.hangrymatesrider.ActivitiesAndFragments.Activities.OpenShiftActivity;
import com.foodies.hangrymatesrider.ActivitiesAndFragments.Activities.SplashScreenActivity;
import com.foodies.hangrymatesrider.ActivitiesAndFragments.Activities.WeeklyEarningsActivity;
import com.foodies.hangrymatesrider.Constants.AllConstants;
import com.foodies.hangrymatesrider.Constants.ApiRequest;
import com.foodies.hangrymatesrider.Constants.Callback;
import com.foodies.hangrymatesrider.Constants.Config;
import com.foodies.hangrymatesrider.Constants.Functions;
import com.foodies.hangrymatesrider.Constants.PreferenceClass;
import com.foodies.hangrymatesrider.Services.UpdateLocation;
import com.foodies.hangrymatesrider.Utils.FontHelper;
import com.foodies.hangrymatesrider.Utils.RelateToFragment_OnBack.RootFragment;
import com.foodies.hangrymatesrider.ActivitiesAndFragments.Activities.LoginAcitvity;
import com.gmail.samehadar.iosdialog.CamomileSpinner;
import com.foodies.hangrymatesrider.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Created by Dinosoftlabs on 10/18/2019.
 */

public class RProfileFragment extends RootFragment {

    RelativeLayout profile_div,log_out_div,today_job_div,change_password_div,review_div,language_div,rl_weekly_earnings,rl_sfits;
    SharedPreferences profile_pref;
    public static boolean FLAG_RIDER;
    String user_id;
    CamomileSpinner orderProgressBar;
    RelativeLayout transparent_layer,progressDialog;
    public static boolean RIDER_REVIEW;

    View v;
    Context context;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.rider_profile_fragment, container, false);
        context=getContext();

        profile_pref = getContext().getSharedPreferences(PreferenceClass.user,Context.MODE_PRIVATE);
        user_id = profile_pref.getString(PreferenceClass.pre_user_id, "");
        FrameLayout frameLayout = v.findViewById(R.id.profile_main_container);
        FontHelper.applyFont(getContext(),frameLayout, AllConstants.verdana);
        init(v);

        return v;
    }

    public void init(View v){

        progressDialog = v.findViewById(R.id.progressDialog);
        transparent_layer = v.findViewById(R.id.transparent_layer);
        orderProgressBar = v.findViewById(R.id.orderProgress);
        orderProgressBar.start();
        profile_div = v.findViewById(R.id.profile_div);
        review_div = v.findViewById(R.id.review_div);
        language_div=v.findViewById(R.id.language_div);
        log_out_div = v.findViewById(R.id.log_out_div);
        today_job_div = v.findViewById(R.id.today_job_div);
        change_password_div = v.findViewById(R.id.change_password_div);
        today_job_div = v.findViewById(R.id.today_job_div);
        rl_weekly_earnings = v.findViewById(R.id.rl_weekly_earnings);
        rl_sfits = v.findViewById(R.id.rl_sfits);


        rl_weekly_earnings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, WeeklyEarningsActivity.class);
                startActivity(intent);

            }
        });

        rl_sfits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, MyShiftActivity.class);
                startActivity(intent);

            }
        });

        log_out_div.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showUserOnlineStatus();

            }
        });

        profile_div.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Fragment restaurantMenuItemsFragment = new RiderAccountInfoFragment();
                FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                transaction.addToBackStack(null);
                transaction.add(R.id.profile_main_container, restaurantMenuItemsFragment,"parent").commit();

            }
        });




        change_password_div.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment restaurantMenuItemsFragment = new ChangePasswordFragment();
                FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                transaction.addToBackStack(null);
                transaction.add(R.id.profile_main_container, restaurantMenuItemsFragment,"ParentFragment").commit();
                FLAG_RIDER = true;
            }
        });



        today_job_div.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment restaurantMenuItemsFragment = new RTodayJobFragment();
                FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                transaction.addToBackStack(null);
                transaction.add(R.id.profile_main_container, restaurantMenuItemsFragment,"ParentFragment").commit();
            }
        });

        review_div .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RIDER_REVIEW = true;
                Fragment reviewListFragment = new ReviewListFragment();
                FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                transaction.addToBackStack(null);
                transaction.replace(R.id.profile_main_container, reviewListFragment,"ParentFragment_MenuItems").commit();
            }
        });


        TextView language_txt=v.findViewById(R.id.language_txt);

        List<String> language_names= Arrays.asList(getResources().getStringArray(R.array.language_names_for_show));
        List <String> language_code= Arrays.asList(getResources().getStringArray(R.array.language_code));

        String language= Locale.getDefault().getLanguage();
        if(profile_pref.getString(PreferenceClass.selected_language,null)!=null)
            language = profile_pref.getString(PreferenceClass.selected_language, language_code.get(0));

        if(language_code.contains(language)){
            language_txt.setText(language_names.get(language_code.indexOf(language)));
        }
        else {
            language_txt.setText("english");
        }


        language_div.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Open_Language_dialog();
            }
        });


    }



    public void Open_Language_dialog(){
        List<String> language_names=  Arrays.asList(getResources().getStringArray(R.array.language_names_for_show));
        List <String> language_code=  Arrays.asList(getResources().getStringArray(R.array.language_code));

        final CharSequence[] options = getResources().getStringArray(R.array.language_names_for_show);
        Functions.Show_Options(getContext(), options, new Callback() {
            @Override
            public void Responce(String resp) {

                profile_pref.edit().putString(PreferenceClass.selected_language,language_code.get(language_names.indexOf(resp))).commit();

                startActivity(new Intent(getActivity(), SplashScreenActivity.class));
                getActivity().finish();

            }
        });
    }


    private void logOutUser(){
        SharedPreferences.Editor editor = profile_pref.edit();
        editor.putString(PreferenceClass.USER_TYPE,"");
        editor.putString(PreferenceClass.pre_email, "");
        editor.putString(PreferenceClass.pre_pass, "");
        editor.putString(PreferenceClass.pre_first, "");
        editor.putString(PreferenceClass.pre_last, "");
        editor.putString(PreferenceClass.pre_contact, "");
        editor.putString(PreferenceClass.pre_user_id, "");

        editor.putBoolean(PreferenceClass.IS_LOGIN, false);
        editor.commit();

        getActivity().startActivity(new Intent(getContext(), LoginAcitvity.class));
       getActivity().finish();

        Intent intent = new Intent(getContext(), UpdateLocation.class);
        getContext().stopService(intent);

    }

    public void showUserOnlineStatus(){

        transparent_layer.setVisibility(View.VISIBLE);
        progressDialog.setVisibility(View.VISIBLE);


        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user_id", user_id);
            jsonObject.put("online", "0");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiRequest.Call_Api(context, Config.UPDATE_RIDER_STATUS, jsonObject, new Callback() {
            @Override
            public void Responce(String resp) {

                transparent_layer.setVisibility(View.GONE);
                progressDialog.setVisibility(View.GONE);


                try {
                    JSONObject jsonObject1 = new JSONObject(resp);

                    int code = Integer.parseInt(jsonObject1.optString("code"));

                    if (code==200){

                        JSONObject msg=jsonObject1.optJSONObject("msg");
                        JSONObject UserInfo=msg.optJSONObject("UserInfo");

                        String online=UserInfo.optString("online");

                        SharedPreferences.Editor editor = profile_pref.edit();

                        if(online.equals("1")){
                            editor.putString(PreferenceClass.RIDER_ONLINE_STATUS,"1");
                            editor.commit();
                        }else {
                            editor.putString(PreferenceClass.RIDER_ONLINE_STATUS,"0");
                            editor.commit();
                            logOutUser();
                        }



                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });



    }


}
