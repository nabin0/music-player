package com.nabin.musik.fragments;

import android.content.Intent;
import android.media.audiofx.AudioEffect;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.nabin.musik.R;


public class EqualizerFragment extends Fragment {


    public EqualizerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_equalizer, container, false);



        return view;
    }
}