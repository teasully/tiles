package project.etrumper.thomas.ghostbutton;

/**
 * Created by thoma on 6/16/2016.
 */
public class TextSelection extends Text {

    Text[] options;

    int currentSelection;

    float word_spacing = 0.5f;

    TextSelection(String prompt, String ... selections){
        super(prompt);
        this.TAG = this.TAG.concat(".TEXTSELECTION");

        this.options = new Text[selections.length];
        for(int i = 0; i < selections.length; i++){
            this.options[i] = new Text(selections[i]);
        }

        this.currentSelection = 0;
    }

    @Override
    protected void update(float ypos) {
        super.update(ypos);
        this.position[1] = ypos;
        // Space out letters
        int i = 0;
        for (ChessPiece child : this.children) {
            child.position = new float[]{this.position[0] - (i * letter_spacing * super.scaleModifier), this.position[1], this.position[2]};
            i++;
        }
        // Draw current selection
        this.options[Overlay.getRemainder(currentSelection, this.options.length)].update(ypos);
        // Space out letters
        i = 0;
        for (ChessPiece child : this.options[Overlay.getRemainder(currentSelection, this.options.length)].children) {
            child.position = new float[]{this.position[0] - super.getLength() - (i * letter_spacing * super.scaleModifier) - this.word_spacing * super.scaleModifier, this.position[1], this.position[2]};
            i++;
        }
    }

    @Override
    protected void draw(){
        if(MaterialManager.getColor("Letter") == WeaponColor.GREEN) {
            MaterialManager.changeMaterialColor("Letter", WeaponColor.GREEN);
        }
        this.options[Overlay.getRemainder(currentSelection, this.options.length)].draw();

        MaterialManager.changeMaterialColor("Letter", WeaponColor.RED);
        super.draw();
    }

    @Override
    protected int[] getData(){
        return new int[] {Overlay.getRemainder(this.currentSelection, this.options.length)};
    }

    protected float getOptionsLength(){
        return this.options[Overlay.getRemainder(currentSelection, this.options.length)].getLength();
    }

    @Override
    protected float getLength(){
        return super.getLength() + this.word_spacing * super.scaleModifier + this.getOptionsLength(); // Total size of phrase
    }


}
