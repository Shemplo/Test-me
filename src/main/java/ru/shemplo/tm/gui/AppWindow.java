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
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import lombok.Getter;
import ru.shemplo.tm.RunTestMe;
import ru.shemplo.tm.entity.AnswerNotExistsException;
import ru.shemplo.tm.entity.HistoryLogger;
import ru.shemplo.tm.entity.Question;
import ru.shemplo.tm.entity.QuestionAnswerType;
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
    
    @Getter
    private final Image windowIcon = Optional.ofNullable (iconIS)
          . map (Image::new).orElse (null);
    
    @Getter private Stage stage;
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
		
	    if (windowIcon != null) {	        
	        stage.getIcons ().add (windowIcon);
	    }
	    
	    stage.setTitle (String.format (TITLE_FORMAT, 
            questionsLoader.getPackName ()));
	    stage.setResizable (false);
		stage.setScene (scene);
		stage.sizeToScene ();
	    this.stage.show ();
	}
	
	private Label commentLabel, statisticsLabel, questionNameLabel, 
	    questionDifficultyLabel;
	private TextArea questionContent;
	private VBox optionsBox;
	
	private Button checkButton, nextQuestionButton, historyButton;
	
	private Parent createLayout () {
	    final VBox mainContainer = new VBox (8);
	    mainContainer.setPadding (new Insets (8));
	    mainContainer.setMinWidth (550);
	    
	    final HBox headerLine = new HBox (8);
	    mainContainer.getChildren ().add (headerLine);
	    
	    questionNameLabel = new Label ("Question #1");
	    headerLine.getChildren ().add (questionNameLabel);
	    
	    final HBox wideBox = new HBox ();
        HBox.setHgrow (wideBox, Priority.ALWAYS);
        headerLine.getChildren ().add (wideBox);
        
        questionDifficultyLabel = new Label ("");
        headerLine.getChildren ().add (questionDifficultyLabel);
	    
	    questionContent = new TextArea ("");
	    questionContent.setFocusTraversable (false);
	    questionContent.setEditable (false);
	    questionContent.setMaxHeight (100);
	    questionContent.setWrapText (true);
	    mainContainer.getChildren ().add (questionContent);
	    
	    final Label label1 = new Label ("Choose correct answer from options below or enter your answer:");
	    mainContainer.getChildren ().add (label1);
	    
	    optionsBox = new VBox (8);
	    mainContainer.getChildren ().add (optionsBox);
	    
	    commentLabel = new Label ("");
	    commentLabel.setTextFill (Color.DARKRED);
	    commentLabel.setWrapText (true);
	    mainContainer.getChildren ().add (commentLabel);
	    
	    final HBox buttonsLine = new HBox (8);
	    buttonsLine.setAlignment (Pos.CENTER_LEFT);
	    mainContainer.getChildren ().add (buttonsLine);
	    
	    checkButton = new Button ("_Check answer");
	    checkButton.setMnemonicParsing (true);
	    checkButton.setOnMouseClicked (me -> {
	        checkAnswer ();
	    });
	    buttonsLine.getChildren ().add (checkButton);
	    
	    nextQuestionButton = new Button ("_Next question");
	    nextQuestionButton.setMnemonicParsing (true);
	    nextQuestionButton.setOnMouseClicked (me -> {
	        nextQuestion ();
	    });
	    buttonsLine.getChildren ().add (nextQuestionButton);
	    
	    final HBox wideBox2 = new HBox ();
	    HBox.setHgrow (wideBox2, Priority.ALWAYS);
	    buttonsLine.getChildren ().add (wideBox2);
	    
	    statisticsLabel = new Label (String.format (STATISTICS_FORMAT, 0, 0, 100));
	    buttonsLine.getChildren ().add (statisticsLabel);
	    
	    historyButton = new Button ("_Open history");
	    historyButton.setMnemonicParsing (true);
	    historyButton.setOnMouseClicked (me -> {
	        HistoryWindow historyWindow = new HistoryWindow (this);
	        historyWindow.show ();
	    });
	    buttonsLine.getChildren ().add (historyButton);
	    
	    return mainContainer;
	}
	
	private final QuestionsLoader questionsLoader = QuestionsLoader.getInstance ();
	@Getter private final HistoryLogger historyLogger = new HistoryLogger ();
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
	        questionNameLabel.setText (String.format (
                "Question #%d", question.getId ()
            ));
	        if (question.getDifficulty () != null) {	            
	            questionDifficultyLabel.setText (String.format (
                    "Difficulty: %s", question.getDifficulty ()
                ));
	        } else {
	            questionDifficultyLabel.setText ("");
	        }
	        questionContent.setText (question.getQuestion ());
	        checkButton.setDisable (false);
	        commentLabel.setText ("");
	        
	        optionsBox.getChildren ().clear ();
	        optionsBox.getChildren ().addAll (options);
	        
	        stage.sizeToScene ();
	    });
	}
	
	private void checkAnswer () {
	    Platform.runLater (() -> { commentLabel.setText (""); });
        
        Question question = questionsLoader.getQuestions ().get (questionIndex);
        
        try {            
            boolean isCorrect = false;
            
            if (question.isOptionsSelection ()) {
                isCorrect = checkOptionsSelectionAnswer (question);
            } else if (QuestionAnswerType.PATTERN.equals (question.getAnswerType ())) {
                isCorrect = checkPatternAnswer (question);
            } else if (QuestionAnswerType.INPUT.equals (question.getAnswerType ())) {
                AnswerOption option = (AnswerOption) optionsBox.getChildren ().get (0);
                historyLogger.log (question, null, option.getValue (), false);
            }
            
            if (!QuestionAnswerType.INPUT.equals (question.getAnswerType ())) {                
                if (!isCorrect) {
                    Platform.runLater (() -> commentLabel.setText (question.getComment ()));
                }
                
                if (isCorrect) { correctAnswers++; }
                totalQuestions++;
            }
            
            Platform.runLater (() -> {
                statisticsLabel.setText (String.format (STATISTICS_FORMAT, 
                    (int) totalQuestions, (int) correctAnswers, 
                    (int) (correctAnswers / totalQuestions * 100)
                ));
                nextQuestionButton.setDisable (false);
                checkButton.setDisable (true);
            });
        } catch (AnswerNotExistsException anee) {
            Platform.runLater (() -> {
                commentLabel.setText ("Select at leat one option as an asnwer or enter answer in the field");
            });
        }
	}
	
	private boolean checkOptionsSelectionAnswer (Question question) {
	    Set <Integer> answer = question.getCorrectOptions ();
        
        boolean isError = false, isAnySelected = false;
        for (Node node : optionsBox.getChildren ()) {
            final AnswerOption option = (AnswerOption) node;
            isAnySelected |= option.isSelected ();
        }
        
        if (!isAnySelected) {
            throw new AnswerNotExistsException ();
        }
        
        List <Integer> selectedOptions = new ArrayList <> ();
        int answersFound = 0;
        
        for (Node node : optionsBox.getChildren ()) {
            final AnswerOption option = (AnswerOption) node;
            
            boolean isAnswer = answer.contains (option.getIndex ());
            
            if (isAnswer) {
                if (option.isSelected ()) {                    
                    selectedOptions.add (option.getIndex () + 1);
                }
                option.markAsCorrect (); 
                answersFound++;
            }
            
            if (option.isSelected () && !isAnswer) {
                selectedOptions.add (-option.getIndex () - 1);
                option.markAsError ();
                isError = true;
            }
            
            option.fixSelection ();
        }
        
	    boolean isCorrect = !isError && answersFound == answer.size ();
	    historyLogger.log (question, selectedOptions, null, isCorrect);
	    return isCorrect;
	}
	
	private boolean checkPatternAnswer (Question question) {
	    AnswerOption option = (AnswerOption) optionsBox.getChildren ().get (0);
        option.fixSelection ();
        
        final String answer = option.getValue ();
        for (String pattern : question.getOptions ()) {
            if (answer.matches (pattern)) {
                historyLogger.log (question, null, answer, true);
                option.markAsCorrect ();
                return true;
            }
        }
        
        historyLogger.log (question, null, answer, false);
        option.markAsError ();
        return false;
	}

}
