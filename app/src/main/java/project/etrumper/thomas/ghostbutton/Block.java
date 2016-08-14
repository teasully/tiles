package project.etrumper.thomas.ghostbutton;

/**
 * Created by thoma on 8/2/2016.
 * Property of boxedworks.
 */
public class Block extends EntityTile3D {

    Vector3f drawColor;

    boolean canCollide;

    Block(int ID){
        // Set base model and tag
        super("Scenery1", EntityType.BLOCK, ID, 0.5f, 0.5f);
        this.setCurrentAnimation(this.addAnimation("wallstandard1"));
        this.TAG = "Block";
        // Set locals
        this.canCollide = true;
    }

    @Override
    protected void draw(){
        // If cannot collide, be invisible
        if(!this.canCollide){
            return;
        }
        // Change color to current
        MaterialManager.changeMaterialColor("Wall", this.drawColor);
        // Draw
        super.draw();
    }

}
