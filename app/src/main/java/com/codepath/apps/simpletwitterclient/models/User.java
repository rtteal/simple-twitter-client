package com.codepath.apps.simpletwitterclient.models;

import org.json.JSONException;
import org.json.JSONObject;

public class User {
    public final String name, screenName, profileImage;
    public final long uid;

    public User(String name, String screenName, String profileImage, long uid) {
        this.name = name;
        this.screenName = screenName;
        this.profileImage = profileImage;
        this.uid = uid;
    }

    public static User fromJson(JSONObject json){
        try {
            String name = json.getString("name");
            long id = json.getLong("id");
            String screenName = json.getString("screen_name");
            String profileImage = json.getString("profile_image_url");
            return new User(name, screenName, profileImage, id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String toString(){
        return String.format("{id: %s, screenName: %s, name: %s, profileImage: %s", uid, screenName, name, profileImage);
    }
}
