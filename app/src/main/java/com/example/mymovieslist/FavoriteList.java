package com.example.mymovieslist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mymovieslist.Database.FavoriteMovieDatabase;

import java.util.ArrayList;
import java.util.List;

import static androidx.recyclerview.widget.DividerItemDecoration.VERTICAL;

public class FavoriteList extends AppCompatActivity implements FavoriteMovieAdapter.favMovieClickListener {
    //memeber variable declaration
   FavoriteMovieDatabase myDatabase;
   RecyclerView favoriteRecyclerview;
   FavoriteMovieAdapter myfavAdapter;
   List<Movie> favList;
   private static final String TAG=FavoriteList.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_list);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //binding recycler view
        favoriteRecyclerview=(RecyclerView) findViewById(R.id.fav_movie_recycler);


        favoriteRecyclerview.setLayoutManager(new LinearLayoutManager(this));
        //initialiazation of adapter
        myfavAdapter=new FavoriteMovieAdapter(this,this);
        //setting adapter to the recycler view
        favoriteRecyclerview.setAdapter(myfavAdapter);
        //mmethod to control the swipping action
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int direction) {
                AppExecutors.getInstance().getDiskIo().execute(new Runnable() {
                    @Override
                    public void run() {
                        int position = viewHolder.getAdapterPosition();
                        List<Movie> favList = myfavAdapter.getFavMovieList();
                        myDatabase.myFavoriteMovieDao().deleteFavMovie(favList.get(position));

                    }
                });

            }
        }).attachToRecyclerView(favoriteRecyclerview);

        //initialization of database
        myDatabase=FavoriteMovieDatabase.getInstance(getApplicationContext());
        retrieveFavMovieList();

    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    private void retrieveFavMovieList() {
        //livedata observe the changes in databse
        final LiveData<List<Movie>> list=myDatabase.myFavoriteMovieDao().loadAllFavoriteMovies();
        list.observe(this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(List<Movie> movies) {
                Log.d(TAG,"Receiving database update from LiveData");
                myfavAdapter.setTasks(movies);
            }
        });


    }

    @Override
    public void onMovieClickListener(int itemId) {
        Toast.makeText(this, "Clicked", Toast.LENGTH_SHORT).show();
    }
}
