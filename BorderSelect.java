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
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.LayoutStyle.ComponentPlacement;

public class BorderSelect extends JFrame {

	public static CornerImage displayImg;
	int top, bottom, left, right;
	public static int[] calibBorders = new int[4];
	public static int[] calibCorners = new int[8];
	
	static String cornerToSet = null;
	
	BufferedImage image;
	
	
	/**
	 * Create the frame.
	 */
	public BorderSelect() {
		
//		super();
		
		System.out.println("new calibration");
		/*
		 * frame to allow user to select regions outside of user drawn corners
		 * 
		 * method: click button for specific corner, then click the image to set value
		 * do for all calibBorders then select complete
		 * 
		 * */
		
		this.setUndecorated(true);
		this.setVisible(true);
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setBounds(BLOOPRINT.getProjectorBounds());
		
		
		
		
		this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		
		
		displayImg = new CornerImage(BLOOP.sketch);
		setContentPane(displayImg);
		
		JButton tR_button = new JButton("TOP RIGHT");
		tR_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("select just outside TOP RIGHT corner");
				cornerToSet = "TR";
			}
		});
		
		JButton bL_button = new JButton("BOTTOM LEFT");
		bL_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("select just outside BOTTOM LEFT corner");
				cornerToSet = "BL";
			}
		});
		
		JButton bR_button = new JButton("BOTTOM RIGHT");
		bR_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("select just outside BOTTOM RIGHT corner");
				cornerToSet = "BR";
			}
		});
		
		JButton tL_button = new JButton("TOP LEFT");
		tL_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("select just outside TOP LEFT corner");
				cornerToSet = "TL";
			}
		});
		
		this.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				setCornerValue(e);
				System.out.println("x = "+e.getX());
				System.out.println("y = "+e.getY());
			}
			@Override
			public void mouseReleased(MouseEvent e) {}
			@Override
			public void mousePressed(MouseEvent e) {}
			@Override
			public void mouseExited(MouseEvent e) {}
			@Override
			public void mouseEntered(MouseEvent e) {}
			
		});
		
		
		/*
		 * exit the calibration window
		 * */
		JButton exitCalibrate = new JButton("All Set?");
		exitCalibrate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
//				/////////////////////////////////////////////
//				///////////////////////////////////////////
//				//	TODO:
//				//	for quick test only
//				//	delete all within this block!!
////				calibBorders[0] = 10;
////				calibBorders[1] = 440;
////				calibBorders[2] = 10;
////				calibBorders[3] = 790;
//				
//				
//				//	corners
//				calibCorners[0] = 10; 
//				calibCorners[1] = 10; 
//				calibCorners[2] = 790; 
//				calibCorners[3] = 9; 
//				calibCorners[4] = 9; 
//				calibCorners[5] = 440; 
//				calibCorners[6] = 791; 
//				calibCorners[7] = 441; 
//				
//				
//				
//				///////////////////////////////////////////
//				///////////////////////////////////////////
				
				
				
				/*
				 * calibration object handled separately
				 * */
				try {
					new Calibration(displayImg.getWidth(),displayImg.getHeight());
				} catch (Exception e1) {
					System.err.println("\nERROR: BorderSelect.BorderSelect() -> new Calibration()");
					e1.getMessage();
					e1.printStackTrace();
				}
				
				dispose();
			}
		});
		

		GroupLayout groupLayout = new GroupLayout(getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(Alignment.LEADING, groupLayout.createSequentialGroup()
					.addGap(160)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(bL_button)
							.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
							.addComponent(bR_button))
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(tL_button)
							.addGap(214)
							.addComponent(tR_button)))
					.addContainerGap(221, Short.MAX_VALUE))
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap(389, Short.MAX_VALUE)
					.addComponent(exitCalibrate)
					.addGap(388))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(137)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(tL_button)
						.addComponent(tR_button))
					.addGap(46)
					.addComponent(exitCalibrate)
					.addGap(62)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(bL_button)
						.addComponent(bR_button))
					.addContainerGap(130, Short.MAX_VALUE))
		);
		getContentPane().setLayout(groupLayout);
		
		setKeyControls();
		this.requestFocus();
	}
	
	private void setCornerValue(MouseEvent event){
		int x = event.getX();
		int y = event.getY();
		
		if(cornerToSet == "TL") {
			calibCorners[0] = x;
			calibCorners[1] = y;
		}
		else if (cornerToSet == "TR") {
			calibCorners[2] = x;
			calibCorners[3] = y;
		}
		else if (cornerToSet == "BL") {
			calibCorners[4] = x;
			calibCorners[5] = y;
		}
		else if (cornerToSet == "BR") {
			calibCorners[6] = x;
			calibCorners[7] = y;
		}
		else {
			System.out.println("you need to select a border to set");
		}

//		System.out.println(cornerToSet+"\t->\t"+some);
		
		
//		return some;
	}//END setCornerValue()
	
	/**
	 * set key controls
	 * */
	private void setKeyControls() {
		
		Action esc = new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				System.out.println("exit calibrate");
				dispose();
				BLOOPRINT.blooprint.requestFocus();
				
			}
		};
		
		JPanel content = (JPanel) this.getContentPane();
		InputMap inputMap = content.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
	    
		/*----------------------------------------------------------------*/
		KeyStroke escape = KeyStroke.getKeyStroke("ESCAPE");		
	    inputMap.put(escape, "esc");
	    content.getActionMap().put("esc", esc);
	    /*----------------------------------------------------------------*/
		
	}//END setKeyControls()

	/**
	 * 
	 * */
	public class CornerImage extends JPanel{
		
		BufferedImage borderImg = null;
		int panelHeight = 0;
		int panelWidth = 0;

	    public CornerImage(BufferedImage img) {
	    	
	    	this.borderImg = img;
	    	this.panelHeight = this.getHeight();
	    	this.panelWidth = this.getWidth();
	    	
	    }//END CornerImage() contructor

	    @Override
	    public void paintComponent(Graphics g) {
	        super.paintComponent(g);
	        this.setBackground(Color.WHITE);
	        
	        g.drawImage(borderImg.getScaledInstance(this.getWidth(), -1, 0), 0, 0, null);
//	        g.drawImage(borderImg.getScaledInstance(-1, this.getHeight(), 0), 0, 0, null);
	        
	        
	        
	    }

	}//END CornerImage class
	
}
