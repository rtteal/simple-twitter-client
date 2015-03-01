package com.codepath.apps.simpletwitterclient.fragments;

import android.os.Bundle;

import com.codepath.apps.simpletwitterclient.TwitterClient;

/**
 * This class displays the tweets from this user.
 */
public class UserTimelineFragment extends TweetsListFragment{

    public UserTimelineFragment(){
        setUrl(TwitterClient.USER);
    }

    public static UserTimelineFragment newInstance(String screenName) {
        UserTimelineFragment userTimelineFragment = new UserTimelineFragment();
        Bundle args = new Bundle();
        args.putString("screen_name", screenName);
        userTimelineFragment.setArguments(args);
        return userTimelineFragment;
    }
}

