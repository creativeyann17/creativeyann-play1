package models.shopping;

import javax.persistence.Entity;
import javax.persistence.Table;

import play.db.jpa.Model;

@Entity
@Table(name="ITEM")
public class Item extends Model{
	
	public String name;
	public String description;
	public String images;
	public String buy;
	public String sell;
	public String buyer;
	
	@Override
	public String toString(){
		return name;
	}

}
