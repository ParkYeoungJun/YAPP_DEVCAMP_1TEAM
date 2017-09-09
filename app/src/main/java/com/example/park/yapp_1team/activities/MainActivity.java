package com.example.park.yapp_1team.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.park.yapp_1team.R;
import com.example.park.yapp_1team.adapters.MainRecyclerViewAdapter;
import com.example.park.yapp_1team.interfaces.CheckEvent;
import com.example.park.yapp_1team.items.MovieInfoListItem;
import com.example.park.yapp_1team.items.MovieListItem;
import com.example.park.yapp_1team.items.TheaterCodeItem;
import com.example.park.yapp_1team.network.MovieInfoCrawling;
import com.example.park.yapp_1team.network.MovieListCrawling;
import com.example.park.yapp_1team.network.TheaterInfoCrawling;
import com.example.park.yapp_1team.utils.CustomComparator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.example.park.yapp_1team.utils.Strings.CGV;
import static com.example.park.yapp_1team.utils.Strings.LOTTE;
import static com.example.park.yapp_1team.utils.URL.NAVER_SELECT;
import static com.example.park.yapp_1team.utils.URL.NAVER_URL;

public class MainActivity extends BaseActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private RecyclerView movieListRecyclerView;
    private MainRecyclerViewAdapter movieListRecyclerViewAdapter;
    private LinearLayout layoutShowMovieTIme;

    private List<MovieListItem> dataArray;
    private List<String> movieNames = new ArrayList<>();

    private CustomComparator customComparator = new CustomComparator();

    private CheckEvent checkEvent = new CheckEvent() {
        @Override
        public void check(int position, ImageView imgMovieThumbnail, ImageView imgLine) {
            List<MovieListItem> items = movieListRecyclerViewAdapter.getDatas();
            Log.e(TAG, items.get(position).getName() + " " + items.get(position).getOriginalOrder() + " : " + items.get(position).getCurrentOrder());
            if (items.get(position).getCheck() == 0) {
                movieListRecyclerViewAdapter.setCurrentOrder(movieListRecyclerViewAdapter.getCurrentOrder() + 1);
                items.get(position).setCheck(1);
                movieNames.add(items.get(position).getName());
                items.get(position).setCurrentOrder(movieListRecyclerViewAdapter.getCurrentOrder());
            } else {
                items.get(position).setCheck(0);
                movieNames.remove(items.get(position).getName());
                items.get(position).setCurrentOrder(items.get(position).getOriginalOrder());
            }

            Collections.sort(items, customComparator);
            movieListRecyclerViewAdapter.addList(items);
            movieListRecyclerViewAdapter.notifyDataSetChanged();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        statusBarChange();
        initialize();
        event();
    }

    private void event() {
        layoutShowMovieTIme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (movieNames.size() > 0) {
                    Intent it = new Intent(getApplicationContext(), SelectMovieInfoActivity.class);
                    it.putExtra("names", (Serializable) movieNames);
                    startActivity(it);
                } else {
                    Toast.makeText(getApplicationContext(), "영화를 한 개 이상 선택하세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initialize() {
        movieListRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_start);
        layoutShowMovieTIme = (LinearLayout) findViewById(R.id.layout_show_movie_time);
        movieListRecyclerView.setHasFixedSize(true);
        movieListRecyclerViewAdapter = new MainRecyclerViewAdapter(this, checkEvent);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
        movieListRecyclerView.setLayoutManager(gridLayoutManager);

        dataArray = movieCrawling();
        Iterator iterator = dataArray.iterator();
        while (iterator.hasNext()) {
            movieListRecyclerViewAdapter.add((MovieListItem) iterator.next());
        }
        movieListRecyclerView.setAdapter(movieListRecyclerViewAdapter);
    }

    private List<MovieListItem> movieCrawling() {
        MovieInfoCrawling test = new MovieInfoCrawling(NAVER_URL, NAVER_SELECT);

        List<MovieListItem> items = new ArrayList<>();
        try {
            items = (ArrayList<MovieListItem>) test.execute().get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        return items;
    }


    /**
     * 이게 왜 있는지 이해가 안감
     */
    private void crawling() {
        // cgv theater info crawling
        TheaterInfoCrawling theaterInfoCrawling = new TheaterInfoCrawling(CGV);
        ArrayList<TheaterCodeItem> cgv_theater = new ArrayList<>();

        try {
            cgv_theater = (ArrayList<TheaterCodeItem>) theaterInfoCrawling.execute().get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        // cgv movie time crawling
        MovieListCrawling movieListCrawling = new MovieListCrawling(cgv_theater.get(1).getTheater(), cgv_theater.get(1).getArea());
        ArrayList<MovieInfoListItem> cgv_movie = new ArrayList<>();

        try {
            cgv_movie = (ArrayList<MovieInfoListItem>) movieListCrawling.execute().get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        // lotte theater info crawling
        theaterInfoCrawling = new TheaterInfoCrawling(LOTTE);
        ArrayList<TheaterCodeItem> lotte_theater = new ArrayList<>();

        try {
            lotte_theater = (ArrayList<TheaterCodeItem>) theaterInfoCrawling.execute().get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        // lotte movie time crawling
        movieListCrawling = new MovieListCrawling(lotte_theater.get(1).getTheater(), lotte_theater.get(1).getArea(), lotte_theater.get(1).getDetailarea());
        ArrayList<MovieInfoListItem> lotte_movie = new ArrayList<>();

        try {
            lotte_movie = (ArrayList<MovieInfoListItem>) movieListCrawling.execute().get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
