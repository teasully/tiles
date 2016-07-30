package project.etrumper.thomas.ghostbutton;

import android.util.Log;

/**
 * Created by thoma on 2/12/2016.
 */
public class Triangle3d {

    float[] positions;
    protected int numVertices;

    float color[] = {0.f, 0.f, 0.f, 1.f};// RGBA

    Triangle3d(float[] positions, float[] color){
        if(positions.length != 9){
            Log.e("Triangle3d", "positions != 9");
        }else if(color.length != 4) {
            Log.e("Triangle3d", "color != 4");
        }else{
            this.positions = positions;
            this.numVertices = 3;
            this.color = color;
        }
    }

}
