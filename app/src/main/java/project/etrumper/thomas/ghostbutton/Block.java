package project.etrumper.thomas.ghostbutton;

/**
 * Created by thoma on 8/2/2016.
 * Property of boxedworks.
 */
public class Block extends EntityTile3D {

    Vector3f drawColor;

    long disappearTimer, // Time in milli for it to disappear
        countTimer; // Used for calculating disappear time

    private boolean canCollide;

    Block(String libName){
        super(libName, 0.5f, 0.5f);
        this.disappearTimer = 0;
        this.countTimer = 0;

        this.canCollide = true;
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
                if(!this.canCollide()){
                    this.canCollide = true;
                    return;
                }
                // Set local variables
                this.canCollide = false;
                this.countTimer = this.disappearTimer;
                // Get top block to resolve issues; player supposed to fall
                Tile3D above = GameConstants.tileMap3D.getTopTile(this.tilePosition);
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
    protected void draw(){
        if(!this.canCollide()){
            return;
        }
        // Change color to current
        MaterialManager.changeMaterialColor("Wall", this.drawColor);
        // Draw
        super.draw();
    }

    public void disappear(){
        // Check if can disappear
        if(this.disappearTimer == 0){
            return;
        }
        // Check if is already disappearing
        if(this.countTimer <= 0) {
            this.countTimer = this.disappearTimer;
        }
    }

    public boolean canCollide(){
        return this.canCollide;
    }

}
