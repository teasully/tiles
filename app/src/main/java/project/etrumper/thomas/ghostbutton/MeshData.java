package project.etrumper.thomas.ghostbutton;

/**
 * Created by thoma on 4/25/2016.
 */
public class MeshData {

    String name;
    String[] mtls;
    float[][] verts, norms;

    MeshData(String name, String[] mtls, float[][] verts, float[][] norms){
        this.name = name;
        this.mtls = mtls;
        this.verts = verts;
        this.norms = norms;
    }

    MeshData d = new MeshData("Archer.001.1",
                        new String[]{"Red", "Blue"},
                        new float[][]{new float[]{0},
                                      new float[]{0}},
                        new float[][]{new float[]{0},
                                      new float[]{0}});

}
