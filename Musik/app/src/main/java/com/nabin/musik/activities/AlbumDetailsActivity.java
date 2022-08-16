package com.nabin.musik.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.nabin.musik.R;
import com.nabin.musik.adapters.SongsListAdapter;
import com.nabin.musik.fragments.MySongsFragment;
import com.nabin.musik.interfaces.SongListRecyclerViewItemClick;
import com.nabin.musik.models.SongModel;

import java.util.ArrayList;

public class AlbumDetailsActivity extends AppCompatActivity implements SongListRecyclerViewItemClick {
    // Views
    private RecyclerView mAlbumSongsRecyclerView;
    private SongsListAdapter mSongsAdapter;
    private ImageView mAlbumArt;
    private TextView mAlbumName;

    //Vars
    private ArrayList<SongModel> mAlbumSongs = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_details);

        mAlbumSongsRecyclerView = findViewById(R.id.songsDetailListRecyclerView);
        mAlbumArt = findViewById(R.id.albumArtImageAlbumDetail);
        mAlbumName = findViewById(R.id.albumNameAlbumDetailTv);

        //Get intent and filter songs for particular song
        String albumName = null;
        if (getIntent().hasExtra("album_name")) {
            albumName = getIntent().getStringExtra("album_name");
        }
        filterAlbumSongs(albumName);

        // Set album art and name
        mAlbumName.setText(albumName);
        Uri uri = Uri.parse(mAlbumSongs.get(0).getImagePath());
        Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (uri != null) {
                                Glide.with(AlbumDetailsActivity.this)
                                        .load(uri)
                                        .placeholder(R.drawable.music_logo)
                                        .into(mAlbumArt);
                            } else {
                                Glide.with(AlbumDetailsActivity.this)
                                        .load(R.drawable.music_logo)
                                        .into(mAlbumArt);
                                Toast.makeText(AlbumDetailsActivity.this, "Image not found", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }).start();

        // Set Recyclerview
        setRecyclerView();
    }

    private void setRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mAlbumSongsRecyclerView.setLayoutManager(layoutManager);

        mSongsAdapter = new SongsListAdapter(mAlbumSongs, this, this);
        mAlbumSongsRecyclerView.setAdapter(mSongsAdapter);
    }

    private void filterAlbumSongs(String albumName) {
        for (SongModel song : MySongsFragment.mAllSongs) {
            if (song.getAlbumName().equals(albumName)) {
                mAlbumSongs.add(song);
            }
        }
    }

    @Override
    public void onRecyclerItemClick(int position) {
        Intent intent = new Intent(this, PlaySongActivity.class);
        intent.putExtra("position", position);
        startActivity(intent);
    }

}