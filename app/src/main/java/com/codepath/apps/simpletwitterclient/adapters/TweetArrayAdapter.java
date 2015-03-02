package com.codepath.apps.simpletwitterclient.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.simpletwitterclient.R;
import com.codepath.apps.simpletwitterclient.models.Tweet;
import com.squareup.picasso.Picasso;

import java.util.List;

public class TweetArrayAdapter extends ArrayAdapter<Tweet> {
    private static final String TAG = TweetArrayAdapter.class.getSimpleName();

    public interface OnTweetClickListener {
        void onProfileClick(String screenName);
        void onReplyClick(String screenName, String inResponseTo);
    }

    public interface OnTweetSendListener {
        void onTweetSend(String tweet);
        void onReplySend(String screenName, String inResponseTo);
    }

    public TweetArrayAdapter(Context context, List<Tweet> objects) {
        super(context, android.R.layout.simple_list_item_1, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Tweet tweet = getItem(position);
        ViewHolder viewHolder;
        if (null == convertView){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_tweet, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.ivProfilePic = (ImageView) convertView.findViewById(R.id.ivProfilePic);
            viewHolder.tvUserName = (TextView) convertView.findViewById(R.id.tvUserName);
            viewHolder.tvScreenName = (TextView) convertView.findViewById(R.id.tvScreenName);
            viewHolder.tvBody = (TextView) convertView.findViewById(R.id.tvBody);
            viewHolder.tvRelativeTime = (TextView) convertView.findViewById(R.id.tvRelativeTime);
            viewHolder.ivReply = (ImageView) convertView.findViewById(R.id.ivReply);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.tvUserName.setText(tweet.user.name);
        viewHolder.tvBody.setText(tweet.body);
        viewHolder.tvRelativeTime.setText(tweet.getCreatedAt());
        viewHolder.tvScreenName.setText("@" + tweet.user.screenName);
        Picasso.with(getContext()).load(tweet.user.profileImage).into(viewHolder.ivProfilePic);
        viewHolder.ivProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getContext() instanceof OnTweetClickListener){
                    ((OnTweetClickListener) getContext()).onProfileClick(tweet.user.screenName);
                } else {
                    throw new ClassCastException(getContext().toString()
                            + " must implement OnProfileClickListener");
                }
            }
        });
        viewHolder.ivReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getContext() instanceof OnTweetClickListener){
                    ((OnTweetClickListener) getContext()).onReplyClick(tweet.user.screenName, "" + tweet.uid);
                } else {
                    throw new ClassCastException(getContext().toString()
                            + " must implement OnReplyClickListener");
                }
            }
        });
        return convertView;
    }

    private static class ViewHolder{
        private ImageView ivProfilePic;
        private TextView tvUserName;
        private TextView tvScreenName;
        private TextView tvBody;
        private TextView tvRelativeTime;
        private ImageView ivReply;
    }

}
