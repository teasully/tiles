package project.etrumper.thomas.ghostbutton;

/**
 * Created by thoma on 2/15/2016.
 */

public class Light extends BasicEntity {

    LightProperties lightProperties;

    float[] savedDiffuse;

    Light(String _TAG, LightProperties lightProperties) {
        super(_TAG);
        this.lightProperties = lightProperties;
        //this.attributeType = AttributeType.LIGHT;

        //addAttribute(new Ease());

        positionalVelocity = 0.5f;
    }

    Light(String _TAG, float[] diffuse) {
        super(_TAG);
        this.lightProperties = new LightProperties(new float[]{},
                diffuse, new float[]{1f, 1f, 1f});
        //this.attributeType = AttributeType.LIGHT;

        //addAttribute(new Ease());

        positionalVelocity = 0.5f;
    }

    protected float[] getPosition() {
        return new float[]{
                position[0],// - GameConstants.camera.position[0],
                position[1],// - GameConstants.camera.position[1],
                position[2]// - GameConstants.camera.position[2]
        };
    }

    protected void on() {
        if(savedDiffuse != null){
            lightProperties.diffuse = savedDiffuse;
        }
    }

    protected void off() {
        savedDiffuse = lightProperties.diffuse;
        lightProperties.diffuse = new float[]{0f, 0f, 0f};
    }
}
