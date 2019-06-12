package com.demo.socketdemo.socket;


/**
 * 重连检测
 */
public class ConnectCheck implements Runnable{
	
	private TestClient tcpClient;
	
	public boolean checkFlag = true;
	
	public boolean connectFlag;
	
	//检测间隔
	private static final long checkTime = 2000;
	//检测次数
	private static final int checkCount = 3;
	
	private int checkNumber;
	
	public ConnectCheck(TestClient tcpClient){
		this.tcpClient = tcpClient;
		checkFlag = true;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(checkFlag){
			try {
				Thread.sleep(checkTime);
				if(connectFlag){
					checkNumber = 0;
				}else{
					checkNumber++;
				}
				tcpClient.getLocalSocketClient().sendString("测试。。。。");
				connectFlag = false;
				if(checkNumber>=checkCount){
					tcpClient.reConnect();
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
