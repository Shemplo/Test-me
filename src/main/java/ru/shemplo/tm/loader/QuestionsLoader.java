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
    
    @Getter private List <Question> questions = List.of ();
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
        questions = new ArrayList <> ();
        
        for (int i = 0; i < questionsArray.length (); i++) {
            JSONObject questionObject = questionsArray.getJSONObject (i);
            try {
                questions.add (parseQuestion (questionObject, i));
            } catch (IllegalStateException ise) {
                // action is not required (question is ignored)
                System.out.println (ise.getMessage ());
            }
        }
        
        System.out.println (String.format ("[INFO] %d questions was loaded", questions.size ()));
        questions = Collections.unmodifiableList (questions);
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
        assertFieldInObject (object, "question", index);
        assertFieldInObject (object, "options", index);
        assertFieldInObject (object, "answer", index);
        
        final QuestionAnswerType answerType = fetchAnswerType (object, index);
        final Set <Integer> answer = fetchAnswer (object, answerType, index);
        
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
    
    public int getQuestionsNumber () {
        return questions.size ();
    }
    
    private void assertFieldInObject (JSONObject object, String field, int index) {
        if (object.has (field)) { return; }
        
        String message = String.format ("[WARN] Object #%d doesn't have `%s` field", index + 1, field);
        throw new IllegalStateException (message);
    }
    
    private static QuestionAnswerType fetchAnswerType (JSONObject object, int index) {
        QuestionAnswerType answerType = QuestionAnswerType.SINGLE;
        if (object.has ("answer-type")) {
            try {
                String value = object.getString ("answer-type").toUpperCase ();
                answerType = QuestionAnswerType.valueOf (value);
            } catch (IllegalArgumentException iae) {
                // it's expected behavior
            } 
        }
        
        return answerType;
    }
    
    private static Set <Integer> fetchAnswer (JSONObject object, QuestionAnswerType answerType, int index) {
        final Set <Integer> answer = new HashSet <> ();
        
        JSONArray answersArray = object.getJSONArray ("answer");
        if (QuestionAnswerType.SINGLE.equals (answerType) && answersArray.length () != 1) {
            String message = String.format ("[WARN] Answer of object #%d should contain 1 correct option", index + 1);
            throw new IllegalStateException (message);
        }
        
        for (int i = 0; i < answersArray.length (); i++) {
            int correctOption = answersArray.getInt (i);
            answer.add (correctOption);
        }
        
        return answer;
    }
    
}
