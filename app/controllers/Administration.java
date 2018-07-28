package controllers;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import models.Article;
import models.Message;
import models.jobs.CameraJob;
import play.db.jpa.NoTransaction;
import play.db.jpa.Transactional;
import play.libs.F.Promise;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;

@With(Compress.class)
public class Administration extends Controller{
	
	@Before
	public static void checkLogin(){
		if(!Application.loginValid()){
			Application.admin();
		}else{
			session.put("login", System.currentTimeMillis());
		}
	}
	
	@Transactional(readOnly=true)
	public static void index(){
		renderArgs.put("messagesCount", Message.count());
		renderArgs.put("page", Application.page);
		render();
	}
	
	@NoTransaction
	public static void console(){
		renderArgs.put("consoleMessages", Application.page.getConsole());
		renderArgs.put("page", Application.page);
		render();
	}
	
	@Transactional(readOnly=true)
	public static void messages(){
		renderArgs.put("page", Application.page);
		List<Message> msgs=Message.find("order by date desc").fetch();
        render(msgs);
	}
	
	@Transactional(readOnly=true)
	public static void articles(Long id){
		List<Article> articles=Article.find("order by date desc").fetch();
		if(id!=null){
			renderArgs.put("article", Article.findById(id));	
		}
		renderArgs.put("articles", articles);
		renderArgs.put("page", Application.page);
		render();
	}
	
	@NoTransaction
	public static void camera(){
		renderArgs.put("cameraPicture", CameraJob.INSTANCE.getCameraPicture());
		renderArgs.put("page", Application.page);
		render();
	}
	
	@NoTransaction
	public static void doCameraPicture(){
		checkAuthenticity();
		CameraJob job=CameraJob.INSTANCE;
		/*Promise promise=job.now();
		awaitSessionAware(promise);*/
		job.doJob();
		if(job.getLastException()!=null){
			flash("cameraException", job.getLastException());
		}
		camera();
	}
	
	/*protected static <T> T awaitSessionAware(Future<T> future) {
	    final String sessionId = session.getId();
	    T result = await(future);
	    session.put("___ID", sessionId);
	    return result;
	}*/
	
	public static void msgActions(){
		checkAuthenticity();
		Map<String, String[]>params=request.params.all();
		for(String key:params.keySet()){
			if(key.equals("remove")){
				String[] values=params.get(key);
				for(String value:values){
					Long id=Long.parseLong(value);
					Message.delete("id = ?", id);
				}
			}
		}
		messages();
	}

}
