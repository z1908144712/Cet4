package com.example.bishe.cet4.object;

/**
 * Created by Skywilling on 2018/1/3.
 */

public class Word {
    private int id;
    private String english;
    private String chinese;
    private String phonetic;
    private String example;

    public Word(int id, String english, String chinese, String phonetic, String example) {
        this.id = id;
        this.english = english;
        this.chinese = chinese;
        this.phonetic = phonetic;
        this.example = example;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEnglish() {
        return english;
    }

    public void setEnglish(String english) {
        this.english = english;
    }

    public String getChinese() {
        return chinese;
    }

    public void setChinese(String chinese) {
        this.chinese = chinese;
    }

    public String getPhonetic() {
        return phonetic;
    }

    public void setPhonetic(String phonetic) {
        this.phonetic = phonetic;
    }

    public String getExample() {
        return example;
    }

    public void setExample(String example) {
        this.example = example;
    }
}
