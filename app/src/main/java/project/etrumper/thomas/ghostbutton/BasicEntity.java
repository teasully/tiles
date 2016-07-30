package project.etrumper.thomas.ghostbutton;

import android.opengl.Matrix;
import android.util.Log;

/**
 * Created by thoma on 2/13/2016.
 */
public class BasicEntity extends Logable {

    static int IDCounter = 0;

    int ID; // Unique ID

    float[] position, velocity, rotation, scale;

    float positionalVelocity, anglarVelocity, scalingVelocity,  // For changing values
            deltaTimeModifier;  // For changing speed of change

    boolean visible;    // For drawing

    Animation currentAnimation;

    RawModel model;

    Ease ease;  // For moving from one place to another

    Animation[] animations;

    BasicEntity(String libraryObjectName) {
        super(libraryObjectName);

        this.ID = IDCounter++;

        this.position = new float[3];
        this.velocity = new float[9];   // 3 coords + 3 angulars + 3 scalar
        this.rotation = new float[3];
        this.scale = new float[3];

        this.deltaTimeModifier = 1.f;   // Time modifier for slowing + speed

        this.visible = true;

        this.currentAnimation = null;

        this.model = new RawModel(TAG);

        this.animations = new Animation[0];
    }

    protected void update() {
        // Update animations
        for (Animation animation : this.animations) {
            animation.update();
        }
        // Update ease
        if (this.ease != null) {
            this.ease.update(new float[]{SuperManager.deltaTime * deltaTimeModifier});
            float[] positionalVelocity = ease.getData(new float[]{this.position[0], this.position[1], this.position[2], 0}),
                    angularVelocity = ease.getData(new float[]{this.rotation[0], this.rotation[1], this.rotation[2], 1}),
                    scalingVelocity = ease.getData(new float[]{this.scale[0], this.scale[1], this.scale[2], 2});
            this.velocity = new float[]{positionalVelocity[0], positionalVelocity[1], positionalVelocity[2],
                    angularVelocity[0], angularVelocity[1], angularVelocity[2],
                    scalingVelocity[0], scalingVelocity[1], scalingVelocity[2]};
        }
        // Update position | rotation | scale with velocity data
        if (this.velocity[0] != 0 || this.velocity[1] != 0 || this.velocity[2] != 0) {
            movePosition(new float[]{this.velocity[0], this.velocity[1], this.velocity[2]});
        }
        if (this.velocity[3] != 0 || this.velocity[4] != 0 || this.velocity[5] != 0) {
            moveRotation(new float[]{this.velocity[3], this.velocity[4], this.velocity[5]});
        }
        if (this.velocity[6] != 0 || this.velocity[7] != 0 || this.velocity[8] != 0) {
            moveScale(new float[]{this.velocity[6], this.velocity[7], this.velocity[8]});
        }
        // Fix rotation so it does not go above 360 or under 0 just for numbers' sake
        for (int i = 0; i < 3; i++) {
            if (rotation[i] >= 360f || rotation[i] < 0f) {
                rotation[i] %= 360;
            }
        }
    }

    protected void changeDeltaTimeModifier(float newModifier) {
        for (Animation animation : this.animations) {
            animation.deltaTimeModifier = newModifier;
        }
    }

    protected void interpolate(boolean value) {
        for (Animation animation : this.animations) {
            animation.interpolate = value;
        }
    }

    private void addAnimation(Animation animation) {
        Animation[] newAnims = new Animation[this.animations.length + 1];
        System.arraycopy(this.animations, 0, newAnims, 0, this.animations.length);
        newAnims[this.animations.length] = animation;
        this.animations = newAnims;
    }


    protected Animation addAnimation(String objectName, String animationName) {
        Animation animation = new Animation(objectName, animationName);
        this.addAnimation(animation);
        return animation;
    }

    protected Animation addAnimation(String animationName) {
        return this.addAnimation(this.TAG, animationName);
    }

    protected Animation addAnimation(String animationName, long timeInMilli, Animation first, Animation second, boolean interpolate) {
        Animation animation = Animation.getTweenedAnimation(this.TAG, animationName, timeInMilli, first, second, interpolate);
        this.addAnimation(animation);
        return animation;
    }

    protected void updateMesh() {
        int gotID = this.currentAnimation.getcurrentFrame();
        this.model.meshID = gotID;
        if (gotID == -1) {
            //LOGE("updateMesh got -1");
        }
    }

    protected void play() {
        this.currentAnimation.play();
    }

    protected void setCurrentAnimation(Animation setAnim) {
        this.currentAnimation = setAnim;
    }

    protected void randomizeRotation(int rotIndex, int numberDivisions) {
        int number = SuperManager.r.nextInt() % numberDivisions;
        float addRot = ((float) number / (float) numberDivisions) * 360.f;
        this.rotation[rotIndex] = addRot;
    }

    /*protected void queueModel(String name){
        String[] newQueue = new String[this.queuedModels.length + 1];
        int i = 0;
        for(String model : this.queuedModels){
            newQueue[i++] = model;
        }
        newQueue[i] = name;
        this.queuedModels = newQueue;
    }*/

    /*protected void queueModel(String libraryName, String modelName){
        this.queueModel(String.format("%s.%s", libraryName, modelName));
    }*/

    /*protected void dequeueModel(String name){
        String[] newQueue = new String[this.queuedModels.length - 1];
        int u = 0;
        for(int i = 0; i < this.queuedModels.length; i++){
            String model = this.queuedModels[i];
            if(model.equals((name))){
               continue;
            }
            newQueue[u++] = model;
        }
        this.queuedModels = newQueue;
    }*/

    protected void addDrawElement() {
        SuperManager.addDrawElement(this);
    }

    protected void removeDrawElement() {
        try {
            SuperManager.removeDrawElement(this);
        } catch (MandatoryException e) {
            LOGE(e.toString());
        }
    }

    /*protected RawModel getRawModel(int index){
        if(index > this.attributes.length - 1){
            return null;
        }
        if(this.attributes[index].attributeType != AttributeType.RAWMODEL){
            return null;
        }
        return (RawModel)this.attributes[index];
    }*/

    protected void setPosition(float[] position) {
        if (position.length != 3) {
            LOGE("Trying to setPosition with position[] of invalid length");
        } else {
            for (int i = 0; i < 3; i++) {
                if (Float.isNaN(position[i])) {
                    LOGE(String.format("Trying to movePosition position index %d to NaN", i));
                } else {
                    this.position[i] = position[i];
                }
            }
        }
    }

    protected void movePosition(float[] offset) {
        if (offset.length != 3) {
            LOGE("Trying to movePosition() with invalid offset[] length");
        } else {
            for (int i = 0; i < 3; i++) {
                if (Float.isNaN(offset[i])) {
                    LOGE(String.format("Trying to movePosition position index %d by NaN", i));
                } else {
                    this.position[i] += offset[i];
                }
            }
        }
        //LOGE(String.format("Position moved by %f, %f, %f", offset[0], offset[1], offset[2]));
    }

    protected void setRotation(float[] rotation) {
        if (rotation.length != 3) {
            LOGE("Trying to setRotation() with invalid rotation[] length");
        } else {
            for (int i = 0; i < 3; i++) {
                if (Float.isNaN(rotation[i])) {
                    LOGE(String.format("Trying to movePosition rotation index %d to NaN", i));
                } else {
                    this.rotation[i] = rotation[i];
                }
            }
        }
    }

    protected void moveRotation(float[] offset) {
        if (offset.length != 3) {
            LOGE("Trying to moveRotation() with invalid offset[] length");
        } else {
            for (int i = 0; i < 3; i++) {
                if (Float.isNaN(offset[i])) {
                    LOGE(String.format("Trying to moveRotation index %d by NaN", i));
                } else {
                    this.rotation[i] += offset[i];
                }
            }
        }
    }

    protected void setScale(float[] scale) {
        if (rotation.length != 3) {
            LOGE("Trying to setScale() with invalid rotation[] length");
        } else {
            for (int i = 0; i < 3; i++) {
                if (Float.isNaN(scale[i])) {
                    LOGE(String.format("Trying to moveScale index %d to NaN", i));
                } else {
                    this.scale[i] = scale[i];
                }
            }
        }
    }

    protected void moveScale(float[] offset) {
        if (offset.length != 3) {
            LOGE("Trying to moveScale() with invalid offset[] length");
        } else {
            for (int i = 0; i < 3; i++) {
                if (Float.isNaN(offset[i])) {
                    LOGE(String.format("Trying to movePosition roation index %d by NaN", i));
                } else {
                    this.scale[i] += offset[i];
                }
            }
        }
    }

    protected void draw() {
        /*if(this.visible) {
            for(Attribute attribute : this.attributes) {
                if (attribute != null) {
                    if (attribute.attributeType == AttributeType.RAWMODEL) {
                        RawModel model = (RawModel) attribute;
                        if (model.visible) {
                            model.draw(this.position, this.rotation, this.scale);
                        }
                    }
                }
            }
        }*/
        if (this.visible && this.currentAnimation != null) {
            this.currentAnimation.draw(this, this.model);
            //return;
        }
        //LOGE("Null current anim");
    }

    protected float[] getPosition() {
        return this.position;
    }

    protected float[] getRotation() {
        return this.rotation;
    }

    protected float[] getScale() {
        return this.scale;
    }

    /*protected float[] getModelMatrix(){
        float[] modelMatrix = new float[16];

        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.translateM(modelMatrix, 0, position[0], position[1],position[2]);  // Combine global position with local position
        for(int i = 0; i < 3; i++){
            Matrix.rotateM(modelMatrix, 0, rotation[i],
                    (i == 0 ? 1f : 0f),
                    (i == 1 ? 1f : 0f),
                    (i == 2 ? 1f : 0f));    // Rotate for each axis
        }
        Matrix.scaleM(modelMatrix, 0, scale[0], scale[1], scale[2]); // Scale
        return modelMatrix;
    }*/

    /*protected float[] getAttributeModelMatrix(int attributeIdex){
        RawModel model = getRawModel(attributeIdex);
        if(model == null){
            return new float[]{};
        }
        return model.getModelMatrixWithGlobal(this.position, this.rotation, this.scale);
    }*/

    protected void addEase(EaseType type) {
        this.ease = new Ease();
        this.ease.easeType = type;
    }

    protected void startEase(float[] destination, long timeInMilli, int mode) {
        Ease.startEase(destination, this, timeInMilli, mode);
    }

    protected void startEase(float[] destination, long timeInMilli) {
        this.startEase(destination, timeInMilli, 0);
    }

    protected void pointCameraTo(){
        GameConstants.camera.target = new float[]{this.position[0], this.position[1], this.position[2]};
    }

    protected void LOGE(String message) {
        Log.e(this.TAG, message);
    }
}
