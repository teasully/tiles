package project.etrumper.thomas.ghostbutton;

/**
 * Created by thoma on 7/31/2016.
 * Property of boxedworks.
 */
public class ColorPalette extends Logable {

    Vector3f[] colors;

    ColorPalette(String name, Vector3f ... colors){
        super(name);

        this.colors = colors;
    }

    public Vector3f getColor(int index){
        if(index < 0 || index >= this.colors.length){
            LOGE("Index out of range");
            return null;
        }
        return this.colors[index];
    }

    public void addColors(Vector3f ... colors){
        if(colors == null || colors.length == 0){
            return;
        }
        if(this.colors == null){
            this.colors = new Vector3f[0];
        }
        Vector3f[] temp = new Vector3f[this.colors.length + colors.length];
        System.arraycopy(this.colors, 0, temp, 0, this.colors.length);
        System.arraycopy(colors, 0, temp, this.colors.length, colors.length);
        this.colors = temp;
    }

    public void addColors(Material ... materials){
        Vector3f[] temp = new Vector3f[materials.length];
        for(int i = 0; i < materials.length; i++){
            temp[i] = new Vector3f(materials[i].lightProperties.diffuse);
        }
        this.addColors(temp);
    }

}
