package project.etrumper.thomas.ghostbutton;

/**
 * Created by thoma on 6/14/2016.
 */
public class Library extends Logable{

    String libraryName, objectName;

    private Mesh[] meshes;

    private boolean loading;

    Library(String libraryName, String objectName) {
        super(String.format("Library_%s.%s", libraryName, objectName));

        this.libraryName = libraryName;
        this.objectName = objectName;

        // Set this.meshes[] length to 0
        this.unload();

        this.loading = false;

        this.DEBUG = false;
    }

    public void load(){
        // Check to see if another library is loading
        if(this.loading){
            LOGE("Trying to loadElements already loading library");
            return;
        }
        this.loading = true;

        Loader.loadAnimationLibrary(libraryName);
    }

    public boolean isLoading(){
        return this.loading;
    }

    public boolean setNotLoading(){
        // Check to see if this has aready been set to not loading
        if(!this.loading){
            LOGE(String.format("Trying to set already loaded library %s as loaded", this.libraryName));
            return false;
        }
        // If trying to set as loaded but no data
        if(!this.loaded()){
            LOGE("Trying to set non-loaded library to loaded");
            return false;
        }
        LOGE("Loaded");
        this.loading = false;
        return true;
    }

    public void unload(){
        this.meshes = new Mesh[0];
    }

    public boolean loaded(){
        return (this.meshes.length > 0);
    }

    public Mesh getMesh(int index){
        return this.meshes[index];
    }

    public void addMeshes(Mesh ... incommingMeshes){
        // Check to make sure parameters are not null
        if(incommingMeshes == null || incommingMeshes.length == 0){
            LOGE("Trying to pass null to addMeshes(Mesh ...)");
            return;
        }
        // Fast check to see if incoming meshes can replace empty current
        if(this.meshes.length == 0){
            meshes = incommingMeshes;
            return;
        }
        // Error check incoming meshes
        for(Mesh mesh : incommingMeshes){
            // Check for null
            if(mesh == null){
                LOGE(String.format("Trying to add null mesh to library %s\n\t%d meshes not loaded", this.libraryName, incommingMeshes.length - 1));
                return;
            }
            // Check for duplicates
            for(Mesh existingMesh : this.meshes){
                if(existingMesh.objectName.equals(mesh.objectName)){
                    LOGE(String.format("Trying to add duplicate mesh %s to library %s\n\t%d meshes not loaded", mesh.objectName, this.libraryName, incommingMeshes.length));
                    return;
                }
            }
        }
        // Combine current meshes with passed meshes
        Mesh[] newMeshes = new Mesh[this.meshes.length + incommingMeshes.length];
        System.arraycopy(this.meshes, 0, newMeshes, 0, this.meshes.length);
        System.arraycopy(incommingMeshes, 0, newMeshes, this.meshes.length, incommingMeshes.length);
        this.meshes = newMeshes;
    }

    protected int[] getAnimationIndex(String animationName){
        if(!this.loaded() || this.isLoading()){
            return null;
        }
        int[] output = new int[0];
        int meshIndex = 0;
        // Iterate through meshes, indexing needed meshes
        for (Mesh mesh : meshes) {
            String[] name = mesh.objectName.split("\\.");
            String objName = name[0],
                    animName = name[1];
            if(objName.equals(this.objectName) && animName.equals(animationName)){
                int[] newOutput = new int[output.length + 1];
                System.arraycopy(output, 0, newOutput, 0, output.length);
                newOutput[output.length] = meshIndex;
                output = newOutput;
            }
            meshIndex++;
        }
        // Return null means animation not found
        if(output.length == 0){
            return null;
        }
        return output;
    }

}
