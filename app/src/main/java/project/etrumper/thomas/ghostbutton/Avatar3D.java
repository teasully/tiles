package project.etrumper.thomas.ghostbutton;

/**
 * Created by thoma on 7/18/2016.
 */
public class Avatar3D extends EntityTile3D {

    enum Direction {
        NORTH,
        SOUTH,
        EAST,
        WEST
    }

    Ease2 movementEaseX, movementEaseY;

    GravityAxis jump;

    Direction cameraDirection, avatarDirection;

    Sound tick, sCork1, sCork2, sCork3, sCork4, sCork5, sCork6;

    boolean outOfMap = false;

    // Super-jump variables
    long jumpTimer = 0, // Keeps track of milli each jump
            releaseTimer = -1; // Tracks time button released
    boolean charging = false, // Tells when player is charging super-jump
            superHigh = false; // Tells if it is a high or long jump

    Vector3i spawnPos;

    Avatar3D(Vector3i position) {
        super("CubeMonster", 0.45f, 0.3f);

        super.currentAnimation = addAnimation("idle");

        this.spawnPos = position;

        this.cameraDirection = Direction.NORTH;
        this.avatarDirection = Direction.NORTH;

        this.tilePosition = position;
        // Fix super() constructor rotation
        this.rotation = new float[]{0f, 0f, 0f};
        // Merge tilePos and pos for camera
        this.convertTilePosToGlobal();
        // Set camera to tilePosition
        this.moveCameraTo();
        this.moveCameraTargetTo();
        // Initiate tweens as null
        this.movementEaseX = null;
        this.movementEaseY = null;
        // Initiate gravity object as null
        this.jump = null;
        // Load sound(s)
        this.tick = new Sound(R.raw.tick1);
        this.sCork1 = new Sound(R.raw.cork1);
        this.sCork2 = new Sound(R.raw.cork2);
        this.sCork3 = new Sound(R.raw.cork3);
        this.sCork4 = new Sound(R.raw.cork4);
        this.sCork5 = new Sound(R.raw.cork5);
        this.sCork6 = new Sound(R.raw.cork6);
        // Set speeds
        this.deltaTimeModifier = 2.f;
        GravityAxis.dtModifier = this.deltaTimeModifier;
    }

    // Object constants
    static float player_camera_offset = 2f,
            camera_z_zoom = 5f,
            player_z_jump = 5.6f;
    static long movement_speed = 1200, // Time in milli for movement to cease
        super_jump_bias = 150; // Time in milli for lenience on performing super jumps

    @Override
    protected void update() {
        super.update();
        // Check to see if movement ease
        if (this.movementEaseX != null) {
            this.movementEaseX.update();
            this.position[0] = this.movementEaseX.easeLinear();
            // Check if finished
            if (this.movementEaseX.done()) {
                this.movementEaseX = null;
            }
        }
        if (this.movementEaseY != null) {
            this.movementEaseY.update();
            this.position[1] = this.movementEaseY.easeLinear();
            // Check if finished
            if (this.movementEaseY.done()) {
                this.movementEaseY = null;
            }
        }
        // Check jump
        if (this.jump != null) {
            // Increment jump timer
            this.jumpTimer += SuperManager.deltaTime;
            // Rotate cube
            float rotateAmount = 3.2f * 1; // Last int is number of rotations while in air
            if (this.avatarDirection == Direction.NORTH) {
                this.rotation[0] -= rotateAmount;
            } else if (this.avatarDirection == Direction.SOUTH) {
                this.rotation[0] += rotateAmount;
            } else if (this.avatarDirection == Direction.WEST) {
                this.rotation[1] -= rotateAmount;
            } else if (this.avatarDirection == Direction.EAST) {
                this.rotation[1] += rotateAmount;
            }
            // Update object and y-pos
            this.jump.update();
            this.position[2] = jump.getPosition();
            this.tilePosition.z((int) this.position[2]);
            // Timeout remover
            if (this.jump.done()) {
                this.jump = null;
            } else {
                float tileZ = -10f; // Set default as pit in case empty space is found
                // Check if out of map
                if (!this.outOfMap) {
                    // Check if done jumping
                    Tile3D belowTile = GameConstants.tileMap3D.getTile(this.getFixedPos());
                    // Check if out of map or if there is no solid
                    if (belowTile != null && !belowTile.hasSolid()) {
                        // Check if block below is solid
                        belowTile = GameConstants.tileMap3D.getFirstBottomTile(this.getFixedPos());
                    }
                    if (belowTile != null && belowTile.hasSolid()) {
                        tileZ = belowTile.tilePosition.z() + (this.heightRadius + belowTile.children[0].heightRadius); // 1f to account for offset (tiles are one block lower)
                    }
                }else{
                    // Is probably out of map.. check if is above map
                    Tile3D belowTile = GameConstants.tileMap3D.getFirstBottomTile(new Vector3i((int) this.position[0], (int) this.position[1], GameConstants.tileMap3D.dimensions.z() - 1));
                    if(belowTile != null && belowTile.hasSolid()){
                        tileZ = belowTile.tilePosition.z() + (this.heightRadius + belowTile.children[0].heightRadius);
                    }
                }
                float charZ = this.position[2];
                if (charZ <= tileZ) {
                    this.rotation[0] = 0f;
                    this.rotation[1] = 0f;
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
                    if(this.outOfMap){
                        this.position[0] = this.spawnPos.x();
                        this.position[1] = this.spawnPos.y();
                        this.position[2] = 30f;
                        GameConstants.camera.target = this.position;
                        this.jump = new GravityAxis(this.position[2], -8f, 10000);
                        this.jump.gravity = 1f;
                    }else{
                        // Play sound
                        this.tick.play();
                    }
                }
            }
        }
        // Check if charging jump so can start timer
        else{
            if(this.charging){
                this.releaseTimer += SuperManager.deltaTime;
            }
        }
        // Update tilemap tilePosition
        if (this.movementEaseX != null || this.movementEaseY != null || this.jump != null) {
            this.updateMap();
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

    protected void moveForward(int moveBy) {
        long moveTime = (long)(movement_speed / deltaTimeModifier);
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
        this.avatarDirection = this.cameraDirection;
    }

    protected void updateMap() {
        // Check to make sure the tilePosition has changed
        Vector3i tilePos = new Vector3i(
                Math.round(this.position[0]),
                Math.round(this.position[1]),
                Math.round(this.position[2])
        );
        if(this.tilePosition.equals(tilePos)){
            return;
        }
        // Check if new tile exists
        if(GameConstants.tileMap3D.getTile(tilePos) == null){
            //LOGE("Entity out of map");
            this.outOfMap = true;
            return;
        }else{
            this.outOfMap = false;
        }
        // Remove from map
        if (!GameConstants.tileMap3D.getTile(this.tilePosition).removeEntity(this)) {
            if(!GameConstants.tileMap3D.hardRemove(this))
                return;
        }
        // Update tile tilePosition
        this.tilePosition = tilePos;
        // Place back into map with new tilePosition
        GameConstants.tileMap3D.getTile(this.tilePosition).addEntity(this);
    }

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

    private Vector3i getFixedPos() {
        return new Vector3i(this.tilePosition.x(), this.tilePosition.y(), (int) (this.position[2] + this.heightRadius));
    }

    private void handleCamera(){
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
                camera_z_zoom = 5f;
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

    private void highJump(){
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

    private void longJump(){
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

    private boolean canHighJump() {
        // Make sure isn't out of map
        if (this.outOfMap) {
            return false;
        }
        /// Gather all tiles that need to be empty
        // Check tiles for null
        Tile3D ownTile = GameConstants.tileMap3D.getTile(this);
        if (ownTile == null) {
            return true;
        }
        Tile3D tile1 = ownTile.top();
        if (tile1 == null) {
            return true;
        }
        Tile3D tile2 = tile1.directional(this.cameraDirection);
        if (tile2 == null) {
            return true;
        }
        // Check all for solids
        Tile3D[] tiles = new Tile3D[]{
                tile1, tile2
        };
        for (Tile3D tile : tiles) {
            // Check if empty
            if (tile.hasSolid()) {
                // See if can jump in place
                if(!tile1.hasSolid()){
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
            return false;
        }
        Tile3D tile = this.getFrontTile();
        // Check for null tile; tile does not exist, reached edge of map; let jump out
        if (tile == null) {
            return true;
        }
        // Check if front tile has piece; too tall to jump
        if(tile.hasSolid()){
            return false;
        }
        // Get block after this
        tile = GameConstants.tileMap3D.getTileByDirection(tile.tilePosition, this.cameraDirection);
        // Check if is at edge of map
        if(tile == null){
            return true;
        }
        // Make sure isn't occupied
        if(tile.hasSolid()){
            return false;
        }
        return true;
    }

    private void superLongJump(){
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

    private void superHighJump(){
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
            return false;
        }
        Tile3D tile = this.getFrontTile();
        // Check for null tile; tile does not exist, reached edge of map; let jump out
        if (tile == null) {
            return true;
        }
        // Check if front tile has piece; too tall to jump
        if(tile.hasSolid()){
            return false;
        }
        // Get block after this
        tile = GameConstants.tileMap3D.getTileByDirection(tile.tilePosition, this.cameraDirection);
        // Check if is at edge of map
        if(tile == null){
            return true;
        }
        // Make sure isn't occupied
        if(tile.hasSolid()){
            return false;
        }
        // Get block after this
        tile = GameConstants.tileMap3D.getTileByDirection(tile.tilePosition, this.cameraDirection);
        // Check if is at edge of map
        if(tile == null){
            return true;
        }
        // Make sure isn't occupied
        if(tile.hasSolid()){
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
            return false;
        }
        // Check tiles for null
        Tile3D ownTile = GameConstants.tileMap3D.getTile(this);
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
        Tile3D tile3 = tile2.directional(this.cameraDirection);
        if (tile3 == null) {
            return true;
        }
        // Check all for solids
        Tile3D[] tiles = new Tile3D[]{
                tile1, tile2, tile3
        };
        for (Tile3D tile : tiles) {
            // Check if empty
            if (tile.hasSolid()) {
                return false;
            }
        }
        return true;
    }

    private boolean isJumping(){
        return (this.jump != null);
    }

    protected void moveCameraTo() {
        this.moveCameraTo(new Vector3f(0f, 0f, 0f));
    }

    protected void moveCameraTo(Vector3f offset) {
        float[] temp;
        if (this.cameraDirection == Direction.NORTH) {
            temp = new float[]{(this.position[0]) + offset.x(), (this.position[1] - player_camera_offset) + offset.y(), this.position[2] + camera_z_zoom + offset.z()};
        } else if (this.cameraDirection == Direction.SOUTH) {
            temp = new float[]{(this.position[0]) + offset.x(), (this.position[1] + player_camera_offset) + offset.y(), this.position[2] + camera_z_zoom + offset.z()};
        } else if (this.cameraDirection == Direction.WEST) {
            temp = new float[]{(this.position[0] + player_camera_offset) + offset.x(), (this.position[1]) + offset.y(), this.position[2] + camera_z_zoom + offset.z()};
        } else {
            temp = new float[]{(this.position[0] - player_camera_offset) + offset.x(), (this.position[1]) + offset.y(), this.position[2] + camera_z_zoom + offset.z()};
        }
        GameConstants.camera.startEase(temp, 60);
    }

    protected void moveCameraNextTo() {
        float[] temp;
        if (this.cameraDirection == Direction.EAST) {
            temp = new float[]{(this.position[0]), (this.position[1] - player_camera_offset), this.position[2] + camera_z_zoom};
        } else if (this.cameraDirection == Direction.WEST) {
            temp = new float[]{(this.position[0]), (this.position[1] + player_camera_offset), this.position[2] + camera_z_zoom};
        } else if (this.cameraDirection == Direction.NORTH) {
            temp = new float[]{(this.position[0] + player_camera_offset), (this.position[1]), this.position[2] + camera_z_zoom};
        } else {
            temp = new float[]{(this.position[0] - player_camera_offset), (this.position[1]), this.position[2] + camera_z_zoom};
        }
        GameConstants.camera.startEase(temp, 50);
    }

    protected void moveCameraInto(){
        float[] temp;
        float horizontalOffset = 0.7f;
        if(this.cameraDirection == Direction.NORTH){
            temp = new float[]{this.position[0], this.position[1] - horizontalOffset, this.position[2] + camera_z_zoom};
        }else if(this.cameraDirection == Direction.SOUTH){
            temp = new float[]{this.position[0], this.position[1] + horizontalOffset, this.position[2] + camera_z_zoom};
        }else if(this.cameraDirection == Direction.WEST){
            temp = new float[]{this.position[0] + horizontalOffset, this.position[1], this.position[2] + camera_z_zoom};
        }else{
            temp = new float[]{this.position[0] - horizontalOffset, this.position[1], this.position[2] + camera_z_zoom};
        }
        GameConstants.camera.startEase(temp, 50);
    }

    protected void moveCameraTargetTo() {
        this.moveCameraTargetTo(new Vector3f(0f, 0f, 0f));
    }

    protected void moveCameraTargetTo(Vector3f offset) {
        float[] temp = new float[]{
                this.position[0] + offset.x(),
                this.position[1] + offset.y(),
                this.position[2] + offset.z(),
        };
        GameConstants.camera.easeTargetTo(temp, 50);
    }

    protected void moveCameraTargetInFront(){
        float hOffset = 1f;
        Vector3f temp;
        if(this.cameraDirection == Direction.NORTH){
            temp = new Vector3f(0f, hOffset, camera_z_zoom);
        }else if(this.cameraDirection == Direction.SOUTH){
            temp = new Vector3f(0f, -hOffset, camera_z_zoom);
        }else if(this.cameraDirection == Direction.WEST){
            temp = new Vector3f(-hOffset, 0f, camera_z_zoom);
        }else{
            temp = new Vector3f(hOffset, 0f, camera_z_zoom);
        }
        this.moveCameraTargetTo(temp);
    }

    protected Tile3D getFrontTile() {
        if (this.cameraDirection == Direction.NORTH) {
            return GameConstants.tileMap3D.getNorthTile(this.getFixedPos());
        } else if (this.cameraDirection == Direction.SOUTH) {
            return GameConstants.tileMap3D.getSouthTile(this.getFixedPos());
        } else if (this.cameraDirection == Direction.WEST) {
            return GameConstants.tileMap3D.getWestTile(this.getFixedPos());
        } else {
            return GameConstants.tileMap3D.getEastTile(this.getFixedPos());
        }
    }

    protected Tile3D getBackTile() {
        if (this.cameraDirection == Direction.SOUTH) {
            return GameConstants.tileMap3D.getNorthTile(this.getFixedPos());
        } else if (this.cameraDirection == Direction.NORTH) {
            return GameConstants.tileMap3D.getSouthTile(this.getFixedPos());
        } else if (this.cameraDirection == Direction.EAST) {
            return GameConstants.tileMap3D.getWestTile(this.getFixedPos());
        } else {
            return GameConstants.tileMap3D.getEastTile(this.getFixedPos());
        }
    }

    int sCounter = 0;
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
