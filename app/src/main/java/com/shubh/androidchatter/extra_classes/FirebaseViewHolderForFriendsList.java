package com.shubh.androidchatter.extra_classes;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shubh.androidchatter.R;

/**
 * Created by root on 21/7/17.
 */

public class FirebaseViewHolderForFriendsList extends RecyclerView.ViewHolder{
        private static final String TAG = FirebaseViewHolderForFriendsList.class.getSimpleName();
   public TextView friendName;
    public TextView friendEmail;
    public ImageView friendprofilePic;
    public LinearLayout ll;
        public FirebaseViewHolderForFriendsList(View v) {
            super(v);
            ll=(LinearLayout)v.findViewById(R.id.LinearLayout);
            friendName = (TextView)v.findViewById(R.id.tv_chatname);
            friendEmail= (TextView)v.findViewById(R.id.tv_chatEmail);
            friendprofilePic=(ImageView)v.findViewById(R.id.iv_ChatsProfilePic);
        }
}