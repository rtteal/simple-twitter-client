package com.codepath.apps.simpletwitterclient;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.codepath.apps.simpletwitterclient.adapters.TweetArrayAdapter;
import com.codepath.apps.simpletwitterclient.fragments.TweetFragment;
import com.codepath.apps.simpletwitterclient.listeners.EndlessScrollListener;
import com.codepath.apps.simpletwitterclient.models.Tweet;
import com.codepath.apps.simpletwitterclient.models.User;
import com.codepath.apps.simpletwitterclient.util.TwitterHelpers;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TimelineActivity extends ActionBarActivity implements TweetFragment.TweetSendListener{
    private TwitterClient client;
    private List<Tweet> tweets = new ArrayList<>();
    private ListView lvTweets;
    private TweetArrayAdapter adapter;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        client = TwitterApplication.getRestClient();
        fetchCurrentUser();
        lvTweets = (ListView) findViewById(R.id.lvTweets);
        adapter = new TweetArrayAdapter(this, tweets);
        lvTweets.setAdapter(adapter);
        lvTweets.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to your AdapterView
                customLoadMoreDataFromApi(adapter.getItem(adapter.getCount()-1).uid - 26);
                // or customLoadMoreDataFromApi(totalItemsCount);
            }
        });
        populateTimeline();
    }

    private void populateTimeline() {
        client.getHomeTimeline(new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                adapter.addAll(Tweet.fromJsonArray(response));
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e("ERROR", throwable.getMessage());
                populateTimelineFromDb();
            }
        });
    }

    private void populateTimelineFromDb(){
        adapter.clear();
        adapter.addAll(Tweet.recentItems());
    }

    private void customLoadMoreDataFromApi(long offset) {
        RequestParams params = new RequestParams();
        params.put("count", 25);
        params.put("max_id", offset);
        client.getHomeTimeline(params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                adapter.addAll(Tweet.fromJsonArray(response));
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e("ERROR", throwable.getMessage());
                populateTimelineFromDb();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_compose_tweet) {
            TweetFragment tweetFragment = TweetFragment.newInstance(currentUser);
            tweetFragment.show(getSupportFragmentManager(), "fragment_tweet");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTweetSend(String tweet) {
        if (!TwitterHelpers.checkForInternetConnectivity(this)) {
            if (null != tweet && tweet.trim().length() > 0) {
                client.sendTweet(tweet, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        tweets.add(0, Tweet.fromJson(response));
                        adapter.notifyDataSetChanged();
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

    private void fetchCurrentUser(){
        TwitterHelpers.checkForInternetConnectivity(this);
        client.getCurrentUserInfo(new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                currentUser = User.fromJson(response);
                Log.d("Taylor", "user created: " + currentUser.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e("ERROR", throwable.getMessage());
            }
        });
    }
}
