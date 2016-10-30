package com.example.android.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/**
 * Created by aaa on 2016/10/11.
 */
public class MovieProvider extends ContentProvider {
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MovieDbHelper mOpenHelper;

    static final int POPULAR = 100;
    static final int POPULAR_WITH_ID = 101;
    static final int TOP_RATED = 200;
    static final int TOP_RATED_WITH_ID = 201;
    static final int FAVORITE = 300;
    static final int FAVORITE_WITH_ID = 301;

    static UriMatcher buildUriMatcher(){
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, MovieContract.PATH_POPULAR, POPULAR);
        matcher.addURI(authority, MovieContract.PATH_POPULAR + "/#", POPULAR_WITH_ID);
        matcher.addURI(authority, MovieContract.PATH_TOP_RATED + "/#", TOP_RATED_WITH_ID);
        matcher.addURI(authority, MovieContract.PATH_TOP_RATED, TOP_RATED);
        matcher.addURI(authority, MovieContract.PATH_FAVORITE, FAVORITE);
        matcher.addURI(authority, MovieContract.PATH_FAVORITE + "/#", FAVORITE_WITH_ID);

        return matcher;
    }
    @Override
    public boolean onCreate(){
        mOpenHelper = new MovieDbHelper(getContext());
        return true;
    }
    @Override
    public String getType(Uri uri){
        final int match = sUriMatcher.match(uri);

        switch(match){
            case POPULAR:
                return MovieContract.PopularEntry.CONTENT_TYPE;
            case POPULAR_WITH_ID:
                return MovieContract.PopularEntry.CONTENT_ITEM_TYPE;
            case TOP_RATED:
                return MovieContract.TopRatedEntry.CONTENT_TYPE;
            case TOP_RATED_WITH_ID:
                return MovieContract.TopRatedEntry.CONTENT_ITEM_TYPE;
            case FAVORITE:
                return MovieContract.FavoriteEntry.CONTENT_TYPE;
            case FAVORITE_WITH_ID:
                return MovieContract.FavoriteEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

    }
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder ){
        Cursor retCursor;
        switch(sUriMatcher.match(uri)){
            case POPULAR: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.PopularEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case POPULAR_WITH_ID: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.PopularEntry.TABLE_NAME,
                        projection,
                        MovieContract.PopularEntry._ID + " = ?",
                        new String[] {String.valueOf(ContentUris.parseId(uri))},
                        null,
                        null,
                        sortOrder);
                break;
            }
            case TOP_RATED: {
                retCursor =  mOpenHelper.getReadableDatabase().query(
                        MovieContract.TopRatedEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case TOP_RATED_WITH_ID: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.TopRatedEntry.TABLE_NAME,
                        projection,
                        MovieContract.TopRatedEntry._ID + " = ?",
                        new String[] {String.valueOf(ContentUris.parseId(uri))},
                        null,
                        null,
                        sortOrder);
                break;
            }
            case FAVORITE: {
                retCursor =  mOpenHelper.getReadableDatabase().query(
                        MovieContract.FavoriteEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case FAVORITE_WITH_ID: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.FavoriteEntry.TABLE_NAME,
                        projection,
                        MovieContract.FavoriteEntry._ID + " = ?",
                        new String[] {String.valueOf(ContentUris.parseId(uri))},
                        null,
                        null,
                        sortOrder);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case POPULAR: {
                long _id = db.insert(MovieContract.PopularEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = MovieContract.PopularEntry.buildPopularUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case TOP_RATED: {
                long _id = db.insert(MovieContract.TopRatedEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = MovieContract.TopRatedEntry.buildTopRatedUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case FAVORITE: {
                long _id = db.insert(MovieContract.FavoriteEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = MovieContract.FavoriteEntry.buildFavoriteUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs){
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        if(null == selection) selection = "1";
        switch(match){
            case POPULAR:
                rowsDeleted = db.delete(
                        MovieContract.PopularEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case POPULAR_WITH_ID:
                rowsDeleted = db.delete(MovieContract.PopularEntry.TABLE_NAME,
                        MovieContract.PopularEntry._ID + " = ?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))});
                break;
            case TOP_RATED:
                rowsDeleted = db.delete(
                        MovieContract.TopRatedEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case TOP_RATED_WITH_ID:
                rowsDeleted = db.delete(MovieContract.TopRatedEntry.TABLE_NAME,
                        MovieContract.TopRatedEntry._ID + " = ?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))});
                break;
            case FAVORITE:
                rowsDeleted = db.delete(
                        MovieContract.FavoriteEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case FAVORITE_WITH_ID:
                rowsDeleted = db.delete(MovieContract.FavoriteEntry.TABLE_NAME,
                        MovieContract.FavoriteEntry._ID + " = ?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if(rowsDeleted != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }
    @Override
    public int update(
            Uri uri, ContentValues values, String selection, String[] selectionArgs){
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch(match){
            case POPULAR:
                rowsUpdated = db.update(MovieContract.PopularEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case POPULAR_WITH_ID:
                rowsUpdated = db.update(MovieContract.PopularEntry.TABLE_NAME,
                        values,
                        MovieContract.PopularEntry._ID + " = ?",
                        new String[] {String.valueOf(ContentUris.parseId(uri))});
                break;
            case TOP_RATED:
                rowsUpdated = db.update(MovieContract.TopRatedEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case TOP_RATED_WITH_ID:
                rowsUpdated = db.update(MovieContract.TopRatedEntry.TABLE_NAME,
                        values,
                        MovieContract.TopRatedEntry._ID + " = ?",
                        new String[] {String.valueOf(ContentUris.parseId(uri))});
                break;
            case FAVORITE:
                rowsUpdated = db.update(MovieContract.FavoriteEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case FAVORITE_WITH_ID:
                rowsUpdated = db.update(MovieContract.FavoriteEntry.TABLE_NAME,
                        values,
                        MovieContract.FavoriteEntry._ID + " = ?",
                        new String[] {String.valueOf(ContentUris.parseId(uri))});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if(rowsUpdated != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }
    @Override
    public int bulkInsert(Uri uri, ContentValues[] values){
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        String tableName;

        switch(match){
            case POPULAR:
                tableName = MovieContract.PopularEntry.TABLE_NAME;
                break;
            case TOP_RATED:
                tableName = MovieContract.TopRatedEntry.TABLE_NAME;
                break;
            case FAVORITE:
                tableName = MovieContract.FavoriteEntry.TABLE_NAME;
                break;
            default:
                return super.bulkInsert(uri, values);
        }
        db.beginTransaction();
        int returnCount = 0;
        try{
            for(ContentValues value : values){
                long _id = db.insert(tableName, null, value);
                if(_id != -1){
                    returnCount++;
                }
            }
            db.setTransactionSuccessful();
        }finally{
            db.endTransaction();
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnCount;
    }




}
