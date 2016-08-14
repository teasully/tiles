package project.etrumper.thomas.ghostbutton;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.Vector;

/**
 * Created by thoma on 8/7/2016.
 * Property of boxedworks.
 */
public class Editor extends Logable{

    enum EditorMode{
        MAKING,
        SOLVING,
        TESTING
    }

    EditorMode mode,
        lastMode;

    int selected,   // Tells what buttons to light up
            zLayer; // What z-layer is being worked on

    private boolean oneLayerToggle,
        playerPlaced,
        saved,
            showLayerToggle = false;

    private float cameraZoom = 6.5f,
        cameraOffset = 2f,
            currentItemID; // Used to select block to place

    private char selectorColor;

    private EntityTile3D bPauseButton,
            bLayerUpButton,
            bLayerDownButton,
            bLayerToggleButton,
            bPlaceBlockButton,
                block1,
                block2,
                block3,
                block4,
                block5,
            tileMarker, // Tells what tile is being hoovered over
            bSelectionMarker,    // Tells what item is selected to be placed down
            bRotateCW,
            bRotateCCW,
            bStartEndPlayeTest,
            UIBackground;

    private TileMap3D map;
    public TileMap3D testingMap;

    private Tile3D currentTile;

    private String[] mapsBase64;

    Editor(EditorMode mode){
        super("Editor");

        this.init(mode);
    }

    private void init(EditorMode mode){
        this.mode = mode;
        this.lastMode = null;
        this.currentItemID = 1;
        this.selected = -1;
        this.zLayer = 0;
        this.oneLayerToggle = false;
        this.playerPlaced = false;
        this.selectorColor = 'g';
        // Initiate and overload draw functions for buttons
        this.loadButtons();
        // Set testing map to empty
        this.map = null;
        this.testingMap = null;
        // Load preview blocks
        this.setPreviewBlocks();
        // Init map array
        this.mapsBase64 = new String[0];
    }

    private void loadButtons(){
        this.bPauseButton = new EntityTile3D("Scenery1"){
            @Override
            protected void draw(){
                Vector3f color;
                if(selected == 0){
                    color = new Vector3f(1f, 0f, 0f);
                }else{
                    color = new Vector3f(0f, 1f, 0f);
                }
                MaterialManager.changeMaterialColor("Button", color);
                super.draw();
            }
        };
        this.bPauseButton.setCurrentAnimation(this.bPauseButton.addAnimation("button"));
        this.bPauseButton.position = new float[]{5f, 8f, 9f};
        this.bLayerUpButton = new EntityTile3D("Scenery1"){
            @Override
            protected void draw(){
                Vector3f color;
                if(selected == 1){
                    color = new Vector3f(1f, 0f, 0f);
                }else{
                    color = new Vector3f(0f, 1f, 0f);
                }
                MaterialManager.changeMaterialColor("Arrow", color);
                super.draw();
            }
        };
        this.bLayerUpButton.setCurrentAnimation(this.bLayerUpButton.addAnimation("right_arrow"));
        this.bLayerUpButton.position = new float[]{2.5f, 0.7f, 5f};
        this.bLayerUpButton.rotation[1] = -90f;
        this.bLayerDownButton = new EntityTile3D("Scenery1"){
            @Override
            protected void draw(){
                Vector3f color;
                if(selected == 2){
                    color = new Vector3f(1f, 0f, 0f);
                }else{
                    color = new Vector3f(0f, 1f, 0f);
                }
                MaterialManager.changeMaterialColor("Arrow", color);
                super.draw();
            }
        };
        this.bLayerDownButton.setCurrentAnimation(this.bLayerDownButton.addAnimation("right_arrow"));
        this.bLayerDownButton.position = new float[]{2.5f, -0.7f, 5f};
        this.bLayerDownButton.rotation[1] = 90f;
        this.bLayerToggleButton = new EntityTile3D("Scenery1"){
            @Override
            protected void draw(){
                Vector3f color;
                if(selected == 3){
                    if(oneLayerToggle){
                        color = new Vector3f(1f, 0f, 0f);
                    }else{
                        color = new Vector3f(0f, 1f, 0f);
                    }
                }else{
                    if(!oneLayerToggle){
                        color = new Vector3f(1f, 0f, 0f);
                    }else{
                        color = new Vector3f(0f, 1f, 0f);
                    }
                }
                MaterialManager.changeMaterialColor("Button", color);
                super.draw();
            }
        };
        this.bLayerToggleButton.setCurrentAnimation(this.bLayerToggleButton.addAnimation("button"));
        this.bLayerToggleButton.position = new float[]{5f, -8f, 9f};
        this.bPlaceBlockButton = new EntityTile3D("Scenery1"){
            @Override
            protected void draw(){
                Vector3f color;
                if(selected == 4){
                    color = new Vector3f(1f, 0f, 0f);
                }else{
                    color = new Vector3f(0f, 1f, 0f);
                }
                MaterialManager.changeMaterialColor("Button", color);
                super.draw();
            }
        };
        this.bPlaceBlockButton.setCurrentAnimation(this.bPlaceBlockButton.addAnimation("button"));
        this.bPlaceBlockButton.position = new float[]{0f, -4f, 5f};
        this.tileMarker = new EntityTile3D("Scenery1"){
            @Override
            protected void draw(){
                MaterialManager.saveMaterial("Wall");
                Material mat = MaterialManager.getMaterial("Wall");
                float opacity = mat.opacity;
                Vector3f color = null;
                if(selectorColor == 'r') {
                    color = new Vector3f(1f, 0f, 0f);
                }
                else if(selectorColor == 'g') {
                    color = new Vector3f(0f, 1f, 0f);
                }
                else if(selectorColor == 'b') {
                    color = new Vector3f(0f, 0f, 1f);
                }
                MaterialManager.materials[MaterialManager.getMaterialIndex("Wall")].opacity = 0.5f;
                MaterialManager.changeMaterialColor("Wall", color);
                super.draw();
                MaterialManager.restoreSavedMaterial();
                MaterialManager.materials[MaterialManager.getMaterialIndex("Wall")].opacity = opacity;

            }
        };
        this.tileMarker.setCurrentAnimation(this.tileMarker.addAnimation("cube"));
        this.tileMarker.position = new float[]{0f, 0f, -2f};
        this.tileMarker.scale = new float[]{0.1f, 0.1f, 0.1f};
        this.bRotateCW = new EntityTile3D("Scenery1"){
            @Override
            protected void draw(){
                Vector3f color;
                if(selected == 5){
                    color = new Vector3f(0f, 1f, 0f);
                } else {
                    color = new Vector3f(1f, 0f, 0f);
                }
                MaterialManager.changeMaterialColor("Button", color);
                super.draw();
            }
        };
        this.bRotateCW.setCurrentAnimation(this.bRotateCW.addAnimation("button"));
        this.bRotateCW.position = new float[]{2f, -5.5f, 7f};
        this.bRotateCCW = new EntityTile3D("Scenery1"){
            @Override
            protected void draw() {
                Vector3f color;
                if (selected == 6) {
                    color = new Vector3f(0f, 1f, 0f);
                } else {
                    color = new Vector3f(1f, 0f, 0f);
                }
                MaterialManager.changeMaterialColor("Button", color);
                super.draw();
            }
        };
        this.bRotateCCW.setCurrentAnimation(this.bRotateCCW.addAnimation("button"));
        this.bRotateCCW.position = new float[]{-2f, -5.5f, 7f};
        this.bStartEndPlayeTest = new EntityTile3D("Scenery1"){
            @Override
            protected void draw() {
                Vector3f color;
                if (selected == 7) {
                    color = new Vector3f(0f, 1f, 0f);
                } else {
                    color = new Vector3f(1f, 0f, 0f);
                }
                MaterialManager.changeMaterialColor("Button", color);
                super.draw();
            }
        };
        this.bStartEndPlayeTest.setCurrentAnimation(this.bStartEndPlayeTest.addAnimation("button"));
        this.bStartEndPlayeTest.position = new float[]{-5f, -7.5f, 9f};

        this.UIBackground = new EntityTile3D("Scenery1") {
            @Override
            protected void draw() {
                Vector3f color = new Vector3f(.2f, .1f, 0.1f);
                MaterialManager.changeMaterialColor("Wall", color);
                super.draw();
            }
        };
        UIBackground.currentAnimation = UIBackground.addAnimation("wallstandard1");
        UIBackground.position = new float[]{0f, 8f, 10f};
        UIBackground.scale = new float[]{12f, 0f, 4f};
    }

    protected void setCurrentMap(String ... data) {
        this.playerPlaced = false;
        // Load empty map
        this.map = new TileMap3D(data) { // 5x5x5 map
            @Override
            protected void draw() {
                if (oneLayerToggle) {
                    // Only draw current Z
                    for (int x = 0; x < this.dimensions.x(); x++) {
                        for (int y = 0; y < this.dimensions.y(); y++) {
                            this.tiles[x][y][zLayer].draw();
                            if (this.tiles[x][y][zLayer].hasEntity()) {
                                EntityTile3D entity = this.tiles[x][y][zLayer].children[0];
                                entity.drawDirection();
                            }
                        }
                    }
                    return;
                }
                // Iterate through tiles
                for (Tile3D[][] a : this.tiles) {
                    for (Tile3D[] b : a) {
                        for (Tile3D tile3D : b) {
                            tile3D.draw();
                            if (tile3D.hasEntity()) {
                                EntityTile3D entity = tile3D.children[0];
                                entity.drawDirection();
                            }
                        }
                    }
                }
            }
        };
        // Set current tile for camera
        this.zLayer = this.map.dimensions.z() / 2;
        this.currentTile = this.map.getTile(new Vector3i(
                Math.round((float) this.map.dimensions.x() / 2.f),
                Math.round((float) this.map.dimensions.y() / 2.f),
                this.zLayer
        ));
        this.moveCameraTo();
        // Move selector
        this.updateSelector();
        // Set map settings overlay
        TextSelection ts = (TextSelection) Overlay.editorSettingsMenu.children[0];
        ts.options[0] = new Text(this.map.name.toLowerCase());
    }

    public void handleInput(){
        if(this.mode == EditorMode.TESTING){
            this.handleTestInput();
        }else{
            this.handleEditorInput();
        }
    }

    private void handleEditorInput(){
        // Check swipes
        if (GameConstants.controller.direction != null) {
            // Check y pos to see where swiping
            float y = (TouchManager.y / SuperManager.height);
            if (y <= 0.25) {
                if (GameConstants.controller.direction == ChessPiece.PieceDirection.LEFT) {
                    this.getInput("object_left");
                } else if (GameConstants.controller.direction == ChessPiece.PieceDirection.RIGHT) {
                    this.getInput("object_right");
                }
            }
            // Set to null
            GameConstants.controller.direction = null;
        }
        if (GameConstants.controller.justReleased) {
            GameConstants.controller.justReleased = false;
            int selection = this.selected;
            if (selection != -1) {
                switch (selection) {
                    case (0):
                        this.getInput("pause");
                        break;
                    case (1):
                        this.getInput("up_arrow");
                        break;
                    case (2):
                        this.getInput("down_arrow");
                        break;
                    case (3):
                        this.getInput("layer_toggle");
                        break;
                    case (4):
                        this.getInput("place");
                        break;
                    case(5):
                        this.getInput("rotate_ccw");
                        break;
                    case(6):
                        this.getInput("rotate_cw");
                        break;
                    case(7):
                        this.getInput("play_test_toggle");
                        break;
                }
            }
            this.selected = -1;
        }
        // Drag map
        else if(TouchManager.down) {
            float y = (TouchManager.y / SuperManager.height);
            if (y > 0.25f && y < 0.85f) {
                // Check x movement
                if (Math.abs(TouchManager.rDX) > 1.5f) {
                    // Make sure won't go out of bounds
                    float dx = (TouchManager.rDX / SuperManager.width * 8f);
                    if (GameConstants.camera.target[1] + dx >= this.map.dimensions.y()) {
                        GameConstants.camera.position[1] = this.map.dimensions.y();
                        GameConstants.camera.target[1] = this.map.dimensions.y();
                    } else if (GameConstants.camera.target[1] + dx <= 0) {
                        GameConstants.camera.position[1] = 0;
                        GameConstants.camera.target[1] = 0;
                    }
                    // Move camera by amount
                    else {
                        GameConstants.camera.position[1] += dx;
                        GameConstants.camera.target[1] += dx;
                    }
                }
                // Check y movement
                if (Math.abs(TouchManager.rDY) > 1.5f) {
                    float dy = (TouchManager.rDY / SuperManager.height * 8f);
                    if (GameConstants.camera.target[0] + dy >= this.map.dimensions.x()) {
                        GameConstants.camera.position[0] = this.map.dimensions.x() - this.cameraOffset;
                        GameConstants.camera.target[0] = this.map.dimensions.x() + 0.001f;
                    } else if (GameConstants.camera.target[0] + dy <= 0) {
                        GameConstants.camera.position[0] = -this.cameraOffset;
                        GameConstants.camera.target[0] = 0.001f;
                    }
                    // Move camera by amount
                    else {
                        GameConstants.camera.position[0] += dy;
                        GameConstants.camera.target[0] += dy;
                    }
                }
                // Update camera
                GameConstants.camera.updateCamera();
                // Update current block
                Tile3D saved = this.currentTile;
                this.currentTile = this.map.getTile(new Vector3i(
                        (int) GameConstants.camera.target[0],
                        (int) (GameConstants.camera.target[1] + 0.5f),
                        this.zLayer
                ));
                if (this.currentTile == null) {
                    this.currentTile = saved;
                } else {
                    this.updateSelector();
                    // Set selector color
                    this.selectorColor = 'g';
                    if(this.currentTile.hasEntity()){
                        this.selectorColor = 'r';
                        if(this.isEditable(this.currentTile)){
                            this.selectorColor = 'b';
                        }
                    }
                }
            }
        }
        // Check buttons
        if (GameConstants.controller.justPressed) {
            GameConstants.controller.justPressed = false;
            float y = (TouchManager.y / SuperManager.height),
                    x = (TouchManager.x / SuperManager.width);
            this.selected = -1;
            // Check pause button
            if (y < 0.1f && x < 0.15f) {
                this.selected = 0;
            }
            // Check down arrow
            else if (y < 0.5f && y > 0.35f && x < 0.18f) {
                this.selected = 1;
            }
            // Check up arrow
            else if (y > 0.5f && y < 0.65 && x < 0.18f) {
                this.selected = 2;
            }
            // Check layer toggle
            else if (y > 0.9f && x < 0.15f) {
                this.selected = 3;
            }
            // Check place button
            else if (y > 0.8f && x < 0.55f && x > 0.45f) {
                this.selected = 4;
            }
            // Check rotateCW
            else if (y > 0.82f && x < 0.4f && x > 0.2f){
                this.selected = 5;
            }
            // Check rotateCCW
            else if (y > 0.82f && x > 0.6f && x < 0.8f){
                this.selected = 6;
            }
            // Check play-test toggle
            else if (y > 0.9f && x > 0.85f) {
                this.selected = 7;
            }
        }
    }

    private void handleTestInput() {
        if (GameConstants.controller.justReleased) {
            GameConstants.controller.justReleased = false;
            int selection = this.selected;
            if (selection != -1) {
                switch (selection) {
                    case (0):
                        this.getInput("pause");
                        break;
                    case (1):
                        this.getInput("up_arrow");
                        break;
                    case (2):
                        this.getInput("down_arrow");
                        break;
                    case (7):
                        this.getInput("play_test_toggle");
                        break;
                }
            }
            this.selected = -1;
        }
        // Drag map
        else if(TouchManager.down) {
            // Check x movement
            if (Math.abs(TouchManager.rDX) > 1.5f) {
                // Make sure won't go out of bounds
                float dx = (TouchManager.rDX / SuperManager.width * 8f);
                if (GameConstants.camera.target[1] + dx >= this.testingMap.dimensions.y()) {
                    GameConstants.camera.position[1] = this.testingMap.dimensions.y();
                    GameConstants.camera.target[1] = this.testingMap.dimensions.y();
                } else if (GameConstants.camera.target[1] + dx <= 0) {
                    GameConstants.camera.position[1] = 0;
                    GameConstants.camera.target[1] = 0;
                }
                // Move camera by amount
                else {
                    GameConstants.camera.position[1] += dx;
                    GameConstants.camera.target[1] += dx;
                }
            }
            // Check y movement
            if (Math.abs(TouchManager.rDY) > 1.5f) {
                float dy = (TouchManager.rDY / SuperManager.height * 8f);
                if (GameConstants.camera.target[0] + dy >= this.testingMap.dimensions.x()) {
                    GameConstants.camera.position[0] = this.testingMap.dimensions.x() -this.cameraOffset;
                    GameConstants.camera.target[0] = this.testingMap.dimensions.x() + 0.001f;
                } else if (GameConstants.camera.target[0] + dy <= 0) {
                    GameConstants.camera.position[0] = -this.cameraOffset;
                    GameConstants.camera.target[0] = 0.001f;
                }
                // Move camera by amount
                else{
                    GameConstants.camera.position[0] += dy;
                    GameConstants.camera.target[0] += dy;
                }
            }
            // Update camera
            GameConstants.camera.updateCamera();
            // Update current block
            Tile3D saved = this.currentTile;
            this.currentTile = this.testingMap.getTile(new Vector3i(
                    (int)GameConstants.camera.target[0],
                    (int)(GameConstants.camera.target[1] + 0.5f),
                    this.zLayer
            ));
            if(this.currentTile == null){
                this.currentTile = saved;
            }else{
                this.updateSelector();
                // Set selector color
                this.selectorColor = 'g';
                if(this.currentTile.hasEntity()){
                    this.selectorColor = 'r';
                    if(this.isEditable(this.currentTile)){
                        this.selectorColor = 'b';
                    }
                }
            }
        }
        // Check buttons
        if (GameConstants.controller.justPressed) {
            GameConstants.controller.justPressed = false;
            float y = (TouchManager.y / SuperManager.height),
                    x = (TouchManager.x / SuperManager.width);
            this.selected = -1;
            // Check pause button
            if (y < 0.1f && x < 0.15f) {
                this.selected = 0;
            }
            // Check down arrow
            else if (y < 0.5f && y > 0.35f && x < 0.18f) {
                this.selected = 1;
            }
            // Check up arrow
            else if (y > 0.5f && y < 0.65 && x < 0.18f) {
                this.selected = 2;
            }
            // Check play-test toggle
            else if (y > 0.9f && x > 0.85f) {
                this.selected = 7;
            }
        }
    }

    public void getInput(String input){
        switch(input) {
            case ("object_left"):
                this.nextItem();
                break;
            case ("object_right"):
                this.previousItem();
                break;
            case ("map_left"):
                this.moveNorth();
                break;
            case ("map_right"):
                this.moveSouth();
                break;
            case ("map_up"):
                this.moveEast();
                break;
            case ("map_down"):
                this.moveWest();
                break;
            case ("pause"):
                if(this.mode == EditorMode.SOLVING) {
                    Overlay.handleMultiTap(2);
                }else if(this.mode == EditorMode.MAKING){
                    Overlay.handleMultiTap(3);
                }else{
                    Overlay.handleMultiTap(4);
                }
                break;
            case ("up_arrow"):
                this.moveUp();
                break;
            case ("down_arrow"):
                this.moveDown();
                break;
            case ("layer_toggle"):
                if(this.showLayerToggle) {
                    this.layerToggle();
                }
                break;
            case ("place"):
                this.placeBlock();
                break;
            case ("playtest"):
                this.playTest();
                break;
            case ("save_device"):
                this.saveToDevice();
                break;
            case ("save_copy"):
                this.saveToClipboard();
                break;
            case ("quit"):
                this.quit();
                break;
            case("restart_playtest"):
                this.restartPlayTest();
                break;
            case("to_editor"):
                this.returnToEditor();
                break;
            case("rotate_cw"):
                this.rotateCW();
                break;
            case("rotate_ccw"):
                this.rotateCCW();
                break;
            case("play_test_toggle"):
                if(this.mode == EditorMode.TESTING){
                    this.returnToEditor();
                }else{
                    this.playTest();
                }
                break;
            default:
                LOGE("getInput got default case " + input);
        }
    }

    private void rotateCW(){
        if(this.selectorColor != 'b' || GameConstants.camera.isEasing()){
            return;
        }
        for(EntityTile3D child : this.currentTile.children){
            if(child.directional) {
                child.turnCW_Z();
                this.saved = false;
            }
        }
    }

    private void rotateCCW(){
        if(this.selectorColor != 'b' || GameConstants.camera.isEasing()){
            return;
        }
        for(EntityTile3D child : this.currentTile.children){
            if(child.directional) {
                child.turnCCW_Z();
                this.saved = false;
            }
        }
    }

    private void restartPlayTest(){
        this.playTest();
    }

    private void returnToEditor(){
        if(this.mode != EditorMode.TESTING){
            LOGE("Trying to return to editor when not play-testing");
            return;
        }
        this.testingMap = null;
        this.mode = this.lastMode;
        // Change screen to overlay
        Overlay.changeScreen(Overlay.CurrentScreen.OVERLAY_ONLY);
        Overlay.showControls = false;
        // Move camera
        this.moveCameraTo();
    }

    private void quit(){
        this.mode = null;
        this.map = null;
        this.testingMap = null;
        this.playerPlaced = false;
        Overlay.showControls = false;
    }

    public boolean isSaved(){
        return this.saved;
    }

    private void playTest(){
        if(this.mode != EditorMode.TESTING){
            // Tell editor is play-testing
            this.lastMode = this.mode;
            this.mode = EditorMode.TESTING;
        }
        // Create testing map based off of current map
        this.testingMap = new TileMap3D(this.decodeMapBase64(this.map.toBase64()));
        // Change screen
        Overlay.changeScreen(Overlay.CurrentScreen.OVERLAY_ONLY);
    }

    private void saveToClipboard() {
        String base64 = this.map.toBase64();
        Loader.copy("map_data", base64);
        Logable.alertBoxSimple("Complete", "Map copied to clipboard:\n" + base64, "Perfect");
        this.saved = true;
    }

    private void saveToDevice() {
        // Check if already saved
        if (this.saved) {
            Logable.alertBoxSimple("Error", "No changes to save", "Great");
            return;
        }
        // Get encoded map data
        String base64 = this.map.toBase64();
        this.addMapBase64(base64);
        GameConstants.saveGame();
        Logable.alertBoxSimple("Complete", "Map saved to device:\n" + base64, "Perfect");
        this.saved = true;
    }

    public static String[] decodeMapBase64(String base64){
        String decoded = null;
        try {
            decoded = new String(Base64.decode(base64, Base64.DEFAULT));
        } catch (Exception e) {
            Log.e("Editor", e.toString());
        }
        if(decoded == null){
            return null;
        }
        return decoded.split("\n");
    }

    private void nextItem(){
        this.currentItemID++;
        this.checkCurrentItemScope();
        this.setPreviewBlocks();
    }

    private void previousItem(){
        this.currentItemID--;
        this.checkCurrentItemScope();
        this.setPreviewBlocks();
    }

    private void checkCurrentItemScope() {
        if (this.currentItemID < 0f) {
            this.currentItemID = ObjectManager.numIDS - 1f;
        } else if (this.currentItemID >= ObjectManager.numIDS) {
            this.currentItemID = 0f;
        }
    }

    private void layerToggle(){
        if(this.oneLayerToggle){
            this.oneLayerToggle = false;
        }else{
            this.oneLayerToggle = true;
        }
    }

    public void addMapBase64(String ... maps){
        if(maps == null || maps.length == 0){
            return;
        }
        for(String map : maps){
            this.addMapBase64(map);
        }
    }

    public void addMapBase64(String map){
        String mapName = this.decodeMapBase64(map)[0];
        // Check if map already exists
        int mapIndex = 0;
        for(String savedMap : this.mapsBase64){
            String decodedSavedMap = this.decodeMapBase64(savedMap)[0];
            if(decodedSavedMap.equals(mapName)){
                // Overwrite old map and exit function
                this.mapsBase64[mapIndex] = map;
                return;
            }
            mapIndex++;
        }
        String[] newMaps = new String[this.mapsBase64.length + 1];
        System.arraycopy(this.mapsBase64, 0, newMaps, 0, this.mapsBase64.length);
        newMaps[this.mapsBase64.length] = map;
        this.mapsBase64 = newMaps;
    }

    public String[] getMapsBase64(){
        return this.mapsBase64;
    }

    public void update() {
        this.tileMarker.update();
        this.bPauseButton.update();
        this.bStartEndPlayeTest.update();
        this.UIBackground.update();
        if(this.mode == EditorMode.TESTING){
            this.testingMap.update();
            // Update the selector
        }else {
            if(this.map != null) {
                this.map.update();
            }
            this.bLayerUpButton.update();
            this.bLayerDownButton.update();
            this.bLayerToggleButton.update();
            this.bPlaceBlockButton.update();
            if (this.block1 != null) {
                this.block1.update();
            }
            if (this.block2 != null) {
                this.block2.update();
            }
            if (this.block3 != null) {
                this.block3.update();
            }
            if (this.block4 != null) {
                this.block4.update();
            }
            if (this.block5 != null) {
                this.block5.update();
            }
            this.bRotateCW.update();
            this.bRotateCCW.update();
        }
    }

    public void drawMap(){
        this.tileMarker.draw();
        if(this.mode == EditorMode.TESTING){
            this.testingMap.draw();
            return;
        }
        if(this.map != null) {
            this.map.draw();
        }
    }

    public void drawUI() {
        // Check overlay to make sure no menu is showing
        if (Overlay.currentScreen != Overlay.CurrentScreen.OVERLAY_ONLY) {
            return;
        }
        this.bStartEndPlayeTest.draw();
        this.bPauseButton.draw();
        this.bLayerUpButton.draw();
        this.bLayerDownButton.draw();
        // Check if is play-testing
        if (this.mode != EditorMode.TESTING) {
            this.UIBackground.draw();
            if(this.showLayerToggle) {
                this.bLayerToggleButton.draw();
            }
            this.bPlaceBlockButton.draw();
            if (this.block1 != null) {
                this.block1.draw();
            }
            if (this.block2 != null) {
                this.block2.draw();
            }
            if (this.block3 != null) {
                this.block3.draw();
            }
            if (this.block4 != null) {
                this.block4.draw();
            }
            if (this.block5 != null) {
                this.block5.draw();
            }
            // Check if editable
            if(this.selectorColor == 'b'){
                this.bRotateCW.draw();
                this.bRotateCCW.draw();
            }
        }
    }

    public String getMapName(){
        if(this.map == null){
            return null;
        }
        return this.map.name;
    }

    public void placedPlayer(){
        if(this.playerPlaced){
            LOGE("Trying to set player placed when already placed");
        }
        this.playerPlaced = true;
    }

    private void placeBlock() {
        if(this.currentTile.hasEntity()){
            // Check if player is trying remove block they shouldn't
            if(this.mode == EditorMode.SOLVING && this.currentTile.children[0].placedByUser){
                return;
            }
            // Check if same IDs; no use replacing
            if(this.currentTile.children[0].ID == this.currentItemID){
                return;
            }
        }
        // Check if player being placed
        if(this.currentItemID % ObjectManager.numIDS == 1){
            // Check if player already placed
            if(this.playerPlaced){
                // Remove player from old spot
                Tile3D playerTile = this.map.getTile("Player");
                playerTile.removeEntity(playerTile.getPlayer());
            }
        }
        // Check if there is already a solid in the block
        if(this.currentTile.hasEntity()){
            EntityTile3D removeEntity;
            if(this.currentTile.hasPlayer()){
                this.playerPlaced = false;
                removeEntity = this.currentTile.getPlayer();
            }else{
                removeEntity = this.currentTile.getBlock();
            }
            // Remove the block so they do not overlap
            this.currentTile.removeEntity(removeEntity);
        }
        // Retrieve new object and set locals
        Vector3i tilePos = this.currentTile.tilePosition;
        EntityTile3D temp = ObjectManager.getObject(
                (int)this.currentItemID % ObjectManager.numIDS,
                tilePos.x(),
                tilePos.y(),
                tilePos.z()
        );
        // Check if empty space
        if(this.currentItemID % ObjectManager.numIDS != 0) {
            temp.position = new float[]{tilePos.x(), tilePos.y(), tilePos.z()};
            temp.disabled = true;
            this.currentTile.addEntity(temp);
            // Set selector color
            this.selectorColor = 'r';
            // Set player placed if was
            if (this.currentItemID % ObjectManager.numIDS == 1) {
                this.playerPlaced = true;
            }
            if(isEditable(this.currentTile)){
                this.selectorColor = 'b';
            }
        }else {
            this.selectorColor = 'g';
        }
        // Change saved to false because map was changed
        this.saved = false;
    }

    private void updateSelector(){
        this.tileMarker.position = new float[]{
                this.currentTile.tilePosition.x(),
                this.currentTile.tilePosition.y(),
                this.currentTile.tilePosition.z() - 2f
        };
    }

    private void setPreviewBlocks(){
        // Set the three blocks you can see in top of screen
        int ID1 = (int)this.currentItemID % ObjectManager.numIDS - 2,
                ID2 = ID1 + 1,
                ID3 = ID2 + 1,
                ID4 = ID3 + 1,
                ID5 = ID4 + 1;
        // Check out of bounds cases and loop around objects
        if(ID1 == -1){
            ID1 = ObjectManager.numIDS - 1;
        }
        if(ID1 == -2){
            ID1 = ObjectManager.numIDS -2;
        }
        if(ID2 == -1){
            ID2 = ObjectManager.numIDS - 1;
        }
        if(ID4 == ObjectManager.numIDS){
            ID4 = 0;
        }
        if(ID5 == ObjectManager.numIDS){
            ID5 = 0;
        }
        if(ID5 == ObjectManager.numIDS + 1){
            ID5 = 1;
        }
        this.block1 = ObjectManager.getObject(ID5, 0, 0, 0);
        this.block2 = ObjectManager.getObject(ID4, 0, 0, 0);
        this.block3 = ObjectManager.getObject(ID3, 0, 0, 0);
        this.block4 = ObjectManager.getObject(ID2, 0, 0, 0);
        this.block5 = ObjectManager.getObject(ID1, 0, 0, 0);
        // Set positions and scales
        float newScale = -0.35f;
        if(this.block1 != null) {
            this.block1.disabled = true;
            this.block1.position = new float[]{-2.f, 3.5f, 5.f};
            this.block1.scale = new float[]{newScale, newScale, newScale};
        }
        if(this.block2 != null) {
            this.block2.disabled = true;
            this.block2.position = new float[]{-1.f, 3.5f, 5.f};
            this.block2.scale = new float[]{newScale, newScale, newScale};
        }
        if(this.block3 != null) {
            this.block3.disabled = true;
            this.block3.position = new float[]{0f, 3.5f, 5f};
        }
        if(this.block4 != null) {
            this.block4.disabled = true;
            this.block4.position = new float[]{1.f, 3.5f, 5f};
            this.block4.scale = new float[]{newScale, newScale, newScale};
        }
        if(this.block5 != null) {
            this.block5.disabled = true;
            this.block5.position = new float[]{2.f, 3.5f, 5f};
            this.block5.scale = new float[]{newScale, newScale, newScale};
        }
    }

    private void moveWest(){
        this.move(new Vector3i(-1, 0, 0));
    }

    private void moveEast(){
        this.move(new Vector3i(1, 0, 0));
    }

    private void moveNorth(){
        this.move(new Vector3i(0, 1, 0));
    }

    private void moveSouth(){
        this.move(new Vector3i(0, -1, 0));
    }

    private void moveUp(){
        if(this.move(new Vector3i(0, 0, 1))){
            this.zLayer ++;
        }
    }

    private void moveDown(){
        if(this.move(new Vector3i(0, 0, -1))){
            this.zLayer--;
        }
    }

    private boolean move(Vector3i offset){
        if(this.currentTile == null){
            return false;
        }
        Vector3i current = this.currentTile.tilePosition,
                newPos = Vector3i.add(current, offset);
        // Get block with new pos
        Tile3D tile;
        if(this.mode != EditorMode.TESTING){
            tile = this.map.getTile(newPos);
        }else{
            tile = this.testingMap.getTile(newPos);
        }
        // If null, return
        if(tile == null){
            return false;
        }
        // Set selector color
        this.selectorColor = 'g';
        if(tile.hasEntity()){
            this.selectorColor = 'r';
            if(this.isEditable(tile)){
                this.selectorColor = 'b';
            }
        }
        // Set gotten block as current pos and move camera
        this.currentTile = tile;
        this.moveCameraTo();
        // Move tile selector
        this.updateSelector();
        return true;
    }

    private boolean isEditable(Tile3D tile){
        if(tile == null || !tile.hasEntity()){
            return false;
        }
        for(EntityTile3D entity : tile.children){
            if(this.isEditable(entity)){
                return true;
            }
        }
        return false;
    }

    private boolean isEditable(EntityTile3D entity) {
        if (entity == null) {
            return false;
        }
        if (entity.directional) {
            return true;
        }
        return false;
    }

    private void moveCameraTo(){
        Vector3i tilePos = this.currentTile.tilePosition;
        GameConstants.camera.easePositionTo(new float[]{tilePos.x() - this.cameraOffset, tilePos.y(), tilePos.z() + this.cameraZoom}, 500);
        GameConstants.camera.easeTargetTo(new float[]{tilePos.x() + 0.001f, tilePos.y(), tilePos.z()}, 500);
    }

    public void dialogueNewFromScratch(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.activity);
        // Set non dismissible
        builder.setCancelable(false);
        // Get the layout inflater
        LayoutInflater inflater = MainActivity.activity.getLayoutInflater();
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View v = inflater.inflate(R.layout.new_from_scratch, null);
        builder.setView(v);
        // Get view elements
        final EditText mapName = (EditText)v.findViewById(R.id.map_name),
                x = (EditText)v.findViewById(R.id.map_size_x),
                y = (EditText)v.findViewById(R.id.map_size_y),
                z = (EditText)v.findViewById(R.id.map_size_z);
        final Button positive = (Button)v.findViewById(R.id.positive),
                negative = (Button)v.findViewById(R.id.negative);
        MainActivity.activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final Dialog dialog = builder.create();
                positive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Get data from elements
                        String map_name = mapName.getText().toString();
                        Vector3i dimensions = new Vector3i(0, 0, 0);
                        // Check data range
                        if (map_name.length() == 0) {
                            mapName.setError("Invalid name");
                            return;
                        }
                        try{
                            dimensions.x(Integer.parseInt(x.getText().toString()));
                        }catch(NumberFormatException e){
                            x.setError("Invalid number");
                            return;
                        }
                        try{
                            dimensions.y(Integer.parseInt(y.getText().toString()));
                        }catch(NumberFormatException e){
                            y.setError("Invalid number");
                            return;
                        }
                        try{
                            dimensions.z(Integer.parseInt(z.getText().toString()));
                        }catch(NumberFormatException e){
                            z.setError("Invalid number");
                            return;
                        }
                        if (dimensions.x() <= 0) {
                            x.setError("Number too low");
                            return;
                        } else if (dimensions.x() > 10) {
                            x.setError("Number too high");
                            return;
                        }
                        if (dimensions.y() <= 0) {
                            y.setError("Number too low");
                            return;
                        } else if (dimensions.y() > 10) {
                            y.setError("Number too high");
                            return;
                        }
                        if (dimensions.z() <= 1) {
                            z.setError("Number too low");
                            return;
                        } else if (dimensions.z() > 5) {
                            z.setError("Number too high");
                            return;
                        }
                        // Gather data for map
                        String[] data = {
                                map_name,
                                ""+dimensions.x(),
                                ""+dimensions.y(),
                                ""+dimensions.z()
                        };
                        // Set mode
                        GameConstants.editor.mode = EditorMode.MAKING;
                        // Create and set map
                        setCurrentMap(data);
                        // Change screen
                        Overlay.changeScreen(Overlay.CurrentScreen.OVERLAY_ONLY);
                        // Remove dialogue
                        dialog.dismiss();
                        saved = false;
                    }
                });
                negative.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

    }

    public void dialogueLoadFromDevice() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.activity);
        builder.setTitle("Level selector");
        builder.setCancelable(false);
        MainActivity.activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Get array of map names
                String[] names = new String[mapsBase64.length];
                for (int i = 0; i < names.length; i++) {
                    names[i] = decodeMapBase64(mapsBase64[i])[0];
                }
                builder.setItems(names, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Set mode and change screen
                        GameConstants.editor.mode = EditorMode.MAKING;
                        Overlay.changeScreen(Overlay.CurrentScreen.OVERLAY_ONLY);
                        // Create and set map
                        setCurrentMap(decodeMapBase64(mapsBase64[which]));
                        // Remove dialogue
                        dialog.dismiss();
                        saved = true;
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                Dialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    public void dialogueDeleteMaps() {
        final Vector<Integer> selected = new Vector<>(0);
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.activity);
        builder.setTitle("Level selector");
        builder.setCancelable(false);
        MainActivity.activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Get array of map names
                String[] names = new String[mapsBase64.length];
                for (int i = 0; i < names.length; i++) {
                    names[i] = decodeMapBase64(mapsBase64[i])[0];
                }
                builder.setMultiChoiceItems(names, null, new DialogInterface.OnMultiChoiceClickListener() {
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        if (isChecked) {
                            // If the user checked the item, add it to the selected items
                            selected.add(which);
                        } else if (selected.contains(which)) {
                            // Else, if the item is already in the array, remove it
                            selected.remove(Integer.valueOf(which));
                        }
                    }
                });
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Remove selected maps
                        for (int i : selected) {
                            mapsBase64[i] = null;
                        }
                        // Move non null maps to new array
                        String[] temp = new String[mapsBase64.length - selected.size()];
                        int i = 0;
                        for (String mapData : mapsBase64) {
                            if (mapData == null) {
                                continue;
                            }
                            temp[i++] = mapData;
                        }
                        mapsBase64 = temp;
                        // Save the new map data
                        GameConstants.saveGame();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                Dialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    public void changeMapName(String newName){
        this.map.name = newName;
        // Set map settings overlay
        TextSelection ts = (TextSelection) Overlay.editorSettingsMenu.children[0];
        ts.options[0] = new Text(this.map.name.toLowerCase());
    }

    public void loadMapFromPaste(){
        String base64 = Loader.paste();
        if(base64 == null){
            LOGE("Got null");
            return;
        }
        // Set mode
        GameConstants.editor.mode = EditorMode.MAKING;
        // Create and set map
        this.setCurrentMap(this.decodeMapBase64(base64));
        // Change screen
        Overlay.changeScreen(Overlay.CurrentScreen.OVERLAY_ONLY);
        this.saved = false;
    }

}
