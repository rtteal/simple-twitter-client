package com.codepath.apps.simpletwitterclient;

import android.content.Context;
import android.util.Log;

import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;


public class TwitterClient extends OAuthBaseClient {
	public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class; // Change this
	public static final String REST_URL = "https://api.twitter.com/1.1"; // Change this, base API URL
	public static final String REST_CONSUMER_KEY = "NSUf8REI0YhFQmylk7iSEIPpK";       // Change this
	public static final String REST_CONSUMER_SECRET = "Wt5lykjZ3AVdYtZWIGIArRdL5XTsMoilOshPDfqH2uMf58Nayc"; // Change this
	public static final String REST_CALLBACK_URL = "oauth://SFTweetsApp"; // Change this (here and in manifest)
    private static final String TAG = TwitterClient.class.getSimpleName();

	public TwitterClient(Context context) {
		super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
	}

    public void getHomeTimeline(AsyncHttpResponseHandler handler){
        getHomeTimeline(-1, handler);
    }

    public void getHomeTimeline(long offset, AsyncHttpResponseHandler handler){
        RequestParams params = new RequestParams();
        params.put("count", 25);
        if (offset > 0) params.put("max_id", offset);
        else params.put("since_id", 1);
        String apiUrl = getApiUrl("statuses/home_timeline.json");
        getClient().get(apiUrl, params, handler);
    }

    public void sendTweet(String tweet, AsyncHttpResponseHandler handler){
        String apiUrl = getApiUrl("statuses/update.json");
        RequestParams params = new RequestParams();
        params.put("status", tweet);
        Log.d(TAG, apiUrl + ", params: " + params);
        getClient().post(apiUrl, params, handler);
    }

    public void getCurrentUserInfo(AsyncHttpResponseHandler handler){
        String apiUrl = getApiUrl("account/verify_credentials.json");
        Log.d(TAG, apiUrl);
        getClient().get(apiUrl, handler);
    }

    public void getMentionsTimeline(AsyncHttpResponseHandler handler){
        getMentionsTimeline(-1, handler);
    }

    public void getMentionsTimeline(long offset, AsyncHttpResponseHandler handler){
        RequestParams params = new RequestParams();
        params.put("count", 25);
        if (offset > 0 ) params.put("max_id", offset);
        String apiUrl = getApiUrl("statuses/mentions_timeline.json");
        Log.d(TAG, apiUrl + ", params: " + params);
        getClient().get(apiUrl, params, handler);
    }

    public void getUserTimeline(String screenName, AsyncHttpResponseHandler handler){
        getUserTimeline(-1, screenName, handler);
    }

    public void getUserTimeline(long offset, String screenName, AsyncHttpResponseHandler handler){
        RequestParams params = new RequestParams();
        params.put("count", 25);
        params.put("screen_name", screenName);
        if (offset > 0) params.put("max_id", offset);
        String apiUrl = getApiUrl("statuses/user_timeline.json");
        Log.d(TAG, apiUrl + ", params: " + params);
        getClient().get(apiUrl, params, handler);
    }
}