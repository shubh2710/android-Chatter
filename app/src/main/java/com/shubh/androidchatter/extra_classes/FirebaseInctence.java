package com.shubh.androidchatter.extra_classes;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by root on 30/7/17.
 */

public class FirebaseInctence {
    public static FirebaseDatabase firebasebase=null;


    public FirebaseDatabase getinstence(){
        if(firebasebase==null){
            FirebaseDatabase.getInstance().setPersistenceEnabled(false);
           return firebasebase=FirebaseDatabase.getInstance();
        }else
            return firebasebase=FirebaseDatabase.getInstance();
    }
}