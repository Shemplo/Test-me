package ru.shemplo.tm.entity;

import java.util.List;
import java.util.Set;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter @Setter
@RequiredArgsConstructor
public class Question {

    private final int id;
    
	@NonNull private String question;
	
	@NonNull private QuestionAnswerType answerType;
	
	private List <String> options;
	
	private Set <Integer> correctOptions;
	
	private String comment;
	
	private String difficulty;
	
	public boolean isOptionsSelection () {
	    return QuestionAnswerType.SINGLE.equals (answerType) 
	        || QuestionAnswerType.SEVERAL.equals (answerType);
	}
	
}
