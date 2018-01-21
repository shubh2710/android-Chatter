package com.shubh.androidchatter.extra_classes;


import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.shubh.androidchatter.Activity.HomeActivity;
import com.shubh.androidchatter.Activity.MainActivity;
import com.shubh.androidchatter.Activity.UserChatActivity;
import com.shubh.androidchatter.database.ChatsDbHelper;
import com.shubh.androidchatter.informationModels.Chats;
import com.shubh.androidchatter.MyApplication;
import com.shubh.androidchatter.R;
import com.shubh.androidchatter.informationModels.MessagesModel;
import com.shubh.androidchatter.informationModels.Requests;

import java.util.ArrayList;
import java.util.HashMap;

import static com.shubh.androidchatter.extra_classes.DbKeys.PREFEMAIL;
import static com.shubh.androidchatter.extra_classes.DbKeys.PREFNAME;

/**
 * Created by root on 23/7/17.
 */
public class FirebaseNotificationServices extends Service {

    FirebaseDatabase database;
    public DatabaseReference msg;
    public DatabaseReference refRequest;
    FirebaseAuth firebaseAuth;
    private Notification mNotification;
    private static int notificationId = 1;
    Context context;
    String msgs;
    private String oldemail=null;
    static String TAG = "FirebaseService";

    DatabaseReference chat;
    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(this, "service started", Toast.LENGTH_SHORT).show();
        database = new FirebaseInctence().getinstence();
        chat=database.getReference("chatList");
        context =getApplicationContext();
        msg=database.getReference("masseges");
        refRequest=database.getReference("requests");
        firebaseAuth = FirebaseAuth.getInstance();
        CheckChatsSyncState();
        SetupNotifire();
    }


    public String readPrefes(String prefesName, String defaultValue){
        SharedPreferences sharedPrefs= MyApplication.preferences;
        return sharedPrefs.getString(prefesName,defaultValue);
    }
    private String TohashCode(String code){
        String str1=code;
        Log.d("hashcodeServeice",str1);
        int hash1=(str1.hashCode());
        Log.d("hashcode1",hash1+"");
        return hash1+"";
    }
    private void SetupNotifire(){
        Log.e("setupService Notif","setup");
        final DatabaseReference msgList =msg.child("convensation").child(TohashCode(readPrefes(PREFEMAIL,null)));
        msgList.keepSynced(true);
        msgList.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d("onmyChildAddedService", dataSnapshot.toString()+" ");
                Log.e("onmycountService", dataSnapshot.getChildrenCount()+" ");
                if(dataSnapshot != null){
                    MessagesModel msg=dataSnapshot.getValue(MessagesModel.class);
                    if(!msg.getMessageSyncState()){
                        if(!msg.getMessageUser().equals(readPrefes(DbKeys.PREFWHICHCHAT,"no")))
                            showNotification(context,msg);
                }
            }
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.d("onChildChanged", dataSnapshot.toString()+" ");
                if(dataSnapshot != null){

                }
            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d("onChildRemoved",dataSnapshot.toString());

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                Log.d("onChildMoved",dataSnapshot.toString());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("onCancelled",databaseError.toString());
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent,flags,startId);

        //stopSelf();
        return Service.START_STICKY;
    }
    @Override
    public void onTaskRemoved(Intent rootIntent){
        Intent restartServiceIntent = new Intent(getApplicationContext(), this.getClass());
        restartServiceIntent.setPackage(getPackageName());
        PendingIntent restartServicePendingIntent = PendingIntent.getService(getApplicationContext(), 1, restartServiceIntent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmService.set(
                AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + 1000,
                restartServicePendingIntent);

        super.onTaskRemoved(rootIntent);
    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void CheckChatsSyncState(){
        final DatabaseReference msgList =msg.child("convensation").child(TohashCode(readPrefes(PREFEMAIL,null)));
        msgList.keepSynced(true);
        msgList.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Log.e("Count check sycn" ,""+snapshot.getChildrenCount());
                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                    MessagesModel msg=postSnapshot.getValue(MessagesModel.class);
                    Log.e("Get Data check sycn", msg.getMessageSyncState()+"");
                    if(!msg.getMessageSyncState()){
                       showNotification(context,msg);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("The read failed: " ,databaseError.getMessage());
            }
        });

    }

    private void showNotification(final Context context,final MessagesModel post){
        if(checkFriend(post.getMessageUser())){
            checkChatInDb(post);
                String m = post.getMessageText();
                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.images)
                        .setContentTitle(post.getSenderUserName())
                        .setDefaults(NotificationCompat.DEFAULT_ALL)
                        .setContentText(m)
                        .setAutoCancel(true);
                String chatfor = post.getMessageUser();
                Intent backIntent = new Intent(context, HomeActivity.class);
                Log.d("which chat", chatfor);
                backIntent.putExtra("sendTo", chatfor);
                backIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                final PendingIntent pendingIntent = PendingIntent.getActivities(context, 900,
                        new Intent[]{backIntent}, PendingIntent.FLAG_UPDATE_CURRENT);
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                stackBuilder.addParentStack(MainActivity.class);
                mBuilder.setContentIntent(pendingIntent);
                NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.notify(1, mBuilder.build());
        }else {
            final DatabaseReference request=refRequest.child(TohashCode(readPrefes(PREFEMAIL,null)));
            request.keepSynced(true);
            request.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    Log.e("Count check request" ,""+snapshot.getChildrenCount());
                    Boolean isfound=false;
                    Requests req=null;
                    for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                        req=postSnapshot.getValue(Requests.class);
                        Log.e("Requests", req.getEmail());
                        if (req.getEmail().equals(post.getMessageUser())) {
                            Log.e("Request found", post.getMessageUser());
                            isfound=true;
                        }
                    }
                    addRequest(isfound,post);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e("The read failed: " ,databaseError.getMessage());
                }
            });
        }
    }
    private void checkChatInDb(MessagesModel m) {
        ChatsDbHelper dbHelper=new ChatsDbHelper(context);
        SQLiteDatabase database=dbHelper.getReadableDatabase();
        if(dbHelper.CheckChatExist(m.getMessageUser(),database)){
            dbHelper.saveChatToLocalDb(m.getSenderUserName(),m.getMessageUser(),0,database);
        }
    }
    private void addRequest(boolean check,MessagesModel post) {
        if(oldemail==null || oldemail!=post.getMessageUser()){
            oldemail=post.getMessageUser();
            if (!check) {
                final DatabaseReference request = refRequest.child(TohashCode(readPrefes(PREFEMAIL, null)));
                Requests r = new Requests(post.getSenderUserName(), false, post.getMessageUser());
                request.push().setValue(r);
            }
        }
    }
    private boolean checkFriend(String messageUser) {
        ChatsDbHelper dbHelper=new ChatsDbHelper(context);
        SQLiteDatabase database=dbHelper.getReadableDatabase();
        return  dbHelper.CheckFriendExist(messageUser,database);
    }
}