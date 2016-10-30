package com.example.android.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.android.popularmovies.data.MovieContract.PopularEntry;
import com.example.android.popularmovies.data.MovieContract.TopRatedEntry;
import com.example.android.popularmovies.data.MovieContract.FavoriteEntry;

/**
 * Created by aaa on 2016/10/08.
 */
public class MovieDbHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    static final String DATABASE_NAME = "movie.db";
    public MovieDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase){
        final String SQL_CREATE_POPULAR_TABLE = "CREATE TABLE " + PopularEntry.TABLE_NAME + " (" +
                PopularEntry._ID + " INTEGER PRIMARY KEY, " +
                PopularEntry.COLUMN_MOVIE_ID + " INTEGER UNIQUE NOT NULL, " +
                PopularEntry.COLUMN_TITLE + " TEXT NOT NULL, "+
                PopularEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                PopularEntry.COLUMN_YEAR + " TEXT NOT NULL, "+
                PopularEntry.COLUMN_VOTE_AVERAGE + " REAL NOT NULL, " +
                PopularEntry.COLUMN_THUMBNAIL + " TEXT NOT NULL "+
                " );";
        final String SQL_CREATE_TOP_RATED_TABLE = "CREATE TABLE " + TopRatedEntry.TABLE_NAME + " (" +
                TopRatedEntry._ID + " INTEGER PRIMARY KEY, " +
                TopRatedEntry.COLUMN_MOVIE_ID + " INTEGER UNIQUE NOT NULL, " +
                TopRatedEntry.COLUMN_TITLE + " TEXT NOT NULL, "+
                TopRatedEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                TopRatedEntry.COLUMN_YEAR + " TEXT NOT NULL, "+
                TopRatedEntry.COLUMN_VOTE_AVERAGE + " REAL NOT NULL, " +
                TopRatedEntry.COLUMN_THUMBNAIL + " TEXT NOT NULL "+
                " );";
        final String SQL_CREATE_FAVORITE_TABLE = "CREATE TABLE " + FavoriteEntry.TABLE_NAME + " (" +
                FavoriteEntry._ID + " INTEGER PRIMARY KEY, " +
                FavoriteEntry.COLUMN_MOVIE_ID + " INTEGER UNIQUE NOT NULL, " +
                FavoriteEntry.COLUMN_TITLE + " TEXT NOT NULL, "+
                FavoriteEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                FavoriteEntry.COLUMN_YEAR + " TEXT NOT NULL, "+
                FavoriteEntry.COLUMN_VOTE_AVERAGE + " REAL NOT NULL, " +
                FavoriteEntry.COLUMN_THUMBNAIL + " TEXT NOT NULL "+
                " );";
        sqLiteDatabase.execSQL(SQL_CREATE_POPULAR_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_TOP_RATED_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_FAVORITE_TABLE);
    }
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        // If you want to update the schema without wiping data, commenting out the next 2 lines
        // should be your top priority before modifying this method.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + PopularEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TopRatedEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FavoriteEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

}
















