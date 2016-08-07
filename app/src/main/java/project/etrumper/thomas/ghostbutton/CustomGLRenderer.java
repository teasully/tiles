package project.etrumper.thomas.ghostbutton;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.AsyncTask;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by thoma on 2/10/2016.
 */
public class CustomGLRenderer implements GLSurfaceView.Renderer {

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        // Set clear color (BLACK)
        GLES20.glClearColor(0.1f, 0.1f, 0.1f, 1.f);
        // Enable depth detection
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        // Don't draw faces hidden behind others
        GLES20.glEnable(GLES20.GL_CULL_FACE);
        // Alpha blending (opacity)
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        // Load programs via ProgramManager
        ProgramManager.init();

        //SuperManager.self.execute();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        //GLES20.glViewport(0, 0, width, height);
        SuperManager.onSurfaceChanged(width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        // Update game elements via SuperManager
        SuperManager.update();
        // Draw tile map
        {
            // Clear buffers
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
            // Draw 3DTileMap
            if (GameConstants.tileMap3D != null) {
                GameConstants.tileMap3D.draw();
            }
            // Finish; draw the screen
            GLES20.glFlush();
        }
        // Draw overlay
        {
            // Save camera defaults
            float[] savePos = GameConstants.camera.position,
                    saveTarget = GameConstants.camera.target,
                    savedUp = GameConstants.camera.upVector;
            // Move camera to origin
            GameConstants.camera.position = new float[]{0f, 0f, 0f};
            GameConstants.camera.target = new float[]{0f, 0f, 1f};
            GameConstants.camera.upVector = new float[]{0f, 1f, 0f};
            // Update with new info
            GameConstants.camera.updateCamera();
            // Refresh depth buffer so drawn items do not go through scene
            GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT);
            // Draw the overlay
            Overlay.draw();
            // Finish; draw the screen
            GLES20.glFlush();
            // Revert camera
            GameConstants.camera.position = savePos;
            GameConstants.camera.target = saveTarget;
            GameConstants.camera.upVector = savedUp;
        }
    }

    public static int loadShader(int type, String shaderCode){

        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        IntBuffer intBuf=ByteBuffer.allocateDirect(4).order(ByteOrder.nativeOrder()).asIntBuffer();
        int status;
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, intBuf);
        status = intBuf.get(0);
        if(status == 0)
        {
            String g = GLES20.glGetShaderInfoLog(shader);

            Log.e(type == GLES20.GL_VERTEX_SHADER ? "VertexShader" : "FragmentShader", g);

            GLES20.glDeleteShader(shader); // Don't leak the shader.
        }

        return shader;
    }

    public void cleanUp(){

    }
}
