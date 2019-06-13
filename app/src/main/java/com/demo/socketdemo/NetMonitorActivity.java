package com.demo.socketdemo;

import android.content.Intent;
import android.icu.text.RelativeDateTimeFormatter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.base.lib.baseui.AppBaseActivity;
import com.base.lib.util.AbStrUtil;
import com.demo.socketdemo.service.SocketService;

/**
 * @copyright : 深圳市喜投金融服务有限公司
 * Created by yixf on 2019/6/13
 * @description:
 */
public class NetMonitorActivity extends AppBaseActivity {

    public static NetMonitorActivity monitorActivity;

    private TextView speedText,unitText;

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
        startActivity(new Intent(this,SettingActivity.class));
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
        double speed = Double.valueOf(msg);
        if(speed<1){
            msg = AbStrUtil.formatDouble(speed*1024,1);
            unitText.setText("BS");
        }else if(speed>1000){
            msg = AbStrUtil.formatDouble(speed/1024,1);
            unitText.setText("MBS");
        }else {
            unitText.setText("KBS");
        }
        speedText.setText(msg);
    }
}
