package controllers;

import java.io.File;
import java.util.List;

import models.Article;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.With;

@With(Compress.class)
public class Articles extends Controller{
	
	public static void create(File html) throws Exception{
		checkAuthenticity();
	    Article a = new Article();
	    a.edit(params,html,false);
	    validation.valid(a);
	    if(validation.hasErrors()) {
	    	params.flash();
	    	validation.keep();
	    	Administration.articles(null);
	    }else{
	    	a.saveHTMLFile(html);
	    }
	    a.save();
	    Administration.articles(null);
	}
	
	public static void update(long id,File html) throws Exception{
		checkAuthenticity();
		Article a = Article.findById(id);
	    a.edit(params,html,true);
	    validation.valid(a);
	    if(validation.hasErrors()) {
	    	validation.keep();
	    	Administration.articles(id);
	    }else if(html!=null){
	    	a.saveHTMLFile(html);
	    }
	    a.save();
	    Administration.articles(null);
	}
	
	public static void actions(){
		checkAuthenticity();
		String update=params.get("update");
		if(update!=null && !update.isEmpty()){
			long id=Long.parseLong(update);
			Administration.articles(id);
		}
		String[] remove=params.getAll("remove");
		if(remove!=null){
			for(String id:remove){
				Article.delete("id = ?", Long.parseLong(id));
			}
		}
		Administration.articles(null);
	}

}
