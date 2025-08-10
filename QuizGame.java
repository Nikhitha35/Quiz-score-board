import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class QuizGame extends Frame {
    private Scoreboard scoreboard;
    private String currentPlayer;
    private int currentScore;
    private int currentQuestionIndex;
    
    // Quiz questions and answers
    private String[][] questions = {
        {"What is the capital of France?", "Paris", "London", "Berlin", "Madrid", "1"},
        {"Which planet is known as the Red Planet?", "Venus", "Mars", "Jupiter", "Saturn", "2"},
        {"What is the largest mammal?", "Elephant", "Blue Whale", "Giraffe", "Polar Bear", "2"},
        {"Who painted the Mona Lisa?", "Vincent van Gogh", "Pablo Picasso", "Leonardo da Vinci", "Michelangelo", "3"},
        {"What is the chemical symbol for gold?", "Go", "Gd", "Au", "Ag", "3"},
        {"Which country is home to the kangaroo?", "South Africa", "Brazil", "Australia", "New Zealand", "3"},
        {"What is the largest ocean on Earth?", "Atlantic", "Indian", "Arctic", "Pacific", "4"},
        {"How many continents are there?", "5", "6", "7", "8", "3"},
        {"What is the main component of the Sun?", "Liquid lava", "Hydrogen", "Oxygen", "Carbon", "2"},
        {"Which year did World War II end?", "1943", "1945", "1947", "1950", "2"}
    };
    
    private Label questionLabel;
    private CheckboxGroup optionsGroup;
    private Checkbox[] optionCheckboxes;
    private Button submitButton;
    private Button nextButton;
    private Label scoreLabel;
    private TextField nameField;
    private Button startButton;
    private Button showScoresButton;
    private Panel currentPanel;
    
    public QuizGame() {
        scoreboard = new Scoreboard();
        setupUI();
    }
    
    private void setupUI() {
        setTitle("Quiz Game");
        setSize(500, 400);
        setLayout(new BorderLayout());
        
        // Initialize components
        questionLabel = new Label("", Label.CENTER);
        questionLabel.setFont(new Font("Arial", Font.BOLD, 16));
        
        optionsGroup = new CheckboxGroup();
        optionCheckboxes = new Checkbox[4];
        for (int i = 0; i < 4; i++) {
            optionCheckboxes[i] = new Checkbox("", optionsGroup, false);
        }
        
        submitButton = new Button("Submit Answer");
        nextButton = new Button("Next Question");
        scoreLabel = new Label("Score: 0", Label.CENTER);
        nameField = new TextField(20);
        startButton = new Button("Start Quiz");
        showScoresButton = new Button("Show High Scores");
        
        // Add event listeners
        submitButton.addActionListener(e -> checkAnswer());
        nextButton.addActionListener(e -> showNextQuestion());
        startButton.addActionListener(e -> startQuiz());
        showScoresButton.addActionListener(e -> showHighScores());
        
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                scoreboard.close();
                dispose();
            }
        });
        
        // Show welcome panel initially
        showWelcomePanel();
    }
    
    private void showWelcomePanel() {
        if (currentPanel != null) {
            remove(currentPanel);
        }
        
        Panel welcomePanel = new Panel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        Label welcomeLabel = new Label("Welcome to the Quiz Game!", Label.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        
        Label nameLabel = new Label("Enter your name:");
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        welcomePanel.add(welcomeLabel, gbc);
        
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        welcomePanel.add(nameLabel, gbc);
        
        gbc.gridx = 1;
        welcomePanel.add(nameField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        welcomePanel.add(startButton, gbc);
        
        gbc.gridx = 1;
        welcomePanel.add(showScoresButton, gbc);
        
        add(welcomePanel, BorderLayout.CENTER);
        currentPanel = welcomePanel;
        validate();
    }
    
    private void startQuiz() {
        currentPlayer = nameField.getText().trim();
        if (currentPlayer.isEmpty()) {
            showMessage("Please enter your name to start the quiz.");
            return;
        }
        
        currentScore = 0;
        currentQuestionIndex = 0;
        scoreLabel.setText("Score: " + currentScore);
        
        showQuestionPanel();
        showNextQuestion();
    }
    
    private void showQuestionPanel() {
        if (currentPanel != null) {
            remove(currentPanel);
        }
        
        Panel questionPanel = new Panel(new BorderLayout());
        
        Panel centerPanel = new Panel(new GridLayout(5, 1));
        centerPanel.add(questionLabel);
        
        for (Checkbox cb : optionCheckboxes) {
            centerPanel.add(cb);
        }
        
        Panel buttonPanel = new Panel(new FlowLayout());
        buttonPanel.add(submitButton);
        buttonPanel.add(nextButton);
        buttonPanel.add(showScoresButton);
        
        questionPanel.add(centerPanel, BorderLayout.CENTER);
        questionPanel.add(scoreLabel, BorderLayout.NORTH);
        questionPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(questionPanel, BorderLayout.CENTER);
        currentPanel = questionPanel;
        validate();
    }
    
    private void showNextQuestion() {
        if (currentQuestionIndex >= questions.length) {
            endQuiz();
            return;
        }
        
        String[] questionData = questions[currentQuestionIndex];
        questionLabel.setText(questionData[0]);
        
        for (int i = 0; i < 4; i++) {
            optionCheckboxes[i].setLabel(questionData[i+1]);
            optionCheckboxes[i].setState(false);
        }
        
        submitButton.setEnabled(true);
        nextButton.setEnabled(false);
    }
    
    private void checkAnswer() {
        Checkbox selected = optionsGroup.getSelectedCheckbox();
        if (selected == null) {
            showMessage("Please select an answer!");
            return;
        }
        
        String[] questionData = questions[currentQuestionIndex];
        int correctIndex = Integer.parseInt(questionData[5]) - 1;
        
        for (int i = 0; i < 4; i++) {
            if (optionCheckboxes[i] == selected) {
                if (i == correctIndex) {
                    currentScore += 10;
                    scoreLabel.setText("Score: " + currentScore);
                    showMessage("Correct! +10 points");
                } else {
                    showMessage("Wrong! The correct answer was: " + questionData[correctIndex + 1]);
                }
                break;
            }
        }
        
        submitButton.setEnabled(false);
        nextButton.setEnabled(true);
        currentQuestionIndex++;
    }
    
    private void endQuiz() {
        scoreboard.addScore(currentPlayer, currentScore);
        
        Panel endPanel = new Panel(new BorderLayout());
        Label endLabel = new Label("Quiz Complete!", Label.CENTER);
        endLabel.setFont(new Font("Arial", Font.BOLD, 18));
        
        Label scoreLabel = new Label(currentPlayer + ", your final score is: " + currentScore, Label.CENTER);
        
        Panel buttonPanel = new Panel();
        Button restartButton = new Button("Play Again");
        restartButton.addActionListener(e -> showWelcomePanel());
        buttonPanel.add(restartButton);
        buttonPanel.add(showScoresButton);
        
        endPanel.add(endLabel, BorderLayout.NORTH);
        endPanel.add(scoreLabel, BorderLayout.CENTER);
        endPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        if (currentPanel != null) {
            remove(currentPanel);
        }
        add(endPanel, BorderLayout.CENTER);
        currentPanel = endPanel;
        validate();
    }
    
    private void showHighScores() {
        String scores = scoreboard.getHighScores();
        showMessage(scores);
    }
    
    private void showMessage(String message) {
        Dialog d = new Dialog(this, "Message", true);
        d.setLayout(new BorderLayout());
        
        // Make dialog resizable
        d.setResizable(true);
        
        // Use TextArea instead of Label for better text formatting
        TextArea textArea = new TextArea(message, 10, 40, TextArea.SCROLLBARS_VERTICAL_ONLY);
        textArea.setEditable(false);
        d.add(textArea, BorderLayout.CENTER);
        
        Button okButton = new Button("OK");
        okButton.addActionListener(e -> d.dispose());
        d.add(okButton, BorderLayout.SOUTH);
        
        // Increase dialog size
        d.setSize(400, 300);
        d.setLocationRelativeTo(this);
        d.setVisible(true);
    }
    
    public static void main(String[] args) {
        QuizGame game = new QuizGame();
        game.setVisible(true);
    }
}