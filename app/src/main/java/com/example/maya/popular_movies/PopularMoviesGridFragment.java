package com.example.maya.popular_movies;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Spinner;
import com.example.maya.popular_movies.data.MovieContract;
import com.example.maya.popular_movies.service.MovieService;
import java.util.ArrayList;

/**
 * Created by maya on 28/08/16.
 */
public class PopularMoviesGridFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, DownloadResultReceiver.Receiver {

    private ImageAdapter myAdapter;
    boolean isShowFavorite=false;
    private int FAVORITES_LOADER=0;
    public DownloadResultReceiver mReceiver;
    public ImageLoaderAdapter mImageLoaderAdapter;
    private int mPosition;
    private GridView mGridview;
    private Callback mActivity;
    private int mSpinnerSelection;

    private int mSortBy=0;
    private String sortByQueryClumn=MovieContract.COLUMN_POPULARITY;;

    final private String RESULT_DATA="result";

    final private String SPINNER_SELECTION="spinner selection";


    //helpers
    private int firstTimeForSpinner=0;
    private boolean mTwoPane;

    private final String LOG_TAG = MovieService.class.getSimpleName();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //to handel the action bar inside the fragment
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, null);
        mGridview = (GridView) rootView.findViewById(R.id.gridview);
        myAdapter=new ImageAdapter(getActivity(),mActivity,mTwoPane);
        mGridview.setAdapter(myAdapter);

        if(savedInstanceState!=null){
            mSpinnerSelection=savedInstanceState.getInt(SPINNER_SELECTION);
            mSortBy=savedInstanceState.getInt(Utility.EXTRA_SORT_BY);
        }

        mImageLoaderAdapter = new ImageLoaderAdapter(getActivity(), null, 0, mActivity, mTwoPane);

        mReceiver = new DownloadResultReceiver(new Handler());
        mReceiver.setReceiver(this);

        if(!isShowFavorite) {
            fetchDATA();
        }

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mActivity = (Callback) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement Callback");
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.action_bar, menu);

        MenuItem item = menu.findItem(R.id.action_sort_by);
        final Spinner spinner = (Spinner) MenuItemCompat.getActionView(item);

        spinner.post(new Runnable() {
            @Override
            public void run() {
                spinner.setSelection(mSpinnerSelection);
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                //avoid the automatic onClick event when spinner is initialized
                if(firstTimeForSpinner==Utility.sortByPopularity){
                    firstTimeForSpinner++;
                    return;
                }

                switch (i) {
                    case Utility.sortByPopularity:
                        mSpinnerSelection=Utility.sortByPopularity;
                        mSortBy = Utility.sortByPopularity;
                        myAdapter.sortBy(Utility.sortByPopularity);
                        sortByQueryClumn = MovieContract.COLUMN_POPULARITY;
                        break;

                    case Utility.sortByTitle:
                        mSpinnerSelection=Utility.sortByTitle;
                        mSortBy = Utility.sortByTitle;
                        myAdapter.sortBy(Utility.sortByTitle);
                        sortByQueryClumn = MovieContract.COLUMN_TITLE;
                        break;

                    case Utility.sortByRating:
                        mSpinnerSelection = Utility.sortByRating;
                        mSortBy = Utility.sortByRating;
                        myAdapter.sortBy(Utility.sortByRating);
                        sortByQueryClumn = MovieContract.COLUMN_RATING;
                        break;
                }

                //decide which adapter to notify
                if(isShowFavorite) {
                    showFavoriteMovies();
                    mImageLoaderAdapter.notifyDataSetChanged();
                }
                else{
                    myAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });


        ArrayAdapter< CharSequence > adapter = ArrayAdapter.createFromResource(getActivity(), R.array.order, R.layout.spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setBackgroundColor(getResources().getColor(R.color.backgroundDetail));
        spinner.setPopupBackgroundDrawable(getResources().getDrawable(R.drawable.spinner_background));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_favorits) {
            if(!isShowFavorite) {
                showFavoriteMovies();
            }

            return true;

        }else if(id==R.id.action_all){

            mGridview.setAdapter(myAdapter);
            isShowFavorite=false;
            fetchDATA();
        }
        return super.onOptionsItemSelected(item);
    }

    public void showFavoriteMovies(){

        mGridview.setAdapter(mImageLoaderAdapter);
        getLoaderManager().restartLoader(FAVORITES_LOADER, null, this);
        isShowFavorite=true;
        mGridview.setSelection(0);

    }



    public void fetchDATA(){
        /**
         * Fetching data from web, with intent service MOVIE SERVICE
         */
        Intent intent = new Intent(getActivity(), MovieService.class);
        intent.putExtra(Utility.RECIEVER_EXTRA, mReceiver);
        intent.putExtra(Utility.EXTRA_PARSE_TYPE, Utility.parseMovies);
        intent.putExtra(Utility.EXTRA_SORT_BY, mSortBy);
        intent.putExtra(Utility.URL_EXTRA,getResources().getString(R.string.movies_url));

        getActivity().startService(intent);
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        /**
         * recieve result from service, got all popular movies from web
         */
        if (resultData == null) {
            Log.d(LOG_TAG, "results array in from service is null!");
        }else{
            ArrayList<Movie> results = (ArrayList<Movie>) resultData.getSerializable(RESULT_DATA);
            myAdapter.clear();
            for(Movie movie : results) {
                myAdapter.addImage(movie);
            }
            myAdapter.sortBy(mSortBy);
            myAdapter.notifyDataSetChanged();
        }
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        //create a sort order string for raw query
        String orderbyString=sortByQueryClumn;
        switch (sortByQueryClumn){
            case MovieContract.COLUMN_TITLE:
                orderbyString = orderbyString + " ASC";
                break;
            default:
                orderbyString=orderbyString+" DESC";
                break;

        }
        return new CursorLoader(getActivity(),
                MovieContract.CONTENT_URI,
                null,
                null,
                null,
                orderbyString);
    }



    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        /**
         * add all the information from the cursor to mymovies array, for the click logic.
         */
        mImageLoaderAdapter.swapCursor(data);

        if (mPosition != ListView.INVALID_POSITION) {
            mGridview.smoothScrollToPosition(mPosition);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mImageLoaderAdapter.swapCursor(null);
    }


    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        public void onItemSelected(Movie movie);
    }

    public void setTwoPane(boolean isTwoPane){
        mTwoPane=isTwoPane;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {

        outState.putInt(SPINNER_SELECTION,mSpinnerSelection);
        outState.putInt(Utility.EXTRA_SORT_BY,mSortBy);
        super.onSaveInstanceState(outState);

    }



}
