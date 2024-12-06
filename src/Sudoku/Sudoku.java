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

import Sudoku.GameBoardPanel;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.awt.*;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
/**
 * The main Sudoku program
 */
public class Sudoku extends JFrame {
    private static final long serialVersionUID = 1L;  // to prevent serial warning

String mode;
    private static final long serialVersionUID = 1L;  // to prevent serial warning
    // private variables
    private JLabel highScoreLabelE = new JLabel("Easy : None");
    private JLabel highScoreLabelM = new JLabel("Medium : None");

    private JLabel highScoreLabelH = new JLabel("Hard : None");

    private int fastestTimerEasy;
    private int fastestTimerMedium;
    private int fastestTimerHard;

    private String highestName = "";

    private GameBoardPanel board = new GameBoardPanel();
    private JButton btnPlay = new JButton("Play");
    private JButton btnPause = new JButton("Pause");
    private JLabel timerLabel = new JLabel("05:00");
    private JLabel messageLabel = new JLabel("Welcome to Sudoku!");
    private JButton musicToggleButton = new JButton("Music Off");
    private JButton resetGameButton = new JButton("Reset Game");
    private JLabel highscore = new JLabel("HighScore ");


    private Clip backgroundMusicClip; // For looping background music
    private ExecutorService executorService = Executors.newSingleThreadExecutor(); // To handle background music playback
    private boolean isMusicPlaying = false; // Music status flag
    private String playerName = "Player"; // Default name

    private Timer timer;
    private int timeLeft; // 5 menit dalam detik
    private final int timeLeftEasy = 120;  // 2 menit
    private final int timeLeftMedium = 240;  // 4 menit
    private final int timeLeftHard = 360;  // 6 menit
    private boolean isTimerRunning = false;
    private int wrongAttempts = 0; // Track wrong attempts
    JButton showAnswerButton = new JButton("Cheat");

    private Theme currentTheme = Theme.DEFAULT; // Tema awal
    private JButton themeButton = new JButton("Change Theme"); // Button for theme selection
    private Font customFont;

    // Constructor
    public Sudoku() {
        try {
            InputStream fontStream = getClass().getResourceAsStream("/HelveticaNeueMedium.otf");
            customFont = Font.createFont(Font.TRUETYPE_FONT, fontStream).deriveFont(16f); // Ukuran default 16
        } catch (Exception e) {
            e.printStackTrace();
            customFont = new Font("Arial", Font.BOLD, 16); // Fallback ke Arial
        }
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        //Setup Frame
        // Configure container
        Container cp = getContentPane();
        cp.setLayout(new BorderLayout());
        cp.setBackground(Color.BLACK);

// Configure Board
        cp.add(board, BorderLayout.CENTER);

// Configure message label
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        messageLabel.setPreferredSize(new Dimension(250, 45));
        messageLabel.setFont(customFont.deriveFont(25f)); // Ukuran lebih besar
        messageLabel.setForeground(new Color(176, 224, 230)); // Gaming theme color
        messageLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0)); // Padding untuk keseluruhan panel
        cp.add(messageLabel, BorderLayout.NORTH); // Tambahkan di atas papan

// Create a panel to hold the right-side components
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBackground(Color.BLACK);
        rightPanel.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 20)); // Padding untuk keseluruhan panel

// Add timer label to right panel
        timerLabel.setFont(customFont.deriveFont(20f));
        timerLabel.setForeground(new Color(176, 224, 230));
        timerLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        rightPanel.add(timerLabel);

// Create styled buttons
        JButton styledThemeButton = createStyledButton(themeButton);
        themeButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        rightPanel.add(Box.createVerticalStrut(8)); // Spasi antar tombol
        rightPanel.add(styledThemeButton);

// Create Music toggle
        musicToggleButton.setFont(customFont);
        musicToggleButton.setBackground(Color.DARK_GRAY);
        musicToggleButton.setForeground(Color.WHITE);
        musicToggleButton.setFocusPainted(false);
        musicToggleButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        rightPanel.add(Box.createVerticalStrut(12));
        rightPanel.add(musicToggleButton);

        highScoreLabelE.setText(String.format("Easy: %s", formatTime(fastestTimerEasy)));
        highScoreLabelM.setText(String.format("Medium: %s", formatTime(fastestTimerMedium)));
        highScoreLabelH.setText(String.format("Hard: %s", formatTime(fastestTimerHard)));

        rightPanel.revalidate();
        rightPanel.repaint();

        highscore.setFont(customFont.deriveFont(20f));
        highscore.setForeground(new Color(176, 224, 230));
        highscore.setAlignmentX(Component.LEFT_ALIGNMENT);
        rightPanel.add(Box.createVerticalStrut(10)); // Spasi vertikal
        rightPanel.add(highscore);

        highScoreLabelE.setFont(customFont.deriveFont(20f));
        highScoreLabelE.setForeground(new Color(176, 224, 230));
        highScoreLabelE.setAlignmentX(Component.LEFT_ALIGNMENT);
        rightPanel.add(Box.createVerticalStrut(10)); // Spasi vertikal
        rightPanel.add(highScoreLabelE);

        highScoreLabelM.setFont(customFont.deriveFont(20f));
        highScoreLabelM.setForeground(new Color(176, 224, 230));
        highScoreLabelM.setAlignmentX(Component.LEFT_ALIGNMENT);
        rightPanel.add(Box.createVerticalStrut(10)); // Spasi vertikal
        rightPanel.add(highScoreLabelM);

        highScoreLabelH.setFont(customFont.deriveFont(20f));
        highScoreLabelH.setForeground(new Color(176, 224, 230));
        highScoreLabelH.setAlignmentX(Component.LEFT_ALIGNMENT);
        rightPanel.add(Box.createVerticalStrut(10)); // Spasi vertikal
        rightPanel.add(highScoreLabelH);


// Add bottom panel with buttons
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new FlowLayout(FlowLayout.CENTER)); // Center-align buttons
        bottomPanel.setBackground(Color.BLACK);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Padding for the panel

// Add buttons to the bottom panel
        bottomPanel.add(createStyledButton(btnPlay));
        bottomPanel.add(Box.createHorizontalStrut(5)); // Horizontal spacing
        bottomPanel.add(createStyledButton(btnPause));
        bottomPanel.add(Box.createHorizontalStrut(5)); // Horizontal spacing
        bottomPanel.add(createStyledButton(showAnswerButton));
        bottomPanel.add(Box.createHorizontalStrut(5)); // Horizontal spacing
        bottomPanel.add(createStyledButton(resetGameButton));

// Add bottom panel to container
        cp.add(bottomPanel, BorderLayout.SOUTH);
        cp.add(rightPanel, BorderLayout.EAST);

// Add action listeners
        showAnswerButton.addActionListener(e -> board.showSolution());
        themeButton.addActionListener(e -> showThemeSelectionDialog());
        btnPlay.addActionListener(e -> startTimer());
        btnPause.addActionListener(e -> pauseTimer());
        musicToggleButton.addActionListener(e -> toggleMusic());
        resetGameButton.addActionListener(e -> resetGame());

        // Timer untuk hitungan mundur
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (timeLeft > 0) {
                    timeLeft--;
                    updateTimerLabel();
                } else {
                    timer.stop();
                    isTimerRunning = false;
                    playSound("adlin.wav");
                    JOptionPane.showMessageDialog(Sudoku.this, "Time's up! You lost.", "Game Over", JOptionPane.INFORMATION_MESSAGE);
                }

                // Periksa jika permainan selesai
                if (board.isSolved()) {
                    timer.stop(); // Hentikan timer saat permainan selesai
                    isTimerRunning = false;
                    playSound("zaky.wav");
                    // Gunakan pesan "Congratulations!" yang sudah ada sebelumnya
                    saveScore(timeLeft, playerName);
                    JOptionPane.showMessageDialog(Sudoku.this, "Congratulations!", "You Win", JOptionPane.INFORMATION_MESSAGE);
                    startNewGame();
                }
            }
        });

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                System.exit(0); // Menghentikan program sepenuhnya
            }
        });

        // Mulai game baru saat aplikasi dijalankan
        startNewGame();
        startBackgroundMusic("membasuh.wav");
        pack();     // Pack the UI components, instead of using setSize()
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  // to handle window-closing
        setTitle("Sudoku");
        setVisible(true);
}
