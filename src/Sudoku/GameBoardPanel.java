package Sudoku;
import javax.swing.border.Border;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Set;

public class GameBoardPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    public static final int CELL_SIZE = 60;
    public static final int BOARD_WIDTH = CELL_SIZE * SudokuConstants.GRID_SIZE;
    public static final int BOARD_HEIGHT = CELL_SIZE * SudokuConstants.GRID_SIZE;
    private Cell[][] cells = new Cell[SudokuConstants.GRID_SIZE][SudokuConstants.GRID_SIZE];
    private Puzzle puzzle = new Puzzle();
    private Color conflictColor = new Color(255, 182, 193); // Light pink for conflicts
    private Set<Cell> highlightedCells = new HashSet<>(); // To track highlighted cells
    /** Constructor */
    public GameBoardPanel() {
        super.setLayout(new GridLayout(SudokuConstants.GRID_SIZE, SudokuConstants.GRID_SIZE));  // JPanel

        // Allocate the 2D array of Cell, and added into JPanel.
        for (int row = 0; row < SudokuConstants.GRID_SIZE; ++row) {
            for (int col = 0; col < SudokuConstants.GRID_SIZE; ++col) {
                cells[row][col] = new Cell(row, col);
                super.add(cells[row][col]);   // JPanel
            }
        }

        // [TODO 3] Allocate a common listener as the ActionEvent listener for all the
        //  Cells (JTextFields)
        CellInputListener listener = new CellInputListener();

        // [TODO 4] Adds this common listener to all editable cells
        for (int row = 0; row < SudokuConstants.GRID_SIZE; ++row) {
            for (int col = 0; col < SudokuConstants.GRID_SIZE; ++col) {
                if (cells[row][col].isEditable()) {
                    cells[row][col].addActionListener(listener);   // For all editable rows and cols
                }
            }
        }
        super.setPreferredSize(new Dimension(BOARD_WIDTH, BOARD_HEIGHT));
        for (int row = 0; row < SudokuConstants.GRID_SIZE; ++row) {
            for (int col = 0; col < SudokuConstants.GRID_SIZE; ++col) {
                int top = (row % SudokuConstants.SUBGRID_SIZE == 0) ? 3 : 1;
                int left = (col % SudokuConstants.SUBGRID_SIZE == 0) ? 3 : 1; 
                int bottom = (row == SudokuConstants.GRID_SIZE - 1) ? 3 : 1;
                int right = (col == SudokuConstants.GRID_SIZE - 1) ? 3 : 1;  

                if (row % SudokuConstants.SUBGRID_SIZE == 0) {
                    top = 5; 
                }
                if (col % SudokuConstants.SUBGRID_SIZE == 0) {
                    left = 5; 
                }

                Border border = BorderFactory.createMatteBorder(top, left, bottom, right, Color.BLACK);

                Border thinGrayBorder = BorderFactory.createMatteBorder(
                        (top == 1) ? 1 : 0, (left == 1) ? 1 : 0, (bottom == 1) ? 1 : 0, (right == 1) ? 1 : 0,
                        Color.WHITE);

                cells[row][col].setBorder(BorderFactory.createCompoundBorder(border, thinGrayBorder));

                super.add(cells[row][col]);
            }
        }
    }

    /**
     * Generate a new puzzle; and reset the game board of cells based on the puzzle.
     * You can call this method to start a new game.
     */
    public void newGame() {
        // Generate a new puzzle
        puzzle.newPuzzle(2);

        // Initialize all the 9x9 cells, based on the puzzle.
        for (int row = 0; row < SudokuConstants.GRID_SIZE; ++row) {
            for (int col = 0; col < SudokuConstants.GRID_SIZE; ++col) {
                cells[row][col].newGame(puzzle.numbers[row][col], puzzle.isGiven[row][col]);
            }
        }
    }

    /**
     * Return true if the puzzle is solved
     * i.e., none of the cell have status of TO_GUESS or WRONG_GUESS
     */
    public boolean isSolved() {
        for (int row = 0; row < SudokuConstants.GRID_SIZE; ++row) {
            for (int col = 0; col < SudokuConstants.GRID_SIZE; ++col) {
                if (cells[row][col].status == CellStatus.TO_GUESS || cells[row][col].status == CellStatus.WRONG_GUESS) {
                    return false;
                }
            }
        }
        return true;
    }

    // [TODO 2] Define a Listener Inner Class for all the editable Cells
    private class CellInputListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            // Get a reference of the JTextField that triggers this action event
            Cell sourceCell = (Cell)e.getSource();

            // Retrieve the int entered
            int numberIn = Integer.parseInt(sourceCell.getText());
            // For debugging
            System.out.println("You entered " + numberIn);

            /*
             * [TODO 5] (later - after TODO 3 and 4)
             * Check the numberIn against sourceCell.number.
             * Update the cell status sourceCell.status,
             * and re-paint the cell via sourceCell.paint().
             */
            if (numberIn == sourceCell.number) {
                sourceCell.status = CellStatus.CORRECT_GUESS;
            } else {
                sourceCell.status = CellStatus.WRONG_GUESS;
            }
            sourceCell.paint();   // re-paint this cell based on its status

            /*
             * [TODO 6] (later)
             * Check if the player has solved the puzzle after this move,
             *   by calling isSolved(). Put up a congratulation JOptionPane, if so.
             */
            if (isSolved()){
                JOptionPane.showMessageDialog(null, "Congratulation");
            }
        }
    }
}
