package vgapp.assist.services;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.widget.Toast;
import java.util.ArrayList;
import vgapp.assist.utils.AppConfig;
import vgapp.assist.utils.ConvertToText;
import vgapp.assist.utils.ReproduceAudioWpp;
import vgapp.assist.utils.SpeakText;

public class NotificationService extends NotificationListenerService {
	Context context;
	String titleData="", textData="";
	SpeakText speakText;
	ArrayList<Long> notifications;
    ReproduceAudioWpp reproduceAudio;
	
	@Override
	public void onCreate() {
		super.onCreate();
		context = getApplicationContext();
		notifications = new ArrayList<>();
		log("Serviço iniciado!");
		speakText = new SpeakText(this);
		reproduceAudio = new ReproduceAudioWpp(this);
	}

	@Override
	public void onNotificationPosted(StatusBarNotification sbn) {
		String packageName = sbn.getPackageName();
		if (!(packageName.equals("com.whatsapp"))) return;
	    Log.d("NP", "New Notification");
		if(!(new AppConfig(this).canReadNotifications())) return;
       Log.d("NP", "Can Read Notifications");
		if (System.currentTimeMillis() - sbn.getNotification().when > 3000 || notifications.contains(sbn.getNotification().when)) return;

		notifications.add(sbn.getNotification().when);

		Bundle extras = sbn.getNotification().extras;
		String characterFilter = "[^\\p{L}\\p{M}\\p{N}\\p{P}\\p{Z}\\p{Cf}\\p{Cs}\\s]";

		if (extras.getString("android.title") != null) {
			titleData = extras.getString("android.title");
			titleData = titleData.replaceAll(characterFilter, "");
		} else {
			titleData = "";
		}
		if (extras.getCharSequence("android.text") != null) {
			textData = extras.getCharSequence("android.text").toString();
			textData = textData.replaceAll(characterFilter, "");
		} else {
			textData = "";
		}

		titleData = titleData.replaceAll("\\(\\d+\\s[Mm]ensagens?\\)", "");

		if (titleData.contains("WhatsApp") || textData.contains("Novas mensagens")) return;

        Intent intent = new Intent("ACTION_NOTIFICATION_RECEIVED");
		intent.putExtra("title", titleData);
		intent.putExtra("text", textData);
		sendBroadcast(intent);
       Log.d("NP", "intent sended");
        
        
		ConvertToText notiText = new ConvertToText(titleData, textData);
		Log.d("NP", "Can Read Group");
		if(notiText.fromGroup() && !( new AppConfig(this).canReadGroups())) return;
		Log.d("NP", "Can Read Group: true");
		
		speakText.speak(notiText.getText());
		
		if (notiText.isAudioMessage() && new AppConfig(this).canPlayAudio()) {
            Log.d("NP", "Can Play Audio");
			reproduceAudio.play();
		}

	}
	@Override
	public void onNotificationRemoved(StatusBarNotification sbn) {
		Log.d("Msg", "Notification Removed");
	}

	

	private void log(Object obj) {
		Toast.makeText(this, obj.toString(), 5000).show();
	}

}

