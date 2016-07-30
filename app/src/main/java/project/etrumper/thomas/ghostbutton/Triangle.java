package project.etrumper.thomas.ghostbutton;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by thoma on 2/10/2016.
 */
public class Triangle extends RawModel {

    static FloatBuffer vertexBuffer = null;

    float triangleCoords[];

    String materialName;

    float[] velocity;

    Triangle(float[] triangleCoords, String materialName, float[] velocity) {
        super("Triangle");
        this.triangleCoords = triangleCoords;
        this.materialName = materialName;
        this.velocity = velocity;

        float rot = SuperManager.r.nextFloat() % .360f;
        this.rotation = new float[]{180f, 0f, rot * 1000f};
        this.position = new float[]{0f, 0f, 6f};
        this.scale = new float[]{0.05f, 0.05f, 0.05f};
    }

    protected void asFloatBuffer() {
        if (vertexBuffer == null) {
            ByteBuffer bb = ByteBuffer.allocateDirect(triangleCoords.length * 4);
            bb.order(ByteOrder.nativeOrder());
            vertexBuffer = bb.asFloatBuffer();
            vertexBuffer.put(triangleCoords);
            vertexBuffer.position(0);
        }
    }

    protected void update(){
        if(this.velocity[0] != 0f || this.velocity[1] != 0f || this.velocity[2] != 0f){
            float dt = SuperManager.deltaTime / 1000f;
            this.position = new float[]{this.position[0] + (this.velocity[0] * dt), this.position[1] + (this.velocity[1] * dt), this.position[2] + (this.velocity[2] * dt)};
        }
        //this.position = SuperManager.player.position;
    }

    public void draw(){
        float[] fake = new float[]{0f, 0f, 0f};
        this.draw(fake, fake, fake);
    }

    protected void draw(float[] globalPosition, float[] globalRotation, float[] globalScale) {
        float[] modelMatrix = super.getModelMatrixWithGlobal(globalPosition, globalRotation, globalScale),
                viewMatrix = GameConstants.camera.viewMatrix,
                projectionMatrix = SuperManager.projectionMatrix;

        float[] modelToViewMatrix = new float[16],
                viewToProjectionMatrix = new float[16];

        Matrix.multiplyMM(modelToViewMatrix, 0, viewMatrix, 0, modelMatrix, 0);  // Translate model to view matrix
        Matrix.multiplyMM(viewToProjectionMatrix, 0, projectionMatrix, 0, modelToViewMatrix, 0); // Translate view to projection matrix

        int vertexStride = 3 * 4, // 12 bytes per vertex
                mPositionHandle;
        if (vertexBuffer == null) {
            this.asFloatBuffer();
        }

        Material mat = MaterialManager.getMaterial(this.materialName);
        int programID = ProgramManager.useProgram(mat.programName);

        mPositionHandle = GLES20.glGetAttribLocation(programID, "vPosition");// get handle to vertex shader's vPosition member
        GLES20.glEnableVertexAttribArray(mPositionHandle);// Enable a handle to the triangle vertices
        GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);// Prepare the triangle coordinate data

        GLES20.glUniformMatrix4fv(GLES20.glGetUniformLocation(programID, "uMVMatrix"),
                1, false, modelToViewMatrix, 0);
        GLES20.glUniformMatrix4fv(GLES20.glGetUniformLocation(programID, "uMVPMatrix"),
                1, false, viewToProjectionMatrix, 0);   // Pass the projection and view transformation to the shaders

        GLES20.glUniform3fv(GLES20.glGetUniformLocation(programID, "mAmbient"),
                1, mat.lightProperties.ambient, 0);
        GLES20.glUniform3fv(GLES20.glGetUniformLocation(programID, "mDiffuse"),
                1, mat.lightProperties.diffuse, 0);
        GLES20.glUniform1f(GLES20.glGetUniformLocation(programID, "opacity"), mat.opacity);

        float x = 0f, y = 0f;
        if (GameConstants.player != null) {
            x = GameConstants.player.position[0];
            y = GameConstants.player.position[1];
        }

        GLES20.glUniform1f(GLES20.glGetUniformLocation(programID, "pX"), x);
        GLES20.glUniform1f(GLES20.glGetUniformLocation(programID, "pY"), y);
        GLES20.glUniform1f(GLES20.glGetUniformLocation(programID, "elapsedTime"), (SuperManager.globalTime / 1000.f));

        //Send light info to shader
        GLES20.glUniform3fv(GLES20.glGetUniformLocation(programID, "sceneAmbience"),
                1, GameConstants.sceneAmbience, 0);

        GLES20.glUniform1i(GLES20.glGetUniformLocation(programID, "MAXLIGHTS"), LightManager.getLights().length);
        GLES20.glUniform3fv(GLES20.glGetUniformLocation(programID, "lightProperties"),
                LightManager.getLights().length * 3, LightManager.getLightInformation(), 0);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3); // Draw the triangles

        GLES20.glDisableVertexAttribArray(mPositionHandle); // Disable vertex arrays
    }

}
