# 📋 Test me

This is applications that loads questions in _JSON_ format and then randomly asks user to answer for some selected question in test format. For example, it can be helpful when you are preparing for exam in University or just started to learn something new.

### Format of `questions.json` file

File must match such format:
```json
{
  "name": "name of this set of questions", //_optional
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
  "options": [
    "answer option 1",
    "answer option 2",
    ...
  ],
  "answer": 0, //_index_of_correct_answer_option_in_`options`_array_(starts_from_0)
  "comment": "String that will be shown if wrong answer was selected" //_optional
}
```
