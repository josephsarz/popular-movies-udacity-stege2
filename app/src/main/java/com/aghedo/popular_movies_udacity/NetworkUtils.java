package com.aghedo.popular_movies_udacity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;

public class NetworkUtils {

    private final static String BASE_URL_MOVIES = "http://api.themoviedb.org/";
    private final static String PARAM_API_KEY = "api_key";

    public static String builtUrl() {

        // TODO check the sort order from the PrefUtils

        Uri mUri = Uri.parse(BASE_URL_MOVIES)
                .buildUpon()
               // .appendPath(sortOrder)
                .appendQueryParameter(PARAM_API_KEY, BuildConfig.MOVIEDB_API_KEY)
                .build();

        return mUri.toString();
    }


    public static boolean isOnline(Context mContext) {
        ConnectivityManager cm =
                (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public interface MovieApiClient {
        @GET("/3/movie/{sort_order}?api_key="+BuildConfig.MOVIEDB_API_KEY)
        Call<String> movieModelList(
                @Path("sort_order") String sortOrder
        );
    }

    public static MovieApiClient retrofitInstance() {

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(BASE_URL_MOVIES)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create());;

        Retrofit retrofit = builder
                .client(httpClient.build())
                .build();

        return retrofit.create(MovieApiClient.class);

    }

}
