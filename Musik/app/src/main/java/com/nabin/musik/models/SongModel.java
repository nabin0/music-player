package com.nabin.musik.models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Parcel;
import android.os.Parcelable;

public class SongModel implements Parcelable {
    // Songs useful attributes
    private String songName;
    private String albumName;
    private String artistName;
    private String songDuration;
    private String songUri;
    private String albumId;
    private String imagePath;
    private String audioPath;

    public SongModel(){

    }

    public SongModel(String songName, String imagePath, String artistName, String songDuration){
        this.songName = songName;
        this.artistName = artistName;
        this.songDuration = songDuration;
        this.imagePath = imagePath;
    }


    public SongModel(String songName, String albumName, String artistName, String songDuration, String songUri, String albumId, String imagePath, String audioPath) {
        this.songName = songName;
        this.albumName = albumName;
        this.artistName = artistName;
        this.songDuration = songDuration;
        this.songUri = songUri;
        this.albumId = albumId;
        this.imagePath = imagePath;
        this.audioPath = audioPath;
    }

    protected SongModel(Parcel in) {
        songName = in.readString();
        albumName = in.readString();
        artistName = in.readString();
        songDuration = in.readString();
        songUri = in.readString();
        albumId = in.readString();
        imagePath = in.readString();
        audioPath = in.readString();
    }

    public static final Creator<SongModel> CREATOR = new Creator<SongModel>() {
        @Override
        public SongModel createFromParcel(Parcel in) {
            return new SongModel(in);
        }

        @Override
        public SongModel[] newArray(int size) {
            return new SongModel[size];
        }
    };

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getSongDuration() {
        return songDuration;
    }

    public void setSongDuration(String songDuration) {
        this.songDuration = songDuration;
    }

    public String getSongUri() {
        return songUri;
    }

    public void setSongUri(String songUri) {
        this.songUri = songUri;
    }

    public String getAlbumId() {
        return albumId;
    }

    public void setAlbumId(String albumId) {
        this.albumId = albumId;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getAudioPath() {
        return audioPath;
    }

    public void setAudioPath(String audioPath) {
        this.audioPath = audioPath;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(songName);
        parcel.writeString(albumName);
        parcel.writeString(artistName);
        parcel.writeString(songDuration);
        parcel.writeString(songUri);
        parcel.writeString(albumId);
        parcel.writeString(imagePath);
        parcel.writeString(audioPath);
    }
}
