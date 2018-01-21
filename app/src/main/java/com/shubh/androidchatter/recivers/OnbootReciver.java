package com.shubh.androidchatter.recivers;

/**
 * Created by root on 23/7/17.
 */
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.shubh.androidchatter.extra_classes.FirebaseNotificationServices;

import static com.facebook.FacebookSdk.getApplicationContext;

public class OnbootReciver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("reciver","on bootup");
        }
}