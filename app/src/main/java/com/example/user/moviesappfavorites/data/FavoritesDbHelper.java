package com.example.user.moviesappfavorites.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class FavoritesDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "favorites.db";
    private static final int DATABASE_VERSION = 3; //increment this when you update the database

    //constructor
    public FavoritesDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_FAVORITES_TABLE = "CREATE TABLE " +
                FavoritesContract.FavoritesAdd.TABLE_NAME + " (" +
                FavoritesContract.FavoritesAdd._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                FavoritesContract.FavoritesAdd.COLUMN_MOVIE_ID + " INTEGER NOT NULL," +
                FavoritesContract.FavoritesAdd.COLUMN_MOVIE_NAME + " TEXT NOT NULL," +
                FavoritesContract.FavoritesAdd.COLUMN_MOVIE_POSTER + " TEXT NOT NULL," +
                FavoritesContract.FavoritesAdd.COLUMN_MOVIE_RATE + " TEXT NOT NULL," +
                FavoritesContract.FavoritesAdd.COLUMN_MOVIE_RELEASE + " TEXT NOT NULL," +
                FavoritesContract.FavoritesAdd.COLUMN_MOVIE_OVERVIEW + " TEXT NOT NULL" +
                "); ";
        db.execSQL(SQL_CREATE_FAVORITES_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + FavoritesContract.FavoritesAdd.TABLE_NAME);
        onCreate(db);
    }

}
