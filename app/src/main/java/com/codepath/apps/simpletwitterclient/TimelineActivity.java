package com.codepath.apps.simpletwitterclient;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.astuetz.PagerSlidingTabStrip;
import com.codepath.apps.simpletwitterclient.fragments.HomeTimelineFragment;
import com.codepath.apps.simpletwitterclient.fragments.MentionsTimelineFragment;
import com.codepath.apps.simpletwitterclient.fragments.TweetFragment;
import com.codepath.apps.simpletwitterclient.fragments.TweetFragment.TweetSendListener;
import com.codepath.apps.simpletwitterclient.models.User;
import com.codepath.apps.simpletwitterclient.util.TwitterHelpers;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONObject;

public class TimelineActivity extends ActionBarActivity implements TweetSendListener {
    private ViewPager vpPager;
    private User currentUser;
    private HomeTimelineFragment homeTimelineFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        fetchCurrentUser();
        vpPager = (ViewPager) findViewById(R.id.viewpager);
        TweetsPagerAdapter adapter = new TweetsPagerAdapter(getSupportFragmentManager());
        vpPager.setAdapter(adapter);
        homeTimelineFragment = adapter.homeTimelineFragment;
        PagerSlidingTabStrip tabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabStrip.setViewPager(vpPager);
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

    private void fetchCurrentUser(){
        TwitterHelpers.checkForInternetConnectivity(this);
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

    @Override
    public void onTweetSend(String tweet) {
        homeTimelineFragment.onTweetSend(tweet);
    }

    public void onProfileView(MenuItem item) {
        Intent i = new Intent(this, ProfileActivity.class);
        startActivity(i);
    }

    public class TweetsPagerAdapter extends FragmentPagerAdapter{
        private final String[] tabTitles = {"Home", "Mentions"};
        HomeTimelineFragment homeTimelineFragment = new HomeTimelineFragment();

        public TweetsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0){
                return homeTimelineFragment;
            } else if (position == 1) {
                return new MentionsTimelineFragment();
            }
            Log.e("Error", "TweetsPagerAdapter.getItem returned the default");
            return new HomeTimelineFragment(); // fail-safe, shouldn't happen
        }

        @Override
        public int getCount() {
            return tabTitles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }
    }
}
