package project.etrumper.thomas.ghostbutton;

import android.util.Log;

/**
 * Created by thoma on 3/30/2016.
 */
public class TileMap {

    Tile[][] map;

    Wave currentWave;

    ChessPiece[] garbage; // Holds bodies, particles, whatever
    int garbageIt;
    final static int MAX_GARBAGE = 40;

    int[] offset;

    float tileWidth;    // Distance between tiles in the global world.. usually gotten from 3D software etc. Blender
    int w = 0, h = 0;

    String tag = "TileMap";
    String[] rawMap;

    Grid grid;

    TileMap(String[] rawMap, float tileWidth, int xOffset, int yOffset) {
        // Load in the raw data
        this.loadRawMap(rawMap);
        // Load the map tiles
        this.loadTiles();
        // Set local variables
        this.tileWidth = tileWidth;
        this.offset = new int[]{xOffset, yOffset};
    }

    protected void loadRawMap(String[] rawMap){
        this.rawMap = rawMap;
        // Flip raw map so loaded in correct orientation (x and y flipped)
        String[] newMap = new String[rawMap.length];
        this.w = 0;
        this.h = 0;
        for (int y = rawMap.length - 1; y >= 0; y--, this.h++) {
            String[] line = rawMap[y].split(" ");
            String newLine = "";
            for (int x = line.length - 1; x >= 0; x--) {
                String data = line[x];
                newLine = newLine.concat(data + (x == 0 ? "" : " "));
                if(this.h == 0){
                    this.w++;
                }
            }
            newMap[this.h] = newLine;
        }
        this.rawMap = newMap;
    }

    protected void loadTiles(){
        // Load and initiate tiles
        this.map = new Tile[h][w];
        for (int y = 0; y < this.map.length; y++) {
            for (int x = 0; x < this.map[y].length; x++) {
                this.map[y][x] = new Tile();
                this.map[y][x].setPosition(x, y);
            }
        }
    }

    protected void loadElements() {
        // Check to make sure Tiles are loaded
        if(this.map == null){
            this.loadTiles();
        }
        // Loop through rawMap data
        for (int y = 0; y < h; y++) {
            // Get column data
            String[] line = rawMap[y].split(" ");
            for (int x = 0; x < w; x++) {
                // Parse data into ObjectManager ID
                String data = line[x];
                int ID = Integer.parseInt(data);
                ChessPiece gotPiece = ObjectManager.getObject(ID, x, y);
                // Make sure returned piece isn't setting to null
                if(gotPiece != null) {
                    this.map[y][x].setPiece(gotPiece);
                }
            }
        }
        LightManager.getLights()[0].position = GameConstants.player.position;
        // Load garbage
        this.garbage = new ChessPiece[0];
        this.garbageIt = 0;
        // Load the floor
        this.grid = new Grid("Scenery1", "island");
        // Center floor
        int[] pos = this.map[this.map.length / 2][this.map[0].length / 2].position;
        this.grid.position = this.getGlobalPosition(pos);
        // Scale floor
        this.grid.scale[0] = 1.3f;
        this.grid.scale[1] = 0.5f;
        // Load first wave
        this.currentWave = new Wave(50);
    }

    protected void unloadElements(){
        // Clean up
        this.map = null;
        this.garbage = null;
        this.grid = null;
        // Remove static player reference
        GameConstants.player = null;
    }

    protected void resetMap(){
        GameConstants.frameRate = 60;
        this.loadTiles();
        this.loadElements();
    }

    protected int[] getTilePosition(float ... position) {
        return new int[]{(int) (-position[0] / this.tileWidth) - this.offset[0], (int) (-position[1] / this.tileWidth) - this.offset[1]};
    }

    protected float[] getGlobalPosition(int ... tilePos){
        return new float[]{ (tilePos[0] + this.offset[0]) * this.tileWidth, (tilePos[1] + this.offset[1]) * this.tileWidth, GameConstants.zDepth};
    }

    protected void addPiece(ChessPiece piece) {
        int[] pos = piece.tilePos;
        int x = pos[0],
                y = pos[1];
        if (this.map[y][x].getPiece() == null) {
            this.map[y][x].setPiece(piece);
            //return;
        }
        //Log.e("Tile", String.format("Trying to overwrite piece %s with %s at %d, %d", this.map[y][x].getPiece().TAG, piece.TAG, x, y));
    }

    protected boolean isEmptyPiece(int[] tilePos) {
        Tile tile = this.map[tilePos[1]][tilePos[0]];
        return (tile.getPiece() == null);
    }

    protected int getSpaceItems(int[] tilePos) {
        Tile tile = this.map[tilePos[1]][tilePos[0]];
        return (Tile.MAXITEMS - tile.getItems().length);
    }

    protected Tile[] getSurroundingTilesSquare(int[] tilePos){
        Tile[] returnTiles = new Tile[9];
        // Place middle tile
        returnTiles[0] = this.map[tilePos[1]][tilePos[0]];
        // Get surrounding tiles
        returnTiles[1] = this.getNorthTile(tilePos[0], tilePos[1]);
        returnTiles[2] = this.getSouthTile(tilePos[0], tilePos[1]);
        returnTiles[3] = this.getWestTile(tilePos[0], tilePos[1]);
        returnTiles[4] = this.getEastTile(tilePos[0], tilePos[1]);
        // Get left and right of top and bottom tile
        returnTiles[5] = this.getWestTile(tilePos[0], tilePos[1] + 1);
        returnTiles[6] = this.getEastTile(tilePos[0], tilePos[1] + 1);
        returnTiles[7] = this.getWestTile(tilePos[0], tilePos[1] - 1);
        returnTiles[8] = this.getEastTile(tilePos[0], tilePos[1] - 1);
        return returnTiles;
    }

    protected void addItem(ChessPiece item) {
        int[] pos = item.tilePos;
        int x = pos[0],
                y = pos[1];
        map[y][x].addItem(item);
        //Log.e("Tile", String.format("Trying to overwrite item %s with %s at %d, %d", this.map[y][x].getItem().TAG, item.TAG, x, y));
    }

    protected boolean removePiece(ChessPiece piece) {
        Tile tile = this.map[piece.tilePos[1]][piece.tilePos[0]];
        boolean hardSearch = false;
        if (tile.getPiece() == null) {
            //LOGE(String.format("%s.%d: Trying to remove null piece at location %d, %d", piece.TAG, piece.ID, piece.tilePos[0], piece.tilePos[1]));
            hardSearch = true;
        }else if (tile.getPiece().ID != piece.ID) {
            //LOGE(String.format("%s.%d: Trying to remove piece %s.%d location %d, %d", piece.TAG, piece.ID, tile.getPiece().TAG, tile.getPiece().ID, piece.tilePos[0], piece.tilePos[1]));
            hardSearch = true;
        }
        int ID = piece.ID;
        if(!hardSearch) {
            tile.setPiece(null);
            // Make sure piece had not claimed a tile and not declaimed
            for (int y = 0; y < this.map.length; y++) {
                for (int x = 0; x < this.map[y].length; x++) {
                    if (this.map[y][x].claimID == ID) {
                        // Pass -1 to tell tile to declaim
                        this.map[y][x].claim(-1);
                    }
                }
            }
            return true;
        }
        // Make sure piece had not claimed a tile and not declaimed, and mostly find other piece by searching all tiles
        boolean found = false;
        for (int y = 0; y < this.map.length; y++) {
            for (int x = 0; x < this.map[y].length; x++) {
                if(this.map[y][x].getPiece() != null && this.map[y][x].getPiece().ID == ID){
                    this.map[y][x].setPiece(null);
                    this.map[y][x].claim(-1);
                    found = true;
                }
            }
        }
        return found;
    }

    protected void addGarbage(ChessPiece piece){
        // Find piece in map and remove
        this.removePiece(piece);
        // Check if full
        if(this.garbageIt >= MAX_GARBAGE){
            // Use modulo to add over
            this.garbage[garbageIt++ % MAX_GARBAGE] = piece;
            return;
        }
        ChessPiece[] newGarbage = new ChessPiece[this.garbage.length + 1];
        System.arraycopy(this.garbage, 0, newGarbage, 0, garbageIt);
        newGarbage[garbageIt++] = piece;
        this.garbage = newGarbage;
    }

    protected void removeItem(ChessPiece item) {
        Tile tile = this.map[item.tilePos[1]][item.tilePos[0]];
        tile.removeItem(item);
    }

    public Tile getNorthTile(int x, int y){
        if (y >= this.map.length - 1) {
            return null;
        }
        return this.map[y + 1][x];
    }

    public Tile getSouthTile(int x, int y) {
        if (y <= 0) {
            return null;
        }
        return this.map[y - 1][x];
    }

    public Tile getEastTile(int x, int y) {
        if (x <= 0) {
            return null;
        }
        return this.map[y][x - 1];
    }

    public Tile getWestTile(int x, int y) {
        if (x >= map[0].length - 1) {
            return null;
        }
        return this.map[y][x + 1];
    }

    protected Tile[] getSurroundingTiles(int x, int y) { // L R U D
        //LOGE("Checking " + x + ": " + y);
        Tile[] tiles = new Tile[4];
        tiles[0] = this.getWestTile(x, y);
        tiles[1] = this.getEastTile(x, y);
        tiles[2] = this.getNorthTile(x, y);
        tiles[3] = this.getSouthTile(x, y);
        return tiles;
    }

    protected Tile[] getSurroundingTiles(int ... position) {
        return getSurroundingTiles(position[0], position[1]);
    }

    protected ChessPiece getAcrossPiece(ChessPiece piece){
        return getAcrossPiece(piece, 0);
    }

    protected ChessPiece getAcrossPiece(ChessPiece piece, int maxDistance){
        // Fix maxDistance if 0
        if(maxDistance == 0){
            maxDistance = Integer.MAX_VALUE;
        }
        // Set local variables
        ChessPiece returnPiece = null;
        int[] currentPos = new int[]{piece.tilePos[0], piece.tilePos[1]};
        Tile currentTile;
        // Loop until piece found or maxDistance reached
        while(maxDistance-- > 0){
            if(piece.direction == ChessPiece.PieceDirection.UP){
                currentTile = this.getNorthTile(currentPos[0], currentPos[1]);
            }else if(piece.direction == ChessPiece.PieceDirection.DOWN){
                currentTile = this.getSouthTile(currentPos[0], currentPos[1]);
            }else if(piece.direction == ChessPiece.PieceDirection.LEFT){
                currentTile = this.getWestTile(currentPos[0], currentPos[1]);
            }else{
                currentTile = this.getEastTile(currentPos[0], currentPos[1]);
            }
            if(currentTile == null){
                break;
            }
            returnPiece = currentTile.getPiece();
            if(returnPiece != null){
                break;
            }
            currentPos = currentTile.position;
        }
        return returnPiece;
    }

    protected ChessPiece[] getAcrossPieces(ChessPiece piece){
        ChessPiece[] returnPieces = new ChessPiece[0];
        int[] currentPos = new int[]{piece.tilePos[0], piece.tilePos[1]};
        Tile currentTile;
        while(true){
            // Get tile based on piece cameraDirection
            if(piece.direction == ChessPiece.PieceDirection.UP){
                currentTile = this.getNorthTile(currentPos[0], currentPos[1]);
            }else if(piece.direction == ChessPiece.PieceDirection.DOWN){
                currentTile = this.getSouthTile(currentPos[0], currentPos[1]);
            }else if(piece.direction == ChessPiece.PieceDirection.LEFT){
                currentTile = this.getWestTile(currentPos[0], currentPos[1]);
            }else{
                currentTile = this.getEastTile(currentPos[0], currentPos[1]);
            }
            // Check for edge of map
            if(currentTile == null){
                break;
            }
            // check current tile's piece
            ChessPiece returnPiece = currentTile.getPiece();
            if(returnPiece != null){
                // Add piece and continue
                ChessPiece[] newPieces = new ChessPiece[returnPieces.length + 1];
                System.arraycopy(returnPieces, 0, newPieces, 0, returnPieces.length);
                newPieces[returnPieces.length] = returnPiece;
                returnPieces = newPieces;
            }
            // Set current tile position as to continue
            currentPos = currentTile.position;
        }
        if(returnPieces.length == 0){
            return null;
        }
        return returnPieces;
    }

    protected void changeFramerate(long newFrameRate) {
        // Loop through map
        for (Tile[] tiles : this.map) {
            for (Tile tile : tiles) {
                // Get piece
                if (tile.getPiece() != null) {
                    for (Animation animation : tile.getPiece().animations) {
                        animation.changeFrameRate(newFrameRate);
                    }
                    // Handle children
                    ChessPiece[] children = tile.getPiece().getAllChildren();
                    if (children != null) {
                        for (ChessPiece child : children) {
                            for (Animation animation : child.animations) {
                                animation.changeFrameRate(newFrameRate);
                            }
                        }
                    }
                }
                // Get items
                if (tile.getItems() != null && tile.getItems().length != 0) {
                    for (ChessPiece item : tile.getItems()) {
                        for (Animation animation : item.animations) {
                            animation.changeFrameRate(newFrameRate);
                        }
                    }
                }
            }
        }
        // Loop through garbage
        for (ChessPiece piece : this.garbage) {
            for (Animation animation : piece.animations) {
                animation.changeFrameRate(newFrameRate);
            }
            // Handle children
            ChessPiece[] children = piece.getAllChildren();
            if (children != null) {
                for (ChessPiece child : children) {
                    for (Animation animation : child.animations) {
                        animation.changeFrameRate(newFrameRate);
                    }
                }
            }
        }
    }

    protected void update() {
        // Update tiles
        for (int i = 0; i < map.length; i++) {
            for (int u = 0; u < map[i].length; u++) {
                map[i][u].update();
            }
        }
        // Update garbage
        if(this.garbage != null) {
            for (ChessPiece piece : this.garbage) {
                piece.update();
            }
        }
        // Update grid
        if(this.grid != null) {
            this.grid.update();
        }
    }

    protected void draw() {
        for (int i = 0; i < map.length; i++) {
            for (int u = 0; u < map[i].length; u++) {
                map[i][u].draw();
            }
        }
        // Draw garbage
        if(this.garbage != null) {
            for (ChessPiece piece : this.garbage) {
                piece.draw();
            }
        }
        // Draw grid
        if(this.grid != null) {
            this.grid.draw();
        }
    }

    public static double getDistance(ChessPiece piece1, ChessPiece piece2){
        return getDistance(piece1.tilePos, piece2.tilePos);
    }

    public static double getDistance(int[] pos1, int[] pos2){
        return (Math.sqrt(Math.pow(pos2[0] - pos1[0], 2) + Math.pow(pos2[1] - pos1[1], 2)));
    }

    public static double getDistance(int x1, int x2){
        return x2 - x1;
    }

    public static double getXDistance(int[] pos1, int[] pos2){
        return getDistance(pos1[0], pos2[0]);
    }

    public static double getYDistance(int[] pos1, int[] pos2){
        return getDistance(pos1[1], pos2[1]);
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder("");
        for(int x = 0; x < this.map.length; x++){
            for(int y = 0; y < this.map[x].length; y++){
                if(this.map[y][x].getPiece() == null){
                    sb.append("o ");
                }else{
                    sb.append("x ");
                }
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    protected void LOGE(String message){
        Log.e(tag, message);
    }

    protected void print(){
        LOGE(toString());
    }

}
