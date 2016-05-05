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
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.InputMap;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;


/**
 * 
 * */
public class BLOOPRINT extends JFrame {

	/**
	 * TODO:
	 * system information
	 * */
	
	//	LINUX SYSTEM
	public static String homeDirectory = 			"/home/dave/Blooprint/";
	public static String sourceDir = 				"/home/dave/Blooprint/Blooprint.xyz/src/xyz/blooprint/";
	public static String sketchDir = 				homeDirectory+"in/";
	public static String blankImageFileName = 		homeDirectory+"blank.jpg";
	public static String rawCornersImageFileName = 	sketchDir+"rawCorners.jpg";
	public static String newTextImageFileName = 	sketchDir+"newText.jpg";
	public static String sketchImageFileName = 		sketchDir+"sketch.jpg";
	
	//	WINDOWS SYSTEM
	public static String win_homeDirectory = 			"C:/Users/david_000/coding/Blooprint.xyz/";
	public static String win_sourceDir =				win_homeDirectory+"src/xyz/blooprint/";
	public static String win_sketchDir = 				win_homeDirectory+"in/";
	public static String win_tmpDir = 					win_sketchDir+"tmp/";
	public static String win_blankImageFileName = 		win_homeDirectory+"blank.jpg";
	public static String win_rawCornersImageFileName = 	win_sketchDir+"rawCorners.jpg";
	public static String win_newTextImageFileName = 	win_sketchDir+"newText.jpg";
	public static String win_sketchImageFileName = 		win_sketchDir+"sketch.jpg";

	
	
	public static BLOOPRINT blooprint;//the current BLOOPRINT object on whiteboard
	
	public String title;//should always be UPPERCASE
	public static List<BLIP> blips;//loaded, edited, saved
	public BufferedImage image;//loaded, edited, saved
	public BufferedImage blank;
	public displayImagePanel displayImagePanel;//for displaying ^image
	
	public static Connection connx;
	
	static int[] borderValues = new int[4];
	public JComboBox<String> loadAvailable;
	public JTextField newTitle;
	private static final String DEFAULT_OPEN = "Open Blooprint";
	public String drawMode = "write";
	public static JTextArea startMessage;
	public static JProgressBar progressBar;

	
	/**
	 * user interacts with single blooprint instance
	 * */
	public static void main(String[] args) throws IOException {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				
				try {
					
					/*
					 * BLOOPRINT.XYZ: program start
					 * */
					
					/**	
					 * TODO:
					 * 'blooprint.xyz'.'_calibration' table needs to be created upon program installation
					 * last calibration used for this station
					 * 
					 * need to have at least one calibration data set in table for now
					 * */
					Calibration.loadCalibration();
					
					blooprint = new BLOOPRINT();	//	whiteboard display object
					
					blooprint.setExtendedState(JFrame.MAXIMIZED_BOTH);
					
					blooprint.setKeyControls();
					projectorDisplay(blooprint);
					blooprint.setVisible(true);
					
					//	Thread: prompt user to request help
					new OfferHelpThread();
					
					//	blooprint always needs focus for key listening
					blooprint.requestFocus();
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	/*
	 * BLOOPRINT object is the visual compilation of all user input
	 * */
	public BLOOPRINT() throws Exception{
		getContentPane().setBackground(Color.WHITE);
		
		/**
		 * BLOOPRINT object is instantaneous whiteboard display
		 * new blooprint = new frame
		 * */
		this.setBounds(getProjectorBounds());
		this.setTitle("Whiteboard Output");
		
		/**
		 * TODO:	set frame to be truly full screen (cover menu bar at top) -> linux causing difficulties
		 * */
		this.setExtendedState(JFrame.MAXIMIZED_BOTH);

		
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		/**
		 * undecided:
		 * should blank image come from a file or create each time
		 * */
//		this.displayImagePanel = new displayImagePanel(ImageIO.read(new File(blankImageFileName)));
		this.displayImagePanel = new displayImagePanel(makeBlankImage());
		
		this.setContentPane(displayImagePanel);//panel that contains the blooprint.image
		this.loadAvailable= new JComboBox<String>();//list of blooprints ready to load
		this.loadAvailable.addItem(DEFAULT_OPEN);//prompt user to select something to open
		this.blips = new ArrayList<BLIP>();//empty list of BLIP objects
		getContentPane().setLayout(null);
		loadAvailable.setBounds(12, 12, 150, 24);
		getContentPane().add(loadAvailable);
		loadAvailable.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					
					/*	this is just a working title to reference to the 
					 * */
					String workingBlooprint = "";
					
					if(!DEFAULT_OPEN.equals((String)loadAvailable.getSelectedItem())){
						workingBlooprint = (String)loadAvailable.getSelectedItem();
						workingBlooprint = workingBlooprint.toUpperCase();
						try {
							loadBlooprint(workingBlooprint);
						} catch (Exception ex) {
							// TODO Auto-generated catch block
							ex.printStackTrace();
						}
					}
					loadAvailable.setVisible(false);
					System.out.println("opening blooprint.............");
				}
			}			
		});
		
		try {
			setLoadables();
		} catch (Exception ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
		
		newTitle = new JTextField();
		newTitle.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					String workingBlooprint = newTitle.getText().toUpperCase();
					try {
						BLOOPRINT.addNewBPTables(workingBlooprint);
						loadBlooprint(workingBlooprint);
					} catch (Exception ex) {
						// TODO Auto-generated catch block
						ex.printStackTrace();
					}
					newTitle.setVisible(false);
					System.out.println("\nadding "+workingBlooprint+".............");
			    }
			}
		});
		
		newTitle.setBounds(196, 15, 150, 24);
		getContentPane().add(newTitle);
		newTitle.setColumns(10);
		newTitle.setText("New Title");
		
		//	TODO
		progressBar = new JProgressBar();
		progressBar.setBounds(this.getWidth()/3, this.getHeight()*2/3, this.getWidth()/3, 15);
		progressBar.setVisible(false);
		getContentPane().add(progressBar);
		
		//	opening message to alert potential new user of what to do
		//	all user interaction is self-explanatory after reading the help menu once
		startMessage = new JTextArea();
		startMessage.setBounds(this.getWidth()/3, this.getHeight()/3, 300, 50);
		startMessage.setEditable(false);
		startMessage.setVisible(false);
		startMessage.setText("OPEN or create NEW blooprint\n\nfor HELP -> ctrl H");
		getContentPane().add(startMessage);
		
		loadAvailable.setVisible(false);
		newTitle.setVisible(false);
		
	}//END BLooprint() constructor
	
	/**
	 * returns blank image for display
	 * */
	private static BufferedImage makeBlankImage() throws Exception {
		
		Double width = getProjectorBounds().getWidth();
		Double height = getProjectorBounds().getHeight();
		Integer w = width.intValue();
		Integer h = height.intValue();
		BufferedImage some = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics = some.createGraphics();
		graphics.setPaint ( new Color ( 255, 255, 255 ) );
		graphics.fillRect ( 0, 0, some.getWidth(), some.getHeight() );
		return some;
		
	}//END makeBlankImage()
	
	/**
	 * loads BLOOPRINT object for display
	 * */
	private void loadBlooprint(String workingBlooprint) throws Exception {
		/**
		 * new blooprint created each time: -user opens -user creates new
		 * previous blooprint is disposed and new blooprint is set up for interaction 
		 * */
		blooprint.dispose();
		blooprint = new BLOOPRINT();
		blooprint.setKeyControls();
		projectorDisplay(blooprint);
		blooprint.setVisible(true);
		blooprint.title = workingBlooprint;
		blooprint.image = blooprint.loadImage();
		displayImagePanel(blooprint.image);
		
		//	load saved BLIPS
		blooprint.blips = loadTextAreas();
		for (int x = 0; x < blooprint.blips.size(); x++){
			// loop through loaded list and displaying to blooprint one by one
			blooprint.blips.get(x).display();
		}
		
		/*
		 * attempted to request focus in constructor, but doesn't work
		 * */
		blooprint.requestFocus();
		
	}//END loadBlooprint()
	
	/**
	 * displayImagePanel contains panel of which blooprint.image is displayed on
	 * */
	public class displayImagePanel extends JPanel{
		
		public BufferedImage imageOUT;

	    public displayImagePanel(BufferedImage img) {
	    	
	    	this.imageOUT = img;
	    	
	    }//END displayImagePanel() constructor

	    @Override
	    public void paintComponent(Graphics g) {
	        super.paintComponent(g);
	        this.setBackground(Color.WHITE);
	        
	        //	TODO:	get better scale aspect ratio method
	        try{
	        	g.drawImage(imageOUT.getScaledInstance(this.getWidth(), -1, 0), 0, 0, null);
//	        	g.drawImage(imageOUT.getScaledInstance(-1, this.getHeight(), 0), 0, 0, null);
	        }
	        catch(Exception ex){
	        	System.out.println("\nERROR: Display Image\n");
	        	ex.getMessage();
	        	ex.printStackTrace();
	        }   
	    }
	    

	}//END displayImagePanel class
	
	/**
	 * loads the OPEN combobox -> ctrl + O 
	 * */
	private void setLoadables() throws Exception {
		loadAvailable.removeAll();
		Connection connx = null;
		Statement st = null;
		ResultSet rs = null;
		try{
			connx = BLOOPRINT.getDataBaseConnection();
			
			String query = "SELECT title FROM _blooprints";
			
			st = connx.createStatement();
			rs = st.executeQuery(query);
			
			//	TODO: needs to update after an addition and a deletion occurrence
			while(rs.next()){
				String name = rs.getString("title");
				loadAvailable.addItem(name);
			}
			
		}catch(Exception e){
			e.getMessage();
		}finally{
			
			connx.close();
			st.close();
			rs.close();
		}
		
	}//END setLoadables()
	
	/**
	 * ctrl + S -> saves text state of all BLIPS currently displayed 
	 * */
	public void saveTextAreas() throws Exception{
		
		Connection connx = getDataBaseConnection();
		
		for(int index = 0; index < blips.size(); index++){
			
			int id = blips.get(index).id;
			int x = blips.get(index).jTextArea.getBounds().x;
			int y = blips.get(index).jTextArea.getBounds().y;
			int width = blips.get(index).jTextArea.getBounds().width;
			int height = blips.get(index).jTextArea.getBounds().height;
			String textEntry = blips.get(index).jTextArea.getText();
			
			// scale to unit values
			float _x = (float)x / (float)blooprint.getWidth();
			float _y = (float)y / (float)blooprint.getHeight();
			float _width = (float)width / (float)blooprint.getWidth();
			float _height = (float)height / (float)blooprint.getHeight();
			
			try{
				/*	TODO:
				 * 
				 * if x AND y equal any of the table rows, update THOSE rows
				 * else create new row
				 * 
				 * */
				
				String cmd = "INSERT INTO "+blooprint.title.toUpperCase()+"_BLIPS (id,x,y,width,height,textEntry) "
						+"VALUES ("+id+","+_x+","+_y+","+_width+","+_height+",'"+textEntry+"') ON DUPLICATE KEY UPDATE "
						+"id = VALUES(id),"
						+"x = VALUES(x),"
						+"y = VALUES(y),"
						+"width = VALUES(width),"
						+"height = VALUES(height),"
						+"textEntry = VALUES(textEntry)";
						
				PreparedStatement statement = (PreparedStatement) connx.prepareStatement(cmd);				
				statement.executeUpdate();
				
			}catch(Exception ex){
				System.out.println("\nERROR:\nBLOOPRINT.saveTextAreas()"+ex.getMessage());
				ex.printStackTrace();
			}
		}
		
		connx.close();
		
	}//END saveTextAreas()
	
	/**
	 * loads BLIPS from DB into blooprint.blips object
	 * object data may be changed per user
	 * List<BLIP> blips can be appended, deleted, saved during runtime
	 * */
	private List<BLIP> loadTextAreas() throws Exception {
		
		List<BLIP> some = new ArrayList<BLIP>();
		
		try{
			Connection connx = getDataBaseConnection();
			Statement statement = (Statement)connx.createStatement();
			
			int id;
			float x;
			float y;
			float width;
			float height;
			String text;
			
			String cmd = "SELECT * FROM " +blooprint.title.toUpperCase()+"_BLIPS";
			
			ResultSet result = statement.executeQuery(cmd);
			
			while(result.next()){
				id = result.getInt("id");
				x = result.getFloat("x");
				y = result.getFloat("y");
				width = result.getFloat("width");
				height = result.getFloat("height");
				text = result.getString("textEntry");
				
				// scale from unit values
				int _x = (int)Math.round(x * blooprint.getWidth());
				int _y = (int)Math.round(y * blooprint.getHeight());
				int _width = (int)Math.round(width * blooprint.getWidth());
				int _height = (int)Math.round(height * blooprint.getHeight());
				
				Rectangle box = new Rectangle(_x, _y, _width, _height);
				
				BLIP textArea = new BLIP(id, box, text);
				
				some.add(textArea);
				
			}
			
			connx.close();
			
		}catch(Exception exc){
			exc.getMessage();
		}
		
		return some;
		
	}//END loadTextAreas()
	
	/**
	 * blooprint.image is updated for display on blooprint.displayImagePanel
	 * */
	public static void displayImagePanel(BufferedImage img) {
		try{
			blooprint.displayImagePanel.imageOUT = img;
			blooprint.displayImagePanel.revalidate();
			blooprint.displayImagePanel.repaint();
		}catch(Exception e){
			System.err.println("ERROR:\nBLOOPRINT.displayImagePanel()");
			e.printStackTrace();
		}
	}//END displayImagePanel()
	
	/**
	 * ctrl + ENTER -> bloop action
	 * DB table is updated with added image of latest blooprint.image state
	 * */
	public void saveImage() throws IOException {
		/*
		 * BufferedImage object to BLOB object
		 * */
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(blooprint.image, "jpg", baos);
		InputStream is = new ByteArrayInputStream(baos.toByteArray());
		
		try{
			
			Connection connx = getDataBaseConnection();
			String cmd = "insert into "+blooprint.title.toUpperCase()+"_BLOOPS (image) values (?)";
			PreparedStatement statement = (PreparedStatement) connx.prepareStatement(cmd);
			statement.setBlob(1, is);
			statement.executeUpdate();
			connx.close();
			
		}catch(Exception ex){
			System.out.println(ex.getMessage());
		}
		
		
	}//END saveImage()
	
	/**
	 * load blooprint.image from DB table
	 * BLOB object to binary stream to BufferedImage object
	 * */
	public BufferedImage loadImage() throws Exception {
		/*
		 * BLOB object to BufferedImage object
		 * */		
		BufferedImage image = null;
		
		try{
			Connection connx = getDataBaseConnection();
			Statement statement = connx.createStatement();
			
			InputStream is = null;
			
			String cmd = "SELECT image FROM "+blooprint.title.toUpperCase()+"_BLOOPS ORDER BY id DESC LIMIT 1";
			
			ResultSet result = statement.executeQuery(cmd);
			
			if(result.next()){
				is = result.getBinaryStream("image");
				
				image = ImageIO.read(is);
			}
			
			connx.close();
			
		}catch(Exception exc){
			exc.getMessage();
		}
		
		return image;
	}//END loadImage()
	
	/**
	 * new DB tables must be added to DB every time a new blooprint is created
	 * 
	 * TODO: verify no other blooprints of new title exist, and if so, give user option to save as new name, OR ADDED PAGE
	 * */
	public static void addNewBPTables(String name) throws Exception {
		
//		if(!loadable.contains(name)){
			
			try{
				Connection connx = getDataBaseConnection();
				
				Statement statement = connx.createStatement();
				
				String cmd = "create table "+name.toUpperCase()+"_BLOOPS ( "
					      + "id int PRIMARY KEY AUTO_INCREMENT,"
					      + "image longblob)";

				statement.executeUpdate(cmd);
				
				cmd = "create table "+name.toUpperCase()+"_BLIPS ("
						+ "id int NOT NULL PRIMARY KEY AUTO_INCREMENT,"
						+ "x float NOT NULL,"
						+ "y float NOT NULL,"
						+ "width float NOT NULL,"
						+ "height float NOT NULL,"
						+ "textEntry TEXT)";
				
				statement.executeUpdate(cmd);
				
			    
				
				System.out.println(name+"_BLOOPS + "+name+"_BLIPS created");
			    
			    /*
			     * set first image in blooprint to blank image
			     * TODO: set correct aspect ratio
			     * */
			    cmd = "INSERT INTO "+name.toUpperCase()+"_BLOOPS (image) VALUES (?)";
			    File theFile = new File(blankImageFileName);
			    FileInputStream data = new FileInputStream(theFile);
			    PreparedStatement state = (PreparedStatement) connx.prepareStatement(cmd);
				state.setBlob(1, data);
				state.executeUpdate();
			    
			    connx.close();
			    System.out.println("mysql connection closed");
				
			}catch(Exception e1){
				
				e1.getMessage();
			}
			
			
			try{
				
				Connection connx = getDataBaseConnection();						
				
				String cmd = "INSERT INTO _blooprints (title) VALUES (?)";
				
				// create the mysql insert preparedstatement
				PreparedStatement preparedStmt = (PreparedStatement)connx.prepareStatement(cmd);
				preparedStmt.setString (1, name);
				
				preparedStmt.execute();
				
				connx.close();
				
				System.out.println("mysql connection closed");
				
			}catch(Exception e1){
				
				e1.getMessage();
				
			}
//		}
//		else{
//			/*
//			 * TODO:	-if blooprint already exists
//			 * 			-save options
//			 * */
//			System.out.println("TODO......");
//		}
		
		
		
	}//END addNewBPTables()
	
	/**
	 * connect to MySQL DB
	 * TODO: setup text file for script to load DB access info
	 * adjust accordingly	
	 * */
	public static Connection getDataBaseConnection() throws Exception{
		try{

			/**
			* TODO:
			* make sure to download JAR
			* http://dev.mysql.com/downloads/connector/j/
			*/
			Class.forName("com.mysql.jdbc.Driver");
			String url = "jdbc:mysql://127.0.0.1:3306/blooprint.xyz";
			String username = "root";
			String password = "password";
			Connection c = DriverManager.getConnection(url,username,password);
			return c;// if connection worked
		}catch(Exception e){
			System.out.println("ERROR:\nBLOOPRINT.getDataBaseConnection()");
			e.printStackTrace();
		}
		return null; //if connection not made
	}//END getDataBaseConnection()
	
	/**
	 * enables user to interact with program by keyboard controls
	 * */
	protected void setKeyControls() {
		
		Action bloopAction = new AbstractAction() {
		      public void actionPerformed(ActionEvent actionEvent) {
		    	
		    	  
		    	  try {
						blooprint.saveTextAreas();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						System.err.println("\nERROR:\nBLOOPRINT.saveTextAreas()\n"+e);
					}
		    	  
		    	  
		    	  System.out.println("\nBLOOP\n");
					
		    	  
		    		try {
						new BLOOP(drawMode, false, false);
						
						
						System.out.println("\ndone with bloop\n");
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
		    		
		      }
		};

		Action blipAction = new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				System.out.println("\nBLIP\n");
				
				try {
					new BLOOP(null,false,true);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		};
		
		Action saveAction = new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				System.out.println("saving blips..........");
				
				try {
					blooprint.saveTextAreas();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					System.err.println("\nERROR:\nBLOOPRINT.saveTextAreas()\n"+e);
				}
			}
		};

		Action drawAction = new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				System.out.println("write mode set");
				drawMode = "write";
				
			}
		};
		
		Action eraseAction = new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				System.out.println("erase mode set");
				drawMode = "erase";
				
			}
		};
		
		Action openAction = new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				newTitle.setVisible(false);
				
				loadAvailable.setVisible(true);
				loadAvailable.requestFocus();
				
			}
		};

		Action calibrateAction = new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					new BLOOP(null, true, false);
				} catch (Exception ex) {
					// TODO Auto-generated catch block
					ex.printStackTrace();
				}
			}
		};

		Action newBlooprintAction = new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				loadAvailable.setVisible(false);
				
				newTitle.setVisible(true);
				newTitle.requestFocus();
				
			}
		};

		Action escapeAction = new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				
				newTitle.setVisible(false);
				loadAvailable.setVisible(false);
				
				System.out.println("ESCAPE");
				blooprint.requestFocus();
				
			}
		};
		
		Action helpAction = new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				
				HelpMenu help = new HelpMenu();
				help.setVisible(true);
				
			}
		};
		
		JPanel content = (JPanel) blooprint.getContentPane();
		InputMap inputMap = content.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
	    
		/*----------------------------------------------------------------*/
		KeyStroke ctrlSpace = KeyStroke.getKeyStroke("control ENTER");		
	    inputMap.put(ctrlSpace, "BLOOP");
	    content.getActionMap().put("BLOOP", bloopAction);
	    /*----------------------------------------------------------------*/
		KeyStroke ctrlT = KeyStroke.getKeyStroke("control T");		
	    inputMap.put(ctrlT, "NEWTEXT");
		content.getActionMap().put("NEWTEXT", blipAction);
		/*----------------------------------------------------------------*/
		KeyStroke ctrlS = KeyStroke.getKeyStroke("control S");		
	    inputMap.put(ctrlS, "SAVETEXT");
		content.getActionMap().put("SAVETEXT", saveAction);
		/*----------------------------------------------------------------*/
		KeyStroke ctrlD = KeyStroke.getKeyStroke("control D");		
	    inputMap.put(ctrlD, "DRAW");
		content.getActionMap().put("DRAW", drawAction);
		/*----------------------------------------------------------------*/
		KeyStroke ctrlE = KeyStroke.getKeyStroke("control E");		
	    inputMap.put(ctrlE, "ERASE");
		content.getActionMap().put("ERASE", eraseAction);
		/*----------------------------------------------------------------*/
		KeyStroke ctrlO = KeyStroke.getKeyStroke("control O");		
	    inputMap.put(ctrlO, "OPEN");
		content.getActionMap().put("OPEN", openAction);
		/*----------------------------------------------------------------*/
		KeyStroke ctrlF = KeyStroke.getKeyStroke("control F");		
	    inputMap.put(ctrlF, "CALIBRATE");
		content.getActionMap().put("CALIBRATE", calibrateAction);
		/*----------------------------------------------------------------*/
		KeyStroke ctrlN = KeyStroke.getKeyStroke("control N");		
	    inputMap.put(ctrlN, "NEWBLOOPRINT");
		content.getActionMap().put("NEWBLOOPRINT", newBlooprintAction);
		/*----------------------------------------------------------------*/
		KeyStroke esc = KeyStroke.getKeyStroke("ESCAPE");		
	    inputMap.put(esc, "ESCAPE");
		content.getActionMap().put("ESCAPE", escapeAction);
		/*----------------------------------------------------------------*/
		KeyStroke ctrlH = KeyStroke.getKeyStroke("control H");		
	    inputMap.put(ctrlH, "HELP");
		content.getActionMap().put("HELP", helpAction);
		/*----------------------------------------------------------------*/
		
		
		
		
		
		
	}//END setKeyControls()
	
	/**
	 * display entire program video output through projector 
	 * */
	public static void projectorDisplay(JFrame frame) {
		/**
		 * change int to projector monitor
		 * */
		int screen = 1;
		
		/*
		 * index which screen to display on
		 * */
	    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
	    GraphicsDevice[] gd = ge.getScreenDevices();
	    if( screen > -1 && screen < gd.length ) {
	        frame.setLocation(gd[screen].getDefaultConfiguration().getBounds().x, frame.getY());
	    }
	    else if( gd.length > 0 ) {
//	    	frame.setBounds(200, 50, 500, 300);
	        frame.setLocation(gd[0].getDefaultConfiguration().getBounds().x, frame.getY());
	    }
	    else {
	        throw new RuntimeException( "No Screens Found" );
	    }
	    
	    try{
	    	
	    	blooprint.setUndecorated(true);
	    }
	    catch(Exception e){
	    	e.getMessage();
	    	e.printStackTrace();
	    }
//	    blooprint.setAlwaysOnTop(true);
//		blooprint.setSize(java.awt.Toolkit.getDefaultToolkit().getScreenSize());
//		blooprint.setResizable(false);
		
	    
	    
	}//END projectorDisplay()
	
	/**
	 * for maximizing output display to limits of projector
	 * 
	 *  see this.setExtendedState(JFrame.MAXIMIZED_BOTH); in main method
	 * */
	public static Rectangle getProjectorBounds() {
		
		Rectangle some = new Rectangle();

		/*
		 * TODO:
		 * set rectangle to bounds of projector
		 * */
		
		/*---delete this--------------*/
		some.setBounds(50, 50, 800, 450);
		/*----------------------------*/
		
		return some;
	}
	
	/**
	 * progress of every bloop action
	 * start:	bloop action -> ctrl + ENTER
	 * end:		BLOOPRINT.displayImagePanel.imageOUT display
	 * */
	public static class bloopProgress implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			
		}
		
		
		
		
	}

	/**
	 * sets the shell commands according to the operating system used
	 * */
	public static String[] prepCmds(String cmd){
		
		String system_os = System.getProperty("os.name").toLowerCase();
		
		if(system_os.contains("windows")){
			
//			String[] some = {"cmd.exe","/c", cmd};
			String[] some = {"powershell.exe","/c", cmd};// using PowerShell here
			return some;
			
		}
		
		/**
		 * we're taking advantage of the PowerShell offered in windows so that we don't have to alter any from linux
		 * Linux > Windows* > MacOSX ;)
		 * */
		else{
			String[] some = {"/bin/bash","-c", cmd};
			return some;
		}
			
	}//END prepCmds()
	
	/**
	 * method for running custom command
	 * */
	public static List<String> command(String cmd) throws IOException{
		
		List<String> some = new ArrayList<String>();
		
		Runtime rt = Runtime.getRuntime();
		String[] commands = prepCmds(cmd);
		Process proc = rt.exec(commands);

		BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
		BufferedReader stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
		
		// read the output from the command
		String line = null;
		
		while ((line = stdInput.readLine()) != null) {
		    
			some.add(line);
//			System.out.println(line);
		    
		}
		
//		// read any errors from the attempted command
//		System.out.println("Here is the standard error of the command (if any):\n");
//		while ((s = stdError.readLine()) != null) {
//		    System.out.println(s);
//		}
		
		return some;

	}//END command()

	/**
	 * FileTrigger class is used for checking when a file is transferred to specific file
	 * used to  
	 * */
	public static class FileTrigger implements Runnable{
		
		Thread watchThread;
		Path directory = Paths.get(win_tmpDir);
		
		@Override
		public void run() {
			
			System.out.println("checking for file change..........");
			
			// TODO Auto-generated method stub
			try {
				checkChange();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		public FileTrigger(){
			watchThread = new Thread(this);
			watchThread.start();
		}
		
		private void checkChange() throws IOException{
			
			WatchService watchService = FileSystems.getDefault().newWatchService();
			WatchKey watchKey = directory.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);
			
			boolean flag = true;
			while(flag){
				for(WatchEvent<?> event : watchKey.pollEvents()){
					System.out.println(event.kind());
					
					String some = event.kind().toString();
					if(some.toUpperCase() == "ENTRY_CREATE"){
						
						
						flag = false;
					}
				}
			}
			
			
			
		}//END checkChange() method
		
	}//END FileTrigger class
	
	/**
	 * thread for running timed text display prompting user to open, create new, ask for help
	 * */
	public static class OfferHelpThread implements Runnable{
		
		Thread offerHelpThread;
		
		
		public OfferHelpThread() {
			offerHelpThread=  new Thread(this);
			offerHelpThread.start();
			
		}

		@Override
		public void run() {
			try {
				offerHelp();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		private void offerHelp() throws InterruptedException {
			
			/*
			 * TODO:
			 * turn into single JTextArea
			 * */
			startMessage.setVisible(true);
			Thread.sleep(4000);
			startMessage.setVisible(false);
			System.out.println("help has been offered");
		}
		
		
		
		
	}//END OfferHelpThread class
	
	

}





