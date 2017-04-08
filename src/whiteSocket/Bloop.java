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

package whiteSocket;

import whiteSocket.*;

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


import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


import java.io.FileReader;
//import java.util.Iterator;

import org.apache.commons.io.FileUtils;

public class Bloop{
	
	
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
	 * MARK = fixed RGB value for input drawing recognition
	 * SEE -> isMarker()
	 * */
	public static int mark = 150;

	public static String calibrationFile = "/calibration/calibration.json";
	public static String unitClicksFile = "/calibration/unitClicks.json";
	public static String blooprintLoc = "/blooprints/";
	public static String blooprintFile = "";
	public static String sketchLoc = "/sketches/";
	public static String sketchFile = "";
	public static String test_sketch = "";
	public static String test_image = "";

	public static void main(String[] args) throws Exception{

		title = args[0];
		
		test_sketch = "./tests/" + args[0] + ".jpg";
		test_image = "./tests/blooprints/" + args[1] + ".jpg";

		sketchFile = sketchLoc + title + ".jpg";

		blooprintFile = blooprintLoc + args[1] + ".jpg";

		inMode = args[2];

		markerHex = Integer.parseInt(args[3],16);

		if(args[1] != "null") blooprint = loadBlooprint();
		sketch = loadSketch();

	    switch(inMode){

			case "write":

//				loadCalibration();
				calibrate();
				
				

//
//				write();
//				saveBlooprint();
				break;
//
//			case "erase":
//				/**
//				*	purpose: save updated blooprint image to DB
//				**/
//				loadCalibration();
//
//				areaOfInterest = getLightBorder();
//
//				/*
//				 * start flooding right below center of topSlope
//				 * */
//				tx = (Stretch.ax+Stretch.cx)/2;
//				ty = (Stretch.ay+Stretch.cy)/2;
//
//				try {
//					areaOfInterest = Area.floodBorder(null, areaOfInterest, tx, ty+5);
////					printAOI(areaOfInterest, "fill");
//				}
//				catch(Exception e) {
//					System.out.println("ERROR floodBorder():" + e.getMessage());
//				}
//				
//				Color pxColor = null;
//				
//				for (int row = 0; row < sketch.getHeight(); row++){
//					for(int col = 0; col < sketch.getWidth(); col++){
//						
//						pxColor = new Color(sketch.getRGB(col,row));
//						
//						if(areaOfInterest[row][col] && isMarker(pxColor) && !totalErase[row][col]){
//							
//							eraseAreas.add(new Area(col,row));
//							
//						}
//						
//					}
//				}
//				
//				for (Area some : eraseAreas) {	
//					erase(some.area);
//				}
//				
//				saveBlooprint();
//				break;


			default:
				return;
		}


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

	public static void printAOI(boolean[][] isHit, String action) throws IOException {
		/*
		 * TODO: this method could come in handy for a gui to learn the blooprint system and all its components
		 * this method exists to display that we're examining the correct AREA OF INTEREST in sketch image	 *
		 * */
		
		InputStream stream = Bloop.class.getClass().getResourceAsStream("/sketches/calibrate.jpg");
		BufferedImage ghostBorder = null;
		if(stream == null) {
//			ghostBorder = ImageIO.read(new File());
		}
		else {
			ghostBorder = ImageIO.read(stream);
		}

		try{

//			BufferedImage ghostBorder = ImageIO.read(Bloop.class.getClass().getResourceAsStream("/sketches/calibrate.jpg"));

			for (int row = 0; row < ghostBorder.getHeight(); row ++){
				for (int col = 0; col < ghostBorder.getWidth(); col++){

					if(isHit[row][col]){
						ghostBorder.setRGB(col, row, 0x000000);//turn black
					}

				}
			}

			if(action == "fill"){
//				ImageIO.write(ghostBorder, "jpg", new File("/blooprints/areaOfInterest_filled.jpg"));

				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ImageIO.write(ghostBorder, "jpg", baos);
				InputStream stream2 = new ByteArrayInputStream(baos.toByteArray());
				File outputfile = new File("./tests/areaOfInterest_filled.jpg");
				FileUtils.copyInputStreamToFile(stream2, outputfile);
			}
			else if (action == "border"){
//				ImageIO.write(ghostBorder, "jpg", new File("/blooprints/areaOfInterest.jpg"));

				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ImageIO.write(ghostBorder, "jpg", baos);
				InputStream stream2 = new ByteArrayInputStream(baos.toByteArray());
				File outputfile = new File("./tests/areaOfInterest.jpg");
				FileUtils.copyInputStreamToFile(stream2, outputfile);

			}


		}
		catch(Exception e){
			System.out.println(e);
		}

	}//END printAOI()

	public static void getClientUnitClicks() throws Exception{
		/*
		 * get client side selected points just outside lit corners
		 * */
		
		JSONParser parser = new JSONParser();
		InputStream stream = null;
		JSONObject unitObject = null;


		try {

			stream = Bloop.class.getClass().getResourceAsStream(unitClicksFile);
			unitObject = (JSONObject)parser.parse(new InputStreamReader(stream, "UTF-8"));
			System.out.println("unitClicks stream = "+ stream );

			unit_aax = (Double) unitObject.get("unit_ulx");
			unit_aay = (Double) unitObject.get("unit_uly");
			unit_bbx = (Double) unitObject.get("unit_urx");
			unit_bby = (Double) unitObject.get("unit_ury");
			unit_ccx = (Double) unitObject.get("unit_llx");
			unit_ccy = (Double) unitObject.get("unit_lly");
			unit_ddx = (Double) unitObject.get("unit_lrx");
			unit_ddy = (Double) unitObject.get("unit_lry");


        } catch (Exception e) {
        	System.out.println("ERROR getUnitClientClick(): " + e.getMessage());
        }

	}//END getClientUnitClicks()

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
		Area.printImgBool(areaOfInterest, "AOI");
		
		/*
		 * start flooding right below center of topSlope
		 * */
		tx = (Stretch.ax+Stretch.bx)/2;
		ty = (Stretch.ay+Stretch.by)/2+5;
		areaOfInterest = Area.floodBorder(null, areaOfInterest, tx, ty);

		Area.printImgBool(areaOfInterest, "filled");
		
		setCenters();

	}//END calibrate()

	public static void loadCalibration() throws Exception{
		/*
		 *	calibration data is to be used every write/erase
		 */

		JSONParser parser = new JSONParser();
		JSONObject obj = null;
		InputStream stream = null;

        try {

        	stream = Bloop.class.getClass().getResourceAsStream(calibrationFile);
			obj = (JSONObject)parser.parse(new InputStreamReader(stream, "UTF-8"));
			System.out.println("load calibration stream = "+ stream );

			Stretch.ax = (int)(long) obj.get("ax");
			Stretch.ay = (int)(long) obj.get("ay");
			Stretch.bx = (int)(long) obj.get("bx");
			Stretch.by = (int)(long) obj.get("by");
			Stretch.cx = (int)(long) obj.get("cx");
			Stretch.cy = (int)(long) obj.get("cy");
			Stretch.dx = (int)(long) obj.get("dx");
			Stretch.dy = (int)(long) obj.get("dy");
			Stretch.fx = (int)(long) obj.get("fx");
			Stretch.fy = (int)(long) obj.get("fy");
			Stretch.gx = (int)(long) obj.get("gx");
			Stretch.hy = (int)(long) obj.get("hy");
			aax = (int)(long) obj.get("aax");
			aay = (int)(long) obj.get("aay");
			bbx = (int)(long) obj.get("bbx");
			bby = (int)(long) obj.get("bby");
			ccx = (int)(long) obj.get("ccx");
			ccy = (int)(long) obj.get("ccy");
			ddx = (int)(long) obj.get("ddx");
			ddy = (int)(long) obj.get("ddy");
			Stretch.mA = (Double) obj.get("mA");
			Stretch.mB = (Double) obj.get("mB");
			Stretch.mC = (Double) obj.get("mC");
			Stretch.mD = (Double) obj.get("mD");
			Stretch.xCenterIN = (Double) obj.get("xCenterIN");
			Stretch.yCenterIN = (Double) obj.get("yCenterIN");
			Stretch.xCenterOUT = (Double) obj.get("xCenterOUT");
			Stretch.yCenterOUT = (Double) obj.get("yCenterOUT");
			unit_aax = (Double) obj.get("unit_aax");
			unit_aay = (Double) obj.get("unit_aay");
			unit_bbx = (Double) obj.get("unit_bbx");
			unit_bby = (Double) obj.get("unit_bby");
			unit_ccx = (Double) obj.get("unit_ccx");
			unit_ccy = (Double) obj.get("unit_ccy");
			unit_ddx = (Double) obj.get("unit_ddx");
			unit_ddy = (Double) obj.get("unit_ddy");

        } catch (Exception e) {
            System.out.println("ERROR loadCalibration(): " + e.getMessage());
        }

		topSlope 	= ((double)Stretch.cy-(double)Stretch.ay)/((double)Stretch.cx-(double)Stretch.ax);
		bottomSlope = ((double)Stretch.dy-(double)Stretch.by)/((double)Stretch.dx-(double)Stretch.bx);
		leftSlope 	= ((double)Stretch.ay-(double)Stretch.dy)/((double)Stretch.ax-(double)Stretch.dx);
		rightSlope 	= ((double)Stretch.cy-(double)Stretch.by)/((double)Stretch.cx-(double)Stretch.bx);

	}//END loadCalibration()

	public static void saveCalibration() throws Exception {
		/**
		 * User convenience calibration information saved for load next program start
		 * */

		System.out.println("saveCalibration()...");
		JSONObject obj = new JSONObject();
//		FileWriter file = null;

		try{

			obj.put("ax", Stretch.ax);
			obj.put("ay", Stretch.ay);
			obj.put("bx", Stretch.bx);
			obj.put("cx", Stretch.cx);
			obj.put("by", Stretch.by);
			obj.put("cy", Stretch.cy);
			obj.put("dx", Stretch.dx);
			obj.put("dy", Stretch.dy);
			obj.put("fx", Stretch.fx);
			obj.put("fy", Stretch.fy);
			obj.put("gx", Stretch.gx);
			obj.put("hy", Stretch.hy);
			obj.put("aax", aax);
			obj.put("aay", aay);
			obj.put("bbx", bbx);
			obj.put("bby", bby);
			obj.put("ccx", ccx);
			obj.put("ccy", ccy);
			obj.put("ddx", ddx);
			obj.put("ddy", ddy);
			obj.put("unit_aax", unit_aax);
			obj.put("unit_aay", unit_aay);
			obj.put("unit_bbx", unit_bbx);
			obj.put("unit_bby", unit_bby);
			obj.put("unit_ccx", unit_ccx);
			obj.put("unit_ccy", unit_ccy);
			obj.put("unit_ddx", unit_ddx);
			obj.put("unit_ddy", unit_ddy);
			obj.put("mA", Stretch.mA);
			obj.put("mB", Stretch.mB);
			obj.put("mC", Stretch.mC);
			obj.put("mD", Stretch.mD);
			obj.put("xCenterIN", Stretch.xCenterIN);
			obj.put("yCenterIN", Stretch.yCenterIN);
			obj.put("xCenterOUT", Stretch.xCenterOUT);
			obj.put("yCenterOUT", Stretch.yCenterOUT);

		}catch(Exception ex){
			System.out.println("ERROR assembling calibration JSON Object: " + ex.getMessage());
		}

		try {
			System.out.println("writing calibration to file");
			FileUtils.writeStringToFile(new File("./api/calibration/calibration.json"),obj.toJSONString(),"UTF-8");
		}
		catch(Exception e){
			System.out.println("ERROR saveCalibration() - writing JSON to file: " + e.getMessage());
		}

	}//END saveCalibration()

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

	public static void setCenters() throws Exception{
		/**
		 * Find center of the input image.
		 * Only considering location of light projector lit area on whiteboard.
		 * */
		Stretch.mA = (double)(Stretch.by - Stretch.ay) / (double)(Stretch.bx - Stretch.ax);
		Stretch.mB = (double)(Stretch.cy - Stretch.dy) / (double)(Stretch.cx - Stretch.dx);
		Stretch.mC = (double)(Stretch.fy - Stretch.ey) / (double)(Stretch.fx - Stretch.ex);
		Stretch.mD = (double)(Stretch.gy - Stretch.hy) / (double)(Stretch.gx - Stretch.hx);
		Stretch.xCenterIN = (double)(Stretch.dy - Stretch.ay - (Stretch.dx * Stretch.mB) + (Stretch.ax * Stretch.mA)) / (double)(Stretch.mA - Stretch.mB);
		Stretch.yCenterIN = (double)(Stretch.mA * (Stretch.xCenterIN - Stretch.ax)) + (double)Stretch.ay;
		Stretch.xCenterOUT = (double)(Stretch.hy - Stretch.ey + (Stretch.ex * Stretch.mC) - (Stretch.hx * Stretch.mD)) / (double)(Stretch.mC - Stretch.mD);
		Stretch.yCenterOUT = (double)(Stretch.mC * (Stretch.xCenterOUT - Stretch.ex)) + (double)Stretch.ey;
	}//END setCenters()

	public static BufferedImage loadBlooprint() throws IOException {
		/**
		 * load image from DB table - either an input sketch or a compiled blooprint image
		 *	sketch arg = "null"
		 * BLOB object to binary stream to BufferedImage object
		 * */
		System.out.println("loadBlooprint() from " + blooprintFile);
		InputStream stream = null;
		BufferedImage some = null;
		try{
			/**
			 * http://stackoverflow.com/questions/39081215/access-a-resource-outside-a-jar-from-the-jar
			 * */
//			stream = Bloop.class.getClass().getResourceAsStream(blooprintFile);
			if ( stream == null ) { 
				some = ImageIO.read(new File(test_image));
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
//			stream = Bloop.class.getClass().getResourceAsStream(sketchFile);
			if ( stream == null ) { 
				some = ImageIO.read(new File(test_sketch));
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
		/**
		 * DB table is updated with added image of latest blooprint image state
		 * */

		System.out.println("saveBlooprint()...");

		try{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(blooprint, "jpg", baos);
			InputStream stream = new ByteArrayInputStream(baos.toByteArray());
			File outputfile = new File("./api/blooprints/"+title+".jpg");
			FileUtils.copyInputStreamToFile(stream, outputfile);
//		    ImageIO.write(blooprint, "jpg", outputfile);

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
		/**
		 * checking if instantaneous pixel is ANY color or not
		 * returns true or false
		 *
		 * TODO: for now the only color use case is black.  need to incorporate red, green, and blue user options
		 * */
		if((x.getRed() > mark & x.getGreen() < 100 & x.getBlue() < 100)
				|| (x.getRed() < 100 & x.getGreen() > mark & x.getBlue() < 100)
				|| (x.getRed() < 100 & x.getGreen() < 100 & x.getBlue() > mark)
				|| (x.getRed() < 100 & x.getGreen() < 100 & x.getBlue() < 100)){

			return true;
		}
		return false;
	}//END isMarker()

}
