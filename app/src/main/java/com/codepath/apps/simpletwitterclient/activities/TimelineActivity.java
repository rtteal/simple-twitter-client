package com.codepath.apps.simpletwitterclient.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;

import com.astuetz.PagerSlidingTabStrip;
import com.codepath.apps.simpletwitterclient.R;
import com.codepath.apps.simpletwitterclient.TwitterApplication;
import com.codepath.apps.simpletwitterclient.TwitterClient.OnCurrentUserRequestListener;
import com.codepath.apps.simpletwitterclient.adapters.SmartFragmentStatePagerAdapter;
import com.codepath.apps.simpletwitterclient.adapters.TweetArrayAdapter;
import com.codepath.apps.simpletwitterclient.fragments.HomeTimelineFragment;
import com.codepath.apps.simpletwitterclient.fragments.MentionsTimelineFragment;
import com.codepath.apps.simpletwitterclient.fragments.TweetFragment;
import com.codepath.apps.simpletwitterclient.adapters.TweetArrayAdapter.OnTweetSendListener;
import com.codepath.apps.simpletwitterclient.models.User;

public class TimelineActivity extends ActionBarActivity implements OnTweetSendListener, TweetArrayAdapter.OnTweetClickListener,
        OnCurrentUserRequestListener {
    private static final String TAG = TimelineActivity.class.getSimpleName();
    private ViewPager vpPager;
    private TweetsPagerAdapter tweetsPagerAdapter;
    private ProgressBar pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        pb = (ProgressBar) findViewById(R.id.pbLoading);
        vpPager = (ViewPager) findViewById(R.id.viewpager);
        tweetsPagerAdapter = new TweetsPagerAdapter(getSupportFragmentManager());
        vpPager.setAdapter(tweetsPagerAdapter);
        PagerSlidingTabStrip tabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabStrip.setViewPager(vpPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
        return true;
    }

    public void onComposeTweet(MenuItem item){
        TweetFragment tweetFragment = TweetFragment.newInstance(null, null);
        tweetFragment.show(getSupportFragmentManager(), "fragment_tweet");
    }

    @Override
    public void onTweetSend(String tweet) {
        ((OnTweetSendListener) tweetsPagerAdapter.getRegisteredFragment(0)).onTweetSend(tweet);
        vpPager.setCurrentItem(0, true);
    }

    public void onProfileView(MenuItem item) {
        // will callback to setupCurrentUser upon completion
        TwitterApplication.getRestClient().getCurrentUserInfo(this);
    }

    @Override
    public void setupCurrentUser(User currentUser) {
        onProfileClick(currentUser.screenName);
    }

    @Override
    public void onProfileClick(String screenName) {
        showProgressBar();
        Intent i = new Intent(this, ProfileActivity.class);
        i.putExtra("screen_name", screenName);
        startActivity(i);
        hideProgressBar();
    }

    public void showProgressBar() {
        pb.setVisibility(ProgressBar.VISIBLE);
    }

    public void hideProgressBar() {
        pb.setVisibility(ProgressBar.INVISIBLE);
    }

    @Override
    public void onReplyClick(String screenName, String inResponseTo) {
        TweetFragment tweetFragment = TweetFragment.newInstance(screenName, inResponseTo);
        tweetFragment.show(getSupportFragmentManager(), "fragment_tweet");
    }

    @Override
    public void onReplySend(String tweet, String inResponseTo) {
        ((OnTweetSendListener) tweetsPagerAdapter.getRegisteredFragment(0)).onReplySend(tweet, inResponseTo);
        vpPager.setCurrentItem(0, true);
    }

    public class TweetsPagerAdapter extends SmartFragmentStatePagerAdapter {
        private final String[] tabTitles = {"Home", "Mentions"};

        public TweetsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0){
                return new HomeTimelineFragment();
            } else if (position == 1) {
                return new MentionsTimelineFragment();
            }
            Log.e(TAG, "TweetsPagerAdapter.getItem returned the default");
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
