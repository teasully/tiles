package project.etrumper.thomas.ghostbutton;

import android.content.Context;
import android.util.Log;

import java.util.Random;

/**
 * Created by thoma on 2/10/2016.
 */
public class SuperManager extends Manager {

    static Context context;

    static int width, height,
            drawCount;  // # of frames drawn since beginning
    static long desiredWait, currentWait,  // desiredWait = length between each frame | currentWait = timer for prior
            FPS,    // Desired frames per second
            currentSeconds, lastSeconds,    // Used for delta time = (current - last)
            globalTime; // Time since run began
    static long deltaTime;  // Time between updates
    static boolean shouldDraw;

    static float[] projectionMatrix;
    final static String defaultProgramName = "basic";

    //static TiltManager tiltManager;

    static BasicEntity[] drawElements;

    SuperManager(Context context) {
        super("SuperManager");
        SuperManager.context = context;

        drawCount = 0;

        TouchManager.init();
        SoundManager.init();
        MaterialManager.init();
        Overlay.init();
        //GestureManager.init();
        //tiltManager = TiltManager.init();

        LightManager.init(1);   // 4 MAXLIGHTS
        float brightness = 1f;
        LightManager.addLight(new Light("Light 0", new float[]{brightness, brightness, brightness}));
        LightManager.getLights()[0].position = new float[]{0, 0f, -4};

        //LightManager.addLight(new Light("Light 1", new float[]{10f, 10f, 10f}));

        MeshManager.registerLibraries(
                "cubemonster_library",
                "scenery1_library",
                "letters_library");

        GameConstants.camera = new Camera();

        FPS = 60;
        desiredWait = 1000 / FPS;
        currentWait = 0;
        lastSeconds = System.currentTimeMillis();
        globalTime = 0;

        shouldDraw = false;

        projectionMatrix = new float[16];

        GameConstants.init();
    }

    static Random r = new Random(SuperManager.globalTime);

    protected static void update() {
        // Calculate frame maths
        try {
            frameMath();
        } catch (MandatoryException e) {
            Log.e("SuperManager", e.toString());
        }
        // Update draw elements
        if (drawElements != null) {
            for (BasicEntity entity : drawElements) {
                entity.update();
            }
        }
        // If playing, update tile map elements
        if(Overlay.currentScreen == Overlay.CurrentScreen.OVERLAY_ONLY) {
            //GameConstants.tileMap.update();
            GameConstants.tileMap3D.update();
        }
        // Update game constants and lights
        GameConstants.update();
        LightManager.updateLights();
        // Update overlay
        Overlay.update();
    }

    protected static boolean shouldDrawF(){
        if(shouldDraw){
            shouldDraw = false;
            currentWait = 0;
            return true;
        }
        return false;
    }

    protected static void onResume(){
        //tiltManager.onResume();
        //Log.e("SuperManager", "Resumed");
    }

    protected static void onPause(){
        //tiltManager.onPause();
        //Log.e("SuperManager", "Paused");
    }

    protected static void onSurfaceChanged(int width, int height){
        SuperManager.width = width;
        SuperManager.height = height;

        GameConstants.camera.frustM(projectionMatrix, width, height);    // Sets up projection matrix with dimensions
    }

    private static void frameMath() throws MandatoryException{ // Calculates delta time and if the program should draw
        currentSeconds = System.currentTimeMillis();
        deltaTime = currentSeconds - lastSeconds;
        lastSeconds = currentSeconds;

        if(deltaTime < 0){  // Sould never happen..
            deltaTime = 0;
            throw new MandatoryException("Delta time less than zero");
        }else if(deltaTime > 200){
            throw new MandatoryException("Delta time spike");
        }
        if(false){
            float median = getDeltaMedian(deltaTime);
            Log.e("Supermanager", String.format("Average DT: %f", median));
            int accur = 15;
            if(median != -1 && deltaTime > median + accur){
                Log.e("Supermanager", String.format("Delta time %d over median %f plus %d", deltaTime, median, accur));
            }
        }
        globalTime += deltaTime;    // time since program start
        if(!shouldDraw) {
            currentWait += deltaTime;
            if (currentWait >= desiredWait) {
                shouldDraw = true;
                drawCount++;    // Used for FPS
            }
        }
    }

    final static int MAXBUF = 100;
    static float[] deltaBuffer = new float[MAXBUF];
    static int counter = 0;
    static float getDeltaMedian(long deltaTime){
        deltaBuffer[counter++ % MAXBUF] = deltaTime;
        if(counter >= MAXBUF){
            float total = 0f;
            for(float num : deltaBuffer){
                total += num;
            }
            return total / MAXBUF;
        }
        return -1f;
    }

    /*static float[] getAttributeMVP(BasicEntity entity, int attributeIndex, Camera camera){
        float[] modelMatrix = entity.getAttributeModelMatrix(attributeIndex);
        if(modelMatrix.length == 0){
            return new float[]{};
        }
        float[] modelToView = new float[16],
                viewToProjection = new float[16];
        Matrix.multiplyMM(modelToView, 0, camera.viewMatrix, 0, modelMatrix, 0);
        Matrix.multiplyMM(viewToProjection, 0, projectionMatrix, 0, modelToView, 0);
        return viewToProjection;
    }*/

    static void addDrawElement(BasicEntity entity){
        if (entity != null) {
            if (drawElements == null || drawElements.length == 0) {
                drawElements = new BasicEntity[]{entity};
                return;
            }
            for (BasicEntity centity : drawElements) {
                if (centity == null || centity.ID == entity.ID) {
                    return;//throw new MandatoryException(centity == null ? "Null centity in addDrawElement()" : "Trying to add entity twice to addDrawElemnt()");
                }
            }
            BasicEntity[] newDrawElements = new BasicEntity[drawElements.length + 1];
            int i = 0;
            for (BasicEntity centity : drawElements) {
                newDrawElements[i] = drawElements[i++];
            }
            newDrawElements[i] = entity;
            drawElements = newDrawElements;
        }
        //throw new MandatoryException("Passing null entity to addDrawElement()");
    }

    static void removeDrawElement(BasicEntity entity) throws MandatoryException{
        if(entity != null){
            BasicEntity[] newDrawElements = new BasicEntity[drawElements.length-1];
            int i = 0, f = 0;   // f used for error handling
            for(BasicEntity centity : drawElements){
                if(centity.ID != entity.ID){
                    newDrawElements[i++] = centity;
                }else{
                    f = 1;
                }
            }
            if(f == 0){ // Means no object removed
                throw new MandatoryException("Trying to removeDrawElement() with entity that is not in drawElements");
            }
            drawElements = newDrawElements;
        }
        throw new MandatoryException("Passing null entity to removeDrawElement()");
    }
}
