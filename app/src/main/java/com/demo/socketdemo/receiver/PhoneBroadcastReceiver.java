package com.demo.socketdemo.receiver;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

import com.demo.socketdemo.service.SocketService;

/**
 * @copyright : 深圳市喜投金融服务有限公司
 * Created by yixf on 2019/1/4
 * @description:
 */
public class PhoneBroadcastReceiver extends BroadcastReceiver {

    private TelephonyManager telMgr;

    @Override
    public void onReceive(Context context, Intent intent) {
        telMgr = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);
        switch (telMgr.getCallState()) {
            //来电
            case TelephonyManager.CALL_STATE_RINGING:

                break;
            //接听
            case TelephonyManager.CALL_STATE_OFFHOOK:
                SocketService.getVRService().sendMessageToServer("mobile:state=connect|");
                break;
            //挂断
            case TelephonyManager.CALL_STATE_IDLE:
                SocketService.getVRService().sendMessageToServer("mobile:state=disconnect|");
                break;
        }
    }
}
