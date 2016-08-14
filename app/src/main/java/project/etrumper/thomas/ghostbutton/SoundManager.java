package project.etrumper.thomas.ghostbutton;

import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;

import java.io.IOException;

/**
 * Created by thoma on 2/14/2016.
 */
public class SoundManager{

    static SoundPool soundPool;

    static int MAX_STREAMS = 30,
        MAX_SOUNDS = 40;

    static String TAG = "SoundManager";

    static Sound[] sounds;

    public static void init(){
        soundPool = new SoundPool(MAX_STREAMS, AudioManager.STREAM_MUSIC, 0);
        sounds = new Sound[MAX_SOUNDS];
    }

    public static int load(int rawSoundID){
        for(Sound sound : sounds){
            if(sound != null && sound.rawSoundID == rawSoundID){
                //print("Loading already loaded sound");
                return sound.soundID;
            }
        }
        return soundPool.load(SuperManager.context, rawSoundID, 0);
    }

    public static Sound loadSound(int rawSoundID) throws MandatoryException{
        for(Sound sound : sounds){
            if(sound != null && sound.rawSoundID == rawSoundID){
                return sound;
            }
        }
        int iter = 0;
        for(Sound asound : sounds){
            if(asound == null){
                sounds[iter] = new Sound(rawSoundID);
                return sounds[iter];
            }
            iter++;
        }
        throw new MandatoryException("Current Soundpool reached max loaded Sounds");
    }

    public static void playBySound(Sound sound){
        soundPool.play(sound.soundID, sound.volume, sound.volume, 0, 0, sound.rate);
    }

    public static void print(String message){
        Log.e(TAG, message);
    }

    public static void remove(Sound sound) throws MandatoryException{
        int iter = 0;
        for(Sound asound : sounds){
            if(asound != null && asound.rawSoundID == sound.rawSoundID){
                sounds[iter] = null;
                return;
            }
            iter++;
        }
        throw new MandatoryException("Sound object does not exist to remove()");
    }
}
