package com.example.bishe.cet4.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
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

import java.util.List;

public class WordDetailActivity extends Activity {
    private WebView webView=null;
    private Language languageFrom=null;
    private Language languageTo=null;
    private TranslateParameters translateParameters=null;
    private Translator translator=null;
    private Handler handler=null;
    private String keyword =null;
    private ProgressBar progressBar=null;
    private int type;
    private int type_show;
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
                .timeout(3000)
                .from(languageFrom)
                .to(languageTo)
                .build();
        translator=Translator.getInstance(translateParameters);
        handler=new Handler();
        translator.lookup(keyword, "requestId", new TranslateListener() {
            @Override
            public void onError(TranslateErrorCode translateErrorCode, String s) {
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
                        });
                        webView.setWebChromeClient(new WebChromeClient(){
                            @Override
                            public void onProgressChanged(WebView view, int newProgress) {
                                view.loadUrl(JSFilter.jsfilter_base);
                                view.loadUrl(JSFilter.jsfilter_apk_download);
                                if(type_show==TYPE_WORD){
                                    view.loadUrl(JSFilter.jsfilter_word);
                                }
                                if(newProgress==100){
                                    webView.setVisibility(View.VISIBLE);
                                    progressBar.setVisibility(View.INVISIBLE);
                                }else{
                                    webView.setVisibility(View.INVISIBLE);
                                    progressBar.setVisibility(View.VISIBLE);
                                    progressBar.setProgress(newProgress);
                                }
                            }

                            @Override
                            public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
                                new AlertDialog.Builder(WordDetailActivity.this)
                                        .setTitle("Alert")
                                        .setMessage(message)
                                        .setCancelable(false)
                                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                result.confirm();
                                            }
                                        })
                                        .create().show();
                                return true;
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
}
