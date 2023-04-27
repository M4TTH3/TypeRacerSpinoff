package com.matthew._final;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.*;


public class TypeList {

    // Fields
    private ArrayList<String> wordsList;
    private HashMap<String, Integer> toTypeList;
    private HashSet<String> wordsGuessed;
    private JTextArea listDisplay;


    // Construct class
    public TypeList(String fileName) {

        this.wordsList = new ArrayList<>();
        this.toTypeList = new HashMap<>();
        this.wordsGuessed = new HashSet<>();
        this.listDisplay = new JTextArea();

        // Generate list of words available
        try {
            Scanner scanner = new Scanner(new File(fileName));
            while (scanner.hasNextLine()) {
                this.wordsList.add(scanner.nextLine());
            }
            scanner.close();
        } catch (IOException exception) {
            System.out.println("Error in file reading");
            System.exit(0);
        }

        // Initialize boundaries of text area
        this.listDisplay.setEditable(false);
        this.listDisplay.setBounds(20, 100, 200, 500);
        this.listDisplay.setFont(new Font("Dialog", Font.PLAIN, 26));

        // Generate new list
        generateNewList();
        // Set the display's text
        updateText();
    }


    // Generates a new list of words after timer ends
    // @param
    // @return void
    public void generateNewList() {
        this.toTypeList.clear(); // Clear current list
        Random rand = new Random();
        for (int i = 0; i < 14; i++) { // Generate 14 new words
            // Grab random word from ArrayList of words
            String tempWord = this.wordsList.get(rand.nextInt(this.wordsList.size()));

            // Check word hasn't been used already
            if (this.wordsGuessed.contains(tempWord)) {
                // If the word was guessed already, reloop
                i -= 1;
                continue;
            }

            // 50/50 chance first letter becomes capital
            tempWord = (rand.nextInt(2) == 1 ? tempWord: tempWord.substring(0,1).toUpperCase() + tempWord.substring(1));

            // Add to toTypeList with the word and it's corresponding value
            this.toTypeList.put(tempWord, getWordValue(tempWord));
        }
        updateText(); // Reupdate the textarea
    }


    // Sets the text area's text
    // @param
    // @return void
    private void updateText() {
        StringBuilder tempText = new StringBuilder();
        for (String i: this.toTypeList.keySet()) {
            tempText.append(" ").append(i).append("\t").append(this.toTypeList.get(i)).append("\n");
        }
        listDisplay.setText(tempText.toString());
    }


    // Set text area to all the words guessed
    // @param
    // @return void
    public void updateToGuessedWords() {
        StringBuilder temp = new StringBuilder();
        for (String i: wordsGuessed) {
            temp.append(" ").append(i).append("\n");
        }
        listDisplay.setText(temp.toString());
    }


    // Checks if input typed is in the list and return amount of points it gains
    // @param s
    // @return int
    public int inList (String s) {
        int points = -1;
            if (this.toTypeList.containsKey(s)) {
                points = this.toTypeList.get(s);
                this.toTypeList.remove(s); // Remove the word guessed from the list and update text
                updateText();
                wordsGuessed.add(s);
            }
        return points;
    }


    // get method
    // @param
    // @return JTextArea
    public JTextArea getListDisplay(){
        return this.listDisplay;
    }


    // Generate a value for the word
    // @param string
    // @return int
    private int getWordValue(String string) {
        int points = 0;
        // If it has a first capital letter, it gets a point increase
        if (Character.isUpperCase(string.charAt(0))) points++;

        // The length of the word affects how points can be gained
        switch (string.length()) {
            case 3: case 4:
                points += 3;
                break;
            case 5: case 6: case 7:
                points += 4;
                break;
            case 10:
                points += 7;
                break;
            default:
                points += 5;
        }

        return points;
    }


    // Get method
    // @param
    // @return HashMap<String, Integer>
    public HashMap<String, Integer> getToTypeList() {
        return toTypeList;
    }


    // Get Method
    // @param
    // @return HashSet<String>
    public HashSet<String> getWordsGuessed() {
        return wordsGuessed;
    }
}
