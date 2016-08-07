package project.etrumper.thomas.ghostbutton;

import android.util.Log;

/**
 * Created by thoma on 2/15/2016.
 */
public class LightManager{

    private static int currentLights;

    private static Light[] lights;

    public static void init(int MAXLIGHTS){
        currentLights = 0;

        lights = new Light[MAXLIGHTS];
    }

    public static void addLight(Light light){
        if(currentLights  < lights.length) {
            lights[currentLights++] = light;
        }else{
            Log.i("LightManager", "Cannot addLight, max lights");
        }
    }

    public static Light[] getLights(){
        return lights;
    }

    public static float[] getLightInformation(){
        float[] result = new float[lights.length * 3 * 3];  // 1 light = 3 vectors of 3 floats
        int count = 0;
        for(int i = 0; i < lights.length; i++){
            Light currentLight = lights[i];
            if(currentLight != null){
                count++;
                int offset = i * 9;
                result[offset + 0] = currentLight.getPosition()[0];
                result[offset + 1] = currentLight.getPosition()[1];
                result[offset + 2] = currentLight.getPosition()[2];

                result[offset + 3] = currentLight.lightProperties.diffuse[0];
                result[offset + 4] = currentLight.lightProperties.diffuse[1];
                result[offset + 5] = currentLight.lightProperties.diffuse[2];

                result[offset + 6] = currentLight.lightProperties.specular[0];
                result[offset + 7] = currentLight.lightProperties.specular[1];
                result[offset + 8] = currentLight.lightProperties.specular[2];
            }
        }
        return result;
    }

    public static void updateLights(){
        for(Light light : lights){
            if(light != null){
                light.update();
            }
        }
    }

}
