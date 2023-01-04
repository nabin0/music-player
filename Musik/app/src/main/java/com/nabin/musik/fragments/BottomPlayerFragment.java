package com.nabin.musik.fragments;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.nabin.musik.R;
import com.nabin.musik.Services.MyMusicPlayerService;
import com.nabin.musik.activities.MainActivity;
import com.nabin.musik.activities.PlaySongActivity;

public class BottomPlayerFragment extends Fragment implements ServiceConnection {

    private MyMusicPlayerService mMyMusicPlayerService;

    private ImageView mAlbumArt;
    private ImageView mPlayPause;
    private ImageView mNextSong;
    private TextView mSongName;
    private TextView mSongArtist;

    public BottomPlayerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bottom_player, container, false);
        //Init views
        mAlbumArt = view.findViewById(R.id.albumArtBottomFrag);
        mSongName = view.findViewById(R.id.songNameBottomFrag);
        mSongArtist = view.findViewById(R.id.songArtistBottomFrag);
        mPlayPause = view.findViewById(R.id.playPauseBottomFrag);
        mNextSong = view.findViewById(R.id.playNextBottomFrag);

        mPlayPause.setOnClickListener(view1 -> {
            if(mMyMusicPlayerService != null){
                mMyMusicPlayerService.playPauseSong();
            }
            if(mMyMusicPlayerService.isPlaying()){
                mPlayPause.setImageResource(R.drawable.ic_baseline_pause);
            }else{
                mPlayPause.setImageResource(R.drawable.ic_baseline_play_arrow);
            }
        });

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), PlaySongActivity.class);
                intent.putExtra("position", MainActivity.currentSongPosition);
                intent.putExtra("from_activity", "bottom_fragment");
                startActivity(intent);
            }
        });

        
        mNextSong.setOnClickListener(view12 -> {
            if(mMyMusicPlayerService != null){
                mMyMusicPlayerService.playNextSong();
            }

            //Show data
            if(MainActivity.SHOW_BOTTOM_SONG_CONTROL){
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences(MyMusicPlayerService.SONG_DETAIL_CONTROL_DATA, Context.MODE_PRIVATE);
                mSongName.setText(sharedPreferences.getString(MyMusicPlayerService.SONG_NAME, null));
                mSongArtist.setText(sharedPreferences.getString(MyMusicPlayerService.SONG_ARTIST, null));
                Glide.with(getContext()).load(sharedPreferences.getString(MyMusicPlayerService.SONG_IMAGE_PATH, null))
                        .placeholder(R.drawable.music_logo)
                        .into(mAlbumArt);
            }else{
                // show default data
            }
        });
        
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        if(MainActivity.SHOW_BOTTOM_SONG_CONTROL){
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences(MyMusicPlayerService.SONG_DETAIL_CONTROL_DATA, Context.MODE_PRIVATE);
            mSongName.setText(sharedPreferences.getString(MyMusicPlayerService.SONG_NAME, null));
            mSongArtist.setText(sharedPreferences.getString(MyMusicPlayerService.SONG_ARTIST, null));
            Glide.with(getContext()).load(sharedPreferences.getString(MyMusicPlayerService.SONG_IMAGE_PATH, null))
                    .placeholder(R.drawable.music_logo)
                    .into(mAlbumArt);
        }else{
            // show default data
        }

        // Bind service
        Intent intent = new Intent(getContext(), MyMusicPlayerService.class);
        if(getContext() != null){
            getContext().bindService(intent, this, Context.BIND_AUTO_CREATE);
            if(mMyMusicPlayerService != null && mMyMusicPlayerService.isPlaying()){
                mPlayPause.setImageResource(R.drawable.ic_baseline_pause);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // UnBind service
        Intent intent = new Intent(getContext(), MyMusicPlayerService.class);
        if(getContext() != null){
            getContext().unbindService(this);
        }
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        MyMusicPlayerService.MyBinder binder = (MyMusicPlayerService.MyBinder) iBinder;
        mMyMusicPlayerService = binder.getService();
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        mMyMusicPlayerService = null;
    }
}