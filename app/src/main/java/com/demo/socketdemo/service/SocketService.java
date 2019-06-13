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
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.base.lib.util.AbStrUtil;
import com.demo.socketdemo.MainActivity;
import com.demo.socketdemo.NetMonitorActivity;
import com.demo.socketdemo.R;
import com.demo.socketdemo.socket.StaticUtil;
import com.demo.socketdemo.socket.UdpClientConnector;
import com.demo.socketdemo.util.DeviceUtils;
import com.demo.socketdemo.util.TrafficInfo;
import com.demo.socketdemo.widget.FloatView;

/**
 * VR播放服务
 */
public class SocketService extends Service {

	private static Handler handler;

	private static SocketService mScoketService;

	private static final String channelID = "UpdateCheck_channel_id";
	private static final String channelName = "UpdateCheck_channelname";

	public static SocketService getVRService(){
		return mScoketService;
	}

	private SharedPreferences preferences;

	private UdpClientConnector udpClientConnector;

	private FloatView floatView;
	public TextView netText;
	private View guideView;
	private long interval;
	private TrafficInfo speed;
	private String phoneId;
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 1) {
				String speed = AbStrUtil.formatDouble(Double.valueOf(msg.obj.toString())*(1000/interval),1);
				sendMessageToServer("NetInfo:PhoneID="+phoneId+",Flow="+speed+"|");
				NetMonitorActivity.monitorActivity.sendNetMes(speed);
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

		preferences = getApplicationContext().getSharedPreferences(
				StaticUtil.REAL_9, Context.MODE_PRIVATE);
		phoneId = preferences.getString("phoneId","1");

		speed = new TrafficInfo(getApplicationContext(),mHandler,getUid());
		interval = Long.valueOf(preferences.getString("interval",StaticUtil.INTEVAL));
		speed.setUpdate_interval(interval);
		speed.startCalculateNetSpeed(); // 开启网速监测‘

		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
			createNotificationChannel();
			Notification notification = new Notification.Builder(getApplicationContext(), channelID).build();
			startForeground(1, notification);
		}
		initFloatView();
	}
	/**
	 * 初始化新手指引悬浮窗
	 */
	private void initFloatView(){
		if(floatView!=null)return;
		guideView = NetMonitorActivity.monitorActivity.getLayoutInflater().inflate(R.layout.network_float_layout,null);
		netText = guideView.findViewById(R.id.float_network_text);
		floatView = new FloatView(NetMonitorActivity.monitorActivity, DeviceUtils.getScreenWidth(this)-DeviceUtils.getDeviceDimen(this, 175),
				DeviceUtils.getDeviceDimen(this,400), guideView);
		floatView.setFloatViewClickListener(new FloatView.IFloatViewClick() {
			@Override
			public void onFloatViewClick() {

			}
			@Override
			public void onCloseViewClick() {

			}
		});
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				if(floatView!=null){
					floatView.addToWindow();
				}
			}
		},1000);
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
		udpClientConnector.createConnector(preferences.getString("ip",StaticUtil.SOCKET_IP),preferences.getInt("port",StaticUtil.SOCKET_PORT));
		flags = START_STICKY;
		return super.onStartCommand(intent, flags, startId);
	}
	@Override
	public void onDestroy() {
		System.out.println("SocketService...onDestroy");
		if(floatView!=null){
			floatView.removeFromWindow();
		}
        super.onDestroy();
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
	 * 配置改变的时候调用
	 */
	public void changeConfig(String ip, String port,String phoneId,long interval){
		this.phoneId = phoneId;
		this.interval = interval;
		speed.setUpdate_interval(interval);
		udpClientConnector.createConnector(ip,Integer.valueOf(port));
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
