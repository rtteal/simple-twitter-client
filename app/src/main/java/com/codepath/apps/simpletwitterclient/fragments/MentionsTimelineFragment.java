package com.codepath.apps.simpletwitterclient.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.apps.simpletwitterclient.R;
import com.codepath.apps.simpletwitterclient.TwitterApplication;
import com.codepath.apps.simpletwitterclient.TwitterClient;
import com.codepath.apps.simpletwitterclient.listeners.EndlessScrollListener;
import com.codepath.apps.simpletwitterclient.fragments.TweetFragment.TweetSendListener;
import com.codepath.apps.simpletwitterclient.models.Tweet;
import com.codepath.apps.simpletwitterclient.models.User;
import com.codepath.apps.simpletwitterclient.util.TwitterHelpers;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

public class MentionsTimelineFragment extends TweetsListFragment implements TweetSendListener {
    private TwitterClient client;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("Taylor", "MentionsTimelineFragment created");
        client = TwitterApplication.getRestClient();
        //fetchCurrentUser();
        populateTimeline();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, parent, savedInstanceState);
        setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to your AdapterView
                customLoadMoreDataFromApi(adapter.getItem(adapter.getCount()-1).uid - 26);
                // or customLoadMoreDataFromApi(totalItemsCount);
            }
        });
        return v;
    }

    private void populateTimeline() {
        client.getMentionsTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.d("Taylor", "fetching mentions");
                addAll(Tweet.fromJsonArray(response));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e("ERROR", throwable.getMessage());
                populateTimelineFromDb();
            }
        });
    }

    private void populateTimelineFromDb(){
        clear();
        addAll(Tweet.recentItems());
    }

    private void customLoadMoreDataFromApi(long offset) {
        RequestParams params = new RequestParams();
        params.put("count", 25);
        params.put("max_id", offset);
        client.getMentionsTimeline(params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                addAll(Tweet.fromJsonArray(response));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e("ERROR", throwable.getMessage());
                populateTimelineFromDb();
            }
        });
    }

    @Override
    public void onTweetSend(String tweet) {
        if (!TwitterHelpers.checkForInternetConnectivity(getActivity())) {
            if (null != tweet && tweet.trim().length() > 0) {
                client.sendTweet(tweet, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        tweets.add(0, Tweet.fromJson(response));
                        notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        Log.e("ERROR", throwable.getMessage());
                        populateTimelineFromDb();
                    }
                });
            }
        }
    }


/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.menu_timeline, menu);
        return true;
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_compose_tweet) {
            //TweetFragment tweetFragment = TweetFragment.newInstance(currentUser);
            //tweetFragment.show(getSupportFragmentManager(), "fragment_tweet");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    private void fetchCurrentUser(){
        TwitterHelpers.checkForInternetConnectivity(getActivity());
        TwitterApplication.getRestClient().getCurrentUserInfo(new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                currentUser = User.fromJson(response);
                Log.d("Taylor", "user created: " + currentUser);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e("ERROR", throwable.getMessage());
            }
        });
    }

}
