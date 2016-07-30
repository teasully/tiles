package project.etrumper.thomas.ghostbutton;

import android.util.Log;

/**
 * Created by thoma on 2/10/2016.
 */
public class Manager {

    String _TAG;

    Manager(String TAG){
        _TAG = TAG;
    }

    protected void LOGE(String message){
        Log.e(_TAG, message);
    }

    protected void LOGE(Throwable e){
        LOGE(Log.getStackTraceString(e));
    }

}
