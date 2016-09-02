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
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.sql.Connection;
import java.sql.PreparedStatement;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.border.Border;




/**
 * BLIP object: all methods and objects that have anything to do with user text input
 * 
 * BLIP objects contain all text added by designer to supplement handwriting collected by BLOOP objects
 * BLIP objects are used in website where designers and interns are able to discuss and further comment
 * website commenting eventually should be tied into designer consideration to update blooprint accordingly 
 * */
public class BLIP extends BLOOPRINT{

	public Border border;
	public JTextArea jTextArea;
	public int id;
	
	public JPanel content;
	public InputMap inputMap;
	
	public BLIP(int id, Rectangle box, String text) throws Exception{
		
		/**
		 * TODO:
		 * for each BLIP displayed on BLOOPRINT, would like to have no border, but when BLIP gains focus 
		 * when traversing through for editing, BLIP with focus should display single pixel border for user 
		 * clarification as to which BLIP they're editing
		 * */
		
		this.id = id;
//		this.border = BorderFactory.createEmptyBorder();
		this.border = BorderFactory.createLineBorder(Color.BLACK);
		
		
    	if(box == null){
    		
    		/**
    		 * box drawn by user on whiteboard dictating exactly where they want new BLIP text to be located on BLOOPRINT
    		 * */
    		int[] scanBox = new int[4];
    		scanBox = zoomToBox();
    		int[] corners = Calibration.getScanBoxCorners(scanBox[0], scanBox[1], scanBox[2], scanBox[3]);
    		box = makeDisplayRectangle(corners);
    	}
    	
    	this.jTextArea = new JTextArea();
    	this.jTextArea.setBorder(BorderFactory.createCompoundBorder(border,BorderFactory.createEmptyBorder(1,1,1,1)));
    	this.jTextArea.setBounds(box.x, box.y, box.width, box.height);
    	this.jTextArea.setWrapStyleWord(true);
    	this.jTextArea.setText(text);
    	this.jTextArea.setLineWrap(true);
    	this.jTextArea.setEditable(true);
	}//END BLIP() constructor
	
	private static Rectangle makeDisplayRectangle(int[] corners) {
		
		Rectangle some = new Rectangle();
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
		double x2 		= (double)x 		/ (double)BLOOP.sketch.getWidth() 	* (double)blooprint.getWidth();
		double y2 		= (double)y 		/ (double)BLOOP.sketch.getHeight() * (double)blooprint.getHeight();
		double width2 	= (double)width 	/ (double)BLOOP.sketch.getWidth() 	* (double)blooprint.getWidth();
		double height2 	= (double)height 	/ (double)BLOOP.sketch.getHeight() * (double)blooprint.getHeight();
		
		
		int aa = (int)Math.round(x2);
		int bb = (int)Math.round(y2);
		int cc = (int)Math.round(width2);
		int dd = (int)Math.round(height2);
		
		some.setBounds(aa,bb,cc,dd);
		return some;
	}//END makeDisplayRectangle()
	
	private static int[] zoomToBox() {
	
		/**
		 * these value's starting points are backwards in order for the boolean comparisons below to initiate properly
		 * */
		int[] some = new int[4];
		int xmax = 0;
		int xmin = BLOOP.sketch.getWidth();
		int ymax = 0;
		int ymin = BLOOP.sketch.getHeight();
		
		here:

			for(int row = 0; row < BLOOP.sketch.getHeight(); row++){
				for(int col = 0; col < BLOOP.sketch.getWidth(); col++){
					
					/*
					 * dealing with pixels input by user - sketch
					 * */
					Color pixel = new Color(BLOOP.sketch.getRGB(col,row));
					BLOOP.xIN = col;
					BLOOP.yIN = row;
		            
		            
		            
		            if(BLOOP.isMarker(pixel)){

		            	System.out.println("xIN = " + BLOOP.xIN);
		            	System.out.println("yIN = " + BLOOP.yIN);
						
						System.out.println("\nfound eraser border!!!\n");
						
						/*
						 * encapsulate eraser area
						 * */
						int[] inCoord = new int[2];
						inCoord[0] = BLOOP.xIN;
						inCoord[1] = BLOOP.yIN;
						boolean flag = true;
						while(flag){
							
							
							//	2dArray[y][x]
//							some[inCoord[1]][inCoord[0]] = true;
							
							inCoord = BLOOP.getNextBorderPixel(inCoord);
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
							
							if((inCoord[0] == BLOOP.xIN) && (inCoord[1] == BLOOP.yIN)){
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
	
	
		some[0] = ymin-2;//could be 1 - lol
		some[1] = ymax+2;
		some[2] = xmin-2;
		some[3] = xmax+2;
		
		return some;
	}
	
	public void saveTextArea(String bp_name) throws Exception{
		
		/**
		 * BLIP objects should also save user who created BLIP
		 * */
				
		Connection connx = BLOOPRINT.getDataBaseConnection();
		String cmd = "insert into "+bp_name+"_BLIPS (x,y,width,height,textEntry) values (?,?,?,?,?)";
		PreparedStatement statement = (PreparedStatement) connx.prepareStatement(cmd);
		statement.setInt(1, jTextArea.getBounds().x);
		statement.setInt(2, jTextArea.getBounds().y);
		statement.setInt(3, jTextArea.getBounds().width);
		statement.setInt(4, jTextArea.getBounds().height);
		statement.setString(5, jTextArea.getText());
		statement.executeUpdate();
		connx.close();
		
	}//END saveTextArea()

	public void display() {
		
		blooprint.add(this.jTextArea);
		blooprint.revalidate();
		blooprint.repaint();
		this.jTextArea.requestFocus();
//		this.border = BorderFactory.createLineBorder(Color.BLACK);
		
	}//END display()
	
}
