package controllers;

import play.*;
import play.cache.Cache;
import play.db.jpa.NoTransaction;
import play.db.jpa.Transactional;
import play.i18n.Lang;
import play.i18n.Messages;
import play.mvc.*;
import play.mvc.Http.Header;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.URL;
import java.security.MessageDigest;
import java.util.*;

import models.*;

@With(Compress.class)
public class Application extends Controller {
	
	public static ApplicationPage page=new ApplicationPage();
	
	@Before
	public static void Init(){
		if(!session.contains("WELCOME")){
			session.put("WELCOME", System.currentTimeMillis());
			String hostName="N/A";
			try{
				hostName=InetAddress.getByName(request.remoteAddress).getHostName();
			}catch(Exception e){
				
			}
			page.addConsole("New connection from: %s (%s)", request.remoteAddress,hostName);
		}
	}
	
	@NoTransaction
	public static void logout(){
		session.remove("login");
		Application.admin();
	}
	
	@NoTransaction
	public static void login(String login,String password){
		checkAuthenticity();
		String goodLogin=Play.configuration.getProperty("application.login");
		String goodPwd=Play.configuration.getProperty("application.pwd");
		if(goodLogin.equals(login) && goodPwd.equals(ToMD5(password))){
			session.put("login", System.currentTimeMillis());
			Administration.index();
		}else{
			flash("login",login);
			flash("login.error","Invalid login and/or password");
			page.addConsole("Try to log in admin mode: %s with login: %s and password: %s", request.remoteAddress,login,password);
			Application.admin();
		}
	}
	
	private static String ToMD5(String pwd){
		try{
			final MessageDigest messageDigest = MessageDigest.getInstance("MD5");
			messageDigest.update(pwd.getBytes("UTF8"));
			final byte[] resultByte = messageDigest.digest();
			return new BigInteger(1,resultByte).toString(16);
		}catch(Exception e){
			return null;
		}
	}
	
	@NoTransaction
    public static void admin() {
		if(loginValid()){
			Administration.index();
		}
		 //if it's not secure, but Heroku has already done the SSL processing then it might actually be secure after all
        if (!request.secure && request.headers.get("x-forwarded-proto") != null) {
            request.secure = request.headers.get("x-forwarded-proto").values.contains("https");
        }
		if (!request.secure){
			String url = redirectHostHttps() + request.url;
			redirect(url);
		}
		renderArgs.put("page", page);
        render();
    }
	
	/** Renames the host to be https://, handles both Heroku and local testing. */
    @Util
    public static String redirectHostHttps() {
        if (Play.mode.equals(Play.Mode.DEV)) {
            String[] pieces = request.host.split(":");
            String httpsPort = (String) Play.configuration.get("https.port");
            return "https://" + pieces[0] + ":" + httpsPort; 
        } else {
            if (request.host.endsWith("domain.com")) {
                return "https://secure.domain.com";
            } else {
                return "https://" + request.host;
            }
        }
    }    

	@NoTransaction
    public static void index() {
		
		renderArgs.put("page", page);
        render();
    }
	
	@Transactional(readOnly=true)
    public static void articles(Long id) {
		List<Article> articles=null;
		if(id==null){
			articles=Article.find("visible= true order by date desc").fetch();
			renderArgs.put("articles", articles);
		}else{
			Article article=Article.findById(id);
			renderArgs.put("article", article);
		}
		renderArgs.put("page", page);
        render();
    }
	
	@NoTransaction
    public static void projects() {
		
		renderArgs.put("page", page);
        render();
    }
	
	@NoTransaction
    public static void contact() {
		
		renderArgs.put("page", page);
        render();
    }
	
	@NoTransaction
    public static void about() {
		
		renderArgs.put("page", page);
        render();
    }
	
	@NoTransaction
    public static void probe() {
		
		renderArgs.put("page", page);
        render();
    }
	
	@NoTransaction
    public static void siteV1() {
		
		renderArgs.put("page", page);
        render();
    }
	
	public static void postMessage(){
		checkAuthenticity();
		String sessionIDContact=session.getId()+"_postMessage";
		Object tmp=Cache.get(sessionIDContact);
		if(tmp!=null){
			params.flash();
			flash.put("contact.antispam", Messages.get("contact.antispam"));
		}else{
			Message msg=new Message();
			msg.edit(params);
		    validation.valid(msg);
		    if(validation.hasErrors()) {
		    	params.flash();
		    	validation.keep();
		    	flash.put("contact.error", Messages.get("contact.error",validation.errors()));
		    }else{
		    	msg.save();
		    	page.addConsole("New message from: %s", request.remoteAddress);
		    	flash.put("contact.success", Messages.get("contact.success"));
		    	Cache.set(sessionIDContact,true,"1min");
		    }
		}
		contact();
	}
	
	@NoTransaction
	public static void setLanguage(String lang){
    	if(lang!=null && page.languages.containsKey(lang)){
    		Lang.change(lang);
    		page.updateLanguages();
    	}
    	redirectCallingPage();
    }
	
	public static boolean loginValid(){
		String login=session.get("login");
		if(login==null)
			return false;
		long diff=Long.parseLong(String.valueOf(Play.configuration.get("application.timeout")));
		long start=Long.parseLong(login);
		if( (System.currentTimeMillis()-start)<diff){
			return true;
		}else{
			flash("login.error","Session timeout!");
			return false;
		}
	}
	
	public static void redirectCallingPage(){
		try {
			Header refererHeader = Http.Request.current().headers.get("referer");
			if (refererHeader != null) {
				List<String> refererList = refererHeader.values;
				if (refererList != null) {
					String callingPageURL = refererList.get(0);
					if (callingPageURL != null && callingPageURL.length() > 0) {
						redirect(new URL(callingPageURL).getFile());
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		index();
	}

}