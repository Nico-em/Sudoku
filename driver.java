import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class driver {

    public static void main(String[] args) {
        // run all puzzles with and without FC, MRV
        try {
            newProcess("javac SudokuSolver.java");
            newProcess("java SudokuSolver puzzle1.txt");
            newProcess("java SudokuSolver puzzle1.txt FC");
            newProcess("java SudokuSolver puzzle1.txt FC MRV");
            newProcess("java SudokuSolver puzzle2.txt");
            newProcess("java SudokuSolver puzzle2.txt FC");
            newProcess("java SudokuSolver puzzle2.txt FC MRV");
            newProcess("java SudokuSolver puzzle3.txt");
            newProcess("java SudokuSolver puzzle3.txt FC");
            newProcess("java SudokuSolver puzzle3.txt FC MRV");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static void newProcess(String terminalCommand) throws Exception {
        // print command
        System.out.println("\n" + terminalCommand);
        // Java process api: https://docs.oracle.com/javase/8/docs/api/java/lang/Process.html
        Process newPro = Runtime.getRuntime().exec(terminalCommand);
        // print result
        String input = null;
        InputStream inputS = newPro.getInputStream();
        BufferedReader bufferInputStream = new BufferedReader(new InputStreamReader(inputS));
        while ((input = bufferInputStream.readLine()) != null) {
            System.out.println(input);
        }

        // print Error if necessary
        String error = null;
        InputStream inputE = newPro.getErrorStream();
        BufferedReader bufferInputError = new BufferedReader(new InputStreamReader(inputE));
        while ((error = bufferInputStream.readLine()) != null) {
            System.out.println(error);
        }
        newPro.waitFor();
      }

}
