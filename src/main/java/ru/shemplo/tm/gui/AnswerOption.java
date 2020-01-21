package ru.shemplo.tm.gui;

import java.util.Optional;

import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import lombok.Getter;
import ru.shemplo.tm.entity.Question;
import ru.shemplo.tm.entity.QuestionAnswerType;

@Getter
public class AnswerOption extends HBox {
    
    private static final Image correctIcon = Optional.ofNullable (
        AnswerOption.class.getResourceAsStream ("/gfx/tick.png")
    ).map (is -> new Image (is, 15d, 15d, true, false)).orElse (null);
    
    private static final Image wrongIcon = Optional.ofNullable (
        AnswerOption.class.getResourceAsStream ("/gfx/quit.png")
    ).map (is -> new Image (is, 15d, 15d, true, false)).orElse (null);
    
    private final CheckBox checkbox;
    private final RadioButton radio;
    private final TextField input;
    private final Label mark;
    private final int index;
    
    public AnswerOption (int index, Question question, String content, ToggleGroup group) {
        super (8);
        
        this.index = index;
        
        mark = new Label ();
        mark.setMinWidth (16);
        getChildren ().add (mark);
        
        final QuestionAnswerType answerType = question.getAnswerType ();
        if (QuestionAnswerType.SINGLE.equals (answerType)) {            
            radio = new RadioButton (content);
            radio.setToggleGroup (group);
            radio.setWrapText (true);
            getChildren ().add (radio);
            
            checkbox = null;
            input = null;
        } else if (QuestionAnswerType.SEVERAL.equals (answerType)) {
            checkbox = new CheckBox (content);
            getChildren ().add (checkbox);
            
            input = null;
            radio = null;
        } else if (QuestionAnswerType.PATTERN.equals (answerType)
                || QuestionAnswerType.INPUT.equals (answerType)) {
            input = new TextField ("");
            HBox.setHgrow (input, Priority.ALWAYS);
            getChildren ().add (input);
            
            checkbox = null;
            radio = null;
        } else {
            checkbox = null;
            input = null;
            radio = null;
        }
        
        setAlignment (Pos.TOP_LEFT);
        setMaxWidth (1024);
    }
    
    public boolean isSelected () {
        if (checkbox != null) {
            return checkbox.isSelected ();
        }
        if (radio != null) {
            return radio.isSelected ();
        }
        return false;
    }
    
    public String getValue () {
        if (input != null) {
            return input.getText ().strip ();
        }
        return null;
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
        Optional.ofNullable (checkbox).ifPresent (a -> a.setDisable (true));
        Optional.ofNullable (input).ifPresent (a -> a.setDisable (true));
        Optional.ofNullable (radio).ifPresent (a -> a.setDisable (true));
    }
    
}
