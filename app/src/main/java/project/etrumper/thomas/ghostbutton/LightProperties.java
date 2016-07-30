package project.etrumper.thomas.ghostbutton;

/**
 * Created by thoma on 2/15/2016.
 */
public class LightProperties {

    float[] ambient, diffuse, specular;   // 3 lights for Phong shader

    LightProperties(float[] ambient, float[] diffuse, float[] specular){
        this.ambient = ambient;
        this.diffuse = diffuse;
        this.specular = specular;
    }

}
