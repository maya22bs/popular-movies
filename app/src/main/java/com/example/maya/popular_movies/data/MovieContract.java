package com.example.maya.popular_movies.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;
import android.widget.BaseAdapter;

/**
 * Created by maya on 24/08/16.
 */
public class MovieContract implements BaseColumns {

    public static final String TABLE_NAME = "favorite_movies";

    public static final String COLUMN_TITLE="title";
    public static final String COLUMN_POSTERURL="poster_path";
    public static final String COLUMN_DESCRIPTION="overview";
    public static final String COLUMN_POPULARITY="popularity";
    public static final String COLUMN_RATING="vote_average";
    public static final String COLUMN_RELEASEDATE="release_date";
    public static final String COLUMN_MOVIE_ID="movieid";


    public static final String CONTENT_AUTHORITY = "com.example.maya.popular_movies";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_FAVORITE_MOVIES = "favorite_movies";


    public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORITE_MOVIES).build();

    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVORITE_MOVIES;

    public static final String[] COLUMNS={COLUMN_TITLE,COLUMN_POSTERURL,COLUMN_DESCRIPTION,COLUMN_POPULARITY,COLUMN_RATING,COLUMN_RELEASEDATE,COLUMN_MOVIE_ID};

}
