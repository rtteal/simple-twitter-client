package com.codepath.apps.simpletwitterclient.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.codepath.apps.simpletwitterclient.R;
import com.codepath.apps.simpletwitterclient.TwitterApplication;
import com.codepath.apps.simpletwitterclient.TwitterClient;
import com.codepath.apps.simpletwitterclient.adapters.TweetArrayAdapter;
import com.codepath.apps.simpletwitterclient.fragments.TweetFragment.TweetSendListener;
import com.codepath.apps.simpletwitterclient.listeners.EndlessScrollListener;
import com.codepath.apps.simpletwitterclient.models.Tweet;
import com.codepath.apps.simpletwitterclient.util.TwitterHelpers;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public abstract class TweetsListFragment extends Fragment implements TweetSendListener {
    private List<Tweet> tweets = new ArrayList<>();
    private ListView lvTweets;
    private TweetArrayAdapter adapter;
    private TwitterClient client = TwitterApplication.getRestClient();
    private static final String TAG = TweetsListFragment.class.getSimpleName();
    ProgressBar pb;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tweets_list, parent, false);
        lvTweets = (ListView) v.findViewById(R.id.lvTweets);
        lvTweets.setAdapter(adapter);
        lvTweets.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                customLoadMoreDataFromApi(adapter.getItem(adapter.getCount()-1).uid - 26);
            }
        });
        return v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pb = (ProgressBar) getActivity().findViewById(R.id.pbLoading);
        adapter = new TweetArrayAdapter(getActivity(), tweets);
        populateTimeline();
    }

    @Override
    public void onTweetSend(String tweet) {
        if (!TwitterHelpers.checkForInternetConnectivity(getActivity())) {
            if (null != tweet && tweet.trim().length() > 0) {
                showProgressBar();
                client.sendTweet(tweet, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        hideProgressBar();
                        adapter.insert(Tweet.fromJson(response), 0);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        Log.e(TAG, throwable.getMessage());
                        hideProgressBar();
                        populateTimelineFromDb();
                    }
                });
            }
        }
    }

    public void addAll(List<Tweet> tweets){
        adapter.addAll(tweets);
    }

    void populateTimelineFromDb(){
        adapter.clear();
        addAll(Tweet.recentItems());
    }

    public void showProgressBar() {
        pb.setVisibility(ProgressBar.VISIBLE);
    }

    public void hideProgressBar() {
        pb.setVisibility(ProgressBar.INVISIBLE);
    }

    abstract void customLoadMoreDataFromApi(long offset);

    abstract void populateTimeline();
}
