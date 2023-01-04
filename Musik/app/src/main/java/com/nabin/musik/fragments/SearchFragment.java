package com.nabin.musik.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.nabin.musik.R;
import com.nabin.musik.activities.PlaySongActivity;
import com.nabin.musik.adapters.SongsListAdapter;
import com.nabin.musik.interfaces.SongListRecyclerViewItemClick;
import com.nabin.musik.models.SongModel;

import java.util.ArrayList;
import java.util.Locale;

public class SearchFragment extends Fragment implements TextWatcher {


    private EditText searchEditText;
    private RecyclerView mRecyclerView;
    private SongsListAdapter mAdapter;
    private ArrayList<SongModel> mFilteredSongs;
    private static ArrayList<SongModel> mAllSongs;

    public SearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        mAllSongs = MySongsFragment.mAllSongs;
        mFilteredSongs = MySongsFragment.mAllSongs;

        searchEditText = view.findViewById(R.id.searchSongs);
        mRecyclerView = view.findViewById(R.id.filteredSongList);

        searchEditText.requestFocus();
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(searchEditText, InputMethodManager.SHOW_IMPLICIT);


        mAdapter = new SongsListAdapter(mAllSongs, getActivity(), position -> {
            Intent intent = new Intent(getActivity(), PlaySongActivity.class);
            intent.putExtra("position", position);
            intent.putParcelableArrayListExtra("my_songs", mFilteredSongs);
            startActivity(intent);
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        searchEditText.addTextChangedListener(this);
        return view;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        String query = searchEditText.getText().toString().toLowerCase(Locale.ROOT);
        ArrayList<SongModel> filteredList = new ArrayList<>();

        for (SongModel song : mAllSongs) {
            if (song.getSongName().toLowerCase(Locale.ROOT).contains(query)) {
                filteredList.add(song);
            }
        }
        mFilteredSongs = filteredList;
        mAdapter.updateSongs(filteredList);
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
}