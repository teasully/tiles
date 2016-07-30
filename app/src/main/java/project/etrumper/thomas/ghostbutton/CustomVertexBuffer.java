package project.etrumper.thomas.ghostbutton;

import android.util.Log;

import java.nio.FloatBuffer;

/**
 * Created by thoma on 2/12/2016.
 */
public class CustomVertexBuffer {

    FloatBuffer floatBufferPositions, floatBufferNormals;
    String material;
    int vertexCount;

    CustomVertexBuffer(FloatBuffer floatBufferPositions, FloatBuffer floatBufferNormals, String material, int vertexCount){
        this.floatBufferPositions = floatBufferPositions;
        this.floatBufferNormals = floatBufferNormals;
        this.material = material;
        this.vertexCount = vertexCount;
    }

}
