package com.example.bishe.cet4.object;

import java.util.Objects;

public class WordPlan {
    private String time;
    private String words_num;
    private String learn_time;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getWords_num() {
        return words_num;
    }

    public void setWords_num(String words_num) {
        this.words_num = words_num;
    }

    public String getLearn_time() {
        return learn_time;
    }

    public void setLearn_time(String learn_time) {
        this.learn_time = learn_time;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WordPlan wordPlan = (WordPlan) o;
        return time.equals(wordPlan.time) &&
                learn_time.equals(wordPlan.learn_time) &&
                words_num.equals(wordPlan.words_num);
    }

    @Override
    public int hashCode() {

        return Objects.hash(time, words_num, learn_time);
    }

    @Override
    public String toString() {
        return "WordPlan{" +
                "time='" + time + '\'' +
                ", words_num='" + words_num + '\'' +
                ", learn_time='" + learn_time + '\'' +
                '}';
    }
}
