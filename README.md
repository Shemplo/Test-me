# 📋 Test me

This is applications that loads questions in _JSON_ format and then randomly asks user to answer for some selected question in test format. For example, it can be helpful when you are preparing for exam in University or just started to learn something new.

### Requirements

* **JRE 13+** (Actually it can be run even under JRE 11, for this change `source` and `target` versions to 11 in `pom.xml` file before building project)

### Format of `questions.json` file

File must match such format:
```json
{
  "name": "name of this set of questions", //_optional
  "order": "LINEAR|LOOP|RANDOM", //_optional,_RANDOM_by_default
  "questions": [
    ...
  ]
}
```

Formats of question objects:

* One correct option

```json
{
  "question": "Content of the question. It may contain \n to break line",
  "answer-type": "SINGLE",
  "difficulty": "**", //_from_1_to_5_star_characters_for_show_difficulty_of_question_(optional)
  "options": [
    "answer option 1",
    "answer option 2",
    ...
  ],
  "answer": [0], //_index_of_correct_option_in_`options`_array_(starts_from_0,_must_constain_single_value)
  "comment": "String that will be shown if wrong answer was selected" //_optional
}
```

* Several correct options

```json
{
  "question": "Content of the question. It may contain \n to break line",
  "answer-type": "SEVERAL",
  "difficulty": "***", //_from_1_to_5_star_characters_for_show_difficulty_of_question_(optional)
  "options": [
    "answer option 1",
    "answer option 2",
    ...
  ],
  "answer": [0, 1], //_indices_of_correct_options_in_`options`_array_(starts_from_0)
  "comment": "String that will be shown if wrong answer was selected" //_optional
}
```

* Custom text input

```json
{
  "question": "Content of the question. It may contain \n to break line",
  "answer-type": "pattern",
  "difficulty": "*", //_from_1_to_5_star_characters_for_show_difficulty_of_question_(optional)
  "options": [ //_answer_is_correct_if_it_matches_at_least_one_regular_expression_from_`options`
    "regular expression 1",
    ...
  ],
  "comment": "String that will be shown if wrong answer was entered" //_optional
}
```
