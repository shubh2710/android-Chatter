package com.shubh.androidchatter.adapters;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.shubh.androidchatter.informationModels.FriendsModel;
import com.shubh.androidchatter.R;
import com.shubh.androidchatter.extra_classes.FirebaseInctence;
import com.shubh.androidchatter.extra_classes.FirebaseViewHolderForFriendsList;
import com.squareup.picasso.Picasso;

/**
 * Created by root on 21/7/17.
 */

public class fireBaseAdapterForFriendsList extends FirebaseRecyclerAdapter<FriendsModel, FirebaseViewHolderForFriendsList> {
        private static final String TAG = fireBaseAdapterForFriendsList.class.getSimpleName();
        private Context context;
        FirebaseDatabase database = new FirebaseInctence().getinstence();
        DatabaseReference users=database.getReference("users");

        private StorageReference storageRef = FirebaseStorage.getInstance().getReference();

        public fireBaseAdapterForFriendsList(Class<FriendsModel> modelClass, int modelLayout, Class<FirebaseViewHolderForFriendsList> viewHolderClass, DatabaseReference ref, Context context) {
            super(modelClass, modelLayout, viewHolderClass, ref);
            this.context=context;
        }
        @Override
        protected void populateViewHolder(final FirebaseViewHolderForFriendsList viewHolder, FriendsModel model, int position) {
            // Set their text

            viewHolder.friendName.setText(model.getName());
            viewHolder.friendEmail.setText(model.getEmail());
            if(model.getProfileUrl()!=" "){
                Glide.with(context)
                        .load(model.getProfileUrl())
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
                        .into(viewHolder.friendprofilePic);
            }
            else {
                Picasso.with(context)
                        .load(R.drawable.images)
                        .noFade()
                        .into(viewHolder.friendprofilePic);

            }

        }
}