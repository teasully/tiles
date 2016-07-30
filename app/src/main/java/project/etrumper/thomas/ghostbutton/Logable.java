package project.etrumper.thomas.ghostbutton;

import android.util.Log;

/**
 * Created by thoma on 6/14/2016.
 */
public class Logable {

    boolean DEBUG = true;

    Logable(String TAG){
        this.TAG = TAG;
    }

    String TAG;

    protected void LOGE(String message){
        // Do not log if object is not set in debug mode
        if(!this.DEBUG){
            return;
        }
        Log.e(this.TAG, message);
    }

}
