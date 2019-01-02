import java.lang.Object;
import java.util.Arrays;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileReader;
import java.lang.System.*;
// import java.io.File;
import java.io.*;

class SudokuSolver {

    static int[][][] domain;
    static int[][] solution;
    static int[][] domainSize;
    static int varAssigned;
    static int backtrackNum;


    /** ---------------------------------- RECURSIVE FUNCTION --------------------------------------**/

    /** Function to call recursive methods
        Handles Forward checking and minimun remianing values **/
    private static boolean backTrackSolver(Boolean FC, Boolean MRV){
        int[] variable;

        // forward checking with minimum remaining value, calls nextVariableMRV
        if (FC && MRV) {
            System.out.println("Backtracking with forward checking and minimum remaining values");
            // get first variable, the smallest domain size
            variable = nextVariableMRV();
            // return the result from the recursive Forward checking function
            return backtrackerFC(variable, FC, MRV);
        }
        // forward checking and no minimum remaining values, calls nextVariable
        else if (FC && !MRV){
            System.out.println("Backtracking with forward checking");
            // get variable start
            variable = nextVariable(new int[] {0, 0});
            // return the result from the recursive Forward checking function
            return backtrackerFC(variable, FC, MRV);
        }
        // simple backtracking, calls nextVariable
        else if (!FC && !MRV) {
            System.out.println("Simple backtracking");
            // get variable to Start
            variable = nextVariable(new int[] {0, 0});
            // return the result from the recursive backtracking function
            return backtracker(variable, MRV);
        }

        return false;

    }

    /** ---------------------------------- BACKTRACKING --------------------------------------**/

    /** Method for simple backtracking **/
    private static boolean backtracker(int[] variable, Boolean MRV) {
        backtrackNum++;
        // Base case  - if current value is 9 return false for backtracking, no assignements satisfy the constraints
        if (solution[variable[0]][variable[1]] == 9) {
            // reset value back to 0
            solution[variable[0]][variable[1]] = 0;
            return false;
        }
        // try value
        solution[variable[0]][variable[1]] ++;

        // check if no constraints are broken
        // if fine assignement call backtracker on new nextVariable
        if (checkConstraints(variable)){
            // increment varAssigned counter
            varAssigned ++;
            // Base case  - check if goal, return true
            if (isGoal(variable, MRV)) {
                return true;
            } else {
                // try the next variable
                boolean backtrack = backtracker(nextVariable(variable), MRV);
                // if it ultimately works return TRUE
                if(backtrack) {
                    return true;
                // if it failed reassign current value
                } else {
                    return backtracker(variable, MRV);
                }
            }
        }
        // if constraints broke try next value
        else {
            return backtracker(variable, MRV);
        }
    }

    /**
    Method to check constraints shared with a variable
    **/
    private static boolean checkConstraints(int[] variable) {

        // check vertical
        for (int i = 0; i < 9; i ++) {
            if (i == variable[0]) {
                continue;
            }
            // if values are the same return false
            if (solution[variable[0]][variable[1]] == solution[i][variable[1]]) {
                return false;
            }
        }

        // check horizontal
        for (int j = 0; j < 9; j ++) {
            if (j == variable[1]) {
                continue;
            }
            // if values are the same return false
            if (solution[variable[0]][variable[1]] == solution[variable[0]][j]) {
                return false;
            }
        }

        // check 3X3 block: get indecies for the block
        int blockRow = (variable[0]/3)*3;
        int blockRowEnd = blockRow + 3;
        int blockCol = (variable[1]/3)*3;
        int blockColEnd = blockCol + 3;

        for (int j = blockCol; j < blockColEnd; j++) {
            for (int i = blockRow; i < blockRowEnd; i++) {
                if (i == variable[0] && j == variable[1]) {
                    continue;
                }
                // if values are the same return false
                if (solution[variable[0]][variable[1]] == solution[i][j]) {
                    return false;
                }
            }
        }
        // none of the constraints broke. return true
        return true;
    }

    /**
    returns next variable for assignement after current
    finds next 0 value using left to right, top to bottom ordering
    **/
    private static int[] nextVariable(int[] current) {
        int row = current[0];
        int column = current[1];
        // from current variable position, iterated to find value of 0
        while (row < 9) {
            while (column < 9) {
                if (solution[row][column] == 0) {
                    return new int[] {row, column};
                }

                column ++;
            }
            row ++;
            column = 0;
        }
        // no value found, return null
        return null;
    }

    /**
    Goal test - no more variables to assign - solution found
    **/
    private static boolean isGoal(int[] current, Boolean MRV){
        // for Minimum remaining values, use nextVariableMRV otherwise nextVariable
        // both methods return null if there are no more variables to assign
        if (MRV) {
            if (nextVariableMRV() == null) {
                return true;
            } else {
                return false;
            }
        } else {
            if (nextVariable(current) == null) {
                return true;
            } else {
                return false;
            }
        }

    }

/** ---------------------------------- FORWARD CHECKING --------------------------------------**/

    private static boolean backtrackerFC(int[] variable, Boolean FC, Boolean MRV) {
        int[] var = variable;
        backtrackNum ++;

        // get next value for this variable
        int currentValue = solution[variable[0]][variable[1]];
        int nextVal = nextValue(variable, currentValue);

        // Base case  - if no more values in the domain, no assignements satisfy the constraints
        if (nextVal == 0) {
            solution[var[0]][var[1]] = 0;
            return false;
        }

        // assign value to variable
        solution[var[0]][var[1]] = nextVal;
        // increment varAssigned
        varAssigned ++;

        // adjust domains of variables with shared domains,
        fixHorizontal(var, 1);
        fixVertical(var, 1);
        fixBlock(var, 1);
        // call domainSizeFill to re adjust the domainSize array
        Boolean domains = domainSizeFill();
        // printDomainSize();
        // if returns false, there exists a domain of 0, reset value and domains
        if (domains == false) {
            fixHorizontal(var, -1);
            fixVertical(var, -1);
            fixBlock(var, -1);
            domainSizeFill();
            return backtrackerFC(var, FC, MRV);
        }

        // check if isGoal - Base case
        // else call backtrackerFC on next variable
        if (isGoal(var, MRV)) {
            return true;
        } else {
            Boolean backtrack;
            if (MRV) {
                // if Minimun remaining value, nextVariableMRV need to be called
                backtrack = backtrackerFC(nextVariableMRV(), FC, MRV);
            } else {
                backtrack = backtrackerFC(nextVariable(var), FC, MRV);
            }
            // if variable allowed for an assignment return true
            if (backtrack) {
                return true;
            } else {
                // else, unmodify constraints and recall on this variable
                fixHorizontal(var, -1);
                fixVertical(var, -1);
                fixBlock(var, -1);
                domainSizeFill();

                return backtrackerFC(var, FC, MRV);
            }
        }
    }

    /**
    Method to initialise the domain and domainSize Array
    **/
    private static void domainInit(Boolean MRV){
        // for each number in the sudoku, check all constraints and modify domain
        for (int i = 0; i < 9; i ++) {
            for (int j = 0; j < 9; j ++) {
                if (solution[i][j] != 0) {
                    int[] var = new int[] {i, j};
                    fixHorizontal(var, 1);
                    fixVertical(var, 1);
                    fixBlock(var, 1);

                }
            }
        }
        // once adjustments are done, fill domainSize array
        domainSizeFill();
    }

    /**
    Method to adjust domain of horizontal variables with shared constraints
    **/
    private static void fixHorizontal(int[] variable, int constraint) {
        // iterate through row, skip the variable that has just been assigned
        for (int column = 0; column < 9; column ++) {
            if (column == variable[1]) {
                continue;
            }
            // get the value that has added constraints
            int value = solution[variable[0]][variable[1]];
            // add constraint int to the element representing 'value' for all variables
            domain[variable[0]][column][value-1] += constraint;
        }
    }

    /**
    Method to adjust domain of vertical variables with shared constraints
    **/
    private static void fixVertical(int[] variable, int constraint) {
        // iterate through column, skip the variable that has just been assigned
        for (int row = 0; row < 9; row ++) {
            if (row == variable[0]) {
                continue;
            }
            // get the value that has added constraints
            int value = solution[variable[0]][variable[1]];
            // add constraint int to the element representing 'value' for all variables
            domain[row][variable[1]][value-1] += constraint;

        }

    }

    /**
    Method to adjust domain of 3X3 block variables with shared constraints
    **/
    private static void fixBlock(int[] variable, int constraint) {
        // check 3X3 block
        int blockRow = (variable[0]/3)*3;
        int blockRowEnd = blockRow + 3;
        int blockCol = (variable[1]/3)*3;
        int blockColEnd = blockCol + 3;
        // iterate through every variable in the constrained block, skip the variable that has just been assigned
        for (int j = blockCol; j < blockColEnd; j++) {
            for (int i = blockRow; i < blockRowEnd; i++) {
                if (i == variable[0] && j == variable[1]) {
                    continue;
                }
                // get the value that has added constraints
                int value = solution[variable[0]][variable[1]];
                // add constraint int to the element representing 'value' for all variables
                domain[i][j][value-1] += constraint;
            }
        }

    }

    /**
    Method to fins the nextValue from the Domain array
    **/
    private static int nextValue(int[] variable, int current){
        // look for next 0 value since this is a value in domain with no constraints
        for (int z = current; z < 9; z ++) {
            if (domain[variable[0]][variable[1]][z] == 0) {
                // return z + 1 for index to value
                return z + 1;
            }
        }
        // if no more values in domain:
        return 0;
    }

    /** ---------------------------------- MINIMUM REMAINING VALUES --------------------------------------**/

    /**
    Method to get next variable for Minimum remaining value
    **/
    private static int[] nextVariableMRV(){
        int[] smallest = null;
        // find first value with solution value of 0 (unassigned)
        for (int i = 0; i < 9; i ++) {
            for (int j = 0; j < 9; j ++) {
                if (solution[i][j] == 0) {
                    smallest = new int[] {i, j};
                    break;
                }
            }
        }
        // if none found, solution is a full assignment. No more variables, return null.
        if (smallest == null) {
            return null;
        }

        // search through domain size array for smallest domain, return smallest
        for (int i = 0; i < 9; i ++) {
            for (int j = 0; j < 9; j ++) {
                if (solution[i][j] == 0) {
                    if (domainSize[i][j] < domainSize[smallest[0]][smallest[1]]) {
                        smallest[0] = i;
                        smallest[1] = j;
                    }
                }
            }
        }
        return smallest;
    }

    /**
    Method to fill the domainSize array
    **/
    private static Boolean domainSizeFill() {
        // iterate through domain array, count 0 values in domain, these are values with no constraints
        for(int i = 0; i < 9; i ++) {
            for (int j = 0; j < 9; j ++) {
                int count = 0;
                // if unassigned variable count domain size
                if (solution[i][j] == 0) {
                    for (int k = 0; k < 9; k ++) {
                        if(domain[i][j][k] == 0) {
                            count++;
                        }
                    }
                } else {
                    // else, variable assigned set count to 10
                    // bigger than any unassigned value so will not be picked for assigning
                    count = 10;
                }
                // once counting has finished, set value in domainSize
                domainSize[i][j] = count;
                // if domain size is zero return false so that backtracking can occur
                if (count == 0) {
                    return false;
                }
            }
        }
        return true;
     }

     private static void printDomainSize() {
         for(int i = 0; i < 9; i ++) {
             for (int j = 0; j < 9; j ++) {
                 System.out.print(domainSize[i][j] + " ");
             }
             System.out.print("\n");
         }
     }


    /** ---------------------------------- MAIN --------------------------------------**/

    public static void main(String[] args) throws Exception{
        // get program start time
        long TOTAL_START = System.currentTimeMillis();

        // domain 9x9x9 Boolean matrix for variable domains
        domain = new int[9][9][9];
        for (int i = 0; i < 9; i ++) {
            for (int j = 0; j < 9; j ++) {
                // set every value to 0
                Arrays.fill(domain[i][j], 0);
            }
        }
        // solution 9x9 int matrix for partial solutions
        solution = new int[9][9];
        // domainSize 9x9 matrix for minimum remaining values
        domainSize = new int[9][9];
        for (int k = 0; k < 9; k ++) {
            Arrays.fill(domainSize[k], 9);
        }
        // number of variables assigned and backtrack counters
        varAssigned = 0;
        backtrackNum = 0;
        // booleans for Forward checking and Minimum remaining values
        Boolean FC = false;
        Boolean MRV = false;
        // set FC and MRV values: allow for any combination of FC, MRV and LCV
        int index = 1;
        while(index < args.length) {
            if (args[index].equals("FC")) {
                FC = true;
            } else if (args[index].equals("MRV")) {
                MRV = true;
            } else if (args[index].equals("LCV")) {
                System.out.println("\nLCV was not implemented");
            }
            index ++;
        }

        // open file file element of args
        BufferedReader file = new BufferedReader(new FileReader(args[0]));
        String line;
        // iterate through lines and add to solutions Array
        int row = 0;
        int unassigned = 0;
        while ((line = file.readLine()) != null) {
            // fill in solution Array
            String[] lineArray = line.split("\\s+");
            // iterate through each row, inserting the sudoku number
            for (int col = 0; col < solution[row].length; col ++) {
                solution[row][col] = Integer.parseInt(lineArray[col]);
                if (solution[row][col] == 0) {
                    unassigned ++;
                }
            }
            row ++;
        }
        file.close();

        System.out.println("Number of unassigned variables: " + unassigned);

        // initialize domain and domainsize here since solution array is fill
        if(FC) {
            domainInit(MRV);
        }

        // Start search counter and call the backtracker to solve sudoku
        long SEARCH_START = System.currentTimeMillis();
        // if returns true, solution was found
        if (backTrackSolver(FC, MRV)) {
            // prints out solution
            for (int i = 0; i < 9; i ++) {
                for(int k = 0; k < 9; k++) {
                    System.out.printf("%d ", solution[i][k]);
                }
                System.out.println();

            }
            // prints out the number of variables assigned and backtracks
            System.out.println("Number of variables assigned: " + varAssigned);
            System.out.println("Number of backtracks: " + backtrackNum);
        } else {
            // else - if no solution was found
            System.out.println("No Solution Found :( \n");
        }

        // find end time
        long END = System.currentTimeMillis();

        long TOTAL_TIME = END - TOTAL_START;
        long TOTAL_SEARCH = END - SEARCH_START;

        // output time statistics
        System.out.printf("total time: %d%n", TOTAL_TIME);
        System.out.printf("search time: %d%n", TOTAL_SEARCH);

    }
}
