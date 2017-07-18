package com.aghedo.popular_movies_udacity.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class MovieProvider extends ContentProvider {

    private final static int MOVIE = 100;
    private final static int MOVIE_WITH_ID = 101;
    private static UriMatcher sUriMatcher = buildUriMatcher();
    private MovieDbHelper dbHelper;

    private static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.MOVIE_PATH, MOVIE);
        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.MOVIE_PATH + "/#", MOVIE_WITH_ID);

        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        dbHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri,
                        @Nullable String[] projection,
                        @Nullable String selection,
                        @Nullable String[] selectionArgs,
                        @Nullable String sortOrder) {

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        int match = sUriMatcher.match(uri);
        Cursor rCursor;
        switch (match) {

            case MOVIE:
                rCursor = db.query(MovieContract.MovieEntry.TABLE_NAME,
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;


            case MOVIE_WITH_ID:

                String id = uri.getPathSegments().get(1);
                String mSelection = MovieContract.MovieEntry._ID + "=?";
                String[] mSelectionArg = {id};

                rCursor = db.query(MovieContract.MovieEntry.TABLE_NAME,
                        projection, mSelection, mSelectionArg, null, null, sortOrder);

                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);

        }

        rCursor.setNotificationUri(getContext().getContentResolver(), uri);

        return rCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);

        Uri returnUri = null;

        switch (match) {

            case MOVIE:
                long id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, values);

                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(MovieContract.MovieEntry.MOVIE_URI, id);
                } else {
                    throw new SQLException("Failed to insert");
                }

                break;

            case MOVIE_WITH_ID:
                break;

        }

        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        int itemsDeleted = 0;
        String id;

        switch (match) {

            case MOVIE:
                itemsDeleted = db.delete(MovieContract.MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;

            case MOVIE_WITH_ID:
                id = uri.getPathSegments().get(1);
                itemsDeleted = db.delete(MovieContract.MovieEntry.TABLE_NAME, "_id=?", new String[]{id});
                break;

        }

        getContext().getContentResolver().notifyChange(uri, null);

        return itemsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}