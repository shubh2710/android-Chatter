package com.shubh.androidchatter.extra_classes;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shubh.androidchatter.R;

/**
 * Created by root on 21/7/17.
 */

public class FirebaseViewHolder extends RecyclerView.ViewHolder{
        private static final String TAG = FirebaseViewHolder.class.getSimpleName();
   public TextView messageText;
    public RelativeLayout myLayout;
    public RelativeLayout HisLayout;
   public TextView messageUser;
   public TextView messageTime;

    public TextView MymessageText;
    public TextView MymessageUser;
    public TextView MymessageTime;
    public TextView MymessageStatus;
        public FirebaseViewHolder(View v) {
            super(v);
            myLayout=(RelativeLayout)v.findViewById(R.id.mylayout);
            HisLayout=(RelativeLayout)v.findViewById(R.id.layout);
            MymessageStatus=(TextView)v.findViewById(R.id.message_status);
            MymessageText = (TextView)v.findViewById(R.id.message_textTo);
            MymessageUser = (TextView)v.findViewById(R.id.message_userTo);
            MymessageTime = (TextView)v.findViewById(R.id.message_timeTo);
            messageText = (TextView)v.findViewById(R.id.message_text);
            messageUser = (TextView)v.findViewById(R.id.message_user);
            messageTime = (TextView)v.findViewById(R.id.message_time);
        }
}
