package models;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import play.data.validation.Required;
import play.db.jpa.Model;
import play.mvc.Scope.Params;

@Entity
@Table(name="MESSAGE")
public class Message extends Model {
	
	@Required
	@Column(name="SUBJECT",nullable=false,length=50)
	public String subject;
	
	@Required
	@Column(name="EMAIL",nullable=false,length=50)
	public String email;
	
	@Required
	@Column(name="DATE",nullable=false)
	public Date date;

	@Required
	@Column(name="MESSAGE",nullable=false,length=1024)
	public String message;

	public void edit(Params params) {
		this.subject=params.get("subject");
		this.email=params.get("email");
		this.date=new Date();
		this.message=params.get("message");
	}
	
}
