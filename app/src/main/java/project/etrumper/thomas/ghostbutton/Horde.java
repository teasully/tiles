package project.etrumper.thomas.ghostbutton;

/**
 * Created by thoma on 6/21/2016.
 */
public class Horde extends Logable{

    enum HordeType{
        CUBE
    }

    HordeType hordeType;

    ChessPiece[] mobs;
    ChessPiece.PieceDirection moveDirection; // used to tell horde which dir to go

    Horde(HordeType hordeType, int[] tilePos){
        super("Horde");
        this.hordeType = hordeType;
        // Check valid horde spot
        if(this.canSpawn(tilePos)) {
            this.mobs = new ChessPiece[9];
            this.moveDirection = ChessPiece.PieceDirection.DOWN;
            this.spawn(tilePos, ChessPiece.WeaponColor.RED);
        }
    }

    private boolean canSpawn(int[] tilePos){
        // Check tile and surrounding based on size
        Tile[] tiles = GameConstants.tileMap.getSurroundingTilesSquare(tilePos);
        for(Tile tile : tiles){
            if(!tile.canMoveOntoPiece()){
                LOGE("Cant spawn because of " + tile.getPiece().TAG + " " + tile.getPiece().ID);
                return false;
            }
        }
        return true;
    }

    private void spawn(int[] tilePos, ChessPiece.WeaponColor color){
        // Get surrounding tiles and spawn cubes
        Tile[] tiles = GameConstants.tileMap.getSurroundingTilesSquare(tilePos);
        for(int i = 0; i < tiles.length; i++){
            CubeMonster mob = new CubeMonster(tiles[i].position[0], tiles[i].position[1], moveDirection);
            // make hordes red
            mob.weaponColor = color;
            // reset cube speed based on new color
            mob.changeDeltaTimeModifier(mob.getSpeed());
            this.mobs[i] = mob;
            tiles[i].setPiece(this.mobs[i]);
        }
    }
}
