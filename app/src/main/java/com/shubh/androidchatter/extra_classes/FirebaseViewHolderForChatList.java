package com.shubh.androidchatter.extra_classes;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.shubh.androidchatter.R;
import com.squareup.picasso.Picasso;

/**
 * Created by root on 21/7/17.
 */

public class FirebaseViewHolderForChatList extends RecyclerView.ViewHolder{
        private static final String TAG = FirebaseViewHolderForChatList.class.getSimpleName();
   public TextView chatName;
    public TextView chatEmail;
    public ImageView profilePic;
    public LinearLayout ll;
        public FirebaseViewHolderForChatList(View v) {
            super(v);
            ll=(LinearLayout)v.findViewById(R.id.LinearLayout);
            chatName = (TextView)v.findViewById(R.id.tv_chatname);
            chatEmail= (TextView)v.findViewById(R.id.tv_chatEmail);
            profilePic=(ImageView)v.findViewById(R.id.iv_ChatsProfilePic);
        }
}