package com.example.bishe.cet4.object;


import java.util.Objects;

public class WordCollection{
    private Integer id;
    private String english;
    private Integer wrong_num;

    public WordCollection(Integer id,String english, Integer wrong_num) {
        this.id=id;
        this.english = english;
        this.wrong_num = wrong_num;
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

    public Integer getWrong_num() {
        return wrong_num;
    }

    public void setWrong_num(Integer wrong_num) {
        this.wrong_num = wrong_num;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WordCollection that = (WordCollection) o;
        return id==that.id&&
                english.equals(that.english) &&
                wrong_num.equals(that.wrong_num);
    }

    @Override
    public int hashCode() {

        return Objects.hash(english, wrong_num);
    }

    public void setWrong_num(int wrong_num) {
        this.wrong_num = wrong_num;
    }
}
