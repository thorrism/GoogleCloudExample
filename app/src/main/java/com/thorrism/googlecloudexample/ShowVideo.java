package com.thorrism.googlecloudexample;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.MediaController;
import android.widget.VideoView;


public class ShowVideo extends Activity {
    private VideoView mVideoView;
    private MediaController mController;
    private String mPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_video);
        mVideoView  = (VideoView) findViewById(R.id.video_element_view);
        mController = new MediaController(ShowVideo.this);
        mVideoView.setMediaController(mController);

        if(mPath == null) mPath = getIntent().getExtras().getString("PATH");
        mVideoView.setVideoPath(mPath);
        mVideoView.start();
    }

    @Override
    public void onSaveInstanceState(Bundle savedState){
        super.onSaveInstanceState(savedState);
        savedState.putString("PATH", mPath);
        savedState.putInt("POSITION", mVideoView.getCurrentPosition());
    }

    @Override
    public void onRestoreInstanceState(Bundle savedState){
        super.onRestoreInstanceState(savedState);
        mPath = savedState.getString("PATH");
        mVideoView.seekTo(savedState.getInt("POSITION"));
        mVideoView.start();
    }
}
