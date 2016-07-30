package project.etrumper.thomas.ghostbutton;

/**
 * Created by thoma on 6/27/2016.
 */
public class Barricade extends ChessPiece {

    Animation aOne, aTwo, aThree, aFour;

    Barricade(int x, int y){
        super("Scenery1", PieceDirection.LEFT, PieceType.ENTITY, x, y);
        // Set to up
        super.ActionLeft();

        this.aOne = addAnimation("barricade1");
        this.aTwo = addAnimation("barricade1_three_fourth");
        this.aThree = addAnimation("barricade1_half");
        this.aFour = addAnimation("barricade1_one_fourth");
        // Set tag to allow other entities to recognize
        this.TAG = "Barricade";
    }

    @Override
    protected void update(){
        super.update();
        // Get barricade health from GameConstants as fraction
        float health = 1f - (float) GameConstants.Barricade_Health / (float) GameConstants.Max_Barricade_Health;
        // Use health to get correct model (to make look broken)
        if(health < 0.25f){
            super.setCurrentAnimation(this.aOne);
        }else if(health < 0.5f){
            super.setCurrentAnimation(this.aTwo);
        }else if(health < 0.75f){
            super.setCurrentAnimation(this.aThree);
        }else{
            super.setCurrentAnimation(this.aFour);
            if(!this.TAG.equals("Trash.Barricade")) {
                this.TAG = "Trash.Barricade";
                this.remove(null);
            }
        }
        super.updateMesh();
    }

    @Override
    protected void remove(ChessPiece removedBy){
        super.remove(removedBy);
        // Add to garbage
        GameConstants.tileMap.addGarbage(this);
    }

}
