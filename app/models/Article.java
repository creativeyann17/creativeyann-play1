package models;

import java.io.File;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import models.FileUtils;
import play.Play;
import play.data.validation.Required;
import play.data.validation.Validation;
import play.db.jpa.Model;
import play.mvc.Scope.Params;

@Entity
@Table(name="ARTICLE")
public class Article extends Model {
	
	@Required
	@Column(name="NAME",nullable=false,length=50)
	public String name;
	
	@Required
	@Column(name="LANGUAGE",nullable=false,length=10)
	public String language;
	
	@Required
	@Column(name="DATE",nullable=false)
	public Date date;

	@Required
	@Column(name="DESCRIPTION",nullable=false,length=200)
	public String description;
	
	@Required
	@Column(name="CONTEXT",nullable=false,length=50)
	public String context;
	
	@Required
	@Column(name="HTML",nullable=false,length=32)
	public String html;
	
	@Transient
	public String oldHTML;
	
	@Column(name="VISIBLE",nullable=false)
	public boolean visible=false;

	
	public void edit(Params params,File html, boolean isUpdate){
		this.name=params.get("name");
		this.language=params.get("language");
		this.date=new Date();
		this.description=params.get("description");
		this.context=params.get("context");
		this.visible=Boolean.parseBoolean(params.get("visible"));
		if(html!=null){
			this.oldHTML=this.html;
			this.html=html.getName();
			if(!isUpdate){
				Article article=Article.find("html = ?", this.html).first();
				if(article!=null) Validation.addError("a.html", "Already set to another article",this.html);
			}
		}
	}
	
	public void saveHTMLFile(File htmlFile) throws Exception{
		if(this.oldHTML!=null){
			File old=new File(Play.applicationPath.getAbsolutePath()+"/public/articles/"+this.oldHTML);
			if(old.exists())old.delete();
		}
		File dest=new File(Play.applicationPath.getAbsolutePath()+"/public/articles/"+htmlFile.getName());
		if(dest.exists())dest.delete();
		FileUtils.copy(htmlFile, dest);
	}
}