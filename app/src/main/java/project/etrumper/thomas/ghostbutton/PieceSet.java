package project.etrumper.thomas.ghostbutton;

/**
 * Created by thoma on 4/25/2016.
 */
public class PieceSet {

    ChessPiece[] pieces;

    PieceSet(ChessPiece ... pieces){
        this.pieces = pieces;
    }

    public void moveForward(){
        for(ChessPiece piece : this.pieces){
            piece.ActionUp();
        }
    }

    public void turnCW(){
        for(ChessPiece piece : this.pieces){
            piece.ActionLeft();
        }
    }

    public void play(){
        for(ChessPiece piece : this.pieces){
            piece.currentAnimation.play();
        }
    }

    public void addPiece(ChessPiece piece){
        ChessPiece[] newPieces = new ChessPiece[this.pieces.length + 1];
        System.arraycopy(this.pieces, 0, newPieces, 0, this.pieces.length);
        newPieces[newPieces.length - 1] = piece;
        this.pieces = newPieces;
    }
}
