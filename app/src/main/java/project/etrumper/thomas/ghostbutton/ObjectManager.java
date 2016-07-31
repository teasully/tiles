package project.etrumper.thomas.ghostbutton;

import android.util.Log;

/**
 * Created by thoma on 5/30/2016.
 */
public class ObjectManager {

    static public EntityTile3D getObject(final int ID, int x, int y, int z) {
        EntityTile3D returnEntity = null;
        switch (ID) {
            case (0):
                break;
            case (1):
                returnEntity = new Avatar3D(new Vector3i(x, y, z));
                GameConstants.camera.startEase(new float[]{-x, -y, 0}, 4000);
                break;
            case (2):
                returnEntity = new EntityTile3D("Scenery1", 0.5f, 0.5f);
                returnEntity.setCurrentAnimation(returnEntity.addAnimation("wallstandard1"));
                break;
        }
        if(returnEntity != null){
            returnEntity.tilePosition = new Vector3i(x, y, z);
            returnEntity.moveToPosition(returnEntity.tilePosition);
        }
        return returnEntity;
    }

}
