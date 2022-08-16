package com.nabin.musik.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nabin.musik.R;
import com.nabin.musik.adapters.AlbumAdapter;
import com.nabin.musik.models.SongModel;

import java.util.ArrayList;
import java.util.HashMap;

public class AlbumFragment extends Fragment {

    private RecyclerView albumRecyclerView;
    public AlbumAdapter mAlbumAdapter;

    private ArrayList<SongModel> mAllSongs = new ArrayList<>();

    public AlbumFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_album_list, container, false);

        albumRecyclerView = view.findViewById(R.id.albumRecyclerView);

        getAlbums();

        initRecyclerView();

        return view;
    }

    private void getAlbums() {
        HashMap<String, Integer> songsMap = new HashMap<>();

        for(SongModel song : MySongsFragment.mAllSongs){
            if(songsMap.containsKey(song.getAlbumName())){
                continue;
            }
            songsMap.put(song.getAlbumName(), 1);
            mAllSongs.add(song);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void initRecyclerView() {
        // Set Layout manager
        LinearLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        albumRecyclerView.setLayoutManager(layoutManager);

        //Set Adapter
        mAlbumAdapter = new AlbumAdapter(getContext(), mAllSongs);
        albumRecyclerView.setAdapter(mAlbumAdapter);
        mAlbumAdapter.notifyDataSetChanged();
    }
}