package apaplqueens;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
 
import javax.swing.JFileChooser;
import javax.swing.JFrame; 
import javax.swing.JOptionPane;
 

import core.GameLogic;
import GUI.QueensGUI;
import apapl.Environment;
import apapl.ExternalActionFailedException;
import apapl.data.APLIdent;
import apapl.data.APLList;
import apapl.data.APLNum;
import apapl.data.Term; 

public class APAPLInterface extends Environment {
	private GameLogic gl = null;												
	private HashMap<String, Integer> agents = new HashMap<String, Integer>();	
	private HashMap<Integer, String> agents2 = new HashMap<Integer, String>();	// Registered agents
	private static Term YES = new APLIdent("yes"), NO = new APLIdent("no");		
	private boolean show_gui = true;											// Can turn off GUI
	private QueensGUI g = null; 												
	private int move_delay = 500;
	private String solutions_folder = "./resources/solutions/";					
	
	public APAPLInterface(){  
		String s = (String)JOptionPane.showInputDialog(null,"Number of queens:","N-Queens puzzle",JOptionPane.PLAIN_MESSAGE,null,null,"8");
		int i = Integer.parseInt(s);											
		gl = new GameLogic(i);													
		if(show_gui){															
			g = new QueensGUI(gl, 30);											// Create GUI and set FPS
			JFrame f = new JFrame();											// If you want to change the interface then you want to customize this frame
			g.setPreferredSize(new Dimension(720, 720));						
			f.add(g);	
			f.addWindowListener(new StopTheAnimation());
			f.pack();
			f.setVisible(true);
			g.start_animation();												// Starts the animation thread
		} 
	}
	
	class StopTheAnimation extends WindowAdapter {
		  public void windowClosing(WindowEvent evt){g.stop_animation();} 		
	}

	
	
	protected void addAgent(String agName) {
		if(agents.size()<gl.get_board().length){				
			agents.put(agName,agents.size()); 					
			agents2.put(agents.size()-1, agName);
		}
	}  
	

	public static void main(String [] args) { } 
	
	
	public Term move(String agName, APLIdent dir) throws ExternalActionFailedException {
		Integer agent = agents.get(agName);											
		if(agent!=null){															
			boolean success = false;
			if(dir.getName().equals("up")) success = gl.move(agent, true);			
			else if(dir.getName().equals("down")) success = gl.move(agent, false);	
			else throw new ExternalActionFailedException("Not a valid move direction: "+dir.getName()); 
			if(show_gui&&move_delay>0){
				try{Thread.sleep(move_delay);}catch(Exception e){}					
			}
			if(success) return YES;						
			else throw new ExternalActionFailedException("Could not move.");
		} else  throw new ExternalActionFailedException("Agent not registered."); 
	}
	
	
	public Term perceive(String agName) throws ExternalActionFailedException {
		Integer agent = agents.get(agName);									
		LinkedList<Term> terms = new LinkedList<Term>(); 					
		if(agent!=null){
			terms.add(new APLNum(agent));									
			for(int q = 0; q < gl.get_board().length; q++){					
				terms.add(new APLIdent(agents2.get(q)));
				terms.add(new APLNum(gl.get_board()[q]));
			}
		}
		return new APLList(terms);										
	}
	
	
	public Term finished(String agName) throws ExternalActionFailedException {
		Integer agent = agents.get(agName);												
		if(agent!=null){																
			if(gl.is_solution()){														
				if(show_gui){															
					try{Thread.sleep(500);}catch(Exception e){}							
					File f = new File(solutions_folder);
					if(!f.exists()){													
						JFileChooser jfc = new JFileChooser(".");						
						jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);		
						jfc.setAcceptAllFileFilterUsed(false);
						jfc.setDialogTitle("Choose icon directory");
						jfc.setSelectedFile(f); 										
						if(jfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) 	
							solutions_folder = jfc.getSelectedFile().getPath()+"/"; 	
					}
					g.saveImage(solutions_folder+"/sol"+gl.get_solution_count());		
				}
				return YES;
			} else return NO; 
		} else  throw new ExternalActionFailedException("Agent not registered."); 		
	}

	
	public Term set_move_delay(String agName, APLNum amount) throws ExternalActionFailedException {
		Integer agent = agents.get(agName);											
		if(agent!=null){															
			move_delay = amount.getVal().intValue();
			return YES;
		} else  throw new ExternalActionFailedException("Agent not registered.");	
	}
		
    
	public Term set_show_gui(String agName, APLIdent value) throws ExternalActionFailedException {
		Integer agent = agents.get(agName);
		if(agent!=null){					
			show_gui = value.getName().equalsIgnoreCase("yes");
			return YES;
		} else  throw new ExternalActionFailedException("Agent not registered."); 
	}
} 