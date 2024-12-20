/**
 * ES234317-Algorithm and Data Structures
 * Semester Ganjil, 2024/2025
 * Group Capstone Project
 * Group 
 * 1 - 5026231068 - Nailah Adlina
 * 2 - 5026231069 - Muhammad Zaky Al Khair
 * 3 - 5026231173 - Naura Salsabila
 */

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

        // [TODO 3] Allocate a common listener as the ActionEvent listener for all the Cells (JTextFields)
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

                if (row % SudokuConstants.SUBGRID_SIZE == 0)
                    top = 5; 
                if (col % SudokuConstants.SUBGRID_SIZE == 0)
                    left = 5; 
                
                Border border = BorderFactory.createMatteBorder(top, left, bottom, right, Color.BLACK);
                Border thinGrayBorder = BorderFactory.createMatteBorder((top == 1) ? 1 : 0, (left == 1) ? 1 : 0, (bottom == 1) ? 1 : 0, (right == 1) ? 1 : 0, Color.WHITE);

                cells[row][col].setBorder(BorderFactory.createCompoundBorder(border, thinGrayBorder));
                super.add(cells[row][col]);
            }
        }
    }

    public void newGame(int cellsToGuess) {
        puzzle.newPuzzle(cellsToGuess);
        for (int row = 0; row < SudokuConstants.GRID_SIZE; ++row) {
            for (int col = 0; col < SudokuConstants.GRID_SIZE; ++col) {
                cells[row][col].newGame(puzzle.numbers[row][col], puzzle.isGiven[row][col]);
            }
        }
        resetHighlighting(); // Reset highlighting when starting a new game
    }

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

    private void highlightConflicts(Cell sourceCell, int value) {
        resetHighlighting(); // Clear previous highlights
        int row = sourceCell.row;
        int col = sourceCell.col;

        // Highlight cells in the same row and column
        for (int i = 0; i < SudokuConstants.GRID_SIZE; i++) {
            if (cells[row][i].number == value && cells[row][i] != sourceCell) {
                cells[row][i].setBackground(conflictColor);
                highlightedCells.add(cells[row][i]);
            }
            if (cells[i][col].number == value && cells[i][col] != sourceCell) {
                cells[i][col].setBackground(conflictColor);
                highlightedCells.add(cells[i][col]);
            }
        }
    }
    
    private void resetHighlighting() {
        for (Cell cell : highlightedCells) {
            cell.setBackground(new Color(240, 240, 240)); // Reset to default background color
        }
        highlightedCells.clear();
    }
    
    public int countCellsRemaining() {
        int count = 0;
        for (int row = 0; row < SudokuConstants.GRID_SIZE; ++row) {
            for (int col = 0; col < SudokuConstants.GRID_SIZE; ++col) {
                if (cells[row][col].status == CellStatus.TO_GUESS || cells[row][col].status == CellStatus.WRONG_GUESS) { // Only count TO_GUESS cells
                    count++;
                }
            }
        }
        return count;
    }
    
    public void showSolution() {
        for (int row = 0; row < SudokuConstants.GRID_SIZE; ++row) {
            for (int col = 0; col < SudokuConstants.GRID_SIZE; ++col) {
                // Set solusi untuk setiap sel, baik yang sudah diberikan maupun yang ditebak
                cells[row][col].setText(String.valueOf(puzzle.numbers[row][col]));
                cells[row][col].setForeground(Color.BLACK); // Warna teks untuk solusi
                cells[row][col].setBackground(Color.LIGHT_GRAY); // Warna latar belakang untuk solusi
                cells[row][col].setEditable(false); // Membuat sel tidak dapat diedit
                cells[row][col].status = CellStatus.CORRECT_GUESS; // Status menjadi benar
            }
        }
        resetHighlighting(); // Menghapus highlight konflik, jika ada
    }

   private class CellInputListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            Cell sourceCell = (Cell) e.getSource();

            try {
                int numberIn = Integer.parseInt(sourceCell.getText());
                if (numberIn == sourceCell.number) {
                    sourceCell.status = CellStatus.CORRECT_GUESS;
                    resetHighlighting(); // Clear conflicts when the guess is correct
                } else {
                    sourceCell.status = CellStatus.WRONG_GUESS;
                    highlightConflicts(sourceCell, numberIn); // Highlight conflicts
                    ((Sudoku) SwingUtilities.getWindowAncestor(GameBoardPanel.this)).incrementWrongAttempts();
                }
                sourceCell.paint();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Input harus berupa angka.", "Error", JOptionPane.ERROR_MESSAGE);
            }
            ((Sudoku) SwingUtilities.getWindowAncestor(GameBoardPanel.this)).updateStatusBar();
        }
    }
}
