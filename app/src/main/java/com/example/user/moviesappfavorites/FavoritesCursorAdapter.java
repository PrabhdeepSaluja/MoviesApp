package com.example.user.moviesappfavorites;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.user.moviesappfavorites.data.FavoritesContract;
import com.squareup.picasso.Picasso;

public class FavoritesCursorAdapter extends RecyclerView.Adapter<FavoritesCursorAdapter.FavoriteViewHolder> {

    private Cursor mCursor;
    private Context mContext;
    public String name = "";
    public String poster = "";
    public int id;

    /*Cursor Adapter-Adapter that exposes data from a Cursor to a ListView widget.
     The Cursor must include a column named "_id" or this class will not work.*/

    public FavoritesCursorAdapter(Context mContext){
        this.mContext = mContext;
    }

    @Override
    public FavoriteViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(mContext).inflate(R.layout.movies_list_item, parent, false);
        return new FavoriteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FavoriteViewHolder holder, int position){
        int idIndex = mCursor.getColumnIndex(FavoritesContract.FavoritesAdd._ID);
        int posterIndex = mCursor.getColumnIndex(FavoritesContract.FavoritesAdd.COLUMN_MOVIE_POSTER);

        mCursor.moveToPosition(position);

        id = mCursor.getInt(idIndex);
        poster = mCursor.getString(posterIndex);

        //SETTERS
        holder.itemView.setTag(id);
        Picasso.get()
                .load(poster)
                .placeholder(R.drawable.image_loading)
                .error(R.drawable.image_not_found)
                .into(holder.mMovieListImageView);


    }

    @Override
    public int getItemCount() {
        if (mCursor == null) {
            return 0;
        }
        return mCursor.getCount();
    }

    //update the cursor when a new data is added
    public Cursor swapCursor(Cursor c){
        if (mCursor == c){
            return null;
        }

        Cursor temp = mCursor;
        this.mCursor = c;

        if (c != null){
            this.notifyDataSetChanged();
        }
        return temp;
    }

    class FavoriteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView mMovieListImageView;

        public FavoriteViewHolder(View itemView){
            super(itemView);

            mMovieListImageView = (ImageView) itemView.findViewById(R.id.iv_movie_posters);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            Class destinationClass = FavoritesDetailActivity.class;

            String name = mCursor.getString(mCursor.getColumnIndex(FavoritesContract.FavoritesAdd.COLUMN_MOVIE_NAME));
            int movieId = mCursor.getInt(mCursor.getColumnIndex(FavoritesContract.FavoritesAdd.COLUMN_MOVIE_ID));
            String overview = mCursor.getString(mCursor.getColumnIndex(FavoritesContract.FavoritesAdd.COLUMN_MOVIE_OVERVIEW));
            String rate = mCursor.getString(mCursor.getColumnIndex(FavoritesContract.FavoritesAdd.COLUMN_MOVIE_RATE));
            String release = mCursor.getString(mCursor.getColumnIndex(FavoritesContract.FavoritesAdd.COLUMN_MOVIE_RELEASE));
            String poster = mCursor.getString(mCursor.getColumnIndex(FavoritesContract.FavoritesAdd.COLUMN_MOVIE_POSTER));

            Intent intentToStartDetailActivity = new Intent(mContext, destinationClass);
            intentToStartDetailActivity.putExtra(Intent.EXTRA_TEXT, adapterPosition);
            intentToStartDetailActivity.putExtra("title", name);
            intentToStartDetailActivity.putExtra("poster", poster);
            intentToStartDetailActivity.putExtra("rate", rate);
            intentToStartDetailActivity.putExtra("release", release);
            intentToStartDetailActivity.putExtra("overview", overview);
            intentToStartDetailActivity.putExtra("id", movieId);

            mContext.startActivity(intentToStartDetailActivity);

        }
    }

}