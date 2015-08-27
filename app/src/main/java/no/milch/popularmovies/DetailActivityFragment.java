package no.milch.popularmovies;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Read movie information from intent variables
        MovieInfo movieInfo = (MovieInfo)getActivity()
                .getIntent()
                .getParcelableExtra(getString(R.string.movieinfo_key));

        // Inflate view
        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        // Fill in the values of the view
        TextView title = (TextView)view.findViewById(R.id.detail_title);
        title.setText(movieInfo.originalTitle);

        TextView release = (TextView)view.findViewById(R.id.detail_releasedate);
        release.setText("Released: " + movieInfo.releaseDate);

        TextView rating = (TextView)view.findViewById(R.id.detail_rating);
        rating.setText("Rating: " + movieInfo.rating);

        TextView synopsis = (TextView)view.findViewById(R.id.detail_synopsis);
        synopsis.setText(movieInfo.plotSynopsis);

        ImageView icon = (ImageView)view.findViewById(R.id.detail_icon);
        // Load icon image
        Picasso.with(getActivity())
                .load(movieInfo.imageUrl)
                .fit()
                .centerCrop()
                .into(icon);

        return view;
    }
}
