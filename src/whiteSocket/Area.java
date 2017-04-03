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
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;

import whiteSocket.Bloop;

public class Area {

	int startX, startY, pxCount, minX, maxX, minY, maxY;
	boolean[][] area;
	static int lastBorderPx = 5;

	static boolean[][] hasBeenHit;

	public Area(int x, int y) {

		this.startX = x;
		this.startY = y;
		this.area = new boolean[Bloop.sketch.getHeight()][Bloop.sketch.getWidth()];
		this.area = floodBorder(this, getBorder(this), this.startX, this.startY + 2);
//		getBorder(this);

	}// END constructor

	
	public static void getCornerBlobs() throws IOException {
		
//		ArrayList<Area> _1 = new ArrayList<Area>();

		System.out.println("getCornerBlobs() ...");

		hasBeenHit = new boolean[Bloop.sketch.getHeight()][Bloop.sketch.getWidth()];

		Color pxColor = null;

		here:

		for (int row = 0; row < Bloop.sketch.getHeight(); row++) {
			for (int col = 0; col < Bloop.sketch.getWidth(); col++) {
				/*
				 * QUAD #1 - UL
				 */

				int xIN = col;
				int yIN = row;

				pxColor = new Color(Bloop.sketch.getRGB(xIN, yIN));
				if (Bloop.isMarker(pxColor) && !hasBeenHit[yIN][xIN]) {
					
					System.out.println("found a blob");
					
////					_1.add(new Area(xIN,yIN));
//					Area some = new Area(xIN,yIN);
					

					int[] inCoord = new int[2];
					inCoord[0] = xIN;
					inCoord[1] = yIN;
					boolean flag = true;
					while (flag) {

						// 2dArray[y][x]
						hasBeenHit[inCoord[1]][inCoord[0]] = true;

						inCoord = getNextBorderPixel(inCoord);
						
						if ((inCoord[0] == xIN) && (inCoord[1] == yIN)) {
							System.out.println("found whole corner blob");

							flag = false;
							break here;
						}

					}

				}
			}
		}
		
		printImgBool(hasBeenHit);

	}// END getCornerBlobs()

	public static boolean[][] getBorder(Area area) {
		
		System.out.println("getting border ...");
		/**
		 * dealing with area drawn by user to erase sets binary map single pixel
		 * strand border for future use in floodBorder() method
		 */

		boolean[][] border = new boolean[Bloop.sketch.getHeight()][Bloop.sketch.getWidth()];

		/*
		 * encapsulate eraser area
		 */
		int[] inCoord = new int[2];
		inCoord[0] = area.startX;
		inCoord[1] = area.startY;
		boolean flag = true;

		while (flag) {

			area.pxCount++;
			area.area[inCoord[1]][inCoord[0]] = true;
			
			hasBeenHit[inCoord[1]][inCoord[0]] = true;
			
//			Bloop.totalErase[inCoord[1]][inCoord[0]] = true;
			
			// 2dArray[y][x]
			border[inCoord[1]][inCoord[0]] = true;
			
			inCoord = getNextBorderPixel(inCoord);
			
			if ((inCoord[0] == area.startX) && (inCoord[1] == area.startY)) {
				
				System.out.println("found whole border");
				
				flag = false;
			}
		}
		
		return border;
	}// END getBorder()

	public static int[] getNextBorderPixel(int[] coord) {
		/**
		 * locate pixel after last found pixel until entire border is lined
		 */

		int[] next = new int[2];

		/*
		 * all 8 surrounding pixels need to be checked clockwise
		 */
		Color a = new Color(Bloop.sketch.getRGB(coord[0] + 1, coord[1])); // R
		Color b = new Color(Bloop.sketch.getRGB(coord[0] + 1, coord[1] + 1)); // RD
		Color c = new Color(Bloop.sketch.getRGB(coord[0], coord[1] + 1)); // D
		Color d = new Color(Bloop.sketch.getRGB(coord[0] - 1, coord[1] + 1)); // LD
		Color e = new Color(Bloop.sketch.getRGB(coord[0] - 1, coord[1])); // L
		Color f = new Color(Bloop.sketch.getRGB(coord[0] - 1, coord[1] - 1)); // LU
		Color g = new Color(Bloop.sketch.getRGB(coord[0], coord[1] - 1)); // U
		Color h = new Color(Bloop.sketch.getRGB(coord[0] + 1, coord[1] - 1)); // RU
		
		switch(lastBorderPx) {
		case 1:
			if (comparePixels(c,b)) {
				next[0] = coord[0]; // D
				next[1] = coord[1] + 1;
				lastBorderPx = 7;
			}
			else if (comparePixels(d,c)) {
				next[0] = coord[0] - 1; // LD
				next[1] = coord[1] + 1;
				lastBorderPx = 8;
			}
			else if (comparePixels(e,d)) {
				next[0] = coord[0] - 1; // L
				next[1] = coord[1];
				lastBorderPx = 1;
			}
			else if (comparePixels(f,e)) {
				next[0] = coord[0] - 1; // LU
				next[1] = coord[1] - 1;
				lastBorderPx = 2;
			}
			else if (comparePixels(g,f)) {
				next[0] = coord[0]; // U
				next[1] = coord[1] - 1;
				lastBorderPx = 3;
			}
			else if (comparePixels(h,g)) {
				next[0] = coord[0] + 1; // RU
				next[1] = coord[1] - 1;
				lastBorderPx = 4;
			}
			else if (comparePixels(a,h)) {
				next[0] = coord[0] + 1; // R
				next[1] = coord[1];
				lastBorderPx = 5;
			}
			else if (comparePixels(b,a)) {
				next[0] = coord[0] + 1; // RD
				next[1] = coord[1] + 1;
				lastBorderPx = 6;
			}
			break;
		case 2:
			if (comparePixels(d,c)) {
				next[0] = coord[0] - 1; // LD
				next[1] = coord[1] + 1;
				lastBorderPx = 8;
			}
			else if (comparePixels(e,d)) {
				next[0] = coord[0] - 1; // L
				next[1] = coord[1];
				lastBorderPx = 1;
			}
			else if (comparePixels(f,e)) {
				next[0] = coord[0] - 1; // LU
				next[1] = coord[1] - 1;
				lastBorderPx = 2;
			}
			else if (comparePixels(g,f)) {
				next[0] = coord[0]; // U
				next[1] = coord[1] - 1;
				lastBorderPx = 3;
			}
			else if (comparePixels(h,g)) {
				next[0] = coord[0] + 1; // RU
				next[1] = coord[1] - 1;
				lastBorderPx = 4;
			}
			else if (comparePixels(a,h)) {
				next[0] = coord[0] + 1; // R
				next[1] = coord[1];
				lastBorderPx = 5;
			}
			else if (comparePixels(b,a)) {
				next[0] = coord[0] + 1; // RD
				next[1] = coord[1] + 1;
				lastBorderPx = 6;
			}
			else if (comparePixels(c,b)) {
				next[0] = coord[0]; // D
				next[1] = coord[1] + 1;
				lastBorderPx = 7;
			}
			break;
		case 3:
			if (comparePixels(e,d)) {
				next[0] = coord[0] - 1; // L
				next[1] = coord[1];
				lastBorderPx = 1;
			}
			else if (comparePixels(f,e)) {
				next[0] = coord[0] - 1; // LU
				next[1] = coord[1] - 1;
				lastBorderPx = 2;
			}
			else if (comparePixels(g,f)) {
				next[0] = coord[0]; // U
				next[1] = coord[1] - 1;
				lastBorderPx = 3;
			}
			else if (comparePixels(h,g)) {
				next[0] = coord[0] + 1; // RU
				next[1] = coord[1] - 1;
				lastBorderPx = 4;
			}
			else if (comparePixels(a,h)) {
				next[0] = coord[0] + 1; // R
				next[1] = coord[1];
				lastBorderPx = 5;
			}
			else if (comparePixels(b,a)) {
				next[0] = coord[0] + 1; // RD
				next[1] = coord[1] + 1;
				lastBorderPx = 6;
			}
			else if (comparePixels(c,b)) {
				next[0] = coord[0]; // D
				next[1] = coord[1] + 1;
				lastBorderPx = 7;
			}
			else if (comparePixels(d,c)) {
				next[0] = coord[0] - 1; // LD
				next[1] = coord[1] + 1;
				lastBorderPx = 8;
			}
			break;
		case 4:
			if (comparePixels(f,e)) {
				next[0] = coord[0] - 1; // LU
				next[1] = coord[1] - 1;
				lastBorderPx = 2;
			}
			else if (comparePixels(g,f)) {
				next[0] = coord[0]; // U
				next[1] = coord[1] - 1;
				lastBorderPx = 3;
			}
			else if (comparePixels(h,g)) {
				next[0] = coord[0] + 1; // RU
				next[1] = coord[1] - 1;
				lastBorderPx = 4;
			}
			else if (comparePixels(a,h)) {
				next[0] = coord[0] + 1; // R
				next[1] = coord[1];
				lastBorderPx = 5;
			}
			else if (comparePixels(b,a)) {
				next[0] = coord[0] + 1; // RD
				next[1] = coord[1] + 1;
				lastBorderPx = 6;
			}
			else if (comparePixels(c,b)) {
				next[0] = coord[0]; // D
				next[1] = coord[1] + 1;
				lastBorderPx = 7;
			}
			else if (comparePixels(d,c)) {
				next[0] = coord[0] - 1; // LD
				next[1] = coord[1] + 1;
				lastBorderPx = 8;
			}
			else if (comparePixels(e,d)) {
				next[0] = coord[0] - 1; // L
				next[1] = coord[1];
				lastBorderPx = 1;
			}
			break;
		case 5:
			if (comparePixels(g,f)) {
				next[0] = coord[0]; // U
				next[1] = coord[1] - 1;
				lastBorderPx = 3;
			}
			else if (comparePixels(h,g)) {
				next[0] = coord[0] + 1; // RU
				next[1] = coord[1] - 1;
				lastBorderPx = 4;
			}
			else if (comparePixels(a,h)) {
				next[0] = coord[0] + 1; // R
				next[1] = coord[1];
				lastBorderPx = 5;
			}
			else if (comparePixels(b,a)) {
				next[0] = coord[0] + 1; // RD
				next[1] = coord[1] + 1;
				lastBorderPx = 6;
			}
			else if (comparePixels(c,b)) {
				next[0] = coord[0]; // D
				next[1] = coord[1] + 1;
				lastBorderPx = 7;
			}
			else if (comparePixels(d,c)) {
				next[0] = coord[0] - 1; // LD
				next[1] = coord[1] + 1;
				lastBorderPx = 8;
			}
			else if (comparePixels(e,d)) {
				next[0] = coord[0] - 1; // L
				next[1] = coord[1];
				lastBorderPx = 1;
			}
			else if (comparePixels(f,e)) {
				next[0] = coord[0] - 1; // LU
				next[1] = coord[1] - 1;
				lastBorderPx = 2;
			}
			break;
		case 6:
			if (comparePixels(h,g)) {
				next[0] = coord[0] + 1; // RU
				next[1] = coord[1] - 1;
				lastBorderPx = 4;
			}
			else if (comparePixels(a,h)) {
				next[0] = coord[0] + 1; // R
				next[1] = coord[1];
				lastBorderPx = 5;
			}
			else if (comparePixels(b,a)) {
				next[0] = coord[0] + 1; // RD
				next[1] = coord[1] + 1;
				lastBorderPx = 6;
			}
			else if (comparePixels(c,b)) {
				next[0] = coord[0]; // D
				next[1] = coord[1] + 1;
				lastBorderPx = 7;
			}
			else if (comparePixels(d,c)) {
				next[0] = coord[0] - 1; // LD
				next[1] = coord[1] + 1;
				lastBorderPx = 8;
			}
			else if (comparePixels(e,d)) {
				next[0] = coord[0] - 1; // L
				next[1] = coord[1];
				lastBorderPx = 1;
			}
			else if (comparePixels(f,e)) {
				next[0] = coord[0] - 1; // LU
				next[1] = coord[1] - 1;
				lastBorderPx = 2;
			}
			else if (comparePixels(g,f)) {
				next[0] = coord[0]; // U
				next[1] = coord[1] - 1;
				lastBorderPx = 3;
			}
			break;
		case 7:
			if (comparePixels(a,h)) {
				next[0] = coord[0] + 1; // R
				next[1] = coord[1];
				lastBorderPx = 5;
			}
			else if (comparePixels(b,a)) {
				next[0] = coord[0] + 1; // RD
				next[1] = coord[1] + 1;
				lastBorderPx = 6;
			}
			else if (comparePixels(c,b)) {
				next[0] = coord[0]; // D
				next[1] = coord[1] + 1;
				lastBorderPx = 7;
			}
			else if (comparePixels(d,c)) {
				next[0] = coord[0] - 1; // LD
				next[1] = coord[1] + 1;
				lastBorderPx = 8;
			}
			else if (comparePixels(e,d)) {
				next[0] = coord[0] - 1; // L
				next[1] = coord[1];
				lastBorderPx = 1;
			}
			else if (comparePixels(f,e)) {
				next[0] = coord[0] - 1; // LU
				next[1] = coord[1] - 1;
				lastBorderPx = 2;
			}
			else if (comparePixels(g,f)) {
				next[0] = coord[0]; // U
				next[1] = coord[1] - 1;
				lastBorderPx = 3;
			}
			else if (comparePixels(h,g)) {
				next[0] = coord[0] + 1; // RU
				next[1] = coord[1] - 1;
				lastBorderPx = 4;
			}
			break;
		case 8:
			if (comparePixels(b,a)) {
				next[0] = coord[0] + 1; // RD
				next[1] = coord[1] + 1;
				lastBorderPx = 6;
			}
			else if (comparePixels(c,b)) {
				next[0] = coord[0]; // D
				next[1] = coord[1] + 1;
				lastBorderPx = 7;
			}
			else if (comparePixels(d,c)) {
				next[0] = coord[0] - 1; // LD
				next[1] = coord[1] + 1;
				lastBorderPx = 8;
			}
			else if (comparePixels(e,d)) {
				next[0] = coord[0] - 1; // L
				next[1] = coord[1];
				lastBorderPx = 1;
			}
			else if (comparePixels(f,e)) {
				next[0] = coord[0] - 1; // LU
				next[1] = coord[1] - 1;
				lastBorderPx = 2;
			}
			else if (comparePixels(g,f)) {
				next[0] = coord[0]; // U
				next[1] = coord[1] - 1;
				lastBorderPx = 3;
			}
			else if (comparePixels(h,g)) {
				next[0] = coord[0] + 1; // RU
				next[1] = coord[1] - 1;
				lastBorderPx = 4;
			}
			else if (comparePixels(a,h)) {
				next[0] = coord[0] + 1; // R
				next[1] = coord[1];
				lastBorderPx = 5;
			}
			break;
		default:
			System.err.println("something wrong with setting next border pixel!!!");
			break;
		}
		
		return next;
	}// END getNextBorderPixel()
	
	public static boolean[][] floodBorder(Area area, boolean[][] floodArea, int x, int y) {
		/**
		 * paint bucket-like algorithm to fill binary map border this filled
		 * area becomes the area to be filtered through the stretch() area
		 * pixels to be turned Color.WHITE (unless notified otherwise by user
		 * dictation)
		 *
		 */

		if (!floodArea[y][x]) {

			Queue<Point> queue = new LinkedList<Point>();
			queue.add(new Point(x, y));

			while (!queue.isEmpty()) {

				Point p = queue.remove();

				if (!floodArea[p.y][p.x]) {

					floodArea[p.y][p.x] = true;

//					Bloop.totalErase[p.y][p.x] = true;
					hasBeenHit[p.y][p.x] = true;
					area.area[p.y][p.x] = true;
					area.pxCount++;

					queue.add(new Point(p.x + 1, p.y));
					queue.add(new Point(p.x - 1, p.y));
					queue.add(new Point(p.x, p.y + 1));
					queue.add(new Point(p.x, p.y - 1));

				}
			}
		}

		return floodArea;
	}// END floodBorder()

	static BufferedImage testOut = new BufferedImage(Bloop.sketch.getWidth(), Bloop.sketch.getHeight(), BufferedImage.TYPE_INT_RGB);
	public static void createWhiteImage(){ 
		Graphics2D    graphics = testOut.createGraphics();	
		graphics.setPaint ( Color.white );
		graphics.fillRect ( 0, 0, testOut.getWidth(), testOut.getHeight() );		
	}//END createWhiteImage
	
	public static void printImgBool(boolean[][] some) throws IOException {
		createWhiteImage();
		for (int row = 0; row < Bloop.sketch.getHeight(); row++) {
			for (int col = 0; col < Bloop.sketch.getWidth(); col++) {
				if(some[row][col]) {
					testOut.setRGB(col, row, 0x000000);
				}
			}
		}
		saveImg(testOut);
	}//END printImgBool()
	
	public static void saveImg(BufferedImage some) throws IOException {
		try {
			File outputfile = new File("./tests/border");
			ImageIO.write(some, "bmp", outputfile);
		} catch (Exception ex) {
			System.out.println("ERROR saveBlooprint(): " + ex.getMessage());
		}
	}// END saveImg()
	
	private static boolean comparePixels(Color a, Color b) {
		if (Bloop.isMarker(a) && !Bloop.isMarker(b)) {
			return true;
		}
		return false;
	}//END comparePixels()
	
	
}
