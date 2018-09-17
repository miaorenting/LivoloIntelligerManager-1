package livolo.com.livolointelligermanager.receiver;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import livolo.com.livolointelligermanager.config.Constants;

/**
 * Created by mayn on 2018/5/21.
 */

public class TCPReceive {

    private Socket mSocket;
    private ServerSocket mServerSocket;
    private InputStream mInputStream;
    private boolean isAlive = true;
    private Handler mHandler;

    public TCPReceive(Handler mHandler){
        this.mHandler = mHandler;
    }

    public void startTCP() {
        new Thread() {
            @Override
            public void run() {
                try {
                    mServerSocket = new ServerSocket(5566);
                    byte[] buffer = new byte[1024];
                    while (isAlive) {
                        mSocket = mServerSocket.accept();
                        mInputStream = mSocket.getInputStream();
                        int count = mInputStream.read(buffer);
                        byte[] resultByte = new byte[count];
                        for (int i = 0;i<count;i++){
                            resultByte[i]=buffer[i];
                        }
                        String result = new String(resultByte);
                        Log.e("-------------","-------------------"+result);
                        Message msg = mHandler.obtainMessage();
                        msg.obj = result;
                        msg.what = Constants.GET_NETWORD_RESULT;
                        msg.sendToTarget();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public void closed(){
        isAlive = false;
        if (mSocket!=null){
            try {
                mSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(mServerSocket != null){
            try {
                mServerSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
