package com.example.maya.popular_movies;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.example.maya.popular_movies.service.MovieService;
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

public class ReviewsActivity extends AppCompatActivity implements  DownloadResultReceiver.Receiver  {

    ReviewAdapter mAdapter;
    private String movieID;
    ArrayList<String> reviews;
    public DownloadResultReceiver mReceiver;

    final private String RESULT_DATA="result";
    private final String LOG_TAG = MovieService.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        ListView reviewsList=(ListView) findViewById(R.id.reviews_list_view);
        mAdapter=new ReviewAdapter(ReviewsActivity.this);
        reviewsList.setAdapter(mAdapter);

        reviews=new ArrayList<String>();

        Intent intent=getIntent();
        if(intent!=null){
            movieID=intent.getStringExtra(Utility.ID_EXTRA);

        }

       reviewsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
           public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
               if(mAdapter.getToExpend()!=i || mAdapter.getToExpend()==-1) {
                   mAdapter.setToExpend(i);
               }
               else{
                   mAdapter.setToExpend(-1);
               }
               mAdapter.notifyDataSetChanged();
           }
       });


        mReceiver = new DownloadResultReceiver(new Handler());
        mReceiver.setReceiver(ReviewsActivity.this);

    }


    @Override
    protected void onStart() {
        super.onStart();
        fetchDATA();

    }

    public void fetchDATA(){

        Intent intent = new Intent(Intent.ACTION_SYNC, null, this, MovieService.class);
        intent.putExtra(Utility.RECIEVER_EXTRA, mReceiver);
        intent.putExtra(Utility.EXTRA_PARSE_TYPE, Utility.parseReviews);
        intent.putExtra(Utility.EXTRA_SORT_BY, -1);
        intent.putExtra(Utility.URL_EXTRA,getResources().getString(R.string.reviews_url,movieID));

        startService(intent);
    }

    private String getYearFromDate(String date){
        return date.substring(0,4);
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        /**
         * recieve result from service, got all reviews from web
         */
        ArrayList<String> results = (ArrayList<String>) resultData.getSerializable(RESULT_DATA);

        if(results==null){
            Log.d(LOG_TAG, "results in details fragment from service is null!");
        }
        else{
            for(String review : results) {
                mAdapter.addReview(review);
                reviews.add(review);
                mAdapter.notifyDataSetChanged();
            }
        }

    }

}
