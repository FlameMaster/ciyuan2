package com.fengchen.light.utils;

import android.support.annotation.NonNull;

import com.fengchen.light.model.ImageParameter;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ===============================================
 * = 作 者：风 尘
 * <p/>
 * = 版 权 所 有：7416064@qq.com
 * <p/>
 * = 地 点：中 国 北 京 市 朝 阳 区
 * <p/>
 * = 时 间：2016/5/5 17:46
 * <p/>
 * = 分 类 说 明：字符串工具类
 * ================================================
 */
public class StringUtil {

    /*正则表达式：验证用户名*/
    public static final String REGEX_USERNAME = "^[a-zA-Z]\\w{5,17}$";
    /* 正则表达式：验证密码*/
//    public static final String REGEX_PASSWORD = "^[a-zA-Z0-9]{6,16}$";
    public static final String REGEX_PASSWORD = "^[!-~]{6,20}$";
    /* 正则表达式：验证手机号*/
    public static final String REGEX_MOBILE = "^((13[0-9])|(15[^4,\\D])|(17[0-9])|(18[0-9]))\\d{8}$";
    /*正则表达式：验证邮箱*/
    public static final String REGEX_EMAIL = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
    /*正则表达式：验证汉字*/
    public static final String REGEX_CHINESE = "^[\u4e00-\u9fa5],{0,}$";
    /*正则表达式：验证身份证*/
    public static final String REGEX_ID_CARD = "(^\\d{18}$)|(^\\d{15}$)";
    /*正则表达式：验证URL*/
    public static final String REGEX_URL = "http(s)?://([\\w-]+\\.)+[\\w-]+(/[\\w- ./?%&=]*)?";
    /*正则表达式：验证IP地址*/
    public static final String REGEX_IP_ADDR = "(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)";





    /**
     * 利用签名辅助类，将字符串字节数组
     * @param str  需要转的字符串
     */
    public static String md5(String str) {
        byte[] digest = null;
        try {
            MessageDigest md = MessageDigest.getInstance("md5");
            digest = md.digest(str.getBytes());
            return bytes2hex02(digest);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    /***
     *  流转字符串
     * @param bytes 需要转换的流
     * @return 返回的字符串
     */
    public static String bytes2hex02(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        String tmp = null;
        for (byte b : bytes) {
            // 将每个字节与0xFF进行与运算，然后转化为10进制，然后借助于Integer再转化为16进制
            tmp = Integer.toHexString(0xFF & b);
            if (tmp.length() == 1)// 每个字节8为，转为16进制标志，2个16进制位
            {
                tmp = "0" + tmp;
            }
            sb.append(tmp);
        }

        return sb.toString();

    }

    /*验证邮箱*/
    public static boolean isEmail(String email){
        boolean flag = false;
        try{
            Pattern regex = Pattern.compile(REGEX_EMAIL);
            Matcher matcher = regex.matcher(email);
            flag = matcher.matches();
        }catch(Exception e){
            flag = false;
        }
        return flag;
    }

    /*判断字符串是否仅为数字*/
    public static boolean isNumeric1(String str) {
        for (int i = str.length(); --i >= 0; ) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /*用正则表达式判断字符串是否仅为数字*/
    public static boolean isNumeric2(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }

    /*用正则表达式判断手机号码*/
    public static boolean isPhone(String str) {
        return Pattern.matches(REGEX_MOBILE, str);
    }

    /*用正则表达式判断密码格式*/
    public static boolean isPassword(String str) {
        return Pattern.matches(REGEX_PASSWORD,str);
    }

    /*用ascii码判断字符串是否仅为数字*/
    public static boolean isNumeric3(String str) {
        for (int i = str.length(); --i >= 0; ) {
            int chr = str.charAt(i);
            if (chr < 48 || chr > 57)
                return false;
        }
        return true;
    }

    /*判断一个字符串的首字符是否为字母*/
    public static boolean test(String s) {
        char c = s.charAt(0);
        int i = (int) c;
        if ((i >= 65 && i <= 90) || (i >= 97 && i <= 122)) {
            return true;
        } else {
            return false;
        }
    }

    /*判断一个字符串的首字符是否为字母*/
    public static boolean check(String fstrData) {
        char c = fstrData.charAt(0);
        if (((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z'))) {
            return true;
        } else {
            return false;
        }
    }

    /*判断是否为汉字*/
    public static boolean vd(String str) {

        char[] chars = str.toCharArray();
        boolean isGB2312 = false;
        for (int i = 0; i < chars.length; i++) {
            byte[] bytes = ("" + chars[i]).getBytes();
            if (bytes.length == 2) {
                int[] ints = new int[2];
                ints[0] = bytes[0] & 0xff;
                ints[1] = bytes[1] & 0xff;
                if (ints[0] >= 0x81 && ints[0] <= 0xFE && ints[1] >= 0x40
                        && ints[1] <= 0xFE) {
                    isGB2312 = true;
                    break;
                }
            }
        }
        return isGB2312;
    }

    /*非空判断*/
    public static boolean noNull(String str) {
        if (str == null || str.equals("")|| str.equals("null"))
            return false;
        return true;
    }


    /**
     * url转成缓存的key
     * @param imageParameter
     * @return
     */
    public static String toCacheKey(ImageParameter imageParameter) {

        String cacheKey = imageParameter.getUrl()
                + "?x-oss-process=image/resize,m_fill,w_"
                + imageParameter.getWidth()
                + ",h_"
                + imageParameter.getHeight()
                + ",limit_0/auto-orient,0/quality,q_90"
                ;
        return cacheKey;
    }
}
