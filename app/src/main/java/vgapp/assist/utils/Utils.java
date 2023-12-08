package vgapp.assist.utils;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import androidx.documentfile.provider.DocumentFile;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
	private static final String WHATSAPP_AUDIO_PATTERN = ".*?-(\\d+)-WA(\\d+).opus";
	
    public static Uri findLastWppAudioFile(Context context) {
        Uri whatsappUri = Uri.parse(new AppConfig(context).getWppUri());
		
        if (whatsappUri != null) {
            DocumentFile[] listFiles = DocumentFile.fromTreeUri(context, whatsappUri).listFiles();

            DocumentFile lastAudioFile = null;
            int maxPrefix = 0;

            for (DocumentFile file : listFiles) {
                try {
                    String uri = file.getUri().toString();
                    int prefix = Integer.parseInt(uri.substring(uri.lastIndexOf("%2F") + 3));

                    if (prefix > maxPrefix) {
                        lastAudioFile = file;
                        maxPrefix = prefix;
                    }
                } catch (Exception e) {
					Log.e("LWAF", e.toString());
                }
            }

            if (lastAudioFile != null) {
                DocumentFile foundFile = null;
                int maxFirstNum = 0;
                int maxSecondNum = 0;

                for (DocumentFile audioFile : lastAudioFile.listFiles()) {
                    try {
                        Matcher matcher = Pattern.compile(WHATSAPP_AUDIO_PATTERN).matcher(audioFile.getUri().toString());

                        if (matcher.find()) {
                            int firstNum = Integer.parseInt(matcher.group(1));
                            int secondNum = Integer.parseInt(matcher.group(2));

                            if (firstNum > maxFirstNum || (firstNum == maxFirstNum && secondNum > maxSecondNum)) {
                                foundFile = audioFile;
                                maxFirstNum = firstNum;
                                maxSecondNum = secondNum;
                            }
                        }
                    } catch (Exception e) {
                        Log.e("LWAF", e.toString());
                    }
                }

                if (foundFile != null) {
					Log.d("LWAF", foundFile.getUri().toString());
                    return foundFile.getUri();
                }
            }
        }
		Log.d("LWAF", "null");
        return null;
    }

    public static ArrayList<DocumentFile> listFiles(DocumentFile documentFile, String str, ArrayList<String> arrayList, boolean z) {
        ArrayList<DocumentFile>  list = new ArrayList<DocumentFile>();
		Log.d("DFT", list.toString());
		if(documentFile == null) return list;
		
        if (arrayList.contains(documentFile.getName())) {
			Log.d("DFT", list.toString());
            return list;
        }
        
        if (documentFile.isDirectory()) {
            for (DocumentFile listDocumentFileTree : documentFile.listFiles()) {
                list.addAll(listFiles(listDocumentFileTree, str, arrayList, z));
                if (z && list.size() > 0) {
					Log.d("DFT", list.toString());
                    return list;
                }
            }
        } else {
            String name = documentFile.getName();
            if (documentFile.canRead() && name != null && name.endsWith(str)) {
                list.add(documentFile);
            }
        }
		Log.d("DFT", list.toString());
        return list;
    }
	
	public static ArrayList<DocumentFile> getWppAudioFiles(Uri uri, Context ctx, boolean z ){
		final DocumentFile files = DocumentFile.fromTreeUri(ctx, uri);
		ArrayList<String> list = new ArrayList<>();
		list.add("WhatsApp Documents");
		list.add("WhatsApp Audio");
		list.add("Wallpaper");
		list.add(".Statuses");
		list.add("WhatsApp Sticker");
		list.add("WhatsApp Videos");
		list.add("WhatsApp Animated Gifs");
		list.add("WhatsApp Images");
		return   listFiles(files, ".opus", list, z); 
	}
	
	
}
