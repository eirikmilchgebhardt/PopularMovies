package no.milch.popularmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Custom adapter for populating the poster items
 * Created by Scott on 24.08.2015.
 */

public class PosterAdapter extends ArrayAdapter<MovieInfo> {
    private String LOG_TAG = getClass().getSimpleName(); // Tag used for logging
    private Context context; // Context saved for future use

    // Constructor
    public PosterAdapter(Context context) {
        // Call rest of class
        super(context, 0);
        // Save context for future use;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get associated MovieInfo
        MovieInfo movieInfo = getItem(position);

        //Log.v(LOG_TAG, "MovieInfo: " + movieInfo.toString());

        // Only create new poster_item if needed (could be a recycled one)
        if(convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.poster_item, parent, false);
        }

        // Reference image part of poster_item
        ImageView image = (ImageView)convertView.findViewById(R.id.poster_imageview);

        // Load poster image
        Picasso.with(context)
                .load(movieInfo.imageUrl)
                .fit()
                .centerInside()
                .into(image);

        // Return nicely populated view
        return convertView;
    }
}
