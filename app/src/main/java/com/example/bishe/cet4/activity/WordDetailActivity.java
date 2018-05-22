package com.example.bishe.cet4.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.bishe.cet4.R;
import com.example.bishe.cet4.object.JSFilter;
import com.example.bishe.cet4.object.Word;
import com.youdao.sdk.app.Language;
import com.youdao.sdk.app.LanguageUtils;
import com.youdao.sdk.ydonlinetranslate.Translator;
import com.youdao.sdk.ydtranslate.Translate;
import com.youdao.sdk.ydtranslate.TranslateErrorCode;
import com.youdao.sdk.ydtranslate.TranslateListener;
import com.youdao.sdk.ydtranslate.TranslateParameters;
import com.zyao89.view.zloading.ZLoadingView;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.bmob.v3.b.V;

public class WordDetailActivity extends Activity implements View.OnClickListener{
    private WebView webView=null;
    private Language languageFrom=null;
    private Language languageTo=null;
    private TranslateParameters translateParameters=null;
    private Translator translator=null;
    private Handler handler=null;
    private Handler handler_timer=null;
    private Timer timer=null;
    private TimerTask timerTask=null;
    private String keyword =null;
    private ProgressBar progressBar=null;
    private View loadingView=null;
    private View timeoutView=null;
    private int type;
    private int type_show;
    private String weburl=null;
    public static final int TYPE_ENGLISH=0;
    public static final int TYPE_CHINESE=1;
    public static final int TYPE_AUTO=2;
    public static final int TYPE_WORD=3;
    public static final int TYPE_DETAIL=4;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.word_detail_activity_layout);
        //初始化控件
        initViews();
        //初始化数据
        initData();
    }

    private void initViews(){
        loadingView=findViewById(R.id.id_loading);
        timeoutView=findViewById(R.id.id_timeout);
        timeoutView.setOnClickListener(this);
        webView=findViewById(R.id.id_web_view);
        progressBar=findViewById(R.id.id_progressBar);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            if(webView.canGoBack()){
                webView.goBack();
                return true;
            }else{
                finish();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void initData(){
        Intent intent=getIntent();
        keyword =intent.getStringExtra("keyword");
        type=intent.getIntExtra("type",-1);
        type_show=intent.getIntExtra("type_show",-1);
        switch (type){
            case TYPE_ENGLISH:
                languageFrom= LanguageUtils.getLangByName("英文");
                languageTo=LanguageUtils.getLangByName("中文");
                break;
            case TYPE_CHINESE:
                languageFrom= LanguageUtils.getLangByName("中文");
                languageTo=LanguageUtils.getLangByName("英文");
                break;
            case TYPE_AUTO:
                languageFrom= LanguageUtils.getLangByName("自动");
                languageTo=LanguageUtils.getLangByName("自动");
                break;
        }
        translateParameters=new TranslateParameters.Builder()
                .source("youdao")
                .timeout(5000)
                .from(languageFrom)
                .to(languageTo)
                .build();
        translator=Translator.getInstance(translateParameters);
        handler=new Handler();
        handler_timer=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case 0:
                        timeout();
                        break;
                }
                super.handleMessage(msg);
            }
        };
        translator.lookup(keyword, "requestId", new TranslateListener() {
            @Override
            public void onError(TranslateErrorCode translateErrorCode, String s) {
                handler_timer.sendEmptyMessage(0);
                System.out.println(translateErrorCode.name());
            }

            @Override
            public void onResult(final Translate translate, String s, String s1) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        webView.getSettings().setJavaScriptEnabled(true);
                        webView.setWebViewClient(new WebViewClient(){
                            @Override
                            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                                view.loadUrl(url);
                                return true;
                            }

                            @Override
                            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                                weburl=url;
                                timer=new Timer();
                                timerTask=new TimerTask() {
                                    @Override
                                    public void run() {
                                        timer.cancel();
                                        timer.purge();
                                        handler_timer.sendEmptyMessage(0);
                                    }
                                };
                                timer.schedule(timerTask,10000,1);
                            }

                            @Override
                            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                                handler_timer.sendEmptyMessage(0);
                                System.out.println(errorResponse.getStatusCode());
                            }

                            @Override
                            public void onPageFinished(WebView view, String url) {
                                timer.cancel();
                                timer.purge();
                            }
                        });
                        webView.setWebChromeClient(new WebChromeClient(){
                            @Override
                            public void onProgressChanged(WebView view, int newProgress) {
                                view.loadUrl(JSFilter.jsfilter_base);
                                view.loadUrl(JSFilter.jsfilter_apk_download);
                                if(type_show==TYPE_WORD){
                                    view.loadUrl(JSFilter.jsfilter_word);
                                }
                                System.out.println(newProgress);
                                if(newProgress==100){
                                    loadsuccess();
                                }else{
                                    loading();
                                }
                            }
                        });
                        webView.loadUrl(translate.getDeeplink());
                    }
                });
            }

            @Override
            public void onResult(List<Translate> list, List<String> list1, List<TranslateErrorCode> list2, String s) {

            }
        });
    }

    private void loadsuccess(){
        System.out.println(webView.getVisibility());
        if(webView.getVisibility()==View.GONE){
            webView.setVisibility(View.VISIBLE);
        }
        if(loadingView.getVisibility()==View.VISIBLE){
            loadingView.setVisibility(View.GONE);
        }
        if(progressBar.getVisibility()==View.VISIBLE){
            progressBar.setVisibility(View.GONE);
        }
        if(timeoutView.getVisibility()==View.VISIBLE){
            timeoutView.setVisibility(View.GONE);
        }
    }

    private void timeout(){
        if(timeoutView.getVisibility()==View.GONE){
            timeoutView.setVisibility(View.VISIBLE);
        }
        if(progressBar.getVisibility()==View.VISIBLE){
            progressBar.setVisibility(View.GONE);
        }
        if(webView.getVisibility()== View.VISIBLE){
            webView.setVisibility(View.GONE);
        }
        if(loadingView.getVisibility()==View.VISIBLE){
            loadingView.setVisibility(View.GONE);
        }
    }

    private void loading(){
        if(timeoutView.getVisibility()==View.VISIBLE){
            timeoutView.setVisibility(View.GONE);
        }
        if(progressBar.getVisibility()==View.GONE){
            progressBar.setVisibility(View.VISIBLE);
        }
        if(webView.getVisibility()==View.VISIBLE){
            webView.setVisibility(View.GONE);
        }
        if(loadingView.getVisibility()==View.GONE){
            loadingView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.id_timeout:
                loading();
                webView.loadUrl(weburl);
                break;
        }
    }
}
