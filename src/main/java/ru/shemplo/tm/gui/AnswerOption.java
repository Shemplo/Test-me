package ru.shemplo.tm.gui;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import lombok.Getter;

@Getter
public class AnswerOption extends HBox {
    
    private final RadioButton radio;
    private final Label mark;
    private final int index;
    
    public AnswerOption (int index, String content, ToggleGroup group) {
        super (8);
        
        this.index = index;
        
        radio = new RadioButton (content);
        radio.setToggleGroup (group);
        radio.setWrapText (true);
        getChildren ().add (radio);
        
        mark = new Label ();
        getChildren ().add (mark);
        
        setAlignment (Pos.TOP_LEFT);
    }
    
    public boolean isSelected () {
        return radio.isSelected ();
    }
    
    public void markAsCorrect () {
        mark.setText ("(correct)");
    }
    
    public void markAsError () {
        mark.setText ("(error)");
    }
    
    public void fixSelection () {
        radio.setDisable (true);
    }
    
}
