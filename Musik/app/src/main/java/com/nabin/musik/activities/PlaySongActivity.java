package com.nabin.musik.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.nabin.musik.R;
import com.nabin.musik.Services.MyMusicPlayerService;
import com.nabin.musik.adapters.SongsListAdapter;
import com.nabin.musik.db.FavouriteSongsDao;
import com.nabin.musik.db.RoomDb;
import com.nabin.musik.fragments.BottomSheetSongsListFragment;
import com.nabin.musik.interfaces.HeadsetActionInterface;
import com.nabin.musik.interfaces.MusicActionInterface;
import com.nabin.musik.models.SongModel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class PlaySongActivity extends AppCompatActivity implements View.OnClickListener, MusicActionInterface, ServiceConnection, HeadsetActionInterface {

    private static final String TAG = "MyTag";

    //Views
    private ImageView songArtImage;
    private ImageView favouriteBtnImage;
    private ImageView songsListBottomSheetImageView;
    private ImageView playPauseBtnImage;
    private ImageView repeatBtnImage;
    private ImageView nextSongBtnImage;
    private ImageView previousSongBtnImage;
    private SeekBar seekBar;
    private TextView totalDuration;
    private TextView currentDuration;
    private TextView songName;
    private TextView songArtistName;
    private ImageView upBtn;
    public static boolean MUSIC_PLAYER_SERVICE_BOUND = false;


    //Vars
    public static ArrayList<SongModel> mAllSongs;
    private Handler mHandler = new Handler();
    private int mSongPosition;
    private Thread playPauseThread, nextSongThread, previousSongThread;
    public static boolean FLAG_REPEAT_SONG = false;
    public static boolean FLAG_REPEAT_PLAYLIST = true;
    public static boolean FLAG_SHUFFLE = false;
    private static MyMusicPlayerService mService;
    private LocalBroadcastManager broadcastManager;
    private RoomDb database;
    List<SongModel> favSongsList = new ArrayList<>();
    private boolean isFavoriteSong = false;


    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("close_app")) {
                MUSIC_PLAYER_SERVICE_BOUND = false;
                unbindService(PlaySongActivity.this);
                stopService(new Intent(PlaySongActivity.this, MyMusicPlayerService.class));
                finishAffinity();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_song);

        //Register broadcast
        broadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("close_app");
        broadcastManager.registerReceiver(broadcastReceiver, intentFilter);
        database = RoomDb.getInstance(this);
        initViews();

//        new FetchSongAsync(database.dao()).execute();
        favSongsList = database.dao().getAllFavouriteSongs();

        getIntentData();

        //listeners events
        setListeners();

        // Seekbar changes events
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (mService != null && b) {
                    mService.seekTo(i);
                    currentDuration.setText(formattedSecs(i));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        //Bind Service
        if (!MUSIC_PLAYER_SERVICE_BOUND) {
            Intent intent = new Intent(getApplicationContext(), MyMusicPlayerService.class);
            bindService(intent, this, BIND_AUTO_CREATE);
            MUSIC_PLAYER_SERVICE_BOUND = true;
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
//        if (MUSIC_PLAYER_SERVICE_BOUND) {
//            unbindService(this);
//            Log.d(TAG, "onPause: unbind called");
//            MUSIC_PLAYER_SERVICE_BOUND = false;
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        broadcastManager.unregisterReceiver(broadcastReceiver);
    }

    private void getIntentData() {
        if (getIntent() != null) {
            if (getIntent().hasExtra("from_activity")) {
                mSongPosition = getIntent().getIntExtra("position", 0);
                MainActivity.currenstSongPositin = mSongPosition;
                setCurrentSongDataToUIOnly(mSongPosition);
            } else {
                mSongPosition = getIntent().getIntExtra("position", 0);
                mAllSongs = SongsListAdapter.mSongs;
                MainActivity.currenstSongPositin = mSongPosition;
                playNewSong(mSongPosition);
                playPauseBtnImage.setImageResource(R.drawable.ic_baseline_pause);
            }
        }
    }

    private void setCurrentSongDataToUIOnly(int mSongPosition) {
        SongModel song = mAllSongs.get(mSongPosition);
        if (song != null) {
            totalDuration.setText(formattedSecs(mService.getDuration()));
            seekBar.setMax(mService.getDuration());
            updateSeekBar();
            playPauseBtnImage.setImageResource(R.drawable.ic_baseline_pause);
            songName.setText(song.getSongName());
            songArtistName.setText(song.getArtistName());
            Glide.with(getApplicationContext()).load(Uri.parse(song.getImagePath())).placeholder(R.drawable.music_logo).into(songArtImage);
            if (isFavourite(song)) {
                isFavoriteSong = true;
                favouriteBtnImage.setImageResource(R.drawable.ic_filled_heart);
            } else {
                favouriteBtnImage.setImageResource(R.drawable.ic_heart);
                isFavoriteSong = false;
            }
        }
    }


    private void playNewSong(int position) {
        isFavoriteSong = false;
        MainActivity.currenstSongPositin = position;
        if (mService != null) {
            if (mService.isPlaying()) {
                mService.stop();
            }
            mService.release();
            mService.createMediaPlayer(mSongPosition);
            totalDuration.setText(formattedSecs(mService.getDuration()));
            seekBar.setMax(mService.getDuration());
            updateSeekBar();
            mService.start();
            mService.onCompleted();
        } else {
            Intent intent = new Intent(PlaySongActivity.this, MyMusicPlayerService.class);
            intent.putExtra("currPosition", mSongPosition);
            startService(intent);
        }
        seekBar.setProgress(0);
        playPauseBtnImage.setImageResource(R.drawable.ic_baseline_pause);
        songName.setText(mAllSongs.get(position).getSongName());
        songArtistName.setText(mAllSongs.get(position).getArtistName());
        Glide.with(getApplicationContext()).load(Uri.parse(mAllSongs.get(position).getImagePath())).placeholder(R.drawable.music_logo).into(songArtImage);
        if (isFavourite(mAllSongs.get(position))) {
            isFavoriteSong = true;
            favouriteBtnImage.setImageResource(R.drawable.ic_filled_heart);
        } else {
            favouriteBtnImage.setImageResource(R.drawable.ic_heart);
            isFavoriteSong = false;
        }
    }

    private void initViews() {
        songArtImage = findViewById(R.id.albumArtImage);
        favouriteBtnImage = findViewById(R.id.favouriteSongImage);
        songsListBottomSheetImageView = findViewById(R.id.bottomSheetSongsListBtn);
        playPauseBtnImage = findViewById(R.id.playPauseBtn);
        repeatBtnImage = findViewById(R.id.repeatBtnImage);
        nextSongBtnImage = findViewById(R.id.skipNextBtn);
        previousSongBtnImage = findViewById(R.id.skipPreviousBtn);
        seekBar = findViewById(R.id.songSeekBar);
        currentDuration = findViewById(R.id.currentDurationTv);
        totalDuration = findViewById(R.id.totalDurationTv);
        songName = findViewById(R.id.songName);
        songArtistName = findViewById(R.id.songArtist);
        upBtn = findViewById(R.id.upBtnBackToMainActivity);
    }

    private void setListeners() {
        repeatBtnImage.setOnClickListener(this);
        upBtn.setOnClickListener(this);
        songsListBottomSheetImageView.setOnClickListener(this);
        favouriteBtnImage.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.repeatBtnImage) {
            songPlayBehaviour();
        } else if (id == R.id.upBtnBackToMainActivity) {
            finish();
        } else if (id == R.id.bottomSheetSongsListBtn) {
            BottomSheetSongsListFragment bottomSheetSongsListFragment = new BottomSheetSongsListFragment();
            bottomSheetSongsListFragment.show(getSupportFragmentManager(), bottomSheetSongsListFragment.getTag());
        } else if (id == R.id.favouriteSongImage) {
            addOrRemoveFavourite();
        }
    }

    private void addOrRemoveFavourite() {
        if (isFavoriteSong) {
            Toast.makeText(this, "remove", Toast.LENGTH_SHORT).show();
            database.dao().delete(mAllSongs.get(mSongPosition));
            favouriteBtnImage.setImageResource(R.drawable.ic_heart);
            isFavoriteSong = false;
        } else {
            Toast.makeText(this, "added", Toast.LENGTH_SHORT).show();
            favouriteBtnImage.setImageResource(R.drawable.ic_filled_heart);
            database.dao().insert(mAllSongs.get(mSongPosition));
            isFavoriteSong = true;
        }
    }

    private boolean isFavourite(SongModel songModel) {
        favSongsList = database.dao().getAllFavouriteSongs();
        for (SongModel model : favSongsList) {
            if (model.getSongUri().equals(songModel.getSongUri())) return true;
        }
        return false;
    }

    private void songPlayBehaviour() {
        // Repeat playlist -> shuffle -> repeat song
        if (FLAG_REPEAT_PLAYLIST) {
            repeatBtnImage.setImageResource(R.drawable.ic_shuffle);
            FLAG_SHUFFLE = true;
            FLAG_REPEAT_SONG = false;
            FLAG_REPEAT_PLAYLIST = false;
            Toast.makeText(mService, "Shuffle Playlist", Toast.LENGTH_SHORT).show();
        } else if (FLAG_SHUFFLE) {
            FLAG_SHUFFLE = false;
            FLAG_REPEAT_SONG = true;
            repeatBtnImage.setImageResource(R.drawable.ic_single_song_loop);
            Toast.makeText(mService, "Repeat Current Song", Toast.LENGTH_SHORT).show();
        } else {
            FLAG_REPEAT_SONG = false;
            FLAG_REPEAT_PLAYLIST = true;
            repeatBtnImage.setImageResource(R.drawable.ic_baseline_repeat);
            Toast.makeText(mService, "Repeat Current PlayList", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        playPauseThread();
        skipNextThread();
        skipPreviousThread();
    }

    private void updateSeekBar() {
        PlaySongActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (mService != null) {
                        currentDuration.setText(formattedSecs(mService.getCurrentPosition()));
                        seekBar.setProgress(mService.getCurrentPosition());
                        mHandler.postDelayed(this, 1000);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Play Previous Song Thread
     */
    private void skipPreviousThread() {
        previousSongThread = new Thread(new Runnable() {
            @Override
            public void run() {
                previousSongBtnImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        playPreviousSong();
                    }
                });
            }
        });
        previousSongThread.start();
    }

    public void playPreviousSong() {
        if (FLAG_SHUFFLE) {
            mSongPosition = ThreadLocalRandom.current().nextInt(0, mAllSongs.size());
        } else {
            if (mSongPosition > 0) {
                mSongPosition -= 1;
            } else {
                mSongPosition = mAllSongs.size() - 1;
            }
        }

        playNewSong(mSongPosition);
        mService.showNotification(R.drawable.ic_baseline_pause);
    }

    /**
     * Play Next Song Thread
     */
    private void skipNextThread() {
        nextSongThread = new Thread(new Runnable() {
            @Override
            public void run() {
                nextSongBtnImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        playNextSong();
                    }
                });
            }
        });
        nextSongThread.start();
    }

    public void playNextSong() {
        if (FLAG_SHUFFLE) {
            mSongPosition = ThreadLocalRandom.current().nextInt(0, mAllSongs.size());
        } else {
            if (mSongPosition < mAllSongs.size() - 1) {
                mSongPosition += 1;
            } else {
                mSongPosition = 0;
            }
        }

        playNewSong(mSongPosition);
        mService.showNotification(R.drawable.ic_baseline_pause);
    }

    /**
     * Play Pause Song Thread
     */
    private void playPauseThread() {
        playPauseThread = new Thread(new Runnable() {
            @Override
            public void run() {
                playPauseBtnImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        playPauseSong();
                    }
                });
            }
        });
        playPauseThread.start();
    }

    public void playPauseSong() {
        if (mService.isPlaying()) {
            //Pause song
            mService.pause();
            playPauseBtnImage.setImageResource(R.drawable.ic_baseline_play_arrow);
            mService.showNotification(R.drawable.ic_baseline_play_arrow);
            updateSeekBar();
        } else {
            //Play song
            mService.start();
            playPauseBtnImage.setImageResource(R.drawable.ic_baseline_pause);
            updateSeekBar();
            mService.showNotification(R.drawable.ic_baseline_pause);
        }
    }

    /**
     * Other Methods
     */

    // Format Milliseconds into readable time Format
    @NonNull
    private String formattedSecs(int currPosition) {
        String formattedTime = "";

        String secs = String.valueOf((currPosition / 1000) % 60);
        String mins = String.valueOf((currPosition / 1000) / 60);
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

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        MyMusicPlayerService.MyBinder myBinder = (MyMusicPlayerService.MyBinder) iBinder;
        mService = myBinder.getService();
        seekBar.setProgress(0);
        updateSeekBar();
        seekBar.setMax(mService.getDuration());
        totalDuration.setText(formattedSecs(mService.getDuration()));
        mService.showNotification(R.drawable.ic_baseline_pause);
        mService.onCompleted();
        mService.setActionInterface(this);
        mService.setHeadsetActionInterface(this);
        MUSIC_PLAYER_SERVICE_BOUND = true;
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        mService.stop();
        Toast.makeText(mService, "stopped", Toast.LENGTH_SHORT).show();
        MUSIC_PLAYER_SERVICE_BOUND = false;
        mService = null;
    }

    @Override
    public void playSongOnHeadsetPluggedIn() {
        if (!mService.isPlaying()) {
            //Play song
            mService.start();
            playPauseBtnImage.setImageResource(R.drawable.ic_baseline_pause);
            updateSeekBar();
            mService.showNotification(R.drawable.ic_baseline_pause);
        }
    }

    @Override
    public void pauseSongOnHeadsetPluggedOut() {
        if (mService.isPlaying()) {
            //Pause song
            mService.pause();
            playPauseBtnImage.setImageResource(R.drawable.ic_baseline_play_arrow);
            mService.showNotification(R.drawable.ic_baseline_play_arrow);
            updateSeekBar();
        }
    }


    private static class InsertSongAsync extends AsyncTask<SongModel, Void, Void> {
        private FavouriteSongsDao dao;

        private InsertSongAsync(FavouriteSongsDao dao) {
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(SongModel... songModels) {
            dao.insert(songModels[0]);
            return null;
        }
    }

    private static class DeleteSongAsync extends AsyncTask<SongModel, Void, Void> {
        private FavouriteSongsDao dao;

        private DeleteSongAsync(FavouriteSongsDao dao) {
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(SongModel... songModels) {
            dao.delete(songModels[0]);
            return null;
        }
    }

}