package com.nabin.musik.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.nabin.musik.models.SongModel;

import java.util.List;

@Dao
public interface FavouriteSongsDao {

    @Insert
    void insert(SongModel songModel);

    @Delete
    void delete(SongModel songModel);

    @Query("SELECT * FROM favourite_songs")
    List<SongModel> getAllFavouriteSongs();
}
