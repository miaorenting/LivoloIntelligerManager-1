package livolo.com.livolointelligermanager.receiver;

import android.util.Log;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Arrays;

/**
 * Created by mayn on 2018/5/4.
 */

public class UDPReceive implements Runnable {
    public DatagramSocket udpSocket;
    DatagramPacket udpPacket = null;
    byte[] data = new byte[128];
    @Override
    public void run() {
        try {
            udpSocket = new DatagramSocket();//6464
            udpPacket = new DatagramPacket(data, data.length);
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        while (true){
            try {
                udpSocket.receive(udpPacket);
            } catch (Exception e) {
                e.printStackTrace();
            }
            //接收到的byte[]
            byte [] m = Arrays.copyOf(udpPacket.getData(), udpPacket.getLength());
            StringBuffer sb = new StringBuffer();
            for (int i = 0;i<m.length;i++){
                sb.append(m[i]);
            }
            Log.e("========udp===",sb.toString());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

    }

    public void close(){
        udpSocket.close();

    }

}
