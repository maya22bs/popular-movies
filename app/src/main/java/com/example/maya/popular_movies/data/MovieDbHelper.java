package com.example.maya.popular_movies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by maya on 24/08/16.
 */
public class MovieDbHelper extends SQLiteOpenHelper {
    private static MovieDbHelper sInstance;
    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "movie.db";

    public static synchronized MovieDbHelper getInstance(Context context) {

        if (sInstance == null) {
            sInstance = new MovieDbHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    private MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_FAVORITE_MOVIES = "CREATE TABLE " + MovieContract.TABLE_NAME + " (" +

                  BaseColumns._ID +" INTEGER PRIMARY KEY AUTOINCREMENT," +
                MovieContract.COLUMN_MOVIE_ID + " TEXT UNIQUE NOT NULL," +
                MovieContract.COLUMN_TITLE + " TEXT NOT NULL, " +
                MovieContract.COLUMN_POSTERURL + " TEXT NOT NULL, " +
                MovieContract.COLUMN_DESCRIPTION + " TEXT NOT NULL, " +
                MovieContract.COLUMN_POPULARITY + " REAL NOT NULL, " +
                MovieContract.COLUMN_RATING + " REAL NOT NULL, " +
                MovieContract.COLUMN_RELEASEDATE + " TEXT NOT NULL " +
                " );";

        sqLiteDatabase.execSQL(SQL_CREATE_FAVORITE_MOVIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.TABLE_NAME);
        onCreate(sqLiteDatabase);

    }
}
