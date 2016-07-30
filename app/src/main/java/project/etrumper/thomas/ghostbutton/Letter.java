package project.etrumper.thomas.ghostbutton;

/**
 * Created by thoma on 6/14/2016.
 */
public class Letter extends ChessPiece {

    Letter(String libraryObjectName, final String letter){
        super(libraryObjectName, PieceDirection.DOWN, PieceType.STATIONARY);

        // Check if current letter is apace
        if(libraryObjectName == null && letter == null){
            this.TAG = "LETTER.SPACE";
        }else {
            this.setCurrentAnimation(this.addAnimation(letter));
        }
    }

    @Override
    protected void update(){
        // Do not update if is space
        if(this.TAG.equals("LETTER.SPACE")){
            return;
        }
        super.update();
        super.updateMesh();
    }

    @Override
    protected void draw(){
        // Do not draw if is space
        if(this.TAG.equals("LETTER.SPACE")){
            return;
        }
        super.draw();
    }
}
