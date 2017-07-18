package com.aghedo.popular_movies_udacity.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class MovieContract {

    public static final String CONTENT_AUTHORITY = "com.aghedo.popular_movies_udacity";
    public final static Uri BASE_CONTENT_URL = Uri.parse("content://" + CONTENT_AUTHORITY);

    public final static String MOVIE_PATH = "movie";
    public final static String TRAILERS_PATH = "trailers";
    public final static String REVIEW_PATH = "reviews";

    public static class MovieEntry implements BaseColumns {


        public final static String TABLE_NAME = "movie";
        public static final String COLUMN_IMAGE_POSTER_PATH = "poster_path";
        public static final String COLUMN_MOVIE_AVERAGE = "average";
        public final static String COLUMN_MOVIE_TITLE = "title";
        public final static String COLUMN_MOVIE_ID = "movie_id";
        public final static String COLUMN_MOVIE_OVERVIEW = "overview";
        public static Uri MOVIE_URI = BASE_CONTENT_URL.buildUpon().appendPath(MOVIE_PATH).build();

    }

    /*public static class TrailersEntry implements BaseColumns {

        public static Uri TRAILERS_URI = BASE_CONTENT_URL.buildUpon().appendPath(TRAILERS_PATH).build();
        public final static String TABLE_NAME = "trailers";

        public final static String COLUMN_MOVIE_ID = "movie_id";
        public final static String COLUMN_KEY = "key";
        public final static String COLUMN_NAME = "name";
    }

    public static class ReviewsEntry implements BaseColumns {

        public static Uri REVIEWS_URI = BASE_CONTENT_URL.buildUpon().appendPath(REVIEW_PATH).build();
        public final static String TABLE_NAME = "reviews";

        public final static String COLUMN_MOVIE_ID = "movie_id";
        public final static String COLUMN_AUTHOR = "author";
        public final static String COLUMN_CONTENT = "content";
    }*/

}
