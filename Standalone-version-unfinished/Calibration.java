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
import java.awt.image.BufferedImage;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * CALIBRATION object applied to every BLOOP input object.
 * Reset per user request.
 * BLOOP.manipulate() uses Calibration object details for image transformation
 * 
 * TODO:
 * calibration needs to be based on user dictated corners
 * 
 * */
public class Calibration extends BorderSelect{
	
//	public static BufferedImage BLOOP.sketch;
	
	public static int topBorder;
	public static int bottomBorder;
	public static int leftBorder;
	public static int rightBorder;
	
	public static int aax;
	public static int aay;
	public static int bbx;
	public static int bby;
	public static int ccx;
	public static int ccy;
	public static int ddx;
	public static int ddy;

	public static double topSlope,bottomSlope,leftSlope,rightSlope;
	
	public static boolean[][] inActionArea;
	
	
//	public static int tL;//top left corner	
//	public static int tR;//top right corner
//	public static int bL;//bottom left corner
//	public static int bR;//bottom right corner
	
	public static int[] corners;
	
	private static int id;
	public static int ax,ay,bx,by,cx,cy,dx,dy,fx,fy,gx,hy;
	public static double ex, ey, gy, hx = 0;
	public static double mA, mB, mC, mD, yCenterIN, xCenterIN, xCenterOUT, yCenterOUT, xOUT_temp, 
						yOUT_temp, lxA, lxB, lyA, lyB, kxA, kxB, kyA, kyB, jx, jy, ix, iy, lx, ly, 
						kx, ky, A, B, C, lA, lB, lC, lD, lE, lF, lG, lH;
	

	public Calibration () {
//		super();
	}
	
	/**
	 * CALIRATION object contains all information pertaining to image BLOOP transformation
	 * */
	public Calibration(int width, int height) throws Exception {
		
//		super();
		
		fx = BLOOP.sketch.getWidth()-1;
		gx = BLOOP.sketch.getWidth()-1;
	    fy = BLOOP.sketch.getHeight()-1;
	    hy = BLOOP.sketch.getHeight()-1;
	    
	    System.out.println("Calibration object created");
	    

	    /**
	     * RESOLUTION PERSPECTIVE: BLOOP.sketch input image resolution size
	     * these are xy-coords in the BLOOP.sketch resolution
	     * 
	     * setting boundaries which all pixels of interest are located.
	     * done by finding location just outside user drawn corners and using these 4 points as 
	     * outer shell to draw custom border
	     * */
		double aax_ = (double)calibCorners[0] / (double)width * (double)BLOOP.sketch.getWidth();
		double aay_ = (double)calibCorners[1] / (double)height * (double)BLOOP.sketch.getHeight();
		double bbx_ = (double)calibCorners[2] / (double)width  * (double)BLOOP.sketch.getWidth();
		double bby_	= (double)calibCorners[3] / (double)height  * (double)BLOOP.sketch.getHeight();
		double ccx_	= (double)calibCorners[4] / (double)width  * (double)BLOOP.sketch.getWidth();
		double ccy_	= (double)calibCorners[5] / (double)height  * (double)BLOOP.sketch.getHeight();
		double ddx_	= (double)calibCorners[6] / (double)width  * (double)BLOOP.sketch.getWidth();
		double ddy_	= (double)calibCorners[7] / (double)height  * (double)BLOOP.sketch.getHeight();
		/**
		 * RESOLUTION PERSPECTIVE: BLOOP.sketch input image resolution size
		 * these are the integer values of the outer corners
		 * order: TL->TR->BL->BR
		 * */
		aax = (int)Math.round(aax_);
		aay = (int)Math.round(aay_);
		bbx = (int)Math.round(bbx_);
		bby = (int)Math.round(bby_);
		ccx = (int)Math.round(ccx_);
		ccy = (int)Math.round(ccy_);
		ddx = (int)Math.round(ddx_);
		ddy = (int)Math.round(ddy_);
		
		System.out.println("aax = "+aax);
		System.out.println("aay = "+aay);
		System.out.println("bbx = "+bbx);
		System.out.println("bby = "+bby);
		System.out.println("ccx = "+ccx);
		System.out.println("ccy = "+ccy);
		System.out.println("ddx = "+ddx);
		System.out.println("ddy = "+ddy);
		
		
		/**
		 * calibration object uses boolean[][] where true values represent 
		 * lit projection area on whiteboard in the BLOOP.sketch image
		 * */
		inActionArea = getActionBorder();
		inActionArea = BLOOP.floodBorder(inActionArea, aax+1, aay+1);
		setCorners();
		setCenters();
		saveCalibration();

		
		
//		topBorder 		= (int)Math.round(top);
//		bottomBorder 	= (int)Math.round(bot);
//		leftBorder 		= (int)Math.round(lef);
//		rightBorder		= (int)Math.round(rig);
		
//		System.out.println("BLOOP.sketch borders......."
//							+"\n\ttop\t= "+topBorder
//							+"\n\tbottom\t= "+bottomBorder
//							+"\n\tleft\t= "+leftBorder
//							+"\n\tright\t= "+rightBorder);
		/*
		 * BLOOP.sketch corners
		 * */
//		corners = getScanBoxCorners(topBorder,bottomBorder,leftBorder,rightBorder);
//		
//		ax = corners[0];//UL
//		ay = corners[1];
//		cx = corners[2];//UR
//		cy = corners[3];
//		dx = corners[4];//LL
//		dy = corners[5];
//		bx = corners[6];//LR
//		by = corners[7];
		
		
		
		
		System.out.println("through calibration.......");
		
	}//END Calibration() constructor
	
	/**
	 * dealing with lit projector area on whiteboard
	 * sets binary border for future use in BLOOP.floodBorder() method
	 * */
	private boolean[][] getActionBorder() {
		
		
		boolean[][] some = new boolean[BLOOP.sketch.getHeight()][BLOOP.sketch.getWidth()];
		
		/**
		 * BLOOP.sketch should result these slopes as NOT 0 OR INFINITY
		 * 
		 * TODO:
		 * case for when (rare) slopes calculate to these special cases
		 * */
		topSlope 	= ((double)bby-(double)aay)/((double)bbx-(double)aax);
		bottomSlope = ((double)ddy-(double)ccy)/((double)ddx-(double)ccx);
		leftSlope 	= ((double)ccy-(double)aay)/((double)ccx-(double)aax);
		rightSlope 	= ((double)ddy-(double)bby)/((double)ddx-(double)bbx);
		
		System.out.println("topSlope = "+topSlope);
		System.out.println("bottomSlope = "+bottomSlope);
		System.out.println("leftSlope = "+leftSlope);
		System.out.println("rightSlope = "+rightSlope);
		
		
		
		for(int x = aax; x <= bbx; x++){//top
			
			double intersect_double = bby - (topSlope*bbx);
			int intersect = (int) Math.round(intersect_double);
			double y_double = (topSlope * x) + intersect;
			int y = (int) Math.round(y_double);
			some[y][x] = true;
						
		}
		for(int x = ccx; x <= ddx; x++){//bottom

			double intersect_double = ddy - (bottomSlope*ddx);
			int intersect = (int) Math.round(intersect_double);
			double y_double = (bottomSlope * x) + intersect;
			int y = (int) Math.round(y_double);
			some[y][x] = true;
			
		}
		for(int y = aay; y <= ccy; y++){//left
			
			double intersect_double = ccy - (leftSlope*ccx);
			int intersect = (int) Math.round(intersect_double);
			double x_double = (y-intersect)/leftSlope;
			int x = (int) Math.round(x_double);
			some[y][x] = true;
			
		}
		for(int y = bby; y <= ddy; y++){//right
			
			double intersect_double = ddy - (rightSlope*ddx);
			int intersect = (int) Math.round(intersect_double);
			double x_double = (y-intersect)/rightSlope;
			int x = (int) Math.round(x_double);
			some[y][x] = true;
			
		}
		
		
		return some;
	}
	
	/**
	 * finds user defined projector corners (xy-coordinates) on whiteboard
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
                	pixel = new Color(BLOOP.sketch.getRGB(col, row));
                    if (inActionArea[row][col] && BLOOP.isMarker(pixel))
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
                int col = BLOOP.sketch.getWidth()-1;
                row = rowStart;
                while (row > 0)
                {
                	pixel = new Color(BLOOP.sketch.getRGB(col, row));
                    if (inActionArea[row][col] && BLOOP.isMarker(pixel))
                    {
                    	cx = col;
                    	cy = row;
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
        rowStart = BLOOP.sketch.getHeight()-1;
        Next3:
        try
        {
        	while (!hit)
            {
                rowStart--;
                int col = 0;
                row = rowStart;
                while (row < BLOOP.sketch.getHeight()-1)
                {
                	pixel = new Color(BLOOP.sketch.getRGB(col, row));
                    if (inActionArea[row][col] && BLOOP.isMarker(pixel))
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
        rowStart = BLOOP.sketch.getHeight()-1;
        Next4:
        try
        {
            while (!hit)
            {
                rowStart--;
                int col = BLOOP.sketch.getWidth()-1;
                row = rowStart;
                while(row < BLOOP.sketch.getHeight()-1)
                {
                	pixel = new Color(BLOOP.sketch.getRGB(col, row));
                    if (inActionArea[row][col] && BLOOP.isMarker(pixel))
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

	        
		

	}//END getScanBoxCorners()
	
	/**
	 * load Calibration object variables from DB on program start
	 * */
	public static void loadCalibration() throws Exception{
		
		try{
			System.out.println("loading calibration.......");
			
			
			Connection connx = BLOOPRINT.getDataBaseConnection();
			Statement statement = (Statement) connx.createStatement();
			String cmd = "SELECT * FROM _calibration ORDER BY id DESC LIMIT 1";
			ResultSet result = statement.executeQuery(cmd);
			while(result.next()){
				ax = result.getInt("ax");
				ay = result.getInt("ay");
				bx = result.getInt("bx");
				by = result.getInt("by");
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

		
		
	}//END loadCalibration()

	/**
	 * User convenience calibration information saved for load next program start
	 * 
	 * TODO:
	 * change table columns.  no longer need borders -> DO need corner_XY values
	 * */
	private static void saveCalibration() throws Exception {
		
		Connection connx = BLOOPRINT.getDataBaseConnection();
		
		String cmd = "INSERT INTO `blooprint`.`_calibration` (`id`, `ax`, `ay`, `bx`, `by`, "
				+"`cx`, `cy`, `dx`, `dy`, `fx`, `fy`, `gx`, `hy`, `aax`, `aay`, "
				+"`bbx`, `bby`, `ccx`, `ccy`, `ddx`, `ddy`, `mA`, `mB`, `mC`, `mD`, `xCenterIN`, `yCenterIN`, "
				+"`xCenterOUT`, `yCenterOUT`) VALUES (NULL, '"+ax+"','"+ay+"','"+bx+"','"+by
				+"','"+cx+"','"+cy+"','"+dx+"','"+dy+"','"+fx+"','"+fy+"','"+gx+"','"+hy+"','"
				+aax+"','"+aay+"','"+bbx+"','"+bby+"','"+ccx+"','"+ccy+"','"+ddx+"','"+ddy+"','"+mA+"','"
				+mB+"','"+mC+"','"+mD+"','"+xCenterIN+"','"+yCenterIN+"','"+xCenterOUT+"','"
				+yCenterOUT+"');";
		
		PreparedStatement statement = (PreparedStatement)connx.prepareStatement(cmd);
		
		try{
			
			statement.executeUpdate();
		}catch(Exception e){
			System.out.println("\nERROR: Calibration.saveCalibration()\n");
			e.getMessage();
			e.printStackTrace();
		}
		
		connx.close();
		
	}//END saveCalibration()
	
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
                	pixel = new Color(BLOOP.sketch.getRGB(col, row));
                    if (BLOOP.isMarker(pixel))
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
                	pixel = new Color(BLOOP.sketch.getRGB(col, row));
                    if (BLOOP.isMarker(pixel))
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
                	pixel = new Color(BLOOP.sketch.getRGB(col, row));
                    if (BLOOP.isMarker(pixel))
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
                	pixel = new Color(BLOOP.sketch.getRGB(col, row));
                    if (BLOOP.isMarker(pixel))
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
	
	/**
	 * STRETCH() method:
	 * translational pixel manipulation mechanism
	 *
	 *	This method is the core of Blooprint.xyz its input-output mechanism should remain as-is.
	 *	Please see derivation approach in project description.
	 *
	 *	Forseeable upgrade:
	 *	Due to physical radial misalignment of camera lens to projector lens, in theory, there could be a
	 *	dead zone of action on pixels located (thin sliver) along the image center vertical and center horizontal
	 *	This dead zone must be accounted for somehow if my theory is correct, but the whole of the image
	 *	manipulation should remain unchanged.
	 * */
	public static int[] stretch(int x, int y) {
		
		int[] some = new int[2];
		
		jx = (y - (x * mB) + (xCenterIN * mA) - yCenterIN) / (mA - mB);
        jy = (mA * (jx - xCenterIN)) + yCenterIN;
        ix = (y - (x * mA) + (xCenterIN * mB) - yCenterIN) / (mB - mA);
        iy = (mB * (ix - xCenterIN)) + yCenterIN;

        if (jy >= yCenterIN)
        {
            lA = Math.sqrt((Math.pow(jx - xCenterIN, 2)) + (Math.pow(jy - yCenterIN, 2)));
            lB = Math.sqrt((Math.pow(Calibration.bx - xCenterIN, 2)) + (Math.pow(Calibration.by - yCenterIN, 2)));
            lF = Math.sqrt((Math.pow(fx - xCenterOUT, 2)) + (Math.pow(fy - yCenterOUT, 2)));
            
            lE = lA * lF / lB;
            
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
            lB = Math.sqrt((Math.pow(Calibration.ax - xCenterIN, 2)) + (Math.pow(Calibration.ay - yCenterIN, 2)));
            lF = Math.sqrt((Math.pow(ex - xCenterOUT, 2)) + (Math.pow(ey - yCenterOUT, 2)));
            
            lE = lA * lF / lB;
            
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
            lD = Math.sqrt((Math.pow(Calibration.dx - xCenterIN, 2)) + (Math.pow(Calibration.dy - yCenterIN, 2)));
            lH = Math.sqrt((Math.pow(hx - xCenterOUT, 2)) + (Math.pow(hy - yCenterOUT, 2)));
            
            lG = lC * lH / lD;
            
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
            lD = Math.sqrt((Math.pow(Calibration.cx - xCenterIN, 2)) + (Math.pow(Calibration.cy - yCenterIN, 2)));
            lH = Math.sqrt((Math.pow(gx - xCenterOUT, 2)) + (Math.pow(gy - yCenterOUT, 2)));
            
            lG = lC * lH / lD;
            
            A = 1 + Math.pow(mD, 2);
            B = (-2 * xCenterOUT) - (2 * gx * Math.pow(mD, 2)) + (2 * gy * mD) - (2 * yCenterOUT * mD);
            C = Math.pow(xCenterOUT, 2) + Math.pow(gx * mD, 2) - (2 * gx * gy * mD) + Math.pow(gy, 2) + (2 * yCenterOUT * gx * mD) - (2 * yCenterOUT * gy) + Math.pow(yCenterOUT, 2) - Math.pow(lG, 2);

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
	

}//END Calibration class






































