package project.etrumper.thomas.ghostbutton;

import android.util.Log;

import org.w3c.dom.Attr;

/**
 * Created by thoma on 2/13/2016.
 */

public class Attribute {

    enum AttributeType{
        EASE,
        RAWMODEL,
        BASICENTITY,
        CAMERA,
        LIGHT,
        BOUNDINGBOX,
        PROJECTILE,
        RAYPROJECTILE,
        ANIMATION
    }

    String _TAG;

    protected float[] data;

    AttributeType attributeType;

    Attribute[] attributes;

    Attribute(int dataLength, String _TAG, AttributeType attributeType){
        this._TAG = _TAG;

        this.attributeType = attributeType;

        data = new float[dataLength];

        //attributes = new Attribute[]{};

        this.attributes = new Attribute[0];
    }

    protected float[] update(float[] data){
        return new float[]{};
    }

    protected float[] getData(){
        return getData(new float[]{});
    }

    protected float[] getData(float[] data){
        return this.data;
    }

    public static int getFirstAttributeTypeIndex(AttributeType type, Attribute[] attributes){
        for(int i = 0; i < attributes.length; i++){
            if(attributes[i] != null && attributes[i].attributeType == type){
                return i;
            }
        }
        return -1;  // -1 on failure
    }

    protected Attribute getAttributeByType(AttributeType type){
        Attribute attribute = null;
        int index = Attribute.getFirstAttributeTypeIndex(type, this.attributes);
        if(index != -1){
            attribute = this.attributes[index];
        }
        return attribute;
    }

    protected int addAttribute(Attribute attribute){
        int index = nextAvailableAttributeSlot();
        if(index != -1){
            addAttribute(index, attribute);
            return index;
        }
        //LOGE("No available attribute slots");
        Attribute[] newAtrributes = new Attribute[this.attributes.length + 1];
        System.arraycopy(this.attributes, 0, newAtrributes, 0, this.attributes.length);
        newAtrributes[this.attributes.length] = attribute;
        this.attributes = newAtrributes;
        return this.attributes.length - 1;
    }

    protected void addAttribute(int index, Attribute attribute){
        if(index > this.attributes.length - 1){
            // Trying to add more than max attributes
            return;
        }
        this.attributes[index] = attribute;
    }

    protected int nextAvailableAttributeSlot(){
        for(int i = 0; i < this.attributes.length; i++){
            if(this.attributes[i] == null){
                return i;
            }
        }
        return -1;  //No slots left
    }

    protected void LOGE(String message){
        Log.e(_TAG, message);
    }

}
