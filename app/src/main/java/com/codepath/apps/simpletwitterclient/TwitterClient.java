package com.codepath.apps.simpletwitterclient;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;

import android.content.Context;
import android.util.Log;

import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/*
 * 
 * This is the object responsible for communicating with a REST API. 
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes: 
 *   https://github.com/fernandezpablo85/scribe-java/tree/master/src/main/java/org/scribe/builder/api
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 * 
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 * 
 */
public class TwitterClient extends OAuthBaseClient {
	public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class; // Change this
	public static final String REST_URL = "https://api.twitter.com/1.1"; // Change this, base API URL
	public static final String REST_CONSUMER_KEY = "NSUf8REI0YhFQmylk7iSEIPpK";       // Change this
	public static final String REST_CONSUMER_SECRET = "Wt5lykjZ3AVdYtZWIGIArRdL5XTsMoilOshPDfqH2uMf58Nayc"; // Change this
	public static final String REST_CALLBACK_URL = "oauth://SFTweetsApp"; // Change this (here and in manifest)

	public TwitterClient(Context context) {
		super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
	}

    public void getHomeTimeline(AsyncHttpResponseHandler handler){
        RequestParams params = new RequestParams();
        params.put("count", 25);
        params.put("since_id", 1);
        getHomeTimeline(params, handler);
    }

    public void getHomeTimeline(RequestParams params, AsyncHttpResponseHandler handler){
        String apiUrl = getApiUrl("statuses/home_timeline.json");
        getClient().get(apiUrl, params, handler);
    }

    public void sendTweet(String tweet, AsyncHttpResponseHandler handler){
        String apiUrl = getApiUrl("statuses/update.json");
        RequestParams params = new RequestParams();
        params.put("status", tweet);
        getClient().post(apiUrl, params, handler);
    }

    public void getCurrentUserInfo(AsyncHttpResponseHandler handler){
        String apiUrl = getApiUrl("account/verify_credentials.json");
        getClient().get(apiUrl, handler);
    }

    public void getMentionsTimeline(AsyncHttpResponseHandler handler){
        RequestParams params = new RequestParams();
        params.put("count", 25);
        getMentionsTimeline(params, handler);
    }

    public void getMentionsTimeline(RequestParams params, AsyncHttpResponseHandler handler){
        String apiUrl = getApiUrl("statuses/mentions_timeline.json");
        getClient().get(apiUrl, params, handler);
    }

    public void getUserTimeline(String screenName, AsyncHttpResponseHandler handler){
        RequestParams params = new RequestParams();
        params.put("count", 25);
        params.put("screen_name", screenName);
        getUserTimeline(params, handler);
    }

    public void getUserTimeline(AsyncHttpResponseHandler handler){
        RequestParams params = new RequestParams();
        params.put("count", 25);
        getUserTimeline(params, handler);
    }

    public void getUserTimeline(RequestParams params, AsyncHttpResponseHandler handler){
        String apiUrl = getApiUrl("statuses/user_timeline.json");
        Log.d("profileDebug", apiUrl + ", params: " + params);
        getClient().get(apiUrl, params, handler);
    }

	/* 1. Define the endpoint URL with getApiUrl and pass a relative path to the endpoint
	 * 	  i.e getApiUrl("statuses/home_timeline.json");
	 * 2. Define the parameters to pass to the request (query or body)
	 *    i.e RequestParams params = new RequestParams("foo", "bar");
	 * 3. Define the request method and make a call to the client
	 *    i.e client.get(apiUrl, params, handler);
	 *    i.e client.post(apiUrl, params, handler);
	 */
}