package project.etrumper.thomas.ghostbutton;

import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

import java.util.ArrayList;

/**
 * Created by thoma on 4/29/2016.
 */
public class GestureManager implements GestureOverlayView.OnGesturePerformedListener {

    static String _TAG;

    static GestureLibrary gestureLibrary;

    static GestureManager manager;

    static GestureDetectorCompat gestureDetector;

    static int onscrollDOWNID = 0;

    protected static void init(){
        manager = new GestureManager();
        _TAG = "GestureManager";

        //gestureLibrary = GestureLibraries.fromRawResource(SuperManager.context, R.raw.gesture);
        if(!gestureLibrary.load()){
            LOGE("Failed to loadElements gesture.txt");
            return;
        }

        gestureDetector = new GestureDetectorCompat(SuperManager.context, new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public void onShowPress(MotionEvent e) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                //if(onscrollDOWNID == TouchManager.DOWNID){
                //    return true;
               // }
                //onscrollDOWNID = TouchManager.DOWNID;
                //LOGE("Scroll " + distanceX + " : " + distanceY);
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {

            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                LOGE("Fling " + velocityX + " : " + velocityY);
                return true;
            }
        });
    }

    @Override
    public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
        ArrayList<Prediction> predictions = gestureLibrary.recognize(gesture);
        // one prediction needed
        if (predictions.size() > 0) {
            Prediction prediction = predictions.get(0);
            // checking prediction
            if (prediction.score > 1.0) {
                // and action
                //LOGE(prediction.name);
            }
        }
    }

    protected static void LOGE(String message){
        Log.e(_TAG, message);
    }
}
