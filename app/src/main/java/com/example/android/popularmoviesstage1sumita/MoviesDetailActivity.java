package com.example.android.popularmoviesstage1sumita;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmoviesstage1sumita.data.MovieContract;
import com.example.android.popularmoviesstage1sumita.utils.MovieDetails;
import com.example.android.popularmoviesstage1sumita.utils.MoviesUtil;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;

public class MoviesDetailActivity extends AppCompatActivity {
    private TextView movieTitle;
    private ImageView moviePoster;
    private TextView movieSynopsis;
    private TextView movieRatings;
    private TextView movieReleaseDate;
    private int movieId;
    private Button saveAsFavorite;
    private static final String WHERE_CLAUSE = "movieID = ";
    private String where_clause;
    private final String POSTER_PATH = "posterpath";
    private final String TAG = MoviesDetailActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies_detail);
        Cursor mCursor;

        //Bundle extras = getIntent().getExtras();
        movieId = getIntent().getIntExtra("MovieId", 0);
        setContentView(R.layout.activity_movies_detail);
        movieTitle = (TextView) findViewById(R.id.title);
        moviePoster = (ImageView) findViewById(R.id.poster);
        movieSynopsis = (TextView) findViewById(R.id.synopsis);
        movieRatings = (TextView) findViewById(R.id.user_rating);
        movieReleaseDate = (TextView) findViewById(R.id.release_date);
        saveAsFavorite = (Button) findViewById(R.id.favorite_movie);
        //Checking the status of save as favorite movies from Content Provider
        Uri uri = MovieContract.MovieEntry.CONTENT_URI;
        where_clause = WHERE_CLAUSE + movieId + ";";
        try {
            mCursor = getContentResolver().query(uri, null, where_clause, null, null);
            if (mCursor.getCount() == 0) {
                saveAsFavorite.setText(R.string.save_as_favorite);
            }
            else
                saveAsFavorite.setText(R.string.remove_from_favorite);
            mCursor.close();
        } catch(Exception e) {
            e.printStackTrace();
        }

        // FetchMovies function is called for the Movie Details of the Movie clicked on Main Menu by passing
        // the MovieId this Activity received from onClick function.

        new FetchMovies(this).execute(String.valueOf(movieId));
    }

    public void saveAsFavorite(View view) {
        final String saveMovie = "Save As Favorite";
        final String removeMovie = "Remove From Favorite";

        // Getting Save as favorite flag from Content Provider
        if (saveAsFavorite.getText().equals(saveMovie)) {
            SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
            String posterPath = sharedPref.getString(POSTER_PATH, null);

            // Create new empty ContentValues object
            ContentValues contentValues = new ContentValues();

            // Put the task description and selected mPriority into the ContentValues
            contentValues.put(MovieContract.MovieEntry.COLUMN_ID, (movieId));
            contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE, (movieTitle).getText().toString());
            contentValues.put(MovieContract.MovieEntry.COLUMN_RATING, (movieRatings).getText().toString());
            contentValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, (movieReleaseDate).getText().toString());
            contentValues.put(MovieContract.MovieEntry.COLUMN_SYNOPSIS, (movieSynopsis).getText().toString());
            contentValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, posterPath);
            // Insert the content values via a ContentResolver
            getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, contentValues);
            saveAsFavorite.setText(R.string.remove_from_favorite);
        }

        // COMPLETED (8) Display the URI that's returned with a Toast
        // [Hint] Don't forget to call finish() to return to MainActivity after this insert is complete
        else if (saveAsFavorite.getText().equals(removeMovie)) {
            saveAsFavorite.setText(R.string.save_as_favorite);
            int rowDeleted = getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI,where_clause, null);
            if (rowDeleted == 0) {
                Log.i(TAG, "Favorite Movie not Deleted");
            }

        }
    }

/**
 * Make Class FetchMovies for asynchronous task of getting the Movie Details from API Key
 */
private class FetchMovies extends AsyncTask<String, Void, MovieDetails> {
    private final Context mContext;

    FetchMovies(Context context) {
        mContext = context;
    }

    @Override
    protected MovieDetails doInBackground(String... params) {

        URL movieURL = MoviesUtil.buildUrl(params[0]);
        //Testing
        URL reviewsURL = MoviesUtil.buildUrlForReviews(params[0]);
        URL videosURL = MoviesUtil.buildUrlForVideos(params[0]);

        Log.i(TAG,"reviewsURL is:" + reviewsURL.toString());
        Log.i(TAG,"videosURL is:" + videosURL.toString());


        MovieDetails resultMovieDetail = new MovieDetails();
        try {
            String movieResponse = MoviesUtil.getResponseFromHttpUrl(movieURL);
            resultMovieDetail = MoviesUtil.convertJsonToMovieIdDetail(movieResponse);
        } catch (IOException | JSONException e) {
            Log.e(TAG, e.getMessage());
        }
        return resultMovieDetail;
    }

    /**
     * Setting all the details in the XML file
     */
    @Override
    protected void onPostExecute(MovieDetails movieDetails) {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(POSTER_PATH, movieDetails.getPosterPath());
        editor.apply();
        movieTitle.setText(movieDetails.getMovieTitle());
        movieRatings.setText(movieDetails.getRating());
        movieReleaseDate.setText(movieDetails.getReleaseDate());
        movieSynopsis.setText(movieDetails.getSynopsis());
        Picasso.with(mContext).load(movieDetails.getPosterPath()).into(moviePoster);
    }
}

}
