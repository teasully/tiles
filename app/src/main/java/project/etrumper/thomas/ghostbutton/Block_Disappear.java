package project.etrumper.thomas.ghostbutton;

/**
 * Created by thoma on 8/10/2016.
 * Property of boxedworks.
 */
public class Block_Disappear extends Block {

    private long disappearTimer, // Time in milli for it to disappear
            countTimer; // Used for calculating disappear time

    Block_Disappear(int ID){
        super(ID);
        // Set locals
        this.disappearTimer = 500;
        this.countTimer = 0;
    }

    @Override
    protected void update(){
        super.update();
        // Check if is disappearing
        if(this.countTimer > 0) {
            // If is, decrement countTimer
            this.countTimer -= SuperManager.deltaTime;
            // Check if countTimer is zero
            if(this.countTimer <= 0) {
                // Check if already disappeared; then is reappearing
                if(!this.canCollide){
                    this.canCollide = true;
                    return;
                }
                // Set local variables
                this.canCollide = false;
                this.countTimer = this.disappearTimer;
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
                // Check is player is already jumping
                if(player.jump != null){
                    return;
                }
                // Set player gravity
                player.fall();
            }
        }
    }

    @Override
    protected void inputHandler(String input){
        switch (input){
            case("landed"):
                this.disappear();
                break;
        }
    }

    private void disappear(){
        // Check if can disappear
        if(this.disappearTimer == 0){
            return;
        }
        // Check if is already disappearing
        if(this.countTimer <= 0) {
            this.countTimer = this.disappearTimer;
        }
    }

}
