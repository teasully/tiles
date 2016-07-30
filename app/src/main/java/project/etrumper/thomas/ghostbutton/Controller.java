package project.etrumper.thomas.ghostbutton;

/**
 * Created by thoma on 4/29/2016.
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

        this.addDrawElement();  // For updating, not drawing
    }

    static int inputHandlerID = 0;

    @Override
    protected void update() {
        if (Overlay.currentScreen == Overlay.CurrentScreen.OVERLAY_ONLY) {
            // Handle game input
            if (this.justPressed) {
                this.justPressed = false;
                float y = (TouchManager.y / SuperManager.height);
                Overlay.pressed = 0;
                if (y >= 0.8f) {
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
                    }
                }
                inputHandlerID++;
            }
            if(this.justReleased){
                this.justReleased = false;
                // Set overlay button # pressed to 0 for none
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
            /*if (this.cameraDirection != null) {
                if (this.cameraDirection == ChessPiece.PieceDirection.UP) {
                    GameConstants.player.queueTask(ChessPiece.PieceTask.UP);
                } else if (this.cameraDirection == ChessPiece.PieceDirection.DOWN) {
                    GameConstants.player.queueTask(ChessPiece.PieceTask.DOWN);
                }
                this.cameraDirection = null;
            }*/
        }else{
            // Get selection off of rough y
            int selection = (int)((TouchManager.y / SuperManager.height - 0.45f) * 15.5f),
                    numElements = Overlay.currentMenu.children.length - 1;
            // Make sure selection in range
            if(selection > numElements || selection < 0){
                selection = -1;
            }
            int oldSelection = Overlay.currentMenu.selcted;
            Overlay.currentMenu.selcted = selection;
            // Only play sound if new selection
            if(oldSelection != selection) {
                Overlay.currentMenu.sMove.play();
            }
            // Send tap to menu
            if(this.justReleased){
                this.justReleased = false;
                // Fix double tap
                this.justPressed = false;
                // Send null if justReleased, no cameraDirection
                Overlay.currentMenu.tapped(null);
            }
        }
        /*/ Handle multiple taps
        if(this.numTaps > 1){
            this.justPressed = false;
            this.justReleased = false;
            // Change to pause screen
            Overlay.handleMultiTap(this.numTaps);
            TouchManager.performed = true;
            this.numTaps = 0;
        }*/
    }
}
