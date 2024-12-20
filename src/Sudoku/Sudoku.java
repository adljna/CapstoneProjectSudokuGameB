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

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
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
        messageLabel.setFont(customFont.deriveFont(22f)); // Ukuran lebih besar
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
        startBackgroundMusic("/lagu.wav");
        pack();     // Pack the UI components, instead of using setSize()
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  // to handle window-closing
        setTitle("Sudoku");
        setVisible(true);
    }

    // Memulai permainan baru
    private void startNewGame() {
        if (playerName.equals("Player")) { // Hanya minta nama pertama kali
            boolean validName = false;
            while (!validName) {
                playerName = JOptionPane.showInputDialog(
                        this,
                        "Enter your name:",
                        "Player Name",
                        JOptionPane.QUESTION_MESSAGE
                );
                if (playerName == null ){
                    System.exit(0);
                }
                else if (playerName.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(
                            this,
                            "Name cannot be empty. Please enter your name.",
                            "Invalid Input",
                            JOptionPane.WARNING_MESSAGE
                    );
                } else {
                    validName = true;
                }
            }
        }
        String[] options = {"Easy", "Medium", "Hard"};
        int choice = JOptionPane.showOptionDialog(
                null,
                "Pilih level yang diinginkan:",
                "Input Level",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );
        if (choice == JOptionPane.CLOSED_OPTION) {
            System.exit(0); // Keluar dari aplikasi
        }
        int cellsToGuess;
        if (choice == 0) { // Easy
            timeLeft = timeLeftEasy;
            cellsToGuess = 4;
            mode = "Easy";
        } else if (choice == 1) { // Medium
            timeLeft = timeLeftMedium;
            cellsToGuess = 7;
            mode = "Medium";
        } else if (choice == 2) { // Hard
            timeLeft = timeLeftHard;
            cellsToGuess = 10;
            mode = "Hard";
        } else { // Default jika tidak ada pilihan
            JOptionPane.showMessageDialog(null, "Tidak ada level yang dipilih. Menggunakan default Easy.", "Info", JOptionPane.INFORMATION_MESSAGE);
            timeLeft = timeLeftEasy;
            cellsToGuess = 4;
        }
        wrongAttempts = 0; // Reset wrong attempts
        updateStatusBar(); // Update the status bar
        updateTimerLabel();
        board.newGame(cellsToGuess); // Berikan parameter cellsToGuess
        startTimer(); // Otomatis mulai timer
        resetMessageLabel();
    }
    private void saveScore(int score, String name) {
        int time; // Menghitung waktu yang digunakan berdasarkan waktu tersisa
        if ("Easy".equals(mode)) {
            time = timeLeftEasy - score;
            if (fastestTimerEasy == 0 || time < fastestTimerEasy) {
                fastestTimerEasy = time;
                highestName = name;
                highScoreLabelE.setText(String.format("Easy: %s", formatTime(time)));
            }
        } else if ("Medium".equals(mode)) {
            time = timeLeftMedium - score;
            if (fastestTimerMedium == 0 || time < fastestTimerMedium) {
                fastestTimerMedium = time;
                highestName = name;
                highScoreLabelM.setText(String.format("Medium: %s", formatTime(time)));
            }
        } else if ("Hard".equals(mode)) {
            time = timeLeftHard - score;
            if (fastestTimerHard == 0 || time < fastestTimerHard) {
                fastestTimerHard = time;
                highestName = name;
                highScoreLabelH.setText(String.format("Hard: %s", formatTime(time)));
            }
        }
    }

    private String formatTime(int x) {
        int minutes = x / 60;
        int seconds = x % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    // Memulai timer
    private void startTimer() {
        if (!isTimerRunning) {
            timer.start();
            isTimerRunning = true;
        }
    }

    // Menjeda timer
    private void pauseTimer() {
        if (isTimerRunning) {
            timer.stop();
            isTimerRunning = false;
        }
    }

    // Memperbarui label timer
    private void updateTimerLabel() {
        int minutes = timeLeft / 60;
        int seconds = timeLeft % 60;
        timerLabel.setText(String.format("%02d:%02d", minutes, seconds));
    }
    public void updateStatusBar() {
        int cellsRemaining = board.countCellsRemaining();
        messageLabel.setText(String.format("Player: %s | Cells remaining: %d | Wrong attempts: %d", playerName, cellsRemaining, wrongAttempts));
    }

    public void incrementWrongAttempts() {
        wrongAttempts++;
        updateStatusBar();
        if (wrongAttempts >= 3) {
            playSound("naura.wav"); // Play losing sound
            JOptionPane.showMessageDialog(this, "Three wrong attempts! Restarting the game.", "Game Over", JOptionPane.INFORMATION_MESSAGE);
            startNewGame();
        }
    }

    // Reset game
    private void resetGame() {
        startNewGame();
    }
    public void resetMessageLabel() {
        messageLabel.setText("Welcome to Sudoku " + playerName + "!");
    }
    private JButton createStyledButton(JButton button) {
        button.setFont(customFont);
        button.setBackground(Color.DARK_GRAY);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        return button;
    }
    private void playSound(String soundFileName) {
        try {
            File soundFile = new File(getClass().getResource("/" + soundFileName).toURI());
            Clip clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(soundFile));
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // Start background music
    private void startBackgroundMusic(String musicFileName) {
        try {
            InputStream audioSrc = getClass().getResourceAsStream(musicFileName);
            if (audioSrc == null) {
                throw new IllegalArgumentException("Music file not found: " + musicFileName);
            }
            InputStream bufferedIn = new BufferedInputStream(audioSrc);
            backgroundMusicClip = AudioSystem.getClip();
            backgroundMusicClip.open(AudioSystem.getAudioInputStream(bufferedIn));
            backgroundMusicClip.loop(Clip.LOOP_CONTINUOUSLY); // Loop the music
            backgroundMusicClip.start();
            isMusicPlaying = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Stop background music
    private void stopBackgroundMusic() {
        if (backgroundMusicClip != null && backgroundMusicClip.isRunning()) {
            backgroundMusicClip.stop();
            backgroundMusicClip.close();
            isMusicPlaying = false;
        }
    }

    // Toggle background music
    private void toggleMusic() {
        if (isMusicPlaying) {
            stopBackgroundMusic();
            musicToggleButton.setText("Music On");
        } else {
            startBackgroundMusic("/lagu.wav");
            musicToggleButton.setText("Music Off");
        }
    }
    @Override
    public void dispose() {
        stopBackgroundMusic(); // Stop music on exit
        executorService.shutdownNow(); // Clean up the executor service
        super.dispose();
    }
    private void showThemeSelectionDialog() {
        String[] themes = {"Default", "White Mode", "Colorful UI", "Wood Chocolate"};

// Default colors for a more standard UI theme
        UIManager.put("OptionPane.background", Color.WHITE);
        UIManager.put("Panel.background", Color.WHITE);
        UIManager.put("OptionPane.messageForeground", Color.BLACK);
        UIManager.put("ComboBox.selectionBackground", Color.LIGHT_GRAY);
        UIManager.put("ComboBox.selectionForeground", Color.BLACK);


        String selectedTheme = (String) JOptionPane.showInputDialog(
                this,
                "Select a theme:",
                "Theme Selection",
                JOptionPane.PLAIN_MESSAGE,
                null,
                themes,
                themes[0]
        );

        if (selectedTheme != null) {
            switch (selectedTheme) {
                case "Default":
                    applyTheme(Theme.DEFAULT);
                    break;
                case "White Mode":
                    applyTheme(Theme.WHITE);
                    break;
                case "Colorful UI":
                    applyTheme(Theme.COLORFUL);
                    break;
                case "Wood Chocolate":
                    applyTheme(Theme.WOOD_CHOCOLATE);
                    break;
            }
        }
    }

    private void applyTheme(Theme theme) {
        currentTheme = theme;

        // Update UI colors based on theme
        Color backgroundColor = Color.BLACK;
        Color foregroundColor = new Color(176, 224, 230);
        Color buttonBackgroundColor = new Color(70, 70, 70);
        Color buttonForegroundColor = Color.WHITE;

        switch (theme) {
            case DEFAULT:
                backgroundColor = Color.BLACK;
                foregroundColor = new Color(176, 224, 230);
                buttonBackgroundColor = new Color(70, 70, 70);
                buttonForegroundColor = Color.WHITE;
                break;
            case WHITE:
                backgroundColor = Color.WHITE;
                foregroundColor = new Color(50, 50, 50);
                buttonBackgroundColor = new Color(240, 240, 240);
                buttonForegroundColor = new Color(50, 50, 50);
                break;
            case COLORFUL:
                backgroundColor = new Color(255, 105, 180); // Pink background
                foregroundColor = new Color(255, 255, 0);   // Yellow text
                buttonBackgroundColor = new Color(0, 255, 255); // Cyan buttons
                buttonForegroundColor = new Color(128, 0, 128); // Purple text
                break;
            case WOOD_CHOCOLATE:
                backgroundColor = new Color(139, 69, 19); // Wood brown background
                foregroundColor = new Color(255, 228, 181); // Light brown text
                buttonBackgroundColor = new Color(205, 133, 63); // Chocolate buttons
                buttonForegroundColor = new Color(255, 228, 181); // Light text
                break;
        }

        // Update main frame background and labels
        getContentPane().setBackground(backgroundColor);
        messageLabel.setForeground(foregroundColor);
        timerLabel.setForeground(foregroundColor);

        // Update buttons and panels
        updateComponentColors(getContentPane(), backgroundColor, buttonBackgroundColor, buttonForegroundColor);

        // Repaint the frame to apply changes
        repaint();
    }

    private void updateComponentColors(Container container, Color panelBackground, Color buttonBackground, Color buttonForeground) {
        for (Component component : container.getComponents()) {
            if (component instanceof JPanel) {
                component.setBackground(panelBackground);
                updateComponentColors((Container) component, panelBackground, buttonBackground, buttonForeground);
            } else if (component instanceof JButton) {
                JButton button = (JButton) component;
                button.setBackground(buttonBackground);
                button.setForeground(buttonForeground);
                button.setFocusPainted(false);
            } else if (component instanceof JLabel) {
                JLabel label = (JLabel) component;
                label.setForeground(buttonForeground);
            }
        }
    }
}
