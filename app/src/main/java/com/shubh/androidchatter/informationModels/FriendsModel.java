package com.shubh.androidchatter.informationModels;

/**
 * Created by root on 3/8/17.
 */

public class FriendsModel extends User{

    public FriendsModel(String name, String email, boolean profile, String profileUrl, Long lastseen, String status) {
        super(name, email, profile, profileUrl, lastseen, status);
    }

    public FriendsModel() {
    }
}
