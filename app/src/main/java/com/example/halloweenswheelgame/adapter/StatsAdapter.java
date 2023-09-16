package com.example.halloweenswheelgame.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.halloweenswheelgame.R;
import com.example.halloweenswheelgame.model.Stat;

import java.util.List;

public class StatsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    //member
    List<Stat> stats;

    //constructor


    public StatsAdapter(List<Stat> stats) {
        this.stats = stats;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        //inflate the row item layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_stats, parent, false);
        //return our stats view holder class
        return new StatsViewHolder((view)); //pass in the inflated view
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        //here we set values to view
        //get the current Stat and view holder
        Stat stat = stats.get(position);
        StatsViewHolder viewHolder = (StatsViewHolder) holder; //casting to Stats view holder

        //set values to views
        viewHolder.indexHolder.setText(stat.getIndex()); //index ie. 1st, 2nd etc
        viewHolder.pointHolder.setText(stat.getPoints()); //earned spin point worth
    }

    @Override
    public int getItemCount() {
        return stats.size();
    }

    //stat view holder
    public static class StatsViewHolder extends RecyclerView.ViewHolder{
        //members
        TextView indexHolder, pointHolder;
        public StatsViewHolder(@NonNull View itemView) {
            super(itemView);

            //assign members
            indexHolder = itemView.findViewById(R.id.indexHolder);
            pointHolder = itemView.findViewById(R.id.pointHolder);
        }
    }
}
