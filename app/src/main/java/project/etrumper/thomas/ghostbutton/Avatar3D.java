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

    Ease2 movementEaseX, movementEaseY; // // TODO: 7/23/2016 add horizontal velocity

    GravityAxis jump;

    Direction cameraDirection, avatarDirection;

    Sound tick;

    boolean outOfMap = false;

    Avatar3D(Vector3i position) {
        super("CubeMonster", 0.45f, 0.3f);

        super.currentAnimation = addAnimation("idle");

        this.cameraDirection = Direction.NORTH;
        this.avatarDirection = Direction.NORTH;

        this.tilePosition = position;
        // Fix super() constructor rotation
        this.rotation = new float[]{0f, 0f, 0f};
        // Merge tilePos and pos for camera
        this.convertTilePosToGlobal();
        // Set camera to position
        this.moveCameraTo();
        this.moveCameraTargetTo();
        // Initiate tweens as null
        this.movementEaseX = null;
        this.movementEaseY = null;
        // Initiate gravity object as null
        this.jump = null;
        // Load sound(s)
        this.tick = new Sound(R.raw.tick1);
        // Set speeds
        this.deltaTimeModifier = 2.f;
        GravityAxis.dtModifier = this.deltaTimeModifier;
    }

    // Object constants
    static float player_camera_offset = 2f,
            camera_z_zoom = 5f;
    static long movement_speed = 1200;

    @Override
    protected void update() {
        super.update();
        // Check to see if movement ease
        if (this.movementEaseX != null) {
            this.movementEaseX.update();
            this.position[0] = this.movementEaseX.easeQuadradic();
            // Check if finished
            if (this.movementEaseX.done()) {
                this.movementEaseX = null;
            }
        }
        if (this.movementEaseY != null) {
            this.movementEaseY.update();
            this.position[1] = this.movementEaseY.easeQuadradic();
            // Check if finished
            if (this.movementEaseY.done()) {
                this.movementEaseY = null;
            }
        }
        // Check jump
        if (this.jump != null) {
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
                float tileZ = -3f; // Set default as pit in case empty space is found
                // Check if out of map
                if(!this.outOfMap){
                    // Check if done jumping
                    Tile3D belowTile = GameConstants.tileMap3D.getTile(this.getFixedPos());
                    // Check if out of map or if there is no solid
                    if (belowTile != null && !belowTile.hasSolid()) {
                        // Check if block below is solid
                        belowTile = GameConstants.tileMap3D.getFirstBottomTile(this.getFixedPos());
                        if (belowTile == null) {
                            belowTile = null;
                            //LOGE("No bottom tile found");
                        }
                    }
                    if (belowTile != null) {
                        tileZ = belowTile.position.z() + (this.heightRadius + belowTile.children[0].heightRadius); // 1f to account for offset (tiles are one block lower)
                    }
                }
                float charZ = this.position[2];
                if (charZ <= tileZ) {
                    this.rotation[0] = 0f;
                    this.rotation[1] = 0f;
                    // Set final position
                    this.position[2] = tileZ;
                    // Remove jump
                    this.jump = null;
                    // Play sound
                    this.tick.play();
                }
                //LOGE(charZ + " <=--char:|:tile--=> " + tileZ);
            }
        }
        // Update tilemap position
        if (this.movementEaseX != null || this.movementEaseY != null || this.jump != null) {
            this.updateMap();
        }
        // Adjust camera
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
        // Check to make sure the position has changed
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
            LOGE("Entity out of map");
            this.outOfMap = true;
            return;
        }
        // Remove from map
        if (!GameConstants.tileMap3D.getTile(this.tilePosition).removeEntity(this)) {
            if(!GameConstants.tileMap3D.hardRemove(this))
                return;
        }
        // Update tile position
        this.tilePosition = tilePos;
        // Place back into map with new position
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

    private void highJump(){
        // Check block in front to check if can jump forward
        if(!canHighJump()){
            return;
        }
        // Start the movement ease
        this.moveForward(1);
        // Jump
        this.jump = new GravityAxis(this.position[2], 5.2f, 10000);
    }

    private void longJump(){
        // Check no block in front
        if(!canLongJump()){
            return;
        }
        // Start the movement ease
        this.moveForward(2);
        // Jump
        this.jump = new GravityAxis(this.position[2], 5.2f, 10000);
    }

    private boolean canHighJump(){
        // Make sure isn't out of map
        if(this.outOfMap){
            return false;
        }
        // Make sure isn't jumping
        if(this.isJumping()){
            return false;
        }
        Tile3D tile = this.getFrontTile();
        // Check for null tile; tile does not exist, reached edge of map
        if (tile == null) {
            LOGE("Moving out of map");
            return true;
        }
        /*/ Check if is too high
        tile = GameConstants.tileMap3D.getTopTile(tile.position);
        if (tile == null) {
            LOGE("Trying to jump higher than ceiling");
            return false;
        }*/
        if (tile.hasSolid()) {
            LOGE("Too tall to jump");
            return false;
        }
        return true;
    }

    private boolean canLongJump(){
        // Make sure isn't out of map
        if(this.outOfMap){
            return false;
        }
        // Make sure isn't jumping
        if(this.isJumping()){
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
        if(this.cameraDirection == Direction.NORTH) {
            tile = GameConstants.tileMap3D.getNorthTile(tile.position);
        }else if(this.cameraDirection == Direction.SOUTH){
            tile = GameConstants.tileMap3D.getSouthTile(tile.position);
        }else if(this.cameraDirection == Direction.WEST){
            tile = GameConstants.tileMap3D.getWestTile(tile.position);
        }else{
            tile = GameConstants.tileMap3D.getEastTile(tile.position);
        }
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
        GameConstants.camera.startEase(temp, 50);
    }

    protected void moveCameraNextTo() {
        float[] temp;
        if (this.cameraDirection == Direction.EAST) {
            temp = new float[]{(this.position[0]), (this.position[1] - player_camera_offset), camera_z_zoom};
        } else if (this.cameraDirection == Direction.WEST) {
            temp = new float[]{(this.position[0]), (this.position[1] + player_camera_offset), camera_z_zoom};
        } else if (this.cameraDirection == Direction.NORTH) {
            temp = new float[]{(this.position[0] + player_camera_offset), (this.position[1]), camera_z_zoom};
        } else {
            temp = new float[]{(this.position[0] - player_camera_offset), (this.position[1]), camera_z_zoom};
        }
        GameConstants.camera.startEase(temp, 50);
    }

    protected void moveCameraInto(){
        float[] temp;
        float horizontalOffset = 0.5f;
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

}
