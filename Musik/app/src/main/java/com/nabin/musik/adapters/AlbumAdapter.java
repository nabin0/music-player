package com.nabin.musik.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.nabin.musik.R;
import com.nabin.musik.activities.AlbumDetailsActivity;
import com.nabin.musik.models.SongModel;

import java.util.ArrayList;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.MyAlbumViewHolder> {
    private Context mContext;
    private ArrayList<SongModel> mAllSongsList = new ArrayList<>();
    private View view;

    public AlbumAdapter(Context mContext, ArrayList<SongModel> mAllSongsList) {
        this.mContext = mContext;
        this.mAllSongsList = mAllSongsList;
    }

    @NonNull
    @Override
    public MyAlbumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(mContext).inflate(R.layout.album_item_layout, parent, false);
        return new MyAlbumViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyAlbumViewHolder holder, int position) {
        holder.albumNameTv.setText(mAllSongsList.get(position).getAlbumName());
        Uri uri = Uri.parse(mAllSongsList.get(position).getImagePath());
        // Setting album image
        Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (uri != null) {
                                Glide.with(mContext)
                                        .load(uri)
                                        .placeholder(R.drawable.music_logo)
                                        .into(holder.albumArtIv);
                            } else {
                                Glide.with(mContext)
                                        .load(R.drawable.music_logo)
                                        .into(holder.albumArtIv);
                                Toast.makeText(mContext, "Image not found", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }).start();

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, AlbumDetailsActivity.class);
                intent.putExtra("album_name", mAllSongsList.get(position).getAlbumName());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mAllSongsList.size();
    }


    public static class MyAlbumViewHolder extends RecyclerView.ViewHolder {
        private ImageView albumArtIv;
        private TextView albumNameTv;
        public MyAlbumViewHolder(@NonNull View itemView) {
            super(itemView);
            albumArtIv = itemView.findViewById(R.id.albumItemAlbumArtImageview);
            albumNameTv = itemView.findViewById(R.id.albumItemAlbumName);
        }
    }
}
