package com.quest.geotwit.twitter;

import android.os.Parcel;
import android.os.Parcelable;

import com.twitter.sdk.android.core.models.HashtagEntity;
import com.twitter.sdk.android.core.models.Tweet;

import java.util.ArrayList;
import java.util.List;

/**
 * Encapsulates the fields of a tweet.  Possible de-serialization target.
 */
public class TweetObject implements Parcelable {

    private String text;
    private double[] coordinates;
    private String createdAt;
    private String[] hashTags;
    private int favoriteCount;
    private long id;

    public TweetObject() {}

    public TweetObject(Tweet tweet) {
        text = tweet.text;
        if (tweet.coordinates != null) {
            coordinates = new double[] {tweet.coordinates.getLongitude(), tweet.coordinates.getLatitude()};
        }
        createdAt = tweet.createdAt;
        List<HashtagEntity> ht = tweet.entities.hashtags;
        List<String> tags = new ArrayList<>();
        for (HashtagEntity he : ht) {
            tags.add(he.text);
        }
        hashTags = new String[tags.size()];
        tags.toArray(hashTags);
        favoriteCount = tweet.favoriteCount;
        id = tweet.id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public double[] getCoordinates() {
        return coordinates;
    }

    public double getLongitude() {
        return coordinates == null ? -79.200 : coordinates[0];
    }

    public double getLatitude() {
        return coordinates == null ? 43.800 : coordinates[1];
    }

    public void setCoordinates(double[] coordinates) {
        this.coordinates = coordinates;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String[] getHashTags() {
        return hashTags;
    }

    public void setHashTags(String[] hashTags) {
        this.hashTags = hashTags;
    }

    public int getFavoriteCount() {
        return favoriteCount;
    }

    public void setFavoriteCount(int favoriteCount) {
        this.favoriteCount = favoriteCount;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(text);
        parcel.writeDoubleArray(coordinates);
        parcel.writeString(createdAt);
        parcel.writeStringArray(hashTags);
        parcel.writeInt(favoriteCount);
        parcel.writeLong(id);
    }

    public static final Parcelable.Creator<TweetObject> CREATOR = new Parcelable.Creator<TweetObject>() {
        public TweetObject createFromParcel(Parcel parcel) {
            return new TweetObject(parcel);
        }

        public TweetObject[] newArray(int size) {
            return new TweetObject[size];
        }
    };

    private TweetObject(Parcel parcel) {
        text = parcel.readString();
        coordinates = parcel.createDoubleArray();
        createdAt = parcel.readString();
        hashTags = parcel.createStringArray();
        favoriteCount = parcel.readInt();
        id = parcel.readLong();
    }
}
