package project.etrumper.thomas.ghostbutton;

import android.util.Log;
import android.util.StringBuilderPrinter;

import java.security.acl.LastOwnerException;

/**
 * Created by thoma on 6/20/2016.
 * Property of boxedworks.
 */
public class GameConstants {

    static int Number_Colors = 1;    // Difficulty
    static float ambiance = 1f; // Amount of light (or RGB for more specific)

    static Camera camera;
    static Editor editor;
    static float menuCameraZoom = 12f,
        defaultShininess = 1f;

    static float[] sceneAmbience = new float[]{ambiance, ambiance, ambiance};

    static Controller controller;

    static TileMap3D tileMap3D;

    static long frameRate = 60;

    public static void init(){
        controller = new Controller();
        // Init editor
        GameConstants.editor = new Editor(null);
        // Init overlay
        Overlay.init();
        // Load config
        loadGame();
    }

    public static void update() {
        // Update controller
        controller.update();
        // Update camera
        camera.update();
        camera.updateCamera();
    }

    public static void resetMap() {
        // Reset tileMap
        tileMap3D = new TileMap3D(Maps.W0L0, Maps.W0L1, Maps.W0L2, Maps.W0L3);
    }

    public static void saveGame(){
        StringBuilder saveData = new StringBuilder("");
        // Save the camera mode
        int cameraMode = Overlay.optionsMenu.getData()[0];
        saveData.append(String.format("camera_mode %s", cameraMode));
        // Save current maps
        String[] mapsBase64 = editor.getMapsBase64();
        for(String map : mapsBase64){
            saveData.append(String.format("&map %s", map));
        }
        // Save to internal storage
        Loader.writeToFile(saveData.toString());
        //LOGE("Saved game files");
    }

    public static void loadGame(){
        // Retrieve string
        String temp = Loader.readFromFile();
        if(temp == null || temp.length() == 0 || temp.equals("")){
            LOGE("No config.txt file to load");
            return;
        }
        String[] datum = temp.split("&");
        for(String data : datum){
            String[] words = data.split(" ");
            switch (words[0]){
                case("camera_mode"):
                    TextSelection ts = (TextSelection) Overlay.optionsMenu.children[0];
                    ts.currentSelection = Integer.parseInt(words[1]);
                    break;
                case("map"):
                    editor.addMapBase64(words[1]);
                    break;
                default:
                    LOGE(String.format("Reading un-parsed line of code:\n%s", data));
                    break;
            }
        }
        //LOGE("Loaded game files");
    }

    public static TileMap3D getMap(){
        // Check editor mode
        if(GameConstants.editor.mode == Editor.EditorMode.TESTING){
            return GameConstants.editor.testingMap;
        }
        return GameConstants.tileMap3D;
    }

    private static void LOGE(String message){
        Log.e("GameConstants", message);
    }
}
