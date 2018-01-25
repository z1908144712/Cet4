package com.example.bishe.cet4.database;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Skywilling on 2018/1/2.
 */

public class AssetsDatabaseManager {
    private String tag="AssetsDataManager";
    private String databasepath="/data/data/%s/databases";
    private static AssetsDatabaseManager assetsDatabaseManager=null;
    private Context context=null;
    private static Map<String,SQLiteDatabase> databases=new HashMap<String,SQLiteDatabase>();

    public static void initManager(Context context){
        if(assetsDatabaseManager==null){
            assetsDatabaseManager=new AssetsDatabaseManager(context);
        }
    }

    public static AssetsDatabaseManager getAssetsDatabaseManager(){
        return assetsDatabaseManager;
    }

    private AssetsDatabaseManager(Context context){
        this.context=context;
    }

    public SQLiteDatabase getDatabase(String dbfile){
        if(databases.get(dbfile)!=null){
            return databases.get(dbfile);
        }
        if(context==null){
            return null;
        }
        String spath=getDatabaseFilepath();
        String sfile=getDatabaseFile(dbfile);
        File file=new File(sfile);
        SharedPreferences dbs=context.getSharedPreferences(AssetsDatabaseManager.class.toString(),0);
        boolean flag=dbs.getBoolean(dbfile,false);
        if(!flag||!file.exists()){
            file=new File(spath);
            if(!file.exists()&&!file.mkdirs()){
                return null;
            }
            if(!copyAssetsToFilesystem(dbfile,sfile)){
                return null;
            }
            dbs.edit().putBoolean(dbfile,true).commit();
        }
        SQLiteDatabase db=SQLiteDatabase.openDatabase(sfile,null,SQLiteDatabase.NO_LOCALIZED_COLLATORS);
        if(db!=null){
            databases.put(dbfile,db);
        }
        return db;
    }
    private boolean copyAssetsToFilesystem(String assetsSrc,String des){
        InputStream inputStream=null;
        OutputStream outputStream=null;
        AssetManager assetManager=context.getAssets();
        try {
            inputStream = assetManager.open(assetsSrc);
            outputStream=new FileOutputStream(des);
            byte [] buffer=new byte[1024];
            int length;
            while((length=inputStream.read(buffer))>0){
                outputStream.write(buffer,0,length);
            }
            inputStream.close();
            outputStream.close();
        }catch (Exception e){
            e.printStackTrace();
            try {
                if(inputStream!=null){
                    inputStream.close();
                }
                if(outputStream!=null){
                    outputStream.close();
                }
            }catch (Exception ee){
                ee.printStackTrace();
            }
            return false;
        }
        return true;
    }
    private String getDatabaseFilepath(){
        return String.format(databasepath,context.getApplicationInfo().packageName);
    }
    private String getDatabaseFile(String dbfile){
        return getDatabaseFilepath()+"/"+dbfile;
    }
    public boolean closeDatabase(String dbfile){
        if(databases.get(dbfile)!=null){
            SQLiteDatabase db=databases.get(dbfile);
            db.close();
            databases.remove(dbfile);
            return true;
        }
        return false;
    }
    public static void closeAllDatabase(){
        if(assetsDatabaseManager!=null){
            for(int i=0;i<databases.size();i++){
                if(databases.get(i)!=null){
                    databases.get(i).close();
                }
            }
            databases.clear();
        }
    }

}
