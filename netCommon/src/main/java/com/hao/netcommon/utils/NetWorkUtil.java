package com.hao.netcommon.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


/**
 * 网络相关的帮助类
 * Created by 胡亚敏 on 2016-4-12.
 */
public class NetWorkUtil {

    private static String TAG = NetWorkUtil.class.getName();
    /**
     * 网络类型 - 无连接
     */

    public static final String NETWORK_TYPE_WIFI = "wifi";
    public static final String NETWORK_TYPE_3G = "eg";
    public static final String NETWORK_TYPE_2G = "2g";
    public static final String NETWORK_TYPE_WAP = "wap";
    public static final String NETWORK_TYPE_UNKNOWN = "unknown";
    public static final String NETWORK_TYPE_DISCONNECT = "disconnect";

    /**
     * 判断网络是否可用
     *
     * @param context
     * @return
     */
    public static boolean isNetWorkActive(Context context) {
        // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
        try {
            ConnectivityManager connectivity = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null) {
                // 获取网络连接管理的对象
                NetworkInfo info = connectivity.getActiveNetworkInfo();
                if (info != null && info.isConnected()) {
                    // 判断当前网络是否已经连接
                    if (info.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
            NetworkInfo ethNetInfo = connectivity.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);
            if (ethNetInfo != null && ethNetInfo.isConnected() && ethNetInfo.getState() ==
                    NetworkInfo.State.CONNECTED) {//有线网络
                return true;
            }

        } catch (Exception e) {
        }
        return false;
    }


    /**
     * 判断wifi是否可用
     *
     * @param context
     * @return
     */
    public static boolean isWifiActive(Context context) {
        boolean result;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (wifiNetworkInfo != null && wifiNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            Log.d(TAG, "wifi正常");
            result = true;
        } else {
            Log.d(TAG, "wifi关闭");
            result = false;
        }
        return result;
    }

}
