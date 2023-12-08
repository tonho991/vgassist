package vgapp.assist.utils;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import java.io.File;
import java.util.ArrayList;

public class ReproduceAudioWpp {

    private Context context;
    private ArrayList<String> list;
    private int playPosition = 0;
    private boolean isPlaying;
    private MediaPlayer mediaPlayer;

    public ReproduceAudioWpp(Context ctx) {
        list = new ArrayList<>();
        context = ctx;
    }

    public void play() {
        String audioPath = getLastAudioPath();
        if (audioPath != null) {
            list.add(audioPath);
            if (!isPlaying) {
                reproduceAudio();
            }
        }
    }

    public void reproduceAudio() {
        if (isPlaying) return;

        if (playPosition < list.size()) {
            String filePath = list.get(playPosition);

            if (filePath != null && !filePath.isEmpty()) {
                try {
                    mediaPlayer = new MediaPlayer();
                    mediaPlayer.setDataSource(context, Uri.parse(filePath));
                    mediaPlayer.prepare();
                    isPlaying = true;
                    
                    mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener(){
                         public void onPrepared(MediaPlayer mp){
                             mediaPlayer.start();
                         }
                    });

                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            public void onCompletion(MediaPlayer mp) {
                                if (playPosition < list.size() - 1) {
                                    playPosition++;
                                    reproduceAudio();
                                } else {
                                    list.clear();
                                    playPosition = 0;
                                    isPlaying = false;
                                    if (mediaPlayer != null) {
                                        mediaPlayer.release();
                                        mediaPlayer = null;
                                    }
                                }
                            }
                        });
                } catch (Exception e) {
                    e.printStackTrace();
                    isPlaying = false;
                }
            }
        }
    }


    private String getLastAudioPath() {
        try {
            if (Build.VERSION.SDK_INT >= 30) {
                Uri lastAudioPath = Utils.findLastWppAudioFile(this.context);
                if (lastAudioPath != null) {
                    Log.d("LAP", lastAudioPath.toString());
                    return lastAudioPath.toString();
                }
            }

            File whatsappDir = new File(Environment.getExternalStorageDirectory(), "/WhatsApp/Media/WhatsApp Voice Notes");
            File whatsappDirAlternative = new File("/storage/emulated/0/Android/media/com.whatsapp/WhatsApp/Media/WhatsApp Voice Notes");
            File[] audioFiles = whatsappDir.listFiles();
            if (audioFiles == null || audioFiles.length == 0) {
                audioFiles = whatsappDirAlternative.listFiles();
            }

            if (audioFiles != null && audioFiles.length > 0) {
                File latestFile = null;
                long latestModified = 0;

                for (File file : audioFiles) {
                    long fileLastModified = file.lastModified();
                    if (fileLastModified > latestModified) {
                        latestModified = fileLastModified;
                        latestFile = file;
                    }
                }

                if (latestFile != null) {
                    File[] subFiles = latestFile.listFiles();
                    if (subFiles != null && subFiles.length > 0) {
                        File latestSubFile = null;
                        long latestSubModified = 0;

                        for (File subFile : subFiles) {
                            long subFileLastModified = subFile.lastModified();
                            if (subFileLastModified > latestSubModified) {
                                latestSubModified = subFileLastModified;
                                latestSubFile = subFile;
                            }
                        }

                        if (latestSubFile != null) {
                            Log.d("LAP", latestSubFile.getPath());
                            return latestSubFile.getPath();
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.e("LAP", e.toString());
        }
        Log.d("LAP", "return null");
        return null;
    }



}

