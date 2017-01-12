package app.android.example.com.poifinder.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import app.android.example.com.poifinder.R;
import app.android.example.com.poifinder.utilities.Place;

/**
 * Created by ruiguo on 1/11/16.
 */

/*
public class PlaceListAdapter extends RecyclerView.Adapter<PlaceListAdapter.MyViewHolder> {

    private static final String LOG_TAG = "PlaceListAdapter";
    private ArrayList<Place> gridData;
    private Context mContext;
    private int itemLayoutId;


    public PlaceListAdapter (Context context, ArrayList<Place> places){

        this.mContext = context;
        this.gridData= places;
    }
    public void setGridData(ArrayList <Place> placeList){
        this.gridData = placeList;
        notifyDataSetChanged();
        Log.d(LOG_TAG, "setData"+this.gridData.size());
    }

    public void swap (ArrayList list){
        if(gridData!=null){
            gridData.clear();
            gridData.addAll(list);
        }
        else{
            gridData= list;
        }
        notifyDataSetChanged();
    }


    @Override
    public PlaceListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.place_list_content,parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PlaceListAdapter.MyViewHolder holder, int position) {
        Place place = gridData.get(position);
        if(place.getPhoto().equals("No preview photos")==false) {
            Picasso.with(mContext).load(place.getPhoto()).into(holder.thumbnail);
        }
        else {
            holder.thumbnail.setImageResource(R.mipmap.ic_launcher);
        }

        holder.nameView.setText(place.getPlace_name());
        holder.ratingBar.setRating((float) place.getPlace_rating());
    }

    @Override
    public int getItemCount() {
        return gridData.size();
    }
    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public ImageView thumbnail;
        public TextView nameView;
        public RatingBar ratingBar;

        public MyViewHolder(View itemView) {
            super(itemView);
            thumbnail = (ImageView) itemView.findViewById(R.id.grid_item_image);
            nameView = (TextView)itemView.findViewById(R.id.listView_placeName);
            ratingBar= (RatingBar)itemView.findViewById(R.id.listView_placeRating);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    if (mTwoPane) {
                        selectedMovie= (Movie) movieList.get(getPosition());
                        Log.d(TAG, "select no"+getItemCount());
                        Bundle arguments = new Bundle();
                        if(selectedMovie!=null) {
                            arguments.putString("title", selectedMovie.getTitle());
                            arguments.putInt("id", selectedMovie.getId());
                            arguments.putString("poster_url", selectedMovie.getPoster());
                            arguments.putString("release_year", selectedMovie.getYear());
                            arguments.putString("vote_average", selectedMovie.getVote_average());
                            arguments.putString("movie_details", selectedMovie.getMovie_details());
                            MovieDetailFragment fragment = new MovieDetailFragment();
                            fragment.setArguments(arguments);
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.movie_detail_container, fragment)
                                    .commit();
                        }
                        else{
                            Log.d(TAG, "passing object is null");
                            Toast.makeText(getApplicationContext(), "passing object is null", Toast.LENGTH_LONG).show();

                        }

                    } else {
                        Context context = v.getContext();

                        Movie selectedMovie = (Movie) movieList.get(getPosition());
                        Log.d(TAG, "select no"+getItemCount());

                        Intent intent = new Intent(context, MovieDetailActivity.class);
                        intent.putExtra(MovieDetailFragment.ARG_ITEM_ID, selectedMovie.getId());
                        intent.putExtra("title", selectedMovie.getTitle());
                        intent.putExtra("id", selectedMovie.getId());
                        intent.putExtra("poster_url", selectedMovie.getPoster());
                        intent.putExtra("release_year", selectedMovie.getYear());
                        intent.putExtra("vote_average", selectedMovie.getVote_average());
                        intent.putExtra("movie_details", selectedMovie.getMovie_details());
                        context.startActivity(intent);
                    }

                }
            });
                }
            });
        }

        @Override
        public void onClick(View view) {

        }
    }
}
*/