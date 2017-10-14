package com.mirego.sherbook.views;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.mirego.sherbook.R;
import com.mirego.sherbook.services.PlaybackService;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MediaControlFragment extends Fragment {
    public MediaControlFragment() {
        // Required empty public constructor
    }

    public enum MediaAction { //Enum is overkill for current task, but expecting more functionality later
        Play,
        Pause,
    }

    PlaybackService playbackService;
    MediaAction CurrentValidAction = MediaAction.Play;

    @BindView(R.id.media_control_seekbar)
    SeekBar seekBar;
    int progressChangedValue = 0;

    @BindView(R.id.media_control_play)
    ImageButton PlayButton;

    @BindView(R.id.media_control_name)
    TextView Name;

    @BindView(R.id.media_control_time)
    TextView Time;

    @BindView(R.id.media_control_image)
    ImageView Image;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_media_control, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        progressChangedValue = progress;
        if(playbackService == null)
        {
            playbackService = ((MainActivity)getActivity()).playbackService;
        }
        int mediaTime = playbackService.getDuration() * (progress/seekBar.getMax());
        playbackService.seekTo(mediaTime);
    }

    public void onStopTrackingTouch(SeekBar seekBar) {
        seekBar.setProgress(progressChangedValue);
    }

    public void setSeekBarProgress(int progressPercent){
        seekBar.setProgress(progressPercent);
    }

    public void setMediaName(String mediaName) {
        Name.setText(mediaName);
    }   

    public void setMediaTime(int currentTime, int maxTime) {
        // Display time in format : 00:00 / 20:00
        Time.setText(String.format("%02d:%02d / %02d:%02d", currentTime/60,currentTime%60,maxTime/60,maxTime%60));
    }

    @OnClick(R.id.media_control_play)
    public void onPlayButtonPressed(View view) {
        switch (CurrentValidAction)
        {
            case Play:
                if(playbackService == null)
                {
                    playbackService = ((MainActivity)getActivity()).playbackService;
                }
                //Snackbar.make(view, "Play", Snackbar.LENGTH_SHORT).show(); //Debug message
                PlayButton.setImageResource(android.R.drawable.ic_media_pause);
                CurrentValidAction = MediaAction.Pause;
                playbackService.start();
                setMediaName(playbackService.getTrackName());
                setMediaTime(playbackService.getCurrentPosition()/1000,playbackService.getDuration()/1000);
                break;
            case Pause:
                if(playbackService == null)
                {
                    playbackService = ((MainActivity)getActivity()).playbackService;
                }
                //Snackbar.make(view, "Pause", Snackbar.LENGTH_SHORT).show(); //Debug message
                PlayButton.setImageResource(android.R.drawable.ic_media_play);
                CurrentValidAction = MediaAction.Play;
                playbackService.pause();
                break;
        }
    }
}
