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
            int row = 4, column = 0;
            perceive(maze, row, column);
            
            String safeFrontier = "safeFrontier("+row+","+column+",X2,Y2)";
            System.out.println(safeFrontier +" "+ Query.hasSolution(safeFrontier));
            /*
            String t5 = "safeMove("+row+","+column+",X2,Y2)";
		Query q5 = new Query(t5);
		System.out.println("each solution of " + t5);
		while (q5.hasMoreSolutions()) {
			Map<String, Term> s5 = q5.nextSolution();
			System.out.println("X2 = " + s5.get("X2") + ", Y2 = " + s5.get("Y2"));
		}
            */
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
        
        String safeMove = "assert(("
                + "safeMove(X1,Y1,X2,Y2):-"
                    //the two cells aren't the same
                    + "dif(position(X1,Y1),position(X2,Y2)),"
                    //isn't close to a hazard
                    + "not(dangerousToMove(X1,Y1)),"
                    //is a neighbor
                    + "neighborOf(X1,Y1,X2,Y2),"
                    //is a valid position
                    + "position(X2,Y2)"
                + "))";
        Query.hasSolution(safeMove);
        
        //make first move
        String move = "assert((move("+(dimension-1)+",0,0)))";
        Query.hasSolution(move);
        
        String safeFrontier = "assert(("
                + "safeFrontier(X1,Y1,X2,Y2):-"
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
        Query.hasSolution(safeFrontier);
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
}
