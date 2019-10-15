package com.demo.socketdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;
import com.base.lib.baseui.AppBaseActivity;
import com.base.lib.util.AbStrUtil;
import com.demo.socketdemo.service.SocketService;

import java.text.DecimalFormat;
import java.util.Random;

/**
 * @copyright : 深圳市喜投金融服务有限公司
 * Created by yixf on 2019/6/13
 * @description:
 */
public class NetMonitorActivity extends AppBaseActivity {

    public static NetMonitorActivity monitorActivity;

    private TextView speedText,unitText;

    private long firstTime;

    private int clickCount = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_monitor_layout);
        monitorActivity = this;

        speedText = findViewById(R.id.net_speed_text);
        unitText = findViewById(R.id.net_unit_text);
        startService();
    }
    public void exitApp(View view){
        finishSelf();
    }
    /**
     * 跳转至设置
     * @param view
     */
    public void toSetting(View view){
        clickCount++;
        if((System.currentTimeMillis()-firstTime) > 3000)  //System.currentTimeMillis()无论何时调用，肯定大于2000
        {
            firstTime = System.currentTimeMillis();
            clickCount = 0;
        }else{
            if(clickCount==2){
                clickCount = 0;
                startActivity(new Intent(this,SettingActivity.class));
            }
        }
    }
    /**
     * 启动服务
     */
    public void startService(){
        Intent intent = new Intent(this, SocketService.class);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }
    }

    public void sendNetMes(String msg){
     //   double speed = Double.valueOf(msg);
        double speed = nextDouble(3.8,5.1)*100;
        String uinit = "Kbps";
        if(speed<1024*128){//1Mbps=128Kb/s
            uinit = "Mbps";
            msg = AbStrUtil.formatDouble(speed*8/1024,1);
        }else {//1Gbps=1024Mbps=128*1024Kb/s
            uinit = "Gbps";
            msg = AbStrUtil.formatDouble(speed*8/(1024*1024),1);
        }
        speedText.setText(AbStrUtil.roundByScale(speed,0)+"");
        unitText.setText("Mbps");
        SocketService.getVRService().setNetFloatText(msg+uinit);
    }
    /**
     * 获取随机数
     * @param min
     * @param max
     * @return
     */
    public static double nextDouble(double min,double max){
//保留两位小数
        DecimalFormat df = new DecimalFormat("#.00");
        if(min==max){return min;}return Double.parseDouble(df.format(min+((max-min)*new  Random().nextDouble())));
    }
    @Override
    protected void onResume() {
        super.onResume();
        if(SocketService.getVRService()!=null){
            SocketService.getVRService().removeFloatView();
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(SocketService.getVRService()!=null){
            SocketService.getVRService().addFloatView();
        }
    }
}
