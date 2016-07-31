package project.etrumper.thomas.ghostbutton;

/**
 * Created by thoma on 6/14/2016.
 */
public class TextMenu extends ChessPiece {

    enum MenuType {
        VERTICAL_STANDARD
    }

    Text prompt, pointer;

    MenuType menuType;

    int selcted;

    Sound sSelect;

    TextMenu(String prompt, String... options) {
        super("TextMenu." + prompt, null, PieceType.STATIONARY);
        // Create prompt and make larger
        this.prompt = new Text(prompt);
        this.prompt.changeScale(0.5f);
        // Create current selection pointer
        this.pointer = new Text(">");
        // Set the default selection to 0
        this.selcted = 0;
        // Load sounds
        this.sSelect = super.loadSound(R.raw.tick1);
        // Add selections
        for (String option : options) {
            addChild(new Text(option));
        }
        // Allows custom init from overloaded functions
        this.init();
    }

    // Add function to be overloaded
    protected void init(){

    }

    @Override
    protected void update() {
        super.update();
        // Change y tilePosition to space out vertically
        float currenty = 3.5f,
                dy = -1f;
        // Update prompt
        prompt.position[0] = this.position[0] + (prompt.getLength() / 2f - 1f);
        prompt.update(this.position[1] + currenty);
        prompt.position[2] = this.position[2];
        currenty += (dy * (this.prompt.scaleModifier + 1f));
        // Update selections
        for (ChessPiece selection : this.children) {
            // Cast the Text to use custom update function for y-pos
            Text text = (Text) selection;
            text.position[0] = this.position[0] + (text.getLength() / 2f - 0.5f);
            text.update(this.position[1] + currenty);
            text.position[2] = this.position[2];
            currenty += (dy * (text.scaleModifier + 1f));
        }
        // Update pointer
        if(this.getSelection() != -1) {
            Text selection = (Text) this.children[this.getSelection()];
            this.pointer.position[0] = this.position[0] + selection.getLength() / 2f + 0.75f;
            // Add spin
            this.pointer.rotation[0] += SuperManager.deltaTime / 5.f;
            this.pointer.update(selection.position[1]);
        }else{
            // Place pointer out of screen
            pointer.update(10);
        }
        pointer.position[2] = this.position[2];
    }

    @Override
    protected void draw() {
        // Check if this is visible or nah
        if(!this.visible){
            return;
        }
        // Draw the prompt blue
        MaterialManager.changeMaterialColor("Letter",MaterialManager.getVector3fColor("Blue"));
        this.prompt.draw();
        int i = 0;
        for (ChessPiece selection : this.children) {
            // Draw the selected text green and the unselected red
            if (this.getSelection() != -1 && this.getSelection() == i) {
                MaterialManager.changeMaterialColor("Letter",MaterialManager.getVector3fColor("Green"));
                // Draw pointer when drawing selection
                this.pointer.draw();
            } else {
                MaterialManager.changeMaterialColor("Letter",MaterialManager.getVector3fColor("Red"));
            }
            // Cast the Text to use custom draw function for y-pos
            Text text = (Text) selection;
            text.draw();
            i++;
        }
    }

    protected void tapped(PieceDirection direction){
        //Check selections
        switch(this.getSelection()){
            case(0):
                this.tapped0(direction);
                break;
            case(1):
                this.tapped1(direction);
                break;
            case(2):
                this.tapped2(direction);
                break;
            case(3):
                this.tapped3(direction);
                break;
            case(4):
                this.tapped4(direction);
                break;
            case(5):
                this.tapped5(direction);
                break;
            case(6):
                this.tapped6(direction);
                break;
            case(7):
                this.tapped7(direction);
                break;
            case(8):
                this.tapped8(direction);
                break;
            // Catch unhandled taps
            default:
                LOGE("Caught " + this.getSelection());
                break;
        }
    }

    protected int getSelection(){
        return this.selcted;
    }

    protected Text handleTextSelection(ChessPiece child, PieceDirection direction){
        Text text = (Text) child;
        if(text.isTextSelection()) {
            TextSelection textSelection = (TextSelection) text;
            // Change if tap
            if (direction == null) {
                textSelection.currentSelection++;
            }
        }
        return text;
    }

    protected void tapped0(PieceDirection direction){

    }

    protected void tapped1(PieceDirection direction){

    }

    protected void tapped2(PieceDirection direction){

    }

    protected void tapped3(PieceDirection direction){

    }

    protected void tapped4(PieceDirection direction){

    }

    protected void tapped5(PieceDirection direction){

    }

    protected void tapped6(PieceDirection direction){

    }

    protected void tapped7(PieceDirection direction){

    }

    protected void tapped8(PieceDirection direction){

    }

    protected int[] getData(){
        return null;
    }

}
