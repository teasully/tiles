package project.etrumper.thomas.ghostbutton;

/**
 * Created by thoma on 6/14/2016.
 */
public class Text extends ChessPiece {

    float letter_spacing = 1.1f,
        scaleModifier;

    Text(String word) {
        super(word, null, PieceType.STATIONARY);
        // Handle letters
        for (char c : word.toCharArray()) {
            switch (Character.toLowerCase(c)) {
                case ('a'):
                case ('b'):
                case ('c'):
                case ('d'):
                case ('e'):
                case ('f'):
                case ('g'):
                case ('h'):
                case ('i'):
                case ('j'):
                case ('k'):
                case ('l'):
                case ('m'):
                case ('n'):
                case ('o'):
                case ('p'):
                case ('q'):
                case ('r'):
                case ('s'):
                case ('t'):
                case ('u'):
                case ('v'):
                case ('w'):
                case ('x'):
                case ('y'):
                case ('z'):
                    addChild(new Letter("Letters", c + ""));
                    break;
                case(' '):
                    addChild(new Letter(null, null));
                    break;
                case('>'):
                    addChild(new Letter("Letters", "pointer"));
                    break;
                default:
                    LOGE(String.format("Letter %c does not exist in library", c));
            }
        }
        //this.position = new float[]{this.getLength() / 2f, 0f, 5f};
        this.position[2] = 5.f;
        this.rotation[1] = 180f;

        for (ChessPiece child : this.children) {
            child.rotation = this.rotation;
        }

        this.changeScale(-0.2f); // Make text smaller
    }

    protected void changeScale(float newScale){
        this.scaleModifier = 1.f + newScale; // Messed up scaling needs fixing
        for(ChessPiece child : this.children) {
            child.scale = new float[]{newScale, newScale, newScale};
        }
    }

    protected float getLength() {
        return letter_spacing * this.children.length * this.scaleModifier;
    }

    protected void update(float ypos){
        super.update();
        this.position[1] = ypos;
        // Space out letters
        int i = 0;
        for(ChessPiece child : this.children){
            child.position = new float[]{this.position[0] - (i * letter_spacing * this.scaleModifier), this.position[1], this.position[2]};
            i++;
        }
    }

    protected boolean isTextSelection(){
        return (this.TAG.endsWith("TEXTSELECTION"));
    }

    protected int[] getData(){
        return null;
    }

}
