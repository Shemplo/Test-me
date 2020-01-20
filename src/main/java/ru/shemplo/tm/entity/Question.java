package ru.shemplo.tm.entity;

import java.util.List;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter @Setter
@RequiredArgsConstructor
public class Question {

	@NonNull private String question;
	
	private List <String> options;
	
	private int correctOption; // answer
	
	private String comment;
	
}
