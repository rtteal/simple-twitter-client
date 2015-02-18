package com.codepath.apps.simpletwitterclient.models;

import android.util.Log;

import com.codepath.apps.simpletwitterclient.TwitterClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rtteal on 2/17/2015.
 */
public class Tweet {
    public final String body, createdAt;
    final public User user;
    public final long uid; // unique id for tweet

    public Tweet(String body, String createdAt, long uid, User user) {
        this.body = body;
        this.createdAt = createdAt;
        this.uid = uid;
        this.user = user;
    }

    public static Tweet fromJson(JSONObject json){
        try {
            String body = json.getString("text");
            long uid = json.getLong("id");
            String createAt = json.getString("created_at");
            User user = User.fromJson(json.getJSONObject("user"));
            return new Tweet(body, createAt, uid, user);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Tweet> fromJsonArray(JSONArray jsonArray){
        List<Tweet> tweets = new ArrayList<>(jsonArray.length());
        for (int i = 0; i < jsonArray.length(); i++){
            try {
                Tweet t = fromJson(jsonArray.getJSONObject(i));
                if (null != t) tweets.add(t);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return tweets;
    }
}
