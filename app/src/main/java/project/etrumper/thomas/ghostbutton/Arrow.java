package project.etrumper.thomas.ghostbutton;

/**
 * Created by thoma on 6/9/2016.
 */
public class Arrow extends ChessPiece {

    private String materialName;

    Animation aDrop;

    Arrow(String libraryName, String materialName, int x, int y){
        super(libraryName, PieceDirection.DOWN, PieceType.ITEM);

        this.tilePos = new int[]{x, y};
        this.position = GameConstants.tileMap.getGlobalPosition(x, y);

        this.materialName = materialName;

        super.aIdle = super.addAnimation("bolt_idle");
        this.aDrop = super.addAnimation("bolt_drop");

        super.currentAnimation = this.aDrop;
        super.play();
    }

    @Override
    protected void update(){
        super.update();
        if(this.canMove()){
            if(this.currentAnimation == this.aDrop){
                this.currentAnimation.play();
            }
            this.idle();
        }

        this.updateMesh();
    }

    @Override
    protected void draw(){
        MaterialManager.saveMaterial(materialName);
        MaterialManager.changeMaterialColor(materialName, GameConstants.player.weaponColor);
        super.draw();
        MaterialManager.restoreSavedMaterial();
    }

}
