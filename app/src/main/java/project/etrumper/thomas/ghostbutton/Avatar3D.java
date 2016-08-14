package project.etrumper.thomas.ghostbutton;

/**
 * Created by thoma on 7/18/2016.
 * Property of boxedworks.
 */
public class Avatar3D extends EntityTile3D {

    Ease2 movementEaseX, movementEaseY;

    GravityAxis jump;

    private Sound tick, sCork1, sCork2, sCork3, sCork4, sCork5, sCork6;

    private boolean outOfMap = false;

    private static float dissapearHeight = -15f,
                reappearHeight = 10f;
    // Super-jump variables
    long jumpTimer = 0, // Keeps track of milli each jump
            releaseTimer = -1; // Tracks time button released
    boolean charging = false, // Tells when player is charging super-jump
            superHigh = false; // Tells if it is a high or long jump

    Avatar3D() {
        super("CubeMonster", EntityType.ENTITY, 1, 0.45f, 0.3f);

        this.addAnimations();
        // Change TAG after loading animations
        super.TAG = "Player";

        this.entityDirection = Direction.EAST;
        this.spawnDirection = this.entityDirection;

        // Fix super() constructor rotation
        this.rotation = new float[]{90f, 90f, 0f};
        // Initiate tweens as null
        this.movementEaseX = null;
        this.movementEaseY = null;
        // Initiate gravity object as null
        this.jump = null;
        // Load sound(s)
        this.tick = new Sound(R.raw.tick1);
        this.sCork1 = new Sound(R.raw.cork_c);
        this.sCork2 = new Sound(R.raw.cork_d);
        this.sCork3 = new Sound(R.raw.cork_e);
        this.sCork4 = new Sound(R.raw.cork_f);
        this.sCork5 = new Sound(R.raw.cork_a);
        this.sCork6 = new Sound(R.raw.cork_b);
        // Set speeds
        this.deltaTimeModifier = 2.f;
        GravityAxis.dtModifier = this.deltaTimeModifier;
        this.directional = true;
    }

    // Object constants
    static float player_camera_offset = 2f,
            camera_z_zoom = 5f,
            player_z_jump = 5.6f;
    static long movement_speed = 1200, // Time in milli for movement to cease
        super_jump_bias = 150, // Time in milli for lenience on performing super jumps
        easeTimes = 30;

    boolean firstUpdate = false;

    @Override
    protected void update() {
        super.update();
        // Check to see if movement ease
        if (this.movementEaseX != null) {
            this.movementEaseX.update();
            this.position[0] = (float)this.movementEaseX.easeLinear();
            // Check if finished
            if (this.movementEaseX.done()) {
                this.movementEaseX = null;
            }
        }
        if (this.movementEaseY != null) {
            this.movementEaseY.update();
            this.position[1] = (float)this.movementEaseY.easeLinear();
            // Check if finished
            if (this.movementEaseY.done()) {
                this.movementEaseY = null;
                //this.rotation[0] = 0f;
            }
        }
        // Check jump
        if (this.jump != null) {
            // Increment jump timer
            this.jumpTimer += SuperManager.deltaTime;
            // Update object and y-pos
            this.jump.update();
            this.position[2] = (float)jump.getPosition();
            // Removes jump if falling for too long
            if (this.jump.done()) {
                this.jump = null;
            } else {
                float tileZ = dissapearHeight; // Set default as pit in case empty space is found
                // Check if out of map
                if (!this.outOfMap) {
                    // Check for block below avatar
                    Tile3D belowTile = GameConstants.getMap().getFirstBottomTile(this.tilePosition);
                    if (belowTile != null && belowTile.hasBlock()) {
                        tileZ = belowTile.tilePosition.z() + (this.heightRadius + belowTile.children[0].heightRadius); // 1f to account for offset (tiles are one block lower)
                    }
                } else {
                    // Is out of map.. check if is out of map in x or y
                    int gX = (int)(Math.ceil(this.position[0])),
                            gY = (int)(Math.ceil(this.position[1]));
                    Vector3i mD = GameConstants.getMap().dimensions;
                    if(gX <= 0 || gX >= mD.x() || gY <= 0 || gY >= mD.y()){

                    }else {
                        Tile3D belowTile = GameConstants.getMap().getFirstBottomTile(new Vector3i(this.tilePosition.x(), this.tilePosition.y(), GameConstants.getMap().dimensions.z() - 1));
                        if (belowTile != null && belowTile.hasBlock()) {
                            tileZ = belowTile.tilePosition.z() + (this.heightRadius + belowTile.children[0].heightRadius);
                        }
                    }
                }
                float charZ = this.position[2];
                if (charZ <= tileZ) {
                    this.rotation[0] = 90f;
                    this.rotation[1] = 90f;
                    this.rotation[2] = 0f;
                    // Set final tilePosition
                    this.position[2] = tileZ;
                    // Remove jump
                    this.jump = null;
                    // Check for super jump
                    if (this.charging && this.releaseTimer >= this.jumpTimer - super_jump_bias) {
                        if (this.superHigh) {
                            this.superHighJump();
                        } else {
                            this.superLongJump();
                        }
                    }
                    // Record the time
                    this.releaseTimer = this.jumpTimer;
                    // If out ot map, put back
                    if (this.outOfMap) {
                        this.position[0] = this.spawnLocation.x();
                        this.position[1] = this.spawnLocation.y();
                        this.position[2] = reappearHeight;
                        this.resolveRoatationTo(this.spawnDirection);
                        this.jump = new GravityAxis(this.position[2], -8f, 10000);
                        this.jump.gravity = 1f;
                    } else {
                        this.landed();
                    }
                }
            }
        }
        // Check if charging jump so can start timer
        else {
            if (this.charging) {
                this.releaseTimer += SuperManager.deltaTime;
            }
        }
        // Update tilemap tilePosition
        if (this.movementEaseX != null || this.movementEaseY != null || this.jump != null) {
            this.updateMap();
            // Rotate cube
            float rotateAmount = 160.f * (SuperManager.deltaTime / 1000f) * 2f;
            if (this.movementEaseX != null) {
                if (this.movementEaseX.desiredChangeValue < 0f) {
                    if (this.jump != null) {
                        this.rotation[2] -= rotateAmount;
                    } else {
                        this.rotation[1] = 180f;
                    }
                } else {
                    if (this.jump != null) {
                        this.rotation[2] -= rotateAmount;
                    } else {
                        this.rotation[1] = 0f;
                    }
                }
            }
            if (this.movementEaseY != null) {
                if (this.movementEaseY.desiredChangeValue >= 0f) {
                    if (this.jump != null) {
                        this.rotation[0] -= rotateAmount;
                    } else {
                        this.rotation[1] = 90f;
                    }
                } else {
                    if (this.jump != null) {
                        this.rotation[0] += rotateAmount;
                    } else {
                        this.rotation[1] = 270f;
                    }
                }
            }
        }
        // Check for first time being updated
        if(!this.disabled) {
            if (!this.firstUpdate) {
                firstUpdate = true;
                this.enteredNextTile();
                this.currentAnimation.calculateFrameTime((long)(this.movement_speed / this.deltaTimeModifier));
            }
        }
    }

    protected void moveForward(int moveBy) {
        long moveTime = (long) (movement_speed / deltaTimeModifier);
        if (this.entityDirection == Direction.NORTH) {
            this.movementEaseY = Ease2.getEase2(this.position[1], this.tilePosition.y() + moveBy, moveTime);
        } else if (this.entityDirection == Direction.SOUTH) {
            this.movementEaseY = Ease2.getEase2(this.position[1], this.tilePosition.y() - moveBy, moveTime);
        } else if (this.entityDirection == Direction.WEST) {
            this.movementEaseX = Ease2.getEase2(this.position[0], this.tilePosition.x() - moveBy, moveTime);
        } else if (this.entityDirection == Direction.EAST) {
            this.movementEaseX = Ease2.getEase2(this.position[0], this.tilePosition.x() + moveBy, moveTime);
        }
    }

    private void updateMap() {
        // Check to make sure the tilePosition has changed
        Vector3i tilePos = new Vector3i(
                Math.round(this.position[0]),
                Math.round(this.position[1]),
                Math.round(this.position[2])
        );
        // Check to see if entered new tile
        if (Vector3i.equals(tilePos, this.tilePosition)) {
            return;
        }
        // Update tile tilePosition
        this.tilePosition = tilePos;
        // Check if new tile exists
        if (GameConstants.getMap().getTile(tilePos) != null) {
            this.outOfMap = false;

            // Remove from map
            if (!GameConstants.getMap().getTile(this.tilePosition).removeEntity(this)) {
                GameConstants.getMap().hardRemove(this);
            }
            // Place back into map with new tilePosition
            GameConstants.getMap().getTile(this.tilePosition).addEntity(this);
        } else {
            this.outOfMap = true;
        }
        // If not in the air
        if (this.jump == null) {
            this.enteredNextTile();
        }
    }

    protected void enteredNextTile(){
        if(this.outOfMap){
            this.fall();
            return;
        }
        // Get tile under self
        Tile3D belowTile = GameConstants.getMap().getTile(this.tilePosition).bottom();
        if(!belowTile.hasBlock()){
            this.fall();
        }
        // Check if is rolling still
        if(!this.currentAnimation.isPlaying) {
            // Check if is a block
            Block block = belowTile.getBlock();
            if (block != null) {
                block.inputHandler("landed");
            }
        }
    }

    private Vector3i getFixedPos() {
        return new Vector3i(this.tilePosition.x(), this.tilePosition.y(), (int) (this.position[2] + this.heightRadius));
    }

    protected void handleCamera(){
        switch (Overlay.optionsMenu.getData()[0]) {
            // Behind
            case (0):
                player_camera_offset = 2f;
                camera_z_zoom = 5f;
                this.moveCameraTo();
                this.moveCameraTargetTo();
                break;
            // Beside
            case (1):
                player_camera_offset = 4f;
                camera_z_zoom = 1.5f;
                this.moveCameraNextTo();
                this.moveCameraTargetTo();
                break;
            // Inside
            case (2):
                camera_z_zoom = this.heightRadius + 0.6f;
                this.moveCameraInto();
                camera_z_zoom = this.heightRadius + 0.3f;
                this.moveCameraTargetInFront();
                break;
            // Overhead
            case (3):
                player_camera_offset = .01f;
                camera_z_zoom = 8f;
                this.moveCameraTo();
                this.moveCameraTargetTo();
                break;
        }
        // If first person, do not draw character
        if(Overlay.optionsMenu.getData()[0] == 2){
            this.visible = false;
        }else{
            this.visible = true;
        }
    }

    protected void addAnimations(){
        super.currentAnimation = addAnimation("move");
    }

    protected void rollForward(){
        if(!this.canRollForward()){
            return;
        }
        this.moveForward(1);
        this.currentAnimation.currentTime = 0;
        this.currentAnimation.play();
    }

    protected boolean canRollForward(){
        // Check if already rolling
        if(this.movementEaseX != null || this.movementEaseY != null || this.jump != null){
            return false;
        }
        if(this.outOfMap){
            return true;
        }
        // Check tile in front
        Tile3D tile = this.getFrontTile();
        if(tile == null){
            return true;
        }
        if(tile.hasBlock()){
            return false;
        }
        return true;
    }

    protected void landed(){
        // Play sound
        this.tick.play();
        this.enteredNextTile();
    }

    protected void highJump(){
        // Check if jumping; if is, set up for super jump
        if(this.isJumping()){
            this.charging = true;
            this.superHigh = true;
            return;
        }
        // Check block in front to check if can jump forward
        if(!canHighJump()){
            return;
        }
        // Start the movement ease
        this.moveForward(1);
        // Jump
        this.jump = new GravityAxis(this.position[2], player_z_jump, 10000);
        this.playRandomSound("jump");
        // Reset super jump variables
        this.jumpTimer = 0;
        this.releaseTimer = -1;
        this.charging = false;
    }

    protected void longJump(){
        // Check if jumping; if is, set up for super jump
        if(this.isJumping()){
            this.charging = true;
            this.superHigh = false;
            return;
        }
        // Check no block in front
        if(!this.canLongJump()){
            return;
        }

        // Start the movement ease
        this.moveForward(2);
        // Jump
        this.jump = new GravityAxis(this.position[2], player_z_jump, 10000);
        this.playRandomSound("jump");
        // Reset super jump variables
        this.jumpTimer = 0;
        this.releaseTimer = -1;
        this.charging = false;
    }

    protected boolean canHighJump() {
        // Make sure isn't out of map
        if (this.outOfMap) {
            return true;
        }
        /// Gather all tiles that need to be empty
        // Check tiles for null
        Tile3D ownTile = GameConstants.getMap().getTile(this);
        if (ownTile == null) {
            return true;
        }
        Tile3D tile1 = ownTile.top();
        if (tile1 == null) {
            return true;
        }
        Tile3D tile2 = tile1.directional(this.entityDirection);
        if (tile2 == null) {
            return true;
        }
        // Check all for solids
        Tile3D[] tiles = new Tile3D[]{
                tile1, tile2
        };
        for (Tile3D tile : tiles) {
            // Check if empty
            if (tile.hasBlock()) {
                // See if can jump in place
                if(!tile1.hasBlock()){
                    this.jump = new GravityAxis(this.position[2], 4f, 10000);
                    this.playRandomSound("short_jump");
                }
                return false;
            }
        }
        return true;
    }

    private boolean canLongJump(){
        // Make sure isn't out of map
        if(this.outOfMap){
            return true;
        }
        Tile3D tile = this.getFrontTile();
        // Check for null tile; tile does not exist, reached edge of map; let jump out
        if (tile == null) {
            return true;
        }
        // Check if front tile has piece; too tall to jump
        if(tile.hasBlock()){
            return false;
        }
        // Get block after this
        tile = GameConstants.getMap().getTileByDirection(tile.tilePosition, this.entityDirection);
        // Check if is at edge of map
        if(tile == null){
            return true;
        }
        // Make sure isn't occupied
        if(tile.hasBlock()){
            return false;
        }
        return true;
    }

    protected void superLongJump(){
        this.charging = false;
        if(!this.canSuperLongJump()){
            return;
        }
        // Start the movement ease
        this.moveForward(3);
        // Jump
        this.jump = new GravityAxis(this.position[2], player_z_jump, 10000);
        this.playRandomSound("super_jump");
    }

    protected void superHighJump(){
        this.charging = false;
        if(!this.canSuperHighJump()){
            return;
        }
        // Start the movement ease
        this.moveForward(1);
        // Jump
        this.jump = new GravityAxis(this.position[2], 7f, 10000);
        this.playRandomSound("super_jump");
    }

    private boolean canSuperLongJump(){
        if(this.isJumping()){
            return false;
        }
        // Make sure isn't out of map
        if(this.outOfMap){
            return true;
        }
        Tile3D tile = this.getFrontTile();
        // Check for null tile; tile does not exist, reached edge of map; let jump out
        if (tile == null) {
            return true;
        }
        // Check if front tile has piece; too tall to jump
        if(tile.hasBlock()){
            return false;
        }
        // Get block after this
        tile = GameConstants.getMap().getTileByDirection(tile.tilePosition, this.entityDirection);
        // Check if is at edge of map
        if(tile == null){
            return true;
        }
        // Make sure isn't occupied
        if(tile.hasBlock()){
            return false;
        }
        // Get block after this
        tile = GameConstants.getMap().getTileByDirection(tile.tilePosition, this.entityDirection);
        // Check if is at edge of map
        if(tile == null){
            return true;
        }
        // Make sure isn't occupied
        if(tile.hasBlock()){
            return false;
        }
        return true;
    }

    private boolean canSuperHighJump(){
        if(this.isJumping()){
            return false;
        }
        // Make sure isn't out of map
        if (this.outOfMap) {
            return true;
        }
        // Check tiles for null
        Tile3D ownTile = GameConstants.getMap().getTile(this);
        if (ownTile == null) {
            return true;
        }
        Tile3D tile1 = ownTile.top();
        if (tile1 == null) {
            return true;
        }
        Tile3D tile2 = tile1.top();
        if (tile2 == null) {
            return true;
        }
        Tile3D tile3 = tile2.directional(this.entityDirection);
        if (tile3 == null) {
            return true;
        }
        // Check all for solids
        Tile3D[] tiles = new Tile3D[]{
                tile1, tile2, tile3
        };
        for (Tile3D tile : tiles) {
            // Check if empty
            if (tile.hasBlock()) {
                return false;
            }
        }
        return true;
    }

    protected boolean isJumping(){
        return (this.jump != null);
    }

    @Override
    protected  void setDirection(Direction direction){
        this.resolveRoatationTo(direction);
    }

    private void moveCameraTo() {
        this.moveCameraTo(new Vector3f(0f, 0f, 0f));
    }

    private void moveCameraTo(Vector3f offset) {
        float[] temp;
        if (this.entityDirection == Direction.NORTH) {
            temp = new float[]{(this.position[0]) + offset.x(), (this.position[1] - player_camera_offset) + offset.y(), this.position[2] + camera_z_zoom + offset.z()};
        } else if (this.entityDirection == Direction.SOUTH) {
            temp = new float[]{(this.position[0]) + offset.x(), (this.position[1] + player_camera_offset) + offset.y(), this.position[2] + camera_z_zoom + offset.z()};
        } else if (this.entityDirection == Direction.WEST) {
            temp = new float[]{(this.position[0] + player_camera_offset) + offset.x(), (this.position[1]) + offset.y(), this.position[2] + camera_z_zoom + offset.z()};
        } else {
            temp = new float[]{(this.position[0] - player_camera_offset) + offset.x(), (this.position[1]) + offset.y(), this.position[2] + camera_z_zoom + offset.z()};
        }
        GameConstants.camera.easePositionTo(temp, easeTimes);
        //GameConstants.camera.position = temp;
    }

    private void moveCameraNextTo() {
        float[] temp;
        if (this.entityDirection == Direction.EAST) {
            temp = new float[]{(this.position[0]), (this.position[1] - player_camera_offset), this.position[2] + camera_z_zoom};
        } else if (this.entityDirection == Direction.WEST) {
            temp = new float[]{(this.position[0]), (this.position[1] + player_camera_offset), this.position[2] + camera_z_zoom};
        } else if (this.entityDirection == Direction.NORTH) {
            temp = new float[]{(this.position[0] + player_camera_offset), (this.position[1]), this.position[2] + camera_z_zoom};
        } else {
            temp = new float[]{(this.position[0] - player_camera_offset), (this.position[1]), this.position[2] + camera_z_zoom};
        }
        GameConstants.camera.easePositionTo(temp, easeTimes);
        //GameConstants.camera.position = temp;
    }

    private void moveCameraInto(){
        float[] temp;
        float horizontalOffset = 0.7f;
        if(this.entityDirection == Direction.NORTH){
            temp = new float[]{this.position[0], this.position[1] - horizontalOffset, this.position[2] + camera_z_zoom};
        }else if(this.entityDirection == Direction.SOUTH){
            temp = new float[]{this.position[0], this.position[1] + horizontalOffset, this.position[2] + camera_z_zoom};
        }else if(this.entityDirection == Direction.WEST){
            temp = new float[]{this.position[0] + horizontalOffset, this.position[1], this.position[2] + camera_z_zoom};
        }else{
            temp = new float[]{this.position[0] - horizontalOffset, this.position[1], this.position[2] + camera_z_zoom};
        }
        GameConstants.camera.easePositionTo(temp, easeTimes);
        //GameConstants.camera.position = temp;
    }

    private void moveCameraTargetTo() {
        this.moveCameraTargetTo(new Vector3f(0f, 0f, 0f));
    }

    private void moveCameraTargetTo(Vector3f offset) {
        float[] temp = new float[]{
                this.position[0] + offset.x(),
                this.position[1] + offset.y(),
                this.position[2] + offset.z(),
        };
        GameConstants.camera.easeTargetTo(temp, easeTimes);
        //GameConstants.camera.target = temp;
    }

    private void moveCameraTargetInFront() {
        float hOffset = 1f;
        float[] temp;
        if (this.entityDirection == Direction.NORTH) {
            temp = new float[]{this.position[0], this.position[1] + hOffset, this.position[2] + camera_z_zoom};
        } else if (this.entityDirection == Direction.SOUTH) {
            temp = new float[]{this.position[0], this.position[1] - hOffset, this.position[2] + camera_z_zoom};
        } else if (this.entityDirection == Direction.WEST) {
            temp = new float[]{this.position[0] - hOffset, this.position[1], this.position[2] + camera_z_zoom};
        } else {
            temp = new float[]{this.position[0] + hOffset, this.position[1], this.position[2] + camera_z_zoom};
        }
        GameConstants.camera.easeTargetTo(temp, easeTimes);
        //GameConstants.camera.target = temp;
    }

    private Tile3D getFrontTile() {
        if (this.entityDirection == Direction.NORTH) {
            return GameConstants.getMap().getNorthTile(this.getFixedPos());
        } else if (this.entityDirection == Direction.SOUTH) {
            return GameConstants.getMap().getSouthTile(this.getFixedPos());
        } else if (this.entityDirection == Direction.WEST) {
            return GameConstants.getMap().getWestTile(this.getFixedPos());
        } else {
            return GameConstants.getMap().getEastTile(this.getFixedPos());
        }
    }

    protected Tile3D getBackTile() {
        if (this.entityDirection == Direction.SOUTH) {
            return GameConstants.getMap().getNorthTile(this.getFixedPos());
        } else if (this.entityDirection == Direction.NORTH) {
            return GameConstants.getMap().getSouthTile(this.getFixedPos());
        } else if (this.entityDirection == Direction.EAST) {
            return GameConstants.getMap().getWestTile(this.getFixedPos());
        } else {
            return GameConstants.getMap().getEastTile(this.getFixedPos());
        }
    }

    public void fall(){
        if(this.jump == null){
            this.jump = new GravityAxis(this.position[2], 0f, 10000);
        }
    }

    private int sCounter = 0;
    private void playRandomSound(String sound){
        switch (sound.toLowerCase()){
            case("jump"):
                if(sCounter++ % 2 == 0){
                    this.sCork3.play();
                }else{
                    this.sCork4.play();
                }
                break;
            case("super_jump"):
                if(sCounter++ % 2 == 0){
                    this.sCork5.play();
                }else{
                    this.sCork6.play();
                }
                break;
            case("short_jump"):
                if(sCounter++ % 2 == 0){
                    this.sCork1.play();
                }else{
                    this.sCork2.play();
                }
                break;
        }
    }

}
