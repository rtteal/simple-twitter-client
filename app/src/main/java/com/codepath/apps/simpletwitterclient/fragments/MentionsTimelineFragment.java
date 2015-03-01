package com.codepath.apps.simpletwitterclient.fragments;

import com.codepath.apps.simpletwitterclient.TwitterClient;

public class MentionsTimelineFragment extends TweetsListFragment {

    public MentionsTimelineFragment(){
        setUrl(TwitterClient.MENTIONS);
    }
}
