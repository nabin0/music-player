package com.nabin.musik.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.nabin.musik.R;
import com.nabin.musik.adapters.FavoriteSongsAdapter;
import com.nabin.musik.adapters.SongsListAdapter;
import com.nabin.musik.db.RoomDb;
import com.nabin.musik.interfaces.SongListRecyclerViewItemClick;
import com.nabin.musik.models.SongModel;

import java.util.ArrayList;

public class FavouriteMusicActivity extends AppCompatActivity implements SongListRecyclerViewItemClick {

    private RoomDb database;
    private ArrayList<SongModel> favouriteSongs;
    private RecyclerView recyclerView;
    private FavoriteSongsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite_music);
        database = RoomDb.getInstance(this);
        favouriteSongs = new ArrayList<>();
        initRecyclerView();
    }

    private void initRecyclerView() {
        recyclerView = findViewById(R.id.favouriteSongsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FavoriteSongsAdapter(favouriteSongs, this, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onRecyclerItemClick(int position) {
        Intent intent = new Intent(this, PlaySongActivity.class);
        intent.putExtra("position", position);
        intent.putExtra("my_songs", favouriteSongs);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        favouriteSongs = (ArrayList<SongModel>) database.dao().getAllFavouriteSongs();
        adapter.updateSongs(favouriteSongs);
    }
}