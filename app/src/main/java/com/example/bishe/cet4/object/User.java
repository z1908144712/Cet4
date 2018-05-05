package com.example.bishe.cet4.object;


import java.io.Serializable;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobDate;

public class User extends BmobObject implements Serializable{
    private String username;
    private String password;
    private BmobDate create_time;
    private BmobDate login_time;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public BmobDate getCreate_time() {
        return create_time;
    }

    public void setCreate_time(BmobDate create_time) {
        this.create_time = create_time;
    }

    public BmobDate getLogin_time() {
        return login_time;
    }

    public void setLogin_time(BmobDate login_time) {
        this.login_time = login_time;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", create_time=" + create_time +
                ", login_time=" + login_time +
                '}';
    }
}
