package project.etrumper.thomas.ghostbutton;

import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Arrays;
import java.util.Vector;

/**
 * Created by thoma on 2/18/2016.
 */
public class Mesh {

    String objectName;
    int numMaterials, numVerts, numNorms;
    float[] verts, norms, // Used for initialize()
            lowerMostPoint, topMostPoint;   // Used for initializing BoundingBox
    Indice[] indices;
    CustomVertexBuffer[] vertexBuffers;

    Mesh(String objectName, Indice[] indices, int numMaterials) {
        this.objectName = objectName;
        this.indices = indices;
        this.numMaterials = numMaterials;
        //Log.e("Mesh", String.format("Created object %s with %d indices and %d materials", objectName, indices.length, numMaterials));
    }

    Mesh(String objectName){
        this.objectName = objectName;
        //Log.e(this.objectName, "Created");
    }

    public void setFloatArrays(float[] verts, float[] norms) {
        this.verts = verts;
        this.norms = norms;
        this.numVerts = verts.length;
        this.numNorms = norms.length;
        this.initialize();
        this.clean();
    }

    public void initialize() {   // Uses verts and norms to calculate vertexBuffer and normalBuffer
        if (verts == null || norms == null) { // if either arrays have no data
            return;
        }
        lowerMostPoint = new float[]{Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE};
        topMostPoint = new float[]{Float.MIN_VALUE, Float.MIN_VALUE, Float.MIN_VALUE};
        String[] materialBuffer = new String[numMaterials];
        Vector<float[]> _buffers1 = new Vector<>(),
                _buffers2 = new Vector<>(); // Create Vertex + Normal float buffers
        int materialBufferIt = 0;

        for (Indice indice : indices) {
            String indiceMaterial = indice.material;
            boolean found = false;
            int materialIt = 0;
            for (String material : materialBuffer) {
                if (material != null) {
                    if (material.equals(indiceMaterial)) {    //If material is found
                        found = true;
                        break;
                    }
                    materialIt++;
                }
            }
            float[] cverts = indice.calculatePositions(this.verts),
                    cnorms = indice.calculateNormals(this.norms);
            for(int i = 0; i < cverts.length; i+=3){
                float x = cverts[i],
                        y = cverts[i+1],
                        z = cverts[i+2];
                float sumCurrentLower = (lowerMostPoint[0] + lowerMostPoint[1] + lowerMostPoint[2]),
                        sumCurrentTop = (topMostPoint[0] + topMostPoint[1] + topMostPoint[2]),
                        sumCurrentPoint = (x + y + z);
                if(sumCurrentPoint < sumCurrentLower){
                    lowerMostPoint = new float[]{x, y, z};
                }
                if(sumCurrentPoint > sumCurrentTop){
                    topMostPoint = new float[]{x, y, z};
                }
            }
            if (found) {
                _buffers1.set(materialIt, VectorMath.combineVectors(_buffers1.get(materialIt), cverts));
                _buffers2.set(materialIt, VectorMath.combineVectors(_buffers2.get(materialIt), cnorms));
            } else {
                materialBuffer[materialBufferIt++] = indiceMaterial;
                _buffers1.add(cverts);
                _buffers2.add(cnorms);
            }
        }

        //Log.e("mesh."+objectName, String.format("Lowest point: %f, %f, %f | Highes: %f, %f, %f", lowerMostPoint[0], lowerMostPoint[1], lowerMostPoint[2], topMostPoint[0], topMostPoint[1],topMostPoint[2]));
        vertexBuffers = new CustomVertexBuffer[numMaterials];

        for (int i = 0; i < numMaterials; i++) {

            float[] _positions = _buffers1.get(i),
                    _normals = _buffers2.get(i);

            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(_positions.length * 4);   // float = 4 bytes
            byteBuffer.order(ByteOrder.nativeOrder());
            FloatBuffer floatBufferPos = byteBuffer.asFloatBuffer();
            floatBufferPos.put(_positions);
            floatBufferPos.position(0);

            byteBuffer = ByteBuffer.allocateDirect(_normals.length * 4);   // float = 4 bytes
            byteBuffer.order(ByteOrder.nativeOrder());
            FloatBuffer floatBufferNorm = byteBuffer.asFloatBuffer();
            floatBufferNorm.put(_normals);
            floatBufferNorm.position(0);

            vertexBuffers[i] = new CustomVertexBuffer(floatBufferPos, floatBufferNorm, materialBuffer[i], _positions.length / 3);
        }
    }

    public void clean() {  // Clean vert, norm, and indice arrays to free memory..All vertex positions stored in CVB
        verts = null;
        norms = null;
        indices = null;
    }

    public void hardCodeFloats(String mtl, ByteBuffer verts, ByteBuffer norms){
        if(this.vertexBuffers == null || this.vertexBuffers.length == 0){
            this.vertexBuffers = new CustomVertexBuffer[1];
        }else{
            CustomVertexBuffer[] newBuffers = new CustomVertexBuffer[this.vertexBuffers.length + 1];
            System.arraycopy(this.vertexBuffers, 0, newBuffers, 0, vertexBuffers.length);
            this.vertexBuffers = newBuffers;
        }
        this.vertexBuffers[this.vertexBuffers.length - 1] = new CustomVertexBuffer(verts.asFloatBuffer(), norms.asFloatBuffer(), mtl, verts.remaining() / 4 / 3);  // 4 bytes per float, 3 floats per vert
    }

    public void hardCodeFloats(String inMaterial, float[] inVerts, float[] inNorms){
        if(this.vertexBuffers == null || this.vertexBuffers.length == 0){
            this.vertexBuffers = new CustomVertexBuffer[1];
        }else{
            CustomVertexBuffer[] newBuffers = new CustomVertexBuffer[this.vertexBuffers.length + 1];
            System.arraycopy(this.vertexBuffers, 0, newBuffers, 0, vertexBuffers.length);
            this.vertexBuffers = newBuffers;
        }
        this.numVerts = inVerts.length;
        this.numNorms = inNorms.length;

        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(inVerts.length * 4);   // float = 4 bytes
        byteBuffer.order(ByteOrder.nativeOrder());
        FloatBuffer floatBufferPos = byteBuffer.asFloatBuffer();
        floatBufferPos.put(inVerts);
        floatBufferPos.position(0);

        byteBuffer = ByteBuffer.allocateDirect(inNorms.length * 4);   // float = 4 bytes
        byteBuffer.order(ByteOrder.nativeOrder());
        FloatBuffer floatBufferNorm = byteBuffer.asFloatBuffer();
        floatBufferNorm.put(inNorms);
        floatBufferNorm.position(0);

        this.vertexBuffers[this.vertexBuffers.length - 1] = new CustomVertexBuffer(floatBufferPos, floatBufferNorm, inMaterial, inVerts.length / 3);
    }
}
