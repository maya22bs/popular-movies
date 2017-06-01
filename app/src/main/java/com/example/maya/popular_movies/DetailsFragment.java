package com.example.maya.popular_movies;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.example.maya.popular_movies.data.MovieContract;
import com.example.maya.popular_movies.data.MovieDbHelper;
import com.example.maya.popular_movies.service.MovieService;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

/**
 * Created by maya on 28/08/16.
 */
public class DetailsFragment extends android.support.v4.app.Fragment implements DownloadResultReceiver.Receiver {

    private TrailersAdapter myAdapter;
    private ArrayList<String> trailers;
    private String movieID;
    public DownloadResultReceiver mReceiver;

    //Strings
    final private String RESULT_DATA="result";
    private final String IS_FIRST_LAUNCH_ARG="is first launch";

    //helpers
    private boolean isFirstLaunch=true;
    private final String LOG_TAG = MovieService.class.getSimpleName();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        trailers = new ArrayList<String>();

        Bundle arguments = getArguments();
        movieID=arguments.getString(Utility.ID_EXTRA);
        isFirstLaunch = arguments.getBoolean(IS_FIRST_LAUNCH_ARG);

        Button reviewsButton=(Button) rootView.findViewById(R.id.reviews_button);
        reviewsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ReviewsActivity.class);
                intent.putExtra(Utility.ID_EXTRA,movieID);
                startActivity(intent);
            }
        });


        String title = arguments.getString(Utility.TITLE_EXTRE);
        String url = arguments.getString(Utility.POSTER_URL_EXTRA);
        String description = arguments.getString(Utility.DESCRIPTION_EXTRA);
        double rating = arguments.getDouble(Utility.RATING_EXTRA, 0.0);
        String date = arguments.getString(Utility.DATE_EXTRA);
        String id = arguments.getString(Utility.ID_EXTRA);
        double popularity = arguments.getDouble(Utility.POPULARITY_EXTRA, 0);


        //for the first launch in tablet mose, no views are available , set before clicked!
        if(!isFirstLaunch) {

            //get views
            LinearLayout layout=(LinearLayout) rootView.findViewById(R.id.detail_linear_layout);
            layout.setVisibility(View.VISIBLE);
            ImageView posterView = (ImageView) rootView.findViewById(R.id.poster_image);
            TextView titleView = (TextView) rootView.findViewById(R.id.title_text_view);
            TextView descriptionView = (TextView) rootView.findViewById(R.id.description_text_view);
            TextView ratingView = (TextView) rootView.findViewById(R.id.rating_text_view);
            TextView dateView = (TextView) rootView.findViewById(R.id.date_text_view);

            //set data to UI
            Picasso.with(getActivity().getApplicationContext()).load("http://image.tmdb.org/t/p/w500/" + url).error(R.mipmap.ic_launcher).into(posterView);
            titleView.setText(title);
            descriptionView.setText(description);
            ratingView.setText(Double.toString(rating) + "/10");
            dateView.setText(Utility.getYearFromDate(date));

            ListView trailersList = (ListView) rootView.findViewById(R.id.trailers_list);
            myAdapter = new TrailersAdapter(getActivity());
            trailersList.setAdapter(myAdapter);

            trailersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + trailers.get(i)));
                        startActivity(intent);

                    } catch (ActivityNotFoundException ex) {
                        Intent intent = new Intent(Intent.ACTION_VIEW,
                                Uri.parse("http://www.youtube.com/watch?v=" + trailers.get(i)));
                        startActivity(intent);
                    }
                }
            });


            Button favoriteButton = (Button) rootView.findViewById(R.id.mark_favorite_button);

            //content values for getting favorites movies from DB
            final ContentValues insertValues = new ContentValues();
            insertValues.put(MovieContract.COLUMN_MOVIE_ID, id);
            insertValues.put(MovieContract.COLUMN_DESCRIPTION, description);
            insertValues.put(MovieContract.COLUMN_RATING, rating);
            insertValues.put(MovieContract.COLUMN_RELEASEDATE, date);
            insertValues.put(MovieContract.COLUMN_TITLE, title);
            insertValues.put(MovieContract.COLUMN_POSTERURL, url);
            insertValues.put(MovieContract.COLUMN_POPULARITY, popularity);

            favoriteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Cursor cursor=getActivity().getContentResolver().query(MovieContract.CONTENT_URI,MovieContract.COLUMNS,MovieContract.COLUMN_MOVIE_ID+" = "+movieID,null,null,null);
                    if(cursor.getCount() == 0) {
                        getActivity().getContentResolver().insert(MovieContract.CONTENT_URI, insertValues);
                    }
                    getContext().getApplicationContext().getContentResolver().notifyChange(MovieContract.CONTENT_URI, null);


                }
            });


            //initialize reciever for movie service
            mReceiver = new DownloadResultReceiver(new Handler());
            mReceiver.setReceiver(this);
            fetchDATA();
        }
        return rootView;
    }





    public void fetchDATA(){
        /**
         * Fetching data from web, with intent service MOVIE SERVICE
         */
        Intent intent = new Intent(getActivity(), MovieService.class);
        intent.putExtra(Utility.RECIEVER_EXTRA, mReceiver);
        intent.putExtra(Utility.EXTRA_PARSE_TYPE, Utility.parseTrailers);
        intent.putExtra(Utility.EXTRA_SORT_BY, -1);
        intent.putExtra(Utility.URL_EXTRA,getResources().getString(R.string.tratilers_url,movieID));
        getActivity().startService(intent);

    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        /**
         * recieve result from service, got all trailers from web
         */
        ArrayList<String> results = (ArrayList<String>) resultData.getSerializable(RESULT_DATA);
        if(results==null){
            Log.d(LOG_TAG, "results array in from service is null!");
        }
        else{
            for(String trailer : results) {
                myAdapter.addTrailer(trailer);
                trailers.add(trailer);
                myAdapter.notifyDataSetChanged();
            }
        }
    }




}
