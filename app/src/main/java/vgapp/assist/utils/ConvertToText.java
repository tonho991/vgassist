package vgapp.assist.utils;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class ConvertToText {
    
	 private String title, text, textSpk = "";
	 private String type = "type_msg";
	 private boolean fromGroup, isAudio;
	 
	 public static final String 
	 TYPE_AUDIO = "type_audio",
	 TYPE_STICKER = "type_stk",
	 TYPE_DOC = "type_doc",
	 TYPE_IMG = "type_img",
	 TYPE_VIDEO = "type_vdo",
	 TYPE_MESSAGE = "type_msg" 
	 ;
	
	 public ConvertToText(String p1, String p2){
		  title = p1;
		  text = p2;
		  textSpk = convert();
	 }
    
	 public String  getText(){
		 return textSpk;
	 }
	 
	 private String convert(){
		 String finalText = "";
		 String characterFilter = "[^\\p{L}\\p{M}\\p{N}\\p{P}\\p{Z}\\p{Cf}\\p{Cs}\\s]";
		 String msg = "";
		 boolean isDoc = title.matches(".*📄.*\\..*");
		
		 title = title.replaceAll(characterFilter, "").replaceAll("\\(\\d+\\s[Mm]ensagens?\\)", "").toLowerCase().trim();
		 text = text.replaceAll(characterFilter, "").toLowerCase().trim();
		 msg = text;
		 isAudio = false;
		 
		 title =  isNumber(title) ? "número desconhecido" : title;
		 
		 if(msg.equals("figurinha")){
			 type = TYPE_STICKER;
			 return title + " te enviou uma figurinha";
		 } else if(msg.equals("foto")){
			 type = TYPE_IMG;
			 return title + " te enviou uma foto";
		 } else if(msg.contains("vídeo")){
			 type = TYPE_VIDEO;
			  return title + " te enviou um vídeo";
		 } else if (isDoc){
			 type = TYPE_DOC;
			 return title + " te enviou um documento";
		 } else if(msg.contains("mensagem de voz (")){
			 type = TYPE_AUDIO;
			 isAudio = true;
			 return title + " te enviou uma mensagem de voz";
		 } 
		 
		 if(title.contains(":")){
			 String parts[] = title.split(":");
			 String group = parts[0].trim();
			 String contact = parts[1].trim();
			
			 contact = isNumber(contact) ? "número desconhecido" : contact;
			 
			 if(title.contains("respondeu a")){
				 parts = text.split(":");
				 contact = isNumber(parts[0].trim()) ? "número desconhecido" : contact;
				 msg = parts[1].trim();
				 finalText = group + ","  + contact + " respondeu você, e diz " + msg;
				 fromGroup = true;
			 } else {
				 fromGroup = false;
				 finalText = group + "," + contact + " diz, " + msg;
			 }
			
			
		 }else {
			 finalText = (isNumber(title) ? "número desconhecido" : title)  + " diz, " + text;
		 }
		 
		 return finalText;
	 }
	 
	 public String getType(){
		 return type;
	 }
	 
	 public boolean fromGroup(){
		 return fromGroup;
	 }
	 
	 public boolean isAudioMessage(){
		 return isAudio;
	 }
	 
	 private boolean isNumber(String number){
		 number = number.replaceAll("([\\s*()-]+)", "");
		 Pattern pattern = Pattern.compile("\\b(\\d{13}|\\d{12})\\b");
		 Matcher matcher = pattern.matcher(number);
		 return matcher.matches();
	 }
	 
}
