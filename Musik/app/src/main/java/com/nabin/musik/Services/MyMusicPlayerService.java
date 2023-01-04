package com.nabin.musik.Services;

import static com.nabin.musik.activities.PlaySongActivity.mAllSongs;
import static com.nabin.musik.application.App.ACTION_PLAY;
import static com.nabin.musik.application.App.ACTION_STOP_SERVICE;
import static com.nabin.musik.application.App.CHANNEL_1;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.session.MediaSession;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.ParcelFileDescriptor;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.nabin.musik.R;
import com.nabin.musik.activities.MainActivity;
import com.nabin.musik.activities.PlaySongActivity;
import com.nabin.musik.application.App;
import com.nabin.musik.interfaces.HeadsetActionInterface;
import com.nabin.musik.interfaces.MusicActionInterface;
import com.nabin.musik.models.SongModel;
import com.nabin.musik.receivers.NotificationReceiver;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class MyMusicPlayerService extends Service implements MediaPlayer.OnCompletionListener {
    private static final String TAG = "MyTag";

    private final MyBinder myBinder = new MyBinder();
    public static MediaPlayer mMediaPlayer;
    public ArrayList<SongModel> mSongsList = new ArrayList<>();
    private int mPosition = -1;
    private MusicActionInterface actionInterface;
    private HeadsetActionInterface headsetActionInterface;
    private MediaSessionCompat mediaSessionCompat;
    private String action_name = null;
    private boolean mIsPlaying = false;

    //Vars for sharedpref bottom player
    public static final String SONG_DETAIL_CONTROL_DATA = "SONG_DETAIL_CONTROL_DATA";
    public static final String SONG_NAME = "song_name";
    public static final String SONG_ARTIST = "song_artist";
    public static final String SONG_URI = "song_uri";
    public static final String SONG_IMAGE_PATH = "song_image_path";


    MediaSession mediaSession;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //Media session
        mediaSessionCompat = new MediaSessionCompat(getBaseContext(), "my song");
        mediaSessionCompat.setActive(true);

        mediaSession = new MediaSession(getBaseContext(), "mediasession");
        mediaSession.setActive(true);
        mSongsList = mAllSongs;
    }

    public class MyBinder extends Binder {
        public MyMusicPlayerService getService() {
            return MyMusicPlayerService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mSongsList = mAllSongs;
        if (intent.hasExtra("currPosition")) {
            mPosition = intent.getIntExtra("currPosition", -1);
            if (mPosition != -1) {
                playMedia(mPosition);
                Log.d(TAG, "onStartCommand: play media in start cmd");
            }
        }
        if (intent.hasExtra("actionName")) {
            action_name = intent.getStringExtra("actionName");
        }
        if (action_name != null) {
            switch (action_name) {
                case "play":
                    if (headsetActionInterface != null) {
                        headsetActionInterface.playSongOnHeadsetPluggedIn();
                    }
                    break;
                case "pause":
                    if (headsetActionInterface != null) {
                        headsetActionInterface.pauseSongOnHeadsetPluggedOut();
                    }
                    break;
                case "playPause":
                    if (actionInterface != null) {
                        actionInterface.playPauseSong();
                    }
                    break;
                case "next":
                    if (actionInterface != null) {
                        actionInterface.playNextSong();
                    }
                    break;
                case "previous":
                    if (actionInterface != null) {
                        actionInterface.playPreviousSong();
                    }
                    break;
                case "stopService":
                    LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(getApplicationContext());
                    localBroadcastManager.sendBroadcast(new Intent("close_app"));
                    if (mMediaPlayer != null) {
                        mMediaPlayer.stop();
                        mMediaPlayer.release();
                    }
                    stopForeground(true);
                    stopSelf();
                    break;
                default:
                    stopSelf();
            }
        }

        return START_NOT_STICKY;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void setActionInterface(MusicActionInterface actionInterface) {
        this.actionInterface = actionInterface;
    }

    public void setHeadsetActionInterface(HeadsetActionInterface actionInterface) {
        this.headsetActionInterface = actionInterface;
    }

    void playMedia(int position) {
        if (mMediaPlayer != null) {
            stop();
            release();
        }
        createMediaPlayer(position);
        start();
    }

    public void start() {
        mIsPlaying = true;
        mMediaPlayer.start();
    }

    public void createMediaPlayer(int position) {
        mPosition = position;
        mSongsList = mAllSongs;
        Log.d(TAG, "createMediaPlayer: " + mSongsList.size()+ " path : "+ mSongsList.get(position).getSongUri());
        mMediaPlayer = MediaPlayer.create(getBaseContext(), Uri.parse(mSongsList.get(mPosition).getSongUri()));

        //Shared pref for bottom control
        SharedPreferences.Editor editor = getSharedPreferences(SONG_DETAIL_CONTROL_DATA, MODE_PRIVATE).edit();
        editor.putString(SONG_NAME, mAllSongs.get(position).getSongName());
        editor.putString(SONG_ARTIST, mAllSongs.get(position).getArtistName());
        editor.putString(SONG_IMAGE_PATH, mAllSongs.get(position).getImagePath());
        editor.putString(SONG_URI, mAllSongs.get(position).getSongUri());
        editor.apply();
    }

    public void pause() {
        mIsPlaying = false;
        mMediaPlayer.pause();
    }

    public void stop() {
        if (mIsPlaying) {
            mMediaPlayer.stop();
        }
        mIsPlaying = false;
    }

    public void playNextSong() {
        if (actionInterface != null) {
            actionInterface.playNextSong();
        }
    }

    public void playPauseSong() {
        if (actionInterface != null) {
            actionInterface.playPauseSong();
        }
    }

    public void release() {
        mMediaPlayer.release();
    }

    public int getDuration() {
        return mMediaPlayer.getDuration();
    }

    public int getCurrentPosition() {
        return mMediaPlayer.getCurrentPosition();
    }

    public boolean isPlaying() {
        return mIsPlaying;
    }

    public void seekTo(int position) {
        mMediaPlayer.seekTo(position);
    }

    public void onCompleted() {
        mMediaPlayer.setOnCompletionListener(this);
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        if (actionInterface != null) {
            if (PlaySongActivity.FLAG_REPEAT_SONG) {
                if (mMediaPlayer != null) {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    createMediaPlayer(mPosition);
                    mMediaPlayer.start();
                    onCompleted();
                }
            } else {
                actionInterface.playNextSong();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void showNotification(int playPauseBtnId) {

        //Create Pending intents

        //Navigate to playing activity
        Intent playIntent = new Intent(this, MainActivity.class);
        PendingIntent playPendingIntent;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            playPendingIntent = PendingIntent.getActivity(this, 0, playIntent, PendingIntent.FLAG_IMMUTABLE);
        } else {
            playPendingIntent = PendingIntent.getActivity(this, 0, playIntent, 0);

        }

        // Invokes broadcast receiver
        Intent pauseIntent = new Intent(this, NotificationReceiver.class).setAction(ACTION_PLAY);
        PendingIntent pausePendingIntent = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            pausePendingIntent = PendingIntent.getBroadcast(this, 0, pauseIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        } else {
            pausePendingIntent = PendingIntent.getBroadcast(this, 0, pauseIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        Intent previousIntent = new Intent(this, NotificationReceiver.class).setAction(App.ACTION_PREVIOUS);
        PendingIntent previousPendingIntent = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            previousPendingIntent = PendingIntent.getBroadcast(this, 0, previousIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        } else {
            previousPendingIntent = PendingIntent.getBroadcast(this, 0, previousIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        Intent nextIntent = new Intent(getApplicationContext(), NotificationReceiver.class).setAction(App.ACTION_NEXT);
        PendingIntent nextPendingIntent = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            nextPendingIntent = PendingIntent.getBroadcast(this, 0, nextIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        } else {
            nextPendingIntent = PendingIntent.getBroadcast(this, 0, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        //Stop service method
        Intent closeService = new Intent(getApplicationContext(), NotificationReceiver.class).setAction(ACTION_STOP_SERVICE);
        PendingIntent stopForegroundServiceNotification = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            stopForegroundServiceNotification = PendingIntent.getBroadcast(this, 0, closeService, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_CANCEL_CURRENT);
        } else {
            stopForegroundServiceNotification = PendingIntent.getBroadcast(this, 0, closeService, PendingIntent.FLAG_CANCEL_CURRENT);
        }

        Bitmap largeImage = getArtFromAlbumArtUri(getApplicationContext(), Uri.parse(mSongsList.get(mPosition).getImagePath()));
        mediaSessionCompat.setActive(true);

        Log.d(TAG, "showNotification: " + mSongsList.get(mPosition).getImagePath());

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_1)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setLargeIcon(largeImage)
                .setContentTitle(mSongsList.get(mPosition).getSongName())
                .setContentText(mSongsList.get(mPosition).getArtistName())
                .setContentIntent(playPendingIntent)
                .addAction(R.drawable.ic_baseline_skip_previous, "Previous", previousPendingIntent)
                .addAction(playPauseBtnId, "PlayPause", pausePendingIntent)
                .addAction(R.drawable.ic_baseline_skip_next, "Next", nextPendingIntent)
                .addAction(R.drawable.ic_baseline_cancel_24, "CloseService", stopForegroundServiceNotification)
                .setOnlyAlertOnce(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle().setMediaSession(mediaSessionCompat.getSessionToken()))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build();

        startForeground(1, notification);
    }

    Bitmap getArtFromAlbumArtUri(Context context, Uri artUri) {
        Bitmap artBitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        try {
            ParcelFileDescriptor parcelFileDescriptor = context.getContentResolver()
                    .openFileDescriptor(artUri, "r");

            if (parcelFileDescriptor != null) {
                FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
                artBitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (artBitmap != null) {
            return artBitmap;
        }
        return getDefaultArtImage();
    }

    private Bitmap getDefaultArtImage() {
        Bitmap artBitmap = null;
        Drawable drawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.music_logo);

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }
        return artBitmap;
    }

}
