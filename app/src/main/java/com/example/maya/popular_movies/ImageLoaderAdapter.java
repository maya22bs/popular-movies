package com.example.maya.popular_movies;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.example.maya.popular_movies.data.MovieContract;
import com.squareup.picasso.Picasso;

/**
 * Created by maya on 28/08/16.
 */
public class ImageLoaderAdapter extends CursorAdapter {

    private boolean mTwoPane;
    private PopularMoviesGridFragment.Callback mActivity;

    public ImageLoaderAdapter(Context context, Cursor c, int flags,PopularMoviesGridFragment.Callback callback, boolean isTwoPane) {

        super(context, c, flags);
        mTwoPane=isTwoPane;
        mActivity=callback;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        ImageView imageView = new ImageView(context);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setAdjustViewBounds(true);
        return imageView;
    }

    @Override
    public void bindView( View view, Context context,  Cursor cursor) {

        Picasso.with(context).load("http://image.tmdb.org/t/p/w154/" + cursor.getString(cursor.getColumnIndex(MovieContract.COLUMN_POSTERURL))).error(R.mipmap.ic_launcher).into((ImageView)view);

        Movie movie = new Movie();
        movie.title = cursor.getString(cursor.getColumnIndex(MovieContract.COLUMN_TITLE));
        movie.poster_path = cursor.getString(cursor.getColumnIndex(MovieContract.COLUMN_POSTERURL));
        movie.overview = cursor.getString(cursor.getColumnIndex(MovieContract.COLUMN_DESCRIPTION));
        movie.popularity = cursor.getLong(cursor.getColumnIndex(MovieContract.COLUMN_POPULARITY));
        movie.vote_average = cursor.getDouble(cursor.getColumnIndex(MovieContract.COLUMN_RATING));
        movie.release_date = cursor.getString(cursor.getColumnIndex(MovieContract.COLUMN_RELEASEDATE));
        movie.id = cursor.getString(cursor.getColumnIndex(MovieContract.COLUMN_MOVIE_ID));

       final Movie finalMovie=movie;
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!mTwoPane) {

                    Intent intent = new Intent(view.getContext(), DetailsActivity.class);
                    intent.putExtra(Utility.TITLE_EXTRE, finalMovie.title);
                    intent.putExtra(Utility.POSTER_URL_EXTRA,finalMovie.poster_path);
                    intent.putExtra(Utility.DESCRIPTION_EXTRA, finalMovie.overview);
                    intent.putExtra(Utility.RATING_EXTRA, finalMovie.vote_average);
                    intent.putExtra(Utility.DATE_EXTRA, finalMovie.release_date);
                    intent.putExtra(Utility.ID_EXTRA, finalMovie.id);
                    intent.putExtra(Utility.POPULARITY_EXTRA,  finalMovie.popularity);

                    view.getContext().startActivity(intent);

                } else {
                    mActivity.onItemSelected(finalMovie);
                }

            }
        });

    }


}
