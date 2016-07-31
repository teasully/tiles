package project.etrumper.thomas.ghostbutton;

/**
 * Created by thoma on 6/9/2016.
 */
public class Overlay {

    enum CurrentScreen {
        TITLE,
        OPTIONS,
        PAUSE,
        START,
        OVERLAY_ONLY
    }

    static CurrentScreen currentScreen = null,
        lastScreen = null;

    // Main menu is shown on app creation
    static TextMenu mainMenu = new TextMenu("blocked", "play", "extras") {
        // start the game
        @Override
        protected void tapped0(PieceDirection direction) {
            if (direction == null) {
                changeScreen(CurrentScreen.START);
                // Change map using options menu
                optionsMenu.tapped3(null);
                // reset and load map
                //GameConstants.tileMap.resetMap();
            }
        }
        // switch to options menu
        @Override
        protected void tapped1(PieceDirection direction) {
            if (direction == null) {
                changeScreen(CurrentScreen.OPTIONS);
            }
        }
    },
    // Options menu handles all the customizable options
    optionsMenu = new TextMenu("extras", "placeholder", "back") {
        // Init selectors
        @Override
        protected void init(){
            // camera mode
            this.children[0] = new TextSelection("camera", "behind", "beside", "inside", "overhead");
        }
        // Handle character selection
        @Override
        protected void tapped0(PieceDirection direction) {
            super.handleTextSelection(super.children[0], direction);
        }
        // Go back to last screen
        @Override
        protected void tapped1(PieceDirection direction) {
            if (direction == null) {
                changeScreen(lastScreen);
            }
        }
        // Return selection data
        @Override
        protected int[] getData() {
            Text text = (Text) this.children[0];
            return new int[]{text.getData()[0]};
        }

    },
    // Start menu is displayed before the level begins
    startMenu = new TextMenu("pregame menu", "start", "exit") {
        // Override init to scale down prompt text
        @Override
        protected void init(){
            this.prompt.changeScale(0.f);
        }
        // Check if start button pressed
        @Override
        protected void tapped0(PieceDirection direction) {
            if (direction == null) {
                changeScreen(CurrentScreen.OVERLAY_ONLY);
            }
            GameConstants.resetMap();
        }
        // Check if exit pressed
        @Override
        protected void tapped1(PieceDirection direction) {
            if (direction == null) {
                changeScreen(CurrentScreen.TITLE);
            }
        }
    },
    // Pause menu when game is paused..
    pauseMenu = new TextMenu("pause", "resume", "extras", "restart", "exit"){
        // Resume game
        @Override
        protected void tapped0(PieceDirection direction){
            if(direction == null) {
                changeScreen(CurrentScreen.OVERLAY_ONLY);
            }
        }
        // Open options menu
        @Override
        protected void tapped1(PieceDirection direction){
            if(direction == null) {
                changeScreen(CurrentScreen.OPTIONS);
            }
        }
        // Reload tilemap
        @Override
        protected void tapped2(PieceDirection direction){
            if(direction == null) {
                changeScreen(CurrentScreen.START);
            }
            GameConstants.tileMap3D = null;
        }
        // Remove tilemap and go to title (exit to main menu)
        @Override
        protected void tapped3(PieceDirection direction){
            if(direction == null) {
                changeScreen(CurrentScreen.TITLE);
            }
            GameConstants.tileMap3D = null;
        }
    },
            currentMenu = mainMenu;

    static TextMenu[] menus = new TextMenu[]{
            mainMenu, optionsMenu, startMenu, pauseMenu
    };

    static EntityTile3D LeftArrow,
            LeftButton,
            RightButton,
            RightArrow,
            PauseButton;

    public static void draw() {
        // Check to see if menus on top of game
        if (currentScreen != CurrentScreen.OVERLAY_ONLY) {
            // Draw menu
            currentMenu.draw();
        }else{
            // Draw controls
            LeftArrow.draw();
            LeftButton.draw();
            RightButton.draw();
            RightArrow.draw();
            PauseButton.draw();
        }
    }

    public static void update() {
        // Initiate title screen on boot
        if(currentScreen == null){
            changeScreen(CurrentScreen.TITLE);
        }
        if(currentScreen != CurrentScreen.OVERLAY_ONLY) {
            currentMenu.update();
        }
        LeftArrow.update();
        LeftButton.update();
        RightButton.update();
        RightArrow.update();
        PauseButton.update();
    }

    protected static void handleMultiTap(final int number){
        // If is double tap, and OVERLAY_ONLY, then pause game
        if(number == 2) {
            if (lastScreen == CurrentScreen.OVERLAY_ONLY) {
                changeScreen(CurrentScreen.OVERLAY_ONLY);
            }else if (currentScreen == CurrentScreen.OVERLAY_ONLY) {
                changeScreen(CurrentScreen.PAUSE);
            }
        }
    }

    private static void changeScreen(CurrentScreen newScreen){
        // Save the scene being changed
        lastScreen = currentScreen;
        // Change the scene
        currentScreen = newScreen;
        // Change the current TextMenu being updated and drawn based on passed parameter
        if(currentScreen == CurrentScreen.TITLE){
            currentMenu = mainMenu;
        }else if(currentScreen == CurrentScreen.OPTIONS){
            currentMenu = optionsMenu;
        }else if(currentScreen == CurrentScreen.START){
            currentMenu = startMenu;
        }else if(currentScreen == CurrentScreen.PAUSE){
            currentMenu = pauseMenu;
        }
        // Update current menu to prevent graphical glitch
        currentMenu.update();
        // Set current selection to 0
        currentMenu.selcted = -1;
        /*/ Fix camera rotation and focus
        if(currentScreen == CurrentScreen.OVERLAY_ONLY){
            GameConstants.camera.startEase(savedPosition, 500);
            GameConstants.camera.easeTargetTo(savedTarget, 500);
            GameConstants.camera.easeUpVectorTo(new float[]{0, 0f, 1f}, 500);
        }else{
            // Focus camera onto menu
            currentMenu.tilePosition = new float[]{savedTarget[0], savedTarget[1], savedTarget[2] - 3f};
            GameConstants.camera.startEase(new float[]{currentMenu.tilePosition[0], currentMenu.tilePosition[1], currentMenu.tilePosition[2] - GameConstants.menuCameraZoom}, 500);
            GameConstants.camera.easeTargetTo(currentMenu.tilePosition, 500);
            GameConstants.camera.easeUpVectorTo(new float[]{0, 1f, 0f}, 500);
        }*/
    }

    static int pressed = 0;

    public static void init() {
        // Set menus' positions
        for(TextMenu menu : menus){
            menu.position = new float[]{0f, 0f, 0f + GameConstants.menuCameraZoom};
        }
        // Add specific functions to controls
        LeftArrow = new EntityTile3D("Scenery1", 1, 1){
            @Override
        protected void draw(){
                //MaterialManager.saveMaterial("Arrow");
                Vector3f color;
                if(pressed == 1){
                    color = new Vector3f(1f, 0f, 0f);
                }else{
                    color = new Vector3f(0f, 1f, 0f);
                }
                MaterialManager.changeMaterialColor("Arrow", color);
                super.draw();
            }
        };
        LeftArrow.currentAnimation = LeftArrow.addAnimation("left_arrow");
        LeftArrow.position = new float[]{2.5f, -4f, 5f};
        LeftArrow.rotation[2] = 180f;
        LeftButton = new EntityTile3D("Scenery1", 1, 1){
            @Override
            protected void draw(){
                Vector3f color;
                if(pressed == 2){
                    color = new Vector3f(1f, 0f, 0f);
                }else{
                    color = new Vector3f(0f, 1f, 0f);
                }
                MaterialManager.changeMaterialColor("Button", color);
                super.draw();
            }
        };
        LeftButton.currentAnimation = LeftButton.addAnimation("button");
        LeftButton.position = new float[]{0.8f, -4f, 5f};
        RightButton = new EntityTile3D("Scenery1", 1, 1){
            @Override
            protected void draw(){
                Vector3f color;
                if(pressed == 3){
                    color = new Vector3f(1f, 0f, 0f);
                }else{
                    color = new Vector3f(0f, 1f, 0f);
                }
                MaterialManager.changeMaterialColor("Button", color);
                super.draw();
            }
        };
        RightButton.currentAnimation = RightButton.addAnimation("button");
        RightButton.position = new float[]{-0.8f, -4f, 5f};
        RightArrow = new EntityTile3D("Scenery1", 1, 1){
            @Override
            protected void draw(){
                //MaterialManager.saveMaterial("Arrow");
                Vector3f color;
                if(pressed == 4){
                    color = new Vector3f(1f, 0f, 0f);
                }else{
                    color = new Vector3f(0f, 1f, 0f);
                }
                MaterialManager.changeMaterialColor("Arrow", color);
                super.draw();
            }
        };
        RightArrow.currentAnimation = RightArrow.addAnimation("right_arrow");
        RightArrow.position = new float[]{-2.5f, -4f, 5f};
        RightArrow.rotation[2] = 180f;
        PauseButton = new EntityTile3D("Scenery1", 1, 1){
            @Override
            protected void draw(){
                Vector3f color;
                if(pressed == 5){
                    color = new Vector3f(1f, 0f, 0f);
                }else{
                    color = new Vector3f(0f, 1f, 0f);
                }
                MaterialManager.changeMaterialColor("Button", color);
                super.draw();
            }
        };
        PauseButton.currentAnimation = PauseButton.addAnimation("button");
        PauseButton.position = new float[]{5f, 8f, 9f};
    }

    protected static int getRemainder(int value, int size){
        // Get modulo
        int result = value % size;
        // If negative, subtract frm size to get correct return value
        if(result < 0){
            result = size + result;
        }
        return result;
    }

}
