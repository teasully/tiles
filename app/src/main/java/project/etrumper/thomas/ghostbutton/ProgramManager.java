package project.etrumper.thomas.ghostbutton;

import android.util.Log;

/**
 * Created by thoma on 6/6/2016.
 */
public class ProgramManager {

    static boolean debug = true;

    static String defaultVertexShader = "vertex_shader";

    static Program programs[];

    public static void init(){
        programs = new Program[0];

        addProgram(SuperManager.defaultProgramName, "lighting_shader");
        addProgram("test", "test_frag_shader");
        addProgram("particles", "particle_vert", "particle_frag");
    }

    public static int useProgram(final int index){
        if(index >= programs.length){
            LOGE("Failed to use program index " + index + " OUT OF RANGE");
            return -1;
        }
        programs[index].use();
        return programs[index].programID;
    }

    public static int useProgram(final String programName){
        for(Program p : programs){
            if(p.programName.equals(programName)){
                p.use();
                return p.programID;
            }
        }
        LOGE("Failed to find program name " + programName);
        return -1;
    }

    private static void addProgram(final String programName, final String vertShaderName, final String fragShaderName){
        Program[] newPrograms = new Program[programs.length + 1];
        System.arraycopy(programs, 0, newPrograms, 0, programs.length);
        newPrograms[newPrograms.length-1] = new Program(programName, vertShaderName, fragShaderName);
        programs = newPrograms;
    }

    private static void addProgram(String programName, String fragShaderName){
        addProgram(programName, defaultVertexShader, fragShaderName);
    }

    private static void LOGE(String message){
        if(debug)
            Log.e("ProgramManager", message);
    }

}
