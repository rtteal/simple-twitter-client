package com.codepath.apps.simpletwitterclient.fragments;

import android.os.Bundle;
import android.util.Log;

import com.codepath.apps.simpletwitterclient.TwitterApplication;
import com.codepath.apps.simpletwitterclient.TwitterClient;
import com.codepath.apps.simpletwitterclient.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * This class displays the tweets from this user.
 */
public class UserTimelineFragment extends TweetsListFragment{
    private TwitterClient client = TwitterApplication.getRestClient();
    private static final String TAG = UserTimelineFragment.class.getSimpleName();

    public static UserTimelineFragment newInstance(String screenName) {
        UserTimelineFragment userTimelineFragment = new UserTimelineFragment();
        Bundle args = new Bundle();
        args.putString("screen_name", screenName);
        userTimelineFragment.setArguments(args);
        return userTimelineFragment;
    }

    @Override
    void populateTimeline() {
        client.getUserTimeline(getArguments().getString("screen_name"), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                addAll(Tweet.fromJsonArray(response));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d(TAG, throwable.getMessage());
                populateTimelineFromDb();
            }
        });
    }

    @Override
    void customLoadMoreDataFromApi(long offset) {
        client.getUserTimeline(offset, getArguments().getString("screen_name"), new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                addAll(Tweet.fromJsonArray(response));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e(TAG, throwable.getMessage());
                populateTimelineFromDb();
            }
        });
    }
}

