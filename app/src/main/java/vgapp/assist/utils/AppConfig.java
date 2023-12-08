package vgapp.assist.utils;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

public class AppConfig {

	private SharedPreferences sp;
	private SharedPreferences.Editor editor;
	private Context ctx;

	public AppConfig(Context ctx) {
		sp = ctx.getSharedPreferences("CONFIG", Activity.MODE_PRIVATE);
		editor = sp.edit();
		this.ctx = ctx;
	}

	public String getWppUri() {
		return sp.getString("WPP_URI", "");
	}

	public boolean canReadGroups() {
		return sp.getBoolean("READ_GROUPS", false);
	}

	public boolean canPlayAudio() {
		if (getWppUri().equals("") || !(Build.VERSION.SDK_INT >= 30))   {
            Log.d("CPA", "wpp uri null, or device not support");
			return false;
		}
		return (Utils.getWppAudioFiles(Uri.parse(getWppUri()), ctx, true).size() > 0) && (sp.getBoolean("PLAY_AUDIO", false));
	}

	public boolean canReadNotifications() {
		return sp.getBoolean("SERVICE_STATUS", false);
	}

    public boolean isFemaleVoice(){
        return sp.getBoolean("FEMALE_VOICE", true);
    }
    
    public float getVoiceSpeed(){
        return sp.getFloat("VOICE_SPEED", 1.0f);
    }
    
    public float getVoiceVolume(){
        return sp.getFloat("VOICE_VOL", 100.0f);
    }
    
	public AppConfig setWppUri(String p1) {
		editor.putString("WPP_URI", p1).commit();
		return this;
	}

	public AppConfig setServiceOn(boolean p1) {
		editor.putBoolean("SERVICE_STATUS", p1).commit();
		return this;
	}
    
    public AppConfig setFemaleVoice(boolean p1){
        editor.putBoolean("FEMALE_VOICE", p1).commit();
        return this;
    }

	public AppConfig setReadGroups(boolean p1) {
		editor.putBoolean("READ_GROUPS", p1).commit();
		return this;
	}

	public AppConfig setCanPlayAudio(boolean p1) {
		editor.putBoolean("PLAY_AUDIO", p1).commit();
		return this;
	}

    public AppConfig setVoiceSpeed(float p1){
        editor.putFloat("VOICE_SPEED", p1).commit();
        return this;
    }

    public AppConfig setVoiceVolume(float p1){
        editor.putFloat("VOICE_VOL", p1).commit();
        return this;
        
    }
}
