package livolo.com.livolointelligermanager.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.littlejie.circleprogress.utils.Constant;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;

import livolo.com.livolointelligermanager.R;
import livolo.com.livolointelligermanager.config.Constants;
import livolo.com.livolointelligermanager.config.SysApplication;
import livolo.com.livolointelligermanager.receiver.BrodcaseTool;

/**
 * Created by mayn on 2018/4/28.
 */

public class MQTTService extends Service {

    public static final String TAG = MQTTService.class.getSimpleName();
    private String userName = "livolo_app_sub";
    private String passWord = "livolo123";
    //订阅主题
    private static String Topic = "livolo/app/";//+homeid
    private static String aliveTopic = "livolo/app/common";

    public static MqttAndroidClient client;
    private MqttConnectOptions conOpt;

    private int resend =0;
    private boolean connected = false;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        init();
        return super.onStartCommand(intent, flags, startId);//在执行完onStartCommand后，服务被异常kill掉，系统不会自动重启该服务
    }

    private void init() {
        // 服务器地址（协议+地址+端口号）
        client = new MqttAndroidClient(getApplicationContext(), Constants.mqttHost, Constants.UserID + System.currentTimeMillis());
        conOpt = new MqttConnectOptions();
        // 清除缓存
        conOpt.setCleanSession(true);
        // 设置超时时间，单位：秒
        conOpt.setConnectionTimeout(10);
        // 心跳包发送间隔，单位：秒
        conOpt.setKeepAliveInterval(20);
        // 用户名
        conOpt.setUserName(userName);
        // 密码
        conOpt.setPassword(passWord.toCharArray());
        //自动重连
        conOpt.setAutomaticReconnect(false);
        if (!client.isConnected() && isConnectIsNomarl()) {
            try {
                client.setCallback(callback);
                /** 连接MQTT服务器 */
                client.connect(conOpt);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }

    public static void publish(String msg) {
        Integer qos = 0;
        Boolean retained = false;
        try {
            client.publish(Topic+Constants.HomeID, msg.getBytes(), qos.intValue(), retained.booleanValue());
            Log.e("-----------mqtt TOPIC:"+Topic+Constants.HomeID,msg);
           publish("测试");
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private MqttCallbackExtended callback = new MqttCallbackExtended() {
        @Override
        public void connectComplete(boolean reconnect, String serverURI) {//
            //链接成功时调用，reconnect = false表示一次链接，true表示重连操作链接成功，自动重连
            Log.e(TAG, "connectComplete ---> " + reconnect + "  serverURI--->" + serverURI);
            try {
                // 订阅myTopic话题
                Log.e("-----TOPIC----","---------订阅topic："+Topic+Constants.HomeID);
                client.subscribe(Topic + Constants.HomeID, 2);
                client.subscribe(aliveTopic,2);
                mhandler.sendEmptyMessageDelayed(0,10000);
            } catch (MqttException e) {
                connected = false;
                resend = 0;
                mhandler.sendEmptyMessageDelayed(0,10000);
//                e.printStackTrace();
            }
        }

        @Override
        public void connectionLost(Throwable cause) {
            //链接断开时，回调
            Log.e(TAG, "----cause --->  " + cause);
            if (Constants.UserID == null){
                Constants.UserID = "";
            }
            init();
            mHandler.sendEmptyMessageDelayed(0,10000);
        }

        Handler mHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                try {
                    if (!client.isConnected())
                        client.connect(conOpt);
                } catch (MqttException e) {
                    e.printStackTrace();
                }
                if (!client.isConnected()){
                    mHandler.sendEmptyMessageDelayed(0,10000);
                }
                return false;
            }
        });

        @Override
        public void messageArrived(String topic, MqttMessage message) {
            String result = new String(message.getPayload());
            if (topic.equals(Topic+Constants.HomeID)){
                Log.e(TAG, "---------推送---topic --> " + topic + "  message ------->" + result);
                try {
                    JSONObject object = new JSONObject(result);
                    String resultCode = object.getString("result_code");
                    String resultMsg = object.getString("result_msg");
                    JSONObject resultData = object.getJSONObject("data");
                    String command = resultData.getString("command");
                    String thing = resultData.getString("thing");
                    if (resultCode.equals("000")) {
                        if (command.equals("operate") || command.equals("manual")) {
                            BrodcaseTool.sendMainDataRefresh(SysApplication.getInstance());
                        } else if (command.equals("add") && thing.equals("switch")) {
                            String switchId = resultData.getString("switch_id");
                            String switchName = resultData.getString("switch_name");
                            BrodcaseTool.sendAddSwitch(SysApplication.getInstance(), switchId, switchName);//添加开关返回的信息
                        } else if (command.equals("reconnect")) {
                            //设备重连后，主动获取设备状态
                            BrodcaseTool.sendMainDataRefresh(SysApplication.getInstance());
                        } else if (command.equals("loginout")) {//loginout
                            if (resultData.getString("user_id").equals(Constants.UserID)) {
                                BrodcaseTool.sendSysExit(SysApplication.getInstance(), R.string.login_out_by_other);
                            }
                        } else if (command.equals("adjust")) {
                            //{"fresh_status":"0","thing":"switch","command":"adjust"}
                            //渐变开关操作返回
                        }
                    } else if (resultCode.equals("111")) {
                        if (command.equals("operate") || command.equals("manual")) {
//                        BrodcaseTool.sendMainDataRefresh(SysApplication.getInstance());
                        } else if (command.equals("add") && thing.equals("switch")) {
                            Log.e("---------------------","--------------------------");
                            BrodcaseTool.sendAddSwitchFailed(SysApplication.getInstance(), resultMsg);//添加开关返回的信息
                        }
//                    Toast.makeText(SysApplication.getInstance(),resultMsg,Toast.LENGTH_SHORT).show();
                    } else if (resultCode.equals("112")) {
                        Toast.makeText(SysApplication.getInstance(), resultMsg, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(SysApplication.getInstance(), resultMsg, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else if (topic.equals(aliveTopic)){
                Log.e(TAG, "---------推送---topic --> " + topic + "  心跳推送！！！ ------->" + result);
                connected = true;
            }
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {
            //消息发送完成时，得到回调
            Log.i(TAG, "token ---> " + token);
        }
    };

    private Handler mhandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            if (!connected){
                if (resend>3){
                    BrodcaseTool.sendSysExit(SysApplication.getInstance(),R.string.login_again);
                }else{
                    init();
                    resend++;
                }
            }
            return false;

        }
    });

    /**
     * 判断网络是否连接
     */
    private boolean isConnectIsNomarl() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if (info != null && info.isAvailable()) {
            String name = info.getTypeName();
            Log.e(TAG, "MQTT当前网络名称：" + name);
            return true;
        } else {
            Log.e(TAG, "MQTT 没有可用网络");
            connected = false;
            resend = 0;
            mhandler.sendEmptyMessageDelayed(0,5000);

            return false;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("----------","----------------------------------------------------------------------------------------------service destory");
    }
}
