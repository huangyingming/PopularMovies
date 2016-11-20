package com.example.android.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.popularmovies.data.MovieContract.FavoriteEntry;
import com.example.android.popularmovies.data.MovieContract.MovieEntry;
import com.example.android.popularmovies.data.MovieContract.ReviewEntry;
import com.example.android.popularmovies.data.MovieContract.TrailerEntry;

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
        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +
                MovieEntry._ID + " INTEGER PRIMARY KEY, " +
                MovieEntry.COLUMN_MOVIE_ID + " INTEGER UNIQUE NOT NULL, " +
                MovieEntry.COLUMN_TITLE + " TEXT NOT NULL, "+
                MovieEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_YEAR + " TEXT NOT NULL, "+
                MovieEntry.COLUMN_VOTE_AVERAGE + " REAL NOT NULL, " +
                MovieEntry.COLUMN_THUMBNAIL + " TEXT NOT NULL "+
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

        final String SQL_CREATE_TRAILER_TABLE = "CREATE TABLE " + TrailerEntry.TABLE_NAME + " (" +
                TrailerEntry._ID + " INTEGER PRIMARY KEY, " +
                TrailerEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                TrailerEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                TrailerEntry.COLUMN_YOUTUBE_KEY + " TEXT UNIQUE NOT NULL, " +
                " FOREIGN KEY (" +  TrailerEntry.COLUMN_MOVIE_ID + ") REFERENCES " +
                MovieEntry.TABLE_NAME + " (" + MovieEntry.COLUMN_MOVIE_ID + "));";


        final String SQL_CREATE_REVIEW_TABLE = "CREATE TABLE " + ReviewEntry.TABLE_NAME + " (" +
                ReviewEntry._ID + " INTEGER PRIMARY KEY, "+
                ReviewEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                ReviewEntry.COLUMN_AUTHOR + " TEXT NOT NULL, " +
                ReviewEntry.COLUMN_CONTENT + " TEXT NOT NULL, " +
                " FOREIGN KEY (" +  ReviewEntry.COLUMN_MOVIE_ID + ") REFERENCES " +
                MovieEntry.TABLE_NAME + " (" + MovieEntry.COLUMN_MOVIE_ID + "), " +
                " UNIQUE (" + ReviewEntry.COLUMN_MOVIE_ID + ", " +
                ReviewEntry.COLUMN_AUTHOR + ", " +
                ReviewEntry.COLUMN_CONTENT + ") ON CONFLICT REPLACE);";


        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_FAVORITE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_REVIEW_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_TRAILER_TABLE);
    }
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        // If you want to update the schema without wiping data, commenting out the next 2 lines
        // should be your top priority before modifying this method.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FavoriteEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TrailerEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ReviewEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

}
















