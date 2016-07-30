package project.etrumper.thomas.ghostbutton;

import android.util.Log;

/**
 * Created by thoma on 3/30/2016.
 */
public class Tile {

    static final int MAXITEMS = 4, MAXSCENERY = 4;

    int[] position;

    private ChessPiece piece;
    private ChessPiece[] items, scenery;

    private boolean claimed;
    int claimID;

    Tile(){
        this.init();
    }

    private void init(){
        this.piece = null;
        this.items = new ChessPiece[0];
        this.claimed = false;
        this.claimID = -1;
    }

    protected void setPiece(ChessPiece piece){
        this.piece = piece;
        this.claimed = false;
    }

    protected ChessPiece getPiece(){
        return this.piece;
    }

    public ChessPiece[] getItems() {
        return this.items;
    }

    public void addItem(ChessPiece item) {
        // Check to make sure there is enough space in this.items
        if(this.items.length == MAXITEMS){
            LOGE(String.format("Trying to add over MAXITEMS item %s.%d", item.TAG, item.ID));
            return;
        }
        // Scan to see if item already exists in this.items
        boolean found = false;
        for(ChessPiece gItem : this.items){
            if(gItem.ID == item.ID){
                found = true;
            }
        }
        if(found){
            LOGE(String.format("Trying to add duplicate item %s.%dd", item.TAG, item.ID));
            return;
        }
        // Resize array and add new item
        ChessPiece[] newItems = new ChessPiece[this.items.length + 1];
        System.arraycopy(this.items, 0, newItems, 0, this.items.length);
        newItems[this.items.length] = item;
        this.items = newItems;
    }

    protected void removeItem(ChessPiece item){
        // Make sure item exists
        if(item == null){
            LOGE("Trying to remove null item");
            return;
        }
        // Scan through items, checking ID to remove item
        int removed = 0;
        for(int i = 0; i < this.items.length; i++){
            if(this.items[i].ID == item.ID){
                this.items[i] = null;
                removed++;
            }
        }
        if(removed == 0){
            LOGE("Could not find item " + item.TAG);
            return;
        }
        ChessPiece[] newItems = new ChessPiece[this.items.length - removed];
        int newIter = 0;
        for(int i = 0; i < this.items.length; i++){
            if(this.items[i] != null){
                newItems[newIter++] = this.items[i];
            }
        }
        this.items = newItems;
    }

    protected void setPosition(int x, int y){
        position = new int[]{x, y};
    }

    protected boolean claim(int claimID){
        if(claimID == -1){
            this.claimed = false;
            return false;
        }
        if(this.claimed){
            //Log.e("Tile", "Trying to claim a claimed block");
            return false;
        }
        this.claimID = claimID;
        this.claimed = true;
        return true;
    }

    protected boolean canMoveOntoPiece(){
        return (this.getPiece() == null && !this.claimed);
    }

    protected void update(){
        if(this.piece != null){
            this.piece.update();
        }
        for(ChessPiece item : this.items){
            item.update();
        }
    }

    protected void draw(){
        if(this.piece != null) {
            this.piece.draw();
        }
        for(ChessPiece item : this.items){
            item.draw();
        }
    }

    private void LOGE(String message){
        Log.e(String.format("%s.%d.%d", "Tile", this.position[0], this.position[1]), message);
    }
}
