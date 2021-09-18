package com.liyaan.utils;



import java.security.MessageDigest;

/**
 * MD5工具类
 */
public class MD5Util {

    public final static String md5Lower(String s){
        String md5 = getMd5(s);
//        return StringUtils.isNotEmpty(md5)? md5.toLowerCase() : md5;
        return md5.toLowerCase();
    }
    public final static String encodeMd5(String url){
        String md5 = getMd5(url);
        return md5.toLowerCase();
    }
    public final static String getMd5(String s) {
        char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        try {
            byte[] btInput = s.getBytes();
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(btInput);
            // 获得密文
            byte[] md = mdInst.digest();
            // 把密文转换成十六进制的字符串形式
            int j = md.length;
            char[] str = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            return null;
        }
    }

    public static String byteToHexString(byte[] b) {
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < b.length; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            hexString.append(hex.toUpperCase());
        }
        return hexString.toString();
    }

    public static String byteToHexStringNoUpper(byte[] b) {
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < b.length; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
