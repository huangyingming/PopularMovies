package com.example.android.popularmovies;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.android.popularmovies.data.MovieContract;
import com.example.android.popularmovies.sync.PopularMoviesSyncAdapter;

import static com.example.android.popularmovies.R.id.gridview;

/**
 * Created by aaa on 2016/08/17.
 */
public class ThumbnailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    private static final int MOVIE_LOADER = 0;

    private static final String[] MOVIE_COLUMNS = {
            MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_MOVIE_ID,
            MovieContract.MovieEntry.COLUMN_TITLE,
            MovieContract.MovieEntry.COLUMN_OVERVIEW,
            MovieContract.MovieEntry.COLUMN_YEAR,
            MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE,
            MovieContract.MovieEntry.COLUMN_THUMBNAIL
    };
    static final int COL_ID = 0;
    static final int COL_MOVIE_ID = 1;
    static final int COL_TITLE = 2;
    static final int COL_OVERVIEW = 3;
    static final int COL_YEAR = 4;
    static final int COL_VOTE_AVERAGE = 5;
    static final int COL_THUMBNAIL = 6;
    private MovieAdapter mMovieAdapter;
    private GridView mGridView;
    private int mPosition = GridView.INVALID_POSITION;
    private static final String SELECTED_KEY = "selected_position";

    public interface Callback {
        public void onItemSelected(Uri uri);
    }
    public ThumbnailFragment() {
    }
    @Override
    public void onCreate(Bundle savedInstanceState){
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mFavoriteDeleteReceiver,
                new IntentFilter("favorite_delete"));
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }
    private BroadcastReceiver mFavoriteDeleteReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent){
            getLoaderManager().restartLoader(MOVIE_LOADER, null, ThumbnailFragment.this);
        }
    };
    @Override
    public void onDestroyView(){
        Log.d("Debugging", "onDestroyView()");
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mFavoriteDeleteReceiver);
        super.onDestroyView();
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.thumbnailfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        //if(id == R.id.action_refresh){
        //updateThumbnails();
        //    return true;
        //}
        return super.onOptionsItemSelected(item);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mMovieAdapter = new MovieAdapter(getActivity(), null, 0);
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mGridView = (GridView) rootView.findViewById(gridview);
        mGridView.setAdapter(mMovieAdapter);



        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view,
                                    int position, long l) {
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if(cursor != null){
                    Uri uri;
                    if(Utility.getHowToSort(getActivity()).equals("favorite")){
                        uri = MovieContract.FavoriteEntry.buildFavoriteUri(cursor.getLong(COL_ID));
                    }else{
                        uri = MovieContract.MovieEntry.buildMovieUri(cursor.getLong(COL_ID));
                    }
                    ((Callback) getActivity()).onItemSelected(uri);
                }
                mPosition = position;
            }
        });
        if(savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)){
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        getLoaderManager().initLoader(MOVIE_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }
    void onSortChanged(){
        if(!Utility.getHowToSort(getActivity()).equals("favorite")){
            updateThumbnails();
        }
        getLoaderManager().restartLoader(MOVIE_LOADER, null, this);

    }
    private void updateThumbnails(){

        PopularMoviesSyncAdapter.syncImmediately(getActivity());
    }
    @Override
    public void onSaveInstanceState(Bundle outState){
        if(mPosition != GridView.INVALID_POSITION){
            outState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle){

        String how_to_sort = Utility.getHowToSort(getActivity());

        String sortOrder = MovieContract.MovieEntry._ID + " ASC";
        Uri uri;
        if(how_to_sort.equals("favorite")){
            uri = MovieContract.FavoriteEntry.CONTENT_URI;
        }else{
            uri = MovieContract.MovieEntry.CONTENT_URI;
        }
        return new CursorLoader(getActivity(),
                uri,
                null,
                null,
                null,
                sortOrder);
    }
    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor){
        mMovieAdapter.swapCursor(cursor);
        if(mPosition != GridView.INVALID_POSITION){
            mGridView.smoothScrollToPosition(mPosition);
        }
    }
    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader){
        mMovieAdapter.swapCursor(null);
    }



}
