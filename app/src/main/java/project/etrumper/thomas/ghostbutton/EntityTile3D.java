package project.etrumper.thomas.ghostbutton;

/**
 * Created by thoma on 7/18/2016.
 */
public class EntityTile3D extends BasicEntity {

    Vector3i tilePosition;

    int lastInputHandler = -1;

    float heightRadius, widthRadius;

    EntityTile3D(String libraryName, float widthRadius, float heightRadius){
        super(libraryName);

        this.widthRadius = widthRadius;
        this.heightRadius = heightRadius;

        this.rotation = new float[]{270f, 0f, 0f};
    }

    @Override
    protected void update(){
        super.update();
        super.updateMesh();
    }

    protected void moveToPosition(Vector3i position){
        this.setPosition(new float[]{position.x(), position.y(), position.z()});
    }

    protected void convertTilePosToGlobal(){
        this.setPosition(new float[]{this.tilePosition.x(), this.tilePosition.y(), this.tilePosition.z()});
    }

    protected void inputHandler(String input){

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

}
