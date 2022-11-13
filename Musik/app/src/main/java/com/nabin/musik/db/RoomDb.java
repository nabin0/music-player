package com.nabin.musik.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.nabin.musik.models.SongModel;

@Database(entities = {SongModel.class}, version = 2)
public abstract class RoomDb extends RoomDatabase {
    private static RoomDb roomDbInstance;

    public abstract FavouriteSongsDao dao();

    public static synchronized RoomDb getInstance(Context context) {
        if (roomDbInstance == null) {
            roomDbInstance = Room.databaseBuilder(context.getApplicationContext(), RoomDb.class,
                            "songs_database")
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build();
        }
        return roomDbInstance;
    }

}
