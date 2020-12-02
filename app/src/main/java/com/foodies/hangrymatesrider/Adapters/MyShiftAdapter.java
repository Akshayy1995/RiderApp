package com.foodies.hangrymatesrider.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.foodies.hangrymatesrider.ActivitiesAndFragments.Activities.MyShiftActivity;
import com.foodies.hangrymatesrider.ActivitiesAndFragments.Activities.OpenShiftActivity;
import com.foodies.hangrymatesrider.Constants.ApiRequest;
import com.foodies.hangrymatesrider.Constants.Callback;
import com.foodies.hangrymatesrider.Constants.Config;
import com.foodies.hangrymatesrider.Constants.PreferenceClass;
import com.foodies.hangrymatesrider.Models.MyShiftModel;
import com.foodies.hangrymatesrider.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Dinosoftlabs on 10/18/2019.
 */

public class MyShiftAdapter extends RecyclerView.Adapter<MyShiftAdapter.ViewHolder> {

    ArrayList<MyShiftModel> getDataAdapter;
    Context context;
    String type;
    String user_id;
    SharedPreferences sharedPreferences;
    RelativeLayout progressDialog;

    public MyShiftAdapter(ArrayList<MyShiftModel> getDataAdapter, Context context, String type, RelativeLayout progressDialog){
        super();
        this.getDataAdapter = getDataAdapter;
        this.context = context;
        this.type =type;
        this.progressDialog = progressDialog;

    }

    @Override
    public MyShiftAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item_my_shift, parent, false);

        MyShiftAdapter.ViewHolder viewHolder = new MyShiftAdapter.ViewHolder(v);
        sharedPreferences = context.getSharedPreferences(PreferenceClass.user, Context.MODE_PRIVATE);
        user_id = sharedPreferences.getString(PreferenceClass.pre_user_id,"");

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        MyShiftModel jobListModel = getDataAdapter.get(position);

        holder.date.setText(jobListModel.getDate().toString());
        holder.time.setText(jobListModel.getStarting_time()+"-"+jobListModel.getEnding_time());

        if (type.equals("open_shift")){
            holder.review.setVisibility(View.GONE);
            holder.btn_add.setVisibility(View.VISIBLE);
        }else {
            holder.btn_add.setVisibility(View.GONE);
            if (jobListModel.getAdmin_confirm().equals("0")) {
                holder.review.setVisibility(View.VISIBLE);
                holder.btn_confirm.setVisibility(View.GONE);
            } else if (jobListModel.getAdmin_confirm().equals("1")&&jobListModel.getConfirm().equals("1")){
                holder.review.setVisibility(View.VISIBLE);
                holder.btn_confirm.setVisibility(View.GONE);
                holder.review.setText("Confirmed");
            } else {
                holder.review.setVisibility(View.GONE);
                holder.btn_confirm.setVisibility(View.VISIBLE);
            }
        }

        holder.btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressDialog.setVisibility(View.VISIBLE);
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("id",getDataAdapter.get(position).getId());
                    jsonObject.put("confirm","1");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

               // Toast.makeText(context, ""+getDataAdapter.get(position).getId(), Toast.LENGTH_SHORT).show();
                Log.d("update_status",getDataAdapter.get(position).getId());
                ApiRequest.Call_Api(context, Config.UPDATE_RIDER_SHIFT_STATUS, jsonObject, new Callback() {
                    @Override
                    public void Responce(String resp) {
                        try {
                            progressDialog.setVisibility(View.GONE);
                            JSONObject jsonResponse = new JSONObject(resp);
                            int code_id = Integer.parseInt(jsonResponse.optString("code"));

                            if (code_id == 200) {
                                if (type.equals("open_shift")){
                                    ((OpenShiftActivity)context).finish();
                                    context.startActivity(new Intent(context,MyShiftActivity.class));
                                }else {
                                    ((MyShiftActivity)context).finish();
                                    context.startActivity(new Intent(context,MyShiftActivity.class));
                                }


                            }else {
                                Log.d("confirm", String.valueOf(jsonResponse));
                            }

                        }catch (JSONException e){
                            progressDialog.setVisibility(View.VISIBLE);
                            e.getCause();
                            Log.d("confirm",e.getLocalizedMessage()+" "+e.getMessage());

                        }

                    }
                });





            }
        });

        holder.btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressDialog.setVisibility(View.VISIBLE);
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("user_id",user_id);
                    jsonObject.put("starting_time",getDataAdapter.get(position).getStarting_time());
                    jsonObject.put("ending_time",getDataAdapter.get(position).getEnding_time());
                    jsonObject.put("date",getDataAdapter.get(position).getDate());
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                ApiRequest.Call_Api(context, Config.ADD_RIDER_TIMING, jsonObject, new Callback() {
                    @Override
                    public void Responce(String resp) {
                        try {
                            progressDialog.setVisibility(View.GONE);
                            JSONObject jsonResponse = new JSONObject(resp);
                            int code_id = Integer.parseInt(jsonResponse.optString("code"));

                            if (code_id == 200) {
                                if (type.equals("open_shift")){
                                    ((OpenShiftActivity)context).finish();
                                    context.startActivity(new Intent(context,OpenShiftActivity.class));
                                }else {
                                    ((MyShiftActivity)context).finish();
                                    context.startActivity(new Intent(context,OpenShiftActivity.class));
                                }


                            }else {
                               Log.d("add_riding","else");
                            }

                        }catch (JSONException e){
                            progressDialog.setVisibility(View.VISIBLE);
                            e.getCause();
                            Log.d("add_riding",e.getLocalizedMessage());

                        }

                    }
                });






            }
        });
    }


    @Override
    public int getItemCount() {
        return getDataAdapter.size() ;
    }



    class ViewHolder extends RecyclerView.ViewHolder{

        public TextView time,date,review;
        public Button btn_add,btn_confirm;

        public ViewHolder(View itemView) {

            super(itemView);

            time = itemView.findViewById(R.id.txt_my_time);
            date = itemView.findViewById(R.id.txt_my_date);
            review = itemView.findViewById(R.id.txt_my_shift_in_app_review);
            btn_add = itemView.findViewById(R.id.btn_add);
            btn_confirm = itemView.findViewById(R.id.btn_confirm);

        }
    }


}