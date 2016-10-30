package com.example.android.popularmovies;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.data.MovieContract;
import com.squareup.picasso.Picasso;

/**
 * Created by aaa on 2016/08/21.
 */
public class DetailActivity extends ActionBarActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new DetailFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
        private static final String LOG_TAG = DetailFragment.class.getSimpleName();
        private static final int DETAIL_LOADER = 0;
        private static final String[] MOVIE_COLUMNS = {
                MovieContract.MovieEntry.COLUMN_MOVIE_ID,
                MovieContract.MovieEntry.COLUMN_TITLE,
                MovieContract.MovieEntry.COLUMN_OVERVIEW,
                MovieContract.MovieEntry.COLUMN_YEAR,
                MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE,
                MovieContract.MovieEntry.COLUMN_THUMBNAIL
        };
        private static final int COL_MOVIE_ID = 0;
        private static final int COL_TITLE = 1;
        private static final int COL_OVERVIEW = 2;
        private static final int COL_YEAR = 3;
        private static final int COL_VOTE_AVERAGE = 4;
        private static final int COL_THUMBNAIL = 5;

        public DetailFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            return inflater.inflate(R.layout.fragment_detail, container, false);
        }
        @Override
        public void onActivityCreated(Bundle savedInstanceState){
            getLoaderManager().initLoader(DETAIL_LOADER, null, this);
            super.onActivityCreated(savedInstanceState);
        }
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args){
            Log.v(LOG_TAG, "In onCreateLoader");
            Intent intent = getActivity().getIntent();
            if(intent == null){
                return null;
            }
            return new CursorLoader(
                    getActivity(),
                    intent.getData(),
                    MOVIE_COLUMNS,
                    null,
                    null,
                    null
            );
        }
        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data){
            Log.v(LOG_TAG, "In onLoadFinished");
            if (!data.moveToFirst()) { return; }




            String movieId = Integer.toString(data.getInt(COL_MOVIE_ID));
            String title = data.getString(COL_TITLE);
            String overview = data.getString(COL_OVERVIEW);
            String year = data.getString(COL_YEAR);
            String vote_average = Double.toString(data.getDouble(COL_VOTE_AVERAGE));
            String thumbnail = data.getString(COL_THUMBNAIL);

            ((TextView) getView().findViewById(R.id.original_title))
                    .setText(title);
            ImageView imageView = (ImageView)getView().findViewById(R.id.thumbnail);
            Picasso.with(getActivity()).load(thumbnail).into(imageView);

            ((TextView) getView().findViewById(R.id.overview))
                    .setText(overview);
            ((TextView) getView().findViewById(R.id.vote_average))
                    .setText(vote_average);
            ((TextView) getView().findViewById(R.id.release_date))
                    .setText(year);
        }
        @Override
        public void onLoaderReset(Loader<Cursor> loader){}
    }
}
