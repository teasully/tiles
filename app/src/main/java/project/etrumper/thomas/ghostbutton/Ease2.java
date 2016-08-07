package project.etrumper.thomas.ghostbutton;

/**
 * Created by thoma on 6/23/2016.
 */
public class Ease2 extends Logable{

    long lengthInMilli, currentTimeInMilli; // Time variables
    float beginningValue, desiredChangeValue, deltaTimeModifier;    // Value variables and delta time modifier
    private boolean halved; // Used for checking half-way mark

    Ease2(float beginningValue, float desiredChange, long lengthOfEase){
        super("Ease2");
        // Initiate local variables
        this.currentTimeInMilli = 0;
        this.deltaTimeModifier = 1.f;

        this.lengthInMilli = lengthOfEase;
        this.beginningValue = beginningValue;
        this.desiredChangeValue = desiredChange;

        this.halved = false;
    }

    public void update(){
        this.currentTimeInMilli += (SuperManager.deltaTime * this.deltaTimeModifier);
        // Check if is over limit
        if(this.currentTimeInMilli > this.lengthInMilli){
            this.currentTimeInMilli = this.lengthInMilli;
        }
    }

    public boolean isHalfway(){
        // Check if has been checked already
        if(this.halved){
            return false;
        }
        // Check the halfway mark
        if(this.currentTimeInMilli >= this.lengthInMilli / 2){
            // Change boolean so won't be checked more than once
            this.halved = true;
            return true;
        }
        return false;
    }

    public boolean done(){
        return (this.currentTimeInMilli >= this.lengthInMilli);
    }

    public double easeLinear() {
        return this.desiredChangeValue * (this.currentTimeInMilli / 1000f) / (this.lengthInMilli / 1000f) + this.beginningValue;
    }

    public double easeQuadradic() {
        return this.easeQuadradic(this.currentTimeInMilli, this.beginningValue, this.desiredChangeValue, this.lengthInMilli);
    }

    private double easeQuadradic(double t, double b, double c, double d) {
        t /= d / 2f;
        if (t < 1f) {
            return c / 2f * t * t + b;
        }
        t--;
        return -c / 2f * (t * (t - 2f) - 1f) + b;
    }

    public static Ease2 getEase2(float originalValue, float newValue, long timeInMilli){
        // Check for error case
        if(originalValue == newValue){
            return null;
        }
        // Return new Ease2
        return new Ease2(originalValue, newValue - originalValue, timeInMilli);
    }

    /*
        += GUIDE TO VAGUE CONSTANTS IN TWEENING FORMULAS =+
        @t is the current time (or tilePosition) of the tween. This can be seconds or frames, steps, seconds, ms, whatever – as long as the unit is the same as is used for the total time [3].
        @b is the beginning value of the property.
        @c is the change between the beginning and destination value of the property.
        @d is the total time of the tween.
     */
}
