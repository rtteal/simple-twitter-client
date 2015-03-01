package com.codepath.apps.simpletwitterclient.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
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

import java.util.ArrayList;
import java.util.List;

public abstract class TweetsListFragment extends Fragment implements TweetSendListener {
    private List<Tweet> tweets = new ArrayList<>();
    private ListView lvTweets;
    private TweetArrayAdapter adapter;
    private TwitterClient client = TwitterApplication.getRestClient();
    private static final String TAG = TweetsListFragment.class.getSimpleName();
    private ProgressBar pb;
    private SwipeRefreshLayout swipeContainer;
    String url;
    String screenName;

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
        setUpSwipeRefresh(v);
        return v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pb = (ProgressBar) getActivity().findViewById(R.id.pbLoading);
        adapter = new TweetArrayAdapter(getActivity(), tweets);
        if (null != getArguments())
            screenName = getArguments().getString("screen_name");
        populateTimeline();
    }

    @Override
    public void onTweetSend(String tweet) {
        client.sendTweet(tweet, this);
    }

    private void setUpSwipeRefresh(View v){
        swipeContainer = (SwipeRefreshLayout) v.findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadNewDataFromApi(adapter.getItem(0).uid);
                swipeContainer.setRefreshing(false);
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    public void addAll(List<Tweet> tweets){
        adapter.addAll(tweets);
    }

    public void populateTimelineFromDb(){
        adapter.clear();
        adapter.addAll(Tweet.recentItems());
    }

    public void insertAll(List<Tweet> tweets){
        for (Tweet t : tweets)
            adapter.insert(t, 0);
    }

    public void showProgressBar() {
        pb.setVisibility(ProgressBar.VISIBLE);
    }

    public void hideProgressBar() {
        pb.setVisibility(ProgressBar.INVISIBLE);
    }

    void populateTimeline() {
        client.getTimeline(null, url, screenName, this);
    }

    void customLoadMoreDataFromApi(long maxId) {
        client.getTimeline(maxId, TwitterClient.Type.GET_OLDER, url, screenName, this);
    }

    void loadNewDataFromApi(long maxId){
        client.getTimeline(maxId, TwitterClient.Type.GET_NEWER, url, screenName, this);
    }

    public void insert(Tweet tweet){
        adapter.insert(tweet, 0);
    }

    void setUrl(String url){
        this.url = url;
    }
}
