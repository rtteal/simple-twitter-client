package com.codepath.apps.simpletwitterclient.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;

import com.codepath.apps.simpletwitterclient.R;
import com.codepath.apps.simpletwitterclient.TwitterApplication;
import com.codepath.apps.simpletwitterclient.TwitterClient;
import com.codepath.apps.simpletwitterclient.fragments.UserTimelineFragment;
import com.codepath.apps.simpletwitterclient.fragments.UserheadFragment;

public class ProfileActivity extends ActionBarActivity {
    private static final String TAG = ProfileActivity.class.getSimpleName();
    private TwitterClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        client = TwitterApplication.getRestClient();
        String screenName = getIntent().getStringExtra("screen_name");
        getSupportActionBar().setTitle("Profile");
        if (null == savedInstanceState){
            UserheadFragment fragmentUserhead = UserheadFragment.newInstance(screenName);
            UserTimelineFragment fragmentUserTimeline = UserTimelineFragment.newInstance(screenName);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.flHeader, fragmentUserhead);
            ft.replace(R.id.flContainer, fragmentUserTimeline);
            ft.commit();
        }
    }

}
