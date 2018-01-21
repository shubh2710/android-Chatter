package com.shubh.androidchatter.recivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.shubh.androidchatter.extra_classes.FirebaseNotificationServices;

/**
 * Created by root on 24/7/17.
 */

public class NetWatcher extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //here, check that the network connection is available. If yes, start your service. If not, stop your service.
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        Intent i;
        if (info != null) {
            if (info.isConnected()) {
                //start service
                 i = new Intent(context, FirebaseNotificationServices.class);
                context.startService(i);
                Toast.makeText(context,"service started",Toast.LENGTH_LONG).show();
            }
            else {
                //stop service
                Toast.makeText(context,"service stop",Toast.LENGTH_LONG).show();
                 i = new Intent(context, FirebaseNotificationServices.class);
                context.stopService(i);
            }
        }
    }
}