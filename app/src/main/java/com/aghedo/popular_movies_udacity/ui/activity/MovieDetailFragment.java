package com.aghedo.popular_movies_udacity.ui.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aghedo.popular_movies_udacity.R;
import com.aghedo.popular_movies_udacity.data.MovieContract;
import com.aghedo.popular_movies_udacity.model.MovieModel;
import com.aghedo.popular_movies_udacity.model.ReviewModel;
import com.aghedo.popular_movies_udacity.model.TrailerModel;
import com.aghedo.popular_movies_udacity.ui.adapter.ReviewListAdapter;
import com.aghedo.popular_movies_udacity.ui.adapter.TrailerListAdapter;
import com.aghedo.popular_movies_udacity.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.aghedo.popular_movies_udacity.ui.activity.MovieListActivity.BUNDLE_EXTRA;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor>, TrailerListAdapter.ItemClickListener {

    private static final int TASK_LOADER_ID = 1998;
    MovieModel movieModel;
    ImageButton favouriteButton;
    boolean isFavourite = false;
    private ArrayList<TrailerModel> trailerArrayList = new ArrayList<>();
    private ArrayList<ReviewModel> reviewArrayList = new ArrayList<>();
    private TrailerListAdapter adapter;
    private ReviewListAdapter reviewsAdapter;

    public MovieDetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movie_detail, container, false);

        setRetainInstance(true);
        TextView releaseDate, overview, voteAverage;
        ImageView imageBackground = (ImageView) view.findViewById(R.id.image_background);
        ImageView posterBackground = (ImageView) view.findViewById(R.id.iv_movie_poster_image);

        releaseDate = (TextView) view.findViewById(R.id.tv_release_date);
        overview = (TextView) view.findViewById(R.id.tv_overview);
        voteAverage = (TextView) view.findViewById(R.id.tv_vote_average);

        favouriteButton = (ImageButton) view.findViewById(R.id.ib_favourite);
        favouriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Clicked", Toast.LENGTH_SHORT).show();
                handleFavButtonClick();
            }
        });

        adapter = new TrailerListAdapter(getActivity(), this);
        reviewsAdapter = new ReviewListAdapter(getActivity());

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        RecyclerView recyclerViewTrailers = (RecyclerView) view.findViewById(R.id.rv_trailers_list);
        recyclerViewTrailers.setLayoutManager(layoutManager);
        recyclerViewTrailers.setAdapter(adapter);

        RecyclerView recyclerViewReviews = (RecyclerView) view.findViewById(R.id.rv_reviews_list);
        recyclerViewReviews.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerViewReviews.setAdapter(reviewsAdapter);

        Bundle bundle = getArguments();

        if (bundle != null)
            movieModel = bundle.getParcelable(BUNDLE_EXTRA);

        if (movieModel != null) {
            Toast.makeText(getActivity(), movieModel.getId(), Toast.LENGTH_SHORT).show();
            //getActivity().getSupportActionBar().setTitle(bundle.getTitle());
            releaseDate.setText(movieModel.getReleaseDate());
            overview.setText(movieModel.getOverview());
            voteAverage.setText(movieModel.getAverage());
            Picasso.with(getActivity()).
                    load("http://image.tmdb.org/t/p/w185/" + movieModel.getPosterPath()).into(posterBackground);

            //Picasso.with(getActivity()).
            //      load("http://image.tmdb.org/t/p/w185/" +movieModel.getBackdropPath()).into(imageBackground);

            makeTrailersRequest();
            makeReviewsRequest();

            getActivity().getSupportLoaderManager().initLoader(TASK_LOADER_ID, null, this);

        }

        return view;

    }

    private void makeReviewsRequest() {

        if (NetworkUtils.isOnline(getActivity())) {

            //Call<String> call =

            NetworkUtils.reviewsApiInstance()
                    .reviewsList(movieModel.getId())
                    .enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            Log.d("TAG", String.valueOf(response.body()));

                            JSONObject jsonObject;
                            JSONArray jsonArray;

                            if (response.isSuccessful()) {
                                String body = response.body();

                                try {
                                    jsonObject = new JSONObject(body);
                                    jsonArray = jsonObject.getJSONArray("results");

                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject object = jsonArray.getJSONObject(i);

                                        ReviewModel trailerModel = new ReviewModel();
                                        trailerModel.setAuthor(object.getString("author"));
                                        trailerModel.setContent(object.getString("content"));

                                        reviewArrayList.add(trailerModel);
                                    }

                                    reviewsAdapter.setReviewList(reviewArrayList);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }

                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {

                            //Toast.makeText(getActivity(), "An error occurred", Toast.LENGTH_SHORT).show();
                            Log.d("TAG", String.valueOf(t));

                        }
                    });

        } else {

            Toast.makeText(getActivity(), "No internet connection", Toast.LENGTH_SHORT).show();

        }

    }

    private void makeTrailersRequest() {

        if (NetworkUtils.isOnline(getActivity())) {

            Call<String> call = NetworkUtils.trailersApiInstance().trailersList(movieModel.getId());

            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    Log.d("TAG", String.valueOf(response.body()));

                    JSONObject jsonObject;
                    JSONArray jsonArray;

                    if (response.isSuccessful()) {
                        String body = response.body();

                        try {
                            jsonObject = new JSONObject(body);
                            jsonArray = jsonObject.getJSONArray("results");

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);

                                TrailerModel trailerModel = new TrailerModel();
                                trailerModel.setKey(object.getString("key"));
                                trailerModel.setName(object.getString("name"));

                                trailerArrayList.add(trailerModel);
                            }

                            adapter.setTrailerList(trailerArrayList);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }


                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {

                    //Toast.makeText(getActivity(), "An error occurred", Toast.LENGTH_SHORT).show();
                    //Log.d("TAG", String.valueOf(t));

                }
            });

        } else {

            Toast.makeText(getActivity(), "No internet connection", Toast.LENGTH_SHORT).show();

        }

    }

    public void handleFavButtonClick() {

        Toast.makeText(getActivity(), "buttonClicked", Toast.LENGTH_SHORT).show();
        if (isFavourite) {
            Log.d("TAG", "about to remove from favourites");
            //It was already in favourites
            adjustFavButton(false);
            removeFromFavourites();
            //TODO: remove from content provider
        } else {
            Log.d("TAG", "about to add to favourites");
            // was never among the favourites
            adjustFavButton(true);
            addToFavourites();
        }

    }

    private void removeFromFavourites() {

        isFavourite = false;
        String mSelection = MovieContract.MovieEntry.COLUMN_MOVIE_ID + "=?";
        String[] mSelectionArgs = {movieModel.getId()};
        getActivity().getContentResolver().delete(MovieContract.MovieEntry.MOVIE_URI, mSelection, mSelectionArgs);

        //TODO: restart loader in MainActivity Fragment;
        //TODO: set isFavourite to false;
        //MovieContract.MovieEntry.MOVIE_URI.buildUpon().appendPath().build()

    }

    private void addToFavourites() {
        Log.d("TAG", "addToFavourites");

        /**
         * Insert into the movie table
         */
        ContentValues values = new ContentValues();
        values.put(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE, movieModel.getTitle());
        values.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movieModel.getId());
        values.put(MovieContract.MovieEntry.COLUMN_IMAGE_POSTER_PATH, movieModel.getPosterPath());
        values.put(MovieContract.MovieEntry.COLUMN_MOVIE_OVERVIEW, movieModel.getOverview());
        values.put(MovieContract.MovieEntry.COLUMN_MOVIE_AVERAGE, movieModel.getAverage());

        Uri uri = getActivity().getContentResolver().insert(MovieContract.MovieEntry.MOVIE_URI, values);

        /**
         * Insert into the trailers table
         */

        if (uri != null) {
            Toast.makeText(getActivity(), uri.toString(), Toast.LENGTH_SHORT).show();
            Log.d("TAG", uri.toString());
            isFavourite = true;
        } else
            Toast.makeText(getActivity(), "uri is null", Toast.LENGTH_SHORT).show();

    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        AsyncTaskLoader<Cursor> asyncTaskLoader;
        asyncTaskLoader = new AsyncTaskLoader<Cursor>(getActivity()) {
            @Override
            public Cursor loadInBackground() {

                String mSelection = MovieContract.MovieEntry.COLUMN_MOVIE_ID + "=?";
                String[] mSelectionArgs = {movieModel.getId()};

                try {

                    return getActivity().getContentResolver().query(
                            MovieContract.MovieEntry.MOVIE_URI,
                            null,
                            mSelection, //selection
                            mSelectionArgs, // selection args
                            null
                    );

                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }

            }

            @Override
            protected void onStartLoading() {
                forceLoad();
            }
        };

        return asyncTaskLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {


        if (data != null) {
            Toast.makeText(getActivity(), String.valueOf(data.getCount()), Toast.LENGTH_SHORT).show();

            if (data.getCount() == 1) {
                isFavourite = true;
                adjustFavButton(true);
                Toast.makeText(getActivity(), loader.dataToString(data), Toast.LENGTH_SHORT).show();
                Log.d("TAG", String.valueOf(data.getCount()));
            } else {
                isFavourite = false;
                adjustFavButton(false);
                Toast.makeText(getActivity(), "It is not among the favourite", Toast.LENGTH_SHORT).show();
            }
        } else {
            //null
        }
    }

    private void adjustFavButton(boolean b) {

        if (b) {
            favouriteButton.setBackgroundResource(R.drawable.favourite);
        } else {
            favouriteButton.setBackgroundResource(R.drawable.unfavourite);
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void trailerOnClick(TrailerModel movieModel) {

        String url = "https://www.youtube.com/watch?v=" + movieModel.getKey();
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);

    }
}