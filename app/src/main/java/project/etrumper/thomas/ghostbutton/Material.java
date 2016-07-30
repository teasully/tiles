package project.etrumper.thomas.ghostbutton;

/**
 * Created by thoma on 2/12/2016.
 */
public class Material {

    String name, programName;
    LightProperties lightProperties;
    float shininess, opacity;

    Material(String name, LightProperties lightProperties, float shininess, float opacity) {
        this.init(name, SuperManager.defaultProgramName, lightProperties, shininess, opacity);
    }

    Material(String name, String programName, LightProperties lightProperties, float shininess, float opacity) {
        this.init(name, programName, lightProperties, shininess, opacity);
    }

    private void init(String name, String programName, LightProperties lightProperties, float shininess, float opacity){
        this.name = name;
        this.programName = programName;
        this.lightProperties = lightProperties;
        this.shininess = shininess;
        this.opacity = opacity;
    }

}
