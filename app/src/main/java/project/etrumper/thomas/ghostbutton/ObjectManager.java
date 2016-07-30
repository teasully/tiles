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


    // Returns objects via ID numbers and themes
    static public ChessPiece getObject(final int ID, int x, int y){
        ChessPiece returnPiece = null;
        switch(ID){
            case(0):
                returnPiece = null;
                break;
            case(1):
                switch(Overlay.getRemainder(Overlay.optionsMenu.getData()[0], 2)) {
                    case (0):
                        GameConstants.player = new Archer(x, y);
                        break;
                    case (1):
                        GameConstants.player = new CubeMonster(x, y, null) {
                            @Override
                            protected void brainstuff() {

                            }
                        };
                        break;
                }
                GameConstants.player.playerControlled = true;
                returnPiece = GameConstants.player;
                break;
            case(2):
                returnPiece = new Wall(x, y, "Scenery1", "wallstandard1");
                break;
            case(3):
                returnPiece = new CubeMonster(x, y, null);
                break;
            case(4):
                Horde horde = new Horde(Horde.HordeType.CUBE, new int[]{x, y});
                break;
            case(5):
                returnPiece = new Barricade(x, y);
                break;
            case(6):
                returnPiece = new Spawner(x, y);
                break;
            default:
                Log.e("ObjectManager", "Unhandled Object ID: " + ID);
                break;
        }

        return returnPiece;
    }

}
