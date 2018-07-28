package controllers;

import models.shopping.Item;
import play.mvc.Before;

@CRUD.For(Item.class)
public class Items extends CRUD {
	
	@Before
	public static void checkLogin(){
		if(!Application.loginValid()){
			Application.admin();
		}else{
			session.put("login", System.currentTimeMillis());
		}
	}

}
