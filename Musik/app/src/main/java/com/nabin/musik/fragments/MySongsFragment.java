    package com.nabin.musik.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nabin.musik.R;
import com.nabin.musik.activities.FavouriteMusicActivity;
import com.nabin.musik.activities.MainActivity;
import com.nabin.musik.activities.PlaySongActivity;
import com.nabin.musik.adapters.SongsListAdapter;
import com.nabin.musik.interfaces.SongListRecyclerViewItemClick;
import com.nabin.musik.models.SongModel;
import com.nabin.musik.utils.FetchSongs;
import com.nabin.musik.utils.RecyclerViewItemDecorator;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public class MySongsFragment extends Fragment implements SongListRecyclerViewItemClick {
    //Views
    public static ArrayList<SongModel> mAllSongs = new ArrayList<>();
    private CardView cardFavoriteSongs;
    private CardView cardAlbums;

    //Vars
    public static int STORAGE_PERMISSION_REQUEST_CODE = 55;
    private RecyclerView songsListRecyclerView;
    public SongsListAdapter mAdapter;


    public MySongsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_songs, container, false);

        //Set Toolbar
        ((AppCompatActivity) getActivity()).setSupportActionBar((Toolbar) view.findViewById(R.id.mainActivityToolbar));
        Objects.requireNonNull(((AppCompatActivity) getActivity()).getSupportActionBar()).setDisplayShowTitleEnabled(false);

        intiViews(view);

        // Fetch songs
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                mAllSongs = (ArrayList<SongModel>) FetchSongs.getAllAudioFromDevice(getContext());
            } else {
//                showPermissionRequestSnackbar();
                requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
        } else {
            // Do your task
            mAllSongs = (ArrayList<SongModel>) FetchSongs.getAllAudioFromDevice(getContext());
        }

        intiRecyclerView();
        setClickListeners();

        return view;
    }

    private void intiViews(View view) {
        songsListRecyclerView = view.findViewById(R.id.songsListRv);
        cardFavoriteSongs = view.findViewById(R.id.cardFavouriteSongs);
        cardAlbums = view.findViewById(R.id.cardAlbums);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void intiRecyclerView() {
        // Set Layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        songsListRecyclerView.setLayoutManager(layoutManager);

        //Set decorator in list items
        RecyclerViewItemDecorator decorator = new RecyclerViewItemDecorator(14);
        songsListRecyclerView.addItemDecoration(decorator);

        //Set Adapter
        mAdapter = new SongsListAdapter(mAllSongs, getContext(), MySongsFragment.this);
        songsListRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    private void setClickListeners() {
        cardFavoriteSongs.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), FavouriteMusicActivity.class);
            getContext().startActivity(intent);
        });

        cardAlbums.setOnClickListener(view -> {
            ((MainActivity) getActivity()).replaceWithAlbumFragment();
        });

    }


    @Override
    public void onRecyclerItemClick(int position) {
        Intent intent = new Intent(getActivity(), PlaySongActivity.class);
        intent.putExtra("position", position);
        intent.putParcelableArrayListExtra("my_songs", mAllSongs);
        startActivity(intent);
    }

    // Set Menu and Listen Clicks
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Handle Option click
        return super.onOptionsItemSelected(item);
    }

    private void showPermissionRequestSnackbar() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            //Show the dialogue box
            new AlertDialog.Builder(getContext())
                    .setTitle("Storage Permission Required.")
                    .setMessage("Storage permission required to fetch songs form the storage.")
                    .setPositiveButton("ALLOW", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    STORAGE_PERMISSION_REQUEST_CODE);
                        }
                    }).setNegativeButton("DENY", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }).show();
        } else {
            // Request for permission
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    STORAGE_PERMISSION_REQUEST_CODE);
        }
    }

    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            result -> {
                if (result) {
                    //Permission Granted
                    mAllSongs = (ArrayList<SongModel>) FetchSongs.getAllAudioFromDevice(getContext());
                    mAdapter.updateSongs(mAllSongs);
                } else {
                    //Permission Denied
                    showPermissionRequestSnackbar();
                }
            });

}