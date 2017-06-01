package com.example.maya.popular_movies;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.GridView;


import com.example.maya.popular_movies.service.MovieService;

public class MainActivity extends AppCompatActivity implements  PopularMoviesGridFragment.Callback{


    private int mPosition;
    private static final String SELECTED_KEY = "selected_position";

    private boolean mTwoPane;
    private static final String DETAILFRAGMENT_TAG = "DFTAG";


    private final String LOG_TAG = MovieService.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.movie_detail_container) != null) { //check if we are in tablet

            mTwoPane = true;
            if (savedInstanceState == null) { //set only for the first time
                Bundle bundle=new Bundle();
                bundle.putBoolean(Utility.IS_TWO_PANE_ARG, true);
                bundle.putBoolean(Utility.IS_FIRST_LAUNCH_ARG, true);
                DetailsFragment fragment =new DetailsFragment();
                //set details fragment
                fragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container, fragment, DETAILFRAGMENT_TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
        }

        if(savedInstanceState!=null && savedInstanceState.containsKey(SELECTED_KEY)){
            mPosition=savedInstanceState.getInt(SELECTED_KEY);
        }

        //informe grid fragment that the app runs on tablet
        PopularMoviesGridFragment moviesFragment =  ((PopularMoviesGridFragment)getSupportFragmentManager()
                .findFragmentById(R.id.main_activity_fragment));
        moviesFragment.setTwoPane(mTwoPane);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if(mPosition!=GridView.INVALID_POSITION){
            outState.putInt(SELECTED_KEY,mPosition);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onItemSelected(Movie movie) {

        Bundle args = new Bundle();
        args.putBoolean(Utility.IS_FIRST_LAUNCH_ARG,false);
        args.putBoolean(Utility.IS_TWO_PANE_ARG,true);
        args.putString(Utility.TITLE_EXTRE, movie.title);
        args.putString(Utility.POSTER_URL_EXTRA, movie.poster_path);
        args.putString(Utility.DESCRIPTION_EXTRA, movie.overview);
        args.putDouble(Utility.RATING_EXTRA, movie.vote_average);
        args.putString(Utility.DATE_EXTRA, movie.release_date);
        args.putString(Utility.ID_EXTRA, movie.id);
        args.putDouble(Utility.POPULARITY_EXTRA, movie.popularity);

        DetailsFragment fragment = new DetailsFragment();
        fragment.setArguments(args);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.movie_detail_container, fragment, DETAILFRAGMENT_TAG)
                .commit();


    }


}
