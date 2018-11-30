package csci446_assignment3;

import java.util.Hashtable;
import java.util.Map;
import org.jpl7.*;

import java.util.Random;

public class Main {
    public static void main(String[] args){
        Cell[][] maze = null;
        int mazeDimension = 0;
        if (args.length == 1) {
            switch (args[0]) {
                case "4x4":
                    maze = constructMaze(4);
                    mazeDimension = 4;
                    break;

                case "5x5":
                    maze = constructMaze(5);
                    mazeDimension = 5;
                    break;
                        
                case "8x8":
                    maze = constructMaze(8);
                    mazeDimension = 8;
                    break;
                  
                case "10x10":
                    maze = constructMaze(10);
                    mazeDimension = 10;
                    break;

                default:
                    throw new IllegalArgumentException("Must be 4x4, 5x5, 8x8, or 10x10");
            }
        }
        else {
            System.err.println("Usage: java assignment2.main DIMENSIONS");
        }
        
        if(maze != null){
            initialAssertions(mazeDimension);
            //starts at the start position at time 0
            int row = mazeDimension-1, column = 0, time = 0;
            perceive(maze, row, column);
            
            /*
            String safeFrontier = "nextMove("+row+","+column+",X2,Y2)";
            System.out.println(safeFrontier +" "+ Query.hasSolution(safeFrontier));
            */
            String t5 = "safeMove("+row+","+column+",X2,Y2)";
		Query q5 = new Query(t5);
		System.out.println("each solution of " + t5);
		while (q5.hasMoreSolutions()) {
			Map<String, Term> s5 = q5.nextSolution();
			System.out.println("X2 = " + s5.get("X2") + ", Y2 = " + s5.get("Y2"));
		}
            
            Cell nextMove = nextMove(row,column,time);
            //if a next move was found
            if(nextMove != null){
                //if the cell can be reached from current position
                if(nextMove.time == time){
                    //update current position and time
                    row = nextMove.row;
                    column = nextMove.column;
                    time++;
                    //assert the new move
                    String move = "assert((move("+row+","+column+","+time+")))";
                    Query.hasSolution(move);
                }
                //if the cell can't be reached from current position
                else if(nextMove.time != time){
                    //we need to backtrack
                    //asserts moves needed to make it to the new position
                    //returns an updated time
                    time = backtrack(time, nextMove.time);
                    row = nextMove.row;
                    column = nextMove.column;
                }
            }
        }
    }
    
    public static Cell[][] constructMaze(int dimension){
        
        //start out with a two dimensional array of cells with nothing in them
        Cell[][] result = new Cell[dimension][dimension];
        for(int i = 0; i < result.length; i++){
            for(int j = 0; j < result[0].length; j++){
                result[i][j] = new Cell();
            }
        }
        
        Random rand = new Random();
        
        //find a random spot to put the wumpus
        int wumpusRow = rand.nextInt(dimension);
        int wumpusColumn = rand.nextInt(dimension);
        //make sure it's not the starting cell
        while(wumpusRow==0&&wumpusColumn==dimension-1){
            wumpusRow = rand.nextInt(dimension);
            wumpusColumn = rand.nextInt(dimension);
        }
        result[wumpusRow][wumpusColumn].wumpus = true;
        System.out.println("place wumpus in ("+wumpusRow+", "+wumpusColumn+")");
        //add a stench to the surrounding cells
        if(wumpusRow-1>=0){
            result[wumpusRow-1][wumpusColumn].stench = true;
        }
        if(wumpusRow+1<dimension){
            result[wumpusRow+1][wumpusColumn].stench = true;
        }
        if(wumpusColumn-1>=0){
            result[wumpusRow][wumpusColumn-1].stench = true;
        }
        if(wumpusColumn+1<dimension){
            result[wumpusRow][wumpusColumn+1].stench = true;
        }
        
        //find a random cell
        int goldRow = rand.nextInt(dimension);
        int goldColumn = rand.nextInt(dimension);
        //put the gold in a cell that doesn't have the wumpus
        while(result[goldRow][goldColumn].wumpus){
            goldRow = rand.nextInt(dimension);
            goldColumn = rand.nextInt(dimension);
        }
        result[wumpusRow][wumpusColumn].gold = true;
        System.out.println("place gold in ("+goldRow+", "+goldColumn+")");
        //add a glitter to the cell
        result[wumpusRow][wumpusColumn].glitter = true;
        
        //add in the pits at 20% probability
        //go through each of the cells
        for(int i = 0; i < result.length; i++){
            for(int j = 0; j < result[0].length; j++){
                //if there's no gold and no wumpus in the cell and it's not the starting cell
                if(!result[i][j].gold&&!result[i][j].wumpus&&i!=0&&j!=dimension-1){
                    int pitProbability = rand.nextInt(100);
                    if(pitProbability<20){
                        //place a pit in the cell
                        result[i][j].pit = true;
                        System.out.println("place pit in ("+i+", "+j+")");
                        //add a breeze to the surrounding cells
                        if(i-1>=0){
                            result[i-1][j].breeze = true;
                        }
                        if(i+1<dimension){
                            result[i+1][j].breeze = true;
                        }
                        if(j-1>=0){
                            result[i][j-1].breeze = true;
                        }
                        if(j+1<dimension){
                            result[i][j+1].breeze = true;
                        }
                    }
                }
                //put every possible position into the knowledge base
                String assertPos = "assert(position("+i+","+j+"))";
                Query.hasSolution(assertPos);
            }
        }
        
        return result;
    }
    
    public static void initialAssertions(int dimension){
        //assert neighbors and what direction each neighbor is
        for(int i = 0; i < dimension; i++){
            for(int j = 0; j < dimension; j++){
                
                if(i-1>=0){
                    String assertNeighborLeft = "assert(neighborLeft("+i+","+j+","+(i-1)+","+j+"))";
                    Query.hasSolution(assertNeighborLeft);
                    String assertNeighbor = "assert(neighborOf("+i+","+j+","+(i-1)+","+j+"))";
                    Query.hasSolution(assertNeighbor);
                }
                String assertLeft = "assert(isLeftOf("+i+","+j+","+(i-1)+","+j+"))";
                Query.hasSolution(assertLeft);
                
                if(i+1<dimension){
                    String assertNeighborRight = "assert(neighborRight("+i+","+j+","+(i+1)+","+j+"))";
                    Query.hasSolution(assertNeighborRight);
                    String assertNeighbor = "assert(neighborOf("+i+","+j+","+(i+1)+","+j+"))";
                    Query.hasSolution(assertNeighbor);
                }
                String assertRight = "assert(isRightOf("+i+","+j+","+(i+1)+","+j+"))";
                Query.hasSolution(assertRight);
                
                if(j-1>=0){
                    String assertNeighborUp = "assert(neighborUp("+i+","+j+","+i+","+(j-1)+"))";
                    Query.hasSolution(assertNeighborUp);
                    String assertNeighbor = "assert(neighborOf("+i+","+j+","+i+","+(j-1)+"))";
                    Query.hasSolution(assertNeighbor);
                }
                String assertUp = "assert(isUpOf("+i+","+j+","+i+","+(j-1)+"))";
                Query.hasSolution(assertUp);
                
                if(j+1<dimension){
                    String assertNeighborDown = "assert(neighborDown("+i+","+j+","+i+","+(j+1)+"))";
                    Query.hasSolution(assertNeighborDown);
                    String assertNeighbor = "assert(neighborOf("+i+","+j+","+i+","+(j+1)+"))";
                    Query.hasSolution(assertNeighbor);
                }
                String assertDown = "assert(isDownOf("+i+","+j+","+i+","+(j+1)+"))";
                Query.hasSolution(assertDown);
            }
        }
        
        //a cell has a hazard if it either has a wumpus or has a pit
        String hasHazard = "assert((hasHazard(X,Y):-hasWumpus(X,Y);hasPit(X,Y)))";
        Query.hasSolution(hasHazard);
        
        //assert the corners
        String corner = "assert(("
                + "isCorner(X,Y):-"
                    + "((X=0),(Y=0));"
                    + "((X=0),(Y="+(dimension-1)+"));"
                    + "((X="+(dimension-1)+"),(Y=0));"
                    + "((X="+(dimension-1)+"),(Y="+(dimension-1)+"))"
                + "))";
        Query.hasSolution(corner);
        
        //a cell has a wumpus in it if all its neighbors have a stench
        String hasWumpus = "assert(("
                + "hasWumpus(X1,Y1):-"
                    //can't be a corner
                    + "(not(isCorner(X1,Y1))),"
                    //either is a neighbor and is a valid position and has a stench
                    + "(((isLeftOf(X1,Y1,X2,Y2)),(position(X2,Y2)),(hasStench(X2,Y2)));"
                    //or is a neighbor and isn't a valid position
                    + "((isLeftOf(X1,Y1,X2,Y2)),(not(position(X2,Y2))))),"
                
                    + "(((isRightOf(X1,Y1,X3,Y3)),(position(X3,Y3)),(hasStench(X3,Y3)));"
                    + "((isRightOf(X1,Y1,X3,Y3)),(not(position(X3,Y3))))),"
                    
                    + "(((isUpOf(X1,Y1,X4,Y4)),(position(X4,Y4)),(hasStench(X4,Y4)));"
                    + "((isUpOf(X1,Y1,X4,Y4)),(not(position(X4,Y4))))),"
                
                    + "(((isDownOf(X1,Y1,X5,Y5)),(position(X5,Y5)),(hasStench(X5,Y5)));"
                    + "((isDownOf(X1,Y1,X5,Y5)),(not(position(X5,Y5)))))"
                + "))";
        Query.hasSolution(hasWumpus);
        
        //a cell has a pit in it if all its neighbors have a breeze
        String hasPit = "assert(("
                + "hasPit(X1,Y1):-"
                    //can't be a corner
                    + "(not(isCorner(X1,Y1))),"
                    //either is a neighbor and is a valid position and has a breeze
                    + "(((isLeftOf(X1,Y1,X2,Y2)),(position(X2,Y2)),(hasBreeze(X2,Y2)));"
                    //or is a neighbor and isn't a valid position
                    + "((isLeftOf(X1,Y1,X2,Y2)),(not(position(X2,Y2))))),"
                
                    + "(((isRightOf(X1,Y1,X3,Y3)),(position(X3,Y3)),(hasBreeze(X3,Y3)));"
                    + "((isRightOf(X1,Y1,X3,Y3)),(not(position(X3,Y3))))),"
                    
                    + "(((isUpOf(X1,Y1,X4,Y4)),(position(X4,Y4)),(hasBreeze(X4,Y4)));"
                    + "((isUpOf(X1,Y1,X4,Y4)),(not(position(X4,Y4))))),"
                
                    + "(((isDownOf(X1,Y1,X5,Y5)),(position(X5,Y5)),(hasBreeze(X5,Y5)));"
                    + "((isDownOf(X1,Y1,X5,Y5)),(not(position(X5,Y5)))))"
                + "))";
        Query.hasSolution(hasPit);
        
        //a cell has the gold in it if the current cell has a glitter
        String hasGold = "assert(("
                + "hasGold(X,Y):-hasGlitter(X,Y)"
                + "))";
        Query.hasSolution(hasGold);
        
        //put dummy variables in an invalid position so the queries are defined initially
        String dummyStench = "assert(hasStench("+dimension+","+dimension+"))";
        Query.hasSolution(dummyStench);
        String dummyBreeze = "assert(hasBreeze("+dimension+","+dimension+"))";
        Query.hasSolution(dummyBreeze);
        String dummyGlitter = "assert(hasGlitter("+dimension+","+dimension+"))";
        Query.hasSolution(dummyGlitter);
        
        //move is not dangerous
        String dangerousToMove = "assert(("
                + "dangerousToMove(X,Y):-"
                    + "(hasStench(X,Y);"
                    + "hasBreeze(X,Y))"
                + "))";
        Query.hasSolution(dangerousToMove);
        
        //make first move
        String move = "assert((move("+(dimension-1)+",0,0)))";
        Query.hasSolution(move);
        
        //finds move guaranteed to be safe
        String safeMove = "assert(("
                + "safeMove(X1,Y1,X2,Y2):-"
                    //first an second position are different
                    + "dif(position(X1,Y1),position(X2,Y2)),"
                    //first position is a valid
                    + "position(X1,Y1),"
                    //second position is a valid
                    + "position(X2,Y2),"
                    //first and second cell are neighbors
                    + "neighborOf(X1,Y1,X2,Y2),"
                    //first cell has been moved to already
                    + "move(X1,Y1,T),"
                    //second cell has not been moved to already
                    + "not(move(X2,Y2,T)),"
                    //isn't dangerous to move from the first cell
                    + "not(dangerousToMove(X1,Y1))"
                + "))";
        Query.hasSolution(safeMove);
        
        //finds move on the frontier that might be dangerous
        String dangerousMove = "assert(("
                + "dangerousMove(X1,Y1,X2,Y2):-"
                    //first an second position are different
                    + "dif(position(X1,Y1),position(X2,Y2)),"
                    //first and second cell are neighbors
                    + "neighborOf(X1,Y1,X2,Y2),"
                    //first position is a valid
                    + "position(X1,Y1),"
                    //second position is a valid
                    + "position(X2,Y2),"
                    //first cell has been moved to already
                    + "move(X1,Y1,T),"
                    //second cell has not been moved to already
                    + "not(move(X2,Y2,T)),"
                    //it's dangerous to move from the first cell
                    + "dangerousToMove(X1,Y1),"
                    //there isn't a hazard there as far as we know
                    + "not(hasHazard(X2,Y2))"
                + "))";
        Query.hasSolution(dangerousMove);
        
        //takes a new position
        //returns an existing move adjacent to new position
        String existingMoveAdjacentToNewPosition = "assert(("
                + "existingMoveAdjacentToNewPosition(X1,Y1,X2,Y2):-"
                    //first an second position are different
                    + "dif(position(X1,Y1),position(X2,Y2)),"
                    //first position is a valid
                    + "position(X1,Y1),"
                    //second position is a valid
                    + "position(X2,Y2),"
                    //first position has been moved to at some point
                    + "move(X1,Y1,T),"
                    //second cell has not been moved to already
                    + "not(move(X2,Y2,T)),"
                    //first and second cell are neighbors
                    + "neighborOf(X1,Y1,X2,Y2)"
                + "))";
         Query.hasSolution(existingMoveAdjacentToNewPosition);
    }
    
    public static void perceive(Cell[][] maze, int row, int column){
        if(maze[row][column].breeze){
            System.out.println("current position hasBreeze");
            String hasBreeze = "assert(hasBreeze("+row+","+column+"))";
            Query.hasSolution(hasBreeze);
        }
        if(maze[row][column].stench){
            System.out.println("current position hasStench");
            String hasStench = "assert(hasStench("+row+","+column+"))";
            Query.hasSolution(hasStench);
        }
        if(maze[row][column].glitter){
            String hasGlitter = "assert(hasGlitter("+row+","+column+"))";
            Query.hasSolution(hasGlitter);
        }
    }
    
    //takes in current position and time
    //returns cell with the coordinates of new move
    //and time that the cell that was moved from was moved to
    public static Cell nextMove(int row1, int column1, int time1){
        Cell result = null;
        
        String safeAdjacent = "safeMove("+row1+","+column1+",X,Y)";
        String safeFrontier = "safeMove(X1,Y1,X2,Y2)";
        String dangerousAdjacent = "dangerousMove("+row1+","+column1+",X,Y)";
        String dangerousFrontier = "dangerousMove(X1,Y1,X2,Y2)";
        //if there's a safe move adjacent to current position
        if(Query.hasSolution(safeAdjacent)){
            System.out.println("There's a safe move adjacent to current position");
            //retrieve the position of the safe move
            Query adj = new Query(safeAdjacent);
            Map<String, Term> results = adj.oneSolution();
            int row2 = java.lang.Integer.parseInt(results.get("X").toString());
            int column2 = java.lang.Integer.parseInt(results.get("Y").toString());
            result = new Cell(row2,column2,time1);
        }
        //if there's a safe move on the frontier
        else if(Query.hasSolution(safeFrontier)){
            System.out.println("There's a safe move on the frontier");
            //retrieve the position of the safe move
            Query frontier = new Query(safeFrontier);
            Map<String, Term> results = frontier.oneSolution();
            //find the row and column of the new position
            int row2 = java.lang.Integer.parseInt(results.get("X2").toString());
            int column2 = java.lang.Integer.parseInt(results.get("Y2").toString());
            //find the time that a move adjacent to the new position was made
            int time2 = moveAtTime(row2,column2);
            //make a cell with the new position
            //and the time that the position adjacent to the new position was moved to
            result = new Cell(row2,column2,time2);
        }
        //if there's a dangerous move adjacent to current position
        else if(Query.hasSolution(dangerousAdjacent)){
            System.out.println("There's a dangerous move adjacent to current position");
            
        }
        //is there's a dangerous move on the frontier
        else if(Query.hasSolution(dangerousAdjacent)){
            System.out.println("There's a dangerous move on the frontier");
            
        }
        
        return result;
    }
    
    //takes in coordinate of second cell
    //returns the time that an adjacent first cell was moved to
    public static int moveAtTime(int row2, int column2){
        int time = -1;
        //finds an existing move adjacent to the new position
        String existingMoveAdjacentToNewPosition = "existingMoveAdjacentToNewPosition(X1,X2,"+row2+","+column2+")";
        if(Query.hasSolution(existingMoveAdjacentToNewPosition)){
            Query findCoordinate = new Query(existingMoveAdjacentToNewPosition);
            Map<String, Term> results = findCoordinate.oneSolution();
            //find the row and column of the existing move adjacent to the new position
            int row1 = java.lang.Integer.parseInt(results.get("X1").toString());
            int column1 = java.lang.Integer.parseInt(results.get("Y1").toString());
            //find the most recent time the move was made
            //by passing in the row and column of the existing move
            String timeTheMoveWasMade = "move("+row1+","+column1+",T)";
            if(Query.hasSolution(timeTheMoveWasMade)){
                Query findTime = new Query(timeTheMoveWasMade);
                Map<String, Term> timeResult = findTime.oneSolution();
                time = java.lang.Integer.parseInt(timeResult.get("T").toString());
            }
        }
        return time;
    }
    
    //HAS NOT BEEN TESTED YET
    //time1 is the current time
    //time2 is the time of the existing move that was made to the position that is adjacent to the new position
    //returns the updated time
    public static int backtrack(int t1, int t2){
        int time1 = t1;
        int time2 = t2;
        //keeps track of what the updated time will be
        int newCurrentTime = t1;
        //while we still aren't at the new position
        while(time1>time2){
            //find the coordinates of the next position
            //meaning the move that came before the move at time1
            String currentPos = "move(X,Y,"+(time1-1)+")";
            Query.hasSolution(currentPos);
            Query adj = new Query(currentPos);
            Map<String, Term> results = adj.oneSolution();
            int x1 = java.lang.Integer.parseInt(results.get("X").toString());
            int y1 = java.lang.Integer.parseInt(results.get("Y").toString());
            //assert a new move at the new current time, put move at top of results
            //also increment it by 1
            String newMove = "asserta((move("+x1+","+y1+","+(newCurrentTime++)+")))";
            Query.hasSolution(newMove);
            //decrement time1 so the next previous move can be found
            time1--;
        }
        return newCurrentTime;
    }
    
    public static void incrementTime(int t){
        t++;
    }
}
