package project.etrumper.thomas.ghostbutton;

import android.telephony.SubscriptionInfo;

/**
 * Created by thoma on 7/22/2016.
 */
public class GravityAxis extends Logable {

    private long elapsedTime,
            totalLength;

    float startPos,
            velocity;

    float gravity = -10f;
    static float dtModifier = 2f;

    GravityAxis(float startPos, float velocity, long length) {
        super("GravityAxis");
        this.elapsedTime = 0;
        this.totalLength = length;
        this.startPos = startPos;
        this.velocity = velocity;
    }

    public void update() {
        this.elapsedTime += SuperManager.deltaTime * dtModifier;
        if (this.elapsedTime > this.totalLength) {
            this.elapsedTime = totalLength;
        }
    }

    public float getPosition() {
        return this.startPos + calculateGravity();
    }

    private float calculateGravity(){
        return (((gravity * (elapsedTime / 1000f * elapsedTime / 1000f)) / 2f) + (velocity * elapsedTime / 1000f));
    }

    public boolean done() {
        return (this.elapsedTime >= this.totalLength);
    }

}