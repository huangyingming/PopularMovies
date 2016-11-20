package com.example.android.popularmovies.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.android.popularmovies.BuildConfig;
import com.example.android.popularmovies.Trailer;

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

;


/**
 * Created by aaa on 2016/11/09.
 */

public class FetchTrailerService extends IntentService {
    public static final String MOVIE_ID_EXTRA = "mie";
    private final String LOG_TAG = FetchTrailerService.class.getSimpleName();

    public FetchTrailerService() {
        super("FetchTrailerService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String movieId = intent.getStringExtra(MOVIE_ID_EXTRA);

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String trailerJsonStr = null;

        try {
            // Construct the URL for the TheMovieDatabase query
            String baseUrl = "https://api.themoviedb.org/3/movie/" +  movieId + "/videos";

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
            trailerJsonStr = buffer.toString();
            getTrailerDataFromJson(trailerJsonStr, movieId);



        } catch (IOException e) {
            Log.e("FetchTrailerService", "Error ", e);
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
    private void getTrailerDataFromJson(String trailerJsonStr, String movieId) throws JSONException {
        final String TMD_RESULTS = "results";
        final String TMD_YOUTUBE_KEY = "key";
        final String TMD_NAME = "name";


        try {
            JSONObject trailerJson = new JSONObject(trailerJsonStr);
            JSONArray trailerArray = trailerJson.getJSONArray(TMD_RESULTS);
            //Vector<ContentValues> cVVector = new Vector<ContentValues>(trailerArray.length());
            ArrayList<Trailer> trailers = new ArrayList<Trailer>();

            for(int i = 0; i < trailerArray.length(); i++){
                JSONObject trailerInfo = trailerArray.getJSONObject(i);

                String youtubeKey = trailerInfo.getString(TMD_YOUTUBE_KEY);
                String name = trailerInfo.getString(TMD_NAME);

                /*
                ContentValues trailerValues = new ContentValues();
                trailerValues.put(MovieContract.TrailerEntry.COLUMN_MOVIE_ID, Integer.parseInt(movieId));
                trailerValues.put(MovieContract.TrailerEntry.COLUMN_YOUTUBE_KEY, youtubeKey);
                trailerValues.put(MovieContract.TrailerEntry.COLUMN_NAME, name);

                cVVector.add(trailerValues);
                */
                trailers.add(new Trailer(Integer.parseInt(movieId), youtubeKey, name));
            }



            Intent intent = new Intent("send_trailers");
            intent.putParcelableArrayListExtra("trailers", trailers);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);


            Log.d(LOG_TAG, "FetchTrailerService. " + trailers.size() + " Broadcasted");


        }catch(JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }
    /*
    public static class AlarmReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent){
            Intent sendIntent = new Intent(context, FetchTrailerService.class);
            sendIntent.putExtra(MOVIE_ID_EXTRA, intent.getStringExtra(MOVIE_ID_EXTRA));
            context.startService(sendIntent);

        }

    }*/
}
