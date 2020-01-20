package ru.shemplo.tm.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import ru.shemplo.tm.entity.Question;
import ru.shemplo.tm.loader.QuestionsLoader;

public class AppWindow extends Application {

    private Stage stage;
    private Scene scene;
    
	@Override
	public void start (Stage stage) throws Exception {
		this.stage = stage;
		
		scene = new Scene (createLayout ());
	    nextQuestion ();
		
	    stage.setTitle ("Learn Java Actions | v0.0.1");
		stage.setScene (scene);
		stage.sizeToScene ();
	    this.stage.show ();
	}
	
	private TextArea questionContent;
	private Label commentLabel;
	private VBox optionsBox;
	
	private Button checkButton, nextQuestionButton;
	
	private Parent createLayout () {
	    final VBox mainContainer = new VBox (8);
	    mainContainer.setPadding (new Insets (8));
	    
	    questionContent = new TextArea ("");
	    questionContent.setEditable (false);
	    questionContent.setMaxHeight (100);
	    mainContainer.getChildren ().add (questionContent);
	    
	    final Label label1 = new Label ("Choose correct answer:");
	    mainContainer.getChildren ().add (label1);
	    
	    optionsBox = new VBox (8);
	    mainContainer.getChildren ().add (optionsBox);
	    
	    commentLabel = new Label ("");
	    mainContainer.getChildren ().add (commentLabel);
	    
	    final HBox buttonsLine = new HBox (8);
	    mainContainer.getChildren ().add (buttonsLine);
	    
	    checkButton = new Button ("Check answer");
	    checkButton.setOnMouseClicked (me -> {
	        Platform.runLater (() -> { commentLabel.setText (""); });
	        
	        Question question = questionsLoader.getQuestions ().get (questionIndex);
	        int answer = question.getCorrectOption ();
	        
	        boolean isError = false, isAnySelected = false;
	        for (Node node : optionsBox.getChildren ()) {
	            final AnswerOption option = (AnswerOption) node;
	            isAnySelected |= option.isSelected ();
	        }
	        
	        if (!isAnySelected) {
	            Platform.runLater (() -> {
                    commentLabel.setText ("Select one options as an asnwer");
                });
	            
	            return;
	        }
	        
	        for (Node node : optionsBox.getChildren ()) {
                final AnswerOption option = (AnswerOption) node;
                boolean isAnswer = answer == option.getIndex ();
                
                if (isAnswer) { option.markAsCorrect (); }
                if ((option.isSelected () && !isAnswer)) {
                    option.markAsError ();
                    isError = true;
                }
                
                option.fixSelection ();
            }
	        
	        if (isError && question.getComment () != null) {
	            Platform.runLater (() -> {
	                commentLabel.setText (question.getComment ());
	            });
	        }
	        
	        Platform.runLater (() -> {
	            nextQuestionButton.setDisable (false);
	            checkButton.setDisable (true);
	        });
	    });
	    buttonsLine.getChildren ().add (checkButton);
	    
	    nextQuestionButton = new Button ("Next question");
	    nextQuestionButton.setOnMouseClicked (me -> {
	        nextQuestion ();
	    });
	    buttonsLine.getChildren ().add (nextQuestionButton);
	    
	    return mainContainer;
	}
	
	private final QuestionsLoader questionsLoader = QuestionsLoader.getInstance ();
	private final Random random = new Random ();
	private int questionIndex = -1;
	
	private void nextQuestion () {
	    int r = questionIndex;
	    do {	        
	        r = random.nextInt (questionsLoader.getQuestionsNumber ());
	    } while (r == questionIndex);
	    
	    Question question = questionsLoader.getQuestions ().get (questionIndex = r);
	    
	    final List <AnswerOption> options = new ArrayList <> ();
	    final ToggleGroup answersGroup = new ToggleGroup ();
	    
	    for (int i = 0; i < question.getOptions ().size (); i++) {
	        options.add (new AnswerOption (i, question.getOptions ().get (i), answersGroup));
	    }
	    
	    Collections.shuffle (options);
	    
	    Platform.runLater (() -> {
	        questionContent.setText (question.getQuestion ());
	        checkButton.setDisable (false);
	        commentLabel.setText ("");
	        
	        optionsBox.getChildren ().clear ();
	        optionsBox.getChildren ().addAll (options);
	        
	        stage.sizeToScene ();
	    });
	}

}
