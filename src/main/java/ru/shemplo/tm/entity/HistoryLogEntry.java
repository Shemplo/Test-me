package ru.shemplo.tm.entity;

import java.util.List;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter @Setter
@RequiredArgsConstructor
public class HistoryLogEntry {
    
    private final Question question;
    
    private List <Integer> options;
    
    private String value;
    
    @NonNull private Boolean isCorrect;
    
    public void setCorrect (boolean isCorrect) {
        if (!QuestionAnswerType.INPUT.equals (question.getAnswerType ())) {
            return; // only INPUT answers can be validated manualy
        }
        
        this.isCorrect = isCorrect;
    }
    
    public boolean isValid () {
        return options != null || value != null;
    }
    
}
