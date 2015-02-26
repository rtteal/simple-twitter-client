package com.codepath.apps.simpletwitterclient.adapters;

import android.content.Context;
import android.util.Log;
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
    private final Context context;

    public interface OnProfileClickListener{
        void onProfileClick(String screenName);
    }

    public TweetArrayAdapter(Context context, List<Tweet> objects) {
        super(context, android.R.layout.simple_list_item_1, objects);
        this.context = context;
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
                Log.d("clickdebug1", "screenname:" + tweet.user.screenName);
                if (context instanceof OnProfileClickListener){
                    Log.d("clickdebug1.1", "screenname:" + tweet.user.screenName);
                    ((OnProfileClickListener) context).onProfileClick(tweet.user.screenName);
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
    }

}
