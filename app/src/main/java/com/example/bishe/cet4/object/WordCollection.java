package com.example.bishe.cet4.object;

public class WordCollection {
    private String english;
    private int wrong_num;

    public WordCollection(String english, int wrong_num) {
        this.english = english;
        this.wrong_num = wrong_num;
    }

    public String getEnglish() {
        return english;
    }

    public void setEnglish(String english) {
        this.english = english;
    }

    public int getWrong_num() {
        return wrong_num;
    }

    public void setWrong_num(int wrong_num) {
        this.wrong_num = wrong_num;
    }
}
