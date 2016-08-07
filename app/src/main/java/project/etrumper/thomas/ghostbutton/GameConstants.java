package project.etrumper.thomas.ghostbutton;

import android.util.Log;

import java.security.acl.LastOwnerException;

/**
 * Created by thoma on 6/20/2016.
 * Property of boxedworks.
 */
public class GameConstants {

    static int Number_Colors = 1;    // Difficulty
    static float ambiance = 1f; // Amount of light (or RGB for more specific)

    // Other
    static Camera camera;
    static float menuCameraZoom = 12f,
        defaultShininess = 1f;

    static float[] sceneAmbience = new float[]{ambiance, ambiance, ambiance};

    static Controller controller;

    static TileMap3D tileMap3D;

    static Ease2 frameRateEase = null;

    static long frameRate = 60;

    public static void easeFrameRateTo(long desiredFrameRate, long lengthOfEase) {
        // Make new ease2
        frameRateEase = Ease2.getEase2(frameRate, desiredFrameRate, lengthOfEase);
    }

    public static void init(){
        controller = new Controller();
        // Load config
        loadGame();
    }

    public static void update() {
        /*/ Check if easing
        if (frameRateEase != null) {
            // Update frame rate
            frameRate = (long) frameRateEase.easeQuadradic();
            tileMap.changeFramerate(frameRate);
            frameRateEase.update();
            // Check if ease is done
            if (frameRateEase.done()) {
                // Fix for frame rate not being precise
                frameRate = (long) (frameRateEase.beginningValue + frameRateEase.desiredChangeValue);
                tileMap.changeFramerate(frameRate);
                frameRateEase = null;
            }
        }*/
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
        String saveData = "";
        // Save the camera mode
        int cameraMode = Overlay.optionsMenu.getData()[0];
        saveData = saveData.concat("camera_mode " + cameraMode + "\n");
        // Save to internal storage
        Loader.writeToFile(saveData);
        LOGE("Saved game files");
    }

    public static void loadGame(){
        // Retrieve string
        String temp = Loader.readFromFile();
        if(temp == null || temp.length() == 0 || temp.equals("")){
            LOGE("No config.txt file to load");
            return;
        }
        String[] datum = temp.split(System.getProperty("line.separator"));
        for(String data : datum){
            String[] words = data.split(" ");
            switch (words[0]){
                case("camera_mode"):
                    TextSelection ts = (TextSelection) Overlay.optionsMenu.children[0];
                    ts.currentSelection = Integer.parseInt(words[1]);
                    break;
            }
        }
        LOGE("Successfully loaded game files");
    }

    private static void LOGE(String message){
        Log.e("GameConstants", message);
    }
}
