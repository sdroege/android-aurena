package com.collabora.android_aurena;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import com.gst_sdk.GStreamer;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.os.Bundle;
import android.os.Environment;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class AndroidAurena extends Activity implements SurfaceHolder.Callback {
    private static native boolean classInit();
    private native void nativeInit();
    private native void nativeFinalize();
    private native void nativePlay();
    private native void nativePause();
    private native void nativeSurfaceInit(Object surface);
    private native void nativeSurfaceFinalize();
    private long native_custom_data;

    private boolean is_playing_desired;
    private int position;
    private int duration;

    /* Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        try {
        GStreamer.init(this);
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            finish(); 
            return;
        }

        setContentView(R.layout.main);

        ImageButton play = (ImageButton) this.findViewById(R.id.button_play);
        play.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                is_playing_desired = true;
                nativePlay();
            }
        });

        ImageButton pause = (ImageButton) this.findViewById(R.id.button_stop);
        pause.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                is_playing_desired = false;
                nativePause();
            }
        });
        
        SurfaceView sv = (SurfaceView) this.findViewById(R.id.surface_video);
        SurfaceHolder sh = sv.getHolder();
        sh.addCallback(this);

        is_playing_desired = false;

        nativeInit();
    }
    
    protected void onDestroy() {
        nativeFinalize();
        super.onDestroy();
    }

    /* Called from native code */
    private void setMessage(final String message) {
        final TextView tv = (TextView) this.findViewById(R.id.textview_message);
        runOnUiThread (new Runnable() {
          public void run() {
            tv.setText(message);
          }
        });
    }
    
    /* Called from native code */
    private void onGStreamerInitialized () {    	
        if (is_playing_desired) {
            nativePlay();
        } else {
            nativePause();
        }
    }

    /* The text widget acts as an slave for the seek bar, so it reflects what the seek bar shows, whether
     * it is an actual pipeline position or the position the user is currently dragging to.
     */
    private void updateTimeWidget () {
        final TextView tv = (TextView) this.findViewById(R.id.textview_time);

        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        final String message = df.format(new Date (position)) + " / " + df.format(new Date (duration));
        tv.setText(message);        
    }

    /* Called from native code */
    private void setCurrentPosition(final int position, final int duration) {
        this.position = position;
        this.duration = duration;

        runOnUiThread (new Runnable() {
          public void run() {
            updateTimeWidget();
          }
        });
    }

    /* Called from native code */
    private void setCurrentState (int state) {
        Log.d ("GStreamer", "State has changed to " + state);
        switch (state) {
        case 1:
            setMessage ("NULL");
            break;
        case 2:
            setMessage ("READY");
            break;
        case 3:
            setMessage ("PAUSED");
            break;
        case 4:
            setMessage ("PLAYING");
            break;
        }
    }

    static {
        System.loadLibrary("gstreamer_android");
        System.loadLibrary("android-aurena");
        classInit();
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width,
            int height) {
        Log.d("GStreamer", "Surface changed to format " + format + " width "
                + width + " height " + height);
        nativeSurfaceInit (holder.getSurface());
    }

    public void surfaceCreated(SurfaceHolder holder) {
        Log.d("GStreamer", "Surface created: " + holder.getSurface());
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d("GStreamer", "Surface destroyed");
        nativeSurfaceFinalize ();
    }
}
