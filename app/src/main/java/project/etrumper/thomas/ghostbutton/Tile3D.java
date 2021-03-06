package project.etrumper.thomas.ghostbutton;

/**
 * Created by thoma on 7/18/2016.
 * Property of boxedworks.
 */
public class Tile3D extends Logable{

    // Holds things in tile
    EntityTile3D[] children;

    Vector3i tilePosition;

    Tile3D(){
        super("Tile3D");
        this.children = new EntityTile3D[0];
    }

    Tile3D(EntityTile3D ... entities){
        super("Tile3D");
        this.children = new EntityTile3D[0];
        this.addEntity(entities);
    }

    public void draw(){
        // Iterate through entities
        for(EntityTile3D entityTile3D : this.children){
            entityTile3D.draw();
        }
    }

    public void update(){
        // Iterate through entities
        for(EntityTile3D entityTile3D : this.children){
            entityTile3D.update();
        }
    }

    public void inputHandler(String input){
        for(EntityTile3D child : this.children){
            child.inputHandler(input);
        }
    }

    public void addEntity(EntityTile3D ... entityTile3Ds){
        // Check params
        if(entityTile3Ds == null || entityTile3Ds.length == 0){
            LOGE("Trying to add null or < 0 len entities");
            return;
        }
        // Loop through params to check isChild
        for(EntityTile3D entityTile3D : entityTile3Ds){
            // Add entity
            this.addEntity(entityTile3D);
        }
    }

    private void addEntity(EntityTile3D entityTile3D){
        // Check null
        if(entityTile3D == null){
            LOGE("Trying to add null entity");
            return;
        }
        // Make sure isn't adding double
        if(this.isChild(entityTile3D)){
            LOGE("Trying to add double entity");
            return;
        }
        // use System.arraycopy()
        EntityTile3D[] temp = new EntityTile3D[this.children.length + 1];
        System.arraycopy(this.children, 0, temp, 0, this.children.length);
        temp[this.children.length] = entityTile3D;
        this.children = temp;
    }

    public boolean removeEntity(EntityTile3D ... entityTile3Ds){
        if(entityTile3Ds == null || entityTile3Ds.length == 0){
            LOGE("Trying to remove null or < 0 len entities");
            return false;
        }
        boolean found = false;
        for(EntityTile3D entityTile3D : entityTile3Ds){
            if(!found) {
                found = this.removeEntity(entityTile3D);
            }else{
                this.removeEntity(entityTile3D);
            }
        }
        return found;
    }

    private boolean removeEntity(EntityTile3D entityTile3D){
        if(entityTile3D == null){
            //LOGE("Trying to remove null entity");
            return false;
        }
        // Cycle through children and remove by ID
        int removed = 0;
        for(int i = 0; i < this.children.length; i++){
            if(this.children[i].ID == entityTile3D.ID){
                this.children[i] = null;
                removed++;
            }
        }
        // Check to see if any removed
        if(removed == 0){
            //LOGE("Removed 0 entities from tile");
            return false;
        }
        EntityTile3D[] temp = new EntityTile3D[this.children.length - removed];
        int tempIT = 0;
        // Move non-null children to new array
        for(EntityTile3D child : this.children){
            if(child == null){
                continue;
            }
            temp[tempIT++] = child;
        }
        this.children = temp;
        return true;
    }

    public boolean hasEntity(){
        return(this.children.length > 0);
    }

    public boolean hasBlock(){
        // Get block and check if canCollide
        Block block = this.getBlock();
        return(block != null && block.canCollide);
    }

    public Block getBlock() {
        // Check for type
        for (EntityTile3D child : this.children) {
            if (child.type == EntityTile3D.EntityType.BLOCK) {
                return (Block) child;
            }
        }
        return null;
    }

    public boolean hasItem(){
        Item item = this.getItem();
        return(item != null);
    }

    public Item getItem(){
        // Check for type
        for (EntityTile3D child : this.children) {
            if (child.type == EntityTile3D.EntityType.ITEM) {
                return (Item) child;
            }
        }
        return null;
    }

    public Avatar3D getPlayer(){
        for (EntityTile3D child : this.children) {
            if (child.TAG.equals("Player")) {
                return (Avatar3D) child;
            }
        }
        return null;
    }

    public boolean hasPlayer(){
        return(this.getPlayer() != null);
    }

    public boolean empty(){
        return (this.children.length == 0);
    }

    public boolean isChild(EntityTile3D entityTile3D){
        // Loop through children
        for(EntityTile3D child : this.children){
            // Check against IDs
            if(child.ID == entityTile3D.ID){
                return true;
            }
        }
        // Did not find match
        return false;
    }

    public boolean isChild(String TAG){
        // Loop through children
        for(EntityTile3D child : this.children){
            // Check against TAGs
            if(child.TAG.equals(TAG)){
                return true;
            }
        }
        // Did not find match
        return false;
    }

    public Tile3D north(){
        return GameConstants.getMap().getNorthTile(this.tilePosition);
    }

    public Tile3D south(){
        return GameConstants.getMap().getSouthTile(this.tilePosition);
    }

    public Tile3D west(){
        return GameConstants.getMap().getWestTile(this.tilePosition);
    }

    public Tile3D east(){
        return GameConstants.getMap().getEastTile(this.tilePosition);
    }

    public Tile3D top(){
        return GameConstants.getMap().getTopTile(this.tilePosition);
    }

    public Tile3D directional(Avatar3D.Direction direction){
        return GameConstants.getMap().getTileByDirection(this.tilePosition, direction);
    }

    public Tile3D bottom(){
        return GameConstants.getMap().getBottomTile(this.tilePosition);
    }

}
