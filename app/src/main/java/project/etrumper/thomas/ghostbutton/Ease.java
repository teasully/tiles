package project.etrumper.thomas.ghostbutton;

import android.util.Log;

/**
 * Created by thoma on 2/13/2016.
 */

enum EaseType{
    LINEAR,
    QUADRATIC
}

public class Ease extends Attribute {

    int shouldEase[];
    long[] easeTimers, easeLengths;
    float[] startPosition, destination;

    EaseType easeType;

    Ease() {
        super(9, "Ease", AttributeType.EASE);

        shouldEase = new int[9];

        easeTimers = new long[9];
        easeLengths = new long[9];
        startPosition = new float[9];
        destination = new float[9];

        easeType = EaseType.LINEAR;
    }

    @Override
    protected float[] update(float[] inData) {
        if (inData.length == 1 && shouldEaseF()) {
            long deltaTime = (long) inData[0];
            data = getEasePosition(destination, deltaTime);
        } else if (inData.length == 5) {
            int index = (int) inData[0],
                    indexModifier = 0;  // 0 for positions, 3 for angles, 6 for scaling
            if (inData[1] == 1) {
                indexModifier = 3;
            }else if(inData[1] == 2){
                indexModifier = 6;
            }
            if(inData[2] < 50.f || inData[2] == Float.MAX_VALUE){   // If motion would take 50 milliseconds..useless movement
                return new float[]{};
            }
            shouldEase[indexModifier + index] = 1;
            easeTimers[index + indexModifier] = 0;
            easeLengths[index + indexModifier] = (long) inData[2];
            startPosition[index + indexModifier] = inData[3];
            destination[index + indexModifier] = inData[4];
        }
        return new float[]{};
    }

    private float[] getEasePosition(float[] easePoint, float deltaTime) {
        float[] velocities = new float[9];
        for (int i = 0; i < 9; i++) {
            if (shouldEase[i] == 0 || easeTimers[i] > easeLengths[i]) {
                velocities[i] = 0.666f;
            } else {
                easeTimers[i] += deltaTime;
                float t = easeTimers[i],
                        b = startPosition[i],
                        c = (easePoint[i] - startPosition[i]),
                        d = easeLengths[i];
                float result = 0f;
                if (easeType == EaseType.LINEAR) {
                    result = (float) easeLinear(t / 1000f, b, c, d / 1000f);
                } else if (easeType == EaseType.QUADRATIC) {
                    result = (float) easeQuadradic(t / 1000f, b, c, d / 1000f);
                }
                velocities[i] = result;
            }
        }
        return velocities;
    }

    private double easeLinear(double t, double b, double c, double d) {
        return c * t / d + b;
    }

    private double easeQuadradic(double t, double b, double c, double d) {
        t /= d / 2f;
        if (t < 1f) {
            return c / 2f * t * t + b;
        }
        t--;
        return -c / 2f * (t * (t - 2f) - 1f) + b;
    }

    @Override
    protected float[] getData(float[] inData) {    // positions[3], mode(0 = pos, 1 = angle, 2 = scale)
        float[] sendData = new float[3];
        // Make sure parameters have enough data
        if (inData.length != 4) {
            LOGE("Trying to getData in Ease with length != 4");
            return new float[]{};
        }
        // Set up iterator for correct data
        int i = 0;
        if (inData[3] == 1) { // If looking for angle data
            i = 3;
        }else if(inData[3] == 2){
            i = 6;
        }
        for (int u = 0; u < 3; u++, i++) {
            if (data[i] != 0.666f) {
                sendData[u] = (inData[u] - (shouldEase[i] == 1 ? data[i] : inData[u])) * -1f;    // Calculate velocity
            } else {
                if(shouldEase[i] == 1) {    // If is last update
                    shouldEase[i] = 0;
                    easeTimers[i] = 0;
                    sendData[u] = (inData[u] - this.destination[i]) * -1f;
                }
            }
        }
        return sendData;
    }

    protected boolean shouldEaseF() {
        return (shouldEase[0] == 1 || shouldEase[1] == 1 || shouldEase[2] == 1 || shouldEase[3] == 1 ||
                shouldEase[4] == 1 || shouldEase[5] == 1 || shouldEase[6] == 1 || shouldEase[7] == 1 ||
                shouldEase[8] == 1);
    }

    public static void startEase(float[] destination, BasicEntity object, long timeInMilli, int mode) {
        Ease ease = object.ease;
        if (ease == null) {
            Log.e("startEase", "Returned getFirstAttributeTypeIndex returned -1");
            return;
        }
        int u = 0;
        if (mode == 1) {
            u = 3;
        }else if(mode == 2){
            u = 6;
        }
        for (int i = 0; i < 3; i++, u++) {
            if (Float.isNaN(destination[i]) || destination[i] == 0.666f) {
                // Either NaN or not needed
            } else {
                float gPosition = object.position[i];
                if(mode == 1){
                    gPosition = object.rotation[i];
                }else if(mode == 2){
                    gPosition = object.scale[i];
                }
                float[] data = {i, mode, timeInMilli, gPosition, destination[i]};
                ease.update(data);
            }
        }
    }

    public static void startEase(float[] destination, BasicEntity object, int mode) {
        Ease ease = object.ease;
        if (ease == null) {
            Log.e("startEase", "Returned getFirstAttributeTypeIndex returned -1");
            return;
        }
        int u = 0;
        if (mode == 1) {
            u = 3;
        }else if(mode == 2){
            u = 6;
        }
        for (int i = 0; i < 3; i++, u++) {
            if (Float.isNaN(destination[i]) || destination[i] == 0.666f) {
                // Either NaN or not needed
            } else {
                float velocity = object.positionalVelocity;
                float gPosition = object.position[i];
                if(mode == 1){
                    velocity = object.anglarVelocity;
                    gPosition = object.rotation[i];
                }else if(mode == 2){
                    velocity = object.scalingVelocity;
                    gPosition = object.scale[i];
                }
                float time = Math.abs((destination[i] - gPosition) / velocity) * 1000.f;
                float[] data = {i, mode, time, gPosition, destination[i]};
                ease.update(data);
            }
        }
    }

    public static void startEaseBy(float[] offset, BasicEntity object, int mode) {
        float[] currentPosition = object.position;
        if(mode == 1){
            currentPosition = object.rotation;
        }else if(mode == 2){
            currentPosition = object.scale;
        }
        float[] newPosition = new float[]{currentPosition[0] + offset[0], currentPosition[1] + offset[1], currentPosition[2] + offset[2]};
        startEase(newPosition, object, mode);
    }

    public static void startEaseBy(float[] offset, BasicEntity object, long timeInMilli, int mode) {
        float[] currentPosition = object.position;
        if(mode == 1){
            currentPosition = object.rotation;
        }else if(mode == 2){
            currentPosition = object.scale;
        }
        float[] newPosition = new float[]{currentPosition[0] + offset[0], currentPosition[1] + offset[1], currentPosition[2] + offset[2]};
        startEase(newPosition, object, timeInMilli, mode);
    }

}