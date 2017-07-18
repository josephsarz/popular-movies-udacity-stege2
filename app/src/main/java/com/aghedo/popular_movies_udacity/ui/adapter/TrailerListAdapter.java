package com.aghedo.popular_movies_udacity.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aghedo.popular_movies_udacity.R;
import com.aghedo.popular_movies_udacity.model.TrailerModel;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TrailerListAdapter extends RecyclerView.Adapter<TrailerListAdapter.ViewHolder> {

    private ArrayList<TrailerModel> trailerArrayList = new ArrayList<>();
    private Context mContext;
    private ItemClickListener mItemClickListener;

    public TrailerListAdapter(Context mContext, ItemClickListener mItemClickListener) {
        this.mContext = mContext;
        this.mItemClickListener = mItemClickListener;

    }

    public void setTrailerList(ArrayList<TrailerModel> movieArrayList) {
        this.trailerArrayList = movieArrayList;
        this.notifyDataSetChanged();
    }

    @Override
    public TrailerListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(mContext).inflate(R.layout.trailer_item_list, parent, false);
        return new TrailerListAdapter.ViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(TrailerListAdapter.ViewHolder holder, int position) {
        holder.nameOfTrailer.setText(trailerArrayList.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return trailerArrayList.size();
    }

    public interface ItemClickListener {
        void trailerOnClick(TrailerModel movieModel);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.tv_name)
        TextView nameOfTrailer;

        ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void onClick(View v) {
            TrailerModel model = trailerArrayList.get(getAdapterPosition());
            mItemClickListener.trailerOnClick(model);
        }
    }

}