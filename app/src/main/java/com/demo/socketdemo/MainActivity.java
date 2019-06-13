package com.demo.socketdemo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.base.lib.baseui.AppBaseActivity;
import com.demo.socketdemo.service.SocketService;
import com.demo.socketdemo.socket.StaticUtil;
import com.demo.socketdemo.util.DeviceUtils;
import com.demo.socketdemo.widget.FloatView;

public class MainActivity extends AppBaseActivity {
	
	public static MainActivity mActivity;

	private EditText ipText, portText;

	private SharedPreferences sPreferences;

	private Handler mHandler;

	private FloatView floatView;
	private TextView netText;
	private View guideView;

	private static int REQUEST_DIALOG_PERMISSION = 1000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		mActivity = this;
		setContentView(R.layout.activity_main);

		initData();

		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M&&!Settings.canDrawOverlays(this) ) {
			Toast.makeText(this,"请在设置中打开悬浮窗权限",Toast.LENGTH_SHORT).show();
			requestSettingCanDrawOverlays();
		}else {
			acquireWakeLock();

			startService();

			initFloatView();
		}

		mHandler = new Handler();
	}
	private void initData() {
		sPreferences = getSharedPreferences(StaticUtil.REAL_9,
				Context.MODE_PRIVATE);

		StaticUtil.SOCKET_IP = sPreferences.getString("ip",
				StaticUtil.SOCKET_IP);
		StaticUtil.SOCKET_PORT = sPreferences.getInt("port",
				StaticUtil.SOCKET_PORT);
		StaticUtil.PHONE_ID = sPreferences.getInt("id",StaticUtil.PHONE_ID);

	    ipText = findViewById(R.id.ip_text);
		portText = findViewById(R.id.port_text);

		ipText.setText("" + StaticUtil.SOCKET_IP);
		portText.setText(StaticUtil.SOCKET_PORT + "");
	}
	/**
	 * 初始化新手指引悬浮窗
	 */
	private void initFloatView(){
		if(floatView!=null)return;
		guideView = getLayoutInflater().inflate(R.layout.network_float_layout,null);
		netText = guideView.findViewById(R.id.float_network_text);
		floatView = new FloatView(MainActivity.mActivity, DeviceUtils.getScreenWidth(this)-DeviceUtils.getDeviceDimen(this, 175),
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
	public void connectService(View view) {
//		StaticUtil.saveIpConfig(this, ipText.getText().toString(), portText
//				.getText().toString());
//		startService();
//		finish();
	}
	/**
	 * 启动服务
	 */
	public void startService(){
		Intent intent = new Intent(this,SocketService.class);
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
			startForegroundService(intent);
		} else {
			startService(intent);
		}
	}
	public void sendTest(View view){
		SocketService.getVRService().sendMessageToServer("测试....");
	}
	private PowerManager.WakeLock mWakeLock;
	//申请设备电源锁
	private void acquireWakeLock()
	{
		if (null == mWakeLock)
		{
			PowerManager pm = (PowerManager)getSystemService(Context.POWER_SERVICE);
			mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, this.getClass().getName());
			if (null != mWakeLock)
			{
				mWakeLock.acquire();
			}
		}
	}
	//释放设备电源锁
	public void releaseWakeLock()
	{
		if (null != mWakeLock)
		{
			mWakeLock.release();
			mWakeLock = null;
		}
	}
	public void sendNetMes(String msg){
		netText.setText(msg+"kb/s");
	}
	private void requestSettingCanDrawOverlays() {
		int sdkInt = Build.VERSION.SDK_INT;
		if (sdkInt >= Build.VERSION_CODES.O) {//8.0以上
			Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
			startActivityForResult(intent, REQUEST_DIALOG_PERMISSION);
		} else if (sdkInt >= Build.VERSION_CODES.M) {//6.0-8.0
			Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
			intent.setData(Uri.parse("package:" + getPackageName()));
			startActivityForResult(intent, REQUEST_DIALOG_PERMISSION);
		} else {//4.4-6.0一下
			//无需处理了
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode==REQUEST_DIALOG_PERMISSION){
			acquireWakeLock();

			startService();

			initFloatView();
		}
	}
}