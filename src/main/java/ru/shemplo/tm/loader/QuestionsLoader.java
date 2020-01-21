package ru.shemplo.tm.loader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

import org.json.JSONArray;
import org.json.JSONObject;

import lombok.Getter;
import ru.shemplo.tm.entity.Question;
import ru.shemplo.tm.entity.QuestionAnswerType;

public class QuestionsLoader {
    
    private static final String QUESTIONS_PATH = "/questions.json";
    
    private static QuestionsLoader instance;
    
    public synchronized static QuestionsLoader getInstance () {
        if (instance == null) {
            instance = new QuestionsLoader ();
        }
        
        return instance;
    }
    
    private List <Question> questions = new ArrayList <> ();
    @Getter private String packName = "(default pack)";
    private boolean loaded = false;
    
    private QuestionsLoader () {}
    
    public void loadQuestions () throws IOException {
        if (loaded) { return; }
        
        final String content = readResourceContent ();
        
        final JSONObject packObject = new JSONObject (content);
        if (!packObject.has ("questions")) {
            String message = "Questions pack doesn't have `questions` field";
            throw new IOException (message);
        }
        
        if (packObject.has ("name")) {
            packName = packObject.getString ("name");
        }
        
        final JSONArray questionsArray = packObject.getJSONArray ("questions");
        for (int i = 0; i < questionsArray.length (); i++) {
            JSONObject questionObject = questionsArray.getJSONObject (i);
            Optional.ofNullable (parseQuestion (questionObject, i))
                    .ifPresent  (questions::add);
        }
        
        System.out.println (String.format ("[INFO] %d questions was loaded", questions.size ()));
        loaded = true;
    }
    
    private String readResourceContent () throws IOException {
        var is = QuestionsLoader.class.getResourceAsStream (QUESTIONS_PATH);
        if (is == null) {
            throw new IOException ("Questions file not found in resources");
        }
        
        StringBuilder sb = new StringBuilder ();
        char [] buffer = new char [1 << 10];
        
        try (
            var r  = new InputStreamReader (is, StandardCharsets.UTF_8);
        ) {
            int read = -1;
            while ((read = r.read (buffer)) != -1) {
                sb.append (buffer, 0, read);
            }
        } 
        
        return sb.toString ();
    }
    
    private Question parseQuestion (JSONObject object, int index) {
        if (!object.has ("question")) {
            System.out.println (String.format ("[WARN] Object #%d doesn't have `question` field", index + 1));
            return null;
        }
        
        if (!object.has ("options")) {
            System.out.println (String.format ("[WARN] Object #%d doesn't have `options` field", index + 1));
            return null;
        }
        
        if (!object.has ("answer")) {
            System.out.println (String.format ("[WARN] Object #%d doesn't have `answer` field", index + 1));
            return null;
        }
        
        QuestionAnswerType answerType = QuestionAnswerType.SINGLE;
        if (object.has ("answer-type")) {
            try {
                String value = object.getString ("answer-type");
                answerType = QuestionAnswerType.valueOf (value);
            } catch (IllegalArgumentException iae) {
                // it's expected behavior
            } 
        }
        
        final Set <Integer> answer = new HashSet <> ();
        
        JSONArray answersArray = object.getJSONArray ("answer");
        if (QuestionAnswerType.SINGLE.equals (answerType) && answersArray.length () != 1) {
            System.out.println (String.format (
                "[WARN] Answer of object #%d should contain 1 correct option", 
                index + 1
            ));
            return null;
        }
        
        for (int i = 0; i < answersArray.length (); i++) {
            int correctOption = answersArray.getInt (i);
            answer.add (correctOption);
        }
        
        Question question = new Question (object.getString ("question"), answerType);
        question.setCorrectOptions (Collections.unmodifiableSet (answer));
        question.setOptions (new ArrayList <> ());
        
        if (object.has ("comment")) {
            question.setComment (object.getString ("comment"));
        }
        
        JSONArray options = object.getJSONArray ("options");
        for (int i = 0; i < options.length (); i++) {
            question.getOptions ().add (options.getString (i));
        }

        question.setOptions (Collections.unmodifiableList (question.getOptions ()));
        return question;
    }
    
    public List <Question> getQuestions () {
        return Collections.unmodifiableList (questions);
    }
    
    public int getQuestionsNumber () {
        return questions.size ();
    }
    
}
