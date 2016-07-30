package project.etrumper.thomas.ghostbutton;

import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Vector;

/**
 * Created by thoma on 2/12/2016.
 */
public class Loader {

    public static void loadAnimationLibrary(final String fileName) {
        new AsyncTask<String, Void, Void>() {
            private String fileName;
            @Override
            protected Void doInBackground(String... params) {
                this.fileName = params[0];
                // Load a StringBuilder to hold each animations data
                StringBuilder currentStringBuilder = new StringBuilder();
                // Load the file's contents into a BufferedReader for line by line reading
                BufferedReader bf = new BufferedReader(new StringReader(loadFile(fileName)));
                String line, objectName = null;
                // Add try block for bg.readLine()'s IOException
                try {
                    while ((line = bf.readLine()) != null) {
                        if (line.length() > 10) {
                            // Set the current animation name and unload current data into new animation
                            if (line.substring(0, 10).equals("animation ")) {
                                // Make sure there is data to loadElements into a new animation
                                if (currentStringBuilder.toString().split("\n").length > 2) {
                                    // Load data into animation
                                    loadAnimationFromString(currentStringBuilder.toString(), objectName, fileName);
                                }
                                // Clear the held data
                                currentStringBuilder.delete(0, currentStringBuilder.length());
                            }
                            // Set the library's object name
                            else if (line.length() > 16 && line.substring(0, 16).equals("animationobject ")) {
                                objectName = line.split(" ")[1];
                            }
                        }
                        // Add the current line to the StringBuilder (data to be parsed)
                        currentStringBuilder.append(line);
                        // Add new line as to separate data by line
                        currentStringBuilder.append("\n");
                    }
                } catch (IOException e) {
                    Log.e("Loader", e.toString());
                }
                // Load remaining data
                loadAnimationFromString(currentStringBuilder.toString(), objectName, fileName);
                return null;
            }

            @Override
            protected void onPostExecute(Void result){
                MeshManager.setLibraryNotLoading(fileName);
            }
        }.execute(fileName);
    }

    public static void loadAnimationFromString(String contents, String objectName, String libraryName) {
        // Make sure file is not empty
        if (contents == null || contents.length() == 0) {
            Log.e("Loader", String.format("Library %s.%s has no contents", libraryName, objectName));
            return;
        }
        String animationName = "";
        int currentFrame,
                currentMesh = 0;
        Mesh[] meshes = new Mesh[0];
        ByteBuffer currentVerts = null,
                currentNorms = null;
        BufferedReader bf = new BufferedReader(new StringReader(contents));
        String line,
                currentMaterial = null;
        try {
            // Read line by line file, splitting data into chunks and using each respectively
            while ((line = bf.readLine()) != null) {
                // Set current animation name
                if (line.startsWith("animation ")) {
                    animationName = line.split(" ")[1];
                    continue;
                }
                // Set number of meshes (frames) to be imported
                if (line.startsWith("nf ")) {
                    meshes = new Mesh[Integer.parseInt(line.split(" ")[1])];
                    continue;
                }
                // Sets current frame number
                if (line.startsWith("frame ")) {
                    currentFrame = Integer.parseInt(line.split(" ")[1]);
                    meshes[currentMesh++] = new Mesh(String.format("%s.%s.%d", objectName, animationName, currentFrame));
                    continue;
                }
                // Sets current material being imported
                if (line.startsWith("mtl ")) {
                    currentMaterial = line.split(" ")[1];
                    continue;
                }
                // Imports Base64 encoded vertices, decodes them, and sets as current vertices
                if (line.charAt(0) == 'v') {
                    byte[] bytes = Base64.decode(bf.readLine(), 0);
                    currentVerts = ByteBuffer.allocateDirect(bytes.length);
                    currentVerts.order(ByteOrder.nativeOrder());
                    currentVerts.put(bytes);
                    currentVerts.position(0);
                    continue;
                }
                // Imports Base64 encoded normals, decodes them, and sets as current normals
                // NOTE: May not need normals in the future... deciding with shader
                if (line.startsWith("no")) {
                    byte[] bytes = Base64.decode(bf.readLine(), 0);
                    currentNorms = ByteBuffer.allocateDirect(bytes.length);
                    currentNorms.order(ByteOrder.nativeOrder());
                    currentNorms.put(bytes);
                    currentNorms.position(0);
                    meshes[currentMesh - 1].hardCodeFloats(currentMaterial, currentVerts, currentNorms);
                }
            }
        } catch (IOException e) {
            Log.e("Loader.AsyncTask", "IOEXCEPTION:" + e.toString());
        }
        // Send meshes to MeshManager
        MeshManager.addMeshes(libraryName, meshes);
    }

    public static String loadFile(String fileName) {
        InputStream is = null;
        int rID = SuperManager.context.getResources().getIdentifier("project.etrumper.thomas.ghostbutton:raw/" + fileName, null, null);
        try {
            is = SuperManager.context.getResources().openRawResource(rID);
        } catch (Exception e) {
            Log.e("loadFile()", e.toString());
        }
        if (is != null) {
            try {
                byte[] buffer = new byte[is.available()];
                is.read(buffer);
                ByteArrayOutputStream oS = new ByteArrayOutputStream();
                oS.write(buffer);
                oS.close();
                is.close();
                return oS.toString();
            } catch (IOException e) {
                Log.e("Loader", e.toString());
            }
        }
        return "";
    }
}
