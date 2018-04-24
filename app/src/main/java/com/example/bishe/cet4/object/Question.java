package com.example.bishe.cet4.object;

public class Question {
    private String question;
    private int right_answer;
    private String[] items;

    public Question(){}


    public String getQuestion() {
        return question;
    }

    public void setQuestion(String english) {
        this.question = english;
    }

    public int getRight_answer() {
        return right_answer;
    }

    public void setRight_answer(int right_answer) {
        this.right_answer = right_answer;
    }

    public String[] getItems() {
        return items;
    }

    public void setItems(String[] items) {
        this.items = items;
    }
}
