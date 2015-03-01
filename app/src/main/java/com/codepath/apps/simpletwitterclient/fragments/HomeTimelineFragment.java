package com.codepath.apps.simpletwitterclient.fragments;

import com.codepath.apps.simpletwitterclient.TwitterClient;

public class HomeTimelineFragment extends TweetsListFragment{

    public HomeTimelineFragment(){
        setUrl(TwitterClient.HOME);
    }
}
