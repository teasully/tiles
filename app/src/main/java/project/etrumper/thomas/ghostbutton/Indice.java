package project.etrumper.thomas.ghostbutton;

import android.util.Log;

/**
 * Created by thoma on 2/12/2016.
 */
public class Indice implements Comparable<Indice>{

    int[] vIndex, nIndex;
    String material;

    Indice(int v1, int v2, int v3, int n1, int n2, int n3, String material){
        vIndex = new int[3];
        vIndex[0] = v1;
        vIndex[1] = v2;
        vIndex[2] = v3;
        nIndex = new int[3];
        nIndex[0] = n1;
        nIndex[1] = n2;
        nIndex[2] = n3;
        this.material = material;
        //Log.e("Indice", String.format("Created %s Indice with verts %d, %d, %d and normals %d, %d, %d", material, v1, v2, v3, n1, n2, n3));
    }

    public float[] calculatePositions(float[] vertices){
        float[] positions = new float[9];

        positions[0] = vertices[(this.vIndex[0] - 1)*3];
        positions[1] = vertices[(this.vIndex[0] - 1)*3 + 1];
        positions[2] = vertices[(this.vIndex[0] - 1)*3 + 2];

        positions[3] = vertices[(this.vIndex[1] - 1)*3];
        positions[4] = vertices[(this.vIndex[1] - 1)*3 + 1];
        positions[5] = vertices[(this.vIndex[1] - 1)*3 + 2];

        positions[6] = vertices[(this.vIndex[2] - 1)*3];
        positions[7] = vertices[(this.vIndex[2] - 1)*3 + 1];
        positions[8] = vertices[(this.vIndex[2] - 1)*3 + 2];

        return positions;
    }

    public float[] calculateNormals(float[] normals){
        float[] norms = new float[9];

        //Log.e("Indice", String.format("%d %d %d", nIndex[0], nIndex[1], nIndex[1]));

        if((this.nIndex[0] - 1)*3 >= normals.length){
            Log.e("Indice", "Normals out of range in calculateNormals.. giving 0.f");
            return new float[]{0, 0, 0, 0, 0, 0, 0, 0, 0};
        }

        norms[0] = normals[(this.nIndex[0] - 1)*3];
        norms[1] = normals[(this.nIndex[0] - 1)*3 + 1];
        norms[2] = normals[(this.nIndex[0] - 1)*3 + 2];

        norms[3] = normals[(this.nIndex[1] - 1)*3];
        norms[4] = normals[(this.nIndex[1] - 1)*3 + 1];
        norms[5] = normals[(this.nIndex[1] - 1)*3 + 2];

        norms[6] = normals[(this.nIndex[2] - 1)*3];
        norms[7] = normals[(this.nIndex[2] - 1)*3 + 1];
        norms[8] = normals[(this.nIndex[2] - 1)*3 + 2];

        return norms;
    }

    @Override
    public int compareTo(Indice other){
        int aa = this.material.hashCode(),
                bb = other.material.hashCode();
        if(aa < bb){
            return -1;
        }
        if(aa > bb){
            return 1;
        }
        return 0;
    }
}
