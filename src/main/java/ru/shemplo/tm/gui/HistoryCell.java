package ru.shemplo.tm.gui;

import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lombok.RequiredArgsConstructor;
import ru.shemplo.tm.entity.HistoryLogEntry;
import ru.shemplo.tm.entity.Question;
import ru.shemplo.tm.entity.QuestionAnswerType;

@RequiredArgsConstructor
public class HistoryCell extends ListCell <HistoryLogEntry> {
    
    @SuppressWarnings ("unused")
    private final HistoryWindow window;
    
    {
        setMaxWidth (512);
    }
    
    @Override
    protected void updateItem (HistoryLogEntry item, boolean empty) {
        super.updateItem (item, empty);
        
        if (item == null || empty) {
            setGraphic (null);
            return;
        }
        
        final VBox root = new VBox (8);
        root.setMaxWidth (480);
        
        final Question question = item.getQuestion ();
        Label questionHeader = new Label ("Question #" + question.getId () 
            + (question.getDifficulty () != null ? ", " + question.getDifficulty () : ""));
        root.getChildren ().add (questionHeader);
        
        Label questionLabel = new Label (question.getQuestion ());
        questionLabel.setEllipsisString ("...");
        questionLabel.setMaxWidth (480);
        root.getChildren ().add (questionLabel);
        
        if (item.getQuestion ().isOptionsSelection ()) {
            for (int selectedOption : item.getOptions ()) {
                final Image verdictImage = selectedOption > 0 
                    ? AnswerOption.getCorrectIcon () 
                    : AnswerOption.getWrongIcon ();
                int index = Math.abs (selectedOption) - 1;
                
                final HBox line = new HBox (8);
                root.getChildren ().add (line);
                
                line.getChildren ().add (new ImageView (verdictImage));
                
                String optionValue = question.getOptions ().get (index);
                Label answerContent = new Label (optionValue); 
                answerContent.setMaxWidth (480 - 16);
                answerContent.setWrapText (true);
                line.getChildren ().add (answerContent);
            }
        } else if (QuestionAnswerType.PATTERN.equals (item.getQuestion ().getAnswerType ())) {
            final Image verdictImage = item.getIsCorrect ()
                ? AnswerOption.getCorrectIcon () 
                : AnswerOption.getWrongIcon ();
                
            final HBox line = new HBox (8);
            root.getChildren ().add (line);
            
            line.getChildren ().add (new ImageView (verdictImage));
            
            Label answerContent = new Label (item.getValue ()); 
            answerContent.setMaxWidth (480);
            answerContent.setWrapText (true);
            line.getChildren ().add (answerContent);
        }
        
        setGraphic (root);
    }
    
}
