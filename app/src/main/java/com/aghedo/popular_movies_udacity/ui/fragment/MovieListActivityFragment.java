package com.aghedo.popular_movies_udacity.ui.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.aghedo.popular_movies_udacity.R;
import com.aghedo.popular_movies_udacity.data.MovieContract;
import com.aghedo.popular_movies_udacity.model.MovieModel;
import com.aghedo.popular_movies_udacity.ui.activity.MovieDetail;
import com.aghedo.popular_movies_udacity.ui.activity.MovieDetailFragment;
import com.aghedo.popular_movies_udacity.ui.activity.SettingsActivity;
import com.aghedo.popular_movies_udacity.ui.adapter.MovieListAdapter;
import com.aghedo.popular_movies_udacity.utils.NetworkUtils;
import com.aghedo.popular_movies_udacity.utils.PreferenceUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.aghedo.popular_movies_udacity.ui.activity.MovieListActivity.BUNDLE_EXTRA;
import static com.aghedo.popular_movies_udacity.ui.activity.MovieListActivity.EXTRA_PARCELABLE;
import static com.aghedo.popular_movies_udacity.ui.activity.MovieListActivity.twoPane;

/**
 * A placeholder fragment containing a simple view.
 */

public class MovieListActivityFragment extends Fragment
        implements MovieListAdapter.ItemClickListener,
        SharedPreferences.OnSharedPreferenceChangeListener,
        LoaderManager.LoaderCallbacks<Cursor> {

    public static final int TASK_LOADER_ID = 1997;
    private static final String LOG_TAG = MovieListActivityFragment.class.getSimpleName();
    private static final String MY_LIST = "rv_array_list";
    MovieListAdapter movieListAdapter;
    SharedPreferences sharedPreferences;
    @BindView(R.id.pb_loading_movie_list)
    ProgressBar progressBar;
    @BindView(R.id.tv_error_message)
    TextView errorMessage;
    @BindView(R.id.rv_movie_list)
    RecyclerView mMovieList;
    private ArrayList<MovieModel> movieArrayList = new ArrayList<>();

    public MovieListActivityFragment() {
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(MY_LIST, movieArrayList);
        Log.d(LOG_TAG, "On Save Instance");
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        setRetainInstance(true);
        View mView = inflater.inflate(R.layout.fragment_movie_list, container, false);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        ButterKnife.bind(this, mView);
        setHasOptionsMenu(true);

        progressBar.setVisibility(View.INVISIBLE);
        errorMessage.setText(R.string.error_message);
        mMovieList = (RecyclerView) mView.findViewById(R.id.rv_movie_list);
        mMovieList.setLayoutManager(gridLayoutManager);
        movieListAdapter = new MovieListAdapter(getActivity(), this);

        final String sortOrder = PreferenceUtils.currentSortOrder(getActivity());

        if (savedInstanceState != null) {
            movieArrayList = savedInstanceState.getParcelableArrayList(MY_LIST);
            movieListAdapter.setMovieList(movieArrayList);
            Toast.makeText(getActivity(), "Using the save instance state", Toast.LENGTH_SHORT).show();
            showResponse();
        } else {

            if (sortOrder.equals("favourites")) {
                getActivity().getSupportLoaderManager().restartLoader(TASK_LOADER_ID, null, this);
            } else {
                fetchMovieList(PreferenceUtils.currentSortOrder(getActivity()));
            }

        }
        mMovieList.setAdapter(movieListAdapter);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

        return mView;
    }

    public void showErrorMessage() {
        progressBar.setVisibility(View.INVISIBLE);
        errorMessage.setVisibility(View.VISIBLE);
        mMovieList.setVisibility(View.INVISIBLE);
    }

    public void showResponse() {
        progressBar.setVisibility(View.INVISIBLE);
        errorMessage.setVisibility(View.INVISIBLE);
        mMovieList.setVisibility(View.VISIBLE);
    }

    private void fetchMovieList(String sortOrder) {

        progressBar.setVisibility(View.VISIBLE);
        movieArrayList.clear();

        if (movieListAdapter != null)
            movieListAdapter.notifyDataSetChanged();

        if (NetworkUtils.isOnline(getActivity())) {

            Call<String> call = NetworkUtils.movieApiInstance().movieModelList(sortOrder);
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    Log.d(LOG_TAG, String.valueOf(response.body()));
                    showResponse();
                    if (response.isSuccessful())
                        parseJson(response.body());
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    showErrorMessage();
                    Toast.makeText(getActivity(), "An error occurred", Toast.LENGTH_SHORT).show();
                    Log.d(LOG_TAG, String.valueOf(t));

                }
            });

        } else {

            progressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(getActivity(), "No internet connection", Toast.LENGTH_SHORT).show();

        }

    }

    private void parseJson(String response) {

        JSONObject jsonObject;
        JSONArray jsonArray;

        try {
            jsonObject = new JSONObject(response);
            jsonArray = jsonObject.getJSONArray("results");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);

                MovieModel mMovieModel = new MovieModel();
                mMovieModel.setId(object.getString("id"));
                mMovieModel.setPosterPath(object.getString("poster_path"));
                mMovieModel.setTitle(object.getString("original_title"));
                mMovieModel.setBackdropPath(object.getString("backdrop_path"));
                mMovieModel.setReleaseDate(object.getString("release_date"));
                mMovieModel.setAverage(object.getString("vote_average"));
                mMovieModel.setOverview(object.getString("overview"));

                movieArrayList.add(mMovieModel);
            }

            movieListAdapter.setMovieList(movieArrayList);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(getActivity(), SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        String currentSortOrder = PreferenceUtils.currentSortOrder(getActivity());
        if (key.equals(getString(R.string.pref_list_key))) {

            if (currentSortOrder.equals("favourites")) {
                //TODO: restart Loader Manager
                showResponse();
                getActivity().getSupportLoaderManager().initLoader(TASK_LOADER_ID, null, this);

            } else {
                getActivity().getSupportLoaderManager().destroyLoader(TASK_LOADER_ID);
                fetchMovieList(currentSortOrder);
            }
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        String sortOrder = PreferenceUtils.currentSortOrder(getActivity());
        if (sortOrder.equals("favourites"))
            getActivity().getSupportLoaderManager().restartLoader(TASK_LOADER_ID, null, this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<Cursor>(getActivity()) {

            Cursor cursor;

            @Override
            public Cursor loadInBackground() {
                try {
                    return getActivity().getContentResolver().query(
                            MovieContract.MovieEntry.MOVIE_URI,
                            null,
                            null,
                            null,
                            null
                    );

                } catch (Exception e) {
                    //Toast.makeText(getActivity(),"Exception",Toast.LENGTH_LONG).show();
                    Log.d("TAG", "catch exception");
                    return null;
                }

            }

            @Override
            protected void onStartLoading() {
                Log.d("LOG", "onStartLoading");
                if (cursor != null)
                    deliverResult(cursor);
                else
                    forceLoad();
            }

            @Override
            public void deliverResult(Cursor data) {
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        notifyAdapter(data);
        Log.d("LOG", "onLoadFinished");
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        notifyAdapter(null);
    }

    protected void notifyAdapter(final Cursor data) {

        if (data == null)
            return;

        int title = data.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE);
        int average = data.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_AVERAGE);
        int overview = data.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_OVERVIEW);
        int id = data.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID);
        int posterPath = data.getColumnIndex(MovieContract.MovieEntry.COLUMN_IMAGE_POSTER_PATH);

        //ArrayList<MovieModel> cursorList = new ArrayList<>();
        movieArrayList.clear();
        Log.d("LOG", String.valueOf(data.getCount()));

        for (int i = 0; i < data.getCount(); i++) {

            data.moveToPosition(i);
            MovieModel model = new MovieModel();

            model.setTitle(data.getString(title));
            model.setAverage(data.getString(average));
            model.setOverview(data.getString(overview));
            model.setId(data.getString(id));
            model.setPosterPath(data.getString(posterPath));

            movieArrayList.add(model);
        }

        movieListAdapter.setMovieList(movieArrayList);

    }

    @Override
    public void recyclerViewOnClick(MovieModel movieModel) {

        if (twoPane) {

            Toast.makeText(getActivity(), movieModel.getId(), Toast.LENGTH_SHORT).show();
            MovieDetailFragment movieDetailFragment = new MovieDetailFragment();

            Bundle args = new Bundle();
            args.putParcelable(BUNDLE_EXTRA, movieModel);
            movieDetailFragment.setArguments(args);

            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_tablet, movieDetailFragment)
                    .commit();

        } else {

            startActivity(new Intent(getActivity(), MovieDetail.class)
                    .putExtra(EXTRA_PARCELABLE, movieModel));

        }

    }

}