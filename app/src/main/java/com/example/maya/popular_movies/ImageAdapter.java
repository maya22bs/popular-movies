package com.example.maya.popular_movies;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by maya on 16/08/16.
 */
public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<Movie> movies;
    private boolean mTwoPane;
    private PopularMoviesGridFragment.Callback mActivity;


    public ImageAdapter(Context c, PopularMoviesGridFragment.Callback callback, boolean isTwoPane) {
        mContext = c;
        movies=new  ArrayList<Movie>();
        mTwoPane=isTwoPane;
        mActivity=callback;
    }

    public int getCount() {
        return movies.size();
    }

    public Object getItem(int position) {
        return  movies.get(position);
    }

    public long getItemId(int position) {
        return movies.get(position).hashCode();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;

        final Movie currMovie= (Movie) getItem(position);

        if (convertView == null) {
            imageView = new ImageView(mContext);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setAdjustViewBounds(true);

        } else {
            imageView = (ImageView) convertView;
        }

        final View v=imageView; // for using in onClick, has to be final
        Picasso.with(mContext).load("http://image.tmdb.org/t/p/w154/" + currMovie.poster_path).error(R.mipmap.ic_launcher).into(imageView);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mTwoPane) {

                    Intent intent = new Intent(v.getContext(), DetailsActivity.class);
                    intent.putExtra(Utility.TITLE_EXTRE, currMovie.title);
                    intent.putExtra(Utility.POSTER_URL_EXTRA, currMovie.poster_path);
                    intent.putExtra(Utility.DESCRIPTION_EXTRA, currMovie.overview);
                    intent.putExtra(Utility.RATING_EXTRA, currMovie.vote_average);
                    intent.putExtra(Utility.DATE_EXTRA, currMovie.release_date);
                    intent.putExtra(Utility.ID_EXTRA, currMovie.id);
                    intent.putExtra(Utility.POPULARITY_EXTRA, currMovie.popularity);

                    v.getContext().startActivity(intent);

                } else {
                    mActivity.onItemSelected(currMovie);
                }
            }
        });
        return imageView;

    }
    public void clear(){
        movies.clear();
    }

    public void addImage(Movie movie) {
        this.movies.add(movie);
    }

    public void sortBy(int by){
        switch (by) {
            case 0: Collections.sort(movies,ComparatorByPopularity);
                break;
            case 1: Collections.sort(movies,ComparatorByName);
                break;
            case 2: Collections.sort(movies,ComparatorByRating);
                break;
        }
    }

    //comperators for sorting
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

    public ArrayList<Movie> getMoviesArray(){
        return movies;
    }

}
