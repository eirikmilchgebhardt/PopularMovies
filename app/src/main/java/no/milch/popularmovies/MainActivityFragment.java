package no.milch.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    // Tag for when saving state to bundle
    private static final String SAVED_STATE_TAG = "MovieInfo";
    private static final String SAVED_POS_TAG = "MovieIndex";

    // Save menu to alter text later
    private Menu menu;
    // Save adapter for easy update
    private PosterAdapter posterAdapter;
    // Save grid view for position update
    private GridView gridView;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Make _empty_ poster adapter for this fragment.
        // adapter will either be filled by async task or as a restore of state.
        this.posterAdapter = new PosterAdapter(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate view
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        // Find movie grid view
        this.gridView = (GridView) view.findViewById(R.id.MoviePosters);
        // Attach poster adapter to grid view
        gridView.setAdapter(this.posterAdapter);

        // Register an onClick listener
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get movie information from posterAdapter and send it to DetailActivity
                MovieInfo movieInfo = posterAdapter.getItem(position);
                // Can be passed along directly since it is a Parsable
                startActivity(new Intent(getActivity(), DetailActivity.class)
                        .putExtra(getString(R.string.movieinfo_key), movieInfo));
            }
        });

        // If grid was populated by savedInstance (in onActivityCreated) set position in grid to saved state
        if(savedInstanceState != null && savedInstanceState.containsKey(SAVED_POS_TAG)){
            gridView.smoothScrollToPosition(savedInstanceState.getInt(SAVED_POS_TAG));
        }

        // Return view
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save existing state of movies by iterating through the poster adapter
        // and pick out the current movies info and save the information as a bundle.
        ArrayList<MovieInfo> movieList = new ArrayList<MovieInfo>();
        for (int i=0; i<posterAdapter.getCount(); i++) {
            movieList.add(posterAdapter.getItem(i));
        }
        // Save existing movies info
        outState.putParcelableArrayList(SAVED_STATE_TAG, movieList);
        // Save position in grid
        outState.putInt(SAVED_POS_TAG, this.gridView.getFirstVisiblePosition());
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            // Probably orientation change. restore saved instance state from bundle
            ArrayList<MovieInfo> movieList = savedInstanceState.getParcelableArrayList(SAVED_STATE_TAG);
            for (MovieInfo movie: movieList) {
                posterAdapter.add(movie);
            }
        } else {
            // Get sort order from SharedStorage, default to popular if sort order is not found
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String sortOrder = sharedPref.getString(getString(R.string.pref_sort_key), getString(R.string.pref_sort_popular));

            // Initiate call to TMDB and pass on poster adapter to fill it
            TMDB_AsyncTask tmdb_asyncTask = new TMDB_AsyncTask(this.posterAdapter);
            tmdb_asyncTask.execute(sortOrder); // Execute with sort order rating
        }
    }
}
