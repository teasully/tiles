package project.etrumper.thomas.ghostbutton;

import android.app.Activity;
import android.content.DialogInterface;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.ArrayList;

public class MainActivity extends Activity {

    GLSurfaceView surfaceView;

    static MainActivity activity;

    @Override
    protected final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = this;

        new SuperManager(getApplicationContext());

        setContentView(R.layout.activity_main);
        RelativeLayout rl = (RelativeLayout)findViewById(R.id.screen);

        surfaceView = new CustomGLSurfaceView();
        rl.addView(surfaceView);

        GestureOverlayView gestureOverlayView = new GestureOverlayView(SuperManager.context);
        //gestureOverlayView.addOnGesturePerformedListener(GestureManager.manager);
        //gestureOverlayView.setGestureStrokeAngleThreshold(90f);
        View.OnTouchListener touch = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                /*if(TouchManager.handleTouch(event)){
                    return true;
                }
                return GestureManager.gestureDetector.onTouchEvent(event);*/
                return TouchManager.handleTouch(event);
            }
        };
        gestureOverlayView.setOnTouchListener(touch);
        /*gestureOverlayView.addOnGestureListener(new GestureOverlayView.OnGestureListener() {
            @Override
            public void onGestureStarted(GestureOverlayView overlay, MotionEvent event) {
                TouchManager.onDown(event);
                //Log.e("Main", "start");
            }

            @Override
            public void onGesture(GestureOverlayView overlay, MotionEvent event) {

            }

            @Override
            public void onGestureEnded(GestureOverlayView overlay, MotionEvent event) {
                TouchManager.onUp(event);
                Log.e("Main", "end");
            }

            @Override
            public void onGestureCancelled(GestureOverlayView overlay, MotionEvent event) {
                Log.e("Main", "Canceled");
            }
        });*/
        gestureOverlayView.setGestureVisible(false);

        rl.addView(gestureOverlayView);


        Display display = getWindowManager().getDefaultDisplay();

        ViewGroup.LayoutParams params = gestureOverlayView.getLayoutParams();
        params.width = display.getWidth();
        params.height = display.getHeight();
        gestureOverlayView.setLayoutParams(params);
        gestureOverlayView.setTranslationX(0);
        params = surfaceView.getLayoutParams();
        params.width = display.getWidth();
        params.height = display.getHeight();
        surfaceView.setLayoutParams(params);
        surfaceView.setTranslationX(0);

        //setContentView(surfaceView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SuperManager.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        SuperManager.onPause();
    }

}
