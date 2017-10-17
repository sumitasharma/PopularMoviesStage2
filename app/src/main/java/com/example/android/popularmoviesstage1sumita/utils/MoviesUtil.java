package com.example.android.popularmoviesstage1sumita.utils;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Class for Movie Utility Functions
 */

public class MoviesUtil {

    public static final String TRAILER_URL = "http://www.youtube.com/watch?v=";
    static final String VIDEOURLPREFIX = "https://www.youtube.com/watch?v=";
    static final String VIDEOTHUMBNAILPREFIX = "https://i.ytimg.com/vi/";
    static final String VIDEOTHUMBNAILPOSTFIX = "/hqdefault.jpg";
    private static final String TAG = MoviesUtil.class.getSimpleName();
    /**
     * Enter your API_KEY in String MOVIES_API_KEY for this Project to work
     */
    private static final String MOVIES_API_KEY =
            "";
    private static final String MOVIES_API_BASE_URL =
            "https://api.themoviedb.org/3/movie/";
    private static final String QUERY_PARAM = "api_key";
    private static final String RESULTS = "results";
    private static final String ID = "id";
    private static final String TITLE = "title";
    private static final String POSTER_PATH = "poster_path";
    private static final String OVERVIEW = "overview";
    private static final String RELEASE_DATE = "release_date";
    private static final String VOTE_AVERAGE = "vote_average";
    private static final String IMAGE_PATH = "http://image.tmdb.org/t/p/";
    private static final String IMAGE_SIZE = "w780";
    private static final String PATH_VIDEOS = "videos";
    private static final String PATH_REVIEWS = "reviews";
    // Variables defined for Movie Videos
    private static final String KEY = "key";
    // Variables defined for Movie Reviews
    private static final String AUTHOR = "author";
    private static final String CONTENT = "content";
    private static final String REVIEW_URL = "url";

    /**
     * Builds the URL for Movies
     */
    public static URL buildUrl(String movieQuery) {
        Uri builtUri = Uri.parse(MOVIES_API_BASE_URL).buildUpon()
                .appendPath(movieQuery)
                .appendQueryParameter(QUERY_PARAM, MOVIES_API_KEY)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static URL buildUrlForVideos(String movieQuery) {
        Uri builtUriForVideos = Uri.parse(MOVIES_API_BASE_URL).buildUpon()
                .appendPath(movieQuery)
                .appendQueryParameter(QUERY_PARAM, MOVIES_API_KEY).appendPath(PATH_VIDEOS).build();
        URL url = null;
        try {
            url = new URL(builtUriForVideos.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static URL buildUrlForReviews(String movieQuery) {
        Uri builtUriForReviews = Uri.parse(MOVIES_API_BASE_URL).buildUpon()
            .appendPath(movieQuery)
            .appendQueryParameter(QUERY_PARAM, MOVIES_API_KEY).appendPath(PATH_REVIEWS).build();
        URL url = null;
        try {
        url = new URL(builtUriForReviews.toString());
    } catch (MalformedURLException e) {
        e.printStackTrace();
    }
        return url;
}

    // Connecting to Internet
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();
            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    // Convert JSON string to MovieDetails for SortBy returning MovieDetails[] array for Main Activity
    public static MovieDetails[] convertJsonToMovieSortBy(String fullJsonMoviesData) throws JSONException {
        //Convert fullJsonMoviesData to JsonObject
        JSONObject movieDetails = new JSONObject(fullJsonMoviesData);
        JSONArray movieDetailsArray = movieDetails.getJSONArray(RESULTS);
        MovieDetails[] results = new MovieDetails[movieDetailsArray.length()];
        for (int i = 0; i < movieDetailsArray.length(); i++) {
            MovieDetails mDetails = new MovieDetails();
            mDetails.setId(movieDetailsArray.getJSONObject(i).getInt(ID));
            mDetails.setMovieTitle(movieDetailsArray.getJSONObject(i).getString(TITLE));
            mDetails.setPosterPath(createPosterPath(movieDetailsArray.getJSONObject(i).getString(POSTER_PATH)));
            mDetails.setSynopsis(movieDetailsArray.getJSONObject(i).getString(OVERVIEW));
            mDetails.setReleaseDate(movieDetailsArray.getJSONObject(i).getString(RELEASE_DATE));
            mDetails.setRating(movieDetailsArray.getJSONObject(i).getString(VOTE_AVERAGE));
            results[i] = mDetails;
        }
        return results;
    }

    // Convert JSON string to MovieDetails for a particular Movie - For Movie Detail View
    public static MovieDetails convertJsonToMovieIdDetail(String fullJsonMoviesData) throws JSONException {
        //Convert IdJsonMoviesData to JsonObject
        JSONObject movieIdDetails = new JSONObject(fullJsonMoviesData);
        MovieDetails mDetails = new MovieDetails();
        mDetails.setId(movieIdDetails.getInt(ID));
        mDetails.setMovieTitle(movieIdDetails.getString(TITLE));
        mDetails.setPosterPath(createPosterPath(movieIdDetails.getString(POSTER_PATH)));
        mDetails.setSynopsis(movieIdDetails.getString(OVERVIEW));
        mDetails.setReleaseDate(movieIdDetails.getString(RELEASE_DATE));
        mDetails.setRating(movieIdDetails.getString(VOTE_AVERAGE));
        return mDetails;
    }

    private static MovieDetails convertJsonToMoviesVideos(String fullJsonMoviesData, MovieDetails movieDetails) throws JSONException {
        //Convert fullJsonMoviesData to JsonObject
        JSONObject movieDetailsJson = new JSONObject(fullJsonMoviesData);
        JSONArray movieDetailsArray = movieDetailsJson.getJSONArray(RESULTS);
        MovieVideosDetail[] results = new MovieVideosDetail[movieDetailsArray.length()];
        for (int i = 0; i < movieDetailsArray.length(); i++) {
            MovieVideosDetail mDetails = new MovieVideosDetail();
            mDetails.setID(movieDetailsArray.getJSONObject(i).getString(ID));
            mDetails.setKey(movieDetailsArray.getJSONObject(i).getString(KEY));
            results[i] = mDetails;
        }
        movieDetails.setMovieVideosDetail(results);
        return movieDetails;
    }

    private static MovieDetails convertJsonToMoviesReview(String fullJsonMoviesData, MovieDetails movieDetails) throws JSONException {
        //Convert fullJsonMoviesData to JsonObject
        JSONObject movieDetailsJson = new JSONObject(fullJsonMoviesData);
        JSONArray movieDetailsArray = movieDetailsJson.getJSONArray(RESULTS);
        MovieReviewsDetail[] results = new MovieReviewsDetail[movieDetailsArray.length()];
        for (int i = 0; i < movieDetailsArray.length(); i++) {
            MovieReviewsDetail mDetails = new MovieReviewsDetail();
            mDetails.setId(movieDetailsArray.getJSONObject(i).getString(ID));
            mDetails.setAuthor(movieDetailsArray.getJSONObject(i).getString(AUTHOR));
            mDetails.setContent(movieDetailsArray.getJSONObject(i).getString(CONTENT));
            mDetails.setUrl(movieDetailsArray.getJSONObject(i).getString(REVIEW_URL));
            results[i] = mDetails;
        }
        movieDetails.setMovieReviewsDetail(results);
        return movieDetails;
    }

    public static MovieDetails getCompleteMovieDetails(String movieId) {
        MovieDetails movieDetails = new MovieDetails();
        URL movieURL = MoviesUtil.buildUrl(movieId);
        URL reviewsURL = MoviesUtil.buildUrlForReviews(movieId);
        URL videosURL = MoviesUtil.buildUrlForVideos(movieId);

        try {
            //getResponse for movieURL and setMovieDetails for movieURL
            String movieResponse = MoviesUtil.getResponseFromHttpUrl(movieURL);
            movieDetails = MoviesUtil.convertJsonToMovieIdDetail(movieResponse);
            String movieReviewResponse = MoviesUtil.getResponseFromHttpUrl(reviewsURL);
            movieDetails = MoviesUtil.convertJsonToMoviesReview(movieReviewResponse, movieDetails);
            String movieVideoResponse = MoviesUtil.getResponseFromHttpUrl(videosURL);
            movieDetails = MoviesUtil.convertJsonToMoviesVideos(movieVideoResponse,movieDetails);

        } catch (IOException | JSONException e) {
            Log.i(TAG,"Exception caught"+ e.getMessage());
            Log.e(TAG, e.getMessage());
        }
        return movieDetails;
    }

    // Function createPosterPath creates posterPath
    private static String createPosterPath(String posterPath) {
        // Had to appendPath(posterPath.substring(1)), otherwise uri was returning an extra "/".
        Uri uri = Uri.parse(IMAGE_PATH).buildUpon().appendPath(IMAGE_SIZE).appendPath(posterPath.substring(1)).build();
        return uri.toString();
    }
}
