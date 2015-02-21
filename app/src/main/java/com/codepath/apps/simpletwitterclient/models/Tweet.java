package com.codepath.apps.simpletwitterclient.models;

import android.text.format.DateUtils;
import android.util.Log;

import com.codepath.apps.simpletwitterclient.TwitterClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by rtteal on 2/17/2015.
 */
public class Tweet {
    public final String body, createdAt;
    public final User user;
    public final long uid; // unique id for tweet
    private static final int ONE_MINUTE = 60;
    private static final int ONE_HOUR = 60 * 60;
    private static final int ONE_DAY = 60 * 60 * 24;
    private static final int ONE_WEEK = 60 * 60 * 24 * 7;

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
            return new Tweet(body, getFormattedTime(createAt), uid, user);
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

    private static String getFormattedTime(final String input){
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        long longCreatedAt = 0;
        try {
            longCreatedAt = sf.parse(input).getTime()/1000;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        final long now = System.currentTimeMillis()/1000;
        final long elapsedTime = now - longCreatedAt;
        Log.d("Taylor", String.format("longCreatedAt: %s, now: %s, elapsedTime: %s", longCreatedAt, now, elapsedTime));
        if (elapsedTime < 0) { // suspect that timezone is causing some of these to be negative
            return "1s";
        } else if(elapsedTime < ONE_MINUTE){
            return elapsedTime + "s";
        } else if (elapsedTime < ONE_HOUR){
            return Math.round(elapsedTime/ONE_MINUTE) + "m";
        } else if (elapsedTime < ONE_DAY){
            return Math.round(elapsedTime/ONE_HOUR) + "h";
        } else if (elapsedTime < ONE_WEEK){
            return Math.round(elapsedTime/ONE_DAY) + "d";
        } else {
            return Math.round(elapsedTime/ONE_WEEK) + "w";
        }
    }
}
