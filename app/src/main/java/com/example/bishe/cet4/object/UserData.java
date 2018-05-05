package com.example.bishe.cet4.object;

import com.google.gson.Gson;

import java.util.List;
import java.util.Objects;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobDate;

public class UserData extends BmobObject{
    private String username;
    private Integer plandays;
    private Integer learneddays;
    private List wordplan;
    private List wrongwords;
    private BmobDate synctime;
    private BmobDate begin_time;
    private String previous_time;
    private Integer plannum;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getPlandays() {
        return plandays;
    }

    public void setPlandays(Integer plandays) {
        this.plandays = plandays;
    }

    public Integer getLearneddays() {
        return learneddays;
    }

    public void setLearneddays(Integer learneddays) {
        this.learneddays = learneddays;
    }

    public List getWordplan() {
        return wordplan;
    }

    public void setWordplan(List wordplan) {
        this.wordplan = wordplan;
    }

    public List getWrongwords() {
        return wrongwords;
    }

    public void setWrongwords(List wrongwords) {
        this.wrongwords = wrongwords;
    }

    public BmobDate getSynctime() {
        return synctime;
    }

    public void setSynctime(BmobDate synctime) {
        this.synctime = synctime;
    }

    public BmobDate getBegin_time() {
        return begin_time;
    }

    public void setBegin_time(BmobDate begin_time) {
        this.begin_time = begin_time;
    }

    public String getPrevious_time() {
        return previous_time;
    }

    public void setPrevious_time(String previous_time) {
        this.previous_time = previous_time;
    }

    public Integer getPlannum() {
        return plannum;
    }

    public void setPlannum(Integer plannum) {
        this.plannum = plannum;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserData userData = (UserData) o;
        return username.equals(userData.username) &&
                plandays==userData.plandays &&
                learneddays==userData.learneddays &&
                equalsWordPlan(wordplan, userData.wordplan) &&
                equalsWordCollection(wrongwords, userData.wrongwords) &&
                begin_time.getDate().equals(userData.begin_time.getDate()) &&
                previous_time.equals(userData.previous_time) &&
                plannum==userData.plannum;
    }

    private boolean equalsWordPlan(List object1,List object2){
        if(object1==null||object2==null||object1.size()!=object2.size()){
            return false;
        }
        for(int i=0;i<object1.size();i++){
            Gson gson=new Gson();
            Object o1=gson.fromJson(gson.toJson(object1.get(i)),WordPlan.class);
            Object o2=gson.fromJson(gson.toJson(object2.get(i)),WordPlan.class);
            if(!o1.equals(o2)){
                return false;
            }
        }
        return true;
    }

    private boolean equalsWordCollection(List object1,List object2){
        if(object1==null||object2==null||object1.size()!=object2.size()){
            return false;
        }
        for(int i=0;i<object1.size();i++){
            Gson gson=new Gson();
            Object o1=gson.fromJson(gson.toJson(object1.get(i)),WordCollection.class);
            Object o2=gson.fromJson(gson.toJson(object1.get(i)),WordCollection.class);
            if(!o1.equals(o2)){
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {

        return Objects.hash(username, plandays, learneddays, wordplan, wrongwords, begin_time, previous_time, plannum);
    }

    @Override
    public String toString() {
        return "UserData{" +
                "username='" + username + '\'' +
                ", plandays=" + plandays +
                ", learneddays=" + learneddays +
                ", wordplan=" + wordplan +
                ", wrongwords=" + wrongwords +
                ", synctime=" + synctime +
                ", begin_time=" + begin_time +
                ", previous_time='" + previous_time + '\'' +
                ", plannum=" + plannum +
                '}';
    }
}
