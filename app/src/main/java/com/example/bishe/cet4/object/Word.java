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
    private String main_chinese;
    private String us_phonetic;
    private String uk_phonetic;

    public Word(Integer id, String english, String chinese, String phonetic, String main_chinese, String us_phonetic, String uk_phonetic) {
        this.id = id;
        this.english = english;
        this.chinese = chinese;
        this.phonetic = phonetic;
        this.main_chinese = main_chinese;
        this.us_phonetic = us_phonetic;
        this.uk_phonetic = uk_phonetic;
    }

    public String getMain_chinese() {
        return main_chinese;
    }

    public void setMain_chinese(String main_chinese) {
        this.main_chinese = main_chinese;
    }

    public String getUs_phonetic() {
        return us_phonetic;
    }

    public void setUs_phonetic(String us_phonetic) {
        this.us_phonetic = us_phonetic;
    }

    public String getUk_phonetic() {
        return uk_phonetic;
    }

    public void setUk_phonetic(String uk_phonetic) {
        this.uk_phonetic = uk_phonetic;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Word word = (Word) o;
        return  id.equals(word.id) &&
                english.equals(word.english) &&
                chinese.equals(word.chinese) &&
                phonetic.equals(word.phonetic) &&
                main_chinese.equals(word.main_chinese)&&
                uk_phonetic.equals(word.uk_phonetic)&&
                us_phonetic.equals(word.us_phonetic);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, english, chinese, phonetic);
    }

    @Override
    public String toString() {
        return "Word{" +
                "id=" + id +
                ", english='" + english + '\'' +
                ", chinese='" + chinese + '\'' +
                ", phonetic='" + phonetic + '\'' +
                '}';
    }
}
