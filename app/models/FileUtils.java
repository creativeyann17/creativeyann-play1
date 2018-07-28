package models;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class FileUtils {

	private FileUtils(){}
	
	public static void copy(File src, File dest) throws IOException {

		if (src.isDirectory()) {

			if (!dest.exists()) {
				dest.mkdir();
			}

			String files[] = src.list();

			for (String file : files) {
				File srcFile = new File(src, file);
				File destFile = new File(dest, file);
				copy(srcFile, destFile);
			}

		} else {

			InputStream in = new FileInputStream(src);
			OutputStream out = new FileOutputStream(dest);

			byte[] buffer = new byte[1024];

			int length;

			while ((length = in.read(buffer)) > 0) {
				out.write(buffer, 0, length);
			}

			in.close();
			out.close();

		}
	}
	
	public static boolean delete(File root)throws IOException{
    	if(root.isDirectory()){
    		for (File file : root.listFiles()) {
    		   delete(file);
    		}
    		return root.delete();
    	}else{
    		return root.delete();	    	   
    	}
    }
}
