package com.nabin.musik.activities;


import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.nabin.musik.R;
import com.nabin.musik.Services.MyMusicPlayerService;
import com.nabin.musik.fragments.AlbumFragment;
import com.nabin.musik.fragments.EqualizerFragment;
import com.nabin.musik.fragments.MySongsFragment;
import com.nabin.musik.fragments.OnlineSongsFragment;
import com.nabin.musik.fragments.SearchFragment;
import com.nabin.musik.interfaces.SongListRecyclerViewItemClick;

public class MainActivity extends AppCompatActivity implements SongListRecyclerViewItemClick {
    // Views
    private BottomNavigationView mBottomNavigationView;

    //Vars
    public static final String SORT_SHARED_PREF_VALUE = "sort_by_shared_pref";
    public static final String SORT_MODE = "sort_mode";
    public static Boolean SHOW_BOTTOM_SONG_CONTROL = false;
    private LocalBroadcastManager broadcastManager;

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("close_app")) {
                finishAffinity();
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize all the views
        initViews();

        //Register register
        broadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("close_app");
        broadcastManager.registerReceiver(broadcastReceiver, intentFilter);

        /*
         * Setting item selectListener on bottom navigation view.
         * Flag == 0 will add fragment
         * Flag == 1 will replace fragment
         */
        mBottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.songsList) {
                    setFragmentOnBottomItemClick(new MySongsFragment(), 1);
                } else if (id == R.id.searchSongs) {
                    setFragmentOnBottomItemClick(new SearchFragment(), 1);
                } else if (id == R.id.playSongsOnline) {
                    setFragmentOnBottomItemClick(new OnlineSongsFragment(), 1);
                } else if (id == R.id.album) {
                    setFragmentOnBottomItemClick(new AlbumFragment(), 1);
                } else {
                    setFragmentOnBottomItemClick(new EqualizerFragment(), 1);
                }
                return true;
            }
        });
        mBottomNavigationView.setSelectedItemId(R.id.songsList);

    }

    @Override
    protected void onResume() {
        super.onResume();

        //Get shared preference data for the bottom player
        SharedPreferences sharedPreferences = getSharedPreferences(MyMusicPlayerService.SONG_DETAIL_CONTROL_DATA, MODE_PRIVATE);

        if (sharedPreferences != null) {
            String songUri = sharedPreferences.getString(MyMusicPlayerService.SONG_URI, null);
            if (songUri != null) {
                SHOW_BOTTOM_SONG_CONTROL = true;
            } else {
                SHOW_BOTTOM_SONG_CONTROL = false;
            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //Unregister receiver
        unregisterReceiver(broadcastReceiver);
    }

    private void initViews() {
        mBottomNavigationView = findViewById(R.id.bottomNavigationView);
    }

    private void setFragmentOnBottomItemClick(Fragment fragment, int flag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (flag == 0)
            transaction.add(R.id.frameContainerFrameLayout, fragment);
        else
            transaction.replace(R.id.frameContainerFrameLayout, fragment);
        transaction.commit();
    }

    // Set Menu and Listen Clicks

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar_menu_items, menu);

        MenuItem searchItem = menu.findItem(R.id.searchBar);
        androidx.appcompat.widget.SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setQueryHint("Search Music...");

        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = getSharedPreferences(SORT_SHARED_PREF_VALUE, MODE_PRIVATE).edit();
        // Handle Option click
        int id = item.getItemId();
        switch (id) {
            case R.id.sortByName:
                editor.putString(SORT_MODE, "sortByName");
                editor.apply();
                this.recreate();
                break;
            case R.id.sortByDate:
                editor.putString(SORT_MODE, "sortByDate");
                editor.apply();
                this.recreate();
                break;
            case R.id.sortBySize:
                editor.putString(SORT_MODE, "sortBySize");
                editor.apply();
                this.recreate();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MySongsFragment.STORAGE_PERMISSION_REQUEST_CODE) {
//            if (grantResults.length > 0) {
//                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    MySongsFragment.mAllSongs = (ArrayList<SongModel>) FetchSongs.getAllAudioFromDevice(this);
//                    MySongsFragment.mAdapter.updateSongs(MySongsFragment.mAllSongs);
//                }
//            }
        }
    }

    @Override
    public void onRecyclerItemClick(int position) {
        Intent playSongIntent = new Intent(MainActivity.this, PlaySongActivity.class);
        playSongIntent.putExtra("position", position);
        startActivity(playSongIntent);
    }
}