package project.etrumper.thomas.ghostbutton;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

/**
 * Created by thoma on 2/17/2016.
 */
public class TiltManager implements SensorEventListener{

    static SensorManager sensorManager;
    static Sensor accelerometerSensor, magneticSensor;
    static float[] accelerometerValues;

    static TiltManager tiltManager;

    public static TiltManager init(){
        sensorManager = (SensorManager) SuperManager.context.getSystemService(Context.SENSOR_SERVICE);
        if(sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) == null){
            LOGE("No accelerometer sensor available..");
        }else{
            accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }
        if(sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) == null){
            LOGE("No magnetic field sensor available");
            magneticSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        }
        tiltManager = new TiltManager();
        return tiltManager;
    }

    private static void LOGE(String message){
        Log.e("TiltManager", message);
    }

    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy){

    }

    @Override
    public final void onSensorChanged(SensorEvent event){
        accelerometerValues = null;
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            accelerometerValues = event.values;
            //LOGE(String.format("%f, %f, %f : %f", accelerometerValues[0], accelerometerValues[1], accelerometerValues[2], accelerometerValues[1] + accelerometerValues[2]));
        }
    }

    public void onResume(){
        sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
        //sensorManager.registerListener(this, magneticSensor, SensorManager.SENSOR_DELAY_GAME);
    }

    public void onPause(){
        sensorManager.unregisterListener(this, accelerometerSensor);
        //sensorManager.unregisterListener(this, magneticSensor);
    }

}
