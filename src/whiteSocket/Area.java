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

package whiteSocket;

import java.awt.Color;

import whiteSocket.Bloop;

public class Area {
	
	boolean[][] area = new boolean[Bloop.sketch.getHeight()][Bloop.sketch.getWidth()];
	int startX, startY;
	
	/*first border hit*/
	public static int borderStart_X,borderStart_Y;


	
	
	public void constructor(int x, int y) {
		this.startX = x;
		this.startY = y;
	}

	public static boolean[][] getSketchDrawnArea() throws Exception {
		/**
		 * returns binary map. area of interest on whiteboard, just outside of projected corners
		 * 
		 * TODO: need to scan for multiple getSketchDrawnArea areas.  so far we are only checking for
		 * the first one that we come across.
		 * */

		boolean[][] area = getUserDrawnBorder();

		int xStart = borderStart_X;
		int yStart = borderStart_Y + 2; /*TODO:	must consider the case in which borderStart_Y+2 is not inside border wall*/

		area = Bloop.floodBorder(area, xStart,yStart);

		return area;
	}//END getSketchDrawnArea()

	public static boolean[][] getUserDrawnBorder() {
		/**
		 * dealing with area drawn by user to erase
		 * sets binary map single pixel strand border for future use in floodBorder() method
		
		TODO:
		after first border pixel is hit, continue searching through rest of sketch image for other areas
		*/

		boolean[][] border = new boolean[Bloop.sketch.getHeight()][Bloop.sketch.getWidth()];

		here:

			for(int row = 0; row < Bloop.sketch.getHeight(); row++){
				for(int col = 0; col < Bloop.sketch.getWidth(); col++){

					/*
					 * dealing with pixels input by user - sketch
					 * */
					Color pxColor = new Color(Bloop.sketch.getRGB(col,row));
					int xIN = col;
		            int yIN = row;

		            if(Bloop.areaOfInterest[row][col] && Bloop.isMarker(pxColor)){

						System.out.println("found eraser border!!!");

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
	 * recursive method locating pixel after last found pixel until entire border is lined
	 * */
	public static int[] getNextBorderPixel(int[] coord) {

		int[] next = new int[2];

		/*
		 * all 8 surrounding pixels need to be checked counterclockwise
		 * */
		Color a = new Color(Bloop.sketch.getRGB(coord[0]+1,coord[1]));	//	R
		Color b = new Color(Bloop.sketch.getRGB(coord[0]+1,coord[1]+1));	//	RD
		Color c = new Color(Bloop.sketch.getRGB(coord[0],coord[1]+1));	//	D
		Color d = new Color(Bloop.sketch.getRGB(coord[0]-1,coord[1]+1));	//	LD
		Color e = new Color(Bloop.sketch.getRGB(coord[0]-1,coord[1]));	//	L
		Color f = new Color(Bloop.sketch.getRGB(coord[0]-1,coord[1]-1));	//	LU
		Color g = new Color(Bloop.sketch.getRGB(coord[0],coord[1]-1));	//	U
		Color h = new Color(Bloop.sketch.getRGB(coord[0]+1,coord[1]-1));	//	RU


		if(Bloop.isMarker(a) & !Bloop.isMarker(h)){
			next[0] = coord[0]+1;	//	R
			next[1] = coord[1];
		}
		else if(Bloop.isMarker(b) & !Bloop.isMarker(a)){
			next[0] = coord[0]+1;	//	RD
			next[1] = coord[1]+1;
		}
		else if(Bloop.isMarker(c) & !Bloop.isMarker(b)){
			next[0] = coord[0];		//	D
			next[1] = coord[1]+1;
		}
		else if(Bloop.isMarker(d) & !Bloop.isMarker(c)){
			next[0] = coord[0]-1;	//	LD
			next[1] = coord[1]+1;
		}
		else if(Bloop.isMarker(e) & !Bloop.isMarker(d)){
			next[0] = coord[0]-1;	//	L
			next[1] = coord[1];
		}
		else if(Bloop.isMarker(f) & !Bloop.isMarker(e)){
			next[0] = coord[0]-1;	//	LU
			next[1] = coord[1]-1;
		}
		else if(Bloop.isMarker(g) & !Bloop.isMarker(f)){
			next[0] = coord[0];		//	U
			next[1] = coord[1]-1;
		}
		else if(Bloop.isMarker(h) & !Bloop.isMarker(g)){
			next[0] = coord[0]+1;	//	RU
			next[1] = coord[1]-1;
		}
		else{
			System.err.println("something wrong with setting next border pixel!!!");
		}


		return next;
	}//END getNextBorderPixel()



}
