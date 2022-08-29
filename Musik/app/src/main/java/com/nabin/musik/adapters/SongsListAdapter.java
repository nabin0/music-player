package com.nabin.musik.adapters;


import android.annotation.SuppressLint;
import android.content.Context;
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
import com.nabin.musik.interfaces.SongListRecyclerViewItemClick;
import com.nabin.musik.models.SongModel;

import java.util.ArrayList;

public class SongsListAdapter extends RecyclerView.Adapter<SongsListAdapter.SongViewHolder> {
    public static ArrayList<SongModel> mSongs;
    private final Context context;
    private final SongListRecyclerViewItemClick mSongListRecyclerViewItemClick;

    public SongsListAdapter(ArrayList<SongModel> mSongs, Context context, SongListRecyclerViewItemClick mSongListRecyclerViewItemClick) {
        SongsListAdapter.mSongs = mSongs;
        this.context = context;
        this.mSongListRecyclerViewItemClick = mSongListRecyclerViewItemClick;
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_item_layout_rv, parent, false);
        return new SongViewHolder(view, mSongListRecyclerViewItemClick);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        holder.songName.setText(mSongs.get(position).getSongName());
        holder.songArtist.setText(mSongs.get(position).getArtistName());
        holder.songDuration.setText(formattedSecs(Integer.parseInt(mSongs.get(position).getSongDuration())) + " min");
        Uri uri = Uri.parse(mSongs.get(position).getImagePath());

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
                                Glide.with(context)
                                        .load(uri)
                                        .placeholder(R.drawable.music_logo)
                                        .into(holder.songImage);
                            } else {
                                Glide.with(context)
                                        .load(R.drawable.music_logo)
                                        .into(holder.songImage);
                                Toast.makeText(context, "Image not found", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }).start();
    }

    @Override
    public int getItemCount() {
        return mSongs.size();
    }


    public static class SongViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ImageView songImage;
        private final TextView songName;
        private final TextView songArtist;
        private final TextView songDuration;
        private final SongListRecyclerViewItemClick songListRecyclerViewItemClick;

        public SongViewHolder(@NonNull View itemView, SongListRecyclerViewItemClick mSongListRecyclerViewItemClick) {
            super(itemView);
            songName = itemView.findViewById(R.id.songNameTv);
            songImage = itemView.findViewById(R.id.songImage);
            songArtist = itemView.findViewById(R.id.songArtistTv);
            songDuration = itemView.findViewById(R.id.songDurationTv);
            songListRecyclerViewItemClick = mSongListRecyclerViewItemClick;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            songListRecyclerViewItemClick.onRecyclerItemClick(getAdapterPosition());
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateSongs(ArrayList<SongModel> songsList){
        mSongs = new ArrayList<>();
        mSongs.addAll(songsList);
        notifyDataSetChanged();
    }
    private String formattedSecs(int msecs) {
        String formattedTime = "";

        String secs = String.valueOf((msecs / 1000) % 60);
        String mins = String.valueOf((msecs / 1000) / 60);
        formattedTime = mins + ":" + secs;
        if (mins.length() < 2) {
            mins = "0" + mins;
        }
        if (secs.length() < 2) {
            secs = "0" + secs;
        }
        formattedTime = mins + ":" + secs;

        return formattedTime;
    }
}
