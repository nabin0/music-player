package com.nabin.musik.utils;

import android.content.ContentUris;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.nabin.musik.activities.MainActivity;
import com.nabin.musik.models.SongModel;

import java.util.ArrayList;
import java.util.List;

public class FetchSongs {
    public static List<SongModel> getAllAudioFromDevice(final Context context) {
        final List<SongModel> tempAudioList = new ArrayList<>();

        //Shared preference for sorting

        SharedPreferences preferences = context.getSharedPreferences(MainActivity.SORT_SHARED_PREF_VALUE, Context.MODE_PRIVATE);

        String mode = preferences.getString(MainActivity.SORT_MODE, "sortByName");


        // uri for external storage (content://)
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        String order = null;
        switch (mode){
            case "sortByName":
                order = MediaStore.MediaColumns.TITLE + " ASC";
                break;
            case "sortByDate":
                order = MediaStore.Audio.AudioColumns.DATE_ADDED + " DESC";
                break;
            case "sortBySize":
                order = MediaStore.Audio.AudioColumns.SIZE + " ASC";
                break;
        }


        String[] projection = {
                MediaStore.Audio.AudioColumns.TITLE,
                MediaStore.Audio.AudioColumns.ALBUM,
                MediaStore.Audio.ArtistColumns.ARTIST,
                MediaStore.Audio.AudioColumns.DURATION,
                MediaStore.Audio.AudioColumns.DATA,
                MediaStore.Audio.AudioColumns.ALBUM_ID,
        };
        String selection = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            selection = MediaStore.MediaColumns.DURATION + " >= 30000";
        }

        Cursor cursor = context.getContentResolver().query(uri, projection, selection, null, order, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                SongModel audioModel = new SongModel();

                String name = cursor.getString(0);
                String album = cursor.getString(1);
                String artist = cursor.getString(2);
                String duration = cursor.getString(3);
                String path = cursor.getString(4);
                String album_id = cursor.getString(5);
                Uri imagePath = Uri.parse("content://media/external/audio/albumart");
                Uri imagePathUri = ContentUris.withAppendedId(imagePath, Long.parseLong(album_id));

                //Set data to model object
                audioModel.setSongName(name);
                audioModel.setArtistName(artist);
                audioModel.setAlbumId(album_id);
                audioModel.setAlbumName(album);
                audioModel.setSongDuration(duration);
                audioModel.setSongUri(path);
                audioModel.setImagePath(imagePathUri.toString());

                tempAudioList.add(audioModel);
            }
        }
        return tempAudioList;
    }


    public static byte[] getAlbumImageByte(String path) {
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        try {
            mediaMetadataRetriever.setDataSource(path);
            byte[] data = mediaMetadataRetriever.getEmbeddedPicture();
            if(data != null)
                return data;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


//    public void fetchMusic(View view) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 8);
//        }
//        getAllAudioFromDevice(this);
//    }

}

//Uri imagePath = Uri.parse("content://media/external/audio/albumart");