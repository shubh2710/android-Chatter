package com.shubh.androidchatter.adapters;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.shubh.androidchatter.informationModels.Chats;
import com.shubh.androidchatter.R;
import com.shubh.androidchatter.informationModels.User;
import com.shubh.androidchatter.extra_classes.FirebaseInctence;
import com.shubh.androidchatter.extra_classes.FirebaseViewHolderForChatList;
import com.squareup.picasso.Picasso;

/**
 * Created by root on 21/7/17.
 */

public class fireBaseAdapterForChatList extends FirebaseRecyclerAdapter<Chats, FirebaseViewHolderForChatList> {
        private static final String TAG = fireBaseAdapterForChatList.class.getSimpleName();
        private Context context;
        FirebaseDatabase database = new FirebaseInctence().getinstence();
        DatabaseReference users=database.getReference("users");
        private StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        public fireBaseAdapterForChatList(Class<Chats> modelClass, int modelLayout, Class<FirebaseViewHolderForChatList> viewHolderClass, DatabaseReference ref, Context context) {
            super(modelClass, modelLayout, viewHolderClass, ref);
            this.context=context;
        }
        @Override
        protected void populateViewHolder(final FirebaseViewHolderForChatList viewHolder, Chats model, int position) {
            // Set their text
            if(model!=null){
            if(model.getSyncState())
                viewHolder.ll.setBackgroundColor(Color.argb(100,100,50,20));
            else
                viewHolder.ll.setBackgroundColor(Color.argb(100,100,50,100));
            viewHolder.chatName.setText(model.getChatName());
            viewHolder.chatEmail.setText(model.getChatFor());
            getursetDetile(model.getChatFor(),viewHolder.profilePic);
            }
        }
    private void getursetDetile(String chatTo ,final ImageView pic) {
        users.keepSynced(true);
        Query query = users.orderByChild("email").equalTo(chatTo);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataSnapshot nodeDataSnapshot = dataSnapshot.getChildren().iterator().next();
                User post = nodeDataSnapshot.getValue(User.class);
                Log.e("User profile", post.getProfileUrl());
                if(post.getProfileUrl()!=" "){
                    Glide.with(context)
                            .load(post.getProfileUrl())
                            .placeholder(R.drawable.images)
                            .listener(new RequestListener<String, GlideDrawable>() {
                                @Override
                                public boolean onException(Exception e, String model, com.bumptech.glide.request.target.Target<GlideDrawable> target, boolean isFirstResource) {
                                    return false;
                                }
                                @Override
                                public boolean onResourceReady(GlideDrawable resource, String model, com.bumptech.glide.request.target.Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                    // viewHolder.profilePic.setImageResource();
                                    return false;
                                }

                            })
                            .dontAnimate()
                            .into(pic);
                }
                else {
                    Picasso.with(context)
                            .load(R.drawable.images)
                            .noFade()
                            .into(pic);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private String TohashCode(String code){
        String str1=code;
        Log.d("hashcode1",str1);
        int hash1=(str1.hashCode());
        Log.d("hashcode1",hash1+"");
        return hash1+"";
    }
}