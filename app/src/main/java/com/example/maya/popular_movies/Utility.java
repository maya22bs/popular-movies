package com.example.maya.popular_movies;

/**
 * Created by maya on 30/08/16.
 */
public class Utility {

     public final static String TITLE_EXTRE="title extra";
     public final static String POSTER_URL_EXTRA="url extra";
     public final static String DESCRIPTION_EXTRA="description extra";
     public final static String RATING_EXTRA="rating extra";
     public final static String DATE_EXTRA="date extra";
     public final static String ID_EXTRA="id extra";
     public final static String POPULARITY_EXTRA="popularity extra";


     public final static String RECIEVER_EXTRA="reciever";
     public final static String EXTRA_PARSE_TYPE="parse type";
     public final static String EXTRA_SORT_BY="sort by";
     public final static String URL_EXTRA="url extra";
     public final static int parseMovies=0;
     public final static int parseTrailers=1;
     public final static int parseReviews=2;
     public final static String IS_TWO_PANE_ARG="is two pane";
     public final static String IS_FIRST_LAUNCH_ARG="is first launch";


     public final static int sortByPopularity=0;
     public final static int sortByTitle=1;
     public final static int sortByRating=2;

     static String getYearFromDate(String date){
          return date.substring(0,4);
     }
}
