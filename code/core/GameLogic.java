package core;

public class GameLogic {
	private int[] board = null; 		
	private int solution_counter = 0; 	
	
	public GameLogic(){}
	public GameLogic(int nrQueens){ init(nrQueens); }
	
		public void init(int nrQueens){
		board = new int[nrQueens];
		solution_counter = 0;
	}
	
	public boolean move(int queen, boolean up){ 
		if(up&&board[queen]>=1) board[queen]--;							
		else if(!up&&board[queen]<(board.length-1)) board[queen]++;		// Board is square cannot move beyond it
		else return false;												// Was illegal move
		return true;													
	}
	
	
	public boolean is_solution(){
		for(int q1 = 0; q1 < board.length; q1++)
			for(int q2 = q1+1; q2 < board.length; q2++)
				if(board[q1] == board[q2] || (Math.abs(board[q1]-board[q2]) == q2-q1))
					return false; 
		solution_counter++;
		return true;
	}  
	
	
	public int get_solution_count(){
		return solution_counter;
	}
	
	
	public int[] get_board(){
		return board;
	}
}
