package whiteSocket;

import java.awt.Color;

public class Border {
	
	int xMin, xMax, yMin, yMax;
	static int lastBorderPx = 5;
	
	public Border(int xIN, int yIN) {		
		System.out.println("init border ...");
		
		this.xMin = Bloop.sketch.getWidth()-1;
		this.xMax = 0;
		this.yMin = Bloop.sketch.getHeight()-1;
		this.yMax = 0;
		
		int[] inCoord = new int[2];
		inCoord[0] = xIN;
		inCoord[1] = yIN;
		boolean flag = true;
		while (flag) {
		
			// 2dArray[y][x]
			Area.hasBeenHit[inCoord[1]][inCoord[0]] = true;
		
			inCoord = getNextBorderPixel(inCoord);
			
			if(inCoord[0] > this.xMax) this.xMax = inCoord[0];
			if(inCoord[0] < this.xMin) this.xMin = inCoord[0];
			if(inCoord[1] > this.yMax) this.yMax = inCoord[1];
			if(inCoord[1] < this.yMin) this.yMin = inCoord[1];
			
			if ((inCoord[0] == xIN) && (inCoord[1] == yIN)) {
				System.out.println("found whole corner blob");
		
				flag = false;
			}
		}
		
	}
	
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
			if (Bloop.comparePixels(c,b)) {
				next[0] = coord[0]; // D
				next[1] = coord[1] + 1;
				lastBorderPx = 7;
			}
			else if (Bloop.comparePixels(d,c)) {
				next[0] = coord[0] - 1; // LD
				next[1] = coord[1] + 1;
				lastBorderPx = 8;
			}
			else if (Bloop.comparePixels(e,d)) {
				next[0] = coord[0] - 1; // L
				next[1] = coord[1];
				lastBorderPx = 1;
			}
			else if (Bloop.comparePixels(f,e)) {
				next[0] = coord[0] - 1; // LU
				next[1] = coord[1] - 1;
				lastBorderPx = 2;
			}
			else if (Bloop.comparePixels(g,f)) {
				next[0] = coord[0]; // U
				next[1] = coord[1] - 1;
				lastBorderPx = 3;
			}
			else if (Bloop.comparePixels(h,g)) {
				next[0] = coord[0] + 1; // RU
				next[1] = coord[1] - 1;
				lastBorderPx = 4;
			}
			else if (Bloop.comparePixels(a,h)) {
				next[0] = coord[0] + 1; // R
				next[1] = coord[1];
				lastBorderPx = 5;
			}
			else if (Bloop.comparePixels(b,a)) {
				next[0] = coord[0] + 1; // RD
				next[1] = coord[1] + 1;
				lastBorderPx = 6;
			}
			break;
		case 2:
			if (Bloop.comparePixels(d,c)) {
				next[0] = coord[0] - 1; // LD
				next[1] = coord[1] + 1;
				lastBorderPx = 8;
			}
			else if (Bloop.comparePixels(e,d)) {
				next[0] = coord[0] - 1; // L
				next[1] = coord[1];
				lastBorderPx = 1;
			}
			else if (Bloop.comparePixels(f,e)) {
				next[0] = coord[0] - 1; // LU
				next[1] = coord[1] - 1;
				lastBorderPx = 2;
			}
			else if (Bloop.comparePixels(g,f)) {
				next[0] = coord[0]; // U
				next[1] = coord[1] - 1;
				lastBorderPx = 3;
			}
			else if (Bloop.comparePixels(h,g)) {
				next[0] = coord[0] + 1; // RU
				next[1] = coord[1] - 1;
				lastBorderPx = 4;
			}
			else if (Bloop.comparePixels(a,h)) {
				next[0] = coord[0] + 1; // R
				next[1] = coord[1];
				lastBorderPx = 5;
			}
			else if (Bloop.comparePixels(b,a)) {
				next[0] = coord[0] + 1; // RD
				next[1] = coord[1] + 1;
				lastBorderPx = 6;
			}
			else if (Bloop.comparePixels(c,b)) {
				next[0] = coord[0]; // D
				next[1] = coord[1] + 1;
				lastBorderPx = 7;
			}
			break;
		case 3:
			if (Bloop.comparePixels(e,d)) {
				next[0] = coord[0] - 1; // L
				next[1] = coord[1];
				lastBorderPx = 1;
			}
			else if (Bloop.comparePixels(f,e)) {
				next[0] = coord[0] - 1; // LU
				next[1] = coord[1] - 1;
				lastBorderPx = 2;
			}
			else if (Bloop.comparePixels(g,f)) {
				next[0] = coord[0]; // U
				next[1] = coord[1] - 1;
				lastBorderPx = 3;
			}
			else if (Bloop.comparePixels(h,g)) {
				next[0] = coord[0] + 1; // RU
				next[1] = coord[1] - 1;
				lastBorderPx = 4;
			}
			else if (Bloop.comparePixels(a,h)) {
				next[0] = coord[0] + 1; // R
				next[1] = coord[1];
				lastBorderPx = 5;
			}
			else if (Bloop.comparePixels(b,a)) {
				next[0] = coord[0] + 1; // RD
				next[1] = coord[1] + 1;
				lastBorderPx = 6;
			}
			else if (Bloop.comparePixels(c,b)) {
				next[0] = coord[0]; // D
				next[1] = coord[1] + 1;
				lastBorderPx = 7;
			}
			else if (Bloop.comparePixels(d,c)) {
				next[0] = coord[0] - 1; // LD
				next[1] = coord[1] + 1;
				lastBorderPx = 8;
			}
			break;
		case 4:
			if (Bloop.comparePixels(f,e)) {
				next[0] = coord[0] - 1; // LU
				next[1] = coord[1] - 1;
				lastBorderPx = 2;
			}
			else if (Bloop.comparePixels(g,f)) {
				next[0] = coord[0]; // U
				next[1] = coord[1] - 1;
				lastBorderPx = 3;
			}
			else if (Bloop.comparePixels(h,g)) {
				next[0] = coord[0] + 1; // RU
				next[1] = coord[1] - 1;
				lastBorderPx = 4;
			}
			else if (Bloop.comparePixels(a,h)) {
				next[0] = coord[0] + 1; // R
				next[1] = coord[1];
				lastBorderPx = 5;
			}
			else if (Bloop.comparePixels(b,a)) {
				next[0] = coord[0] + 1; // RD
				next[1] = coord[1] + 1;
				lastBorderPx = 6;
			}
			else if (Bloop.comparePixels(c,b)) {
				next[0] = coord[0]; // D
				next[1] = coord[1] + 1;
				lastBorderPx = 7;
			}
			else if (Bloop.comparePixels(d,c)) {
				next[0] = coord[0] - 1; // LD
				next[1] = coord[1] + 1;
				lastBorderPx = 8;
			}
			else if (Bloop.comparePixels(e,d)) {
				next[0] = coord[0] - 1; // L
				next[1] = coord[1];
				lastBorderPx = 1;
			}
			break;
		case 5:
			if (Bloop.comparePixels(g,f)) {
				next[0] = coord[0]; // U
				next[1] = coord[1] - 1;
				lastBorderPx = 3;
			}
			else if (Bloop.comparePixels(h,g)) {
				next[0] = coord[0] + 1; // RU
				next[1] = coord[1] - 1;
				lastBorderPx = 4;
			}
			else if (Bloop.comparePixels(a,h)) {
				next[0] = coord[0] + 1; // R
				next[1] = coord[1];
				lastBorderPx = 5;
			}
			else if (Bloop.comparePixels(b,a)) {
				next[0] = coord[0] + 1; // RD
				next[1] = coord[1] + 1;
				lastBorderPx = 6;
			}
			else if (Bloop.comparePixels(c,b)) {
				next[0] = coord[0]; // D
				next[1] = coord[1] + 1;
				lastBorderPx = 7;
			}
			else if (Bloop.comparePixels(d,c)) {
				next[0] = coord[0] - 1; // LD
				next[1] = coord[1] + 1;
				lastBorderPx = 8;
			}
			else if (Bloop.comparePixels(e,d)) {
				next[0] = coord[0] - 1; // L
				next[1] = coord[1];
				lastBorderPx = 1;
			}
			else if (Bloop.comparePixels(f,e)) {
				next[0] = coord[0] - 1; // LU
				next[1] = coord[1] - 1;
				lastBorderPx = 2;
			}
			break;
		case 6:
			if (Bloop.comparePixels(h,g)) {
				next[0] = coord[0] + 1; // RU
				next[1] = coord[1] - 1;
				lastBorderPx = 4;
			}
			else if (Bloop.comparePixels(a,h)) {
				next[0] = coord[0] + 1; // R
				next[1] = coord[1];
				lastBorderPx = 5;
			}
			else if (Bloop.comparePixels(b,a)) {
				next[0] = coord[0] + 1; // RD
				next[1] = coord[1] + 1;
				lastBorderPx = 6;
			}
			else if (Bloop.comparePixels(c,b)) {
				next[0] = coord[0]; // D
				next[1] = coord[1] + 1;
				lastBorderPx = 7;
			}
			else if (Bloop.comparePixels(d,c)) {
				next[0] = coord[0] - 1; // LD
				next[1] = coord[1] + 1;
				lastBorderPx = 8;
			}
			else if (Bloop.comparePixels(e,d)) {
				next[0] = coord[0] - 1; // L
				next[1] = coord[1];
				lastBorderPx = 1;
			}
			else if (Bloop.comparePixels(f,e)) {
				next[0] = coord[0] - 1; // LU
				next[1] = coord[1] - 1;
				lastBorderPx = 2;
			}
			else if (Bloop.comparePixels(g,f)) {
				next[0] = coord[0]; // U
				next[1] = coord[1] - 1;
				lastBorderPx = 3;
			}
			break;
		case 7:
			if (Bloop.comparePixels(a,h)) {
				next[0] = coord[0] + 1; // R
				next[1] = coord[1];
				lastBorderPx = 5;
			}
			else if (Bloop.comparePixels(b,a)) {
				next[0] = coord[0] + 1; // RD
				next[1] = coord[1] + 1;
				lastBorderPx = 6;
			}
			else if (Bloop.comparePixels(c,b)) {
				next[0] = coord[0]; // D
				next[1] = coord[1] + 1;
				lastBorderPx = 7;
			}
			else if (Bloop.comparePixels(d,c)) {
				next[0] = coord[0] - 1; // LD
				next[1] = coord[1] + 1;
				lastBorderPx = 8;
			}
			else if (Bloop.comparePixels(e,d)) {
				next[0] = coord[0] - 1; // L
				next[1] = coord[1];
				lastBorderPx = 1;
			}
			else if (Bloop.comparePixels(f,e)) {
				next[0] = coord[0] - 1; // LU
				next[1] = coord[1] - 1;
				lastBorderPx = 2;
			}
			else if (Bloop.comparePixels(g,f)) {
				next[0] = coord[0]; // U
				next[1] = coord[1] - 1;
				lastBorderPx = 3;
			}
			else if (Bloop.comparePixels(h,g)) {
				next[0] = coord[0] + 1; // RU
				next[1] = coord[1] - 1;
				lastBorderPx = 4;
			}
			break;
		case 8:
			if (Bloop.comparePixels(b,a)) {
				next[0] = coord[0] + 1; // RD
				next[1] = coord[1] + 1;
				lastBorderPx = 6;
			}
			else if (Bloop.comparePixels(c,b)) {
				next[0] = coord[0]; // D
				next[1] = coord[1] + 1;
				lastBorderPx = 7;
			}
			else if (Bloop.comparePixels(d,c)) {
				next[0] = coord[0] - 1; // LD
				next[1] = coord[1] + 1;
				lastBorderPx = 8;
			}
			else if (Bloop.comparePixels(e,d)) {
				next[0] = coord[0] - 1; // L
				next[1] = coord[1];
				lastBorderPx = 1;
			}
			else if (Bloop.comparePixels(f,e)) {
				next[0] = coord[0] - 1; // LU
				next[1] = coord[1] - 1;
				lastBorderPx = 2;
			}
			else if (Bloop.comparePixels(g,f)) {
				next[0] = coord[0]; // U
				next[1] = coord[1] - 1;
				lastBorderPx = 3;
			}
			else if (Bloop.comparePixels(h,g)) {
				next[0] = coord[0] + 1; // RU
				next[1] = coord[1] - 1;
				lastBorderPx = 4;
			}
			else if (Bloop.comparePixels(a,h)) {
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
	


}
