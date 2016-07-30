package project.etrumper.thomas.ghostbutton;

/**
 * Created by thoma on 6/28/2016.
 */
public class Wave extends Logable{

    private ChessPiece[] mobs;
    
    Wave(int number){
        super("Wave");

        this.mobs = new ChessPiece[0];

        this.generateWave(number);
    }

    private void generateWave(int number){
        // Create new array to hold mobs
        this.mobs = new ChessPiece[number];
        for(int i = 0; i < number; i++){
            this.mobs[i] = new CubeMonster(ChessPiece.PieceDirection.DOWN);
        }
    }

    public ChessPiece getMob(){
        // Make sure isn't empty
        if(this.isEmpty()){
            return null;
        }
        ChessPiece rMob = this.mobs[this.mobs.length - 1];
        // Remove mob from wave
        ChessPiece[] newMobs = new ChessPiece[this.mobs.length - 1];
        System.arraycopy(this.mobs, 0, newMobs, 0, this.mobs.length - 1);
        this.mobs = newMobs;
        return rMob;
    }

    public boolean isEmpty(){
        return (this.mobs.length == 0);
    }

}
