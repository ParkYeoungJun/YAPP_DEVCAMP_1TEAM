package com.example.park.yapp_1team.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.park.yapp_1team.R;
import com.example.park.yapp_1team.items.MovieListItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HunJin on 2017-08-22.
 */

public class MainRecyclerViewAdapter extends RecyclerView.Adapter<MainRecyclerViewAdapter.ViewHolder> {

    private List<MovieListItem> datas = new ArrayList<>();
    private List<String> movieName = new ArrayList<>();

    private Context mContext;

    public MainRecyclerViewAdapter(Context context) {
        this.mContext = context;
    }

    public void add(MovieListItem MovieListItem) {
        datas.add(MovieListItem);
    }

    public List<String> get() {
        return movieName;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView, imageView2;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.image1_recyclerview2);
            imageView2 = (ImageView) itemView.findViewById(R.id.image2_recyclerview2);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.itemview_main, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final MovieListItem MovieListItem = datas.get(position);

        if(MovieListItem.getURL().isEmpty()) {
            Glide.with(mContext).load(R.drawable.ic_panorama_black_24dp).into(holder.imageView);
        } else {
            Glide.with(mContext).load(MovieListItem.getURL()).into(holder.imageView);
        }

        // maybe save status
        if (MovieListItem.getCheck() == 1) {
            holder.imageView.setColorFilter(Color.parseColor("#99000000"));
            holder.imageView2.setVisibility(View.VISIBLE);
        } else {
            holder.imageView.setColorFilter(Color.parseColor("#00000000"));
            holder.imageView2.setVisibility(View.INVISIBLE);
        }

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (MovieListItem.getCheck() == 0) {                                  //check 안되어있으면

                    holder.imageView.setColorFilter(Color.parseColor("#99000000"));
                    MovieListItem.setCheck(1);
                    movieName.add(MovieListItem.getName());
                    Log.e("aaaaaaaa", "" + movieName);

                    holder.imageView2.setVisibility(View.VISIBLE);
                    //textview1.setText(movieName.size());
                } else if (MovieListItem.getCheck() == 1) {
                    holder.imageView.setColorFilter(Color.parseColor("#00000000"));
                    MovieListItem.setCheck(0);
                    movieName.remove(MovieListItem.getName());
                    Log.e("aaaaaaaa", "" + movieName);

                    holder.imageView2.setVisibility(View.INVISIBLE);
                    //   textview1.setText(movieName.size());
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return datas.size();
    }
}
