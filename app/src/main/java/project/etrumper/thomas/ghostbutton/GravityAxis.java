package project.etrumper.thomas.ghostbutton;

import android.telephony.SubscriptionInfo;

/**
 * Created by thoma on 7/22/2016.
 */
public class GravityAxis extends Logable {

    private long elapsedTime,
            totalLength;

    double startPos,
            velocity;

    double gravity = -10d;
    static double dtModifier = 2d;

    GravityAxis(double startPos, double velocity, long length) {
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

    public double getPosition() {
        return this.startPos + calculateGravity();
    }

    private double calculateGravity(){
        return (((gravity * (elapsedTime / 1000d * elapsedTime / 1000d)) / 2d) + (velocity * elapsedTime / 1000d));
    }

    public boolean done() {
        return (this.elapsedTime >= this.totalLength);
    }

}