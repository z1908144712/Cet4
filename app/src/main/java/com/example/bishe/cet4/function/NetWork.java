package com.example.bishe.cet4.function;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.IOException;

public class NetWork {
    public static boolean ping(String url){
        try {
            Process process=Runtime.getRuntime().exec("ping -c 2 -w 100 "+url);
            int status=process.waitFor();
            if(status==0){
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }
    public static boolean isNetworkConnected(Context context) {
    if(context != null) {
        ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
        if(mNetworkInfo != null) {
            return true;
        }
        }
        return false;
    }
}
