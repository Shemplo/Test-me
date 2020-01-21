package ru.shemplo.tm.gui;

import java.io.InputStream;
import java.util.*;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import ru.shemplo.tm.RunTestMe;
import ru.shemplo.tm.entity.Question;
import ru.shemplo.tm.loader.QuestionsLoader;

public class AppWindow extends Application {

    public static final String STATISTICS_FORMAT = "Session: %d questions, %d correct (%d%%)";
    public static final String TITLE_FORMAT = "Test me ( v0.0.1 ) | %s";
    
    private final MouseEvent fakeMouseEvent = new MouseEvent (
        MouseEvent.MOUSE_CLICKED, 0, 0, 0, 0, MouseButton.PRIMARY, 1, 
        false, false, false, false, false, false, false, false, false, 
        false, false, false, null
    );
    
    private final InputStream iconIS = AppWindow.class
          . getResourceAsStream ("/gfx/question.png");
    
    private Stage stage;
    private Scene scene;
    
	@Override
	public void start (Stage stage) throws Exception {
		this.stage = stage;
		
		scene = new Scene (createLayout ());
		scene.setOnKeyPressed (ke -> {
		    if (KeyCode.C.equals (ke.getCode ())) {
		        checkButton.getOnMouseClicked ().handle (fakeMouseEvent);
		    } else if (KeyCode.N.equals (ke.getCode ())) {
		        nextQuestionButton.getOnMouseClicked ().handle (fakeMouseEvent);
		    }
		});
	    nextQuestion ();
		
	    if (iconIS != null) {	        
	        stage.getIcons ().add (new Image (iconIS));
	    }
	    
	    stage.setTitle (String.format (TITLE_FORMAT, 
            questionsLoader.getPackName ()));
	    stage.setResizable (false);
		stage.setScene (scene);
		stage.sizeToScene ();
	    this.stage.show ();
	}
	
	private Label commentLabel, statisticsLabel;
	private TextArea questionContent;
	private VBox optionsBox;
	
	private Button checkButton, nextQuestionButton, historyButton;
	
	private Parent createLayout () {
	    final VBox mainContainer = new VBox (8);
	    mainContainer.setPadding (new Insets (8));
	    mainContainer.setMinWidth (550);
	    
	    questionContent = new TextArea ("");
	    questionContent.setFocusTraversable (false);
	    questionContent.setEditable (false);
	    questionContent.setMaxHeight (100);
	    mainContainer.getChildren ().add (questionContent);
	    
	    final Label label1 = new Label ("Choose correct answer from options below:");
	    mainContainer.getChildren ().add (label1);
	    
	    optionsBox = new VBox (8);
	    mainContainer.getChildren ().add (optionsBox);
	    
	    commentLabel = new Label ("");
	    mainContainer.getChildren ().add (commentLabel);
	    
	    final HBox buttonsLine = new HBox (8);
	    buttonsLine.setAlignment (Pos.CENTER_LEFT);
	    mainContainer.getChildren ().add (buttonsLine);
	    
	    checkButton = new Button ("_Check answer");
	    checkButton.setMnemonicParsing (true);
	    checkButton.setOnMouseClicked (me -> {
	        Platform.runLater (() -> { commentLabel.setText (""); });
	        
	        Question question = questionsLoader.getQuestions ().get (questionIndex);
	        Set <Integer> answer = question.getCorrectOptions ();
	        
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
	        
	        int answersFound = 0;
	        for (Node node : optionsBox.getChildren ()) {
                final AnswerOption option = (AnswerOption) node;
                
                boolean isAnswer = answer.contains (option.getIndex ());
                
                if (isAnswer) { 
                    option.markAsCorrect (); 
                    answersFound++;
                }
                
                if ((option.isSelected () && !isAnswer)) {
                    option.markAsError ();
                    isError = true;
                }
                
                option.fixSelection ();
            }
	        
	        if ((isError || answersFound != answer.size ()) 
	                && question.getComment () != null) {
	            Platform.runLater (() -> {
	                commentLabel.setText (question.getComment ());
	            });
	        }
	        
	        // updating statistics of answers for the questions
	        if (!isError && answersFound == answer.size ()) { 
	            correctAnswers++; 
            }
	        totalQuestions++;
	        
	        Platform.runLater (() -> {
	            statisticsLabel.setText (String.format (STATISTICS_FORMAT, 
                    (int) totalQuestions, (int) correctAnswers, 
                    (int) (correctAnswers / totalQuestions * 100)
                ));
	            nextQuestionButton.setDisable (false);
	            checkButton.setDisable (true);
	        });
	    });
	    buttonsLine.getChildren ().add (checkButton);
	    
	    nextQuestionButton = new Button ("_Next question");
	    nextQuestionButton.setMnemonicParsing (true);
	    nextQuestionButton.setOnMouseClicked (me -> {
	        nextQuestion ();
	    });
	    buttonsLine.getChildren ().add (nextQuestionButton);
	    
	    final HBox wideBox = new HBox ();
	    HBox.setHgrow (wideBox, Priority.ALWAYS);
	    buttonsLine.getChildren ().add (wideBox);
	    
	    statisticsLabel = new Label (String.format (STATISTICS_FORMAT, 0, 0, 100));
	    buttonsLine.getChildren ().add (statisticsLabel);
	    
	    //historyButton = new Button ("_Open history");
	    //historyButton.setMnemonicParsing (true);
	    //buttonsLine.getChildren ().add (historyButton);
	    
	    return mainContainer;
	}
	
	private final QuestionsLoader questionsLoader = QuestionsLoader.getInstance ();
	private final Random random = new Random ();
	private int questionIndex = -1;
	
	private final Queue <Integer> uniqueQueue = new LinkedList <> ();
	private final Set <Integer> uniquePool = new HashSet <> ();
	
	private double totalQuestions = 0, correctAnswers = 0;
	
	private void nextQuestion () {
	    int r = questionIndex;
	    do {	        
	        r = random.nextInt (questionsLoader.getQuestionsNumber ());
	    } while (uniquePool.contains (r));
	    
	    Question question = questionsLoader.getQuestions ().get (questionIndex = r);
	    
	    uniqueQueue.add (questionIndex);
	    uniquePool.add (questionIndex);
	    if (uniqueQueue.size () > RunTestMe.UNIQUE_QUESTIONS_IN_A_ROW) {
	        uniquePool.remove (uniqueQueue.poll ());
	    }
	    
	    final List <AnswerOption> options = new ArrayList <> ();
	    final ToggleGroup answersGroup = new ToggleGroup ();
	    
	    for (int i = 0; i < question.getOptions ().size (); i++) {
	        final String option = question.getOptions ().get (i);
	        options.add (new AnswerOption (i, question, option, answersGroup));
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
