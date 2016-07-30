package project.etrumper.thomas.ghostbutton;


import android.util.Log;

/**
 * Created by thoma on 7/18/2016.
 */
public class Vector3f extends Logable{

    float[] vector;

    Vector3f(){
        super("Vector3f");
        this.vector = new float[3];
    }

    Vector3f(float ... vector){
        super("Vector3f");
        if(vector.length != 3){
            LOGE("Constructor with float[] vector.length != 3");
        }else{
            this.vector = vector;
        }
    }

    public float x(){
        return this.vector[0];
    }

    public float y(){
        return this.vector[1];
    }

    public float z(){
        return this.vector[2];
    }

    public void x(float x){
        this.vector[0] = x;
    }

    public void y(float y){
        this.vector[1] = y;
    }

    public void z(float z){
        this.vector[2] = z;
    }

    public static Vector3f add(Vector3f ... vectors){
        // Check params
        if(vectors == null || vectors.length < 1){
            Log.e("Vector3i", "Trying to add with null or < 1 vectors");
            return null;
        }else if(vectors.length == 1){
            return vectors[0];
        }
        // Set local variable to send back
        float[] result = new float[]{0, 0, 0};
        // Iterate through params and add to local variable
        for(Vector3f vector : vectors){
            result = new float[]{result[0] + vector.x(), result[1] + vector.y(), result[2] + vector.z()};
        }
        // return result
        return new Vector3f(result);
    }

    public void increment(int index) {
        switch (index) {
            case (0):
                this.vector[0]++;
                break;
            case (1):
                this.vector[1]++;
                break;
            case (2):
                this.vector[2]++;
                break;
        }
    }

    public void deincrement(int index) {
        switch (index) {
            case (0):
                this.vector[0]--;
                break;
            case (1):
                this.vector[1]--;
                break;
            case (2):
                this.vector[2]--;
                break;
        }
    }

    public static boolean equals(Vector3f ... vectors){
        // param check
        if(vectors == null || vectors.length <= 1){
            return false;
        }
        Vector3f compare = vectors[0];
        for(Vector3f vector3i : vectors){
            if(compare.x() != vector3i.x() || compare.y()!= vector3i.y() || compare.z() != vector3i.z()){
                return false;
            }
        }
        return true;
    }

}
