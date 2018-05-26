package GUI;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
 
import core.GameLogic;

/**
 * Graphical interface for the queens environment. 
 *
 */
public class QueensGUI extends JPanel{
	private GameLogic gl = null;									
	private String icon_dir = "./resources/icons/";					
	private BufferedImage queen = null;								
	private Font font = new Font( Font.SANS_SERIF,Font.BOLD, 20); 	
	private FrameCaller frame_caller = null;						
	private Thread frame_caller_thread = null;						// Thread that holds the runnable
	private int[] queens = null; 									
	private int cell_size = 0;										
	
	public QueensGUI(GameLogic gl, int fps){
		this.gl = gl;
		File f = new File(icon_dir);
		if(!f.exists()){													
			JFileChooser jfc = new JFileChooser(".");						
			jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);		
			jfc.setAcceptAllFileFilterUsed(false);
			jfc.setDialogTitle("Choose icon directory");
			jfc.setSelectedFile(f); 										
			if(jfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) 	
				icon_dir = jfc.getSelectedFile().getPath()+"/"; 			
		}
		queen = getIcon(icon_dir+"queen2.png");								
		queens = new int[gl.get_board().length];							
		frame_caller = new FrameCaller(this, fps);							
		cell_size = queen.getHeight();
	}
	
	
	public void start_animation(){
		frame_caller.setHalt(false);					// Will prevent the run-loop from halting
		frame_caller_thread = new Thread(frame_caller); // Create new thread
		frame_caller_thread.start();					// Start it
	}
	
	
	public void stop_animation(){
		frame_caller.setHalt(true); 					// Will stop the current run-loop
	}
	
	
	public void frame_update(){
		for(int nr = 0; nr<queens.length; nr++){
			queens[nr] = queens[nr] + (int)((gl.get_board()[nr]*cell_size-queens[nr])*0.2d); 
		}
	}
	
	public void paint(Graphics gr){
		Graphics2D g = (Graphics2D) gr;
		g.setBackground(Color.white); 
		g.clearRect(0, 0,getWidth(), getHeight());
		g.addRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING,					// Anti-alias looks pretty//hata do
				RenderingHints.VALUE_ANTIALIAS_ON));
		
		
		int z = gl.get_board().length;
		boolean dark = true;
		boolean dark2 = true;
		for(int y = 1; y <=z; y++){
			for(int x = 1; x <=z; x++){
				if(dark) g.setColor(Color.lightGray);
				else g.setColor(Color.white);
				g.fillRect(x*cell_size, y*cell_size, cell_size, cell_size);
				dark = !dark;
			}
			dark2 = !dark2;
			dark = dark2;
		}
		g.setColor(Color.black);
		for(int y = 1; y <= z+1; y++)
			g.drawLine(cell_size, y*cell_size, (z+1)*cell_size, y*cell_size); 			  	
		for(int x = 1; x <= z+1; x++)
			g.drawLine(x*cell_size,cell_size,x*cell_size,(z+1)*cell_size);					
		
		
		for(int i = 0; i < queens.length; i++)
			g.drawImage(queen, (i+1)*cell_size, queens[i]+cell_size, queen.getWidth(), queen.getHeight(), null);
		
		// Draw title
		g.setFont(font);
		g.drawString("N-Queens world. Found solutions: "+gl.get_solution_count(), cell_size, cell_size-3);
	}
	
	
	public void saveImage(String name){
		File file = new File(name+".png");
		try{
			if (!file.exists())file.createNewFile();
			BufferedImage image = new Robot().createScreenCapture(new Rectangle(getLocationOnScreen().x, getLocationOnScreen().y, getWidth(), getHeight()));
			ImageIO.write(image, "png", file);
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public BufferedImage getIcon(String icon_name){
		try{ return ImageIO.read(new File(icon_name));
		} catch(Exception e){ System.out.println(icon_name);e.printStackTrace(); return null; }
	}

	
	private class FrameCaller implements Runnable {
		QueensGUI panel; 		
		int fps;		 		
		boolean halt = false;   
		
		
		public FrameCaller(QueensGUI panel, int fps){
			this.panel = panel; 
			this.fps = fps;
		}
		
		public void run(){
			try{
				while(!halt){
					Thread.sleep(1000/fps); // Sleep a bit
					panel.frame_update();   // Update all data
					panel.repaint();		// Repaint the state
				}
			} catch(Exception e){ e.printStackTrace(); }
		}
		
		
		public void setHalt(boolean b){
			halt = b;
		}
	}
}
