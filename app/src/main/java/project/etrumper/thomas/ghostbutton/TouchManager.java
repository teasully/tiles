package project.etrumper.thomas.ghostbutton;

import android.util.Log;
import android.view.MotionEvent;


/**
 * Created by thoma on 2/10/2016.
 */

public class TouchManager{

    static float x, y, dx, dy, mPreviousX, mPreviousY;
    static int pointercount, DOWNID;

    static boolean down, lastDown, performed;

    static public void init(){
        down = false;
        lastDown = false;
        performed = false;
        DOWNID = 0;
    }

    static int moveSampleIter = 0;

    static int holdDOWNID = 0;

    static float rDX, lX, rDY, lY;

    static public boolean handleTouch(MotionEvent e) {
        // Gather pointer data
        x = e.getX();
        y = e.getY();
        if(e.getPointerCount() > pointercount) {
            pointercount = e.getPointerCount();
        }
        // Switch between events
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                onDown(e);
                break;
            case MotionEvent.ACTION_UP:
                onUp(e);
                break;
            case MotionEvent.ACTION_MOVE:
                rDX = x - lX;
                lX = x;
                rDY = y - lY;
                lY = y;
                if(holdDOWNID == DOWNID){
                    break;
                }
                dx = x - mPreviousX;
                dy = y - mPreviousY;
                final float MINDISTANCE = 20f;
                if(Math.abs(dx) < MINDISTANCE && Math.abs(dy) < MINDISTANCE){
                    // Held
                    break;
                }
                holdDOWNID = DOWNID;
                //performed = true;
                if(Math.abs(dx) > Math.abs(dy)){
                    if(dx > 0){
                        GameConstants.controller.direction = ChessPiece.PieceDirection.RIGHT;
                        break;
                    }else{
                        GameConstants.controller.direction = ChessPiece.PieceDirection.LEFT;
                        break;
                    }
                }
                if(dy > 0){
                    GameConstants.controller.direction = ChessPiece.PieceDirection.DOWN;
                    break;
                }
                GameConstants.controller.direction = ChessPiece.PieceDirection.UP;
                break;
        }
        return true;
    }

    static public void onDown(MotionEvent e){
        // Set and increment some local variables
        down = true;
        mPreviousX = e.getX();
        mPreviousY = e.getY();
        moveSampleIter = 0;
        performed = false;
        DOWNID++;
        // Bug check for the controller
        GameConstants.controller.justReleased = false;
        // If is the first time onDown() is called; finger just pressed
        if (!lastDown) {
            lastDown = true;
            GameConstants.controller.justPressed = true;
            lX = mPreviousX;
            lY = mPreviousY;
        }
    }

    static public void onUp(MotionEvent e){
        down = false;
        lastDown = false;
        if(pointercount > 1) {
            GameConstants.controller.numTaps = pointercount;
            pointercount = 0;
        }
        if(!performed){
            // Send tap info
            GameConstants.controller.justReleased = true;
        }
    }
    //static float[] data = new float[]{0, 0, 0, 0, 0, 0};

    /*public static boolean modelBoundingBoxTapped(RawModel model, BoundingBox boundingBox) {
        float normalizedWinx = (x * 2 / (float) SuperManager.width) - 1,
                normalizedWiny = 1 - (y * 2 / (float) SuperManager.height);
        float[] ray = unProject(x, y),
                rayOrigin = new float[]{-normalizedWinx, normalizedWiny, 0f};
        //float distance = VectorMath.magnitude(VectorMath.subtractVectors(rayOrigin, model.globalPosition));
        float[] farRay = VectorMath.multiplyVectorBy(ray, 20);
            //data = new float[]{farRay[0], farRay[1], farRay[2], rayOrigin[0], rayOrigin[1], rayOrigin[2]};
        if (BoundingBox.IsLineInBox(rayOrigin, farRay, boundingBox)) {
            model.onTapped();
            return true;
        }
        return false;
    }

    static protected boolean lastDown(){
        if(lastDown){
            lastDown = false;
            return true;
        }
        return false;
    }

    static protected void handleTaps(Attribute ... attributes){
        if(!down){
            return;
        }
        for(Attribute attribute : attributes){
            if(attribute != null) {
                for (Attribute attribute1 : attribute.attributes) {
                    if (attribute1 != null) {
                        handleTaps(attribute1);
                    }
                }
                if(attribute.attributeType == Attribute.AttributeType.RAWMODEL){
                    int bbIndex = Attribute.getFirstAttributeTypeIndex(Attribute.AttributeType.BOUNDINGBOX, attribute.attributes);
                    if(bbIndex != -1){
                        BoundingBox box = (BoundingBox)attribute.attributes[bbIndex];
                        if(box != null){
                            modelBoundingBoxTapped((RawModel)attribute, box);
                        }
                    }
                }
            }
        }
    }

    public static float[] unProject(float winx, float winy) {
        float normalizedWinx = (winx * 2 / (float) SuperManager.width) - 1,
                normalizedwiny = 1 - (winy * 2 / (float) SuperManager.height);
        float[] clipCoord = new float[]{normalizedWinx, normalizedwiny, -1f, 1f},
                invertedProjectionM = new float[16];
        Matrix.invertM(invertedProjectionM, 0, SuperManager.projectionMatrix, 0);
        float[] eyeCoords = VectorMath.transform(invertedProjectionM, clipCoord);
        eyeCoords = new float[]{eyeCoords[0], eyeCoords[1], -1f, 0f};
        float[] invertedViewM = new float[16];
        Matrix.invertM(invertedViewM, 0, SuperManager.camera.viewMatrix, 0);
        float[] rayToWorld = VectorMath.transform(invertedViewM, eyeCoords),
                fingerRay = new float[]{rayToWorld[0], rayToWorld[1], rayToWorld[2]};
        float[] worldRay = VectorMath.normalize(fingerRay);
        return new float[]{worldRay[0], worldRay[1], worldRay[2]};
    }*/

}
