package project.etrumper.thomas.ghostbutton;

import android.util.Log;

/**
 * Created by thoma on 7/18/2016.
 */
public class TileMap3D {

    Tile3D[] tiles;

    Vector3i dimensions;

    TileMap3D(String[] ... layers) {
        /*
            Used to load in a level. Loading starts from bottom up.  Null params or len will result in error
         */
        this.tiles = new Tile3D[0];
        if (layers == null || layers.length == 0) {
            LOGE("Trying to parse null or < 0 len layers");
        } else {
            this.dimensions = new Vector3i();
            this.load3DMap(layers);
        }
    }

    public void inputHandler(String input){
        for(Tile3D tile : this.tiles){
            tile.inputHandler(input);
        }
       Controller.inputHandlerID++;
    }

    public void draw(){
        // Iterate through tiles
        for(Tile3D tile3D : this.tiles){
            tile3D.draw();
        }
    }

    public void update(){
        // Iterate through tiles
        for(Tile3D tile3D : this.tiles){
            tile3D.update();
        }
    }

    public Tile3D getTile(Vector3i tilePosition){
        // Check out of scope
        if(tilePosition.x() >= this.dimensions.x()){
            return null;
        }
        if(tilePosition.y() >= this.dimensions.y()){
            return null;
        }
        if(tilePosition.z() >= this.dimensions.z()){
            return null;
        }
        for(Tile3D tile : this.tiles){
            if(Vector3i.equals(tile.tilePosition, tilePosition)){
                return tile;
            }
        }
        return null;
    }

    public Tile3D getTile(EntityTile3D entity){
        // Try and just get tile
        Tile3D temp = this.getTile(entity.tilePosition);
        // Check if resides in tile
        if(temp.isChild(entity)){
           return temp;
        }
        // Else search manually
        for(Tile3D tile3D : this.tiles){
            if(tile3D.isChild(entity)){
                return tile3D;
            }
        }
        return null;
    }

    public Tile3D getTileByDirection(Vector3i tilePos, Avatar3D.Direction direction){
        if(direction == Avatar3D.Direction.NORTH){
            return this.getNorthTile(tilePos);
        }else if(direction == Avatar3D.Direction.SOUTH){
            return this.getSouthTile(tilePos);
        }else if(direction == Avatar3D.Direction.WEST){
            return this.getWestTile(tilePos);
        }else{
            return this.getEastTile(tilePos);
        }
    }

    public Tile3D[] get2DSurrounding(Vector3i tilePos) {
        return new Tile3D[]{
                this.getNorthTile(tilePos),
                this.getSouthTile(tilePos),
                this.getWestTile(tilePos),
                this.getEastTile(tilePos)
        };
    }

    public Tile3D getTopTile(Vector3i tilePos){
        return this.getTile(new Vector3i(tilePos.x(), tilePos.y(), tilePos.z() + 1));
    }

    public Tile3D getFirstTopTile(Vector3i tilePos){
        return this.getNearestTile(tilePos, new Vector3i(0, 0, 1));
    }

    public Tile3D getBottomTile(Vector3i tilePos){
        return this.getTile(new Vector3i(tilePos.x(), tilePos.y(), tilePos.z() - 1));
    }

    public Tile3D getFirstBottomTile(Vector3i tilePos){
        return this.getNearestTile(tilePos, new Vector3i(0, 0, -1));
    }

    public Tile3D getNorthTile(Vector3i tilePos){
        return this.getTile(new Vector3i(tilePos.x(), tilePos.y() + 1, tilePos.z()));
    }

    public Tile3D getFirstNorthTile(Vector3i tilePos){
        return this.getNearestTile(tilePos, new Vector3i(0, 1, 0));
    }

    public Tile3D getSouthTile(Vector3i tilePos){
        return this.getTile(new Vector3i(tilePos.x(), tilePos.y() - 1, tilePos.z()));
    }

    public Tile3D getFirstSouthTile(Vector3i tilePos){
        return this.getNearestTile(tilePos, new Vector3i(0, -1, 0));
    }

    public Tile3D getWestTile(Vector3i tilePos){
        return this.getTile(new Vector3i(tilePos.x() - 1, tilePos.y(), tilePos.z()));
    }

    public Tile3D getWestNorthTile(Vector3i tilePos){
        return this.getNearestTile(tilePos, new Vector3i(-1, 0, 0));
    }

    public Tile3D getEastTile(Vector3i tilePos){
        return this.getTile(new Vector3i(tilePos.x() + 1, tilePos.y(), tilePos.z()));
    }

    public Tile3D getFirstEastTile(Vector3i tilePos){
        return this.getNearestTile(tilePos, new Vector3i(1, 0, 0));
    }

    public Tile3D getNearestTile(Vector3i startPosition, Vector3i offset){
        int x = startPosition.x(),
                y = startPosition.y(),
                z = startPosition.z();
        // Loop until a return is found
        Tile3D temp = null;
        while(temp == null){
            // Increment by offset
            x += offset.x();
            y += offset.y();
            z += offset.z();
            // Get tile based off of new increment
            temp = this.getTile(new Vector3i(x, y, z));
            // If tile is null, reached end of map
            if(temp == null){
                return null;
            }
            // Only return if has something in it
            if(temp.children.length == 0){
                temp = null;
            }
        }
        return temp;
    }

    private void load3DMap(String[] ... layers) {
        // Record z dimension
        this.dimensions.z(layers.length);
        for (int i = 0; i < layers.length; i++) {
            // Parse layer
            Tile3D[] tiles = this.parseLayer(i, layers[i]);
            // Add tiles
            this.addTiles(tiles);
        }
    }

    private Tile3D[] parseLayer(int currentZ, String ... layer) {
        // Check params
        if (layer == null || layer.length == 0) {
            LOGE("Trying to parseLayer with null or < 0 len");
            return null;
        }
        // Record y dimension
        this.dimensions.y(layer.length);
        // Initiate return array
        Tile3D[] tiles = new Tile3D[0];
        // Iterate through params
        int y = 0;
        for (String row : layer) {
            // Get tiles
            Tile3D[] gotTiles = this.parseRow(row, y++, currentZ);
            // Check return
            if (gotTiles == null) {
                LOGE("Got null for this.parseRow(row);");
                continue;
            }
            // Add to returns
            Tile3D[] temp = new Tile3D[tiles.length + gotTiles.length];
            System.arraycopy(tiles, 0, temp, 0, tiles.length);
            System.arraycopy(gotTiles, 0, temp, tiles.length, gotTiles.length);
            tiles = temp;
        }
        // Return tiles
        return tiles;
    }

    private Tile3D[] parseRow(String row, int currentY, int currentZ) {
        // Check parameters
        if (row == null || row.length() == 0) {
            LOGE("Trying to load null or < 0 len row");
            return null;
        }
        // Initiate return array
        Tile3D[] tiles = new Tile3D[0];
        // Separate IDs
        String[] IDS = row.split(" ");
        // Record x dimension
        this.dimensions.x(IDS.length);
        // Cycle through IDs
        int x = 0;
        for (String id : IDS) {
            // Pass ID to ObjectManager
            EntityTile3D entityTile3D = ObjectManager.getObject(Integer.parseInt(id), x, currentY, currentZ);
            // Create tile and add object
            Tile3D tile;
            if(entityTile3D!= null){
                tile = new Tile3D(entityTile3D);
            }else {
                tile = new Tile3D();
            }
            tile.tilePosition = new Vector3i(x++, currentY, currentZ);
            // Expand tiles and add with arraycopy()
            Tile3D[] temp = new Tile3D[tiles.length + 1];
            System.arraycopy(tiles, 0, temp, 0, tiles.length);
            temp[tiles.length] = tile;
            tiles = temp;
        }
        // Check null
        if (tiles.length == 0) {
            LOGE("Parsed null row");
            return null;
        }
        // Return row
        return tiles;
    }

    public boolean hardRemove(EntityTile3D entityTile3D){
        boolean removed = false;
        for(Tile3D tile3D : this.tiles){
            if(removed){
                tile3D.removeEntity(entityTile3D);
            }else{
                removed = tile3D.removeEntity(entityTile3D);
            }
        }
        return removed;
    }

    private void addTiles(Tile3D ... tiles) {
        // Add params to current tiles
        Tile3D[] temp = new Tile3D[this.tiles.length + tiles.length];
        System.arraycopy(this.tiles, 0, temp, 0, this.tiles.length);
        System.arraycopy(tiles, 0, temp, this.tiles.length, tiles.length);
        this.tiles = temp;
    }

    private void LOGE(String message) {
        Log.e("TileMap3D", message);
    }

}
