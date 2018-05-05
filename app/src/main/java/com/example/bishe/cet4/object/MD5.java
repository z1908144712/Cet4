package com.example.bishe.cet4.object;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5 {
    private String text;

    public MD5(String text) {
        this.text = text;
    }

    public String getMD5() {
        MessageDigest md5= null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        md5.update(text.getBytes());
        byte []md5Array=md5.digest();
        return bytesToHex(md5Array);
    }

    private String bytesToHex(byte[] md5Array){
        BigInteger bigInteger=new BigInteger(1,md5Array);
        return bigInteger.toString(16);
    }
}
