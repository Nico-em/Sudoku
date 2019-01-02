## Sudoku Solver

**Description:**




**Table of Contents:**

driver.java: this file compiles and runs the sudoku solver 3 times: without heuristics, with FC and with FC and MRV.

SudokuSolver.java: this file reads in the puzzle and calls the recursivebacktracking method to solve the puzzle. When run in the terminal, it takes the following arguments: a puzzle in a text file, FC or FC and MRV.

puzzle.txt: an example of the sudoku layout expected by the SudokuSolver.

README.md


**Usage:**

This project can either be run with or without the driver:

A) Without the driver:

First compile the Sudoku solver: javac SudokuSolver.java

Then run any of the following: java SudokuSolver puzzle.txt, java SudokuSolver puzzle.txt FC or java SudokuSolver puzzle.txt FC MRV

B) With the driver:

First compile the driver: javac driver.java

Run the driver: java driver

This will run all the argument combinations.


**Credits:**

Nico-em
