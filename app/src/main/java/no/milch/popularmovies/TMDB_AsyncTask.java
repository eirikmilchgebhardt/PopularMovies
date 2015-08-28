package no.milch.popularmovies;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Async task that fetch movie information from TheMovieDB
 * Parameters:
 *       String - sorting order (popular/rating)
 *       Void - T.B.D.
 * Returns:
 *       List<MovieInfo> - resulset as poster info objects
 *
 * Created by Scott on 24.08.2015.
 */
public class TMDB_AsyncTask extends AsyncTask<String, Void, List<MovieInfo>> {
    // Constants used to query TheMovieDB
    final String TMDB_BASE_URL = "https://api.themoviedb.org/3";
    final String TMDB_IMAGE_URL = "http://image.tmdb.org/t/p/w185";
    final String TMDB_DISCOVER_PARAM1 = "discover";
    final String TMDB_DISCOVER_PARAM2 = "movie";
    final String TMDB_CONFIG_PARAM = "configuration";
    final String TMDB_SORT = "sort_by";
    final String TMDB_KEY = "api_key";
    final String MY_KEY = "2d5a40519390aca5186e4c0e94334347"; // TODO: Private API KEY. NB: Should be removed before submitting.

    private String LOG_TAG = getClass().getSimpleName(); // Tag used for logging
    private ArrayAdapter associatedAdapter = null; // Associated poster adapter saved for future use

    public TMDB_AsyncTask(ArrayAdapter associatedAdapter) {
        super();
        // Save associated adapter for future use
        this.associatedAdapter = associatedAdapter;
    }

    @Override
    protected List<MovieInfo> doInBackground(String... params) {
        // Decode sorting preference from param[0]
        String sortby = (
                params[0].equals(associatedAdapter.getContext().getString(R.string.pref_sort_popular)) ? "popularity.desc" :
                        params[0].equals(associatedAdapter.getContext().getString(R.string.pref_sort_rating)) ? "vote_average.desc" :
                                ""
        );

        List<MovieInfo> movieInfos = null;
        try {
            // Build URI for querying TheMovieDB
            Uri builtUri = Uri.parse(TMDB_BASE_URL).buildUpon()
                    .appendPath(TMDB_DISCOVER_PARAM1)
                    .appendPath(TMDB_DISCOVER_PARAM2)
                    .appendQueryParameter(TMDB_SORT, sortby)
                    .appendQueryParameter(TMDB_KEY, MY_KEY)
                    .build();
            URL url = new URL(builtUri.toString());
            //Log.v(LOG_TAG, "TMDB movie info url: " + url.toString());

            // Call TMDB
            String jsonString = doCallJson(url);

            // Loop through result and populate movie info
            movieInfos = new ArrayList<MovieInfo>();
            JSONObject json = new JSONObject(jsonString);
            JSONArray jArray = json.getJSONArray("results");
            for (int i = 0; i < jArray.length(); i++) {
                JSONObject oneMovie = jArray.getJSONObject(i);

                MovieInfo movieInfo = new MovieInfo();
                movieInfo.imageUrl = TMDB_IMAGE_URL + oneMovie.getString("poster_path");
                movieInfo.originalTitle = ensureGoodString(oneMovie.getString("original_title"));
                movieInfo.plotSynopsis = ensureGoodString(oneMovie.getString("overview"));
                movieInfo.rating = ensureGoodString(oneMovie.getString("vote_average"));
                movieInfo.releaseDate = ensureGoodString(oneMovie.getString("release_date"));

                movieInfos.add(movieInfo);

                //Log.v(LOG_TAG, "Added movie: " + movieInfo.originalTitle);
            }
        }
        catch (Exception e)
        {
            Log.e(LOG_TAG, "Error", e);
        }
        return movieInfos;
    }

    private String ensureGoodString(String string){
        if (string == null) return "";
        else if (string == "null") return "";
        else return string;
    }

    @Override
    protected void onPostExecute(List<MovieInfo> movieInfos) {
        if(movieInfos != null)  {
            associatedAdapter.clear();
            // Min version set to 15. addAll came in 11.
            associatedAdapter.addAll(movieInfos);
        }
    }

    private String doCallJson(URL url){
        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String json = null;

        try {
            // Create request to server and open the connection
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

            json = buffer.toString();

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error: ", e);
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream: ", e);
                }
            }
        }

        return json;
    }
}
