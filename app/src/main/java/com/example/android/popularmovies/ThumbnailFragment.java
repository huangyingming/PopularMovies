package com.example.android.popularmovies;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
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


    public ThumbnailFragment() {
    }
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.thumbnailfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if(id == R.id.action_refresh){
            updateThumbnails();
            return true;
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mMovieAdapter = new MovieAdapter(getActivity(), null, 0);
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        GridView gridview = (GridView) rootView.findViewById(R.id.gridview);
        gridview.setAdapter(mMovieAdapter);



        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view,
                                    int position, long l) {
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if(cursor != null){
                    Intent intent = new Intent(getActivity(), DetailActivity.class)
                            .setData(MovieContract.MovieEntry.buildMovieUri(
                                    cursor.getLong(COL_ID)));
                    startActivity(intent);


                }

            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        getLoaderManager().initLoader(MOVIE_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }
    void onSortChanged(){
        updateThumbnails();
        getLoaderManager().restartLoader(MOVIE_LOADER, null, this);
    }
    private void updateThumbnails(){
        /*
        String how_to_sort = Utility.getHowToSort(getActivity());

        Intent alarmIntent = new Intent(getActivity(), PopularMoviesService.AlarmReceiver.class);
        alarmIntent.putExtra(PopularMoviesService.SORT_EXTRA, how_to_sort);
        PendingIntent pi = PendingIntent.getBroadcast(getActivity(),0,alarmIntent,PendingIntent.FLAG_ONE_SHOT);
        AlarmManager am = (AlarmManager)getActivity().getSystemService(Context.ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 5000, pi);
        */
        PopularMoviesSyncAdapter.syncImmediately(getActivity());
    }
    /*
    @Override
    public void onStart(){
        super.onStart();
        updateThumbnails();
    }*/
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
        Log.d("ThumbnailFragment", "Debugging onLoadFinished called");
        mMovieAdapter.swapCursor(cursor);
    }
    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader){
        mMovieAdapter.swapCursor(null);
    }


}
