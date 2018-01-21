package com.shubh.androidchatter.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.shubh.androidchatter.MyApplication;
import com.shubh.androidchatter.R;
import com.shubh.androidchatter.database.ChatsDbHelper;
import com.shubh.androidchatter.extra_classes.FirebaseInctence;
import com.shubh.androidchatter.informationModels.MessagesModel;
import com.shubh.androidchatter.informationModels.User;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static com.shubh.androidchatter.extra_classes.DbKeys.PREFEMAIL;


public class RecycleViewAdapter extends RecyclerView.Adapter<RecycleViewAdapter.MyViewHolder>{

    private Context context;
    private List<MessagesModel> data= Collections.emptyList();
    private LayoutInflater inflater;
    Resources resources;

    FirebaseDatabase database = new FirebaseInctence().getinstence();
    DatabaseReference msg=database.getReference("masseges");

    public RecycleViewAdapter(Context context, List<MessagesModel> data, Resources resources){
        inflater= LayoutInflater.from(context);
        this.data=data;
        this.resources=resources;
        this.context=context;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view=inflater.inflate(R.layout.message,parent,false);
        MyViewHolder holder=new MyViewHolder(view);
        return holder;
    }
    @Override
    public void onBindViewHolder(MyViewHolder viewHolder, int position) {
        MessagesModel model=data.get(position);
        String myemail=readPrefes(context,PREFEMAIL,null);
        Log.d("all msg",model.getMessageText());
        if(model.getMessageUser().equals(myemail)){
            Log.d("MY msg",model.getMessageText());
            viewHolder.HisLayout.setVisibility(View.GONE);
            viewHolder.myLayout.setVisibility(View.VISIBLE);
            viewHolder.MymessageText.setText(model.getMessageText());
            viewHolder.MymessageUser.setText("me");
            viewHolder.MymessageTime.setText(DateFormat.format("(HH:mm)",model.getMessageTime()));
            if(model.getMessageSyncState()){
                viewHolder.MymessageStatus.setText("D");
                viewHolder.MymessageStatus.setTextColor(Color.argb(100,239, 42, 78));
            }else {
                viewHolder.MymessageStatus.setText("P");
                viewHolder.MymessageStatus.setTextColor(Color.argb(100,239, 42, 110));
            }
        }else{
            Log.e("HIS msg",model.getMessageText());
            viewHolder.myLayout.setVisibility(View.GONE);
            viewHolder.HisLayout.setVisibility(View.VISIBLE);
            viewHolder.messageText.setText(model.getMessageText());
            viewHolder.messageUser.setText(model.getSenderUserName());
            viewHolder.messageTime.setText(DateFormat.format("(HH:mm)",model.getMessageTime()));
        }
        if(model.getMessageSyncState()==false && model.getMessageSendTo().equals(myemail)) {
            // updateSyncState(model);
        }
        else
        if(model.getMessageSyncState()==false)
            checkSyncStatus(model,myemail);
        Log.d("POSITION","postion  on bind"+position);
    }

    private void checkSyncStatus(final MessagesModel model, String myemail) {
        final DatabaseReference updateMsg =msg.child("convensation").child(TohashCode(model.getMessageSendTo()));
        updateMsg.keepSynced(true);
        Query query = updateMsg.orderByChild("messageUser").equalTo(model.getMessageUser());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                    MessagesModel post = postSnapshot.getValue(MessagesModel.class);
                    if(post.getMessageTime()==model.getMessageTime()){
                            if(post.getMessageSyncState()){
                                Log.e("message seen",post.getMessageSyncState()+"");
                                updateMsgSyncInDb(post,model);
                                updateMsg.child(postSnapshot.getRef().getKey()).removeValue();
                            }
                    }
            }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void updateMsgSyncInDb(MessagesModel post,MessagesModel model) {
        ChatsDbHelper dbHelper=new ChatsDbHelper(context);
        SQLiteDatabase database=dbHelper.getWritableDatabase();
        dbHelper.updateMsgLocalDataBase(post.getMessageUser(),post.getMessageSendTo(),post.getMessageTime(),1,database);
        model.setMessageSyncState(true);
    }
    @Override
    public int getItemCount() {
        return data.size();
    }
   public class MyViewHolder extends RecyclerView.ViewHolder{
       public TextView messageText;
       public RelativeLayout myLayout;
       public RelativeLayout HisLayout;
       public TextView messageUser;
       public TextView messageTime;

       public TextView MymessageText;
       public TextView MymessageUser;
       public TextView MymessageTime;
       public TextView MymessageStatus;
       public MyViewHolder(View v) {
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
    public void updateSyncState(MessagesModel model){
        final DatabaseReference updateMsg =msg.child("convensation").child(TohashCode(model.getMessageSendTo()));
        updateMsg.keepSynced(true);
        Query query = updateMsg.orderByChild("messageUser").equalTo(model.getMessageUser());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataSnapshot nodeDataSnapshot = dataSnapshot.getChildren().iterator().next();
                MessagesModel post = nodeDataSnapshot.getValue(MessagesModel.class);
                if(!post.getMessageSyncState()){
                        String key = nodeDataSnapshot.getKey();
                        String path = "/" + key;
                        HashMap<String, Object> result = new HashMap<>();
                        result.put("messageSyncState", true);
                        Log.e("update chat",result.toString());
                        updateMsg.child(path).updateChildren(result);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
    private String TohashCode(String code){
        String str1=code;
        Log.d("hashcode",str1);
        int hash1=(str1.hashCode());
        Log.d("hashcode1",hash1+"");
        return hash1+"";
    }
    public static String readPrefes(Context context, String prefesName, String defaultValue){
        SharedPreferences sharedPrefs= MyApplication.preferences;
        return sharedPrefs.getString(prefesName,defaultValue);
    }

}
