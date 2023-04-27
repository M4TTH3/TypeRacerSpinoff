package com.matthew._final;

//
// @author m4tth
//

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.*;

import java.util.ArrayList;

import java.util.Collections;
import java.util.Random;



public class Main {

    // Initializing visual and objects
    private static JFrame frame = new JFrame("Type Racer");
    private static TypeList typeList = new TypeList("src/WordsList");
    private static JTextField userInput = new JTextField();
    private static JLabel timeTillReset = new JLabel();
    private static JLabel laFini = new JLabel();

    // Holds the points of user and cpu
    private static int currentPoints = 0;
    private static int cpuPoints = 0;

    // Used to keep track of duration for list refresh and CPU updating
    private static double startTime;
    private static double twoSecondTimer;

    // Used as a baseline for the cpu to gauge the speed of user
    private static int characterCount;

    // Initialize race visuals
    private static JLabel red = new JLabel();
    private static JLabel blue = new JLabel();
    private static JLabel track = new JLabel();

    // Intro screen
    private static JButton intro = new JButton();
    private static boolean isIntro = true;

    // To start the game after first key strikes
    private static boolean hasStart = false;

    // Used to loop through some graphics
    public static Timer timer;

    // Play again button
    private static JButton playAgain = new JButton("Play Again");


    /**
     * Creates the GUI.
     *
     * @param args the command line arguments
     */

    public static void main(String[] args) {

        // Set boundaries for all the visual objects

        // Set frame boundaries
        frame.setSize(800, 800);
        frame.setLayout(null);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        playAgain.setBounds(540, 620, 210, 120);

        red.setBounds(380, 540, 50, 50);
        blue.setBounds(570, 540, 50, 50);
        track.setBounds(250, 100, 500, 500);

        userInput.setBounds(20, 620, 500, 120);
        userInput.setFont(new Font("Dialog", Font.PLAIN, 60));
        userInput.setHighlighter(null); // Makes it so you can't highlight with cursaor

        timeTillReset.setBounds(20, 20, 250, 80);
        timeTillReset.setFont(new Font("Dialog", Font.PLAIN, 50));

        laFini.setBounds(220, 20, 580, 80);
        laFini.setFont(new Font("Dialog", Font.ITALIC, 60));
        laFini.setText("            La Fini");

        intro.setBounds(0, 0, 800, 800);

        // Hide everything else for intro
        // If the intro is clicked, make everything visible again and start the game
        if (isIntro) {
            red.setVisible(false);
            blue.setVisible(false);
            track.setVisible(false);
            playAgain.setVisible(false);
            userInput.setVisible(false);
            timeTillReset.setVisible(false);
            laFini.setVisible(false);

            intro.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    red.setVisible(true);
                    blue.setVisible(true);
                    track.setVisible(true);
                    playAgain.setVisible(true);
                    userInput.setVisible(true);
                    timeTillReset.setVisible(true);
                    laFini.setVisible(true);

                    resetVariables();
                    intro.setVisible(false);
                    isIntro = false;
                }
            });

        }

        // Use a timer to have constantly running
        timer = new Timer(10, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // If no one has won yet, make a countdown of 7 seconds
                // When it reaches 7 seconds clear all the texts and inputs
                // Set a small delay to give users who type in between list changing.
                if (currentPoints < 44 && cpuPoints < 44) {
                    double timeElapsed = (System.currentTimeMillis() - startTime) / 1000;
                    if (timeElapsed >= 7.00) {
                        timeTillReset.setText("0.000");
                        userInput.setEnabled(false); // Ensures user can't keep typing
                        userInput.setText("");
                        if (timeElapsed >= 7.5) {
                            userInput.setEnabled(true);
                            userInput.requestFocusInWindow(); // Select the input box so they can immediately type

                            startTime = System.currentTimeMillis(); // Refresh the 7 second clock
                            typeList.generateNewList(); // Generate new list
                        }
                    } else {
                        // When you are before 7 seconds print the time remaining
                        timeTillReset.setText(String.format("%.3f", (7.00 - timeElapsed)));

                        // Every 2 seconds, increase the CPU's score and animate it's box
                        if ((System.currentTimeMillis() - twoSecondTimer) / 1000 >= 2) {
                            int increase = generateScore(characterCount, typeList);
                            if (increase > 0) {
                                cpuPoints += increase;

                                // For the box animation, make sure it doesn't go past the track. If it will, just set it to the end
                                // The distance it travels is 440 pixels. The total points are 44, so each point is worth 10 pixels
                                if (red.getY() - (10 * increase) >= 100) {
                                    red.setBounds(380, red.getY() - (10 * increase), 50, 50);
                                } else {
                                    red.setBounds(380, 100, 50, 50);
                                }
                                characterCount = 0; // Refresh the character count to check users speed in the next 2 seconds
                            }
                            twoSecondTimer = System.currentTimeMillis(); // Reset clock
                        }
                    }
                } else {
                    // When a player wins, send messages, and reveal all the words guessed
                    userInput.setEnabled(false);
                    if (cpuPoints > currentPoints) {
                        userInput.setText("     YOU LOST!");
                    } else {
                        userInput.setText("     YOU WON!");
                    }
                    timeTillReset.setText("0.000");
                    typeList.updateToGuessedWords();

                    laFini.setFont(new Font("Dialog", Font.BOLD, 40));
                    laFini.setText("      Thank you for teaching");
                }
            }
        });

        // Override functions that are initialized when a key event occurs
        userInput.addKeyListener(new KeyAdapter() {

            @Override
            public void keyReleased(KeyEvent e) {
                characterCount ++; // Whenever a key is released increase the amount of characters the user types
                if (!hasStart) {
                    timer.start(); // Enables timer
                    startTime = System.currentTimeMillis();
                    twoSecondTimer = System.currentTimeMillis();
                    hasStart = true;
                }

                // If you hit enter, it clears the input box instead of hitting delete
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    userInput.setText("");
                }

                // Grab value of current text input to see if it's a word inside the list
                // If it's inside the list, it will return > 0.
                int increase = typeList.inList(userInput.getText());
                if (increase != -1) {
                    currentPoints += increase; // Increase users points
                    userInput.setText("");

                    // For the box animation, make sure it doesn't go past the track. If it will, just set it to the end
                    // The distance it travels is 440 pixels. The total points are 44, so each point is worth 10 pixels
                    if (blue.getY() - (10 * increase) >= 100) {
                        blue.setBounds(570, blue.getY() - (10 * increase), 50, 50);
                    } else {
                        blue.setBounds(570, 100, 50, 50);
                    }
                }
            }
        });

        // Play again button. Reinitializes all the values
        playAgain.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetVariables();
            }
        });

        // Add all the objects and images

        frame.add(intro);
        frame.add(playAgain);

        frame.add(typeList.getListDisplay());
        frame.add(userInput);
        frame.add(timeTillReset);
        frame.add(laFini);

        frame.add(red);
        frame.add(blue);
        frame.add(track);

        intro.setIcon(new ImageIcon("src/Intro.png"));
        track.setIcon(new ImageIcon("src/Track.png"));
        red.setIcon(new ImageIcon("src/Red.png"));
        blue.setIcon(new ImageIcon("src/Blue.png"));
    }


    // Generates a unique score for the CPU based on users speed
    // @param int characterCount, TypeList typeList
    // @return int
    public static int generateScore(int characterCount, TypeList typeList) {
        int points = 0;

        Random rand = new Random(); // Going to generate a random character for the cpu based on user's

        // If the user is idle, make the cpu move very slowly
        if (characterCount < 3) {
            return rand.nextInt(5);
        }

        // Unique value for amount of characters CPU can have in words
        int cpuCharCount = characterCount + rand.nextInt(4) - rand.nextInt(characterCount + 2);;

        // Take all the strings in the current list and shuffle to randomize
        java.util.List<String> toTypeKeys = new ArrayList<String>(typeList.getToTypeList().keySet());
        Collections.shuffle(toTypeKeys);

        // While the cpu still has characters remaining, it checks if there are any words it can still claim.
        // Deduct every word's character from the CPU's remaining
        for (String i: toTypeKeys) {
            if (cpuCharCount == 0) {
                break;
            }
            if (cpuCharCount >= i.length()) {
                int valueOfWord = typeList.getToTypeList().get(i);
                points += valueOfWord;
                cpuCharCount -= valueOfWord;
            }
        }

        return points;
    }


    // Resets all variables to start the game
    // @param
    // @return void
    public static void resetVariables() {
        currentPoints = 0;
        cpuPoints = 0;

        typeList.generateNewList(); // New list of words
        typeList.getWordsGuessed().clear();

        red.setBounds(380, 540, 50, 50);
        blue.setBounds(570, 540, 50, 50);

        timeTillReset.setText("7.000");

        userInput.setText("");
        userInput.setEnabled(true);
        userInput.requestFocusInWindow();

        // Stop the timer so that when a key is stroken it starts it again
        timer.stop();
        hasStart = false;
    }

}
