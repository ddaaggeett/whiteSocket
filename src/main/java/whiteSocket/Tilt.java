package main.java.whiteSocket;

import main.java.whiteSocket.*;

public class Tilt {
	
	static int[] vpA;
	static int[] vpB;
	
	public static int[] getIntersection(int[] a, int[] d, int[] c, int[] b) {
		/*
		 * returns xy intersection point of 2 arbitrary lines defined by 2 points each
		 * lines AD and CB
		 * */
		
		double[] _intersect = new double[2];
		int[] intersect = new int[2];
		
		int ax = a[0];
		int ay = a[1];
		int dx = d[0];
		int dy = d[1];
		int cx = c[0];
		int cy = c[1];
		int bx = b[0];
		int by = b[1];
		
		double slope_AD = Math.sqrt(Math.pow(ay-dy, 2) + Math.pow(ax-dx, 2));
		double cross_AD = ay - (slope_AD * ax);	//	y = (slope_AD * x) + cross_AD
		double slope_CB = Math.sqrt(Math.pow(cy-by, 2) + Math.pow(cx-bx, 2));
		double cross_CB = cy - (slope_CB * cx);	//	y = (slope_CB * x) + cross_CB
		
		double const_A = cross_AD - cross_CB + by - ay;
		
		_intersect[0] = ((ax*by*bx) - (ax*cy*bx) - (dx*by*bx) + (dx*cy*bx) - (cx*dy*ax) + (cx*ay*ax) + (bx*dy*ax) - (bx*ay*ax) + (const_A*(cx-bx)*(ax-dx)))/((cx*ay) - (cx*dy) - (bx*ay) + (dx*dy) - (ax*cy) + (ax*by) + (dx*cy) - (dx*by));
		_intersect[1] = slope_AD*(_intersect[0] - ax) + cross_AD + ay;
		
		intersect[0] = (int) _intersect[0];
		intersect[1] = (int) _intersect[1];
		
		return intersect;
	}// END getIntersection()
	
	public static double getDistBetween(int[] a, int[] b) {
		
		return Math.sqrt(Math.pow(a[0]-b[0], 2) + Math.pow(a[1]-b[1], 2));
		
	}//	END getDistBetween()	

	public static void setTiltedCalibration() {
		
		int[] a = new int[2];
		a[0] = Stretch.ax;
		a[1] = Stretch.ay;
		int[] b = new int[2];
		b[0] = Stretch.bx;
		b[1] = Stretch.by;
		int[] c = new int[2];
		c[0] = Stretch.cx;
		c[1] = Stretch.cy;
		int[] d = new int[2];
		d[0] = Stretch.dx;
		d[1] = Stretch.dy;
		
		
		vpA = getIntersection(a,d,c,b);
		vpB = getIntersection(a,c,d,b);
		 
		
	}//END setTiltedCalibration()

}
