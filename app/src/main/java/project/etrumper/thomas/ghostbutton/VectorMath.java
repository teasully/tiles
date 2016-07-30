package project.etrumper.thomas.ghostbutton;

import android.util.Log;

import java.lang.reflect.Array;
import java.util.Arrays;

/**
 * Created by thoma on 2/17/2016.
 */
public class VectorMath {

    public static float[] transform(float[] mat16, float[] vec) {
        float a = mat16[0],
                b = mat16[1],
                c = mat16[2],
                d = mat16[3],
                e = mat16[4],
                f = mat16[5],
                g = mat16[6],
                h = mat16[7],
                i = mat16[8],
                j = mat16[9],
                k = mat16[10],
                l = mat16[11],
                m = mat16[12],
                n = mat16[13],
                o = mat16[14],
                p = mat16[15],
                x = vec[0],
                y = vec[1],
                z = vec[2];
        if (vec.length == 4) {
            float w = vec[3];
            return new float[]{a * x + b * y + c * z + d * w,
                    e * x + f * y + g * z + h * w,
                    i * x + j * y + k * z + l * w,
                    m * x + n * y + o * z + p * w};
        }
        return new float[]{a * x + e * y + i * z + m,
                b * x + f * y + j * z + n,
                c * x + g * y + k * z + o};
    }

    public static float magnitude(float[] vector) {
        return (float) Math.sqrt(vector[0] * vector[0] + vector[1] * vector[1] + vector[2] * vector[2]);
    }

    public static float[] normalize(float[] vector) {
        float mag = magnitude(vector);
        return new float[]{vector[0] / mag, vector[1] / mag, vector[2] / mag};
    }

    public static float[] addVectors(float[]... params) {
        if(params.length > 0){
            int len = params[0].length; // length of one of the vectors (they must be same size)
            float[] passVector = new float[len];
            for(float[] vector : params){
                //Log.e("addVectors", String.format("Adding %s by %s", toString(passVector), toString(vector)));
                for(int i = 0; i < len; i++){
                    passVector[i] += vector[i];
                }
                //Log.e("addVectors", String.format("Got %s", toString(passVector)));
            }
            //Log.e("addVectors", String.format("Returning %s", toString(passVector)));
            return passVector;
        }
        return new float[]{};
    }

    public static float[] combineVectors(float[] ... params){
        int numElements = 0;
        for(float[] array : params){
            numElements += array.length;
        }
        float[] returnArray = new float[numElements];
        int iter = 0;
        for(float[] array : params){
            for(float data : array){
                returnArray[iter] = data;
                iter++;
            }
        }
        return returnArray;
    }

    public static Attribute[] combineVectors(Attribute[] first, Attribute[] second){
        int length = 0;
        for(Attribute attribute : first){
            if(attribute != null){
                length++;
            }
        }
        for(Attribute attribute : second){
            if(attribute != null){
                length++;
            }
        }
        if(length == 0){
            return new Attribute[]{};
        }
        Attribute[] returnVector = new Attribute[length];
        int currentIndex = 0;
        for(Attribute attribute : first){
            if(attribute != null){
                returnVector[currentIndex++] = attribute;
            }
        }
        for(Attribute attribute : second){
            if(attribute != null){
                returnVector[currentIndex++] = attribute;
            }
        }
        return returnVector;
    }

    public static float[] addOneToArray(float[] array, float f){
        array = Arrays.copyOf(array, array.length + 1);
        array[array.length - 1] = f;
        return array;
    }

    public static String toString(float[] vector){
        String res = "";
        for(float value : vector){
            res = res.concat("" + value + " ");
        }
        return res;
    }

    public static float[] subtractVectors(float[] original, float[]... subtractBy) {
        if(subtractBy.length > 0){
            float[] n = original.clone();
            for(float[] vector : subtractBy){
                //Log.e("subtractVectors", String.format("Subtracting %s by %s", toString(original), toString(vector)));
                for(int i = 0; i < n.length; i++){
                    n[i] -= vector[i];
                }
                //Log.e("subtractVectors", String.format("Got %s", toString(original)));
            }
            //Log.e("subtractVectors", String.format("Return %s", toString(original)));
            return n;
        }
        return new float[]{};
    }

    public static float[] multiplyVectorBy(float[] vector, float amount){
        float[] passvec = new float[vector.length];
        int iter = 0;
        //Log.e("multiplyVectorBy", String.format("Multiplying %s by %f", toString(vector), amount));
        for(float value : vector){
            passvec[iter++] = value * amount;
        }
        //Log.e("multiplyVectorBy", String.format("Got %s", toString(passvec)));
        return passvec;
    }

    public static float[] invertSimple(float[] mf) {
        float[] R = new float[16];
        R[0] = mf[0];
        R[1] = mf[4];
        R[2] = mf[8];
        R[3] = 0.0f;
        R[4] = mf[1];
        R[5] = mf[5];
        R[6] = mf[9];
        R[7] = 0.0f;
        R[8] = mf[2];
        R[9] = mf[6];
        R[10] = mf[10];
        R[11] = 0.0f;
        R[12] = -(mf[12] * mf[0]) - (mf[13] * mf[1]) - (mf[14] * mf[2]);
        R[13] = -(mf[12] * mf[4]) - (mf[13] * mf[5]) - (mf[14] * mf[6]);
        R[14] = -(mf[12] * mf[8]) - (mf[13] * mf[9]) - (mf[14] * mf[10]);
        R[15] = 1.0f;
        return R;
    }
}
