package com.example.maya.popular_movies.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.maya.popular_movies.service.MovieService;

/**
 * Created by maya on 25/08/16.
 */

public class MovieProvider extends ContentProvider {

    private MovieDbHelper mOpenHelper;
    static final int FAVORITE_MOVIES = 100;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private final String LOG_TAG = MovieService.class.getSimpleName();

    @Override
    public boolean onCreate() {
        mOpenHelper = MovieDbHelper.getInstance(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] strings, String s, String[] strings1, String s1) {

        Cursor retCursor=null;
        switch (sUriMatcher.match(uri)) {
            case FAVORITE_MOVIES:
            {
                retCursor = mOpenHelper.getReadableDatabase().query(MovieContract.TABLE_NAME, strings, s,strings1,null,null, s1);

                break;
            }
        }
        return retCursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {

        return MovieContract.CONTENT_TYPE;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri=null;

        switch (match) {
            case FAVORITE_MOVIES: {
                long _id = db.insert(MovieContract.TABLE_NAME, null, contentValues);
                if (_id > 0)
                    returnUri = uri;
                else
                Log.d(LOG_TAG,"Failed to insert row into " + uri);
                break;
            }
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }


    public static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, MovieContract.PATH_FAVORITE_MOVIES, FAVORITE_MOVIES);
        return matcher;
    }
}
