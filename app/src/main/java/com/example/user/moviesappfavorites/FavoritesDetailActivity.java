package com.example.user.moviesappfavorites;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.moviesappfavorites.data.FavoritesContract;
import com.example.user.moviesappfavorites.data.FavoritesDbHelper;
import com.example.user.moviesappfavorites.model.Review;
import com.example.user.moviesappfavorites.model.Trailer;
import com.example.user.moviesappfavorites.utilities.NetworkUtils;
import com.example.user.moviesappfavorites.utilities.TheMovieDbJsonUtils;
import com.squareup.picasso.Picasso;

import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FavoritesDetailActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView mRecyclerViewReviews;
    private static Bundle mBundleRecyclerViewState;
    private final String KEY_RECYCLER_STATE = "recycler_state";
    private TrailerAdapter mTrailerAdapter;
    private ReviewAdapter mReviewAdapter;
    private Trailer[] jsonTrailerData;
    private Review[] jsonReviewData;
    private int id = 0;
    private String title = "";
    private String poster = "";
    private String rate = "";
    private String release = "";
    private String overview = "";
    private SQLiteDatabase mDb;
    String[] mProjection =
            {
                    FavoritesContract.FavoritesAdd._ID,
                    FavoritesContract.FavoritesAdd.COLUMN_MOVIE_ID
            };

    private String[] mSelectionArgs = {""};
    private String mSelectionClause;
    Uri mNewUri;


    @BindView(R.id.iv_detail_movie_poster)
    ImageView mMoviePosterDisplay;
    @BindView(R.id.tv_detail_title)
    TextView mMovieTitleDisplay;
    @BindView(R.id.tv_detail_rate)
    TextView mMovieRateDisplay;
    @BindView(R.id.tv_detail_release_date)
    TextView mMovieReleaseDisplay;
    @BindView(R.id.tv_plot_synopsis)
    TextView mMoviePlotSynopsisDisplay;
    @BindView(R.id.trailer_error_message)
    TextView mTrailerErrorMessage;
    @BindView(R.id.review_error_message)
    TextView mReviewErrorMessage;
    @BindView(R.id.add_to_favorites)
    Button mFavorites;
    @BindView(R.id.detail_scrollview)
    ScrollView mScrollView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites_detail);

        //favorites
        FavoritesDbHelper dbHelper = new FavoritesDbHelper(this);
        mDb = dbHelper.getWritableDatabase();

        //trailers
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_trailers);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        //set the layout manager
        mRecyclerView.setLayoutManager(layoutManager);
        //changes in content shouldn't change the layout size
        mRecyclerView.setHasFixedSize(true);

        //set trailer adapter for recycler view
        mRecyclerView.setAdapter(mTrailerAdapter);


        //reviews
        mRecyclerViewReviews = (RecyclerView) findViewById(R.id.recyclerview_reviews);

        LinearLayoutManager reviewsLayoutManager = new LinearLayoutManager(this);
        //set the layout manager
        mRecyclerViewReviews.setLayoutManager(reviewsLayoutManager);
        //changes in content shouldn't change the layout size
        mRecyclerViewReviews.setHasFixedSize(true);

        //set review adapter for recycler view
        mRecyclerViewReviews.setAdapter(mReviewAdapter);

        ButterKnife.bind(this);

        // https://stackoverflow.com/questions/41791737/how-to-pass-json-image-from-recycler-view-to-another-activity
        poster = getIntent().getStringExtra("poster");
        title = getIntent().getStringExtra("title");
        rate = getIntent().getStringExtra("rate");
        release = getIntent().getStringExtra("release");
        overview = getIntent().getStringExtra("overview");
        id = getIntent().getIntExtra("id",0);


        mMovieTitleDisplay.setText(title);
        mMoviePlotSynopsisDisplay.setText(overview);
        mMovieRateDisplay.setText(rate + getString(R.string.rate_out_of_ten));
        mMovieReleaseDisplay.setText(release);
        Picasso.get()
                .load(poster)
                .placeholder(R.drawable.image_loading)
                .error(R.drawable.image_not_found)
                .into(mMoviePosterDisplay);

        mFavorites.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (isMovieFavorited(String.valueOf(id))) {
                    removeFavorites(String.valueOf(id));

                    Context context = getApplicationContext();
                    CharSequence removedFavorites = "This movie is removed from your favorites.";
                    Toast toast = Toast.makeText(context, removedFavorites, Toast.LENGTH_SHORT);
                    toast.show();

                    mFavorites.setText(getString(R.string.add_to_favorites));
                } else {
                    addToFavorites(title, id, poster, rate, release, overview);
                    Context context = getApplicationContext();
                    CharSequence addedFavorites = "This movie is added to your favorites.";
                    Toast toast = Toast.makeText(context, addedFavorites, Toast.LENGTH_SHORT);
                    toast.show();

                    mFavorites.setText(getString(R.string.remove_from_favorites));
                }
            }
        });

        loadTrailerData();
        loadReviewData();
        isMovieFavorited(String.valueOf(id));
    }


    private void loadTrailerData() {
        String trailerId = String.valueOf(id);
        new FetchTrailerTask().execute(trailerId);
    }

    private void loadReviewData() {
        String reviewId = String.valueOf(id);
        new FetchReviewTask().execute(reviewId);
    }

    // Async Task for trailers
    public class FetchTrailerTask extends AsyncTask<String, Void, Trailer[]> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Trailer[] doInBackground(String... params) {
            if (params.length == 0){
                return null;
            }

            URL movieRequestUrl = NetworkUtils.buildTrailerUrl(id);

            try {
                String jsonMovieResponse = NetworkUtils.getResponseFromHttpUrl(movieRequestUrl);

                jsonTrailerData
                        = TheMovieDbJsonUtils.getTrailerInformationsFromJson(FavoritesDetailActivity.this, jsonMovieResponse);

                return jsonTrailerData;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Trailer[] trailerData) {
            if (trailerData != null) {
                mTrailerAdapter = new TrailerAdapter(trailerData, FavoritesDetailActivity.this);
                mRecyclerView.setAdapter(mTrailerAdapter);
            } else {
                mTrailerErrorMessage.setVisibility(View.VISIBLE);
            }

        }

    }


    //Async task for reviews
    public class FetchReviewTask extends AsyncTask<String, Void, Review[]> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Review[] doInBackground(String... params) {
            if (params.length == 0){
                return null;
            }

            URL movieRequestUrl = NetworkUtils.buildReviewUrl(id);

            try {
                String jsonMovieResponse = NetworkUtils.getResponseFromHttpUrl(movieRequestUrl);

                jsonReviewData
                        = TheMovieDbJsonUtils.getReviewInformationsFromJson(FavoritesDetailActivity.this, jsonMovieResponse);

                return jsonReviewData;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Review[] reviewData) {
            if (reviewData != null) {
                mReviewAdapter = new ReviewAdapter(reviewData);
                mRecyclerViewReviews.setAdapter(mReviewAdapter);
            } else {
                mReviewErrorMessage.setVisibility(View.VISIBLE);
            }
        }

    }


    //add to favorites
    private void addToFavorites(String name, int id, String poster, String rate, String release, String overview){
        //create a ContentValues instance to pass the values onto the insert query
        ContentValues cv = new ContentValues();
        //call put to insert the values with the keys
        cv.put(FavoritesContract.FavoritesAdd.COLUMN_MOVIE_ID, id);
        cv.put(FavoritesContract.FavoritesAdd.COLUMN_MOVIE_NAME, name);
        cv.put(FavoritesContract.FavoritesAdd.COLUMN_MOVIE_POSTER, poster);
        cv.put(FavoritesContract.FavoritesAdd.COLUMN_MOVIE_RATE, rate);
        cv.put(FavoritesContract.FavoritesAdd.COLUMN_MOVIE_RELEASE, release);
        cv.put(FavoritesContract.FavoritesAdd.COLUMN_MOVIE_OVERVIEW, overview);
        //run an insert query on TABLE_NAME with the ContentValues created
        //return mDb.insert(FavoritesContract.FavoritesAdd.TABLE_NAME, null, cv);
        mNewUri = getContentResolver().insert(
                FavoritesContract.FavoritesAdd.CONTENT_URI,
                cv
        );
    }

    //remove favorites
    private void removeFavorites(String id){
        mSelectionClause = FavoritesContract.FavoritesAdd.COLUMN_MOVIE_ID + " LIKE ?";
        String[] selectionArgs = new String[] {id};
        //return mDb.delete(FavoritesContract.FavoritesAdd.TABLE_NAME,
        //      FavoritesContract.FavoritesAdd.COLUMN_MOVIE_ID + "=" + id, null) > 0;
        getContentResolver().delete(
                FavoritesContract.FavoritesAdd.CONTENT_URI,
                mSelectionClause,
                selectionArgs
        );
    }

    //check if the id exist in database
    public boolean isMovieFavorited(String id){
        mSelectionClause = FavoritesContract.FavoritesAdd.COLUMN_MOVIE_ID + " = ?";
        mSelectionArgs[0] = id;
        Cursor mCursor = getContentResolver().query(
                FavoritesContract.FavoritesAdd.CONTENT_URI,
                mProjection,
                mSelectionClause,
                mSelectionArgs,
                null);

        if(mCursor.getCount() <= 0){
            mCursor.close();
            mFavorites.setText(getString(R.string.add_to_favorites));
            return false;
        }
        mCursor.close();
        mFavorites.setText(getString(R.string.remove_from_favorites));
        return true;
    }


    //https://stackoverflow.com/questions/28236390/recyclerview-store-restore-state-between-activities
    @Override
    protected void onPause() {
        mBundleRecyclerViewState = new Bundle();
        Parcelable listState = mRecyclerViewReviews.getLayoutManager().onSaveInstanceState();
        mBundleRecyclerViewState.putParcelable(KEY_RECYCLER_STATE, listState);

        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mBundleRecyclerViewState != null) {
            Parcelable listState = mBundleRecyclerViewState.getParcelable(KEY_RECYCLER_STATE);
            mRecyclerViewReviews.getLayoutManager().onRestoreInstanceState(listState);
        }
    }

}

