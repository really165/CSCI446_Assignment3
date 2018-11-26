package csci446_assignment3;

import java.util.Hashtable;
import java.util.Map;
import org.jpl7.*;

import java.util.Random;

public class Main {
    public static void main(String[] args){
        Cell[][] maze = null;
        if (args.length == 1) {
            switch (args[0]) {
                case "4x4":
                    maze = constructMaze(4);
                    break;

                case "5x5":
                    maze = constructMaze(5);
                    break;
                        
                case "8x8":
                    maze = constructMaze(8);
                    break;
                  
                case "10x10":
                    maze = constructMaze(10);
                    break;

                default:
                    throw new IllegalArgumentException("Must be 4x4, 5x5, 8x8, or 10x10");
            }
        }
        else {
            System.err.println("Usage: java assignment2.main DIMENSIONS");
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
        
        //put the wumpus in a random cell
        int wumpusRow = rand.nextInt(dimension);
        int wumpusColumn = rand.nextInt(dimension);
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
            }
        }
        
        return result;
    }
}