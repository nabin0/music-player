package com.nabin.musik.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import com.nabin.musik.R;

public class SettingsFragment extends Fragment {
    public static final String DARK_MODE_SP = "dark_mode";

    SwitchCompat mUiSwitchThemeMode;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        mUiSwitchThemeMode = view.findViewById(R.id.switchUiMode);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(DARK_MODE_SP, Context.MODE_PRIVATE);
        if(sharedPreferences.getBoolean("dark_mode", false)){
            mUiSwitchThemeMode.setChecked(true);
        }

        mUiSwitchThemeMode.setOnCheckedChangeListener((compoundButton, b) -> {
            SharedPreferences.Editor editor = getActivity().getSharedPreferences(DARK_MODE_SP, Context.MODE_PRIVATE).edit();
            if (b) {
                //Set Dark Mode
                editor.putBoolean("dark_mode",true);
                editor.putBoolean("recreate_activity", true);
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                // Set Light Mode
                editor.putBoolean("dark_mode",false);
                editor.putBoolean("recreate_activity", true);
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
            editor.apply();
        });

        return view;
    }
}