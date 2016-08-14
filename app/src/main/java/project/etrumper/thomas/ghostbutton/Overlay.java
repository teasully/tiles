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
        OVERLAY_ONLY,
        EDITOR_START,
        EDITOR_LOAD,
        EDITOR_OPTIONS,
        EDITOR_FILE,
        EDITOR_SETTINGS,
        EDITOR_SAVE,
        EDITOR_QUIT,
        PLAYTEST_PAUSE
    }

    static boolean showControls;

    static CurrentScreen currentScreen = null,
            lastScreen = null;

    // Main menu is shown on app creation
    static TextMenu mainMenu = new TextMenu("blocked", "play", "editor", "extras") {
        @Override
        protected void init(){
            this.children[0] = new Text("play"){
                @Override
                protected void draw(){
                    MaterialManager.changeMaterialColor("Letter", MaterialManager.getVector3fColor("Grey"));
                    super.draw();
                }
            };
        }
        // start the game
        @Override
        protected void tapped0() {
            //changeScreen(CurrentScreen.START);
        }

        // Go to editor start screen
        @Override
        protected void tapped1() {
            changeScreen(CurrentScreen.EDITOR_START);
        }

        // switch to extras menu
        @Override
        protected void tapped2(PieceDirection direction) {
            if (direction == null) {
                changeScreen(CurrentScreen.OPTIONS);
            }
        }
    },
    // Options menu handles all the customizable options
    optionsMenu = new TextMenu("extras", "placeholder", "back") {
        // Init selectors
        @Override
        protected void init() {
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
        protected void tapped1() {
            // Save options
            GameConstants.saveGame();
            // Return to last screen
            changeScreen(lastScreen);
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
        protected void init() {
            this.prompt.changeScale(0.f);
        }

        // Check if start button pressed
        @Override
        protected void tapped0(PieceDirection direction) {
            if (direction == null) {
                changeScreen(CurrentScreen.OVERLAY_ONLY);
            }
            GameConstants.editor.setCurrentMap(Editor.decodeMapBase64(""));
            GameConstants.editor.mode = Editor.EditorMode.SOLVING;
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
    pauseMenu = new TextMenu("pause", "resume", "extras", "restart", "exit") {
        // Resume game
        @Override
        protected void tapped0(PieceDirection direction) {
            if (direction == null) {
                changeScreen(CurrentScreen.OVERLAY_ONLY);
            }
        }

        // Open options menu
        @Override
        protected void tapped1(PieceDirection direction) {
            if (direction == null) {
                changeScreen(CurrentScreen.OPTIONS);
            }
        }

        // Reload tilemap
        @Override
        protected void tapped2(PieceDirection direction) {
            if (direction == null) {
                changeScreen(CurrentScreen.START);
            }
            GameConstants.tileMap3D = null;
        }

        // Remove tilemap and go to title (exit to main menu)
        @Override
        protected void tapped3() {
            changeScreen(CurrentScreen.TITLE);
            GameConstants.editor.getInput("quit");
        }
    },
    // Menu seen after clicking "editor" from main menu
    editorStartMenu = new TextMenu("editor", "new", "load", "delete", "back") {
        @Override
        protected void tapped0() {
            // Open map from scratch dialogue
            GameConstants.editor.dialogueNewFromScratch();
        }
        @Override
        protected void tapped1() {
            // Change to load menu
            changeScreen(CurrentScreen.EDITOR_LOAD);
        }
        @Override
        protected void tapped2() {
            // Open delete prompt
            if(GameConstants.editor.getMapsBase64().length == 0) {
                Logable.alertBoxSimple("Error", "No maps found on device", "Nice.");
                return;
            }
            GameConstants.editor.dialogueDeleteMaps();
        }
        @Override
        protected void tapped3() {
            // Return to title menu
            changeScreen(CurrentScreen.TITLE);
        }
    },
    // Options menu for editor
    editorOptionsMenu = new TextMenu("options", "file", "settings", "back") {
        @Override
        protected void tapped0() {
            // Change to file menu
            changeScreen(CurrentScreen.EDITOR_FILE);
        }

        @Override
        protected void tapped1() {
            // Open map settings
            changeScreen(CurrentScreen.EDITOR_SETTINGS);
        }

        @Override
        protected void tapped2() {
            // Return to editing
            changeScreen(CurrentScreen.OVERLAY_ONLY);
        }
    },
    // Changes map setting such as name, size, color palette
    editorSettingsMenu = new TextMenu("settings", "name", "back"){
        @Override
        protected void init(){
            // Create text selection
            TextSelection ts = new TextSelection("name", "placeholder");
            this.children[0] = ts;
        }
        @Override
        protected void tapped0(){
            String newName = Logable.getUserString("Enter new map name", "Submit", "Cancel");
            if(newName == null){
                return;
            }
            GameConstants.editor.changeMapName(newName);
        }
        @Override
        protected void tapped1(){
            changeScreen(lastScreen);
        }
    },
    // Gives user ability to create new map, load a map, and exit
    editorFileMenu = new TextMenu("file", "new", "load", "save", "quit", "back") {
        @Override
        protected void tapped0() {
            // Open map from scratch dialogue
            GameConstants.editor.dialogueNewFromScratch();
        }

        @Override
        protected void tapped1() {
            // Go to load menu
            changeScreen(CurrentScreen.EDITOR_LOAD);
        }

        @Override
        protected void tapped2() {
            // Go to save menu
            changeScreen(CurrentScreen.EDITOR_SAVE);
        }

        @Override
        protected void tapped3() {
            // If saved, exit, else go to question menu
            if(GameConstants.editor.isSaved()){
                GameConstants.editor.getInput("quit");
                GameConstants.editor.mode = null;
                changeScreen(CurrentScreen.TITLE);
            }else{
                changeScreen(CurrentScreen.EDITOR_QUIT);
            }
        }

        @Override
        protected void tapped4() {
            // Return editor options
            changeScreen(CurrentScreen.EDITOR_OPTIONS);
        }
    },
    // Different ways of loading a map
    editorLoadMenu = new TextMenu("load", "from device", "from clipboard", "back"){
        @Override
        protected void tapped0(){
            // Check if any maps were loaded
            if (GameConstants.editor.getMapsBase64().length == 0) {
                Logable.alertBoxSimple("Error", "No maps found on device", "Alright");
                return;
            }
            // Open list of saved maps
            GameConstants.editor.dialogueLoadFromDevice();
        }
        @Override
        protected void tapped1(){
            // Use clipboard to paste Base64 encoded map data
            GameConstants.editor.loadMapFromPaste();
        }
        @Override
        protected void tapped2(){
            // Return to last screen
            changeScreen(lastScreen);
        }
    },
    // Prompts user how to save map
    editorSaveMenu = new TextMenu("save", "to device", "to clipboard", "back") {
        @Override
        protected void tapped0() {
            // Add to base64 maps and save
            GameConstants.editor.getInput("save_device");
        }

        @Override
        protected void tapped1() {
            // Copy map base64 to clipboard
            GameConstants.editor.getInput("save_copy");
        }

        @Override
        protected void tapped2() {
            // Return to last screen
            changeScreen(lastScreen);
        }
    },
    // Makes sure user saves changes
    editorQuitMenu = new TextMenu("lose changes", "yes", "no"){
        @Override
        protected void init(){
            this.prompt.changeScale(0.f);
        }
        @Override
        protected void tapped0() {
            // Exit to main menu
            GameConstants.editor.getInput("quit");
            GameConstants.editor.mode = null;
            changeScreen(CurrentScreen.TITLE);
        }
        @Override
        protected void tapped1() {
            // Return to editor
            changeScreen(lastScreen);
        }
    },
    // Pause menu to exit play-testing
    playTestPauseMenu = new TextMenu("pause", "restart", "back"){
        @Override
        protected void tapped0(){
            // Restart map
            GameConstants.editor.getInput("restart_playtest");
        }
        @Override
        protected void tapped1(){
            // Return to playtest
            changeScreen(lastScreen);
        }
    },
            currentMenu = mainMenu;

    static TextMenu[] menus = new TextMenu[]{
            mainMenu, optionsMenu, startMenu, pauseMenu,
            editorOptionsMenu, editorFileMenu, editorSettingsMenu, editorSaveMenu,
            editorStartMenu, editorLoadMenu, editorQuitMenu,
            playTestPauseMenu
    };

    static EntityTile3D LeftArrow,
            LeftButton,
            RightButton,
            RightArrow,
            PauseButton,
            background;

    public static void draw() {
        // Check to see if menus on top of game
        if (currentScreen != CurrentScreen.OVERLAY_ONLY) {
            // Draw menu
            background.draw();
            currentMenu.draw();
        }
        else if (showControls) {
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
        if (currentScreen == null) {
            changeScreen(CurrentScreen.TITLE);
        }
        if (currentScreen != CurrentScreen.OVERLAY_ONLY) {
            currentMenu.update();
            background.update();
        } else if (showControls) {
            LeftArrow.update();
            LeftButton.update();
            RightButton.update();
            RightArrow.update();
            PauseButton.update();
        }
    }

    protected static void handleMultiTap(final int number) {
        // If is double tap, and OVERLAY_ONLY, then pause game
        if (number == 2) {
            if (lastScreen == CurrentScreen.OVERLAY_ONLY) {
                changeScreen(CurrentScreen.OVERLAY_ONLY);
            } else if (currentScreen == CurrentScreen.OVERLAY_ONLY) {
                changeScreen(CurrentScreen.PAUSE);
            }
        } else if (number == 3) {
            changeScreen(CurrentScreen.EDITOR_OPTIONS);
        } else if (number == 4) {
            changeScreen(CurrentScreen.PLAYTEST_PAUSE);
        }
    }

    public static void changeScreen(CurrentScreen newScreen) {
        // Save the scene being changed
        if(lastScreen != currentScreen) {
            lastScreen = currentScreen;
        }
        // Change the scene
        currentScreen = newScreen;
        // Change the current TextMenu being updated and drawn based on passed parameter
        if (currentScreen == CurrentScreen.TITLE) {
            currentMenu = mainMenu;
        } else if (currentScreen == CurrentScreen.OPTIONS) {
            currentMenu = optionsMenu;
        } else if (currentScreen == CurrentScreen.START) {
            currentMenu = startMenu;
        } else if (currentScreen == CurrentScreen.PAUSE) {
            currentMenu = pauseMenu;
        }else if (currentScreen == CurrentScreen.EDITOR_OPTIONS){
            currentMenu = editorOptionsMenu;
        }else if(currentScreen == CurrentScreen.EDITOR_FILE){
            currentMenu = editorFileMenu;
        }else if(currentScreen == CurrentScreen.EDITOR_SETTINGS){
            currentMenu = editorSettingsMenu;
        }else if(currentScreen == CurrentScreen.EDITOR_SAVE){
            currentMenu = editorSaveMenu;
        }else if(currentScreen == CurrentScreen.EDITOR_START){
            currentMenu = editorStartMenu;
        }else if(currentScreen == CurrentScreen.EDITOR_LOAD){
            currentMenu = editorLoadMenu;
        }else if(currentScreen == CurrentScreen.EDITOR_QUIT){
            currentMenu = editorQuitMenu;
        }else if(currentScreen == CurrentScreen.PLAYTEST_PAUSE){
            currentMenu = playTestPauseMenu;
        }
        // Update current menu to prevent graphical glitch
        currentMenu.update();
        // Set current selection to 0
        currentMenu.selcted = -1;
    }

    static int pressed = 0;

    public static void init() {
        showControls = false;
        // Set menus' positions
        for (TextMenu menu : menus) {
            menu.position = new float[]{0f, 0f, 0f + GameConstants.menuCameraZoom};
        }
        // Add specific functions to controls
        LeftArrow = new EntityTile3D("Scenery1") {
            @Override
            protected void draw() {
                //MaterialManager.saveMaterial("Arrow");
                Vector3f color;
                if (pressed == 1) {
                    color = new Vector3f(1f, 0f, 0f);
                } else {
                    color = new Vector3f(0f, 1f, 0f);
                }
                MaterialManager.changeMaterialColor("Arrow", color);
                super.draw();
            }
        };
        LeftArrow.currentAnimation = LeftArrow.addAnimation("left_arrow");
        LeftArrow.position = new float[]{2.5f, -4f, 5f};
        LeftArrow.rotation[2] = 180f;
        LeftButton = new EntityTile3D("Scenery1") {
            @Override
            protected void draw() {
                Vector3f color;
                if (pressed == 2) {
                    color = new Vector3f(1f, 0f, 0f);
                } else {
                    color = new Vector3f(0f, 1f, 0f);
                }
                MaterialManager.changeMaterialColor("Button", color);
                super.draw();
            }
        };
        LeftButton.currentAnimation = LeftButton.addAnimation("button");
        LeftButton.position = new float[]{0.8f, -4f, 5f};
        RightButton = new EntityTile3D("Scenery1") {
            @Override
            protected void draw() {
                Vector3f color;
                if (pressed == 3) {
                    color = new Vector3f(1f, 0f, 0f);
                } else {
                    color = new Vector3f(0f, 1f, 0f);
                }
                MaterialManager.changeMaterialColor("Button", color);
                super.draw();
            }
        };
        RightButton.currentAnimation = RightButton.addAnimation("button");
        RightButton.position = new float[]{-0.8f, -4f, 5f};
        RightArrow = new EntityTile3D("Scenery1") {
            @Override
            protected void draw() {
                //MaterialManager.saveMaterial("Arrow");
                Vector3f color;
                if (pressed == 4) {
                    color = new Vector3f(1f, 0f, 0f);
                } else {
                    color = new Vector3f(0f, 1f, 0f);
                }
                MaterialManager.changeMaterialColor("Arrow", color);
                super.draw();
            }
        };
        RightArrow.currentAnimation = RightArrow.addAnimation("right_arrow");
        RightArrow.position = new float[]{-2.5f, -4f, 5f};
        RightArrow.rotation[2] = 180f;
        PauseButton = new EntityTile3D("Scenery1") {
            @Override
            protected void draw() {
                Vector3f color;
                if (pressed == 5) {
                    color = new Vector3f(1f, 0f, 0f);
                } else {
                    color = new Vector3f(0f, 1f, 0f);
                }
                MaterialManager.changeMaterialColor("Button", color);
                super.draw();
            }
        };
        PauseButton.currentAnimation = PauseButton.addAnimation("button");
        PauseButton.position = new float[]{5f, 8f, 9f};
        background = new EntityTile3D("Scenery1") {
            @Override
            protected void draw() {
                Vector3f color = new Vector3f(.2f, .1f, 0.1f);
                MaterialManager.changeMaterialColor("Wall", color);
                super.draw();
            }
        };
        background.currentAnimation = background.addAnimation("wallstandard1");
        background.position = new float[]{0f, 0f, 13f};
        background.scale = new float[]{15f, 0f, 25f};
    }

    protected static int getRemainder(int value, int size) {
        // Get modulo
        int result = value % size;
        // If negative, subtract frm size to get correct return value
        if (result < 0) {
            result = size + result;
        }
        return result;
    }

}
