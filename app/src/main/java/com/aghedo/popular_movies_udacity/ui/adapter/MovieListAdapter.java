package com.aghedo.popular_movies_udacity.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aghedo.popular_movies_udacity.R;
import com.aghedo.popular_movies_udacity.model.MovieModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieListAdapter extends RecyclerView.Adapter<MovieListAdapter.ViewHolder> {

    private ArrayList<MovieModel> movieArrayList = new ArrayList<>();
    private Context mContext;
    private ItemClickListener mItemClickListener;

    public MovieListAdapter(Context mContext, ItemClickListener mItemClickListener) {
        this.mContext = mContext;
        this.mItemClickListener = mItemClickListener;

    }

    public void setMovieList(ArrayList<MovieModel> movieArrayList) {
        this.movieArrayList = movieArrayList;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(mContext).inflate(R.layout.movie_item_list, parent, false);
        return new ViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //http://image.tmdb.org/t/p/w185//nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg
        holder.movieTitle.setText(movieArrayList.get(position).getTitle());
        Picasso.with(mContext)
                .load("http://image.tmdb.org/t/p/w185/" + movieArrayList.get(position).getPosterPath())
                .error(R.drawable.no_image)
                //.placeholder(R.drawable.ic_info_black_24dp)
                .into(holder.posterImage);
    }

    @Override
    public int getItemCount() {
        return movieArrayList.size();
    }

    public interface ItemClickListener {
        void recyclerViewOnClick(MovieModel movieModel);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.iv_movie_poster_image)
        ImageView posterImage;

        @BindView(R.id.tv_movie_title)
        TextView movieTitle;

        ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void onClick(View v) {
            MovieModel model = movieArrayList.get(getAdapterPosition());
            mItemClickListener.recyclerViewOnClick(model);
        }
    }

}