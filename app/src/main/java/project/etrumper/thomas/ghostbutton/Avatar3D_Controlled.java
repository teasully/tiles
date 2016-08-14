package project.etrumper.thomas.ghostbutton;

/**
 * Created by thoma on 8/10/2016.
 * Property of boxedworks.
 */
public class Avatar3D_Controlled extends Avatar3D {

    Direction cameraDirection;

    Avatar3D_Controlled(){
        super();

        this.cameraDirection = this.spawnDirection;
        // Set controls visibility
        Overlay.showControls = true;
    }

    @Override
    protected void update(){
        super.update();
        // Do not move camera if disabled
        if(this.disabled){
            return;
        }
        // Adjust camera
        this.handleCamera();
    }

    @Override
    protected void inputHandler(String input) {
        // Make sure input isn't a double
        if (!super.isFreshInput()) {
            return;
        }
        // Check custom input provided by Controller
        switch (input) {
            case ("left_button"):
                // Change current cameraDirection
                this.turnCCW_Z();
                break;
            case ("middle_l_button"):
                // High jump
                this.highJump();
                break;
            case ("middle_r_button"):
                // Long jump
                this.longJump();
                break;
            case ("right_button"):
                // Change current cameraDirection
                this.turnCW_Z();
                break;
            case("middle_l_button_release"):
                if(this.charging) {
                    // If isn't jumping, record the release timer to be used in update loop
                    if(this.isJumping()) {
                        this.releaseTimer = this.jumpTimer;
                    }
                    // Else, check the release time against the jump time
                    else{
                        if(this.releaseTimer <= this.jumpTimer + super_jump_bias){
                            this.superHighJump();
                        }
                    }
                }
                break;
            case("middle_r_button_release"):
                if(this.charging) {
                    // If isn't jumping, record the release timer to be used in update loop
                    if(this.isJumping()) {
                        this.releaseTimer = this.jumpTimer;
                    }
                    // Else, check the release time against the jump time
                    else{
                        if(this.releaseTimer <= this.jumpTimer + super_jump_bias){
                            this.superLongJump();
                        }
                    }
                }
                break;
        }
    }

    @Override
    protected Direction turnCW_Z() {
        Direction temp;
        if (this.cameraDirection == Direction.NORTH) {
            temp = Direction.EAST;
        } else if (this.cameraDirection == Direction.EAST) {
            temp = Direction.SOUTH;
        } else if (this.cameraDirection == Direction.SOUTH) {
            temp = Direction.WEST;
        } else {
            temp = Direction.NORTH;
        }
        this.cameraDirection = temp;
        return temp;
    }

    @Override
    protected Direction turnCCW_Z() {
        Direction temp;
        if (this.cameraDirection == Direction.NORTH) {
            temp = Direction.WEST;
        } else if (this.cameraDirection == Direction.WEST) {
            temp = Direction.SOUTH;
        } else if (this.cameraDirection == Direction.SOUTH) {
            temp = Direction.EAST;
        } else {
            temp = Direction.NORTH;
        }
        this.cameraDirection = temp;
        return temp;
    }

    @Override
    protected void moveForward(int moveBy) {
        long moveTime = (long) (movement_speed / deltaTimeModifier);
        if (this.cameraDirection == Direction.NORTH) {
            this.movementEaseY = Ease2.getEase2(this.position[1], this.tilePosition.y() + moveBy, moveTime);
        } else if (this.cameraDirection == Direction.SOUTH) {
            this.movementEaseY = Ease2.getEase2(this.position[1], this.tilePosition.y() - moveBy, moveTime);
        } else if (this.cameraDirection == Direction.WEST) {
            this.movementEaseX = Ease2.getEase2(this.position[0], this.tilePosition.x() - moveBy, moveTime);
        } else if (this.cameraDirection == Direction.EAST) {
            this.movementEaseX = Ease2.getEase2(this.position[0], this.tilePosition.x() + moveBy, moveTime);
        }
        // Set avatar direction
        this.entityDirection = this.cameraDirection;
    }

}
