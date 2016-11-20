package com.example.android.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by aaa on 2016/11/12.
 */

public class Review implements Parcelable {
    int mMovieId;
    String mAuthor;
    String mContent;
    private Review(Parcel in){
        this.mMovieId = in.readInt();
        this.mAuthor = in.readString();
        this.mContent = in.readString();
    }
    @Override
    public int describeContents(){
        return 0;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags){
        dest.writeInt(this.mMovieId);
        dest.writeString(this.mAuthor);
        dest.writeString(this.mContent);
    }
    public final Parcelable.Creator<Review> CREATOR = new
            Parcelable.Creator<Review>(){
                @Override
                public Review createFromParcel(Parcel parcel){
                    return new Review(parcel);
                }
                @Override
                public Review[] newArray(int i){
                    return new Review[i];
                }
            };
    public Review(int movieId, String youtubeKey, String name){
        this.mMovieId = movieId;
        this.mAuthor = youtubeKey;
        this.mContent = name;
    }
}
