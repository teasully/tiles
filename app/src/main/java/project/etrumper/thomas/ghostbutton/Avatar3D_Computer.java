package project.etrumper.thomas.ghostbutton;

/**
 * Created by thoma on 8/10/2016.
 * Property of boxedworks.
 */
public class Avatar3D_Computer extends Avatar3D{

    Animation roll, end;

    Avatar3D_Computer(){
        super();
    }

    @Override
    protected void update(){
        super.update();
        if(this.disabled){
            return;
        }
        if(this.canMove()){
            if(this.currentAnimation == this.roll){
                this.enteredNextTile();
            }else {
                this.setCurrentAnimation(this.roll);
            }
            this.rollForward();
        }
    }

    private boolean canMove(){
        if(this.jump != null){
            return false;
        }
        if(this.currentAnimation.isPlaying){
            return false;
        }
        return true;
    }

    @Override
    protected void landed(){
        this.currentAnimation.isPlaying = false;
        super.landed();
        this.setCurrentAnimation(this.end);
        this.play();
    }

    @Override
    protected void addAnimations(){
        this.end = this.addAnimation("move_end");
        this.roll = this.addAnimation("move");
        this.setCurrentAnimation(this.roll);
    }

}
