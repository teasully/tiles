package project.etrumper.thomas.ghostbutton;

/**
 * Created by thoma on 4/29/2016.
 * Property of boxedworks.
 */

public class Controller extends BasicEntity{

    ChessPiece.PieceDirection direction;

    boolean justPressed,
            justReleased;

    long timeHeld;

    int numTaps;

    Controller(){
        super("Controller");
        this.direction = null;
        this.justPressed = false;
        this.justReleased = false;
        this.timeHeld = 0;
        this.numTaps = 0;
    }

    static int inputHandlerID = 0;

    static Sound sButtonPress = new Sound(R.raw.tick1);

    @Override
    protected void update() {
        if (Overlay.currentScreen == Overlay.CurrentScreen.OVERLAY_ONLY) {
            // Handle game input
            if (this.justPressed) {
                this.justPressed = false;
                float y = (TouchManager.y / SuperManager.height);
                Overlay.pressed = 0;
                if (y >= 0.8f) {
                    //sButtonPress.play();
                    float x = (TouchManager.x / SuperManager.width);
                    if (x <= 0.25f) {
                        GameConstants.tileMap3D.inputHandler("left_button");
                        Overlay.pressed = 1;
                    } else if (x <= 0.50f) {
                        GameConstants.tileMap3D.inputHandler("middle_l_button");
                        Overlay.pressed = 2;
                    } else if (x <= 0.75f) {
                        GameConstants.tileMap3D.inputHandler("middle_r_button");
                        Overlay.pressed = 3;
                    } else {
                        GameConstants.tileMap3D.inputHandler("right_button");
                        Overlay.pressed = 4;
                    }
                }else if(y <= 0.1f){
                    float x = (TouchManager.x / SuperManager.width);
                    if(x <= 0.15f) {
                        // Pause button pressed
                        Overlay.pressed = 5;
                        sButtonPress.play();
                    }
                }
            }
            if(this.justReleased){
                this.justReleased = false;
                // Check if overlay had something selected
                if(Overlay.pressed != 0){
                    // Check for super jump cases
                    switch(Overlay.pressed){
                        // High jump released
                        case(2):
                            GameConstants.tileMap3D.inputHandler("middle_l_button_release");
                            break;
                        // Long jump released
                        case(3):
                            GameConstants.tileMap3D.inputHandler("middle_r_button_release");
                            break;
                    }
                    // Play sound to deselect
                    if(Overlay.pressed == 5) {
                        sButtonPress.play();
                    }
                }
                Overlay.pressed = 0;
                // Check if needs pausing; tapped on top of screen
                float y = (TouchManager.y / SuperManager.height);
                if(y <= 0.1f) {
                    float x = (TouchManager.x / SuperManager.width);
                    if (x <= 0.15f) {
                        // Tell Overlay to pause
                        Overlay.handleMultiTap(2);
                    }
                }
            }
        }else{
            // Get selection off of rough y
            int selection = (int)((TouchManager.y / SuperManager.height - 0.45f) * 15.5f),
                    numElements = Overlay.currentMenu.children.length - 1;
            // Make sure selection in range
            if(selection > numElements || selection < 0){
                selection = -1;
            }
            // Make sound if just pressed on selection
            if(this.justPressed && selection != -1){
                this.justPressed = false;
                // Play button press sound
                sButtonPress.play();
            }
            Overlay.currentMenu.selcted = selection;
            // Send tap release to menu
            if(this.justReleased && selection != -1){
                this.justReleased = false;
                // Play button release sound
                sButtonPress.play();
                // Fix double tap
                this.justPressed = false;
                // Send null if justReleased, no cameraDirection
                Overlay.currentMenu.tapped(null);
            }
        }
    }
}
