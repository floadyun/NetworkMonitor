package com.demo.socketdemo.socket;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;

import com.base.lib.util.ToastUtil;
import com.demo.socketdemo.service.SocketService;

/**
 * 静态数据类
 */
public class StaticUtil {
	
	public static final String REAL_9 = "net_monitor";
	
	public static String SOCKET_IP = "192.168.1.125";

	public static int SOCKET_PORT = 8898;

	public static int PHONE_ID = 1;

	public static long INTERVAL = 200;
}