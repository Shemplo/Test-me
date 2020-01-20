package ru.shemplo.tm.gui;

import java.util.Optional;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import lombok.Getter;

@Getter
public class AnswerOption extends HBox {
    
    private static final Image correctIcon = Optional.ofNullable (
        AnswerOption.class.getResourceAsStream ("/gfx/tick.png")
    ).map (is -> new Image (is, 15d, 15d, true, false)).orElse (null);
    
    private static final Image wrongIcon = Optional.ofNullable (
        AnswerOption.class.getResourceAsStream ("/gfx/quit.png")
    ).map (is -> new Image (is, 15d, 15d, true, false)).orElse (null);
    
    private final RadioButton radio;
    private final Label mark;
    private final int index;
    
    public AnswerOption (int index, String content, ToggleGroup group) {
        super (8);
        
        this.index = index;
        
        mark = new Label ();
        mark.setMinWidth (16);
        getChildren ().add (mark);
        
        radio = new RadioButton (content);
        radio.setToggleGroup (group);
        radio.setWrapText (true);
        getChildren ().add (radio);
        
        setAlignment (Pos.TOP_LEFT);
        setMaxWidth (1024);
    }
    
    public boolean isSelected () {
        return radio.isSelected ();
    }
    
    public void markAsCorrect () {
        if (correctIcon != null) {            
            mark.setGraphic (new ImageView (correctIcon));
        } else {
            mark.setText ("+");
        }
    }
    
    public void markAsError () {
        if (wrongIcon != null) {            
            mark.setGraphic (new ImageView (wrongIcon));
        } else {
            mark.setText ("-");
        }
    }
    
    public void fixSelection () {
        radio.setDisable (true);
    }
    
}
