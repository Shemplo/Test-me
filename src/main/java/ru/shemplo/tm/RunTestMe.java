package ru.shemplo.tm;

import java.io.IOException;

import javax.swing.JOptionPane;

import javafx.application.Application;
import ru.shemplo.tm.gui.AppWindow;
import ru.shemplo.tm.loader.QuestionsLoader;

public class RunTestMe {
    
    public static void main (String ... args) {
        try {
            QuestionsLoader.getInstance ().loadQuestions ();
        } catch (IOException e) {
            final String message = "Failed to load questions from resources\n"
                + "Check that this version of application was built correctly\n"
                + "\n"
                + "If you don't know what to do write for help to Shemplo";
            final String title = "Questions Loader Error";
            
            JOptionPane.showMessageDialog (null, message, title, JOptionPane.ERROR_MESSAGE);
            return; // stopping application
        }
        
        Application.launch (AppWindow.class, args);
    }
    
}
