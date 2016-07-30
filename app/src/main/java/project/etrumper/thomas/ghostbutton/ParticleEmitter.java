package project.etrumper.thomas.ghostbutton;

import android.util.Log;

/**
 * Created by thoma on 6/13/2016.
 */
public class ParticleEmitter {

    Triangle[] particles;

    float maxX = 0.5f,
        maxY = 0.5f,
        maxZ = 0.2f;

    float[] triangleCoords = new float[]{
            0f, 0.5f, 1.f,
            -1f, -0.5f, 1.f,
            1f, -0.5f, 1.f
    };

    ParticleEmitter(int numParticles, float[] position){
        this.particles = new Triangle[numParticles];
        for(int i = 0; i < numParticles; i++){
            float x = SuperManager.r.nextFloat() % maxX,
                    y = SuperManager.r.nextFloat() % maxY,
                    z = -SuperManager.r.nextFloat() % maxZ;
            int neg = SuperManager.r.nextInt() % 6;
            switch(neg){
                case(0):
                    x = -x;
                    break;
                case(1):
                    y = -y;
                    break;
                case(2):
                    //z = -z;
                    break;
            }
            this.particles[i] = new Triangle(triangleCoords, "Particle", new float[]{x, y, z});
            this.particles[i].position = position;
        }
    }

    public void update(){
        for(Triangle particle : this.particles){
            particle.update();
        }
    }

    public void draw(){
        for(Triangle particle : this.particles){
            particle.draw();
        }
    }
}
