package com.example.android.popularmovies.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.android.popularmovies.BuildConfig;
import com.example.android.popularmovies.Review;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by aaa on 2016/11/12.
 */

public class FetchReviewService extends IntentService {
    public static final String MOVIE_ID_EXTRA = "mie";
    private final String LOG_TAG = FetchReviewService.class.getSimpleName();

    public FetchReviewService() {
        super("FetchReviewService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String movieId = intent.getStringExtra(MOVIE_ID_EXTRA);

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String reviewJsonStr = null;

        try {
            // Construct the URL for the TheMovieDatabase query
            String baseUrl = "https://api.themoviedb.org/3/movie/" +  movieId + "/reviews";

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
            reviewJsonStr = buffer.toString();
            getReviewDataFromJson(reviewJsonStr, movieId);



        } catch (IOException e) {
            Log.e("FetchReviewService", "Error ", e);
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
    private void getReviewDataFromJson(String reviewJsonStr, String movieId) throws JSONException {
        final String TMD_RESULTS = "results";
        final String TMD_AUTHOR = "author";
        final String TMD_CONTENT = "content";


        try {
            JSONObject reviewJson = new JSONObject(reviewJsonStr);
            JSONArray reviewArray = reviewJson.getJSONArray(TMD_RESULTS);
            ArrayList<Review> reviews = new ArrayList<Review>();

            for(int i = 0; i < reviewArray.length(); i++){
                JSONObject reviewInfo = reviewArray.getJSONObject(i);

                String author = reviewInfo.getString(TMD_AUTHOR);
                String content = reviewInfo.getString(TMD_CONTENT);

                /*
                ContentValues trailerValues = new ContentValues();
                trailerValues.put(MovieContract.TrailerEntry.COLUMN_MOVIE_ID, Integer.parseInt(movieId));
                trailerValues.put(MovieContract.TrailerEntry.COLUMN_YOUTUBE_KEY, author);
                trailerValues.put(MovieContract.TrailerEntry.COLUMN_NAME, content);

                cVVector.add(trailerValues);
                */
                reviews.add(new Review(Integer.parseInt(movieId), author, content));
            }



            Intent intent = new Intent("send_reviews");
            intent.putParcelableArrayListExtra("reviews", reviews);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);


            Log.d(LOG_TAG, "FetchReviewService. " + reviews.size() + " Broadcasted");


        }catch(JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }
}
