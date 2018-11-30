package csci446_assignment3;

public class Cell{
    
    boolean wumpus = false, pit = false, gold = false;
    boolean stench = false, breeze = false, glitter = false;
    
    int row,column,time;
    
    public Cell(){
    }
    
    //constructor with row, column, and time
    public Cell(int r, int c, int t){
        this.row = r;
        this.column = c;
        this.time = t;
    }
}
