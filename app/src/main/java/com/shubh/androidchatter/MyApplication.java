package com.shubh.androidchatter;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatDelegate;

import static com.shubh.androidchatter.extra_classes.DbKeys.PREF_FILE_NAME;

/**
 * Created by root on 23/7/17.
 */
public class MyApplication extends Application {
    public static SharedPreferences preferences;


    @Override
    public void onCreate() {

        super.onCreate();
        //AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        preferences = getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
    }
        }
