package project.etrumper.thomas.ghostbutton;

/**
 * Created by thoma on 2/23/2016.
 */
public class Projectile extends BasicEntity {

    Projectile(String libraryName, float[] pos, float[] velocity) {
        super(libraryName);

        this.ease.easeType = EaseType.LINEAR;


        this.position = new float[]{pos[0], pos[1], pos[2]};
        this.velocity[0] = velocity[0];
        this.velocity[1] = velocity[1];
        this.velocity[2] = velocity[2];

        Ease.startEase(GameConstants.tileMap.getGlobalPosition(0, 0), this, 5000, 0);
    }

    /*@Override
    protected void update(){
        super.update();
        movePosition(VectorMath.multiplyVectorBy(velocity, SuperManager.deltaTime / 1000f));
    }*/

}
