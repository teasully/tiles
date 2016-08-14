package project.etrumper.thomas.ghostbutton;

/**
 * Created by thoma on 8/12/2016.
 * Property of boxedworks.
 */

public class Item_End extends Item{

    Item_End(int ID){
        super("CubeMonster", ID);
        this.setCurrentAnimation(this.addAnimation("idle"));
        // Set TAG for identification
        this.TAG = "End";
    }

}
