package project.etrumper.thomas.ghostbutton;

/**
 * Created by thoma on 5/30/2016.
 */
public class ObjectManager {

    static int numIDS = 6;

    static public EntityTile3D getObject(final int ID, int x, int y, int z) {
        EntityTile3D returnEntity = null;
        switch (ID) {
            // Empty
            case (0):
                break;
            // Player
            case (1):
                returnEntity = new Avatar3D_Computer();
                break;
            // Wall
            case (2):
                Vector3f color = MaterialManager.getColorPalette(0);
                Block block = new Block(ID);
                block.drawColor = color;
                returnEntity = block;
                break;
            // Disappearing wall
            case(3):
                color = MaterialManager.getColorPalette(1);
                block = new Block_Turn(ID);
                block.drawColor = color;
                returnEntity = block;
                break;
            // High jump block
            case(4):
                color = MaterialManager.getColorPalette(2);
                block = new Block_HighJump(ID);
                block.drawColor = color;
                returnEntity = block;
                break;
            // Long jump block
            case(5):
                color = MaterialManager.getColorPalette(3);
                block = new Block_LongJump(ID);
                block.drawColor = color;
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
