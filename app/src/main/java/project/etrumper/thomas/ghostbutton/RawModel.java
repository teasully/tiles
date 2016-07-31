package project.etrumper.thomas.ghostbutton;

import android.opengl.GLES20;
import android.opengl.Matrix;

/**
 * Created by thoma on 2/12/2016.
 */
public class RawModel extends Attribute {

    boolean visible;

    int meshID; // Used to access mesh from MeshManager
    float[] position,
            rotation,
            scale,
            pivotPoint,
            globalPosition;

    RawModel(String meshName) {
        super(0, meshName + ".mesh", AttributeType.RAWMODEL);
        this.init();
    }

    protected void init(){
        this.meshID = -1;
        this.visible = true;

        this.position = new float[3];
        this.rotation = new float[3];
        this.scale = new float[]{1.f, 1.f, 1.f};
        this.pivotPoint = new float[]{0.f, 0.f, 0.f};
        this.globalPosition = new float[3];
    }

    protected void draw(String libraryName, float[] globalPosition, float[] globalRotation, float[] globalScale) {
        // Make sure meshID is valid
        if (meshID == -1) {
            return;
        }
        float[] modelMatrix = getModelMatrixWithGlobal(globalPosition, globalRotation, globalScale),
                projectionMatrix = SuperManager.projectionMatrix;

        float[] modelToViewMatrix = new float[16],
                viewToProjectionMatrix = new float[16];

        Matrix.multiplyMM(modelToViewMatrix, 0, GameConstants.camera.viewMatrix, 0, modelMatrix, 0);  // Translate model to view matrix
        Matrix.multiplyMM(viewToProjectionMatrix, 0, projectionMatrix, 0, modelToViewMatrix, 0); // Translate view to projection matrix

        int vertexStride = 3 * 4, // 12 bytes per vertex
                mPositionHandle,
                mNormalHandle;
        CustomVertexBuffer[] buffers = MeshManager.getMesh(libraryName, this.meshID).vertexBuffers;
        if(buffers.length == 0){
            LOGE("Trying to draw with no buffers");
            return;
        }

        for (CustomVertexBuffer currentVertexBuffer : buffers) {

            Material mat = MaterialManager.getMaterial(currentVertexBuffer.material);
            int programID = ProgramManager.useProgram(mat.programName);

            mPositionHandle = GLES20.glGetAttribLocation(programID, "vPosition");// get handle to vertex shader's vPosition member
            GLES20.glEnableVertexAttribArray(mPositionHandle);// Enable a handle to the triangle vertices
            GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false,
                    vertexStride, currentVertexBuffer.floatBufferPositions);// Prepare the triangle coordinate data

            mNormalHandle = GLES20.glGetAttribLocation(programID, "vNormal");
            GLES20.glEnableVertexAttribArray(mNormalHandle);
            GLES20.glVertexAttribPointer(mNormalHandle, 3, GLES20.GL_FLOAT, false,
                    vertexStride, currentVertexBuffer.floatBufferNormals);

            GLES20.glUniformMatrix4fv(GLES20.glGetUniformLocation(programID, "uMVMatrix"),
                    1, false, modelToViewMatrix, 0);
            GLES20.glUniformMatrix4fv(GLES20.glGetUniformLocation(programID, "uMVPMatrix"),
                    1, false, viewToProjectionMatrix, 0);   // Pass the projection and view transformation to the shaders

            GLES20.glUniform3fv(GLES20.glGetUniformLocation(programID, "mAmbient"),
                    1, mat.lightProperties.ambient, 0);
            GLES20.glUniform3fv(GLES20.glGetUniformLocation(programID, "mDiffuse"),
                    1, mat.lightProperties.diffuse, 0);
            GLES20.glUniform1f(GLES20.glGetUniformLocation(programID, "opacity"), mat.opacity);

            /*float x = 0f, y = 0f;
            if(GameConstants.player != null){
                x = GameConstants.player.position[0];
                y = GameConstants.player.position[1];
            }

            GLES20.glUniform1f(GLES20.glGetUniformLocation(programID, "pX"), x);
            GLES20.glUniform1f(GLES20.glGetUniformLocation(programID, "pY"), y);*/
            GLES20.glUniform1f(GLES20.glGetUniformLocation(programID, "elapsedTime"), (SuperManager.globalTime / 1000.f));

            //Send light info to shader
            GLES20.glUniform3fv(GLES20.glGetUniformLocation(programID, "sceneAmbience"),
                    1, GameConstants.sceneAmbience, 0);

            GLES20.glUniform1i(GLES20.glGetUniformLocation(programID, "MAXLIGHTS"), LightManager.getLights().length);
            GLES20.glUniform3fv(GLES20.glGetUniformLocation(programID, "lightProperties"),
                    LightManager.getLights().length * 3, LightManager.getLightInformation(), 0);

            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, currentVertexBuffer.vertexCount); // Draw the triangles

            GLES20.glDisableVertexAttribArray(mNormalHandle);
            GLES20.glDisableVertexAttribArray(mPositionHandle); // Disable vertex arrays
        }

    }

    protected float[] getModelMatrixWithGlobal(float[] globalPosition, float[] globalRotation, float[] globalScale) {
        float[] modelMatrix = new float[16];
        Matrix.setIdentityM(modelMatrix, 0);
        this.globalPosition = VectorMath.addVectors(position, globalPosition);
        Matrix.translateM(modelMatrix, 0,
                globalPosition[0] + position[0] + pivotPoint[0],
                globalPosition[1] + position[1] + pivotPoint[1],
                globalPosition[2] + position[2] + pivotPoint[2]);  // Combine global tilePosition with local tilePosition
        for (int i = 0; i < 3; i++) {
            Matrix.rotateM(modelMatrix, 0, globalRotation[i] + this.rotation[i],
                    (i == 0 ? 1f : 0f),
                    (i == 1 ? 1f : 0f),
                    (i == 2 ? 1f : 0f));    // Rotate for each axis
        }
        Matrix.scaleM(modelMatrix, 0, scale[0] + globalScale[0], scale[1] + globalScale[1], scale[2] + globalScale[2]); // Scale
        return modelMatrix;
    }

}
