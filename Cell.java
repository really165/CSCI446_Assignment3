package csci446_assignment3;

public class Cell{
    
    boolean wumpus = false, pit = false, gold = false;
    boolean stench = false, breeze = false, glitter = false;
    
    int row,column,time;
    Cell[][] maze = null;
    
    public Cell(){
    }
    
    //constructor with row, column, and time
    public Cell(int r, int c, int t){
        this.row = r;
        this.column = c;
        this.time = t;
    }
    
    //constructor with row, column, time, and maze
    public Cell(int r, int c, int t, Cell[][] m){
        this.row = r;
        this.column = c;
        this.time = t;
        this.maze = m;
    }
}
