package project.etrumper.thomas.ghostbutton;

/**
 * Created by thoma on 8/13/2016.
 * Property of boxedworks.
 */

public class Block_HighJump extends Block {

    Block_HighJump(int ID){
        super(ID);
    }

    @Override
    protected void inputHandler(String input){
        switch (input){
            case("landed"):
                this.triggerJump();
                break;
        }
    }

    private void triggerJump(){
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
        // Set jump
        player.highJump();
    }
}
