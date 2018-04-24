package com.example.bishe.cet4.object;

public class JSFilter {
    public static final String jsfilter_base="javascript:function jsfilter(){" +
            "var hd=document.getElementById('hd');" +
            "if(hd!=null){hd.style.display=\"none\";}" +
            "var dict_dl=document.querySelector('.dict-dl');" +
            "if(dict_dl!=null){dict_dl.style.display=\"none\"}" +
            "var amend=document.querySelector('.amend');" +
            "if(amend!=null){amend.style.display=\"none\"}" +
            "var ft=document.getElementById('ft');" +
            "if(ft!=null){ft.parentNode.removeChild(ft)};" +
            "var dictNav=document.getElementById('dictNav');" +
            "if(dictNav!=null){dictNav.parentNode.removeChild(dictNav);}" +
            "}" +
            "jsfilter();";

    public static final String jsfilter_word="javascript:function word(){" +
            "var id_=document.getElementById('ee_contentWrp');" +
            "if(id_!=null){id_.parentNode.removeChild(id_);}" +
            "id_=document.getElementById('special_contentWrp');" +
            "if(id_!=null){id_.parentNode.removeChild(id_);}" +
            "id_=document.getElementById('web_trans_contentWrp');" +
            "if(id_!=null){id_.parentNode.removeChild(id_);}" +
            "id_=document.getElementById('rel_word_contentWrp');" +
            "if(id_!=null){id_.parentNode.removeChild(id_);}" +
            "id_=document.getElementById('syno_contentWrp');" +
            "if(id_!=null){id_.parentNode.removeChild(id_);}" +
            "id_=document.getElementById('blng_sents_part_contentWrp');" +
            "if(id_!=null){id_.parentNode.removeChild(id_);}" +
            "id_=document.getElementById('auth_sents_part_contentWrp');" +
            "if(id_!=null){id_.parentNode.removeChild(id_);}" +
            "}" +
            "word();";

    public static final String jsfilter_apk_download="javascript:function apk(){" +
            "var divs=document.getElementsByTagName('div');" +
            "for(var i=0;i<divs.length;i++){" +
            "if(new RegExp('.apk').test(divs[i].innerHTML)){" +
            "divs[i].parentNode.removeChild(divs[i]);" +
            "break;" +
            "}" +
            "}" +
            "document.querySelector('body').style.marginTop=0;" +
            "}" +
            "apk();";
    public static final String jsfilter_html="javascript:function apk(){" +
            "alert(document.getElementsByTagName('html')[0].innerHTML);" +
            "}" +
            "apk();";
}
