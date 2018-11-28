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
            int x = 0, y = mazeDimension-1;
            
            String t5 = "neighborOf(0,0,X,Y)";
		Query q5 = new Query(t5);
		System.out.println("each solution of " + t5);
		while (q5.hasMoreSolutions()) {
			Map<String, Term> s5 = q5.nextSolution();
			System.out.println("(" + s5.get("X") + ", " + s5.get("Y") + ")");
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
        for(int i = 0; i < dimension-1; i++){
            for(int j = 0; j < dimension-1; j++){
                
                if(i-1>=0){
                    String assertNeighborLeft = "assert(neighborOf("+i+","+j+","+(i-1)+","+j+"))";
                    Query.hasSolution(assertNeighborLeft);
                }
                String assertLeft = "assert(isLeftOf("+i+","+j+","+(i-1)+","+j+"))";
                Query.hasSolution(assertLeft);
                
                if(i+1<dimension){
                    String assertNeighborRight = "assert(neighborOf("+i+","+j+","+(i+1)+","+j+"))";
                    Query.hasSolution(assertNeighborRight);
                }
                String assertRight = "assert(isRightOf("+i+","+j+","+(i+1)+","+j+"))";
                Query.hasSolution(assertRight);
                
                if(j-1>=0){
                    String assertNeighborUp = "assert(neighborOf("+i+","+j+","+i+","+(j-1)+"))";
                    Query.hasSolution(assertNeighborUp);
                }
                String assertUp = "assert(isUpOf("+i+","+j+","+i+","+(j-1)+"))";
                Query.hasSolution(assertUp);
                
                if(j+1<dimension){
                    String assertNeighborDown = "assert(neighborOf("+i+","+j+","+i+","+(j+1)+"))";
                    Query.hasSolution(assertNeighborDown);
                }
                String assertDown = "assert(isDownOf("+i+","+j+","+i+","+(j+1)+"))";
                Query.hasSolution(assertDown);
            }
        }
        
        //make some rules
        //a cell has a hazard if it either has a wumpus or has a pit
        String hasHazard = "assert((hasHazard(X,Y):-hasWumpus(X,Y);hasPit(X,Y)))";
        Query.hasSolution(hasHazard);
        
        //a cell has a wumpus in it if all its neighbors have a stench
        String hasWumpus = "assert(("
                + "hasWumpus(X1,Y1):-"
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
    }
}
