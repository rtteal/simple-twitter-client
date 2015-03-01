package com.codepath.apps.simpletwitterclient;

import android.content.Context;
import android.util.Log;

import com.codepath.apps.simpletwitterclient.fragments.TweetsListFragment;
import com.codepath.apps.simpletwitterclient.models.Tweet;
import com.codepath.apps.simpletwitterclient.util.TwitterHelpers;
import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;
import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;


public class TwitterClient extends OAuthBaseClient {
	private static final Class<? extends Api> REST_API_CLASS = TwitterApi.class; // Change this
    private static final String REST_URL = "https://api.twitter.com/1.1"; // Change this, base API URL
    private static final String REST_CONSUMER_KEY = "NSUf8REI0YhFQmylk7iSEIPpK";       // Change this
    private static final String REST_CONSUMER_SECRET = "Wt5lykjZ3AVdYtZWIGIArRdL5XTsMoilOshPDfqH2uMf58Nayc"; // Change this
    private static final String REST_CALLBACK_URL = "oauth://SFTweetsApp"; // Change this (here and in manifest)
    private static final String TAG = TwitterClient.class.getSimpleName();
    public static final String HOME = "statuses/home_timeline.json";
    public static final String TWEET = "statuses/update.json";
    public static final String CURRENT_USER = "account/verify_credentials.json";
    public static final String MENTIONS = "statuses/mentions_timeline.json";
    public static final String USER = "statuses/user_timeline.json";
    public enum Type {GET_NEWER, GET_OLDER}

	public TwitterClient(Context context) {
		super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
	}

    /**
     *
     * @param type GET_OLDER - used when the user scrolls to the bottom of the list
     *             GET_NEWER - used when the user wants to check for new data
     * @param url
     * @param screenName
     * @param tweetsListFrag
     */
    public void getTimeline(Type type, String url, String screenName, final TweetsListFragment tweetsListFrag){
        getTimeline(1, type, url, screenName, tweetsListFrag);
    }

    /**
     *
     * @param maxId
     * @param type GET_OLDER - used when the user scrolls to the bottom of the list
     *             GET_NEWER - used when the user wants to check for new data
     * @param url
     * @param screenName
     * @param tweetsListFrag
     */
    public void getTimeline(long maxId, Type type, String url, String screenName, final TweetsListFragment tweetsListFrag){
        if (TwitterHelpers.checkForInternetConnectivity(tweetsListFrag.getActivity()))
            return;
        RequestParams params = new RequestParams();
        params.put("count", 25);
        if (maxId == 1) params.put("since_id", maxId);
        if (null != screenName) params.put("screen_name", screenName);
        if (null == type){
            getTimeline(type, url, params, tweetsListFrag);
            return;
        }
        switch (type) {
            case GET_NEWER: params.put("since_id", maxId);
                            break;
            case GET_OLDER: params.put("max_id", maxId);
        }
        getTimeline(type, url, params, tweetsListFrag);
    }

    private void getTimeline(final Type type, String url, RequestParams params, final TweetsListFragment tweetsListFrag){
        tweetsListFrag.showProgressBar();
        getClient().get(getApiUrl(url), params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                if (type != null && type == Type.GET_NEWER) {
                    tweetsListFrag.insertAll(Tweet.fromJsonArray(response));
                } else {
                    tweetsListFrag.addAll(Tweet.fromJsonArray(response));
                }
                tweetsListFrag.hideProgressBar();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e(TAG, throwable.getMessage());
                tweetsListFrag.populateTimelineFromDb();
                tweetsListFrag.hideProgressBar();
            }
        });
    }

    public void sendTweet(String tweet, final TweetsListFragment tweetsListFrag){
        if (TwitterHelpers.checkForInternetConnectivity(tweetsListFrag.getActivity()))
            return;
        if (null != tweet && tweet.trim().length() > 0) {
            String apiUrl = getApiUrl("statuses/update.json");
            RequestParams params = new RequestParams();
            params.put("status", tweet);
            Log.d(TAG, apiUrl + ", params: " + params);
            getClient().post(apiUrl, params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    tweetsListFrag.hideProgressBar();
                    tweetsListFrag.insert(Tweet.fromJson(response));
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.e(TAG, throwable.getMessage());
                    tweetsListFrag.hideProgressBar();
                    tweetsListFrag.populateTimelineFromDb();
                }
            });
        }
    }

    public void getCurrentUserInfo(AsyncHttpResponseHandler handler){
        String apiUrl = getApiUrl("account/verify_credentials.json");
        Log.d(TAG, apiUrl);
        getClient().get(apiUrl, handler);
    }

    public void getUserTimeline(String screenName, AsyncHttpResponseHandler handler){
        RequestParams params = new RequestParams();
        params.put("count", 25);
        params.put("screen_name", screenName);
        String apiUrl = getApiUrl("statuses/user_timeline.json");
        Log.d(TAG, apiUrl + ", params: " + params);
        getClient().get(apiUrl, params, handler);
    }
}