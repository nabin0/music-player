package com.nabin.musik.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.nabin.musik.R;
import com.nabin.musik.activities.PlaySongActivity;
import com.nabin.musik.adapters.SongsListAdapter;
import com.nabin.musik.interfaces.SongListRecyclerViewItemClick;
import com.nabin.musik.utils.RecyclerViewItemDecorator;

public class BottomSheetSongsListFragment extends BottomSheetDialogFragment implements SongListRecyclerViewItemClick {
    private RecyclerView mRecyclerView;


    public BottomSheetSongsListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bottom_sheet_songs_list, container, false);

        //Set bottomSheet Recyclerview
        mRecyclerView = view.findViewById(R.id.bottomSheetRecyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);

        //Set decorator in list items
        RecyclerViewItemDecorator decorator = new RecyclerViewItemDecorator(10);
        mRecyclerView.addItemDecoration(decorator);

        SongsListAdapter listAdapter = new SongsListAdapter(PlaySongActivity.mAllSongs, getContext(), this);
        mRecyclerView.setAdapter(listAdapter);
        return view;
    }

    @Override
    public void onRecyclerItemClick(int position) {
        Intent intent = new Intent(getContext(), PlaySongActivity.class);
        intent.putExtra("position", position);
        getActivity().finish();
        startActivity(intent);
        dismiss();
    }
}