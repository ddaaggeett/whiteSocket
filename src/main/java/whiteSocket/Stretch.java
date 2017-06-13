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

package main.java.whiteSocket;

public class Stretch {
	
	static int width, height; 

	public static int ax, ay, bx, by, cx, cy, dx, dy, fx, fy, gx, hy;
	public static double ex, ey, gy, hx = 0;
	public static double mA, mB, mC, mD, yCenterIN, xCenterIN, xCenterOUT, yCenterOUT, xOUT_temp, yOUT_temp, lxA, lxB,
			lyA, lyB, kxA, kxB, kyA, kyB, jx, jy, ix, iy, lx, ly, kx, ky, A, B, C, lA, lB, lC, lD, lE, lF, lG, lH;

	/**
	 * STRETCH() method: input pixel location -> output pixel location
	 *
	 * This method uses the vanishing point method.
	 * vpA and vpB found per Bloop.calibrate()
	 */
	public static int[] stretch(int x, int y) {
		
		int[] in = new int[2];
		in[0] = x;
		in[1] = y;
		int[] some = new int[2];
		
		int[] a = new int[2];
		int[] b = new int[2];
		int[] c = new int[2];
		int[] d = new int[2];
		
		//	TOP
		a[0] = ax; 
		a[1] = ay;
		b[0] = cx; 
		b[1] = cy;
		c = Tilt.vpA;
		d[0] = x; 
		d[1] = y;
		int[] pT = Tilt.getIntersection(a, b, c, d);
		
		//	BOTTOM
		a[0] = dx; 
		a[1] = dy;
		b[0] = bx; 
		b[1] = by;
		c = Tilt.vpA;
		d[0] = x; 
		d[1] = y;
		int[] pB = Tilt.getIntersection(a, b, c, d);
		
		//	LEFT
		a[0] = ax; 
		a[1] = ay;
		b[0] = dx; 
		b[1] = dy;
		c = Tilt.vpB;
		d[0] = x; 
		d[1] = y;
		int[] pL = Tilt.getIntersection(a, b, c, d);
		
		//	RIGHT
		a[0] = cx; 
		a[1] = cy;
		b[0] = ax; 
		b[1] = ay;
		c = Tilt.vpB;
		d[0] = ax; 
		d[1] = ay;
		int[] pR = Tilt.getIntersection(a, b, c, d);
		
		
		double dL = Tilt.getDistBetween(pL, in);
		double dW = Tilt.getDistBetween(pL, pR);
		double dT = Tilt.getDistBetween(pT, in);
		double dH = Tilt.getDistBetween(pT, pB);
		
		double xUnit = dL/dW;
		double yUnit = dT/dH;
		
		some[0] = (int) (xUnit * width);
		some[1] = (int) (yUnit * height);		
		
		return some;
	}// END stretch()

}
