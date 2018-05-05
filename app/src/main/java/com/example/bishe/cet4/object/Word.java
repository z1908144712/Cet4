package com.example.bishe.cet4.object;

import java.util.Objects;

import cn.bmob.v3.BmobObject;

/**
 * Created by Skywilling on 2018/1/3.
 */

public class Word extends BmobObject{
    private Integer id;
    private String english;
    private String chinese;
    private String phonetic;
    private String example;

    public Word(Integer id, String english, String chinese, String phonetic, String example) {
        this.id = id;
        this.english = english;
        this.chinese = chinese;
        this.phonetic = phonetic;
        this.example = example;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Word word = (Word) o;
        return id==word.id &&
                english.equals(word.english) &&
                chinese.equals(word.chinese) &&
                phonetic.equals(word.phonetic) &&
                example.equals(word.example);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, english, chinese, phonetic, example);
    }

    @Override
    public String toString() {
        return "Word{" +
                "id=" + id +
                ", english='" + english + '\'' +
                ", chinese='" + chinese + '\'' +
                ", phonetic='" + phonetic + '\'' +
                ", example='" + example + '\'' +
                '}';
    }
}
