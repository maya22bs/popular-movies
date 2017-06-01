package com.example.maya.popular_movies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by maya on 16/08/16.
 */
public class Movie implements Parcelable {

    public String title;
    public String poster_path;
    public String overview;
    public double popularity;
    public double vote_average;//rating
    public String release_date;
    public String id;

    public Movie() {
    };

    public Movie(Parcel source) {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

    }

    public static final Parcelable.Creator<Movie> CREATOR =
            new Parcelable.Creator<Movie>(){
                @Override
                public Movie createFromParcel(Parcel source) {
                    return new Movie(source);
                }

                @Override
                public Movie[] newArray(int size) {
                    return new Movie[size];
                }
            };

}
