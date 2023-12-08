package vgapp.assist.utils;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Set;
import android.speech.tts.Voice;
import java.util.List;

public class SpeakText {

    private TextToSpeech textToSpeech;
    private ArrayList<String> list;
    private int position;
    private boolean isPlaying;
    private Context ctx;

    public SpeakText(Context ctx) {
        list = new ArrayList<>();
        position = 0;
        isPlaying = false;
        this.ctx = ctx;

        textToSpeech = new TextToSpeech(ctx, new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if (status == TextToSpeech.SUCCESS) {
                        int result = textToSpeech.setLanguage(Locale.getDefault());
                        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                            Log.e("TTS", "Idioma não suportado");
                        }
                    } else {
                        Log.e("TTS", "Falha na inicialização");
                    }
                }
            });
    }

    public void speak(String text) {
        list.add(text);
        if (!isPlaying) {
            play();
        }
    }

    private void play() {
        Log.d("TTS", "Speak called");
        if (textToSpeech == null) return;

        getVoice();
        
        textToSpeech.setPitch(new AppConfig(ctx).getVoiceVolume());
        textToSpeech.setSpeechRate(new AppConfig(ctx).getVoiceSpeed());


        textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                public void onStart(String p1) {
                    Log.d("TTS", "Speak started");
                    isPlaying = true;
                }

                public void onDone(String p1) {
                    Log.d("TTS", "Speak done");
                    isPlaying = false;
                    position++;
                    if (position < list.size()) {
                        play();
                        Log.d("TTS", "Next speak");
                    } else {
                        Log.d("TTS", "Speak list finished");
                        list.clear();
                        position = 0;
                    }
                }

                public void onError(String p1) {
                    isPlaying = false;
                    Log.d("TTS", "Error: " + p1);
                }
            });
        Log.d("TTS", "Starting to speak");
        textToSpeech.speak(list.get(position), TextToSpeech.QUEUE_FLUSH, null, TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID);
    }
    
    private void getVoice() {
        Set<Voice> voices = textToSpeech.getVoices();
        for (Voice voice : voices) {
            if (voice.getName().contains("pt-br")) {
                if (new AppConfig(ctx).isFemaleVoice()) {
                    if (voice.getName().equals("pt-br-x-afs#female_1-local")) {
                        Log.d("TTS", "female voice");
                        textToSpeech.setVoice(voice);
                        break;
                    }
                } else {
                    if (voice.getName().equals("pt-br-x-afs#male_1-local")) {
                        Log.d("TTS", "male voice");
                        textToSpeech.setVoice(voice);
                        break;
                    }
                }
            }
        }
    }
}
