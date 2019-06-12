package com.demo.socketdemo.socket;


import com.demo.socketdemo.service.SocketService;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;


/**
 * TCP Socket客户端
 */
public  class SocketTcpClient implements Runnable {

    private BaseSocketService socketService;
    
    private InetSocketAddress inetSocketAddress;

    private int timeOut = 5000;
    
    private Socket mSocket;
    
    private ConnectCheck connectCheck;

    /**
     * 建立连接
     */
    public void connect(BaseSocketService baseDispatcher) {
        socketService = baseDispatcher;
        new Thread(this).start();
//        connectCheck = new ConnectCheck(this);
//        new Thread(connectCheck).start();
    }
    //断开socket连接
    public void disConnect(){
        if(socketService.socket!=null){
            socketService.onStop();
            try {
                mSocket.close();
            }catch (IOException e){

            }
            mSocket = null;
        }
    	connectCheck.checkFlag = false;
    	connectCheck = null;
    }
    /**
     * 重新连接
     */
    public void reConnect(){
    	SocketService.showMessageNotify("重新连接...");
    	disConnect();
    	connect(socketService);	
    }
    @Override
    public void run() {
        try {
            mSocket = new Socket();
            inetSocketAddress = new InetSocketAddress(StaticUtil.getSocketIp(), StaticUtil.getSocketPort());
            mSocket.connect(inetSocketAddress, timeOut);
            socketService.setSocket(mSocket);
            socketService.onStart();
            this.socketService.onConnectWin();
        } catch (Exception e) {
            this.socketService.onConnectFail(e);
        }
    }
    /**
     * 设置socket连接状态
     * @param isConnect
     */
    public void setConnected(boolean isConnect){
    	connectCheck.connectFlag = isConnect;
    }
    /**
     * 向服务器发送消息
     * @param msg
     */
    public void sendMessage(String msg){
    	socketService.onSend(msg);
    }
}
