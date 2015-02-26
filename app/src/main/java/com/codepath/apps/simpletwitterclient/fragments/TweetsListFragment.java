package com.codepath.apps.simpletwitterclient.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;

import com.codepath.apps.simpletwitterclient.R;
import com.codepath.apps.simpletwitterclient.adapters.TweetArrayAdapter;
import com.codepath.apps.simpletwitterclient.models.Tweet;
import com.codepath.apps.simpletwitterclient.models.User;

import java.util.ArrayList;
import java.util.List;

public class TweetsListFragment extends Fragment {
    List<Tweet> tweets = new ArrayList<>();
    private ListView lvTweets;
    TweetArrayAdapter adapter;
    User currentUser = new User();

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tweets_list, parent, false);
        lvTweets = (ListView) v.findViewById(R.id.lvTweets);
        lvTweets.setAdapter(adapter);
        return v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new TweetArrayAdapter(getActivity(), tweets);
    }

    public void addAll(List<Tweet> tweets){
        adapter.addAll(tweets);
    }

    public void clear(){
        adapter.clear();
    }

    public void insert(Tweet tweet, int position){
        adapter.insert(tweet, position);
    }

    public void notifyDataSetChanged(){
        adapter.notifyDataSetChanged();
    }

    public void setOnScrollListener(OnScrollListener onScrollListener){
        lvTweets.setOnScrollListener(onScrollListener);
    }
}
