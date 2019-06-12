package com.demo.socketdemo.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.demo.socketdemo.MainActivity;
import com.demo.socketdemo.R;
import com.demo.socketdemo.socket.ConnectCheck;
import com.demo.socketdemo.socket.SocketCharacterStreamService;
import com.demo.socketdemo.socket.SocketTcpClient;
import com.demo.socketdemo.socket.StaticUtil;
import com.demo.socketdemo.socket.TestClient;
import com.demo.socketdemo.socket.UdpClientConnector;
import com.demo.socketdemo.socket.UdpHelper;
import com.demo.socketdemo.util.DeviceUtils;
import com.demo.socketdemo.util.TrafficInfo;
import com.demo.socketdemo.widget.FloatView;

/**
 * VR播放服务
 */
public class SocketService extends Service {

	private SocketTcpClient tcpClient;

	private SocketCharacterStreamService socketService;
	
	private static Handler handler;

	private static SocketService mScoketService;

	private static final String channelID = "UpdateCheck_channel_id";
	private static final String channelName = "UpdateCheck_channelname";

	public static SocketService getVRService(){
		return mScoketService;
	}

	TestClient testClient;

	private UdpClientConnector udpClientConnector;

	private TrafficInfo speed;
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 1) {
//				setSpeed(msg.obj + "kb/s");
//				netText.setText(msg+"kb");
				sendMessageToServer("NetInfo:PhoneID=1,Flow="+msg.obj+"|");
				MainActivity.mActivity.sendNetMes(msg.obj.toString());
			}
			super.handleMessage(msg);
		}

	};
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		handler = new Handler();
		mScoketService = this;

		initUDPSocket();

		speed = new TrafficInfo(getApplicationContext(),mHandler,getUid());
		speed.startCalculateNetSpeed(); // 开启网速监测‘

		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
			createNotificationChannel();
			Notification notification = new Notification.Builder(getApplicationContext(), channelID).build();
			startForeground(1, notification);
		}
	}
	private void createNotificationChannel(){
		NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
		NotificationChannel notificationChannel = new NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_LOW);
		notificationManager.createNotificationChannel(notificationChannel);
	}

	private void initUDPSocket(){
		udpClientConnector = UdpClientConnector.getInstance();
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		SharedPreferences preferences = getApplicationContext().getSharedPreferences(
				StaticUtil.REAL_9, Context.MODE_PRIVATE);
		udpClientConnector.createConnector(preferences.getString("ip",StaticUtil.SOCKET_IP),preferences.getInt("port",StaticUtil.SOCKET_PORT));
		flags = START_STICKY;
		return super.onStartCommand(intent, flags, startId);
	}
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		if(tcpClient!=null){
			tcpClient.disConnect();
		}
//		if(floatView!=null){
//			floatView.removeFromWindow();
//            floatView = null;
//		}
		System.out.println("SocketService...onDestroy");
		MainActivity.mActivity.startService();
        super.onDestroy();
	}
	public void onReceiverMessage(String msg) {
		Toast.makeText(MainActivity.mActivity, msg, Toast.LENGTH_SHORT).show();
		if(msg.contains("-")){
			tcpClient.setConnected(true);
		}
	}
	/**
	 * 发送消息至服务端
	 * @param message
	 */
	public void sendMessageToServer(final String message){

		udpClientConnector.sendStr(message);
	}
	public static void showMessageNotify(final String message){
		if(MainActivity.mActivity!=null){
			handler.post(new Runnable(){
				@Override
				public void run() {
					// TODO Auto-generated method stub
					Toast.makeText(MainActivity.mActivity, message, Toast.LENGTH_SHORT).show();
				}
			});
		}
	}
	/**
	 * 获取当前应用uid
	 */
	public int getUid() {
		int uid = -1;
		PackageManager packageManager = getApplicationContext().getPackageManager();
		try {
			PackageInfo packageInfo = packageManager.getPackageInfo(getApplicationContext().getPackageName(), PackageManager.GET_META_DATA);

			uid = packageInfo.applicationInfo.uid;

		} catch (PackageManager.NameNotFoundException e) {
		}
		return uid;
	}

}
