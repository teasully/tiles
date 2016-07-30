package project.etrumper.thomas.ghostbutton;

/**
 * Created by thoma on 5/28/2016.
 */
public class AnimationEvent {

    int frameNumber;    // frame number that event fires on

    boolean fired, handled;

    String name;

    AnimationEvent(String name, int frameNumber){
        this.name = name;
        this.frameNumber = frameNumber;
        this.fired = false;
        this.handled = false;
    }

    protected void fire(){
            this.fired = true;
    }

    protected void reset(){
        this.fired = false;
        this.handled = false;
    }

}
