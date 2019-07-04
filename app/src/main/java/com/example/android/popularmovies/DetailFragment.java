package com.example.android.popularmovies;

import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.android.popularmovies.data.MovieContract;
import com.example.android.popularmovies.service.FetchReviewService;
import com.example.android.popularmovies.service.FetchTrailerService;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by aaa on 2016/11/19.
 */

public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String LOG_TAG = DetailFragment.class.getSimpleName();
    static final String DETAIL_URI = "URI";
    private static final int DETAIL_LOADER = 0;
    private static final String[] MOVIE_COLUMNS = {
            MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_MOVIE_ID,
            MovieContract.MovieEntry.COLUMN_TITLE,
            MovieContract.MovieEntry.COLUMN_OVERVIEW,
            MovieContract.MovieEntry.COLUMN_YEAR,
            MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE,
            MovieContract.MovieEntry.COLUMN_THUMBNAIL
    };
    private static final int COL_ID = 0;
    private static final int COL_MOVIE_ID = 1;
    private static final int COL_TITLE = 2;
    private static final int COL_OVERVIEW = 3;
    private static final int COL_YEAR = 4;
    private static final int COL_VOTE_AVERAGE = 5;
    private static final int COL_THUMBNAIL = 6;

    private boolean mMakeAsFavorite;
    private String mHowToSort;
    private Uri mUri;

    private TextView mTitleView;
    private ImageView mImageView;
    private TextView mOverviewView;
    private TextView mVoteAverageView;
    private TextView mReleaseDateView;
    private Button mButton;
    private LinearLayout mTrailersView;
    private LinearLayout mReviewsView;
    public DetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if(arguments != null){
            mUri = arguments.getParcelable(DetailFragment.DETAIL_URI);
        }
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        mTitleView = (TextView) rootView.findViewById(R.id.original_title);
        mImageView = (ImageView)rootView.findViewById(R.id.thumbnail);
        mOverviewView = (TextView) rootView.findViewById(R.id.overview);
        mVoteAverageView = (TextView) rootView.findViewById(R.id.vote_average);
        mReleaseDateView = (TextView) rootView.findViewById(R.id.release_date);
        mButton = (Button) rootView.findViewById(R.id.button);
        mTrailersView = (LinearLayout) rootView.findViewById(R.id.trailers);
        mReviewsView = (LinearLayout) rootView.findViewById(R.id.reviews);
        return rootView;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }
    void onSortChanged(){
        if(mUri != null) {
            getLoaderManager().restartLoader(DETAIL_LOADER, null, this);
            Log.d("Debugging", "restartLoader");
        }
    }
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (null != mUri) {
            return new CursorLoader(
                    getActivity(),
                    mUri,
                    MOVIE_COLUMNS,
                    null,
                    null,
                    null
            );

        }
        return null;
    }
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.v(LOG_TAG, "In onLoadFinished");
        if (!data.moveToFirst()) { return; }

        String id = Integer.toString(data.getInt(COL_ID));
        String movieId = Integer.toString(data.getInt(COL_MOVIE_ID));
        String title = data.getString(COL_TITLE);
        String overview = data.getString(COL_OVERVIEW);
        String year = data.getString(COL_YEAR);
        String vote_average = Double.toString(data.getDouble(COL_VOTE_AVERAGE));
        String thumbnail = data.getString(COL_THUMBNAIL);

        mTitleView.setText(title);

        Picasso.with(getActivity()).load(thumbnail).into(mImageView);

        mOverviewView.setText(overview);
        mVoteAverageView.setText(getActivity().getString(R.string.vote_average, vote_average));
        mReleaseDateView.setText(year);

        if (Utility.isOnline(getActivity())) {
            Intent trailerIntent = new Intent(getActivity(), FetchTrailerService.class);
            trailerIntent.putExtra(FetchTrailerService.MOVIE_ID_EXTRA,
                    movieId);
            getActivity().startService(trailerIntent);

            Intent reviewIntent = new Intent(getActivity(), FetchReviewService.class);
            reviewIntent.putExtra(FetchReviewService.MOVIE_ID_EXTRA,
                    movieId);
            getActivity().startService(reviewIntent);
        }
        //set the favorite button

        mHowToSort = Utility.getHowToSort(getActivity());
        mMakeAsFavorite = isMakeAsFavorite(data.getInt(COL_MOVIE_ID));
        mButton.setTag(data);
        if (mMakeAsFavorite) {
            mButton.setText(getActivity().getString(R.string.make_as_favorite));
        } else {
            mButton.setText(getActivity().getString(R.string.unfavorite));
        }
        mButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Button button = (Button) v;
                Cursor data = (Cursor)button.getTag();
                if (mMakeAsFavorite) {
                    ContentValues movieValues = new ContentValues();
                    DatabaseUtils.cursorRowToContentValues(data, movieValues);
                    movieValues.remove(MovieContract.MovieEntry._ID);
                    getActivity().getContentResolver().insert(MovieContract.FavoriteEntry.CONTENT_URI,
                            movieValues);
                    button.setText(getActivity().getString(R.string.unfavorite));
                    mMakeAsFavorite = false;
                } else {
                    int movieId = data.getInt(COL_MOVIE_ID);
                    Uri uri = MovieContract.FavoriteEntry.buildFavoriteUriWithMovieId(movieId);
                    getActivity().getContentResolver().delete(
                            uri,
                            null,
                            null);


                    if (mHowToSort.equals("favorite")) {
                        Intent intent = new Intent("favorite_delete");
                        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
                        button.setEnabled(false);
                    } else {
                        button.setText(getActivity().getString(R.string.make_as_favorite));
                        mMakeAsFavorite = true;
                    }
                }
            }
        });
    }
    private boolean isMakeAsFavorite(int movieId) {
        if (mHowToSort.equals("favorite")) {
            return false;
        }
        Cursor c = getActivity().getContentResolver().query(
                MovieContract.FavoriteEntry.buildFavoriteUriWithMovieId(movieId),
                null,
                null,
                null,
                null
        );
        boolean result = !c.moveToFirst();
        c.close();
        return result;
    }
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mTrailerReceiver,
                new IntentFilter("send_trailers"));
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mReviewReceiver,
                new IntentFilter("send_reviews"));
        super.onCreate(savedInstanceState);

    }
    private BroadcastReceiver mTrailerReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent){
            //Log.d("mTrailerReceiver", "onReceive()");
            ArrayList<Trailer> trailers = intent.getParcelableArrayListExtra("trailers");
            createTrailerView(trailers);

        }
    };
    private BroadcastReceiver mReviewReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent){
            ArrayList<Review> reviews = intent.getParcelableArrayListExtra("reviews");
            createReviewView(reviews);
        }
    };
    private void createTrailerView(ArrayList<Trailer> trailers) {
        if (trailers == null|| trailers.size() == 0) {
            TextView noTrailers = new TextView(getActivity());
            noTrailers.setText(getActivity().getString(R.string.no_trailers));
            mTrailersView.addView(noTrailers);
            return;
        }
        LinearLayout trailerView;
        for (Trailer trailer : trailers) {
            trailerView = (LinearLayout)LayoutInflater.from(getActivity()).inflate(R.layout.list_item_trailer,
                    mTrailersView, false);
            ((TextView)trailerView.findViewById(R.id.trailer_name)).setText(trailer.mName);
            trailerView.setTag(trailer.mYoutubeKey);
            trailerView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + v.getTag()));
                    Intent webIntent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://www.youtube.com/watch?v=" + v.getTag()));
                    try {
                        startActivity(appIntent);
                    } catch (ActivityNotFoundException ex) {
                        startActivity(webIntent);
                    }
                }
            });
            mTrailersView.addView(trailerView);
        }
    }
    private void createReviewView(ArrayList<Review> reviews){
        if (reviews == null|| reviews.size() == 0) {
            TextView noReviews = new TextView(getActivity());
            noReviews.setText(getActivity().getString(R.string.no_reviews));
            mReviewsView.addView(noReviews);
            return;
        }
        LinearLayout reviewView;
        for (Review review : reviews) {
            reviewView = (LinearLayout)LayoutInflater.from(getActivity()).inflate(R.layout.list_item_review,
                    mReviewsView, false);
            ((TextView)reviewView.findViewById(R.id.author)).setText(review.mAuthor);
            ((TextView)reviewView.findViewById(R.id.content)).setText(review.mContent);
            mReviewsView.addView(reviewView);
        }
    }
    @Override
    public void onStop() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mTrailerReceiver);
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mReviewReceiver);
        super.onStop();
    }
}