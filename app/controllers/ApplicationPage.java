package controllers;

import java.io.File;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import play.Logger;
import play.Play;
import play.i18n.Lang;
import play.i18n.Messages;
import play.mvc.Before;
import play.mvc.Http;
import play.mvc.Http.Header;

public class ApplicationPage {
	
	private static DateFormat dateFormat=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	private static List<String> console=new ArrayList<String>(1000);
	public static Map<String, String> languages=new TreeMap<String, String>();
	
	private static long startTime = System.currentTimeMillis();
	private static Calendar calendar=Calendar.getInstance();
	
	public static String appVersion = Play.configuration.getProperty("application.version", "N/A");
	public static String hwDetails = Play.configuration.getProperty("application.host","") +" "+ Runtime.getRuntime().availableProcessors()+"xCPU "+String.format("%s", System.getProperty("os.arch"));
	public static String osDetails = String.format("%s", System.getProperty("os.name"));
	public static String kernelDetails = String.format("%s", System.getProperty("os.version"));
	public static String jreDetails = String.format("%s %s %s bits", System.getProperty("java.vendor"), System.getProperty("java.version"), System.getProperty("sun.arch.data.model"));
	public static Date lastUpdate=new Date(getLastUpdate(new File(Play.applicationPath+"/app"), 0));
	
	static{
		updateLanguages();
	}
	
	public static void addConsole(String msg, Object...args){
		String formatMsg=String.format("[%s] %s", dateFormat.format(new Date()), String.format(msg, args));
		synchronized (console) {
			if(console.size()==1000){
				console.remove(0);
			}
			console.add(formatMsg+"\n");
			Logger.info(formatMsg);
		}
	}
	
	public static String getConsole(){
		String all="";
		synchronized (console) {
			for(String entry:console){
				all+=entry;
			}
		}
		return all;
	}
	
	public static void updateLanguages(){
		languages.clear();
		String[] langs=Play.configuration.getProperty("application.langs").split(",");
		for(String lang : langs){
			languages.put(lang,Messages.get(lang, lang));
		}
	}
	
	public static Date getRuntime(){
		Date diff=new Date(new Date().getTime() - (System.currentTimeMillis()-startTime));
		return diff;
	}
	
	public static String getMemory(){
		Runtime runtime=Runtime.getRuntime();
		long used=runtime.totalMemory()-runtime.freeMemory();
		used=used/(1024*1024);
		long total=runtime.totalMemory()/(1024*1024);
		long max=runtime.maxMemory()/(1024*1024);
		return Messages.get("memory.consumption",used,total,max);
	}
	
	public int getMyAge(){
		calendar.setTime(new Date());
		return calendar.get(Calendar.YEAR)-1987;
	}
	
	private static long getLastUpdate(File root,long minimal){
		long last=minimal;
		if(root.isDirectory()){
			for(File file:root.listFiles()){
				long ret=getLastUpdate(file, minimal);
				if(ret>last)last=ret;
			}
		}else{
			if(root.lastModified()>last){
				last=root.lastModified();
			}
		}
		return last;
	}

}
