package livolo.com.livolointelligermanager.util;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.json.JSONObject;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;

import livolo.com.livolointelligermanager.config.Constants;

/**
 * Created by mayn on 2018/5/5.
 */

public class UDPTool {

    private final static String SEND_IP = "255.255.255.255";  //发送IP
    private final static int SEND_PORT = 8021;               //发送端口号
    private final static int RECEIVE_PORT = 8022;            //接收端口号

    private boolean listenStatus = true;  //接收线程的循环标识

    private DatagramSocket receiveSocket;

    private InetAddress serverAddr;
    private Handler mHandler;

    public UDPTool(Handler mHandler){
        this.mHandler = mHandler;
    }

    public void startReceive(){
        new UdpReceiveThread().start();
    }

    public void sendMsg(){
        //点击按钮则发送UDP报文
        new UdpSendThread().start();
    }

    public void closeReceive(){
        //停止接收线程，关闭套接字连接
        listenStatus = false;
        if (receiveSocket!=null){
            receiveSocket.close();
        }
    }

    /*
     *   UDP数据发送线程 */
    public class UdpSendThread extends Thread {
        @Override
        public void run() {
            try {
                byte[] buf = "AResultCode:0".getBytes();
                // 创建DatagramSocket对象，使用随机端口
                DatagramSocket sendSocket = new DatagramSocket();
                serverAddr = InetAddress.getByName(SEND_IP);
                DatagramPacket outPacket = new DatagramPacket(buf, buf.length, serverAddr, SEND_PORT);
                sendSocket.send(outPacket);
                sendSocket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /*
    * UDP数据接收线程
    * */
    public class UdpReceiveThread extends Thread {
        @Override
        public void run() {
            try {
                DatagramPacket inPacket = null;
                byte[] inBuf =null;
                if (receiveSocket == null){
                    receiveSocket = new DatagramSocket(null);
                    receiveSocket.setReuseAddress(true);
                    receiveSocket.bind(new InetSocketAddress(RECEIVE_PORT));
                    inBuf = new byte[512];
                    inPacket = new DatagramPacket(inBuf, inBuf.length);
                }
                while (listenStatus) {
                    receiveSocket.receive(inPacket);
                    byte[] receiveInfo = inPacket.getData();
                    int len = 0;
                    for (int i = 0;i<receiveInfo.length;i++){
                       if(receiveInfo[i] == (byte)0){
                           len = i;
                           break;
                       }
                    }
                    byte[] resultByte = new byte[len];
                    for (int m = 0;m<len;m++){
                        resultByte[m] = receiveInfo[m];
                    }
                    String result = new String(resultByte);
                    Message msg = mHandler.obtainMessage();
                    msg.what = Constants.GET_NETWORD_RESULT;
                    msg.obj = result;
                    msg.sendToTarget();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
