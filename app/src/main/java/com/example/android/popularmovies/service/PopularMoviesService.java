package com.example.android.popularmovies.service;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.util.Log;

import com.example.android.popularmovies.BuildConfig;
import com.example.android.popularmovies.MovieAdapter;
import com.example.android.popularmovies.data.MovieContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

/**
 * Created by aaa on 2016/10/30.
 */
public class PopularMoviesService extends IntentService {
    private MovieAdapter mMovieAdapter;
    public static final String SORT_EXTRA = "se";
    private final String LOG_TAG = PopularMoviesService.class.getSimpleName();

    public PopularMoviesService() {
        super("PopularMoviesService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String how_to_sort = intent.getStringExtra(SORT_EXTRA);

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String movieJsonStr = null;

        try {
            // Construct the URL for the TheMovieDatabase query
            String baseUrl = "http://api.themoviedb.org/3/movie/" +  how_to_sort;

            String apiKey = "?api_key=" + BuildConfig.THE_MOVIE_DB_API_KEY;
            URL url = new URL(baseUrl.concat(apiKey));

            // Create the request to TheMovieDatabase, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return;
            }
            movieJsonStr = buffer.toString();
            getMovieDataFromJson(movieJsonStr);



        } catch (IOException e) {
            Log.e("FetchMovietask", "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attemping
            // to parse it.

        } catch (JSONException e){
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();

        }finally{
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e("LOG_TAG", "Error closing stream", e);
                }
            }
        }

        return;



    }
    private void getMovieDataFromJson(String movieJsonStr) throws JSONException {
        final String TMD_RESULTS = "results";
        final String TMD_ORIGINAL_TITLE = "original_title";
        final String TMD_OVERVIEW = "overview";
        final String TMD_RELEASE_DATE = "release_date";
        final String TMD_POSTER_PATH = "poster_path";
        final String TMD_VOTE_AVERAGE = "vote_average";
        final String TMD_ID = "id";
        final String MOVIE_IMAGE_URI = "http://image.tmdb.org/t/p/w185/";



        try {
            JSONObject movieJson = new JSONObject(movieJsonStr);
            JSONArray movieArray = movieJson.getJSONArray(TMD_RESULTS);
            Vector<ContentValues> cVVector = new Vector<ContentValues>(movieArray.length());

            for(int i = 0; i < movieArray.length(); i++){
                JSONObject movieInfo = movieArray.getJSONObject(i);
                int id = movieInfo.getInt(TMD_ID);
                String original_title = movieInfo.getString(TMD_ORIGINAL_TITLE);
                String overview = movieInfo.getString(TMD_OVERVIEW);
                String release_date = movieInfo.getString(TMD_RELEASE_DATE);
                String poster_path = MOVIE_IMAGE_URI + movieInfo.getString(TMD_POSTER_PATH);
                double vote_average = movieInfo.getDouble(TMD_VOTE_AVERAGE);

                ContentValues movieValues = new ContentValues();
                movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, id);
                movieValues.put(MovieContract.MovieEntry.COLUMN_TITLE, original_title);
                movieValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, overview);
                movieValues.put(MovieContract.MovieEntry.COLUMN_YEAR, release_date);
                movieValues.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, vote_average);
                movieValues.put(MovieContract.MovieEntry.COLUMN_THUMBNAIL, poster_path);

                cVVector.add(movieValues);
            }
            int inserted = 0;

            if(cVVector.size() > 0){
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                this.getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI, null, null);
                this.getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI, cvArray);
            }
            Log.d(LOG_TAG, "PopularMoviesService. " + cVVector.size() + " Inserted");


        }catch(JSONException e){
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }




    }
}
