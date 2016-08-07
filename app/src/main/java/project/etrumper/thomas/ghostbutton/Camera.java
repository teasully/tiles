package project.etrumper.thomas.ghostbutton;

import android.opengl.Matrix;

/**
 * Created by thoma on 2/15/2016.
 */
public class Camera extends BasicEntity {

    float[] viewMatrix,
            offset; // Used for shake
    float intensity; // Higher numbers make less shake

    long shakeTimer;

    Ease2 targetX, targetY, targetZ,
            upVectorX, upVectorY, upVectorZ,
            posX, posY, posZ;

    Camera() {
        super("Camera");

        this.targetX = null;
        this.targetY = null;
        this.targetZ = null;
        this.upVectorX = null;
        this.upVectorY = null;
        this.upVectorZ = null;
        this.posX = null;
        this.posY = null;
        this.posZ = null;

        viewMatrix = new float[16];
        this.offset = new float[3];

        this.shakeTimer = 0;
        this.intensity = 50.f;

        rotation = new float[]{0f, 0f, 0f};
        position = new float[]{0f, 0f, 0f};
    }

    float[] target = new float[]{0, 0, 0},
            upVector = new float[]{0f, 0f, 1f};

    @Override
    protected void update() {
        super.update();
        // Check if camera should be shaken
        if (this.shakeTimer > 0) {
            this.shakeTimer -= SuperManager.deltaTime;
            int r1 = SuperManager.r.nextInt() % 40,
                    r2 = SuperManager.r.nextInt() % 20;
            this.offset[0] = (float) r1 / this.intensity;
            this.offset[1] = (float) r2 / this.intensity;
        } else {
            this.shakeTimer = 0;
            this.offset[0] = 0;
            this.offset[1] = 0;
        }
        // Check target easing for each axis
        if (this.targetX != null) {
            this.targetX.update();
            this.target[0] = (float)this.targetX.easeLinear();
            if (this.targetX.done()) {
                this.targetX = null;
            }
        }
        if (this.targetY != null) {
            this.targetY.update();
            this.target[1] = (float)this.targetY.easeLinear();
            if (this.targetY.done()) {
                this.targetY = null;
            }
        }
        if (this.targetZ != null) {
            this.targetZ.update();
            this.target[2] = (float)this.targetZ.easeLinear();
            if (this.targetZ.done()) {
                this.targetZ = null;
            }
        }
        // Check up vector easing
        if (this.upVectorX != null) {
            this.upVectorX.update();
            this.upVector[0] = (float)this.upVectorX.easeLinear();
            if (this.upVectorX.done()) {
                this.upVectorX = null;
            }
        }
        if (this.upVectorY != null) {
            this.upVectorY.update();
            this.upVector[1] = (float)this.upVectorY.easeLinear();
            if (this.upVectorY.done()) {
                this.upVectorY = null;
            }
        }
        if (this.upVectorZ != null) {
            this.upVectorZ.update();
            this.upVector[2] = (float)this.upVectorZ.easeLinear();
            if (this.upVectorZ.done()) {
                this.upVectorZ = null;
            }
        }
        // Update position
        if (this.posX != null) {
            this.posX.update();
            this.position[0] = (float)this.posX.easeLinear();
            if (this.posX.done()) {
                this.posX = null;
            }
        }
        if (this.posY != null) {
            this.posY.update();
            this.position[1] = (float)this.posY.easeLinear();
            if (this.posY.done()) {
                this.posY = null;
            }
        }
        if (this.posZ != null) {
            this.posZ.update();
            this.position[2] = (float)this.posZ.easeLinear();
            if (this.posZ.done()) {
                this.posZ = null;
            }
        }
    }

    protected void updateCamera() {
        Matrix.setLookAtM(viewMatrix, // Output
                0, // Output offset
                position[0], position[1], position[2], // Camera tilePosition in world
                target[0], target[1], target[2], // Target for camera to point towards
                upVector[0], upVector[1], upVector[2]); // Sets the up vector
    }

    protected void frustM(float[] projectionMatrix, float width, float height) {
        float ratio = width / height;
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1, 1, 1f, 20f);
    }

    public void easeTargetTo(float[] pos, long timeInMilli) {
        if(this.targetX != null){
            return;
        }
        this.targetX = Ease2.getEase2(this.target[0], pos[0], timeInMilli);
        this.targetY = Ease2.getEase2(this.target[1], pos[1], timeInMilli);
        this.targetZ = Ease2.getEase2(this.target[2], pos[2], timeInMilli);
    }

    public void easeUpVectorTo(float[] pos, long timeInMilli) {
        if(this.upVectorX != null){
            return;
        }
        this.upVectorX = Ease2.getEase2(this.upVector[0], pos[0], timeInMilli);
        this.upVectorY = Ease2.getEase2(this.upVector[1], pos[1], timeInMilli);
        this.upVectorZ = Ease2.getEase2(this.upVector[2], pos[2], timeInMilli);
    }

    public void easePositionTo(float[] pos, long timeInMilli) {
        if(this.posX != null){
            return;
        }
        this.posX = Ease2.getEase2(this.position[0], pos[0], timeInMilli);
        this.posY = Ease2.getEase2(this.position[1], pos[1], timeInMilli);
        this.posZ = Ease2.getEase2(this.position[2], pos[2], timeInMilli);
    }

    protected void startShake(long time) {
        this.shakeTimer = time;
    }

}
