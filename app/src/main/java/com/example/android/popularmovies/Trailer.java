package com.example.android.popularmovies;


import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by aaa on 2016/11/09.
 */

public class Trailer implements Parcelable {
    int mMovieId;
    String mYoutubeKey;
    String mName;
    private Trailer(Parcel in){
        this.mMovieId = in.readInt();
        this.mYoutubeKey = in.readString();
        this.mName = in.readString();
    }
    @Override
    public int describeContents(){
        return 0;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags){
        dest.writeInt(this.mMovieId);
        dest.writeString(this.mYoutubeKey);
        dest.writeString(this.mName);
    }
    public final Parcelable.Creator<Trailer> CREATOR = new
            Parcelable.Creator<Trailer>(){
                @Override
                public Trailer createFromParcel(Parcel parcel){
                    return new Trailer(parcel);
                }
                @Override
                public Trailer[] newArray(int i){
                    return new Trailer[i];
                }
    };
    public Trailer(int movieId, String youtubeKey, String name){
        this.mMovieId = movieId;
        this.mYoutubeKey = youtubeKey;
        this.mName = name;
    }
}
