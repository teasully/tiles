package project.etrumper.thomas.ghostbutton;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

/**
 * Created by thoma on 2/18/2016.
 */

public class MeshManager {

    static Library[] libraries = new Library[0];

    public static int getLibraryIndex(String libraryName) {
        // Iterate through libraries, checking name until found
        int iter = 0;
        boolean found = false;
        for (Library library : MeshManager.libraries) {
            if (library.libraryName.equals(libraryName)) {
                found = true;
                break;
            }
            iter++;
        }
        // Return -1 if not found
        if (!found) {
            return -1;
        }
        return iter;
    }

    public static void addMeshes(String libraryName, Mesh... newMeshes) {
        MeshManager.libraries[getLibraryIndex(libraryName)].addMeshes(newMeshes);
    }

    public static Mesh getMesh(String libraryName, int index) {
        // Get library and return appropriate index
        return MeshManager.libraries[getLibraryIndex(libraryName)].getMesh(index);
    }

    public static void registerLibraries(String... libraries) {
        // Iterate through passed strings
        for (String library : libraries) {
            // Check to see if library is already registered
            for (Library libraryObj : MeshManager.libraries) {
                if (libraryObj.libraryName.equals(library)) {
                    //LOGE(String.format("Library %s already found in registry", library));
                    return;
                }
            }
            // Find library object name
            BufferedReader bf = new BufferedReader(new StringReader(Loader.loadFile(library)));
            String objectName = null;
            try {
                // Get rid of first line
                bf.readLine();
                objectName = bf.readLine().split(" ")[1];
            } catch (IOException e) {
                LOGE(e.toString());
            }
            // Check to see if library object is already registered
            for (Library libraryObj : MeshManager.libraries) {
                if (libraryObj.libraryName.equals(library)) {
                    //LOGE(String.format("Library object %s already found in registry", objectName));
                    return;
                }
            }
            // Add new Library object to registry with library name and library object name
            Library[] newLibraries = new Library[MeshManager.libraries.length + 1];
            System.arraycopy(MeshManager.libraries, 0, newLibraries, 0, MeshManager.libraries.length);
            newLibraries[MeshManager.libraries.length] = new Library(library, objectName);
            MeshManager.libraries = newLibraries;
            // LOGE(String.format("Registered library %s object %s", library, objectName));
        }
    }

    public static int[] getAnimationIndex(String objectName, String animationName) {
        // Get library by object
        Library gotLibrary = getLibraryByObject(objectName);
        // Check if not null
        if (gotLibrary == null) {
            //LOGE(String.format("Object %s | Animation %s could not find library", objectName, animationName));
            return null;
        }
        // Check if library is loading
        if (gotLibrary.isLoading()) {
            //LOGE(String.format("Waiting for library %s to loadElements", gotLibrary.libraryName));
            return null;
        }
        // Check if library is loaded, if not loadElements it
        if (!gotLibrary.loaded()) {
            //LOGE(String.format("Loading library %s", gotLibrary.libraryName));
            gotLibrary.load();
            return null;
        }
        // Try to get animation index
        int[] animationIndex = gotLibrary.getAnimationIndex(animationName);
        // If return null then animation not found, unload the library and print error
        if (animationIndex == null) {
            gotLibrary.unload();
            //LOGE(String.format("Animation %s.%s not found in library %s", objectName, animationName, gotLibrary.libraryName));
            return null;
        }
        // Else, the animation was found and can return the animation index
        return animationIndex;
    }


    protected static Library getLibraryByObject(String libraryObjectName){
        // Iterate through libraries to find one with matching library object and return it
        for(Library library : MeshManager.libraries){
            if(library.objectName.equals(libraryObjectName)){
                return library;
            }
        }
        // If not found, return null
        return null;
    }

    public static boolean setLibraryNotLoading(String libraryName){
        // Wrapper
        return MeshManager.libraries[getLibraryIndex(libraryName)].setNotLoading();
    }

    private static void LOGE(String message) {
        Log.e("MeshManager", message);
    }
}
