package com.shubh.androidchatter.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.shubh.androidchatter.informationModels.Chats;
import com.shubh.androidchatter.MyApplication;
import com.shubh.androidchatter.R;
import com.shubh.androidchatter.informationModels.User;
import com.shubh.androidchatter.extra_classes.DbKeys;
import com.shubh.androidchatter.extra_classes.FirebaseInctence;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;

import id.zelory.compressor.Compressor;

import static com.shubh.androidchatter.extra_classes.DbKeys.PREFEMAIL;

public class SetUpProfileActivity extends AppCompatActivity implements View.OnClickListener {
private ImageView profile;
private EditText status;
    private StorageReference mStorageRef;
    private ImageButton button;
    private CropImageView cropImageView;
    private Uri filePath;
    private Button save;
    private ProgressDialog pd;
    private Context context=this;
    private Uri imageUri=null;
    private File f=null;

    FirebaseDatabase database = new FirebaseInctence().getinstence();
    DatabaseReference chat=database.getReference("chatList");
    DatabaseReference users=database.getReference("users");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Profile");
        setContentView(R.layout.activity_set_up_profile);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        save=(Button)findViewById(R.id.b_save);
        profile =(ImageView)findViewById(R.id.iv_SetUpProfilePic);
        button=(ImageButton)findViewById(R.id.iv_SetUpProfilePicButton);
        status=(EditText)findViewById(R.id.et_status);
        button.setOnClickListener(this);
        save.setOnClickListener(this);
        profile.setImageResource(R.drawable.images);
        checkForOldData();
       // updateUser(" ");
    }

    private void checkForOldData() {
        users.keepSynced(true);
        users.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    User post = postSnapshot.getValue(User.class);
                    Log.e("Get Data", post.getEmail());
                    if(post.getEmail().equals(readPrefes(context,PREFEMAIL,null))){
                        Log.d("user detail",post.getEmail());
                        status.setText(post.getStatus());
                        if(post.getProfileUrl()!=" "){
                            Glide.with(context).load(post.getProfileUrl())
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
                                    .into(profile);
                        }
                        else {
                            Picasso.with(context)
                                    .load(R.drawable.images)
                                    .noFade()
                                    .into(profile);

                        }

                    }
                    else{
                        Log.d("user not exisit","ni hai");
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.b_save:
                if(imageUri!=null && f!=null){
                Uri file = imageUri;
                  //  pd = ProgressDialog.show(this, "Uploading", "wait...");
                  //  pd.setCancelable(false);
                    Intent i=new Intent(context,HomeActivity.class);
                    startActivity(i);
                    Toast.makeText(context,"Profile is uploading",Toast.LENGTH_LONG).show();
                    finish();
                    String url=TohashCode(readPrefes(this,PREFEMAIL,null));
                    StorageReference riversRef = mStorageRef.child("profiles/"+url+".jpg");
                    riversRef.putFile(Uri.fromFile(f)).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            Log.e("Tuts+", "Bytes uploaded: " + progress);
                        }
                        })
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                // Get a URL to the uploaded content
                                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                updateUser(downloadUrl.toString());
                                updateChats(downloadUrl.toString());
                               // pd.dismiss();
                                Toast.makeText(context,"profile Uploaded",Toast.LENGTH_LONG).show();

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle unsuccessful uploads
                              //  pd.dismiss();
                                Toast.makeText(context,"Filed To Upload",Toast.LENGTH_LONG).show();
                            }
                        });
                }
                break;
            case R.id.iv_SetUpProfilePicButton:
                showFileChooser();
                break;
        }
    }

    private void updateChats(final String newUrl) {

        DatabaseReference chats=chat.child("chats").child(TohashCode(readPrefes(context,PREFEMAIL,null)));
        chats.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    Chats post = postSnapshot.getValue(Chats.class);
                    Log.e("Get Data", post.getChatFor());
                    updateProfile(post.getChatFor(),newUrl);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void updateProfile(String chatFor, final String newUrl) {
        final DatabaseReference chatList =chat.child("chats").child(TohashCode(chatFor));
        Query query = chatList.orderByChild("syncState");
        chatList.keepSynced(true);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataSnapshot nodeDataSnapshot = dataSnapshot.getChildren().iterator().next();
                String key = nodeDataSnapshot.getKey(); // this key is `K1NRz9l5PU_0CFDtgXz`
                String path = "/" + key;
                HashMap<String, Object> result = new HashMap<>();
                result.put("profileUrl", newUrl);
                Log.d("updating chat profile",path);
                chatList.child(path).updateChildren(result);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    private void updateUser(final String downloadUrl) {
        final DatabaseReference user =users;
        user.keepSynced(true);
        saveToPref(this, DbKeys.PREFPROFILE,downloadUrl);
        Query query = user.orderByChild("email").equalTo(readPrefes(context,PREFEMAIL,null));
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataSnapshot nodeDataSnapshot = dataSnapshot.getChildren().iterator().next();
                String key = nodeDataSnapshot.getKey(); // this key is `K1NRz9l5PU_0CFDtgXz`
                String path = "/" + key;
                HashMap<String, Object> result = new HashMap<>();
                result.put("lastseen", new Date().getTime());
                if(status.getText().toString()==null)
                    result.put("status", "Hello i am new here");
                else
                    result.put("status", status.getText().toString());
                result.put("profileUrl",downloadUrl);
                result.put("profile",true);
                Log.d("updating chat",path);
                user.child(path).updateChildren(result);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Pdf"), 123);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 123 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            Log.d("File Path",filePath.toString());
            CropImage.activity(filePath)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                //cropImageView.setImageUriAsync(resultUri);
                try {
                    f = new Compressor(this)
                            .setMaxWidth(300)
                            .setMaxHeight(300)
                            .setQuality(50)
                            .setCompressFormat(Bitmap.CompressFormat.JPEG)
                            .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(
                                    Environment.DIRECTORY_PICTURES).getAbsolutePath())
                            .compressToFile(new File(resultUri.getPath()));
                    //f = new Compressor(this).compressToFile(new File(resultUri.getPath()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Picasso.with(this)
                        .load(resultUri).
                         placeholder(R.drawable.images)
                        .noFade()
                        .into(profile, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {

                    }
                    @Override
                    public void onError() {

                    }
                });

                imageUri=resultUri;
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
    
    public static String readPrefes(Context context, String prefesName, String defaultValue){
        SharedPreferences sharedPrefs= MyApplication.preferences;
        return sharedPrefs.getString(prefesName,defaultValue);
    }
    private String TohashCode(String code){
        String str1=code;
        Log.d("hashcode2",str1);
        int hash1=(str1.hashCode());
        Log.d("hashcode2",hash1+"");
        return hash1+"";
    }
    public static void saveToPref(Context context, String preferenceName, String preferenceValue ){
        SharedPreferences sheredPreference= MyApplication.preferences;
        SharedPreferences.Editor editor=sheredPreference.edit();
        editor.putString(preferenceName,preferenceValue);
        editor.apply();
    }

}