package livolo.com.livolointelligermanager.util;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.Charset;

import livolo.com.livolointelligermanager.config.Constants;

/**
 * Created by mayn on 2018/5/29.
 */

public class TCPTool {
    private Context context;
    private Handler mHandler;

    public TCPTool(Handler mHandler){
        this.mHandler = mHandler;
    }

    public void startTCPClient() {
        final String host = "192.168.0.1";
        final int port = 20000;
        new Thread(new Runnable() {
            @Override
            public void run() {
                Socket socket = null;
                try {
                    // 1、创建连接
                    socket = new Socket(host, port);
                    if (socket.isConnected()) {
//                        logD("connect to Server success");
                        Log.e("--------------","---------------------connect to Server success");
                    }else{
                        Log.e("--------------","---------------------connect to Server false");
                    }
                    // 2、设置读流的超时时间
                    socket.setSoTimeout(8000);

                    // 3、获取输出流与输入流
                    OutputStream outputStream = socket.getOutputStream();
                    InputStream inputStream = socket.getInputStream();

                    // 4、发送信息
                    byte[] sendData = "{\"ssid\"：\"livolo\"，\"password\"：\"123456789A\"}".getBytes(Charset.forName("UTF-8"));
                    outputStream.write(sendData, 0, sendData.length);
                    outputStream.flush();

                    // 5、接收信息
                    byte[] buf = new byte[1024];
                    int len = inputStream.read(buf);
                    String receData = new String(buf, 0, len, Charset.forName("UTF-8"));
                    Message msg = mHandler.obtainMessage();
                    msg.what = Constants.GET_NETWORD_RESULT;
                    msg.obj = receData;
                    msg.sendToTarget();
                    Log.e("--------------------","-------------------------receData:"+receData);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (socket != null) {
                        try {
                            socket.close();
                            socket = null;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }).start();
    }

}
