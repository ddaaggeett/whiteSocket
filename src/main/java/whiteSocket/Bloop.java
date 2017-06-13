/**
*   WhiteSocket
*   Copyright (C) 2015-2017 - Dave Daggett - Blooprint, LLC
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

/**
*	run this API (JAR file) from the Blooprint application (https://github.com/blooprint/blooprint)
*	java -jar Blooprint.jar <args>
*
*	purpose: input image from hard drive -> returns processed image to hard drive for display
*
*	for now, it's recommended the input image aspect ratio EQUALS the output image aspect ratio
*/

package main.java.whiteSocket;

import main.java.whiteSocket.*;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import javax.imageio.ImageIO;

import java.io.FileReader;
//import java.util.Iterator;

import org.apache.commons.io.FileUtils;

public class Bloop{

	private static boolean jarMode = false;

	public static ArrayList<Area> eraseAreas = null;
	/* TODO
	 * little redundant, but can fix later
	 * */
	public static boolean[][] totalErase = null;

	public static String title = "";	//	sketch image name (timestamp)
	public static String inMode = "";	//	write/erase/calibrate
	public static String writeColor = "black";  //	default black
	public static int markerHex = 0x000000;  //	default black
	public static BufferedImage sketch;
	public static BufferedImage blooprint;

	public static double topSlope,bottomSlope,leftSlope,rightSlope;

	public static int aax,aay,bbx,bby,ccx,ccy,ddx,ddy;

	public static double unit_aax,unit_aay,unit_bbx,unit_bby,unit_ccx,unit_ccy,unit_ddx,unit_ddy;

	public static double clientWidth, clientHeight;

	public static boolean[][] areaOfInterest;
	public static boolean[][] totalEraseArea;

	public static int tx, ty;

	/*
	 * brightThreshold = value at white any value less must be marker pixel
	 * TODO: tinker with proper value or better method -> see getBrightnessThreshold()
	 * */
	public static int brightThreshold;

	public static String blooprintFile = "";
	public static String sketchFile = "";
	public static String test_sketch = "";
	public static String test_image = "";
	
	public static void main(String[] args) throws Exception{

		System.out.println("****************\n****************\n\nYou are using Blooprint \u00ae software.\n\nPlease refer to our license here:\nhttp://github.com/blooprint/whiteSocket/blob/master/LICENSE\n\n****************\n****************");
		
		title = args[0];

		jarMode = (args[4].toLowerCase().equals("true") ? true : false);

		if (jarMode) {
			System.out.println("\nwhiteSocket: PRODUCTION EXECUTABLE/JAR MODE\n");
			sketchFile = "/input/" + args[0] + ".bmp";
			blooprintFile = "/output/" + args[1] + ".bmp";	
		}
		else {
			System.out.println("\nwhiteSocket: DEVELOPMENT MODE\n");
			sketchFile = "./io/input/" + args[0] + ".bmp";
			blooprintFile = "./io/output/" + args[1] + ".bmp";
		}
		

		inMode = args[2];

		markerHex = Integer.parseInt(args[3],16);
		System.out.println("marker decimal value = " + markerHex);
		

		if(args[1] != null) blooprint = loadBlooprint();
		sketch = loadSketch();
		
		brightThreshold = getBrightnessThreshold();

	    switch(inMode){

			case "write":
				calibrate();
				write();
				saveBlooprint();
				break;

			case "erase":
				calibrate();

				eraseAreas = new ArrayList<Area>();
				Area.totalErase = new boolean[sketch.getHeight()][sketch.getWidth()];

				Color pxColor = null;
				for (int row = 0; row < sketch.getHeight(); row++){
					for(int col = 0; col < sketch.getWidth(); col++){

						pxColor = new Color(sketch.getRGB(col,row));
						/*
						 * TODO:
						 * !totalErase[][] - to make sure the pixel isn't yet considered as an eraser pixel
						 * */
						if(areaOfInterest[row][col] && isMarker(pxColor) && !Area.totalErase[row][col]){
							eraseAreas.add(new Area(col,row));
						}
					}
				}

				for (Area some : eraseAreas) {
					erase(some.area);
				}

				saveBlooprint();
				break;

			default:
				break;
		}
	    
	    System.exit(0);

	}//END main()

	public static void write() {
		/**
		 * bloop blooprint.image pixel location intended by user bloop action
		 * sets Color.RED,BLUE,GREEN according to user intension
		 * */

		System.out.println("writing......");


		int[] xyOUT = new int[2];

		for (int row = 0; row < sketch.getHeight(); row++){
			for(int col = 0; col < sketch.getWidth(); col++){

				int xIN = col;
				int yIN = row;

				Color pxColor = new Color(sketch.getRGB(xIN,yIN));

				try{
					if(areaOfInterest[yIN][xIN]){

						if (isMarker(pxColor)){

							xyOUT = Stretch.stretch(xIN, yIN);

							blooprint.setRGB(xyOUT[0], xyOUT[1], markerHex);

						}
					}
				}
				catch(Exception e){
					System.out.println("ERROR write(): " + e.getMessage());
					System.out.println("x = " + xyOUT[0] + "\ty = " + xyOUT[1]);
					System.exit(0);
				}


			}
		}
	}//END write()

	public static void erase(boolean[][] eraseArea) {
		/**
		 * erase the area found inside the outer border of marker line drawn
		 * */

		System.out.println("erasing......");


		int[] xyOUT = new int[2];

		for (int row = 0; row < sketch.getHeight(); row++){
			for(int col = 0; col < sketch.getWidth(); col++){

				int xIN = col;
				int yIN = row;

				try{
					if(eraseArea[yIN][xIN]){

						xyOUT = Stretch.stretch(xIN, yIN);
						blooprint.setRGB(xyOUT[0], xyOUT[1], 0xffffff);


					}
				}
				catch(Exception e){
					e.printStackTrace();
				}


			}
		}

	}//END erase()

	public static boolean comparePixels(Color a, Color b) {
		if (Bloop.isMarker(a) && !Bloop.isMarker(b)) {
			return true;
		}
		return false;
	}//END comparePixels()

	public static int[] zoomToBox() {
		/*
		 * returns box coordinates wrapping a border
		 * */
		Border border = null;
		int[] box = new int[4];

		here:
		for(int row = 0; row < sketch.getHeight(); row++){
			for(int col = 0; col < sketch.getWidth(); col++){
				int xIN = col;
				int yIN = row;
				Color pixel = new Color(sketch.getRGB(xIN,yIN));
	            if(areaOfInterest[yIN][xIN] && isMarker(pixel)){
	            	border = new Border(xIN, yIN);
	            	break here;
		        }
			}
		}

		box[0] = border.yMin-2;
		box[1] = border.yMax+2;
		box[2] = border.xMin-2;
		box[3] = border.xMax+2;

		return box;
	}//END zoomToBox()

	public static int[] getScanBoxCorners(int ymin, int ymax, int xmin, int xmax) {
		/**
		 * finds user defined projector corners (xy-coordinates) on whiteboard
		 *
		 * action occurs on the input camera image
		 *
		 * returns int[8] - corner order UL, UR, LL, LR in x,y sequence
		 * */
		int[] some = new int[8];
		Color pixel = null;
        boolean hit = false;
        int rowStart = ymin;
        int row;

        Next1:
        try
        {
            while (!hit)
            {
                rowStart++;
                int col = xmin;
                row = rowStart;
                while (row > ymin)
                {
                	pixel = new Color(sketch.getRGB(col, row));
                    if (isMarker(pixel))
                    {
                    	some[0] = col;
                    	some[1] = row;
                        System.out.println("ULx = "+some[0]);
                        System.out.println("ULy = "+some[1]);
                        hit = true;
                        break Next1;
                    }
                    col++;
                    row--;
                }
            }//end UL corner
        }catch(Exception e)
        {
        	System.out.println("\nCalibration.getScanBoxCorners() ERROR: UP-LEFT corner\n" + e.getMessage());
        }
        //  upper right
        hit = false;
        rowStart = ymin;
        Next2:
        try
        {
            while (!hit)
            {
                rowStart++;
                int col = xmax;
                row = rowStart;
                while (row > ymin)
                {
                	pixel = new Color(sketch.getRGB(col, row));
                    if (isMarker(pixel))
                    {
                    	some[2] = col;
                    	some[3] = row;
                        System.out.println("URx = "+some[2]);
                        System.out.println("URy = "+some[3]);
                        hit = true;
                        break Next2;
                    }
                    col--;
                    row--;
                }
            }//end UR corner
        }
        catch (Exception e)
        {
        	System.out.println("\nCalibration.getScanBoxCorners() ERROR: UP-RIGHT corner\n" + e.getMessage());
        }
        //  lower left
        hit = false;
        rowStart = ymax;
        Next3:
        try
        {
        	while (!hit)
            {
                rowStart--;
                int col = xmin;
                row = rowStart;
                while (row < ymax)
                {
                	pixel = new Color(sketch.getRGB(col, row));
                    if (isMarker(pixel))
                    {
                    	some[4] = col;
                    	some[5] = row;
                        System.out.println("LLx = "+some[4]);
                        System.out.println("LLy = "+some[5]);
                        hit = true;
                        break Next3;
                    }
                    col++;
                    row++;
                }
            }//end LL corner
        }
        catch (Exception e)
        {
        	System.out.println("\nCalibration.getScanBoxCorners() ERROR: LOW-LEFT corner\n" + e.getMessage());
        }
        //  lower right
        hit = false;
        rowStart = ymax;
        Next4:
        try
        {
            while (!hit)
            {
                rowStart--;
                int col = xmax;
                row = rowStart;
                while(row < ymax)
                {
                	pixel = new Color(sketch.getRGB(col, row));
                    if (isMarker(pixel))
                    {
                    	some[6] = col;
                    	some[7] = row;
                        System.out.println("LRx = "+some[6]);
                        System.out.println("LRy = "+some[7]);
                        hit = true;
                        break Next4;
                    }
                    col--;
                    row++;
                }
            }//end LR corner
        }
        catch (Exception e)
        {
        	System.out.println("\nCalibration.getScanBoxCorners() ERROR: LOW-RIGHT corner\n" + e.getMessage());
        }

        return some;

	}//END getScanBoxCorners()

	public static float[] setUnitTextbox(int[] corners) {
		/*
		returns Rectangle to  -> x,y,width,height
		*/

		Rectangle rect = new Rectangle();
		int x = corners[0];
		int y = corners[1];

//		corner order UL, UR, LL, LR in x,y sequence

		int width = 0;
		int height = 0;

		if(corners[2] >= corners[6]){
			width = corners[6]-x;
		}
		else{
			width = corners[2]-x;
		}

		if(corners[5] > corners[7]){
			height = corners[7]-y;
		}
		else{
			height = corners[5]-y;
		}


		//	scales to display output
		double x2 		= (double)x 		/ (double)sketch.getWidth() 	* (double)blooprint.getWidth();
		double y2 		= (double)y 		/ (double)sketch.getHeight() * (double)blooprint.getHeight();
		double width2 	= (double)width 	/ (double)sketch.getWidth() 	* (double)blooprint.getWidth();
		double height2 	= (double)height 	/ (double)sketch.getHeight() * (double)blooprint.getHeight();


		int aa = (int)Math.round(x2);
		int bb = (int)Math.round(y2);
		int cc = (int)Math.round(width2);
		int dd = (int)Math.round(height2);

		rect.setBounds(aa,bb,cc,dd);


		/*
		Unit boc in order to scale to any client side browser screen dimensions.
		Must be re-scaled to web application DOM element textarea location
		*/
		float[] unit = new float[4];
		unit[0] = (float)aa / (float)blooprint.getWidth();
		unit[1] = (float)bb / (float)blooprint.getHeight();
		unit[2] = (float)cc / (float)blooprint.getWidth();
		unit[3] = (float)dd / (float)blooprint.getHeight();



		return unit;
	}//END setUnitTextbox()

	public static void calibrate() throws Exception{
		System.out.println("init calibration ...");
		Area.getLightBounds();

		areaOfInterest = getAreaOfInterestBorder();
//		Area.printImgBool(areaOfInterest, "aoi-border");

		/*
		 * start flooding right below center of topSlope
		 * */
		tx = (Stretch.ax+Stretch.bx)/2;
		ty = (Stretch.ay+Stretch.by)/2+5;
		areaOfInterest = Area.floodBorder(null, areaOfInterest, tx, ty);
		
//		Area.printImgBool(areaOfInterest, "aoi-fill");
		
		Stretch.width = blooprint.getWidth();
		Stretch.height= blooprint.getHeight();

		Tilt.setTiltedCalibration();

	}//END calibrate()

	public static void setCorners() {
		/**
		 *	sets -> ax,ay,bx,by,cx,cy,dx,dy
		 *	gets corner values user draws on whiteboard (corners of lit projection area)
		 *
		 *	TODO:
		 * */

		boolean hit = false;
		int rowStart = 0;
		int row;
		Color pixel = null;

		Next1:
		try
        {
            while (!hit)
            {
                rowStart++;
                int col = 0;
                row = rowStart;
                while (row > 0)
                {
                	pixel = new Color(sketch.getRGB(col, row));
                    if (areaOfInterest[row][col] && isMarker(pixel))
                    {
                    	Stretch.ax = col;
                    	Stretch.ay = row;
                        System.out.println("Corner 1:\tULx = "+Stretch.ax+"\tULy = "+Stretch.ay);
                        hit = true;
                        break Next1;
                    }
                    col++;
                    row--;
                }
            }//end UL corner
        }catch(Exception e)
        {
        	System.err.println("\nCalibration.setCorners() ERROR: UP-LEFT corner\n");
        	e.getMessage();
        	e.printStackTrace();
        }

		//  upper right
        hit = false;
        rowStart = 0;
        Next2:
        try
        {
            while (!hit)
            {
                rowStart++;
                int col = sketch.getWidth()-1;
                row = rowStart;
                while (row > 0)
                {
                	pixel = new Color(sketch.getRGB(col, row));
                    if (areaOfInterest[row][col] && isMarker(pixel))
                    {
                    	Stretch.cx = col;
                    	Stretch.cy = row;
                        System.out.println("Corner 2:\tURx = "+Stretch.cx+"\tURy = "+Stretch.cy);
                        hit = true;
                        break Next2;
                    }
                    col--;
                    row--;
                }
            }//end UR corner
        }
        catch (Exception e)
        {
        	System.err.println("\nCalibration.setCorners() ERROR: UP-RIGHT corner\n");
        	e.getMessage();
        	e.printStackTrace();
        }
        //  lower left
        hit = false;
        rowStart = sketch.getHeight()-1;
        Next3:
        try
        {
        	while (!hit)
            {
                rowStart--;
                int col = 0;
                row = rowStart;
                while (row < sketch.getHeight()-1)
                {
                	pixel = new Color(sketch.getRGB(col, row));
                    if (areaOfInterest[row][col] && isMarker(pixel))
                    {
                    	Stretch.dx = col;
                    	Stretch.dy = row;
                        System.out.println("Corner 3:\tLLx = "+Stretch.dx+"\tLLy = "+Stretch.dy);
                        hit = true;
                        break Next3;
                    }
                    col++;
                    row++;
                }
            }//end LL corner
        }
        catch (Exception e)
        {
        	System.err.println("\nCalibration.setCorners() ERROR: LOW-LEFT corner\n");
        	e.getMessage();
        	e.printStackTrace();
        }
        //  lower right
        hit = false;
        rowStart = sketch.getHeight()-1;
        Next4:
        try
        {
            while (!hit)
            {
                rowStart--;
                int col = sketch.getWidth()-1;
                row = rowStart;
                while(row < sketch.getHeight()-1)
                {
                	pixel = new Color(sketch.getRGB(col, row));
                    if (areaOfInterest[row][col] && isMarker(pixel))
                    {
                    	Stretch.bx = col;
                    	Stretch.by = row;
                        System.out.println("Corner 4:\tLRx = "+Stretch.bx+"\tLRy = "+Stretch.by);
                        hit = true;
                        break Next4;
                    }
                    col--;
                    row++;
                }
            }//end LR corner
        }
        catch (Exception e)
        {
        	System.err.println("\nCalibration.setCorners() ERROR: LOW-RIGHT corner\n");
        	e.getMessage();
        	e.printStackTrace();
        }
	}//END setCorners()

	public static BufferedImage loadBlooprint() throws IOException {
		/**
		 * load image from DB table - either an input sketch or a compiled blooprint image
		 *	sketch areg = "null"
		 * BLOB object to binary stream to BufferedImage object
		 * */
		System.out.println("loadBlooprint() from " + blooprintFile);
		InputStream stream = null;
		BufferedImage some = null;
		try{
			/**
			 * http://stackoverflow.com/questions/39081215/access-a-resource-outside-a-jar-from-the-jar
			 * */

			stream = Bloop.class.getClass().getResourceAsStream(blooprintFile);
//			stream = null;

			if ( !jarMode ) {
				System.out.println("loading blooprint as test image");
				some = ImageIO.read(new File(blooprintFile));
				return some;
			}

			System.out.println("blooprint stream = "+ stream );
		}
		catch(Exception e) {
			System.out.println("error blooprint to stream: " + e.getMessage());
		}
		try{

			some = ImageIO.read(stream);
		}catch(Exception exc){
			exc.getMessage();
		}
		return some;
	}//END loadBlooprint()

	public static BufferedImage loadSketch() throws Exception {
		/**
		 * load image from DB table - either an input sketch or a compiled blooprint image
		 *	sketch arg = "null"
		 * BLOB object to binary stream to BufferedImage object
		 * */
		System.out.println("loadSketch() from " + sketchFile);
		InputStream stream = null;
		BufferedImage some = null;
		try{
			/**
			 * http://stackoverflow.com/questions/39081215/access-a-resource-outside-a-jar-from-the-jar
			 * */

			stream = Bloop.class.getClass().getResourceAsStream(sketchFile);
//			stream = null;

			if ( !jarMode ) {
				some = ImageIO.read(new File(sketchFile));
				Stretch.fx = some.getWidth()-1;
				Stretch.gx = some.getWidth()-1;
				Stretch.fy = some.getHeight()-1;
				Stretch.hy = some.getHeight()-1;
				return some;
			}
			System.out.println("sketch stream = "+ stream );
		}
		catch(Exception e) {
			System.out.print("error sketch to stream: ");
			System.out.println(e.getMessage());
		}
		try{
			some = ImageIO.read(stream);
			Stretch.fx = some.getWidth()-1;
			Stretch.gx = some.getWidth()-1;
			Stretch.fy = some.getHeight()-1;
			Stretch.hy = some.getHeight()-1;
		}catch(Exception exc){
			System.out.print("error stream to image: ");
			System.out.println(exc.getMessage());
		}
		return some;
	}//END loadSketch()

	public static void saveBlooprint() throws IOException {
		System.out.println("saveBlooprint()...");

		try{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(blooprint, "bmp", baos);
			InputStream stream = new ByteArrayInputStream(baos.toByteArray());
			File outputfile = null;
			
			if(jarMode) outputfile = new File("./output/"+title+".bmp");
			else outputfile = new File("./io/output/"+title+".bmp");

			FileUtils.copyInputStreamToFile(stream, outputfile);

		}catch(Exception ex){
			System.out.println("ERROR saveBlooprint(): " + ex.getMessage());
		}
	}//END saveBlooprint()

	public static boolean[][] getAreaOfInterestBorder() {
		/**
		 * dealing with lit projector area on whiteboard
		 * sets binary border for future use in floodBorder() method
		 * */

		boolean[][] border = new boolean[sketch.getHeight()][sketch.getWidth()];

		for(int x = Stretch.ax; x <= Stretch.cx; x++){//top

			double intersect_double = Stretch.cy - (topSlope*Stretch.cx);
			int intersect = (int) Math.round(intersect_double);
			double y_double = (topSlope * x) + intersect;
			int y = (int) Math.round(y_double);
			border[y][x] = true;

		}
		for(int x = Stretch.dx; x <= Stretch.bx; x++){//bottom

			double intersect_double = Stretch.by - (bottomSlope*Stretch.bx);
			int intersect = (int) Math.round(intersect_double);
			double y_double = (bottomSlope * x) + intersect;
			int y = (int) Math.round(y_double);
			border[y][x] = true;

		}
		for(int y = Stretch.ay; y <= Stretch.dy; y++){//left

			double intersect_double = Stretch.dy - (leftSlope*Stretch.dx);
			int intersect = (int) Math.round(intersect_double);
			double x_double = (y-intersect)/leftSlope;
			int x = (int) Math.round(x_double);
			border[y][x] = true;

		}
		for(int y = Stretch.cy; y <= Stretch.by; y++){//right

			double intersect_double = Stretch.by - (rightSlope*Stretch.bx);
			int intersect = (int) Math.round(intersect_double);
			double x_double = (y-intersect)/rightSlope;
			int x = (int) Math.round(x_double);
			border[y][x] = true;

		}

		return border;
	}//END getAreaOfInterestBorder()

	public static boolean isMarker(Color x) {
		
		int brightness = (int) (0.2126*x.getRed() + 0.7152*x.getGreen() + 0.0722*x.getBlue());
		
		if( brightness < brightThreshold) return true;
		else return false;
		
	}//END isMarker()

	private static int getBrightnessThreshold() {
		/**
		 * get average pixel brightness of white pixels whiteboard input image
		 * NOTE: lower brightness value = darker the pixel
		 * using relative luminance
		 * https://en.wikipedia.org/wiki/Relative_luminance
		 * 
		 * rough threshold value = 30% less than average of darkest corner
		 * assumes average corner spans 60% brightness (+/-30%)
		 */
		
		Color color = null;
		float brightness = 0;
		int r,g,b;
		int count = 0;
		int avg_UL,avg_UR,avg_LL,avg_LR;
		float totalBrightness = 0;
		
		for (int row = 0; row < 10; row++){	//	UL
			for(int col = 0; col < 10; col++){
				count++;
				color = new Color(sketch.getRGB(col,row));
				r = color.getRed();
				g = color.getGreen();
				b = color.getBlue();
				brightness = (float) (0.2126*r + 0.7152*g + 0.0722*b);
				totalBrightness += brightness;
			}
		}
		avg_UL = (int) (totalBrightness/count);
		
		totalBrightness = 0;
		count = 0;
		for (int row = 0; row < 10; row++){	//  UR
			for(int col = sketch.getWidth()-10; col < sketch.getWidth(); col++){
				count++;
				color = new Color(sketch.getRGB(col,row));
				r = color.getRed();
				g = color.getGreen();
				b = color.getBlue();
				brightness = (float) (0.2126*r + 0.7152*g + 0.0722*b);
				totalBrightness += brightness;
			}
		}
		avg_UR = (int) (totalBrightness/count);
		
		totalBrightness = 0;
		count = 0;
		for (int row = sketch.getHeight()-10; row < sketch.getHeight(); row++){	//	LL
			for(int col = 0; col < 10; col++){
				count++;
				color = new Color(sketch.getRGB(col,row));
				r = color.getRed();
				g = color.getGreen();
				b = color.getBlue();
				brightness = (float) (0.2126*r + 0.7152*g + 0.0722*b);
				totalBrightness += brightness;
			}
		}
		avg_LL = (int) (totalBrightness/count);
		
		totalBrightness = 0;
		count = 0;
		for (int row = sketch.getHeight()-10; row < sketch.getHeight(); row++){	//	LR
			for(int col = sketch.getWidth()-10; col < sketch.getWidth(); col++){
				count++;
				color = new Color(sketch.getRGB(col,row));
				r = color.getRed();
				g = color.getGreen();
				b = color.getBlue();
				brightness = (float) (0.2126*r + 0.7152*g + 0.0722*b);
				totalBrightness += brightness;
			}
		}
		avg_LR = (int) (totalBrightness/count);
		
		int threshold = (int) (0.7*Math.min(Math.min(avg_LL,avg_LR),Math.min(avg_UL,avg_UR)));
		
		System.out.println("\nthreshold = "+threshold);
		
		return threshold;
	}
}
