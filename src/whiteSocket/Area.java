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
import java.awt.Point;
import java.util.LinkedList;
import java.util.Queue;

import whiteSocket.Bloop;

public class Area {
	
	int startX, startY;
	boolean[][] area;
		
	public Area(int x, int y) {
		
		this.startX = x;
		this.startY = y;
		this.area = floodBorder(getBorder(this), this.startX, this.startY+2);

	}//END constructor
	

	public static boolean[][] getBorder(Area area) {
		/**
		 * dealing with area drawn by user to erase
		 * sets binary map single pixel strand border for future use in floodBorder() method
		*/

		boolean[][] border = new boolean[Bloop.sketch.getHeight()][Bloop.sketch.getWidth()];

		/*
		 * encapsulate eraser area
		 * */
		int[] inCoord = new int[2];
		inCoord[0] = area.startX;
		inCoord[1] = area.startY;
		boolean flag = true;
		while(flag){
			
			Bloop.totalErase[inCoord[1]][inCoord[0]] = true;

			//	2dArray[y][x]
			border[inCoord[1]][inCoord[0]] = true;

			inCoord = getNextBorderPixel(inCoord);

			if((inCoord[0] == area.startX) && (inCoord[1] == area.startY)){

				System.out.println("found single border");

				flag = false;
			}
		}
						
		return border;
	}//END getBorder()
	

	public static int[] getNextBorderPixel(int[] coord) {
		/**
		 * locate pixel after last found pixel until entire border is lined
		 * */
		
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
	

	public static boolean[][] floodBorder(boolean[][] floodArea, int x, int y) {
		/**
		 * paint bucket-like algorithm to fill binary map border
		 * this filled area becomes the area to be filtered through the stretch()
		 * area pixels to be turned Color.WHITE (unless notified otherwise by user dictation)
		 *
		 * */
		
        if (!floodArea[y][x]) {



		    Queue<Point> queue = new LinkedList<Point>();
		    queue.add(new Point(x, y));

		    while (!queue.isEmpty()) {

		    	Point p = queue.remove();

	        	if (!floodArea[p.y][p.x]) {

	            	floodArea[p.y][p.x] = true;
	            	
	            	Bloop.totalErase[p.y][p.x] = true;


	                queue.add(new Point(p.x + 1, p.y));
	                queue.add(new Point(p.x - 1, p.y));
	                queue.add(new Point(p.x, p.y + 1));
	                queue.add(new Point(p.x, p.y - 1));

	            }
		    }

		}

		return floodArea;
	}//END floodBorder()

	

}
