/**
*   BLOOPRINT.XYZ: we are think tank
*   Copyright (C) 2016 - Dave Daggett - Blooprint, LLC
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
*	Blooprint command line API:
*
*	javac Blooprint.java
*	java Blooprint [user email] [blooprint title] [calibrate/bloop/erase/blip]
*	
*	purpose: input image from DB -> returns processed image to DB for display
*
*	RULES:
*	-must make blooprint image, sketch image, and client browser the same aspect ratio - for now
*	
*/

package xyz.blooprint;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.Queue;

import javax.imageio.ImageIO;




public class Blooprint{

	public static String title = "";	//	blooprint DB table name
	public static String inMode = "";	//	bloop/erase/blip/calibrate
	public static BufferedImage sketch;
	public static BufferedImage blooprint;

	public static double topSlope,bottomSlope,leftSlope,rightSlope;


	public static int aax,aay,bbx,bby,ccx,ccy,ddx,ddy;
	public static int ax,ay,bx,by,cx,cy,dx,dy,fx,fy,gx,hy;
	public static double ex, ey, gy, hx = 0;

	public static double mA, mB, mC, mD, yCenterIN, xCenterIN, xCenterOUT, yCenterOUT, xOUT_temp, 
						yOUT_temp, lxA, lxB, lyA, lyB, kxA, kxB, kyA, kyB, jx, jy, ix, iy, lx, ly, 
						kx, ky, A, B, C, lA, lB, lC, lD, lE, lF, lG, lH;
	
	/*first border hit*/
	public static int borderStart_X,borderStart_Y;
	
	
	public static double unit_aax,unit_aay,unit_bbx,unit_bby,unit_ccx,unit_ccy,unit_ddx,unit_ddy;
	
	public static double clientWidth, clientHeight;
	
	public static boolean[][] areaOfInterest;
	public static boolean[][] sketchDrawnArea;
	
	public static int tx, ty;
	
	/*
	 * MARK = fixed RGB value for input drawing recognition
	 * SEE -> isMarker()
	 * */
	public static int mark = 150;
	
	public static void main(String[] args) throws Exception{
		
		title = args[0];
		inMode = args[1];
		
		blooprint = loadImage(title);
		sketch = loadImage(null);
		
		
	    switch(inMode){

			case "calibrate":
				/*
				 * WEB DEV NOTE:
				 * 
				 * user must have option to calibrate at any time.
				 * 
				 * PROCESS
				 * Image is captured, image is immediately displayed to client full screen.
				 * Image will contain image of surface behind whiteboard - this is the raw
				 * camera capture which includes area beyond what is a writable surface to the user.
				 * The user then clicks at points just beyond the corners shown in the image.
				 * The user points must be ON the whiteboard shown in the image.
				 * USER REQUIREMENT:
				 * Each point should be able to make a line draw to the
				 * adjacent 2 points that is undisturbed by the writable area. 
				 * ie - the diagonal is not an undisturbed line because
				 * you have to pass through the writable area
				 * */
				
				getClientUnitClicks();
			    calibrate();
			    break;
			
			case "bloop":
				/**
				*	purpose: save updated blooprint image to DB
				**/
				loadCalibration();
				areaOfInterest = getLightBorder();
				
				/*
				 * start flooding right below center of topSlope
				 * */
				tx = (ax+cx)/2;
				ty = (ay+cy)/2;
				areaOfInterest = floodBorder(areaOfInterest, tx, ty+5);
				
				bloop();
				saveBlooprint();
				break;
			
			case "erase":
				/**
				*	purpose: save updated blooprint image to DB
				**/
				loadCalibration();
				
				areaOfInterest = getLightBorder();
				
				/*
				 * start flooding right below center of topSlope
				 * */
				tx = (ax+cx)/2;
				ty = (ay+cy)/2;
				areaOfInterest = floodBorder(areaOfInterest, tx, ty+5);
				
				sketchDrawnArea = getSketchDrawnArea();
				erase(sketchDrawnArea);
				saveBlooprint();
				break;
			
			case "blip":
				/*
				*	purpose: save textbox location unit values to DB -> x,y,width,height
				*	does NOT save updated blooprint image to DB -> only new BLIP location info
				*
				*	WEB DEV NOTE:
				*	new blip location can just as easily be created in a user
				*	click-and-drag box area type entry in DOM elements
				*	The Blip action is to give the option of entirely
				*	eliminating the need for a mouse entirely.
				**/
				loadCalibration();
				
				areaOfInterest = getLightBorder();
				
				/*
				 * start flooding right below center of topSlope
				 * */
				tx = (ax+cx)/2;
				ty = (ay+cy)/2;
				areaOfInterest = floodBorder(areaOfInterest, tx, ty+5);
				
				/**
	    		 * box drawn by user on whiteboard dictating exactly where they want new BLIP text to be located on BLOOPRINT
	    		 * */
	    		int[] scanBox = new int[4];
	    		scanBox = zoomToBox();
	    		int[] userIntendedCorners = getScanBoxCorners(scanBox[0], scanBox[1], scanBox[2], scanBox[3]);

				/*
				save BLIP unitBox to DB
				*/
	    		float[] unitBox = setUnitTextbox(userIntendedCorners);
				setBlip(unitBox);
				
				break;
			
		}

		
	}//END main()
	
	/**
	 * STRETCH() method: input pixel location -> output pixel location
	 *
	 *	This method is the core of Blooprint.xyz its input-output mechanism should remain as-is.
	 *	Please see derivation approach in project description.
	 * */
	public static int[] stretch(int x, int y) {
		
		
		int[] some = new int[2];
		
		jx = ((double)y - ((double)x * mB) + (xCenterIN * mA) - yCenterIN) / (mA - mB);
        jy = (mA * (jx - xCenterIN)) + yCenterIN;
        ix = ((double)y - ((double)x * mA) + (xCenterIN * mB) - yCenterIN) / (mB - mA);
        iy = (mB * (ix - xCenterIN)) + yCenterIN;
        
//        System.out.println("============================STRETCH==================================");
//        System.out.println("jx = "+jx+"\tjy = "+jy);
//        System.out.println("ix = "+ix+"\tiy = "+iy);

        
        if (jy >= yCenterIN)
        {
            lA = Math.sqrt((Math.pow(jx - xCenterIN, 2)) + (Math.pow(jy - yCenterIN, 2)));
            lB = Math.sqrt((Math.pow(bx - xCenterIN, 2)) + (Math.pow(by - yCenterIN, 2)));
            lF = Math.sqrt((Math.pow(fx - xCenterOUT, 2)) + (Math.pow(fy - yCenterOUT, 2)));
            
            lE = lA * lF / lB;
            
//            System.out.println("lA = "+lA+"\tlB = "+lB+"\tlF = "+lF+"\tlE = "+lE);
            
            A = 1 + Math.pow(mC, 2);
            B = (-2 * xCenterOUT) - (2 * fx * Math.pow(mC, 2)) + (2 * fy * mC) - (2 * yCenterOUT * mC);
            C = Math.pow(xCenterOUT, 2) + Math.pow(fx * mC, 2) - (2 * fx * fy * mC) + Math.pow(fy, 2) + (2 * yCenterOUT * fx * mC) - (2 * yCenterOUT * fy) + Math.pow(yCenterOUT, 2) - Math.pow(lE, 2);

            lxA = (-B + Math.sqrt(Math.pow(B, 2) - (4 * A * C))) / (2 * A);
            lxB = (-B - Math.sqrt(Math.pow(B, 2) - (4 * A * C))) / (2 * A);

            lyA = (mC * (lxA - fx)) + fy;
            lyB = (mC * (lxB - fx)) + fy;

            if (lyA >= yCenterOUT)
            {
                lx = lxA;
                ly = lyA;
            }
            else
            {
                lx = lxB;
                ly = lyB;
            }
        }
        else
        {
            lA = Math.sqrt((Math.pow(jx - xCenterIN, 2)) + (Math.pow(jy - yCenterIN, 2)));
            lB = Math.sqrt((Math.pow(ax - xCenterIN, 2)) + (Math.pow(ay - yCenterIN, 2)));
            lF = Math.sqrt((Math.pow(ex - xCenterOUT, 2)) + (Math.pow(ey - yCenterOUT, 2)));
            
            lE = lA * lF / lB;
            
//            System.out.println("lA = "+lA+"\tlB = "+lB+"\tlF = "+lF+"\tlE = "+lE);
            
            
            A = 1 + Math.pow(mC, 2);
            B = (-2 * xCenterOUT) - (2 * ex * Math.pow(mC, 2)) + (2 * ey * mC) - (2 * yCenterOUT * mC);
            C = Math.pow(xCenterOUT, 2) + Math.pow(ex * mC, 2) - (2 * ex * ey * mC) + Math.pow(ey, 2) + (2 * yCenterOUT * ex * mC) - (2 * yCenterOUT * ey) + Math.pow(yCenterOUT, 2) - Math.pow(lE, 2);

            lxA = (-B + Math.sqrt(Math.pow(B, 2) - (4 * A * C))) / (2 * A);
            lxB = (-B - Math.sqrt(Math.pow(B, 2) - (4 * A * C))) / (2 * A);

            lyA = (mC * (lxA - ex)) + ey;
            lyB = (mC * (lxB - ex)) + ey;

            if (lyA < yCenterOUT)
            {
                lx = lxA;
                ly = lyA;
            }
            else
            {
                lx = lxB;
                ly = lyB;
            }
        }

        if (iy >= yCenterIN)
        {
            lC = Math.sqrt((Math.pow(ix - xCenterIN, 2)) + (Math.pow(iy - yCenterIN, 2))); 
            lD = Math.sqrt((Math.pow(dx - xCenterIN, 2)) + (Math.pow(dy - yCenterIN, 2)));
            lH = Math.sqrt((Math.pow(hx - xCenterOUT, 2)) + (Math.pow(hy - yCenterOUT, 2)));
            
            lG = lC * lH / lD;
            
//            System.out.println("lC = "+lC+"\tlD = "+lD+"\tlH = "+lH+"\tlG = "+lG);
            
            
            A = 1 + Math.pow(mD, 2);
            B = (-2 * xCenterOUT) - (2 * hx * Math.pow(mD, 2)) + (2 * hy * mD) - (2 * yCenterOUT * mD);
            C = Math.pow(xCenterOUT, 2) + Math.pow(hx * mD, 2) - (2 * hx * hy * mD) + Math.pow(hy, 2) + (2 * yCenterOUT * hx * mD) - (2 * yCenterOUT * hy) + Math.pow(yCenterOUT, 2) - Math.pow(lG, 2);

            kxA = (-B + Math.sqrt(Math.pow(B, 2) - (4 * A * C))) / (2 * A);
            kxB = (-B - Math.sqrt(Math.pow(B, 2) - (4 * A * C))) / (2 * A);

            kyA = (mD * (kxA - hx)) + hy;
            kyB = (mD * (kxB - hx)) + hy;

            if (kyA >= yCenterOUT)
            {
                kx = kxA;
                ky = kyA;
            }
            else
            {
                kx = kxB;
                ky = kyB;
            }
        }
        else
        {
            lC = Math.sqrt((Math.pow(ix - xCenterIN, 2)) + (Math.pow(iy - yCenterIN, 2)));
            lD = Math.sqrt((Math.pow(cx - xCenterIN, 2)) + (Math.pow(cy - yCenterIN, 2)));
            lH = Math.sqrt((Math.pow(gx - xCenterOUT, 2)) + (Math.pow(gy - yCenterOUT, 2)));
            
            lG = lC * lH / lD;
            
//            System.out.println("lC = "+lC+"\tlD = "+lD+"\tlH = "+lH+"\tlG = "+lG);
            
            
            A = 1 + Math.pow(mD, 2);
            B = (-2 * xCenterOUT) - (2 * gx * Math.pow(mD, 2)) + (2 * gy * mD) - (2 * yCenterOUT * mD);
            C = Math.pow(xCenterOUT, 2) + Math.pow(gx * mD, 2) - (2 * gx * gy * mD) + Math.pow(gy, 2) + (2 * yCenterOUT * gx * mD) - (2 * yCenterOUT * gy) + Math.pow(yCenterOUT, 2) - Math.pow(lG, 2);

//            System.out.println("lC = "+lC+"\tlD = "+lD+"\tlH = "+lH+"\tlG = "+lG);
            
            
            kxA = (-B + Math.sqrt(Math.pow(B, 2) - (4 * A * C))) / (2 * A);
            kxB = (-B - Math.sqrt(Math.pow(B, 2) - (4 * A * C))) / (2 * A);

            kyA = (mD * (kxA - gx)) + gy;
            kyB = (mD * (kxB - gx)) + gy;

            if (kyA < yCenterOUT)
            {
                kx = kxA;
                ky = kyA;
            }
            else
            {
                kx = kxB;
                ky = kyB;
            }
        }
        
        
        xOUT_temp = (ky - ly + (lx * mD) - (kx * mC)) / (mD - mC);
        yOUT_temp = (mD * (xOUT_temp - lx)) + ly;

        
        some[0] = (int) Math.round(xOUT_temp);
        some[1] = (int) Math.round(yOUT_temp);
        
        
		return some;
	}//END stretch()

	/**
	 * bloop blooprint.image pixel location intended by user bloop action
	 * sets Color.RED,BLUE,GREEN according to user intension
	 * */
	public static void bloop() {
		
		System.out.println("blooping......");


		int[] xyOUT = new int[2];	
		
		for (int row = 0; row < sketch.getHeight(); row++){
			for(int col = 0; col < sketch.getWidth(); col++){
				
				int xIN = col;
				int yIN = row;
				
				Color pxColor = new Color(sketch.getRGB(xIN,yIN));
				
				try{
					if(areaOfInterest[yIN][xIN]){
						
						if (isMarker(pxColor)){
							
							System.out.println("xIN = "+xIN+"\tyIN = "+yIN);
							System.out.println("red = "+pxColor.getRed()+"\tgreen = "+pxColor.getGreen()+"\tblue = "+pxColor.getBlue());
							xyOUT = stretch(xIN, yIN);
							blooprint.setRGB(xyOUT[0], xyOUT[1], 0x000000);
						}
						
						
					}
				}
				catch(Exception e){
					e.printStackTrace();
				}
				
				
			}
		}
	}//END bloop()
	
	/**
	 * erase the area found inside the outer border of marker line drawn
	 * */
	public static void erase(boolean[][] eraseArea) {

		
		System.out.println("erasing......");


		int[] xyOUT = new int[2];	
		
		for (int row = 0; row < sketch.getHeight(); row++){
			for(int col = 0; col < sketch.getWidth(); col++){
				
				int xIN = col;
				int yIN = row;
				
				try{
					if(eraseArea[yIN][xIN]){
						
						xyOUT = stretch(xIN, yIN);
						blooprint.setRGB(xyOUT[0], xyOUT[1], 0xffffff);
						
						
					}
				}
				catch(Exception e){
					e.printStackTrace();
				}
				
				
			}
		}

	}//END erase()


	
	/*
	 * TODO: this method could come in handy for a gui to learn the blooprint system and all its components
	 * this method exists to display that we're examining the correct AREA OF INTEREST in sketch image	 * 
	 * */
	public static void printAOI(boolean[][] isHit, String action) throws IOException {
		
		
		try{
		
			BufferedImage ghostBorder = ImageIO.read(new File("some directory/calibrate.jpg"));
			
			for (int row = 0; row < ghostBorder.getHeight(); row ++){
				for (int col = 0; col < ghostBorder.getWidth(); col++){
					
					if(isHit[row][col]){
						ghostBorder.setRGB(col, row, 0x000000);//turn black
					}
					
				}
			}
			
			if(action == "fill"){
				ImageIO.write(ghostBorder, "jpg", new File("some directory/areaOfInterest_filled.jpg"));
			}
			else{
				ImageIO.write(ghostBorder, "jpg", new File("some directory/areaOfInterest.jpg"));
			}
			
			
		}
		catch(Exception e){
			System.out.println(e);
		}
		
	}//END printAOI()
			
		    

	/*
	 * get client side selected points just outside lit corners
	 * */
	public static void getClientUnitClicks(){
		
		/*
		 * double[] array 
		 * */
		int[] some = new int[8];
		
		try{
			System.out.println("getting client clicks.......");
			
			
			Connection connx = getDataBaseConnection();
			Statement statement = (Statement) connx.createStatement();
			String cmd = "SELECT * FROM _clientclicks ORDER BY id DESC LIMIT 1";
			ResultSet result = statement.executeQuery(cmd);
			while(result.next()){
				
				some[0] = result.getInt("ulx");
				some[1] = result.getInt("uly");
				some[2] = result.getInt("urx");
				some[3] = result.getInt("ury");
				some[4] = result.getInt("llx");
				some[5] = result.getInt("lly");
				some[6] = result.getInt("lrx");
				some[7] = result.getInt("lry");
				clientWidth = result.getInt("width");
				clientHeight = result.getInt("height");
				
			}
		}
		catch(Exception e){
			System.out.println("\nERROR loadCalibration\n"+e+"\n");
		}
		
		unit_aax = (double)some[0]/(double)clientWidth;
		unit_aay = (double)some[1]/(double)clientHeight;
		unit_bbx = (double)some[2]/(double)clientWidth;
		unit_bby = (double)some[3]/(double)clientHeight;
		unit_ccx = (double)some[4]/(double)clientWidth;
		unit_ccy = (double)some[5]/(double)clientHeight;
		unit_ddx = (double)some[6]/(double)clientWidth;
		unit_ddy = (double)some[7]/(double)clientHeight;
		

	}//END getClientUnitClicks()

	
	/*
	creates a scan area around the box drawn by user in sketch
	returns xMIN, xMAX, yMIN, yMAX in sketch
	*/
	public static int[] zoomToBox() {
	
		/**
		 * these value's starting points are backwards in order for the boolean comparisons below to initiate properly
		 * */
		int[] some = new int[4];
		int xmax = 0;
		int xmin = sketch.getWidth();
		int ymax = 0;
		int ymin = sketch.getHeight();
		
		here:

			for(int row = 0; row < sketch.getHeight(); row++){
				for(int col = 0; col < sketch.getWidth(); col++){
					
					
					int xIN = col;
					int yIN = row;
					
					/*
					 * dealing with pixels input by user - sketch
					 * */
					Color pixel = new Color(sketch.getRGB(xIN,yIN));
					
		            
		            if(areaOfInterest[yIN][xIN]){
		            	if(isMarker(pixel)){

			            	System.out.println("xIN = " + xIN);
			            	System.out.println("yIN = " + yIN);
							
							System.out.println("\nfound user-drawn border!!!\n");
							
							/*
							 * encapsulate eraser area
							 * */
							int[] inCoord = new int[2];
							inCoord[0] = xIN;
							inCoord[1] = yIN;
							boolean flag = true;
							while(flag){
								
								
								//	2dArray[y][x]
//								some[inCoord[1]][inCoord[0]] = true;
								
								inCoord = getNextBorderPixel(inCoord);
								/*
								 * set square boundaries of user input area
								 * */
								if(inCoord[0] > xmax){
									xmax = inCoord[0];
								}
								if(inCoord[0] < xmin){
									xmin = inCoord[0];
								}
								if(inCoord[1] > ymax){
									ymax = inCoord[1];
								}
								if(inCoord[1] < ymin){
									ymin = inCoord[1];
								}
								
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
				}
			}//END outer loop
	
	
		some[0] = ymin-2;//could be 1 - lol
		some[1] = ymax+2;
		some[2] = xmin-2;
		some[3] = xmax+2;
		
		return some;
	}//END zoomToBox()
	
	/**
	 * finds user defined projector corners (xy-coordinates) on whiteboard
	 * */
	public static int[] getScanBoxCorners(int ymin, int ymax, int xmin, int xmax) {
		/*
		 * action occurs on the input camera image
		 * 
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
	
	/*
	returns Rectangle to  -> x,y,width,height
	*/
	public static float[] setUnitTextbox(int[] corners) {
		
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
	
	
	/*
	BLIP is the generation of user drawn area to be workspace of new qwerty keyboard text input.
	Text input is handled by web application.  Blooprint API handles the user decision to draw
	exactly where they want to start typing.
	ALTERNATIVE OPTION is to use a mouse click and drag action by client.
	*/
	public static void setBlip(float box[]) throws Exception{
		
		Connection connx = getDataBaseConnection();
		
		try{
			/*	TODO:
			 * 
			 * if x AND y equal any of the table rows, update THOSE rows
			 * else create new row
			 * 
			 * */
			
			/*
			MySQL table DOES have additional columns:
				-textEntry
				-username
				-etc
			*/
			String cmd = "INSERT INTO "+title+"_blips (x,y,width,height) "
					+"VALUES ("+box[0]+","+box[1]+","+box[2]+","+box[3]+") ON DUPLICATE KEY UPDATE "
					+"x = VALUES(x),"
					+"y = VALUES(y),"
					+"width = VALUES(width),"
					+"height = VALUES(height)";
					
			PreparedStatement statement = (PreparedStatement) connx.prepareStatement(cmd);				
			statement.executeUpdate();
			
		}catch(Exception ex){
			System.out.println("\nERROR:\nsetBlip()"+ex.getMessage());
			ex.printStackTrace();
		}
		
		
		connx.close();
		
	}//END setBlip()
	
	

/*=====================================================================*/	
/*=====================================================================*/	
	
	
	/*
	Sets calibration values to DB
	*/
	public static void calibrate() throws Exception{
		
		
		aax = (int)Math.round(unit_aax * (double)sketch.getWidth());
		aay = (int)Math.round(unit_aay * (double)sketch.getHeight());
		bbx = (int)Math.round(unit_bbx * (double)sketch.getWidth());
		bby = (int)Math.round(unit_bby * (double)sketch.getHeight());
		ccx = (int)Math.round(unit_ccx * (double)sketch.getWidth());
		ccy = (int)Math.round(unit_ccy * (double)sketch.getHeight());
		ddx = (int)Math.round(unit_ddx * (double)sketch.getWidth());
		ddy = (int)Math.round(unit_ddy * (double)sketch.getHeight());
		
		
		/*
		 * if slopes will equal 0 or INFINITY : move one of the pixels off by 1 just to give it some slope
		 * */
		if(bbx == ddx) ddx = ddx + 1;
		if(ccx == aax) aax = aax - 1;
		if(bby == aay) aay = aay - 1;
		if(ccy == ddy) ddy = ddy + 1;
		
		
		
		topSlope = ((double)bby-(double)aay)/((double)bbx-(double)aax);
		bottomSlope = ((double)ddy-(double)ccy)/((double)ddx-(double)ccx);
		leftSlope = ((double)ccy-(double)aay)/((double)ccx-(double)aax);
		rightSlope 	= ((double)ddy-(double)bby)/((double)ddx-(double)bbx);
		
		
		
		/**
		 * calibration object uses boolean[][] where true values represent 
		 * lit projection area on whiteboard
		 * */
		areaOfInterest = getAreaOfInterestBorder();
		
		
		int tx = (bbx+aax)/2;
		int ty = (bby+aay)/2;
		/*
		 * TODO: set flood starting point to just below the center point
		 * of the top line spanning a and b
		 * 
		 * areaOfInterest = floodBorder(areaOfInterest, X, Y);
		 * */
		areaOfInterest = floodBorder(areaOfInterest, tx, ty+5);
		setCorners();
		setCenters();
		saveCalibration();
	}//END calibrate()

	/*
	*	calibration data is to be used every bloop/erase/blip
	*/
	public static void loadCalibration() throws Exception{
		
		try{
			System.out.println("loading calibration.......");
			
			Connection connx = getDataBaseConnection();
			Statement statement = (Statement) connx.createStatement();
			String cmd = "SELECT * FROM _calibration ORDER BY id DESC LIMIT 1";
			ResultSet result = statement.executeQuery(cmd);
			while(result.next()){
				ax = result.getInt("ax");
				ay = result.getInt("ay");
				bx = result.getInt("bx");
				by = result.getInt("by_");
				cx = result.getInt("cx");
				cy = result.getInt("cy");
				dx = result.getInt("dx");
				dy = result.getInt("dy");
				fx = result.getInt("fx");
				fy = result.getInt("fy");
				gx = result.getInt("gx");
				hy = result.getInt("hy");
				aax = result.getInt("aax");
				aay = result.getInt("aay");
				bbx = result.getInt("bbx");
				bby = result.getInt("bby");
				ccx = result.getInt("ccx");
				ccy = result.getInt("ccy");
				ddx = result.getInt("ddx");
				ddy = result.getInt("ddy");
				unit_aax = result.getDouble("unit_aax");
				unit_aay = result.getDouble("unit_aay");
				unit_bbx = result.getDouble("unit_bbx");
				unit_bby = result.getDouble("unit_bby");
				unit_ccx = result.getDouble("unit_ccx");
				unit_ccy = result.getDouble("unit_ccy");
				unit_ddx = result.getDouble("unit_ddx");
				unit_ddy = result.getDouble("unit_ddy");
				mA = result.getDouble("mA");
				mB = result.getDouble("mB");
				mC = result.getDouble("mC");
				mD = result.getDouble("mD");
				xCenterIN = result.getDouble("xCenterIN");
				yCenterIN = result.getDouble("yCenterIN");
				xCenterOUT = result.getDouble("xCenterOUT");
				yCenterOUT = result.getDouble("yCenterOUT");
				
			}
		}
		catch(Exception e){
			System.out.println("\nERROR loadCalibration\n"+e+"\n");
		}
		
		topSlope 	= ((double)cy-(double)ay)/((double)cx-(double)ax);
		bottomSlope = ((double)dy-(double)by)/((double)dx-(double)bx);
		leftSlope 	= ((double)ay-(double)dy)/((double)ax-(double)dx);
		rightSlope 	= ((double)cy-(double)by)/((double)cx-(double)bx);
		
		
	}//END loadCalibration()

	/**
	 * User convenience calibration information saved for load next program start
	 * */
	public static void saveCalibration() throws Exception {
		
		try{
			
			Connection connx = getDataBaseConnection();
			String cmd = "INSERT INTO _calibration(ax,ay,bx,by_,cx,cy,dx,dy,fx,fy,gx,hy,"
					+"aax,aay,bbx,bby,ccx,ccy,ddx,ddy,unit_aax,unit_aay,unit_bbx,unit_bby,"
					+"unit_ccx,unit_ccy,unit_ddx,unit_ddy,mA,mB,mC,mD,xCenterIN,yCenterIN,xCenterOUT,yCenterOUT) "
					+"VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			PreparedStatement statement = (PreparedStatement) connx.prepareStatement(cmd);
			statement.setInt(1, ax);
			statement.setInt(2, ay);
			statement.setInt(3, bx);
			statement.setInt(4, by);
			statement.setInt(5, cx);
			statement.setInt(6, cy);
			statement.setInt(7, dx);
			statement.setInt(8, dy);
			statement.setInt(9, fx);
			statement.setInt(10, fy);
			statement.setInt(11, gx);
			statement.setInt(12, hy);
			statement.setInt(13, aax);
			statement.setInt(14, aay);
			statement.setInt(15, bbx);
			statement.setInt(16, bby);
			statement.setInt(17, ccx);
			statement.setInt(18, ccy);
			statement.setInt(19, ddx);
			statement.setInt(20, ddy);
			statement.setDouble(21, unit_aax);
			statement.setDouble(22, unit_aay);
			statement.setDouble(23, unit_bbx);
			statement.setDouble(24, unit_bby);
			statement.setDouble(25, unit_ccx);
			statement.setDouble(26, unit_ccy);
			statement.setDouble(27, unit_ddx);
			statement.setDouble(28, unit_ddy);
			statement.setDouble(29, mA);
			statement.setDouble(30, mB);
			statement.setDouble(31, mC);
			statement.setDouble(32, mD);
			statement.setDouble(33, xCenterIN);
			statement.setDouble(34, yCenterIN);
			statement.setDouble(35, xCenterOUT);
			statement.setDouble(36, yCenterOUT);
			
			statement.executeUpdate();
			connx.close();
			
		}catch(Exception ex){
			System.out.println(ex);
		}
		
		
	}//END saveCalibration()
	
	/**
	*	sets -> ax,ay,bx,by,cx,cy,dx,dy
	*	gets corner values user draws on whiteboard (corners of lit projection area)
	*
	*	TODO: 
	 * */
	public static void setCorners() {
		
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
                    	ax = col;
                    	ay = row;                        
                        System.out.println("Corner 1:\nULx = "+ax+"\nULy = "+ay);
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
                    	cx = col;
                    	cy = row;
                    	
                    	System.out.println("cx = "+cx+"\tcy = "+cy);
                    	
                        System.out.println("Corner 2:\nURx = "+cx+"\nURy = "+cy);
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
                    	dx = col;
                    	dy = row;
                        System.out.println("Corner 3:\nLLx = "+dx+"\nLLy = "+dy);
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
                    	bx = col;
                    	by = row;
                        System.out.println("Corner 4:\nLRx = "+bx+"\nLRy = "+by);
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
	
	/**
	 * Find center of the input image.
	 * Only considering location of light projector lit area on whiteboard.
	 * */
	public static void setCenters() throws Exception{
	    mA = (double)(by - ay) / (double)(bx - ax);
		mB = (double)(cy - dy) / (double)(cx - dx);
        mC = (double)(fy - ey) / (double)(fx - ex);
        mD = (double)(gy - hy) / (double)(gx - hx);
        xCenterIN = (double)(dy - ay - (dx * mB) + (ax * mA)) / (double)(mA - mB);
        yCenterIN = (double)(mA * (xCenterIN - ax)) + (double)ay;
        xCenterOUT = (double)(hy - ey + (ex * mC) - (hx * mD)) / (double)(mC - mD);
        yCenterOUT = (double)(mC * (xCenterOUT - ex)) + (double)ey;
	}//END setCenters()


	/**
	 * dealing with area drawn by user to erase
	 * sets binary map single pixel strand border for future use in floodBorder() method
	 * */
	public static boolean[][] getUserDrawnBorder() {
		
		boolean[][] border = new boolean[sketch.getHeight()][sketch.getWidth()];
		
		here:
			
			for(int row = 0; row < sketch.getHeight(); row++){
				for(int col = 0; col < sketch.getWidth(); col++){

					/*
					 * dealing with pixels input by user - sketch
					 * */
					Color pxColor = new Color(sketch.getRGB(col,row));
					int xIN = col;
		            int yIN = row;
		            
		            
		            
		            if(areaOfInterest[row][col] && isMarker(pxColor)){
		            	
		            	
		            	
		            	

		            	System.out.println("xIN = "+xIN);
		            	System.out.println("yIN = "+yIN);
						
						System.out.println("\nfound eraser border!!!\n");
						
						/*
						 * encapsulate eraser area
						 * */
						int[] inCoord = new int[2];
						inCoord[0] = col;
						inCoord[1] = row;
						boolean flag = true;
						while(flag){
							
							//	2dArray[y][x]
							border[inCoord[1]][inCoord[0]] = true;
							
							inCoord = getNextBorderPixel(inCoord);
							
							if((inCoord[0] == xIN) && (inCoord[1] == yIN)){
								
								
								borderStart_X = xIN;
								borderStart_Y = yIN;
								
								
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
	
	
		return border;
	}//END getUserDrawnBorder()


	/**
	 * returns binary map. area of interest on whiteboard, just outside of projected corners
	 * */
	public static boolean[][] getSketchDrawnArea() {
		
		
		/**
		 * TODO: need to scan for multiple getSketchDrawnArea areas.  so far we are only checking for
		 * the first one that we come across.
		 * */
		
		boolean[][] area = getUserDrawnBorder();
		
		int xStart = borderStart_X;
		int yStart = borderStart_Y + 2; /*TODO:	must consider the case in which borderStart_Y+2 is not inside border wall*/
		
		area = floodBorder(area, xStart,yStart);
		
		return area;
	}//END getSketchDrawnArea()

	/**
	 * recursive method locating pixel after last found pixel until entire border is lined
	 * */
	public static int[] getNextBorderPixel(int[] coord) {

		int[] next = new int[2];
		
		
		System.out.println("============================\nlastX = "+coord[0]+"\tlastY = "+coord[1]);
		
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
	}//END getNextBorderPixel()

	public static Connection getDataBaseConnection() throws Exception{
		try{

			/**
			* TODO:
			* make sure to download JAR
			* http://dev.mysql.com/downloads/connector/j/
			*/
			Class.forName("com.mysql.jdbc.Driver");
			String url = "jdbc:mysql://127.0.0.1:3306/blooprint";
			String username = "root";
			String password = "password";
			Connection c = DriverManager.getConnection(url,username,password);
			return c;// if connection worked
		}catch(Exception e){
			System.out.println("ERROR:\ngetDataBaseConnection()");
			e.printStackTrace();
		}
		return null; //if connection not made
	}//END getDataBaseConnection()

	/**
	 * load image from DB table - either an input sketch or a compiled blooprint image
	 *	sketch arg = "null"
	 * BLOB object to binary stream to BufferedImage object
	 * */
	public static BufferedImage loadImage(String title) throws Exception {

		BufferedImage some = null;
		
		if (title == null){	//	if sketch
			/**
			*	load image from sketch table in DB (default)
			**/
			
			
			try{
				Connection connx = getDataBaseConnection();
				Statement statement = connx.createStatement();
				
				InputStream is = null;
				
				/**
				*	TODO: create DB table -> sketches (universal table - blooprint.xyz wide)
				*		- will need unique sketch ID to user per work station
				**/
				String cmd = "SELECT image FROM _sketches ORDER BY id DESC LIMIT 1";
				
				ResultSet result = statement.executeQuery(cmd);
				
				if(result.next()){
					is = result.getBinaryStream("image");
					
					some = ImageIO.read(is);
				}
				
				connx.close();
				
				fx = some.getWidth()-1;
				gx = some.getWidth()-1;
			    fy = some.getHeight()-1;
			    hy = some.getHeight()-1;
				
			}catch(Exception exc){
				exc.getMessage();
			}
			
		}
		else{	//	if blooprint

			/**
			*	load last image from particular active blooprint
			*
			 * BLOB object to BufferedImage object
			 * */		
			
			try{
				Connection connx = getDataBaseConnection();
				Statement statement = connx.createStatement();
				
				InputStream is = null;
				
				String cmd = "SELECT blooprint FROM "+title+" ORDER BY id DESC LIMIT 1";
				
				ResultSet result = statement.executeQuery(cmd);
				
				if(result.next()){
					is = result.getBinaryStream("blooprint");
					
					some = ImageIO.read(is);
				}
				
				connx.close();
				
			}catch(Exception exc){
				exc.getMessage();
			}
		}
		
		return some;
	}//END loadImage()

	/**
	 * DB table is updated with added image of latest blooprint image state
	 * */
	public static void saveBlooprint() throws IOException {
		/*
		 * BufferedImage object to BLOB object
		 * */
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(blooprint, "jpg", baos);
		InputStream is = new ByteArrayInputStream(baos.toByteArray());
		
		try{
			
			Connection connx = getDataBaseConnection();
			String cmd = "INSERT INTO "+title+"(blooprint) VALUES (?)";
			PreparedStatement statement = (PreparedStatement) connx.prepareStatement(cmd);
			statement.setBlob(1, is);
			statement.executeUpdate();
			connx.close();
			
		}catch(Exception ex){
			System.out.println(ex.getMessage());
		}
	}//END saveBlooprint()

	/**
	 * dealing with lit projector area on whiteboard
	 * sets binary border for future use in floodBorder() method
	 * */
	public static boolean[][] getLightBorder() {		
		
		boolean[][] border = new boolean[sketch.getHeight()][sketch.getWidth()];
		
		
		for(int x = ax; x <= cx; x++){//top
			
			double intersect_double = cy - (topSlope*cx);
			int intersect = (int) Math.round(intersect_double);
			double y_double = (topSlope * x) + intersect;
			int y = (int) Math.round(y_double);
			border[y][x] = true;
						
		}
		for(int x = dx; x <= bx; x++){//bottom

			double intersect_double = by - (bottomSlope*bx);
			int intersect = (int) Math.round(intersect_double);
			double y_double = (bottomSlope * x) + intersect;
			int y = (int) Math.round(y_double);
			border[y][x] = true;
			
		}
		for(int y = ay; y <= dy; y++){//left
			
			double intersect_double = dy - (leftSlope*dx);
			int intersect = (int) Math.round(intersect_double);
			double x_double = (y-intersect)/leftSlope;
			int x = (int) Math.round(x_double);
			border[y][x] = true;
			
		}
		for(int y = cy; y <= by; y++){//right
			
			double intersect_double = by - (rightSlope*bx);
			int intersect = (int) Math.round(intersect_double);
			double x_double = (y-intersect)/rightSlope;
			int x = (int) Math.round(x_double);
			border[y][x] = true;
			
		}
		
		return border;
	}//END getLightBorder()

	/**
	 * dealing with lit projector area on whiteboard
	 * sets binary border for future use in floodBorder() method
	 * */
	public static boolean[][] getAreaOfInterestBorder() {		
		
		boolean[][] border = new boolean[sketch.getHeight()][sketch.getWidth()];
				
		for(int x = aax; x <= bbx; x++){//top
			
			double intersect_double = bby - (topSlope*bbx);
			int intersect = (int) Math.round(intersect_double);
			double y_double = (topSlope * x) + intersect;
			int y = (int) Math.round(y_double);
			border[y][x] = true;
						
		}
		for(int x = ccx; x <= ddx; x++){//bottom

			double intersect_double = ddy - (bottomSlope*ddx);
			int intersect = (int) Math.round(intersect_double);
			double y_double = (bottomSlope * x) + intersect;
			int y = (int) Math.round(y_double);
			border[y][x] = true;
			
		}
		for(int y = aay; y <= ccy; y++){//left
			
			double intersect_double = ccy - (leftSlope*ccx);
			int intersect = (int) Math.round(intersect_double);
			double x_double = (y-intersect)/leftSlope;
			int x = (int) Math.round(x_double);
			border[y][x] = true;
			
		}
		for(int y = bby; y <= ddy; y++){//right
			
			double intersect_double = ddy - (rightSlope*ddx);
			int intersect = (int) Math.round(intersect_double);
			double x_double = (y-intersect)/rightSlope;
			int x = (int) Math.round(x_double);
			border[y][x] = true;
			
		}
		
		return border;
	}//END getAreaOfInterestBorder()

	/**
	 * paint bucket-like algorithm to fill binary map border
	 * this filled area becomes the area to be filtered through the stretch() 
	 * area pixels to be turned Color.WHITE (unless notified otherwise by user dictation)
	 * 
	 * */
	public static boolean[][] floodBorder(boolean[][] floodArea, int x, int y) {
		
        if (!floodArea[y][x]) {
        	
        	

		    Queue<Point> queue = new LinkedList<Point>();
		    queue.add(new Point(x, y));

		    while (!queue.isEmpty()) {
		        
		    	Point p = queue.remove();

	        	if (!floodArea[p.y][p.x]) {
	        		
	            	floodArea[p.y][p.x] = true;

	                queue.add(new Point(p.x + 1, p.y));
	                queue.add(new Point(p.x - 1, p.y));
	                queue.add(new Point(p.x, p.y + 1));
	                queue.add(new Point(p.x, p.y - 1));
	                
	            }
		    }
		    
		}
        
		return floodArea;
	}//END floodBorder()


	/**
	 * checking if instantaneous pixel is ANY color or not
	 * returns true or false
	 * 
	 * TODO: for now the only color use case is black.  need to incorporate red, green, and blue user options
	 * */
	public static boolean isMarker(Color x) {
		if((x.getRed() > mark & x.getGreen() < 100 & x.getBlue() < 100)
				|| (x.getRed() < 100 & x.getGreen() > mark & x.getBlue() < 100)
				|| (x.getRed() < 100 & x.getGreen() < 100 & x.getBlue() > mark)
				|| (x.getRed() < 100 & x.getGreen() < 100 & x.getBlue() < 100)){
			
			return true;
		}
		return false;
	}//END isMarker()
	
}