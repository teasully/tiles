package project.etrumper.thomas.ghostbutton;

/**
 * Created by thoma on 6/13/2016.
 */
public class Weapon extends ChessPiece {

    Animation aReady, aUnready, aReadyIdle, aReadySidestepL, aReadySidestepR;

    Weapon(String weaponName){
        super(weaponName, PieceDirection.DOWN, PieceType.ITEM);
    }

    protected Animation quickAddAnim(String animationName){
        return super.addAnimation(String.format("%s_%s", animationName, this.TAG.toLowerCase()));
    }

}
