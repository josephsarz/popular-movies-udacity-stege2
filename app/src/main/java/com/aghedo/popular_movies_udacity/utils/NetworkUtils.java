package com.aghedo.popular_movies_udacity.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.aghedo.popular_movies_udacity.BuildConfig;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;


public class NetworkUtils {

    private final static String MOVIE_BASE_URL = "http://api.themoviedb.org/";
    private final static String PARAM_API_KEY = "api_key";

    public static boolean isOnline(Context mContext) {
        ConnectivityManager cm =
                (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public static MovieApiClient movieApiInstance() {

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(MOVIE_BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder
                .client(httpClient.build())
                .build();

        return retrofit.create(MovieApiClient.class);

    }

    public static TrailersApiClient trailersApiInstance() {

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(MOVIE_BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder
                .client(httpClient.build())
                .build();

        return retrofit.create(TrailersApiClient.class);

    }

    public static ReviewsApiClient reviewsApiInstance() {

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(MOVIE_BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder
                .client(httpClient.build())
                .build();

        return retrofit.create(ReviewsApiClient.class);

    }

    public interface MovieApiClient {
        @GET("/3/movie/{sort_order}?api_key=" + BuildConfig.MOVIEDB_API_KEY)
        Call<String> movieModelList(@Path("sort_order") String sortOrder);
    }

    public interface TrailersApiClient {
        //api.themoviedb.org/3/movie/118340/videos?api_key=78672b9eec5df84f1a4f9ae81fa31d59
        @GET("/3/movie/{movie_id}/videos?api_key=" + BuildConfig.MOVIEDB_API_KEY)
        Call<String> trailersList(@Path("movie_id") String movieId);
    }

    public interface ReviewsApiClient {
        //api.themoviedb.org/3/movie/118340/reviews?api_key=78672b9eec5df84f1a4f9ae81fa31d59
        @GET("/3/movie/{movie_id}/reviews?api_key=" + BuildConfig.MOVIEDB_API_KEY)
        Call<String> reviewsList(@Path("movie_id") String movieId);
    }

}
