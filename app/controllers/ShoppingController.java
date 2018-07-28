package controllers;

import java.util.List;

import models.shopping.Item;
import play.mvc.Controller;

public class ShoppingController extends Controller{
	
	public static void index(){
		List<Item> items = Item.find("order by id").fetch();
		render(items);
	}
	
	public static void buy(Long id, String buyer){
		checkAuthenticity();
		Item item = Item.findById(id);
		item.buyer = buyer;
		item.save();
		index();
	}

}
