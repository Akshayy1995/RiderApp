package com.foodies.hangrymatesrider.ActivitiesAndFragments.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.foodies.hangrymatesrider.Constants.PreferenceClass;
import com.foodies.hangrymatesrider.Models.ChatMessage;
import com.foodies.hangrymatesrider.Utils.RelateToFragment_OnBack.RootFragment;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.foodies.hangrymatesrider.R;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Dinosoftlabs on 10/18/2019.
 */

public class RChatFragment extends RootFragment {

    ImageView send_icon;
    private FirebaseListAdapter<ChatMessage> adapter;
    SharedPreferences chatPref;
    String sender_id,receiver_id, f_name,l_name,user_name;
    ListView listOfMessages;
    DatabaseReference mDatabase;
    String time_start,time_end;
    int today_day;

    View v;
    Context context;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.rider_chat_fragment, container, false);
        chatPref = getContext().getSharedPreferences(PreferenceClass.user, Context.MODE_PRIVATE);

        sender_id = chatPref.getString(PreferenceClass.pre_user_id,"");
        receiver_id="0";

        time_start = "11:59:00";
        time_end="23:59:00";

        Calendar cal = Calendar.getInstance();
        today_day = cal.get(Calendar.DAY_OF_MONTH);

         f_name = chatPref.getString(PreferenceClass.pre_first,"");
         l_name = chatPref.getString(PreferenceClass.pre_last,"");
         user_name = f_name+ " "+ l_name;


        final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        mDatabase = firebaseDatabase.getReference().child("Chat").child(sender_id+"-"+receiver_id);

        initUI(v);
        displayChatMessages();
        return v;
    }

    public void initUI(final View v){


        listOfMessages = (ListView) v.findViewById(R.id.list_of_messages);


        send_icon = v.findViewById(R.id.send_icon);

        send_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText input = (EditText)v.findViewById(R.id.input);

                  if(input.getText().toString().isEmpty()){
                    Toast.makeText(getContext(),"Add Character to Send.",Toast.LENGTH_SHORT).show();
                }
                else {
                    mDatabase.push().setValue(new ChatMessage(receiver_id,
                            sender_id,user_name,input.getText().toString()));
                }


                input.setText("");

            }
        });

    }

    public void displayChatMessages() {

        Query query = FirebaseDatabase.getInstance().getReference().child("Chat").child(sender_id+"-"+receiver_id);
        query.keepSynced(true);

        FirebaseListOptions<ChatMessage> options = new FirebaseListOptions.Builder<ChatMessage>()
                .setQuery(query, ChatMessage.class)
                .setLayout(R.layout.row_item_chat_list)
                .build();


        adapter = new FirebaseListAdapter<ChatMessage>(options) {
            @Override
            protected void populateView(View v, ChatMessage model, int position) {

                ChatMessage getDataAdapter1 =  adapter.getItem(position);

                TextView messageTime = (TextView) v.findViewById(R.id.message_time);
                messageTime.setText( ChangeDate(getDataAdapter1.getTimestamp()));
                RelativeLayout bubble_admin = (RelativeLayout)v.findViewById(R.id.bubble_admin);
                RelativeLayout bubble_rider = (RelativeLayout)v.findViewById(R.id.bubble);


                bubble_admin.setTag(getDataAdapter1);
                ChatMessage chatMessage=(ChatMessage)bubble_admin.getTag();

                String senderID = model.getSender_id();

                if(senderID!=null) {
                    if (!chatMessage.getSender_id().equalsIgnoreCase(sender_id)) {

                        bubble_admin.setVisibility(View.VISIBLE);
                        bubble_rider.setVisibility(View.GONE);


                        TextView messageText = (TextView) v.findViewById(R.id.message_text_admin);
                        messageText.setTextColor(getResources().getColor(R.color.black));

                        messageText.setText(model.getText());

                    } else {
                        bubble_admin.setVisibility(View.GONE);
                        bubble_rider.setVisibility(View.VISIBLE);
                        TextView messageText = (TextView) v.findViewById(R.id.message_text);
                        messageText.setText(model.getText());

                    }
                }

            }
        };
        listOfMessages.setAdapter(adapter);
        listOfMessages.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mDatabase.push().setValue(new ChatMessage(receiver_id,
                                    sender_id,user_name,"Hi im here for work"));
                        }
                    },2000);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    public String ChangeDate(String date){

        try {


            long currenttime = System.currentTimeMillis();

            SimpleDateFormat f = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
            long databasedate = 0;
            Date d = null;
            try {
                d = f.parse(date);
                databasedate = d.getTime();

            } catch (ParseException e) {
                e.printStackTrace();
            }
            long difference = currenttime - databasedate;
            if (difference < 86400000) {
                int chatday = Integer.parseInt(date.substring(0, 2));
                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
                if (today_day == chatday)
                    return "Today " + sdf.format(d);
                else if ((today_day - chatday) == 1)
                    return "Yesterday " + sdf.format(d);
            } else if (difference < 172800000) {
                int chatday = Integer.parseInt(date.substring(0, 2));
                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
                if ((today_day - chatday) == 1)
                    return "Yesterday " + sdf.format(d);
            }

            SimpleDateFormat sdf = new SimpleDateFormat("MMM-dd-yyyy hh:mm a");
            return sdf.format(d);
        }
        catch (Exception e){

        }
        finally {

            return "";
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }



}
