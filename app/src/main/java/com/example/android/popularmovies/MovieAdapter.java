package com.example.android.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.example.android.popularmovies.data.MovieContract;
import com.squareup.picasso.Picasso;

/**
 * Created by aaa on 2016/10/15.
 */
public class MovieAdapter extends CursorAdapter{
    private Context mContext;

    public static class ViewHolder {
        public final ImageView imageView;
        public ViewHolder(View view){
            imageView = (ImageView) view.findViewById(R.id.imageViewMovie);
        }
    }

    public MovieAdapter(Context context, Cursor c, int flags){
        super(context, c, flags);
    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent){
        View view = LayoutInflater.from(context).inflate(R.layout.grid_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }
    @Override
    public void bindView(View view, Context context, Cursor cursor){

        String thumbnail = cursor.getString(ThumbnailFragment.COL_THUMBNAIL);
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        Picasso.with(context).load(thumbnail).into(viewHolder.imageView);

    }
}
