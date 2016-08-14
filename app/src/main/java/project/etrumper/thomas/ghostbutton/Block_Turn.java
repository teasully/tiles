package project.etrumper.thomas.ghostbutton;

import static project.etrumper.thomas.ghostbutton.EntityTile3D.Direction.EAST;

/**
 * Created by thoma on 8/11/2016.
 * Property of boxedworks.
 */
public class Block_Turn extends Block{

    Block_Turn(int ID){
        super(ID);
        // Set default direction
        this.entityDirection = EAST;
        // Set turnable
        this.directional = true;
    }

    @Override
    protected void inputHandler(String input){
        switch (input){
            case("landed"):
                this.changeDirection();
                break;
        }
    }

    private void changeDirection(){
        // Get top block to resolve issues; player supposed to fall
        Tile3D above = GameConstants.getMap().getTopTile(this.tilePosition);
        if(above == null){
            return;
        }
        // Check for player
        Avatar3D player = above.getPlayer();
        if(player == null){
            return;
        }
        // Set player direction
        player.resolveRoatationTo(this.entityDirection);
    }

}
