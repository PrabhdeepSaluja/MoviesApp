package com.example.user.moviesappfavorites.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class FavoritesContract {
    public static final String AUTHORITY = "com.example.user.moviesappfavorites";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final String PATH_FAVORITES = "favorites";

    public static final class FavoritesAdd implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORITES).build();

        public static final String TABLE_NAME = "favorites";

        public static final String COLUMN_MOVIE_ID = "movieId";
        public static final String COLUMN_MOVIE_NAME = "movieName";
        public static final String COLUMN_MOVIE_POSTER = "moviePoster";
        public static final String COLUMN_MOVIE_RATE = "movieRate";
        public static final String COLUMN_MOVIE_RELEASE = "movieRelease";
        public static final String COLUMN_MOVIE_OVERVIEW = "movieOverview";
    }
}
