package xyz.blooprint;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

/**
 * Capture OBJECT - new thread - 
 * */
public class Capture extends BLOOP implements Runnable{
	
	Thread captureThread;

	@Override
	public void run() {
		try {
			
			
			sketch = getSketch();
			
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * for pulling purposes - only pull 1 file at a time
	 * */
	public void clearCamera() throws IOException{
		System.out.println("emptying the camera folder on android device..........");
		command("adb shell rm /sdcard/dcim/camera/");
	}
	
	/**
	 * runs and pulls captured image from camera to tmpDIR
	 * */
	private void captureAction() throws IOException {
	
		/**
		 * TODO:
		 * 
		 * */
		Long time = System.currentTimeMillis();
		String fileString = time.toString();
		System.out.println("fileString = "+fileString);
		List<String> some = command("adb shell ls /sdcard/dcim/camera/");
		int before = some.size();
		System.out.println("before\t=\t"+before);
		command("adb shell input keyevent 66");
		boolean flag = true;
		while(flag){
			some = new ArrayList<String>();
			some = command("adb shell ls /sdcard/dcim/camera/");
			int after = some.size();
			if(some.size() != before){
				System.out.println("after\t=\t"+after);
				command("adb pull /sdcard/dcim/camera/ "+win_tmpDir);
				flag = false;
			}
		}
	}//END captureAction() method

	public Capture() throws Exception{
		captureThread = new Thread(this);
		captureThread.start();
	}
	
	/**
	 * pulls sketch from BLOOPRINT.sketchDir
	 * */
	private BufferedImage getSketch() throws Exception {
		
		Long time = System.currentTimeMillis();
		String fileString = time.toString();
		System.out.println("fileString = "+fileString);
		
//		File oldName = new File(win_sketchDir+"tmp/*.jpg");
//		File newName = new File(win_sketchDir+fileString+".jpg");
//		
//		if(oldName.renameTo(newName)) {
//			System.out.println("RENAMED AND TRANSFERRED");
//		}
//		else{
//			System.out.println("Error");
//		}

		
		//	TODO: auto LINUX vs WINDOWS directory
		File newSketchFile = gatherNewestFile(win_sketchDir);
		BufferedImage img = ImageIO.read(newSketchFile);
		return img;
		
		
		
	}//END getSketch()

	
	/**
	 * checks file name and loops until differs from last file name
	 * means that new file is certainly added and can be copied
	 * */
	private File gatherNewestFile(String dir) throws Exception {
		File lastSketchFile = getLastFile(dir);
		File newFile = null;
		
		captureAction();
		
		boolean flag = true;
		while(flag){
			newFile = getLastFile(dir);
			if(newFile != lastSketchFile){
				flag = false;
			}
		}
		return newFile;
		
	}//END gatherNewestFile()

	/**
	 * returns single file that was saved last time
	 * */
	private File getLastFile(String dirPath) {
		//	TODO: is empty directory
		File dir = new File(dirPath);
	    File[] files = dir.listFiles();
	    if (files == null || files.length == 0) {
	        return null;
	    }

	    File lastModifiedFile = files[0];
	    for (int i = 1; i < files.length; i++) {
	       if (lastModifiedFile.lastModified() < files[i].lastModified()) {
	           lastModifiedFile = files[i];
	       }
	    }
	    return lastModifiedFile;
	    
	}//END getLastFile()	

	
}//END Capture CLASS
