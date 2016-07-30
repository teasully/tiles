package project.etrumper.thomas.ghostbutton;

/**
 * Created by thoma on 6/1/2016.
 */
public class Wall extends ChessPiece{

    Wall(int x, int y, String sceneName, String objectName){
        super(sceneName, PieceDirection.UP, PieceType.STATIONARY);
        super.setCurrentAnimation(addAnimation(objectName));
        super.TAG = "Wall";

        this.rotation = new float[]{270f, 0f, 0f};

        this.moveByBoardTo(x, y);
    }

    @Override
    protected void update(){
        super.update();
        this.updateMesh();
    }

}
