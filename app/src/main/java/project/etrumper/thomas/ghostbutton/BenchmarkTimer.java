package project.etrumper.thomas.ghostbutton;

import android.util.Log;

/**
 * Created by thoma on 4/19/2016.
 */
public class BenchmarkTimer {

    long t;

    BenchmarkTimer(){
        this.start();
    }

    public void start(){
        t = System.currentTimeMillis();
    }

    public void end(String tag){
        Log.e(tag+".btimer", String.format("%f", (System.currentTimeMillis() - t) * 1f));
    }

    public void end(){
        Log.e("Unnammed Benchmark", String.format("%f", (System.currentTimeMillis() - t) * 1f));
    }

}
