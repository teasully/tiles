package project.etrumper.thomas.ghostbutton;

/**
 * Created by thoma on 2/23/2016.
 */
public class RayProjectile extends Projectile {

    int meshID;

    RayProjectile(float[] pos, float[] vel){
        super("RayProjectile",pos, vel);
        //meshID = addAttribute(new RawModel("RayProjectile")); // Empty rawModel;
        //RawModel mesh = getRawModel(meshID);
       // mesh.addAttribute(new BoundingBox(mesh.getModelMatrixWithGlobal(this), new float[]{0.1f, 0.1f, 0.1f}, "RayProjectile")); // Gives the fake mesh a .1x.1x.1 BoundingBox
    }

   /* protected boolean isTapped(){   // Returns if the mesh was justReleased
        if(getRawModel(meshID).justReleased){
            getRawModel(meshID).justReleased = false;
            return true;
        }
        return false;
    }*/

    @Override
    protected void update(){
        movePosition(velocity); // Doesn't need to multiply by deltaTime like super does
    }

}
