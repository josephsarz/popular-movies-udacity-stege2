package com.aghedo.popular_movies_udacity.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

import com.aghedo.popular_movies_udacity.R;
import com.aghedo.popular_movies_udacity.model.MovieModel;

import static com.aghedo.popular_movies_udacity.ui.activity.MovieListActivity.BUNDLE_EXTRA;
import static com.aghedo.popular_movies_udacity.ui.activity.MovieListActivity.EXTRA_PARCELABLE;

public class MovieDetail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_movie_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        if (getIntent().getExtras() != null) {
            MovieModel model = getIntent()
                    .getExtras()
                    .getParcelable(EXTRA_PARCELABLE);

            Bundle args = new Bundle();
            args.putParcelable(BUNDLE_EXTRA, model);

            MovieDetailFragment movieDetailFragment = new MovieDetailFragment();
            movieDetailFragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fl_movie_detail, movieDetailFragment)
                    .commit();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_movie_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }
}