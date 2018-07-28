package models.jobs;

import java.io.File;
import java.util.concurrent.locks.ReentrantLock;

import play.Play;
import play.jobs.Job;

public class CameraJob extends Job{
	
	public static final CameraJob INSTANCE = new CameraJob();
	private ReentrantLock mutex=new ReentrantLock();
	
	private Exception lastException;
	
	private static final String CAMERA_PICTURE_DFL = "/public/images/cameraPicture.jpg";
	
	private String cameraPicture=CAMERA_PICTURE_DFL;
	
	private CameraJob(){

	}
	
	private void cleanPictures(){
		File folder=new File(Play.applicationPath+"/public/camera/");
		if(!folder.exists()){
			folder.mkdirs();
		}
		for(File file:folder.listFiles()){
			if(file.isFile() && file.getName().endsWith(".jpg")){
				file.delete();
			}
		}
	}
	
	public void doJob(){
		this.mutex.lock();
		
		try{
			lastException=null;
			
			this.cleanPictures();
			
			// prepare temporary file
			File cameraFile=new File(Play.applicationPath+"/public/camera/"+System.currentTimeMillis()+".jpg");
			
			// take picture
			Runtime runtime=Runtime.getRuntime();
			String[]args=new String[]{"/bin/bash","-c","raspistill -t 1 -w 640 -h 480 -q 90 -e jpg -o "+cameraFile.getAbsolutePath()};
			Process process=runtime.exec(args);
			int ret=process.waitFor();
			if(ret!=0)
				throw new Exception("Camera command line returns: "+ret);
			
		    // save the path to this picture
		    this.cameraPicture=cameraFile.getAbsolutePath().replace(Play.applicationPath.getAbsolutePath(),"");
		
		}catch(Exception e){
			this.lastException=e;
			cameraPicture=CAMERA_PICTURE_DFL;
		}finally{
			this.mutex.unlock();
		}
		
	}
	
	public String getCameraPicture(){
		return cameraPicture;
	}

	public Exception getLastException() {
		return lastException;
	}

}
