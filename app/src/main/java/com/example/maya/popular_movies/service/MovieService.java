package com.example.maya.popular_movies.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import com.example.maya.popular_movies.Movie;
import com.example.maya.popular_movies.Utility;
import com.google.gson.Gson;

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
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by maya on 25/08/16.
 */
public class MovieService extends IntentService {

    private int parseType;

    final private int parseMovies=0;
    final private int parseTrailers=1;
    final private int parseReviews=2;
    final private int RUNNING=0;

    private int mSortBy=0;
    private String mURL;

    final private String RESULT_DATA="result";

    private final String LOG_TAG = MovieService.class.getSimpleName();

    public MovieService(){

        super("MovieService");
    }
    @Override
    protected void onHandleIntent(Intent intent) {

        parseType=intent.getIntExtra(Utility.EXTRA_PARSE_TYPE,0);
        mSortBy=intent.getIntExtra(Utility.EXTRA_SORT_BY,0);
        mURL=intent.getStringExtra(Utility.URL_EXTRA);

        // check internet connection
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if(! (netInfo != null && netInfo.isConnected())){ //no internet connection
            Log.d(LOG_TAG,"no internet connection");
            return;
        }


        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String moviesJsonStr = null;

        try {

            URL url = new URL(mURL);

            // Create the request to OpenWeatherMap, and open the connection
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
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                return;
            }
            moviesJsonStr = buffer.toString();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            return;

        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        try {
            Bundle bundle=new Bundle();
            final ResultReceiver receiver = intent.getParcelableExtra(Utility.RECIEVER_EXTRA);
            if(receiver==null){
                Log.d(LOG_TAG, "reciever is null!");
            }
            switch (parseType) {
                case parseMovies:
                    ArrayList<Movie>  movies=getMoviesFromJson(moviesJsonStr);
                    bundle.putSerializable(RESULT_DATA, movies);
                    break;
                case parseTrailers:
                    ArrayList<String>  trailers=getTrailersFromJson(moviesJsonStr);
                    bundle.putSerializable(RESULT_DATA, trailers);
                    break;
                case parseReviews:
                    ArrayList<String>  reviews=getReviewsFromJson(moviesJsonStr);
                    bundle.putSerializable(RESULT_DATA, reviews);
                    break;
            }

            receiver.send(RUNNING, bundle);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        // This will only happen if there was an error getting or parsing the forecast.
        return;
    }

    private ArrayList<Movie>  getMoviesFromJson(String moviesJsonArg) throws JSONException {

        final String OWM_LIST = "results";

        JSONObject moviesJson = new JSONObject(moviesJsonArg);
        JSONArray moviesArray = moviesJson.getJSONArray(OWM_LIST);

         ArrayList<Movie> resultMovies = new  ArrayList<Movie>();

        for(int i = 0; i < moviesArray.length(); i++) {
            JSONObject movieJSON = moviesArray.getJSONObject(i);

            Gson gson = new Gson();
            Movie deserialized = gson.fromJson(movieJSON.toString(), Movie.class);

            resultMovies.add(deserialized);
        }
        //sort the array by the sort by value
        Comparator<Movie> ComparatorByName = new Comparator<Movie>() {

            public int compare(Movie movie1, Movie movie2) {
                return movie1.title.compareTo(movie2.title);
            }
        };

        Comparator<Movie> ComparatorByPopularity = new Comparator<Movie>()
        {
            public int compare(Movie movie1, Movie movie2)
            {
                if (movie1.popularity < movie2.popularity) {
                    return 1;
                }
                else if(movie1.popularity > movie2.popularity) {
                    return -1;
                }
                else {
                    return 0;
                }
            }
        };

        Comparator<Movie> ComparatorByRating = new Comparator<Movie>()
        {
            public int compare(Movie movie1, Movie movie2)
            {
                if (movie1.vote_average < movie2.vote_average) {
                    return 1;
                }
                else if(movie1.vote_average > movie2.vote_average) {
                    return -1;
                }
                else {
                    return 0;
                }
            }
        };
        if(mSortBy==0){
            Collections.sort(resultMovies,ComparatorByPopularity);
        }else if(mSortBy==1){
            Collections.sort(resultMovies,ComparatorByName);
        }else if(mSortBy==2){
            Collections.sort(resultMovies,ComparatorByRating);
        }
        return resultMovies;
    }

    private  ArrayList<String> getTrailersFromJson(String trailersJsonArg) throws JSONException {

        final String OWM_LIST = "results";
        JSONObject trailersJson = new JSONObject(trailersJsonArg);
        JSONArray trailersArray = trailersJson.getJSONArray(OWM_LIST);

        ArrayList<String> resultTrailers = new ArrayList<String>();

        for(int i = 0; i < trailersArray.length(); i++) {

            JSONObject trailerJSON = trailersArray.getJSONObject(i);
            String trailer=trailerJSON.getString("key");
            resultTrailers.add(trailer);

        }
        return resultTrailers;
    }

    private  ArrayList<String>  getReviewsFromJson(String reviewsJsonArg) throws JSONException {

        final String OWM_LIST = "results";

        JSONObject reviewsJson = new JSONObject(reviewsJsonArg);
        JSONArray reviewsArray = reviewsJson.getJSONArray(OWM_LIST);

        ArrayList<String> resultReviews = new ArrayList<String>();

        for(int i = 0; i < reviewsArray.length(); i++) {

            JSONObject reviewJSON = reviewsArray.getJSONObject(i);
            String review=reviewJSON.getString("content");
            resultReviews.add(review);

        }
        return resultReviews;

    }
}
