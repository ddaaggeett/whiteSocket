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

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;

public class HelpMenu extends JFrame {

	private JPanel contentPane;


	/**
	 * Create the frame.
	 */
	public HelpMenu() {
		
		System.out.println("help being asked.......");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		this.setBounds(BLOOPRINT.getProjectorBounds());
		
		
		
		this.setTitle("Whiteboard Output");
		this.setUndecorated(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		
		
		this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		
		JTextArea textArea = new JTextArea();
		
		textArea.setText("\n\n\n\n\n\n"
				+"\t\tesc\t\tescape from text areas and this help menu\n\n"
				+"\t\tctrl + ENTER\t\tBLOOP\n"
				+"\t\tctrl + T\t\tBLIP (new text capture)\n"
				+"\t\tctrl + S\t\tSAVE all text\n"
				+"\t\tctrl + D\t\tDRAW mode set\n"
				+"\t\tctrl + E\t\tERASE mode set\n"
				+"\t\tctrl + O\t\tOPEN blooprint\n"
				+"\t\tctrl + N\t\tNEW blooprint\n"
				+"\t\tctrl + F\t\tCALIBRATE hardware");
		
		textArea.setEditable(false);
		
		BLOOPRINT.projectorDisplay(this);
		
		contentPane.add(textArea, BorderLayout.CENTER);
		
		setKeyControls();
		
	}

	/**
	 * set key control
	 * */
	private void setKeyControls() {
		
		Action esc = new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				System.out.println("exit HELP");
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

}
