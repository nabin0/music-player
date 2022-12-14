package com.nabin.musik.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.audiofx.AudioEffect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.nabin.musik.R;
import com.nabin.musik.Services.MyMusicPlayerService;

public class EqualizerFragment extends Fragment {

    private TextView textView;

    public EqualizerFragment() {
        // Required empty public constructor
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_equalizer, container, false);
        textView = view.findViewById(R.id.showMsg);
        if (getArguments() != null) {
            textView.setText(getArguments().getString("eqFound"));
        } else {
            try {
                Intent eqIntent = new Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL);
                eqIntent.putExtra(AudioEffect.EXTRA_PACKAGE_NAME, getActivity().getPackageName());
                eqIntent.putExtra(AudioEffect.EXTRA_CONTENT_TYPE, AudioEffect.CONTENT_TYPE_MUSIC);
                eqIntent.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, MyMusicPlayerService.mMediaPlayer.getAudioSessionId());
                getActivity().startActivityForResult(eqIntent, 15);
            } catch (Exception e) {
                Toast.makeText(getContext(), "Equalizer not found", Toast.LENGTH_SHORT).show();
                textView.setText("Equalizer Not Found");
            }
        }
        return view;
    }
}