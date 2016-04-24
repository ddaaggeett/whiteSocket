/**
*   BLOOPRINT.XYZ: commoditizing hand-written design
*   Copyright (C) 2016 - Dave Daggett, EIT
*
*   This program is free software; you can redistribute it and/or modify
*   it under the terms of the GNU General Public License as published by
*   the Free Software Foundation; either version 3 of the License, or
*   (at your option) any later version.
*
*   This program is distributed in the hope that it will be useful,
*   but WITHOUT ANY WARRANTY; without even the implied warranty of
*   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*   GNU General Public License for more details.
*
*   You should have received a copy of the GNU General Public License
*   along with this program; if not, write to the Free Software Foundation,
*   Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301  USA
*/

package xyz.blooprint;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.Queue;

import javax.imageio.ImageIO;



/**
 * BLOOP object: all methods and objects that have anything to do with hand-drawn/written input
 * */
public class BLOOP extends BLOOPRINT{
	
	public static BufferedImage sketch;
	boolean[][] inBoolArea;
	/*-----------------------------------------------------------------*/
	
	public static int xIN, yIN;
	
	
	
	/*
	 * TODO:	create setup script to create platform ready to perform if hardware has remained untouched since last use
	 * */
	
	
	public BLOOP() throws Exception{ super(); }
    
	/**
	 * a bloop action creates a BLOOP object - image input
	 * */
    public BLOOP(String mode, boolean calibrate, boolean newText) throws Exception {

    	/**
    	 * TODO:
    	 * this needs to be used upon projector frame/ALL hardware setup
    	 * */
    	sketch = getSketch();
    	
    	/**
    	 * new JFrame to allow user to manually calibrate bounds of manipulation activity
    	 * */
    	if(calibrate){
    		
//    		/**
//    		 * ERASE THIS BLOCK
//    		 * this is a test case.
//    		 * sketch will already be set above this if/else block
//    		 * */
//    		sketch = ImageIO.read(new File(rawCornersImageFileName));
//    		/*-----------------*/ 
    		

    		projectorDisplay(new BorderSelect());
    		
    	}
    	else if(newText){
//    		/*-----------------*/
//    		sketch = ImageIO.read(new File(newTextImageFileName));
//    		/*-----------------*/
    		BLIP textArea = new BLIP(0,null,null);
    		
    		
    		blooprint.blips.add(textArea);
    		
    		
    		System.out.println("blooprint.textareas.size = " + blooprint.blips.size());
    		
    		
    		textArea.display();
    		
    	}
    	else{
//    		/*-----------------*/
//    		sketch = ImageIO.read(new File(sketchImageFileName));
//    		/*-----------------*/
    		
    		/* TODO:
    		 * error occurring in write()
    		 * 
    		 * */
    		
    		System.out.println("\nok here\n");
    		
    		manipulatePixels(mode);
    		
    		displayImagePanel(blooprint.image);
    		
    		saveImage();
    		
    		saveTextAreas();
    	}
    	

    	
    	
		
		
	}//END BLOOP() constructor
	
    
    /**
     * divides up the work to be done
     * WRITE+ERASE -> eventually CLOUD,DOTTED,etc
     * */
	private void manipulatePixels(String mode) {
		/*
		 * uses 'sketch' as input - applies changes to 'blooprint' as output
		 * */
		System.out.println("manipulating pixels.....");
		
		switch (mode) {
		case "write":
			write();
			break;
		
		
		
		case "erase":
			inBoolArea = findBoolArea();
			erase(inBoolArea);
			break;
			
			
			
		default:
			write();
			break;
				
        }

		
	}//END manipulatePixels()

	
	/**
	 * binary map of pixels marking where area is pertaining to erase action
	 * */
	public static boolean[][] findBoolArea() {
		
		
		/**
		 * TODO: need to scan for multiple findBoolArea areas.  so far we are only checking for
		 * the first one that we come across.
		 * */
		
		boolean[][] some = getBorder();
		
		
		
		int xStart = xIN;
		int yStart = yIN + 2;
		
		some = floodBorder(some, xStart,yStart);
		
		
		return some;
		
		
	}//END findBoolArea()

	/**
	 * dealing with area drawn by user to erase
	 * sets binary map single pixel strand border for future use in BLOOP.floodBorder() method
	 * */
	private static boolean[][] getBorder() {
		
		boolean[][] some = new boolean[sketch.getHeight()][sketch.getWidth()];
		
		here:
			
			for(int row = 0; row < sketch.getHeight(); row++){
				for(int col = 0; col < sketch.getWidth(); col++){

					/*
					 * dealing with pixels input Calibration.by user - sketch
					 * */
					Color pixel = new Color(sketch.getRGB(col,row));
					xIN = col;
		            yIN = row;
		            
		            
		            
		            if(Calibration.inActionArea[yIN][xIN] && isMarker(pixel)){

		            	System.out.println("xIN = "+xIN);
		            	System.out.println("yIN = "+yIN);
						
						System.out.println("\nfound eraser border!!!\n");
						
						/*
						 * encapsulate eraser area
						 * */
						int[] inCoord = new int[2];
						inCoord[0] = xIN;
						inCoord[1] = yIN;
						boolean flag = true;
						while(flag){
							
							//	2dArray[y][x]
							some[inCoord[1]][inCoord[0]] = true;
							
							inCoord = getNextBorderPixel(inCoord);
							
							if((inCoord[0] == xIN) && (inCoord[1] == yIN)){
								System.out.println("made it all the way around the border");
								
								flag = false;
								break here;
							}
						}
						
						
						
					}
					else{
						continue;
					}
				}
			}//END outer loop
	
	
		return some;
	}
	
	
	/**
	 * erase the area found inside the outer border of marker line drawn
	 * */
	private void erase(boolean[][] area) {
		
		/**	
		 * TODO: 
		 * Once outer corners values are set, we can probably sweep the whole photo from here on 
		 * out and check as long as if the pixels we're interested in are within the inBoolArea[][].
		 * Inner and outer for() loops can be bounds of BLOOP.sketch
		 * */
		for(int row = 0; row < BLOOP.sketch.getHeight(); row++){
        	for ( int col = 0; col < BLOOP.sketch.getWidth(); col++){
        		
        		xIN = col;
	            yIN = row;
	            
	            
	            int[] xyOUT;
        		if(Calibration.inActionArea[yIN][xIN] && area[xIN][yIN]){
        			xyOUT = Calibration.stretch(xIN, yIN);
        			blooprint.image.setRGB(xyOUT[0], xyOUT[1], 0xffffff);
        		}
        		
        	}
        }
		
	}//END erase()
	
	
	
	/**
	 * paint bucket-like algorithm to fill binary map border
	 * this filled area becomes the area to be filtered through the BLOOP.stretch() 
	 * area pixels to be turned Color.WHITE (unless notified otherwise by user dictation)
	 * 
	 * */
	public static boolean[][] floodBorder(boolean[][] some, int x, int y) {
		
        if (!some[y][x]) {

		    Queue<Point> queue = new LinkedList<Point>();
		    queue.add(new Point(x, y));

		    int pixelCount = 0;
		    while (!queue.isEmpty()) {
		        
		    	Point p = queue.remove();

		        	if (!some[p.y][p.x]) {
		                
		            	some[p.y][p.x] = true;
		                pixelCount++;

		                queue.add(new Point(p.x + 1, p.y));
		                queue.add(new Point(p.x - 1, p.y));
		                queue.add(new Point(p.x, p.y + 1));
		                queue.add(new Point(p.x, p.y - 1));
		            }
		    }
		    
		    System.out.println("area detected : " + pixelCount + " pixels");
		}
        
        
        
		return some;
	}//END floodBorder()
	
	
	/**
	 * write blooprint.image pixel location intended by user bloop action
	 * sets Color.RED,BLUE,GREEN according to user intension
	 * */
	private void write() {
		System.out.println("WRITING......");

		for(int row = 0; row < sketch.getHeight(); row++){
			for(int col = 0; col < sketch.getWidth(); col++){
				
				/**
				 * dealing with pixels input Calibration.by user - sketch
				 * */
				Color pixel = new Color(sketch.getRGB(col,row));
				xIN = col;
	            yIN = row;
	            
	            /*
	             * add colored pixels only to the output image - blooprint
	             * stretch(xIN,yIN) only where pixels are marked Calibration.by user
	             * no need to stretch entire sketch over top of blooprint
	             * */
	            int[] xyOUT = new int[2];
	            //change RGB levels accordingly
	            
	            if(Calibration.inActionArea[yIN][xIN]){
	            	
	            	try{
	            		
	            		if(isRed(pixel)){
	            			
	            			xyOUT = Calibration.stretch(xIN,yIN);//sets xOUT, yOUT
	            			System.out.println("RED\tx = "+xyOUT[0]+"\ty = "+xyOUT[1]);
	            			blooprint.image.setRGB(xyOUT[0], xyOUT[1], 0xff0000);//turn red
	            			
	            		}
	            		else if(isGreen(pixel)){
	            			
	            			xyOUT = Calibration.stretch(xIN,yIN);//sets xOUT, yOUT
	            			System.out.println("GREEN\tx = "+xyOUT[0]+"\ty = "+xyOUT[1]);
	            			blooprint.image.setRGB(xyOUT[0], xyOUT[1], 0x00ff00);//turn green
	            			
	            		}
	            		else if(isBlue(pixel)){
	            			
	            			xyOUT = Calibration.stretch(xIN,yIN);//sets xOUT, yOUT
	            			System.out.println("BLUE\tx = "+xyOUT[0]+"\ty = "+xyOUT[1]);
	            			blooprint.image.setRGB(xyOUT[0], xyOUT[1], 0x0000ff);//turn blue
	            			
	            		}
	            	}
	            	catch(Exception e){
	            		System.out.println("error writing.....");
	            		e.getMessage();
	            		e.printStackTrace();
	            		/**
	            		 * so error doesn't happen for every pixel
	            		 * */
	            		System.exit(0);
	            	}
	            }
	            
	            
	                
	            
			}
		}//END loop through pixels
	}//END write()

	
	/**
	 * recursive method locating pixel after last found pixel until entire border is lined
	 * */
	public static int[] getNextBorderPixel(int[] coord) {

		int[] next = new int[2];
		
		/*
		 * all 8 surrounding pixels need to be checked counterclockwise
		 * */
		Color a = new Color(sketch.getRGB(coord[0]+1,coord[1]));	//	R
		Color b = new Color(sketch.getRGB(coord[0]+1,coord[1]+1));	//	RD
		Color c = new Color(sketch.getRGB(coord[0],coord[1]+1));	//	D
		Color d = new Color(sketch.getRGB(coord[0]-1,coord[1]+1));	//	LD
		Color e = new Color(sketch.getRGB(coord[0]-1,coord[1]));	//	L
		Color f = new Color(sketch.getRGB(coord[0]-1,coord[1]-1));	//	LU
		Color g = new Color(sketch.getRGB(coord[0],coord[1]-1));	//	U
		Color h = new Color(sketch.getRGB(coord[0]+1,coord[1]-1));	//	RU
		
		
		if(isMarker(a) & !isMarker(h)){
			next[0] = coord[0]+1;	//	R
			next[1] = coord[1];
		}
		else if(isMarker(b) & !isMarker(a)){
			next[0] = coord[0]+1;	//	RD
			next[1] = coord[1]+1;
		}
		else if(isMarker(c) & !isMarker(b)){
			next[0] = coord[0];		//	D
			next[1] = coord[1]+1;
		}
		else if(isMarker(d) & !isMarker(c)){
			next[0] = coord[0]-1;	//	LD
			next[1] = coord[1]+1;
		}
		else if(isMarker(e) & !isMarker(d)){
			next[0] = coord[0]-1;	//	L
			next[1] = coord[1];
		}
		else if(isMarker(f) & !isMarker(e)){
			next[0] = coord[0]-1;	//	LU
			next[1] = coord[1]-1;
		}
		else if(isMarker(g) & !isMarker(f)){
			next[0] = coord[0];		//	U
			next[1] = coord[1]-1;
		}
		else if(isMarker(h) & !isMarker(g)){
			next[0] = coord[0]+1;	//	RU
			next[1] = coord[1]-1;
		}
		else{
			System.err.println("something wrong with setting next border pixel!!!");
		}
		
		
		return next;
	}//END search()

	
	/**
	 * checking if instantaneous pixel is blue or not
	 * returns true or false
	 * */
	private boolean isBlue(Color pixel) {
		if(pixel.getRed() < 100 & pixel.getGreen() < 100 & pixel.getBlue() > 100){
			return true;
		}
		return false;
	}//END isBlue()

	
	/**
	 * checking if instantaneous pixel is green or not
	 * returns true or false
	 * */
	private boolean isGreen(Color pixel) {
		if(pixel.getRed() < 100 & pixel.getGreen() > 100 & pixel.getBlue() < 100){
			return true;
		}
		return false;
	}//END isGreen()

	
	/**
	 * checking if instantaneous pixel is red or not
	 * returns true or false
	 * */
	private boolean isRed(Color pixel) {
		if(pixel.getRed() > 100 & pixel.getGreen() < 100 & pixel.getBlue() < 100){
			return true;
		}
		return false;
	}//END isRed()

	
	/**
	 * checking if instantaneous pixel is ANY color or not
	 * returns true or false
	 * */
	public static boolean isMarker(Color x) {
		if((x.getRed() > 100 & x.getGreen() < 100 & x.getBlue() < 100)
				|| (x.getRed() < 100 & x.getGreen() > 100 & x.getBlue() < 100)
				|| (x.getRed() < 100 & x.getGreen() < 100 & x.getBlue() > 100)
				|| (x.getRed() < 100 & x.getGreen() < 100 & x.getBlue() < 100)){
			
			return true;
		}
		return false;
	}//END isMarker()
	
	/**
	 * pulls sketch from BLOOPRINT.sketchDir
	 * */
	private BufferedImage getSketch() throws Exception {
		File newSketchFile = gatherNewestFile(sketchDir);
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
		
		capture();
		
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
	
	
	
	
}







