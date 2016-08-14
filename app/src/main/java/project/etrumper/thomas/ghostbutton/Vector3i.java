package project.etrumper.thomas.ghostbutton;

import android.util.Log;

/**
 * Created by thoma on 7/18/2016.
 */
public class Vector3i extends Logable{

    int[] vector;

    Vector3i(){
        super("Vector3i");
        this.vector = new int[3];
    }

    Vector3i(int ... vector){
        super("Vector3i");
        if(vector.length != 3){
            LOGE("Constructor with int[] vector.length != 3");
        }else{
            this.vector = vector;
        }
    }

    public int x(){
        return this.vector[0];
    }

    public int y(){
        return this.vector[1];
    }

    public int z(){
        return this.vector[2];
    }

    public void x(int x){
        this.vector[0] = x;
    }

    public void y(int y){
        this.vector[1] = y;
    }

    public void z(int z){
        this.vector[2] = z;
    }

    public static Vector3i add(Vector3i ... vectors){
        // Check params
        if(vectors == null || vectors.length < 1){
            Log.e("Vector3i", "Trying to add with null or < 1 vectors");
            return null;
        }else if(vectors.length == 1){
            return vectors[0];
        }
        // Set local variable to send back
        int[] result = new int[]{0, 0, 0};
        // Iterate through params and add to local variable
        for(Vector3i vector : vectors){
            result = new int[]{result[0] + vector.x(), result[1] + vector.y(), result[2] + vector.z()};
        }
        // return result
        return new Vector3i(result);
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

    public static boolean equals(Vector3i ... vectors){
        // param check
        if(vectors == null || vectors.length <= 1){
            return false;
        }
        Vector3i compare = vectors[0];
        for(Vector3i vector3i : vectors){
            if(!equals(compare, vector3i)){
                return false;
            }
        }
        return true;
    }

    public static boolean equals(Vector3i one, Vector3i two){
        if(one == null || two == null){
            return false;
        }
        if(one.x() != two.x() || one.y() != two.y() || one.z() != two.z()){
            return false;
        }
        return true;
    }

    @Override
    public String toString(){
        return String.format("%d, %d, %d", this.x(), this.y(), this.z());
    }

}
