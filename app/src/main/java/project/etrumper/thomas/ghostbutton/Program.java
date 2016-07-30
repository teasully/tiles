package project.etrumper.thomas.ghostbutton;

import android.content.Context;
import android.opengl.GLES20;

/**
 * Created by thoma on 2/12/2016.
 */
public class Program {

    public final int programID;
    public final String programName;

    //https://www.youtube.com/watch?v=MRD_zN0SWh0&index=4&list=PLC2D979DC6CF73B47


    Program(String programName, String vertName, String fragName){
        this.programName = programName;
        int vertexShader = CustomGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER,
                Loader.loadFile(vertName));
        int fragmentShader = CustomGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER,
                Loader.loadFile(fragName));

        // create empty OpenGL ES Program
        this.programID = GLES20.glCreateProgram();

        // add the vertex shader to program
        GLES20.glAttachShader(programID, vertexShader);

        // add the fragment shader to program
        GLES20.glAttachShader(programID, fragmentShader);

        // creates OpenGL ES program executables
        GLES20.glLinkProgram(programID);
    }

    protected void use(){
        GLES20.glUseProgram(this.programID);
    }

}
