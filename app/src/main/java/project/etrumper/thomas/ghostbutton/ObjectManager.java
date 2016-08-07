package project.etrumper.thomas.ghostbutton;

/**
 * Created by thoma on 5/30/2016.
 */
public class ObjectManager {

    static public EntityTile3D getObject(final int ID, int x, int y, int z) {
        EntityTile3D returnEntity = null;
        switch (ID) {
            // Empty
            case (0):
                break;
            // Player
            case (1):
                returnEntity = new Avatar3D(new Vector3i(x, y, z));
                GameConstants.camera.easePositionTo(new float[]{-x, -y, 0}, 500);
                break;
            // Wall
            case (2):
                Vector3f color = MaterialManager.getColorPalette(0);
                Block block = new Block("Scenery1");
                block.setCurrentAnimation(block.addAnimation("wallstandard1"));
                block.drawColor = color;
                returnEntity = block;
                break;
            // Disappearing wall
            case(3):
                color = MaterialManager.getColorPalette(0);
                block = new Block("Scenery1");
                block.setCurrentAnimation(block.addAnimation("wallstandard1"));
                block.drawColor = color;
                block.disappearTimer = 500;
                returnEntity = block;
                break;
        }
        if(returnEntity != null){
            returnEntity.tilePosition = new Vector3i(x, y, z);
            returnEntity.spawnLocation = new Vector3i(x, y, z);
            returnEntity.moveToPosition(returnEntity.tilePosition);
        }
        return returnEntity;
    }

}
