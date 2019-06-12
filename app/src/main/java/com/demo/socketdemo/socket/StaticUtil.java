package com.demo.socketdemo.socket;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.demo.socketdemo.service.SocketService;

/**
 * 静态数据类
 */
public class StaticUtil {
	
	public static final String REAL_9 = "real9";
	
	public static String SOCKET_IP = "192.168.0.125";

	public static int SOCKET_PORT = 8898;

	public static int PHONE_ID = 1;

	private static SharedPreferences preferences;
	
	public static void saveIpConfig(Context context, String ip, String port) {
		if (!ip.equals("") && !port.equals("")) {
			preferences = context.getSharedPreferences(
					StaticUtil.REAL_9, Context.MODE_PRIVATE);
			Editor editor = preferences.edit();
			editor.putString("ip", ip);
			editor.putInt("port", Integer.valueOf(port));
			editor.commit();

			if (!ip.equals(SOCKET_IP) || Integer.valueOf(port)!=SOCKET_PORT) {
				SOCKET_IP = ip;
				SOCKET_PORT = Integer.valueOf(port);
			}
		}
	}

	/**
	 * 启动Service
	 * @param context
	 */
	public static void startVRService(Context context){
		if(SocketService.getVRService()==null){
			preferences = context.getSharedPreferences(
					StaticUtil.REAL_9, Context.MODE_PRIVATE);
			context.startService(new Intent(context,SocketService.class));
		}
	}
	/**
	 * 获取Socket Ip
	 * @return
	 */
	public static String  getSocketIp(){

		return preferences.getString("ip",SOCKET_IP);
	}

	/**
	 * 获取Socket 端口
	 * @return
	 */
	public static int getSocketPort(){
		return preferences.getInt("port",SOCKET_PORT);
	}
}