package com.example.user.moviesappfavorites;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.user.moviesappfavorites.model.Trailer;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerAdapterViewHolder> {

    private Trailer[] mTrailerData;
    public static TextView mTrailerListTextView = null;
    final String TMDB_TRAILER_BASE_URL = "https://www.youtube.com/watch?v=";
    Context context;

    public TrailerAdapter(Trailer[] trailer, Context context) {
        mTrailerData = trailer;
        this.context = context;
    }

//        public interface TrailerAdapterOnClickHandler {
//            void onClick(int adapterPosition, String mTrailerURL);
//        }

    public class TrailerAdapterViewHolder extends RecyclerView.ViewHolder{


        public TrailerAdapterViewHolder(View itemView) {
            super(itemView);
            mTrailerListTextView = (TextView) itemView.findViewById(R.id.tv_trailer_names);

        }

    }

    @NonNull
    @Override
    public TrailerAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.trailers_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        //inflate list item xml into a view
        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new TrailerAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrailerAdapterViewHolder holder, int position) {
        //set the Trailer for list item's position
        String TrailerToBind = mTrailerData[position].getName();
        final String TrailerToWatch = mTrailerData[position].getKey();
        mTrailerListTextView.setText(TrailerToBind);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // https://scottweber.com/2013/04/30/adding-click-listeners-to-views-in-adapters/
                Uri openTrailerVideo = Uri.parse(TMDB_TRAILER_BASE_URL + TrailerToWatch);
                Intent intent = new Intent(Intent.ACTION_VIEW, openTrailerVideo);
                context.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        if (null == mTrailerData) {
            return 0;
        }
        return mTrailerData.length;
    }
}
