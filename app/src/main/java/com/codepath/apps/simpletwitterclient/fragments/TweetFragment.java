package com.codepath.apps.simpletwitterclient.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.codepath.apps.simpletwitterclient.R;
import com.codepath.apps.simpletwitterclient.TwitterApplication;
import com.codepath.apps.simpletwitterclient.TwitterClient;
import com.codepath.apps.simpletwitterclient.adapters.TweetArrayAdapter;
import com.codepath.apps.simpletwitterclient.models.User;
import com.squareup.picasso.Picasso;

public class TweetFragment extends DialogFragment implements TwitterClient.OnCurrentUserRequestListener{
    private EditText etTweet;
    private ImageView ivProfilePic;
    private TextView tvName;
    private TextView tvUserName;
    private static final String TWEET = " TWEET";
    private TweetArrayAdapter.OnTweetSendListener listener;
    private ProgressBar pb;

    public static TweetFragment newInstance(String screenName, String inResponseTo) {
        TweetFragment frag = new TweetFragment();
        frag.setStyle(DialogFragment.STYLE_NORMAL, R.style.Dialog);
        if (screenName != null && inResponseTo != null){
            Bundle args = new Bundle();
            args.putString("screen_name", screenName);
            args.putString("in_reply_to_status_id", inResponseTo);
            frag.setArguments(args);
        }
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tweet, container);
        ivProfilePic = (ImageView) view.findViewById(R.id.ivProfilePic);
        tvName = (TextView) view.findViewById(R.id.tvName);
        tvUserName = (TextView) view.findViewById(R.id.tvUserName);
        etTweet = (EditText) view.findViewById(R.id.etTweet);
        pb = (ProgressBar) getActivity().findViewById(R.id.pbLoading);
        showProgressBar();
        // will callback to setupCurrentUser upon completion
        TwitterApplication.getRestClient().getCurrentUserInfo(this);
        return view;
    }

    @Override
    public void setupCurrentUser(User currentUser){
        Picasso.with(getActivity()).load(currentUser.profileImage).into(ivProfilePic);
        tvName.setText(currentUser.name);
        tvUserName.setText("@" + currentUser.screenName);
        if (getArguments() != null) {
            etTweet.setText("@" + getArguments().getString("screen_name") + " ");
            etTweet.setSelection(etTweet.getText().length());
        }
        setOnClickListener();
        setTitle();
        hideProgressBar();
    }

    private void setOnClickListener(){
        etTweet.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                setTitle();
                return false;
            }
        });
        etTweet.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                final String tweet = etTweet.getText().toString();
                if (getArguments() == null) {
                    listener.onTweetSend(tweet);
                } else {
                    listener.onReplySend(tweet, getArguments().getString("in_reply_to_status_id"));
                }
                dismiss();
                return false;
            }
        });
    }

    private void setTitle(){
        getDialog().setTitle(140 - etTweet.getText().length() + TWEET);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof TweetArrayAdapter.OnTweetSendListener) {
            listener = (TweetArrayAdapter.OnTweetSendListener) activity;
        } else {
            throw new ClassCastException(activity.toString()
                    + " must implement TweetFragment.OnTweetSendListener");
        }
    }

    public void showProgressBar() {
        pb.setVisibility(ProgressBar.VISIBLE);
    }

    public void hideProgressBar() {
        pb.setVisibility(ProgressBar.INVISIBLE);
    }
}
