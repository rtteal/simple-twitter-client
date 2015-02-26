package com.codepath.apps.simpletwitterclient.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

@Table(name = "Users")
public class User extends Model {
    @Column(name = "name")
    public String name;

    @Column(name = "screen_name")
    public String screenName;

    @Column(name = "profile_image")
    public String profileImage;

    @Column(name = "tag_line")
    public String tagLine;

    @Column(name = "followers")
    public int followers;

    @Column(name = "following")
    public int following;

    @Column(name = "remote_id",
            unique = true,
            onUpdate = Column.ForeignKeyAction.CASCADE,
            onDelete = Column.ForeignKeyAction.CASCADE)
    public long uid;

    public User(){
    }

    public User(String name, String screenName, String profileImage, long uid,
                String tagLine, int followers, int following) {
        this.name = name;
        this.screenName = screenName;
        this.profileImage = profileImage;
        this.uid = uid;
        this.tagLine = tagLine;
        this.followers = followers;
        this.following = following;
    }

    public static User fromJson(JSONObject json){
        try {
            String name = json.getString("name");
            long id = json.getLong("id");
            String screenName = json.getString("screen_name");
            String profileImage = json.getString("profile_image_url");
            String tagLine = json.getString("description");
            int followers = json.getInt("followers_count");
            int following = json.getInt("friends_count");
            User user = new User(name, screenName, profileImage, id, tagLine, followers, following);
            User persistedUser = getById(id);
            if (null != persistedUser) {
                return persistedUser;
            }
            user.save();
            return user;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String toString(){
        return String.format("{id: %s, screenName: %s, name: %s, profileImage: %s," +
                " tagLine: %s, followers: %s, following: %s}", uid, screenName, name, profileImage, tagLine, followers, following);
    }

    public List<Tweet> items() {
        return getMany(Tweet.class, "User");
    }

    public static User getById(long id){
        return new Select().from(User.class).where("remote_id = ?", id).executeSingle();
    }
}
