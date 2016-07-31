package project.etrumper.thomas.ghostbutton;

/**
 * Created by thoma on 6/20/2016.
 * Property of boxedworks.
 */
public class GameConstants {

    enum GameMode {
        ADVENTURE,
        SURVIVAL
    }

    // Important game variables
    static GameMode gameMode = GameMode.SURVIVAL;
    // Archer
    static boolean Drop_Arrows = false,
            Lose_Arrows = false;
    static int Number_Colors = 1;    // Difficulty
    static float ambiance = 1f; // Amount of light (or RGB for more specific)
    static int Max_Barricade_Health = 5,
            Barricade_Health = Max_Barricade_Health;

    // Other
    static Camera camera;
    static float menuCameraZoom = 12f;
    static boolean cameraTilt = true;

    static float zDepth = 7f;
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
        // Update camera
        camera.update();
        camera.updateCamera();
    }

    public static void resetMap() {
        // Reset tileMap
        tileMap3D = new TileMap3D(Maps.W0L0, Maps.W0L1, Maps.W0L2, Maps.W0L3, Maps.W0L4, Maps.W0L5);
    }
}
