package com.example.dharmendraverma.myapplication;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.example.dharmendraverma.myapplication.gpuimage.GPUBeautificationFilter;
import com.example.dharmendraverma.myapplication.gpuimage.GPUImageCarouselGroup;
import com.example.dharmendraverma.myapplication.gpuimage.GPUImageCarouselOnTouchListener;
import com.example.dharmendraverma.myapplication.gpuimage.GPUImageFilter;
import com.example.dharmendraverma.myapplication.gpuimage.IFBrannanFilterOptimized;
import com.example.dharmendraverma.myapplication.gpuimage.IFFadeOut;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    protected Resources mResources;

   // private VideoSurfaceView mVideoView = null;
    String mVideoFilepath = null;
    GLSurfaceView mGLSurfaceView;
    private VideoRender mRenderer;
    private VideoFilter mVideoFilter;
    RelativeLayout relativeLayout;
/*    private MediaPlayer mMediaPlayer = null;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mResources = getResources();
        /*mMediaPlayer = new MediaPlayer();*/
        mVideoFilepath = "android.resource://" + getPackageName() + "/" + R.raw.video;
        relativeLayout =(RelativeLayout)findViewById(R.id.root) ;
        /*mMediaPlayer = new MediaPlayer();*/
      /*  try {
            mMediaPlayer.setDataSource(this,Uri.parse(mVideoFilepath));

        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }*/
       // mMediaPlayer.prepareAsync();
        mVideoFilter = new VideoFilter(1280,720);
        mVideoFilter.setContext(this);
        mRenderer = new VideoRender(R.raw.video,mVideoFilter,this);
        mGLSurfaceView = (GLSurfaceView)findViewById(R.id.glsurfaceview);
        mGLSurfaceView.setEGLContextClientVersion(2);
        mGLSurfaceView.setRenderer(mRenderer);
        mGLSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        relativeLayout.setOnTouchListener(new GPUImageCarouselOnTouchListener(this) {
            @Override
            public void onTouchMoved(float newPos, int status) {
                mVideoFilter.onTouchMoved(newPos, status);
            }
        });
       // mGLSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
      //  mVideoView = new VideoSurfaceView(R.raw.video,this);
        /*setContentView(mVideoView);*/
    }

    @Override
    protected void onResume() {
        super.onResume();
       // mVideoView.onResume();
    }


}
