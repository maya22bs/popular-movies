package com.example.maya.popular_movies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


public class DetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        //set fragment

        Intent intent = getIntent();

        String title = intent.getStringExtra(Utility.TITLE_EXTRE);
        String  url = intent.getStringExtra(Utility.POSTER_URL_EXTRA);
        String  description = intent.getStringExtra(Utility.DESCRIPTION_EXTRA);
        double  rating = intent.getDoubleExtra(Utility.RATING_EXTRA, 0.0);
        String  date = intent.getStringExtra(Utility.DATE_EXTRA);
        String  id = intent.getStringExtra(Utility.ID_EXTRA);
        double  popularity = intent.getDoubleExtra(Utility.POPULARITY_EXTRA, 0);

        Bundle args = new Bundle();
        args.putBoolean(Utility.IS_FIRST_LAUNCH_ARG,false);
        args.putString(Utility.TITLE_EXTRE, title);
        args.putString(Utility.POSTER_URL_EXTRA, url);
        args.putString(Utility.DESCRIPTION_EXTRA, description);
        args.putDouble(Utility.RATING_EXTRA, rating);
        args.putString(Utility.DATE_EXTRA, date);
        args.putString(Utility.ID_EXTRA, id);
        args.putDouble(Utility.POPULARITY_EXTRA, popularity);

        DetailsFragment fragment = new DetailsFragment();
        fragment.setArguments(args);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.main_activity_containter, fragment)
                .commit();
    }


}
