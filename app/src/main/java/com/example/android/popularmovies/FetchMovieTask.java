package com.example.android.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import com.example.android.popularmovies.data.MovieContract.PopularEntry;
import com.example.android.popularmovies.data.MovieContract.TopRatedEntry;

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
 * Created by aaa on 2016/10/12.
 */
public class FetchMovieTask extends AsyncTask<String, Void, Void> {
    private final String LOG_TAG = FetchMovieTask.class.getSimpleName();


    private Context mContext;
    private String mSort;
    public FetchMovieTask(Context context, String how_to_sort){
        mContext = context;
        mSort = how_to_sort;
    }


    private Void getMovieDataFromJson(String movieJsonStr) throws JSONException {
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

                if(mSort.equals("popular")){
                    movieValues.put(PopularEntry.COLUMN_MOVIE_ID, id);
                    movieValues.put(PopularEntry.COLUMN_TITLE, original_title);
                    movieValues.put(PopularEntry.COLUMN_OVERVIEW, overview);
                    movieValues.put(PopularEntry.COLUMN_YEAR, release_date);
                    movieValues.put(PopularEntry.COLUMN_VOTE_AVERAGE, vote_average);
                    movieValues.put(PopularEntry.COLUMN_THUMBNAIL, poster_path);
                }else {
                    movieValues.put(TopRatedEntry.COLUMN_MOVIE_ID, id);
                    movieValues.put(TopRatedEntry.COLUMN_TITLE, original_title);
                    movieValues.put(TopRatedEntry.COLUMN_OVERVIEW, overview);
                    movieValues.put(TopRatedEntry.COLUMN_YEAR, release_date);
                    movieValues.put(TopRatedEntry.COLUMN_VOTE_AVERAGE, vote_average);
                    movieValues.put(TopRatedEntry.COLUMN_THUMBNAIL, poster_path);
                }
                cVVector.add(movieValues);
            }
            Log.d(LOG_TAG, "FetchMovieTask mSort. " + mSort);
            int inserted = 0;

            if(cVVector.size() > 0){
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                if(mSort.equals("popular")){
                    inserted = mContext.getContentResolver().bulkInsert(PopularEntry.CONTENT_URI, cvArray);

                }else{
                    inserted = mContext.getContentResolver().bulkInsert(TopRatedEntry.CONTENT_URI, cvArray);
                }
            }
            Log.d(LOG_TAG, "Debugging. " + inserted + " Inserted");


        }catch(JSONException e){
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        return null;



    }
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    @Override
    protected Void doInBackground(String... params){
        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        if(!isOnline()){
            return null;
        }
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String movieJsonStr = null;

        try {
            // Construct the URL for the TheMovieDatabase query
            String baseUrl = "http://api.themoviedb.org/3/movie/" +  params[0];

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
                return null;
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
                return null;
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

        return null;

    }
}
