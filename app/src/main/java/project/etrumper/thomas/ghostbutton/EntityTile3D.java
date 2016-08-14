package project.etrumper.thomas.ghostbutton;

/**
 * Created by thoma on 7/18/2016.
 */
public class EntityTile3D extends BasicEntity {

    enum Direction {
        NORTH,
        SOUTH,
        EAST,
        WEST
    }

    enum EntityType{
        ENTITY,
        BLOCK,
        ITEM
    }

    boolean placedByUser;

    private static EntityTile3D DirectionalIndicator = new EntityTile3D("Scenery1"){
        // Draw color
        Vector3f color = new Vector3f(0f, 1f, 0f);
        @Override
        protected void draw(){
            // Init function
            if(DirectionalIndicator.currentAnimation == null) {
                DirectionalIndicator.scale[0] = -0.3f;
                DirectionalIndicator.scale[2] = -0.3f;
                DirectionalIndicator.setCurrentAnimation(DirectionalIndicator.addAnimation("right_arrow"));
                DirectionalIndicator.update();
            }
            MaterialManager.changeMaterialColor("Arrow", color);
            super.draw();
        }
    };

    Vector3i tilePosition;

    int lastInputHandler = -1,
        ID;

    float heightRadius, widthRadius;

    boolean disabled,
        playerPlaced,
        directional;

    Direction spawnDirection, entityDirection;
    EntityType type;

    Vector3i spawnLocation;

    EntityTile3D(String libraryName){
        super(libraryName);

        init(null, -1, 0f, 0f);
    }

    EntityTile3D(String libraryName, EntityType type){
        super(libraryName);

        init(type, -1, 0f, 0f);
    }

    EntityTile3D(String libraryName, EntityType type, int ID){
        super(libraryName);

        init(type, ID, 0f, 0f);
    }

    EntityTile3D(String libraryName, EntityType type, int ID, float widthRadius, float heightRadius){
        super(libraryName);

        this.init(type, ID, widthRadius, heightRadius);
    }

    private void init(EntityType type, int ID, float widthRadius, float heightRadius){
        this.type = type;
        this.ID = ID;

        this.widthRadius = widthRadius;
        this.heightRadius = heightRadius;

        this.rotation = new float[]{270f, 0f, 0f};

        this.disabled = false;
        this.playerPlaced = false;
        this.directional = false;
    }

    @Override
    protected void update(){
        super.update();
        super.updateMesh();
    }

    protected void moveToPosition(Vector3i position){
        this.position = (new float[]{position.x(), position.y(), position.z()});
    }

    protected void convertTilePosToGlobal(){
        this.position = (new float[]{this.tilePosition.x(), this.tilePosition.y(), this.tilePosition.z()});
    }

    protected void inputHandler(String input){

    }

    protected void drawDirection(){
        // Check if is directional
        if (this.directional) {
            // Draw arrow over entity telling what direction it is in
            float[] newPos = new float[]{this.position[0], this.position[1], this.position[2]};
            //float offset = 0.4f;
            if (this.entityDirection == EntityTile3D.Direction.NORTH) {
                //newPos[1] += offset;
                DirectionalIndicator.rotation[1] = 270f;
            } else if (this.entityDirection == EntityTile3D.Direction.SOUTH) {
                //newPos[1] -= offset;
                DirectionalIndicator.rotation[1] = 90f;
            } else if (this.entityDirection == EntityTile3D.Direction.WEST) {
                //newPos[0] -= offset;
                DirectionalIndicator.rotation[1] = 180f;
            } else {
                //newPos[0] += offset;
                DirectionalIndicator.rotation[1] = 0f;
            }
            newPos[2] += 0.5f;
            DirectionalIndicator.position = newPos;
            DirectionalIndicator.draw();
        }
    }

    protected boolean isFreshInput(){
        // Makes sure the object isn't being updated twice
        if(this.lastInputHandler != Controller.inputHandlerID){
            this.lastInputHandler = Controller.inputHandlerID;
            return true;
        }
        return false;
    }

    @Override
    protected void pointCameraTo(){
        GameConstants.camera.target = new float[]{
                this.tilePosition.x(),
                this.tilePosition.y(),
                this.tilePosition.z()
        };
    }

    protected void setDirection(Direction direction){

    }

    protected void resolveRoatationTo(Direction direction){
        if(this.entityDirection == Direction.NORTH){
            if(direction == Direction.EAST){
                this.turnCW_Z();
            }else if(direction == Direction.SOUTH){
                this.turnCW_Z();
                this.turnCW_Z();
            }else if(direction == Direction.WEST){
                this.turnCCW_Z();
            }
        }else if(this.entityDirection == Direction.SOUTH){
            if(direction == Direction.WEST){
                this.turnCW_Z();
            }else if(direction == Direction.NORTH){
                this.turnCW_Z();
                this.turnCW_Z();
            }else if(direction == Direction.EAST){
                this.turnCCW_Z();
            }
        }else if(this.entityDirection == Direction.WEST){
            if(direction == Direction.NORTH){
                this.turnCW_Z();
            }else if(direction == Direction.EAST){
                this.turnCW_Z();
                this.turnCW_Z();
            }else if(direction == Direction.SOUTH){
                this.turnCCW_Z();
            }
        }else{
            if(direction == Direction.SOUTH){
                this.turnCW_Z();
            }else if(direction == Direction.WEST){
                this.turnCW_Z();
                this.turnCW_Z();
            }else if(direction == Direction.NORTH){
                this.turnCCW_Z();
            }
        }
    }

    protected Direction turnCW_Z() {
        Direction temp;
        if (this.entityDirection == Direction.NORTH) {
            temp = Direction.EAST;
        } else if (this.entityDirection == Direction.EAST) {
            temp = Direction.SOUTH;
        } else if (this.entityDirection == Direction.SOUTH) {
            temp = Direction.WEST;
        } else {
            temp = Direction.NORTH;
        }
        this.entityDirection = temp;
        return temp;
    }

    protected Direction turnCCW_Z() {
        Direction temp;
        if (this.entityDirection == Direction.NORTH) {
            temp = Direction.WEST;
        } else if (this.entityDirection == Direction.WEST) {
            temp = Direction.SOUTH;
        } else if (this.entityDirection == Direction.SOUTH) {
            temp = Direction.EAST;
        } else {
            temp = Direction.NORTH;
        }
        this.entityDirection = temp;
        return temp;
    }

}
