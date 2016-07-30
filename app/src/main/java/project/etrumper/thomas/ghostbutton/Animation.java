package project.etrumper.thomas.ghostbutton;

import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
/**
 * Created by thoma on 3/30/2016.
 */
public class Animation {

    String TAG;

    int[] animationIndex;
    long frameTime, // # of milli between frames
            currentTime;    // Current milli since start of anim
    boolean isPlaying,
            interpolate,
            loaded;
    float deltaTimeModifier;

    FloatBuffer[] savedPosBuffers, savedNormBuffers;    // Used to save for tweening

    AnimationEvent[] events;

    Animation(String objectName, String animationName) {
        this.TAG = "Animation." + objectName + "." + animationName;

        this.init(1000/GameConstants.frameRate, false);
    }

    Animation(int[] animationIndex, String libraryName, String animationName, long lengthInMilli, boolean interpolate){
        this.TAG = "Animation." + libraryName;

        this.animationIndex = animationIndex;

        this.init(lengthInMilli, interpolate);
    }

    private void init(long frameTime, boolean interpolate){
        this.currentTime = 0;
        this.frameTime = frameTime;

        this.isPlaying = false;
        this.interpolate = interpolate;

        this.deltaTimeModifier = 1.f;

        this.events = new AnimationEvent[0];

        this.loaded = true;
        this.animationIndex = MeshManager.getAnimationIndex(this.getObjectName(), this.getAnimationName());
        if(this.animationIndex == null){
            this.loaded = false;
        }
    }

    protected float[] update() {
        if(!this.loaded){
            this.animationIndex = MeshManager.getAnimationIndex(this.getObjectName(), this.getAnimationName());
            if(this.animationIndex == null) {
                return null;
            }
            //LOGE("Loaded");
            this.loaded = true;
        }
        if (!isPlaying) {
            return null;
        }
        currentTime += SuperManager.deltaTime * this.deltaTimeModifier;
        for(AnimationEvent event : this.events){
            if(!event.handled && !event.fired && currentTime >= frameTime * (event.frameNumber - 5)){
                event.fire();
            }
        }
        if (currentTime > frameTime * (animationIndex.length - 1)) {
            isPlaying = false;
            //LOGE("Animation done");
            return null;
        }
        return null;
    }

    protected int play() {   // 0 = already playing, 1 = success
        if (this.isPlaying) {
            return 0;
        }
        this.currentTime = 0;
        this.isPlaying = true;
        for(AnimationEvent event : this.events){
            event.reset();
        }
        return 1;
    }

    protected int stop() {   // 0 = not playing, 1 = success
        if (!this.isPlaying) {
            return 0;
        }
        this.isPlaying = false;
        return 1;
    }

    protected void changeFrameRate(long newFPS){
         long newFrameTime = 1000 / newFPS,
                newCurrentTime = (this.currentTime / this.frameTime) * newFrameTime;
        this.frameTime = newFrameTime;
        this.currentTime = newCurrentTime;
    }

    protected void addAnimationEvent(String name, int frameNumber){
        if(!loaded){
            //LOGE("Cannot check animation event bounds because animation index == null");
        }else if(frameNumber > this.animationIndex.length - 1){
            LOGE("Trying to create animation event out of range");
            return;
        }
        AnimationEvent[] newEvents = new AnimationEvent[this.events.length + 1];
        System.arraycopy(this.events, 0, newEvents, 0, this.events.length);
        newEvents[newEvents.length - 1] = new AnimationEvent(name, frameNumber);
        this.events = newEvents;
    }

    protected float getFraction(){
        return (float)(currentTime % frameTime) / frameTime;
    }

    protected  int getcurrentFrame(){
        return this.getcurrentFrame(0);
    }

    protected int getcurrentFrame(int offset){
        // Make sure animation is loaded
        if(!this.loaded){
            return -1;
        }
        // Check if there is only 1 frame
        if(this.animationIndex.length == 1){
            return this.animationIndex[0];
        }
        // Calculate frame based on passed time
        if(currentTime > frameTime * (animationIndex.length - 1)){
            return animationIndex[animationIndex.length - 1 + offset];
        }
        // Get last frame
        return animationIndex[(int)Math.floor(currentTime / frameTime) + offset];
    }

    protected void draw(BasicEntity entity, RawModel model) {
        // Check to make sure library is loaded
        String libName = MeshManager.getLibraryByObject(this.getObjectName()).libraryName;
        if(!MeshManager.libraries[MeshManager.getLibraryIndex(libName)].loaded()){
            return;
        }
        if (!this.isPlaying || !this.interpolate) {
            model.draw(libName, entity.position, entity.rotation, entity.scale);
            return;
        }
        interpolateVerts();
        model.draw(libName, entity.position, entity.rotation, entity.scale);
        if (this.savedPosBuffers != null && this.savedNormBuffers != null) {
            Mesh gotMesh = MeshManager.getMesh(libName, this.getcurrentFrame());
            CustomVertexBuffer[] b1 = gotMesh.vertexBuffers;
            for (int i = 0; i < MeshManager.getMesh(libName, this.getcurrentFrame()).vertexBuffers.length; i++) {   // After draw, put back saved buffers as to not corrupt mesh
                b1[i].floatBufferPositions = this.savedPosBuffers[i];
                b1[i].floatBufferNormals = this.savedNormBuffers[i];
            }
        }
    }

    protected void interpolateVerts(){
        // Make sure animation is updated
        if(this.getcurrentFrame() == -1){
            return;
        }
        // Make sure isn't trying to interpolate last frame
        if(this.getcurrentFrame() == this.animationIndex[this.animationIndex.length - 1]){
            return;
        }
        // Get mesh from library
        Mesh gotMesh = MeshManager.getLibraryByObject(this.getObjectName()).getMesh(this.getcurrentFrame());
        savedPosBuffers = new FloatBuffer[gotMesh.vertexBuffers.length];
        savedNormBuffers = new FloatBuffer[gotMesh.vertexBuffers.length];
        for(int i = 0; i < gotMesh.vertexBuffers.length; i++){
            savedPosBuffers[i] = gotMesh.vertexBuffers[i].floatBufferPositions.duplicate();
        }
        for(int i = 0; i < gotMesh.vertexBuffers.length; i++){
            savedNormBuffers[i] = gotMesh.vertexBuffers[i].floatBufferNormals.duplicate();
        }
        float frac = this.getFraction();
        if (this.getcurrentFrame() == animationIndex.length - 1){
            return;
        }
        Mesh gotMesh2 = MeshManager.getLibraryByObject(this.getObjectName()).getMesh(this.getcurrentFrame(1));
        CustomVertexBuffer[] b1 = gotMesh.vertexBuffers,
                b2 = gotMesh2.vertexBuffers;
        //LOGE(String.format("Verts1: %s, verts2: %s", gotMesh.objectName, gotMesh2.objectName));
        for(int i = 0; i < b1.length; i++) {
            // Interpolate vertex positions
            {
                float[] f1 = new float[b1[i].floatBufferPositions.limit()],
                        f2 = new float[b2[i].floatBufferPositions.limit()];
                FloatBuffer fb1 = b1[i].floatBufferPositions.duplicate(),
                        fb2 = b2[i].floatBufferPositions.duplicate();
                fb1.get(f1);
                fb2.get(f2);

                //LOGE(String.format("F1: %d F2: %d", f1.length, f2.length));
                float[] floatBufferArray = new float[0];
                for (int u = 0; u < f1.length && u < f2.length; u++) {
                    float dist = (f2[u] - f1[u]),
                            interpolated = f1[u] + (dist * frac);
                    floatBufferArray = VectorMath.addOneToArray(floatBufferArray, interpolated);
                }
                ByteBuffer bb = ByteBuffer.allocateDirect(floatBufferArray.length * 4);
                bb.order(ByteOrder.nativeOrder());
                FloatBuffer newFloatBuffer = bb.asFloatBuffer();
                newFloatBuffer.put(floatBufferArray);
                newFloatBuffer.position(0);
                b1[i].floatBufferPositions = newFloatBuffer;
            }
            // Interpolate vertex normals
            {
                float[] f1 = new float[b1[i].floatBufferNormals.limit()],
                        f2 = new float[b2[i].floatBufferNormals.limit()];
                FloatBuffer fb1 = b1[i].floatBufferNormals.duplicate(),
                        fb2 = b2[i].floatBufferNormals.duplicate();
                fb1.get(f1);
                fb2.get(f2);
                float[] floatBufferArray = new float[0];
                for (int u = 0; u < f1.length && u < f2.length; u++) {
                    float dist = (f2[u] - f1[u]),
                            interpolated = f1[u] + (dist * frac);
                    floatBufferArray = VectorMath.addOneToArray(floatBufferArray, interpolated);
                }
                ByteBuffer bb = ByteBuffer.allocateDirect(floatBufferArray.length * 4);
                bb.order(ByteOrder.nativeOrder());
                FloatBuffer newFloatBuffer = bb.asFloatBuffer();
                newFloatBuffer.put(floatBufferArray);
                newFloatBuffer.position(0);
                b1[i].floatBufferNormals = newFloatBuffer;
            }
        }
    }

    protected int getFirstFrame(){
        return this.animationIndex[0];
    }

    protected int getLastFrame(){
        return this.animationIndex[this.animationIndex.length-1];
    }

    static Animation getTweenedAnimation(String libraryName, String animationName, long timeInMilli, Animation first, Animation second, boolean interpolate){
        return new Animation(new int[] {first.getLastFrame(), second.getFirstFrame()}, libraryName, animationName, timeInMilli, interpolate);
    }

    /*static Animation getCombinedAnimation(String animationName, long[] times, Animation ... animations){
        if(animations.length == 0){
            return null;
        }
        if(times.length != animations.length){
            Log.e("Animation", String.format("Tryning to combine %d animations with %d times", animations.length, times.length));
            return null;
        }
        if(animations.length == 1){
            Log.e("Animation", "Trying to combine animation with onlt one animation.. : " + animations[0].animationName);
            return animations[1];
        }
        Animation returnAnimation = Animation.getTweenedAnimation(animationName, times[0], animations[0], animations[1], true);
    }*/

    private String getObjectName(){
        return this.TAG.split("\\.")[1];
    }

    protected String getAnimationName(){
        return this.TAG.split("\\.")[2];
    }

    protected void LOGE(String message){
        Log.e(this.TAG, message);
    }
}
