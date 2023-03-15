package com.hao.netcommon.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Base64;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * @类名： CryptoUtils
 * @功能说明：常用加解密工具类
 * @修改内容：
 * @修改人：
 * @修改时间：
 */
public class CryptoUtils {
    private static final Charset CHAR_SET = Charset.forName("UTF-8");

    /**
     * 摘要类算法，MD5,SHA
     */

    /**
     * 生成str对应的MD5串，128位摘要
     *
     * @param str
     * @return
     */
    public static String genMD5Str(String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        byte[] byteArray = str.getBytes(CHAR_SET);

        byte[] md5Bytes = md5.digest(byteArray);

        return bytesToHexString(md5Bytes);
    }

    /**
     * byte数组转换为十六进制字符串
     *
     * @return HexString 16进制串
     */
    public static String bytesToHexString(byte[] bArray) {
        StringBuilder sb = new StringBuilder(bArray.length);
        String sTemp;
        for (byte element : bArray) {
            sTemp = Integer.toHexString(255 & element);
            if (sTemp.length() < 2) {
                sb.append(0);
            }
            sb.append(sTemp);
        }
        return sb.toString();
    }
}
