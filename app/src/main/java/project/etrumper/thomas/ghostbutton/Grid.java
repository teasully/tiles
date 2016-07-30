package project.etrumper.thomas.ghostbutton;

/**
 * Created by thoma on 3/29/2016.
 */
public class Grid extends ChessPiece{

    //int gridID;

    Grid(String libraryName, String modelName){
        super(libraryName, PieceDirection.DOWN, PieceType.STATIONARY);

        this.currentAnimation = addAnimation(modelName);

        rotation = new float[]{-90f, 90f, 0f};
    }

    @Override
    protected void update(){
        super.update();
        this.updateMesh();
    }

}
