package project.etrumper.thomas.ghostbutton;

import android.provider.Settings;

/**
 * Created by thoma on 6/23/2016.
 */
public class Path {

    private class PathTile{

        int G, FSCORE;
        int[] tilePos;
        PathTile parent, child;

        PathTile(int G, int[] tilePos, PathTile parent, PathTile child){
            this.G = G;
            this.tilePos = tilePos;
            this.parent = parent;
            this.child = child;
        }
    }

    PathTile[] openList,    // Holds nodes to be checked
                closedList; // Holds checked nodes

    private void findPath(int[] begin, int[] end){
        // If no path needed
        if(begin == end){
            return;
        }
        Path.PathTile currentTile = new Path.PathTile(0, begin, null, null);
        while(true) {
            // Add current tile to closed list
            this.addToClosed(currentTile);
            // Get surrounding tiles and add to open list
            Tile[] beginTiles = GameConstants.tileMap.getSurroundingTiles(begin);
            for (Tile tile : beginTiles) {
                if (tile.getPiece() == null || tile.getPiece().type != ChessPiece.PieceType.STATIONARY) {
                   // this.addToOpen(new PathTile(currentTile.G + 1, tile.position, currentTile, ));
                }
            }
        }
    }

    private void addToOpen(PathTile ... tiles){
        PathTile[] newArray = new PathTile[this.openList.length + tiles.length];
        System.arraycopy(this.openList, 0, newArray, 0, this.openList.length);
        System.arraycopy(tiles, 0, newArray, this.openList.length, tiles.length);
        this.openList = newArray;
    }

    private void removeFromOpen(){

    }

    private boolean isInOpen(PathTile tile){
        for(PathTile gtile : this.openList){
            if(gtile.tilePos == tile.tilePos){
                return true;
            }
        }
        return false;
    }

    private void addToClosed(PathTile ... tiles){
        PathTile[] newArray = new PathTile[this.closedList.length + tiles.length];
        System.arraycopy(this.closedList, 0, newArray, 0, this.closedList.length);
        System.arraycopy(tiles, 0, newArray, this.closedList.length, tiles.length);
        this.closedList = newArray;
    }

    private boolean isInClosed(PathTile tile){
        for(PathTile gtile : this.closedList){
            if(gtile.tilePos == tile.tilePos){
                return true;
            }
        }
        return false;
    }

    private static int computeFSCORE(int G, int[] end, int[] current){
        int H = (int) (Math.abs(TileMap.getXDistance(current, end)) + Math.abs(TileMap.getYDistance(current, end)));
        return G + H;
    }

}
