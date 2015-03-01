package com.codepath.apps.simpletwitterclient.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.codepath.apps.simpletwitterclient.R;
import com.codepath.apps.simpletwitterclient.TwitterApplication;
import com.codepath.apps.simpletwitterclient.TwitterClient;
import com.codepath.apps.simpletwitterclient.models.Tweet;
import com.codepath.apps.simpletwitterclient.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

public class UserheadFragment extends Fragment {
    private TwitterClient client = TwitterApplication.getRestClient();
    private static final String TAG = UserheadFragment.class.getSimpleName();
    ProgressBar pb;

    public static UserheadFragment newInstance(String screenName) {
        UserheadFragment userheadFragment = new UserheadFragment();
        Bundle args = new Bundle();
        args.putString("screen_name", screenName);
        userheadFragment.setArguments(args);
        return userheadFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_userhead, parent, false);
        String screenName = getArguments().getString("screen_name");
        client.getUserTimeline(screenName, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                User user = Tweet.fromJsonArray(response).get(0).user;
                populateProfileHeader(v, user);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e(TAG, throwable.getMessage());
            }
        });
        return v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pb = (ProgressBar) getActivity().findViewById(R.id.pbLoading);

    }

    private void populateProfileHeader(View v, User user) {
        TextView tvName = (TextView) v.findViewById(R.id.tvName);
        TextView tvScreenName = (TextView) v.findViewById(R.id.tvScreenName);
        TextView tvFollowers = (TextView) v.findViewById(R.id.tvFollowers);
        TextView tvFollowing = (TextView) v.findViewById(R.id.tvFollowing);
        TextView tvTweets = (TextView) v.findViewById(R.id.tvTweets);
        ImageView ivProfilePic = (ImageView) v.findViewById(R.id.ivProfilePic);
        ImageView ivProfileBannerUrl = (ImageView) v.findViewById(R.id.ivProfileBannerUrl);
        tvName.setText(user.name);
        tvScreenName.setText("@" + user.screenName);
        tvTweets.setText(Html.fromHtml("<b>" + getFriendlyFormat(user.tweets) + "</b><br>TWEETS"));
        tvFollowers.setText(Html.fromHtml("<b>" + getFriendlyFormat(user.followers) + "</b><br>FOLLOWERS"));
        tvFollowing.setText(Html.fromHtml("<b>" + getFriendlyFormat(user.following) + "</b><br>FOLLOWING"));
        if (!(null == user.profileBannerUrl || user.profileBannerUrl.trim().equals(""))){
            Picasso.with(v.getContext())
                    .load(user.profileBannerUrl)
                    .error(v.getContext().getResources().getDrawable(R.drawable.ic_default))
                    .into(ivProfileBannerUrl);
        } else {
            ivProfileBannerUrl.setImageResource(R.drawable.ic_default);
        }
        Picasso.with(v.getContext()).load(user.profileImage).into(ivProfilePic);
    }

    public void showProgressBar() {
        pb.setVisibility(ProgressBar.VISIBLE);
    }

    public void hideProgressBar() {
        pb.setVisibility(ProgressBar.INVISIBLE);
    }

    private String getFriendlyFormat(int num){
        if (num < 1000){
            return "" + num;
        } else if (num < 1000000){
            return num / 1000 + "K";
        } else {
            return String.format("%.3g%n", (double) num / 1000000) + "M";
        }
    }
}
