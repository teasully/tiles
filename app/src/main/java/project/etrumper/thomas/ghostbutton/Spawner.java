package project.etrumper.thomas.ghostbutton;

/**
 * Created by thoma on 6/27/2016.
 */
public class Spawner extends ChessPiece{

    boolean active;

    Spawner(int x, int y){
        super("Scenery1", PieceDirection.DOWN, PieceType.ENTITY, x, y);
        super.currentAnimation = addAnimation("cube");

        super.TAG = "Spawner";

        this.active = true;
    }

    @Override
    protected void update(){
        super.update();
        super.updateMesh();
        if(this.active) {
            // get random number to check to spawn
            int i = SuperManager.r.nextInt() % 25;
            if (i == 0) {
                this.spawn();
            }
        }
    }

    public void spawn(){
        // Check if there is a wave available
        if(GameConstants.tileMap.currentWave == null){
            return;
        }
        // Check if can spawn (has enough room)
        Tile tile = GameConstants.tileMap.getSouthTile(this.tilePos[0], this.tilePos[1]);
        if(!tile.canMoveOntoPiece()){
            return;
        }
        // Get mob from tilemap wave in GameConstants
        ChessPiece mob = GameConstants.tileMap.currentWave.getMob();
        // Check to make sure mob is not null, if is, wave is empty
        if(mob == null){
            this.active = false;
            return;
        }
        // Place under spawner
        mob.moveByBoardTo(tile.position);
    }

}
