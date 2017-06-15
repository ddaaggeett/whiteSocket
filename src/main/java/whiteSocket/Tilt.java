package main.java.whiteSocket;

import main.java.whiteSocket.*;

public class Tilt {
	
	static double[] vpA;
	static double[] vpB;
	
	public static double[] getIntersection(double[] a, double[] b, double[] c, double[] d) {
		/*
		 * returns xy intersection point of 2 arbitrary lines defined by 2 points each
		 * lines AD and CB
		 * */
		
		double[] _intersect = new double[2];
		int[] intersect = new int[2];
		
		double x1 = a[0];
		double y1 = a[1];
		double x2 = b[0];
		double y2 = b[1];
		double x3 = c[0];
		double y3 = c[1];
		double x4 = d[0];
		double y4 = d[1];
		
		double aa = ((x1 * y2) - (y1*x2)) * (x3 - x4);
		double bb = (x1 - x2) * ((x3 * y4) - (y3 * x4));
		double cc = (x1 - x2) * (y3 - y4);
		double dd = (y1 - y2) * (x3 - x4);
		_intersect[0] = (double) (aa - bb) / (cc - dd);
		
		aa = ((x1 * y2) - (y1 * x2)) * (y3 - y4);
		bb = (y1 - y2) * ((x3 * y4) - (y3 * x4));
		cc = (x1 - x2) * (y3 - y4);
		dd = (y1 - y2) * (x3 - x4);
		_intersect[1] = (double) (aa - bb) / (cc - dd);
		
		intersect[0] = (int) _intersect[0];
		intersect[1] = (int) _intersect[1];
		
		return _intersect;
	}// END getIntersection()
	
	public static double getDistBetween(double[] a, double[] b) {
		return Math.sqrt(Math.pow((a[0]-b[0]), 2) + Math.pow((a[1]-b[1]), 2));
	}//	END getDistBetween()	

	public static void setTiltedCalibration() {
		
		double[] a = new double[2];
		a[0] = Stretch.ax;
		a[1] = Stretch.ay;
		double[] b = new double[2];
		b[0] = Stretch.bx;
		b[1] = Stretch.by;
		double[] c = new double[2];
		c[0] = Stretch.cx;
		c[1] = Stretch.cy;
		double[] d = new double[2];
		d[0] = Stretch.dx;
		d[1] = Stretch.dy;
		
		
		vpA = getIntersection(a,d,c,b);
		vpB = getIntersection(a,c,d,b);
		
		System.out.println("\nvanishing point A\nx = " + vpA[0] + "  y = " + vpA[1]);
		System.out.println("vanishing point B\nx = " + vpB[0] + "  y = " + vpB[1]);
		 
		
	}//END setTiltedCalibration()
}
