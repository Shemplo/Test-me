package ru.shemplo.tm.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class HistoryLogger {
    
    private final List <HistoryLogEntry> history = new ArrayList <> ();
    
    public void log (Question question, List <Integer> options, String value, boolean isCorrect) {
        HistoryLogEntry entry = new HistoryLogEntry (question, isCorrect);
        Optional.ofNullable (options).ifPresent (entry::setOptions);
        Optional.ofNullable (value).ifPresent (entry::setValue);
        
        if (entry.isValid ()) { history.add (entry); }
    }
    
    public ObservableList <HistoryLogEntry> getObservableHistory () {
        return FXCollections.observableArrayList (history);
    }
    
}
