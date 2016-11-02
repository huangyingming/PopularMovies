package com.example.android.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.example.android.popularmovies.sync.PopularMoviesSyncAdapter;

public class MainActivity extends AppCompatActivity {
    private final String LOG_TAG = MainActivity.class.getSimpleName();
    private final String THUMBNAILFRAGMENT_TAG = "TFTAG";
    private String mSort;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mSort = Utility.getHowToSort(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new ThumbnailFragment(), THUMBNAILFRAGMENT_TAG)
                    .commit();
        }
        PopularMoviesSyncAdapter.initializeSyncAdapter(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
    @Override
    protected void onResume(){
        super.onResume();
        String how_to_sort = Utility.getHowToSort(this);
        if(how_to_sort != null && !how_to_sort.equals(mSort)){
            ThumbnailFragment tf = (ThumbnailFragment)getSupportFragmentManager()
                    .findFragmentByTag(THUMBNAILFRAGMENT_TAG);
            if(null != tf){
                tf.onSortChanged();
            }
            mSort = how_to_sort;
        }
    }

}
