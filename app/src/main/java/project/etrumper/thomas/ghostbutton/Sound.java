package project.etrumper.thomas.ghostbutton;

import android.util.Log;

/**
 * Created by thoma on 3/2/2016.
 */
public class Sound {

    int rawSoundID, soundID;

    float rate, volume;

    Sound(int rawSoundID){
        this.rawSoundID = rawSoundID;
        this.soundID = SoundManager.load(rawSoundID);
        this.rate = 1.f;
        this.volume = 1.f;
    }

    protected void play() {
        SoundManager.playBySound(this);
    }

}
